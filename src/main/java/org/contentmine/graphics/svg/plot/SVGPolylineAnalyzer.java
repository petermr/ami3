package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealArray.Monotonicity;
import org.contentmine.eucl.stml.STMLArray;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGMarker;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager.BoxEdge;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.contentmine.graphics.svg.util.GraphUtil;

import nu.xom.Attribute;
import nu.xom.Element;


public class SVGPolylineAnalyzer {

	private final static Logger LOG = Logger.getLogger(SVGPolylineAnalyzer.class);

	private static final String VERT = "VERT";
	private static final String HOR = "HOR";
	private static final String IS_SAME_AS = "isSameAs";
	private static final String MERGED = "merged";
	private static final String PLOT = "plot";
	private static final String POLYLINES = "extractedPolylines";
	private static final String XAXIS = "xaxis";
	private static final String YAXIS = "yaxis";
	private static final String ROLE = "role";

	private static final int PLACES = 2;
	

	private Double eps = 0.0000001;
	private Double eps1 = 0.01;
	private int lineCount;
	private int nodeCount;
	private List<SVGPolyline> polylines;
	private List<SVGLine> horizontalLineList;
	private List<SVGLine> verticalLineList;
	private List<SVGLine> zeroLineList;
	private List<SVGMarker> markerList;
	private List<SVGLine> unorientedLineList;
	private Map<Integer, List<SVGLine>> horizontalMap;
	private Map<Integer, List<SVGLine>> verticalMap;
	private boolean mergePolylinesAtContiguousEndPoints = true;
	private AbstractCMElement svgg;

	public SVGPolylineAnalyzer() {
	}

//	public void analyzePolylines() {
//		polylines = new ArrayList<SVGPolyline>();
//		List<SVGElement> svgElems = SVGUtil.getQuerySVGElements(
//				getSVGPage(), "/svg:svg/svg:g[@title='"+POLYLINES+"']/svg:polyline");
//		for (SVGElement svgElem : svgElems) {
//			polylines.add((SVGPolyline)svgElem);
//		}
//	}
	
	public void createNetwork() {
		indexHorizonalVertical();
	}
	
	private void indexHorizonalVertical() {
		zeroLineList = new ArrayList<SVGLine>();
		horizontalLineList = new ArrayList<SVGLine>();
		verticalLineList = new ArrayList<SVGLine>();
		unorientedLineList = new ArrayList<SVGLine>();
		markerList = new ArrayList<SVGMarker>();
		for (SVGPoly polyline : polylines) {
			splitIntoLinesAndIndex(polyline);
			addPoints(polyline);
		}
	}

	public void printLists(String title, Map<Integer, List<SVGLine>> map) {
		LOG.trace(title);
		List<Integer> ii = Arrays.asList(map.keySet().toArray(new Integer[0]));
		Collections.sort(ii);
		for (Integer i : ii) {
			LOG.trace(i+": "+((List<SVGLine>)map.get(i)).size());
		}
	}

	private void splitIntoLinesAndIndex(SVGPoly polyline) {
		List<SVGLine> lineList = polyline.createLineList();
		for (SVGLine line : lineList) {
			Real2Range bbox = line.getBoundingBox();
			
			if (line.isZero(eps)) {
				zeroLineList.add(line);
				SVGMarker marker = new SVGMarker(line.getXY(0));
				marker.getSymbol().setFill("yellow");
				((SVGCircle)marker.getSymbol()).setRad(5.0);
				markerList.add(marker);
			} else if  (line.isHorizontal(eps)) {
				horizontalLineList.add(line);
				addToHorizontalMap(line);
			} else if (line.isVertical(eps)) {
				verticalLineList.add(line);
				addToVerticalMap(line);
			} else {
				unorientedLineList.add(line);
			}
			line.format(PLACES);
		}
	}

	private void addToVerticalMap(SVGLine line) {
		ensureVerticalMap();
		double x = line.getReal2Range().getXMin();
		addLineToMap(line, x, verticalMap);
	}

	private void addToHorizontalMap(SVGLine line) {
		ensureHorizontalMap();
		double y = line.getReal2Range().getYMin();
		addLineToMap(line, y, horizontalMap);
	}

