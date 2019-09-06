package org.contentmine.graphics.svg.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Line2AndReal2Calculator;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.util.GrahamScan;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPathPrimitive;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.LineCache;
import org.contentmine.graphics.svg.cache.PathCache;
import org.contentmine.graphics.svg.cache.RectCache;
import org.contentmine.graphics.svg.cache.ShapeCache;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.CubicPrimitive;
import org.contentmine.graphics.svg.path.LinePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;

import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * Builds higher-level primitives from SVGPaths, SVGLines, etc. to create SVG objects 
 * such as TramLine and (later) Arrow.
 * 
 * <p>SimpleBuilder's main function is to:
 * <ul>
 * <li>Read a raw SVG object and make lists of SVGPath and SVGText (and possibly higher levels ones
 * if present.)</li>
 * <li>turn SVGPaths into SVGLines , etc.</li>
 * <li>identify Junctions (line-line, line-text, and probably more)</li>
 * <li>join lines where they meet into higher level objects (TramLines, SVGRect, crosses, arrows, etc.)</li>
 * <li>create topologies (e.g. connection of lines and Junctions)</li>
 * </ul>
 * 
 * SimpleBuilder uses the services of the org.contentmine.graphics.svg.path package and may later use
 * org.contentmine.graphics.svg.symbol.
 * </p>
 * 
 * <p>Input may either be explicit SVG primitives (e.g. &lt;svg:rect&gt;, &lt;svg:line&gt;) or 
 * implicit ones (&lt;svg:path&gt;) that can be interpreted as the above. The input may be either or
 * both - we can't control it. The implicit get converted to explicit and then merged with the 
 * explicit:
 * <pre>
 *    paths-> implicitLineList + rawLinelist -> explicitLineList 
 * </pre>
 * </p>
 * 
 * <h3>Strategy</h3>
 * <p>createHigherLevelPrimitives() carries out the complete chain from svgRoot to the final
 * primitives. Each step tests to see whether the result of the previous is null.
 * If so it creates a non-null list and fills it if possible. </p>
 * 
 * UPDATE: 2013-10-23 Renamed to "SimpleGeometryManager" as it doesn't deal with Words (which
 * require TextStructurer). It's possible the whole higher-level primitive stuff should be removed to another
 * project.
 * 
 * 
 * UPDATE 2017-10-08 Moved to svghtml and refactored to use Caches.
 * Currently regenerates caches, but should later read them in and then move
 * the methods to be part of the Cache system. Currently compiles but hasn't been tested.
 * There are some interesting building methods that will probably be useful later
 * 
 * @author pm286
 */
public class SimpleBuilder {

	private static final double MAX_AREA_CHANGE_FOR_POLYGON = 0.65;

	public class Line2LengthComparator implements Comparator<Line2> {
		public int compare(Line2 o1, Line2 o2) {
			return (int) (o1.getLength() - o2.getLength());
		}
	}

	private static final double DEFAULT_POINT_EQUIVALENCE_EPSILON = 1e-8;
	private static final double DEFAULT_NEAR_DUPLICATE_REMOVAL_DISTANCE_FOR_LINES = 0.5;//0.35;//0.6;Should maybe only apply to things that were originally lines, or alternatively duplication should look at line overlap
	private static final double DEFAULT_NEAR_DUPLICATE_REMOVAL_DISTANCE_FOR_POLYGONS = 0.6;
	private static final double DEFAULT_MAX_ANGLE_FOR_CORNER = 155;//This was 110 for the old algorithm
	private static final double CUT_LINE_SEWING_DEFAULT_ANGLE_TOLERANCE = 4;
	private static final double CUT_OBJECT_SEWING_DEFAULT_MAXIMUM_GAP = 6;
	private static final double CUT_OBJECT_SEWING_DEFAULT_MINIMUM_GAP = 1.3;//0.5;
	private static final int POLYGON_ABSTRACTION_DEFAULT_MAXIMUM_NUMBER_OF_EDGES = 250;//200;

	private final static Logger LOG = Logger.getLogger(SimpleBuilder.class);
	
	protected AbstractCMElement svgRoot;
	

	protected static double pointEquivalenceEpsilon = DEFAULT_POINT_EQUIVALENCE_EPSILON;
	protected double nearDuplicateLineRemovalDistance = DEFAULT_NEAR_DUPLICATE_REMOVAL_DISTANCE_FOR_LINES;
	protected double nearDuplicatePolygonRemovalDistance = DEFAULT_NEAR_DUPLICATE_REMOVAL_DISTANCE_FOR_POLYGONS;
	protected double maxAngleForCorner = DEFAULT_MAX_ANGLE_FOR_CORNER;
	protected double parallelTolerance = CUT_LINE_SEWING_DEFAULT_ANGLE_TOLERANCE;
	protected double maximumCutObjectGap = CUT_OBJECT_SEWING_DEFAULT_MAXIMUM_GAP;
	protected double minimumCutObjectGap = CUT_OBJECT_SEWING_DEFAULT_MINIMUM_GAP;
	protected int maximumEdgesForAbstraction = POLYGON_ABSTRACTION_DEFAULT_MAXIMUM_NUMBER_OF_EDGES;
	
