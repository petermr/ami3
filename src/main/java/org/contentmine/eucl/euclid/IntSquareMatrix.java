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
 * square matrix class
 * 
 * IntSquareMatrix represents a square m-x-m matrix. The basic matrix algebra
 * for square matrices is represented here Check out the exciting member
 * functions, which are supported by Exceptions where appropriate. (NB. No
 * attempt has been made to provide numerical robustness and inversion,
 * diagonalisation, etc are as you find them.)
 * <P>
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class IntSquareMatrix extends IntMatrix {
    /**
     * helper class to provide types of matrix.
     */
    /** type */
    public enum Type {
        /**  */
        UPPER_TRIANGLE(1),
        /**  */
        LOWER_TRIANGLE(2),
        /**  */
        SYMMETRIC(3),
        /**  */
        DIAGONAL(4),
        /**  */
        OUTER_PRODUCT(5),
        /**  */
        UNKNOWN(6);
        /** integer value */
        public int i;
        private Type(int i) {
            this.i = i;
        }
    }
    final static Logger LOG = Logger.getLogger(IntSquareMatrix.class);
    /**
     * Constructor. This gives a default matrix with cols = rows = 0.
     */
    public IntSquareMatrix() {
        super();
    }
    /**
     * Constructor.
     * 
     * @param rows
     *            number of rows and columns values are set to zero
     */
    public IntSquareMatrix(int rows) {
        super(rows, rows);
    }
    /**
     * Creates square matrix from real matrix.
     * 
     * @param f
     *            real array (length rows) multiplied to give outer product
     * @return square matrix of size rows * rows
     */
    public static IntSquareMatrix outerProduct(IntArray f) {
        int rows = f.size();
        IntSquareMatrix temp = new IntSquareMatrix(rows);
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
     * @return square matrix with elem (i, i) = f(i), else 0
     */
    public static IntSquareMatrix diagonal(IntArray f) {
        int rows = f.size();
        IntSquareMatrix temp = new IntSquareMatrix(rows);
        for (int i = 0; i < rows; i++) {
            temp.flmat[i][i] = f.elementAt(i);
        }
        return temp;
    }
    /**
     * Creates real square matrix from array THE COLUMN IS THE FASTEST MOVING
     * INDEX, that is the matrix is filled as mat(0,0), mat(0,1) ... C-LIKE
     * 
     * @param rows
     *            the final rows and cols of real square matrix
     * @param array
     *            of size (rows * rows)
     * @exception EuclidRuntimeException
     *                <TT>array</TT> size must be multiple of <TT>rows</TT>
     */
    public IntSquareMatrix(int rows, int[] array) {
        super(rows, rows, array);
    }
    /**
     * Creates real square matrix with all elements initialized to int value.
     * 
     * @param rows
     *            size of square matrix
     * @param f
     *            value of all elements
     */
    public IntSquareMatrix(int rows, int f) {
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
     * @throws EuclidRuntimeException
     */
    public IntSquareMatrix(IntMatrix m, int lowrow, int lowcol, int rows)
            throws EuclidRuntimeException {
        super(m, lowrow, lowrow + rows - 1, lowcol, lowcol + rows - 1);
    }
    /**
     * copy constructor.
     * 
     * @param m
     *            matrix to copy
     */
    public IntSquareMatrix(IntSquareMatrix m) {
        super(m);
    }
    /**
     * shallow copy from IntMatrix
     * 
     * the array values are not copied (only the reference)
     * 
     * @param m
     *            matrix to copy reference from
     * 
     * @exception EuclidRuntimeException
     *                <TT>m</TT> must be square (that is cols = rows)
     */
    public IntSquareMatrix(IntMatrix m) throws EuclidRuntimeException {
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
    public IntSquareMatrix(int[][] matrix) throws EuclidRuntimeException {
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
    public void shallowCopy(IntSquareMatrix m) throws EuclidRuntimeException {
        super.shallowCopy((IntMatrix) m);
    }
    /**
     * are two matrices identical
     * 
     * @param r
     *            matrix to compare
     * @return true if equal (see IntMatrix.equals for details)
     */
    public boolean isEqualTo(IntSquareMatrix r) {
        return super.isEqualTo((IntMatrix) r);
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
    public IntSquareMatrix plus(IntSquareMatrix m) throws EuclidRuntimeException {
        IntMatrix temp = super.plus((IntMatrix) m);
        IntSquareMatrix sqm = new IntSquareMatrix(temp);
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
    public IntSquareMatrix subtract(IntSquareMatrix m) throws EuclidRuntimeException {
        IntMatrix temp = super.subtract((IntMatrix) m);
        IntSquareMatrix sqm = new IntSquareMatrix(temp);
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
    public IntSquareMatrix multiply(IntSquareMatrix m) throws EuclidRuntimeException {
        IntMatrix temp = super.multiply((IntMatrix) m);
        IntSquareMatrix sqm = new IntSquareMatrix(temp);
        return sqm;
    }
    /**
     * trace.
     * 
     * @return the trace
     */
    public int trace() {
        int trace = 0;
        for (int i = 0; i < rows; i++) {
            trace += flmat[i][i];
        }
        return trace;
    }
    /**
     * is it a unit matrix.
     * 
     * @return are all diagonals 1 and off-diagonal zero (within Int.isEqual())
     */
    public boolean isUnit() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                int f = flmat[i][j];
                if ((f != 0 && (i != j)) || (f != 1 && (i == j))) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * is matrix symmetric.
     * 
     * @return is Int.isEqual(elem(i,j), elem(j,i))
     */
    public boolean isSymmetric() {
        for (int i = 0; i < rows - 1; i++) {
            for (int j = i + 1; j < rows; j++) {
                if (flmat[i][j] != flmat[j][i]) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * is matrix UpperTriangular.
     * 
     * @return true if all bottom triangle excluding diagona Int.isZero()
     */
    public boolean isUpperTriangular() {
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < i; j++) {
                if (flmat[i][j] != 0)
                    return false;
            }
        }
        return true;
    }
    /**
     * is matrix LowerTriangular. diagonal must also be zero
     * 
     * @return true if all bottom triangle Int.isZero()
     */
    public boolean isLowerTriangular() {
        for (int i = 0; i < rows - 1; i++) {
            for (int j = i + 1; j < rows; j++) {
                if (flmat[i][j] != 0)
                    return false;
            }
        }
        return true;
    }
    /**
     * copy upper triangle into lower triangle. alters this to make it symmetric
     * 
     * @return this as new square matrix
     */
    public IntSquareMatrix copyUpperToLower() {
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
    public IntSquareMatrix copyLowerToUpper() {
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
    public IntArray lowerTriangle() {
        int n = rows;
        IntArray triangle = new IntArray((n * (n + 1)) / 2);
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
                int t = flmat[i][j];
                flmat[i][j] = flmat[j][i];
                flmat[j][i] = t;
            }
        }
    }
}
