package org.contentmine.ami.tools;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMI.ShortErrorMessageHandler;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DirectoryDeleter;
import org.contentmine.cproject.files.Unzipper;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.junit.Assert;
import org.junit.Test;

import picocli.CommandLine;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMICleanTest {
	private static final Logger LOG = Logger.getLogger(AMICleanTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
		System.out.println(temp);
		try {
			new Unzipper().extract(getClass().getResourceAsStream("/uclforest/forestplotssmall.zip"), temp.toFile());

			// gather all project files
			System.out.println("BEFORE");
			List<File> before = listFully(temp);
			before.forEach(System.out::println);

			String args =
					("-p " + temp.toFile().getAbsolutePath()
							+ " -vv clean"
							+ " --dir svg/ pdfimages/ --file scholarly.html");
			AMICleanTool amiCleaner = AMI.execute(AMICleanTool.class, args);

			CProject cProject = amiCleaner.getCProject();
			Assert.assertNotNull("CProject not null", cProject);

			// count all remaining files and assert the targers were deleted
			System.out.println("AFTER");
			List<File> after = listFully(temp);
			after.forEach(System.out::println);

			before.removeAll(after);

			System.out.println("DIFF:");
			before.forEach(System.out::println);
			assertFalse(before.isEmpty()); // the clean tool should have made some difference
		} finally {
			Files.walkFileTree(temp, new DirectoryDeleter());
		}
	}

	private List<File> listFully(Path temp) throws IOException {
		List<File> after = new ArrayList<>();
		Files.walkFileTree(temp, new SimpleFileVisitor<Path>() {
			// Invoke the pattern matching method on each file.
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				after.add(file.toFile());
				return CONTINUE;
			}

			// Invoke the pattern matching method on each directory.
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				after.add(dir.toFile());
				return CONTINUE;
			}
		});
		return after;
	}

	@Test
	/**
	 * tests cleaning directories in a single CTree.
	 */
	public void testCleanSingleTree() {
		String cmd = "-t /Users/pm286/workspace/uclforest/dev/higgins clean --dir pdfimages";
		AMI.execute(cmd);
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResults() {
		File targetDir = new File("target/cooccurrence/osanctum200");
		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/osanctum200"), targetDir);

		String cmd = "-p " + targetDir + " clean --dir results cooccurrence";
		AMI.execute(cmd);

		// delete children of ctrees
		cmd = "-p " + targetDir + ""
			+ " clean"
			+ " --file "
			+ " gene.human.count.xml"
		    + " gene.human.snippets.xml"
		    + " scholarly.html"
//		    + " search.country.count.xml"
//		    + " search.country.snippets.xml"
//		    + " search.disease.count.xml"
//		    + " search.disease.snippets.xml"
//		    + " search.diterpene.count.xml"
//		    + " search.diterpene.snippets.xml"
//		    + " search.drugs.count.xml"
//		    + " search.drugs.snippets.xml"
//		    + " search.monoterpene.count.xml"
//		    + " search.monoterpene.snippets.xml"
//		    + " search.monoterpenes.count.xml"
//		    + " search.monoterpenes.snippets.xml"
//		    + " search.plantparts.count.xml"
//		    + " search.plantparts.snippets.xml"
//		    + " search.spices.count.xml"
//		    + " search.spices.snippets.xml"
		    + " species.binomial.count.xml"
		    + " species.binomial.snippets.xml"
		    + " word.frequencies.count.xml"
		    + " word.frequencies.snippets.xml";
		AMI.execute(cmd);
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResultsGlob() throws IOException {
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
			+ "-p " + targetDir + " clean"
			+ " --fileglob "
			+ " **/*.xml"
 			+ " gene.**.xml"
		    + " **/species.*"
		    + " search.*"
		    + " xml";
		AMI.execute(args);
		
		files = new CMineGlobber().setGlob("**/*.xml").setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml "+files.size(), 0, files.size());
		System.out.println("files: "+files.size());
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResultsRegex() throws IOException {
		File sourceDir = new File("src/test/resources/org/contentmine/ami/oil5");
		CMFileUtil.assertExistingDirectory(sourceDir);
		File targetDir = new File("target/oil5");
		CMFileUtil.forceDelete(targetDir);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CMFileUtil.assertExistingDirectory(targetDir);
		// old style. we'll replace
		List<File> files = new CMineGlobber().setRegex(".*\\.xml").setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml "+files.size(), 792, files.size());

		String args;
		// delete children of ctrees
		args = ""
			+ "-p " + targetDir + " clean"
			+ " --fileregex "
			+ " .*\\.xml"
		    + "";
		AMI.execute(args);
		
		files = new CMineGlobber().setRegex(".*\\.xml").setLocation(targetDir).setRecurse(true).listFiles();
		Assert.assertEquals("xml "+files.size(), 0, files.size());
		System.out.println("files: "+files.size());
	}

	@Test
	public void testCommandsNew() {
//		AbstractAMITool.runCommandsNew(args);
		
	}


}