	private Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
	
	protected long timeout;
	protected long startTime;
	private PathCache pathCache;
	private ShapeCache shapeCache;
	private LineCache lineCache;
	private RectCache rectCache;
	private double maxAreaChangeRatio = MAX_AREA_CHANGE_FOR_POLYGON;

	public SimpleBuilder(AbstractCMElement svgRoot) {
		setSvgRoot(svgRoot);
		this.timeout = Long.MAX_VALUE;
	}

	public SimpleBuilder(AbstractCMElement svgRoot, long timeout) {
		setSvgRoot(svgRoot);
		this.timeout = timeout;
	}

	public SimpleBuilder() {
	}

	public void setSvgRoot(AbstractCMElement svgRoot) {
		this.svgRoot = svgRoot;
		makeCaches(svgRoot);
	}

	/** temporary; creates temporary caches .
	 * later will merge them into mainstream methods.
	 * 
	 * @param svgRoot
	 */
	private void makeCaches(AbstractCMElement svgRoot) {
		ComponentCache componentCache = new ComponentCache();
		componentCache.readGraphicsComponentsAndMakeCaches(svgRoot);
		componentCache.getOrCreateCascadingCaches();
		pathCache = componentCache.getOrCreatePathCache();
		shapeCache = componentCache.getOrCreateShapeCache();
		lineCache = componentCache.getOrCreateLineCache();
		rectCache = componentCache.getOrCreateRectCache();
	}
	
	protected void startTiming() {
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
	}
	
	protected void checkTime(String message) {
		if (System.currentTimeMillis() - startTime >= timeout) {
			LOG.warn("timeout: " + message);
			throw new UncheckedTimeoutException(message);
		}
	}
	
	/**
	 * Turns SVGPaths into higher-level primitives such as SVGLine.
	 * much of this may be duplicated by the Caches and I expect this method
	 * to be gradually retired
	 * 
	 */
	public void createDerivedPrimitives() {
		removeZeroLengthLinesAndEmptyPaths();
		convertPathsToShapes();
		convertPolylinesToPolygonsOrSetsOfLines();
		convertUnfilledPolygonsToSetsOfLines();
		removeWhiteFilledPolygons();
		convertLinelikePolygonsToLines();
		List<SVGPath> pathsIt = pathCache.getCurrentPathList();
		for (SVGPath path : pathsIt) {
//			PathPrimitiveList primitives = path.parseDString();
			PathPrimitiveList primitives = path.getOrCreatePathPrimitiveList();
			PathPrimitiveList newPrimitives = new PathPrimitiveList();
			for (SVGPathPrimitive primitive : primitives) {
				if (primitive instanceof CubicPrimitive) {
					primitive = new LinePrimitive(((CubicPrimitive) primitive).getLastCoord());
				}
				newPrimitives.add(primitive);
			}
			SVGPath newPath = new SVGPath(newPrimitives, path);
			//SVGShape shape = path2ShapeConverter.convertPathToShape(newPath);
			SVGPoly polyline = newPath.createPolyline();
			if (polyline == null) {
				pathsIt.remove(path);
				continue;
			}
			SVGPolygon polygon = ((SVGPolyline) polyline).createPolygon(pointEquivalenceEpsilon);
			if (polygon == null) {
				polyline.setClosed(true);
				polygon = ((SVGPolyline) polyline).createPolygon(pointEquivalenceEpsilon);
			}
			if (polygon.createLineList(true).size() < maximumEdgesForAbstraction) {
				normalizeToSmallestMeaningfulPoly(polygon, true);
			}
			if (polygon.createLineList(true).size() == 3 || polygon.createLineList(true).size() == 4) {
				pathsIt.remove(polygon);
				shapeCache.getPolygonList().add(polygon);
			}
		}
		removeNearDuplicateAndObscuredPrimitives();
		sewTogetherCutPolygons();
		getSmallestMeaningfulPolygons();
		convertLinelikePolygonsToLines();
		removeNearDuplicateAndObscuredPrimitives();
		sewTogetherCutLines();
	}

