package org.contentmine.eucl.euclid.euclid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author pm286
 *
 *There is no StringArray class (use List<String)> but here are some 
 *useful functions
 */
public class StringArrayTest {

	@Test
	public void testGetSets() {
		String[] strings = {"a", "b", "c",};
		List<String> stringList = Arrays.asList(strings);
		Assert.assertEquals("stringList", 3, stringList.size());	
		Set<String> stringSet = new HashSet<String>();
		stringSet.addAll(stringList);
		Assert.assertEquals("set", 3, stringSet.size());
	}
	
	@Test
	public void testGetSets1() {
		String[] strings = {"a", "a", "c",};
		List<String> stringList = Arrays.asList(strings);
		Assert.assertEquals("stringList", 3, stringList.size());	
		Set<String> stringSet = new HashSet<String>();
		stringSet.addAll(stringList);
		Assert.assertEquals("set", 2, stringSet.size());
	}
	
	
	@Test
	public void testGetSetsIdentical() {
		String[] strings = {"a", "a", "a",};
		List<String> stringList = Arrays.asList(strings);
		Assert.assertEquals("stringList", 3, stringList.size());	
		Set<String> stringSet = new HashSet<String>();
		stringSet.addAll(stringList);
		Assert.assertEquals("set", 1, stringSet.size());
	}
}
