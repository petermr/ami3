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

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.Line3;
import org.contentmine.eucl.euclid.Plane3;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.Vector3;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Point3
 * 
 * @author pmr
 * 
 */
public class Point3Test extends GeomTest {

	/**
	 * setup.
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
	public static void assertEquals(String msg, Point3 test, Point3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertNotNull("ref should not be null (" + msg + S_RBRAK,
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
	 *            array must be of length 3
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test, Point3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertEquals("must be of length 3", 3, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.Point3()'
	 */
	@Test
	public void testPoint3() {
		Assert.assertNotNull("point", p0);
		Point3Test
				.assertEquals("point 0", new double[] { 0., 0., 0. }, p0, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.Point3(double, double, double)'
	 */
	@Test
	public void testPoint3DoubleDoubleDouble() {
		Assert.assertNotNull("point", p123);
		Point3Test.assertEquals("point 123", new double[] { 1., 2., 3. }, p123,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.Point3(Point3)'
	 */
	@Test
	public void testPoint3Point3() {
		Point3 pp = new Point3(p123);
		Assert.assertNotNull("point", pp);
		Point3Test.assertEquals("point copy", new double[] { 1., 2., 3. }, pp,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.Point3(double[])'
	 */
	@Test
	public void testPoint3DoubleArray() {
		Point3 pp = new Point3(new double[] { 1., 3., 5. });
		Assert.assertNotNull("point", pp);
		Point3Test.assertEquals("point copy", new double[] { 1., 3., 5. }, pp,
				EPS);
		try {
			pp = new Point3(new double[] { 1., 3. });
			Assert.fail("should always throw " + "must have 3 coordinates");
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("bad coordinates",
					"array size required (3) found 2", e.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.Point3(Vector3)'
	 */
	@Test
	public void testPoint3Vector3() {
		Point3 pp = new Point3(v123);
		Assert.assertNotNull("point", pp);
		Point3Test.assertEquals("point copy", new double[] { 1., 2., 3. }, pp,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.getArray()'
	 */
	@Test
	public void testGetArray() {
		Point3Test.assertEquals("point array", new double[] { 1., 2., 3. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.clear()'
	 */
	@Test
	public void testClear() {
		p123.clear();
		Point3Test.assertEquals("point array", new double[] { 0., 0., 0. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.isEqualTo(Point3)'
	 */
	@Test
	public void testIsEqualToPoint3() {
		Point3 pp = new Point3(p123);
		Assert.assertTrue("point isEqualTo", p123.isEqualTo(pp));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.isEqualTo(Point3, double)'
	 */
	@Test
	public void testIsEqualToPoint3Double() {
		Point3 pp = new Point3(p123);
		Assert.assertTrue("point isEqualTo", p123.isEqualTo(pp, EPS));
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3.equalsCrystallographically(Point3)'
	 */
	@Test
	public void testEqualsCrystallographically() {
		Assert.assertTrue("point isEqualToCrystallographically", p123
				.equalsCrystallographically(p000));
		Assert.assertFalse("point isEqualToCrystallographically", p123
				.equalsCrystallographically(new Point3(0.1, 0.2, 0.3)));
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3.normaliseCrystallographically()'
	 */
	@Test
	public void testNormaliseCrystallographically() {
		Point3 p = new Point3(1.1, 2.2, -0.7);
		p.normaliseCrystallographically();
		Point3Test.assertEquals("normalise", new double[] { 0.1, 0.2, 0.3 }, p,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.isInvariant(Transform3,
	 * boolean)'
	 */
	@Test
	public void testIsInvariant() {
		boolean allowTranslate = false;
		Assert.assertTrue("invariant", p123.isInvariant(tr0, allowTranslate));
		allowTranslate = true;
		Assert.assertTrue("invariant", p123.isInvariant(tr0, allowTranslate));
		allowTranslate = false;
		Assert.assertFalse("invariant", p123.isInvariant(tr1, allowTranslate));
		allowTranslate = true;
		Assert.assertTrue("invariant", p123.isInvariant(tr0, allowTranslate));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.subtract(Point3)'
	 */
	@Test
	public void testSubtractPoint3() {
		Vector3 v = p123.subtract(p321);
		Vector3Test.assertEquals("subtract", new double[] { -2, 0, 2 }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.plus(Point3)'
	 */
	@Test
	public void testPlusPoint3() {
		Point3 pp = p123.plus(p321);
		Point3Test.assertEquals("subtract", new double[] { 4., 4., 4. }, pp,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.plusEquals(Point3)'
	 */
	@Test
	public void testPlusEqualsPoint3() {
		p123.plusEquals(p321);
		Point3Test.assertEquals("plusEquals", new double[] { 4., 4., 4. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.plus(Vector3)'
	 */
	@Test
	public void testPlusVector3() {
		Point3 pp = p123.plus(v321);
		Point3Test.assertEquals("plus", new double[] { 4., 4., 4. }, pp, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.plusEquals(Vector3)'
	 */
	@Test
	public void testPlusEqualsVector3() {
		p123.plusEquals(v321);
		Point3Test.assertEquals("plusEquals", new double[] { 4., 4., 4. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.subtract(Vector3)'
	 */
	@Test
	public void testSubtractVector3() {
		Point3 pp = p123.subtract(v321);
		Point3Test.assertEquals("subtract", new double[] { -2., 0., 2. }, pp,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.subtractEquals(Point3)'
	 */
	@Test
	public void testSubtractEqualsPoint3() {
		p123.subtractEquals(p321);
		Point3Test.assertEquals("subtractEquals", new double[] { -2., 0., 2. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.subtractEquals(Vector3)'
	 */
	@Test
	public void testSubtractEqualsVector3() {
		p123.subtractEquals(v321);
		Point3Test.assertEquals("subtractEquals", new double[] { -2., 0., 2. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.multiplyBy(double)'
	 */
	@Test
	public void testMultiplyBy() {
		Point3 p = p123.multiplyBy(10.);
		Point3Test.assertEquals("multiply", new double[] { 10., 20., 30. }, p,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.multiplyEquals(double)'
	 */
	@Test
	public void testMultiplyEquals() {
		p123.multiplyEquals(10.);
		Point3Test.assertEquals("multiplyEquals",
				new double[] { 10., 20., 30. }, p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.reflect()'
	 */
	@Test
	public void testReflect() {
		p123.reflect();
		Point3Test.assertEquals("reflect", new double[] { -1., -2., -3. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.divideBy(double)'
	 */
	@Test
	public void testDivideBy() {
		Point3 p = p123.divideBy(10.);
		Point3Test.assertEquals("divideBy", new double[] { 0.1, 0.2, 0.3 }, p,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.divideEquals(double)'
	 */
	@Test
	public void testDivideEquals() {
		p123.divideEquals(10.);
		Point3Test.assertEquals("divideEquals", new double[] { 0.1, 0.2, 0.3 },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.elementAt(int)'
	 */
	@Test
	public void testElementAt() {
		double d = p123.elementAt(1);
		Assert.assertEquals("element at", 2., d, EPS);
		try {
			d = p123.elementAt(3);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("element at", "index (3)out of range: 0/2", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.setElementAt(int, double)'
	 */
	@Test
	public void testSetElementAt() {
		p123.setElementAt(1, 10.);
		Point3Test.assertEquals("set element at", new double[] { 1., 10., 3. },
				p123, EPS);
		try {
			p123.setElementAt(3, 10.);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("element at", "index (3)out of range: 0/2", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.transform(Transform3)'
	 */
	@Test
	public void testTransform() {
		Point3 p = p123.transform(tr1);
		Point3Test.assertEquals("transform", new double[] { 1., -2., 3. }, p,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.transformEquals(Transform3)'
	 */
	@Test
	public void testTransformEquals() {
		p123.transformEquals(tr1);
		Point3Test.assertEquals("transform", new double[] { 1., -2., 3. },
				p123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.getDistanceFromOrigin()'
	 */
	@Test
	public void testGetDistanceFromOrigin() {
		double d = p123.getDistanceFromOrigin();
		Assert.assertEquals("distance from origin", Math.sqrt(14.), d, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3.getSquaredDistanceFromPoint(Point3)'
	 */
	@Test
	public void testGetSquaredDistanceFromPoint() {
		double d = p123.getSquaredDistanceFromPoint(p321);
		Assert.assertEquals("distance from origin", 8., d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.getDistanceFromPoint(Point3)'
	 */
	@Test
	public void testGetDistanceFromPoint() {
		double d = p123.getDistanceFromPoint(p321);
		Assert.assertEquals("distance from origin", Math.sqrt(8.), d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.distanceFromPlane(Plane3)'
	 */
	@Test
	public void testDistanceFromPlane() {
		Point3 p = new Point3(0., 0., 0.);
		Plane3 pl = null;
		try {
			pl = new Plane3(new Vector3(1., 1., 1.), 1.0);
		} catch (Exception e) {
			Util.BUG(e);
		}
		double d = p.distanceFromPlane(pl);
		Assert.assertEquals("distance ", -1.0, d, EPS);
		p = new Point3(1., 1., 1.);
		d = p.distanceFromPlane(pl);
		Assert.assertEquals("distance ", Math.sqrt(3.) - 1., d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.getClosestPointOnLine(Line3)'
	 */
	@Test
	public void testGetClosestPointOnLine() {
		Vector3 vv = new Vector3(1., 1., 1.);
		Point3 pp = new Point3(1., 2., 3.);
		Line3 ll = new Line3(pp, vv);
		Assert.assertTrue("on line", pp.isOnLine(ll));
		double d = pp.distanceFromLine(ll);

		Point3 p0 = new Point3(0., 0., 0.);
		Assert.assertFalse("on line", p0.isOnLine(ll));
		Point3 pClose = p0.getClosestPointOnLine(ll);
		Point3Test.assertEquals("nearest point", new double[] { -1, 0., 1. },
				pClose, EPS);
		d = pClose.distanceFromLine(ll);
		Assert.assertEquals("distance", 0.0, d, EPS);
		Vector3 v = pClose.subtract(p0);
		Angle a = null;
		a = v.getAngleMadeWith(vv);
		Assert.assertEquals("angle ", Math.PI / 2., a.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.isOnLine(Line3)'
	 */
	@Test
	public void testIsOnLine() {
		Vector3 vv = new Vector3(1., 1., 1.);
		Point3 p = new Point3(1., 2., 3.);
		Line3 l3 = new Line3(p, vv);
		Assert.assertTrue("on line", p.isOnLine(l3));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.distanceFromLine(Line3)'
	 */
	@Test
	public void testDistanceFromLine() {
		Point3 pp = new Point3(4., 5., 6.);
		Vector3 vv = new Vector3(1., 2., 3.);
		Line3 ll = new Line3(pp, vv);
		double d = pp.distanceFromLine(ll);
		Assert.assertEquals("distance from line", 0.0, d,EPS);
		Point3 p0 = new Point3(0., 0., 0.);
		d = p0.distanceFromLine(ll);
		Assert.assertEquals("distance", 1.9639610121239313, d, EPS);

	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.getMidPoint(Point3)'
	 */
	@Test
	public void testGetMidPoint() {
		Point3 p = p123.getMidPoint(p321);
		Point3Test.assertEquals("mid point", new double[] { 2., 2., 2. }, p,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.getAngle(Point3, Point3,
	 * Point3)'
	 */
	@Test
	public void testGetAngle() {
		Point3 p444 = new Point3(4., 4., 4.);
		Angle p = Point3.getAngle(p123, p444, p321);
		Assert.assertNotNull("angle ", p);
		Assert.assertEquals("angle", 0.7751933733103613, p.getRadian(), EPS);
		p = Point3
				.getAngle(new Point3(1., 0., 0), p000, new Point3(0., 1., 0.));
		Assert.assertNotNull("angle ", p);
		Assert.assertEquals("angle", Math.PI / 2., p.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.getTorsion(Point3, Point3,
	 * Point3, Point3)'
	 */
	@Test
	public void testGetTorsion() {
		Angle p = null;
		p = Point3.getTorsion(new Point3(1., 0., 0), p000, new Point3(0., 1.,
				0.), new Point3(0., 1., 1.));
		Assert.assertEquals("angle", -Math.PI / 2., p.getRadian(), EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3.calculateFromInternalCoordinates(Point3,
	 * Point3, Point3, double, Angle, Angle)'
	 */
	@Test
	public void testCalculateFromInternalCoordinates() {
		Point3 p0 = new Point3(1., 0., 0.);
		Point3 p1 = new Point3(0., 0., 0.);
		Point3 p2 = new Point3(0., 1., 0.);
		Point3 p = Point3.calculateFromInternalCoordinates(p0, p1, p2, 2.0,
				new Angle(Math.PI / 2), new Angle(Math.PI / 2));
		Point3Test.assertEquals("internals", new double[] { 0.0, 1.0, -2.0 },
				p, EPS);

	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3.isOrigin()'
	 */
	@Test
	public void testIsOrigin() {
		Assert.assertTrue("origin", p000.isOrigin());
		Assert.assertFalse("origin", p123.isOrigin());
	}

}
