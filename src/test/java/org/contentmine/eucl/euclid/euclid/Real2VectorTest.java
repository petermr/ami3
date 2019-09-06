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

import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Real2Vector;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * tests real2Vector.
 * 
 * @author pmr
 * 
 */
public class Real2VectorTest {

	Real2Vector r0;
	Real2Vector r1;
	Real2Vector r2;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		r0 = new Real2Vector();
		r1 = new Real2Vector(new double[] { 1., 2., 3., 4., 5., 6., });
		r2 = new Real2Vector(new double[] { 1., 2., 3., 4., 5., 6., 7., 8., 9.,
				10. });
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
	public static void assertEquals(String msg, Real2Vector expected,
			Real2Vector test, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, expected.getXY().getArray(), test
				.getXY().getArray(), epsilon);
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
			Real2Vector expected, double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getXY().getArray().length);
		DoubleTestBase.assertEquals(msg, test, expected.getXY().getArray(),
				epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.Real2Vector()'
	 */
	@Test
	public void testReal2Vector() {
		Assert.assertEquals("r2v", 0, r0.size());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.Real2Vector(double[])'
	 */
	@Test
	public void testReal2VectorDoubleArray() {
		Real2Vector r = new Real2Vector(new double[] { 1., 2., 3., 4., 5., 6. });
		Assert.assertEquals("r2v", 3, r.size());
		try {
			r = new Real2Vector(new double[] { 1., 2., 3., 4., 5. });
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("r2v", "size must be multiple of 2", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.Real2Vector(int, double[],
	 * double[])'
	 */
	@Test
	public void testReal2VectorIntDoubleArrayDoubleArray() {
		Real2Vector r = new Real2Vector(3, new double[] { 1., 2., 3. },
				new double[] { 4., 5., 6. });
		Assert.assertEquals("r2v", 3, r.size());
		try {
			r = new Real2Vector(3, new double[] { 1., 2., 3. }, new double[] {
					4., 5. });
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("r2v", "array size required (3) found 2", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.Real2Vector(RealArray)'
	 */
	@Test
	public void testReal2VectorRealArray() {
		Real2Vector r = new Real2Vector(new RealArray(new double[] { 1., 2.,
				3., 4., 5., 6. }));
		Assert.assertEquals("r2v", 3, r.size());
		try {
			r = new Real2Vector(new RealArray(
					new double[] { 1., 2., 3., 4., 5. }));
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("r2v", "size must be multiple of 2", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.Real2Vector(Real2Vector)'
	 */
	@Test
	public void testReal2VectorReal2Vector() {

		Real2Vector r = new Real2Vector(r1);
		Assert.assertEquals("r2v", 3, r.size());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.add(Real2)'
	 */
	@Test
	public void testAdd() {
		r1.add(new Real2(7., 8.));
		Assert.assertEquals("add", 4, r1.size());
		Assert.assertEquals("add", 7., r1.get(3).getX(),EPS);
		Assert.assertEquals("add", 8., r1.get(3).getY(),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.set(int, Real2)'
	 */
	@Test
	public void testSet() {
		r1.set(1, new Real2(9., 8.));
		Assert.assertEquals("set", 9., r1.get(1).getX(),EPS);
		Assert.assertEquals("set", 8., r1.get(1).getY(),EPS);
		Assert.assertEquals("add", 3, r1.size());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getRange(Axis2)'
	 */
	@Test
	public void testGetRange() {
		RealRange r = r1.getRange(Axis2.X);
		Assert.assertEquals("range", 1., r.getMin(),EPS);
		Assert.assertEquals("range", 5., r.getMax(),EPS);
		r1.set(0, new Real2(-9., 8.));
		r1.set(1, new Real2(9., 8.));
		r = r1.getRange(Axis2.X);
		Assert.assertEquals("range", -9., r.getMin(),EPS);
		Assert.assertEquals("range", 9., r.getMax(),EPS);
		r = r1.getRange(Axis2.Y);
		Assert.assertEquals("range", 6., r.getMin(),EPS);
		Assert.assertEquals("range", 8., r.getMax(),EPS);
		r1.set(0, new Real2(-9., 8.));
		r1.set(1, new Real2(9., 8.));
		r = r1.getRange(Axis2.Y);
		Assert.assertEquals("range", 6., r.getMin(),EPS);
		Assert.assertEquals("range", 8., r.getMax(),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getRange2()'
	 */
	@Test
	public void testGetRange2() {
		Real2Range r2 = r1.getRange2();
		RealRange xr = r2.getXRange();
		RealRange yr = r2.getYRange();
		Assert.assertEquals("range", 1., xr.getMin(),EPS);
		Assert.assertEquals("range", 5., xr.getMax(),EPS);
		Assert.assertEquals("range", 2., yr.getMin(),EPS);
		Assert.assertEquals("range", 6., yr.getMax(),EPS);
		r1.set(0, new Real2(-9., 8.));
		r1.set(1, new Real2(9., 8.));
		r2 = r1.getRange2();
		xr = r2.getXRange();
		yr = r2.getYRange();
		Assert.assertEquals("range", -9., xr.getMin(),EPS);
		Assert.assertEquals("range", 9., xr.getMax(),EPS);
		Assert.assertEquals("range", 6., yr.getMin(),EPS);
		Assert.assertEquals("range", 8., yr.getMax(),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.subArray(IntSet)'
	 */
	@Test
	public void testSubArray() {
		Real2Vector sub = r2.subArray(new IntSet(new int[] { 3, 1, 2 }));
		Real2VectorTest.assertEquals("sub", new double[] { 7., 8., 3., 4., 5.,
				6. }, sub, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.subSet(Real2Range)'
	 */
	@Test
	public void testSubSet() {
		Real2Range rr = new Real2Range(new RealRange(2.5, 7.5), new RealRange(
				2.5, 7.5));
		IntSet is = null;
		is = r2.subSet(rr);
		IntSetTest.assertEquals("sub", new int[] { 1, 2 }, is);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getClosestPoint(Real2)'
	 */
	@Test
	public void testGetClosestPoint() {
		int ip = r2.getClosestPoint(new Real2(3.5, 6.7));
		Assert.assertEquals("closest", 2, ip);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getPoint(Real2, double,
	 * double)'
	 */
	@Test
	public void testGetPoint() {
		int ip = r2.getPoint(new Real2(4., 5.), 0.5, 0.5);
		Assert.assertEquals("closest", -1, ip);
		ip = r2.getPoint(new Real2(4., 5.), 2.0, 2.0);
		Assert.assertEquals("closest", 1, ip);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.translateBy(Real2)'
	 */
	@Test
	public void testTranslateBy() {
		r2.translateBy(new Real2(2., 3.));
		Real2VectorTest.assertEquals("translate", new double[] { 3., 5., 5.,
				7., 7., 9., 9., 11., 11., 13. }, r2, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.plus(Real2)'
	 */
	@Test
	public void testPlus() {
		r2.plus(new Real2(2., 3.));
		Real2VectorTest.assertEquals("plus", new double[] { 3., 5., 5., 7., 7.,
				9., 9., 11., 11., 13. }, r2, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.subtract(Real2)'
	 */
	@Test
	public void testSubtract() {
		r2.subtract(new Real2(2., 3.));
		Real2VectorTest.assertEquals("subtract", new double[] { -1., -1., 1.,
				1., 3., 3., 5., 5., 7., 7. }, r2, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.multiplyBy(double)'
	 */
	@Test
	public void testMultiplyBy() {
		r2.multiplyBy(2.);
		Real2VectorTest.assertEquals("subtract", new double[] { 2., 4., 6., 8.,
				10., 12., 14., 16., 18., 20. }, r2, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.distance(int, int)'
	 */
	@Test
	public void testDistanceIntInt() {
		double d = r2.distance(2, 3);
		Assert.assertEquals("distance", Math.sqrt(8.), d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.distance(IntSet)'
	 */
	@Test
	public void testDistanceIntSet() {
		double d = r2.distance(new IntSet(new int[] { 2, 3 }));
		Assert.assertEquals("distance", Math.sqrt(8.), d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.angle(int, int, int)'
	 */
	@Test
	public void testAngleIntIntInt() {
		Angle a = r2.angle(1, 2, 3);
		Assert.assertEquals("angle", -Math.PI, a.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.angle(IntSet)'
	 */
	@Test
	public void testAngleIntSet() {
		Angle a = r2.angle(new IntSet(new int[] { 1, 2, 3 }));
		Assert.assertEquals("angle", -Math.PI, a.getRadian(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getReal2(int)'
	 */
	@Test
	public void testGetReal2() {
		Real2 r = r2.getReal2(1);
		Assert.assertEquals("angle", 3., r.getX(), EPS);
		Assert.assertEquals("angle", 4., r.getY(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getXY()'
	 */
	@Test
	public void testGetXY() {
		RealArray ra = r2.getXY();
		RealArrayTest.assertEquals("getXY", new double[] { 1., 2., 3., 4., 5.,
				6., 7., 8., 9., 10. }, ra, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getCoordinate(int, Axis2)'
	 */
	@Test
	public void testGetCoordinate() {
		double d = r2.getCoordinate(2, Axis2.X);
		Assert.assertEquals("coord", 5., d, EPS);
		d = r2.getCoordinate(3, Axis2.Y);
		Assert.assertEquals("coord", 8., d, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getXorY(Axis2)'
	 */
	@Test
	public void testGetXorY() {
		RealArray ra = r2.getXorY(Axis2.X);
		RealArrayTest.assertEquals("Xarray",
				new double[] { 1., 3., 5., 7., 9. }, ra, EPS);
		ra = r2.getXorY(Axis2.Y);
		RealArrayTest.assertEquals("Yarray",
				new double[] { 2., 4., 6., 8., 10. }, ra, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.swapXY()'
	 */
	@Test
	public void testSwapXY() {
		r2.swapXY();
		Real2VectorTest.assertEquals("getXY", new double[] { 2., 1., 4., 3.,
				6., 5., 8., 7., 10., 9. }, r2, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.sortAscending(Axis2)'
	 */
	@Test
	public void testSortAscending() {
		Real2Vector r2v = new Real2Vector(new double[] { 1., 3., 5., 2., 7.,
				1., 2., 6. });
		r2v = r2v.sortAscending(Axis2.X);
		Real2VectorTest.assertEquals("sortAsc", new double[] { 1.0, 3.0, 2.0,
				6.0, 5.0, 2.0, 7.0, 1.0 }, r2v, EPS);
		r2v = r2v.sortAscending(Axis2.Y);
		Real2VectorTest.assertEquals("sortAsc", new double[] { 7.0, 1.0, 5.0,
				2.0, 1.0, 3.0, 2.0, 6.0 }, r2v, EPS);
		r2v = r2v.sortDescending(Axis2.X);
		Real2VectorTest.assertEquals("sortAsc", new double[] { 7.0, 1.0, 5.0,
				2.0, 2.0, 6.0, 1.0, 3.0 }, r2v, EPS);
		r2v = r2v.sortDescending(Axis2.Y);
		Real2VectorTest.assertEquals("sortAsc", new double[] { 2.0, 6.0, 1.0,
				3.0, 5.0, 2.0, 7.0, 1.0 }, r2v, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.transformBy(Transform2)'
	 */
	@Test
	public void testTransformBy() {
		Transform2 t = new Transform2(new Angle(Math.PI / 2.));
		r1.transformBy(t);
		Real2VectorTest.assertEquals("transform", new double[] { 2.0, -1.0,
				4.0, -3.0, 6.0, -5.0 }, r1, EPS);

	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Real2Vector.getSquaredDifference(Real2Vector)'
	 */
	@Test
	public void testGetSquaredDifference() {
		Real2Vector r = new Real2Vector(new double[] { 1.1, 2.2, 3.3, 4.4, 5.5,
				6.6 });
		double d = r1.getSquaredDifference(r);
		Assert.assertEquals("squared difference", 0.91, d, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Real2Vector.getSquaredDistances(Real2Vector)'
	 */
	@Test
	public void testGetSquaredDistances() {
		Real2Vector r = new Real2Vector(new double[] { 1.1, 2.2, 3.3, 4.4, 5.5,
				6.6 });
		double[] d = r1.getSquaredDistances(r);
		DoubleTestBase.assertEquals("squared distances", new double[] { 0.05,
				0.25, 0.61 }, d, EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Real2Vector.rotateAboutCentroid(Angle)'
	 */
	@Test
	public void testRotateAboutCentroid() {
		Real2VectorTest.assertEquals("transform", new double[] { 1., 2., 3.,
				4., 5., 6., }, r1, EPS);
		r1.rotateAboutCentroid(new Angle(Math.PI / 4.));
		Real2VectorTest.assertEquals("transform", new double[] {
				3. - Math.sqrt(8.), 4.0, 3.0, 4.0, 3. + Math.sqrt(8.), 4.0 },
				r1, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.regularPolygon(int,
	 * double)'
	 */
	@Test
	public void testRegularPolygon() {
		Real2Vector p = Real2Vector.regularPolygon(3, 1.0);
		Real2VectorTest.assertEquals("polygon", new double[] { 0.0, 1.0,
				0.8660254037844387, -0.5, -0.8660254037844385, -0.5 }, p, EPS);

	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Vector.getCentroid()'
	 */
	@Test
	public void testGetCentroid() {
		Real2Vector r = new Real2Vector(new double[] { 1.1, 2.2, 3.3, 4.4, 5.5,
				6.6 });
		Real2 c = r.getCentroid();
		Assert.assertEquals("centroid", 3.3, c.getX(), EPS);
		Assert.assertEquals("centroid", 4.4, c.getY(), EPS);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Real2Vector.getSerialOfNearestPoint(Real2)'
	 */
	@Test
	public void testGetSerialOfNearestPoint() {
		Real2Vector r = new Real2Vector(new double[] { 1.1, 2.2, 3.3, 4.4, 5.5,
				6.6 });
		int idx = r.getSerialOfNearestPoint(new Real2(3., 4.));
		Assert.assertEquals("nearest point", 1, idx);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Real2Vector.getDistanceMatrix(List<Real2>)'
	 */
	@Test
	public void testGetDistanceMatrix() {
		List<Real2> pointList = new ArrayList<Real2>();
		pointList.add(new Real2(1.1, 2.2));
		pointList.add(new Real2(3.3, 4.4));
		RealMatrix rsm = r1.getDistanceMatrix(pointList);
		DoubleTestBase.assertEquals("distance matrix", new double[] {
				0.22360679774997916, 3.3241540277189325, 2.6172504656604803,
				0.5000000000000001, 5.445181356024793, 2.33452350598575 }, rsm
				.getMatrixAsArray(), EPS);

	}

	/**
	 * Test method for
	 * {@link org.contentmine.eucl.euclid.Real2Vector#Real2Vector(java.util.List)}.
	 */
	@Test
	public final void testReal2VectorListOfReal2() {
		List<Real2> lr2 = new ArrayList<Real2>();
		lr2.add(new Real2(1., 2.));
		lr2.add(new Real2(3., 4.));
		Real2Vector r2v = new Real2Vector(lr2);
		Real2VectorTest.assertEquals("copy", new Real2Vector(new double[] { 1.,
				2., 3., 4. }), r2v, EPS);
	}

	/**
	 * Test method for {@link org.contentmine.eucl.euclid.Real2Vector#get(int)}.
	 */
	@Test
	public final void testGet() {
		Real2Test.assertEquals("real list", new Real2(3., 4.), r2.get(1), EPS);
	}

	/**
	 * Test method for {@link org.contentmine.eucl.euclid.Real2Vector#size()}.
	 */
	@Test
	public final void testSize() {
		Assert.assertEquals("real list", 5, r2.size());
	}

	/**
	 * Test method for {@link org.contentmine.eucl.euclid.Real2Vector#getReal2List()}.
	 */
	@Test
	public final void testGetReal2List() {
		List<Real2> r2l = r2.getReal2List();
		Assert.assertEquals("real list", 5, r2l.size());
	}

	/**
	 * Test method for
	 * {@link org.contentmine.eucl.euclid.Real2Vector#sortDescending(org.contentmine.eucl.euclid.Axis.Axis2)}
	 * .
	 */
	@Test
	public final void testSortDescending() {
		List<Real2> lr = new ArrayList<Real2>();
		lr.add(new Real2(3., 5.));
		lr.add(new Real2(1., 2.));
		lr.add(new Real2(7., 1.));
		lr.add(new Real2(5., 4.));
		Real2Vector r2v = new Real2Vector(lr);
		r2v = r2v.sortDescending(Axis2.X);
		Real2VectorTest.assertEquals("sort", new double[] { 7.0, 1.0, 5.0, 4.0,
				3.0, 5.0, 1.0, 2.0, }, r2v, EPS);
		r2v = r2v.sortDescending(Axis2.Y);
		Real2VectorTest.assertEquals("sort", new double[] { 3.0, 5.0, 5.0, 4.0,
				1.0, 2.0, 7.0, 1.0, }, r2v, EPS);
	}

	/**
	 * Test method for
	 * {@link org.contentmine.eucl.euclid.Real2Vector#regularPolygon(int, double)}.
	 */
	@Test
	public final void testRegularPolygonIntDouble() {
		Real2Vector v = Real2Vector.regularPolygon(4, 1.0);
		Real2VectorTest.assertEquals("polygon", new Real2Vector(
				new Real2Vector(new double[] { 0.0, 1.0, 1.0, 0.0, 0.0, -1.0,
						-1.0, 0.0 })), v, EPS);
		v = Real2Vector.regularPolygon(6, 1.0);
	}

	/**
	 * Test method for
	 * {@link org.contentmine.eucl.euclid.Real2Vector#regularPolygon(int, double, double)}
	 * .
	 */
	@Test
	public final void testRegularPolygonIntDoubleDouble() {
		Real2Vector v = Real2Vector.regularPolygon(4, 1, 0.1);
		Real2VectorTest.assertEquals("polygon", new Real2Vector(
				new Real2Vector(new double[] { 0.09983341664682815,
						0.9950041652780258, 0.9950041652780257,
						-0.09983341664682818, -0.09983341664682811,
						-0.9950041652780258, -0.9950041652780258,
						0.09983341664682761, })), v, EPS);
	}

	/**
	 * Test method for
	 * {@link org.contentmine.eucl.euclid.Real2Vector#regularPolygon(int, org.contentmine.eucl.euclid.Real2, org.contentmine.eucl.euclid.Real2, boolean)}
	 * .
	 */
	@Test
	public final void testRegularPolygonIntReal2Real2Boolean() {
		Real2Vector v = null;
		v = Real2Vector.regularPolygon(5, new Real2(0.0, 0.0), new Real2(1.0,
				0.0), false);
		Real2VectorTest.assertEquals("polygon", new Real2Vector(
				new Real2Vector(new double[] { 0.0, 0.0, 1.0, 0.0,
						1.3090169943749475, -0.9510565162951536, 0.5,
						-1.5388417685876266, -0.30901699437494745,
						-0.9510565162951539 })), v, EPS);

		v = Real2Vector.regularPolygon(4, new Real2(0.0, 0.0), new Real2(3.0,
				5.0), false);
		Real2VectorTest.assertEquals("polygon", new Real2Vector(
				new Real2Vector(new double[] { 0.0, 0.0, 3.0, 5.0, 8.0, 2.0,
						5.0, -3.0, })), v, EPS);

		v = Real2Vector.regularPolygon(4, new Real2(1.0, 2.0), new Real2(3.0,
				5.0), false);
		Real2VectorTest.assertEquals("polygon", new Real2Vector(
				new Real2Vector(new double[] { 1.0, 2.0, 3.0, 5.0, 6.0, 3.0,
						4.0, 0.0, })), v, EPS);

		v = Real2Vector.regularPolygon(4, new Real2(3.0, 5.0), new Real2(1.0,
				2.0), false);
		Real2VectorTest.assertEquals("polygon", new Real2Vector(
				new Real2Vector(new double[] { 3.0, 5.0, 1.0, 2.0, -2.0, 4.0,
						0.0, 7.0 })), v, EPS);

		v = Real2Vector.regularPolygon(4, new Real2(3.0, 5.0), new Real2(1.0,
				2.0), true);
		Real2VectorTest.assertEquals("polygon", new Real2Vector(
				new Real2Vector(new double[] { 3.0, 5.0, 6.0, 3.0, 8.0, 6.0,
						5.0, 8.0 })), v, EPS);
	}

	/**
	 * 
	 */
	@Test
	public final void testGetXArrayYArray() {
		Real2Vector r2v = new Real2Vector();
		r2v.add(new Real2(1., 11.));
		r2v.add(new Real2(2., 12.));
		r2v.add(new Real2(3., 13.));
		r2v.add(new Real2(4., 14.));
		r2v.add(new Real2(5., 15.));
		RealArray xx = r2v.getXArray();
		RealArrayTest.assertEquals("x", new double[] { 1., 2., 3., 4., 5. },
				xx, 0.00001);
		RealArray yy = r2v.getYArray();
		RealArrayTest.assertEquals("y",
				new double[] { 11., 12., 13., 14., 15. }, yy, 0.00001);

	}

	/**
	 * 
	 */
	@Test
	public final void testIsInside() {
		Real2Vector r2v = new Real2Vector();
		r2v.add(new Real2(0., 0.));
		r2v.add(new Real2(0., 10.));
		r2v.add(new Real2(10., 10.));
		r2v.add(new Real2(10., 0.));
		Real2 point = new Real2(5., 3.);
		// simple box
		Assert.assertTrue("inside", r2v.encloses(point));
		// make concave
		r2v.add(new Real2(5., 5.));
		Assert.assertFalse("inside", r2v.encloses(point));
		// avoid point
		r2v.add(new Real2(6., 0.));
		Assert.assertTrue("inside", r2v.encloses(point));
	}

}
