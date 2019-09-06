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

package org.contentmine.eucl.euclid.euclid;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.IntSquareMatrix;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test IntSquareMatrix.
 *
 * @author pmr
 *
 */
public class IntSquareMatrixTest extends MatrixTest {

    static Logger LOG = Logger.getLogger(IntSquareMatrixTest.class);

    IntSquareMatrix m0;

    IntSquareMatrix m1;

    IntSquareMatrix m2;

    /**
     * setup.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        LOG.setLevel(Level.WARN);
        m0 = new IntSquareMatrix();
        m1 = new IntSquareMatrix(3);
        m2 = new IntSquareMatrix(3, new int[] { 11, 12, 13, 21, 22, 23, 31, 32,
                33, });
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.isOrthogonal()'
     */
    @Test
    public void testIsOrthogonal() {
        Assert.assertFalse("isOrthogonal", m2.isOrthogonal());
        IntSquareMatrix m = new IntSquareMatrix(3,
                    new int[] { 0, 1, 0, -1, 0, 0, 0, 0, 1, });
        Assert.assertTrue("isOrthogonal", m.isOrthogonal());
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix()'
     */
    @Test
    public void testIntSquareMatrix() {
        Assert.assertEquals("real square matrix", 0, m0.getRows());
        Assert.assertEquals("real square matrix", 0, m0.getCols());
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix(int)'
     */
    @Test
    public void testIntSquareMatrixInt() {
        Assert.assertEquals("real square matrix", 3, m1.getRows());
        Assert.assertEquals("real square matrix", 3, m1.getCols());
    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.outerProduct(IntArray)'
     */
    @Test
    public void testOuterProduct() {
        IntArray ra = new IntArray(3, new int[] { 1, 2, 3 });
        IntSquareMatrix rsm = IntSquareMatrix.outerProduct(ra);
        IntMatrix rm = new IntMatrix(3, 3, new int[] { 1, 2, 3, 2, 4, 6, 3, 6, 9, });
        MatrixTest.assertEquals("outer product", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.diagonal(IntArray)'
     */
    @Test
    public void testDiagonal() {
        IntArray ra = new IntArray(3, new int[] { 1, 2, 3 });
        IntMatrix rsm = IntSquareMatrix.diagonal(ra);
        IntMatrix rm = new IntMatrix(3, 3, new int[] { 1, 0, 0, 0, 2, 0, 0, 0, 3, });
        MatrixTest.assertEquals("diagonal", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix(int,
     * int[])'
     */
    @Test
    public void testIntSquareMatrixIntIntegerArray() {
        IntMatrix rm = new IntMatrix(3, 3, new int[] { 1, 2, 3, 2, 4, 6, 3, 6, 9, });
        IntSquareMatrix rsm = new IntSquareMatrix(3,
            new int[] { 1, 2, 3, 2, 4, 6, 3, 6, 9, });
        MatrixTest.assertEquals("int int[]", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix(int,
     * int)'
     */
    @Test
    public void testIntSquareMatrixIntInteger() {
        IntMatrix rm = new IntMatrix(3, 3, 10);
        IntSquareMatrix rsm = new IntSquareMatrix(3, 10);
        MatrixTest.assertEquals("int int", rm, (IntMatrix) rsm);
    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix(IntMatrix, int, int,
     * int)'
     */
    @Test
    public void testIntSquareMatrixIntMatrixIntIntInt() {
        IntMatrix rm = new IntMatrix(3, 4, new int[] { 11, 12, 13, 14, 21, 22, 23,
                    24, 31, 32, 33, 34 });
        IntSquareMatrix rsm = new IntSquareMatrix(rm, 1, 1, 2);
        IntMatrix rm1 = new IntMatrix(2, 2, new int[] { 22, 23, 32, 33, });
        MatrixTest.assertEquals("rsm int int int", rm1, (IntMatrix) rsm);
    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix(IntSquareMatrix)'
     */
    @Test
    public void testIntSquareMatrixIntSquareMatrix() {
        IntSquareMatrix rsm = new IntSquareMatrix(m2);
        MatrixTest.assertEquals("copy", m2, rsm);
    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix(IntMatrix)'
     */
    @Test
    public void testIntSquareMatrixIntMatrix() {
        IntMatrix rm = new IntMatrix(2, 2, new int[] { 22, 23, 32, 33, });
        IntSquareMatrix rsm = new IntSquareMatrix(rm);
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.IntSquareMatrix(int[][])'
     */
    @Test
    public void testIntSquareMatrixIntegerArrayArray() {
        int[][] mat = new int[][] { new int[] { 11, 12, 13 },
                new int[] { 21, 22, 23 }, new int[] { 31, 32, 33 }, };
        IntSquareMatrix rsm = new IntSquareMatrix(mat);
        IntMatrix rm = new IntMatrix(3, 3, new int[] { 11, 12, 13, 21, 22, 23, 31,
                    32, 33, });
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.isEqualTo(IntSquareMatrix)'
     */
    @Test
    public void testIsEqualToIntSquareMatrix() {
        IntSquareMatrix rsm = new IntSquareMatrix(m2);
        Assert.assertTrue("isEqualTo", m2.isEqualTo(rsm));
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.plus(IntSquareMatrix)'
     */
    @Test
    public void testPlusIntSquareMatrix() {
        IntSquareMatrix rsm = m2.plus(m2);
        IntMatrix rm = new IntMatrix(3, 3, new int[] { 22, 24, 26, 42, 44, 46, 62,
                    64, 66, });
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.subtract(IntSquareMatrix)'
     */
    @Test
    public void testSubtractIntSquareMatrix() {
        IntSquareMatrix rsm = m2.plus(m2);
        IntSquareMatrix rsm1 = m2.subtract(rsm);
        IntMatrix rm = new IntMatrix(3, 3, new int[] { -11, -12, -13, -21, -22, -23,
                    -31, -32, -33, });
        MatrixTest.assertEquals("real matrix", rm, rsm1);

    }

    /**
     * Test method for
     * 'org.contentmine.eucl.euclid.IntSquareMatrix.multiply(IntSquareMatrix)'
     */
    @Test
    public void testMultiplyIntSquareMatrix() {
        IntSquareMatrix rsm = m2.multiply(m2);
        IntMatrix rm = new IntMatrix(3, 3, new int[] { 776, 812, 848, 1406, 1472,
                    1538, 2036, 2132, 2228, });
        MatrixTest.assertEquals("real matrix", rm, rsm);
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.isUnit()'
     */
    @Test
    public void testIsUnit() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 });
        Assert.assertTrue("unit", m.isUnit());
        m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        Assert.assertFalse("unit", m.isUnit());
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.isSymmetric()'
     */
    @Test
    public void testIsSymmetric() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 1, 0, 3, 0, 1, 0, 3, 0, 1 });
        Assert.assertTrue("unit", m.isSymmetric());
        m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        Assert.assertFalse("unit", m.isSymmetric());
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.isUpperTriangular()'
     */
    @Test
    public void testIsUpperTriangular() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 0, 2, 3, 0, 0, 2, 0, 0, 0 });
        Assert.assertTrue("upper triangular", m.isUpperTriangular());
        m = new IntSquareMatrix(3, new int[] { 1, 2, 3, 0, 1, 2, 0, 0, 1 });
        Assert.assertTrue("upper triangular", m.isUpperTriangular());
        m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        Assert.assertFalse("upper triangular false", m.isUpperTriangular());
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.isLowerTriangular()'
     */
    @Test
    public void testIsLowerTriangular() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 0, 0, 0, 2, 0, 0, 3, 2, 0 });
        Assert.assertTrue("lower triangular", m.isLowerTriangular());
        m = new IntSquareMatrix(3, new int[] { 1, 0, 0, 2, 1, 0, 3, 2, 1 });
        Assert.assertTrue("lower triangular", m.isLowerTriangular());
        m = new IntSquareMatrix(3, new int[] { 1, 1, 1, 2, 3, 4, 3, 4, 7 });
        Assert.assertFalse("lower triangular false", m.isLowerTriangular());
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.copyUpperToLower()'
     */
    @Test
    public void testCopyUpperToLower() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 2, 9 });
        m.copyUpperToLower();
        IntSquareMatrix mm = new IntSquareMatrix(3, new int[] { 6, 7, 8, 7, 5, 4, 8, 4, 9 });
        MatrixTest.assertEquals("copy upper", mm, m);
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.copyLowerToUpper()'
     */
    @Test
    public void testCopyLowerToUpper() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 2, 9 });
        m.copyLowerToUpper();
        IntSquareMatrix mm = new IntSquareMatrix(3, new int[] { 6, 2, 3, 2, 5, 2, 3, 2, 9 });
        MatrixTest.assertEquals("copy upper", mm, m);
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.lowerTriangle()'
     */
    @Test
    public void testLowerTriangle() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 2, 9 });
        IntArray ra = m.lowerTriangle();
        String s = Int.testEquals((new int[] { 6, 2, 5, 3, 2, 9 }), ra.getArray());
		if (s != null) {
			Assert.fail("lower triangle" + "; " + s);
		}
    }

    /**
     * Test method for 'org.contentmine.eucl.euclid.IntSquareMatrix.transpose()'
     */
    @Test
    public void testTranspose() {
        IntSquareMatrix m = new IntSquareMatrix(3, new int[] { 6, 7, 8, 2, 5, 4, 3, 1, 9 });
        m.transpose();
        IntSquareMatrix mm = new IntSquareMatrix(3, new int[] { 6, 2, 3, 7, 5, 1, 8, 4, 9 });
        MatrixTest.assertEquals("transpose", mm, m);
    }


}
