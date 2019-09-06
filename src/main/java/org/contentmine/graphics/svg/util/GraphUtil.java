package org.contentmine.graphics.svg.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGScript;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

public class GraphUtil {
	private final static Logger LOG = Logger.getLogger(GraphUtil.class);
	
	private static final String SVG_PAN_JS = "../../SVGPan.js";
	private static final String DTD_N = ".dtd'>";

	public final static double eps1 = 0.001;
	public static boolean onSameLine(Real2Range thisBox, Real2Range lastBox, double lineToleranceFactor) {
		Boolean onSameLine = false;
		if (lastBox != null && thisBox != null) {
			double deltaY = thisBox.getYMin() - lastBox.getYMin();
			double height = thisBox.getYRange().getRange();
			onSameLine = Math.abs(deltaY) < lineToleranceFactor*height;
		}
		return onSameLine;
	}

	public static Integer guessSpaces(Real2Range lastBox, Real2Range thisBox, double spaceFactor) {
		Integer spaces = null;
		if (lastBox != null) {
			double lastMax = lastBox.getXMax();
			double thisMin = thisBox.getXMin();
			double distance = thisBox.getXMin() - lastBox.getXMax();
			double averageWidth = 0.5 * (lastBox.getXRange().getRange() + thisBox.getXRange().getRange());
			spaces = (int) Math.rint(spaceFactor * distance / averageWidth);
			// really crude and depends on font
			spaces = distance < 2.4 ? 0 : 1;
		}
		return spaces;
	}

	public static String getInteger(Real2Range boundingBox) {
		double rx = boundingBox.getXMin();
		double ry = boundingBox.getYMin();
		return String.valueOf((int)rx+"/"+(int)ry+" ");
	}

	private static boolean inVerticalLineSegment(Line2 line, Real2 nodeCoords) {
		boolean inSegment = false;
		double nodeX = nodeCoords.getX();
		Real2 lineCoords0 = line.getXY(0);
		if (Real.isEqual(nodeX, lineCoords0.getX(), eps1)) {
			Real2 lineCoords1 = line.getXY(1);
			double nodeY = nodeCoords.getY();
			double y0 = lineCoords0.getY();
			double y1 = lineCoords1.getY();
			inSegment = isInSegment(nodeY, y0, y1); 
		}
		return inSegment;
	}

	public static boolean isInSegment(double x, double x0, double x1) {
		return (x1 > x0) ? inSeg(x0, x1, x) : inSeg (x1, x0, x);
	}

	public static boolean inSeg(double ymin, double ymax, double y) {
		return y <= ymax + eps1 && y >= ymin - eps1; 
	}

	public static String stripDTD(String ss) {
		int idx = ss.indexOf(DTD_N);
		ss = "<?xml version='1.0' encoding='UTF-8'?>\n"+ss.substring(idx+DTD_N.length()+2);
		return ss;
	}

	public static String removeNonprintingChars(String ss) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ss.length(); i++) {
			char c = ss.charAt(i);
			if (c < 32 && c != 10 && c != 13) {
				// omit strange non-printing chars (shouldn't be there)
			} else {
				sb.append(c);
			}
		}
		ss = sb.toString();
		return ss;
	}

	public static void debugTextXML(AbstractCMElement textChunk, int length) {
		String s = textChunk.toXML();
		LOG.debug(s.substring(0, Math.min(s.length(), length)));
	}

	public static void debugTextValue(AbstractCMElement textChunk, int length) {
		String s = textChunk.getValue();
		LOG.debug(s.substring(0, Math.min(s.length(), length)));
	}

	public static void debugToFile(Element elem, File file) {
		try {
			SVGUtil.debug(elem, new FileOutputStream(file), 1);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Nodes query(AbstractCMElement svgElement, String xpath) {
		Nodes nodes = null;
		try {
			nodes = svgElement.query(xpath, XMLConstants.SVG_XPATH);
		} catch (Exception e) {
			throw new RuntimeException("Error in xpath: "+xpath);
		}
		return nodes;
	}
	
	public static AbstractCMElement readAndCreateSVGFromFile(String filename) {
		Element element = readAndCreateElement(filename);
		return SVGElement.readAndCreateSVG(element);
	}
	
	public static Element readAndCreateElement(String filename) {
		Element element = null;
		try {
			element = new Builder().build(filename).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read: "+filename, e);
		}
		return element;
	}

	public static File createFile(String filename) {
		File file = new File(filename);
		if (file.exists() && file.isDirectory()) {
			throw new RuntimeException("Must give filename, not directory name"+file.getAbsolutePath());
		}
		file.getParentFile().mkdirs();
		return file;
	}

	private static Element ensureSVGSVGElement(Element element) {
		if (element instanceof SVGElement) {
			if (!(element instanceof SVGSVG)) {
				SVGSVG svg = new SVGSVG();
				if (element.getParent() == null) {
					Document document = new Document(element);
				}
				element.getParent().replaceChild(element, svg);
				svg.appendChild(element);
				element = svg;
			}
		}
		return element;
	}

	public static void writeFileAsSVGSVGWithMouse(String filename, Element element) {
		try {
			File file = createFile(filename);
			SVGElement svgElement = (SVGElement) ensureSVGSVGElement(element);
			List<SVGElement> gList = SVGUtil.getQuerySVGElements(svgElement, "./svg:g");
			if (gList.size() > 0) {
				addZoomScript(svgElement);
				
				SVGG g = (SVGG) gList.get(0);
				Real2Range bbox = BoundingBoxManager.createExtendedBox(g, 10);
				if (bbox != null) {
					Real2 corner = bbox.getLLURCorners()[0];
					corner = corner.multiplyBy(-1);
					g.setTransform(new Transform2(new Vector2(corner)));
				}
			}
			SVGUtil.debug(svgElement, new FileOutputStream(file), 1);
			LOG.trace("Wrote file "+file.getAbsolutePath());
		} catch (Exception e) {
			throw new RuntimeException("Cannot write svg page "+filename, e);
		}
	}

	private static void addZoomScript(SVGElement svgElement) {
		svgElement.addNamespaceDeclaration(XMLConstants.XLINK_PREFIX, XMLConstants.XLINK_NS);
		SVGScript scriptRefElement = new SVGScript();
		scriptRefElement.setHRef(SVG_PAN_JS);
		svgElement.appendChild(scriptRefElement);
	}

	public static Double parseDouble(String value) {
		Double d = null;
		try {
			d = Double.valueOf(value);
		} catch (Exception e) {
			// deliberate
		}
		return d;
	}

	public static Integer parseInteger(String value) {
		Integer i = null;
		try {
			i = new Integer(value);
		} catch (Exception e) {
			// deliberate
		}
		return i;
	}

	/* formats coords
	 * changes the values to given precision
	 */
	public static void format(RealArray coords, int places) {
		Double range = coords.getRange().getRange();
		int ii = (int) Math.round(Math.log10(range)-0.5);
		int places1 = (int) Math.max(0, places - ii);
		coords.format(places1);
	}

}
