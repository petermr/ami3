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

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.RealRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * tests RealRange.
 * 
 * @author pmr
 * 
 */
public class RealRangeTest {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(RealRangeTest.class);
	
	RealRange r0;
	RealRange r1;
	RealRange r2;

	/**
	 * tests equality of ranges.
	 * 
	 * @param msg
	 *            message
	 * @param ref
	 * @param r
	 * @param epsilon
	 */
	public static void assertEquals(String msg, RealRange ref, RealRange r,
			double epsilon) {
		Assert.assertEquals(msg + " min", r.getMin(), ref.getMin(), epsilon);
		Assert.assertEquals(msg + " max", r.getMax(), ref.getMax(), epsilon);
	}

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		r0 = new RealRange();
		r1 = new RealRange(1.0, 1.0);
		r2 = new RealRange(1.0, 3.0);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.RealRange()'
	 */
	@Test
	public void testRealRange() {
		Assert.assertEquals("empty", "NULL", r0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.RealRange(double, double)'
	 */
	@Test
	public void testRealRangeRealReal() {
		Assert.assertEquals("i1", "(1.0,1.0)", r1.toString());
		Assert.assertEquals("i2", "(1.0,3.0)", r2.toString());

	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.RealRange(RealRange)'
	 */
	@Test
	public void testRealRangeRealRange() {
		RealRange ii = new RealRange(r2);
		Assert.assertEquals("ii", "(1.0,3.0)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.isValid()'
	 */
	@Test
	public void testIsValid() {
		Assert.assertTrue("valid", r2.isValid());
		Assert.assertFalse("invalid", r0.isValid());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.isEqualTo(RealRange)'
	 */
	@Test
	public void testIsEqualTo() {
		Assert.assertTrue("equal", r2.isEqualTo(r2, 0.001));
		Assert.assertFalse("equal", r2.isEqualTo(r0, 0.001));
		Assert.assertFalse("equal", r0.isEqualTo(r0, 0.001));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.plus(RealRange)'
	 */
	@Test
	public void testPlus() {
		RealRange ix = new RealRange(1.0, 4.0);
		RealRange iy = new RealRange(2.0, 3.0);
		RealRange ii = ix.plus(iy);
		Assert.assertEquals("ii", "(1.0,4.0)", ii.toString());
		iy = new RealRange(0, 2);
		ii = ix.plus(iy);
		Assert.assertEquals("ii", "(0.0,4.0)", ii.toString());
		iy = new RealRange(2.0, 6.0);
		ii = ix.plus(iy);
		Assert.assertEquals("ii", "(1.0,6.0)", ii.toString());
		iy = new RealRange();
		ii = ix.plus(iy);
		Assert.assertEquals("ii", "(1.0,4.0)", ii.toString());
		//
		RealRange r1 = new RealRange(-1,2);
		RealRange r2 = new RealRange(-3,-4); // invalid
		RealRange r = r1.plus(r2);
		Assert.assertTrue("r1+r2", r.isEqualTo(new RealRange(-1, 2), 0.0001));
		
		r1 = new RealRange(-1,2);
		r2 = new RealRange(-4,-3); // invalid
		r = r1.plus(r2);
		Assert.assertTrue("r1+r2", r.isEqualTo(new RealRange(-4, 2), 0.0001));
		
		r1 = new RealRange(-1,2);
		r2 = new RealRange(-4,3); // invalid
		r = r1.plus(r2);
		Assert.assertTrue("r1+r2", r.isEqualTo(new RealRange(-4, 3), 0.0001));
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.RealRange.doubleersectionWith(RealRange)'
	 */
	@Test
	public void testIntersectionWith() {
		RealRange ix = new RealRange(1.0, 4.0);
		RealRange iy = new RealRange(2.0, 3.0);
		RealRange ii = ix.intersectionWith(iy);
		Assert.assertEquals("ii", "(2.0,3.0)", ii.toString());
		iy = new RealRange(0.0, 2.0);
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("ii", "(1.0,2.0)", ii.toString());
		iy = new RealRange(2.0, 6.0);
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("ii", "(2.0,4.0)", ii.toString());
		iy = new RealRange();
		ii = ix.intersectionWith(iy);
		Assert.assertNull("ii", ii);
	}
	
	@Test
	public void testIntersects() {
		RealRange ix = new RealRange(1.0, 2.01);
		RealRange iy = new RealRange(2.0, 3.0);
		Assert.assertTrue("overlap", ix.intersects(iy));
		ix = new RealRange(1.0, 1.99);
		Assert.assertFalse("overlap", ix.intersects(iy));
		Assert.assertTrue("overlap", ix.intersects(iy, 0.006));
		Assert.assertFalse("overlap", ix.intersects(iy, 0.004));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.getMin()'
	 */
	@Test
	public void testGetMin() {
		Assert.assertEquals("min", 1.0, r2.getMin(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.getMax()'
	 */
	@Test
	public void testGetMax() {
		Assert.assertEquals("max", 3.0, r2.getMax(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.getRange()'
	 */
	@Test
	public void testGetRange() {
		Assert.assertEquals("range", 2.0, r2.getRange(), EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.includes(RealRange)'
	 */
	@Test
	public void testIncludesRealRange() {
		Assert.assertTrue("includes", r2.includes(new RealRange(2.0, 3.0)));
		Assert.assertFalse("includes", r2.includes(new RealRange(0.0, 3.0)));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.includes(double)'
	 */
	@Test
	public void testIncludesReal() {
		Assert.assertTrue("includes", r2.includes(1.0));
		Assert.assertFalse("includes", r2.includes(0.0));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.contains(double)'
	 */
	@Test
	public void testContains() {
		Assert.assertTrue("contains", r2.contains(1.0));
		Assert.assertFalse("contains", r2.contains(0.0));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.add(double)'
	 */
	@Test
	public void testAdd() {
		r2.add(2);
		Assert.assertEquals("ii", "(1.0,3.0)", r2.toString());
		r2.add(0);
		Assert.assertEquals("ii", "(0.0,3.0)", r2.toString());
		r2.add(9);
		Assert.assertEquals("ii", "(0.0,9.0)", r2.toString());
	}

	/**
	 * test getting a random variate. tests limits only
	 */
	@Test
	public void testGetRandomVariate() {
		RealRange range = new RealRange(10, 20);
		double sum = 0.0;
		for (int i = 0; i < 100; i++) {
			double d = range.getRandomVariate();
			Assert.assertTrue("limit: ", d >= 10. && d <= 20.);
			sum += d;
		}
		// crude check
		Assert.assertTrue("distribution", sum > 1400 && sum < 1600);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.RealRange.RealRange(IntRange)'
	 */
	@Test
	public void testRealRangeIntRange() {
		RealRange r = new RealRange(new IntRange(1, 2));
		Assert.assertEquals("int", "(1.0,2.0)", r.toString());
	}

	@Test
	/** scale from one range to another
	 * 
	 */
	public void testConvertScales() {
		RealRange aRange = new RealRange(1,2);
		RealRange bRange = new RealRange(3, 5);
		double a2bScale = aRange.getScaleTo(bRange);
		Assert.assertEquals("a2b", 2.0, a2bScale, 0.001);
		double bConst = aRange.getConstantTo(bRange);
		Assert.assertEquals("bConst", 1.0, bConst, 0.001);
		double ax = 0.5;
		double bx = bConst + ax * a2bScale;
		Assert.assertEquals("bPred", 2.0, bx, 0.001);
		bx = aRange.transformToRange(bRange, ax);
	}
}
