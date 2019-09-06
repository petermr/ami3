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

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.EigenDecompositionImpl;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * square matrix class
 * 
 * RealSquareMatrix represents a square m-x-m matrix. The basic matrix algebra
 * for square matrices is represented here Check out the exciting member
 * functions, which are supported by Exceptions where appropriate. (NB. No
 * attempt has been made to provide numerical robustness and inversion,
 * diagonalisation, etc are as you find them.)
 * <P>
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class RealSquareMatrix extends RealMatrix {
    final static Logger LOG = Logger.getLogger(RealSquareMatrix.class);

    /**
     * helper class to provide types of matrix.
     */
    /** type */
    public enum Type {
        /** */
        UPPER_TRIANGLE(1),
        /** */
        LOWER_TRIANGLE(2),
        /** */
        SYMMETRIC(3),
        /** */
        DIAGONAL(4),
        /** */
        OUTER_PRODUCT(5),
        /** */
        UNKNOWN(6);
        /** integer value */
        public int i;
        private Type(int i) {
            this.i = i;
        }
    }
	private EigenDecompositionImpl eigenDecompositionImpl;
	private RealSquareMatrix inverse;
	private RealArray eigenvalues;
	private RealSquareMatrix eigenvectors;
    /**
     * Constructor. This gives a default matrix with cols = rows = 0.
     */
    public RealSquareMatrix() {
        super();
    }
    /**
     * Constructor.
     * 
     * @param rows
     *            number of rows and columns values are set to zero
     */
    public RealSquareMatrix(int rows) {
        super(rows, rows);
    }
    
    
    /**
     * Creates square matrix from real matrix.
     * 
     * @param f
     *            real array (length rows) multiplied to give outer product
     * @return square matrix of size rows * rows
     */
    public static RealSquareMatrix outerProduct(RealArray f) {
        int rows = f.size();
        RealSquareMatrix temp = new RealSquareMatrix(rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                temp.flmat[i][j] = f.elementAt(i) * f.elementAt(j);
            }
        }
        return temp;
    }
    
    /**
     * create diagonal matrix from real matrix.
     * 
     * @param f
     *            real array (length rows)
     * @return square matrix with elem (i, i) = f(i), else 0.0
     */
    public static RealSquareMatrix diagonal(RealArray f) {
        int rows = f.size();
        RealSquareMatrix temp = new RealSquareMatrix(rows);
        for (int i = 0; i < rows; i++) {
            temp.flmat[i][i] = f.elementAt(i);
        }
        return temp;
    }
    /**
     * Creates real square matrix from array 
     * THE COLUMN IS THE FASTEST MOVING
     * INDEX, that is the matrix is filled as mat(0,0), mat(0,1) ... C-LIKE
     * 
     * @param rows
     *            the final rows and cols of real square matrix
     * @param array
     *            of size (rows * rows)
     * @exception EuclidRuntimeException
     *                <TT>array</TT> size must be multiple of <TT>rows</TT>
     */
    public RealSquareMatrix(int rows, double[] array) throws EuclidRuntimeException {
        super(rows, rows, array);
    }
    /**
     * Creates real square matrix with all elements initialized to double value.
     * 
     * @param rows
     *            size of square matrix
     * @param f
     *            value of all elements
     */
    public RealSquareMatrix(int rows, double f) {
        super(rows, rows, f);
    }
    /**
     * Constructor for submatrix of another matrix.
     * 
     * @param m
     *            matrix to slice (need not be square)
     * @param lowrow
     *            the start row inclusive (count from 0)
     * @param lowcol
     *            the start column inclusive (count from 0)
     * @param rows
     *            size of final matrix
     * @exception EuclidRuntimeException
     *                lowrow, lowcol or rows are not consistent with size of
     *                <TT>m</TT>
     */
    public RealSquareMatrix(RealMatrix m, int lowrow, int lowcol, int rows)
            throws EuclidRuntimeException {
        super(m, lowrow, lowrow + rows - 1, lowcol, lowcol + rows - 1);
    }
    /**
     * copy constructor.
     * 
     * @param m
     *            matrix to copy
     */
    public RealSquareMatrix(RealSquareMatrix m) {
        super(m);
    }
    /**
     * shallow copy from RealMatrix
     * 
     * the array values are not copied (only the reference)
     * 
     * @param m
     *            matrix to copy reference from
     * 
     * @exception EuclidRuntimeException
     *                <TT>m</TT> must be square (that is cols = rows)
     */
    public RealSquareMatrix(RealMatrix m) throws EuclidRuntimeException {
        super(m.rows, m.cols);
        if (m.cols != m.rows) {
            throw new EuclidRuntimeException("non square matrix");
        }
        this.flmat = m.flmat;
    }
    /**
     * constructor from array.
     * 
     * form from a Java 2-D array (it holds row and column count)
     * 
     * @param matrix
     *            to copy
     * @exception EuclidRuntimeException
     *                <TT>matrix</TT> is not square (might even not be
     *                rectangular!)
     */
    public RealSquareMatrix(double[][] matrix) throws EuclidRuntimeException {
        super(matrix);
        if (cols != rows) {
            throw new EuclidRuntimeException("non square matrix");
        }
    }
    /**
     * shallowCopy an existing square matrix.
     * 
     * @param m
     *            matrix to shallow copy
     * @exception EuclidRuntimeException
     *                <TT>m</TT> must have the same number of rows and cols as
     *                <TT>this</TT>
     */
    public void shallowCopy(RealSquareMatrix m) throws EuclidRuntimeException {
        super.shallowCopy((RealMatrix) m);
    }
    
    /** create square matrix from lower triangle values
     * upper triangle is filled with zeros
     * @param f real array (length rows * (rows+1) / 2)
     * @return square matrix with elem (i, j) = f(k++), else 0.0
     */
    public static RealSquareMatrix fromLowerTriangle(RealArray f) {
        int n = f.size();
        int rows = (int) Math.round((Math.sqrt(8*n+1) - 1 + 0.001) / 2.);
        if ((rows * (rows + 1))/2 != n) {
        	throw new RuntimeException("band number of values ("+n+") for lower Triangle");
        }
        RealSquareMatrix temp = new RealSquareMatrix(rows);
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= i; j++) {
                temp.flmat[i][j] = f.elementAt(count);
                if (i != j) {
                	temp.flmat[j][i] = 0.0;
                }
                count++;
            }
        }
        return temp;
    }
    
    /** create square matrix from lower triangle
     * lower triangle is filled with zeros
     * @param f real array (length rows * (rows+1) / 2)
     * @return square matrix with elem (i, j) = f(k++), else 0.0
     */
    public static RealSquareMatrix fromUpperTriangle(RealArray f) {
        int n = f.size();
        int rows = (int) Math.round((Math.sqrt(8*n+1) - 1 + 0.001) / 2.);
        if ((rows * (rows + 1))/2 != n) {
        	throw new RuntimeException("band number of values ("+n+") for lower Triangle");
        }
        RealSquareMatrix temp = new RealSquareMatrix(rows);
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = i; j < rows; j++) {
                temp.flmat[i][j] = f.elementAt(count);
                if (i != j) {
                	temp.flmat[j][i] = 0.0;
                }
                count++;
            }
        }
        return temp;
    }
    
    /**
     * are two matrices identical
     * 
     * @param r
     *            matrix to compare
     * @return true if equal (see RealMatrix.equals for details)
     */
    public boolean isEqualTo(RealSquareMatrix r) {
        return super.isEqualTo((RealMatrix) r);
    }
    /**
     * matrix addition. adds conformable matrices. Does NOT alter this.
     * 
     * @param m
     *            matrix to add
     * @exception EuclidRuntimeException
     *                <TT>m</TT> must have the same number of rows and cols as
     *                <TT>this</TT>
     * @return resultant matrix
     */
    public RealSquareMatrix plus(RealSquareMatrix m) throws EuclidRuntimeException {
        RealMatrix temp = super.plus((RealMatrix) m);
        RealSquareMatrix sqm = new RealSquareMatrix(temp);
        return sqm;
    }
    /**
     * matrix subtraction. subtracts conformable matrices. Does NOT alter this.
     * 
     * @param m
     *            matrix to subtract from this
     * @exception EuclidRuntimeException
     *                <TT>m</TT> must have the same number of rows and cols as
     *                <TT>this</TT>
     * @return resultant matrix
     */
    public RealSquareMatrix subtract(RealSquareMatrix m) throws EuclidRuntimeException {
        RealMatrix temp = super.subtract((RealMatrix) m);
        RealSquareMatrix sqm = new RealSquareMatrix(temp);
        return sqm;
    }
    /**
     * matrix multiplication.
     * 
     * multiplies conformable matrices; result is <TT>this*m </TT>
     * 
     * @param m
     *            matrix to multiply by
     * @exception EuclidRuntimeException
     *                <TT>m</TT> must have the same number of rows as <TT>this</TT>
     *                has cols
     * @return new matrix
     */
    public RealSquareMatrix multiply(RealSquareMatrix m) throws EuclidRuntimeException {
        RealMatrix temp = super.multiply((RealMatrix) m);
        RealSquareMatrix sqm = new RealSquareMatrix(temp);
        return sqm;
    }
    /**
     * determinant. hardcoded up to order 3 at present; rest is VERY slow :-(
     * calls determinant recursively for order > 3
     * 
     * @return the determinant
     */
    public double determinant() {
        double det = 0.0;
        if (rows == 1) {
            det = flmat[0][0];
        } else if (rows == 2) {
            det = flmat[0][0] * flmat[1][1] - flmat[1][0] * flmat[0][1];
        } else if (rows == 3) {
            det = flmat[0][0]
                    * (flmat[1][1] * flmat[2][2] - flmat[1][2] * flmat[2][1])
                    + flmat[0][1]
                    * (flmat[1][2] * flmat[2][0] - flmat[1][0] * flmat[2][2])
                    + flmat[0][2]
                    * (flmat[1][0] * flmat[2][1] - flmat[1][1] * flmat[2][0]);
        } else {
            int sign = 1;
            for (int j = 0; j < cols; j++) {
                det += sign * flmat[0][j] * minorDet(j);
                sign = -sign;
            }
        }
        return det;
    }
    private double minorDet(int ii) {
        int r = rows - 1;
        double array[] = new double[r * r];
        int countN = 0;
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j != ii) {
                    array[countN++] = flmat[i][j];
                }
            }
        }
        RealSquareMatrix mm = null;
        try {
            mm = new RealSquareMatrix(r, array);
        } catch (Exception e) {
            throw new EuclidRuntimeException(e.toString());
        }
        double d = mm.determinant();
        return d;
    }
    /**
     * trace.
     * 
     * @return the trace
     */
    public double trace() {
        double trace = 0.0;
        for (int i = 0; i < rows; i++) {
            trace += flmat[i][i];
        }
        return trace;
    }
    /**
     * is it a unit matrix.
     * 
     * @return are all diagonals 1 and off-diagonal zero (within Real.isEqual())
     */
    public boolean isUnit() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                double f = flmat[i][j];
                if ((!Real.isZero(f, Real.getEpsilon()) && (i != j))
                        || (!Real.isEqual(f, 1.0, Real.getEpsilon()) && (i == j))) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * is matrix symmetric.
     * 
     * @return is Real.isEqual(elem(i,j), elem(j,i))
     */
    public boolean isSymmetric() {
        for (int i = 0; i < rows - 1; i++) {
            for (int j = i + 1; j < rows; j++) {
                if (!Real.isEqual(flmat[i][j], flmat[j][i])) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * orthonormalise matrix. crude (only works for 1, 2x2, 3x3 at present) -
     * use jama?
     * 
     * @exception EuclidRuntimeException
     *                I have only written this for <TT>this.rows</TT> up to 3.
     *                If anyone can find a routine, this will disappear ... -(
     * @return the orthonormalized matrix
     */
    public RealSquareMatrix orthonormalize() {
        if (cols == 1) {
            flmat[0][0] = 1.;
        } else if (cols == 2) {
            Vector3 v1 = new Vector3(flmat[0][0], flmat[0][1], 0.);
            v1 = v1.normalize();
            Vector3 v2 = new Vector3(flmat[1][0], flmat[1][1], 0.);
            v2 = v2.normalize();
            Vector3 v3 = v1.cross(v2);
            v2 = v3.cross(v1);
            v2 = v2.normalize();
            flmat[0][0] = v1.flarray[0];
            flmat[0][1] = v1.flarray[1];
            flmat[1][0] = v2.flarray[0];
            flmat[1][1] = v2.flarray[1];
        } else if (cols == 3) {
            Vector3 v0 = new Vector3(extractRowData(0));
            Vector3 v1 = new Vector3(extractRowData(1));
            Vector3 v2 = new Vector3(extractRowData(2));
            // check handedness
            double det = v0.getScalarTripleProduct(v1, v2);
            v0.normalize();
            v2 = v0.cross(v1);
            v2.normalize();
            v1 = v2.cross(v0);
            if (det < 0.0) {
                v2.negative();
            }
            replaceRowData(0, v0.getArray());
            replaceRowData(1, v1.getArray());
            replaceRowData(2, v2.getArray());
        } else {
            throw new EuclidRuntimeException(
                    "Sorry: orthonormalise only up to 3x3 matrices: had "
                            + cols + " columns");
        }
        return this;
    }
    /**
     * is matrix unitary. i.e is it orthonormal?
     * 
     * (synonym for isUnitary())
     * 
     * @return true if is Unitary
     */
    public boolean isOrthonormal() {
        return isUnitary();
    }
    /**
     * is matrix UpperTriangular.
     * 
     * @return true if all bottom triangle excluding diagona Real.isZero()
     */
    public boolean isUpperTriangular() {
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < i; j++) {
                if (!Real.isZero(flmat[i][j], Real.getEpsilon()))
                    return false;
            }
        }
        return true;
    }
    /**
     * is matrix LowerTriangular. diagonal must also be zero
     * 
     * @return true if all bottom triangle Real.isZero()
     */
    public boolean isLowerTriangular() {
        for (int i = 0; i < rows - 1; i++) {
            for (int j = i + 1; j < rows; j++) {
                if (!Real.isZero(flmat[i][j], Real.getEpsilon()))
                    return false;
            }
        }
        return true;
    }
    double rowDotproduct(int row1, int row2) {
        double sum = 0.0;
        for (int i = 0; i < cols; i++) {
            sum += flmat[row1][i] * flmat[row2][i];
        }
        return sum;
    }
    /**
     * is matrix orthogonal.
     * 
     * checks if Real.isZero(dotProduct) (rowwise calculation)
     * 
     * @return true if orthogonal
     */
    public boolean isOrthogonal() {
        for (int i = 0; i < rows - 1; i++) {
            for (int j = i + 1; j < rows; j++) {
                double dp = rowDotproduct(i, j);
                if (!Real.isZero(dp, Real.getEpsilon())) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * is matrix an improper rotation.
     * 
     * @return true if determinant is -1.0 and isOrthogonal
     * 
     */
    public boolean isImproperRotation() {
        double f = determinant();
        return (Real.isEqual(f, -1.0) && isOrthogonal());
    }
    /**
     * is matrix unitary. i.e is it orthonormal?
     * 
     * (synonym for isUnitary())
     * 
     * @return true if is Unitary
     */
    public boolean isUnitary() {
        double f = determinant();
        double fa = Math.abs(f);
        return (Real.isEqual(fa, 1.0) && isOrthogonal());
    }
    /**
     * copy upper triangle into lower triangle. alters this to make it symmetric
     * 
     * @return this as new square matrix
     */
    public RealSquareMatrix copyUpperToLower() {
        for (int i = 0; i < cols - 1; i++) {
            for (int j = i + 1; j < cols; j++) {
                flmat[j][i] = flmat[i][j];
            }
        }
        return this;
    }
    /**
     * copy lower triangle into upper triangle. alters this to make it symmetric
     * 
     * @return this as new square matrix
     */
    public RealSquareMatrix copyLowerToUpper() {
        for (int i = 0; i < cols - 1; i++) {
            for (int j = i + 1; j < cols; j++) {
                flmat[i][j] = flmat[j][i];
            }
        }
        return this;
    }
    /**
     * copy lower triangle into linear array; order: 0,0; 1,0; 1,1; 2,0
     * 
     * @return linear array of size rows * (rows+1) / 2
     */
    public RealArray lowerTriangle() {
        int n = rows;
        RealArray triangle = new RealArray((n * (n + 1)) / 2);
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= i; j++) {
                triangle.setElementAt(count++, flmat[i][j]);
            }
        }
        return triangle;
    }
    /**
     * transpose. MODIFIES this
     */
    public void transpose() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < i; j++) {
                double t = flmat[i][j];
                flmat[i][j] = flmat[j][i];
                flmat[j][i] = t;
            }
        }
    }
    /**
     * diagonalisation returns eigenvalues and vectors as MODIFIED arguments;
     * 'this' is NOT affected USE JAMA INSTEAD Note that IllCondMatrixException
     * is RETURNED and not thrown
     * 
     * @param eigenvalues
     * @param eigenvectors
     * @param illCond
     * @exception EuclidRuntimeException
     *                must have at least order 2
     * @return flag
     */
    public int diagonaliseAndReturnRank(RealArray eigenvalues,
            RealSquareMatrix eigenvectors, EuclidRuntimeException illCond)
            throws EuclidRuntimeException {
        // because this was translated from FORTRAN there are some offsets
        // store current matrix as 1-D array lower Triangle
        RealArray lowert = this.lowerTriangle();
        // f77 offset
        double[] lower77 = new double[lowert.size() + 1];
        System.arraycopy(lowert.getArray(), 0, lower77, 1, lowert.size());
        int order = rows;
        // size must be at least 2!
        if (rows < 2) {
            throw new EuclidRuntimeException("need at least 2 rows");
        }
        double[] eigenval77 = new double[rows + 1];
        double[] eigenvect77 = new double[rows * rows + 1];
        illCond = null;
        int rank = Diagonalise.vneigl(order, lower77, eigenval77, eigenvect77,
                illCond);
        // unoffset the f77
        double[] eigenval = new double[rows];
        System.arraycopy(eigenval77, 1, eigenval, 0, rows);
        double[] eigenvect = new double[rows * rows];
        System.arraycopy(eigenvect77, 1, eigenvect, 0, rows * rows);
        eigenvalues.shallowCopy(new RealArray(eigenval));
        eigenvectors.shallowCopy(new RealSquareMatrix(rows, eigenvect));
        return rank;
    }
    /**
     * orthogonalise matrix. (only works for 3x3 at present); MODIFIES this
     * 
     * @exception EuclidRuntimeException
     *                I have only written this for <TT>this.rows</TT> up to 3.
     *                If anyone can find a routine, this will disappear ... -(
     */
    public void orthogonalise() throws EuclidRuntimeException {
        if (cols == 3) {
            Vector3 v0 = new Vector3(extractRowData(0));
            Vector3 v1 = new Vector3(extractRowData(1));
            Vector3 v2 = new Vector3(extractRowData(2));
            double l0 = v0.getLength();
            double l1 = v1.getLength();
            double l2 = v2.getLength();
            /**
             * check handedness
             */
            double det = v0.getScalarTripleProduct(v1, v2);
            v0.normalize();
            v2 = v0.cross(v1);
            v2.normalize();
            v1 = v2.cross(v0);
            if (det < 0.0) {
                v2 = v2.negative();
            }
            v0 = v0.multiplyBy(l0);
            v1 = v1.multiplyBy(l1);
            v2 = v2.multiplyBy(l2);
            replaceRowData(0, v0.getArray());
            replaceRowData(1, v1.getArray());
            replaceRowData(2, v2.getArray());
        } else {
            throw new EuclidRuntimeException(
                    "Sorry: orthogonalise only up to 3x3 matrices");
        }
    }

    public RealArray calculateEigenvalues() {
    	solveDecomposition();
    	return eigenvalues;
    }
    
    public RealSquareMatrix calculateInverse() {
    	solveDecomposition();
    	return inverse;
    }
	private void solveDecomposition() {
		if (eigenDecompositionImpl == null) {
	    	Array2DRowRealMatrix realMatrix = new Array2DRowRealMatrix(this.getMatrix());
	    	try {
		    	eigenDecompositionImpl = new EigenDecompositionImpl(realMatrix, 0.0);
		    	inverse = new RealSquareMatrix(eigenDecompositionImpl.getSolver().getInverse().getData());
		    	eigenvalues = new RealArray(eigenDecompositionImpl.getRealEigenvalues());
	    		eigenvectors = new RealSquareMatrix(eigenDecompositionImpl.getV().getData());
	    	} catch (InvalidMatrixException ime) {
		    	inverse = null;
		    	eigenvalues = null;
	    		eigenvectors = null;
	    	}
		}
	}
    
	/**
    	V is an orthogonal matrix, i.e. its transpose is also its inverse.
    	The columns of V are the eigenvectors of the original matrix.    	
    	uses apachae.commons.math
	 * @return
	 */
    public RealSquareMatrix calculateEigenvectors() {
    	solveDecomposition();
    	return eigenvectors;
    }
    
    public void resetEigenDecomposition() {
    	eigenDecompositionImpl = null;
    	inverse = null;
    }
    
    /**
     * create orthogonlisation matrix from cell lengths and angles. Rollett
     * "Computing Methods in Crystallography" Pergamon 1965 p.23
     * 
     * @param celleng
     *            3 lengths
     * @param angle
     *            3 angles in degrees
     * @return matrix
     */
    public static RealSquareMatrix getCrystallographicOrthogonalisation(
            double[] celleng, double[] angle) {
        RealSquareMatrix orthMat = new RealSquareMatrix(3);
        double dtor = Math.PI / 180.0;
        double sina = Math.sin(dtor * angle[0]);
        double cosa = Math.cos(dtor * angle[0]);
        double sinb = Math.sin(dtor * angle[1]);
        double cosb = Math.cos(dtor * angle[1]);
        double cosg = Math.cos(dtor * angle[2]);
        double cosgstar = (cosa * cosb - cosg) / (sina * sinb);
        double singstar = Math.sqrt(1.0 - cosgstar * cosgstar);
        double[][] omat = orthMat.getMatrix();
        omat[0][0] = celleng[0] * sinb * singstar;
        omat[0][1] = 0.0;
        omat[0][2] = 0.0;
        omat[1][0] = -celleng[0] * sinb * cosgstar;
        omat[1][1] = celleng[1] * sina;
        omat[1][2] = 0.0;
        omat[2][0] = celleng[0] * cosb;
        omat[2][1] = celleng[1] * cosa;
        omat[2][2] = celleng[2];
        return orthMat;
    }
    /**
     * inversion of matrix. creates NEW matrix
     * Hard-coded up to 3x3, if matrix is larger uses JAMA
     * 
     * @exception EuclidRuntimeException
     *                singular matrix (or worse!)
     * @return inverse matrix
     */
    public RealSquareMatrix getInverse() throws EuclidRuntimeException {
    	double[][] inv = new double[rows][rows];
    	double[][] temp = getMatrix();
    	
    	double det = this.determinant();
    	if (det == 0) {
    		throw new EuclidRuntimeException("Cannot invert matrix: determinant=0");
    	}
    	double detr = 1 / det;
    	
    	if (this.rows == 1) {           // 1x1 Matrix
        	inv[0][0] = detr;
        } else if (this.rows == 2) {    // 2x2 Matrix
        	inv[0][0] = detr * temp[1][1];
        	inv[1][0] = 0 - (detr * temp[0][1]);
        	inv[0][1] = 0 - (detr * temp[1][0]);
        	inv[1][1] = detr * temp[0][0];
        } else if (this.rows == 3) {    // 3x3 Matrix
        	inv[0][0] = detr * (temp[1][1] * temp[2][2] - temp[1][2] * temp[2][1]);
			inv[0][1] = detr * (temp[0][2] * temp[2][1] - temp[0][1] * temp[2][2]);
			inv[0][2] = detr * (temp[0][1] * temp[1][2] - temp[0][2] * temp[1][1]);
			inv[1][0] = detr * (temp[1][2] * temp[2][0] - temp[1][0] * temp[2][2]);
			inv[1][1] = detr * (temp[0][0] * temp[2][2] - temp[0][2] * temp[2][0]);
			inv[1][2] = detr * (temp[0][2] * temp[1][0] - temp[0][0] * temp[1][2]);
			inv[2][0] = detr * (temp[1][0] * temp[2][1] - temp[1][1] * temp[2][0]);
			inv[2][1] = detr * (temp[0][1] * temp[2][0] - temp[0][0] * temp[2][1]);
			inv[2][2] = detr * (temp[0][0] * temp[1][1] - temp[0][1] * temp[1][0]);
        } else {
            throw new EuclidRuntimeException("Inverse of larger than 3x3 matricies: NYI");
        }
    	
    	RealSquareMatrix imat = new RealSquareMatrix(inv);
    	
        return imat;
    }
    /**
     * invert a square matrix from Hansen (The C++ answer Book - pp 114-5)
     */
    private boolean dopivot(double[][] A, double[][] I, int diag, int nelem) {
        if (A[diag][diag] != 0.0)
            return true;
        int i;
        for (i = diag + 1; i < nelem; i++) {
            if (A[i][diag] != 0.0) {
                double[] t;
                t = A[diag];
                A[diag] = A[i];
                A[i] = t;
                t = I[diag];
                I[diag] = I[i];
                I[i] = t;
                break;
            }
        }
        return i < nelem;
    }
    @SuppressWarnings("unused")
    private void matinv(double[][] A, double[][] I, int nelem)
            throws EuclidRuntimeException {
        // identity
        for (int i = 0; i < nelem; i++) {
            for (int j = 0; j < nelem; j++) {
                I[i][j] = 0.0;
            }
            I[i][i] = 1.0;
        }
        for (int diag = 0; diag < nelem; diag++) {
            if (!dopivot(A, I, diag, nelem)) {
                throw new EuclidRuntimeException("singular matrix");
            }
            double div = A[diag][diag];
            if (div != 1.0) {
                A[diag][diag] = 1.0;
                for (int j = diag + 1; j < nelem; j++)
                    A[diag][j] /= div;
                for (int j = 0; j < nelem; j++)
                    I[diag][j] /= div;
            }
            for (int i = 0; i < nelem; i++) {
                if (i == diag)
                    continue;
                double sub = A[i][diag];
                if (sub != 0.0) {
                    A[i][diag] = 0.0;
                    for (int j = diag + 1; j < nelem; j++)
                        A[i][j] -= sub * A[diag][j];
                    for (int j = 0; j < nelem; j++)
                        I[i][j] -= sub * I[diag][j];
                }
            }
        }
    }
}
/* in separate class because jvc bombs on compiling this */
class Diagonalise implements EuclidConstants {
	private static final Logger LOG = Logger.getLogger(Diagonalise.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    final static double ZERO = 0.0;
    final static double ONE = 1.0;
    final static double TWO = 2.0;
    final static double SQRT2 = Math.sqrt(TWO);
    /**
     * diagonalisation routine
     * 
     * @param order
     * @param a
     *            matrix as 1d array
     * @param eigval
     *            eigenvalues
     * @param eigvec
     *            egenvectors as 1d array
     * @param illCond
     * @throws EuclidRuntimeException
     *             contains reason for failure (probably illconditioned)
     * @exception EuclidRuntimeException
     *                order < 2
     * @return flag
     */
    public static int vneigl(int order, double[] a, double[] eigval,
            double[] eigvec, EuclidRuntimeException illCond) throws EuclidRuntimeException {
        /***********************************************************************
         * // translated from FORTRAN
         * 
         * 
         * BECAUSE THIS HAS BEEN TRANSLATED, THERE IS AN OFFSET OF 1 IN THE
         * ARRAY ADDRESSES
         * 
         * YOU SHOULD NEVER CALL THIS DIRECTLY, BUT USE THE WRAPPER
         * (diagonalise). Category: Eigenvectors and Inversion Function:
         * Destructive diagonalisation of symmetric matrix Mnemonic: Vector
         * N-dimensional : EIGenvectors/values of Lower triangle
         * 
         * 
         * Inputs: long int order : order of matrix
         * 
         * Input And Outputs: double a[] : lower triangular matrix (DESTROYED)
         * 
         * Outputs: double eigval[] : returned eigenvalues double eigvec[] :
         * returned eigenvectors
         * 
         * Syntax: call VNEIGL(order,a,eigval,eigvec)
         * 
         * Other Information: I think this takes a lower triangle A,
         * destructively diagonalises it
         * 
         * into A with eigenvectors in R. Eigenvalues are left down diagonal of
         * A. The method is (I think) Householder and others. N is order of
         * matrix The eigenvectors, although declared in routine as
         * 1-dimensional, can be taken as eigvec(N,N)
         * 
         * *******************************************************************
         * 
         * Local variables:
         */
        double cosx, sinx, xxyy, cosx2, sinx2 = 0.0;
        int i, j, k, l, m = 0;
        double x, y, range, anorm, sincs, anrmx = 0.0;
        int ia, ij, /* il, *//* im, */ll, lm, iq, mm, jq, lq, mq, ind, ilq, imq, ilr, imr = 0;
        double thr = 0.0;
        /* -------------------------------------------------------------------- */
        /* Conversion from F77 */
        // int /*ivar1, ivar2,*/ ivar3 = 0;
        // double rvar1 = 0.0;
        cosx = 0.0;
        sinx = 0.0;
        xxyy = 0.0;
        cosx2 = 0.0;
        sinx2 = 0.0;
        i = 0;
        j = 0;
        k = 0;
        l = 0;
        m = 0;
        x = 0.0;
        y = 0.0;
        range = 0.0;
        anorm = 0.0;
        sincs = 0.0;
        anrmx = 0.0;
        ia = 0;
        ij = 0;
        // il = 0;
        // im = 0;
        ll = 0;
        lm = 0;
        iq = 0;
        mm = 0;
        jq = 0;
        lq = 0;
        mq = 0;
        ind = 0;
        ilq = 0;
        imq = 0;
        ilr = 0;
        imr = 0;
        thr = 0.0;
        // ivar3 = 0;
        // rvar1 = 0.0;
        /* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
        LOG.info("O..." + order);
        /* Function Body */
        if (order < 2) {
            throw new EuclidRuntimeException("order too small");
        }
        /* generate identity matrix */
        range = 1e-6f;
        iq = -(order);
        /* ivar1 = order; */
        for (j = 1; j <= /* ivar1 */order; ++j) {
            iq += order;
            /* ivar2 = order; */
            for (i = 1; i <= /* ivar2 */order; ++i) {
                ij = iq + i;
                eigvec[ij] = ZERO;
                if (i - j == 0) {
                    eigvec[ij] = ONE;
                }
            }
        }
        LOG.info("O " + order);
        /*----------------------------------------------------------------------- */
        /* compute initial and final norms (anorm and anormx) */
        anorm = ZERO;
        /* ivar2 = order; */
        for (i = 1; i <= /* ivar2 */order; ++i) {
            /* ivar1 = order; */
            for (j = i; j <= /* ivar1 */order; ++j) {
                if (i - j != 0) {
                    ia = i + (j * j - j) / 2;
                    anorm += a[ia] * a[ia];
                }
            }
        }
        if (anorm > ZERO) {
            anorm = Math.sqrt(anorm) * SQRT2;
            anrmx = anorm * range / (new Double(order).doubleValue());
            /* initialize indicators and compute threshold, thr */
            ind = 0;
            thr = anorm;
            // L80:
            while (true) {
                thr /= new Double(order).doubleValue();
                // L90:
                while (true) {
                    l = 1;
                    while (true) {
                        // L100:
                        m = l + 1;
                        /* compute sin and cos */
                        // L110:
                        while (true) {
                            mq = (m * m - m) / 2;
                            lq = (l * l - l) / 2;
                            lm = l + mq;
                            if (Math.abs(a[lm]) - thr >= ZERO) {
                                // if (Math.abs(a[lm]) - thr > ZERO) {
                                ind = 1;
                                ll = l + lq;
                                mm = m + mq;
                                x = (a[ll] - a[mm]) * .5f;
                                y = -a[lm] / Math.sqrt(a[lm] * a[lm] + x * x);
                                if (x < ZERO) {
                                    y = -y;
                                }
                                /* check to avoid rounding errors */
                                // L140:
                                xxyy = ONE - y * y;
                                if (xxyy < ZERO) {
                                    xxyy = ZERO;
                                }
                                sinx = y
                                        / Math.sqrt((Math.sqrt(xxyy) + ONE)
                                                * TWO);
                                sinx2 = sinx * sinx;
                                cosx = Math.sqrt(ONE - sinx2);
                                cosx2 = cosx * cosx;
                                sincs = sinx * cosx;
                                /* rotate l and m columns */
                                ilq = order * (l - 1);
                                imq = order * (m - 1);
                                Diagonalise.subroutine(order, a, ilq, imq, l,
                                        m, lq, mq, ll, lm, mm, sinx, cosx,
                                        eigval, eigvec, sincs, sinx2, cosx2);
                            }
                            // L250
                            if (m == order) {
                                break;
                            }
                            // goto L110;
                            // L260
                            ++m;
                        }
                        /* test for l = second from last column */
                        // L270
                        if (l == (order - 1)) {
                            LOG.info(S_LSQUARE + l + EC.S_SLASH + order
                                    + EC.S_RSQUARE);
                            break;
                        }
                        // goto L100;
                        // L280
                        ++l;
                    }
                    // L290
                    LOG.info(S_PLUS + l + EC.S_SLASH + ind);
                    if (ind != 1) {
                        break;
                    }
                    // L300
                    ind = 0;
                    // goto L90;
                }
                LOG.info("====================broke");
                /* compare threshold with final norm */
                // L310:
                if (thr - anrmx <= ZERO) {
                    break;
                    // goto L320;
                }
                // goto L80;
            }
        }
        /* sort eigenvalues and eigenvectors */
        // L320:
        iq = -(order);
        /* ivar1 = order; */
        for (i = 1; i <= /* ivar1 */order; ++i) {
            iq += order;
            ll = i + (i * i - i) / 2;
            jq = order * (i - 2);
            /* ivar2 = order; */
            for (j = i; j <= /* ivar2 */order; ++j) {
                jq += order;
                mm = j + (j * j - j) / 2;
                if (a[ll] - a[mm] >= ZERO) {
                    continue;
                }
                x = a[ll];
                a[ll] = a[mm];
                a[mm] = x;
                /* ivar3 = order; */
                for (k = 1; k <= /* ivar3 */order; ++k) {
                    ilr = iq + k;
                    imr = jq + k;
                    x = eigvec[ilr];
                    eigvec[ilr] = eigvec[imr];
                    eigvec[imr] = x;
                }
            }
        }
        return Diagonalise.eigtest(order, a, eigval, range);
    }
    private static int eigtest(int order, double[] a, double[] eigval,
            double range) {
        /* get eigenvalues and test for singularity */
        int rank = 0;
        for (int i = order; i >= 1; --i) {
            eigval[i] = a[(i * i + i) / 2];
            if (eigval[i] < ZERO) {
                // illCond = new NPDMatrixException();
                break;
            } else if (eigval[i] < range) {
                LOG.info("SING");
                // illCond = new SingMatrixException();
                break;
            } else {
                ++rank;
            }
        }
        return rank;
    }
    static void subroutine(int order, double[] a, int ilq, int imq, int l,
            int m, int lq, int mq, int ll, int lm, int mm, double sinx,
            double cosx, double[] eigval, double[] eigvec, double sincs,
            double sinx2, double cosx2) {
        double x, y;
        /* ivar1 = order; */
        for (int i = 1; i <= /* ivar1 */order; ++i) {
            int iq = (i * i - i) / 2;
            if (i != l) {
                // L150:
                int im, il;
                int ivar2 = i - m;
                if (ivar2 != 0) {
                    if (ivar2 < 0) {
                        // L160:
                        im = i + mq;
                    } else {
                        // L170:
                        im = m + iq;
                    }
                    // L180:
                    if (i >= l) {
                        // L200:
                        il = l + iq;
                    } else {
                        // L190:
                        il = i + lq;
                    }
                    // L210:
                    x = a[il] * cosx - a[im] * sinx;
                    a[im] = a[il] * sinx + a[im] * cosx;
                    a[il] = x;
                }
                // L220:
                int ilr = ilq + i;
                int imr = imq + i;
                x = eigvec[ilr] * cosx - eigvec[imr] * sinx;
                eigvec[imr] = eigvec[ilr] * sinx + eigvec[imr] * cosx;
                eigvec[ilr] = x;
            }
            x = a[lm] * 2.f * sincs;
            y = a[ll] * cosx2 + a[mm] * sinx2 - x;
            x = a[ll] * sinx2 + a[mm] * cosx2 + x;
            a[lm] = (a[ll] - a[mm]) * sincs + a[lm] * (cosx2 - sinx2);
            a[ll] = y;
            a[mm] = x;
            /*
             * tests for completion test for m = last column
             */
        }
    }
}
