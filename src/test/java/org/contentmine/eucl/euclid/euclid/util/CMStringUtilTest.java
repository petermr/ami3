package org.contentmine.eucl.euclid.euclid.util;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.CMStringUtil;
import org.junit.Assert;
import org.junit.Test;

public class CMStringUtilTest {
	private static final Logger LOG = Logger.getLogger(CMStringUtilTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testSortUniqueStringsByEmbeddedIntegers() {
		List<String> strings = Arrays.asList(new String[] {
				"foo5bar",
				"foo33bar",
				"foo2bar",
				"foo1bar",
				"foo11bar",
				"foo3bar"
		});
		List<String> sortedStrings = CMStringUtil.sortUniqueStringsByEmbeddedIntegers(strings);
		Assert.assertEquals("strings", "[foo1bar, foo2bar, foo3bar, foo5bar, foo11bar, foo33bar]",
				sortedStrings.toString());
		// equal strings fails
		try {
			strings = Arrays.asList(new String[] {
					"foo5bar",
					"foo33bar",
					"foo5bar",
			});
			sortedStrings = CMStringUtil.sortUniqueStringsByEmbeddedIntegers(strings);
			Assert.fail("Should throw Exception for duplicate strings");
		} catch (RuntimeException e) {
			Assert.assertTrue(true);
		}
		// inconsistent strings
		strings = Arrays.asList(new String[] {
			"foo5bar",
			"foo33barx",
			"foo1bar",
		});
		sortedStrings = CMStringUtil.sortUniqueStringsByEmbeddedIntegers(strings);
		Assert.assertEquals(2, sortedStrings.size());
		
	}
}
