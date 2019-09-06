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



/**
 * 3-dimensional point class
 * 
 * 
 * Point3 represents a 3-dimensional point. It is one of a set of primitives
 * which can be combined to create and manipulate complex 3-dimensional objects.
 * Points can be transformed with rotation matrices or rotation-translation
 * matrices (Transform3), can be calculated from other primitives or can be used
 * to generate other primitives.
 * 
 * Default point is 0.0, 0.0, 0.0
 * 
 * @author <A HREF=mailto:@p.murray-rust@mail.cryst.bbk.ac.uk>Peter Murray-Rust</A>
 * @see Vector3
 * @see Line3
 * @see Point3Vector
 * @see Plane3
 * 
 * @author (C) P. Murray-Rust, 1996
 */

public class Point3 implements EuclidConstants {
    final static Logger LOG = Logger.getLogger(Point3.class);

    /**
     * tolerance between crystal fractional coordinates. allows for 1/3 being
     * represented as 0.3333 which will not fit normal equality
     */
    public final static double CRYSTALFRACTEPSILON = 0.001;

    /**
     * the coordinates of the point
     */
    protected double[] flarray = new double[3];

    /**
     * constructor.
     */
    public Point3() {
    }

    /**
     * formed from point components
     * 
     * @param x
     * @param y
     * @param z
     */
    public Point3(double x, double y, double z) {
        flarray[0] = x;
        flarray[1] = y;
        flarray[2] = z;
    }

    /**
     * copy constructor
     * 
     * @param p
     */
    public Point3(Point3 p) {
        System.arraycopy(p.flarray, 0, flarray, 0, 3);
    }

    /**
     * constructor from a double[] (or a RealArray).
     * 
     * @param f
     * @throws EuclidRuntimeException
     */
    public Point3(double[] f) throws EuclidRuntimeException {
        Util.check(f, 3);
        System.arraycopy(f, 0, flarray, 0, 3);
    }

    /**
     * make a point from a vector creates the point at head of vector rooted at
     * the origin
     * 
     * @param v
     */
    public Point3(Vector3 v) {
        System.arraycopy(v.flarray, 0, flarray, 0, 3);
    }

    /**
     * get components as double[]
     * 
     * @return the array
     */
    public double[] getArray() {
        return flarray;
    }

    /**
     * sets the point to the origin
     */
    public void clear() {
        flarray[0] = flarray[1] = flarray[2] = 0.0;
    }

    /**
     * are two points identical. compares content of points with Real.isEqual()
     * 
     * @param p
     *            point to compare
     * @return equal if coordinates are equal within Real.epsilon
     */
    public boolean isEqualTo(Point3 p) {
        return Real.isEqual(flarray, p.flarray, Real.getEpsilon());
    }

    /**
     * are two points identical. compares x, y, z coordinates of points
     * 
     * @param p
     *            point to compare
     * @param eps
     *            the tolerance
     * @return equal if coordinates are equal within Real.epsilon
     */
    public boolean isEqualTo(Point3 p, double eps) {
        return Real.isEqual(flarray, p.flarray, eps);
    }

    /**
     * are two crystallographic points identical. shifts x, y, z by +-1.0 if
     * necessary compares content of crystallographically normalised points with
     * Real.isEqual()
     * 
     * @param p
     *            point to compare
     * @return equal if coordinates are equal within CRYSTALFRACTEPSILON
     */
    public boolean equalsCrystallographically(Point3 p) {
    	if (p.flarray == null) return false;
        Point3 crystal = new Point3(this);
        crystal.normaliseCrystallographically();
        Point3 crystalP = new Point3(p);
        crystalP.normaliseCrystallographically();
        return Real.isEqual(crystalP.flarray, crystal.flarray,
                Point3.CRYSTALFRACTEPSILON);
    }

    /**
     * normalise crystallographically. shifts x, y, z so that values lie between
     * 0.0 (inclusive) and 1.0 (exclusive) modifies this
     * 
     * @return Vector3 corresponding to the translation vector to crystallographically normalise Point3
     */
    public Vector3 normaliseCrystallographically() {
    	double[] arr = new double[3];
        for (int i = 0; i < 3; i++) {
        	double start = flarray[i];
            flarray[i] = normaliseCrystallographically(flarray[i]);
            arr[i] = Math.round(flarray[i] - start);
        }
        return new Vector3(arr);
    }

