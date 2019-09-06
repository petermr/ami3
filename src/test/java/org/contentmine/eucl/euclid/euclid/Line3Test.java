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
import org.contentmine.eucl.euclid.Line3;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.Vector3;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Line3
 * 
 * @author pmr
 * 
 */
public class Line3Test extends GeomTest {

	/**
	 * set up.
	 * 
	 */
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
	public static void assertEquals(String msg, Line3 test, Line3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + EC.S_RBRAK,
				expected);
		Point3Test.assertEquals(msg, test.getPoint(), expected.getPoint(),
				epsilon);
		Vector3Test.assertEquals(msg, test.getVector(), expected.getVector(),
				epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param testPoint
	 * @param testVector
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Point3 testPoint,
			Vector3 testVector, Line3 expected, double epsilon) {
		Assert.assertNotNull("testPoint should not be null (" + msg + EC.S_RBRAK,
				testPoint);
		Assert.assertNotNull("testVector should not be null (" + msg + EC.S_RBRAK,
				testVector);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		Point3Test.assertEquals(msg, testPoint, expected.getPoint(), epsilon);
		Vector3Test
				.assertEquals(msg, testVector, expected.getVector(), epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.Line3()'
	 */
	@Test
	public void testLine3() {
		Assert.assertNotNull("line", l0);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.Line3(Point3, Vector3)'
	 */
	@Test
	public void testLine3Point3Vector3() {
		Assert.assertNotNull("line", l123456);
		Point3 p = l123456.getPoint();
		Vector3 v = l123456.getVector();
		Point3Test.assertEquals("line", new double[] { 4., 5., 6. }, p, EPS);
		Vector3Test.assertEquals("line", new double[] { 1. / s14, 2. / s14,
				3. / s14 }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.Line3(Point3, Point3)'
	 */
	@Test
	public void testLine3Point3Point3() {
		Line3 l = new Line3(p100, p001);
		Assert.assertNotNull("line", l);
		Point3 p = l.getPoint();
		Vector3 v = l.getVector();
		Point3Test.assertEquals("line", new double[] { 1., 0., 0. }, p, EPS);
		Vector3Test.assertEquals("line",
				new double[] { -1. / s2, 0., 1. / s2 }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.Line3(Line3)'
	 */
	@Test
	public void testLine3Line3() {
		Line3 l = new Line3(l123456);
		Assert.assertNotNull("line", l);
		Point3 p = l.getPoint();
		Vector3 v = l.getVector();
		Point3Test.assertEquals("line", new double[] { 4., 5., 6. }, p, EPS);
		Vector3Test.assertEquals("line", new double[] { 1. / s14, 2. / s14,
				3. / s14 }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.isEqualTo(Line3)'
	 */
	@Test
	public void testIsEqualTo() {
		Line3 l = new Line3(l123456);
		Assert.assertTrue("isEqualTo", l.isEqualTo(l123456));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.negative()'
	 */
	@Test
	public void testNegative() {
		Line3 l = new Line3(l123456);
		l = l.negative();
		Vector3Test.assertEquals("negative", new double[] { -1. / s14,
				-2. / s14, -3. / s14 }, l.getVector(), EPS);
		Point3Test.assertEquals("negative", new double[] { 4., 5., 6. }, l
				.getPoint(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.transform(Transform3)'
	 */
	@Test
	public void testTransform() {
		Line3 l = l123456.transform(tr1);
		Vector3Test.assertEquals("transform", new double[] { 1. / s14,
				-2. / s14, 3. / s14 }, l.getVector(), EPS);
		Point3Test.assertEquals("transform", new double[] { 4., -5., 6. }, l
				.getPoint(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.isParallelTo(Line3)'
	 */
	@Test
	public void testIsParallelTo() {
		Line3 l = new Line3(l123456);
		Assert.assertTrue("isParallel", l.isParallelTo(l123456));
		l = l.negative();
		Assert.assertFalse("isParallel", l.isParallelTo(l123456));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.isAntiparallelTo(Line3)'
	 */
	@Test
	public void testIsAntiparallelTo() {
		Line3 l = new Line3(l123456);
		Assert.assertFalse("isAntiParallel", l.isAntiparallelTo(l123456));
		l = l.negative();
		Assert.assertTrue("isAntiParallel", l.isAntiparallelTo(l123456));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.containsPoint(Point3)'
	 */
	@Test
	public void testContainsPoint() {
		Assert.assertTrue("contains", l123456.containsPoint(new Point3(4., 5.,
				6.)));
		Assert.assertTrue("contains", l123456.containsPoint(new Point3(3., 3.,
				3.)));
		Assert.assertTrue("contains", l123456.containsPoint(new Point3(2., 1.,
				0.)));
		Assert.assertFalse("contains", l123456.containsPoint(new Point3(3., 5.,
				6.)));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.getClosestPointTo(Point3)'
	 */
	@Test
	public void testGetClosestPointTo() {
		Point3 p = l123456.getClosestPointTo(p100);
		Assert.assertTrue("contains", l123456.containsPoint(p));
		Assert.assertFalse("contains", l123456.containsPoint(p100));

	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.getDistanceFromPoint(Point3)'
	 */
	@Test
	public void testGetDistanceFromPoint() {
		double d = l123456.getDistanceFromPoint(p100);
		Assert.assertEquals("distance from", 1.1649647450214353, d, EPS);
		Point3 p = l123456.getClosestPointTo(p100);
		double dd = p.getDistanceFromPoint(p100);
		Assert.assertEquals("distance from", 1.1649647450214353, dd, EPS);
		Vector3 v = p.subtract(p100);
		Angle a = v.getAngleMadeWith(l123456.getVector());
		Assert.assertNotNull("angle ", a);
		Assert.assertEquals("check angle", Math.PI / 2., a.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Line3.getIntersectionWith(Plane3)'
	 */
	@Test
	public void testGetIntersectionWith() {
		Point3 p = l123456.getIntersectionWith(pl1111);
		Point3Test.assertEquals("intersection", new double[] {
				1.788675134594813, 0.5773502691896262, -0.6339745962155607 },
				p, EPS);
		Assert.assertTrue("contains", l123456.containsPoint(p));
		Assert.assertTrue("contains", pl1111.containsPoint(p));
	}

}
