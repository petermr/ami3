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
 * 3-dimensional plane class
 *
 * Plane3 represents a 3-dimensional plane. It is one of a set of primitives
 * which can be combined to create and manipulate complex 3-dimensional objects.
 * Planes can be transformed with rotation matrices or rotation-translation
 * matrices (Transform3), can be calculated from other primitives or can be used
 * to generate other primitives.
 * <P>
 * A plane is described by a unit vector (vector) and the perpendicular distance
 * (dist) of the plane from the origin. The absolute direction of the plane
 * (vector) IS important, giving the plane a direction (back and front faces).
 * <P>
 * The default plane is a Vector of (0.0, 0.0, 0.0) and a distance of 0.0.
 * Operations on this default may lead to Exceptions such as ZeroLengthvector.
 *
 * @author (C) P. Murray-Rust, 1996
 */

public class Plane3 implements EuclidConstants {
    final static Logger LOG = Logger.getLogger(Plane3.class);

    /**
     * vector of plane (normalised)
     */
    protected Vector3 vect;

    /**
     * distance of plane from origin
     */
    protected double dist;

    /**
     * status (OK or not)
     */
    /**
     * 4-component array representing contents of vector and distance
     */
    double[] array = new double[4];

    /**
     * default constructor. uses default vector3
     */
    public Plane3() {
        vect = new Vector3();
        dist = 0.0;
    }

    /**
     * formed from components. vector is normalised
     *
     * @param l
     *            component
     * @param m
     *            component
     * @param n
     *            component
     * @param d
     *            distance
     * @throws EuclidRuntimeException
     */
    public Plane3(double l, double m, double n, double d)
            throws EuclidRuntimeException {
        vect = new Vector3(l, m, n);
        if (vect.isZero()) {
            throw new EuclidRuntimeException("zero length normal");
        }
        dist = d;
        // normalise vector
        vect.normalize();
    }

    /**
     * formed from components. vector is normalised
     *
     * @param lmn
     *            component
     * @param d
     *            distance
     * @throws EuclidRuntimeException
     */
    public Plane3(double[] lmn, double d) throws EuclidRuntimeException {
        Util.check(lmn, 3);
        vect = new Vector3(lmn);
        dist = d;
        // normalise vector
        vect.normalize();
    }

    /**
     * construct from array.
     *
     * @param array
     *            4-components
     * @throws EuclidRuntimeException
     */
    public Plane3(double[] array) throws EuclidRuntimeException {
        Util.check(array, 4);
        vect = new Vector3();
        System.arraycopy(array, 0, vect.flarray, 0, 3);
        dist = array[3];
        System.arraycopy(array, 0, this.array, 0, 4);
    }

    /**
     * formed from plane and distance. vector is copied and normalised
     *
     * @param v
     *            vector
     * @param d
     *            distance
     * @throws EuclidRuntimeException
     */
    public Plane3(Vector3 v, double d) throws EuclidRuntimeException {
        if (v.isZero()) {
            throw new EuclidRuntimeException("zero length normal");
        }
        vect = new Vector3(v);
        dist = d;
        vect.normalize();
    }

    /**
     * copy constructor:
     *
     * @param pl
     *            place
     */
    public Plane3(Plane3 pl) {
        vect = new Vector3(pl.vect);
        dist = pl.dist;
    }

    /**
     * make a plane from three points.
     *
     * @param p1
     *            point
     * @param p2
     *            point
     * @param p3
     *            point
     * @throws EuclidRuntimeException
     */
    public Plane3(Point3 p1, Point3 p2, Point3 p3) throws EuclidRuntimeException {
        vect = new Vector3();
        dist = 0.0;
        vect = (p2.subtract(p1)).cross(p3.subtract(p2));
        if (vect.isZero()) {
            throw new EuclidRuntimeException("zero length normal");
        }
        vect.normalize();
        Vector3 vp1 = new Vector3(p1);
        dist = vp1.dot(vect);
    }

    /**
     * make a plane from a line and a point not on the line.
     *
     * @param l
     *            point
     * @param p
     *            point
     * @throws EuclidRuntimeException
     */
    public Plane3(Line3 l, Point3 p) throws EuclidRuntimeException {
        // oKness dealt with by previous constructor
        this(l.getPoint(), (Point3) (l.getPoint().plus(l.getVector())), p);
    }

    /**
     * get return contents as an array.
     *
     * @return the array (l,m,n,d)
     */
    public double[] getArray() {
        System.arraycopy(vect.flarray, 0, array, 0, 3);
        array[3] = dist;
        return array;
    }

    /**
     * get vector.
     *
     * @return the vector
     */
    public Vector3 getVector() {
        return vect;
    }