	private void convertLinelikePolygonsToLines() {
		List<SVGPolygon> polygonsIt = shapeCache.getPolygonList();
		for (int i = polygonsIt.size() - 1; i >= 0; i--) {
			SVGPolygon polygon = polygonsIt.get(i);
			SVGLine line = path2ShapeConverter.createNarrowLine(polygon);
			if (line != null) {
				lineCache.add(line);
				polygonsIt.remove(polygon);
			}
		}
		List<SVGRect> rectsIt = rectCache.getOrCreateRectList();
		for (int i = rectsIt.size() - 1; i >= 0; i--) {
			try {
				SVGRect rect = rectsIt.get(i);
				Real2[] r = rect.getBoundingBox().getLLURCorners();
				SVGPolygon polygon = new SVGPolygon(new Real2Array(Arrays.asList(r[0], new Real2(r[1].getX(), r[0].getY()), r[1], new Real2(r[0].getX(), r[1].getY()))));
				SVGLine line = path2ShapeConverter.createNarrowLine(polygon);
				if (line != null) {
					lineCache.add(line);
					rectsIt.remove(rect);
				}
			} catch (NumberFormatException e) {
				LOG.error("unexpected NFE: "+e);
				continue;
			}
		}
	}

	private void removeZeroLengthLinesAndEmptyPaths() {
		List<SVGLine> lineList = lineCache.getOrCreateLineList().getLineList();
		for (int i = lineList.size() - 1; i >= 0; i--) {
			SVGLine line = lineList.get(i);
			if (Real.isZero(line.getLength(), Real.getEpsilon())) {
				lineList.remove(i);
			}
		}
		List<SVGPath> pathList = pathCache.getCurrentPathList();
		// this may be wrong - 
		for (int i = pathList.size() - 1; i >= 0; i--) {
			SVGPath line = pathList.get(i);
			for (SVGPathPrimitive p : line.getOrCreatePathPrimitiveList()) {
				if (!(p instanceof ClosePrimitive) && !(p instanceof MovePrimitive)) {
					continue;
				} else {
					pathList.remove(i);
				}
			}
		}
	}

	private void removeWhiteFilledPolygons() {
		List<SVGPolygon> polygonsList = shapeCache.getPolygonList();
		for (int i = polygonsList.size() - 1; i >= 0; i--) {
			SVGPolygon polygon = polygonsList.get(i);
			if ((polygon.getStyle() != null && polygon.getStyle().contains("fill:#ffffff")) ||
					(polygon.getAttribute("fill") != null && polygon.getAttribute("fill").getValue().equals("#ffffff"))) {
				polygonsList.remove(i);
			}
		}
	}

	protected void sewTogetherCutLines() {
		List<SVGLine> allLines = new ArrayList<SVGLine>(lineCache.getOrCreateLineList().getLineList());
		for (SVGPolygon polygon : shapeCache.getPolygonList()) {
			allLines.addAll(polygon.createLineList(true));
		}
		
		List<SVGLine> lines1 = lineCache.getOrCreateLineList().getLineList();
		for (int iLine1 = 0; iLine1 < lines1.size() - 1; iLine1--) {
			SVGLine line1 = lines1.get(iLine1);
			line2: for (SVGLine line2: lineCache.getOrCreateLineList()) {
				if (line1 == line2) {
					continue;
				}
				checkTime("Took too long to check for cut lines");
				Real2 outerEnd1 = null;
				Real2 outerEnd2 = null;
				Real2 innerEnd1 = null;
				Real2 innerEnd2 = null;
				double shortestDistance = Double.MAX_VALUE;
				double dist1 = line1.getXY(0).getDistance(line2.getXY(0));
				if (dist1 < shortestDistance) {
					outerEnd1 = line1.getXY(1);
					outerEnd2 = line2.getXY(1);
					innerEnd1 = line1.getXY(0);
					innerEnd2 = line2.getXY(0);
					shortestDistance = dist1;
				}
				double dist2 = line1.getXY(0).getDistance(line2.getXY(1));
				if (dist2 < shortestDistance) {
					outerEnd1 = line1.getXY(1);
					outerEnd2 = line2.getXY(0);
					innerEnd1 = line1.getXY(0);
					innerEnd2 = line2.getXY(1);
					shortestDistance = dist2;
				}
				double dist3 = line1.getXY(1).getDistance(line2.getXY(0));
				if (dist3 < shortestDistance) {
					outerEnd1 = line1.getXY(0);
					outerEnd2 = line2.getXY(1);
					innerEnd1 = line1.getXY(1);
					innerEnd2 = line2.getXY(0);
					shortestDistance = dist3;
				}
				double dist4 = line1.getXY(1).getDistance(line2.getXY(1));
				if (dist4 < shortestDistance) {
					outerEnd1 = line1.getXY(0);
					outerEnd2 = line2.getXY(0);
					innerEnd1 = line1.getXY(1);
					innerEnd2 = line2.getXY(1);
					shortestDistance = dist4;
				}
				double line1Length = line1.getXY(0).getDistance(line1.getXY(1));
				double line2Length = line2.getXY(0).getDistance(line2.getXY(1));
				//double hypotheticalLineLength = line1Length + shortestDistance + line2Length;
				if (shortestDistance < maximumCutObjectGap && line1.isParallelOrAntiParallelTo(line2, new Angle(parallelTolerance, Units.DEGREES))) {
					SVGLine newLine = new SVGLine(outerEnd1, outerEnd2);
					Line2 testLine = new Line2(innerEnd1, innerEnd2);
					boolean makingLongerLine = (newLine.getLength() > line1Length && newLine.getLength() > line2Length);//TODO
					if (makingLongerLine && line1.isParallelOrAntiParallelTo(newLine, new Angle(parallelTolerance, Units.DEGREES)) && line2.isParallelOrAntiParallelTo(newLine, new Angle(parallelTolerance, Units.DEGREES))) {
						if (shortestDistance < minimumCutObjectGap) {
							if (shortestDistance < pointEquivalenceEpsilon) {
								//TODO why was this here?
								/*line2.setXY(newLine.getXY(0), 0);
								line2.setXY(newLine.getXY(1), 1);
								lines1.remove();
								break line2;*/
							}
						} else {
							for (SVGLine line3: allLines) {
								if (line3 == line1 || line3 == line2) {
									continue;
								}
								if (lineInWay(line3, testLine)) {
									line2.setXY(newLine.getXY(0), 0);
									line2.setXY(newLine.getXY(1), 1);
									lines1.remove(iLine1);
									break line2;
								}
							}
						}
					}
				}			
			}
		}
		
		/*List<SVGLine> newLines = new ArrayList<SVGLine>();
		for (SVGLine line1: derivedPrimitives.getLineList()) {
			for (SVGLine line2: derivedPrimitives.getLineList()) {
				if ((line1.getXY(0).getDistance(line2.getXY(0)) < maximumCutLineGap || line1.getXY(0).getDistance(line2.getXY(1)) < maximumCutLineGap || line1.getXY(1).getDistance(line2.getXY(0)) < maximumCutLineGap || line1.getXY(1).getDistance(line2.getXY(1)) < maximumCutLineGap) && line1.isParallelOrAntiParallelTo(line2, new Angle(parallelTolerance, Units.DEGREES))) {
					
				}
			}
		}*/
	}

