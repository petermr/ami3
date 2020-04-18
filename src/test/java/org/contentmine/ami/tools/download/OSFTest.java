package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTreeList;
import org.junit.Assert;
import org.junit.Test;

public class OSFTest {

	@Test
	/** issues a search  and turns results into hitList
	 * 
	 */
	public void testOSFIT() throws IOException {
		File targetDir = new File("target/osf/testsearch");
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		String args = 
				"-p " + cProject.toString()
				+ " --site osf"
				+ " --query climate change"
				+ " --metadata __metadata"
				+ " --rawfiletypes html"
				+ " --pagesize 4"
				+ " --pages 1 1"
				+ " --limit 4"
				+ " --resultset hitList1.clean.html"
			;
		AMIDownloadTool downloadTool = new AMIDownloadTool();
		downloadTool.runCommands(args);
		Assert.assertTrue(new File(targetDir, "__metadata/hitList1.html").exists());
		Assert.assertTrue(new File(targetDir, "__metadata/hitList1.clean.html").exists());
		CTreeList cTreeList = new CProject(targetDir).getOrCreateCTreeList();
		Assert.assertEquals(4, cTreeList.size());
		File directory0 = cTreeList.get(0).getDirectory();
		Assert.assertTrue(new File(directory0, "landingPage.html").exists());
		Assert.assertTrue(new File(directory0, "rawFullText.html").exists());
		Assert.assertTrue(new File(directory0, "scholarly.html").exists());
		Assert.assertTrue(new File(directory0, "scrapedMetadata.html").exists());
	}


}