	private void addLineToMap(SVGLine line, double x, Map<Integer, List<SVGLine>> map) {
		Integer ix = new Integer((int)x);
		List<SVGLine> lineList = map.get(ix);
		if (lineList == null) {
			lineList = new ArrayList<SVGLine>();
			map.put(ix,  lineList);
		}
		lineList.add(line);
	}

	private void ensureVerticalMap() {
		if (verticalMap == null) {
			verticalMap = new HashMap<Integer, List<SVGLine>>();
		}
	}

	private void ensureHorizontalMap() {
		if (horizontalMap == null) {
			horizontalMap = new HashMap<Integer, List<SVGLine>>();
		}
	}

	private void addPoints(SVGPoly polyline) {
		List<SVGMarker> pointList = polyline.createMarkerList();
		for (SVGMarker point : pointList) {
			this.markerList.add(point);
		}
	}


	public void analyzePolylines(AbstractCMElement svgg, List<SVGPolyline> polylines) {
		this.svgg = svgg;
		// will fail
//		ChunkAnalyzerXOld chunkAnalyzerX = null;
//		ChunkAnalyzerX chunkAnalyzerX = this.pageEditorX.getCurrentChunkAnalyzer();
//		GraphPlotBox plotBox = chunkAnalyzerX.getPlotBox();
//		List<SVGRect> axisBoxList = SVGRect.extractRects(SVGUtil.getQuerySVGElements(
//				svgg, ".//svg:rect[@class='"+AxisAnalyzer.AXES_BOX+"']"));
//		for (SVGRect axisBox : axisBoxList) {
//			this.analyzePolylines(svgg, polylines, plotBox);
//		}
	}

	// FIXME not used or tested
	public void analyzePolylines(AbstractCMElement svgg, List<SVGPolyline> polylines, GraphPlotBox plotBox) {
		Real2Range boxRange = (plotBox == null) ? null : plotBox.getBoxRange();
		if (boxRange != null)
		for (SVGPoly polyline : polylines) {
			SVGG parentG = (SVGG) polyline.getParent();
			Real2Range polyBox = polyline.getBoundingBox();
			LOG.trace("Polyline "+polyBox);
			if (boxRange.includes(polyBox)) {
				Real2Array polylineCoords = polyline.getReal2Array();
				LOG.trace("COORDS "+polylineCoords.size());
				Axis horizontalAxis = plotBox.getHorizontalAxis();
				Axis verticalAxis = plotBox.getVerticalAxis();
				STMLArray xArray = createCoordinateArray(polylineCoords, horizontalAxis, LineOrientation.HORIZONTAL);
				if (xArray != null) {
					parentG.appendChild(xArray);
				}
				STMLArray yArray = createCoordinateArray(polylineCoords, verticalAxis, LineOrientation.VERTICAL);
				if (yArray != null) {
					parentG.appendChild(yArray);
				}
				parentG.setSVGClassName(PLOT);
//				polyline.setFill("blue");
				/// for debugging
				polyline.setStroke("red");
				polyline.setStrokeWidth(2.5);
			}
		}
	}

	private STMLArray createCoordinateArray(Real2Array polylineCoords, Axis axis, LineOrientation lineOrientation) {
		STMLArray array = null;
		if (axis != null) {
			RealArray coords = (LineOrientation.HORIZONTAL.equals(lineOrientation)) ? 
					polylineCoords.getXArray() : polylineCoords.getYArray();
			RealArray scaledCoords = extractAndScaleCoords(axis, coords);
			if (scaledCoords == null) {
				scaledCoords = coords;  // use original coords
			}
			array = new STMLArray(scaledCoords);
			String axisLabel = (LineOrientation.HORIZONTAL.equals(lineOrientation)) ? XAXIS : YAXIS;
			array.addAttribute(new Attribute(ROLE, axisLabel));
		}
		if (array == null) {
			LOG.warn("CANNOT MAKE AXIS: "+lineOrientation);
		}
		return array;
	}

	private RealArray extractAndScaleCoords(Axis axis, RealArray pixelCoords) {
		RealArray valueCoords = axis.createScaledArrayToRange(pixelCoords);
		if (valueCoords != null) {
			GraphUtil.format(valueCoords, PLACES);
		}
		return valueCoords;
	}
	
