package org.contentmine.graphics.svg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

public class SVGUtil {

	private static final String TRANSFORMS_APPLIED = "transformsApplied";
	private static final Logger LOG = Logger.getLogger(SVGUtil.class);

	/**
	 * adds a new svg:g between element and its children
	 * this can be used to set scales, rendering, etc.
	 * also copies ant transform attribute
	 * @param element to amend (is changed)
	 */
	public static AbstractCMElement interposeGBetweenChildren(AbstractCMElement element) {
		AbstractCMElement g = new SVGG();
		element.appendChild(g);
		while (element.getChildCount() > 1) {
			Node child = element.getChild(0);
			child.detach();
			g.appendChild(child);
		}
		return g;
	}

	/** creates an SVGElement
	 * 
	 * throws RuntimeException on errors
	 * 
	 * @param xmlString
	 * @return
	 */
	public static SVGElement parseToSVGElement(String xmlString) {
		Element element = XMLUtil.parseXML(xmlString);
		return SVGElement.readAndCreateSVG(element);
	}

	/** creates an SVGElement
	 * 
	 * @param is
	 * @return
	 */
	public static SVGElement parseToSVGElement(InputStream is) {
		Element element = null;
		try {
			element = new Builder().build(is).getRootElement();
			return SVGElement.readAndCreateSVG(element);
		} catch (Exception e) {
			throw new RuntimeException("cannot parse input stream", e);
		}
	}

	/** creates an SVGElement
	 * 
	 * @param is
	 * @return
	 */
	public static SVGElement parseToSVGElement(File file) {
		Element element = null;
		if (file == null) {
			throw new RuntimeException("null SVG file");
		}
		if (!file.exists()) {
			throw new RuntimeException("SVG file does not exist: "+file);
		}
		if (file.isDirectory()) {
			throw new RuntimeException("SVG file is directory: "+file);
		}
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("BUG file does not exist: +file");
		}
		return parseToSVGElement(fis);
	}

	public static List<SVGElement> getQuerySVGElements(AbstractCMElement svgElement, String xpath) {
		List<Element> elements = XMLUtil.getQueryElements(svgElement, xpath, SVGConstants.SVG_XPATH);
		List<SVGElement> svgElements = new ArrayList<SVGElement>();
		for (Element element : elements) {
			if (!(element instanceof SVGElement)) {
				throw new RuntimeException("Element was not SVGElement: "+element.toXML());
			}
			svgElements.add((SVGElement) element);
		}
		return svgElements;
	}

	public static Real2 getTransformedXY(SVGElement element) {
		Real2 xy = new Real2(element.getXY());
		Transform2 t2 = element.getCumulativeTransform();
		return xy.getTransformed(t2);
	}
	
	/** transform a (pair) of coordinates by context
	 * can be used for transforming scales, e.g. by 
	 * getTransformedXY(element, new Real2(scalex, scaley)) {
	 * @param element the context (for cumulative transformations)
	 * @param xy
	 * @return transformed pair
	 */
	public static Real2 getTransformedXY(SVGElement element, Real2 xy) {
		Transform2 t2 = element.getCumulativeTransform();
		return xy.getTransformed(t2);
	}

	public static Double decimalPlaces(Double width, int i) {
		int ii = (int) Math.pow(10., i);
		return (double)Math.round(width* ii) / (double)ii;
	}

	/** applies to leaf nodes
	 * BUT removes all ancestral transformations, so be careful
	 * this doesn't remove transforms for other nodes without knowledge
	 * best not called directly
	 * @param svgElements
	 */
	public static void applyCumulativeTransforms(List<SVGElement> svgElements) {
		for (SVGElement svgElement : svgElements) {
			Transform2 t2 = svgElement.getCumulativeTransform();
			svgElement.applyTransformPreserveUprightText(t2);
		}
	}

	/** finds root SVG element ancestor and then removes all transformation in the tree
	 * @param element - any element in tree will do
	 */
	public static void applyAndRemoveCumulativeTransformsFromDocument(AbstractCMElement element) {
		List<SVGElement> roots = SVGUtil.getQuerySVGElements(element, "/svg:svg");
		if (roots.size() == 1) {
			SVGSVG root = (SVGSVG) roots.get(0);
			if (root.getAttribute(TRANSFORMS_APPLIED) == null) {
				List<SVGElement> leafElements = SVGUtil.getQuerySVGElements(root, "//svg:*[count(*)=0]");
				applyCumulativeTransforms(leafElements);
				Nodes transformAttributes = root.query("//@transform");
				for (int i = 0; i < transformAttributes.size(); i++) {
					Attribute attribute = (Attribute) transformAttributes.get(i);
					if (attribute.getParent() instanceof SVGText) {
						SVGText text = (SVGText) attribute.getParent();
						LOG.trace("TEXT "+text.toXML());
						text.setTransformToRotateAboutTextOrigin();
					} else {
						attribute.detach();
					}
				}
				root.addAttribute(new Attribute(TRANSFORMS_APPLIED, "yes"));
			}
//			roots.get(0).debug("ROOT");
		}
	}


