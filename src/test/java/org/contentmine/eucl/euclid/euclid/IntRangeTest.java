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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test IntRange
 * 
 * @author pmr
 * 
 */
public class IntRangeTest {
	private static final Logger LOG = Logger.getLogger(IntRangeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	IntRange i0;
	IntRange i1;
	IntRange i2;

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
		i0 = new IntRange();
		i1 = new IntRange(1, 1);
		i2 = new IntRange(1, 3);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.IntRange()'
	 */
	@Test
	public void testIntRange() {
		Assert.assertEquals("empty", "NULL", i0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.IntRange(int, int)'
	 */
	@Test
	public void testIntRangeIntInt() {
		Assert.assertEquals("i1", "(1,1)", i1.toString());
		Assert.assertEquals("i2", "(1,3)", i2.toString());

	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.IntRange(IntRange)'
	 */
	@Test
	public void testIntRangeIntRange() {
		IntRange ii = new IntRange(i2);
		Assert.assertEquals("ii", "(1,3)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.isValid()'
	 */
	@Test
	public void testIsValid() {
		Assert.assertTrue("valid", i2.isValid());
		Assert.assertFalse("invalid", i0.isValid());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.isEqualTo(IntRange)'
	 */
	@Test
	public void testIsEqualTo() {
		Assert.assertTrue("equal", i2.isEqualTo(i2));
		Assert.assertFalse("equal", i2.isEqualTo(i0));
		Assert.assertFalse("equal", i0.isEqualTo(i0));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.plus(IntRange)'
	 */
	@Test
	public void testPlus() {
		IntRange ix = new IntRange(1, 4);
		IntRange iy = new IntRange(2, 3);
		IntRange ii = ix.plus(iy);
		Assert.assertEquals("ii", "(1,4)", ii.toString());
		iy = new IntRange(0, 2);
		ii = ix.plus(iy);
		Assert.assertEquals("ii", "(0,4)", ii.toString());
		iy = new IntRange(2, 6);
		ii = ix.plus(iy);
		Assert.assertEquals("ii", "(1,6)", ii.toString());
		iy = new IntRange();
		ii = ix.plus(iy);
		Assert.assertEquals("ii", "(1,4)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.intersectionWith(IntRange)'
	 */
	@Test
	public void testIntersectionWith() {
		IntRange ix = new IntRange(1, 4);
		IntRange iy = new IntRange(2, 3);
		IntRange ii = ix.intersectionWith(iy);
		Assert.assertEquals("ii", "(2,3)", ii.toString());
		iy = new IntRange(0, 2);
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("ii", "(1,2)", ii.toString());
		iy = new IntRange(2, 6);
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("ii", "(2,4)", ii.toString());
		iy = new IntRange();
		ii = ix.intersectionWith(iy);
		Assert.assertEquals("ii", "NULL", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.getMin()'
	 */
	@Test
	public void testGetMin() {
		Assert.assertEquals("min", 1, i2.getMin());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.getMax()'
	 */
	@Test
	public void testGetMax() {
		Assert.assertEquals("max", 3, i2.getMax());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.getRange()'
	 */
	@Test
	public void testGetRange() {
		Assert.assertEquals("range", 2, i2.getRange());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.includes(IntRange)'
	 */
	@Test
	public void testIncludesIntRange() {
		Assert.assertTrue("includes", i2.includes(new IntRange(2, 3)));
		Assert.assertFalse("includes", i2.includes(new IntRange(0, 3)));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.includes(int)'
	 */
	@Test
	public void testIncludesInt() {
		Assert.assertTrue("includes", i2.includes(1));
		Assert.assertFalse("includes", i2.includes(0));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.contains(int)'
	 */
	@Test
	public void testContains() {
		Assert.assertTrue("contains", i2.contains(1));
		Assert.assertFalse("contains", i2.contains(0));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntRange.add(int)'
	 */
	@Test
	public void testAdd() {
		i2.add(2);
		Assert.assertEquals("ii", "(1,3)", i2.toString());
		i2.add(0);
		Assert.assertEquals("ii", "(0,3)", i2.toString());
		i2.add(9);
		Assert.assertEquals("ii", "(0,9)", i2.toString());
	}
	
	/** midpoint
	 * 
	 */
	@Test
	public void testMidPoint() {
		Assert.assertEquals("mid", 0, i0.getMidPoint());
		Assert.assertEquals("mid", 1, i1.getMidPoint());
		Assert.assertEquals("mid", 2, i2.getMidPoint());
	}

	@Test
	public void testCanJoin() {
		IntRange range = new IntRange(1,5);
		Assert.assertTrue(range.canJoin(new IntRange(5,8), 0));
		Assert.assertTrue(range.canJoin(new IntRange(5,8), 1));
		// gap
		Assert.assertFalse(range.canJoin(new IntRange(6,8), 0));
		// allowed tolerance
		Assert.assertTrue(range.canJoin(new IntRange(6,8), 1));
		// overlap too large
		Assert.assertFalse(range.canJoin(new IntRange(3,8), 1));
		// included
		Assert.assertFalse(range.canJoin(new IntRange(3,5), 1));
		Assert.assertFalse(range.canJoin(new IntRange(1,5), 1));
	}
	
	/** join ranges
	 * 
	 * 
[(269,364) x 6, (478,554) x 6, (420,478) x 6, (364,420) x 6, (151,269) x 6]
	 */
	@Test
	public void testJoinRanges1() {
		List<IntRange> rangeList = Arrays.asList(
				new IntRange[] {
						new IntRange(269,364),
						new IntRange(478,554),
						new IntRange(420,478),
						new IntRange(364,420),
						new IntRange(151,269),
				});
		String rangeListString = "[(269,364), (478,554), (420,478), (364,420), (151,269)]";
		Assert.assertEquals(rangeListString,  rangeList.toString());
		List<IntRange> totalList = IntRange.joinRanges(rangeList, 0);
		Assert.assertEquals(1,  totalList.size());
		Assert.assertEquals("(151,554)",  totalList.get(0).toString());
		Assert.assertEquals(rangeListString,  rangeList.toString());
		
	}
	
	/** join ranges
	 * 
	 * 
// this has a gap (241-242)
[(253,265) x 6, (276,288) x 6, (242,253) x 6, (265,276) x 6, (222,241) x 6]
	 */
	@Test
	public void testJoinRanges2() {
		List<IntRange> intRangeList = Arrays.asList(
				new IntRange[] {
						new IntRange(253,265),
						new IntRange(276,288),
						new IntRange(242,253),
						new IntRange(265,276),
						new IntRange(222,241),
				});
		List<IntRange> totalList = IntRange.joinRanges(intRangeList, 0);
		Assert.assertEquals(2,  totalList.size());
		Assert.assertEquals("(222,241)",  totalList.get(0).toString());
		Assert.assertEquals("(242,288)",  totalList.get(1).toString());
		// allow a tolerance
		totalList = IntRange.joinRanges(intRangeList, 1);
		Assert.assertEquals(1,  totalList.size());
		Assert.assertEquals("(222,288)",  totalList.get(0).toString());
		
	}
	
	@Test
	public void testCreateArray() {
		IntArray intArray = new IntRange(2,3).createArray();
		Assert.assertEquals("(2,3)", intArray.toString());
		intArray = new IntRange(2,2).createArray();
		Assert.assertEquals("(2)", intArray.toString());
		intArray = new IntRange(2,5).createArray();
		Assert.assertEquals("(2,3,4,5)", intArray.toString());
		intArray = new IntRange(2,1).createArray();
		Assert.assertEquals("()", intArray.toString());
		intArray = new IntRange().createArray();
		Assert.assertEquals("()", intArray.toString());
	}
	
	@Test
	public void testCreate() {
		IntRange intRange;
		intRange = IntRange.create(1, 2);
		Assert.assertEquals("(1,2)", intRange.toString());
		intRange = IntRange.create(1, 1);
		Assert.assertEquals("(1,1)", intRange.toString());
		intRange = IntRange.create(1, 0);
		Assert.assertNull(intRange);
		intRange = IntRange.create(-1, 0);
		Assert.assertEquals("(-1,0)", intRange.toString());
	}
	
	@Test
	public void testParse() {
		IntRange intRange;
		intRange = IntRange.parse("{1,2}");
		Assert.assertEquals("(1,2)", intRange.toString());
		intRange = IntRange.parse("(1,2)");
		Assert.assertEquals("(1,2)", intRange.toString());
		intRange = IntRange.parse(" (1, 2) ");
		Assert.assertEquals("(1,2)", intRange.toString());
		intRange = IntRange.parse(" {1, 2} ");
		Assert.assertEquals("(1,2)", intRange.toString());
		intRange = IntRange.parse(" -1, 2 ");
		Assert.assertEquals("(-1,2)", intRange.toString());
	}
	

}
