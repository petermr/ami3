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

import static org.contentmine.eucl.euclid.EuclidConstants.S_EMPTY;
import static org.contentmine.eucl.euclid.EuclidConstants.S_RBRAK;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.ArrayBase.Trim;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.Int;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.eucl.euclid.test.StringTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test IntArray
 * 
 * @author pmr
 * 
 */
public class IntArrayTest {
	private static final Logger LOG = Logger.getLogger(IntArrayTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	IntArray a0;

	IntArray a1;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		a0 = new IntArray();
		a1 = new IntArray(new int[] { 1, 2, 4, 6 });
	}

	/**
	 * equality test. true if both args not null and equal
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, IntArray test, IntArray expected) {
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
				expected);
		String s = Int.testEquals(test.getArray(), expected.getArray());
		if (s != null) {
			Assert.fail(msg + "; " + s);
		}
	}

	/**
	 * equality test. true if both args not null and equal
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, int[] test, IntArray expected) {
		Assert.assertNotNull("test should not be null (" + msg + S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getArray().length);
		String s = Int.testEquals(test, expected.getArray());
		if (s != null) {
			Assert.fail(msg + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray()'
	 */
	@Test
	public void testIntArray() {
		Assert.assertEquals("empty", 0, a0.size());
		Assert.assertEquals("empty", "()", a0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(int)'
	 */
	@Test
	public void testIntArrayInt() {
		IntArray r = new IntArray(4);
		Assert.assertEquals("r", 4, r.size());
		IntArrayTest.assertEquals("r", new int[] { 0, 0, 0, 0 }, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(int, int, int)'
	 */
	@Test
	public void testIntArrayIntDoubleDouble() {
		IntArray r = new IntArray(4, 1, 2);
		Assert.assertEquals("r", 4, r.size());
		IntArrayTest.assertEquals("r", new int[] { 1, 3, 5, 7 }, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(int, int)'
	 */
	@Test
	public void testIntArrayIntDouble() {
		IntArray r = new IntArray(4, 2);
		Assert.assertEquals("r", 4, r.size());
		IntArrayTest.assertEquals("r", new int[] { 2, 2, 2, 2 }, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(int, int[])'
	 */
	@Test
	public void testIntArrayIntDoubleArray() {
		int[] d = { 1, 2, 3, 4 };
		IntArray r = new IntArray(3, d);
		Assert.assertEquals("r", 3, r.size());
		IntArrayTest.assertEquals("r", new int[] { 1, 2, 3 }, r);
		try {
			r = new IntArray(5, d);
			Assert.fail("should always throw " + "Array size too small");
		} catch (EuclidRuntimeException e) {
			Assert
					.assertEquals("int[]", "Array would overflow", e
							.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(int[])'
	 */
	@Test
	public void testIntArrayDoubleArray() {
		int[] d = { 1, 2, 3, 4 };
		IntArray r = new IntArray(d);
		Assert.assertEquals("r", 4, r.size());
		IntArrayTest.assertEquals("r", new int[] { 1, 2, 3, 4 }, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(IntArray, int, int)'
	 */
	@Test
	public void testIntArrayIntArrayIntInt() {
		IntArray r = new IntArray(a1, 1, 2);
		Assert.assertEquals("r", 2, r.size());
		IntArrayTest.assertEquals("r", new int[] { 2, 4 }, r);
		try {
			r = new IntArray(a1, 0, 5);
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("int array", "index out of range: 0/5", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(IntArray, IntArray)'
	 */
	@Test
	public void testIntArrayIntArrayIntArray() {
		IntArray r = new IntArray(a1, new IntArray(new int[] { 3, 1, 2 }));
		Assert.assertEquals("r", 3, r.size());
		IntArrayTest.assertEquals("r", new int[] { 6, 2, 4 }, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(IntArray)'
	 */
	@Test
	public void testIntArrayIntArray() {
		IntArray r = new IntArray(a1);
		Assert.assertEquals("r", 4, r.size());
		IntArrayTest.assertEquals("r", new int[] { 1, 2, 4, 6 }, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(String[])'
	 */
	@Test
	public void testIntArrayStringArray() {
		IntArray r = new IntArray(new String[] { "1", "2", "4", "6" });
		IntArrayTest.assertEquals("string array", a1, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.IntArray(String)'
	 */
	@Test
	public void testIntArrayString() {
		IntArray r = new IntArray("1 2 4 6");
		IntArrayTest.assertEquals("string array", a1, r);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.elementAt(int)'
	 */
	@Test
	public void testElementAt() {
		Assert.assertEquals("element at", 4, a1.elementAt(2));
		try {
			Assert.assertEquals("element at", 4, a1.elementAt(5));
			Assert.fail("should always throw " + "ArrayIndexOutOfBoundsException");
		} catch (ArrayIndexOutOfBoundsException e) {
			Assert.assertEquals("ArrayIndexOutOfBoundsException", "5", e
					.getMessage());
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.size()'
	 */
	@Test
	public void testSize() {
		Assert.assertEquals("size", 0, a0.size());
		Assert.assertEquals("size", 4, a1.size());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getArray()'
	 */
	@Test
	public void testGetArray() {
		String s = Int.testEquals((new int[] {}), a0.getArray());
		if (s != null) {
			Assert.fail("array" + "; " + s);
		}
		s = Int.testEquals((new int[] { 1, 2, 4, 6 }), a1.getArray());
		if (s != null) {
			Assert.fail("array" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.clearArray()'
	 */
	@Test
	public void testClearArray() {
		a1.clearArray();
		IntArrayTest.assertEquals("clear", new int[] { 0, 0, 0, 0 }, a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getReverseArray()'
	 */
	@Test
	public void testGetReverseArray() {
		int[] d = a1.getReverseArray();
		String s = Int.testEquals((new int[] { 6, 4, 2, 1 }), d);
		if (s != null) {
			Assert.fail("clear" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.isEqualTo(IntArray)'
	 */
	@Test
	public void testIsEqualTo() {
		IntArray a = new IntArray("1 2 4 6");
		Assert.assertTrue("isEqualTo", a1.isEqualTo(a));
		a = new IntArray("1 2 4");
		Assert.assertFalse("isEqualTo", a1.isEqualTo(a));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.equals(IntArray, int)'
	 */
	@Test
	public void testEqualsIntArrayDouble() {
		IntArray a = new IntArray("1 2 4 6");
		Assert.assertTrue("isEqualTo", a1.equals(a));
		a = new IntArray("1 2 4 7");
		Assert.assertFalse("isEqualTo", a1.equals(a));
		a = new IntArray("1 2 4");
		Assert.assertFalse("isEqualTo", a1.equals(a));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.plus(IntArray)'
	 */
	@Test
	public void testPlus() {
		IntArray a2 = a1.plus(new IntArray("10 20 30 40"));
		IntArrayTest.assertEquals("plus", new int[] { 11, 22, 34, 46 }, a2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.subtract(IntArray)'
	 */
	@Test
	public void testSubtract() {
		IntArray a2 = a1.subtract(new IntArray("10 20 30 40"));
		IntArrayTest.assertEquals("subtract", new int[] { -9, -18, -26, -34 },
				a2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.subtractEquals(IntArray)'
	 */
	@Test
	public void testSubtractEquals() {
		IntArray ia = new IntArray("10 20 30 40");
		a1.subtractEquals(ia);
		IntArrayTest.assertEquals("subtract", new int[] { -9, -18, -26, -34 },
				a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.negative()'
	 */
	@Test
	public void testNegative() {
		a1.negative();
		IntArrayTest.assertEquals("negative", new int[] { -1, -2, -4, -6 }, a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.multiplyBy(int)'
	 */
	@Test
	public void testMultiplyBy() {
		IntArray a = a1.multiplyBy(2);
		IntArrayTest.assertEquals("multiplyBy", new int[] { 2, 4, 8, 12 }, a);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.setElementAt(int, int)'
	 */
	@Test
	public void testSetElementAt() {
		a1.setElementAt(2, 10);
		IntArrayTest.assertEquals("setElement", new int[] { 1, 2, 10, 6 }, a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getSubArray(int, int)'
	 */
	@Test
	public void testGetSubArray() {
		IntArray a = a1.getSubArray(2, 3);
		IntArrayTest.assertEquals("subArray", new int[] { 4, 6 }, a);
		a = a1.getSubArray(2, 2);
		IntArrayTest.assertEquals("subArray", new int[] { 4 }, a);
		a = a1.getSubArray(0, 3);
		IntArrayTest.assertEquals("subArray", new int[] { 1, 2, 4, 6 }, a);
		try {
			a = a1.getSubArray(0, 5);
			Assert.fail("should always throw " + "ArrayIndexOutOfBoundsException");
		} catch (ArrayIndexOutOfBoundsException e) {
			Assert.assertEquals("subArray ArrayIndexOutOfBoundsException",
					"java.lang.ArrayIndexOutOfBoundsException", S_EMPTY + e);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.setElements(int, int[])'
	 */
	@Test
	public void testSetElements() {
		a1.setElements(1, new int[] { 10, 20 });
		IntArrayTest.assertEquals("setElement", new int[] { 1, 10, 20, 6 }, a1);
		try {
			a1.setElements(1, new int[] { 10, 20, 30, 40 });
			Assert.fail("should always throw " + "ArrayIndexOutOfBoundsException");
		} catch (ArrayIndexOutOfBoundsException e) {
			Assert.assertEquals("subArray ArrayIndexOutOfBoundsException",
					"java.lang.ArrayIndexOutOfBoundsException", S_EMPTY + e);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.isClear()'
	 */
	@Test
	public void testIsClear() {
		Assert.assertFalse("isClear", a1.isClear());
		a1.clearArray();
		Assert.assertTrue("isClear", a1.isClear());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.setAllElements(int)'
	 */
	@Test
	public void testSetAllElements() {
		a1.setAllElements(10);
		IntArrayTest.assertEquals("setElement", new int[] { 10, 10, 10, 10 },
				a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.sumAllElements()'
	 */
	@Test
	public void testSumAllElements() {
		Assert.assertEquals("sum", 13, a1.sumAllElements());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.absSumAllElements()'
	 */
	@Test
	public void testAbsSumAllElements() {
		IntArray a = new IntArray("-1 3 -11 14");
		Assert.assertEquals("sum", 5, a.sumAllElements());
		Assert.assertEquals("absSum", 29, a.absSumAllElements());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.innerProduct()'
	 */
	@Test
	public void testInnerProduct() {
		Assert.assertEquals("inner", 57, a1.innerProduct());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.dotProduct(IntArray)'
	 */
	@Test
	public void testDotProduct() {
		IntArray a = new IntArray("1 2 3 4");
		int d = a1.dotProduct(a);
		Assert.assertEquals("dot", 41, d);
		a = new IntArray("1 2 3");
		try {
			a1.dotProduct(a);
			Assert.fail("should always throw " + "ArrayIndexOutOfBoundsException");
		} catch (EuclidRuntimeException e) {
			Assert.assertEquals("dot",
					"org.contentmine.eucl.euclid.EuclidRuntimeException", S_EMPTY + e);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.cumulativeSum()'
	 */
	@Test
	public void testCumulativeSum() {
		IntArray a = a1.cumulativeSum();
		IntArrayTest.assertEquals("cumulative", new int[] { 1, 3, 7, 13 }, a);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.trim(int, int)'
	 */
	@Test
	public void testTrim() {
		IntArray a = new IntArray("1 2 3 4 1 3 5 1 3");
		IntArray b = a.trim(Trim.ABOVE, 2);
		int[] d = { 1, 2, 2, 2, 1, 2, 2, 1, 2 };
		IntArrayTest.assertEquals("trim", d, b);
		b = a.trim(Trim.BELOW, 2);
		int[] dd = { 2, 2, 3, 4, 2, 3, 5, 2, 3 };
		IntArrayTest.assertEquals("trim", dd, b);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.indexOfLargestElement()'
	 */
	@Test
	public void testIndexOfLargestElement() {
		Assert.assertEquals("largest", 3, a1.indexOfLargestElement());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.indexOfSmallestElement()'
	 */
	@Test
	public void testIndexOfSmallestElement() {
		Assert.assertEquals("smallest", 0, a1.indexOfSmallestElement());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.largestElement()'
	 */
	@Test
	public void testLargestElement() {
		Assert.assertEquals("largest", 6, a1.largestElement());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getMax()'
	 */
	@Test
	public void testGetMax() {
		Assert.assertEquals("max", 6, a1.getMax());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.smallestElement()'
	 */
	@Test
	public void testSmallestElement() {
		Assert.assertEquals("smallest", 1, a1.smallestElement());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getMin()'
	 */
	@Test
	public void testGetMin() {
		Assert.assertEquals("max", 1, a1.getMin());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getRange()'
	 */
	@Test
	public void testGetRange() {
		IntRange range = a1.getRange();
		Assert.assertEquals("range", 1, range.getMin());
		Assert.assertEquals("range", 6, range.getMax());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.deleteElement(int)'
	 */
	@Test
	public void testDeleteElement() {
		a1.deleteElement(2);
		IntArrayTest.assertEquals("delete", new int[] { 1, 2, 6 }, a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.deleteElements(int, int)'
	 */
	@Test
	public void testDeleteElementsIntInt() {
		IntArray a = new IntArray(a1);
		a.deleteElements(1, 2);
		IntArrayTest.assertEquals("delete", new int[] { 1, 6 }, a);
		a = new IntArray(a1);
		a.deleteElements(0, 3);
		IntArrayTest.assertEquals("delete", new int[] {}, a);
		a = new IntArray(a1);
		a.deleteElements(2, 2);
		IntArrayTest.assertEquals("delete", new int[] { 1, 2, 6 }, a);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.insertElementAt(int, int)'
	 */
	@Test
	public void testInsertElementAt() {
		IntArray a = new IntArray(a1);
		a.insertElementAt(1, 30);
		IntArrayTest.assertEquals("insert", new int[] { 1, 30, 2, 4, 6 }, a);
		a.insertElementAt(0, 20);
		IntArrayTest
				.assertEquals("insert", new int[] { 20, 1, 30, 2, 4, 6 }, a);
		a.insertElementAt(6, 10);
		IntArrayTest.assertEquals("insert",
				new int[] { 20, 1, 30, 2, 4, 6, 10 }, a);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.insertArray(int, IntArray)'
	 */
	@Test
	public void testInsertArray() {
		a1.insertArray(1, new IntArray("44 55"));
		IntArrayTest.assertEquals("insert", new int[] { 1, 44, 55, 2, 4, 6 },
				a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.addElement(int)'
	 */
	@Test
	public void testAddElement() {
		a1.addElement(30);
		IntArrayTest.assertEquals("insert", new int[] { 1, 2, 4, 6, 30 }, a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.addArray(IntArray)'
	 */
	@Test
	public void testAddArray() {
		a1.addArray(new IntArray("5 16 7"));
		IntArrayTest.assertEquals("insert", new int[] { 1, 2, 4, 6, 5, 16, 7 },
				a1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getReorderedArray(IntSet)'
	 */
	@Test
	public void testGetReorderedArray() {
		IntSet intSet = new IntSet(new int[] { 3, 1, 0, 2 });
		IntArray a = a1.getReorderedArray(intSet);
		IntArrayTest.assertEquals("insert", new int[] { 6, 2, 1, 4 }, a);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.inRange(IntRange)'
	 */
	@Test
	public void testInRange() {
		IntRange range = new IntRange(1, 5);
		IntSet intSet = a1.inRange(range);
		IntArray intArray = intSet.getIntArray();
		IntArrayTest.assertEquals("inrange", new int[] { 0, 1, 2 }, intArray);
		intSet = a1.inRange(new IntRange(-3, 7));
		IntArrayTest.assertEquals("inrange", new int[] { 0, 1, 2, 3 }, intSet
				.getIntArray());
		intSet = a1.inRange(new IntRange(5, 5));
		IntArrayTest
				.assertEquals("inrange", new int[] {}, intSet.getIntArray());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.outOfRange(IntRange)'
	 */
	@Test
	public void testOutOfRange() {
		IntRange range = new IntRange(1, 5);
		IntSet intSet = a1.outOfRange(range);
		IntArray intArray = intSet.getIntArray();
		IntArrayTest.assertEquals("inrange", new int[] { 3 }, intArray);
		intSet = a1.outOfRange(new IntRange(-3, 7));
		IntArrayTest
				.assertEquals("inrange", new int[] {}, intSet.getIntArray());
		intSet = a1.outOfRange(new IntRange(4, 6));
		IntArrayTest.assertEquals("inrange", new int[] { 0, 1 }, intSet
				.getIntArray());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.getStringValues()'
	 */
	@Test
	public void testGetStringValues() {
		String[] ss = a1.getStringValues();
		StringTestBase.assertEquals("string values", new String[] { "1", "2",
				"4", "6" }, ss);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.sortAscending()'
	 */
	@Test
	public void testSortAscending() {
		IntArray ra = new IntArray("1 6 3 9 2 0");
		ra.sortAscending();
		IntArrayTest.assertEquals("sortAscending",
				new int[] { 0, 1, 2, 3, 6, 9 }, ra);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.sortDescending()'
	 */
	@Test
	public void testSortDescending() {
		IntArray ra = new IntArray("1 6 3 9 2 0");
		ra.sortDescending();
		IntArrayTest.assertEquals("sortDescending", new int[] { 9, 6, 3, 2, 1,
				0 }, ra);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.reverse()'
	 */
	@Test
	public void testReverse() {
		IntArray ra = new IntArray("1 6 3 9 2 0");
		ra.reverse();
		IntArrayTest
				.assertEquals("reverse", new int[] { 0, 2, 9, 3, 6, 1 }, ra);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.indexSortAscending()'
	 */
	@Test
	public void testIndexSortAscending() {
		IntArray ra = new IntArray("1 6 3 9 2 0");
		IntSet intSet = ra.indexSortAscending();
		IntArrayTest.assertEquals("sortAscending",
				new int[] { 5, 0, 4, 2, 1, 3 }, intSet.getIntArray());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntArray.indexSortDescending()'
	 */
	@Test
	public void testIndexSortDescending() {
		IntArray ra = new IntArray("1 6 3 9 2 0");
		IntSet intSet = ra.indexSortDescending();
		IntArrayTest.assertEquals("sortDescending", new int[] { 3, 1, 2, 4, 0,
				5 }, intSet.getIntArray());
	}

	@Test
	public void testIterator() {
		IntArray intArray = new IntArray(new int[]{0,1,2});
		Iterator<Integer> intIterator = intArray.iterator();
		Assert.assertTrue("start", intIterator.hasNext());
		Assert.assertTrue("start", intIterator.hasNext());
		Assert.assertTrue("start", intIterator.hasNext());
		Assert.assertTrue("start", intIterator.hasNext());
		Assert.assertEquals("start", 0, (int) intIterator.next());
		Assert.assertEquals("start", 1, (int) intIterator.next());
		Assert.assertTrue("after 1", intIterator.hasNext());
		Assert.assertEquals("after 1", 2, (int) intIterator.next());
		Assert.assertFalse("end", intIterator.hasNext());
		Assert.assertNull("after 2", intIterator.next());
	}
	

	@Test
	public void testIterators() {
		IntArray intArray = new IntArray(new int[]{0,1,2});
		Iterator<Integer> intIterator00 = intArray.iterator();
		Iterator<Integer> intIterator01 = intArray.iterator();
		Assert.assertTrue("start", intIterator00.hasNext());
		Assert.assertEquals("start", 0, (int) intIterator00.next());
		Assert.assertEquals("start", 1, (int) intIterator00.next());
		Assert.assertEquals("start", 0, (int) intIterator01.next());
		Assert.assertEquals("end0", 2, (int) intIterator00.next());
		Assert.assertFalse("end0", intIterator00.hasNext());
		Assert.assertTrue("middle1", intIterator01.hasNext());
		Assert.assertNull("endo", intIterator00.next());
		Assert.assertEquals("start", 1, (int) intIterator01.next());
	}
	
	@Test
	public void testCreateMappedArray() {
		// create an IntArray of 0,1,2,...,10
		IntArray testArray = new IntArray(10+1, 0, 1);
		Assert.assertEquals("(0,1,2,3,4,5,6,7,8,9,10)", testArray.toString());
		IntArray subArray = testArray.createMappedIndexes(3);
		Assert.assertEquals("(0,5,10)", subArray.toString());
		subArray = testArray.createMappedIndexes(4);
		Assert.assertEquals("(0,3,7,10)", subArray.toString());
	}
	
	/** creates array from indexes
	 * newArray = this[indexes[0]], this[indexes[1]] ... 
	 * 
	 * @param indexes must be in range 0, this.size()-1
	 * @return new array
	 * @throws RuntimeException if any indexes are out of range
	 */
	
	@Test
	public void testCreateIndexedArray() {
		// create list of integers 0...10
		IntArray testArray = new IntArray(10+1, 0, 1);
		IntArray indexes = new IntArray(new int[]{3,0,9,3,5});
		IntArray newArray = testArray.createMappedArray(indexes);
		Assert.assertEquals("(3,0,9,3,5)", newArray.toString());
		testArray = new IntArray(new int[]{100,10,30,50,3,7,99,2,77,5});
		indexes = new IntArray(new int[]{3,0,9,3,5});
		newArray = testArray.createMappedArray(indexes);
		Assert.assertEquals("(50,100,5,50,7)", newArray.toString());
	}

	/** creates array from indexes
	 * newArray = this[indexes[0]], this[indexes[1]] ... 
	 * 
	 * @param indexes must be in range 0, this.size()-1
	 * @return new array
	 * @throws RuntimeException if any indexes are out of range
	 */
	
	@Test
	public void testCreateSegmentedArray() {
		// create list of 21 integers 0...20
		IntArray testArray = new IntArray(20+1, 0, 1);
		// the first examples are all exact
		int nsegments = 4;
		IntArray segmentedArray = testArray.createSegmentedArray(nsegments);
		Assert.assertEquals("(0,5,10,15,20)", segmentedArray.toString());
		testArray = new IntArray(new int[]{2,4,6,8,10,12,14,16,18});
		nsegments = 4;
		segmentedArray = testArray.createSegmentedArray(nsegments);
		Assert.assertEquals("(2,6,10,14,18)", segmentedArray.toString());
		nsegments = 2;
		segmentedArray = testArray.createSegmentedArray(nsegments);
		Assert.assertEquals("(2,10,18)", segmentedArray.toString());
		nsegments = 8;
		segmentedArray = testArray.createSegmentedArray(nsegments);
		Assert.assertEquals("(2,4,6,8,10,12,14,16,18)", segmentedArray.toString());
		nsegments = 3;
		// note this has nearest integers
		segmentedArray = testArray.createSegmentedArray(nsegments);
		Assert.assertEquals("(2,8,12,18)", segmentedArray.toString());
	}

	@Test
	public void testGetIntArrayFromInt2List() {
		List<Int2> int2List = new ArrayList<Int2>();
		int2List.add(new Int2(1,5));
		int2List.add(new Int2(2,6));
		int2List.add(new Int2(3,7));
		
		IntArray xArray = IntArray.getIntArray(int2List, 0);
		Assert.assertEquals("x", "(1,2,3)", xArray.toString());
		IntArray yArray = IntArray.getIntArray(int2List, 1);
		Assert.assertEquals("x", "(5,6,7)", yArray.toString());
		
	}

	@Test
	public void testGetIntArrayFromInt2ListNull() {
		IntArray xArray = IntArray.getIntArray(null, 0);
		Assert.assertNull("null array", xArray);
		
		List<Int2> int2List = new ArrayList<Int2>();
		int2List.add(new Int2(1,5));
		int2List.add(new Int2(2,6));
		int2List.add(new Int2(3,7));
		
		xArray = IntArray.getIntArray(int2List, -1);
		Assert.assertNull("null array", xArray);
		
		xArray = IntArray.getIntArray(int2List, 2);
		Assert.assertNull("null array", xArray);
		
	}
	
	@Test
	public void testGetIntegerArray() {
		IntArray intArray = new IntArray(new int[]{ 1, 1, 2, 3, 5, 8});
		List<Integer> integerList = intArray.getIntegerList();
		Assert.assertEquals("integers", "[1, 1, 2, 3, 5, 8]", String.valueOf(integerList));
		intArray = new IntArray();
		Assert.assertEquals("integers", "[]", String.valueOf(intArray.getIntegerList()));
		intArray.addElement(3);
		intArray.addElement(5);
		intArray.addElement(1);
		Assert.assertEquals("integers", "[3, 5, 1]", String.valueOf(intArray.getIntegerList()));
		
	}
	
	/** convolution of arrays (filtering).
	 * has previously had bugs
	 * 
	 */
	@Test
	public void testApplyFilterNew() {
		IntArray array = new IntArray("0 1 3 6 3 1 0");
		IntArray filter = new IntArray("-1 3 -1");
		IntArray newArray = array.applyFilterNew(filter);
		Assert.assertEquals("convoluted", "(0,0,2,12,2,0,0)", newArray.toString());
	}


}
