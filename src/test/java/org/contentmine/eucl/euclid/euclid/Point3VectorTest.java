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
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.Int;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.eucl.euclid.Line3;
import org.contentmine.eucl.euclid.Plane3;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.Point3Vector;
import org.contentmine.eucl.euclid.Real3Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform3;
import org.contentmine.eucl.euclid.Vector3;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Point3Vector.
 * 
 * @author pmr
 * 
 */
public class Point3VectorTest {

	Point3Vector p0;
	Point3Vector p1;
	Point3Vector p2;
	final static double s2 = Math.sqrt(2.);
	final static double s3 = Math.sqrt(3.);

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		p0 = new Point3Vector();
		p1 = new Point3Vector(new double[] { 11., 21., 31., 12., 22., 32., 13.,
				23., 33., 14., 24., 34. });
		p2 = new Point3Vector(new double[] { 1., 0., 0., 0., 0., 0., 0., 1.,
				0., 0., 1., 1. });
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
	public static void assertEquals(String msg, Point3Vector test,
			Point3Vector expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
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
			Point3Vector expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getArray().length);
		DoubleTestBase.assertEquals(msg, test, expected.getArray(), epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.Point3Vector()'
	 */
	@Test
	public void testPoint3Vector() {
		Assert.assertNotNull("p3v", p0);
		Assert.assertEquals("p3v", 0, p0.size());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.Point3Vector(double[])'
	 */
	@Test
	public void testPoint3VectorDoubleArray() {
		Assert.assertNotNull("p3v", p1);
		Assert.assertEquals("p3v", 4, p1.size());
		try {
			new Point3Vector(new double[] { 1., 2., 3., 4. });
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("bad array",
					"array length must be multiple of 3", e.getMessage());
		}

	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.Point3Vector(int,
	 * double[], double[], double[])'
	 */
	@Test
	public void testPoint3VectorIntDoubleArrayDoubleArrayDoubleArray() {
		new Point3Vector(3, new double[] { 11., 12., 13., }, new double[] {
				21., 22., 23., }, new double[] { 31., 32., 33., });
		try {
			new Point3Vector(3, new double[] { 11., 12., 13., }, new double[] {
					21., 22., 23., }, new double[] { 31., 32., });
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("bad array", "array size required (3) found 2",
					e.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.Point3Vector(RealArray)'
	 */
	@Test
	public void testPoint3VectorRealArray() {
		Point3Vector p3v = new Point3Vector(new RealArray(new double[] { 11.,
				12., 13., 21., 22., 23., 31., 32., 33., 41., 42., 43., }));
		Assert.assertEquals("p3v", 4, p3v.size());
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3Vector.Point3Vector(Point3Vector)'
	 */
	@Test
	public void testPoint3VectorPoint3Vector() {
		Point3Vector p = new Point3Vector(p1);
		Assert.assertTrue("copy", p.isEqualTo(p1));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.size()'
	 */
	@Test
	public void testSize() {
		Assert.assertEquals("size", 4, p1.size());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.add(Point3)'
	 */
	@Test
	public void testAdd() {
		Point3 p = new Point3(10., 11., 12.);
		Point3Test.assertEquals("add", new double[] { 14., 24., 34. }, p1
				.get(3), EPS);
		Assert.assertEquals("add", 4, p1.size());
		p1.add(p);
		Assert.assertEquals("add", 5, p1.size());
		Point3Test.assertEquals("add", p, p1.get(4), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.setElementAt(Point3,
	 * int)'
	 */
	@Test
	public void testSetElementAtPoint3Int() {
		Point3 p = new Point3(51., 52., 53.);
		p1.setElementAt(p, 2);
		Point3VectorTest.assertEquals("set", new double[] { 11., 21., 31., 12.,
				22., 32., 51., 52., 53., 14., 24., 34., }, p1, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.elementAt(int)'
	 */
	@Test
	public void testElementAt() {
		Point3 p = p1.elementAt(2);
		Point3Test.assertEquals("get", new double[] { 13., 23., 33. }, p, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.get(int)'
	 */
	@Test
	public void testGet() {
		Point3 p = p1.get(2);
		Point3Test.assertEquals("get", new double[] { 13., 23., 33. }, p, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.addElement(Point3)'
	 */
	@Test
	public void testAddElement() {
		Point3 p = new Point3(51., 52., 53.);
		p1.addElement(p);
		Point3VectorTest.assertEquals("set", new double[] { 11., 21., 31., 12.,
				22., 32., 13., 23., 33., 14., 24., 34., 51., 52., 53., }, p1,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.setElementAt(Vector3,
	 * int)'
	 */
	@Test
	public void testSetElementAtVector3Int() {
		Vector3 p = new Vector3(51., 52., 53.);
		p1.setElementAt(p, 2);
		Point3VectorTest.assertEquals("set", new double[] { 11., 21., 31., 12.,
				22., 32., 51., 52., 53., 14., 24., 34., }, p1, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getRange(Axis3)'
	 */
	@Test
	public void testGetRange() {
		RealRange r = p1.getRange(Axis3.X);
		Assert.assertEquals("range", 11., r.getMin(), EPS);
		Assert.assertEquals("range", 14., r.getMax(), EPS);
		r = p1.getRange(Axis3.Y);
		Assert.assertEquals("range", 21., r.getMin(), EPS);
		Assert.assertEquals("range", 24., r.getMax(), EPS);
		r = p1.getRange(Axis3.Z);
		Assert.assertEquals("range", 31., r.getMin(), EPS);
		Assert.assertEquals("range", 34., r.getMax(), EPS);
		Point3 p = new Point3(51., 52., 53.);
		p1.addElement(p);
		r = p1.getRange(Axis3.X);
		Assert.assertEquals("range", 11., r.getMin(), EPS);
		Assert.assertEquals("range", 51., r.getMax(), EPS);
		r = p1.getRange(Axis3.Y);
		Assert.assertEquals("range", 21., r.getMin(), EPS);
		Assert.assertEquals("range", 52., r.getMax(), EPS);
		r = p1.getRange(Axis3.Z);
		Assert.assertEquals("range", 31., r.getMin(), EPS);
		Assert.assertEquals("range", 53., r.getMax(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getRange3()'
	 */
	@Test
	public void testGetRange3() {
		Real3Range rr = p1.getRange3();
		RealRange r = rr.getXRange();
		Assert.assertEquals("range", 11., r.getMin(), EPS);
		Assert.assertEquals("range", 14., r.getMax(), EPS);
		r = rr.getYRange();
		Assert.assertEquals("range", 21., r.getMin(), EPS);
		Assert.assertEquals("range", 24., r.getMax(), EPS);
		r = rr.getZRange();
		Assert.assertEquals("range", 31., r.getMin(), EPS);
		Assert.assertEquals("range", 34., r.getMax(), EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3Vector.getSigmaDeltaSquared(Point3Vector)'
	 */
	@Test
	public void testGetSigmaDeltaSquared() {
		Point3Vector p = new Point3Vector(p1);
		double d = p1.getSigmaDeltaSquared(p);
		Assert.assertEquals("sigma", 0.0, d, EPS);
		for (Point3 pp : p.getPoint3List()) {
			pp.plusEquals(new Vector3(0.1, 0.2, 0.3));
		}
		d = p1.getSigmaDeltaSquared(p);
		Assert.assertEquals("sigma", 0.56, d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.subArray(IntSet)'
	 */
	@Test
	public void testSubArray() {
		Point3Vector sub = p1.subArray(new IntSet(new int[] { 3, 1, 2 }));
		Point3VectorTest.assertEquals("set", new double[] { 14.0, 24.0, 34.0,
				12.0, 22.0, 32.0, 13.0, 23.0, 33.0 }, sub, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.plus(Point3Vector)'
	 */
	@Test
	public void testPlusPoint3Vector() {
		Point3Vector p = new Point3Vector(new double[] { 61., 62., 63., 71.,
				72., 73. });
		try {
			p1.plus(p);
			Assert.fail("should always throw " + "incompatible sizes");
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("plus", "incompatible Point3Vector sizes: 4/2",
					e.getMessage());
		}
		p = new Point3Vector(new double[] { 61., 62., 63., 71., 72., 73., 81.,
				82., 83., 91., 92., 93. });
		p1 = p1.plus(p);
		Point3VectorTest.assertEquals("plus", new double[] { 72.0, 83.0, 94.0,
				83.0, 94.0, 105.0, 94.0, 105.0, 116.0, 105.0, 116.0, 127.0 },
				p1, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.subtract(Point3Vector)'
	 */
	@Test
	public void testSubtractPoint3Vector() {
		Point3Vector p = new Point3Vector(new double[] { 61., 62., 63., 71.,
				72., 73. });
		try {
			p1.subtract(p);
			Assert.fail("should always throw " + "incompatible sizes");
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("plus", "incompatible Point3Vector sizes: 4/2",
					e.getMessage());
		}
		p = new Point3Vector(new double[] { 61., 62., 63., 71., 72., 73., 81.,
				82., 83., 91., 92., 93. });
		p1 = p.subtract(p1);
		Point3VectorTest
				.assertEquals("plus", new double[] { 50.0, 41.0, 32.0, 59.0,
						50.0, 41.0, 68.0, 59.0, 50.0, 77.0, 68.0, 59.0 }, p1,
						EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getLine(int, int)'
	 */
	@Test
	public void testGetLine() {
		Line3 l = p1.getLine(1, 2);
		Vector3Test.assertEquals("line", new double[] { 0.5773502691896258,
				0.5773502691896258, 0.5773502691896258 }, l.getVector(), EPS);
		Point3Test.assertEquals("line", new double[] { 12.0, 22.0, 32.0 }, l
				.getPoint(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getCentroid()'
	 */
	@Test
	public void testGetCentroid() {
		Point3 p = p1.getCentroid();
		Point3Test.assertEquals("centroid", new double[] { 12.5, 22.5, 32.5 },
				p, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.plus(Vector3)'
	 */
	@Test
	public void testPlusVector3() {
		Point3Vector p = p1.plus(new Vector3(10., 20., 30.));
		Point3VectorTest.assertEquals("plus vector", new double[] { 21.0, 41.0,
				61.0, 22.0, 42.0, 62.0, 23.0, 43.0, 63.0, 24.0, 44.0, 64.0 },
				p, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.subtract(Vector3)'
	 */
	@Test
	public void testSubtractVector3() {
		Point3Vector p = p1.subtract(new Vector3(10., 20., 30.));
		Point3VectorTest
				.assertEquals("subtract vector", new double[] { 1.0, 1.0, 1.0,
						2.0, 2.0, 2.0, 3.0, 3.0, 3.0, 4.0, 4.0, 4.0 }, p, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.moveToCentroid()'
	 */
	@Test
	public void testMoveToCentroid() {
		p1.moveToCentroid();
		Point3VectorTest.assertEquals("move to centroid", new double[] { -1.5,
				-1.5, -1.5, -0.5, -0.5, -0.5, 0.5, 0.5, 0.5, 1.5, 1.5, 1.5 },
				p1, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.inertialTensor()'
	 */
	@Test
	public void testInertialTensor() {
		Point3Vector p = new Point3Vector(new double[] { 1., 2., 3., 2., 4.,
				6., 3., 6., 9., 4., 1., 0., 3., 6., 1. });
		RealMatrix m = p.calculateNonMassWeightedInertialTensorOld();
		RealMatrixTest.assertEquals("move to centroid", 3, 3, new double[] {
				5.2, 0.6, -4.4, 0.6, 20.8, 17.8, -4.4, 17.8, 54.8 }, m, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.transform(Transform3)'
	 */
	@Test
	public void testTransformTransform3() {
		Transform3 t = new Transform3("y, -x, z");
		p1.transform(t);
		Point3VectorTest.assertEquals("transform",
				new double[] { 21.0, -11.0, 31.0, 22.0, -12.0, 32.0, 23.0,
						-13.0, 33.0, 24.0, -14.0, 34.0 }, p1, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.transform(Transform3,
	 * IntSet)'
	 */
	@Test
	public void testTransformTransform3IntSet() {
		Transform3 t = new Transform3("y, -x, z");
		p1.transform(t, new IntSet(new int[] { 3, 1, 2 }));
		Point3VectorTest.assertEquals("transform",
				new double[] { 11.0, 21.0, 31.0, 22.0, -12.0, 32.0, 23.0,
						-13.0, 33.0, 24.0, -14.0, 34.0 }, p1, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.distance(int, int)'
	 */
	@Test
	public void testDistanceIntInt() {
		double d = p1.distance(1, 3);
		Assert.assertEquals("distance", 3.464101, d, 0.00001);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.distance(IntSet)'
	 */
	@Test
	public void testDistanceIntSet() {
		double d = 0;
		try {
			d = p1.distance(new IntSet(new int[] { 3, 1, 2 }));
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("distance",
					"int set must have exactly 2 points", e.getMessage());
		}
		d = p1.distance(new IntSet(new int[] { 3, 1 }));
		Assert.assertEquals("distance", 3.464101, d, 0.00001);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.angle(int, int, int)'
	 */
	@Test
	public void testAngleIntIntInt() {
		Angle a = p2.angle(1, 2, 3);
		Assert.assertEquals("angle", Math.PI / 2., a.getRadian(), EPS);
		a = p2.angle(3, 1, 2);
		Assert.assertEquals("angle", Math.PI / 4., a.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.angle(IntSet)'
	 */
	@Test
	public void testAngleIntSet() {
		Angle a = p2.angle(new IntSet(new int[] { 1, 2, 3 }));
		Assert.assertEquals("angle", Math.PI / 2., a.getRadian(), EPS);
		a = p2.angle(new IntSet(new int[] { 2, 0, 1 }));
		Assert.assertEquals("angle", Math.PI / 4., a.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.torsion(int, int, int,
	 * int)'
	 */
	@Test
	public void testTorsionIntIntIntInt() {
		Angle t = p2.torsion(0, 1, 2, 3);
		Assert.assertEquals("torsion", -Math.PI / 2., t.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.torsion(IntSet)'
	 */
	@Test
	public void testTorsionIntSet() {
		Angle t = p2.torsion(new IntSet(new int[] { 0, 1, 2, 3 }));
		Assert.assertEquals("torsion", -Math.PI / 2., t.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getDistanceMatrix()'
	 */
	@Test
	public void testGetDistanceMatrix() {
		RealSquareMatrix rsm = p2.getDistanceMatrix();
		RealSquareMatrixTest.assertEquals("distance matrix", 4,
				new double[] { 0., 1., s2, s3, 1., 0., 1., s2, s2, 1., 0., 1.,
						s3, s2, 1., 0. }, rsm, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3Vector.deviationsFromPlane(Plane3)'
	 */
	@Test
	public void testDeviationsFromPlane() {
		Plane3 pl = new Plane3(1., 0., 0., 0.5);
		RealArray ra = p2.deviationsFromPlane(pl);
		RealArrayTest.assertEquals("deviations", new double[] { 0.5, -0.5,
				-0.5, -0.5 }, ra, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getPoint3(int)'
	 */
	@Test
	public void testGetPoint3() {
		Point3 p = p1.getPoint3(1);
		Point3Test.assertEquals("get point", new double[] { 12., 22., 32. }, p,
				EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getXYZ()'
	 */
	@Test
	public void testGetXYZ() {
		RealArray ra = p1.getXYZ();
		RealArrayTest.assertEquals("get point", new double[] { 11.0, 21.0,
				31.0, 12.0, 22.0, 32.0, 13.0, 23.0, 33.0, 14.0, 24.0, 34.0 },
				ra, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getCoordinate(int,
	 * Axis3)'
	 */
	@Test
	public void testGetCoordinateIntAxis3() {
		double d = p1.getCoordinate(3, Axis3.Y);
		Assert.assertEquals("get coord", 24., d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.getXYZ(Axis3)'
	 */
	@Test
	public void testGetXYZAxis3() {
		RealArray ra = p1.getXYZ(Axis3.Y);
		RealArrayTest.assertEquals("get XYZ",
				new double[] { 21., 22., 23., 24. }, ra, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.rms(Point3Vector)'
	 */
	@Test
	public void testRms() {
		p1.moveToCentroid();
		p2.moveToCentroid();
		double d = p1.rms(p2);
		Assert.assertEquals("rms", 0.9185586535436918, d, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3Vector.getFurthestPointFrom(Point3)'
	 */
	@Test
	public void testGetFurthestPointFrom() {
		int idx = p1.getFurthestPointFrom(new Point3(0., 0., 0.));
		Assert.assertEquals("furthest point", 3, idx);

	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3Vector.getPointMakingSmallestAngle(Point3,
	 * Point3)'
	 */
	@Test
	public void testGetPointMakingSmallestAngle() {
		int idx = p1.getPointMakingSmallestAngle(new Point3(0., 0., 0.),
				new Point3(20., 15., 10.));
		Assert.assertEquals("smallest angle point", 3, idx);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3Vector.alignUsing3Points(Point3Vector)'
	 */
	@Test
	public void testAlignUsing3Points() {
		p1.add(new Point3(20., 10., 0.));
		p2.add(new Point3(5., 10., 5.));
		Transform3 t = p1.alignUsing3Points(p2);
		Transform3Test.assertEquals("align", new double[] {
				0.11432809806666633, 0.8588003169190181, -0.4993907304428592,
				0.0, 0.6186208633307761, -0.45487483749749846,
				-0.6406224392444506, 0.0, -0.7773270312065208,
				-0.2356923797483021, -0.5832767685106605, 0.0, 0.0, 0.0, 0.0,
				1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.get3SeparatedPoints()'
	 */
	@Test
	public void testGet3SeparatedPoints() {
		p1.add(new Point3(20., 10., 0.));
		int[] idx = p1.get3SeparatedPoints();
		String s = Int.testEquals((new int[] { 4, 3, 0 }), idx);
		if (s != null) {
			Assert.fail("separated points" + "; " + s);
		}
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Point3Vector.align3PointVectors(Point3Vector)'
	 */
	@Test
	public void testAlign3PointVectors() {
		Transform3 t = null;
		try {
			t = p1.align3PointVectors(p2);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("align", "this requires 3 points", e
					.getMessage());
		}
		Point3Vector pa = new Point3Vector(new double[] { 1., 0., 0., 0., 1.,
				0., 0., 0., 1. });
		Point3Vector pb = new Point3Vector(new double[] { 0., 1., 0., 1., 0.,
				0., 0., 0., 1. });
		t = pa.align3PointVectors(pb);
		Transform3Test.assertEquals("align", new double[] { 0.0, 0.0, -1.0,
				0.0, -1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-1.0 }, t, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.roughAlign(Point3Vector)'
	 */
	@Test
	public void testRoughAlign() {
		Transform3 t = null;
		Point3Vector pthis = new Point3Vector(new double[] { 1., -1., 0., 0.,
				1., -1., -1., 0., 1. });
		Point3Vector pref = new Point3Vector(new double[] { 0., 1., -1., 1.,
				-1., 0., -1., 0., 1. });
		t = pthis.roughAlign(pref);
		pthis.transform(t);
		Point3VectorTest.assertEquals("transformed pthis", pref, pthis,
				0.000001);

		pref = new Point3Vector(new double[] { 12., 0., 1., 11., 2., 0., 10.,
				1., 2. });
		t = pthis.roughAlign(pref);
		pthis.transform(t);
		RealArray.round(pthis.getArray(), 6);
		Point3VectorTest.assertEquals("transformed pthis", pref, pthis,
				0.000001);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Point3Vector.fitTo(Point3Vector)'
	 */
	@Test
	public void testFitTo() {
		Transform3 t = null;
		Point3Vector pa = new Point3Vector(new double[] { 1., 0., 0., 0., 1.,
				0., 0., 0., 1. });
		Point3Vector pb = new Point3Vector(new double[] { 0., 1., 0., 0., 0.,
				1., 1., 0., 0. });
		t = pa.fitTo(pb);
		pa.transform(t);
		double rms = pa.rms(pb);
		Assert.assertEquals("rms", 0.0, rms, 0.000000001);
		DoubleTestBase.assertEquals("align", new double[] { 0.0, 0.0, 1.0, 0.0,
				1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 }, t
				.getMatrixAsArray(), EPS);

		pb = new Point3Vector(new double[] { 10., 1.1, 0., 10., 0., 0.9, 11.1,
				0., 0. });
		t = pa.fitTo(pb);
		pa.transform(t);
		rms = pa.rms(pb);
		Assert.assertEquals("rms", 0.03470386605101721, rms, 0.0001);
	}

	@Test
	public void calculateInertialTensor() {
		Point3Vector p3v = new Point3Vector(
			new double[] {
			3., 0., 0.,
			0., 2., 0.,
			0., 0., 1.,
			-3., 0., 0.,
			0., -2., 0.,
			0., 0., -1.}
		);
		RealSquareMatrix rsm = p3v.calculateNonMassWeightedInertialTensor();
		RealSquareMatrixTest.assertEquals("rsm", 3,
				new double[]{
				10.0,  0.0,  0.0,
				 0.0, 20.0,  0.0,
				 0.0,  0.0, 26.0,
				},
				rsm, 0.000001);
		RealArray realArray = rsm.calculateEigenvalues();
		RealArrayTest.assertEquals("eigval ", 
				new double[]{26.0, 20.0, 10.0}, realArray, 0.000001);
	}

	@Test
	public void calculateRotationToInertialAxes() {
		Point3Vector p3v = new Point3Vector(
			new double[] {
					3., 0., 0.,
					0., 2., 0.,
					0., 0., 1.,
					-3., 0., 0.,
					0., -2., 0.,
					0., 0., -1.}
		);
		// arbitrary transform
		Transform3 t = new Transform3(new Angle(0.1), new Angle(0.2), new Angle(0.3));
		p3v.transform(t);
		RealSquareMatrix eigvec = p3v.calculateRotationToInertialAxes();
		Transform3 tt = new Transform3(eigvec);
		p3v.transform(tt);
		Point3VectorTest.assertEquals("transform to axes ", 
				new double[]{
				0.0,  0.0,  3.0,
				0.0,  2.0,  0.0,
			   -1.0,  0.0,  0.0,
				0.0,  0.0, -3.0,
				0.0, -2.0,  0.0,
			    1.0,  0.0,  0.0,
		},
			p3v, 0.000001);
	}
}
