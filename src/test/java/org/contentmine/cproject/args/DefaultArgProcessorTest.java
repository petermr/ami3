package org.contentmine.cproject.args;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.cproject.CProjectArgProcessor;
import org.contentmine.cproject.files.RegexPathFilter;
import org.contentmine.eucl.pom.PomList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DefaultArgProcessorTest {
	
	private static final Logger LOG = Logger.getLogger(DefaultArgProcessorTest.class);
	static {
		LOG.setLevel(org.apache.log4j.Level.DEBUG);
	}

	@Test
	@Ignore // side-effects creates files
	public void testArgs() {
		String[] args = {
			"-i", "foo", "bar",
			"-o", "plugh",
		};
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertEquals("input", 2, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo", argProcessor.getInputList().get(0));
		Assert.assertEquals("input", "bar", argProcessor.getInputList().get(1));
		Assert.assertEquals("output", "plugh", argProcessor.getOutput());
	}

	@Test
	public void testSingleWildcards() {
		String[] args = {
			"-i", "foo{1:3}bof", "bar{a|b|zzz}plugh", 
		};
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		// correct
		Assert.assertEquals("input", 2, argProcessor.getInputList().size());
		// deliberate mistake
//		Assert.assertEquals("input", 22, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo{1:3}bof", argProcessor.getInputList().get(1));
		Assert.assertEquals("input", "bar{a|b|zzz}plugh", argProcessor.getInputList().get(0));
		argProcessor.expandWildcardsExhaustively();
		Assert.assertEquals("input", 6, argProcessor.getInputList().size());
		Assert.assertEquals("input", "baraplugh", argProcessor.getInputList().get(0));
		Assert.assertEquals("input", "barbplugh", argProcessor.getInputList().get(1));
		Assert.assertEquals("input", "barzzzplugh", argProcessor.getInputList().get(2));
		Assert.assertEquals("input", "foo1bof", argProcessor.getInputList().get(3));
		Assert.assertEquals("input", "foo2bof", argProcessor.getInputList().get(4));
		Assert.assertEquals("input", "foo3bof", argProcessor.getInputList().get(5));
	}
	
	
	@Test
	public void testMultipleWildcards() {
		String[] args = {
			"-i", "foo{1:3}bof{3:6}plugh",
		};
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertEquals("input", 1, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo{1:3}bof{3:6}plugh", argProcessor.getInputList().get(0));
		argProcessor.expandWildcardsExhaustively();
		Assert.assertEquals("input", 12, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo1bof3plugh", argProcessor.getInputList().get(0));
	}
	
	@Test
	public void testArgCounts() {
		String[] args = {"-o", "foo"};
		new CProjectArgProcessor().parseArgs(args);
		try {
			args = new String[]{"-o", "foo", "bar"};
			new CProjectArgProcessor().parseArgs(args);
		} catch (Exception e) {
			Assert.assertEquals("too many arguments", 
					"cannot process argument: -o (IllegalArgumentException: --output; argument count (2) is not compatible with {1,1})",
					e.getMessage());
		}
	}
	
	@Test
	@Ignore // too much debug output
	public void testMakeDocs() {
		String args = "--makedocs";
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testVersion() {
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs("--version");
	}
	
	@Test
	public void testProject() {
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		try {
			argProcessor.parseArgs("--project");
			Assert.fail("should trap zero arguments");
		} catch (Exception e) {
			// OK
		}
		
		argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs("--project foo");
	}
	
	
	@Test
	public void testLog() throws IOException {
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		File targetFile = new File("target/test/log/");
		targetFile.mkdirs();
		// dummy file
		FileUtils.write(new File(targetFile, "fulltext.txt"), "fulltext");
		argProcessor.parseArgs("-q "+targetFile+" -i fulltext.txt  --c.test --log");
		argProcessor.runAndOutput();
	}
	
	@Test
	@Ignore
	public void testGetVersionNumber() {
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		VersionManager versionManager = argProcessor.getVersionManager();
		Assert.assertEquals("version",  "xx", versionManager.getVersionNumber());
		
		
	}
	
	@Test
	public void testDependency() throws IOException {
		File topDir = new File("../euclid/src/test/resources/" + CHESConstants.ORG_CM + "/pom") ;
		if (!topDir.exists()) {
			LOG.error("no file: " + topDir);
			return;
		}
		LOG.debug(topDir.getCanonicalFile());
		List<File> pomFiles = new RegexPathFilter(".*/pom\\.xml").listNonDirectoriesRecursively(topDir);
		PomList pomList = new PomList(pomFiles);
		LOG.debug(pomList.size());
		String dottyString = pomList.createDottyString("cmpom");
		FileUtils.write(new File("target/dotty/poms1.dot"), dottyString);
	}
}
