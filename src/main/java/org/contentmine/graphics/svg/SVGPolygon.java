/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.svg;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Line2AndReal2Calculator;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;

import nu.xom.Element;
import nu.xom.Node;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGPolygon extends SVGPoly {
	
	private static Logger LOG = Logger.getLogger(SVGPolygon.class);

	public final static String ALL_POLYGON_XPATH = ".//svg:polygon";
	public final static String TAG ="polygon";

	
	/** constructor
	 */
	public SVGPolygon() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGPolygon(SVGElement element) {
        super(element);
        init();
	}
	
	/** constructor
	 */
	public SVGPolygon(Element element) {
        super(element);
        init();
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGPolygon(Real2Array real2Array) {
		this();
		init();
		setReal2Array(real2Array);
	}
	
	public SVGPolygon(List<SVGLine> lineList) {
		this();
		int npoints = lineList.size();
		Real2Array real2Array0 = new Real2Array(npoints);
		for (int i = 0; i < npoints; i++) {
			real2Array0.setElement(i, lineList.get(i).getXY(0));
		}
		setReal2Array(real2Array0);
	}

	protected void init() {
		super.init();
		isClosed = true;
	}
	
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGPolygon(this);
    }
		
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public static SVGPoly createPolygon(SVGRect rect) {
		return rect == null ? null : createPolygon(rect.getBoundingBox());
	}
	
	/** create from a box.
	 * 
	 * @param r2r
	 * @return
	 */
	public static SVGPolygon createPolygon(Real2Range r2r) {
		SVGPolygon poly = null;
		if (r2r != null) {
			Real2[] corners = r2r.getLLURCorners();
			Real2Array polyXY = new Real2Array(Arrays.asList(new Real2[]{
					corners[0], 
					new Real2(corners[0].getX(), corners[1].getY()),
					corners[1],
					new Real2(corners[1].getX(), corners[0].getY())
					}));
			poly = new SVGPolygon(polyXY);
		}
		return poly;
	}

	
	public int size() {
		getReal2Array();
		return real2Array == null ? 0 : real2Array.size();
	}

	@Override
	protected void drawElement(Graphics2D g2d) {
		super.drawPolylineOrGon(g2d, true);
	}
	
	/** makes a new list composed of the polygons in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGPolygon> extractPolygons(List<? extends SVGElement> elements) {
		List<SVGPolygon> polygonList = new ArrayList<SVGPolygon>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGPolygon) {
				polygonList.add((SVGPolygon) element);
			}
		}
		return polygonList;
	}

	public static List<SVGPolygon> extractSelfAndDescendantPolygons(AbstractCMElement g) {
		return SVGPolygon.extractPolygons(SVGUtil.getQuerySVGElements(g, ALL_POLYGON_XPATH));
	}
	
	public List<SVGLine> createLineList(boolean clear) {
		List<SVGLine> polyList = super.createLineList(clear);
		SVGLine line = new SVGLine(real2Array.elementAt(real2Array.size() - 1), real2Array.elementAt(0));
		copyNonSVGAttributes(this, line);
		SVGMarker point = new SVGMarker(real2Array.get(0));
		markerList.get(0).addLine(line);
		markerList.get(markerList.size() - 1).addLine(line);
		if (line.getEuclidLine().getLength() < 0.0000001) {
			LOG.trace("ZERO LINE");
		}
		lineList.add(line);
		return lineList;
	}

	public boolean containsPoint(Real2 xy, double nearDuplicateRemovalDistance) {
		Line2 line = new Line2(xy, xy.plus(new Real2(1, 10000000000000d)));
		Real2 lastPoint = getReal2Array().getLastElement();
		int intersections = 0;
		boolean near = false;
		for (Real2 point : getReal2Array()) {
			Line2 edge = new Line2(lastPoint, point);
			Line2AndReal2Calculator calc = new Line2AndReal2Calculator(edge, xy);
			if (calc.minimumDistance < nearDuplicateRemovalDistance) {
				near = true;
				break;
			}
			Real2 intersection = edge.getIntersection(line);
			if (Double.isNaN(intersection.getX()) || Double.isNaN(intersection.getY())) {
				continue;
			}
			double lambda1 = edge.getLambda(intersection);
			double lambda2 = line.getLambda(intersection);
			if (lambda1 >= 0 && lambda1 <= 1 && lambda2 >= 0 && lambda2 <= 1) {
				intersections++;
			}
			lastPoint = point;
		}
		return (near || intersections % 2 == 1);
	}

	public boolean hasMirror(int startPoint, double eps) {
		boolean hasMirror = false;
		hasMirror = hasMirrorAboutMidLine(startPoint, eps);
		hasMirror &= hasMirroredSideLengths(startPoint, eps);
		
		return hasMirror;
	}

	private boolean hasMirrorAboutMidLine(int stIndex, double eps) {
		boolean hasMirror = true;
		int npoints = real2Array.size();
		int npoints2 = npoints / 2;
		int startIndex = (stIndex + 1) % npoints;
		int endIndex = stIndex + npoints2;
		Real2 startPoint = real2Array.get(startIndex);
		Real2 endPoint = (npoints % 2 == 0) ? real2Array.get(endIndex) :
			real2Array.get(endIndex).getMidPoint(real2Array.get(endIndex + 1));
		Line2 line = new Line2(startPoint, endPoint);
		for (int i = startIndex; i < endIndex; i++) {
			Real2 ppi = real2Array.get(i % npoints);
			Real2 ppn = real2Array.get((npoints - i) % npoints);
			double di = line.getUnsignedDistanceFromPoint(ppi);
			double dn = line.getUnsignedDistanceFromPoint(ppn);
			if (!Real.isEqual(di,  dn, eps)) {
				hasMirror = false;
				break;
			}
		}
		return hasMirror;
	}

	private boolean hasMirroredSideLengths(int stIndex, double eps) {
		boolean hasMirror = true;
		int npoints = real2Array.size();
		int npoints2 = npoints / 2;
		int startIndex = stIndex + 1;
		int endIndex = stIndex + npoints2;
		for (int i = startIndex; i < endIndex; i++) {
			// are opposite sides equal?
			if (i > startIndex) {
				double sidei = real2Array.get(i % npoints).getDistance(real2Array.get((i - 1) % npoints));
				double siden = real2Array.get((npoints - 2) % npoints).getDistance(real2Array.get((npoints - 1) % npoints));
				if (!Real.isEqual(sidei, siden, eps)) {
					hasMirror = false;
					break;
				}
			}
		}
		return hasMirror;
	}

	/** crude circle generator.
	 * assumes all points are on circle and simply creates averages.
	 * does not check points are coincident or collinear.
	 * For more precise answers use Least Squares or other fitters.
	 * 
	 * @param maxMeanDevation from circumference
	 * @return null if <2 points or mean deviation > maxMeanDeviation
	 */
	public SVGCircle createCircle(double maxMeanDevation) {
		SVGCircle circle = null;
		if (real2Array != null && real2Array.size() >= 2) {
			Real2 centre = real2Array.getMean();
			double radsum = 0.0;
			for (Real2 point : real2Array) {
				radsum += centre.getDistance(point);
			}
			centre = centre.multiplyBy(1.0 / real2Array.size());
			circle = new SVGCircle(real2Array.getMean(), radsum / real2Array.size());
			RealArray deviations = circle.calculateUnSignedDistancesFromCircumference(real2Array);
			double meanDeviation = deviations.absSumAllElements() / real2Array.size();
			if (meanDeviation < maxMeanDevation) {
				circle.copyAttributesFrom(this);
			} else {
				circle = null;
			}
		}
		return circle;
		
	}

	/**
	 * Converts circles represented as polygons (closed paths) into SVG circles
	 * <p>
	 * 
	 * @param eps deviation from squareness
	 * @return circle
	 */
	public SVGCircle convertToCircle(double eps) {
		Real2Range bbox = getBoundingBox();
		SVGCircle circle = null;
		if (bbox.isSquare(eps)) {
			Real2 centre = bbox.getCentroid();
			RealArray radArray = new RealArray();
			for (Real2 point : getReal2Array()) {
				radArray.addElement(centre.getDistance(point));
			}
			circle = new SVGCircle();
			circle.copyAttributesFrom(this);
			circle.setRad(radArray.getMean());
			circle.setCXY(centre);
		}
		return circle;
	}
	
	@Override
	public String toString() {
		return "polygon: "+String.valueOf(real2Array);
	}

	/** will join lines to create polygons
	 * if more than one joining is possible (e.g. bicycles) the result is unpredictable and
	 * some joins will not be made
	 * 
	 * @param lineList unaffected
	 * @param eps tolerance for joining
	 * 
	 * @return list of polygons
	 */
	public static List<SVGPolygon> createPolygonsFromLines(List<SVGLine> lineList, double eps) {
		List<SVGPoly> polylineGonList = SVGPoly.createSVGPolyList(lineList, eps );
		List<SVGPolygon> polygonList = SVGPolygon.extractPolygons(polylineGonList);
		return polygonList;
	}
	
	@Override
	protected void completePrimitive(PathPrimitiveList primitiveList) {
		primitiveList.add(new ClosePrimitive());
//		LOG.debug("CLOSE "+primitiveList.createD());
	}



}