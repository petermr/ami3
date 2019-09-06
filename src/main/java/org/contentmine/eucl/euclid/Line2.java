/**
 *    Copyright 2011 Peter Murray-Rust
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

package org.contentmine.eucl.euclid;

import org.apache.log4j.Logger;

/** line
 * determined by one point (R) and a vector (V)
 * this gives L = R + nV
 * can assume that R and R+V are the two "ends of the line"
 * the semantics of this are application-dependent
 * @author pm286
 *
 */
public class Line2 implements EuclidConstants {
	private static Logger LOG = Logger.getLogger(Line2.class);

	public final static Line2 XAXIS = new Line2(new Real2(0.0, 0.0), new Real2(1.0, 0.0));
	public final static Line2 YAXIS = new Line2(new Real2(0.0, 0.0), new Real2(0.0, 1.0));
	
	private Real2 from;
	private Real2 to;
	private Vector2 vector;
	private Vector2 unitVector = null;

	// lazy evaluation
	private double slope = Double.NaN;
	private double c = Double.NaN;
	private double xint = Double.NaN;
	
	/**
	 * generates vector
	 * @param from
	 * @param to
	 */
	public Line2(Real2 from, Real2 to) {
		this.from = new Real2(from);
		this.to = new Real2(to);
		createVector();
		init();
	}

	private void createVector() {
		vector = new Vector2(to.subtract(from));
		if (vector.getLength() < Real.EPS) {
			LOG.trace("line has coincident points: "+from+" ... "+to);
		}
	}
	
	private void init() {
		slope = Double.NaN;
		c = Double.NaN;
		xint = Double.NaN;
	}
	
	/**
	 * generates to
	 * @param from
	 * @param v
	 */
	public Line2(Real2 from, Vector2 v) {
		if (v.getLength() < Real.EPS) {
			throw new EuclidRuntimeException("Cannot form line from coincident points");
		}
		this.from = new Real2(from);
		this.vector = new Vector2(v);
		to = from.plus(v);
	}

