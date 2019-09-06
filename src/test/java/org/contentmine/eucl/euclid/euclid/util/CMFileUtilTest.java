package org.contentmine.eucl.euclid.euclid.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.junit.Assert;
import org.junit.Test;

public class CMFileUtilTest {
	private static final Logger LOG = Logger.getLogger(CMFileUtilTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testSortUniqueFilesByEmbeddedIntegers() {
		List<File> files = Arrays.asList(new File[] {
				new File("/some/biff99z/foo5bar"),
				new File("/some/biff99z/foo33bar"),
				new File("/some/biff99z/foo2bar"),
				new File("/some/biff99z/foo1bar"),
				new File("/some/biff99z/foo11bar"),
				new File("/some/biff99z/foo3bar")
		});
		List<File> sortedFiles = CMFileUtil.sortUniqueFilesByEmbeddedIntegers(files);
		Assert.assertEquals("files", "[/some/biff99z/foo1bar,"
				+ " /some/biff99z/foo2bar,"
				+ " /some/biff99z/foo3bar,"
				+ " /some/biff99z/foo5bar,"
				+ " /some/biff99z/foo11bar,"
				+ " /some/biff99z/foo33bar]",
				sortedFiles.toString());
		// equal files fails
		try {
			files = Arrays.asList(new File[] {
					new File("/some/biff99z/foo5bar"),
					new File("/some/biff99z/foo33bar"),
					new File("/some/biff99z/foo5bar"),
			});
			sortedFiles = CMFileUtil.sortUniqueFilesByEmbeddedIntegers(files);
			Assert.fail("Should throw Exception for duplicate files");
		} catch (RuntimeException e) {
			Assert.assertEquals("Duplicate filename: foo5bar", e.getMessage());
		}
		// inconsistent files fail
		files = Arrays.asList(new File[] {
				new File("/some/biff99z/foo5bar"),
				new File("/some/biff99z/foo33barx"),
				new File("/some/biff99z/foo1bar"),
		});
		sortedFiles = CMFileUtil.sortUniqueFilesByEmbeddedIntegers(files);
		Assert.assertEquals(2, sortedFiles.size());
		
	}

}
