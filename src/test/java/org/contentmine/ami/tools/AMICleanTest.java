package org.contentmine.ami.tools;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMICleanTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DirectoryDeleter;
import org.contentmine.cproject.files.Unzipper;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Test;

import junit.framework.Assert;
import picocli.CommandLine;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.junit.Assert.*;

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
		Unzipper unzipper = new Unzipper();
		Path temp = Files.createTempDirectory("ami-forestplotssmall");
		System.out.println(temp);
		try {
			unzipper.setUnzippedList(new ArrayList<>());
			unzipper.extract(getClass().getResourceAsStream("/uclforest/forestplotssmall.zip"), temp.toFile());

			// TODO count all files that are to be deleted
			System.out.println("BEFORE");
			unzipper.getUnzippedList().forEach(System.out::println);

			String[] args =
					("-p " + temp.toFile().getAbsolutePath()
							+ " -vv clean"
							+ " --dir svg/ pdfimages/ --file scholarly.html").split(" ");
			AMICleanTool amiCleaner = AMI.execute(AMICleanTool.class, args);

			CProject cProject = amiCleaner.getCProject();
			Assert.assertNotNull("CProject not null", cProject);

			// TODO count all remaining files and assert the targers were deleted
			System.out.println("AFTER");
			List<File> after = new ArrayList<>();
			Files.walkFileTree(temp, new SimpleFileVisitor<Path>() {
				// Invoke the pattern matching method on each file.
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					after.add(file.toFile());
					System.out.println(file.toFile());
					return CONTINUE;
				}

				// Invoke the pattern matching method on each directory.
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
					after.add(dir.toFile());
					System.out.println(dir.toFile());
					return CONTINUE;
				}
			});
			unzipper.getUnzippedList().removeAll(after);
			System.out.println("DIFF:");
			unzipper.getUnzippedList().forEach(System.out::println);
			assertFalse(unzipper.getUnzippedList().isEmpty()); // the clean tool should have made some difference
		} finally {
			Files.walkFileTree(temp, new DirectoryDeleter());
		}
	}

	@Test
	/**
	 * tests cleaning directories in a single CTree.
	 */
	public void testCleanSingleTree() {
String cmd = "-t /Users/pm286/workspace/uclforest/dev/higgins clean --dir pdfimages";
AMI.main(cmd.split(" "));
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResults() {
		File targetDir = new File("target/cooccurrence/osanctum200");
		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/osanctum200"), targetDir);

		String cmd = "-p " + targetDir + " --dir results cooccurrence";
		new AMICleanTool().runCommands(cmd);
		// delete children of ctrees
		cmd = "-p " + targetDir + ""
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
		new AMICleanTool().runCommands(cmd);
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResultsGlob() {
		File targetDir = new File("target/cooccurrence/osanctum200");
		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/osanctum200"), targetDir);
		String cmd;
//		String cmd = "-p " + targetDir + " --dir results cooccurrence";
//		new AMICleanTool().runCommands(cmd);
		// delete children of ctrees
		cmd = "-p " + targetDir + ""
			+ " --fileglob "
			+ " gene.**.xml"
		    + " **/species.*"
		    + " search.*"
		    + " xml";
		new AMICleanTool().runCommands(cmd);
	}


}
