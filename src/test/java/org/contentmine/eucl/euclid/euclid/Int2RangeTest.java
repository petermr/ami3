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

import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Int2Range
 * 
 * @author pmr
 * 
 */
public class Int2RangeTest {

	Int2Range i2r0;

	Int2Range i2r1;

	Int2Range i2r2;

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		i2r0 = new Int2Range();
		i2r1 = new Int2Range(new IntRange(1, 2), new IntRange(1, 2));
		i2r2 = new Int2Range(new IntRange(1, 2), new IntRange(3, 4));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.Int2Range()'
	 */
	@Test
	public void testInt2Range() {
		Assert.assertEquals("empty", "(NULL,NULL)", i2r0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.Int2Range(IntRange,
	 * IntRange)'
	 */
	@Test
	public void testInt2RangeIntRangeIntRange() {
		Assert.assertEquals("empty", "((1,2),(3,4))", i2r2.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.Int2Range(Int2Range)'
	 */
	@Test
	public void testInt2RangeInt2Range() {
		Int2Range ii = new Int2Range(i2r2);
		Assert.assertEquals("empty", "((1,2),(3,4))", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.isValid()'
	 */
	@Test
	public void testIsValid() {
		Assert.assertTrue("valid", i2r2.isValid());
		Assert.assertFalse("invalid", i2r0.isValid());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.isEqualTo(Int2Range)'
	 */
	@Test
	public void testIsEqualTo() {
		Assert.assertTrue("isEqual", i2r2.isEqualTo(i2r2));
		Assert.assertFalse("isEqual", i2r2.isEqualTo(i2r1));
		Assert.assertFalse("isEqual", i2r0.isEqualTo(i2r0));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.plus(Int2Range)'
	 */
	@Test
	public void testPlus() {
		Int2Range ix = new Int2Range(new IntRange(1, 4), new IntRange(11, 14));
		Int2Range iy = new Int2Range(new IntRange(2, 5), new IntRange(12, 15));
		Int2Range ii = ix.plus(iy);
		Assert.assertEquals("plus", "((1,5),(11,15))", ii.toString());
		iy = new Int2Range(new IntRange(2, 3), new IntRange(12, 13));
		ii = ix.plus(iy);
		Assert.assertEquals("plus", "((1,4),(11,14))", ii.toString());
		iy = new Int2Range(new IntRange(0, 8), new IntRange(10, 18));
		ii = ix.plus(iy);
		Assert.assertEquals("plus", "((0,8),(10,18))", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.intersectionWith(Int2Range)'
	 */
	@Test
	public void testIntersectionWith() {
		Int2Range ix = new Int2Range(new IntRange(1, 4), new IntRange(11, 14));
		Int2Range iy = new Int2Range(new IntRange(2, 5), new IntRange(12, 15));
		Int2Range ii = ix.intersectionWith(iy);
		Assert.assertEquals("plus", "((2,4),(12,14))", ii.toString());
		iy = new Int2Range(new IntRange(2, 3), new IntRange(12, 13));
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("plus", "((2,3),(12,13))", ii.toString());
		iy = new Int2Range(new IntRange(0, 8), new IntRange(10, 18));
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("plus", "((1,4),(11,14))", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.getXRange()'
	 */
	@Test
	public void testGetXRange() {
		Assert.assertEquals("getXRange", "NULL", i2r0.getXRange().toString());
		Assert.assertEquals("getXRange", "(1,2)", i2r2.getXRange().toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.getYRange()'
	 */
	@Test
	public void testGetYRange() {
		Assert.assertEquals("getXRange", "NULL", i2r0.getYRange().toString());
		Assert.assertEquals("getXRange", "(3,4)", i2r2.getYRange().toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.includes(Int2)'
	 */
	@Test
	public void testIncludesInt2() {
		Int2Range ix = new Int2Range(new IntRange(1, 4), new IntRange(11, 14));
		Assert.assertTrue("include", ix.includes(new Int2(2, 12)));
		Assert.assertTrue("include", ix.includes(new Int2(1, 11)));
		Assert.assertTrue("include", ix.includes(new Int2(4, 14)));
		Assert.assertFalse("include", ix.includes(new Int2(1, 15)));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.includes(Int2Range)'
	 */
	@Test
	public void testIncludesInt2Range() {
		Int2Range ix = new Int2Range(new IntRange(1, 4), new IntRange(11, 14));
		Assert.assertTrue("include", ix.includes(new Int2Range(new IntRange(2,
				3), new IntRange(12, 13))));
		Assert.assertTrue("include", ix.includes(new Int2Range(new IntRange(1,
				4), new IntRange(11, 14))));
		Assert.assertFalse("include", ix.includes(new Int2Range(new IntRange(0,
				4), new IntRange(10, 14))));
		Assert.assertFalse("include", ix.includes(new Int2Range(new IntRange(2,
				5), new IntRange(12, 15))));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2Range.add(Int2)'
	 */
	@Test
	public void testAdd() {
		Int2Range ii = new Int2Range(new IntRange(1, 4), new IntRange(11, 14));
		Assert.assertEquals("plus", "((1,4),(11,14))", ii.toString());
		Int2 i2 = new Int2(2, 12);
		ii.add(i2);
		Assert.assertEquals("plus", "((1,4),(11,14))", ii.toString());
		i2 = new Int2(0, 15);
		ii.add(i2);
		Assert.assertEquals("plus", "((0,4),(11,15))", ii.toString());
		i2 = new Int2(8, 7);
		ii.add(i2);
		Assert.assertEquals("plus", "((0,8),(7,15))", ii.toString());
	}

}