    /**
     * get distance from origin.
     *
     * @return the distance
     */
    public double getDistance() {
        return dist;
    }

    /**
     * reverse direction of plane.
     */
    public void negative() {
        vect.negativeEquals();
    }

    /**
     * are two planes coincident and parallel.
     *
     * @param pl2
     *            plane to compare
     * @return true if equal within Real.isEqual()
     */
    public boolean isEqualTo(Plane3 pl2) {
        if (!vect.isEqualTo(pl2.vect))
            return false;
        return Real.isEqual(pl2.dist, dist);
    }

    /**
     * form coincident antiparallel plane.
     *
     * @return antiparallel plane
     */
    public Plane3 subtract() {
        Plane3 pl = this;
        pl.vect = pl.vect.negative();
        return pl;
    }

    /**
     * distance of point from plane. will be a signed quantity
     *
     * @param p
     *            the point
     * @return the distance
     */
    public double getDistanceFromPoint(Point3 p) {
        Vector3 v = new Vector3(p);
        return (v.dot(vect) - dist);
    }

    /**
     * are two planes parallel. not antiparallel
     *
     * @param pl2
     *            the plane
     * @return true if parallel within Real.isEqual()
     */
    public boolean isParallelTo(Plane3 pl2) {
        return vect.isIdenticalTo(pl2.vect);
    }

    /**
     * are two planes antiparallel. not parallel
     *
     * @param pl2
     *            the plane
     * @return true if antiparallel within Real.isEqual()
     */
    public boolean isAntiparallelTo(Plane3 pl2) {
        Vector3 v = new Vector3(pl2.vect);
        return vect.isIdenticalTo(v.negative());
    }

    /**
     * is a point on the plane.
     *
     * @param p
     *            the point
     * @return true if within Real.isEqual()
     */
    public boolean containsPoint(Point3 p) {
        return Real.isZero(this.getDistanceFromPoint(p), Real.getEpsilon());
    }

    /**
     * point on plane closest to another point. if p2 is on plane then result
     * will coincide
     *
     * @param p2
     *            other point
     * @return the closest point
     */
    public Point3 getClosestPointTo(Point3 p2) {
        Point3 p1 = new Point3();
        double d = getDistanceFromPoint(p2);
        Vector3 v = new Vector3(vect.multiplyBy(d));
        Vector3 vv = new Vector3(p2);
        p1 = new Point3(vv.subtract(v));
        return p1;
    }

    /**
     * point of intersection of plane and line.
     *
     * @param l
     *            line
     * @return intersection point (null if line parallel to plane)
     */
    public Point3 getIntersectionWith(Line3 l) {
        Point3 p = null;
        double lambda;
        Vector3 v = new Vector3(l.getPoint());
        Vector3 lvect = new Vector3(l.getVector());
        double numer = dist - vect.dot(v);
        double denom = vect.dot(lvect);
        // check for line and plane parallel
        if (!Real.isZero(denom, Real.getEpsilon())) {
            lambda = numer / denom;
            p = l.getPoint().plus(lvect.multiplyBy(lambda));
        }
        return p;
    }

    /**
     * get line as intersection of two planes.
     *
     * @param pl2
     *            plane
     * @return intersection line (null if parallel)
     */
    public Line3 getIntersectionWith(Plane3 pl2) {
        Vector3 v3 = vect.cross(pl2.vect);
        v3.normalize();
        // point on p1 nearest origin
        Point3 p = new Point3(vect.multiplyBy(dist));
        Vector3 v1a = vect.cross(v3);
        Line3 l1 = new Line3(p, v1a);
        Point3 p2 = pl2.getIntersectionWith(l1);

        // this should take care of null vector (that is parallel planes)
        return (p2 == null) ? null : new Line3(p2, v3);
    }

    /**
     * point where three planes intersect
     *
     * @param pl2
     *            plane
     * @param pl3
     *            plane
     * @return intersection point (null if any planes parallel)
     */
    public Point3 getIntersectionWith(Plane3 pl2, Plane3 pl3) {
        Point3 p = new Point3();
        Line3 l = pl2.getIntersectionWith(pl3);
        p = getIntersectionWith(l);
        return p;
    }

    /**
     * the angle between 2 planes.
     *
     * @param pl2
     *            plane
     * @return the angle (unsigned)
     */
    public Angle getAngleMadeWith(Plane3 pl2) {
        return this.getVector().getAngleMadeWith(pl2.getVector());
    }

    /**
     * string representation.
     *
     * @return the string
     */
    public String toString() {
        return EuclidConstants.S_LBRAK + vect + EuclidConstants.S_COMMA + dist + EuclidConstants.S_RBRAK;
    }

}
