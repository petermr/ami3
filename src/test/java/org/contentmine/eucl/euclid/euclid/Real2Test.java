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
import static org.contentmine.eucl.euclid.EuclidConstants.S_RBRAK;

import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * tests real2
 * 
 * @author pmr
 * 
 */
public class Real2Test {

	Real2 r0;

	Real2 r11;

	Real2 r12;

	List<Real2> real2List;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		r0 = new Real2();
		r11 = new Real2(1.0, 1.0);
		r12 = new Real2(1.0, 2.0);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	
	public static void assertEquals(String msg, Real2 test, Real2 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test.getXY(), expected.getXY(),
				epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 2
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test, Real2 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertEquals("must be of length 2", 2, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test, expected.getXY(), epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.Real2()'
	 */
	@Test
	public void testReal2() {
		Assert.assertEquals("double2", "(0.0,0.0)", r0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.Real2(double, double)'
	 */
	@Test
	public void testReal2RealReal() {
		Assert.assertEquals("double2", "(1.0,2.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.Real2(Real2)'
	 */
	@Test
	public void testReal2Real2() {
		Real2 ii = new Real2(r12);
		Assert.assertEquals("double2", "(1.0,2.0)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.swap()'
	 */
	@Test
	public void testSwap() {
		r12.swap();
		Assert.assertEquals("double2", "(2.0,1.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.sortAscending()'
	 */
	@Test
	public void testSortAscending() {
		r12.sortAscending();
		Assert.assertEquals("double2", "(1.0,2.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.sortDescending()'
	 */
	@Test
	public void testSortDescending() {
		r12.sortDescending();
		Assert.assertEquals("double2", "(2.0,1.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.clear()'
	 */
	@Test
	public void testClear() {
		r12.clear();
		Assert.assertEquals("double2", "(0.0,0.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.setX(double)'
	 */
	@Test
	public void testSetX() {
		r12.setX(3);
		Assert.assertEquals("double2", "(3.0,2.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.setY(double)'
	 */
	@Test
	public void testSetY() {
		r12.setY(3);
		Assert.assertEquals("double2", "(1.0,3.0)", r12.toString());
	}

//	/**
//	 * Test method for 'org.contentmine.eucl.euclid.Real2.isEqualTo(Real2)'
//	 */
//	@Test
//	public void testIsEqualTo() {
//		Assert.assertTrue("equals", r12.isEqualTo(r12));
//		Assert.assertFalse("equals", r11.isEqualTo(r12));
//	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.plus(Real2)'
	 */
	@Test
	public void testPlus() {
		Real2 ii = r12.plus(r11);
		Assert.assertEquals("plus", "(2.0,3.0)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.subtract(Real2)'
	 */
	@Test
	public void testSubtract() {
		Real2 ii = r12.subtract(r11);
		Assert.assertEquals("subtract", "(0.0,1.0)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.negative()'
	 */
	@Test
	public void testNegative() {
		r12.negative();
		Assert.assertEquals("negative", "(-1.0,-2.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.multiplyBy(double)'
	 */
	@Test
	public void testMultiplyBy() {
		Real2 ii = r12.multiplyBy(3);
		Assert.assertEquals("multiply", "(3.0,6.0)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getX()'
	 */
	@Test
	public void testGetX() {
		Assert.assertEquals("getX", 1.0, r12.getX(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getY()'
	 */
	@Test
	public void testGetY() {
		Assert.assertEquals("getY", 2.0, r12.getY(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.elementAt(double)'
	 */
	@Test
	public void testElementAt() {
		Assert.assertEquals("elementAt", 1.0, r12.elementAt(0), EPS);
		Assert.assertEquals("elementAt", 2.0, r12.elementAt(1), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getMidPodouble(Real2)'
	 */
	@Test
	public void testGetMidPoint() {
		Real2 m = r12.getMidPoint(new Real2(3.0, 4.0));
		Assert.assertEquals("mid point", "(2.0,3.0)", m.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.dotProduct(Real2)'
	 */
	@Test
	public void testDotProduct() {
		double i = r12.dotProduct(new Real2(3.0, 4.0));
		Assert.assertEquals("dot", 11, i, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.toString()'
	 */
	@Test
	public void testToString() {
		Assert.assertEquals("toString", "(1.0,2.0)", r12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getLength()'
	 */
	@Test
	public void testGetLength() {
		Assert.assertEquals("length", Math.sqrt(5.), r12.getLength(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getDistance(Real2)'
	 */
	@Test
	public void testGetDistance() {
		Assert.assertEquals("distance", Math.sqrt(1.), r12.getDistance(r11),
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getSquaredDistance(Real2)'
	 */
	@Test
	public void testGetSquaredDistance() {
		Assert.assertEquals("squared distance", Math.sqrt(1.), r12
				.getSquaredDistance(r11), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getUnitVector()'
	 */
	@Test
	public void testGetUnitVector() {
		Real2 unit = r12.getUnitVector();
		Assert.assertEquals("vector", Math.sqrt(1. / 5.), unit.getX(), EPS);
		Assert.assertEquals("vector", Math.sqrt(4. / 5.), unit.getY(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getAngle(Real2, Real2, Real2)'
	 */
	@Test
	public void testGetAngleReal2Real2Real2() {
		Real2 p1 = new Real2(1. + Math.cos(Math.PI / 3), 2. - Math
				.sin(Math.PI / 3));
		Real2 p2 = new Real2(1., 2.);
		Real2 p3 = new Real2(1. + Math.cos(Math.PI / 3), 2. + Math
				.sin(Math.PI / 3));
		Angle a = Real2.getAngle(p1, p2, p3);
		Assert.assertEquals("angle", 2. * Math.PI / 3, a.getAngle(), EPS);
		
		p1 = new Real2(0., 1.);
		p3 = new Real2(1., 0.);
		p2 = new Real2(0., 0.);
		a = Real2.getAngle(p1, p2, p3);
		Assert.assertEquals("angle", - Math.PI / 2., a.getAngle(), EPS);
		
		p1 = new Real2(0., 1.);
		p3 = new Real2(1., 1.);
		p2 = new Real2(0., 0.);
		a = Real2.getAngle(p1, p2, p3);
		Assert.assertEquals("angle", - Math.PI / 4., a.getAngle(), EPS);
		
		p1 = new Real2(0., 1.);
		p3 = new Real2(Math.sqrt(3.)/2., 0.5);
		p2 = new Real2(0., 0.);
		a = Real2.getAngle(p1, p2, p3);
		Assert.assertEquals("angle", - Math.PI / 3., a.getAngle(), EPS);
		
		p1 = new Real2(0., 1.);
		p3 = new Real2(0.5, Math.sqrt(3.)/2.);
		p2 = new Real2(0., 0.);
		a = Real2.getAngle(p1, p2, p3);
		Assert.assertEquals("angle", - Math.PI / 6., a.getAngle(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.transformBy(Transform2)'
	 */
	@Test
	public void testTransformBy() {
		Transform2 t2 = new Transform2(new Angle(Math.PI / 2));
		r12.transformBy(t2);
		Assert.assertEquals("transform", 2.0, r12.getX(), EPS);
		Assert.assertEquals("transform", -1.0, r12.getY(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getTransformed(Transform2)'
	 */
	@Test
	public void testGetTransformed() {
		Transform2 t2 = new Transform2(new Angle(Math.PI / 2));
		Real2 r2 = r12.getTransformed(t2);
		Assert.assertEquals("transform", 2.0, r2.getX(), EPS);
		Assert.assertEquals("transform", -1.0, r2.getY(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.addPolygonOnLine(Real2, Real2,
	 * int, int, Real2)'
	 */
	// not worth developing
	// @Test
	// public void testAddPolygonOnLine() {
	// //T O D O
	//
	// }
	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getAngle()'
	 */
	@Test
	public void testGetAngle() {
		double d = r12.getAngle();
		Assert.assertEquals("angle", Math.atan2(2., 1.), d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.makePoint(double, double)'
	 */
	@Test
	public void testMakePoint() {
		Real2 real = r12.makePoint(10., Math.PI / 3);
		Assert.assertEquals("make point", 1. + 10 * Math.cos(Math.PI / 3), real
				.getX(), EPS);
		Assert.assertEquals("make point", 2. + 10 * Math.sin(Math.PI / 3), real
				.getY(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getCentroid(List<Real2>)'
	 */
	@Test
	public void testGetCentroid() {
		real2List = new ArrayList<Real2>();
		real2List.add(new Real2(1.0, 2.0));
		real2List.add(new Real2(-2.0, 1.0));
		real2List.add(new Real2(4.0, 3.0));
		Real2 c = Real2.getCentroid(real2List);
		Assert.assertEquals("centroid", 1., c.getX(), EPS);
		Assert.assertEquals("centroid", 2., c.getY(), EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Real2.getSerialOfNearestPoint(List<Real2>, Real2)'
	 */
	@Test
	public void testGetSerialOfNearestPoint() {
		real2List = new ArrayList<Real2>();
		real2List.add(new Real2(1.0, 2.0));
		real2List.add(new Real2(-2.0, 1.0));
		real2List.add(new Real2(4.0, 3.0));
		Real2 p = new Real2(-1.6, 0.8);
		int i = Real2.getSerialOfNearestPoint(real2List, p);
		Assert.assertEquals("nearest", 1, i);
		// equidistant from 0 and 2, will choose the first
		p = new Real2(2.5, 2.5);
		i = Real2.getSerialOfNearestPoint(real2List, p);
		Assert.assertEquals("nearest", 0, i);
		// now choose the nearest
		p = new Real2(2.5, 2.50001);
		i = Real2.getSerialOfNearestPoint(real2List, p);
		Assert.assertEquals("nearest", 2, i);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2.getDistanceMatrix(List<Real2>,
	 * List<Real2>)'
	 */
	@Test
	public void testGetDistanceMatrix() {
		real2List = new ArrayList<Real2>();
		real2List.add(new Real2(1.0, 2.0));
		real2List.add(new Real2(-2.0, 1.0));
		real2List.add(new Real2(4.0, 4.0));
		RealMatrix m = Real2.getDistanceMatrix(real2List, real2List);
		double[] d = new double[9];
		d[0] = 0.0;
		d[1] = Math.sqrt(10.);
		d[2] = Math.sqrt(13.);
		d[3] = Math.sqrt(10.);
		d[4] = 0.0;
		d[5] = Math.sqrt(45.);
		d[6] = Math.sqrt(13.);
		d[7] = Math.sqrt(45.);
		d[8] = 0.0;
		RealMatrixTest.assertEquals("distance matrix", 3, 3, d, m, EPS);
	}
	
	@Test
	public void testFromString() {
		Real2 real2 = new Real2(1.2, 2.3);
		String s = real2.toString();
		Real2 newReal2 = Real2.createFromString(s);
		Assert.assertNotNull(newReal2);
		Real2Test.assertEquals("string", real2, newReal2, 0.001);
	}

}