    private static double normaliseCrystallographically(double d) {
        while (d >= 1.0) {
            d -= 1.0;
        }
        while (d < 0.0) {
            d += 1.0;
        }
        return d;
    }

    /**
     * is point invariant wrt symmetry operation.
     * 
     * tolerance is decided by Real.isEqual()
     * 
     * @param t3
     *            the transformation
     * @param translate
     *            allow crystallographic translations (+-1)
     * @return true if t3 transforms this onto itself
     */
    public boolean isInvariant(Transform3 t3, boolean translate) {
        Point3 pNew = this.transform(t3);
        return (translate) ? pNew.equalsCrystallographically(this) : pNew
                .isEqualTo(this);
    }

    /**
     * vector between two points. result is vector FROM p2 TO this this -= p2
     * alters this
     * 
     * @param p2
     *            point to subtract
     * @return vector
     */
    public Vector3 subtract(Point3 p2) {
        Vector3 v1 = new Vector3(this);
        for (int i = 0; i < 3; i++) {
            v1.flarray[i] -= p2.flarray[i];
        }
        return v1;
    }

    /**
     * New point by adding points as vectors. used for finding centroids, etc.
     * does NOT alter this
     * 
     * @param p
     *            to add
     * @return NEW point
     */
    public Point3 plus(Point3 p) {
        Point3 p1 = new Point3();
        for (int i = 0; i < 3; i++) {
            p1.flarray[i] = flarray[i] + p.flarray[i];
        }
        return p1;
    }

    /**
     * Move this Point3. alters this
     * 
     * @param pt
     *            point to shift by
     */
    public void plusEquals(final Point3 pt) {
        for (int i = 0; i < 3; i++) {
            flarray[i] += pt.flarray[i];
        }
    }

    /**
     * New point from point+vector does NOT alter this
     * 
     * @param v
     *            to add
     * @return NEW point
     */
    public Point3 plus(Vector3 v) {
        Point3 p1 = new Point3();
        for (int i = 0; i < 3; i++) {
            p1.flarray[i] = flarray[i] + v.flarray[i];
        }
        return p1;
    }

    /**
     * point from point and vector. alter this
     * 
     * @param v
     *            to add
     */
    public void plusEquals(Vector3 v) {
        for (int i = 0; i < 3; i++) {
            flarray[i] += v.flarray[i];
        }
    }

    /**
     * New point from point minus vector. does NOT alter this
     * 
     * @param v
     *            to subtract
     * @return NEW point
     */
    public Point3 subtract(Vector3 v) {
        Point3 p1 = new Point3();
        for (int i = 0; i < 3; i++) {
            p1.flarray[i] = flarray[i] - v.flarray[i];
        }
        return p1;
    }

    /**
     * Shift point from point. does alter this
     * 
     * @param pt
     *            the Point3 to subtract from this
     */
    public void subtractEquals(final Point3 pt) {
        for (int i = 0; i < 3; i++) {
            flarray[i] -= pt.flarray[i];
        }
    }

    /**
     * Shift point from vector3. does alter this
     * 
     * @param vec3
     *            the Vector3 to subtract from this
     */
    public void subtractEquals(final Vector3 vec3) {
        for (int i = 0; i < 3; i++) {
            flarray[i] -= vec3.flarray[i];
        }
    }

    /**
     * scale point. does NOT alter this
     * 
     * @param f
     *            factor to multiply by
     * @return NEW point
     */
    public Point3 multiplyBy(double f) {
        Point3 p1 = new Point3();
        for (int i = 0; i < 3; i++) {
            p1.flarray[i] = flarray[i] * f;
        }
        return p1;
    }

    /**
     * scale point. alters this
     * 
     * @param f
     *            factor to multiply by
     */
    public void multiplyEquals(final double f) {
        for (int i = 2; i >= 0; --i) {
            flarray[i] *= f;
        }
    }

    /**
     * create inverse point. alters this = -this
     */
    public void reflect() {
        flarray[0] = -flarray[0];
        flarray[1] = -flarray[1];
        flarray[2] = -flarray[2];
    }

    /**
     * scale point does NOT alter this
     * 
     * @param f
     *            factor to divide by
     * @return NEW point
     */
    public Point3 divideBy(double f) {
        Point3 p1 = new Point3();
        for (int i = 0; i < 3; i++) {
            p1.flarray[i] = flarray[i] / f;
        }
        return p1;
    }

