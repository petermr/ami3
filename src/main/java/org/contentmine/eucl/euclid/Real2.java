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
import java.awt.geom.Point2D;
import java.util.List;

import org.apache.log4j.Logger;
/**
 * A pair of FPt numbers with no other assumptions
 * 
 * Contains two doubles Can be used as it is, but there are some specialised
 * derived classes (for example Complex (a complex number), Point2 (x,y coords),
 * etc), The default value is 0.0, 0.0.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Real2 implements EuclidConstants {
	private static Logger LOG = Logger.getLogger(Real2.class);
    /** the first floating point value */
    public double x;
    /** the second floating point value */
    public double y;
    /**
     * constructor.
     */
    public Real2() {
        x = 0.0;
        y = 0.0;
    }
    /**
     * constructor.
     * 
     * @param x
     * @param y
     */
    public Real2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * constructor.
     * 
     * @param x
     * @param y
     */
    public Real2(int x, int y) {
        this((double) x, (double) y);
    }
    
    /**
     * constructor.
     * 
     * @param x
     * @param y
     */
    public Real2(Int2 int2) {
    	this(int2.getX(), int2.getY());
    }
    
    /**
     * constructor.
     * 
     * @param dd
     * @throws RuntimeException null or not length=2
     */
    
    /**
     * two reals separated by whitespace
     * OR
     * (real, real)
     * 
     * @param s
     */
    public Real2(String s) {
    	this(Util.splitToDoubleArray(s, S_SPACE));
    }
    /**
     * two integers separated by delimiter
     * @param s
     */
    public Real2(String s, String delimiter) {
    	this(Util.splitToDoubleArray(s, delimiter));
    }
    /**
     * constructor.
     * 
     * @param x the two components
     */
    public Real2(double[] x) {
    	if (x == null) {
    		throw new EuclidRuntimeException("requires non-null array");
    	}
    	if (x.length != 2) {
    		throw new EuclidRuntimeException("requires array of length 2; found: "+x.length);
    	}
        this.x = x[0];
        this.y = x[1];
    }
    
    /** reads output format from toString()
     * "(x,y)"
     * @param s
     * @return
     */
    public static Real2 createFromString(String s) {
    	Real2 real2 = null;
    	if (s != null) {
    		s = s.trim();
    		if (s.startsWith(S_LBRAK) && s.endsWith(S_RBRAK)) {
    			real2 = new Real2(s.substring(1, s.length()-1), S_COMMA);
    		}
    	}
    	return real2;
    }
    /**
     * copy constructor
     * 
     * @param r
     */
    public Real2(Real2 r) {
        this.x = r.x;
        this.y = r.y;
    }
    
    public static Real2 createReal2(Point2D p) {
    	return p == null ? null : new Real2(p.getX(), p.getY());
	}
	/**
     * swaps the x and y values
     */
    public void swap() {
        double t = x;
        x = y;
        y = t;
    }
    /**
     * sorts x and y so that x <= y
     */
    public void sortAscending() {
        if (x > y)
            this.swap();
    }
    /**
     * sorts x and y so that x >= y
     */
    public void sortDescending() {
        if (x < y)
            this.swap();
    }
    /**
     * set to 0 0
     */
    public void clear() {
        x = y = 0.0;
    }
    /**
     * set x.
     * 
     * @param xx
     */
    public void setX(double xx) {
        x = xx;
    }
    /**
     * set y.
     * 
     * @param yy
     */
    public void setY(double yy) {
        y = yy;
    }
    /**
     * equality.
     * 
     * @param r to test
     * @return equals
     * @deprecated
     */
    public boolean isEqualTo(Real2 r) {
        return (Real.isEqual(x, r.x) && Real.isEqual(y, r.y));
    }
    
    /**
     * equality.
     * 
     * @param r to test
     * @param eps tolerance
     * @return equals
     */
    public boolean isEqualTo(Real2 r, double eps) {
        return (Real.isEqual(x, r.x, eps) && Real.isEqual(y, r.y, eps));
    }
    
    /**
     * is the point the origin.
     * 
     * @return true if origin
     * @deprecated USE epsilon method
     */
    public boolean isOrigin() {
    	return Real.isZero(x, Real.getEpsilon()) && Real.isZero(y, Real.getEpsilon());
    }

    /**
     * is the point the origin.
     * @param epsilon
     * @return true if origin
     */
    public boolean isOrigin(double epsilon) {
    	return Real.isZero(x, epsilon) && Real.isZero(y, epsilon);
    }

    /**
     * add two points to give vector sum
     * 
     * @param r2
     * @return point
     * 
     */
    public Real2 plus(Real2 r2) {
        return new Real2(x + r2.x, y + r2.y);
    }
    /**
     * add two points to give vector sum
     * 
     * @param r2
     * 
     */
    public void plusEquals(Real2 r2) {
        this.x = x + r2.x;
        this.y = y + r2.y;
    }
    /**
     * subtract two points to give vector difference
     * 
     * @param r2
     * @return point
     * 
     */
    public Real2 subtract(Real2 r2) {
        return new Real2(x - r2.x, y - r2.y);
    }
    /**
     * multiply both components by minus one MODIFIES 'this'
     */
    public void negative() {
        this.x = -this.x;
        this.y = -this.y;
    }
    /**
     * return multiplication of a point by a scalar
     * does not alter this
     * @param f
     * @return point
     */
    public Real2 multiplyBy(double f) {
        return new Real2(x * f, y * f);
    }
    
    /**
     * multiply a point by a scalar 
     * alters this
     * @param f
     */
    public void multiplyEquals(double f) {
    	this.x = x * f;
    	this.y = y * f;
    }
    /**
     * get X value
     * 
     * @return x value
     */
    public double getX() {
        return x;
    }
    /**
     * get Y value
     * 
     * @return y value
     */
    public double getY() {
        return y;
    }
    /**
     * get either value counts from ZERO
     * 
     * @param elem
     * @return value
     * @throws EuclidRuntimeException
     */
    public double elementAt(int elem) throws EuclidRuntimeException {
        if (elem == 0) {
            return x;
        } else if (elem == 1) {
            return y;
        }
        throw new EuclidRuntimeException("bad index " + elem);
    }
    /**
     * get length of Real2 if centered on origin
     * 
     * @return length
     */
    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }
    /**
     * get distance to another Real2
     * 
     * @param r
     * @return distance
     */
    public double getDistance(Real2 r) {
        return (r == null) ? Double.NaN : Math.sqrt((x - r.x) * (x - r.x)
                + (y - r.y) * (y - r.y));
    }
    /**
     * get squared distance to another Real2
     * 
     * @param r
     * @return dist2
     */
    public double getSquaredDistance(Real2 r) {
        return (r == null) ? Double.NaN : (x - r.x) * (x - r.x) + (y - r.y)
                * (y - r.y);
    }
    /**
     * point midway between 'this' and 'p'
     * 
     * @param p
     * @return midpoint
     */
    public Real2 getMidPoint(Real2 p) {
        return (p == null) ? null : new Real2((this.x + p.x) / 2.0, (this.y + p.y) / 2.0);
    }
    /**
     * get unit vector
     * 
     * @return unit vector
     * @exception EuclidRuntimeException
     *                <TT>this</TT> was of zero length
     */
    public Real2 getUnitVector() {
        double length = this.getLength();
        if (Real.isZero(length, Real.getEpsilon())) {
            throw new EuclidRuntimeException("zero length vector");
        }
        Real2 temp = new Real2(this.x, this.y);
        temp = temp.multiplyBy(1.0 / length);
        return temp;
    }
    /**
     * get dot product
     * 
     * @param r
     * @return dot
     */
    public double dotProduct(Real2 r) {
        return (this.x * r.x + this.y * r.y);
    }
    /**
     * get angle between 3 Real2s (the second is in the centre)
     * 
     * @param p1
     * @param p2
     * @param p3
     * @return angle is CLOCKWISE from p1 to p3
     */
    public static Angle getAngle(Real2 p1, Real2 p2, Real2 p3) {
    	if (p1 == null || p2 == null || p3 == null) {
//    		throw new RuntimeException("null coordinates");
    		return null;
    	}
        double x1 = p1.x - p2.x;
        double y1 = p1.y - p2.y;
        double x3 = p3.x - p2.x;
        double y3 = p3.y - p2.y;
        double angle1 = Math.atan2(x1, y1);
        double angle3 = Math.atan2(x3, y3);
        double angle13 = angle1 - angle3;
        Angle angle123 = new Angle(angle13, Angle.Units.RADIANS);
        double d12 = p1.getDistance(p2);
        double d13 = p1.getDistance(p3);
        double d23 = p2.getDistance(p3);
        double cost = (-d13*d13 + d12*d12 + d23*d23)/(2*d12*d23);
        double anglex = Math.acos(cost);
        LOG.trace("AAA "+anglex+"/"+angle13+"//"+p1+"/"+p2+"/"+p3);
        return angle123;
    }
    /**
     * transforms the point by a rot-trans matrix MODIFIES 'this' Note the next
     * routine is often better
     * 
     * @param t
     * 
     */
    public void transformBy(Transform2 t) {
    	if (t != null) {
	        double xx = t.flmat[0][0] * this.x + t.flmat[0][1] * this.y
	                + t.flmat[0][2];
	        double yy = t.flmat[1][0] * this.x + t.flmat[1][1] * this.y
	                + t.flmat[1][2];
	        this.x = xx;
	        this.y = yy;
    	}
    }
    /**
     * gets a point transformed by a rot-trans matrix does NOT MODIFY 'this'
     * 
     * @param t
     * @return point
     */
    public Real2 getTransformed(Transform2 t) {
        Real2 p = new Real2(this);
        p.transformBy(t);
        return p;
    }
    /**
     * creates a polygon
     * 
     * returns a point array from two points Serial numbers of points are 0 and
     * end that is point1 is points[0] and point2 is points[end] if end == 0,
     * end=>1
     * 
     * @param point1
     * @param nPoints
     * @param point2
     * @param end
     * @param repelPoint
     * @return points
     * 
     */
    public static Real2[] addPolygonOnLine(Real2 point1, Real2 point2,
            int nPoints, int end, Real2 repelPoint) {
        if (end < 1)
            end = 1;
        Real2[] newPoints = new Real2[nPoints];
        Real2 mid = point1.getMidPoint(point2);
        double dTheta = Math.PI * 2.0 / (double) nPoints;
        double lineTheta = (point1.subtract(point2)).getAngle();
        double line2 = (point1.subtract(mid)).getLength();
        // this accounts for number of points skipped
        double halfTheta = 0.5 * (dTheta * (double) end);
        // find centre
        double dist = line2 / Math.tan(halfTheta);
        // as far as possible from repelPoint
        double angle = lineTheta - 0.5 * Math.PI;
        Real2 center0 = mid.makePoint(dist, angle);
        Real2 center = center0;
        // this mess takes care of direction
        double theta0 = -halfTheta + angle + Math.PI;
        double theta = theta0;
        if (repelPoint != null) {
            double tempDist = (center.subtract(repelPoint)).getLength();
            Real2 center1 = mid.makePoint(dist, angle + Math.PI);
            // swap it, change direction of traverse
            if ((center1.subtract(repelPoint)).getLength() > tempDist) {
                center = center1;
                dTheta = -dTheta;
                theta = +halfTheta + angle;
            }
        }
        // radius of polygon
        double rad = line2 / Math.sin(halfTheta);
        // now add points
        newPoints = makePoints(center, nPoints, rad, theta, dTheta);
        // for even-number polygons exactly bisected the repel cannot work on
        // centers so
        // also required for centroids on re-entrant nuclei
        // we examine the newly created points
        if (repelPoint != null /* && 2*end == nPoints */) {
            double dista = newPoints[1].subtract(repelPoint).getLength();
            double distb = newPoints[nPoints - 2].subtract(repelPoint)
                    .getLength();
            if (dista > distb) {
                center = center0;
                dTheta = -dTheta;
                theta = theta0;
                newPoints = makePoints(center, nPoints, rad, theta, dTheta);
            } else {
            }
        }
        return newPoints;
    }
    private static Real2[] makePoints(Real2 center, int nPoints, double rad,
            double theta, double dTheta) {
        Real2[] points = new Real2[nPoints];
        for (int i = 0; i < nPoints; i++) {
            points[i] = center.makePoint(rad, theta);
            theta += dTheta;
        }
        return points;
    }
    /**
     * get angle between origin and this point (i.e polar coords) - uses atan2
     * (that is anticlockwise from X axis); if x == y == 0.0 presumably returns
     * NaN
     * 
     * @return angle
     * 
     */
    public double getAngle() {
        return Math.atan2(y, x);
    }
    /**
     * make a new point at (dist, theta) from this
     * 
     * @param rad
     *            the distance to new point
     * @param theta
     *            the angle to new point (anticlockwise from X axis), that is
     *            theta=0 gives (rad, 0), theta=PI/2 gives (0, rad)
     * @return Real2 new point
     */
    public Real2 makePoint(double rad, double theta) {
        Real2 point = new Real2();
        point.x = this.x + rad * Math.cos(theta);
        point.y = this.y + rad * Math.sin(theta);
        return point;
    }
    /**
     * get centroid of all points. does not modify this
     * 
     * @param p2Vector
     * @return the centroid
     */
    public static Real2 getCentroid(List<Real2> p2Vector) {
        if (p2Vector.size() < 1)
            return null;
        Real2 p = new Real2();
        for (int j = 0; j < p2Vector.size(); j++) {
            p = p.plus(p2Vector.get(j));
        }
        double scale = 1.0 / (new Double(p2Vector.size()).doubleValue());
        p = p.multiplyBy(scale);
        return p;
    }
    /**
     * get serialNumber of nearest point
     * 
     * @param p2v
     * @param point
     * @return serial
     */
    public static int getSerialOfNearestPoint(List<Real2> p2v, Real2 point) {
        double dist = Double.MAX_VALUE;
        int serial = -1;
        for (int j = 0; j < p2v.size(); j++) {
            double d = p2v.get(j).subtract(point).getLength();
            if (d < dist) {
                serial = j;
                dist = d;
            }
        }
        return serial;
    }
    /**
     * get rectangular distance matrix.
     * 
     * matrix need not be square or symmetric unless coords == coords2 d(ij) is
     * distance between coord(i) and coords2(j).
     * 
     * @param coords1
     * @param coords2
     * @return distance matrix
     */
    public static RealMatrix getDistanceMatrix(List<Real2> coords1,
            List<Real2> coords2) {
        int size = coords1.size();
        int size2 = coords2.size();
        RealMatrix distMatrix = new RealMatrix(size, size2);
        double[][] distMatrixMat = distMatrix.getMatrix();
        for (int i = 0; i < size; i++) {
            Real2 ri = coords1.get(i);
            for (int j = 0; j < size2; j++) {
                Real2 rj = coords2.get(j);
                double dij = ri.getDistance(rj);
                distMatrixMat[i][j] = dij;
            }
        }
        return distMatrix;
    }
    /**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        return S_LBRAK + x + S_COMMA + y + S_RBRAK;
    }
    /**
     * get x y as array.
     * 
     * @return array of length 2
     */
    public double[] getXY() {
        double[] dd = new double[2];
        dd[0] = x;
        dd[1] = y;
        return dd;
    }
    
    /** round to decimal places.
     * 
     * @param places
     * @return this
     */
    public Real2 format(int places) {
    	x = Util.format(x, places);
    	y = Util.format(y, places);
    	return this;
    }
    /**
     * formats coordinates to x,y
     * e.g. "123,456"
     * 
     * @return formatted trimmed value
     */
	public String trimToIntegersWithoutBrackets() {
		return String.valueOf(format(0))
				.replaceAll("\\(|\\)", "")
				.replaceAll("\\.0", "");
	}
}
