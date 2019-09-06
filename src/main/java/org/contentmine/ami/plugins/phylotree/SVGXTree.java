package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlEdge;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNode;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtu;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtus;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTree;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTrees;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.contentmine.graphics.svg.linestuff.ComplexLine.SideOrientation;
import org.contentmine.graphics.svg.linestuff.LineMerger;
import org.contentmine.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;

public class SVGXTree extends SVGG {
	




	private static final String LENGTH = "length";

	private final static Logger LOG = Logger.getLogger(SVGXTree.class);
	
	private static final String ID = "id";
	private static final String LABEL = "label";
	private static final String NODE= "node";
	static final String OTU = "otu";
	private static final String ROOT_TAXA_BLOCK = "RootTaxaBlock";
	private static final String T = "t";
	private static final String TAX1 = "tax1";
	private static final String TREE = "tree.";
	private static final String TREE1 = "tree1";
	private static final String TREES = "Trees";
	private static final String TREES_BLOCK_FROM_XML = "TreesBlockFromXML";
	private static final String TRUE = "true";
	
	public static final double EPS = 0.01;

	
	private double eps;
	SVGElement parentSVGElement;
	private PhyloTreeSVGAnalyzer treeAnalyzer;
	List<SVGXTreeNode> rootNodeList;
	private double edgeLengthFontSize = 6.;
	private String edgeLengthFill = "red";

	private Element xmlTree;
	private List<SVGLine> svgLines;
	private SideOrientation sideOrientation;
	private LineOrientation treeOrientation;


	public SVGXTree(SVGElement parentSVGG) {
		this.eps = EPS;
		this.parentSVGElement = parentSVGG;
		if (parentSVGG != null) {
			this.setId(TREE+parentSVGG.getId());
			this.setId(parentSVGG.getId());
		}
		this.treeAnalyzer = new PhyloTreeSVGAnalyzer(this);
	}

	/** create tree from SVG
	 * 
	 * @param container selected SVG container (normally a G or SVG)
	 * @param eps error margin
	 * @param method of line join (try MergeMethod.TOUCHING_LINES)
	 * @return
	 */
	public static SVGXTree makeTree(SVGElement container, double eps, MergeMethod method) {
		SVGPolyline.replacePolyLinesBySplitLines(container);
		List<SVGLine> lines = SVGLine.extractSelfAndDescendantLines(container);
		lines = LineMerger.mergeLines(lines, eps, method);		
		SVGXTree tree = new SVGXTree(container);
		PhyloTreeSVGAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		treeAnalyzer.analyzeBranchesAtLineEnds(lines);
		tree.buildTree();
		return tree;
	}
	
	public void setEpsilon(double eps) {
		this.eps = eps;
	}
	
	public LineOrientation getTreeOrientation() {
		return getTreeAnalyzer().getTreeOrientation();
	}
	
	public List<SVGXTreeEdge> getEdgeList() {
		return getTreeAnalyzer().getEdgeList();
	}

	public List<SVGXTreeNode> getNodeList() {
		return getTreeAnalyzer().getNodeList();
	}

	public SVGXTreeNode getRootNode() {
		ensureRootNodeList();
		return (rootNodeList.size() == 1) ? rootNodeList.get(0): null;
	} 

	public void buildTree() {
		getTreeAnalyzer().buildTree();
		treeAnalyzer.addLinkLinesLengthsAndWords();
		addLengthsToSVG();
		Element xmlTree = buildXMLTree();
		if (xmlTree != null) {
			LOG.trace("TREE "+xmlTree.toXML());
			XMLUtil.outputQuietly(xmlTree, new File("target/temp.tree.xml"), 1);
			displayTree("target/temp.svg");
		}
	}

	private Element buildXMLTree() {
		if (xmlTree == null) {
			xmlTree = null;
			SVGXTreeNode rootNode = getRootNode();
			// FIXME
			xmlTree = createElementAndAddDescendants(rootNode);
		}
		return xmlTree;
	}

	public String createNewick() {
		buildXMLTree();
		String s = "no tree";
		if (xmlTree != null) {
			StringBuilder sb = new StringBuilder();
			Element element = xmlTree;
			sb.insert(0,  XMLConstants.S_SEMICOLON);
			processElement(sb, element);
			s = sb.toString();
		}
		return s;
	}
	
	void processElement(StringBuilder sb, Element element) {
		String label = element.getAttributeValue(LABEL);
		if (label == null) {
			label = element.getAttributeValue(ID);
		}
		sb.insert(0, label);
		Elements childElements = element.getChildElements();
		if (childElements.size() > 0) {
			sb.insert(0,XMLConstants.S_RBRAK);
			for (int i = 0; i < childElements.size(); i++) {
				if (i > 0) {
					sb.insert(0,  XMLConstants.S_COMMA);
				}
				processElement(sb, childElements.get(i));
			}
			sb.insert(0,XMLConstants.S_LBRAK);
		}
	}
	
	private Element createElementAndAddDescendants(SVGXTreeNode treeNode) {
		Element node = null;
		if (treeNode !=null) {
			node = new Element(NODE);
			if (treeNode.getId() != null) {
				node.addAttribute(new Attribute(ID, treeNode.getId()));
				LOG.trace("ID "+treeNode.getId());
			}
			if (treeNode.text != null) {
				node.addAttribute(new Attribute(LABEL, treeNode.text.getValue()));
			}
			if (treeNode.childNodeList != null) {
				for (SVGXTreeNode childTreeNode : treeNode.childNodeList) {
					Element childNode = createElementAndAddDescendants(childTreeNode);
					node.appendChild(childNode);
				}
			}
		}
		return node;
	}

