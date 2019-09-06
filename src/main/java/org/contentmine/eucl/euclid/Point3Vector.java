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

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis3;
/**
 * a (Java) Vector of Point3s (Note that 'Vector' is used by Java to describe an
 * array of objects - there is no relationship to geometrical vectors in this
 * package.)
 * <P>
 * There are a large number of routines for manipulating 3-D coordinates.
 * Examples are distance, torsion, angle, planes, fitting, etc. The routines
 * have NOT been optimised. In the previous incarnation (C++) all coordinates
 * were handled by Point3s, etc because pointers are fragile. In Java, however,
 * a lot more intermediate information could be passed through double[] arrays
 * and this will be gradually converted.
 * <P>
 * All the routines compile and give answers. The answers were right in the C++
 * version. Most seem to be right here, but the FITTING ROUTINES ARE CURRENTLY
 * NOT WORKING PROPERLY. This will be fixed...
 * <P>
 * Default is an empty (Java) Vector;
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Point3Vector implements EuclidConstants {
    final static Logger LOG = Logger.getLogger(Point3Vector.class);
    protected List<Point3> vector;
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258126964282635312L;
    /**
     * constructor.
     */
    public Point3Vector() {
        vector = new ArrayList<Point3>();
    }
    /**
     * Formed by feeding in an existing array to a 3xn matrix flarray is in form
     * (x, y, z, x, y, z ...)
     * 
     * @param flarray
     * @exception EuclidException
     *                size of flarray must be multiple of 3
     */
    /**
     * constructor.
     * 
     * @param flarray
     * @exception EuclidRuntimeException
     */
    public Point3Vector(double[] flarray) throws EuclidRuntimeException {
        this();
        if (flarray == null) {
            throw new EuclidRuntimeException("null array");
        }
        int count = 0;
        int n = flarray.length / 3;
        if (flarray.length != 3 * n) {
            throw new EuclidRuntimeException("array length must be multiple of 3");
        }
        for (int i = 0; i < n; i++) {
            Point3 p = new Point3(flarray[count++], flarray[count++],
                    flarray[count++]);
            vector.add(p);
        }
    }
    /**
     * from three parallel arrays of x, y and z - by REFERENCE
     * 
     * @param n
     * @param x
     * @param y
     * @param z
     * @throws EuclidRuntimeException
     * 
     */
    public Point3Vector(int n, double[] x, double[] y, double[] z)
            throws EuclidRuntimeException {
        this();
        Util.check(x, n);
        Util.check(y, n);
        Util.check(z, n);
        for (int i = 0; i < n; i++) {
            vector.add(new Point3(x[i], y[i], z[i]));
        }
    }
    /**
     * constructor from RealArray - by REFERENCE
     * 
     * @param m
     * @exception EuclidRuntimeException
     *                size of flarray must be multiple of 3
     */
    public Point3Vector(RealArray m) throws EuclidRuntimeException {
        this();
        double[] marray = m.getArray();
        int count = marray.length / 3;
        if (marray == null || marray.length != count * 3) {
            throw new EuclidRuntimeException("null array or count not divisible by 3");
        }
        int j = 0;
        for (int i = 0; i < count; i++) {
            vector.add(new Point3(marray[j++], marray[j++], marray[j++]));
        }
    }
    /**
     * copy constructor from Point3Vector COPIES pv
     * 
     * @param pv
     */
    public Point3Vector(Point3Vector pv) {
        this();
        for (int i = 0; i < pv.size(); i++) {
            Point3 point = (Point3) pv.elementAt(i);
            if (point != null) {
            	point = new Point3(point);
            }
            this.addElement(point);
        }
    }
    /**
     * size of vector.
     * 
     * @return the size
     */
    public int size() {
        return vector.size();
    }
    /**
     * get points as list.
     * 
     * @return list
     */
    public List<Point3> getPoint3List() {
        return vector;
    }
    /**
     * is equal.
     * 
     * @param p
     * @return true if equals
     */
    public boolean isEqualTo(Point3Vector p) {
        boolean ok = true;
        if (p == null || this.size() != p.size()) {
            ok = false;
        } else {
            int i = 0;
            for (Point3 pp : p.vector) {
                Point3 thisP = this.vector.get(i++);
                if (!thisP.isEqualTo(pp)) {
                    ok = false;
                    break;
                }
            }
        }
        return ok;
    }
    /**
     * get array.
     * 
     * @return array
     */
    public double[] getArray() {
        double[] array = new double[3 * vector.size()];
        int i = 0;
        for (Point3 p : vector) {
            array[i++] = p.flarray[0];
            array[i++] = p.flarray[1];
            array[i++] = p.flarray[2];
        }
        return array;
    }
    /**
     * add point.
     * 
     * @param p
     *            the point
     */
    public void add(Point3 p) {
        vector.add(p);
    }
    /**
     * insert point.
     * 
     * @param p
     *            point to insert
     * @param i
     *            position to insert at
     */
    public void setElementAt(Point3 p, int i) {
        vector.set(i, p);
    }
    /**
     * get Point3 element.
     * 
     * @param i
     *            serial of the element to get
     * @return the element
     */
    public Point3 elementAt(int i) {
        return vector.get(i);
    }
    /**
     * get Point3 element.
     * 
     * @param i
     *            serial of the element to get
     * @return the element
     */
    public Point3 get(int i) {
        return (Point3) vector.get(i);
    }
    private void checkConformable(Point3Vector v) throws EuclidRuntimeException {
        if (size() != v.size()) {
            throw new EuclidRuntimeException("incompatible Point3Vector sizes: "
                    + size() + EC.S_SLASH + v.size());
        }
    }
    /**
     * add point.
     * 
     * @param p
     */
    public void addElement(Point3 p) {
        vector.add(p);
    }
    /**
     * sets a given coordinate (i) to vector
     * 
     * @param v
     * @param i
     * @exception EuclidRuntimeException
     *                i is >= number of current points (cann use this to
     *                increase size of Point3Vector)
     */
    public void setElementAt(Vector3 v, int i) throws EuclidRuntimeException {
        vector.set(i, new Point3(v));
    }
    /**
     * get range of one coordinate
     * 
     * @param ax
     * @return range
     */
    public RealRange getRange(Axis3 ax) {
        RealArray temp = new RealArray(vector.size());
        double[] dd = temp.getArray();
        int i = 0;
        for (Point3 p : vector) {
            dd[i++] = p.getArray()[ax.value];
        }
        RealRange range = new RealRange();
        if (size() > 0) {
            range.add(temp.smallestElement());
            range.add(temp.largestElement());
        }
        return range;
    }
    /**
     * get range of all 3 coordinates
     * 
     * @return range
     */
    public Real3Range getRange3() {
        Axis3 axes[] = Axis3.values();
        Real3Range range = new Real3Range();
        for (Axis3 ax : axes) {
            range.add(ax, getRange(ax));
        }
        return range;
    }
    /**
     * gets sums of squared distances between points.
     * 
     * result = sigma[i = 1, npoints] (this.point(i)getDistanceFrom(vector.point(i)) **
     * 2
     * 
     * @param v
     *            the other vector
     * @throws EuclidRuntimeException
     *             vectors not of same length
     * @return the sums of squared distances
     */
    public double getSigmaDeltaSquared(Point3Vector v) throws EuclidRuntimeException {
        int np = size();
        if (v.size() != np) {
            throw new EuclidRuntimeException("Vectors of different lengths");
        }
        double d = 0.0;
        for (int i = 0; i < np; i++) {
            Point3 thisP = vector.get(i);
            Point3 vP = v.vector.get(i);
            double dist = thisP.getDistanceFromPoint(vP);
            d += dist * dist;
        }
        return d;
    }
    /**
     * create a NEW subset of the points; points are COPIED
     * 
     * @param is
     * @return vector
     * @exception EuclidRuntimeException
     *                an element of <TT>is</TT> is outside range of <TT>this</TT>
     */
    public Point3Vector subArray(IntSet is) throws EuclidRuntimeException {
        Point3Vector sub = new Point3Vector();
        for (int i = 0; i < is.size(); i++) {
            int ix = is.elementAt(i);
            if (ix < 0 || ix >= this.size()) {
                throw new EuclidRuntimeException("element out of range: "+ix);
            }
            sub.addElement(new Point3(this.getPoint3(ix)));
        }
        return sub;
    }
    
    /**
     * multiply all coords. does not alter this.
     * 
     * @param scale
     * @return the scaled vector
     */
    public Point3Vector multiplyBy(double scale) {
    	Point3Vector p3v = new Point3Vector(this);
    	p3v.multiplyByEquals(scale);
        return p3v;
    }
    
    /**
     * multiply all coords - alters this.
     * 
     * @param scale
     * @return the scaled vector
     */
    public void multiplyByEquals(double scale) {
    	for (int i = 0; i < this.size(); i++) {
    		this.getPoint3(i).multiplyEquals(scale);
    	}
    }
    /**
     * form the point+point sum for two vectors of points
     * 
     * @param pv2
     * @exception EuclidRuntimeException
     *                pv2 is different size from <TT>this</TT>
     * @return vector
     */
    public Point3Vector plus(Point3Vector pv2) throws EuclidRuntimeException {
        checkConformable(pv2);
        Point3Vector pv1 = new Point3Vector();
        for (int i = 0; i < this.size(); i++) {
            Point3 temp = (getPoint3(i)).plus(pv2.getPoint3(i));
            pv1.addElement(temp);
        }
        return pv1;
    }
    /**
     * form the point+point difference for two vectors of points
     * 
     * @param pv2
     * @return vector
     * @exception EuclidRuntimeException
     *                pv2 is different size from <TT>this</TT>
     */
    public Point3Vector subtract(Point3Vector pv2) throws EuclidRuntimeException {
        checkConformable(pv2);
        Point3Vector pv1 = new Point3Vector();
        for (int i = 0; i < size(); i++) {
            Vector3 v = (getPoint3(i)).subtract(pv2.getPoint3(i));
            Point3 temp = new Point3(v.getArray());
            pv1.addElement(temp);
        }
        return pv1;
    }
    /**
     * get the line between two points
     * 
     * @param i1
     * @param i2
     * @return line
     */
    public Line3 getLine(int i1, int i2) {
        Line3 temp = new Line3();
        if (i1 >= 0 && i1 < size() && i2 >= 0 && i2 < size()) {
            temp = new Line3(getPoint3(i1), getPoint3(i2));
        }
        return temp;
    }
    /**
     * get centroid of all points
     * 
     * @return point
     */
    synchronized public Point3 getCentroid() {
        final int size = size();
        if (size < 1) {
            return null;
        }
        Point3 p = new Point3();
        for (int j = size - 1; j >= 0; --j) {
            p = p.plus(getPoint3(j));
        }
        double scale = 1.0 / ((double) size);
        p = p.multiplyBy(scale);
        return p;
    }
    /**
     * translate by a vector - do NOT modify <TT>this</TT>
     * 
     * @param v
     * @return vector
     */
    public Point3Vector plus(Vector3 v) {
        Point3Vector temp = new Point3Vector();
        for (int i = 0; i < size(); i++) {
            Point3 p = getPoint3(i).plus(v);
            temp.addElement(p);
        }
        return temp;
    }
    /**
     * translate by a vector - modify <TT>this</TT>
     * 
     * @param v
     */
    public void plusEquals(Vector3 v) {
        for (int i = 0; i < size(); i++) {
            getPoint3(i).plusEquals(v);
        }
    }
    /**
     * translate negatively. does NOT modify this
     * 
     * @param v vector to subtract
     * @return the NEW translated p3v
     */
    public Point3Vector subtract(Vector3 v) {
        Vector3 v1 = v.negative();
        Point3Vector temp = this.plus(v1);
        return temp;
    }
    
    /**
     * centre molecule on origin. translate to centroid MODIFIES PV
     */
    public void moveToCentroid() {
        Point3 temp = this.getCentroid();
        temp = temp.multiplyBy(-1.);
        this.plusEquals(new Vector3(temp));
    }
    
    /** removes null values if both vectors have them at same position.
     * else throws exception
     * @param a
     * @param b
     */
    public static void removeNullValues(Point3Vector a, Point3Vector b) {
    	int n = a.size();
    	if (b.size() != n) {
    		throw new EuclidRuntimeException("vectors of different sizes");
    	}
    	for (int i = n-1; i >= 0; i--) {
    		Point3 pa = a.elementAt(i);
    		Point3 pb = b.elementAt(i);
    		if (pa != null && pb != null) {
    		} else if (pa == null && pb == null) {
    			a.vector.remove(i);
    			b.vector.remove(i);
    		} else {
    			throw new EuclidRuntimeException("unmatched null values at: "+i);
    		}
    	}
    }
    /**
     * get inertial tensor. (second moments)
     * 
     * @return the inertial tensor
     */
    public RealSquareMatrix calculateNonMassWeightedInertialTensorOld() {
        RealSquareMatrix tensor = new RealSquareMatrix(3);
        Point3 centre = this.getCentroid();
        // subtract centroid from each coord and sum outerproduct of result
        for (int i = 0; i < size(); i++) {
            Point3 temp = new Point3(this.getPoint3(i).subtract(centre));
            RealArray delta = new RealArray(3, temp.getArray());
            RealSquareMatrix op = RealSquareMatrix.outerProduct(delta);
            tensor = tensor.plus(op);
        }
        return tensor;
    }
    
    public RealSquareMatrix calculateRotationToInertialAxes() {
    	RealSquareMatrix inertialTensor = calculateNonMassWeightedInertialTensor();
    	RealSquareMatrix eigenvectors = inertialTensor.calculateEigenvectors();
    	if (eigenvectors != null) {
	    	// make sure axes are right-handed
			double determinant = eigenvectors.determinant();
			if (determinant < 0.1) {
				RealSquareMatrix flip = new RealSquareMatrix(
					new double[][] {
						new double[] {1.0,  0.0,  0.0},  
						new double[] {0.0, -1.0,  0.0},  
						new double[] {0.0,  0.0,  1.0},  
					}
					);
				eigenvectors = eigenvectors.multiply(flip);
			}
			eigenvectors = new RealSquareMatrix(eigenvectors.getTranspose());
    	}
    	return eigenvectors;
    }
    
    /**
     * transform all coordinates. MODIFIES p3Vector
     * 
     * @param t
     */
    public void transform(Transform3 t) {
        for (int i = 0; i < size(); i++) {
            Point3 p = new Point3(getPoint3(i));
            vector.set(i, p.transform(t));
        }
    }
    /**
     * transform subset of coordinates - MODIFIES Vector
     * 
     * @param t
     * @param is
     */
    public void transform(Transform3 t, IntSet is) {
        int nis = is.size();
        for (int j = 0; j < nis; j++) {
            int i = is.elementAt(j);
            if (i >= 0 && i < size()) {
                Point3 p = new Point3(getPoint3(i));
                vector.set(i, p.transform(t));
            }
        }
    }

    /**
     * get distance between 2 points.
     * 
     * @param i1
     *            index of first point
     * @param i2
     *            index of second point
     * @return distance
     */
    public double distance(int i1, int i2) {
        Vector3 v1 = getPoint3(i1).subtract(getPoint3(i2));
        return v1.getLength();
    }
    /**
     * get distance between 2 points.
     * 
     * @param is
     *            of exactly 2 integers
     * @exception EuclidRuntimeException
     *                <TT>is</TT> must have exactly 2 points
     * @return distance
     */
    public double distance(IntSet is) throws EuclidRuntimeException {
        if (is.size() != 2) {
            throw new EuclidRuntimeException("int set must have exactly 2 points");
        }
        return distance(is.elementAt(0), is.elementAt(1));
    }
    /**
     * get angle between 3 points.
     * 
     * @param i1
     *            serial of first point
     * @param i2
     *            serial of second point
     * @param i3
     *            serial of third point
     * @exception EuclidRuntimeException
     *                two points are coincident or identical
     * @return the angle
     */
    public Angle angle(int i1, int i2, int i3) throws EuclidRuntimeException {
        Angle a;
        a = Point3.getAngle(getPoint3(i1), getPoint3(i2), getPoint3(i3));
        return a;
    }
    /**
     * get angle between 3 points
     * 
     * @param is
     *            of exactly 3 integers
     * @exception EuclidRuntimeException
     *                <TT>is</TT> must have exactly 3 points
     * @exception EuclidRuntimeException
     *                two points are coincident or identical
     * @return the angle
     */
    public Angle angle(IntSet is) throws EuclidRuntimeException {
        if (is.size() != 3) {
            throw new EuclidRuntimeException("size must be 3");
        }
        return angle(is.elementAt(0), is.elementAt(1), is.elementAt(2));
    }
    /**
     * get torsion angle between 4 points.
     * 
     * @param i1
     *            serial of first point
     * @param i2
     *            serial of second point
     * @param i3
     *            serial of third point
     * @param i4
     *            serial of fourth point
     * @exception EuclidRuntimeException
     *                either 2 points are identical or coincident or 3
     *                successive points are colinear
     * @return the angle
     */
    public Angle torsion(int i1, int i2, int i3, int i4) throws EuclidRuntimeException {
        return Point3.getTorsion(getPoint3(i1), getPoint3(i2), getPoint3(i3),
                getPoint3(i4));
    }
    /**
     * get torsion angle between 4 points.
     * 
     * @param is
     *            of exactly 3 integers
     * @exception EuclidRuntimeException
     *                <TT>is</TT> must have exactly 4 points
     * @exception EuclidRuntimeException
     *                either 2 points are identical or coincident or 3
     *                successive points are colinear
     * @return the angle
     */
    public Angle torsion(IntSet is) throws EuclidRuntimeException {
        if (is.size() != 4) {
            throw new EuclidRuntimeException("size must be 4");
        }
        return torsion(is.elementAt(0), is.elementAt(1), is.elementAt(2), is
                .elementAt(3));
    }
    /**
     * distance matrix
     * 
     * @return matrix
     */
    public RealSquareMatrix getDistanceMatrix() {
        int size = this.size();
        RealSquareMatrix distances = new RealSquareMatrix(size);
        double zero = 0.0;
        double[][] distanceMatrix = distances.getMatrix();
        for (int i = 0; i < size; i++) {
            distanceMatrix[i][i] = zero;
            for (int j = i + 1; j < size; j++) {
                double distance = this.getPoint3(i).getDistanceFromPoint(
                        this.getPoint3(j));
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }
        return distances;
    }
    
    public RealSquareMatrix calculateNonMassWeightedInertialTensor() {
    	RealSquareMatrix rsm = new RealSquareMatrix(3);
    	for (int i = 0; i < this.size(); i++) {
    		Point3 p = this.get(i);
    		double x = p.getArray()[0];
    		double y = p.getArray()[1];
    		double z = p.getArray()[2];
        	rsm.flmat[0][0] += y*y + z*z;
        	rsm.flmat[1][1] += x*x + z*z;
        	rsm.flmat[2][2] += y*y + x*x;
        	rsm.flmat[0][1] += -x*y;
        	rsm.flmat[0][2] += -x*z;
        	rsm.flmat[1][2] += -y*z;
        	rsm.flmat[1][0] = rsm.flmat[0][1];
        	rsm.flmat[2][0] = rsm.flmat[0][2];
        	rsm.flmat[2][1] = rsm.flmat[1][2];
    	}
    	return rsm;
    }
    
    /**
     * get Inertial axes; do not throw exception for pathological cases, but
     * return it. Axes (lengths and unit vectors) are returned through the
     * arguments eigval and eigvect. OBSOLETE
     * NYI
     * @param eigval
     * @param eigvect
     * @param illCond
     * @exception EuclidRuntimeException
     *                must have at least 3 points
     *                @deprecated (doesn't work)
     */
    public void inertialAxes(RealArray eigval, RealSquareMatrix eigvect,
    		EuclidRuntimeException illCond) throws EuclidRuntimeException {
        RealArray val = new RealArray(3);
        RealSquareMatrix vect = new RealSquareMatrix(3);
        illCond = null;
        eigval.shallowCopy(val);
        eigvect.shallowCopy(vect);
    }
    /**
     * get best plane
     * 
     * @return plane
     * 
     * @exception EuclidRuntimeException
     *                must have at least 3 points
     *                @deprecated doesn't work
     */
    public Plane3 bestPlane() throws EuclidRuntimeException {
        RealSquareMatrix eigvect = new RealSquareMatrix(3);
        RealArray eigval = new RealArray(3);
        EuclidRuntimeException illCond = null;
        inertialAxes(eigval, eigvect, illCond);
        // smallest eigenvalue
        RealArray temp = eigvect.extractRowData(2);
        Vector3 v = new Vector3(temp);
        // construct plane
        double dist = v.dot(getCentroid().getArray());
        Plane3 p = new Plane3(v, dist);
        return p;
    }
    /**
     * get deviations of coordinates from plane
     * 
     * @param p
     * @return deviations
     * 
     */
    public RealArray deviationsFromPlane(Plane3 p) {
        double[] farray = new double[size()];
        for (int i = 0; i < size(); i++) {
            double dist = p.getVector().dot(getPoint3(i).getArray());
            dist = dist - p.getDistance();
            farray[i] = dist;
        }
        return new RealArray(farray);
    }
    /**
     * fit two coordinates of same length and alignment - quaternions
     */
    // Transform3 newfitTo(Point3Vector c) {
    // // Uses Quaternions (method developed by Alan L. MacKay);
    //	
    // // number of points must be the same
    // /*
    // if(size() != c.size()) {
    // Transform3 t;
    // return t;
    // }
    // Point3Vector c1 = c; // reference
    // Point3Vector pv2 = this; // moving molecule
    // c1.moveToCentroid();
    // pv2.moveToCentroid();
    // Point3Vector csum(count);
    // csum = c1 + pv2;
    // Point3Vector cdiff(count);
    // cdiff = c1 - pv2;
    // */
    //	
    // /** now set up the matrix elements*/
    // /*
    // double mat[] = {0.,0.,0.,0., 0.,0.,0.,0., 0.,0.,0.,0., 0.,0.,0.,0.,};
    // double[] psum = csum.flarray;
    // double[] pdiff = cdiff.flarray;
    // for (int i = 0; i < count; i++) {
    // double xp = *(psum);
    // double yp = *(psum + 1);
    // double zp = *(psum + 2);
    //	
    // double xm = *(pdiff);
    // double ym = *(pdiff + 1);
    // double zm = *(pdiff + 2);
    //	
    // mat[0] += xm*xm + ym*ym + zm*zm;
    // mat[1] += yp*zm - ym*zp;
    // mat[2] += xm*zp - xp*zm;
    // mat[3] += xp*ym - xm*yp;
    //	
    // mat[5] += yp*yp + zp*zp + xm*xm;
    // mat[6] += xm*ym - xp*yp;
    // mat[7] += xm*zm - xp*zp;
    //	
    // mat[10] += xp*xp + zp*zp + ym*ym;
    // mat[11] += ym*zm - yp*zp;
    //	
    // mat[15] += xp*xp + yp*yp + zm*zm;
    //	
    // psum++; pdiff++;
    // }
    // RealSquareMatrix s(4, mat);
    // // symmetrize matrix: upper to lower
    //	
    // s.copyUpperToLower();
    // // diagonalise
    // RealSquareMatrix evect(4);
    // RealArray eval(4);
    // s.diagonalise(eval, evect);
    // */
    //	
    // /*jacobi(mat,4,eval,evec,&nrot);*/
    //	
    // /*eigsrt(eval,evec,4);*/
    // /*
    // return evect;
    // */
    // return new Transform3();
    // }
    /**
     * get a single point by REFERENCE
     * 
     * @param i
     * @return point
     */
    public Point3 getPoint3(int i) {
        return vector.get(i);
    }
    /**
     * get the coordinate coordinate array as doubles x,y,z,x,y,z,
     * 
     * @return array
     */
    public RealArray getXYZ() {
        double[] f = new double[3 * size()];
        int count = 0;
        for (int i = 0; i < size(); i++) {
            double[] p = getPoint3(i).flarray;
            f[count++] = p[0];
            f[count++] = p[1];
            f[count++] = p[2];
        }
        return new RealArray(f);
    }
    /**
     * get a single coordinate value
     * 
     * @param i
     * @param j
     * @return coordinate
     */
    public double getCoordinate(int i, Axis3 j) {
        return getPoint3(i).flarray[j.value];
    }
    /**
     * get a single coordinate value; as above but not public
     */
    double getCoordinate(int i, int j) {
        return getPoint3(i).flarray[j];
    }
    /**
     * get a single coordinate array for example all x-coordinates
     * 
     * @param axis
     * @return array
     */
    public RealArray getXYZ(Axis3 axis) {
        double[] f = new double[size()];
        for (int i = 0; i < size(); i++) {
            f[i] = getPoint3(i).flarray[axis.value];
        }
        return new RealArray(f);
    }
    /**
     * rms between two molecules - per atom
     * 
     * @param c
     * @return rms
     */
    public double rms(Point3Vector c) {
        RealArray tt = getXYZ();
        RealArray cc = c.getXYZ();
        tt = tt.subtract(cc);
        return Math.sqrt(tt.innerProduct())
                / (new Double(size())).doubleValue();
    }
    /**
     * get point furthest from another. useful for alignments
     * 
     * @param p
     *            point to avoid (need not be in this)
     * @return the serial of the furthest point
     */
    public int getFurthestPointFrom(Point3 p) {
        double d = -0.1;
        int serial = -1;
        for (int i = 0; i < this.size(); i++) {
            Point3 pp = vector.get(i);
            double dd = p.getDistanceFromPoint(pp);
            if (dd > d) {
                d = dd;
                serial = i;
            }
        }
        return serial;
    }
    /**
     * get point making smallest angle with two others. useful for alignments
     * 
     * @param p1
     *            point to avoid (need not be in this)
     * @param p2
     *            point to avoid (need not be in this)
     * @return the serial of the point making smallest angle
     */
    public int getPointMakingSmallestAngle(Point3 p1, Point3 p2) {
        double a = 999.;
        int serial = -1;
        for (int i = 0; i < this.size(); i++) {
            Point3 pp = vector.get(i);
            if (pp.isEqualTo(p1) || pp.isEqualTo(p2)) {
                continue;
            }
            try {
                Angle ang = Point3.getAngle(p1, pp, p2);
                double aa = ang.getAngle();
                if (aa < a) {
                    a = aa;
                    serial = i;
                }
            } catch (Exception e) {
                ;
            }
        }
        return serial;
    }
    /**
     * fit two coordinates of same length and alignment. very approximate uses
     * get3SeparatedPoints() and then three find point1 furthest from centroid
     * find point2 furthest from point1 find point3 making smallest angle with
     * point1 and point2 m1 is moving molecule, m2 is fixed translate centroids
     * to match then rotate so that m1-point1 coincides with m2-point1 then
     * rotate so that m1-point2 coincides with m2-point2 This is rough, but will
     * be a good starting point for most molecules.
     * <P>
     * 
     * @param ref
     * @return transformation
     * @exception EuclidRuntimeException
     *                some unusual geometry (for example one coordinate set is
     *                linear, has coincident points, etc.)
     */
    public Transform3 alignUsing3Points(Point3Vector ref)
            throws EuclidRuntimeException {
        if (this.size() != ref.size()) {
            throw new EuclidRuntimeException("this and ref must be same size");
        }
        if (this.size() < 3) {
            throw new EuclidRuntimeException("Need at least 3 points");
        }
        int[] serial = get3SeparatedPoints();
        Point3Vector thisVector = new Point3Vector();
        Point3Vector refVector = new Point3Vector();
        for (int i = 0; i < serial.length; i++) {
            thisVector.add(this.getPoint3(serial[i]));
            refVector.add(ref.getPoint3(serial[i]));
        }
        return thisVector.align3PointVectors(refVector);
        /*--
         RealSquareMatrix unit = new RealSquareMatrix(3,
         new double[]{1., 0., 0., 0., 1., 0., 0., 0., 1.}
         );
         Point3Vector moving = new Point3Vector(this);
         Point3 c1 = moving.getCentroid();
         Point3 rc1 = ref.getCentroid();
         Vector3 v12 = rc1.subtract(c1);
         Transform3 tr = new Transform3(unit, v12);
         moving.transform(tr);
         int i1 = moving.getFurthestPointFrom(c1);
         Point3 p1 = (Point3) moving.elementAt(i1);
         int i2 = moving.getFurthestPointFrom(p1);
         Point3 p2 = (Point3) moving.elementAt(i2);
         Point3 r1 = (Point3) ref.elementAt(i1);
         Point3 r2 = (Point3) ref.elementAt(i2);
         Vector3 p12 = p1.subtract(c1);
         Vector3 r12 = r1.subtract(rc1);
         Transform3 rot = new Transform3(p12, r12);
         moving.transform(rot);
         p1 = (Point3) moving.elementAt(i1);
         p2 = (Point3) moving.elementAt(i2);
         tr = rot.concatenate(tr);
         p12 = p1.subtract(p2);
         int i3 = moving.getPointMakingSmallestAngle(p1, p2);
         Point3 p3 = (Point3) moving.elementAt(i3);
         Point3 r3 = (Point3) ref.elementAt(i3);
         Vector3 p13 = p3.subtract(c1);
         Vector3 r13 = r3.subtract(rc1);
         Vector3 px = p12.cross(p13);
         px = px.normalize();
         Vector3 rx = r12.cross(r13);
         rx = rx.normalize();
         Transform3 rotx = new Transform3(px, rx);
         tr = rotx.concatenate(tr);
         return tr;
         --*/
    }
    /**
     * get three widely separated points. useful for initial alignments p1 is
     * furthest from centroid p2 is furthest from p1 p3 is the point making
     * smallest value of p1-p3-p2
     * 
     * @return array with serial numbers of points
     * @exception EuclidRuntimeException
     *                this or ref do not have 3 points or one has colinear or
     *                coincident points
     */
    public int[] get3SeparatedPoints() throws EuclidRuntimeException {
        int[] serial = new int[3];
        Point3 c1 = this.getCentroid();
        serial[0] = this.getFurthestPointFrom(c1);
        Point3 p1 = this.getPoint3(serial[0]);
        serial[1] = this.getFurthestPointFrom(p1);
        Point3 p2 = this.getPoint3(serial[1]);
        if (p1.isEqualTo(p2)) {
            throw new EuclidRuntimeException("Cannot find 3 separated points");
        }
        serial[2] = this.getPointMakingSmallestAngle(p1, p2);
        Point3 p3 = this.getPoint3(serial[2]);
        if (p1.isEqualTo(p3) || p2.isEqualTo(p3)) {
            throw new EuclidRuntimeException("Cannot find 3 separated points");
        }
        if ((p3.subtract(p1)).cross(p3.subtract(p2)).isZero()) {
            throw new EuclidRuntimeException("Cannot find 3 non-colinear points");
        }
        return serial;
    }
    /**
     * fit two sets of three points. simple method. this and ref must each have
     * 3 points rotates p1-p2 vector in each to be parallel then rotates
     * p1-p2-p3 plane to be parallel This is rough, but will be a good starting
     * point for many systems, especially if get3SeparatedPoints is used
     * 
     * no translation this is not changed
     * 
     * @param ref
     *            reference points
     * @return transform such that ttansform * this = ref
     * @exception EuclidRuntimeException
     *                this or ref do not have 3 points or one has colinear or
     *                coincident points
     */
    public Transform3 align3PointVectors(Point3Vector ref)
            throws EuclidRuntimeException {
        if (this.size() != 3) {
            throw new EuclidRuntimeException("this requires 3 points");
        }
        if (ref.size() != 3) {
            throw new EuclidRuntimeException("ref requires 3 points");
        }
        RealSquareMatrix unit = new RealSquareMatrix(3, new double[] { 1., 0.,
                0., 0., 1., 0., 0., 0., 1. });
        Transform3 overallTransform = new Transform3(unit);
        // copy this
        Point3Vector moving = new Point3Vector(this);
        Point3 thisCentroid = moving.getCentroid();
        Point3 refCentroid = ref.getCentroid();
        Point3 p1 = moving.getPoint3(0);
        Point3 p2 = moving.getPoint3(1);
        Point3 r1 = ref.getPoint3(0);
        Vector3 p12 = p1.subtract(thisCentroid);
        Vector3 r12 = r1.subtract(refCentroid);
        // align p1-p2 vectors
        Transform3 rot = new Transform3(p12, r12);
        moving.transform(rot);
        p1 = moving.getPoint3(0);
        p2 = moving.getPoint3(1);
        overallTransform = rot.concatenate(overallTransform);
        p12 = p1.subtract(p2);
        Point3 p3 = moving.getPoint3(2);
        Point3 r3 = ref.getPoint3(2);
        Vector3 p13 = p3.subtract(thisCentroid);
        Vector3 r13 = r3.subtract(refCentroid);
        // get normals
        Vector3 px = p12.cross(p13);
        px = px.normalize();
        Vector3 rx = r12.cross(r13);
        rx = rx.normalize();
        // and align
        Transform3 rotx = new Transform3(px, rx);
        overallTransform = rotx.concatenate(overallTransform);
        return overallTransform;
    }
    /**
     * fit two coordinates of same length and alignment.
     * 
     * rough method .
     * fit 'this' to ref ('this' is moving molecule, ref is fixed)
     * take copies and refer each to their centroid as origin.
     * pick three points in ref which are well separated
     * Find the plane of these three and use the normal,
     * together with the vector to one of the points, to define two coordinate axes.
     * Calculate the third axis as right-handed orthogonal (check).
     * This gives a pure rotation matrix (Tref)
     * using the corresponding points in 'this' repeat to give Tthis
     * Tranformation matrix is then 
     * Tthis(T). Tref.
     * Defining the intercentroid vector as Tthis2ref = centRef - centThis
     * we have 
     * Tfinal = Tthis2Ref . Tthis(T) . Tref . (-Tthis2Ref)  
     *  This is rough, but will be a good starting point for many systems.
     * 
     * @param ref
     * @return transformation
     * @exception EuclidRuntimeException
     *                some unusual geometry (for example one coordinate set is
     *                linear, has coincident points, etc.)
     */
    public Transform3 roughAlign(Point3Vector ref) throws EuclidRuntimeException {
//        int[] points;
        int nn = ref.size();
        if (nn != size()) {
            throw new EuclidRuntimeException("arrays of different lengths: "+this.size()+"/"+nn);
        }
        if (nn < 3) {
            throw new EuclidRuntimeException("must have 3 points to align: "+this.size()+"/"+nn);
        }
        Point3 centThis = this.getCentroid();
        Point3 centRef = ref.getCentroid();
        
        Transform3 r = fit3Points(ref);

        return translateRotateRetranslate(centThis,
				centRef, r);
    }
	private Transform3 fit3Points(Point3Vector ref) {
		int[] points;
        Point3Vector pvThis = new Point3Vector(this);
		pvThis.moveToCentroid();
        Point3Vector pvRef = new Point3Vector(ref);
        pvRef.moveToCentroid();
        points = this.get3SeparatedPoints();
        Transform3 tThis = getTransformOfPlane(points, pvThis);
        Transform3 tRef = getTransformOfPlane(points, pvRef);
        tRef.transpose();
        Transform3 r = tRef.concatenate(tThis);
		return r;
	}
	private Transform3 translateRotateRetranslate(Point3 centThis,
			Point3 centRef, Transform3 rotate) {
		Vector3 this2Origv = new Vector3(centThis.multiplyBy(-1.0));
        Transform3 trans2Orig = new Transform3(this2Origv);
        Transform3 trans1 = rotate.concatenate(trans2Orig);
		Vector3 orig2Refv = new Vector3(centRef);
		Transform3 orig2Ref = new Transform3(orig2Refv);
		Transform3 finalT = orig2Ref.concatenate(trans1);
		return finalT;
	}
    
	private Transform3 getTransformOfPlane(int[] ii, Point3Vector pv) {
		Plane3 p = new Plane3(pv.getPoint3(ii[0]), pv.getPoint3(ii[1]), pv
                .getPoint3(ii[2]));
        // get reference point in each plane
        Vector3 v = new Vector3(pv.getPoint3(ii[0])).normalize();
        // and form axes:
        Vector3 w = p.getVector().cross(v).normalize();
        // form the two sets of axes
        Vector3 vv = v.cross(w);
        Transform3 t = new Transform3(vv, v, w);
		return t;
	}
	
    /**
     * fit two coordinates of same length and alignment
     * 
     * CURRENTLY NOT VALIDATED
     * 
     * @param ref
     * @return transformation
     * @exception EuclidRuntimeException
     *                some unusual geometry (for example one coordinate set is
     *                linear, has coincident points, etc.)
     */
    public Transform3 fitTo(Point3Vector ref) throws EuclidRuntimeException {
        // these should be set as parameters?
        double damp = 1.0;
        double converge = 0.0002;
        Point3 thisCent = this.getCentroid();
        Point3 refCent = ref.getCentroid();
        /**
         * make copies of each molecule and translate to centroid
         */
        Point3Vector thistmp = new Point3Vector(this);
        Point3Vector reftmp = new Point3Vector(ref);
        thistmp.moveToCentroid();
        reftmp.moveToCentroid();
        /**
         * roughly rotate moving molecule onto reference one
         */
        Transform3 t = thistmp.roughAlign(reftmp);
        thistmp.transform(t);
        
        RealArray shift = new RealArray(3);
        int NCYC = 20;
        for (int icyc = 0; icyc < NCYC; icyc++) {
            double maxshift = 0.0;
            /**
             * loop through x,y,z
             */
            for (int jax0 = 0; jax0 < 3; jax0++) {
                int jax1 = (jax0 + 1) % 3;
                int jax2 = (jax1 + 1) % 3;
                double rh = 0.0;
                double lh = 0.0;
                for (int ipt = 0; ipt < size(); ipt++) {
                    double refj1 = reftmp.getCoordinate(ipt, jax1);
                    double refj2 = reftmp.getCoordinate(ipt, jax2);
                    double movj1 = thistmp.getCoordinate(ipt, jax1);
                    double movj2 = thistmp.getCoordinate(ipt, jax2);
                    lh += refj1 * refj1 + refj2 * refj2;
                    rh += refj1 * (refj2 - movj2) - refj2 * (refj1 - movj1);
                }
                /**
                 * get shifts
                 */
                double sft = -(damp * (rh / lh));
                maxshift = Math.max(maxshift, Math.abs(sft));
                shift.setElementAt(jax0, sft);
            }
            /**
             * break out if converged
             */
            if (maxshift < converge) {
                break;
            } else if (maxshift < 0.1) {
                // not yet used
                damp = 1.0;
            } else if (maxshift > 0.1) {
                // not yet used
                damp = 1.0;
            }
            /**
             * make transformation matrix by rotations about 3 axes
             */
            Transform3 t1 = new Transform3(
        		new Angle(shift.elementAt(0)),
                new Angle(shift.elementAt(1)),
                new Angle(shift.elementAt(2)));
            thistmp.transform(t1);
            /**
             * concatenate transformations
             */
            t = new Transform3(t1.concatenate(t));
        }
        Transform3 tt = translateRotateRetranslate(thisCent,
				refCent, t);
        return tt;
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
            sb.append(get(i).toString());
            sb.append(S_NEWLINE);
        }
        sb.append(S_RBRAK);
        return sb.toString();
    }
    
    public RealArray extractRealArray() {
    	return new RealArray(getArray());
    }
	public Double innerProduct() {
		return extractRealArray().innerProduct();
	}
    
}
