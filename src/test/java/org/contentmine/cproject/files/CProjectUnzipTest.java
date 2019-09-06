package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.CProjectArgProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.junit.Ignore;
import org.junit.Test;

public class CProjectUnzipTest {

	private static final Logger LOG = Logger.getLogger(CProjectUnzipTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private File zipsDir;
	private File targetZips;

	@Test
	public void testUnzip() throws IOException {
		File zipsDir = new File(CMineFixtures.TEST_MISC_DIR, "zips");
		if (!TestUtil.checkForeignDirExists(zipsDir)) {return;}
		copyToAndCleanOutDir(zipsDir);
		String args = "-i fulltext.xml -o scholarly.html --project "+targetZips;
		LOG.trace(args);
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}

	@Test
	public void testUnzipWithArgs() throws IOException {
		File zipsDir = new File(CMineFixtures.TEST_MISC_DIR, "zips");
		if (!TestUtil.checkForeignDirExists(zipsDir)) {return;}
		copyToAndCleanOutDir(zipsDir);
		String args = "-i fulltext.xml --unzip --include .*\\.XML --rename .*\\.XML fulltext.xml --project "+targetZips;
		LOG.trace(args);
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}

	@Test
	@Ignore // large
	public void testUnzipLarge() throws IOException {
		copyToAndCleanOutDir(new File("../patents/I20150317/UTIL08979"));
		String args = "-i fulltext.xml --unzip --include .*\\.XML --rename .*\\.XML fulltext.xml --project "+targetZips;
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}

	private void copyToAndCleanOutDir(File zipsDir) throws IOException {
		targetZips = new File("target/zips");
		if (targetZips.exists()) {
			FileUtils.deleteDirectory(targetZips);
		}
		FileUtils.copyDirectory(zipsDir,  targetZips);
		
	}
}
