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

import static org.contentmine.eucl.euclid.EuclidConstants.EPS;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.EuclidTestUtils;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * tests RealSquareMatrix.
 * 
 * @author pmr
 * 
 */
public class RealSquareMatrixTest extends MatrixTest {

	private final static Logger LOG = Logger.getLogger(RealSquareMatrixTest.class);

	RealSquareMatrix m0;
	RealSquareMatrix m1;
	RealSquareMatrix m2;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		LOG.setLevel(Level.WARN);
		m0 = new RealSquareMatrix();
		m1 = new RealSquareMatrix(3);
		m2 = new RealSquareMatrix(3, new double[] { 11., 12., 13., 21., 22.,
				23., 31., 32., 33., });
	}

	/**
	 * equality test. true if both args not null and equal within epsilon and
	 * rows are present and equals and columns are present and equals
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, RealSquareMatrix test,
			RealSquareMatrix expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		Assert.assertNotNull("expected should have columns (" + msg + EC.S_RBRAK,
				expected.getCols());
		Assert.assertNotNull("expected should have rows (" + msg + EC.S_RBRAK,
				expected.getRows());
		Assert.assertNotNull("test should have columns (" + msg + EC.S_RBRAK, test
				.getCols());
		Assert.assertNotNull("test should have rows (" + msg + EC.S_RBRAK, test
				.getRows());
		Assert.assertEquals("rows should be equal (" + msg + EC.S_RBRAK, test
				.getRows(), expected.getRows());
		Assert.assertEquals("columns should be equal (" + msg + EC.S_RBRAK, test
				.getCols(), expected.getCols());
		DoubleTestBase.assertEquals(msg, test.getMatrixAsArray(), expected
				.getMatrixAsArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param rows
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, int rows, double[] test,
			RealSquareMatrix expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + EC.S_RBRAK,
				expected);
		Assert.assertEquals("rows should be equal (" + msg + EC.S_RBRAK, rows,
				expected.getRows());
		DoubleTestBase.assertEquals(msg, test, expected.getMatrixAsArray(),
				epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.isOrthogonal()'
	 */
	@Test
	public void testIsOrthogonal() {
		Assert.assertFalse("isOrthogonal", m2.isOrthogonal());
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 0, 1, 0,
				-1, 0, 0, 0, 0, 1, });
		Assert.assertTrue("isOrthogonal", m.isOrthogonal());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix()'
	 */
	@Test
	public void testRealSquareMatrix() {
		Assert.assertEquals("real square matrix", 0, m0.getRows());
		Assert.assertEquals("real square matrix", 0, m0.getCols());
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix(int)'
	 */
	@Test
	public void testRealSquareMatrixInt() {
		Assert.assertEquals("real square matrix", 3, m1.getRows());
		Assert.assertEquals("real square matrix", 3, m1.getCols());
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.outerProduct(RealArray)'
	 */
	@Test
	public void testOuterProduct() {
		RealArray ra = new RealArray(3, new double[] { 1, 2, 3 });
		RealSquareMatrix rsm = RealSquareMatrix.outerProduct(ra);
		RealMatrix rm = null;
		rm = new RealMatrix(3, 3, new double[] { 1.0, 2.0, 3.0, 2.0, 4.0, 6.0,
				3.0, 6.0, 9.0, });
		MatrixTest.assertEquals("outer product", rm, (RealMatrix) rsm, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.diagonal(RealArray)'
	 */
	@Test
	public void testDiagonal() {
		RealArray ra = new RealArray(3, new double[] { 1, 2, 3 });
		RealMatrix rsm = RealSquareMatrix.diagonal(ra);
		RealMatrix rm = null;
		rm = new RealMatrix(3, 3, new double[] { 1, 0, 0, 0, 2, 0, 0, 0, 3, });
		MatrixTest.assertEquals("diagonal", rm, (RealMatrix) rsm, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix(int,
	 * double[])'
	 */
	@Test
	public void testRealSquareMatrixIntDoubleArray() {
		RealMatrix rm = new RealMatrix(3, 3, new double[] { 1.0, 2.0, 3.0, 2.0,
				4.0, 6.0, 3.0, 6.0, 9.0, });
		RealSquareMatrix rsm = new RealSquareMatrix(3, new double[] { 1.0, 2.0,
				3.0, 2.0, 4.0, 6.0, 3.0, 6.0, 9.0, });
		MatrixTest.assertEquals("int double[]", rm, (RealMatrix) rsm, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix(int,
	 * double)'
	 */
	@Test
	public void testRealSquareMatrixIntDouble() {
		RealMatrix rm = new RealMatrix(3, 3, 10.);
		RealSquareMatrix rsm = new RealSquareMatrix(3, 10.);
		MatrixTest.assertEquals("int double", rm, (RealMatrix) rsm, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix(RealMatrix, int,
	 * int, int)'
	 */
	@Test
	public void testRealSquareMatrixRealMatrixIntIntInt() {
		RealMatrix rm = new RealMatrix(3, 4, new double[] { 11., 12., 13., 14.,
				21., 22., 23., 24., 31., 32., 33., 34. });
		RealSquareMatrix rsm = new RealSquareMatrix(rm, 1, 1, 2);
		RealMatrix rm1 = new RealMatrix(2, 2, new double[] { 22., 23., 32.,
				33., });
		MatrixTest.assertEquals("rsm int int int", rm1, (RealMatrix) rsm, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix(RealSquareMatrix)'
	 */
	@Test
	public void testRealSquareMatrixRealSquareMatrix() {
		RealSquareMatrix rsm = new RealSquareMatrix(m2);
		MatrixTest.assertEquals("copy", m2, rsm, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix(RealMatrix)'
	 */
	@Test
	public void testRealSquareMatrixRealMatrix() {
		RealMatrix rm = new RealMatrix(2, 2,
				new double[] { 22., 23., 32., 33., });
		RealSquareMatrix rsm = new RealSquareMatrix(rm);
		MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.isEqualTo(RealSquareMatrix)'
	 */
	@Test
	public void testIsEqualToRealSquareMatrix() {
		RealSquareMatrix rsm = new RealSquareMatrix(m2);
		Assert.assertTrue("isEqualTo", m2.isEqualTo(rsm));
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.RealSquareMatrix(double[][])'
	 */
	@Test
	public void testRealSquareMatrixDoubleArrayArray() {
		double[][] mat = new double[][] { new double[] { 11., 12., 13. },
				new double[] { 21., 22., 23. }, new double[] { 31., 32., 33. }, };
		RealSquareMatrix rsm = new RealSquareMatrix(mat);
		RealMatrix rm = new RealMatrix(3, 3, new double[] { 11., 12., 13., 21.,
				22., 23., 31., 32., 33., });
		MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.plus(RealSquareMatrix)'
	 */
	@Test
	public void testPlusRealSquareMatrix() {
		RealSquareMatrix rsm = m2.plus(m2);
		RealMatrix rm = new RealMatrix(3, 3, new double[] { 22., 24., 26., 42.,
				44., 46., 62., 64., 66., });
		MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.subtract(RealSquareMatrix)'
	 */
	@Test
	public void testSubtractRealSquareMatrix() {
		RealSquareMatrix rsm = m2.plus(m2);
		RealSquareMatrix rsm1 = m2.subtract(rsm);
		RealMatrix rm = new RealMatrix(3, 3, new double[] { -11., -12., -13.,
				-21., -22., -23., -31., -32., -33., });
		MatrixTest.assertEquals("real matrix", rm, rsm1, EPS);

	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.multiply(RealSquareMatrix)'
	 */
	@Test
	public void testMultiplyRealSquareMatrix() {
		RealSquareMatrix rsm = m2.multiply(m2);
		RealMatrix rm = new RealMatrix(3, 3, new double[] { 776.0, 812.0,
				848.0, 1406.0, 1472.0, 1538.0, 2036.0, 2132.0, 2228.0, });
		MatrixTest.assertEquals("real matrix", rm, rsm, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.determinant()'
	 */
	@Test
	public void testDeterminant() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 1., 1., 1.,
				2., 3., 4., 3., 4., 7. });
		double d = m.determinant();
		Assert.assertEquals("determinant", 2., d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.trace()'
	 */
	@Test
	public void testTrace() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 1., 1., 1.,
				2., 3., 4., 3., 4., 7. });
		double d = m.trace();
		Assert.assertEquals("trace", 11., d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.isUnit()'
	 */
	@Test
	public void testIsUnit() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 1., 0., 0.,
				0., 1., 0., 0., 0., 1. });
		Assert.assertTrue("unit", m.isUnit());
		m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4., 3.,
				4., 7. });
		Assert.assertFalse("unit", m.isUnit());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.isSymmetric()'
	 */
	@Test
	public void testIsSymmetric() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 1., 0., 3.,
				0., 1., 0., 3., 0., 1. });
		Assert.assertTrue("unit", m.isSymmetric());
		m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4., 3.,
				4., 7. });
		Assert.assertFalse("unit", m.isSymmetric());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.orthonormalize()'
	 */
	@Test
	public void testOrthonormalize() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 1., 1., 1.,
				2., 3., 4., 3., 4., 7. });
		Assert.assertFalse("orthonormal", m.isOrthonormal());
		m.orthonormalize();
		Assert.assertTrue("orthonormal", m.isOrthonormal());
		RealSquareMatrix mm = new RealSquareMatrix(3, new double[] {
				0.5773502691896258, 0.5773502691896258, 0.5773502691896258,
				-0.7071067811865477, 0.0, 0.7071067811865476,
				0.40824829046386296, -0.816496580927726, 0.40824829046386313, });
		MatrixTest.assertEquals("orthonormal", mm, m, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.isUpperTriangular()'
	 */
	@Test
	public void testIsUpperTriangular() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 0., 2., 3.,
				0., 0., 2., 0., 0., 0. });
		Assert.assertTrue("upper triangular", m.isUpperTriangular());
		m = new RealSquareMatrix(3, new double[] { 1., 2., 3., 0., 1., 2., 0.,
				0., 1. });
		Assert.assertTrue("upper triangular", m.isUpperTriangular());
		m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4., 3.,
				4., 7. });
		Assert.assertFalse("upper triangular false", m.isUpperTriangular());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.isLowerTriangular()'
	 */
	@Test
	public void testIsLowerTriangular() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 0., 0., 0.,
				2., 0., 0., 3., 2., 0. });
		Assert.assertTrue("lower triangular", m.isLowerTriangular());
		m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 2., 1., 0., 3.,
				2., 1. });
		Assert.assertTrue("lower triangular", m.isLowerTriangular());
		m = new RealSquareMatrix(3, new double[] { 1., 1., 1., 2., 3., 4., 3.,
				4., 7. });
		Assert.assertFalse("lower triangular false", m.isLowerTriangular());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.isImproperRotation()'
	 */
	@Test
	public void testIsImproperRotation() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 1., 0., 0.,
				0., 1., 0., 0., 0., -1. });
		Assert.assertTrue("isImproper", m.isImproperRotation());
		m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 0., -1., 0., 0.,
				0., -1. });
		Assert.assertFalse("isImproper", m.isImproperRotation());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.isUnitary()'
	 */
	@Test
	public void testIsUnitary() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 1., 0., 0.,
				0., 1., 0., 0., 0., -1. });
		Assert.assertTrue("isUnitary", m.isUnitary());
		m = new RealSquareMatrix(3, new double[] { 1., 0., 0., 0., -1., 0., 0.,
				0., -1. });
		Assert.assertTrue("isUnitary", m.isUnitary());
		m = new RealSquareMatrix(3, new double[] { 1., 0., 1., 0., -1., 0., 0.,
				0., -1. });
		Assert.assertFalse("isUnitary", m.isUnitary());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.copyUpperToLower()'
	 */
	@Test
	public void testCopyUpperToLower() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 6., 7., 8.,
				2., 5., 4., 3., 2., 9. });
		m.copyUpperToLower();
		RealSquareMatrix mm = new RealSquareMatrix(3, new double[] { 6., 7.,
				8., 7., 5., 4., 8., 4., 9. });
		MatrixTest.assertEquals("copy upper", mm, m, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.copyLowerToUpper()'
	 */
	@Test
	public void testCopyLowerToUpper() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 6., 7., 8.,
				2., 5., 4., 3., 2., 9. });
		m.copyLowerToUpper();
		RealSquareMatrix mm = new RealSquareMatrix(3, new double[] { 6., 2.,
				3., 2., 5., 2., 3., 2., 9. });
		MatrixTest.assertEquals("copy upper", mm, m, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.lowerTriangle()'
	 */
	@Test
	public void testLowerTriangle() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 6., 7., 8.,
				2., 5., 4., 3., 2., 9. });
		RealArray ra = m.lowerTriangle();
		RealArrayTest.assertEquals("lower triangle", new double[] { 6.0, 2.0,
				5.0, 3.0, 2.0, 9.0 }, ra, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.transpose()'
	 */
	@Test
	public void testTranspose() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 6., 7., 8.,
				2., 5., 4., 3., 1., 9. });
		m.transpose();
		RealSquareMatrix mm = new RealSquareMatrix(3, new double[] { 6., 2.,
				3., 7., 5., 1., 8., 4., 9. });
		MatrixTest.assertEquals("transpose", mm, m, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.orthogonalise()'
	 */
	@Test
	public void testOrthogonalise() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 6., 7., 8.,
				7., 5., 4., 8., 4., 9. });
		m.orthogonalise();
		Assert.assertTrue("orthogonalise", m.isOrthogonal());
		RealSquareMatrix mm = new RealSquareMatrix(3, new double[] { 6.0, 7.0,
				8.0, 7.7316819236624434, -0.35776420212319654,
				-5.485717765889034, 3.8939506336049337, -10.383868356279821,
				6.1654218365411415, });
		MatrixTest.assertEquals("orthogonalise", mm, m, 0.000000000001);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealSquareMatrix.getCrystallographicOrthogonalisation(double[
	 * ] , double[])'
	 */
	@Test
	public void testGetCrystallographicOrthogonalisation() {

		double[] len = { 10., 11., 12. };
		double[] ang = { 80., 90., 100. }; // degrees!
		RealSquareMatrix m = RealSquareMatrix
				.getCrystallographicOrthogonalisation(len, ang);
		RealSquareMatrix mm = new RealSquareMatrix(3, new double[] {
				9.843316493307713, 0.0, 0.0, -1.7632698070846495,
				10.832885283134289, 0.0, 0.0, 1.9101299543362344, 12.0 });
		MatrixTest.assertEquals("orthogonalise", mm, m, 0.000000000001);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealSquareMatrix.getInverse()'
	 */
	@Test
	public void testGetInverse() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 6., 7., 8.,
				2., 5., 4., 1., 3., 9. });
		RealSquareMatrix inv = m.getInverse();
		RealSquareMatrix mm = new RealSquareMatrix(3, new double[] {
				0.3055555555555556, -0.36111111111111116, -0.11111111111111108,
				-0.12962962962962962, 0.42592592592592593,
				-0.07407407407407408, 0.009259259259259259,
				-0.10185185185185185, 0.14814814814814814, });
		MatrixTest.assertEquals("inverse", mm, inv, 0.000000000001);
	}
	
	@Test
	public void calculateEigenvalues() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 
				1., 2., 3.,
				2., 1., 8.,
				3., 8., 7.
		});
		RealArray realArray = m.calculateEigenvalues();
		double[] expected = new double[]{13.57729611363183,-0.03241110263496161,-4.54488501099687};
		EuclidTestUtils.testEquals("inverse", expected, realArray.getArray(), 0.0000001);
	}
	
	@Test
	public void calculateInverse() {
		RealSquareMatrix m = new RealSquareMatrix(3, new double[] { 
				1., 2., 3.,
				2., 1., 8.,
				3., 8., 7.
		});
		RealSquareMatrix expected = new RealSquareMatrix(3, new double[] { 
				-28.5, 5.0, 6.5,
				5.0, -1.0, -1.0,
				6.5, -1.0, -1.5
				});
		RealSquareMatrix rsm = m.calculateInverse();
		EuclidTestUtils.testEquals("inverse", expected.getMatrix(), rsm.getMatrix(), 0.0000001);
		RealSquareMatrix one = m.multiply(rsm);
		Assert.assertNotNull(one);
	}
	
	

}
