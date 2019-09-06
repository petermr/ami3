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
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis3;
/**
 * 3-D transformation matrix class Transform3 represents a transformation matrix
 * for 3-D objects. Its actual form may be implementation-dependent, but we have
 * started with 4x4 matrices. The following transformations will be supported as
 * the class is developed:
 * <P>
 * <BR>
 * TRFORM3_NULL no transformation allowed <BR>
 * ROT_ORIG rotation about the origin <BR>
 * ROT_TRANS rotation and translation <BR>
 * ROT_TRANS_SCALE rotation, translation and single scale factor <BR>
 * ROT_TRANS_AXIAL_SCALE rotation, translation + 3 axial scale factors <BR>
 * ROT_TRANS_SCALE_PERSP rotation, translation, scale, perspective <BR>
 * TRFORM3_ANY any matrix at all - user beware!
 * <P>
 * The basic stuff is all there - the user will do best to look at examples.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Transform3 extends RealSquareMatrix {
    private static final PrintStream SYSOUT = System.out;
	final static Logger LOG = Logger.getLogger(Transform3.class);
    /** type */
    public enum Type {
        /** */
        NULL(1, "none"),
        /** */
        ROT_ORIG(2, "rotation about origin"),
        /** */
        ROT_TRANS(3, "rotation translation"),
        /** */
        ROT_TRANS_SCALE(4, "rotation translation scale"),
        /** */
        ROT_TRANS_AXIAL_SCALE(5, "rotation translation axial scale"),
        /** */
        ROT_TRANS_SCALE_PERSP(6, "perspective"),
        /** */
        ANY(7, "any");
        /** integer value */
        public int i;
        /** String value */
        public String s;
        private Type(int i, String s) {
            this.i = i;
            this.s = s;
        }
    };
    /**
     * Transform3 inherits all public/protected members of RealSquareMatrix and
     * its ancestors
     */
    /**
     * type of transformation ( see above)
     */
    Type trnsfrm = Type.ANY;
    /**
     * construct unit matrix T = I
     */
    public Transform3() {
        super(4);
        createIdentityTransform();
    }
    
	private void createIdentityTransform() {
		for (int i = 0; i < 4; i++) {
            flmat[i][i] = 1.0;
        }
	}
    /**
     * This gives a default unit matrix of type t.
     * 
     * @param t type
     */
    public Transform3(Type t) {
        this();
        trnsfrm = t;
    }
    /**
     * identity matrix with translation component. T = I|vector
     * 
     * @param v translation vector
     */
    public Transform3(Vector3 v) {
        this();
        trnsfrm = Type.ROT_TRANS;
        for (int i = 0; i < 3; i++) {
            flmat[i][3] = v.flarray[i];
        }
    }
    /**
     * from rotation about an axis. (use (Choice3.X), etc
     * 
     * @param axis Choice.X, etc
     * @param rot angle to rotate by
     */
    public Transform3(Axis3 axis, Angle rot) {
        this();
        trnsfrm = Type.ROT_ORIG;
        // unit matrix
        RealSquareMatrix t1 = new RealSquareMatrix(4);
        double cosx = rot.cos();
        double sinx = rot.sin();
        // X-axis
        if (axis == Axis3.X) {
            t1.flmat[0][0] = 1.0;
            t1.flmat[1][1] = cosx;
            t1.flmat[1][2] = sinx;
            t1.flmat[2][1] = -sinx;
            t1.flmat[2][2] = cosx;
            t1.flmat[3][3] = 1.0;
        }
        // Y-axis
        else if (axis == Axis3.Y) {
            t1.flmat[0][0] = cosx;
            t1.flmat[0][2] = -sinx;
            t1.flmat[1][1] = 1.0;
            t1.flmat[2][0] = sinx;
            t1.flmat[2][2] = cosx;
            t1.flmat[3][3] = 1.0;
        }
        // Z-axis
        else if (axis == Axis3.Z) {
            t1.flmat[0][0] = cosx;
            t1.flmat[0][1] = sinx;
            t1.flmat[1][0] = -sinx;
            t1.flmat[1][1] = cosx;
            t1.flmat[2][2] = 1.0;
            t1.flmat[3][3] = 1.0;
        }
        this.flmat = t1.flmat;
        this.trnsfrm = Type.ROT_ORIG;
    }
    /**
     * from rotation about the three orthogonal axes. rotX then rotY then rotZ
     * 
     * @param xrot
     * @param yrot
     * @param zrot
     */
    public Transform3(Angle xrot, Angle yrot, Angle zrot) {
        super(4);
    	double cosx = 0.0;
    	double sinx = 0.0;
        trnsfrm = Type.ROT_ORIG;
        // X-axis
        int mat = 0;
        RealSquareMatrix t1 = new RealSquareMatrix(4);
        //must have non=zero matrix even for zero angle
        cosx = xrot.cos();
        sinx = xrot.sin();
        t1.flmat[0][0] = 1.0;
        t1.flmat[1][1] = cosx;
        t1.flmat[1][2] = sinx;
        t1.flmat[2][1] = -sinx;
        t1.flmat[2][2] = cosx;
        t1.flmat[3][3] = 1.0;
        mat = 1;
        // Y-axis
        if (!yrot.isEqualTo(0.0)) {
            cosx = yrot.cos();
            sinx = yrot.sin();
            // unit matrix
            RealSquareMatrix t2 = new RealSquareMatrix(4);
            t2.flmat[0][0] = cosx;
            t2.flmat[1][1] = 1.0;
            t2.flmat[0][2] = -sinx;
            t2.flmat[2][0] = sinx;
            t2.flmat[2][2] = cosx;
            t2.flmat[3][3] = 1.0;
            if (mat == 1) {
                t1 = t2.multiply(t1);
            } else {
                t1 = t2;
            }
            mat = 1;
        }
        // Z-axis
        if (!zrot.isEqualTo(0.0)) {
            cosx = zrot.cos();
            sinx = zrot.sin();
            // unit matrix
            RealSquareMatrix t2 = new RealSquareMatrix(4);
            t2.flmat[0][0] = cosx;
            t2.flmat[0][1] = sinx;
            t2.flmat[1][0] = -sinx;
            t2.flmat[1][1] = cosx;
            t2.flmat[2][2] = 1.0;
            t2.flmat[3][3] = 1.0;
            if (mat == 1) {
                t1 = t2.multiply(t1);
            } else {
                t1 = t2;
            }
        }
        this.flmat = t1.flmat;
        this.trnsfrm = Type.ROT_ORIG;
    }
    /**
     * from rotation about a point.
     * 
     * @param t
     *            rotation matrix
     * @param p
     *            point to rotate about
     */
    public Transform3(Transform3 t, Point3 p) {
        super(4);
        double f1[] = { 0.0, 0.0, 0.0, 1.0 };
        trnsfrm = Type.ROT_TRANS;
        Vector3 tvect = new Vector3(p);
        // translation only matrices
        Transform3 trans1 = new Transform3(tvect.negative());
        Transform3 trans2 = new Transform3(tvect);
        // remove existing translation
        RealArray f0 = new RealArray(f1);
        Transform3 temp = t;
        temp.replaceColumnData(3, f0);
        // concatenate
        Transform3 temp3 = trans2.concatenate(temp.concatenate(trans1));
        this.flmat = temp3.flmat;
        this.trnsfrm = temp3.trnsfrm;
    }
    /**
     * from rotation about a vector.
     * 
     * @param v
     *            vector to rotate about
     * @param a
     *            angle to rotate by
     */
    public Transform3(Vector3 v, Angle a) {
        super(4);
        trnsfrm = Type.ROT_ORIG;
        Vector3 v2 = v;
        v2.normalize();
        double cosa = a.cos();
        // make a diagonal with identical elements (cos(a))
        RealArray tempf = new RealArray(3, cosa);
        RealSquareMatrix m3 = RealSquareMatrix.diagonal(tempf);
        // outer product of axial components
        RealArray temp1 = new RealArray(v2.getArray());
        RealSquareMatrix m2 = RealSquareMatrix.outerProduct(temp1);
        m2.multiplyBy(1.0 - cosa);
            m2 = m2.plus(m3);
        // final matrix is (0, -v3, v2; v3, 0, -v1; -v2, v1, 0) * sin(a)
        // I expect the coding could be more elegant!
        double sina = a.sin();
        m3.clearMatrix();
        double f = sina * v2.flarray[2];
        m3.flmat[0][1] = -f;
        m3.flmat[1][0] = f;
        f = sina * v2.flarray[1];
        m3.flmat[0][2] = f;
        m3.flmat[2][0] = -f;
        f = sina * v2.flarray[0];
        m3.flmat[1][2] = -f;
        m3.flmat[2][1] = f;
        m2 = m2.plus(m3);
        // transfer to main matrix
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                flmat[i][j] = m2.flmat[i][j];
            }
        }
        flmat[3][3] = 1.0;
    }
    /**
     * Rotation about a line. chooses any point on line as centre of rotation
     * 
     * @param l
     *            line to rotate about
     * @param a
     *            angle to rotate by
     */
    public Transform3(Line3 l, Angle a) {
        super(4);
        trnsfrm = Type.ROT_TRANS;
        Vector3 v = l.getVector();
        Point3 p = l.getPoint();
        Transform3 temp = new Transform3(v, a);
        Vector3 orig = new Vector3(p);
        Transform3 trans1 = new Transform3(orig.negative()); // translate to
                                                                // origin
        orig = new Vector3(p);
        Transform3 trans2 = new Transform3(orig); // translate back
        Transform3 temp2 = new Transform3();
        temp2 = new Transform3(trans2.concatenate(temp.concatenate(trans1))); // concatenate
        // copy back to present one
        this.flmat = temp2.flmat;
    }
    /**
     * rotation of one vector onto another. this documentation has not been
     * checked
     * 
     * @param v1 vector to rotate
     * @param v2 vector to rotate v1 onto
     */
    public Transform3(Vector3 v1, Vector3 v2) {
        super(4);
    	Vector3 v12 = v1.cross(v2);
        // if parallel return unit matrix
    	
    	/*
    	 * 06/03/08 
    	 * I've changed the code so there is a difference between parallel and 
    	 * anti-parallel vectors. This is important for the polymer builder, but
    	 * may break other applications. Contact me if this is a problem - dmj30@cam
    	 */
    	
        if (v12.isZero()) {
        	double unit = 1.0;
        	if(v1.dot(v2) < 0) unit = -1.0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    flmat[i][j] = 0.0;
                }
                flmat[i][i] = unit;
            }
        } else {
            Angle a = v1.getAngleMadeWith(v2);
            Transform3 temp = new Transform3(v12, a);
            this.flmat = temp.flmat;
            this.trnsfrm = temp.trnsfrm;
        }
    }
    /**
     * from 3 vector components. NOT checked fills rows of T with v1, v2, v3
     * 
     * @param v1
     *            first row of T
     * @param v2
     *            second row of T
     * @param v3
     *            third row of T
     */
    public Transform3(Vector3 v1, Vector3 v2, Vector3 v3) {
        super(4);
        for (int i = 0; i < 3; i++) {
            flmat[0][i] = v1.flarray[i];
            flmat[1][i] = v2.flarray[i];
            flmat[2][i] = v3.flarray[i];
            flmat[3][i] = 0.0;
            flmat[i][3] = 0.0;
        }
        flmat[3][3] = 1.0;
        trnsfrm = Type.ROT_ORIG;
    }
    
    /**
     * apply scales to each axis
     * @param scaleX
     * @param scaleY
     * @param scaleZ
     * @return transform
     */
    public static Transform3 applyScales(double scaleX, double scaleY, double scaleZ) {
    	return new Transform3(
    		new double[] {
				scaleX, 0.0, 0.0, 0.0,
				0.0, scaleY, 0.0, 0.0,
				0.0, 0.0, scaleZ, 0.0,
				0.0, 0.0, 0.0, 1.0
		});
    }
    
    /**
     * apply scale to each axis
     * @param scale
     * @return transform
     */
    public static Transform3 applyScale(double scale) {
    	return Transform3.applyScales(scale, scale, scale);
    }
    
    /**
     * From array.
     * 
     * @param array
     *            copied to m00, m01, m02, m03, m10 ...
     * @exception EuclidRuntimeException
     *                array must have 16 elements
     */
    public Transform3(double[] array) throws EuclidRuntimeException {
        super(4, array);
        trnsfrm = checkMatrix();
    }
    /**
     * copy constructor.
     * 
     * @param m
     *            transform to copy
     */
    public Transform3(Transform3 m) {
        super(m);
        trnsfrm = m.trnsfrm;
    }
    /**
     * from a matrix.
     * 
     * @param m
     *            3x3 or 4x4 matrix
     * @exception EuclidRuntimeException
     *                m must be 3*3 or 4*4
     */
    public Transform3(RealSquareMatrix m) throws EuclidRuntimeException {
        this();
        // 3x3 matrix. convert to 4x4
        if (m.getCols() == 3) {
            // convert to 4x4
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    flmat[i][j] = m.flmat[i][j];
                }
            }
        } else if (m.getCols() != 4) {
            throw new EuclidRuntimeException("must have 3 or 4 cols");
        } else {
            this.flmat = m.flmat;
        }
        this.trnsfrm = checkMatrix();
    }
    /**
     * from a matrix and vector.
     * 
     * @param m
     *            3x3 rotation matrix
     * @param v
     *            translation vector
     * @exception EuclidRuntimeException
     *                <TT>m</TT> must be 3*3
     */
    public Transform3(RealSquareMatrix m, Vector3 v) throws EuclidRuntimeException {
        this(m);
        // 3x3 matrix. convert to 4x4
        if (m.getCols() == 3) {
            // convert to 4x4
            for (int i = 0; i < 3; i++) {
                flmat[i][3] = v.flarray[i];
            }
        } else {
            throw new EuclidRuntimeException("must have 3 columns");
        }
    }
    /**
     * from a crystallographic operator.
     * 
     * @param opString
     *            for example 1/2-x,1/2+y,-z
     *            extended to 0.5+x, y-0.25, z  // etc
     * @throws EuclidRuntimeException
     *             corrupt/invalid string
     */
    public Transform3(String opString) throws EuclidRuntimeException {
        super(4);
        String[] ss = opString.split("\\s*,\\s*");
//        StringTokenizer st = new StringTokenizer(opString, EuclidConstants.S_COMMA);
        if (ss.length != 3) {
            throw new EuclidRuntimeException("Must have 3 operators");
        }
        try {
        	ParsedSymop.createTransform(ss);
        } catch (RuntimeException e) {
	        for (int i = 0; i < 3; i++) {
	            String s = ss[i];
	            s = s.trim();
	        	analyzeOperator(opString, i, s);
	        }
        }
    }

	private void analyzeOperator(String opString, int i, String s) {
		StringTokenizer sst = new StringTokenizer(s, "+-", true);
		int ntok = sst.countTokens();
		double sign = 1;
		for (int j = 0; j < ntok; j++) {
		    String ss = sst.nextToken().trim();
		    int idx = ss.indexOf(S_SLASH);
		    if (idx != -1) {
		        final String numerator = ss.substring(0, idx).trim();
		        final String denominator = ss.substring(idx + 1).trim();
		        flmat[i][3] = sign * (double) Integer.parseInt(numerator)
		                / (double) Integer.parseInt(denominator);
		    } else if (ss.equalsIgnoreCase("x")) {
		        flmat[i][0] = sign;
		    } else if (ss.equalsIgnoreCase("y")) {
		        flmat[i][1] = sign;
		    } else if (ss.equalsIgnoreCase("z")) {
		        flmat[i][2] = sign;
		    } else if (ss.equals(S_MINUS)) {
		        sign = -1;
		    } else if (ss.equals(S_PLUS)) {
		        sign = 1;
		    } else if (ss.trim().equals("")) {
		        // unusual, but some people put "y+1"
		    } else {
		        try {
		            flmat[i][3] = sign * (double) Integer.parseInt(ss);
		        } catch (NumberFormatException nfe) {
		            SYSOUT.flush();
		            throw new EuclidRuntimeException("Bad string in symmetry: "
		                    + ss + " in " + opString);
		        }
		    }
		}
	}
    /**
     * clone.
     * 
     * @param m
     * @return transform
     */
    public Transform3 clone(Transform3 m) {
        // delete existing matrix in this
        Transform3 temp = new Transform3((RealSquareMatrix) m);
        temp.trnsfrm = m.trnsfrm;
        return temp;
    }
    /**
     * seem to require this one
     */
    Transform3 clone(RealSquareMatrix m) {
        Transform3 temp = new Transform3(m);
        temp.trnsfrm = checkMatrix();
        return temp;
    }
    /**
     * equality of two transforms. based on equality of RealSquareMatrix
     * 
     * @param m
     *            transform to compare
     * @return true if equal within Real.isEqual()
     */
    public boolean isEqualTo(Transform3 m) {
        return super.isEqualTo((RealSquareMatrix) m) && trnsfrm == m.trnsfrm;
    }
    /**
     * concatenate. (I think this is right...) result = this * m2 i.e. if x' =
     * m2 * x and x'' = this * x'' then x'' = result * x;
     * 
     * @param m2
     *            transform to be concatenated
     * @return result of applying this to m2
     */
    public Transform3 concatenate(Transform3 m2) {
        RealSquareMatrix temp = new RealSquareMatrix(((RealSquareMatrix) this)
                    .multiply((RealSquareMatrix) m2));
        // maximum value is matrix of greatest generality (horrible)
        Transform3 temp1 = new Transform3(temp);
        temp1.trnsfrm = (trnsfrm.i > m2.trnsfrm.i) ? trnsfrm : m2.trnsfrm;
        return temp1;
    }
    /**
     * set transformation type.
     * 
     * @param option
     *            the type
     * @return 0 if ok else 1
     */
    public int setTransformationType(Type option) {
        RealSquareMatrix s3 = new RealSquareMatrix();
        if (option == Type.ROT_ORIG)
        /** orthonormalise and set trans vector to zero */
        {
            s3 = new RealSquareMatrix(this.extractSubMatrixData(0, 2, 0, 2));
            s3.orthonormalize();
            this.replaceSubMatrixData(0, 0, s3);
        } else if (option == Type.ROT_TRANS)
        /** orthonormalise */
        {
            s3 = new RealSquareMatrix(this.extractSubMatrixData(0, 2, 0, 2));
            s3.orthonormalize();
            this.replaceSubMatrixData(0, 0, s3);
        } else if (option == Type.ROT_TRANS_SCALE)
        /**
         * orthogonalise and take geometric mean of the three diagonal scale
         * elements
         */
        {
            s3 = new RealSquareMatrix(this.extractSubMatrixData(0, 2, 0, 2));
            double[] scale = s3.euclideanColumnLengths().getArray();
            double scale3 = Math
                    .exp(Math.log(scale[0] * scale[1] * scale[2]) / 3.0);
            s3.orthonormalize();
            /**
             * diagonal scale matrix
             */
            RealArray sc1 = new RealArray(3, scale3);
            RealSquareMatrix s = RealSquareMatrix.diagonal(sc1);
            s3 = s.multiply(s3);
            replaceSubMatrixData(0, 0, s3);
        } else if (option == Type.ROT_TRANS_SCALE_PERSP) {
        } else if (option == Type.ROT_TRANS_AXIAL_SCALE) {
            s3 = new RealSquareMatrix(this.extractSubMatrixData(0, 2, 0, 2));
            RealArray scale = s3.euclideanColumnLengths();
            s3.orthonormalize();
            /**
             * diagonal scale matrix
             */
            RealSquareMatrix s = RealSquareMatrix.diagonal(scale);
            s3 = s.multiply(s3);
            replaceSubMatrixData(0, 0, s3);
        } else if (option == Type.ANY) {
            /**
             * anything goes!
             */
        } else if (option == Type.NULL) {
        } else {
            return 1;
        }
        trnsfrm = option;
        return 0;
    }
    /**
     * get transformation type.
     * 
     * @return the type
     */
    public Type getTransformationType() {
        return trnsfrm;
    }
    /**
     * get new matrix type. not sure what this is!
     * 
     * @return the type
     */
    public Type checkMatrix() {
        /**
         * get Top LHS (3x3 matrix)
         */
        RealSquareMatrix s3 = new RealSquareMatrix(extractSubMatrixData(0, 2, 0, 2));
        /**
         * and Column3 (translation)
         */
        RealArray c3 = extractColumnData(3);
        if (c3 != null) {
            if (Real.isZero(c3.elementAt(0), Real.getEpsilon()) &&
            		Real.isZero(c3.elementAt(1), Real.getEpsilon())
                    && Real.isZero(c3.elementAt(2), Real.getEpsilon()))
                return Type.NULL;
        }
        /** no translation term */
        {
            if (s3.isUnit())
                return Type.NULL; // unit matrix
            if (s3.isUnitary())
                return Type.ROT_ORIG; // unitary matrix
        }
        /**
         * translation term
         */
        if (s3.isUnitary())
            return Type.ROT_TRANS; // rot + trans; no scale
        /**
         * pure scale, no perspective
         */
        if (s3.isOrthogonal()) {
            double[] scale = s3.euclideanColumnLengths().getArray();
            if (Real.isEqual(scale[0], scale[1])
                    && Real.isEqual(scale[1], scale[2])) {
                return Type.ROT_TRANS_SCALE;
            }
            return Type.ROT_TRANS_AXIAL_SCALE;
        }
        return Type.ANY;
    }
    /**
     * interpret current matrix as rotation about general axis. user must supply
     * an empty axis and an empty angle, which will be filled by the routine.
     * will do better to create AxisAndAngle class
     * 
     * @param axis
     *            (holds return values)
     * @param ang
     *            angle (holds return value)
     * @return flag
     */
    public int getAxisAndAngle(Vector3 axis, Angle ang) {
        RealSquareMatrix s3 = new RealSquareMatrix(this.extractSubMatrixData(0, 2, 0, 2));
        s3.orthonormalize();
        int chirality = 1;
        /**
         * invert improper rotations
         */
        if ((double) s3.determinant() < 0.0) {
            s3.negative();
            chirality = -1;
        }
        double theta = Math.acos(((double) s3.trace() - 1.0) * 0.5);
        double[][] mat = s3.getMatrix();
        double[] lmn = new double[3];
        /**
         * theta might be exactly pi or zero
         */
        if (Real.isEqual(theta, Math.PI) || Real.isEqual(theta, 0.0)) {
            lmn[0] = Math.sqrt((1.0 + mat[0][0]) * 0.5);
            if (Real.isZero(lmn[0], Real.getEpsilon())) {
                lmn[1] = mat[0][1] * 0.5 / lmn[0];
                lmn[2] = mat[0][2] * 0.5 / lmn[0];
            } else {
                lmn[1] = Math.sqrt((1.0 + mat[1][1]) * 0.5);
                lmn[2] = mat[1][2] / (2.0 * lmn[1]);
            }
        } else {
            double c = 1.0 / (2.0 * Math.sin(theta));
            lmn[0] = (mat[2][1] - mat[1][2]) * c;
            lmn[1] = (mat[0][2] - mat[2][0]) * c;
            lmn[2] = (mat[1][0] - mat[0][1]) * c;
        }
        /**
         * stuff into angle and axis
         */
        ang.shallowCopy(new Angle(theta));
        System.arraycopy(lmn, 0, axis.getArray(), 0, 3);
        return chirality;
    }
    /**
     * get translation component.
     * 
     * @return the translation
     */
    public Vector3 getTranslation() {
        return new Vector3(flmat[0][3], flmat[1][3], flmat[2][3]);
    }
    /** increment translation component.
     * add vector to current translation
     * @param dt translation increment
     */
    public void incrementTranslation(Vector3 dt) {
        flmat[0][3] += dt.flarray[0];
        flmat[1][3] += dt.flarray[1];
        flmat[2][3] += dt.flarray[2];
    }
    /** set translation component.
     * 
     * @param t translation vector
     */
    public void setTranslation(Vector3 t) {
        flmat[0][3] = t.flarray[0];
        flmat[1][3] = t.flarray[1];
        flmat[2][3] = t.flarray[2];
    }
    /**
     * get centre of rotation. if R is rotation and t is translation compute p =
     * ~(I - R) . t
     * 
     * @return the centre
     */
    public Point3 getCentreOfRotation() {
        Point3 p = new Point3();
        RealSquareMatrix unit = new RealSquareMatrix(3);
        RealSquareMatrix temp = new RealSquareMatrix(this.extractSubMatrixData(0, 2, 0, 2));
        unit = unit.subtract(temp); // (I - Rmat)
        unit.transpose();
        RealArray t = new RealArray(getTranslation().getArray());
        p = new Point3(unit.multiply(t).getArray());
    // p = ~(I - R) . t
        return p;
    }
    /**
     * get scales.
     * 
     * @return 3-element RealArray)
     */
    public RealArray getScales() {
        RealArray scales;
        RealSquareMatrix s3 = new RealSquareMatrix(extractSubMatrixData(0, 2, 0, 2));
        scales = s3.euclideanColumnLengths();
        return scales;
    }
    /**
     * get Unitary matrix. eliminate scales and translation and normalize
     * 
     * @return unitary 3X3 matrix
     */
    public RealSquareMatrix getRotationMatrix() {
        RealSquareMatrix s;
        RealSquareMatrix s3 = new RealSquareMatrix(extractSubMatrixData(0, 2, 0, 2));
        s3.normaliseByColumns();
        s = s3;
        return s;
    }
    final static String[] oper = {"x", "y", "z"};
    /** return operator in crystallographic form;
     * 
     * @return string of type "x,-y,1/2+z"
     */
    public String getCrystallographicString() {
        String s = "";
        for (int irow = 0; irow < 3; irow++) {
            s += trans(flmat[irow][3]);
            for (int jcol = 0; jcol < 3; jcol++) {
                double f = flmat[irow][jcol];
                if (f > 0.1) {
                    s += EuclidConstants.S_PLUS+oper[jcol];
                } else if (f < -0.1) {
                    s += EuclidConstants.S_MINUS+oper[jcol];
                }
            }
            if (irow < 2) {
                s += EuclidConstants.S_COMMA;
            }
        }
        return s;
    }
    
    private String trans(double dd) {
        int n = (int) Math.round(dd * 12);
        int d = 12;
        if (n % 2 == 0) {
            n /= 2;
            d /= 2;
        }
        if (n % 2 == 0) {
            n /= 2;
            d /= 2;
        }
        if (n % 3 == 0) {
            n /= 3;
            d /= 3;
        }
        String s = EuclidConstants.S_EMPTY;
        if (n != 0) {
            if (d == 1) {
                s = EuclidConstants.S_EMPTY+n;
            } else {
                s += EuclidConstants.S_EMPTY+n+S_SLASH+d;
            }
        }
        return s;
    }
    
    /**
     * check not null
     * 
     * @param t transform3
     */
    public static void checkNotNull(Transform3 t) {
        if (t == null) {
            throw new EuclidRuntimeException("null transform");
        }
    }

}
