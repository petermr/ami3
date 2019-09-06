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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Int2
 * 
 * @author pmr
 * 
 */
public class Int2Test {

	Int2 i0;

	Int2 i11;

	Int2 i12;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		i0 = new Int2();
		i11 = new Int2(1, 1);
		i12 = new Int2(1, 2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.Int2()'
	 */
	@Test
	public void testInt2() {
		Assert.assertEquals("int2", "(0,0)", i0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.Int2(int, int)'
	 */
	@Test
	public void testInt2IntInt() {
		Assert.assertEquals("int2", "(1,2)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.Int2(Int2)'
	 */
	@Test
	public void testInt2Int2() {
		Int2 ii = new Int2(i12);
		Assert.assertEquals("int2", "(1,2)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.swap()'
	 */
	@Test
	public void testSwap() {
		i12.swap();
		Assert.assertEquals("int2", "(2,1)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.sortAscending()'
	 */
	@Test
	public void testSortAscending() {
		i12.sortAscending();
		Assert.assertEquals("int2", "(1,2)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.sortDescending()'
	 */
	@Test
	public void testSortDescending() {
		i12.sortDescending();
		Assert.assertEquals("int2", "(2,1)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.clear()'
	 */
	@Test
	public void testClear() {
		i12.clear();
		Assert.assertEquals("int2", "(0,0)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.setX(int)'
	 */
	@Test
	public void testSetX() {
		i12.setX(3);
		Assert.assertEquals("int2", "(3,2)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.setY(int)'
	 */
	@Test
	public void testSetY() {
		i12.setY(3);
		Assert.assertEquals("int2", "(1,3)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.isEqualTo(Int2)'
	 */
	@Test
	public void testIsEqualTo() {
		Assert.assertTrue("equals", i12.isEqualTo(i12));
		Assert.assertFalse("equals", i11.isEqualTo(i12));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.plus(Int2)'
	 */
	@Test
	public void testPlus() {
		Int2 ii = i12.plus(i11);
		Assert.assertEquals("plus", "(2,3)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.subtract(Int2)'
	 */
	@Test
	public void testSubtract() {
		Int2 ii = i12.subtract(i11);
		Assert.assertEquals("subtract", "(0,1)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.negative()'
	 */
	@Test
	public void testNegative() {
		i12.negative();
		Assert.assertEquals("negative", "(-1,-2)", i12.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.multiplyBy(int)'
	 */
	@Test
	public void testMultiplyBy() {
		Int2 ii = i12.multiplyBy(3);
		Assert.assertEquals("multiply", "(3,6)", ii.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.getX()'
	 */
	@Test
	public void testGetX() {
		Assert.assertEquals("getX", 1, i12.getX());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.getY()'
	 */
	@Test
	public void testGetY() {
		Assert.assertEquals("getY", 2, i12.getY());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.elementAt(int)'
	 */
	@Test
	public void testElementAt() {
		Assert.assertEquals("elementAt", 1, i12.elementAt(0));
		Assert.assertEquals("elementAt", 2, i12.elementAt(1));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.getMidPoint(Int2)'
	 */
	@Test
	public void testGetMidPoint() {
		Int2 m = i12.getMidPoint(new Int2(3, 4));
		Assert.assertEquals("mid point", "(2,3)", m.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.dotProduct(Int2)'
	 */
	@Test
	public void testDotProduct() {
		int i = i12.dotProduct(new Int2(3, 4));
		Assert.assertEquals("dor", 11, i);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int2.toString()'
	 */
	@Test
	public void testToString() {
		Assert.assertEquals("toString", "(1,2)", i12.toString());
	}

}
