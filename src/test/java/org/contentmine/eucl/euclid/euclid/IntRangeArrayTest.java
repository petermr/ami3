package org.contentmine.eucl.euclid.euclid;

import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;
import org.junit.Assert;
import org.junit.Test;

public class IntRangeArrayTest {

	private IntRange ir10_25 = new IntRange(10, 25);
	private IntRange ir10_20 = new IntRange(10, 20);
	private IntRange ir30_40 = new IntRange(30, 40);
	private IntRange ir15_25 = new IntRange(15, 25);
	private IntRange ir15_17 = new IntRange(15, 17);
	private IntRange ir50_60 = new IntRange(50, 60);
	
	@Test
	public void testAdd() {
		IntRangeArray array = new IntRangeArray();
		Assert.assertEquals(0, array.size());
	}
	
	@Test
	public void testAdd1() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		Assert.assertEquals(1, array.size());
	}
	
	@Test
	public void testAdd2() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		array.add(ir15_25);
		Assert.assertEquals(2, array.size());
	}
	
	@Test
	public void testEquals() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		array.add(ir15_25);
		IntRangeArray array2 = new IntRangeArray();
		array2.add(ir10_20);
		array2.add(ir15_25);
		Assert.assertTrue(array.equals(array2));
	}
	
	@Test
	/**
	 * order of addition matters
	 */
	public void testNotEquals() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		array.add(ir15_25);
		IntRangeArray array2 = new IntRangeArray();
		array2.add(ir15_25);
		array2.add(ir10_20);
		Assert.assertFalse("order matters", array.equals(array2));
	}
	
	@Test
	/**
	 * order of addition matters
	 */
	public void testSort() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		array.add(ir15_17);
		array.add(ir15_25);
		array.add(ir30_40);
		array.add(ir50_60);
		IntRangeArray array2 = new IntRangeArray();
		array2.add(ir50_60);
		array2.add(ir15_25);
		array2.add(ir30_40);
		array2.add(ir15_17);
		array2.add(ir10_20);
		Assert.assertFalse("order matters", array.equals(array2));
		array2.sort();
		Assert.assertTrue("after sorting", array.equals(array2));
	}
	
	@Test
	/**
	 * sort and overlap
	 */
	public void testSortAndRemoveOverlapping() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_25);
		array.add(ir30_40);
		array.add(ir50_60);
		IntRangeArray array2 = new IntRangeArray();
		array2.add(ir50_60);
		array2.add(ir15_25);
		array2.add(ir30_40);
		array2.add(ir15_17);
		array2.add(ir10_20);
		Assert.assertFalse("order matters", array.equals(array2));
		array2.sortAndRemoveOverlapping();
		Assert.assertTrue("after sorting", array.equals(array2));
	}
	
	@Test
	/**
	 */
	public void testPlus() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		array.add(ir30_40);
		array.add(ir50_60);
		IntRangeArray array2 = new IntRangeArray();
		array2.add(ir15_25);
		array2.add(ir15_17);
		IntRangeArray plus = array2.plus(array);
		IntRangeArray ref = new IntRangeArray();
		ref.add(ir10_25);
		ref.add(ir30_40);
		ref.add(ir50_60);
		Assert.assertTrue("after sorting", ref.equals(plus));
	}
	
	@Test
	/**
	 */
	public void testInverse() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		array.add(ir30_40);
		array.add(ir50_60);
		IntRangeArray inverse = array.inverse();
		IntRangeArray ref = new IntRangeArray();
		ref.add(new IntRange(20, 30));
		ref.add(new IntRange(40, 50));
		Assert.assertTrue("after sorting", ref.equals(inverse));
	}
	
	@Test
	/**
	 */
	public void testInverse0() {
		IntRangeArray array = new IntRangeArray();
		IntRangeArray inverse = array.inverse();
		Assert.assertNull(inverse);
	}
	
	@Test
	/**
	 */
	public void testInverse1() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		IntRangeArray inverse = array.inverse();
		Assert.assertEquals(0, inverse.size());
	}
	@Test
	/**
	 */
	public void testInverse1a() {
		IntRangeArray array = new IntRangeArray();
		array.add(ir10_20);
		array.add(ir15_25);
		IntRangeArray inverse = array.inverse();
		Assert.assertEquals("inverse without caps", 0, inverse.size());
	}
	
	@Test
	/**
	 */
	public void testInverseWithCaps() {
		IntRangeArray array = new IntRangeArray();
		array.add(new IntRange(0,0));
		array.add(new IntRange(100,100));
		IntRangeArray inverse = array.inverse();
		Assert.assertEquals(1, inverse.size());
		IntRangeArray ref = new IntRangeArray();
		ref.add(new IntRange(0,100));
		Assert.assertTrue("inverse", ref.equals(inverse));
	}
}
