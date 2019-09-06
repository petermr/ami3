package org.contentmine.graphics.svg.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;


public class SVGDiagram extends SVGG {

	public static final String DIAGRAM = "diagram";
	private static final Logger LOG = Logger.getLogger(SVGDiagram.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected List<SVGPath> svgPathList;
	protected List<SVGText> svgTextList;
	protected List<SVGTextBox> textBoxList;
	protected List<List<SVGShape>> shapeListList;
	protected AbstractCMElement newG;
	protected List<SVGTriangle> triangleList;
	protected List<SVGLine> lineList;
	protected List<SVGArrow> arrowList;
	protected double eps = 2.0; // pixel tolerance
	protected List<SVGRect> rectList;
	protected List<SVGConnector> connectorList;
	protected List<SVGPath> pathList;
	protected List<SVGPolyline> polylineList;
	protected List<SVGRoundedBox> roundedBoxList;
	protected AbstractCMElement rawDiagram;

	public SVGDiagram() {
		super();
		this.setSVGClassName(DIAGRAM);
	}

	protected void createShapes() {
		LOG.warn("SUGGEST moving createShapes to Caches");
		triangleList = new ArrayList<SVGTriangle>();
		lineList = new ArrayList<SVGLine>();
		polylineList = new ArrayList<SVGPolyline>();
		rectList = new ArrayList<SVGRect>();
		pathList = new ArrayList<SVGPath>();
		roundedBoxList = new ArrayList<SVGRoundedBox>();
		
		newG = new SVGG();
		for (List<SVGShape> shapeList : shapeListList) {
			for (SVGElement shape : shapeList) {
				if (shape == null) {
					continue;
				}
				shape.detach();
				if (shape instanceof SVGRect) {
					addNewRect((SVGRect)shape);
				} else if (shape instanceof SVGLine) {
					addNewLine((SVGLine)shape);
				} else if (shape instanceof SVGPolyline) {
					SVGPolyline polyline = (SVGPolyline) shape;
					if (polyline.isClosed() && polyline.getLineList().size() == 3) {
						SVGTriangle triangle = new SVGTriangle(polyline);
						addNewTriangle(triangle);
					} else {
						addNewPolyline(polyline);
					}
				} else if (shape instanceof SVGPath) {
					if (((SVGPath)shape).getOrCreateSignatureAttributeValue().equals("M")) {
	//					System.err.println("omitted M");
					} else {
						SVGRoundedBox roundedBox = SVGRoundedBox.createRoundedBox((SVGPath)shape);
						if (roundedBox != null) {
							addNewRoundedBox(roundedBox);
						} else {
							addNewPath((SVGPath)shape);
						}
					}
				} else {
//					System.err.println("Unknown shape "+shape);
					shape.setStroke("green");
					shape.setStrokeWidth(2.0);
					newG.appendChild(shape.copy());
				}
			}
		}
		polylineList = SVGPolyline.quadraticMergePolylines(polylineList, eps);
		lineList = SVGLine.normalizeAndMergeAxialLines(lineList, eps);
		LOG.trace("roundedBoxList: "+roundedBoxList.size());
		LOG.trace("paths: "+pathList.size());
		LOG.trace("polylines: "+polylineList.size());
		LOG.trace("lines "+lineList.size());
		LOG.trace("rects "+rectList.size());
		LOG.trace("triangles "+triangleList.size());
	}

	private void addNewPath(SVGPath path) {
		if (!containsPath(pathList, path, eps)) {
			pathList.add(path);
		}
	}

	private boolean containsPath(List<SVGPath> pathList, SVGPath path0, double delta) {
		for (SVGPath path : pathList) {
			if (path.hasEqualCoordinates(path0, delta)) {
				return true;
			}
		}
		return false;
	}

	private void addNewRoundedBox(SVGRoundedBox roundedBox) {
		if (!containsRoundedBox(roundedBoxList, roundedBox, eps)) {
			roundedBoxList.add(roundedBox);
		}
	}

	private boolean containsRoundedBox(List<SVGRoundedBox> roundedBoxList, SVGRoundedBox roundedBox0, double delta) {
		for (SVGRoundedBox roundedBox : roundedBoxList) {
			if (roundedBox.getPath().hasEqualCoordinates(roundedBox0.getPath(), delta)) {
				return true;
			}
		}
		return false;
	}

	private void addNewTriangle(SVGTriangle triangle) {
		if (!containsTriangle(triangleList, triangle, eps)) {
			triangleList.add(triangle);
		}
	}

	private boolean containsTriangle(List<SVGTriangle> triangleList, SVGTriangle triangle0, double delta) {
		for (SVGTriangle triangle : triangleList) {
			if (triangle.hasEqualCoordinates(triangle0, delta)) {
				return true;
			}
		}
		return false;
	}

	private void addNewPolyline(SVGPolyline polyline) {
		if (!containsPolyline(polylineList, polyline, eps)) {
			polylineList.add(polyline);
		}
	}

	private boolean containsPolyline(List<SVGPolyline> polylineList, SVGPolyline polyline0, double delta) {
		for (SVGPolyline polyline : polylineList) {
			if (polyline.hasEqualCoordinates(polyline0, delta)) {
				return true;
			}
		}
		return false;
	}

	private void addNewLine(SVGLine line) {
		if (!containsLine(lineList, line, eps)) {
			lineList.add(line);
		}
	}

	private boolean containsLine(List<SVGLine> lineList, SVGLine line0, double delta) {
		for (SVGLine line : lineList) {
			if (line.hasEqualCoordinates(line0, delta)) {
				return true;
			}
		}
		return false;
	}

	private void addNewRect(SVGRect rect) {
		if (!containsRect(rectList, rect, eps)) {
			rectList.add(rect);
		}
	}

	private boolean containsRect(List<SVGRect> rectList, SVGRect rect0, double delta) {
		for (SVGRect rect : rectList) {
			if (rect.isEqual(rect0, delta)) {
				return true;
			}
		}
		return false;
	}

	protected void createTextBoxes() {
		textBoxList = new ArrayList<SVGTextBox>();
		for (SVGRect rect : rectList) {
			Real2Range rect2R = rect.getBoundingBox();
			SVGTextBox textBox = null;
			for (SVGText text : svgTextList) {
				Real2Range text2R = text.getBoundingBox();
				if (rect2R.includes(text2R)) {
					if (textBox == null) {
						textBox = new SVGTextBox(rect);
						textBoxList.add(textBox);
					}
					text.detach();
					textBox.add(text);
				}
			}
		}
		LOG.trace("textBoxList "+textBoxList.size());
	}

	protected void createArrows(double delta) {
			arrowList = new ArrayList<SVGArrow>();
			LOG.trace("triangles "+triangleList.size());
			for (SVGTriangle triangle : triangleList) {
				LOG.trace("t "+triangle);
			}
			LOG.trace("lines "+lineList.size());
			for (SVGTriangle triangle : triangleList) {
				int j = 0;
				for (; j < lineList.size(); j++) {
					SVGLine line = lineList.get(j);
					SVGArrow arrow = SVGArrow.createArrow(line, triangle, delta);
					if (arrow != null) {
						LOG.trace("arrow "+arrow);
						newG.appendChild(arrow);
						line.detach();
						lineList.set(j, null);
						triangle.detach();
						arrowList.add(arrow);
						break;
					}
				}
				lineList.remove(j - 1);
			}
			LOG.trace("created arrows: "+arrowList.size());
		}

	protected List<SVGConnector> findConnectors(double delta) {
		connectorList = new ArrayList<SVGConnector>();
		List<SVGLine> arrowsAndLines = new ArrayList<SVGLine>();
		arrowsAndLines.addAll(arrowList);
		arrowsAndLines.addAll(lineList);
		for (SVGLine line : arrowsAndLines) {
			if (line != null) {
				SVGTextBox headBox = SVGTextBox.getTouchingBox(line.getXY(0), textBoxList, delta);
				if (headBox != null) {
					SVGTextBox tailBox = SVGTextBox.getTouchingBox(line.getXY(1), textBoxList, delta);
					if (tailBox != null) {
						SVGConnector link = new SVGConnector(tailBox, headBox);
						LOG.trace("LINK!! "+link);
						connectorList.add(link);
					}
				}
			}
		}
		return connectorList;
	}

	public AbstractCMElement getNewG() {
		return newG;
	}

	protected void createPathsTextAndShapes() {
		svgPathList = SVGPath.extractPaths(rawDiagram);
		svgTextList = SVGText.extractSelfAndDescendantTexts(rawDiagram);
		Path2ShapeConverter  converter = new Path2ShapeConverter();
		shapeListList = converter.convertPathsToShapesAndSplitAtMoves(svgPathList);
		this.createShapes();
	}

	public List<SVGTextBox> getTextBoxList() {
		ensureTextBoxList();
		return textBoxList;
	}

	private void ensureTextBoxList() {
		if (textBoxList == null) {
			textBoxList = new ArrayList<SVGTextBox>();
		}
	}

	public List<SVGArrow> getArrowList() {
		ensureArrowList();
		return arrowList;
	}

	private void ensureArrowList() {
		if (arrowList == null) {
			arrowList = new ArrayList<SVGArrow>();
		}
	}

	public void setArrowList(List<SVGArrow> arrowList) {
		this.arrowList = arrowList;
	}

	public List<SVGPath> getSVGPathList() {
		return svgPathList;
	}

	public List<SVGText> getSVGTextList() {
		return svgTextList;
	}

	public List<List<SVGShape>> getShapeListList() {
		return shapeListList;
	}

	public List<SVGTriangle> getSVGTriangleList() {
		return triangleList;
	}

	public List<SVGLine> getSVGLineList() {
		return lineList;
	}

	public List<SVGRect> getSVGRectList() {
		return rectList;
	}

	public List<SVGConnector> getSVGConnectorList() {
		return connectorList;
	}

	public List<SVGPolyline> getSVGPolylineList() {
		return polylineList;
	}

	public List<SVGRoundedBox> getSVGRoundedBoxList() {
		return roundedBoxList;
	}

	public AbstractCMElement getRawDiagram() {
		return rawDiagram;
	}



}