	protected void sewTogetherCutPolygons() {
		Iterator<SVGPolygon> polygonsIt = shapeCache.getPolygonList().iterator();
		outer: while (polygonsIt.hasNext()) {
			SVGPolygon polygon1 = polygonsIt.next();
			List<SVGLine> p1Lines = polygon1.createLineList(true);
			SVGLine p1LongestLine = null;
			SVGLine p1SecondLongestLine = null;
			double p1LongestLineLength = 0;
			double p1SecondLongestLineLength = 0;
			for (SVGLine line : p1Lines) {
				Double length = line.getXY(0).getDistance(line.getXY(1));//TODO
				if (length > p1LongestLineLength) {
					p1SecondLongestLineLength = p1LongestLineLength;
					p1LongestLineLength = length;
					p1SecondLongestLine = p1LongestLine;
					p1LongestLine = line;
				} else if (length > p1SecondLongestLineLength) {
					p1SecondLongestLineLength = length;
					p1SecondLongestLine = line;
				}
			}
			for (SVGPolygon polygon2 : shapeCache.getPolygonList()) {
				if (polygon2 == polygon1) {
					continue;
				}
				List<SVGLine> p2Lines = polygon2.createLineList(true);
				SVGLine p2LongestLine = null;
				SVGLine p2SecondLongestLine = null;
				double p2LongestLineLength = 0;
				double p2SecondLongestLineLength = 0;
				for (SVGLine line : p2Lines) {
					Double length = line.getXY(0).getDistance(line.getXY(1));//TODO
					if (length > p2LongestLineLength) {
						p2SecondLongestLineLength = p2LongestLineLength;
						p2LongestLineLength = length;
						p2SecondLongestLine = p2LongestLine;
						p2LongestLine = line;
					} else if (length > p2SecondLongestLineLength) {
						p2SecondLongestLineLength = length;
						p2SecondLongestLine = line;
					}
				}
				
				Line2 testLine1 = findShortestLineBetweenEndsOfLinePair(p1LongestLine, p2LongestLine);
				Line2 testLine2 = findShortestLineBetweenEndsOfLinePair(p1LongestLine, p2SecondLongestLine);
				Line2 testLine3 = findShortestLineBetweenEndsOfLinePair(p1SecondLongestLine, p2LongestLine);
				Line2 testLine4 = findShortestLineBetweenEndsOfLinePair(p1SecondLongestLine, p2SecondLongestLine);
				
				boolean test1 = testLine1.isParallelOrAntiParallelTo(p1LongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES)) && testLine1.isParallelOrAntiParallelTo(p2LongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES));
				boolean test2 = testLine2.isParallelOrAntiParallelTo(p1LongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES)) && testLine2.isParallelOrAntiParallelTo(p2SecondLongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES));
				boolean test3 = testLine3.isParallelOrAntiParallelTo(p1SecondLongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES)) && testLine3.isParallelOrAntiParallelTo(p2LongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES));
				boolean test4 = testLine4.isParallelOrAntiParallelTo(p1SecondLongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES)) && testLine4.isParallelOrAntiParallelTo(p2SecondLongestLine.getEuclidLine(), new Angle(parallelTolerance, Units.DEGREES));
				List<Line2> joiningLines = new ArrayList<Line2>();
				if (test1) {
					joiningLines.add(testLine1);
				}
				if (test2) {
					joiningLines.add(testLine2);
				}
				if (test3) {
					joiningLines.add(testLine3);
				}
				if (test4) {
					joiningLines.add(testLine4);
				}
				Real2 intersection = null;
				try {
					intersection = joiningLines.get(0).getIntersection(joiningLines.get(1));
				} catch (Exception e) {
					
				}
				
				boolean testFirstPairOfLines = (test1 && testLine1.getLength() < maximumCutObjectGap && testLine1.getLength() > minimumCutObjectGap) || (test2 && testLine2.getLength() < maximumCutObjectGap && testLine2.getLength() > minimumCutObjectGap);
				boolean testSecondPairOfLines = (test3 && testLine3.getLength() < maximumCutObjectGap && testLine4.getLength() > minimumCutObjectGap) || (test4 && testLine4.getLength() < maximumCutObjectGap && testLine4.getLength() > minimumCutObjectGap);
				if (testFirstPairOfLines && testSecondPairOfLines) {
					boolean linesDoNotIntersect = (intersection == null || (joiningLines.get(0).getLambda(intersection) < 0 || joiningLines.get(0).getLambda(intersection) > 1) && (joiningLines.get(1).getLambda(intersection) < 0 || joiningLines.get(1).getLambda(intersection) > 1));
					if (linesDoNotIntersect) {
						List<SVGLine> allLines = new ArrayList<SVGLine>(lineCache.getOrCreateLineList().getLineList());
						for (SVGPolygon polygon : shapeCache.getPolygonList()) {
							if (polygon != polygon1 && polygon != polygon2) {
								allLines.addAll(polygon.createLineList(true));
							}
						}
						
						for (SVGLine line3: allLines) {
							if (lineInWay(line3, joiningLines.get(0)) || lineInWay(line3, joiningLines.get(0))) {
								Real2Array newPoints = getConvexHull(polygon1.getReal2Array(), polygon2.getReal2Array());
								polygon2.setReal2Array(newPoints);
								polygonsIt.remove();
								continue outer;
							}	
						}
					}
				}
			}
		}
	}

	private boolean lineInWay(SVGLine line3, Line2 testLine) {
		Real2 intersection = null;
		try {
			intersection = testLine.getIntersection(line3.getEuclidLine());
		} catch (Exception e) {
			
		}
		double line3Length = line3.getXY(0).getDistance(line3.getXY(1));
		double testLineLength = testLine.getLength();
		boolean smallLineInWay = line3.getXY(0).getDistance(testLine.getMidPoint()) < testLineLength / 2 && line3.getXY(1).getDistance(testLine.getMidPoint()) < testLineLength / 2;
		boolean lineInWay = intersection != null && testLine.getLambda(intersection) > 0 && testLine.getLambda(intersection) < 1 && line3.getEuclidLine().getLambda(intersection) > testLineLength / (line3Length * 2) && line3.getEuclidLine().getLambda(intersection) < (1 - testLineLength / (line3Length * 2));
		return lineInWay || smallLineInWay;
	}

	private Real2Array getConvexHull(Real2Array... polygons) {
		Real2Array allPointArray = new Real2Array();
		for (Real2Array polygon : polygons) {
			allPointArray.add(polygon);
		}
		GrahamScan hullCreator = new GrahamScan(allPointArray);
		Real2Array newPoints = hullCreator.createHull();
		return newPoints;
	}
	
	private Line2 findShortestLineBetweenEndsOfLinePair(SVGLine line1, SVGLine line2) {
		List<Line2> testLineCandidates = new ArrayList<Line2>();
		testLineCandidates.add(new Line2(line1.getXY(0), line2.getXY(0)));
		testLineCandidates.add(new Line2(line1.getXY(0), line2.getXY(1)));
		testLineCandidates.add(new Line2(line1.getXY(1), line2.getXY(0)));
		testLineCandidates.add(new Line2(line1.getXY(1), line2.getXY(1)));
		Collections.sort(testLineCandidates, new Line2LengthComparator());
		return testLineCandidates.get(0);
	}

	protected void removeNearDuplicateAndObscuredPrimitives() {
		Iterator<SVGPolygon> i = shapeCache.getPolygonList().iterator();
		while (i.hasNext()) {
			SVGPolygon p = i.next();
			Iterator<SVGLine> j = lineCache.getOrCreateLineList().iterator();
			while (j.hasNext()) {
				checkTime("Took too long to look for near-duplicate and obscured primitives");
				SVGLine l = j.next();
				Real2Range box = p.getBoundingBox().getReal2RangeExtendedInX(nearDuplicateLineRemovalDistance, nearDuplicateLineRemovalDistance).getReal2RangeExtendedInY(nearDuplicateLineRemovalDistance, nearDuplicateLineRemovalDistance);
				boolean allPolygonPointsNearLinePoints = true;
				for (Real2 point : p.getReal2Array()) {
					if (point.getDistance(l.getXY(0)) > nearDuplicateLineRemovalDistance && point.getDistance(l.getXY(1)) > nearDuplicateLineRemovalDistance) {
						allPolygonPointsNearLinePoints = false;
						break;
					}
				}
				if (allPolygonPointsNearLinePoints) {
					i.remove();
					break;
				}
				if (box.includes(l.getXY(0)) && box.includes(l.getXY(1))){
					if (p.containsPoint(l.getXY(0), nearDuplicateLineRemovalDistance) && p.containsPoint(l.getXY(0), nearDuplicateLineRemovalDistance)) {
						j.remove();
					}
				}
			}
		}
		removeNearDuplicateLines();
		removeNearDuplicatePolygons();
		/*for (SVGRect p : derivedPrimitives.getRectList()) {
			Iterator<SVGLine> i = derivedPrimitives.getLineList().iterator();
			while (i.hasNext()) {
				SVGLine l = i.next();
				if (p.includes(l)) {
					i.remove();
				}
			}
		}*/
	}

	private void removeNearDuplicateLines() {
		Iterator<SVGLine> j = lineCache.getOrCreateLineList().iterator();
		while (j.hasNext()) {
			SVGLine line1 = j.next();
			for (SVGLine line2 : lineCache.getOrCreateLineList()) {
				checkTime("Took too long to look for near-duplicate and obscured primitives");
				if (line1 != line2) {
					int[] matchings = getEndMatchings(line1, line2);
					if (matchings[0] == 1 && matchings[1] == 0 && matchings[2] == 0 && matchings[3] == 1 || matchings[0] == 0 && matchings[1] == 1 && matchings[2] == 1 && matchings[3] == 0) {
						j.remove();
						break;
					} else if (matchings[0] + matchings[1] + matchings[2] + matchings[3] == 1) {
						int end = 0;
						if (matchings[0] == 1 || matchings[1] == 1) {
							end = 1;
						}
						Line2AndReal2Calculator calc = new Line2AndReal2Calculator(line2.getEuclidLine(), line1.getXY(end));
						if (calc.minimumDistance < nearDuplicateLineRemovalDistance) {
							j.remove();
							break;
						}
					}
				}
			}
		}
	}

	private int[] getEndMatchings(SVGLine line1, SVGLine line2) {
		int[] array = new int[4];
		array[0] = 0;
		array[1] = 0;
		array[2] = 0;
		array[3] = 0;
		if (end0AndEnd0FairlyClose(line1, line2) && end0AndEnd0Close(line1, line2)) {
			array[0] = 1;
		}
		if (end0AndEnd1FairlyClose(line1, line2) && end0AndEnd1Close(line1, line2)) {
			array[1] = 1;
		}
		if (end1AndEnd0FairlyClose(line1, line2) && end1AndEnd0Close(line1, line2)) {
			array[2] = 1;
		}
		if (end1AndEnd1FairlyClose(line1, line2) && end1AndEnd1Close(line1, line2)) {
			array[3] = 1;
		}
		return array;
	}

	private boolean end0AndEnd0Close(SVGLine line1, SVGLine line2) {
		return line1.getXY(0).getDistance(line2.getXY(0)) < nearDuplicateLineRemovalDistance;
	}

	private boolean end1AndEnd1Close(SVGLine line1, SVGLine line2) {
		return line1.getXY(1).getDistance(line2.getXY(1)) < nearDuplicateLineRemovalDistance;
	}

	private boolean end1AndEnd0Close(SVGLine line1, SVGLine line2) {
		return line1.getXY(1).getDistance(line2.getXY(0)) < nearDuplicateLineRemovalDistance;
	}

	private boolean end0AndEnd1Close(SVGLine line1, SVGLine line2) {
		return line1.getXY(0).getDistance(line2.getXY(1)) < nearDuplicateLineRemovalDistance;
	}

	private boolean end0AndEnd0FairlyClose(SVGLine line1, SVGLine line2) {
		return Math.abs(line1.getXY(0).getX() - line2.getXY(0).getX()) < nearDuplicateLineRemovalDistance && Math.abs(line1.getXY(0).getY() - line2.getXY(0).getY()) < nearDuplicateLineRemovalDistance;
	}

	private boolean end1AndEnd1FairlyClose(SVGLine line1, SVGLine line2) {
		return Math.abs(line1.getXY(1).getX() - line2.getXY(1).getX()) < nearDuplicateLineRemovalDistance && Math.abs(line1.getXY(1).getY() - line2.getXY(1).getY()) < nearDuplicateLineRemovalDistance;
	}

	private boolean end1AndEnd0FairlyClose(SVGLine line1, SVGLine line2) {
		return Math.abs(line1.getXY(1).getX() - line2.getXY(0).getX()) < nearDuplicateLineRemovalDistance && Math.abs(line1.getXY(1).getY() - line2.getXY(0).getY()) < nearDuplicateLineRemovalDistance;
	}

	private boolean end0AndEnd1FairlyClose(SVGLine line1, SVGLine line2) {
		return Math.abs(line1.getXY(0).getX() - line2.getXY(1).getX()) < nearDuplicateLineRemovalDistance && Math.abs(line1.getXY(0).getY() - line2.getXY(1).getY()) < nearDuplicateLineRemovalDistance;
	}

	private void removeNearDuplicatePolygons() {
		Iterator<SVGPolygon> j = shapeCache.getPolygonList().iterator();
		while (j.hasNext()) {
			SVGPolygon polygon1 = j.next();
			polygon2: for (SVGPolygon polygon2 : shapeCache.getPolygonList()) {
				checkTime("Took too long to look for near-duplicate and obscured primitives");
				if (polygon1 != polygon2) {
					//boolean allPointsHaveASimilarPoint = true;
					real21: for (Real2 real21 : polygon2.getReal2Array()) {
						for (Real2 real22 : polygon1.getReal2Array()) {
							if (real21.getDistance(real22) < nearDuplicatePolygonRemovalDistance) {
								continue real21;
							}
						}
						continue polygon2;
					}
					//TODO why was the below if here?
					//if (polygon1.getReal2Array().size() > polygon2.getReal2Array().size()) {
					j.remove();
					//}
					break;
				}
			}
		}
	}

	private void convertPolylinesToPolygonsOrSetsOfLines() {
		Iterator<SVGPolyline> i = shapeCache.getPolylineList().iterator();
		while (i.hasNext()) {
			SVGPolyline polyline = i.next();
			SVGPolygon polygon1 = polyline.createPolygon(pointEquivalenceEpsilon);
			if (polygon1 != null) {
				LOG.trace("polygon "+polygon1.getClass().getSimpleName());
				//addId(i, shape, path);
				shapeCache.getPolygonList().add(polygon1);
				//LOG.trace("Lines " + (derivedPrimitives.getLineList() == null ? 0 : derivedPrimitives.getLineList().size()));
				i.remove();
			} else {
				SVGPolygon resultingPolygon = convertFilledPolylineToPolygon(polyline);
				if (resultingPolygon == null) {
					lineCache.addLines(polyline.createLineList());
				} else {
					shapeCache.getPolygonList().add(resultingPolygon);
				}
				i.remove();
			}
		}
	}

	private SVGPolygon convertFilledPolylineToPolygon(SVGPolyline polyline) {
		if ((polyline.getStyle() != null && polyline.getStyle().contains("fill:none")) || (polyline.getAttribute("fill") != null && polyline.getAttribute("fill").getValue().equals("none"))) {
			return null;
		}
		polyline.setClosed(true);
		return polyline.createPolygon(pointEquivalenceEpsilon);
	}

	private void convertUnfilledPolygonsToSetsOfLines() {
		Iterator<SVGPolygon> i = shapeCache.getPolygonList().iterator();
		while (i.hasNext()) {
			SVGPolygon polygon = i.next();
			if ((polygon.getStyle() != null && polygon.getStyle().contains("fill:none")) || (polygon.getAttribute("fill") != null && polygon.getAttribute("fill").getValue().equals("none"))) {
				lineCache.addLines(polygon.createLineList(true));
				i.remove();
			}
		}
	}

	private void convertPathsToShapes() {
		List<SVGPath> newPaths = new ArrayList<SVGPath>();
		Iterator<SVGPath> i = pathCache.getCurrentPathList().iterator();
		while (i.hasNext()) {
			SVGPath path = i.next();
			List<SVGShape> shapes = path2ShapeConverter.convertPathsToShapesAndSplitAtMoves(Collections.singletonList(path)).get(0);
			for (SVGShape shape : shapes) {
				if (!(shape instanceof SVGPath)) {
					shapeCache.add(shape);
				} else {
					newPaths.add((SVGPath) shape);
				}
				LOG.trace("shape "+shape.getClass().getSimpleName());
				//addId(i, shape, path);
				//LOG.trace("Lines " + (derivedPrimitives.getLineList() == null ? 0 : derivedPrimitives.getLineList().size()));
				try {
					i.remove();
				} catch (IllegalStateException e) {
					
				}
			}
		}
		pathCache.addAll(newPaths);
	}

	private void getSmallestMeaningfulPolygons() {
		for (SVGPolygon polygon : shapeCache.getPolygonList()) {
			if (polygon.createLineList(true).size() < maximumEdgesForAbstraction) {
				normalizeToSmallestMeaningfulPoly(polygon, true);
			}
		}
	}
	
	public static double area(Real2Array points) {
		double area = 0;
		int j = points.size() - 1;
		for (int i = 0; i < points.size(); i++) {
			area += (points.get(j).getX() + points.get(i).getX()) * (points.get(j).getY() - points.get(i).getY());
			j = i;
		}
		return Math.abs(area / 2);
	}

	/** this could be a useful method but the docs have fallen off.
	 * and the original name (abstractPoly) isn't very suggestive either.
	 * 
	 * 
	 * @param poly
	 * @param closed
	 * @param maxAngleForCorner
	 */
	public void normalizeToSmallestMeaningfulPoly(SVGPoly poly, boolean closed) {
		Real2Array points = poly.getReal2Array();
		if (points.size() <= 3) {
			return;
		}
		double area = area(points);
		double currentArea = area;
		Real2Array currentPoints = new Real2Array(points);
		while (currentPoints.size() > 3) {
			double smallestAreaChange = Double.MAX_VALUE;
			Real2Array pointsForLargestAreaWithOnePointRemoved = null;
			double areaWithSmallestChange = 0;
			for (int i = 0; i < currentPoints.size(); i++) {
				Real2Array testPoints = new Real2Array(currentPoints);
				testPoints.deleteElement(i);
				double newArea = area(testPoints);
				double diff = Math.abs(currentArea - newArea);
				if (diff < smallestAreaChange) {
					smallestAreaChange = diff;
					pointsForLargestAreaWithOnePointRemoved = testPoints;
					areaWithSmallestChange = newArea;
				}
			}
			if (areaWithSmallestChange / area < maxAreaChangeRatio) {
				break;
			}
			currentPoints = pointsForLargestAreaWithOnePointRemoved;
			currentArea = areaWithSmallestChange;
		}
		poly.setReal2Array(currentPoints);
	}
	
	/*private void addId(int i, SVGElement element, SVGElement reference) {
		String id = (reference == null) ? null : reference.getId();
		if (id == null) {
			id = element.getLocalName()+"."+i;
			element.setId(id);
		}
		
	}
	
	private void ensureIds(List<? extends SVGElement> elementList) {
		for (int i = 0; i < elementList.size(); i++){
			SVGElement element = elementList.get(i);
			addId(i, element, null);
		}
	}*/

	public AbstractCMElement getSVGRoot() {
		return svgRoot;
	}

	/*public List<SVGLine> getSingleLineList() {
		return higherPrimitives.getSingleLineList();
	}

	public List<SVGPath> getDerivedPathList() {
		return derivedPrimitives.getPathList();
	}

	public List<SVGPath> getCurrentPathList() {
		return derivedPrimitives.getPathList();
	}

	public List<SVGShape> getCurrentShapeList() {
		return derivedPrimitives.getShapeList();
	}*/

	/*protected void ensureRawContainer() {
		if (rawPrimitives == null) {
			rawPrimitives = new SVGPrimitives();
		}
	}

	protected void ensureDerivedContainer() {
		if (derivedPrimitives == null) {
			derivedPrimitives = new SVGPrimitives();
		}
	}*/

	/*public List<SVGText> getRawTextList() {
		return derivedPrimitives  == null ? null : derivedPrimitives.getTextList();
	}

	public List<SVGLine> getRawLineList() {
		return (rawPrimitives == null) ? null : rawPrimitives.getLineList();
	}

	public List<SVGLine> getDerivedLineList() {
		return (derivedPrimitives == null) ? null : derivedPrimitives.getLineList();
	}

	public List<Joinable> getJoinableList() {
		return (higherPrimitives == null) ? null : higherPrimitives.getJoinableList();
	}

	public List<TramLine> getTramLineList() {
		return (higherPrimitives == null) ? null : higherPrimitives.getTramLineList();
	}*/

	/*public void extractPlotComponents() {
		ensureRawContainer();
		ensureDerivedContainer();
		List<SVGPath> pathList = SVGPath.extractPaths(getSVGRoot());
		Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
		derivedPrimitives.addShapesToSubclassedLists(path2ShapeConverter.convertPathsToShapes(pathList));
	}*/
	
}