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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.ArrayBase.Trim;

/**
 * rectangular real number matrix class IntMatrix represents a rectangular m-x-n
 * matrix. The basic matrix algebra for non-square matrices is represented here
 * and this class is also a base for square matrices.
 * <P>
 * Read the signature of each member function carefully as some MODIFY the
 * object and some CREATE A NEW ONE. Among the reasons for this is that
 * subclassing (e.g to IntSquareMatrix) is easier with one of these forms in
 * certain cases. Note that if you modify an object, then all references to it
 * will refer to the changed object
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class IntMatrix implements EuclidConstants {
    final static Logger LOG = Logger.getLogger(IntMatrix.class);
    /**
     * number of rows
     */
    protected int rows = 0;
    /**
     * number of columns
     */
    protected int cols = 0;
    /**
     * the matrix
     */
    protected int[][] flmat = new int[0][0];
    DecimalFormat format = null;
    /**
     * construct default matrix. cols = rows = 0
     */
    public IntMatrix() {
    }
    /**
     * Create matrix with given rows and columns. A rows*cols matrix values set
     * to 0 (rows or cols < 0 defaults to 0)
     * 
     * @param r
     *            number of rows
     * @param c
     *            number of columns
     */
    public IntMatrix(int r, int c) {
        if (r < 0)
            r = 0;
        if (c < 0)
            c = 0;
        rows = r;
        cols = c;
        flmat = new int[r][c];
    }
    /**
     * Create from 1-D array. Formed by feeding in an existing 1-D array to a
     * rowsXcols matrix THE COLUMN IS THE FASTEST MOVING INDEX, that is the
     * matrix is filled as flmat(0,0), flmat(0,1) ... C-LIKE. COPIES the array
     * 
     * @param rows
     * @param cols
     * @param array
     * @exception EuclidRuntimeException
     *                size of array is not rows*cols
     */
    public IntMatrix(int rows, int cols, int[] array) throws EuclidRuntimeException {
        this(rows, cols);
        check(rows, cols, array);
        this.rows = rows;
        this.cols = cols;
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flmat[i][j] = array[count++];
            }
        }
    }
    /**
     * creates matrix with initialised values.
     * 
     * @param r
     *            rows
     * @param c
     *            columns
     * @param f
     *            value to initialize with
     */
    public IntMatrix(int r, int c, int f) {
        this(r, c);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flmat[i][j] = f;
            }
        }
    }
    /**
     * create from submatrix of another matrix. fails if lowrow > hirow, lowrow <
     * 0, etc
     * 
     * COPIES the parts of <TT>m</TT>
     * 
     * @param m
     *            the matrix to slice
     * @param lowcol
     *            lowest column index
     * @param hicol
     *            highest column index
     * @param lowrow
     *            lowest row index
     * @param hirow
     *            highest row index
     * @exception EuclidRuntimeException
     *                impossible value of hirow, hicol, lowrow, lowcol
     */
    public IntMatrix(IntMatrix m, int lowrow, int hirow, int lowcol, int hicol)
            throws EuclidRuntimeException {
        this(hirow - lowrow + 1, hicol - lowcol + 1);
        if (hirow >= m.getRows() || lowrow < 0) {
            throw new EuclidRuntimeException("bad row index: " + lowrow + S_SLASH + hirow
                    + " outside 0/" + m.getRows());
        }
        if (hicol >= m.getCols() || lowcol < 0) {
            throw new EuclidRuntimeException("bad col index: " + lowcol + S_SLASH + hicol
                    + " outside 0/" + m.getCols());
        }
        for (int i = 0, mrow = lowrow; i < rows; i++, mrow++) {
            for (int j = 0, mcol = lowcol; j < cols; j++, mcol++) {
                flmat[i][j] = m.flmat[mrow][mcol];
            }
        }
    }
    /**
     * copy constructor. copies matrix including values
     * 
     * @param m
     *            matrix to copy
     */
    public IntMatrix(IntMatrix m) {
        this(m.rows, m.cols);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(m.flmat[i], 0, flmat[i], 0, cols);
        }
    }
    
    /** create from list of rowvalues
     * 
     * @param intListList
     * @return
     */
    public static IntMatrix createByRows(List<List<Integer>> intListList) {
    	IntMatrix intMatrix = null;
    	if (intListList != null) {
    		int rows = intListList.size();
    		if (rows > 0) {
    			List<Integer> row0 =intListList.get(0);
    			int cols = row0.size();
    			if (cols > 0) {
	        		intMatrix = new IntMatrix(rows, cols);
	        		for (int i = 0; i < rows; i++) {
	        			List<Integer> rowi =intListList.get(i);
	        			if (rowi.size() == 0) {
	        				// skip
	        			} else if (rowi.size() == cols) {
		            		for (int j = 0; j < cols; j++) {
		            			intMatrix.flmat[i][j] = rowi.get(j);
		            		}
	        			}
	        		}
    			}
    		}
    	}
    	return intMatrix;
    }
    /**
     * shallow copy constructor. copies references (uses same internal array)
     * 
     * @param m
     *            matrix to copy
     */
    public void shallowCopy(IntMatrix m) {
        this.rows = m.rows;
        this.cols = m.cols;
        this.flmat = m.flmat;
    }
    /**
     * constructs an IntMatrix. intm(i,j) = (int) this(i,j), i.e.gets nearest
     * integers as matrix.
     * 
     * @return the nearest IntMatrix
     */
    public IntMatrix getIntMatrix() {
        IntMatrix im = new IntMatrix(rows, cols);
        int[][] matrix = im.getMatrix();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (int) flmat[i][j];
            }
        }
        return im;
    }
    /**
     * create from a java matrix. must be rectangular copies matrix values (i.e.
     * m can be discarded)
     * 
     * @param m
     *            natrix to copy from
     * @exception EuclidRuntimeException
     *                m has rows of different lengths
     */
    public IntMatrix(int[][] m) throws EuclidRuntimeException {
        this(m.length, m[0].length);
        for (int i = 0; i < rows; i++) {
            if (m[i].length != cols) {
                throw new EuclidRuntimeException("non-rectangular matrix cols: "
                        + cols + " row: " + i + " length: " + m[i].length);
            }
            for (int j = 0; j < cols; j++) {
                flmat[i][j] = m[i][j];
            }
        }
    }
    /** casts doubles to int.
     * 
     * @param realMatrix
     */
    public IntMatrix(RealMatrix realMatrix) {
        this(realMatrix.getRows(), realMatrix.getCols());
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flmat[i][j] = (int) realMatrix.flmat[i][j];
            }
        }
	}
	/**
     * set output format.
     * 
     * @param f
     *            the format
     */
    public void setFormat(DecimalFormat f) {
        format = f;
    }
    /**
     * get output format.
     * 
     * @return the format
     */
    public DecimalFormat getFormat() {
        return format;
    }
    /**
     * get number of rows.
     * 
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }
    /**
     * get number of columns.
     * 
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }
    /**
     * get matrix as java matrix. shallow copy - any alterations to java matrix
     * will alter this and vice versa.
     * 
     * @return matrix as java matrix
     */
    public int[][] getMatrix() {
        return flmat;
    }
    /**
     * get matrix as array.
     * 
     * @return matrix as 1-D array in C order: (m(0,0), m(0,1) ...)
     */
    public int[] getMatrixAsArray() {
        int[] temp = new int[rows * cols];
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                temp[count++] = flmat[i][j];
            }
        }
        return temp;
    }
    /**
     * tests matrices for equality.
     * 
     * uses Int.isEqual(int) for tests
     * 
     * @param m
     * @return true if all corresponding elements are equal
     */
    public boolean isEqualTo(IntMatrix m) {
        boolean ok = true;
        try {
            checkConformable(m);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (flmat[i][j] != m.flmat[i][j]) {
                        ok = false;
                        break;
                    }
                }
            }
        } catch (EuclidRuntimeException e) {
            ok = false;
        }
        return ok;
    }
    
    // check that plus, subtract is possible
    private void checkConformable(IntMatrix m) throws EuclidRuntimeException {
        if (rows != m.rows || cols != m.cols) {
            throw new EuclidRuntimeException("unequal matrices:"+rows+"*"+cols+" != "+m.rows+"*"+m.cols);
        }
    }
    // check that multiply is possible
    private void checkConformable2(IntMatrix m) throws EuclidRuntimeException {
        if (m.rows != this.cols) {
            throw new EuclidRuntimeException("unequal matrices (" + this.cols + ", "
                    + m.rows + S_RBRAK);
        }
    }
    
    private void check(int rows, int cols, int[] array) throws EuclidRuntimeException {
        if (array == null) {
            throw new EuclidRuntimeException("IntMatrix(null)");
        }
        if (array.length != rows * cols) {
            throw new EuclidRuntimeException("rows * cols (" + rows + S_STAR + cols
                    + ") != array (" + array.length + S_RBRAK);
        }
    }
    /**
     * matrix addition. adds conformable matrices giving NEW matrix. this is
     * unaltered
     * 
     * @param m2 matrix
     * @exception EuclidRuntimeException
     *                m and <TT>this</TT> are different sizes
     * @return new matrix
     */
    public IntMatrix plus(IntMatrix m2) throws EuclidRuntimeException {
        IntMatrix m = new IntMatrix(m2.rows, m2.cols);
        checkConformable(m2);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.flmat[i][j] = flmat[i][j] + m2.flmat[i][j];
            }
        }
        return m;
    }
    /**
     * matrix subtraction. subtracts conformable matrices giving NEW matrix this
     * is unaltered
     * 
     * @param m2
     * @exception EuclidRuntimeException
     *                m and <TT>this</TT> are different sizes
     * @return new matrix
     */
    public IntMatrix subtract(IntMatrix m2) throws EuclidRuntimeException {
        IntMatrix m = new IntMatrix(m2.rows, m2.cols);
        checkConformable(m2);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.flmat[i][j] = flmat[i][j] - m2.flmat[i][j];
            }
        }
        return m;
    }
    /**
     * unary minus. negate all elements of matrix; MODIFIES matrix
     */
    public void negative() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flmat[i][j] = -flmat[i][j];
            }
        }
    }
    /**
     * matrix multiplication.
     * 
     * multiplies conformable matrices to give NEW matrix. this is unaltered
     * result = 'this' * m; (order matters)
     * 
     * @param m
     * @exception EuclidRuntimeException
     *                m and <TT>this</TT> are different sizes
     * @return new matrix
     */
    public IntMatrix multiply(IntMatrix m) throws EuclidRuntimeException {
        checkConformable2(m);
        IntMatrix m1 = new IntMatrix(rows, m.cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                m1.flmat[i][j] = 0;
                for (int k = 0; k < cols; k++) {
                    m1.flmat[i][j] += flmat[i][k] * m.flmat[k][j];
                }
            }
        }
        return m1;
    }
    /**
     * matrix multiplication by a scalar. creates this(i,j) = f*this(i,j)
     * MODIFIES matrix
     * 
     * @param f
     *            scalar
     */
    public void multiplyBy(int f) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flmat[i][j] *= f;
            }
        }
    }
    /**
     * matrix multiplication. multiplies conformable matrices and stores result
     * in this matrix. this = 'this' * m;
     * 
     * @param m
     *            matrix to multiply by
     * @exception EuclidRuntimeException
     *                m and <TT>this</TT> are different sizes
     */
    public void multiplyEquals(IntMatrix m) throws EuclidRuntimeException {
        IntMatrix mm = this.multiply(m);
        this.rows = mm.rows;
        this.cols = mm.cols;
        this.flmat = new int[this.rows][];
        for (int i = 0; i < rows; i++) {
            this.flmat[i] = new int[this.cols];
            System.arraycopy(mm.flmat[i], 0, this.flmat[i], 0, this.cols);
        }
    }
    /**
     * subtract value from each row. this[i,j] = this[i,j] - d[j] modifies this
     * 
     * @param d
     *            array of ints to subtract
     */
    /*--
     public void translateByRow(int[] d) {
     checkColumns(d);
     for (int i = rows - 1; i >= 0; -- i) {
     for (int j = cols - 1; j >= 0; -- j) {
     flmat [i] [j] -= d [j];
     }
     }
     }
     --*/
    /**
     * check.
     * 
     * @param d
     * @throws EuclidRuntimeException
     */
    /* private */void checkColumns(int[] d) throws EuclidRuntimeException {
        if (d.length != cols) {
            throw new EuclidRuntimeException("array size " + d.length
                    + "!= cols length " + cols);
        }
    }
    private void checkRows(int[] d) throws EuclidRuntimeException {
        if (d.length != rows) {
            throw new EuclidRuntimeException("array size " + d.length
                    + "!= rows length " + rows);
        }
    }
    /**
     * subtract value from each colum. this[i,j] = this[i,j] - d[i] modifies
     * this
     * 
     * @param d
     *            array of ints to subtract
     * @throws EuclidRuntimeException
     */
    public void translateByColumn(int[] d) throws EuclidRuntimeException {
        checkRows(d);
        for (int i = cols - 1; i >= 0; --i) {
            for (int j = rows - 1; j >= 0; --j) {
                flmat[j][i] -= d[j];
            }
        }
    }
    /**
     * matrix multiplication of a COLUMN vector. creates new vector
     * 
     * @param f
     *            vector to multiply
     * @exception EuclidRuntimeException
     *                f.size() differs from cols
     * @return transformed array
     */
    public IntArray multiply(IntArray f) throws EuclidRuntimeException {
        if (f.size() != this.cols) {
            throw new EuclidRuntimeException("unequal matrices");
        }
        int[] temp = new int[rows];
        int[] farray = f.getArray();
        for (int i = 0; i < rows; i++) {
            temp[i] = 0;
            for (int j = 0; j < cols; j++) {
                temp[i] += this.flmat[i][j] * farray[j];
            }
        }
        IntArray ff = new IntArray(temp);
        return ff;
    }
    /**
     * divide each column of a matrix by a vector of scalars (that is mat[i][j] =
     * mat[i][j] / vect[i] - MODIFIES matrix
     * 
     * @param f
     *            array to divide by
     * @exception EuclidRuntimeException
     *                f.size() and rows differ
     */
    public void columnwiseDivide(IntArray f) throws EuclidRuntimeException {
        if (this.cols != f.size()) {
            throw new EuclidRuntimeException("unequal matrices " + this.cols + S_SLASH
                    + f.size());
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.flmat[i][j] /= f.elementAt(j);
            }
        }
    }
    /**
     * extracts a given element.
     * 
     * @param row
     * @param col
     * @throws EuclidRuntimeException
     *             bad value of row or column
     * @return the element at row,col
     */
    public int elementAt(int row, int col) throws EuclidRuntimeException {
        checkRow(row);
        checkColumn(col);
        return flmat[row][col];
    }
    /**
     * checks a row is in range.
     * 
     * @throws EuclidRuntimeException
     *             if it isn't
     */
    private void checkRow(int row) throws EuclidRuntimeException {
        if (row < 0 || row >= rows)
            throw new EuclidRuntimeException("Bad value of row: " + row + S_SLASH + rows);
    }
    /**
     * checks a col is in range.
     * 
     * @throws EuclidRuntimeException
     *             if it isn't
     */
    private void checkColumn(int col) throws EuclidRuntimeException {
        if (col < 0 || col >= cols)
            throw new EuclidRuntimeException("Bad value of col: " + col + S_SLASH + cols);
    }
    /**
     * extracts a given element.
     * 
     * @param rowcol
     *            represents row,col
     * @return the element at row,col
     * @throws EuclidRuntimeException
     */
    public int elementAt(Int2 rowcol) throws EuclidRuntimeException {
        return elementAt(rowcol.elementAt(0), rowcol.elementAt(1));
    }
    /**
     * sets a given element MODIFIES matrix
     * 
     * @param row
     * @param col
     * @param f
     * @throws EuclidRuntimeException
     */
    public void setElementAt(int row, int col, int f) throws EuclidRuntimeException {
        checkRow(row);
        checkColumn(col);
        flmat[row][col] = f;
    }
    /**
     * get value of largest element.
     * 
     * @return value of largest element
     */
    public int largestElement() {
    	if (this.rows <= 0 || this.cols <= 0) {
            throw new EuclidRuntimeException("zero size matrix");
    	}
        Int2 temp = indexOfLargestElement();
        if (temp == null) {
            throw new EuclidRuntimeException("bug; null index for largest element");
        }
        int d = this.elementAt(temp);
        return d;
    }
    /**
     * get index of largest element.
     * 
     * @return (row, col)
     */
    public Int2 indexOfLargestElement() {
        Int2 int2 = null;
        if (cols != 0 && rows != 0) {
            int f = Integer.MIN_VALUE;
            int im = 0;
            int jm = 0;
            for (int irow = 0; irow < rows; irow++) {
                for (int jcol = 0; jcol < cols; jcol++) {
                    if (f < flmat[irow][jcol]) {
                        f = flmat[irow][jcol];
                        im = irow;
                        jm = jcol;
                    }
                }
            }
            int2 = new Int2(im, jm);
        }
        return int2;
    }
    /**
     * get value of largest element in a column
     * 
     * @param jcol
     * @throws EuclidRuntimeException
     * @return the value
     */
    public int largestElementInColumn(int jcol) throws EuclidRuntimeException {
        return this.elementAt(indexOfLargestElementInColumn(jcol), jcol);
    }
    /**
     * get index of largest element in column.
     * 
     * @param jcol
     *            index
     * @return index (-1 if empty matrix)
     * @throws EuclidRuntimeException
     *             bad value of jcol
     */
    public int indexOfLargestElementInColumn(int jcol) throws EuclidRuntimeException {
        checkColumn(jcol);
        int imax = -1;
        int max = Integer.MIN_VALUE;
        for (int irow = 0; irow < rows; irow++) {
            if (max < flmat[irow][jcol]) {
                max = flmat[irow][jcol];
                imax = irow;
            }
        }
        return imax;
    }
    /**
     * get index of largest element in row.
     * 
     * @param irow
     *            index
     * @return index (-1 if empty matrix)
     * @throws EuclidRuntimeException
     *             bad value of irow
     */
    public int indexOfLargestElementInRow(int irow) throws EuclidRuntimeException {
        checkRow(irow);
        int imax = -1;
        int max = Integer.MIN_VALUE;
        for (int jcol = 0; jcol < cols; jcol++) {
            if (max < flmat[irow][jcol]) {
                max = flmat[irow][jcol];
                imax = jcol;
            }
        }
        return imax;
    }
    /**
     * get index of smallest element in column.
     * 
     * @param jcol
     *            index
     * @return index (-1 if empty matrix)
     * @throws EuclidRuntimeException
     *             bad value of jcol
     */
    public int indexOfSmallestElementInColumn(int jcol) throws EuclidRuntimeException {
        checkColumn(jcol);
        int imin = -1;
        int min = Integer.MAX_VALUE;
        for (int irow = 0; irow < rows; irow++) {
            if (min > flmat[irow][jcol]) {
                min = flmat[irow][jcol];
                imin = irow;
            }
        }
        return imin;
    }
    protected boolean checkNonEmptyMatrix() {
        return (cols > 0 && rows > 0);
    }
    /**
     * get value of largest element in a row.
     * 
     * @param irow
     * @return value (0 if no columns)
     * @throws EuclidRuntimeException
     */
    public int largestElementInRow(int irow) throws EuclidRuntimeException {
        int idx = indexOfLargestElementInRow(irow);
        if (idx < 0) {
            throw new EuclidRuntimeException("empty matrix");
        }
        return this.elementAt(irow, idx);
    }
    /**
     * get index of smallest element in row.
     * 
     * @param irow
     *            index
     * @return index (-1 if empty matrix)
     * @throws EuclidRuntimeException
     *             bad value of irow
     */
    public int indexOfSmallestElementInRow(int irow) throws EuclidRuntimeException {
        checkRow(irow);
        int imin = -1;
        int min = Integer.MAX_VALUE;
        for (int jcol = 0; jcol < cols; jcol++) {
            if (min > flmat[irow][jcol]) {
                min = flmat[irow][jcol];
                imin = jcol;
            }
        }
        return imin;
    }
    /**
     * get value of smallest element.
     * 
     * @return value
     * @throws EuclidRuntimeException
     */
    public int smallestElement() throws EuclidRuntimeException {
        Int2 temp = indexOfSmallestElement();
        return this.elementAt(temp);
    }
    /**
     * get index of smallest element.
     * 
     * @return (row,col) or null for empty matrix
     */
    public Int2 indexOfSmallestElement() {
        int f = Integer.MAX_VALUE;
        int im = -1;
        int jm = -1;
        for (int irow = 0; irow < rows; irow++) {
            for (int jcol = 0; jcol < cols; jcol++) {
                if (f > flmat[irow][jcol]) {
                    f = flmat[irow][jcol];
                    im = irow;
                    jm = jcol;
                }
            }
        }
        return (im >= 0) ? new Int2(im, jm) : null;
    }
    /**
     * get smallest element in a column.
     * 
     * @param jcol
     * @return smallest value
     * @exception EuclidRuntimeException
     *                bad value of jcol
     */
    public int smallestElementInColumn(int jcol) throws EuclidRuntimeException {
        int idx = indexOfSmallestElementInColumn(jcol);
        if (idx < 0) {
            throw new EuclidRuntimeException("empty matrix");
        }
        return this.elementAt(idx, jcol);
    }
    /**
     * get smallest element in a row.
     * 
     * @param irow
     * @return smallest value
     * @exception EuclidRuntimeException
     *                bad value of irow
     */
    public int smallestElementInRow(int irow) throws EuclidRuntimeException {
        int idx = indexOfSmallestElementInRow(irow);
        if (idx < 0) {
            throw new EuclidRuntimeException("empty matrix");
        }
        return this.elementAt(irow, idx);
    }
    /**
     * is matrix Orthogonal row-wise.
     * 
     * that is row(i) * row(j) = 0 if i not equals j.
     * 
     * @return true if orthogonal
     */
    public boolean isOrthogonal() {
        for (int i = 1; i < rows; i++) {
            IntArray rowi = extractRowData(i);
            int dot = 0;
            for (int j = i + 1; j < rows; j++) {
                IntArray rowj = extractRowData(j);
                dot = rowi.dotProduct(rowj);
                if (dot != 0)
                    return false;
            }
        }
        return true;
    }
    /**
     * get column data from matrix.
     * 
     * @param col
     *            the column
     * @return the column data (or length rows)
     * @throws EuclidRuntimeException
     */
    public IntArray extractColumnData(int col) throws EuclidRuntimeException {
        checkColumn(col);
        IntArray fa = new IntArray(rows);
        for (int i = 0; i < rows; i++) {
            fa.setElementAt(i, this.flmat[i][col]);
        }
        return fa;
    }
    /**
     * get row data from matrix.
     * 
     * @param row
     *            the column
     * @return the column data (of length cols)
     */
    public IntArray extractRowData(int row) {
        return new IntArray(flmat[row]);
    }
    /**
     * clear matrix.
     */
    public void clearMatrix() {
        for (int irow = 0; irow < rows; irow++) {
            for (int jcol = 0; jcol < cols; jcol++) {
                flmat[irow][jcol] = 0;
            }
        }
    }
    /**
     * initialise matrix to given int.
     * 
     * @param f
     */
    public void setAllElements(int f) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flmat[i][j] = f;
            }
        }
    }
    /**
     * transpose matrix - creates new Matrix
     * 
     * @return transpose
     */
    public IntMatrix getTranspose() {
        int[][] m = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[j][i] = this.flmat[i][j];
            }
        }
        return new IntMatrix(m);
    }
    /**
     * is the matrix square
     * 
     * @return is square
     */
    public boolean isSquare() {
        return (cols == rows && cols > 0);
    }
    /**
     * delete column from matrix and close up. no-op if impermissible value of
     * col
     * 
     * @param col
     *            the column
     */
    public void deleteColumn(int col) {
        if (col >= 0 && col < cols) {
            int[][] temp = new int[rows][cols - 1];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < col; j++) {
                    temp[i][j] = flmat[i][j];
                }
                for (int j = col + 1; j < cols; j++) {
                    temp[i][j - 1] = flmat[i][j];
                }
            }
            cols--;
            flmat = temp;
        }
    }
    /**
     * delete 2 or more adjacent columns (inclusive) from matrix and close up.
     * no action if impermissible value of low and high
     * 
     * @param low
     *            start column
     * @param high
     *            end column
     */
    public void deleteColumns(int low, int high) {
        high = (high > cols - 1) ? cols - 1 : high;
        low = (low < 0) ? 0 : low;
        for (int i = 0; i < rows; i++) {
            this.flmat[i] = IntArray.deleteElements(this.flmat[i], low, high);
        }
        this.cols -= (high - low + 1);
    }
    /**
     * delete row from matrix and close up.
     * 
     * @param row
     */
    public void deleteRow(int row) {
        deleteRows(row, row);
    }
    /**
     * delete 2 or more adjacent rows (inclusive) from matrix and close up. if
     * (high > rows-1 high -> rows-1; or low < 0, low -> 0
     * 
     * @param low
     *            start row
     * @param high
     *            end row
     */
    public void deleteRows(int low, int high) {
        high = (high >= rows) ? rows - 1 : high;
        low = (low < 0) ? 0 : low;
        if (low > high)
            return;
        int newrows = rows + high - low - 1;
        int temp[][] = new int[newrows][cols];
        int oldrow = 0;
        int newrow = 0;
        while (oldrow < rows) {
            if (oldrow < low || oldrow > high) {
                temp[newrow++] = flmat[oldrow];
            }
            oldrow++;
        }
        this.rows = newrows;
        flmat = temp;
    }
    /**
     * replace data in a single column.
     * 
     * @param column
     * @param f
     *            data must be of length rows
     * @throws EuclidRuntimeException
     */
    public void replaceColumnData(int column, IntArray f)
            throws EuclidRuntimeException {
        checkRows(f);
        checkColumn(column);
        int[] temp = f.getArray();
        for (int i = 0; i < rows; i++) {
            flmat[i][column] = temp[i];
        }
    }
    private void checkRows(IntArray f) throws EuclidRuntimeException {
        if (f == null || f.size() != rows) {
            throw new EuclidRuntimeException("incompatible value of array size: "
                    + f.size() + S_SLASH + rows);
        }
    }
    private void checkColumns(IntArray f) throws EuclidRuntimeException {
        if (f == null || f.size() != cols) {
            throw new EuclidRuntimeException("incompatible value of array size: "
                    + f.size() + S_SLASH + cols);
        }
    }
    private void checkColumns(IntSet is) throws EuclidRuntimeException {
        if (is == null || is.size() != cols) {
            throw new EuclidRuntimeException("incompatible value of IntSet size: "
                    + is.size() + S_SLASH + cols);
        }
    }
    private void checkColumns(IntMatrix m) throws EuclidRuntimeException {
        if (m == null || m.getCols() != cols) {
            throw new EuclidRuntimeException("incompatible value of matrix size: "
                    + m.getCols() + S_SLASH + cols);
        }
    }
    private void checkRows(IntMatrix m) throws EuclidRuntimeException {
        if (m == null || m.getRows() != rows) {
            throw new EuclidRuntimeException("incompatible value of matrix size: "
                    + m.getRows() + S_SLASH + rows);
        }
    }
    /**
     * replace data in a single column.
     * 
     * @param starting_col
     * @param f
     *            data must be of length rows
     * @throws EuclidRuntimeException
     */
    public void replaceColumnData(int starting_col, int[] f)
            throws EuclidRuntimeException {
        replaceColumnData(starting_col, new IntArray(rows, f));
    }
    /**
     * replace data in a block of columns.
     * 
     * @param start_column
     *            (gets overwritten)
     * @param m
     *            must have same row count and fit into gap
     * @throws EuclidRuntimeException
     */
    public void replaceColumnData(int start_column, IntMatrix m)
            throws EuclidRuntimeException {
        // must trap copying a matrix into itself!
        if (this == m) {
            return;
        }
        cols = this.getCols();
        int mcols = m.getCols();
        checkRows(m);
        if (start_column < 0) {
            throw new EuclidRuntimeException("cannot start at negative column: "
                    + start_column);
        }
        int end_column = start_column + mcols;
        if (end_column > cols) {
            throw new EuclidRuntimeException("too many columns to copy: "
                    + start_column + "|" + mcols + S_SLASH + cols);
        }
        copyColumns(m.flmat, start_column, mcols);
    }
    private void copyColumns(int[][] mat, int start_column, int nToCopy) {
        for (int j = 0; j < nToCopy; j++) {
            for (int i = 0; i < rows; i++) {
                this.flmat[i][start_column + j] = mat[i][j];
            }
        }
    }
    /**
     * insert a hole into the matrix and expand. result is blank space in matrix
     * 
     * @param after_col
     * @param delta_cols
     */
    public void makeSpaceForNewColumns(int after_col, int delta_cols) {
        if (after_col >= 0 && after_col <= cols && delta_cols > 0) {
            int newcols = delta_cols + cols;
            IntMatrix temp = new IntMatrix(rows, newcols);
            for (int irow = 0; irow < rows; irow++) {
                for (int jcol = 0; jcol < after_col; jcol++) {
                    temp.flmat[irow][jcol] = this.flmat[irow][jcol];
                }
                for (int jcol = after_col; jcol < cols; jcol++) {
                    temp.flmat[irow][jcol + delta_cols] = this.flmat[irow][jcol];
                }
            }
            shallowCopy(temp);
        }
    }
    /**
     * add data as column or column block into matrix and expand. column is
     * inserted after given column
     * 
     * @param after_col
     *            -1 to cols-1
     * @param f
     * @throws EuclidRuntimeException
     */
    public void insertColumnData(int after_col, IntArray f)
            throws EuclidRuntimeException {
        checkRows(f);
        if (cols == 0) {
            rows = f.size();
            flmat = new int[rows][1];
            int[] arr = f.getArray();
            cols = 1;
            for (int i = 0; i < rows; i++) {
                flmat[i][0] = arr[i];
            }
        } else {
            if (f.size() == rows) {
                makeSpaceForNewColumns(after_col + 1, 1);
                replaceColumnData(after_col + 1, f);
            }
        }
    }
    /**
     * add data as column or column block into matrix and expand.
     * 
     * @param afterCol
     *            -1 to cols-1
     * @param m
     * @throws EuclidRuntimeException
     */
    public void insertColumnData(int afterCol, IntMatrix m)
            throws EuclidRuntimeException {
        // must trap copying a matrix into itself!
        if (this == m) {
            return;
        }
        checkRows(m);
        int mcols = m.getCols();
        cols = this.getCols();
        if (afterCol < -1 || afterCol >= cols) {
            throw new EuclidRuntimeException("afterCol must be >= -1 or < cols: "
                    + afterCol);
        }
        makeSpaceForNewColumns(afterCol + 1, mcols);
        replaceColumnData(afterCol + 1, m);
    }
    /**
     * make space for new rows in matrix and expand.
     * 
     * @param after_row
     *            -1 to rows-1
     * @param delta_rows
     *            size of space
     */
    public void insertRows(int after_row, int delta_rows) {
        if (after_row >= 0 && after_row <= cols && delta_rows > 0) {
            int newrows = delta_rows + rows;
            IntMatrix temp = new IntMatrix(newrows, cols);
            for (int jcol = 0; jcol < cols; jcol++) {
                for (int irow = 0; irow < after_row; irow++) {
                    temp.flmat[irow][jcol] = this.flmat[irow][jcol];
                }
                for (int irow = after_row; irow < rows; irow++) {
                    temp.flmat[irow + delta_rows][jcol] = this.flmat[irow][jcol];
                }
            }
            shallowCopy(temp);
        }
    }
    /**
     * overwrite existing row of data.
     * 
     * @param row
     *            to replace
     * @param f
     *            row to use
     * @exception EuclidRuntimeException
     *                f.size() and cols differ
     */
    public void replaceRowData(int row, IntArray f) throws EuclidRuntimeException {
        checkColumns(f);
        int mcols = f.size();
        System.arraycopy(f.getArray(), 0, flmat[row], 0, mcols);
    }
    /**
     * overwrite existing row of data.
     * 
     * @param row
     *            to replace
     * @param f
     *            row to use
     * @exception EuclidRuntimeException
     *                f.length and cols differ
     */
    public void replaceRowData(int row, int[] f) throws EuclidRuntimeException {
        IntArray temp = new IntArray(cols, f);
        replaceRowData(row, temp);
    }
    /**
     * overwrite existing block of rows; if too big, copying is truncated
     * 
     * @param afterRow
     *            from -1 to rows-1
     * @param m
     *            data to replace with
     * @exception EuclidRuntimeException
     *                m.rows and <TT>this.rows</TT> differ
     */
    public void replaceRowData(int afterRow, IntMatrix m)
            throws EuclidRuntimeException {
        // must trap copying a matrix into itself!
        if (this == m)
            return;
        checkColumns(m);
        if (afterRow < -1) {
            throw new EuclidRuntimeException("afterRow must be >= -1 :" + afterRow);
        }
        if (!(afterRow <= (rows - m.rows))) {
            throw new EuclidRuntimeException("afterRow (" + afterRow
                    + ")must be <= rows (" + rows + ") - m.rows (" + m.rows
                    + S_RBRAK);
        }
        copyRowData(m.flmat, afterRow + 1, m.rows);
    }
    /**
     * insert 2 or more adjacent rows of data into matrix and expand
     * 
     * @param afterRow
     *            from -1 to rows-1
     * @param m
     *            data to insert
     * @exception EuclidRuntimeException
     *                m.cols and <TT>this.cols</TT>differ
     */
    public void insertRowData(int afterRow, IntMatrix m) throws EuclidRuntimeException {
        // must trap copying a matrix into itself!
        if (this == m) {
            return;
        }
        rows = this.getRows();
        int mrows = m.getRows();
        checkColumns(m);
        if (afterRow < -1) {
            throw new EuclidRuntimeException("must insert after -1 or higher");
        }
        if (afterRow >= rows) {
            throw new EuclidRuntimeException("must insert after nrows-1 or lower");
        }
        insertRows(afterRow + 1, mrows);
        copyRowData(m.flmat, afterRow + 1, mrows);
    }
    private void copyRowData(int[][] mat, int afterRow, int nrows) {
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < cols; j++) {
                this.flmat[afterRow + i][j] = mat[i][j];
            }
        }
    }
    /**
     * insert row of data into matrix and expand.
     * 
     * @param after_row
     *            from -1 to rows-1
     * @param f
     *            data to insert
     * @exception EuclidRuntimeException
     *                f.size() and <TT>this.cols</TT> differ
     */
    public void insertRowData(int after_row, IntArray f) throws EuclidRuntimeException {
        checkColumns(f);
        int mcols = f.size();
        if (after_row >= -1 && after_row <= rows && mcols == cols) {
            insertRows(after_row + 1, 1);
            replaceRowData(after_row + 1, f);
        } else {
            throw new EuclidRuntimeException("Cannot add array after  row" + after_row
                    + S_SLASH + rows + "==" + mcols + S_SLASH + cols);
        }
    }
    /**
     * append data to matrix columnwise.
     * 
     * @param f
     *            data to append
     * @exception EuclidRuntimeException
     *                f.size() and <TT>this.rows</TT> differ
     */
    public void appendColumnData(IntArray f) throws EuclidRuntimeException {
        if (cols == 0) {
            rows = f.size();
        }
        insertColumnData(cols - 1, f);
    }
    /**
     * append data to matrix columnwise.
     * 
     * @param m data to append
     * @exception EuclidRuntimeException m.rows and <TT>this.rows</TT> differ
     */
    public void appendColumnData(IntMatrix m) throws EuclidRuntimeException {
        if (cols == 0) {
            rows = m.getRows();
        }
        insertColumnData(cols - 1, m);
    }
    /**
     * append data to matrix rowwise.
     * 
     * @param f
     *            data to append
     * @exception EuclidRuntimeException
     *                m.cols and <TT>this.cols</TT> differ
     */
    public void appendRowData(IntArray f) throws EuclidRuntimeException {
        if (rows == 0) {
            cols = f.size();
        }
        insertRowData(rows - 1, f);
    }
    /**
     * append data to matrix rowwise.
     * 
     * @param m
     *            data to append
     * @exception EuclidRuntimeException
     *                m.cols and <TT>this.cols</TT> differ
     */
    public void appendRowData(IntMatrix m) throws EuclidRuntimeException {
        if (rows == 0) {
            cols = m.getCols();
        }
        insertRowData(rows - 1, m);
    }
    /**
     * replaces the data in a submatrix. starts at (low_row, low_col) and
     * extends by the dimensions for the matrix m
     * 
     * @param low_row
     *            starting row
     * @param low_col
     *            starting col
     * @param m
     *            data to append
     */
    public void replaceSubMatrixData(int low_row, int low_col, IntMatrix m) {
        if (this == m)
            return;
        if (low_row > 0 && low_col > 0) {
            int mrows = m.getRows();
            int mcols = m.getCols();
            if (low_row + mrows - 1 < rows && low_col + mcols - 1 < cols) {
                for (int i = 0; i < mrows; i++) {
                    for (int j = 0; j < mcols; j++) {
                        flmat[i + low_row - 1][j] = m.flmat[i][j];
                    }
                }
            }
        }
    }
    /**
     * reorder the columns of a matrix.
     * 
     * @param is
     *            indexes to reorder by
     * @exception EuclidRuntimeException
     *                is.size() and <TT>this.cols</TT> differ
     * @return new matrix
     */
    public IntMatrix reorderColumnsBy(IntSet is) throws EuclidRuntimeException {
        checkColumns(is);
        IntMatrix temp = new IntMatrix(rows, is.size());
        for (int i = 0; i < is.size(); i++) {
            int icol = is.elementAt(i);
            if (icol >= cols || icol < 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            IntArray coldat = this.extractColumnData(icol);
            temp.replaceColumnData(i, coldat);
        }
        return temp;
    }
    /**
     * reorder the rows of a matrix Deleting rows is allowed
     * 
     * @param is
     *            indexes to reprder by
     * @exception EuclidRuntimeException
     *                is.size() and <TT>this.rows</TT> differ
     * @return matrix
     */
    public IntMatrix reorderRowsBy(IntSet is) throws EuclidRuntimeException {
        if (is.size() != rows) {
            throw new EuclidRuntimeException("unequal matrices");
        }
        IntMatrix temp = new IntMatrix(is.size(), cols);
        for (int i = 0; i < is.size(); i++) {
            int irow = is.elementAt(i);
            if (irow >= rows || irow < 0) {
                throw new EuclidRuntimeException("irow: " + irow);
            }
            IntArray rowdat = this.extractRowData(irow);
            temp.replaceRowData(i, rowdat);
        }
        return temp;
    }
    /**
     * extract a IntMatrix submatrix from a IntMatrix
     * 
     * @param low_row
     *            starting row
     * @param high_row
     *            end row
     * @param low_col
     *            starting col
     * @param high_col
     *            end col
     * @exception EuclidRuntimeException
     *                low/high_row/col are outside range of <TT>this</TT>
     * @return matrix
     */
    public IntMatrix extractSubMatrixData(int low_row, int high_row,
            int low_col, int high_col) throws EuclidRuntimeException {
        return new IntMatrix(this, low_row, high_row, low_col, high_col);
    }
    /**
     * make an Int2_Array from columns.
     * 
     * @param col1
     * @param col2
     * @throws EuclidRuntimeException
     *             bad values of columns
     * @return 2*rows data
     */
    public Int2Array extractColumns(int col1, int col2) throws EuclidRuntimeException {
        IntArray x = this.extractColumnData(col1);
        IntArray y = this.extractColumnData(col2);
        return new Int2Array(x, y);
    }
    /**
     * make an Int2_Array from rows.
     * 
     * @param row1
     * @param row2
     * @throws EuclidRuntimeException
     *             bad values of rows
     * @return 2*cols data
     */
    public Int2Array extractRows(int row1, int row2) throws EuclidRuntimeException {
        IntArray x = this.extractRowData(row1);
        IntArray y = this.extractRowData(row2);
        return new Int2Array(x, y);
    }
    /**
     * produce a mask of those elements which fall in a range. result is matrix
     * with (1) else (0)
     * 
     * @param r
     *            the range
     * @throws EuclidRuntimeException
     *             bad values of rows
     * @return matrix with 1s where data is in range else 0
     */
    public IntMatrix elementsInRange(IntRange r) throws EuclidRuntimeException {
        IntMatrix m = new IntMatrix(rows, cols);
        for (int irow = 0; irow < rows; irow++) {
            for (int jcol = 0; jcol < cols; jcol++) {
                int elem = 0;
                if (r.includes(elementAt(irow, jcol))) {
                    elem = 1;
                }
                m.setElementAt(irow, jcol, elem);
            }
        }
        return m;
    }
    /**
     * output matrix - very crude
     * 
     * @return the string
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        // rows and cols
        if (rows > 0 && cols > 0) {
            sb.append(S_LCURLY);
            sb.append(rows);
            sb.append(S_COMMA);
            sb.append(cols);
            sb.append(S_RCURLY);
        } else {
            sb.append(S_LBRAK);
        }
        for (int i = 0; i < rows; i++) {
            sb.append(S_NEWLINE);
            sb.append(S_LBRAK);
            for (int j = 0; j < cols; j++) {
                if (j > 0) {
                    sb.append(S_COMMA);
                }
                if (format == null) {
                    sb.append(flmat[i][j]);
                } else {
                    sb.append(format.format(flmat[i][j]));
                }
            }
            sb.append(S_RBRAK);
        }
        if (rows == 0 || cols == 0) {
            sb.append(S_RBRAK);
        }
        return sb.toString();
    }
    /**
     * output xml as a CML matrix.
     * 
     * @param w
     *            the writer
     * @exception IOException
     */
    public void writeXML(Writer w) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("<matrix rows='" + rows + "' columns='" + cols + "'>");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i != 0 || j != 0)
                    sb.append(S_SPACE);
                if (format == null) {
                    sb.append(flmat[i][j]);
                } else {
                    sb.append(format.format(flmat[i][j]));
                }
            }
        }
        sb.append("</matrix>");
        w.write(sb.toString());
    }
	/**
	 * should really be in IntMatrix
	 */
	public static List<Integer> findLargestUniqueElementsInRowColumn(IntMatrix intMatrix) {
		List<Integer> intList = new ArrayList<Integer>();
		for (int jcol = 0, max = intMatrix.getCols(); jcol < max; jcol++) {
			int irow = intMatrix.indexOfLargestElementInColumn(jcol);
			int maxval = intMatrix.elementAt(irow, jcol);
			if (maxval == -1) {
				irow = -1;
			} else {
				for (int ii = irow + 1, maxrow = intMatrix.getRows(); ii < maxrow; ii++) {
					int val = intMatrix.elementAt(ii, jcol);
					if (val >= maxval) {
						irow = -1;
						break;
					}
				}
			}
			intList.add(irow);
		}
		return intList;
	}
	
	/** replace values outside limits with limits.
	 * alters this
	 * @param lowLimit
	 * @param hiLimit
	 * @return this (alters this)
	 */
	public void trim(int lowLimit, int hiLimit) {
		for (int icol = 0; icol < this.getCols(); icol++) {
			IntArray col = this.extractColumnData(icol);
			// try to get rid of negative values
			col = col.trim(Trim.BELOW, lowLimit).trim(Trim.ABOVE, hiLimit);
			this.replaceColumnData(icol, col);
		}
	}
	
	/** use this one
	 * No header
	 * 
	 * @param cooccurrenceFile
	 * @throws IOException
	 */
	public void writeCSV(File cooccurrenceFile) throws IOException {
		writeCSV(cooccurrenceFile, (String)null, (String)null);
	}
	
	/** probably a bad idea
	 * 
	 * @param cooccurrenceFile
	 * @param rowName
	 * @param colName
	 * @throws IOException
	 */
	public void writeCSV(File cooccurrenceFile, String rowName, String colName) throws IOException {
		if (cooccurrenceFile != null) {
			cooccurrenceFile.getParentFile().mkdirs();
			FileWriter w = new FileWriter(cooccurrenceFile);
			if (rowName != null && colName != null) {
				// write row/col titles and pad with commas
				w.write(rowName +","+ colName);
				for (int i = 2; i < cols; i++) {
					w.write(",");
				}
				w.write("\n");
			}
			writeCSV(w);
			w.close();
			LOG.trace("wrote: "+cooccurrenceFile);
		}
	}
	
	public void writeCSV(Writer w) throws IOException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j > 0) {
                    sb.append(",");
                }
                String field = String.valueOf(flmat[i][j]);
                sb.append(field);
            }
            sb.append("\n");
        }
        String string = sb.toString();
		w.write(string);
	}

	/** use this one
	 * No header
	 * 
	 * @param cooccurrenceFile
	 * @throws IOException
	 */
	public void writeSVG(File cooccurrenceFile) throws IOException {
		writeSVG(cooccurrenceFile, (String)null, (String)null);
	}
	
	public void writeSVG(File cooccurrenceFile, String rowName, String colName) throws IOException {
		new RuntimeException().printStackTrace();
		throw new RuntimeException("NYI");
//		FileWriter w = new FileWriter(cooccurrenceFile);
//		if (rowName != null && colName != null) {
//			// write row/col titles and pad with commas
//			w.write(rowName +","+ colName);
//			for (int i = 2; i < cols; i++) {
//				w.write(",");
//			}
//			w.write("\n");
//		}
//		writeSVG(w);
//		w.close();
//		LOG.trace("wrote: "+cooccurrenceFile);
	}

	public void writeSVG(Writer w) throws IOException {
		throw new RuntimeException("NYI");
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                if (j > 0) {
//                    sb.append(",");
//                }
//                String field = String.valueOf(flmat[i][j]);
//                sb.append(field);
//            }
//            sb.append("\n");
//        }
//        String string = sb.toString();
//		w.write(string);
	}

	/** reads output of toString() 
	 * 
	 * @param s
	 * @return
	 */
	private static Pattern HEAD = Pattern.compile("\\{(\\d+)\\,(\\d+)\\}");
	public static IntMatrix readMatrix(String s) {
		IntMatrix intMatrix = null;
		String[] ss = s.split("\n");
		if (ss == null || ss.length < 1) {
			throw new RuntimeException("empty intMatrix string");
		}
		Matcher matcher = HEAD.matcher(ss[0]);
		if (!matcher.matches()) {
			throw new RuntimeException("Bad intMatrix header");
		}
		int rows = Integer.valueOf(matcher.group(1));
		if (ss.length != rows + 1) {
			throw new RuntimeException("Bad intMatrix row count; declared " + rows + "; found " + (ss.length - 1));
		}
		int cols = Integer.valueOf(matcher.group(2));
		int[][] data = new int[rows][cols];
		for (int irow = 0; irow < rows; irow++) {
			String rowS = ss[irow + 1].trim();
			if (!rowS.startsWith("(") || !rowS.endsWith(")")) {
				throw new RuntimeException("Bad intMatrix row: "+rowS);
			}
			rowS = rowS.substring(1,  rowS.length() - 1);
			String[] rowSS = rowS.split("\\,");
			IntArray intArray = new IntArray(rowSS);
			if (intArray.size() != cols) {
				throw new RuntimeException("Bad length (" + intArray.size() + ", expected (" + cols + ") ) for intMatrix row "+irow);
			}
			data[irow] = intArray.array;
		}
		intMatrix = new IntMatrix(data);
		return intMatrix;
	}

}
