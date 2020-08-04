package org.contentmine.ami.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DirectoryDeleter;
import org.contentmine.cproject.files.Unzipper;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.junit.Assert;
import org.junit.Test;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMICleanTest extends AbstractAMITest {
	
	private static final String TARGET_CLEAN = new AMICleanTest().createTargetDirname();

	private String createTargetDirname() {
		return "target/" + createAmiModuleName()+"/";
	}
	private String createAmiModuleName() {
		String shortClassName = this.getClass().getSimpleName();
		String moduleName = shortClassName.replaceAll("(Tool|Test|AMI)", "").toLowerCase();
		return moduleName;
	}
	static final Logger LOG = LogManager.getLogger(AMICleanTest.class);
	
@Test
	public void testHelp() {
		new AMICleanTool().runCommands(new String[]{});
	}

	/**
	 * 
	 */
	@Test
	public void testCleanForestPlotsSmall() throws Exception {
		Path temp = Files.createTempDirectory("ami-forestplotssmall");
		//System.out.println(temp);
		try {
			new Unzipper().extract(getClass().getResourceAsStream("/uclforest/forestplotssmall.zip"), temp.toFile());

			// gather all project files
			List<Path> before = CMFileUtil.listFully(temp);
			//System.out.println("BEFORE");
			//before.forEach(System.out::println);
			long svgCount = before.stream()
					.filter(f -> f.toString().contains(File.separator + "svg" + File.separator))
					.count();
			long pdfimagesCount = before.stream()
					.filter(f -> f.toString().contains(File.separator + "pdfimages" + File.separator))
					.count();
			long scholarlyCount = before.stream()
					.filter(f -> f.toString().contains(File.separator + "scholarly.html"))
					.count();
			Assert.assertNotEquals(0, svgCount);
			Assert.assertNotEquals(0, pdfimagesCount);
			Assert.assertNotEquals(0, scholarlyCount);

			String args =
					("-p " + temp.toFile().getAbsolutePath()
							+ " -vv clean"
							+ " **/svg/* **/pdfimages/*  **/scholarly.html");
			AMICleanTool amiCleaner = AMI.execute(AMICleanTool.class, args);

			CProject cProject = amiCleaner.getCProject();
			Assert.assertNotNull("CProject not null", cProject);

			// count all remaining files and assert the targets were deleted
			System.out.println("AFTER");
			List<Path> after = CMFileUtil.listFully(temp);
			//after.forEach(System.out::println);

			long afterSvgCount = after.stream()
					.filter(f -> f.toString().contains(File.separator + "svg" + File.separator))
					.count();
			long afterPdfimagesCount = after.stream()
					.filter(f -> f.toString().contains(File.separator + "pdfimages" + File.separator))
					.count();
			long afterScholarlyCount = after.stream()
					.filter(f -> f.toString().contains(File.separator + "scholarly.html"))
					.count();
			Assert.assertEquals(0, afterSvgCount);
			Assert.assertEquals(0, afterPdfimagesCount);
			Assert.assertEquals(0, afterScholarlyCount);

			long removedCount = svgCount + pdfimagesCount + scholarlyCount;
			Assert.assertEquals("Nothing else was deleted", after.size(), before.size() - removedCount);

		} finally {
			Files.walkFileTree(temp, new DirectoryDeleter());
		}
	}


	@Test
	/**
	 * tests cleaning XML files in a project
	 */
	public void testCleanXMLWithAncestor() throws IOException {
		File sourceDir = OIL5;
		File targetDir = createTargetDir(sourceDir);
		String glob = "**/sections/**/*.xml";
//		List<File> files = new CMineGlobber().setGlob(glob).setLocation(targetDir).setRecurse(true).listFiles();
		List<File> files = new CMineGlobber().setGlob(glob).setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("section directories", 630, files.size());

		String args = " -vv "
			+ "-p " + targetDir
			+ " clean"
			+ " " + glob
			;
		echoAndExecute("-vv -p target/clean/oil5 clean "+glob, args);
		
		files = new CMineGlobber().setGlob(glob).setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml files", 0, files.size());
	}


	@Test
	/**
	 * tests cleaning XML files in a project
	 */
	public void testCleanDirectory() throws IOException {
		File sourceDir = OIL5;
		File targetDir = createTargetDir(sourceDir);
		String glob = "**/sections/";
		List<File> files = new CMineGlobber().setGlob(glob).setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("section directories", 5, files.size());

		String args = " -vv "
			+ "-p " + targetDir
			+ " clean"
			+ " " + glob
			;
		echoAndExecute("-vv -p target/clean/oil5 clean "+glob, args);
		
		files = new CMineGlobber().setGlob(glob).setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml files", 0, files.size());
	}

	private File createTargetDir(File sourceDir) {
		File targetDir = new File(createTargetDirname(), sourceDir.getName()+"/");
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		return targetDir;
	}

	@Test
	/**
	 * tests cleaning XML files in a project
	 */
	public void testCleanXML() throws IOException {
		File targetDir = new File(TARGET_CLEAN, "oil5/");
		CMineTestFixtures.cleanAndCopyDir(OIL5, targetDir);
		
		List<File> files = new CMineGlobber().setGlob("**/*.xml").setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml files", 792, files.size());

		String args = " -vv "
			+ "-p " + targetDir
			+ " clean"
			+ " **/*.xml"
			;
		echoAndExecute("-vv -p target/clean/oil5 clean **/*.xml", args);
		
		files = new CMineGlobber().setGlob("**/*.xml").setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml files", 0, files.size());
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResultsGlob() throws IOException {
//		AMI
		File sourceDir = new File("src/test/resources/org/contentmine/ami/oil5");
		CMFileUtil.assertExistingDirectory(sourceDir);
		File targetDir = new File("target/oil5");
		CMFileUtil.forceDelete(targetDir);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CMFileUtil.assertExistingDirectory(targetDir);
		// old style. we'll replace
		List<File> files = new CMineGlobber().setGlob("**/*.xml").setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml "+files.size(), 792, files.size());

		String args;
		// delete children of ctrees
		args = ""
			+ "-p " + targetDir
			+ " clean"
			//+ " --fileglob "
			+ " **/*.xml"
			+ " **/results"
 			+ " gene.**.xml"
		    + " **/species.*"
		    + " search.*"
		    + " xml";
		AMI.execute(args);
		
		files = new CMineGlobber().setGlob("**/*.xml").setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml "+files.size(), 0, files.size());
	}
}