    /**
     * scale point. alters this
     * 
     * @param f
     *            factor to divide by
     */
    public void divideEquals(final double f) {
        final double f1 = 1.0 / f;
        for (int i = 2; i >= 0; --i) {
            flarray[i] *= f1;
        }
    }

    /**
     * subscript operator.
     * 
     * @param n
     *            the index
     * @throws EuclidRuntimeException
     * @return the element
     */
    public double elementAt(int n) throws EuclidRuntimeException {
        Util.check(n, 0, 2);
        return flarray[n];
    }

    /**
     * sets element.
     * 
     * @param n
     *            the index
     * @param d
     *            the value
     * @throws EuclidRuntimeException
     */
    public void setElementAt(int n, double d) throws EuclidRuntimeException {
        Util.check(n, 0, 2);
        flarray[n] = d;
    }

    /**
     * get transformed point. does NOT modify 'this'
     * 
     * @param t
     *            the transform
     * @return new point
     */
    public Point3 transform(Transform3 t) {
        RealArray col3 = new RealArray(4, 1.0);
        // set the translation in col 3
        col3.setElements(0, this.flarray);
        RealArray result = t.multiply(col3);
        Point3 pout = new Point3(result.getSubArray(0, 2).getArray());
        return pout;
    }

    /**
     * get transformed point. does modify 'this'
     * 
     * @param t
     *            the transform
     */
    public void transformEquals(Transform3 t) {
        RealArray col3 = new RealArray(4, 1.0);
        // set the translation in col 3
        col3.setElements(0, this.flarray);
        RealArray result = t.multiply(col3);
        System.arraycopy(result.getSubArray(0, 2).getArray(), 0, flarray, 0, 3);
    }

    /**
     * distance of point from origin.
     * 
     * @return distance
     */
    public double getDistanceFromOrigin() {
        Vector3 v = new Vector3(this);
        return v.getLength();
    }

    /**
     * Gets the squared Distance between this point and another
     * 
     * @param p2
     *            the other point to get the distance from
     * @return the squared distance
     */
    public double getSquaredDistanceFromPoint(final Point3 p2) {
        double d = flarray[0] - p2.flarray[0];
        double sqdDist = d * d;

        d = flarray[1] - p2.flarray[1];
        sqdDist += (d * d);

        d = flarray[2] - p2.flarray[2];
        sqdDist += (d * d);

        return sqdDist;
    }

    /**
     * distance of point from another point
     * 
     * @param p2
     *            the other point to get the distance from
     * @return the distance
     */
    public double getDistanceFromPoint(Point3 p2) {
        Vector3 v = new Vector3(p2.subtract(this));
        return v.getLength();
    }

    /**
     * distance from plane
     * 
     * @param pl
     * @return distance
     */
    public double distanceFromPlane(Plane3 pl) {
        return pl.getDistanceFromPoint(this);
    }

    /**
     * get closest point on line.
     * 
     * @param l
     *            the line
     * @return the point where distance is shortest
     */
    public Point3 getClosestPointOnLine(Line3 l) {
        return l.getClosestPointTo(this);
    }

    /**
     * is point on line.
     * 
     * @param l
     *            the line
     * @return true if within Real.isEqual() of line
     */
    public boolean isOnLine(Line3 l) {
        // TODO add epsilon
        return l.containsPoint(this);
    }

    /**
     * is point on plane.
     * 
     * @param pl
     *            the plane
     * @return true if within Real.isEqual() of plane
     */
    public boolean isOnPlane(Plane3 pl) {
        return pl.containsPoint(this);
    }

    /**
     * distance from line.
     * 
     * @param l
     *            the line
     * @return the distance
     */
    public double distanceFromLine(Line3 l) {
        return l.getDistanceFromPoint(this);
    }

    /**
     * mid-point of two points.
     * 
     * @param p2
     *            the other point
     * @return the midPoint
     */
    public Point3 getMidPoint(Point3 p2) {
        Point3 p = new Point3();
        {
            for (int i = 0; i < 3; i++) {
                p.flarray[i] = (this.flarray[i] + p2.flarray[i]) / 2.0;
            }
        }
        return p;
    }

