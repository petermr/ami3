package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.eucl.xml.XMLUtil;

public class NWKTree {

	private static final String Y_POS = "yPos";
	private static final String HEIGHT = "height";
	private static final String T = "t";
	private static final String BSET = "bset";
	private static final String WIDTH = "width";
	
	private static final Logger LOG = Logger.getLogger(NWKTree.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private NWKSubtree subtree;
	private NWKBranch branch;
	private Element treeXML;

	/**
   Tree --> Subtree ";" | Branch ";"
      
	 * @param string
	 */
	public NWKTree(NWKSubtree subtree) {
		this.subtree = subtree;
	}

	public NWKTree(NWKBranch branch) {
		this.branch = branch;
	}

	public static NWKTree createTree(StringBuilder sb) {
		NWKTree tree = null;
		if (sb.toString().endsWith(";")) {
			LOG.trace("tree: "+sb);
			sb.deleteCharAt(sb.length() - 1);
			NWKSubtree subtree = NWKSubtree.createSubtree(sb);
			if (subtree != null) {
				tree = new NWKTree(subtree);
			} else {
				NWKBranch branch = NWKBranch.createBranch(sb);
				if (branch != null) {
					tree = new NWKTree(branch);
				}
			}
		}
		return tree;
	}
	
	public String toString() {
		String s = "[tree: ";
		if (subtree != null) {
			s += String.valueOf(subtree);
		}
		if (branch != null) {
			s += String.valueOf(branch);
		}
		s += "]";
		return s;
	}

	public static void trim(StringBuilder sb) {
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
	}

	public String createNewick() {
		StringBuilder sb = new StringBuilder();
		if (subtree != null) {
			subtree.createNewick(sb);
		} else if (branch != null) {
			branch.createNewick(sb);
		}
		return sb.toString()+";";
	}

	public SVGG createSVGOld() {
		SVGG g = new SVGG();
		if (subtree != null) {
			SVGG gg = subtree.createSVGOld();
			g.appendChild(gg);
		}
		if (branch != null) {
			SVGG gg = branch.createSVGOld();
			g.appendChild(gg);
		}
//		g.setTransform(new Transform2(new Vector2(deltaX, 0.)));
		return g;
	}
	

	public Element createXML() {
		if (treeXML == null) {
			treeXML = new Element("top");
			if (subtree != null) {
				Element subNode = subtree.createXML();
				if (subNode != null) {
					treeXML.appendChild(subNode);
				}
			}
			if (branch != null) {
				Element subNode = branch.createXML();
				if (subNode != null) {
					treeXML.appendChild(subNode);
				}
			}
			replaceSingleChildBrNodes();
			addWidths();
			addYpositions();
		}
		return treeXML;
	}

	private void addYpositions() {
		List<Element> tips = XMLUtil.getQueryElements(treeXML, "//t");
		for (int i = 0; i < tips.size(); i++) {
			Element tip = tips.get(i);
			addYPosition(tip, i);
		}
		List<Element> tbset = XMLUtil.getQueryElements(treeXML, "//t | //bset");
		double latestY = 0;
		for (int i = 0; i < tbset.size(); i++) {
			Element element = tbset.get(i);
			String tag = element.getLocalName();
			if (tag.equals(T)) {
				latestY = getYPosition(element);
			} else if (tag.equals(BSET)) {
				int width = getWidth(element);
				double yPos = width / 2.0  + latestY;
				addYPosition(element, yPos);
			}
		}

	}

	private void addWidths() {
		addHeight(treeXML, 1);
		getWidthX(treeXML);
	}

	private void addWidths1(Element element) {
		List<Element> tips = XMLUtil.getQueryElements(element, "//t");
		Stack<Element> unusedStack = new Stack<Element>();
		Set<Element> used = new HashSet<Element>();
		unusedStack.addAll(tips);
//		used.addAll(tips);
		while (!unusedStack.isEmpty()) {
			Element next = unusedStack.pop();
			Element parent = (Element) next.getParent();
			if (parent == null) {
				continue;
			}
			if (!used.contains(parent)) {
				used.add(parent);
				unusedStack.push(parent);
			}
			Elements childElements = next.getChildElements();
			int size = childElements.size();
			int width = 1;
			if (size != 0) {
				width = 0;
				for (int i = 0; i < size; i++) {
					Element child = childElements.get(i);
					width += getWidth(child);
				}
				LOG.trace(size+"/"+width);
			}
			width = Math.max(1,  width);
			addWidth(next, width);
		}
	}

	public static int getWidthX(Element element) {
		int height = getHeight(element);
		Elements childElements = element.getChildElements();
		int width = 0;
		if (element.getLocalName().equals(T)) {
			width = 1;
		} else {
			for (int i = 0; i < childElements.size(); i++) {
				Element childElement = childElements.get(i);
				addHeight(childElement, height + 1);
				int width1 = getWidthX(childElement);
				width += width1;
			}
		}
		addWidth(element, width);
		return width;
	}
	
	private static double getYPosition(Element child) {
		String sizeS = child.getAttributeValue(Y_POS);
		return sizeS == null ? 0 : Double.parseDouble(sizeS);
	}

	private static int getWidth(Element child) {
		String sizeS = child.getAttributeValue(WIDTH);
		return sizeS == null ? 0 : Integer.parseInt(sizeS);
	}

	private static int getHeight(Element child) {
		String sizeS = child.getAttributeValue(HEIGHT);
		return sizeS == null ? 1 : Integer.parseInt(sizeS);
	}

	private static void addYPosition(Element element, double d) {
		element.addAttribute(new Attribute(Y_POS, String.valueOf(d)));
	}

	private static void addWidth(Element element, int i) {
		element.addAttribute(new Attribute(WIDTH, String.valueOf(i)));
	}

	private static void addHeight(Element element, int i) {
		element.addAttribute(new Attribute(HEIGHT, String.valueOf(i)));
	}

	private void replaceSingleChildBrNodes() {
		List<Element> brNodes = XMLUtil.getQueryElements(treeXML, "//br");
		for (Element br : brNodes) {
			if (br.getChildElements().size() == 1) {
				Element parent = (Element) br.getParent();
				Element child = br.getChildElements().get(0);
				child.detach();
				parent.replaceChild(br,  child);
			}
		}
	}

	
	public SVGG createSVG() {
		createXML();
		SVGG g = new SVGG();
		List<Element> ts = XMLUtil.getQueryElements(treeXML, "// t");
		for (Element t : ts) {
			int height = getHeight(t);
			double yPos = getYPosition(t);
			double xx = height * 10.;
			double yy = yPos * 10.;
			SVGText text = new SVGText(new Real2(xx, yy), t.getValue());
			text.setFontSize(8.);
			g.appendChild(text);
		}
		List<Element> bs = XMLUtil.getQueryElements(treeXML, "//bset | //t");
		for (Element b : bs) {
			int height = getHeight(b);
			double yPos = getYPosition(b);
			double x0 = height * 10;
			double y0 = offsetY(yPos) * 10;
			Element parent = (Element) b.getParent();
			if (parent != null) {
				int hh = getHeight(parent);
				double yy = getYPosition(parent);
				double x1 = hh * 10;
				double y1 = offsetY(yy)  * 10;
				SVGLine line = new SVGLine(new Real2(x0, y0), new Real2(x1, y0));
				g.appendChild(line);
				line = new SVGLine(new Real2(x1, y1), new Real2(x1, y0));
				g.appendChild(line);
			}
		}
		return g;
	}

	private double offsetY(double yPos) {
		return yPos - 0.3;
	}
	public SVGG createSVGOld1() {
		SVGG g = new SVGG();
		List<Element> childElements = XMLUtil.getQueryElements(treeXML, "bset | t");
		double deltaX = 30.;
		for (Element childElement : childElements) {
			createSVG(g, childElement, deltaX);
		}
		return g;
	}

	private void createSVG(SVGG g, Element element, double deltaX) {
		String tag = element.getLocalName();
		SVGG gg = new SVGG();
		if (tag.equals(BSET)) {
			createBsetSVG(gg, element, deltaX);
		} else if (tag.equals(T)) {
			createTextSVG(gg, element, deltaX);
		} else {
			throw new RuntimeException("unknown element: "+tag);
		}
		g.appendChild(gg);
	}

	private void createTextSVG(SVGG parentG, Element element, double deltaX) {
		parentG.setTransform(new Transform2(new Vector2(deltaX, 0.0)));
		SVGText text = new SVGText(new Real2(0.0, 0.0), element.getValue());
		parentG.appendChild(text);
		text.setFontSize(12.);
	}

	private void createBsetSVG(SVGG parentG, Element element, double deltaX) {
		List<Element> childElements = XMLUtil.getQueryElements(element, "bset | t");
		for (Element childElement : childElements) {
			createSVG(parentG, childElement, deltaX);
		}
	}

}
