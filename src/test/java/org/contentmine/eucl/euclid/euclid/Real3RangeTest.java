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

import org.contentmine.eucl.euclid.Axis.Axis3;
import org.contentmine.eucl.euclid.Point3;
import org.contentmine.eucl.euclid.Real3Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform3;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Real3Range.
 * 
 * @author pmr
 * 
 */
public class Real3RangeTest {

	Real3Range r0 = null;

	Real3Range r1 = null;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		r0 = new Real3Range();
		r1 = new Real3Range(new RealRange(1., 2.), new RealRange(3., 4.),
				new RealRange(5., 6.));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.Real3Range()'
	 */
	@Test
	public void testReal3Range() {
		Assert.assertEquals("real3", "(NULL,NULL,NULL)", r0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.Real3Range(RealRange,
	 * RealRange, RealRange)'
	 */
	@Test
	public void testReal3RangeRealRangeRealRangeRealRange() {
		Assert.assertEquals("real3", "((1.0,2.0),(3.0,4.0),(5.0,6.0))", r1
				.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.Real3Range(Real3Range)'
	 */
	@Test
	public void testReal3RangeReal3Range() {
		Real3Range rr = new Real3Range(r1);
		Assert.assertEquals("real3", "((1.0,2.0),(3.0,4.0),(5.0,6.0))", rr
				.toString());
	}

	/**
	 * test ranges for equality.
	 * 
	 * @param msg
	 * @param r3ref
	 * @param r3
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Real3Range r3ref,
			Real3Range r3, double epsilon) {
		RealRangeTest.assertEquals("xRange", r3.getXRange(), r3ref.getXRange(),
				epsilon);
		RealRangeTest.assertEquals("yRange", r3.getYRange(), r3ref.getYRange(),
				epsilon);
		RealRangeTest.assertEquals("zRange", r3.getZRange(), r3ref.getZRange(),
				epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.isEqualTo(Real3Range)'
	 */
	@Test
	public void testIsEqualTo() {
		Real3Range rr = new Real3Range(r1);
		Assert.assertTrue("real3", rr.isEqualTo(r1, 0.001));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.plus(Real3Range)'
	 */
	@Test
	public void testPlus() {
		Real3Range r = new Real3Range(new RealRange(1., 3.), new RealRange(2.,
				7.), new RealRange(2., 3.));
		Real3Range rr = r.plus(r1);
		Assert.assertEquals("real3", "((1.0,3.0),(2.0,7.0),(2.0,6.0))", rr
				.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.getXRange()'
	 */
	@Test
	public void testGetXYZRange() {
		RealRange x = r1.getXRange();
		Assert.assertEquals("realx", "(1.0,2.0)", x.toString());
		RealRange y = r1.getYRange();
		Assert.assertEquals("realy", "(3.0,4.0)", y.toString());
		RealRange z = r1.getZRange();
		Assert.assertEquals("realz", "(5.0,6.0)", z.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.add(Axis3, double)'
	 */
	@Test
	public void testAddAxis3Double() {
		r1.add(Axis3.X, 10.);
		Assert.assertEquals("add", "((1.0,10.0),(3.0,4.0),(5.0,6.0))", r1
				.toString());
		r1.add(Axis3.X, -1.);
		Assert.assertEquals("add", "((-1.0,10.0),(3.0,4.0),(5.0,6.0))", r1
				.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.includes(Point3)'
	 */
	@Test
	public void testIncludes() {
		Assert.assertTrue("includes", r1.includes(new Point3(1.1, 3.3, 5.5)));
		Assert.assertFalse("includes", r1.includes(new Point3(0.9, 3.3, 5.5)));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real3Range.add(Point3)'
	 */
	@Test
	public void testAddPoint3() {
		r1.add(new Point3(1.1, 1.2, 10.8));
		Assert.assertEquals("add", "((1.0,2.0),(1.2,4.0),(5.0,10.8))", r1
				.toString());
	}

	/**
	 * test get point with max x, y, z.
	 */
	@Test
	public void testGetMaxPoint3() {
		Point3 maxp = r1.getMaxPoint3();
		Point3Test.assertEquals("max point", new Point3(2., 4., 6.), maxp, EPS);
	}

	/**
	 * test get point with min x, y, z.
	 */
	@Test
	public void testGetMinPoint3() {
		Point3 minp = r1.getMinPoint3();
		Point3Test.assertEquals("min point", new Point3(1., 3., 5.), minp, EPS);
	}

	/**
	 * test transforms range.
	 */
	@Test
	public void testTransformEquals() {
		Transform3 tr = new Transform3("y, -z, -x");
		r1.transformEquals(tr);
		Assert.assertEquals("transformed x range min", 3., r1.getXRange()
				.getMin(), EPS);
		Assert.assertEquals("transformed x range max", 4., r1.getXRange()
				.getMax(), EPS);
		Assert.assertEquals("transformed y range min", -6., r1.getYRange()
				.getMin(), EPS);
		Assert.assertEquals("transformed y range max", -5., r1.getYRange()
				.getMax(), EPS);
		Assert.assertEquals("transformed z range min", -2., r1.getZRange()
				.getMin(), EPS);
		Assert.assertEquals("transformed z range max", -1., r1.getZRange()
				.getMax(), EPS);
	}

}