	private void addMarker(SVGLine line0, Map<Integer, List<SVGLine>> map, Real2 xy, String orient) {
		Double xx = ((HOR.equals(orient)) ? xy.getY() : xy.getX());
		Double yy = ((HOR.equals(orient)) ? xy.getX() : xy.getY());
		List<SVGLine> lineList = map.get((Integer) (int)(double)xx);
		if (lineList != null) {
			for (SVGLine line : lineList) {
				Double xx2 = (HOR.equals(orient)) ? line.getXY(0).getY() : line.getXY(0).getX();
				if (Real.isEqual(xx, xx2, eps1)) {
					Double yy0 = (HOR.equals(orient)) ? line.getXY(0).getX() : line.getXY(0).getY();
					Double yy1 = (HOR.equals(orient)) ? line.getXY(1).getX() : line.getXY(1).getY();
					if (GraphUtil.isInSegment(yy, yy0, yy1)) {
						SVGMarker marker = new SVGMarker(xx, yy);
						marker.addLine(line);
						marker.addLine(line0);
						markerList.add(marker);
					}
				}
			}
		}
	}

	public SVGSVG editSVG() {
		SVGSVG svg = new SVGSVG();
		AbstractCMElement g = new SVGG();
		svg.appendChild(g);
		for (SVGLine zeroLine : zeroLineList) {
			g.appendChild(new SVGLine(zeroLine));
			zeroLine.setStrokeWidth(5.0);
			zeroLine.setStroke("red");
		}
		for (SVGLine horLine : horizontalLineList) {
			g.appendChild(new SVGLine(horLine));
		}
		for (SVGLine verLine : verticalLineList) {
			g.appendChild(new SVGLine(verLine));
		}
		for (SVGMarker marker : markerList) {
//			g.appendChild(new SVGCircle(marker.getSymbol()));
			AbstractCMElement element = marker.getSymbol();
			if (element != null) {
				g.appendChild(element.copy());
			} else {
			}
		}
		
		return svg;
	}
	
	public static void getMatchedPoint(SVGPoly polyline, List<SVGPolyline> polylineList, Real2 end) {
		String col = "red";
		for (SVGPoly polyline1 : polylineList) {
			List<SVGLine> lineList = polyline1.createLineList();
			if (lineList.size() != 3) {
				continue;
			}
			SVGLine line1 = polyline1.createLineList().get(1);
			Real2 midpoint = line1.getEuclidLine().getMidPoint();
			if (end.getDistance(midpoint) < 1.0) {
				col = "green";
				break;
			}
		}
		SVGCircle circle = new SVGCircle(end, 2);
		circle.setFill(col);
		Element root = (Element) polyline.query("/*").get(0);
//		ParentNode parent = polyline.getParent();
		root.appendChild(circle);
	}

	public static void matchEnd(SVGPoly polyline, int line, int end, List<SVGPolyline> polylineList) {
		List<SVGLine> lineList = polyline.createLineList();
		Real2 endpt = lineList.get(line).getXY(end);
		SVGCircle circle = new SVGCircle(endpt, 10.0);
		polyline.getParent().appendChild(circle);
	}


	private void printPolylines(List<SVGPolyline> polylineList) {
		LOG.trace("polyline "+polylineList.size());
		for (SVGPolyline polyline : polylineList) {
			List<org.contentmine.graphics.svg.SVGLine> lineList = polyline.createLineList();
			int size = lineList.size();

			if (size == 1) {
				if (polyline.isAlignedWithAxes(0.01)) {
//					polyline.debug("aligned1");
				} else {
					polyline.debug("size1???");
				}
				// later?
			} else if (size == 3) {
					if (polyline.isAlignedWithAxes(0.01)) {
//						matchEnd(polyline, 0, 0, polylineList);
//						matchEnd(polyline, 2, 1, polylineList);
//						polyline.debug("aligned3");
					} else {
						// italic dash (closed)
//						polyline.debug("size3???");
					}
					// later?
			} else if (size == 4) {
				if (polyline.isBox(0.01)) {
//					polyline.debug("BOX");
				} else {
//					polyline.debug("size4??");
				}
			} else if (polyline.isAlignedWithAxes(0.01)) {
				if (size == 8) {
					// "T"
				} else if (size == 11) {
						// "+"
				} else {
					polyline.debug("aligned"+size);
				}
			} else {
				if (size != 5 && size != 8 && size != 10 && size != 11) {
					polyline.debug("size"+size);
				}
			}
		}
	}
	
