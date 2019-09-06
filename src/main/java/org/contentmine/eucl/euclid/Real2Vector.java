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

import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.Axis.Axis2;
/**
 * a (Java) Vector of Real2s (Note that 'Vector' is used by Java to describe an
 * array of objects - there is no relationship to geometrical vectors in this
 * package.)
 * <P>
 * Support is also given for the two component arrays as RealArrays
 * <P>
 * Default is an empty (Java) Vector;
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Real2Vector implements EuclidConstants {
    List<Real2> vector;
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3834026952770990647L;
    /**
     * constructor.
     */
    public Real2Vector() {
        vector = new ArrayList<Real2>();
    }
    /**
     * Formed by feeding in an existing array to a 2xn matrix THE COLUMN IS THE
     * FASTEST MOVING INDEX, that is the matrix is filled as flarray(0,0,
     * flarray(0,1). Primarily for compatibility with other apps
     * 
     * @param flarray
     * @exception EuclidRuntimeException
     *                array must have even number of elements
     * 
     */
    public Real2Vector(double[] flarray) throws EuclidRuntimeException {
        this();
        int count = 0;
        int n = flarray.length / 2;
        if (flarray.length != 2 * n) {
            throw new EuclidRuntimeException("size must be multiple of 2");
        }
        for (int i = 0; i < n; i++) {
            Real2 p = new Real2(flarray[count++], flarray[count++]);
            vector.add(p);
        }
    }
    /**
     * from two parallel arrays of x, y - by REFERENCE.
     * 
     * @param n
     * @param x
     * @param y
     * @throws EuclidRuntimeException
     * 
     */
    public Real2Vector(int n, double[] x, double[] y) throws EuclidRuntimeException {
        this();
        Util.check(x, n);
        Util.check(y, n);
        for (int i = 0; i < n; i++) {
            vector.add(new Real2(x[i], y[i]));
        }
    }
    
    /** conversion routine.
     * 
     * @param r2a
     */
    public Real2Vector(Real2Array r2a) {
    	this(r2a.size(), r2a.getXArray().getArray(), r2a.getYArray().getArray());
    }
    /**
     * constructor from RealArray - by REFERENCE
     * 
     * @param m
     * @exception EuclidRuntimeException
     *                array must have even number of elements
     */
    public Real2Vector(RealArray m) throws EuclidRuntimeException {
        this();
        int count = m.size() / 2;
        if (m.size() != count * 2) {
            throw new EuclidRuntimeException("size must be multiple of 2");
        }
        double[] marray = m.getArray();
        int j = 0;
        for (int i = 0; i < count; i++) {
            vector.add(new Real2(marray[j++], marray[j++]));
        }
    }
    /**
     * copy constructor from Real2Vector COPIES pv
     * 
     * @param pv
     */
    public Real2Vector(Real2Vector pv) {
        this();
        for (int i = 0; i < pv.size(); i++) {
            vector.add(new Real2(pv.get(i)));
        }
    }
    /**
     * construct from list of Real2.
     * 
     * @param rList
     *            list
     */
    public Real2Vector(List<Real2> rList) {
        this();
        for (Real2 r : rList) {
            add(r);
        }
    }
    /**
     * appens a Real2.
     * 
     * @param p
     *            to append
     */
    public void add(Real2 p) {
        vector.add(p);
    }
    /**
     * gets Real2 element.
     * 
     * @param i
     *            the position
     * @return the Real2 or null if outside range
     */
    public Real2 get(int i) {
        return (Real2) vector.get(i);
    }
    /**
     * @param i
     * @param v
     * @exception EuclidRuntimeException
     *                vector does not have an i'th element
     */
    public void set(int i, Real2 v) throws EuclidRuntimeException {
        vector.set(i, v);
    }
    /**
     * size of vector.
     * 
     * @return the size
     */
    public int size() {
        return vector.size();
    }

    /** access the vector.
     * this is the only storage so should be safe
     * @return vector
     */
    public List<Real2> getVector() {
    	return vector;
    }
    /**
     * get range of one coordinate
     * 
     * @param ax
     * @return range
     */
    public RealRange getRange(Axis2 ax) {
        RealArray temp = new RealArray(vector.size());
        double[] dd = temp.getArray();
        int i = 0;
        for (Real2 p : vector) {
            dd[i++] = (ax.equals(Axis2.X)) ? p.getX() : p.getY();
        }
        RealRange range = new RealRange();
        if (size() > 0) {
            range.add(temp.smallestElement());
            range.add(temp.largestElement());
        }
        return range;
    }
    /**
     * get range of both coordinates
     * 
     * @return range
     */
    public Real2Range getRange2() {
        Axis2 axes[] = Axis2.values();
        Real2Range range2 = new Real2Range();
        for (Axis2 ax : axes) {
            RealRange range = getRange(ax);
            range2.add(ax, range);
        }
        return range2;
    }
    /**
     * create a NEW subset of the points; points are COPIED
     * 
     * @param is
     * @return sub array
     * @exception EuclidRuntimeException
     *                an element of is is out of range of <TT>this</TT>
     */
    public Real2Vector subArray(IntSet is) throws EuclidRuntimeException {
        Real2Vector sub = new Real2Vector();
        for (int i = 0; i < is.size(); i++) {
            int ix = is.elementAt(i);
            if (ix < 0 || ix >= vector.size()) {
                throw new EuclidRuntimeException("index out of range " + ix);
            }
            sub.add(new Real2(this.getReal2(ix)));
        }
        return sub;
    }
    /**
     * create a subset of the points within a box
     * 
     * @param r
     * @return sub set
     */
    public IntSet subSet(Real2Range r) {
        IntSet is = new IntSet();
        for (int i = 0; i < size(); i++) {
            Real2 point = (Real2) vector.get(i);
            if (r.includes(point)) {
                is.addElement(i);
            }
        }
        return is;
    }
    /**
     * get the closest point (both ranges are assumed to have the same scales
     * 
     * @param p
     * @return index of point
     */
    public int getClosestPoint(Real2 p) {
        double dist2 = Double.POSITIVE_INFINITY;
        int ipoint = -1;
        for (int i = 0; i < size(); i++) {
            Real2 point = this.get(i);
            double dx = p.x - point.x;
            double dy = p.y - point.y;
            double d2 = dx * dx + dy * dy;
            if (d2 < dist2) {
                dist2 = d2;
                ipoint = i;
            }
        }
        return ipoint;
    }
    /**
     * get the index of the first point within a box centered on p (that is p+-
     * width/2, height/2) or -1 if none
     * 
     * @param p
     * @param width
     * @param height
     * @return index of point
     * 
     */
    public int getPoint(Real2 p, double width, double height) {
        double hwidth = width / 2.0;
        double hheight = height / 2.0;
        for (int i = 0; i < size(); i++) {
            Real2 point = this.get(i);
            if (Math.abs(p.x - point.x) <= hwidth
                    && Math.abs(p.y - point.y) <= hheight)
                return i;
        }
        return -1;
    }
    /**
     * translate by a vector, synonym for plus. MODIFIES 'this'
     * 
     * @param v
     */
    public void translateBy(Real2 v) {
        for (int i = 0; i < size(); i++) {
            Real2 p = getReal2(i).plus(v);
            vector.set(i, p);
        }
    }
    /**
     * add a Real2 to all elements of this. MODIFIES 'this'
     * 
     * @param p
     */
    public void plus(Real2 p) {
        this.translateBy(p);
    }
    /**
     * translate negatively; MODIFIES 'this'
     * 
     * @param v
     */
    public void subtract(Real2 v) {
        Real2 v1 = new Real2(v);
        v1.negative();
        this.plus(v1);
    }
    /**
     * multiply all coordinates be a given scalar (that is expands scale)
     * 
     * @param f
     */
    public void multiplyBy(double f) {
        for (int i = 0; i < size(); i++) {
            Real2 p = getReal2(i).multiplyBy(f);
            vector.set(i, p);
        }
    }
    /**
     * get distance between 2 points
     * 
     * @param i1
     * @param i2
     * @return distance
     */
    public double distance(int i1, int i2) {
        Real2 v1 = getReal2(i1).subtract(getReal2(i2));
        return v1.getLength();
    }
    /**
     * get distance between 2 points
     * 
     * @param is
     * @return distance
     * @exception EuclidRuntimeException
     *                a value in IntSet is not in the range 0 ... nelem-1
     */
    public double distance(IntSet is) throws EuclidRuntimeException {
        if (is.size() != 2) {
            throw new EuclidRuntimeException("index must be multiple of 2");
        }
        return distance(is.elementAt(0), is.elementAt(1));
    }
    /**
     * get angle between 3 points
     * 
     * @param i1
     * @param i2
     * @param i3
     * @return angle
     * @exception EuclidRuntimeException
     *                two points are coincident
     */
    public Angle angle(int i1, int i2, int i3) throws EuclidRuntimeException {
        return Real2.getAngle(getReal2(i1), getReal2(i2), getReal2(i3));
    }
    /**
     * get angle between 3 points
     * 
     * @param is
     * @return angle
     * @exception EuclidRuntimeException
     *                a value in IntSet is not in the range 0 ... nelem-1
     */
    public Angle angle(IntSet is) throws EuclidRuntimeException {
        if (is.size() != 3) {
            throw new EuclidRuntimeException("index must be multiple of 3");
        }
        return angle(is.elementAt(0), is.elementAt(1), is.elementAt(2));
    }
    /**
     * get the i'th Real2
     * 
     * @param i
     * @return element
     */
    public Real2 getReal2(int i) {
        return (Real2) get(i);
    }
    /** get List of Real2s.
     * 
     * @return the list
     */
    public List<Real2> getReal2List() {
        return vector;
    }
    /**
     * get the coordinate coordinate array as doubles x,y,x,y,
     * 
     * @return array
     */
    public RealArray getXY() {
        double[] f = new double[2 * size()];
        int count = 0;
        for (int i = 0; i < size(); i++) {
            Real2 p = getReal2(i);
            f[count++] = p.getX();
            f[count++] = p.getY();
        }
        return new RealArray(f);
    }
    
    /**
     * get the X coordinate array
     * 
     * @return array
     */
    public RealArray getXArray() {
        double[] f = new double[size()];
        int count = 0;
        for (int i = 0; i < size(); i++) {
            Real2 p = getReal2(i);
            f[count++] = p.getX();
        }
        return new RealArray(f);
    }
    /**
     * get the Y coordinate array
     * 
     * @return array
     */
    public RealArray getYArray() {
        double[] f = new double[size()];
        int count = 0;
        for (int i = 0; i < size(); i++) {
            Real2 p = getReal2(i);
            f[count++] = p.getY();
        }
        return new RealArray(f);
    }
    
    /** convenience
     * 
     * @return real2Array
     */
    public Real2Array getReal2Array() {
    	return new Real2Array(getXArray(), getYArray());
    }
    /**
     * get a single coordinate value
     * 
     * @param i
     * @param j
     * @return coord
     */
    public double getCoordinate(int i, Axis2 j) {
        return (j.value == 0) ? getReal2(i).getX() : getReal2(i).getY();
    }
    /**
     * get a single coordinate array - for example all x-coordinates
     * 
     * @param axis
     * @return array
     */
    public RealArray getXorY(Axis2 axis) {
        double[] f = new double[size()];
        for (int i = 0; i < size(); i++) {
            f[i] = (axis.value == 0) ? getReal2(i).getX() : getReal2(i).getY();
        }
        return new RealArray(f);
    }
    /**
     * swap all X and Y coordinates; MODIFIES array
     */
    public void swapXY() {
        for (int i = 0; i < size(); i++) {
            Real2 temp = (Real2) vector.get(i);
            temp.swap();
            vector.set(i, temp);
        }
    }
    /**
     * sort ARRAY on X or Y coordinate; returns new array
     * 
     * @param ax
     * @return vector
     */
    public Real2Vector sortAscending(Axis2 ax) {
        IntSet is = getXorY(ax).indexSortAscending();
        Real2Vector temp = new Real2Vector();
        for (int i = 0; i < size(); i++) {
            temp.add(this.get(is.elementAt(i)));
        }
        return temp;
    }
    /**
     * sort ARRAY on X or Y coordinate; returns new array
     * 
     * @param ax
     * @return vector
     * 
     */
    public Real2Vector sortDescending(Axis2 ax) {
        IntSet is = getXorY(ax).indexSortDescending();
        Real2Vector temp = new Real2Vector();
        for (int i = 0; i < size(); i++) {
            temp.add(this.get(is.elementAt(i)));
        }
        return temp;
    }
    /**
     * transforms 'this' by rotation-translation matrix; MODIFIES 'this'
     * 
     * @param t
     */
    public void transformBy(Transform2 t) {
        for (int i = 0; i < size(); i++) {
            Real2 point = (Real2) vector.get(i);
            point.transformBy(t);
            vector.set(i, point);
        }
    }
    /**
     * squared difference between corresponding points in 2 real2Vectors. must
     * be of same length else returns Double.NaN
     * 
     * @param r2v
     *            vector to compare
     * @return sum ((x1-x2)**2 + (y1-y2)**2)
     */
    public double getSquaredDifference(Real2Vector r2v) {
        double sum = Double.NaN;
        if (r2v.size() == 0 || vector.size() == 0
                || r2v.size() != vector.size()) {
        } else {
            sum = 0.0;
            for (int i = 0; i < size(); i++) {
                Real2 xy1 = this.get(i);
                Real2 xy2 = r2v.get(i);
                double d2 = xy1.getSquaredDistance(xy2);
                sum += d2;
            }
        }
        return sum;
    }
    /**
     * gets array of squared distances between corresponding points.
     * 
     * array of d(this(i)-r2v(i))^2; returns null if Real2Vectors are different
     * lengths
     * 
     * @param r2v
     *            other vector
     * @return array of squares distances else null
     */
    public double[] getSquaredDistances(Real2Vector r2v) {
        double dist2[] = null;
        if (r2v.size() == vector.size()) {
            dist2 = new double[vector.size()];
            for (int i = 0; i < size(); i++) {
                Real2 xy1 = this.get(i);
                Real2 xy2 = r2v.get(i);
                dist2[i] = xy1.getSquaredDistance(xy2);
            }
        }
        return dist2;
    }
    /**
     * rotate about centroid by given angle; MODIFIES 'this'
     * 
     * @param a
     */
    public void rotateAboutCentroid(Angle a) {
        Real2 temp = this.getCentroid();
        Transform2 t2 = new Transform2(a);
        t2 = new Transform2(t2, temp);
        this.transformBy(t2);
    }
    
    /** creates mirror image
     * changes sign of x-coords
     */
    public void flipX() {
    	for (Real2 r2 : vector) {
    		r2.setX(-r2.getX());
    	}
    }
    
    /** creates mirror image
     * changes sign of y-coords
     */
    public void flipY() {
    	for (Real2 r2 : vector) {
    		r2.setY(-r2.getY());
    	}
    }
    
    /**
     * create regular polygon. 
     * centre is at 0.0, first point at 
     * (rad*cos(angle+dangle), rad*sin(angle+dangle))
     * points go anticlockwise 
     * 
     * @param nsides
     * @param rad the radius
     * @param angle offset to vertical (radians) 
     *     (point 0 is at rad*cos(angle), rad*sin(angle))
     * @return array of points
     */
    public static Real2Vector regularPolygon(int nsides, double rad, double angle) {
        Real2Vector temp = new Real2Vector();
        double dangle = 2 * Math.PI / (new Integer(nsides).doubleValue());
        for (int i = 0; i < nsides; i++) {
            Real2 p = new Real2(rad * Math.sin(angle), rad * Math.cos(angle));
            temp.add(p);
            angle += dangle;
        }
        return temp;
    }

    /**
     * create part of regular polygon. 
     * centre is at 0.0, last point at (rad, 0)
     * points go anticlockwise 
     * produces points pointn, pointn+1, ... nsides-1, 0
     * if npoint is 1 we get full polygon
     * if npoint >= nsides returns null
     * @param nsides
     * @param pointn
     * @param dist0n between point0 and npoint
     * @return array of points
     */
    public static Real2Vector partOfRegularPolygon(int nsides, int pointn, double dist0n) {
        Real2Vector temp = new Real2Vector();
        double dangle = 2 * Math.PI / (new Integer(nsides).doubleValue());
        double sinAngle2 = Math.sin((double) pointn * dangle / 2.);
    	double rad = 0.5 * dist0n / sinAngle2;
    	double angle = dangle;
        for (int i = pointn; i <= nsides; i++) {
            Real2 p = new Real2(rad * Math.sin(angle), rad * Math.cos(angle));
            temp.add(p);
            angle += dangle;
        }
        return temp;
    }
    
    
    /**
     * create regular polygon. 
     * centre is at 0.0, first point at (0, rad)
     * points go anticlockwise (rad*cos, -rad*sin)
     * 
     * @param nsides
     * @param rad the radius
     * @return array of points
     */
    public static Real2Vector regularPolygon(int nsides, double rad) {
    	return regularPolygon(nsides, rad, 0.0);
    }
    
    /** create regular polygon. 
     * goes through points p1, p2
     * if m = (p1 +p2)/2
     * and c is centre of circle
     * and pp = p2 - p1
     * and d = c-m
     * then signed angle(pp, d) is 90
     * 
     * points go clockwise
     * to make the polygon go the other way reverse p1 and p2
     * @param nsides
     * @param p1 first point
     * @param p2 second point
     * @param flip use mirror image
     * @return array of points
     */
    public static Real2Vector regularPolygon(int nsides, Real2 p1, Real2 p2, boolean flip) {
    	double halfAngle = Math.PI / (double) nsides;
        Vector2 p1top2 = new Vector2(p2.subtract(p1));
        double halfSidelength = p1top2.getLength() / 2.0;
        double rad = halfSidelength / Math.sin(halfAngle);
        Real2Vector temp = regularPolygon(nsides, rad);
        if (flip) {
        	temp.flipY();
        }
        // deviation between p1p2 and the y-axis
        Angle rot = null;
        rot = p1top2.getAngleMadeWith(new Vector2(0.0, 1.0));
        rot = new Vector2(1.0, 0.0).getAngleMadeWith(p1top2);
        temp.transformBy(new Transform2(rot));
        temp.transformBy(new Transform2(new Angle(-halfAngle)));
        Real2 shift = p1.subtract(new Vector2(temp.get(0)));
        temp.transformBy(new Transform2(new Vector2(shift)));
        return temp;
    }
    
    /**
     * get centroid of all points. does not modify this
     * 
     * @return the centroid
     */
    public Real2 getCentroid() {
        if (vector.size() < 1)
            return null;
        Real2 p = new Real2();
        for (int j = 0; j < vector.size(); j++) {
            Real2 pp = (Real2) vector.get(j);
            p = p.plus(pp);
        }
        double scale = 1.0 / (new Double(vector.size()).doubleValue());
        p = p.multiplyBy(scale);
        return p;
    }
    /**
     * get serialNumber of nearest point.
     * 
     * @param point
     * @return serial of point
     */
    public int getSerialOfNearestPoint(Real2 point) {
        double dist = Double.MAX_VALUE;
        int serial = -1;
        for (int j = 0; j < vector.size(); j++) {
            double d = this.get(j).subtract(point).getLength();
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
     * @param coords2
     * @return distance matrix
     */
    public RealMatrix getDistanceMatrix(List<Real2> coords2) {
        int size = vector.size();
        int size2 = coords2.size();
        RealMatrix distMatrix = new RealMatrix(size, size2);
        double[][] mat = distMatrix.getMatrix();
        for (int i = 0; i < size; i++) {
            Real2 ri = this.get(i);
            for (int j = 0; j < size2; j++) {
                Real2 rj = coords2.get(j);
                double dij = ri.getDistance(rj);
                mat[i][j] = dij;
            }
        }
        return distMatrix;
    }

    /**
     * if real2Vector is treated as a polygon, determines whether point
     * is inside it
    //  The function will return true if the point x,y is inside the polygon, or
    //  false if it is not.  If the point is exactly on the edge of the polygon,
    //  then the function may return YES or NO.
     */
    public boolean encloses(Real2 point) {
    	int nvect = vector.size();
    	int j = nvect - 1;
    	boolean  oddNodes = false;
    	double x = point.getX();
    	double y = point.getY();
        for (int i=0; i < nvect; i++) {
        	double xi = vector.get(i).getX();
        	double yi = vector.get(i).getY();
        	double xj = vector.get(j).getX();
        	double yj = vector.get(j).getY();
            if (yi < y && yj >= y ||  yj < y && yi >= y) {
                if (xi + (y - yi) / (yj - yi) * (xj - xi) < x) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes; 
    }


    /**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(S_LBRAK);
        for (int i = 0; i < size(); i++) {
            sb.append(getReal2(i));
            if (i < (size() - 1)) {
                sb.append(S_NEWLINE);
            }
        }
        sb.append(S_RBRAK);
        return sb.toString();
    }
}
