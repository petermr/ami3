package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGShape;

public class SVGBarredPointList extends SVGG implements Iterable<SVGBarredPoint> {

	private static Logger LOG = Logger.getLogger(SVGBarredPointList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGBarredPoint> points;
	
	public Iterator<SVGBarredPoint> iterator() {
		ensurePoints();
		return points.iterator();
	}

	private void ensurePoints() {
		if (points == null) {
			points = new ArrayList<SVGBarredPoint>();
		}
	}
	
	public void addPoint(SVGBarredPoint point) {
		ensurePoints();
		points.add(point);
	}
	
	public void addPoint(SVGShape shape) {
		SVGBarredPoint point = SVGBarredPoint.createPoint(shape);
		if (point != null) {
			addPoint(point);
		}
	}

	/** Create barredPoints from circles and error bar lines.
	 * 
	 * convenience method where circleList and lineLisList are aligned.
	 * 
	 * @param circleList
	 * @param lineListList
	 * @param rad
	 * @param eps
	 * @return
	 */
	public static SVGBarredPointList createBarredPointList(
			List<SVGCircle> circleList, List<SVGLineList> lineListList, double maxDist, double eps) {
		if (circleList == null || lineListList == null) {
			return null;
		}
		if (circleList.size() != lineListList.size()) {
			throw new RuntimeException("Unequal numbers of circles and lines");
		}
		SVGBarredPointList barredPointList = new SVGBarredPointList();
		for (int i = 0; i < circleList.size(); i++) {
			SVGCircle circle = circleList.get(i);
			SVGLineList lineList = lineListList.get(i);
			SVGBarredPoint barredPoint = SVGBarredPoint.createPoint(circle);
			if (barredPoint == null) {
				throw new RuntimeException("cannot create circle for barred point");
			}
			List<SVGErrorBar> errorBarList = barredPoint.createErrorBarList(lineList, maxDist, eps);
			if (errorBarList == null) {
				throw new RuntimeException("cannot create errorBars for barred point");
			}
			for (SVGErrorBar errorBar : errorBarList) {
				barredPoint.add(errorBar);
			}
			barredPointList.add(barredPoint);
		}
		return barredPointList;
	}

	private void add(SVGBarredPoint barredPoint) {
		ensurePoints();
		points.add(barredPoint);
	}

	public int size() {
		ensurePoints();
		return points.size();
	}

	public SVGBarredPoint get(int i) {
		ensurePoints();
		return points.get(i);
	}

	public SVGBarredPoint remove(int i) {
		ensurePoints();
		return points.remove(i);
	}
}
