package org.contentmine.cproject.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class GlobTest {

	private static final Logger LOG = Logger.getLogger(GlobTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testGlob() throws IOException {
		CMineGlobber globber = new CMineGlobber();
		globber.setLocation("src/test/resources");
		globber.setGlob("glob:**/results.xml");
		List<File> files = globber.listFiles();
		Assert.assertTrue(files.size() > 10);
	}

	@Test
	public void testGlob1() throws IOException {
		CMineGlobber globber = new CMineGlobber();
		globber.setLocation("src/test/resources");
		globber.setGlob("glob:**/frequencies/results.xml");
		List<File> files = globber.listFiles();
		Assert.assertTrue(files.size() >= 2);
	}

	@Test
	public void testGlob2() throws IOException {
		CMineGlobber globber = new CMineGlobber();
		globber.setLocation("src/test/resources");
		globber.setGlob("**/regex10/**/{human,mo*e}/results.xml");
		List<File> files = globber.listFiles();
		Assert.assertTrue(files.size() >= 2);
	}

	@Test
	public void testDelete() throws IOException {
		CMineGlobber globber = new CMineGlobber();
		File targetTempDir = new File("target/temp/glob/");
		targetTempDir.mkdirs();
		FileUtils.cleanDirectory(targetTempDir);
		for (int i = 0; i < 10; i++) {
			File f = new File(targetTempDir, ""+i+".junk");
			FileUtils.touch(f);
		}
		Assert.assertEquals(10,  FileUtils.listFiles(targetTempDir, new String[]{"junk"}, false).size());
		globber.setLocation(targetTempDir.toString());
		globber.setGlob(targetTempDir.toString()+"/*.junk");
		List<File> files = globber.listFiles();
		Assert.assertEquals(10, files.size());
		globber.deleteFiles();
		Assert.assertEquals(0,  FileUtils.listFiles(targetTempDir, new String[]{}, false).size());
	}

}
