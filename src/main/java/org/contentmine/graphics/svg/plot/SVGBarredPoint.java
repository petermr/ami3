package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Point2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGConstants;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.plot.SVGErrorBar.BarDirection;

/** holds a point with ErrorBars.
 * 
 * @author pm286
 *
 */
public class SVGBarredPoint extends SVGG {

	private static Logger LOG = Logger.getLogger(SVGBarredPoint.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final double POINT_RADIUS = 1.0;

	private SVGShape shape;
	private List<SVGErrorBar> errorBars;
	private Real2 centroid;
	private Real2Range errorBox;

	public SVGBarredPoint() {
		super();
		ensureErrorBars();
	}
	
	public SVGBarredPoint(Real2 centroid) {
		this();
		this.setCentroid(centroid);
	}
	
	private void ensureErrorBars() {
		if (errorBars == null) {
			errorBars = new ArrayList<SVGErrorBar>();
		}
 	}

	public static SVGBarredPoint createPoint(SVGShape shape) {
		SVGBarredPoint point = null;
		if (shape != null) {
			point = new SVGBarredPoint();
			point.shape = shape;
			point.getOrCreateCentroid();
		}
		return point;
	}
	
	/** creates errorBar from line and point.
	 * 
	 * If line is horizontal or vertical and point lies on line and line end
	 * is close to point , then create error bar. 
	 * (i.e. o--- or ---o
	 * |      o
	 * |  or  |
	 * o      |
	 * 
	 * @param line
	 * @param maxDist maximum distance of line end from point
	 * @param eps
	 * @return
	 */
	public SVGErrorBar createErrorBar(SVGLine line, double maxDist, double eps) {
		SVGErrorBar errorBar = null;
		if (line != null) {
			getOrCreateCentroid();
			if (centroid != null) {
				Point2 point = new Point2(centroid.getXY());
				if (line.getEuclidLine().contains(point, eps, true)) {
					SVGLine newLine = createLineWithPointAt0(line, point);
					if (newLine.getXY(0).getDistance(point) < maxDist) {
						int serial = -1;
						if (line.isHorizontal(eps)) {
							serial = (line.getXY(0).getX() < line.getXY(1).getX()) ? BarDirection.RIGHT.ordinal() : BarDirection.LEFT.ordinal();
						} else if (line.isVertical(eps)) {
							serial = (line.getXY(0).getY() < line.getXY(1).getY()) ? BarDirection.TOP.ordinal() : BarDirection.BOTTOM.ordinal();
						}
						BarDirection barDirection = serial == -1 ? null : BarDirection.values()[serial];
						errorBar = new SVGErrorBar(newLine);
						errorBar.setBarDirection(barDirection);
					}
				}
			}
		}
		return errorBar;
	}

	/** create line with Point at the 0 end of line.
	 * 
	 * @param line
	 * @param point
	 * @return
	 */
	private SVGLine createLineWithPointAt0(SVGLine line, Point2 point) {
		SVGLine newLine = new SVGLine(line);
		Real2 xy0 = line.getXY(0);
		Real2 xy1 = line.getXY(1);
		if (xy1.getDistance(point) < xy0.getDistance(point)) {
			newLine.setXY(xy1, 0);
			newLine.setXY(xy0, 1);
		}
		return newLine;
	}
	
	public void setCentroid(Real2 centroid) {
		this.centroid = centroid;
	}
	
	Real2 getOrCreateCentroid() {
		if (centroid == null && shape != null) {
			centroid = shape.getBoundingBox().getCentroid();
		}
		return centroid;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(""+shape+"; "+centroid.format(3)+"; bars: "+errorBars);
		return sb.toString();
	}

	/** creates error bars from lines pointing at 'this'.
	 * 
	 * @param lineList lines to analyze
	 * @param maxDist to nearest end of line to be considered as error bar
	 * @param eps max distance of this centroid from extended line
	 * @return null if lines are not pointing within eps
	 */
	public List<SVGErrorBar> createErrorBarList(SVGLineList lineList, double maxDist, double eps) {
		List <SVGErrorBar> errorBarList = new ArrayList<SVGErrorBar>();
		for (SVGLine line : lineList) {
			SVGErrorBar errorBar = this.createErrorBar(line, maxDist, eps);
			if (errorBar == null) {
				return null;
			} else {
				errorBarList.add(errorBar);
			}
		}
		return errorBarList;
	}

	public void add(SVGErrorBar errorBar) {
		ensureErrorBars();
		errorBars.add(errorBar);
	}
	
	public List<SVGErrorBar> getErrorBarList() {
		ensureErrorBars();
		return errorBars;
	}
	
	/** make a copy and fill with error bars.
	 * 
	 * @return
	 */
	public AbstractCMElement createSVGElement() {
		AbstractCMElement g = new SVGG();
		if (centroid != null) {
			SVGCircle circle = new SVGCircle(centroid, POINT_RADIUS);
			g.appendChild(circle);
		}
		ensureErrorBars();
		for (SVGErrorBar errorBar : errorBars) {
			g.appendChild(errorBar.createSVGElement());
		}
		return g;
	}

	/** shape is either a rect or a line.
	 * 
	 * @return
	 */
	public SVGElement getErrorShape() {
		SVGElement shape = null;
		Real2Range errorBox = getOrCreateErrorBox();
		if (errorBox.getXRange().getRange() < SVGConstants.EPS ||
			errorBox.getYRange().getRange() < SVGConstants.EPS) {
			Real2[] corners = errorBox.getLLURCorners();
			shape = new SVGLine(corners[0], corners[1]);
		} else {
			shape = SVGRect.createFromReal2Range(errorBox);
		}
		shape.setStrokeWidth(SVGConstants.EPS);
		return shape;
	}

	public static List<SVGBarredPoint> extractErrorBarsFromIBeams(List<SVGLine> horizontalLines, List<SVGLine> verticalLines) {
		List<SVGBarredPoint> barredPoints = new ArrayList<SVGBarredPoint>();
		for (SVGLine verticalLine : verticalLines) {
			SVGElement horizontal0 = verticalLine.getTJunctionCrossbar(horizontalLines, 0);
			SVGElement horizontal1 = verticalLine.getTJunctionCrossbar(horizontalLines, 1);
			if (horizontal0 != null && horizontal1 != null) {
				SVGBarredPoint barredPoint = new SVGBarredPoint(verticalLine.getMidPoint());
				List<SVGLine> splitLines = verticalLine.createSplitLines(2);
				SVGErrorBar errorBar0 = new SVGErrorBar(BarDirection.TOP, splitLines.get(0), horizontal0);
				barredPoint.add(errorBar0);
				SVGErrorBar errorBar1 = new SVGErrorBar(BarDirection.BOTTOM,splitLines.get(1), horizontal1);
				barredPoint.add(errorBar1);
				barredPoints.add(barredPoint);
			}
		}
		return barredPoints;
	}

	// ==============================
	
	private Real2Range getOrCreateErrorBox() {
		if (errorBox == null) {
			errorBox = new Real2Range();
			ensureErrorBars();
			for (SVGErrorBar errorBar : errorBars) {
				if (errorBar != null) {
					Real2 xy = errorBar.getLine().getXY(1);
					errorBox.add(xy);
				}
			}
		}
		return errorBox;
	}
}