    /**
     * get angle. p1-p2-p3
     * 
     * @param p1
     *            the start point
     * @param p2
     *            the vertex point
     * @param p3
     *            the remote point
     * @return angle null if coincient points
     */
    public static Angle getAngle(Point3 p1, Point3 p2, Point3 p3) {
        Vector3 v1 = p1.subtract(p2);
        return (v1.isZero()) ? null : v1.getAngleMadeWith(p3.subtract(p2));
    }

    /**
     * torsion angle. p1-p2-p3-p4
     * 
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return angle unsigned radians or null (null args, or colinearity)
     */
    public static Angle getTorsion(Point3 p1, Point3 p2, Point3 p3, Point3 p4) {
    	Angle angle = null;
        Vector3 v23 = p3.subtract(p2);
        Vector3 v13a = p2.subtract(p1);
        Vector3 v13 = v13a.cross(v23);
        Vector3 v24 = v23.cross(p4.subtract(p3));
        v13.normalize();
        v24.normalize();
        double ang = v13.getAngleMadeWith(v24).getAngle();
        if (v13.getScalarTripleProduct(v24, v23) < 0.0) {
            ang = -ang;
        }
        angle = new Angle(ang);
        return angle;
    }

    /**
     * add point using internal coordinates. used for z-matrix like building
     * p1-p2-p3-newPoint
     * 
     * @param p1
     *            existing point
     * @param p2
     *            existing point
     * @param p3
     *            existing point
     * @param length
     *            p3-p4
     * @param angle
     *            p2-p3-p4
     * @param torsion
     *            this-p2-p3-p4
     * @exception EuclidRuntimeException
     *                two points are coincident or 3 colinear
     * @return new point; null if two points are coincident or three points are
     *         colinear
     */
    public static Point3 calculateFromInternalCoordinates(Point3 p1, Point3 p2,
            Point3 p3, double length, Angle angle, Angle torsion)
            throws EuclidRuntimeException {
        Vector3 v32 = p2.subtract(p3);
        Vector3 v12 = p2.subtract(p1);
        // perp to p1-2-3
        Vector3 v13a = v12.cross(v32);
        Vector3 v13n = v13a.normalize();
        // in plane
        Vector3 v32n = v32.normalize();
        Vector3 v34 = v32n.multiplyBy(length);
        Transform3 t = new Transform3(v13n, angle);
        v34 = v34.transform(t);
        v32n = v32n.negative();
        Transform3 t1 = new Transform3(v32n, torsion);
        v34 = v34.transform(t1);
        Point3 p4 = p3.plus(v34);
        return p4;
    }

    /**
     * is a point at Origin
     * 
     * @return is this within Real.isEqual() of origin
     */
    public boolean isOrigin() {
        for (int i = 0; i < 3; i++) {
            if (!Real.isZero(flarray[i], Real.getEpsilon()))
                return false;
        }
        return true;
    }

    /**
     * string representation.
     * 
     * @return the string
     */
    public String toString() {
        return EC.S_LBRAK + flarray[0] + EC.S_COMMA + EC.S_SPACE + flarray[1] + EC.S_COMMA
                + EC.S_SPACE + flarray[2] + EC.S_RBRAK;
    }

    /** equals.
     * @param that
     * @return is equal
     */
    public boolean equals(Object that) {
    	if (this == that) return true;
    	if (!(that instanceof Point3)) return false;
    	Point3 p3 = (Point3) that;
    	return Real.isEqual(this.getArray(), p3.getArray(), Point3.CRYSTALFRACTEPSILON);
    }
    
    /** hash code.
     * @return coe
     */
    public int hashCode() {
    	int result = 17;
    	int c = 1;
    	long x = Double.doubleToLongBits(flarray[0]);
    	c = c * (int)(x^(x>>>32));
    	long y = Double.doubleToLongBits(flarray[1]);
    	c = c * (int)(y^(x>>>32));
    	long z = Double.doubleToLongBits(flarray[2]);
    	c = c * (int)(z^(x>>>32));
    	return 37*result+c;
    }

    /** main.
     * 
     * @param args
     */
    public static void main(String[] args) {
    	Point3 one = new Point3(0.1, 0.2, 0.3);
    	Point3 two = new Point3(0.1, 0.2, 0.5);
    	if (one.equals(two)) {
    		Util.println("are equal in state");
    	}
    	if (one == two) {
    		Util.println("same object");
    	}
    }
    
}
