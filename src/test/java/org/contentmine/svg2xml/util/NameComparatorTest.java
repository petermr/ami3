package org.contentmine.svg2xml.util;

import org.junit.Assert;
import org.junit.Test;

public class NameComparatorTest {

	@Test
	public void testUnequalNames() {
		NameComparator comparator = new NameComparator();
		Assert.assertEquals(-1, comparator.compare("a", "b"));
		Assert.assertEquals(-1, comparator.compare("a2", "b1"));
		Assert.assertEquals(-1, comparator.compare("a1", "a2"));
		Assert.assertEquals(-1, comparator.compare("a1", "a1a"));
	}
	
	@Test
	public void testUnequalNames1() {
		NameComparator comparator = new NameComparator();
		Assert.assertEquals(-1, comparator.compare("a1", "a2"));
		Assert.assertEquals(-1, comparator.compare("a1", "a10"));
	}
	
	@Test
	public void testUnequalNames2() {
		NameComparator comparator = new NameComparator();
		Assert.assertEquals(-1, comparator.compare("a1", "a2"));
		Assert.assertEquals(-1, comparator.compare("a10", "a10a"));
	}
	
	@Test
	public void testEqualNames() {
		NameComparator comparator = new NameComparator();
		Assert.assertEquals(0, comparator.compare("a1", "a1"));
	}
}
