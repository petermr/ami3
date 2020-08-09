package org.contentmine.cproject.util;

import java.io.File;
import java.util.List;

import org.contentmine.ami.tools.AbstractAMITest;
import org.junit.Test;

public class DirectoryMergerTest {

	@Test
	public void testMerge() {
		File parent = AbstractAMITest.TEST_BATTERY10;
		File newDirectory = new File("target/merge/battery/");
		List<File> resultsDirList = new CMineGlobber().setLocation(parent)
		    .setGlob("**/PMC*/results/**/results.xml").setRecurse(true).listFiles();
		DirectoryMerger directoryMerger = new DirectoryMerger()
				.setFromParent(parent)
				.setToParent(newDirectory)
				.merge(resultsDirList);
	}
}
