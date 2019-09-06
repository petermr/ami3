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
/**
 * 3-dimensional line class
 * 
 * Line3 represents a 3-dimensional line It is one of a set of primitives which
 * can be combined to create and manipulate complex 3-dimensional objects. Lines
 * can be transformed with rotation matrices or rotation-translation matrices
 * (Transform3), can be calculated from other primitives or can be used to
 * generate other primitives.
 * 
 * 
 * A line is a vector which is located in space. It is described by a unit
 * vector (vector) and a point (p) on the line. Any point on the line can be used for
 * p, and p could change during the existence of a calculation without affecting
 * the integrity of the line, for example p = {1,1,1}, vector = {1,0,0} is the same
 * line as p = {2,1,1}, vector = {1,0,0}. However the absolute direction of vector IS
 * important, giving the line a direction.
 * 
 * Default is a default Point3 (0.0, 0.0, 0.0) and default Vector3 (0.0, 0.0,
 * 0.0). Operations on this line may lead to Exceptions such as
 * ZeroLengthVector.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Line3 implements EuclidConstants {
    /**
     * the (normalised) vector for the line
     */
    Vector3 vect = new Vector3();
    /**
     * any point on the line
     */
    Point3 point = new Point3();
    /**
     * contents as array
     */
    // double[] array = new double[6];
    /**
     * default consstructor. no vector and point set
     * 
     */
    public Line3() {
    }
    /**
     * construct from point and vector. the line will not necessarily retain the
     * exact point and the vector need not be normalized p and vector are copied
     * 
     * @param p
     *            a point on the line
     * @param v
     *            non-zero vector through the point
     */
    public Line3(Point3 p, Vector3 v) {
        vect = new Vector3(v);
        point = new Point3(p);
        if (!vect.isZero()) {
            // normalise vector
            vect.normalize();
        }
    }
    /**
     * construct from array. dangerous as it is easy to muddle point and vector
     * 
     * @param array
     *            of length 6. first 3 are VECTOR, next are POINT
     */
    /*--
     public Line3(double[] array) throws EuclidRuntimeException {
     Util.check(array, 6);
     vect = new Vector3();
     System.arraycopy(array, 0, vect.flarray, 0, 3);
     vect.normalize();
     point = new Point3();
     System.arraycopy(array, 3, point.flarray, 0, 3);
     System.arraycopy(array, 0, this.array, 0, 6);
     }
     --*/
    /**
     * construct a line from two Point3s. the line will not necessarily retain
     * the exact points
     * 
     * @param p1
     *            a point on the line
     * @param p2
     *            another point on the line
     */
    public Line3(Point3 p1, Point3 p2) {
        this(p1, p2.subtract(p1));
    }
    /**
     * copy constructor.
     * 
     * @param l
     *            Line3 to copy
     */
    public Line3(Line3 l) {
        vect = new Vector3(l.vect);
        point = new Point3(l.point);
    }
    /**
     * are two lines identical. must be coincident and parallel uses
     * vect.equals() and containsPoint
     * 
     * @param l2
     *            Line3 to compare
     * @return equals
     */
    public boolean isEqualTo(Line3 l2) {
        if (!vect.isEqualTo(l2.vect)) {
            return false;
        } else {
            return containsPoint(l2.point);
        }
    }
    /**
     * form coincident antiparallel line.
     * 
     * @return antiparallel line
     */
    public Line3 negative() {
        Line3 l = new Line3(point, vect.negative());
        return l;
    }
    /**
     * get return contents as an array.
     * 
     * @return the array (v0, v1, v2, p0, p1, p2)
     */
    /*--
     public double[] getArray() {
     System.arraycopy(vect.flarray, 0, array, 0, 3);
     System.arraycopy(point.flarray, 0, array, 3, 3);
     return array;
     }
     --*/
    /**
     * get vector from line.
     * 
     * @return the vector (need not be normalized)
     */
    public Vector3 getVector() {
        return vect;
    }
    /**
     * get point from line.
     * 
     * @return any point on line
     */
    public Point3 getPoint() {
        return point;
    }
    /**
     * get transformed line. does not alter this
     * 
     * @param t
     *            transform
     * @return transformed line
     */
    public Line3 transform(Transform3 t) {
        Line3 lout = new Line3();
        lout.point = point.transform(t);
        lout.vect = vect.transform(t);
        return lout;
    }
    /**
     * are two lines parallel. (not antiparallel) does not test coincidence
     * 
     * @param l2
     *            line to compare
     * @return true if parallel
     */
    public boolean isParallelTo(Line3 l2) {
        return vect.isIdenticalTo(l2.vect);
    }
    /**
     * are two lines antiparallel. (not parallel) does not test coincidence
     * 
     * @param l2
     *            line to compare
     * @return true if antiparallel
     */
    public boolean isAntiparallelTo(Line3 l2) {
        Vector3 v = new Vector3(l2.vect);
        return vect.isIdenticalTo(v.negative());
    }
    /**
     * is a point on a line. tests for Real.isZero() distance from line
     * 
     * @param p
     *            point
     * @return true if within Real.isZero()
     */
    public boolean containsPoint(Point3 p) {
        return Real.isZero(getDistanceFromPoint(p), Real.getEpsilon());
    }
    /**
     * point on line closest to another point.
     * 
     * @param p2
     *            reference point
     * @return point on line closest to p2
     */
    public Point3 getClosestPointTo(Point3 p2) {
        Point3 p1 = new Point3();
        Vector3 v2 = new Vector3(p2);
        Vector3 v1 = new Vector3(point);
        p1 = point.plus(vect.multiplyBy((v2.subtract(v1)).dot(vect)));
        return p1;
    }
    /**
     * distance of a point from a line
     * 
     * @param p
     *            reference point
     * @return distance from line
     */
    public double getDistanceFromPoint(Point3 p) {
        Point3 p0 = getClosestPointTo(p);
        Vector3 v = new Vector3(p.subtract(p0));
        return v.getLength();
    }
    /**
     * point of intersection of line and plane calls
     * Plane3.getIntersectionWith(Point3)
     * 
     * @param pl
     *            plane intersecting line
     * @return point (null if line parallel to plane)
     */
    public Point3 getIntersectionWith(Plane3 pl) {
        return pl.getIntersectionWith(this);
    }
    /**
     * get string representation.
     * 
     * @return string
     */
    public String toString() {
        return S_LBRAK + vect + S_COMMA + point + S_RBRAK;
    }
}