	/** sort polylines along X and Y coords and find common points to merge lines
	 *  replace joined lines by common new line
	 */
	public void mergePolylinesAtContiguousEndPoints(double eps) {
		if (this.mergePolylinesAtContiguousEndPoints ) {
			mergePolylinesAtContigousEndPoints(Axis2.X, eps);
			mergePolylinesAtContigousEndPoints(Axis2.Y, eps);
		}
	}

	private void mergePolylinesAtContigousEndPoints(Axis2 axis, double eps) {
		while (true) {
			List<SVGElement> polylines0 = SVGUtil.getQuerySVGElements(svgg, ".//svg:polyline");
			LOG.trace("POL "+polylines0.size());
			List<SVGElement> polylines = SVGUtil.getQuerySVGElements(svgg, ".//svg:polyline[not(@"+MERGED+")]");
			if (polylines.size() == 0) {
				break;
			}
			mergePolylinesAtContiguousPoints(axis, eps, polylines);
		}
	}

	private void mergePolylinesAtContiguousPoints(Axis2 axis, double eps, List<SVGElement> polylines) {
		// will modify all polylines so they are monotonic increasing
		List<SVGPolyline> polylinesXIncreasing = getNormalizedMonotonicity(polylines, Monotonicity.INCREASING, axis);
		BoxEdge boxEdge = (Axis2.X.equals(axis)) ? BoxEdge.XMIN : BoxEdge.YMIN;
		List<SVGElement> sortedPolylines = BoundingBoxManager.getElementsSortedByEdge(polylinesXIncreasing, boxEdge);
		for (AbstractCMElement pp : sortedPolylines) {
			SVGPoly p = (SVGPoly) pp;
			LOG.trace(String.valueOf(p.getFirstCoordinate())+" ==> "+p.getLastCoordinate());
		}
		SVGPolyline newPolyline = null;
		Real2 lastXY = null;
		for (int i = 0; i < sortedPolylines.size(); i++) {
			SVGPoly polyline = (SVGPoly) sortedPolylines.get(i);
			if (newPolyline == null) {
				newPolyline = new SVGPolyline(polyline);
				polyline.getParent().replaceChild(polyline, newPolyline);
				newPolyline.addAttribute(new Attribute(MERGED, "true"));
			} else {
				Real2 firstXY = polyline.getFirstCoordinate();
				double delta = (axis.equals(Axis2.X)) ? 
						firstXY.getX() - lastXY.getX() : firstXY.getY() - lastXY.getY(); 
				if (delta > eps) { // no remaining lines in range
					break;
				} else if (delta < -eps) {
					// else skip overlapping lines
				} else if (firstXY.getDistance(lastXY) < eps) {
					newPolyline.appendIntoSingleLine(polyline, 1);
					LOG.trace("SIZE: "+newPolyline.getPointList().size());
					polyline.detach();
				}
			}
			lastXY = newPolyline.getLastCoordinate();
		}
		LOG.trace("new points "+newPolyline.getPointList().size());
//		newPolyline.debug("NEW POLY");
	}


	private List<SVGPolyline> getNormalizedMonotonicity(List<SVGElement> polylines, Monotonicity monotonicity, Axis2 axis) {
		List<SVGPolyline> polylineSubset = new ArrayList<SVGPolyline>();
		for (AbstractCMElement polylineE : polylines) {
			SVGPolyline polyline = (SVGPolyline) polylineE;
			Monotonicity monotonicity0  = polyline.getMonotonicity(axis);
			if (monotonicity0 != null) {
				if (!monotonicity.equals(monotonicity0)) {
					polyline.reverse();
				}
				polylineSubset.add(polyline);
			}
		}
		return polylineSubset;
 	} 

}
