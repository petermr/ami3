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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.EuclidConstants;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGLine extends SVGShape {

	private static Logger LOG = Logger.getLogger(SVGLine.class);
	
	public enum LineDirection {
		HORIZONTAL(RealRange.Direction.HORIZONTAL),
		VERTICAL(RealRange.Direction.VERTICAL);
		private RealRange.Direction direction;
		private LineDirection(RealRange.Direction direction) {
			this.direction = direction;
		}
		public RealRange.Direction getRealRangeDirection() {
			return this.direction;
		}
		public LineDirection getPerpendicularLineDirection() {
			return this.isHorizontal() ? LineDirection.VERTICAL : LineDirection.HORIZONTAL;
		}
		
		public boolean isHorizontal() {
			return LineDirection.HORIZONTAL.equals(this);
		}
		public boolean isVertical() {
			return LineDirection.VERTICAL.equals(this);
		}
	}
	
	public final static String ALL_LINE_XPATH = ".//svg:line";
	
	private static final String X1 = "x1";
	private static final String X2 = "x2";
	private static final String Y1 = "y1";
	private static final String Y2 = "y2";
	private static final String X = "x";
	private static final String Y = "y";

	public final static String TAG ="line";
	public final static double EPS = 0.01;
	// tolerance for making TJunctions
	public final static double TJUNCTION_DISTANCE = 1.0;

	private Line2D.Double line2;
	private Line2 euclidLine;
	
	/** constructor
	 */
	public SVGLine() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGLine(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGLine(Element element) {
        super((SVGElement) element);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGLine(Real2 x1, Real2 x2) {
		this();
		setXY(x1, 0);
		setXY(x2, 1);
		updateEuclidLine(x1, x2);
	}

	private void updateEuclidLine(Real2 x1, Real2 x2) {
		euclidLine = new Line2(x1, x2);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGLine(Line2 line) {
		this(line.getXY(0), line.getXY(1));
		this.euclidLine = line;
	}
	
	public SVGLine(SVGLine line) {
		super(line);
		updateEuclidLine(this.getXY(0), this.getXY(1));
	}
	
	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	public static void setDefaultStyle(SVGElement line) {
		line.setStroke("black");
		line.setStrokeWidth(1.0);
	}
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGLine(this);
    }

	public Real2 getXY() {
		throw new RuntimeException("Cannot define getXY() for lines");
	}
	
	public Double getX() {
		throw new RuntimeException("Cannot define getX() for lines");
	}
	
	public Double getY() {
		throw new RuntimeException("Cannot define getY() for lines");
	}
	
	/**
	 * @param xy coordinates of the atom
	 * @param serial 0 or 1
	 */
	public void setXY(Real2 x12, int serial) {
		if (x12 == null) {
			System.err.println("null x2/y2 in line: ");
		} else {
			this.addAttribute(new Attribute(X+(serial+1), String.valueOf(x12.getX())));
			this.addAttribute(new Attribute(Y+(serial+1), String.valueOf(x12.getY())));
			if (euclidLine != null) {
				euclidLine.setXY(x12, serial);
			}

		}
	}
	
	public Real2 getXY(int serial) {
		Real2 xy = null;
		if (serial == 0) {
			xy = new Real2(this.getDouble(X1), this.getDouble(Y1));
		} else if (serial == 1) {
			xy = new Real2(this.getDouble(X2), this.getDouble(Y2));
		}
		return xy;
	}
	
	/**
	 * @param x12 coordinates of the atom
	 * @param serial 1 or 2
	 */
	@Deprecated
	public void setX12(Real2 x12, int serial) {
		if (x12 == null) {
			System.err.println("null x2/y2 in line: ");
		} else {
			this.addAttribute(new Attribute(X+serial, String.valueOf(x12.getX())));
			this.addAttribute(new Attribute(Y+serial, String.valueOf(x12.getY())));
			if (euclidLine != null) {
				euclidLine.setXY(x12, serial);
			}
		}
	}
	
	@Deprecated //use getXY
	public Real2 getX12(int serial) {
		Real2 xy = null;
		if (serial == 1) {
			xy = new Real2(this.getDouble(X1), this.getDouble(Y1));
		} else if (serial == 2) {
			xy = new Real2(this.getDouble(X2), this.getDouble(Y2));
		}
		return xy;
	}
	
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
		ensureCumulativeTransform();
		Line2D line = createAndSetLine2D();
		fill(g2d, line);
		draw(g2d, line);
		restoreGraphicsSettingsAndTransform(g2d);
	}

	public void applyAttributes(Graphics2D g2d) {
		if (g2d != null) {
			double width = this.getStrokeWidth();
			Stroke s = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
			g2d.setStroke(s);
			super.applyAttributes(g2d);
		}
	}

	public Line2D.Double createAndSetLine2D() {
		ensureCumulativeTransform();
		double x1 = this.getDouble(X1);
		double y1 = this.getDouble(Y1);
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, cumulativeTransform);
		double x2 = this.getDouble(X2);
		double y2 = this.getDouble(Y2);
		Real2 xy2 = new Real2(x2, y2);
		xy2 = transform(xy2, cumulativeTransform);
		float width = 5.0f;
		String style = this.getAttributeValue(STYLE);
		if (style != null && style.startsWith("stroke-width:")) {
			style = style.substring("stroke-width:".length());
			style = style.substring(0, (style+S_SEMICOLON).indexOf(S_SEMICOLON));
			width = (float) Double.parseDouble(style);
			width *= 15.f;
		}
		line2 = new Line2D.Double(xy1.x, xy1.y, xy2.x, xy2.y);
		return line2;
	}
	
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public Line2D.Double getLine2() {
		return line2;
	}

	public void setLine2(Line2D.Double line2) {
		this.line2 = line2;
	}
	
	public Line2 getEuclidLine() {
		if (euclidLine == null) {
			euclidLine = new Line2(this.getXY(0), this.getXY(1));
		}
		return euclidLine;
	}

	public void setEuclidLine(Line2 euclidLine) {
		this.euclidLine = euclidLine;
	}

	public void applyTransformPreserveUprightText(Transform2 transform) {
		Real2 xy = this.getXY(0);
		setXY(xy.getTransformed(transform), 0);
		xy = this.getXY(1);
		setXY(xy.getTransformed(transform), 1);
	}

	public void format(int places) {
		setXY(getXY(0).format(places), 0);
		setXY(getXY(1).format(places), 1);
	}

	public boolean connectsPoints(Real2 p0, Real2 p1, double eps) {
		return (this.getXY(0).isEqualTo(p0, eps) && this.getXY(1).isEqualTo(p1, eps)) || 
			(this.getXY(0).isEqualTo(p1, eps) && this.getXY(1).isEqualTo(p0, eps)); 
	}
	
	public boolean isVertical(double eps) {
		return Real.isEqual(this.getXY(0).getX(), this.getXY(1).getX(), eps);
	}
	
	public boolean isHorizontal(double eps) {
		return Real.isEqual(this.getXY(0).getY(), this.getXY(1).getY(), eps);
	}

	public boolean isZero(double eps) {
		Real2Range bbox = this.getBoundingBox();
		return bbox.getXRange().getRange() < eps && bbox.getYRange().getRange() < eps;
	}

	/** index of point in line
	 * 
	 * @param point in this to test
	 * @param eps tolerance
	 * @return which end is matched ? none = -1 else 0/1 (if both return 0)
	 */
	public int getIndexOfPoint(Real2 point, double eps) {
		if (point.isEqualTo(this.getXY(0), eps)) return 0;
		if (point.isEqualTo(this.getXY(1), eps)) return 1;
		return -1;
	}

	/** common point of joined lines
	 * 
	 * @param line other line
	 * @param thisEnd index of point in this to test
	 * @param eps tolerance
	 * @return common point or null
	 */
	public Real2 getCommonEndPoint(SVGLine line, int thisEnd, double eps) {
		int lineEnd = line.getIndexOfPoint(this.getXY(thisEnd), eps);
		return lineEnd == -1 ? null : line.getXY(lineEnd);
	}

//	/** index of point on other line joing with this
//	 * 
//	 * @param line other line
//	 * @param thisEnd index of point in this to test
//	 * @param eps tolerance
//	 * @return index of common point in other line (0, 1) or -1 if no match
//	 */
//	public int getEndIndex(SVGLine line, int thisEnd, double eps) {
//		int lineEnd = -1;
//		if (hasCommonPoint(line, thisEnd, 0, eps)) {
//			lineEnd = 0;
//		} else if (hasCommonPoint(line, thisEnd, 1, eps)) {
//			lineEnd = 1;
//		}
//		return lineEnd;
//	}

//	/** index of point on other line joing with this
//	 * 
//	 * @param line other line
//	 * @param thisEnd index of point in this to test
//	 * @param eps tolerance
//	 * @return index of common point in other line (0, 1) or -1 if no match
//	 */
//	public int getCommonEndIndex(SVGLine line, int thisEnd, double eps) {
//		int lineEnd = -1;
//		if (hasCommonPoint(line, thisEnd, 0, eps)) {
//			lineEnd = 0;
//		} else if (hasCommonPoint(line, thisEnd, 1, eps)) {
//			lineEnd = 1;
//		}
//		return lineEnd;
//	}

	/**
	 * does given end of this match given end of line?
	 * 
	 * @param line
	 * @param thisEnd 0 or 1
	 * @param lineEnd 0 or 1
	 * @param eps tolerance
	 * @return common point or null
	 */
	public boolean hasCommonPoint(SVGLine line, int thisEnd, int lineEnd, double eps) {
		return line.getXY(thisEnd).isEqualTo(getXY(lineEnd), eps);
	}

	/** if this butts onto line at right angles.
	 * this and line should be hoizontal/vertical
	 * final point of this should be on target line
	 * @param l line to butt onto
	 * @param eps
	 * @return
	 */
	public boolean makesTJointWith(SVGLine l, double eps) {
		boolean endsOn = false;
		if (this.isHorizontal(eps) && l.isVertical(eps)) {
			RealRange yrange = l.getReal2Range().getYRange();
			double lx = l.getXY(0).getX();
			double thisy = this.getXY(0).getY();
			endsOn = yrange.contains(thisy) && 
				(Real.isEqual(lx, this.getXY(0).getX(), eps) ||
				Real.isEqual(lx, this.getXY(1).getX(), eps));
		} else if (this.isVertical(eps) && l.isHorizontal(eps)) {
			RealRange xrange = l.getReal2Range().getXRange();
			double ly = l.getXY(0).getY();
			double thisx = this.getXY(0).getX();
			endsOn = xrange.contains(thisx) && 
				(Real.isEqual(ly, this.getXY(0).getY(), eps) ||
				Real.isEqual(ly, this.getXY(1).getY(), eps));
		}
		return endsOn;
	}
	
	/** if point is close to one end of line get the other
	 * 
	 * @param point
	 * @param eps
	 * @return coordinates of other end null if point is not at a line end
	 */
	public Real2 getOtherPoint(Real2 point, double eps) {
		Real2 other = null;
		if (point.isEqualTo(getXY(0), eps)) {
			other = getXY(1);
		} else if (point.isEqualTo(getXY(1), eps)) {
			other = getXY(0);
		}
		return other;
	}
	
	public Real2Range getReal2Range() {
		Real2Range real2Range = new Real2Range(getXY(0), getXY(1));
		return real2Range;
	}
	
	public static boolean isEqual(SVGLine line0, SVGLine line1, double eps) {
		Line2 eLine0 = line0.getEuclidLine();
		Line2 eLine1 = line1.getEuclidLine();
		return (eLine0.getXY(0).isEqualTo(eLine1.getXY(0), eps) &&
				eLine0.getXY(1).isEqualTo(eLine1.getXY(1), eps)
				);
	}
	
	/** synonym for getReal2Range.
	 * 
	 * @return
	 */
	public Real2Range getBoundingBox() {
		if (boundingBoxNeedsUpdating()) {
			boundingBox = getReal2Range();
		}
		return boundingBox;
	}
	
	public String getXYString() {
		return getXY(0) + S_SPACE + getXY(1);
	}

	public static List<SVGLine> findHorizontalOrVerticalLines(AbstractCMElement svgElement, double eps) {
		List<SVGLine> horizontalVerticalList = new ArrayList<SVGLine>();
		Nodes lines = svgElement.query(".//svg:line", XMLConstants.SVG_XPATH);
		for (int i = 0; i < lines.size(); i++) {
			SVGLine line = (SVGLine) lines.get(i);
			if (line.isHorizontal(eps) || line.isVertical(eps)) {
				horizontalVerticalList.add(line);
			}
		}
		return horizontalVerticalList;
	}

	public static List<SVGLine> findHorizontalOrVerticalLines(List<SVGLine> lines, LineDirection direction, double eps) {
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (SVGLine line : lines) {
			if ((direction.isHorizontal() && line.isHorizontal(eps)) ||
			    (direction.isVertical() && line.isVertical(eps))) {
				lineList.add(line);
			} else {
				LOG.trace("reject: "+line+" /LV? "+line.isVertical(eps)+"/DV?"+direction.isVertical());
			}
		}
		return lineList;
	}

	public static List<SVGLine> findVerticalLines(List<SVGLine> lines, double eps) {
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (SVGLine line : lines) {
			if (line.isVertical(eps)) {
				lineList.add(line);
			}
		}
		return lineList;
	}

	public static List<SVGLine> findHorizontaLines(List<SVGLine> lines, double eps) {
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (SVGLine line : lines) {
			if (line.isHorizontal(eps)) {
				lineList.add(line);
			}
		}
		return lineList;
	}

	public void setWidth(double width) {
		this.addAttribute(new Attribute("stroke-width", String.valueOf(width)));
	}

	/**
	 * 
	 * @param svgLine
	 * @param d max difference in radians from zero
	 * @return
	 */
	public boolean isParallelTo(SVGLine svgLine, Angle angleEps) {
		return this.getEuclidLine().isParallelTo(svgLine.getEuclidLine(), angleEps);
	}

	/**
	 * 
	 * @param svgLine
	 * @param d max difference in radians from zero
	 * @return
	 */
	public boolean isAntiParallelTo(SVGLine svgLine, Angle angleEps) {
		return this.getEuclidLine().isAntiParallelTo(svgLine.getEuclidLine(), angleEps);
	}


	/**
	 * 
	 * @param svgLine
	 * @param d max difference in radians from zero
	 * @return
	 */
	public boolean isParallelOrAntiParallelTo(SVGLine svgLine, Angle angleEps) {
		return this.getEuclidLine().isParallelOrAntiParallelTo(svgLine.getEuclidLine(), angleEps);
	}

	/**
	 * are two lines perpendicular 
	 * @param svgLine
	 * @param eps max difference between cosine and 0
	 * @return
	 */
	public boolean isPerpendicularTo(SVGLine svgLine, double eps) {
		Angle angle = this.getEuclidLine().getAngleMadeWith(svgLine.getEuclidLine());
		Double dd = Math.abs(angle.cos());
		return (dd < eps && dd > -eps);
	}

	public Double calculateUnsignedDistanceBetweenLines(SVGLine line1, Angle eps) {
		return this.getEuclidLine().calculateUnsignedDistanceBetweenLines(line1.getEuclidLine(), eps);
	}
	
	/** makes a new list composed of the lines in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGLine> extractLines(List<SVGElement> elements) {
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGLine) {
				lineList.add((SVGLine) element);
			}
		}
		return lineList;
	}

	public Double getLength() {
		Line2 line2 = getEuclidLine();
		return (line2 == null) ? null : line2.getLength();
	}

	/** for horizontal or vertical lines make sure that first coord is smallest
	 * 
	 * @param eps
	 */
	public void normalizeDirection(double eps) {
		Real2 xy0 = getXY(0);
		Real2 xy1 = getXY(1);
		if (isHorizontal(eps)) {
			if (xy0.getX() > xy1.getX()) {
				this.setXY(xy0, 1);
				this.setXY(xy1, 0);
			}
		} else if (isVertical(eps)) {
			if (xy0.getY() > xy1.getY()) {
				this.setXY(xy0, 1);
				this.setXY(xy1, 0);
			}
		}
	}
	
	@Override
	public String getGeometricHash() {
		return getAttributeValue(X1)+" "+getAttributeValue(Y1)+" "+getAttributeValue(X2)+" "+getAttributeValue(Y2);
	}

	/** convenience method to extract list of svgLines in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGLine> extractSelfAndDescendantLines(AbstractCMElement svgElement) {
		return SVGLine.extractLines(SVGUtil.getQuerySVGElements(svgElement, ALL_LINE_XPATH));
	}

	public String toString() {
		getEuclidLine();
		return (euclidLine == null) ? null : euclidLine.toString();
	}

	public Real2 getMidPoint() {
		return this.getEuclidLine().getMidPoint();
	}

	public Real2 getNearestPointOnLine(Real2 point) {
		return getEuclidLine().getNearestPointOnLine(point);
	}
	
	/** does either end of this fall within line2 without extension.
	 * 
	 * NOT TESTED
	 * 
	 * @param line2
	 * @param eps to avoid rounding errors
	 * @return
	 */
	public boolean overlapsWithLine(SVGLine line2, double eps) {
		Real2 point = line2.getNearestPointOnLine(getXY(0));
		if (line2.getEuclidLine().contains(point, eps, false)) return true;
		point = line2.getNearestPointOnLine(getXY(1));
		if (line2.getEuclidLine().contains(point, eps, false)) return true;
		return false;
	}

	/**
	 * creates a mean line.
	 * 
	 * <p>
	 * averages the point at end of the two lines. If lines are parallel use
	 * XY(0) and line.XY(0), as first/from point. If antiparallel use XY(0) and line.XY(1).
	 * Similarly second/to uses XY(1) and line.XY(1) for parallel and XY(1) and line.XY(0)
	 * for antiparallel. Mean line is always aligned with line 0.
	 * </p>
	 * <p>Does not check for overlap of lines - that's up to the user to decide the cases. 
	 * Does not add any style</p>
	 * 
	 * @param line
	 * @param angleEps to test whether anti/parallel
	 * @return null if lines not parallel
	 */
	public SVGElement getMeanLine(SVGLine line, Angle angleEps) {
		SVGElement meanLine = null;
		if (isParallelTo(line, angleEps)) {
			meanLine = new SVGLine(getXY(0).getMidPoint(line.getXY(0)), 
			                       getXY(1).getMidPoint(line.getXY(1)));
		} else if (isAntiParallelTo(line, angleEps)) {
			meanLine = new SVGLine(getXY(0).getMidPoint(line.getXY(1)), 
                    getXY(1).getMidPoint(line.getXY(0)));
		}
		return meanLine;
	}

	public Real2 getIntersection(SVGLine line) {
		return (line == null ? null : this.getEuclidLine().getIntersection(line.getEuclidLine()));
	}

	/** create set of concatenated lines.
	 * 
	 * @param points
	 * @param close if true create line (n-1)->0
	 * @return
	 */
	public static AbstractCMElement plotPointsAsTouchingLines(List<Real2> points, boolean close) {
		AbstractCMElement g = new SVGG();
		for (int i = 0; i < points.size() - 1; i++) {
			SVGElement line = new SVGLine(points.get(i), points.get((i + 1)));
			g.appendChild(line);
		}
		if (close) {
			g.appendChild(new SVGLine(points.get(points.size() - 1), points.get(0)));
		}
		return g;
	}

	public static Real2Array extractPoints(List<SVGLine> lines, double eps) {
		Real2Array points = new Real2Array();
		Real2 lastPoint = null;
		for (SVGLine line : lines) {
			Real2 p0 = line.getXY(0);
			if (lastPoint != null && !p0.isEqualTo(lastPoint, eps)) {
				points.add(p0);
			}
			Real2 p1 = line.getXY(1);
			points.add(p1);
			lastPoint = p1;
		}
		return points;
	}

	public boolean hasEqualCoordinates(SVGLine line, double eps) {
		Real2 thisPoint0 = this.getXY(0);
		Real2 thisPoint1 = this.getXY(1);
		Real2 otherPoint0 = line.getXY(0);
		Real2 otherPoint1 = line.getXY(1);
		return thisPoint0.getDistance(otherPoint0) < eps && thisPoint1.getDistance(otherPoint1) < eps;
	}

	public LineDirection getOrCreateDirection(double epsilon) {
		LineDirection direction = null;
		if (isHorizontal(EuclidConstants.EPS)) {
			direction = LineDirection.HORIZONTAL;
		} else if (isHorizontal(epsilon)) {
			direction = LineDirection.VERTICAL;
		}
		return direction;
	}

	/** filters horizontal and vertical lines, normalizes the direction and merges touching ones.
	 * 
	 * modifies list and lines
	 * 
	 * @param lineList merged horizontal and merged vertical lines 
	 * @param eps
	 * @return
	 */
	public static List<SVGLine> normalizeAndMergeAxialLines(List<SVGLine> lineList, double eps) {
		List<SVGLine> newLineList = new ArrayList<SVGLine>();
		SVGLine.normalizeDirections(lineList, eps);
		List<SVGLine> horizontalList = SVGLine.extractAndRemoveHorizontalVerticalLines(lineList, eps, LineDirection.HORIZONTAL);
		horizontalList = mergeParallelLines(horizontalList, eps);
		newLineList.addAll(horizontalList);
		List<SVGLine> verticalList = SVGLine.extractAndRemoveHorizontalVerticalLines(lineList, eps, LineDirection.VERTICAL);
		verticalList = mergeParallelLines(verticalList, eps);
		newLineList.addAll(verticalList);
		lineList.addAll(newLineList);
		return lineList;
	}
		
	public static List<SVGLine> extractAndRemoveHorizontalVerticalLines(
			List<SVGLine> lineList, double eps, LineDirection direction) {
		List<SVGLine> newLineList = new ArrayList<SVGLine>();
		for (int i = lineList.size() - 1; i >= 0; i--) {
			SVGLine line = lineList.get(i);
			if ((direction.isHorizontal() && line.isHorizontal(eps)) ||
					direction.isVertical() && line.isVertical(eps)) {
				lineList.remove(i);
				newLineList.add(line);
			}
		}
		return newLineList;
	}

	/** normalizes directions of all horizontal and vertical lines.
	 * 
	 * MODIFIES the lines
	 * 
	 * @param lineList
	 * @param eps
	 * @return
	 */
	private static void normalizeDirections(List<SVGLine> lineList, double eps) {
		for (SVGLine line : lineList) {
			line.normalizeDirection(eps);
		}
	}

	/** merges touching parallel lines.
	 * 
	 * Assumes all lines are parallel; if not will fail messily
	 * 
	 * @param lineList
	 * @param eps
	 * @return
	 */
	public static List<SVGLine> mergeParallelLines(List<SVGLine> lineList, double eps) {
		
		/** runs through a list of lines joining where possible to create (smaller) list.
		 * if lines overlap takes union
		 * 
		 * Crude algorithm. 
		 *   1 start = 0;
		 *   2 size = number of lines
		 *   3 iline = start ... size-1
		 *   4 test (iline, iline+1) for join at either end
		 *      if join, replace lines with merged line
		 *      start = iline
		 *      goto 2
		 *   5 exit if no change
		 * 
		 * does not check style attributes
		 * 
		 * @param lineList
		 * @param eps
		 * @return
		 */
		List<SVGLine> lineListNew = new ArrayList<SVGLine>();
		lineListNew.addAll(lineList);
		boolean change = true;
		while (change) {
			change = false;
			int size = lineListNew.size();
			for (int iline = 0; iline < size - 1; iline++) {
				size = lineListNew.size();
				for (int jline = iline + 1; jline < size; jline++) {
					SVGLine linei = lineListNew.get(iline);
					SVGLine linej = lineListNew.get(jline);
					LOG.trace("merging \n    "+linei.toXML()+"\n--> "+linej.toXML());
					SVGLine newLine = createMergedHorizontalOrVerticalLine(linei, linej, eps);
//					if (newLine == null) {
//						// try the other direction
//						newLine = createMergedHorizontalOrVerticalLine(linej, linei, eps);
//					}
//					LOG.trace("newline: "+newLine.toXML());
					if (newLine != null) {
						lineListNew.remove(jline);
						lineListNew.remove(iline);
						lineListNew.add(newLine);
						LOG.trace("after close lines "+lineListNew.size());
						for (SVGElement line : lineListNew) {
							LOG.trace(">> "+line.toXML());
						}
						change = true;
						break;
					}
				}
			}
		}
		return lineListNew;
	}

	/** join two horizontal or vertical lines at their ends.
	 * lines might overlap, in which case the maximum line is taken
	 * currently ignores attributes of line 1
	 * 
	 * @param line0
	 * @param line1
	 * @param eps
	 * @return
	 */
	public static SVGLine createMergedHorizontalOrVerticalLine(SVGLine line0, SVGLine line1, double eps) {
		SVGLine newLine = null;
		Real2 point0 = line0.getXY(1);
		double x0 = point0.getX();
		double y0 = point0.getY();
		Real2 point1 = line1.getXY(0);
		double x1 = point1.getX();
		double y1 = point1.getY();
		if (
				(line0.isHorizontal(eps) && line1.isHorizontal(eps) && 
				Real.isEqual(y0, y1, eps) &&
				touchesOrOverlaps(x0, x1, eps)
				)
			||
				(line0.isVertical(eps) && line1.isVertical(eps) && 
				Real.isEqual(x0, x1, eps) &&
				touchesOrOverlaps(y0, y1, eps)
				)
			) {
		
			Real2Range bbox = line0.getBoundingBox().plus(line1.getBoundingBox());
			Real2 point00 = bbox.getLLURCorners()[0];
			Real2 point11 = bbox.getLLURCorners()[1];
			newLine = new SVGLine();
			XMLUtil.copyAttributes(line0, newLine);
			newLine.setXY(point00, 0);
			newLine.setXY(point11, 1);
		}
		return newLine;
	}

	private static boolean touchesOrOverlaps(double x0, double x1, double eps) {
		return Real.isEqual(x0, x1, eps) || x0 > x1;
	}

	/** some visually "joined" lines are actually overlapping.
	 * 
	 * @param last0
	 * @param first1
	 * @param eps
	 * @return
	 */
	private static boolean overlaps(Real2 last0, Real2 first1, double eps) {
		boolean joined = false;
		if (Real.isEqual(last0.getY(), first1.getY(), eps) && last0.getX() > first1.getX()) {
			joined = true;
		} else if (Real.isEqual(last0.getX(), first1.getX(), eps) && last0.getY() > first1.getY()) {
			joined = true;
		}
		return joined;
	}

	/** gets bounding box of list of lines.
	 * 
	 * @param lines
	 * @return bbox (null if no lines)
	 */
	public static Real2Range getReal2Range(List<SVGLine> lines) {
		Real2Range bbox = null;
		if (lines == null) {
			LOG.warn("null lines");
		} else if (lines.size() > 0) {
			bbox = lines.get(0).getBoundingBox();
			for (int i = 1; i < lines.size(); i++) {
				bbox = bbox.plusEquals(lines.get(i).getBoundingBox());
			}
		} else {
			LOG.info("no lines");
		}
		return bbox;
	}

	@Override
	protected boolean isGeometricallyEqualTo(SVGElement shape, double epsilon) {
		if (shape instanceof SVGLine) {
			return SVGLine.isEqual(this, (SVGLine) shape, epsilon);
		}
		return false;
	}

	Angle getAngleMadeWith(SVGLine line1) {
		return line1 == null ? null : getEuclidLine().getAngleMadeWith(line1.getEuclidLine());
	}

	/** lines outside y=0 are not part of the plot but confuse calculation of
	 * bounding box 
	 * @param lineList
	 * @return
	 */
	public static List<SVGLine> removeLinesWithNegativeY(List<SVGLine> lineList) {
		List<SVGLine> newLines = new ArrayList<SVGLine>();
		for (SVGLine line : lineList) {
			Real2Range bbox = line.getBoundingBox();
			if (bbox.getYMax() >= 0.0) {
				newLines.add(line);
			}
		}
		return newLines;
	}

	/** split into n equal lines
	 * start at end 0 and move to end 1
	 * 
	 * @param n
	 * @return
	 */
	public List<SVGLine> createSplitLines(int n) {
		List<SVGLine> splitLines = new ArrayList<SVGLine>();
		Real2 delta = this.getXY(1).subtract(this.getXY(0));
		for (int i = 0; i < n; i++) {
			double startRatio = (double) i / (double) n;
			double endRatio = (double) (i + 1) / (double) n;
			SVGLine splitLine = new SVGLine(this);
			Real2 start = this.getXY(0).plus(delta.multiplyBy(startRatio));
			Real2 end = this.getXY(0).plus(delta.multiplyBy(endRatio));
			splitLine.setXY(start, 0);
			splitLine.setXY(end, 1);
			splitLines.add(splitLine);
		}
		return splitLines;
	}

	/**
	 * @deprecated Use {@link org.contentmine.graphics.svg.SVGLine#getTJunction(List<SVGLine>,int)} instead
	 */
	public SVGElement getTJunction(List<SVGLine> horizontalLines, SVGLine verticalLine, int end) {
		return verticalLine.getTJunctionCrossbar(horizontalLines, end);
	}
	
	/** search perpendicular lines for the line whose midpoint touches this at end = end.
	 * (doesn't actually have to be perpendicular, but has to be midpoint)
	 * 
	 * @param perpendicularLines
	 * @param end
	 * @return
	 */
	public SVGElement getTJunctionCrossbar(List<SVGLine> perpendicularLines, int end) {
		for (SVGLine perpendicularLine : perpendicularLines) {
			Real2 midPoint = perpendicularLine.getMidPoint();
			double dist = midPoint.getDistance(getXY(end));
			if (dist < SVGLine.TJUNCTION_DISTANCE) {
				return perpendicularLine;
			}
		}
		return null;
	}

	/**
	 * when a line is created from another element, copy some of the attributes. 
	 * This is empirical. If originator is not a line, then its fill becomes the stroke.
	 * The ID is not copied nor the class. The originatingElement ID/s is/are set as parent of the line
	 * IN this way the history of the creation can be followed.
	 * Will be messy and empirical
	 * 
	 * @param originatingElement
	 */
	public void copyAttributesFromOriginating(SVGElement originatingElement) {
		this.setStroke(originatingElement.getFill());
		this.setOpacity(originatingElement.getOpacity());
		this.setParentID(originatingElement.getId());
	}

	public SVGLine copyStrokedLine(String stroke) {
		SVGLine line1 = (SVGLine) copy();
		line1.setStroke(stroke);
		line1.setWidth(1.5);
		line1.addAttribute(new Attribute("stroke-linecap", "round"));
		line1.setOpacity(0.5);
		return line1;
	}


}
