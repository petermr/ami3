package org.contentmine.cproject.metadata.quickscrape;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.junit.Assert;
import org.junit.Test;

public class QuickscrapeDownloadTest {
	
	private static final Logger LOG = Logger.getLogger(QuickscrapeDownloadTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	/** THIS IS MERELY PREPARATORY.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateShuffledURLsFromProject() throws IOException {
		File targetDirectory = new File(CMineFixtures.GETPAPERS_TARGET, "20160601small");
		CProject cProject = createTargetDirectoryAndProject(CMineFixtures.GETPAPERS_SRC_20160601, targetDirectory);
		File urlFile      = cProject.createAllowedFile(CProject.URL_LIST);
		FileUtils.deleteQuietly(urlFile);
		cProject.extractShuffledUrlsFromCrossrefToFile(urlFile);
		Assert.assertTrue(urlFile.exists());
		Assert.assertEquals(20, FileUtils.readLines(urlFile).size());
	}

	// -----------------------------
	// make a clean copy in target
	private CProject createTargetDirectoryAndProject(File cProjectDir, File targetDir) throws IOException {
		FileUtils.deleteQuietly(targetDir);
		FileUtils.copyDirectory(cProjectDir, targetDir);
		CProject cProject = new CProject(targetDir);
		return cProject;
	}

}
