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

import java.util.List;

import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.Int;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test IntSet.
 * 
 * @author pmr
 * 
 */
public class IntSetTest {

	IntSet i0;

	IntSet i1;

	IntSet i2;

	IntSet i3;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		i0 = new IntSet();
		i1 = new IntSet(new int[] { 3, 4, 1, 2 });
		i2 = new IntSet(4);
		i3 = new IntSet(2, 5);
	}

	/**
	 * equality test. true if both args not null and equal
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 */
	public static void assertEquals(String msg, IntSet test, IntSet expected) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		String s = Int.testEquals(test.getElements(), expected.getElements());
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
	public static void assertEquals(String msg, int[] test, IntSet expected) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		Assert.assertEquals("must be of equal length ", test.length, expected
				.getElements().length);
		String s = Int.testEquals(test, expected.getElements());
		if (s != null) {
			Assert.fail(msg + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.IntSet()'
	 */
	@Test
	public void testIntSet() {
		Assert.assertEquals("empty", "()", i0.toString());
		Assert.assertFalse("int, int ", i0.contains(0));
		Assert.assertFalse("int, int ", i0.contains(1));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.IntSet(int)'
	 */
	@Test
	public void testIntSetInt() {
		Assert.assertEquals("int[]", "(3,4,1,2)", i1.toString());
		Assert.assertFalse("int, int ", i1.contains(0));
		Assert.assertTrue("int, int ", i1.contains(1));
		Assert.assertTrue("int, int ", i1.contains(2));
		Assert.assertTrue("int, int ", i1.contains(3));
		Assert.assertTrue("int, int ", i1.contains(4));
		Assert.assertFalse("int, int ", i1.contains(5));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.IntSet(int, int)'
	 */
	@Test
	public void testIntSetIntInt() {
		Assert.assertEquals("int", "(2,3,4,5)", i3.toString());
		Assert.assertFalse("int, int ", i3.contains(0));
		Assert.assertFalse("int, int ", i3.contains(1));
		Assert.assertTrue("int, int ", i3.contains(2));
		Assert.assertTrue("int, int ", i3.contains(3));
		Assert.assertTrue("int, int ", i3.contains(4));
		Assert.assertTrue("int, int ", i3.contains(5));
		Assert.assertFalse("int, int ", i3.contains(6));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.IntSet(IntSet)'
	 */
	@Test
	public void testIntSetIntSet() {
		IntSet ii = new IntSet(i1);
		Assert.assertEquals("copy", "(3,4,1,2)", ii.toString());
		Assert.assertFalse("int, int ", ii.contains(0));
		Assert.assertTrue("int, int ", ii.contains(1));
		Assert.assertTrue("int, int ", ii.contains(2));
		Assert.assertTrue("int, int ", ii.contains(3));
		Assert.assertTrue("int, int ", ii.contains(4));
		Assert.assertFalse("int, int ", ii.contains(5));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.IntSet(int[])'
	 */
	@Test
	public void testIntSetIntArray() {
		IntSetTest.assertEquals("int", new int[] { 0, 1, 2, 3 }, i2);
	}

	/**
	 * Test method for
	 * 'org.contentmine.eucl.euclid.IntSet.IntSet.getSubcriptedIntSet(IntSet)'
	 */
	@Test
	public void testIntSetIntSetIntSet() {
		IntSet is0 = new IntSet(new int[] { 0, 1, 2, 3 });
		IntSet is = i1.getSubscriptedIntSet(is0);
		IntSetTest.assertEquals("copy", new int[] { 3, 4, 1, 2 }, is);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.isEqualTo(IntSet)'
	 */
	@Test
	public void testIsEqualTo() {
		Assert.assertTrue("isEqualsTo", i1.isEqualTo(i1));
		Assert.assertFalse("isEqualsTo", i1.isEqualTo(i2));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.getElements()'
	 */
	@Test
	public void testGetElements() {
		String s = Int.testEquals((new int[] {}), i0.getElements());
		if (s != null) {
			Assert.fail("getElements" + "; " + s);
		}
		s = Int.testEquals((new int[] { 3, 4, 1, 2 }), i1
						.getElements());
		if (s != null) {
			Assert.fail("getElements" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.setMax(int)'
	 */
	@Test
	public void testSetMax() {
		i1.setMax(7);
		i1.addElement(6);
		IntSetTest.assertEquals("getElements", new int[] { 3, 4, 1, 2, 6 }, i1);
		i1.addElement(7);
		String s = Int.testEquals((new int[] { 3, 4, 1, 2, 6, 7 }), i1
						.getElements());
		if (s != null) {
			Assert.fail("getElements" + "; " + s);
		}
		try {
			i1.addElement(8);
		} catch (EuclidRuntimeException e) {
			Assert
					.assertEquals(
							"addElement",
							"org.contentmine.eucl.euclid.EuclidRuntimeException: value (8)outside range (-2147483648...7)",
							S_EMPTY + e);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.setMin(int)'
	 */
	@Test
	public void testSetMin() {
		i1.setMin(-3);
		i1.addElement(-2);
		IntSetTest
				.assertEquals("getElements", new int[] { 3, 4, 1, 2, -2 }, i1);
		i1.addElement(-3);
		IntSetTest.assertEquals("getElements",
				new int[] { 3, 4, 1, 2, -2, -3 }, i1);
		try {
			i1.addElement(-4);
		} catch (EuclidRuntimeException e) {
			Assert
					.assertEquals(
							"addElement",
							"org.contentmine.eucl.euclid.EuclidRuntimeException: value (-4)outside range (-3...2147483647)",
							S_EMPTY + e);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.size()'
	 */
	@Test
	public void testSize() {
		Assert.assertEquals("size", 4, i1.size());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.addElement(int)'
	 */
	@Test
	public void testAddElement() {
		i1.addElement(6);
		IntSetTest.assertEquals("addElement", new int[] { 3, 4, 1, 2, 6 }, i1);
		try {
			i1.addElement(4);
		} catch (EuclidRuntimeException e) {
			Assert
					.assertEquals(
							"addElement",
							"org.contentmine.eucl.euclid.EuclidRuntimeException: value already in set: 4",
							S_EMPTY + e);
		}
		IntSetTest.assertEquals("addElement", new int[] { 3, 4, 1, 2, 6 }, i1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.contains(int)'
	 */
	@Test
	public void testContains() {
		Assert.assertTrue("contains", i1.contains(4));
		Assert.assertFalse("contains", i1.contains(5));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.elementAt(int)'
	 */
	@Test
	public void testElementAt() {
		Assert.assertEquals("elementAt", 4, i1.elementAt(1));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.getIntArray()'
	 */
	@Test
	public void testGetIntArray() {
		String s = Int.testEquals((new int[] { 3, 4, 1, 2 }), i1
						.getIntArray().getArray());
		if (s != null) {
			Assert.fail("getIntArray" + "; " + s);
		}
		s = Int.testEquals((new int[] {}), i0.getIntArray()
						.getArray());
		if (s != null) {
			Assert.fail("getIntArray" + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.sortAscending()'
	 */
	@Test
	public void testSortAscending() {
		i1.sortAscending();
		IntSetTest.assertEquals("sort ascending", new int[] { 1, 2, 3, 4 }, i1);
		i0.sortAscending();
		IntSetTest.assertEquals("sort ascending", new int[] {}, i0);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.addSet(IntSet)'
	 */
	@Test
	public void testAddSet() {
		i1.addSet(new IntSet(new int[] { 5, 19, 8, 33 }));
		IntSetTest.assertEquals("addSet",
				new int[] { 3, 4, 1, 2, 5, 19, 8, 33 }, i1);
		IntSetTest.assertEquals("addSet", new int[] { 0, 1, 2, 3 }, i2);
		IntSet newIs = null;
		newIs = new IntSet(new int[] { 3, 4, 5, 6 });
		try {
			i2.addSet(newIs);
		} catch (EuclidRuntimeException e) {
			Assert
					.assertEquals(
							"addSet",
							"org.contentmine.eucl.euclid.EuclidRuntimeException: duplicate element 3",
							S_EMPTY + e);
		}
		IntSetTest.assertEquals("addSet", new int[] { 0, 1, 2, 3 }, i2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.intersectionWith(IntSet)'
	 */
	@Test
	public void testIntersectionWith() {
		IntSet is1 = null;
		IntSet is2 = null;
		is1 = new IntSet(new int[] { 1, 2, 3, 4, 5 });
		is2 = new IntSet(new int[] { 4, 5, 6, 7, 3 });
		IntSet is = is1.intersectionWith((is2));
		IntSetTest.assertEquals("intersection", new int[] { 4, 5, 3 }, is);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.notIn(IntSet)'
	 */
	@Test
	public void testNotIn() {
		IntSet is1 = null;
		IntSet is2 = null;
		is1 = new IntSet(new int[] { 1, 2, 3, 4, 5 });
		is2 = new IntSet(new int[] { 4, 5, 6, 7, 3 });
		IntSet is = is1.notIn(is2);
		IntSetTest.assertEquals("notIn", new int[] { 1, 2 }, is);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.addRange(IntRange)'
	 */
	@Test
	public void testAddRange() {
		IntSet is1 = new IntSet(new int[] { 1, 2, 3, 4, 5 });
		is1.addRange(new IntRange(-2, 0));
		IntSetTest.assertEquals("addRange", new int[] { 1, 2, 3, 4, 5, -2, -1,
				0 }, is1);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.IntSet.inverseMap()'
	 */
	@Test
	public void testInverseMap() {
		IntSet is1 = new IntSet(new int[] { 4, 0, 1, 3, 2 });
		IntSet is = is1.inverseMap();
		IntSetTest.assertEquals("inverse", new int[] { 1, 2, 4, 3, 0 }, is);
	}

	@Test
	public final void testGetSubscriptedIntSet() {
		IntSet is1 = new IntSet(new int[] { 4, 0, 1, 3, 2 });
		IntSet is2 = new IntSet(new int[] { 14, 10, 11, 13, 12 });
		IntSet is3 = is2.getSubscriptedIntSet(is1);
		IntSetTest.assertEquals("subscripts", new IntSet(new int[] { 12, 14,
				10, 13, 11 }), is3);
		is1 = new IntSet(new int[] { 4, 0, 5, 3, 2 });
		try {
			is3 = is2.getSubscriptedIntSet(is1);
			Assert.fail("Should throw exception");
		} catch (EuclidRuntimeException e) {
			;// expected
		}
		is1 = new IntSet(new int[] { 4, 0, 1, 3 });
		is3 = is2.getSubscriptedIntSet(is1);
		IntSetTest.assertEquals("subscripts", new IntSet(new int[] { 12, 14,
				10, 13 }), is3);
	}

	@Test
	public final void testGetPermutations() {
		List<int[]> perm3 = IntSet.getPermutations(new Integer(3));
		String s = Int.testEquals((new int[] { 3, 2, 1 }), perm3
								.get(0));
		if (s != null) {
			Assert.fail("permutation" + "; " + s);
		}
		s = Int.testEquals((new int[] { 2, 3, 1 }), perm3
								.get(1));
		if (s != null) {
			Assert.fail("permutation" + "; " + s);
		}
		s = Int.testEquals((new int[] { 1, 2, 3 }), perm3
								.get(5));
		if (s != null) {
			Assert.fail("permutation" + "; " + s);
		}
	}

}