/**
<g>
  <defs id="defs1">
   <clipPath clipPathUnits="userSpaceOnUse" id="clipPath1">
    <path style=" fill : none; stroke : black; stroke-width : 0.5;" d="M0.0 0.0 L595.0 0.0 L595.0 793.0 L0.0 793.0 L0.0 0.0 Z"/>
   </clipPath>		 
   
   remove these. don't know whether we have to remove from style attribute but try this anyway
   <path style="clip-path:url(#clipPath1); stroke:none;" d="M327.397 218.897 L328.023 215.899 L329.074 215.899 C329.433 215.899 329.692 215.936 329.854 216.007 C330.011 216.08 330.167 216.231 330.321 216.46 C330.545 216.792 330.744 217.186 330.92 217.639 L331.403 218.897 L332.412 218.897 L331.898 217.626 C331.725 217.201 331.497 216.792 331.214 216.397 C331.088 216.222 330.903 216.044 330.657 215.863 C331.459 215.755 332.039 215.52 332.399 215.158 C332.759 214.796 332.937 214.341 332.937 213.791 C332.937 213.397 332.855 213.072 332.691 212.813 C332.527 212.557 332.3 212.38 332.013 212.287 C331.723 212.192 331.299 212.145 330.74 212.145 L327.907 212.145 L326.493 218.897 L327.397 218.897 ZM328.653 212.888 L330.856 212.888 C331.203 212.888 331.447 212.914 331.592 212.966 C331.736 213.018 331.855 213.117 331.941 213.264 C332.032 213.41 332.075 213.58 332.075 213.776 C332.075 214.011 332.016 214.227 331.898 214.43 C331.778 214.634 331.609 214.794 331.39 214.914 C331.17 215.033 330.893 215.11 330.552 215.145 C330.376 215.16 330.0 215.167 329.424 215.167 L328.175 215.167 L328.653 212.888 "/>

   */
	public static void removeAllClipPaths(AbstractCMElement svg) {
		List<SVGElement> clipPathNodes = SVGUtil.getQuerySVGElements(svg, "//svg:defs/svg:clipPath");
		for (int i = 0; i < clipPathNodes.size(); i++) {
			clipPathNodes.get(i).detach();
		}
		Nodes clipPathElements = svg.query("//*[@style[contains(.,'clip-path')]]");
		for (int i = 0; i < clipPathElements.size(); i++) {
			((SVGElement) clipPathElements.get(i)).setClipPath(null);
		}
	}

	public static void addIdsToAllElements(SVGSVG svg) {
		List<SVGElement> elems = SVGUtil.getQuerySVGElements(svg, "//svg:*");
		int i = 0;
		for (SVGElement elem : elems) {
			if (elem.getId() == null) {
				elem.setId(elem.getTag()+(i++));
			}
		}
	}

	/**
	 * find all text elements managed by a parent <g> and explicitly copy any font-size
	 *?  //svg:g[@font-size]/svg:text[not(@font-size)] >> //svg:g[@font-size]/svg:text[@font-size]
	 * @param svg
	 */
	
	public static void denormalizeFontSizes(SVGSVG svg) {
		List<SVGElement> gs = SVGUtil.getQuerySVGElements(svg, "//svg:g[@font-size and svg:text[not(@font-size)]]");
		for (SVGElement g : gs) {
			Double fontSize = g.getFontSize();
			LOG.trace("FS "+fontSize);
			g.getAttribute("font-size").detach();
			List<SVGElement> texts = SVGUtil.getQuerySVGElements(g, "./svg:text[not(@font-size)]");
			for (SVGElement text : texts) {
				text.setFontSize(fontSize);
			}
		}
	}


	public static void removeXmlSpace(SVGSVG svg) {
		Nodes nodes = svg.query(".//@*[local-name()='space' and .='preserve']");
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).detach();
		}
	}

	public static void drawBoxes(List<? extends SVGElement> elementList, AbstractCMElement svgParent,
			String stroke, String fill, double strokeWidth, double opacity) {
		for (SVGElement element : elementList) {
			SVGElement.drawBox(element.getBoundingBox(), element, stroke, fill, strokeWidth, opacity);
		}
	}

	public static void drawBoxes(List<? extends SVGElement> elementList, AbstractCMElement svgParent, double strokeWidth, double opacity) {
		for (SVGElement element : elementList) {
			SVGElement.drawBox(element.getBoundingBox(), element, element.getStroke(), element.getFill(), strokeWidth, opacity);
		}
	}
	
	public static void setBoundingBoxCached(List<? extends SVGElement> elementList, boolean cached) {
		for (SVGElement element : elementList) {
			element.setBoundingBoxCached(cached);
		}
	}
	/** creates an inclusive bounding box for a list of SVGElements.
	 * 
	 * @param elementList
	 * @return bbox; null if empty list or all elements have null bboxes
	 */
	public static Real2Range createBoundingBox(List<? extends SVGElement> elementList) {
		Real2Range r2r = null;
		if (elementList != null && elementList.size() > 0) {
			r2r = elementList.get(0).getBoundingBox();
			for (int i = 1; i < elementList.size(); i++) {
				r2r = r2r.plus(elementList.get(i).getBoundingBox());
			}
		}
		return r2r;
	}
	
	/** find all elements completely within a bounding box.
	 * 
	 * uses Real2Range.includes(Real2Range)
	 * 
	 * @param boundingBox outer container
	 * @param elementList elements to be examined
	 * @return empty list if parameters are null or no elements fit criterion
	 */
	public static List<SVGElement> findElementsWithin(Real2Range boundingBox, List <? extends SVGElement> elementList) {
		List<SVGElement> includedList = new ArrayList<SVGElement>();
		if (boundingBox != null && elementList != null) {
			for (SVGElement element : elementList) {
				Real2Range bbox = element.getBoundingBox();
				if (boundingBox.includes(bbox)) {
					includedList.add(element);
				}
			}
		} 
		return includedList;
	}
	
	
	/** find all elements completely within a bounding box.
	 * 
	 * uses Real2Range.includes(Real2Range)
	 * 
	 * @param boundingBox outer container
	 * @param elementList elements to be examined
	 * @return empty list if parameters are null or no elements fit criterion
	 */
	public static List<SVGElement> findElementsIntersecting(Real2Range boundingBox, List <? extends SVGElement> elementList) {
		List<SVGElement> includedList = new ArrayList<SVGElement>();
		if (boundingBox != null && elementList != null) {
			for (SVGElement element : elementList) {
				Real2Range bbox = element.getBoundingBox();
				// the signature for Real2Range is messy
				Real2Range intersect = boundingBox.intersectionWith(bbox);
				if (intersect != null && intersect.isValid()) {
					includedList.add(element);
				}
			}
		} 
		return includedList;
	}
	
	/** crude quick method to create list of non-Overlapping BoundingBoxes
	 * use only for small number of paths
	 * will only work if paths a
	 * @return
	 */
	public static List<Real2Range> createNonOverlappingBoundingBoxList(List<? extends SVGElement> svgElementList) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		for (SVGElement element : svgElementList) {
			Real2Range bbox = element.getBoundingBox();
			if (bboxList.size() == 0) {
				bboxList.add(bbox);
			} else {
				for (int i = 0; i < bboxList.size(); i++) {
					Real2Range bbox1 = bboxList.get(i);
					// merge into existing box
					if (bbox != null && bbox1 != null && bbox.intersectionWith(bbox1) != null) {
						bbox1 = bbox1.plus(bbox); 
						bboxList.set(i, bbox1);
						bbox = null;
					}
				}
				if (bbox != null) {
					bboxList.add(bbox);
				}
			}
		}
		contractOverLappingBoxes(bboxList);
		return bboxList;
	}

	private static void contractOverLappingBoxes(List<Real2Range> bboxList) {
		while (true) {
			Real2Range bboxi = null;
			int detach = -1;
			outer:
			for (int i = 1; i < bboxList.size(); i++) {
				bboxi = bboxList.get(i);
				for (int j = 0; j < i; j++) {
					Real2Range bboxj = bboxList.get(j);
					// merge into existing box
					if (bboxi.intersectionWith(bboxj) != null) {
						bboxj = bboxj.plus(bboxi); 
						bboxList.set(j, bboxj);
						detach = i;
						break outer;
					}
				}
			}
			if (detach >= 0) {
				bboxList.remove(detach);
			} else {
				break;
			}
		}
	}
	
	/**
	 * Many shapes / paths are drawn twice; if two or more are equal, remove the later one(s)
	 * 
	 * @param shapeList
	 * @return
	 */
	public static List<SVGShape> removeDuplicateShapes(List<SVGShape> shapeList) {
		if (shapeList != null) {
			Set<String> dStringSet = new HashSet<String>();
			int count = 0;
			List<SVGShape> newPathList = new ArrayList<SVGShape>();
			for (SVGShape shape : shapeList) {
				String dString = shape.getGeometricHash();
				if (dStringSet.contains(dString)) {
					LOG.trace("Detached a duplicate path "+dString);
					shape.detach();
					count++;
				} else {
					dStringSet.add(dString);
					newPathList.add(shape);
				}
			}
			if (count > 0) {
				LOG.trace("Detached "+count+" duplicate paths");
				shapeList = newPathList;
			}
		}
		return shapeList;
	}

	public static String getSVGXAttribute(SVGElement svgElement, String attName) {
		Attribute attribute = getSVGXAttributeAttribute(svgElement, attName);
		return (attribute == null) ? null : attribute.getValue();
	}

	public static Attribute getSVGXAttributeAttribute(SVGElement svgElement, String attName) {
		Attribute attribute = svgElement.getAttribute(attName, SVGConstants.SVGX_NS);
		return attribute;
	}

	public static void setSVGXAttribute(SVGElement svgElement, String attName, String value) {
		if (attName != null && value != null) {
			Attribute attribute = new Attribute(SVGConstants.SVGX_PREFIX+XMLConstants.S_COLON+attName, SVGConstants.SVGX_NS, value);
			svgElement.addAttribute(attribute);
		}
	}

	public static void debug(Element gChunk, FileOutputStream fileOutputStream,
			int indent) {
		try {
			XMLUtil.debug(gChunk, fileOutputStream, indent);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void debug(Element element, String filename, int indent) {
		try {
			debug(element, new FileOutputStream(filename), indent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static SVGSVG createSVGSVG(List<? extends SVGElement> elementList) {
		SVGSVG svg = new SVGSVG();
		Real2Range boundingBox = SVGElement.createBoundingBox(elementList);
		if (boundingBox != null) {
			svg.setWidth(boundingBox.getXRange().getRange());
			svg.setHeight(boundingBox.getYRange().getRange());
			Real2 origin = boundingBox.getLLURCorners()[0];
			Transform2 t2 = new Transform2(new Vector2(origin.multiplyBy(-1.0)));
			SVGG g = new SVGG();
			svg.appendChild(g);
			g.setTransform(t2);
			for (SVGElement element : elementList) {
				g.appendChild(SVGElement.readAndCreateSVG(element));
			}
		}
		return svg;
	}

	public static boolean isNullReal2Range(Real2Range r2r) {
		boolean isNull = true;
		if (r2r != null) {
			RealRange xRange = r2r.getXRange();
			RealRange yRange = r2r.getYRange();
			boolean xnull = xRange == null || xRange.toString().equals("NULL");
			boolean ynull = xRange == null || yRange.toString().equals("NULL");
			isNull = xnull || ynull;
		}
		return isNull;
	}

	public static List<SVGShape> makeShapes(SVGElement svgChunk) {
		List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(svgChunk);
		SVGSVG.wrapAndWriteAsSVG(svgChunk, new File("target/debug/shapes0.svg"));

		SVGSVG.wrapAndWriteAsSVG(pathList, new File("target/debug/paths.svg"));
		Path2ShapeConverter converter = new Path2ShapeConverter(pathList);
		List<SVGShape> shapeList = converter.convertPathsToShapes(pathList);
		return shapeList;
	}

	/** some measurements use "px", etc.
	 * This is not systematic - will aim to convert to pixels.
	 * @param w
	 * @return
	 */
	public static String convertUnits(String w) {
		if (w != null && w.endsWith(SVGElement.PX)) {
			w = w.substring(0,  w.length() - SVGElement.PX.length());
		}
		return w;
	}

//	public static AffineTransform createAffineTransform(Transform2 transform2) {
//		AffineTransform affineTransform = null;
//		if (transform2 != null) {
//			double[][] t2matrix = transform2.getMatrix();
//			double[] array = new double[6];
//			array[0] = t2matrix[0][0];
//			array[1] = t2matrix[0][1];
//			array[2] = t2matrix[0][2];
//			array[3] = t2matrix[1][0];
//			array[4] = t2matrix[1][1];
//			array[5] = t2matrix[1][2];
//			affineTransform = new AffineTransform(
//					array[0], array[1], array[2], array[3], array[4], array[5]);
//		}
//		return affineTransform;
//	}
}
