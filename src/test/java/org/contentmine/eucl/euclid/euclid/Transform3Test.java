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
import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform3;
import org.contentmine.eucl.euclid.Transform3.Type;
import org.contentmine.eucl.euclid.Vector3;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Transform3.
 * 
 * @author pmr
 * 
 */
public class Transform3Test extends GeomTest {

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
	public static void assertEquals(String msg, Transform3 test,
			Transform3 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test.getMatrixAsArray(), expected
				.getMatrixAsArray(), epsilon);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            16 values
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, double[] test,
			Transform3 expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertEquals("test should have 16 elements (" + msg + EC.S_RBRAK,
				16, test.length);
		Assert.assertNotNull("ref should not be null (" + msg + EC.S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test, expected.getMatrixAsArray(),
				epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3()'
	 */
	@Test
	public void testTransform3() {
		Assert.assertNotNull("transform3", tr0);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(int)'
	 */
	@Test
	public void testTransform3Int() {
		Transform3 t = new Transform3(Type.ANY);
		Assert.assertNotNull("transform3", t);
		Transform3Test.assertEquals("transform3", new double[] { 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0,
				1.0, }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Vector3)'
	 */
	@Test
	public void testTransform3Vector3() {
		Transform3 t = new Transform3(new Vector3(1., 2., 3.));
		Transform3Test.assertEquals("transform3 vector", new double[] { 1.0,
				0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 2.0, 0.0, 0.0, 1.0, 3.0, 0.0,
				0.0, 0.0, 1.0, }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Axis3, Angle)'
	 */
	@Test
	public void testTransform3Axis3Angle() {
		Transform3 t = new Transform3(Axis3.X, new Angle(Math.PI / 3.));
		// not sure if this is right
		Transform3Test.assertEquals("transform3 vector", new double[] { 1.0,
				0.0, 0.0, 0.0, 0.0, 0.5, 0.8660254037844386, 0.0, 0.0,
				-0.8660254037844386, 0.5, 0.0, 0.0, 0.0, 0.0, 1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Angle, Angle,
	 * Angle)'
	 */
	@Test
	public void testTransform3AngleAngleAngle() {
		Transform3 t = new Transform3(new Angle(Math.PI / 2.), new Angle(
				Math.PI / 2.), new Angle(Math.PI / 2.));
		// not sure if this is right
		Transform3Test.assertEquals("transform3 vector", new double[] { 0., 0.,
				1., 0., 0., -1., 0., 0., 1., 0., 0., 0., 0., 0., 0., 1. }, t,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Transform3,
	 * Point3)'
	 */
	@Test
	public void testTransform3Transform3Point3() {
		Transform3 t = new Transform3(tr0, new Point3(1., 2., 3.));
		// tr0 is rotation 0 degrees, so should give identity matrix
		Transform3Test.assertEquals("transform3 vector", new double[] { 1.0,
				0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Vector3, Angle)'
	 */
	@Test
	public void testTransform3Vector3Angle() {
		Transform3 t = new Transform3(new Vector3(1. / s3, 1. / s3, 1. / s3),
				new Angle(Math.PI / 3.));
		// not sure if this is right
		Transform3Test.assertEquals("transform3 vector angle",
				new double[] { 0.6666666666666667, -0.3333333333333333,
						0.6666666666666667, 0.0, 0.6666666666666667,
						0.6666666666666667, -0.3333333333333333, 0.0,
						-0.3333333333333333, 0.6666666666666667,
						0.6666666666666667, 0.0, 0.0, 0.0, 0.0, 1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Line3, Angle)'
	 */
	@Test
	public void testTransform3Line3Angle() {
		Transform3 t = new Transform3(l123456, new Angle(Math.PI / 3.));
		// not sure if this is right
		Transform3Test.assertEquals("transform3 line3 angle", new double[] {
				0.5357142857142858, -0.6229365034008422, 0.5700529070291328,
				1.5515079319722704, 0.765793646257985, 0.642857142857143,
				-0.01716931065742361, -1.174444435373113, -0.3557671927434186,
				0.4457407392288521, 0.8214285714285715, 0.2657936462579853,
				0.0, 0.0, 0.0, 1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Vector3,
	 * Vector3)'
	 */
	@Test
	public void testTransform3Vector3Vector3() {
		Transform3 t = new Transform3(v123, v321);
		// not sure if this is right
		Transform3Test.assertEquals("transform3 vector vector", new double[] {
				0.761904761904762, 0.1904761904761905, 0.6190476190476192, 0.0,
				-0.38095238095238104, 0.9047619047619049, 0.1904761904761905,
				0.0, -0.5238095238095238, -0.38095238095238104,
				0.761904761904762, 0.0, 0.0, 0.0, 0.0, 1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Vector3,
	 * Vector3, Vector3)'
	 */
	@Test
	public void testTransform3Vector3Vector3Vector3() {
		Transform3 t = null;
		t = new Transform3(v123, v321, v100);
		Transform3Test.assertEquals("transform3 vector vector vector",
				new double[] { 1.0, 2.0, 3.0, 0.0, 3.0, 2.0, 1.0, 0.0, 1.0,
						0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(double[])'
	 */
	@Test
	public void testTransform3DoubleArray() {
		Transform3 t = new Transform3(new double[] { 0., 0., 1., 4., 0., 1.,
				0., 8., -1., 0., 0., 9., 0., 0., 0., 1. });
		Transform3Test.assertEquals("transform3 double[]", new double[] { 0.,
				0., 1., 4., 0., 1., 0., 8., -1., 0., 0., 9., 0., 0., 0., 1. },
				t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(Transform3)'
	 */
	@Test
	public void testTransform3Transform3() {
		Transform3 t = new Transform3(tr1);
		Transform3Test.assertEquals("copy", t, tr1, EPS);

	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Transform3.Transform3(RealSquareMatrix)'
	 */
	@Test
	public void testTransform3RealSquareMatrix() {
		Transform3 t = new Transform3(new RealSquareMatrix(4,
				new double[] { 0., 0., 1., 4., 0., 1., 0., 8., -1., 0., 0., 9.,
						0., 0., 0., 1. }));
		Transform3Test.assertEquals("transform3 rsm", new double[] { 0., 0.,
				1., 4., 0., 1., 0., 8., -1., 0., 0., 9., 0., 0., 0., 1. }, t,
				EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Transform3.Transform3(RealSquareMatrix, Vector3)'
	 */
	@Test
	public void testTransform3RealSquareMatrixVector3() {
		Transform3 t = new Transform3(new RealSquareMatrix(3, new double[] {
				0., 0., 1., 0., 1., 0., -1., 0., 0., }),
				new Vector3(4., 8., 9.));
		Transform3Test.assertEquals("transform3 rsm vector", new double[] { 0.,
				0., 1., 4., 0., 1., 0., 8., -1., 0., 0., 9., 0., 0., 0., 1. },
				t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.Transform3(String)'
	 */
	@Test
	public void testTransform3String() {
		Transform3 t = new Transform3("x, -y, 1/2+z");
		Transform3Test.assertEquals("transform3 string", new double[] { 1.0,
				0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.5, 0.0,
				0.0, 0.0, 0.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.concatenate(Transform3)'
	 */
	@Test
	public void testConcatenate() {
		Transform3 t1 = new Transform3("x, -y, z");
		Transform3 t2 = new Transform3("-x, -y, -z");
		Transform3 t = t1.concatenate(t2);
		Transform3Test.assertEquals("transform3 concatenate", new double[] {
				-1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0,
				0.0, 0.0, 0.0, 0.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.getAxisAndAngle(Vector3,
	 * Angle)'
	 */
	@Test
	public void testGetAxisAndAngle() {
		Transform3 t = new Transform3(new Vector3(1. / s3, 1. / s3, 1. / s3),
				new Angle(Math.PI / 3.));
		Vector3 v = new Vector3();
		Angle a = new Angle();
		t.getAxisAndAngle(v, a);
		// not sure if this is right
		Vector3Test.assertEquals("vector angle", new Vector3(1. / s3, 1. / s3,
				1. / s3), v, EPS);
		Assert.assertEquals("vector angle", Math.PI / 3., a.getRadian(), EPS);
		t = new Transform3("y, -x, z");
		t.getAxisAndAngle(v, a);
		// not sure if this is right
		Vector3Test.assertEquals("vector angle", new double[] { 0., 0., -1. },
				v, EPS);
		Assert.assertEquals("vector angle", Math.PI / 2., a.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.getTranslation()'
	 */
	@Test
	public void testGetTranslation() {
		Transform3 t = new Transform3("x+1/2, y+1/4, z+1/6");
		Vector3Test.assertEquals("transform3 translation", new double[] { 0.5,
				0.25, 1. / 6. }, t.getTranslation(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.setTranslation()'
	 */
	@Test
	public void testSetTranslation() {
		Transform3 t = new Transform3("x+1/2, y+1/4, z+1/6");
		t.incrementTranslation(new Vector3(0.6, 0.7, 0.8));
		Vector3Test.assertEquals("transform3 increment translation",
				new double[] { 1.1, 0.95, 0.8 + (1. / 6.) },
				t.getTranslation(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.getCentreOfRotation()'
	 */
	@Test
	public void testGetCentreOfRotation() {
		Transform3 t = new Transform3("-x+1/2, -y+1/2, z");
		Transform3Test.assertEquals("transform3 translation", new double[] {
				-1.0, 0.0, 0.0, 0.5, 0.0, -1.0, 0.0, 0.5, 0.0, 0.0, 1.0, 0.,
				0.0, 0.0, 0.0, 0.0 }, t, EPS);
		// not sure if this is right
		Point3 p = t.getCentreOfRotation();
		Point3Test.assertEquals("transform3 centre", new double[] { 0.5, 0.5,
				0.0 }, p, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.getScales()'
	 */
	@Test
	public void testGetScales() {
		Transform3 t = new Transform3(new double[] { 10., 0., 0., 0., 0., 20.,
				0., 0., 0., 0., 30., 0., 0., 0., 0., 1.

		});
		RealArray scales = t.getScales();
		RealArrayTest.assertEquals("scales", new double[] { 10., 20., 30. },
				scales, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Transform3.getRotationMatrix()'
	 */
	@Test
	public void testGetRotationMatrix() {
		Transform3 t = new Transform3(new Angle(Math.PI / 2.), new Angle(
				Math.PI / 2.), new Angle(Math.PI / 2.));
		// not sure if this is right
		Transform3Test.assertEquals("transform3 vector", new double[] { 0., 0.,
				1., 0., 0., -1., 0., 0., 1., 0., 0., 0., 0., 0., 0., 1. }, t,
				EPS);
		RealMatrixTest.assertEquals("rotation matrix", 3, 3, new double[] { 0.,
				0., 1., 0., -1., 0., 1., 0., 0. }, t.getRotationMatrix(), EPS);
	}

}
