package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIMakeProjectTool;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.NormaFixtures;
import org.junit.Assert;
import org.junit.Test;

/** tests AMIMakeProject
 * 
 * @author pm286
 *
 */
public class AMIMakeProjectTest {
	private static final Logger LOG = Logger.getLogger(AMIMakeProjectTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String PROJECT1 = "project1/";

	@Test
	public void testMakeProject1long() throws Exception {
		File targetDir = new File(NormaFixtures.TARGET_MAKEPROJECT_DIR, PROJECT1);
		CMineTestFixtures.cleanAndCopyDir(
				new File(NormaFixtures.TEST_MAKEPROJECT_DIR, PROJECT1),
				targetDir);
		Assert.assertTrue(targetDir.exists());
		String cmd = "-p " + targetDir +" --rawfiletypes html,pdf,xml";
		AMIMakeProjectTool.main(cmd);
		List<File> childDirectories = CMineGlobber.listSortedChildDirectories(targetDir);
		Assert.assertEquals("ergen_canagli_17_", FilenameUtils.getBaseName(childDirectories.get(0).toString()));
		Assert.assertEquals("["
				+ "target/makeproject/project1/ergen_canagli_17_,"
				+ " target/makeproject/project1/jneurosci.4415-13.2014,"
				+ " target/makeproject/project1/multiple-1471-2148-11-312,"
				+ " target/makeproject/project1/multiple-1471-2148-11-313,"
				+ " target/makeproject/project1/multiple.312,"
				+ " target/makeproject/project1/pb1,"
				+ " target/makeproject/project1/tree-1471-2148-11-313]", childDirectories.toString());
		Assert.assertEquals(7, childDirectories.size());
		Assert.assertEquals(3, CMineGlobber.listSortedDescendantFiles(targetDir, CTree.HTML).size());
		Assert.assertEquals(5, CMineGlobber.listSortedDescendantFiles(targetDir, CTree.PDF).size());
	}
	
	@Test
	public void testMakeProject1short() throws Exception {
		File targetDir = new File(NormaFixtures.TARGET_MAKEPROJECT_DIR, PROJECT1);
		CMineTestFixtures.cleanAndCopyDir(
				new File(NormaFixtures.TEST_MAKEPROJECT_DIR, PROJECT1),
				targetDir);
		Assert.assertTrue(targetDir.exists());
		String cmd = "-p " + targetDir +" --rawfiletypes html,pdf,xml --compress 12";
		AMIMakeProjectTool.main(cmd);
		List<File> childDirectories = CMineGlobber.listSortedChildDirectories(targetDir);
		Assert.assertEquals(7, childDirectories.size());
		Assert.assertEquals("ergen_canagl", FilenameUtils.getBaseName(childDirectories.get(0).toString()));
		Assert.assertEquals("[target/makeproject/project1/ergen_canagl,"
				+ " target/makeproject/project1/jneurosci.44,"
				+ " target/makeproject/project1/multiple-147,"
				+ " target/makeproject/project1/multiple-1472,"
				+ " target/makeproject/project1/multiple.312,"
				+ " target/makeproject/project1/pb1,"
				+ " target/makeproject/project1/tree-1471-21]", childDirectories.toString());
		Assert.assertEquals(7, childDirectories.size());
		Assert.assertEquals(3, CMineGlobber.listSortedDescendantFiles(targetDir, CTree.HTML).size());
		Assert.assertEquals(5, CMineGlobber.listSortedDescendantFiles(targetDir, CTree.PDF).size());
	}
	
	@Test
	public void testMakeProjectLog() throws Exception {
		File targetDir = new File(NormaFixtures.TARGET_MAKEPROJECT_DIR, PROJECT1);
		CMineTestFixtures.cleanAndCopyDir(
				new File(NormaFixtures.TEST_MAKEPROJECT_DIR, PROJECT1),
				targetDir);
		Assert.assertTrue(targetDir.exists());
		String cmd = "-p " + targetDir +" --rawfiletypes html,pdf,xml --compress 25";
		AbstractAMITool  amiMakeProject = new AMIMakeProjectTool();
		amiMakeProject.runCommands(cmd);
		checkOutputFiles(targetDir, amiMakeProject);
	}

	/**
	 * PRODUCTION
	 * Converts a mixture of html and pdf selectively to just pdf CTrees
	 */ 
	@Test
	public void testMakePartProjectPicocli() throws IOException {
		File targetDir = new File(NormaFixtures.TARGET_MAKEPROJECT_DIR, PROJECT1);
		CMineTestFixtures.cleanAndCopyDir(
				new File(NormaFixtures.TEST_MAKEPROJECT_DIR, PROJECT1),
				targetDir);
		// copy test to target/project1/*.{html,pdf}
		Assert.assertTrue(targetDir.exists());
		// child files
		List<File> files =  new CMineGlobber("**/project1/*.pdf", targetDir).listFiles();
		Assert.assertEquals(5, files.size());
		// no fulltext yet
		files =  new CMineGlobber("**/project1/fulltext.pdf", targetDir).listFiles();
		Assert.assertEquals(0, files.size());
		files =  new CMineGlobber("**/project1/*.html", targetDir).listFiles();
		Assert.assertEquals(3, files.size());
		files =  new CMineGlobber("**/*.xml", targetDir).listFiles();
		Assert.assertEquals(0, files.size());
		files =  new CMineGlobber("**/project1/*.{html,pdf}", targetDir).listFiles();
		Assert.assertEquals(8, files.size());
		String[] args = {
			"--cproject", targetDir.toString(),
			// only convert PDF at this stage
			"--rawfiletypes", "pdf",
			"--log4j", "org.contentmine.ami.AMIMakeProject", "INFO"
			};
		new AMIMakeProjectTool().runCommands(args);
		files =  new CMineGlobber("**/fulltext.pdf", targetDir).listFiles();
		Assert.assertEquals(5, files.size());
		files =  new CMineGlobber("**/fulltext.html", targetDir).listFiles();
		Assert.assertEquals(0, files.size());
		files =  new CMineGlobber("**/fulltext.{pdf,html}", targetDir).listFiles();
		Assert.assertEquals(5, files.size());
		files =  new CMineGlobber("**/project1/fulltext.{pdf,html}", targetDir).listFiles();
		Assert.assertEquals(0, files.size());
		files =  new CMineGlobber("**/project1/*/fulltext.{pdf,html}", targetDir).listFiles();
		Assert.assertEquals(5, files.size());
		files =  new CMineGlobber("**/project1/*.html", targetDir).listFiles();
		Assert.assertEquals(3, files.size());
	}

	/**
	 * PRODUCTION
	 * SHOWCASE
	 * Converts a mixture of html and pdf selectively to just a complete CProject
	 */
	@Test
	public void testMakeProjectPicocli() throws IOException {
		File targetDir = new File(NormaFixtures.TARGET_MAKEPROJECT_DIR, PROJECT1);
		CMineTestFixtures.cleanAndCopyDir(
				new File(NormaFixtures.TEST_MAKEPROJECT_DIR, PROJECT1),
				targetDir);
		List<File> files = null;
		// contains both html and PDF files
		// note spaces and long names
		files = CMineGlobber.listSortedChildFiles(targetDir);
		Assert.assertEquals(8,  files.size());
		Assert.assertEquals(
				"[target/makeproject/project1/Ergen & Canagli_17'.pdf,"
				+ " target/makeproject/project1/JNEUROSCI.4415-13.2014.html,"
				+ " target/makeproject/project1/multiple-1471-2148-11-312.pdf,"
				+ " target/makeproject/project1/multiple-1471-2148-11-313.pdf,"
				+ " target/makeproject/project1/multiple.312.html,"
				+ " target/makeproject/project1/pb1.html,"
				+ " target/makeproject/project1/pb1.pdf,"
				+ " target/makeproject/project1/tree-1471-2148-11-313.pdf]",
				  files.toString());
		files = CMineGlobber.listSortedChildFiles(targetDir, "pdf");
		// as above
				 
		String[] args = {
			"--cproject", targetDir.toString(),
			"--rawfiletypes", "pdf,html",
			"--log4j", "org.contentmine.ami.AMIMakeProject", "INFO"
			};
		new AMIMakeProjectTool().runCommands(args);

		// new directories
		files = CMineGlobber.listSortedChildDirectories(targetDir);
		Assert.assertEquals(
				"[target/makeproject/project1/ergen_canagli_17_,"
				+ " target/makeproject/project1/jneurosci.4415-13.2014,"
				+ " target/makeproject/project1/multiple-1471-2148-11-312,"
				+ " target/makeproject/project1/multiple-1471-2148-11-313,"
				+ " target/makeproject/project1/multiple.312,"
				+ " target/makeproject/project1/pb1,"
				+ " target/makeproject/project1/tree-1471-2148-11-313]",
				  files.toString());

		files = new CMineGlobber("**/fulltext.pdf", targetDir).listFiles();
		Assert.assertEquals(5, files.size());
		files =  new CMineGlobber("**/fulltext.html", targetDir).listFiles();
		Assert.assertEquals(3, files.size());
		files =  new CMineGlobber("**/fulltext.{pdf,html}", targetDir).listFiles();
		Assert.assertEquals(8, files.size());
		files =  new CMineGlobber("**/project1/fulltext.{pdf,html}", targetDir).listFiles();
		// no original files left
		Assert.assertEquals(0, files.size());
		files =  new CMineGlobber("**/project1/*/fulltext.{pdf,html}", targetDir).listFiles();
		Assert.assertEquals(8, files.size());
		files =  new CMineGlobber("**/project1/*.{html,pdf}", targetDir).listFiles();
		Assert.assertEquals(0, files.size());
		files = CMineGlobber.listSortedChildFiles(targetDir, "pdf");
		Assert.assertEquals(0, files.size());
		
		//  check we have created the new log
		files = CMineGlobber.listSortedChildFiles(targetDir);
		Assert.assertEquals(1, files.size());
		files = CMineGlobber.listSortedChildFiles(targetDir, "json");
		Assert.assertEquals(1, files.size());
		Assert.assertEquals("make_project.json", FilenameUtils.getName(files.get(0).toString()));
	}

	/**
	 * PRODUCTION
	 * SHOWCASE
	 * Converts a mixture of html and pdf selectively to just a complete CProject
	 */
	@Test
	public void testMakeProjectPicocliLog() throws IOException {
		File targetDir = new File(NormaFixtures.TARGET_MAKEPROJECT_DIR, PROJECT1);
		CMineTestFixtures.cleanAndCopyDir(
				new File(NormaFixtures.TEST_MAKEPROJECT_DIR, PROJECT1),
				targetDir);
		// as above
				 
		String[] args = {
			"--cproject", targetDir.toString(),
			"--rawfiletypes", "pdf,html",
			"--log4j", "org.contentmine.ami.AMIMakeProject", "INFO",
			"-vv"
			};
		AMIMakeProjectTool amiMakeProject = new AMIMakeProjectTool();
		amiMakeProject.runCommands(args);
		File makeProjectLog = amiMakeProject.getCProject().getMakeProjectLogfile();
		List<String> lines = FileUtils.readLines(makeProjectLog);
		Assert.assertEquals(37, lines.size());
	}


//	public static final File TEST_FORESTOPEN_DIR = new File(NAConstants.TEST_NORMA_DIR, FORESTOPEN);
//	public static final File TARGET_MAKEPROJECT_DIR = new File(TARGET_DIR, MAKEPROJECT);

// ========================================================
	

	/** checks the files output by compression.
	 * 
	 * @param targetDir
	 * @param amiMakeProject
	 */
	private void checkOutputFiles(File targetDir, AbstractAMITool amiMakeProject) {
		List<File> childDirectories = CMineGlobber.listSortedChildDirectories(targetDir);
		Assert.assertEquals(7, childDirectories.size());
		Assert.assertEquals("ergen_canagli_17_", FilenameUtils.getBaseName(childDirectories.get(0).toString()));
		Assert.assertEquals("["
				+ "target/makeproject/project1/ergen_canagli_17_,"
				+ " target/makeproject/project1/jneurosci.4415-13.2014,"
				+ " target/makeproject/project1/multiple-1471-2148-11-312,"
				+ " target/makeproject/project1/multiple-1471-2148-11-313,"
				+ " target/makeproject/project1/multiple.312,"
				+ " target/makeproject/project1/pb1,"
				+ " target/makeproject/project1/tree-1471-2148-11-313]",
				childDirectories.toString());
		CProject cProject = amiMakeProject.getCProject();
		File logFile = cProject.getMakeProjectLogfile();
		Assert.assertNotNull("nust have logFile", logFile);
		
		Assert.assertTrue(logFile+" exists",logFile.exists());
		Assert.assertEquals(7, childDirectories.size());
		Assert.assertEquals(3, CMineGlobber.listSortedDescendantFiles(targetDir, CTree.HTML).size());
		Assert.assertEquals(5, CMineGlobber.listSortedDescendantFiles(targetDir, CTree.PDF).size());
	}
	

}
