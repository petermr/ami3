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
import org.contentmine.eucl.euclid.Axis.Axis3;
import org.contentmine.eucl.euclid.EuclidConstants;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.Vector3;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * tests for Vector3.
 * 
 * @author pmr
 * 
 */
public class Vector3Test extends GeomTest {

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
	public static void assertEquals(String msg, Vector3 test, Vector3 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EuclidConstants.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EuclidConstants.S_RBRAK,
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
	public static void assertEquals(String msg, double[] test,
			Vector3 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EuclidConstants.S_RBRAK, test);
		Assert.assertEquals("must be of length 3", 3, test.length);
		Assert.assertNotNull("expected should not be null (" + msg + EuclidConstants.S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.Vector3()'
	 */
	@Test
	public void testVector3() {
		Vector3Test
				.assertEquals("vector", new double[] { 0., 0., 0. }, v0, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.Vector3(double, double,
	 * double)'
	 */
	@Test
	public void testVector3DoubleDoubleDouble() {
		Vector3Test.assertEquals("vector", new double[] { 1., 2., 3. }, v123,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.Vector3(double[])'
	 */
	@Test
	public void testVector3DoubleArray() {
		Vector3 v = new Vector3(new double[] { 4., 5., 6. });
		Vector3Test.assertEquals("vector", new double[] { 4., 5., 6. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.Vector3(Axis3)'
	 */
	@Test
	public void testVector3Axis3() {
		Vector3 v = new Vector3(Axis3.X);
		Vector3Test.assertEquals("vector", new double[] { 1., 0., 0. }, v, EPS);
		v = new Vector3(Axis3.Y);
		Vector3Test.assertEquals("vector", new double[] { 0., 1., 0. }, v, EPS);
		v = new Vector3(Axis3.Z);
		Vector3Test.assertEquals("vector", new double[] { 0., 0., 1. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.Vector3(Vector3)'
	 */
	@Test
	public void testVector3Vector3() {
		Vector3 v = new Vector3(v123);
		Vector3Test.assertEquals("vector", new double[] { 1., 2., 3. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.Vector3(RealArray)'
	 */
	@Test
	public void testVector3RealArray() {
		Vector3 v = new Vector3(new double[] { 1., 2., 3. });
		Vector3Test.assertEquals("vector", new double[] { 1., 2., 3. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.Vector3(Point3)'
	 */
	@Test
	public void testVector3Point3() {
		Vector3 v = new Vector3(p123);
		Vector3Test.assertEquals("vector", new double[] { 1., 2., 3. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.getArray()'
	 */
	@Test
	public void testGetArray() {
		DoubleTestBase.assertEquals("array", new double[] { 1., 2., 3. }, v123
				.getArray(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.isEqualTo(Vector3)'
	 */
	@Test
	public void testIsEqualTo() {
		Vector3 v = new Vector3(v123);
		Assert.assertTrue("isEqualTo", v.isEqualTo(v));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.longerThan(Vector3)'
	 */
	@Test
	public void testLongerThan() {
		Vector3 v = new Vector3(v123).plus(v100);
		Assert.assertTrue("isLongerThan", v.longerThan(v123));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.multiplyBy(double)'
	 */
	@Test
	public void testMultiplyBy() {
		Vector3 v = v123.multiplyBy(10.);
		Vector3Test.assertEquals("multiply", new double[] { 10., 20., 30. }, v,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.multiplyEquals(double)'
	 */
	@Test
	public void testMultiplyEquals() {
		v123.multiplyEquals(10.);
		Vector3Test.assertEquals("multiply", new double[] { 10., 20., 30. },
				v123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.plus(Vector3)'
	 */
	@Test
	public void testPlus() {
		Vector3 v = v123.plus(v100);
		Vector3Test.assertEquals("plus", new double[] { 2., 2., 3. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.subtract(Vector3)'
	 */
	@Test
	public void testSubtract() {
		Vector3 v = v123.subtract(v100);
		Vector3Test.assertEquals("subtract", new double[] { 0., 2., 3. }, v,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.negative()'
	 */
	@Test
	public void testNegative() {
		Vector3 v = v123.negative();
		Vector3Test.assertEquals("negative", new double[] { -1., -2., -3. }, v,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.elementAt(int)'
	 */
	@Test
	public void testElementAt() {
		double d = v123.elementAt(0);
		Assert.assertEquals("elementAt", 1., d, EPS);
		d = v123.elementAt(1);
		Assert.assertEquals("elementAt", 2., d, EPS);
		d = v123.elementAt(2);
		Assert.assertEquals("elementAt", 3., d, EPS);
		try {
			v123.elementAt(-1);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("elementAt", "index (-1)out of range: 0/2", e
					.getMessage());
		}
		try {
			v123.elementAt(3);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("elementAt", "index (3)out of range: 0/2", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.setElementAt(int, double)'
	 */
	@Test
	public void testSetElementAt() {
		v123.setElementAt(0, 10.);
		Vector3Test.assertEquals("elementAt", new double[] { 10., 2., 3. },
				v123, EPS);
		v123.setElementAt(1, 20.);
		Vector3Test.assertEquals("elementAt", new double[] { 10., 20., 3. },
				v123, EPS);
		v123.setElementAt(2, 30.);
		Vector3Test.assertEquals("elementAt", new double[] { 10., 20., 30. },
				v123, EPS);
		try {
			v123.elementAt(-1);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("elementAt", "index (-1)out of range: 0/2", e
					.getMessage());
		}
		try {
			v123.elementAt(3);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("elementAt", "index (3)out of range: 0/2", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.isIdenticalTo(Vector3)'
	 */
	@Test
	public void testIsIdenticalTo() {
		Vector3 v = new Vector3(v123);
		Assert.assertTrue("identical to", v123.isIdenticalTo(v));
		Assert.assertFalse("identical to", v123.isIdenticalTo(v100));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.isZero()'
	 */
	@Test
	public void testIsZero() {
		Assert.assertTrue("isZero", v000.isZero());
		Assert.assertFalse("isZero", v123.isZero());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.transform(Transform3)'
	 */
	@Test
	public void testTransform() {
		Vector3 v = v123.transform(tr2);
		Vector3Test.assertEquals("transform", new double[] { 1., 2., 3. },
				v123, EPS);
		Vector3Test.assertEquals("transform", new double[] { -1., -2., 3. }, v,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.cross(Vector3)'
	 */
	@Test
	public void testCross() {
		Vector3 v = v100.cross(v010);
		Vector3Test.assertEquals("cross", new double[] { 0., 0., 1. }, v, EPS);
		v = v100.cross(v100);
		Vector3Test.assertEquals("cross", new double[] { 0., 0., 0. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.normalize()'
	 */
	@Test
	public void testNormalise() {
		v123.normalize();
		double d = Math.sqrt(14.);
		Vector3Test.assertEquals("normalise", new double[] { 1. / d, 2. / d,
				3. / d }, v123, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.round()'
	 */
	@Test
	public void testRound() {
		Vector3 v = new Vector3(0.8, -0.1, -1.2);
		v.round();
		Vector3Test.assertEquals("round", new double[] { 1.0, 0.0, -1.0 }, v,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.getUnitVector()'
	 */
	@Test
	public void testGetUnitVector() {
		Vector3 v = v123.getUnitVector();
		double d = Math.sqrt(14.);
		Vector3Test.assertEquals("unit vector", new double[] { 1. / d, 2. / d,
				3. / d }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.getLength()'
	 */
	@Test
	public void testGetLength() {
		Assert.assertEquals("length", Math.sqrt(14.), v123.getLength(), EPS);
		Assert.assertEquals("length", Math.sqrt(0.), v000.getLength(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.dot(Vector3)'
	 */
	@Test
	public void testDotVector3() {
		Assert.assertEquals("dot", 3., v001.dot(v123),EPS);
		Assert.assertEquals("dot", 0., v100.dot(v001),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.getAngleMadeWith(Vector3)'
	 */
	@Test
	public void testGetAngleMadeWith() {
		Angle a = null;
		a = v001.getAngleMadeWith(v100);
		Assert.assertNotNull("angle", a);
		Assert.assertEquals("angle", Math.PI / 2., a.getRadian(), EPS);
		a = v001.getAngleMadeWith(v001);
		Assert.assertNotNull("angle", a);
		Assert.assertEquals("angle", 0., a.getRadian(), EPS);
		a = v001.getAngleMadeWith(v000);
		Assert.assertNull("angle zero length", a);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Vector3.getScalarTripleProduct(Vector3, Vector3)'
	 */
	@Test
	public void testGetScalarTripleProduct() {
		Assert.assertEquals("stp", 0., v001.getScalarTripleProduct(v001, v010),
				EPS);
		Assert.assertEquals("stp", -1.,
				v001.getScalarTripleProduct(v010, v100), EPS);
		Assert.assertEquals("stp", 1., v001.getScalarTripleProduct(v100, v010),
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.projectOnto(Vector3)'
	 */
	@Test
	public void testProjectOnto() {
		Vector3 v = v123.projectOnto(v100);
		Vector3Test
				.assertEquals("project", new double[] { 1., 0., 0. }, v, EPS);
		v = v123.projectOnto(v001);
		Vector3Test
				.assertEquals("project", new double[] { 0., 0., 3. }, v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.isColinearVector(Vector3)'
	 */
	@Test
	public void testIsColinearVector() {
		Assert.assertTrue("colinear", v123.isColinearVector(v123));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.getNonColinearVector()'
	 */
	@Test
	public void testGetNonColinearVector() {
		Vector3 v = v123.getNonColinearVector();
		Assert.assertFalse("colinear", v123.isColinearVector(v));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector3.getPerpendicularVector()'
	 */
	@Test
	public void testGetPerpendicularVector() {
		Vector3 v = v123.getPerpendicularVector();
		Angle a = v.getAngleMadeWith(v123);
		Assert.assertNotNull("perpendicular vector", a);
		Assert.assertEquals("perpendicular", Math.PI / 2., a.getRadian(), EPS);
	}

}
