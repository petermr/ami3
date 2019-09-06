package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGEllipse;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;
import org.contentmine.graphics.svg.objects.SVGRhomb;
import org.contentmine.graphics.svg.objects.SVGTriangle;

/** extracts and tidies shapes read from SVG.
 * 
 * @author pm286
 *
 */
public class ShapeCache extends AbstractCache {
	private static final Logger LOG = Logger.getLogger(ShapeCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGPath> originalPathList;
	// this holds any paths that we can't convert
	private List<SVGPath> pathList;
	// derived
	private List<SVGCircle> circleList;
	private List<SVGEllipse> ellipseList;
	private List<SVGLine> lineList;
	private List<SVGPolygon> polygonList;
	private List<SVGPolyline> polylineList;
	private List<SVGRect> rectList;
	private List<SVGRhomb> rhombList;
	private List<SVGTriangle> triangleList;
	private List<SVGShape> unknownShapeList;
	private List<SVGShape> allShapeList;
	private List<List<SVGShape>> convertedShapeListList;
	private List<SVGShape> convertedShapeList;
	
	public ShapeCache(ComponentCache ownerCache) {
		super(ownerCache);
		init();
	}
	
	private void init() {
		pathList = new ArrayList<SVGPath>();
		unknownShapeList = new ArrayList<SVGShape>();
		
		circleList = new ArrayList<SVGCircle>();
		ellipseList = new ArrayList<SVGEllipse>();
		lineList = new ArrayList<SVGLine>();
		polygonList = new ArrayList<SVGPolygon>();
		polylineList = new ArrayList<SVGPolyline>();
		rectList = new ArrayList<SVGRect>();
		rhombList = new ArrayList<SVGRhomb>();
		triangleList = new ArrayList<SVGTriangle>();
	}
	
	/** converts paths to shapes.
	 * if it can convert to a shape, adds to the appropriate list, else adds as path/s
	 * to the pathList
	 * 
	 * @param paths
	 */
	public void convertToShapes(List<SVGPath> paths) {
		Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
		path2ShapeConverter.setSplitAtMoveCommands(ownerComponentCache.getSplitAtMove());
		convertedShapeListList = path2ShapeConverter.convertPathsToShapesAndSplitAtMoves(paths);
		// see 
		for (List<SVGShape> shapeList : convertedShapeListList) {
			for (SVGShape shape : shapeList) {
				if (shape instanceof SVGCircle) {
					addToListAndSetId(circleList, (SVGCircle) shape);
				} else if (shape instanceof SVGEllipse) {
					addToListAndSetId(ellipseList, (SVGEllipse) shape);
				} else if (shape instanceof SVGLine) {
					addToListAndSetId(lineList, (SVGLine) shape);
				} else if (shape instanceof SVGRhomb) {
					// must preceed superclass SVGPolygon
					addToListAndSetId(getOrCreateRhombList(), (SVGRhomb) shape);
				} else if (shape instanceof SVGTriangle) {
					// must preceed superclass SVGPolygon
//					addToListAndSetId(shapeList, (SVGLine) shape); // why?
					triangleList.add((SVGTriangle) shape);
				} else if (shape instanceof SVGRect) {
					addToListAndSetId(rectList, (SVGRect) shape);
				} else if (shape instanceof SVGPolygon) {
					addToListAndSetId(polygonList, (SVGPolygon) shape); // may be glyphs
				} else if (shape instanceof SVGPolyline) {
					addToListAndSetId(polylineList, (SVGPolyline) shape);
				} else if (shape instanceof SVGPath) {
					addToListAndSetId(pathList, (SVGPath) shape);
					LOG.trace("unprocessed shape: "+shape);
				} else {
					LOG.warn("Unexpected shape: "+shape.getClass()); 
					unknownShapeList.add(shape);
				}
			}
		}
		LOG.trace("polylines:: "+polylineList);
		return;
	}

	public List<SVGRhomb> getOrCreateRhombList() {
		if (rhombList == null) {
			rhombList = new ArrayList<SVGRhomb>();			
		}
		return rhombList;
	}

	/** form id as elementName.toLowerCase()+listcount
	 * 
	 * @param elementList
	 * @param element
	 */
	private <T extends SVGElement> void addToListAndSetId(List<T> elementList, T element) {
		elementList.add(element);
		// more logic here
		element.setId(element.getLocalName().toLowerCase() + elementList.size());
	}

	public List<SVGPath> getPathList() {
		return pathList;
	}

	public List<SVGCircle> getCircleList() {
		return circleList;
	}

	public List<SVGEllipse> getEllipseList() {
		return ellipseList;
	}

	public List<SVGLine> getLineList() {
		return lineList;
	}

	public List<SVGPolygon> getPolygonList() {
		return polygonList;
	}

	public List<SVGPolyline> getPolylineList() {
		return polylineList;
	}

	public List<SVGRect> getRectList() {
		return rectList;
	}

	public List<SVGTriangle> getTriangleList() {
		return triangleList;
	}

	public List<SVGShape> getShapeList() {
		return unknownShapeList;
	}

	public List<List<SVGShape>> getConvertedShapeListList() {
		return convertedShapeListList;
	}

	public List<SVGShape> getOrCreateConvertedShapeList() {
		if (convertedShapeList == null) {
			if (convertedShapeListList != null) {
				convertedShapeList = new ArrayList<SVGShape>();
				for (List<SVGShape> shapeList : convertedShapeListList) {
					convertedShapeList.addAll(shapeList);
				}
			}
		}
		LOG.trace("polylineList: "+polylineList);
		LOG.trace("converted shapes: "+convertedShapeList);
		return convertedShapeList;
	}

	@Override
	public String toString() {
		return "paths: " + pathList.size() 
		+ "; circles: "   + circleList.size()
		+ "; ellipses: "  + ellipseList.size()
		+ "; lines: "     + lineList.size() 
		+ "; polygons: "  + polygonList.size() 
		+ "; polylines: " + polylineList.size() 
		+ "; rects: "     + rectList.size() 
		+ "; shapes: "    + unknownShapeList.size();
	}

	public AbstractCMElement createSVGAnnotations() {
		SVGG g = new SVGG();
		addList(g, polylineList);
		addList(g, circleList);
		g.setFill("orange");
		return g;
	}
	
	public static void addList(AbstractCMElement g, List<? extends SVGElement> list) {
		for (AbstractCMElement element : list) {
			g.appendChild(element.copy());
		}
	}

	public void createListsOfShapes(AbstractCMElement svgElement) {
		List<SVGCircle> circles = SVGCircle.extractSelfAndDescendantCircles(svgElement);
		circleList.addAll(circles);
		List<SVGEllipse> ellipses = SVGEllipse.extractSelfAndDescendantEllipses(svgElement);
		ellipseList.addAll(ellipses);
		List<SVGLine> lines = SVGLine.extractSelfAndDescendantLines(svgElement);
		lineList.addAll(lines);
		List<SVGPolygon> polygons = SVGPolygon.extractSelfAndDescendantPolygons(svgElement);
		polygonList.addAll(polygons);
		List<SVGPolyline> polylines = SVGPolyline.extractSelfAndDescendantPolylines(svgElement);
		polylineList.addAll(polylines);
		List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(svgElement);
		rectList.addAll(rects);
		// these will be polygons
		List<SVGRhomb> rhombs = SVGRhomb.extractSelfAndDescendantRhombs(svgElement);
		rhombList.addAll(rhombs);
		
	}

	public void removeElementsOutsideBox(Real2Range positiveXBox) {
		SVGElement.removeElementsOutsideBox(circleList, positiveXBox);
		SVGElement.removeElementsOutsideBox(ellipseList, positiveXBox);
		SVGElement.removeElementsOutsideBox(lineList, positiveXBox);
		SVGElement.removeElementsOutsideBox(polylineList, positiveXBox);
		SVGElement.removeElementsOutsideBox(polygonList, positiveXBox);
		SVGElement.removeElementsOutsideBox(rectList, positiveXBox);
//		SVGElement.removeElementsOutsideBox(rhombList, positiveXBox);
		SVGElement.removeElementsOutsideBox(triangleList, positiveXBox);
		SVGElement.removeElementsOutsideBox(unknownShapeList, positiveXBox);
	}

	public void removeElementsInsideBox(Real2Range positiveXBox) {
		SVGElement.removeElementsInsideBox(circleList, positiveXBox);
		SVGElement.removeElementsInsideBox(ellipseList, positiveXBox);
		SVGElement.removeElementsInsideBox(lineList, positiveXBox);
		SVGElement.removeElementsInsideBox(polylineList, positiveXBox);
		SVGElement.removeElementsInsideBox(polygonList, positiveXBox);
		SVGElement.removeElementsInsideBox(rectList, positiveXBox);
//		SVGElement.removeElementsInsideBox(rhombList, positiveXBox);
		SVGElement.removeElementsInsideBox(triangleList, positiveXBox);
		SVGElement.removeElementsInsideBox(unknownShapeList, positiveXBox);
	}

	public void extractShapes(List<SVGPath> pathList, AbstractCMElement svgElement) {
		convertToShapes(pathList);
		LOG.trace("polylines: "+polylineList);
		createListsOfShapes(svgElement);
		removeElementsOutsideBox(ownerComponentCache.getPositiveXBox());
		LOG.trace("polylines: "+polylineList);
	}

	public AbstractCMElement debugToSVG(String outFilename) {
		SVGG g = new SVGG();
//		debug(g, originalPathList, "black", "yellow", 0.3);
//		private List<SVGPath> pathList;
		// derived
		debug(g, rectList, "black", "#ffff77", 0.2);
		debug(g, polygonList, "black", "orange", 0.3);
		debug(g, rhombList, "black", "#ff77ff", 0.3);
		debug(g, triangleList, "black", "#ffeeff", 0.3);
		debug(g, ellipseList, "black", "red", 0.3);
		debug(g, lineList, "cyan", "red", 0.3);
		debug(g, polylineList, "magenta", "green", 0.3);
		debug(g, circleList, "black", "blue", 0.3); // highest priority
		debug(g, pathList, "purple", "pink", 0.3);
		debug(g, unknownShapeList, "cyan", "orange", 0.3);
		
		writeDebug("shapes", outFilename, g);
		return g;
	}

	private void debug(AbstractCMElement g, List<? extends SVGElement> elementList, String stroke, String fill, double opacity) {
		for (AbstractCMElement e : elementList) {
			SVGShape shape = (SVGShape) e.copy();
			SVGShape shape1 = (SVGShape) shape.copy();
			Double strokeWidth = shape.getStrokeWidth();
			if (strokeWidth == null) strokeWidth = 0.2;
			double border = Math.max(strokeWidth, 1.5);
			if (shape instanceof SVGLine || shape instanceof SVGPolyline || shape instanceof SVGPath) {
				styleAndDraw(g, stroke, "none", opacity, strokeWidth+border, shape1);
				styleAndDraw(g, fill, "none", opacity, strokeWidth, shape);
			} else if (shape instanceof SVGCircle || shape instanceof SVGPolygon || shape instanceof SVGEllipse) {
				styleAndDraw(g, stroke, "none", opacity, strokeWidth+border, shape1);
				styleAndDraw(g, fill, "none", opacity, strokeWidth, shape);
			} else if (shape instanceof SVGRect || shape instanceof SVGTriangle) {
				styleAndDraw(g, stroke, "none", opacity, strokeWidth+border, shape1);
				styleAndDraw(g, stroke, fill, opacity, strokeWidth, shape);
			} else {
				styleAndDraw(g, stroke, "none", opacity, strokeWidth+border, shape1);
				styleAndDraw(g, stroke, fill, opacity, strokeWidth, shape);
			}
		}
	}

	private void styleAndDraw(AbstractCMElement g, String stroke, String fill, double opacity, double strokeWidth, SVGShape shape) {
		shape.setStroke(stroke);
		shape.setStrokeWidth(strokeWidth);
		shape.setFill(fill);
		shape.setOpacity(opacity);
		shape.addTitle(shape.getClass().getSimpleName());
		g.appendChild(shape);
		
	}

	/** the bounding box of the actual shape components
	 * The extent of the context (e.g. svgCache) may be larger
	 * @return the bounding box of the contained shape
	 */
	public Real2Range getBoundingBox() {
		getOrCreateAllShapeList();
		return getOrCreateBoundingBox(allShapeList);
	}

	public List<SVGShape> getOrCreateAllShapeList() {
		if (allShapeList == null) {
			allShapeList = new ArrayList<SVGShape>();
		// how to do this properly? help!
			addShapes(polygonList);
			addShapes(polylineList);
			addShapes(rectList);
			addShapes(rhombList);
			addShapes(triangleList);
			addShapes(ellipseList);
			addShapes(lineList);
			addShapes(circleList);
			addShapes(pathList);
		}
		return allShapeList;
	}

	private void addShapes(List<? extends SVGShape> shapeList) {
		for (SVGShape shape : shapeList) {
			allShapeList.add(shape);
		}
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateConvertedShapeList();
	}

	@Override
	public void clearAll() {
		superClearAll();
		originalPathList = null;
		pathList = null;
		circleList = null;
		ellipseList = null;
		lineList = null;
		polygonList = null;
		polylineList = null;
		rectList = null;
		rhombList = null;
		triangleList = null;
		unknownShapeList = null;
		allShapeList = null;
		convertedShapeListList = null;
		convertedShapeList = null;
	}

	public void add(SVGShape shape) {
		getOrCreateConvertedShapeList();
		convertedShapeList.add(shape);
	}

	public void addAll(List<SVGShape> shapeList) {
		getOrCreateConvertedShapeList();
		convertedShapeList.addAll(shapeList);
	}

}
