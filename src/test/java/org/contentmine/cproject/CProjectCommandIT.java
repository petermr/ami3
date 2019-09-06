package org.contentmine.cproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.crossref.CrossrefMD;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonArray;

@Ignore("This really should be in POM or CL")
public class CProjectCommandIT {

	/** INPUT AND OUTPUT URLS
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInputOutputUrls1() throws IOException {
		File source1Dir = new File(CMineFixtures.TEST_OPEN_DIR, "truncated");
		File inUrls = new File(CMineFixtures.TEST_OPEN_DIR, "truncated/urls.txt");
		Assert.assertTrue("urls "+inUrls.getAbsolutePath(), inUrls.exists());
		int in = FileUtils.readLines(inUrls).size();
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "truncated");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		File outUrls = new File(target1Dir, "outUrls.txt");
		Assert.assertFalse(outUrls.getAbsolutePath()+" exists", outUrls.exists());
		String cmd = "--project "+target1Dir.toString()+" --inUrls "+" urls.txt" +" --outUrls outUrls.txt";
		new CProject().run(cmd);
		Assert.assertTrue(outUrls.getAbsolutePath()+" exists", outUrls.exists());
		List<String> lines = FileUtils.readLines(outUrls);
		Collections.sort(lines);
		Assert.assertEquals("out urls ", 15, lines.size());
		Assert.assertEquals("url ",  "http://dx.doi.org/10.1063/1.4943235", lines.get(0));
		
	}

	/** MERGE PROJECTS
	 * 
	 * This 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMergeCprojects() throws IOException {
		File source1Dir = new File(CMineFixtures.TEST_OPEN_DIR, "licmini");
		File source2Dir = new File(CMineFixtures.TEST_OPEN_DIR, "pubmini");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "test/licmini");
		File target2Dir = new File(CMineFixtures.GETPAPERS_TARGET, "test/pubmini");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CMineTestFixtures.cleanAndCopyDir(source2Dir, target2Dir);
		CProject project1 = new CProject(target1Dir);
		CProject project2 = new CProject(target2Dir);
		Assert.assertEquals("project1", 57, project1.getOrCreateCTreeList().size());
		Assert.assertEquals("project2",278, project2.getOrCreateCTreeList().size());
		File duplicates = new File(CMineFixtures.GETPAPERS_TARGET, "test/duplicates");
	
		String cmd = "--project "+target1Dir.toString()+" --mergeProjects "+target2Dir.toString()+" --duplicates "+duplicates.toString();
		new CProject().run(cmd);
	
		CProject project1a = new CProject(target1Dir);
		Assert.assertEquals("project1", 310, project1a.getOrCreateCTreeList().size());
		JsonArray array = new CrossrefMD().readMetadataArrayFromConcatenatedFile(new File(target1Dir, "crossref_results.json"));
		Assert.assertEquals(310, array.size());
		Assert.assertEquals("project2", 278, project2.getOrCreateCTreeList().size());
		Assert.assertTrue("duplicates exists", duplicates.exists());
		Assert.assertTrue("duplicates is directory ", duplicates.isDirectory());
		CProject duplicatesProject = new CProject(duplicates);
		Assert.assertEquals("duplicates trees", 25, duplicatesProject.getOrCreateCTreeList().size());
	
	}

	/** CHECK DOWNLOADED FILES
	 * 
	 * sometimes the files downloaded by quickscrape are different MIM types from exoected.
	 * Most commonly "fulltext.pdf" is actually an HTML (?frame) indicating that download failed.
	 * 
	 * This utility will detect incorrect PDFs and rename `fulltext.pdf` to `fulltext.pdf.html`
	 * make take several hundred ms to read bytes from a large PDF file
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCheckFileContents() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "lic20160201truncated");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "lic20160201truncated");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		List<File> pdfFiles = new ArrayList<File>(FileUtils.listFiles(target1Dir, new String[]{"pdf"}, true));
		Assert.assertEquals(26,  pdfFiles.size());
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 43, project1.getOrCreateCTreeList().size());
		File pdf = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf");
		Assert.assertTrue("pdf exists", pdf.exists());
		File pdfHtml = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf.html");
		Assert.assertFalse("pdfHtml not exists", pdfHtml.exists());
	
		String cmd = "--project "+target1Dir.toString()+" --renamePDF";
		new CProject().run(cmd);
		
		pdf = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf");
		Assert.assertFalse("pdf not exists", pdf.exists());
		pdfHtml = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf.html");
		Assert.assertTrue("pdfHtml exists", pdfHtml.exists());
		
		pdfFiles = new ArrayList<File>(FileUtils.listFiles(target1Dir, new String[]{"pdf"}, true));
		Assert.assertEquals(25,  pdfFiles.size());
	
	}

}
