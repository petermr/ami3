package org.contentmine.cproject.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.Unzipper;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnZipTest {

	private static final Logger LOG = Logger.getLogger(UnZipTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	File outDir;
	File targetZip = new File("target/zip");
	File miscZipDir = new File(CMineFixtures.TEST_MISC_DIR, "zip");
	
	@Before
	public void setup() throws IOException {
	}
	
	@Test
	public void testUnZip() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		copyAndCleanOutDir(miscZipDir, targetZip);
		Unzipper unZipper = new Unzipper();
		unZipper.setZipFile(new File(targetZip,"test.zip"));
		unZipper.setOutDir(outDir);
		unZipper.extractZip();
		Assert.assertEquals(11, FileUtils.listFiles(outDir, new String[] {"TIF"}, true).size());
		Assert.assertEquals(1, FileUtils.listFiles(outDir, new String[] {"XML"}, true).size());
	}
	
	@Test
	public void testUnZipBad() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		copyAndCleanOutDir(miscZipDir, targetZip);
		Unzipper unZipper = new Unzipper();
		unZipper.setZipFile(new File(CMineFixtures.TEST_MISC_DIR, "badzip/US08979996-20150317.ZIP"));
		unZipper.setOutDir(outDir);
		unZipper.extractZip();
		String zipRoot = unZipper.getZipRootName();
		LOG.trace(zipRoot);
		Assert.assertEquals(5, FileUtils.listFiles(outDir, new String[] {"TIF"}, true).size());
		Assert.assertEquals(1, FileUtils.listFiles(outDir, new String[] {"XML"}, true).size());
	}
	
	
	@Test
	public void testUnZipInclude() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		copyAndCleanOutDir(miscZipDir, targetZip);
		Unzipper unZipper = new Unzipper();
		unZipper.setZipFile(new File(targetZip,"test.zip"));
		unZipper.setOutDir(outDir);
		unZipper.setIncludePatternString(".*\\.XML");
		unZipper.extractZip();
		Assert.assertEquals(0, FileUtils.listFiles(outDir, new String[] {"TIF"}, true).size());
		Assert.assertEquals(1, FileUtils.listFiles(outDir, new String[] {"XML"}, true).size());
	}

	@Test
	public void testUnZipExclude() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		copyAndCleanOutDir(miscZipDir, targetZip);
		Unzipper unZipper = new Unzipper();
		unZipper.setZipFile(new File(targetZip,"test.zip"));
		unZipper.setOutDir(outDir);
		unZipper.setExcludePatternString(".*\\.XML");
		unZipper.extractZip();
		Assert.assertEquals(11, FileUtils.listFiles(outDir, new String[] {"TIF"}, true).size());
		Assert.assertEquals(0, FileUtils.listFiles(outDir, new String[] {"XML"}, true).size());
	}

	@Test
	public void testUnzipRoot() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		copyAndCleanOutDir(miscZipDir,targetZip);
		Unzipper unZipper = new Unzipper();
		unZipper.setZipFile(new File(targetZip,"test.zip"));
		unZipper.setOutDir(outDir);
		unZipper.setExcludePatternString(".*\\.XML");
		unZipper.extractZip();
		String zipRoot = unZipper.getZipRootName();
		Assert.assertEquals("US08979000-20150317/", zipRoot.toString());
	}


	private void copyAndCleanOutDir(File srcDir, File targetZip) throws IOException {
		FileUtils.copyDirectory(srcDir, targetZip);
		outDir = new File(targetZip, "out");
		if (outDir.exists()) {
			FileUtils.deleteDirectory(outDir);
		}
		FileUtils.copyDirectory(new File(CMineFixtures.TEST_MISC_DIR, "zip"), targetZip);
	}
}
