package org.contentmine.eucl.euclid.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.junit.Test;

import com.google.common.collect.BiMap;

import junit.framework.Assert;

public class CMFileUtilTest {
	private static final Logger LOG = Logger.getLogger(CMFileUtilTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testRenameCaseInsensitive0() throws IOException {
		File targetDir = CMineFixtures.TARGET_DIR;
		File fileUpper = new File(targetDir, "A.txt");
		FileUtils.touch(fileUpper);
		Assert.assertTrue(""+fileUpper, fileUpper.exists());
		File fileUpperJunk = new File(targetDir, "A.txt.jnk");
		if (fileUpperJunk.exists()) FileUtils.forceDelete(fileUpperJunk);
		Assert.assertFalse(""+fileUpperJunk, fileUpperJunk.exists());
		
		FileUtils.moveFile(fileUpper, fileUpperJunk);
		
		Assert.assertFalse(""+fileUpper, fileUpper.exists());
		Assert.assertTrue(""+fileUpperJunk, fileUpperJunk.exists());
		
		File fileLower = new File(targetDir, "a.txt");
		if (fileLower.exists()) FileUtils.forceDelete(fileLower);
		FileUtils.moveFile(fileUpperJunk, fileLower);
		
		Assert.assertTrue(""+fileLower, fileLower.exists());
		Assert.assertFalse(""+fileUpperJunk, fileUpperJunk.exists());

	}
	
	@Test
	public void testRenameCaseInsensitive() throws IOException {
		File targetDir = CMineFixtures.TARGET_DIR;
		File fileUpper = new File(targetDir, "A.txt");
		FileUtils.touch(fileUpper);
		File fileLower = CMFileUtil.convertNameToLowerCase(fileUpper);
		
		Assert.assertTrue(""+fileUpper, !fileUpper.exists());
		Assert.assertTrue(""+fileLower, fileLower.exists());
		Assert.assertEquals(""+fileLower, "Users/pm286/workspace/cmdev/cephis/target/a.txt", fileLower.toString());

	}
	
	@Test
	public void testCompressFileName() throws IOException {
		File targetDir = CMineFixtures.TARGET_DIR;
		File filesDir = new File(targetDir, "filesRename/");
		if (filesDir.exists()) FileUtils.forceDelete(filesDir);
		filesDir.mkdirs();
		List<File> files = new ArrayList<File>();
		int maxLength = 12;
		files.add(create(filesDir, "ABC123.pdf"));
		files.add(create(filesDir, "ABC123&4~'zzz.pdf"));
		files.add(create(filesDir, "ABC123&4~'zzz22.pdf"));
		files.add(create(filesDir, "ABC123&4~'zz322.pdf"));
		files.add(create(filesDir, "ABC123&4~'zz2.pdf"));
		CMFileUtil cmFileUtil = new CMFileUtil();
		cmFileUtil.add(files);
		cmFileUtil.compressFileNames(maxLength);
		BiMap<File, File> newFileByOldFile = cmFileUtil.getOrCreateNewFileByOldFile();
		Set<File> oldKeySet = cmFileUtil.getOldKeySet();
		BiMap<File, File> oldFileByNewFile = cmFileUtil.getOrCreateOldFileByNewFile();
		Set<File> newKeySet = cmFileUtil.getOrCreateOldFileByNewFile().keySet();

		Assert.assertEquals("old keys",
				"["
				+ "target/filesRename/ABC123&4~'zz2.pdf,"
				+ " target/filesRename/ABC123&4~'zzz.pdf,"
				+ " target/filesRename/ABC123&4~'zz322.pdf,"
				+ " target/filesRename/ABC123.pdf,"
				+ " target/filesRename/ABC123&4~'zzz22.pdf]",
				oldKeySet.toString());
		Assert.assertEquals(5, newKeySet.size());
		testNewFileExistsAndIsInMap(newKeySet, filesDir, "abc123.pdf");
		testNewFileExistsAndIsInMap(newKeySet, filesDir, "abc123_4__zz4.pdf");
		testNewFileExistsAndIsInMap(newKeySet, filesDir, "abc123_4__zz.pdf");
		testNewFileExistsAndIsInMap(newKeySet, filesDir, "abc123_4__zz3.pdf");
		testNewFileExistsAndIsInMap(newKeySet, filesDir, "abc123_4__zz2.pdf");
		
	}

	private void testNewFileExistsAndIsInMap(Set<File> newKeySet, File parent, String pathname) {
		File f = new File(parent, pathname);
		// this is difficult as string value is sometimes absolutes, sometimes relative
//		Assert.assertTrue(newKeySet.contains(f));
		Assert.assertTrue(f.exists());
	}

	private File create(File dir, String pathname) {
		File f = new File(dir, pathname);
		try {
			FileUtils.touch(f);
		} catch (IOException ioe) {
			throw new RuntimeException("cannot touch "+f, ioe);
		}
		return f;
	}
	
}