	private void addLengthsToSVG() {
		for (SVGXTreeEdge edge : getEdgeList()) {
			SVGLine line = edge.getLine(); 
			Real2 mid = line.getBoundingBox().getCentroid();
			double d = edge.getLength();
			SVGText text = new SVGText(mid, ""+(int)d);
			text.setFontSize(edgeLengthFontSize);
			text.setFill(edgeLengthFill);
			this.appendChild(text);
		}
	}

	private void displayTree(String filename) {
		SVGSVG svg = new SVGSVG();
		svg.appendChild(this.copy());
		try {
			SVGUtil.debug(svg, new FileOutputStream(filename), 1);
		} catch (Exception e) {
			throw new RuntimeException("Cannot write: ", e);
		}
	}

	public PhyloTreeSVGAnalyzer getTreeAnalyzer() {
		return treeAnalyzer;
	}

	void createFromChildren(SVGG svgg, String xpath, double eps) {
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svgg, xpath);
		if (lineElements.size() != 0) {
			LOG.trace("making tree with "+lineElements.size()+" lines");
			List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
			treeAnalyzer.analyzeBranchesAtLineEnds(svgLines, eps);
			buildTree();
			svgg.appendChild(this);
		}
	}

	public NexmlNEXML createNEXML() {
		NexmlNEXML nexml = createNexmlNEXMLElement();
		NexmlOtus otus = createNexmlOtusElement(nexml);
		NexmlTrees trees = createNexmlTreesElement(nexml);
		NexmlTree tree = createNexmlTreeElement(trees);
		addNodesAndOtus(otus, tree);
		addEdges(tree);
		return nexml;
	}

	private NexmlTree createNexmlTreeElement(Element trees) {
		NexmlTree tree = new NexmlTree();
		tree.setId(TREE1);
		tree.setLabel(TREE1);
		tree.setType(NexmlNEXML.FLOAT_TREE);
		trees.appendChild(tree);
		return tree;
	}

	private NexmlTrees createNexmlTreesElement(Element nexml) {
		NexmlTrees trees = new NexmlTrees();
		trees.setLabel(TREES_BLOCK_FROM_XML);
		trees.setId(TREES);
		trees.setOtus(TAX1);
		nexml.appendChild(trees);
		return trees;
	}

	private NexmlOtus createNexmlOtusElement(Element nexml) {
		NexmlOtus otus = new NexmlOtus();
		otus.setId(TAX1);
		otus.setLabel(ROOT_TAXA_BLOCK);
		nexml.appendChild(otus);
		return otus;
	}

	private NexmlNEXML createNexmlNEXMLElement() {
		NexmlNEXML nexml = new NexmlNEXML();
		nexml.setId(this.getId());
		return nexml;
	}

	private void addNodesAndOtus(Element otus, Element tree) {
		int otuCount = 0;
		for (SVGXTreeNode svgxNode : getNodeList()) {
			NexmlNode node = new NexmlNode();
			tree.appendChild(node);
			node.setId(svgxNode.getId());
			node.setLabel(svgxNode.getId());
			if (svgxNode.getParentTreeNode() == null) {
				node.setRoot(TRUE);
			}
			List<SVGElement> elements = SVGUtil.getQuerySVGElements(svgxNode, "./svg:text[@title='"+OTU+"']");
			if (elements.size() == 1) {
				otuCount = addOtu(otus, otuCount, node, elements);
			}
		}
	}

	private int addOtu(Element otus, int otuCount, NexmlNode node,
			List<SVGElement> elements) {
		String otuId = T+(++otuCount);
		node.setOtuRef(otuId);
		NexmlOtu otu = new NexmlOtu();
		otu.setId(otuId);
		node.setLabel(elements.get(0).getValue());
		otus.appendChild(otu);
		return otuCount;
	}

	private void addEdges(Element tree) {
		for (SVGXTreeEdge svgxEdge : getEdgeList()) {
			NexmlEdge edge = new NexmlEdge();
			tree.appendChild(edge);
			edge.setId(svgxEdge.getId());
			edge.setLabel(svgxEdge.getId());
			String parentId = svgxEdge.getAttributeValue(SVGXTreeEdge.PARENT);
			if (parentId != null) {
				edge.setSource(parentId);
			}
			String childId = svgxEdge.getAttributeValue(SVGXTreeEdge.CHILD);
			if (childId != null) {
				edge.setTarget(childId);
			}
			Double length = Real.normalize(svgxEdge.getLength(), 3);
			if (length != null) {
				edge.addAttribute(new Attribute(SVGX_PREFIX+":"+LENGTH, SVGX_NS, String.valueOf(length)));
			}
		}
	}

	public void replacePolyLinesBySplitLines() {
		SVGPolyline.replacePolyLinesBySplitLines(parentSVGElement);
	}

	public void analyzeBranchesAtLineEnds() {
		this.svgLines = SVGLine.extractSelfAndDescendantLines(parentSVGElement);
		treeAnalyzer.analyzeBranchesAtLineEnds(svgLines);
	}

	void ensureRootNodeList() {
		if (rootNodeList == null) {
			rootNodeList = new ArrayList<SVGXTreeNode>();
		}
	}
	
}
