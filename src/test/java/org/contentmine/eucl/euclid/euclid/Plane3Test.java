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

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.Line3;
import org.contentmine.eucl.euclid.Plane3;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.Vector3;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Plane3
 * 
 * @author pmr
 * 
 */
public class Plane3Test extends GeomTest {

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
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
	public static void assertEquals(String msg, Plane3 test, Plane3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + EC.S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test.getArray(), expected.getArray(),
				epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 4
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test, Plane3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertEquals("must be of length 4", 4, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + EC.S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3()'
	 */
	@Test
	public void testPlane3() {
		Assert.assertNotNull("plane", pl0);
		Plane3Test.assertEquals("plane", new double[] { 0., 0., 0., 0. }, pl0,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3(double, double, double,
	 * double)'
	 */
	@Test
	public void testPlane3DoubleDoubleDoubleDouble() {
		Plane3Test.assertEquals("plane", new double[] { 1., 0., 0., 0. },
				pl1000, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3(double[], double)'
	 */
	@Test
	public void testPlane3DoubleArrayDouble() {
		Plane3Test.assertEquals("plane", new double[] { 1. / s14, 2. / s14,
				3. / s14, 4. }, pl1234, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3(double[])'
	 */
	@Test
	public void testPlane3DoubleArray() {
		Plane3Test.assertEquals("plane", new double[] { 0., 0., 1., 0. },
				pl0010, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3(Vector3, double)'
	 */
	@Test
	public void testPlane3Vector3Double() {
		Plane3Test.assertEquals("plane", new double[] { 1. / s3, 1. / s3,
				1. / s3, 1. }, pl1111, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3(Plane3)'
	 */
	@Test
	public void testPlane3Plane3() {
		Plane3 pl = new Plane3(pl1234);
		Plane3Test.assertEquals("plane", new double[] { 1. / s14, 2. / s14,
				3. / s14, 4. }, pl, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3(Point3, Point3, Point3)'
	 */
	@Test
	public void testPlane3Point3Point3Point3() {
		Plane3 pl = new Plane3(p100, p010, p001);
		Plane3Test.assertEquals("plane", new double[] { 1. / s3, 1. / s3,
				1. / s3, 1. / s3 }, pl, EPS);
		try {
			pl = new Plane3(p100, p010, p100);
			Assert.fail("should always throw " + "zero normal");
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("plane", "zero length normal", e.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.Plane3(Line3, Point3)'
	 */
	@Test
	public void testPlane3Line3Point3() {
		Plane3 pl = new Plane3(l100000, p001);
		Plane3Test.assertEquals("plane", new double[] { 0., -1., 0., 0. }, pl,
				EPS);
		pl = new Plane3(l123456, p321);
		Assert.assertNotNull("plane", pl);
		Plane3Test.assertEquals("plane",
				new double[] { -0.40824829046386013, 0.816496580927727,
						-0.4082482904638642, 9.325873406851315E-15 }, pl, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getVector()'
	 */
	@Test
	public void testGetVector() {
		Plane3 pl = new Plane3(p100, p010, p001);
		Vector3 v = pl.getVector();
		Vector3Test.assertEquals("vector", new double[] { 1. / s3, 1. / s3,
				1. / s3 }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getDistance()'
	 */
	@Test
	public void testGetDistance() {
		Plane3 pl = new Plane3(p100, p010, p001);
		Assert.assertEquals("distance", 1. / s3, pl.getDistance(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.negative()'
	 */
	@Test
	public void testNegative() {
		Plane3 pl = new Plane3(p100, p010, p001);
		Plane3Test.assertEquals("negative", new double[] { 1. / s3, 1. / s3,
				1. / s3, 1. / s3 }, pl, EPS);
		pl.negative();
		Plane3Test.assertEquals("negative", new double[] { -1. / s3, -1. / s3,
				-1. / s3, 1. / s3 }, pl, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.isEqualTo(Plane3)'
	 */
	@Test
	public void testIsEqualTo() {
		Plane3 pl = new Plane3(pl1234);
		Assert.assertTrue("equals", pl.isEqualTo(pl1234));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.subtract()'
	 */
	@Test
	public void testSubtract() {
		Plane3Test.assertEquals("equals", new double[] { 1. / s14, 2. / s14,
				3. / s14, 4. }, pl1234, EPS);
		Plane3 pl = pl1234.subtract();
		Plane3Test.assertEquals("equals", new double[] { -1. / s14, -2. / s14,
				-3. / s14, 4. }, pl, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getDistanceFromPoint(Point3)'
	 */
	@Test
	public void testGetDistanceFromPoint() {
		double d = pl1111.getDistanceFromPoint(p000);
		Assert.assertEquals("equals", -1., d, EPS);
		Plane3 pl = pl1111.subtract();
		Assert.assertEquals("equals", -1., pl.getDistanceFromPoint(p000), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.isParallelTo(Plane3)'
	 */
	@Test
	public void testIsParallelTo() {
		Plane3 pl = new Plane3(pl1234);
		Assert.assertTrue("parallel", pl.isParallelTo(pl1234));
		pl.subtract();
		Assert.assertFalse("parallel", pl.isParallelTo(pl1234));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.isAntiparallelTo(Plane3)'
	 */
	@Test
	public void testIsAntiparallelTo() {
		Plane3 pl = new Plane3(pl1234);
		Assert.assertFalse("antiparallel", pl.isAntiparallelTo(pl1234));
		pl = pl.subtract();
		Assert.assertTrue("isAntiparallelTo", pl.isAntiparallelTo(pl1234));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.containsPoint(Point3)'
	 */
	@Test
	public void testContainsPoint() {
		Assert.assertFalse("contains", pl1111.containsPoint(p111));
		Assert.assertTrue("contains", pl1111.containsPoint(new Point3(1. / s3,
				1. / s3, 1. / s3)));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getClosestPointTo(Point3)'
	 */
	@Test
	public void testGetClosestPointTo() {
		Point3 p = pl1111.getClosestPointTo(p000);
		Point3Test.assertEquals("equals", new double[] { 1. / s3, 1. / s3,
				1. / s3 }, p, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getIntersectionWith(Line3)'
	 */
	@Test
	public void testGetIntersectionWithLine3() {
		Point3 p = pl1111.getIntersectionWith(l123456);
		Point3Test.assertEquals("equals", new double[] { 1.788675134594813,
				0.5773502691896262, -0.6339745962155607 }, p, EPS);
		double d = pl1111.getDistanceFromPoint(p);
		Assert.assertEquals("intersection", 0.0, d, EPS);
		d = l123456.getDistanceFromPoint(p);
		Assert.assertEquals("intersection", 0.0, d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getIntersectionWith(Plane3)'
	 */
	@Test
	public void testGetIntersectionWithPlane3() {
		Line3 l = pl1111.getIntersectionWith(pl1234);
		Vector3Test.assertEquals("plane line", new double[] {
				0.4082482904638631, -0.8164965809277261, 0.4082482904638631, },
				l.getVector(), EPS);
		Point3Test.assertEquals("plane line", new double[] { -5.17391369678938,
				0.5773502691896258, 6.328614235168632 }, l.getPoint(), EPS);
		Point3 p = l.getPoint();
		double d = pl1111.getDistanceFromPoint(p);
		Assert.assertEquals("intersection", 0.0, d, EPS);
		Point3 p1 = p.plus(l.getVector());
		d = pl1234.getDistanceFromPoint(p1);
		Assert.assertEquals("intersection", 0.0, d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getIntersectionWith(Plane3,
	 * Plane3)'
	 */
	@Test
	public void testGetIntersectionWithPlane3Plane3() {
		Point3 p = pl1111.getIntersectionWith(pl1234, pl1000);
		Point3Test.assertEquals("intersection", new double[] { 0.0,
				-9.770477124389135, 11.502527931958012 }, p, EPS);
		// check
		double d = pl1111.getDistanceFromPoint(p);
		Assert.assertEquals("intersection", 0.0, d, EPS);
		d = pl1234.getDistanceFromPoint(p);
		Assert.assertEquals("intersection", 0.0, d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Plane3.getAngleMadeWith(Plane3)'
	 */
	@Test
	public void testGetAngleMadeWith() {
		Angle a = pl1111.getAngleMadeWith(pl1234);
		Assert.assertEquals("angle", 0.38759668665518016, a.getRadian(), EPS);
	}

}
