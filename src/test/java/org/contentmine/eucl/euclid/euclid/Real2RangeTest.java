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

import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * test Real2Range.
 * 
 * @author pmr
 * 
 */
public class Real2RangeTest {

	Real2Range i2r0;

	Real2Range i2r1;

	Real2Range i2r2;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		i2r0 = new Real2Range();
		i2r1 = new Real2Range(new RealRange(1.0, 2.0), new RealRange(1.0, 2.0));
		i2r2 = new Real2Range(new RealRange(1.0, 2.0), new RealRange(3.0, 4.0));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.Real2Range()'
	 */
	@Test
	public void testReal2Range() {
		Assert.assertEquals("empty", "(NULL,NULL)", i2r0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.Real2Range(RealRange,
	 * RealRange)'
	 */
	@Test
	public void testReal2RangeRealRangeRealRange() {
		Assert.assertEquals("real range", "((1.0,2.0),(3.0,4.0))", i2r2
				.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.Real2Range(Real2Range)'
	 */
	@Test
	public void testReal2RangeReal2Range() {
		Real2Range ii = new Real2Range(i2r2);
		Assert.assertEquals("empty", "((1.0,2.0),(3.0,4.0))", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.isValid()'
	 */
	@Test
	public void testIsValid() {
		Assert.assertTrue("valid", i2r2.isValid());
		Assert.assertFalse("invalid", i2r0.isValid());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.isEqualTo(Real2Range)'
	 */
	@Test
	public void testIsEqualTo() {
		Assert.assertTrue("isEqual", i2r2.isEqualTo(i2r2, 0.001));
		Assert.assertFalse("isEqual", i2r2.isEqualTo(i2r1, 0.001));
		Assert.assertFalse("isEqual", i2r0.isEqualTo(i2r0, 0.001));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.plus(Real2Range)'
	 */
	@Test
	public void testPlus() {
		Real2Range ix = new Real2Range(new RealRange(1.0, 4.0), new RealRange(
				11.0, 14.0));
		Real2Range iy = new Real2Range(new RealRange(2.0, 5.0), new RealRange(
				12.0, 15.0));
		Real2Range ii = ix.plus(iy);
		Assert.assertEquals("plus", "((1.0,5.0),(11.0,15.0))", ii.toString());
		iy = new Real2Range(new RealRange(2.0, 3.0), new RealRange(12.0, 13.0));
		ii = ix.plus(iy);
		Assert.assertEquals("plus", "((1.0,4.0),(11.0,14.0))", ii.toString());
		iy = new Real2Range(new RealRange(0.0, 8.0), new RealRange(10.0, 18.0));
		ii = ix.plus(iy);
		Assert.assertEquals("plus", "((0.0,8.0),(10.0,18.0))", ii.toString());
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.Real2Range.doubleersectionWith(Real2Range)'
	 */
	@Test
	public void testIntersectionWith() {
		Real2Range ix = new Real2Range(new RealRange(1.0, 4.0), new RealRange(
				11.0, 14.0));
		Real2Range iy = new Real2Range(new RealRange(2.0, 5.0), new RealRange(
				12.0, 15.0));
		Real2Range ii = ix.intersectionWith(iy);
		Assert.assertEquals("plus", "((2.0,4.0),(12.0,14.0))", ii.toString());
		iy = new Real2Range(new RealRange(2.0, 3.0), new RealRange(12.0, 13.0));
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("plus", "((2.0,3.0),(12.0,13.0))", ii.toString());
		iy = new Real2Range(new RealRange(0.0, 8.0), new RealRange(10.0, 18.0));
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("plus", "((1.0,4.0),(11.0,14.0))", ii.toString());
	}
	
	@Test
	public void testIntersects() {
		Real2Range r2ra = new Real2Range(new RealRange(1.0, 2.0), new RealRange(20.0, 22.0));
		Real2Range r2rb = new Real2Range(new RealRange(1.99, 3.0), new RealRange(21.99, 24.0));
		Assert.assertTrue("overlap", r2ra.intersects(r2rb));
		r2rb = new Real2Range(new RealRange(2.01, 3.0), new RealRange(21.99, 24.0));
		Assert.assertFalse("overlap", r2ra.intersects(r2rb));
		Assert.assertTrue("overlap", r2ra.intersects(r2rb, 0.006));
		Assert.assertFalse("overlap", r2ra.intersects(r2rb, 0.004));
		r2rb = new Real2Range(new RealRange(1.99, 3.0), new RealRange(22.01, 24.0));
		Assert.assertFalse("overlap", r2ra.intersects(r2rb));
		Assert.assertTrue("overlap", r2ra.intersects(r2rb, 0.006));
		Assert.assertFalse("overlap", r2ra.intersects(r2rb, 0.004));
	}



	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.getXRange()'
	 */
	@Test
	public void testGetXRange() {
		Assert.assertNull("getXRange", i2r0.getXRange());
		Assert.assertEquals("getXRange", "(1.0,2.0)", i2r2.getXRange()
				.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.getYRange()'
	 */
	@Test
	public void testGetYRange() {
		Assert.assertNull("getXRange", i2r0.getYRange());
		Assert.assertEquals("getXRange", "(3.0,4.0)", i2r2.getYRange()
				.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.includes(Real2)'
	 */
	@Test
	public void testIncludesReal2() {
		Real2Range ix = new Real2Range(new RealRange(1.0, 4.0), new RealRange(
				11.0, 14.0));
		Assert.assertTrue("include", ix.includes(new Real2(2.0, 12.0)));
		Assert.assertTrue("include", ix.includes(new Real2(1.0, 11.0)));
		Assert.assertTrue("include", ix.includes(new Real2(4.0, 14.0)));
		Assert.assertFalse("include", ix.includes(new Real2(1.0, 15.0)));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.includes(Real2Range)'
	 */
	@Test
	public void testIncludesReal2Range() {
		Real2Range ix = new Real2Range(new RealRange(1.0, 4.0), new RealRange(
				11.0, 14.0));
		Assert.assertTrue("include", ix.includes(new Real2Range(new RealRange(
				2.0, 3.0), new RealRange(12.0, 13.0))));
		Assert.assertTrue("include", ix.includes(new Real2Range(new RealRange(
				1.0, 4.0), new RealRange(11.0, 14.0))));
		Assert.assertFalse("include", ix.includes(new Real2Range(new RealRange(
				0.0, 4.0), new RealRange(10.0, 14.0))));
		Assert.assertFalse("include", ix.includes(new Real2Range(new RealRange(
				2.0, 5.0), new RealRange(12.0, 15.0))));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Range.add(Real2)'
	 */
	@Test
	public void testAdd() {
		Real2Range ii = new Real2Range(new RealRange(1.0, 4.0), new RealRange(
				11.0, 14.0));
		Assert.assertEquals("plus", "((1.0,4.0),(11.0,14.0))", ii.toString());
		Real2 i2 = new Real2(2.0, 12.0);
		ii.add(i2);
		Assert.assertEquals("plus", "((1.0,4.0),(11.0,14.0))", ii.toString());
		i2 = new Real2(0.0, 15.0);
		ii.add(i2);
		Assert.assertEquals("plus", "((0.0,4.0),(11.0,15.0))", ii.toString());
		i2 = new Real2(8.0, 7.0);
		ii.add(i2);
		Assert.assertEquals("plus", "((0.0,8.0),(7.0,15.0))", ii.toString());
	}

	@Test
	public void testIsHorizontal() {
		Real2Range r2r = new Real2Range(new RealRange(0., 200.), new RealRange(0., 0.5));
		Assert.assertTrue("horizontal", r2r.isHorizontal());
		Assert.assertFalse("vertical", r2r.isVertical());
	}

	@Test
	public void testIsHorizontal1() {
		Real2Range r2r = new Real2Range(new RealRange(0., 200.), new RealRange(0., 200.));
		Assert.assertFalse("horizontal", r2r.isHorizontal());
		Assert.assertFalse("vertical", r2r.isVertical());
	}

	@Test
	public void testHorizontalVerticalRatio() {
		Real2Range r2r = new Real2Range(new RealRange(0., 100.), new RealRange(0., 1.));
		Assert.assertTrue("horizontal", r2r.isHorizontal());
		Assert.assertEquals("aspect ratio", 100., r2r.getHorizontalVerticalRatio(), 0.001);
	}
	
	@Test
	public void testIsContainedIn() {
		List<Real2Range> r2rList = new ArrayList<Real2Range>();
		Real2Range box = new Real2Range(new RealRange(50, 60),new RealRange(70, 80));
		r2rList.add(new Real2Range(new RealRange(0, 30),new RealRange(0, 100)));
		Assert.assertFalse(box.isContainedInAnyRange(r2rList));
		r2rList.add(new Real2Range(new RealRange(0, 100),new RealRange(0, 30)));
		Assert.assertFalse(box.isContainedInAnyRange(r2rList));
		r2rList.add(new Real2Range(new RealRange(40, 80),new RealRange(40, 80)));
		Assert.assertTrue(box.isContainedInAnyRange(r2rList));
	}
	
	@Test
	public void testArea() {
		Real2Range r2r = new Real2Range(new RealRange(1.0, 4.0), new RealRange(
				11.0, 24.0));
		Assert.assertEquals("area", 39.0, (double) r2r.calculateArea(), 0.01);
	}
	
	@Test
	@Ignore //null in constructor fails
	public void testAreaNull() {
		Real2Range r2r = new Real2Range(null, new RealRange(
				11.0, 24.0));
		Assert.assertNull("area", r2r.calculateArea());
	}
}