	/** get slope.
	 * "m" in y=m*x+c
	 * if x component is zero returns Double.*_INFINITY;
	 * @return slope, Double.POSITIVE_INFINITY or Double.NEGATIVE_INFINITY;
	 */
	public double getSlope() {
		if (Double.isNaN(slope)) {
			try { 
				slope = vector.getY() / vector.getX();
			} catch (ArithmeticException ae) {
				slope = (vector.getY() > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
			}
		}
		return slope;
	}

	/**
	 * 	 * "c" in y=m*x+c
	 * @return intercept or Double.NaN if slope is infinite
	 */
	public double getYIntercept() {
		if (Double.isNaN(c)) {
			getSlope();
			if (!Double.isNaN(slope) && 
				slope < Double.POSITIVE_INFINITY &&
				slope > Double.NEGATIVE_INFINITY
				) {
				c = from.getY() - from.getX() * slope;
			}
		}
		return c;
	}
	
	/**
	 * "c" in y=m*x+c
	 * @return intercept or Double.NaN if slope is infinite
	 */
	public double getXIntercept() {
		if (Double.isNaN(xint)) {
			getYIntercept();
			if (Double.isNaN(slope) || 
					Double.compare(slope, Double.NEGATIVE_INFINITY) == 0 ||
					Double.compare(slope, Double.POSITIVE_INFINITY) == 0
					) {
				xint = from.getX();
			} else if(Math.abs(slope) > Real.EPS) {
				xint = - c / slope;
			}
		}
		return xint;
	}

	/** get intersection of two lines
	 * see softSurfer algorithm
	 * @param line1
	 * @return null if parallel or antiparallel
	 */
	public Real2 getIntersection(Line2 line1) {
		Real2 inter = null;
		if (this.from.getDistance(line1.from) < Real.EPS) {
			inter = this.from;
		} else {
			double perpv = this.vector.getPerpProduct(line1.vector);
			Vector2 w = new Vector2(this.from.subtract(line1.from));
			double perpw = line1.vector.getPerpProduct(w);
			// this = point + lambda * vector;
			double lambda = perpw / perpv;
			Real2 vv = vector.multiplyBy(lambda);
			inter = this.from.plus(vv); 
		}
		return inter;
	}
	/** does a line contain a point.
	 * line is of zero thickness
	 * 
	 * @param point
	 * @param eps distance within which point muct approach line
	 * @param allowExtension if true allow point to be "outside" line
	 * segment
	 * @return true if within eps of line
	 */
	public boolean contains(Real2 point, double eps, boolean allowExtension) {
		boolean contains = false;
		if (point != null) {
			double dist = Math.abs(this.getDistanceFromPoint(point));
			if (dist < eps) {
				double length = this.getLength() + eps; 
				contains = allowExtension ||
					(point.getDistance(from) < length &&
					point.getDistance(to) < length);
				}
			}
		return contains;
	}


	/** swaps to and from coordinates.
	 * 
	 */
	public void flipCoordinates() {
		Real2 temp = from;
		from = to;
		to = temp;
	}
	
	/**
	 * get unit vector convenience
	 * @return vector
	 */
	public Vector2 getUnitVector() {
		if (unitVector == null) {
			unitVector = new Vector2(vector.getUnitVector());
		}
		return unitVector;
	}
	/** signed perpendicular distance from point to infinite line.
	 * @param point
	 * @return distance
	 * @deprecated use new name (unsignedDistanceFromPoint)
	 */
	public double getDistanceFromPoint(Real2 point) {
		//FIXME for lines parallel to axis
		getUnitVector();
		LOG.trace(unitVector);
		Vector2 w = new Vector2(point.subtract(from));
		LOG.trace(w);
		return unitVector.getPerpProduct(w);
	}


	/** signed perpendicular distance from point to infinite line.
	 * 
	 * will depend on direction of line.
	 * 
	 * @param point
	 * @return distance
	 */
	public double getSignedDistanceFromPoint(Real2 point) {
		getUnitVector();
		LOG.trace(unitVector);
		Vector2 w = new Vector2(point.subtract(from));
		LOG.trace(w);
		return unitVector.getPerpProduct(w);
	}


	
	/** perpendicular distance from point to infinite line.
	 * @param point
	 * @return distance
	 */
	public double getUnsignedDistanceFromPoint(Real2 point) {
		Real2 pb = getNearestPointOnLine(point);
		return pb.getDistance(point);
	}

	/** may be redundant...
	 * 
	 * @param point
	 * @return
	 */
	public Real2 getNearestPointNew(Real2 point) {
/**		dist_Point_to_Line( Point P, Line L)
		{
		     Vector v = L.P1 - L.P0;
		     Vector w = P - L.P0;

		     double c1 = dot(w,v);
		     double c2 = dot(v,v);
		     double b = c1 / c2;

		     Point Pb = L.P0 + b * v;
		     return d(P, Pb);
		}
*/
		Vector2 v = new Vector2(this.getXY(1).subtract(this.getXY(0)));
		Vector2 w = new Vector2(point.subtract(this.getXY(0)));
		double c1 = w.dotProduct(v);
		double c2 = v.dotProduct(v);
		double b = c1 / c2;
		Real2 pb = this.getXY(0).plus(v.multiplyBy(b));
		return pb;
	}

	/** get nearest point on infinite line.
	 * @param point
	 * @return distance
	 */
	public Real2 getNearestPointOnLine(Real2 point) {
		getUnitVector();
		Vector2 lp = new Vector2(point.subtract(this.from));
		double lambda = unitVector.dotProduct(lp);
		Real2 vv = unitVector.multiplyBy(lambda);
		return from.plus(vv);
	}

	/** are two lines parallel within tolerance.
	 * 
	 * @param line
	 * @param eps maximum allowed angle between lines
	 * @return null if any arguments null
	 */
	public Boolean isParallelTo(Line2 line, Angle eps) {
		Boolean parallel = null;
		if (line != null && eps != null) {
			Angle angle = getAngleMadeWith(line);
			angle.normalizeToPlusMinusPI();
			parallel = Math.abs(angle.getRadian()) < Math.abs(eps.getRadian());
		}
		return parallel;
	}

	/**
	 * @param line
	 * @param eps maximum allowed angle between unsigned lines (i.e. << Math.PI/2)
	 * @return null if any arguments null
	 */
	public boolean isAntiParallelTo(Line2 line, Angle eps) {
		Boolean antiParallel = null;
		if (line != null && eps != null) {
			Angle angle = getAngleMadeWith(line);
			angle.normalizeTo2Pi();
			antiParallel = Math.abs(Math.abs(angle.getRadian()) - Math.PI) < Math.abs(eps.getRadian());
		}
		return antiParallel;
	}
	
	/** are unsigned lines parallel.
	 *
	 * @param line
	 * @param eps
	 * @return isParallel() or isAntiParallel; null if line or eps is null
	 */
	public Boolean isParallelOrAntiParallelTo(Line2 line, Angle eps) {
		Boolean para = null;
		if (line != null && eps != null) {
			para = this.isParallelTo(line, eps) || this.isAntiParallelTo(line, eps);
		}
		return para;
	}
	
	/** calculated unsigned distance between parallel lines.
	 * 
	 * <p>uses distance from this.getXY(0) to nearest point on line.</p>
	 * 
	 * <p>if lines are not exactly parallel the result has no absolute meaning but is heuristically useful.</p>
	 * 
	 * @param line
	 * @param eps
	 * @return null if args are null or lines are not parallel
	 */
	public Double calculateUnsignedDistanceBetweenLines(Line2 line, Angle eps) {
		Double d = null;
		if (this.isParallelOrAntiParallelTo(line, eps)) {
			Real2 p = line.getNearestPointOnLine(this.getXY(0));
			d = this.getXY(0).getDistance(p);
		}
		return d;
	}

	
	/** convenience method.
	 * gets angle formed between lines using 
	 * Vector2.getAngleMadeWith(Vector2)
	 * @param line
	 * @return angle or null
	 */
	public Angle getAngleMadeWith(Line2 line) {
		Angle angle = null;
		if (line != null) {
			angle = this.getVector().getAngleMadeWith(line.getVector());
		}
		return angle;
	}
	
	public Boolean isPerpendicularTo(Line2 line, Angle angleEps) {
		if (line == null || angleEps == null) return null;
		Angle angle = this.getAngleMadeWith(line);
		return Math.abs(Math.abs(angle.getRadian()) - Math.PI * 0.5) < angleEps.getRadian();
	}
	
	/** gets multiplier of point from "from"
	 * finds nearest point (pp) on line (so avoids rounding errors)
	 * then finds pp = from + vector * lambda
	 * if pp is within segment , lambda is 0, 1
	 * @param p
	 * @return lambda
	 */
	public double getLambda(Real2 p) {
		Real2 near = this.getNearestPointOnLine(p);
		Real2 delta = near.subtract(from);
		double lambda = (Math.abs(vector.getX()) > Math.abs(vector.getY())) ?
			delta.getX() / vector.getX() : delta.getY() / vector.getY();
		return lambda;
	}

	/** get mid point
	 * @return mid point
	 */
	public Real2 getMidPoint() {
		Real2 mm = this.from.plus(this.to);
		return mm.multiplyBy(0.5);
	}

	/** get length
	 * @return length
	 */
	public double getLength() {
		return vector.getLength();
	}

	/**
	 * @return the from
	 */
	public Real2 getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public Real2 getTo() {
		return to;
	}

	/** get point at either end.
	 * 
	 * @param i (0/from or 1/to)
	 * @return
	 */
	public Real2 getXY(int i) {
		Real2 xy = null;
		if (i == 0) {
			xy = from;
		} else if (i == 1) {
			xy = to;
		} else {
			throw new EuclidRuntimeException("Bad point in Line2 "+i);
		}
		return xy;
	}

	/** set point at either end.
	 * 
	 * @param i (0/from or 1/to)
	 * @return
	 */
	public void setXY(Real2 xy, int i) {
		if (i == 0) {
			from = new Real2(xy);
		} else if (i == 1) {
			to = new Real2(xy);
		} else {
			throw new EuclidRuntimeException("Bad point in Line2 "+i);
		}
		createVector();
	}

	/**
	 * @return the vector
	 */
	public Vector2 getVector() {
		return vector;
	}

	/** creates point at (signed) distance dist from "from" point
	 * 
	 * newPoint = from + (dist / line.length) * vector
	 * @param length
	 * @return new Point
	 * 
	 */
	public Real2 createPointOnLine(Double dist) {
		double length = this.getLength();
		double multiplier = dist / length;
		Real2 newVector = vector.multiplyBy(multiplier);
		Real2 newPoint = new Real2(from);
		newPoint.plusEquals(newVector);
		return newPoint;
	}
	
	/** creates point at (signed) distance dist from index point
	 * 
	 * vector = xy(1-index) <- xy(index)
	 * newPoint = xy(index) + (dist / line.length) * vector
	 * @param length
	 * @return new Point
	 */
	public Real2 createPointOnLine(Double dist, int index) {
		double length = this.getLength();
		double multiplier = dist / length;
		Real2 newVector = vector.multiplyBy(multiplier);
		if (index == 1){
			newVector.negative();
		}
		Real2 newPoint = getXY(index).plus(newVector);
		return newPoint;
	}
	
	/** gets serial number of point in line specification
	 * if point is within EPS of "from" returns 0
	 * if point is within EPS of "to" returns 1
	 * else returns -1
	 */
	public int getSerial(Real2 point, double eps) {
		if (from.getDistance(point) < eps) {
			return 0;
		}
		if (to != null && to.getDistance(point) < eps) {
			return 1;
		}
		return -1;
	}
	
	/**
	 * @return string
	 */
	public String toString() {
		return "line: from("+from+") to("+to+") v("+vector+")";
	}

	public boolean isHorizontal(Angle eps) {
		return this.isParallelOrAntiParallelTo(XAXIS, eps);
	}

	public boolean isVertical(Angle eps) {
		return this.isParallelOrAntiParallelTo(YAXIS, eps);
	}

	/** creates a point at the centre of a square where this line is one of the sides.
	 * find midpoint, and construct perpendicular line of l/2
	 * 
	 * Used an rotating a box (especially a page) so it remaind in the same region
	 * @return
	 */
	public Real2 createSquarePoint() {
		Real2 l2 = to.subtract(from).multiplyBy(0.5);
		Real2 l2a = new Real2(l2.y, -l2.x);
		Real2 p = from.plus(l2).plus(l2a);
		return p;
	}

}
