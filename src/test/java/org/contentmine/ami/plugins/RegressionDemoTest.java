package org.contentmine.ami.plugins;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.regex.RegexArgProcessor;
import org.contentmine.ami.plugins.regex.RegexPlugin;
import org.contentmine.ami.plugins.species.SpeciesPlugin;
import org.contentmine.ami.plugins.word.WordPlugin;
import org.contentmine.ami.wordutil.WordSetWrapper;
import org.contentmine.cproject.files.CTree;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/** collection of archetypal tests from each plugin.
 * 
 * These should always run and will use communal resources
 * 
 * @author pm286
 *
 */
public class RegressionDemoTest {

	
	private static final Logger LOG = LogManager.getLogger(RegressionDemoTest.class);
//	@Test
//	public void testIdentifiersArgProcessor() throws Exception {
//		// SHOWCASE
//		String cmd = "-q target/examples_16_1_1/ -i scholarly.html --context 25 40 "
//				+ "--id.identifier --id.regex regex/identifiers.xml --id.type clin.nct clin.isrctn";
//		AMIFixtures.runStandardTestHarness(
//				new File(NAConstants.TEST_AMI_DIR+"/regressiondemos/http_www.trialsjournal.com_content_16_1_1/"),
//				new File("target/examples_16_1_1/"), 
//				new IdentifierPlugin(),
//				cmd,
//				"identifier/clin.nct/", "identifier/clin.isrctn/");
//	}
//
	/**
	cp -R NAConstants.TEST_AMI_DIR+"/regressiondemos/http_www.trialsjournal.com_content_16_1_1/ temp
	ami-identifier -q target/examples_16_1_1/ -i scholarly.html --context 25 40 --id.identifier --id.type clin.nct clin.isrctn"
	
	cp -R NAConstants.TEST_AMI_DIR+"/regressiondemos/bmc_trials_15_1_511/ temp
	ami-regex -q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 --r.regex regex/consort0.xml
	
	cp -R NAConstants.TEST_AMI_DIR+"/regressiondemos/journal.pone.0121780/ temp
	ami-sequence --sq.sequence --context 35 50 --sq.type dna prot -q target/plosone/sequences/ -i scholarly.html
	
	cp -R NAConstants.TEST_AMI_DIR+"/regressiondemos/journal.pone.0119475/ temp
	ami-species --sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria -i scholarly.html
	
	cp -R NAConstants.TEST_AMI_DIR+"/regressiondemos/http_www.trialsjournal.com_content_16_1_1/ temp
	ami-word -q temp -i scholarly.html --context 25 40 --w.words wordLengths wordFrequencies --w.stopwords "+NAConstants.PLUGINS_WORD+"/stopwords.txt
*/

	@Test
	public void testRegex00() throws IOException {
		FileUtils.copyDirectory(new File(NAConstants.TEST_AMI_DIR+"/regex/"), new File("target/regex00/"));
		String consort00 = NAConstants.TEST_AMI_DIR+"/regex/consort00.xml";
		Assert.assertTrue(new File(consort00).exists());
		String cmd = "-q target/regex00/ -i scholarly.html --context 25 40 --r.regex "+
		    consort00;
		RegexArgProcessor regexArgProcessor = new RegexArgProcessor(cmd);
		regexArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(regexArgProcessor, 1, 0, 
				"<results title=\"consort00\"><result pre=\"-specific LBP (NSLBP), a \" name0=\"diagnose\" value0=\"diagnosis\" post=\"based on exclusion of a specific cause o\" /></results>"
				);

	}
	

	@Test
	public void testRegex0() throws IOException {
		FileUtils.copyDirectory(new File(NAConstants.TEST_AMI_DIR+"/regressiondemos/bmc_trials_15_1_511/"), 
				new File("target/consort0/15_1_511_test/"));
		String consort0 = "src/main/resources/org/contentmine/ami/regex/consort0.xml";
		Assert.assertTrue("file exists "+consort0, new File(consort0).exists());
		String cmd = "-q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 "
				+ " --r.regex "+consort0;
		RegexArgProcessor regexArgProcessor = new RegexArgProcessor(cmd);
		regexArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(regexArgProcessor, 1, 0, 
				"<results title=\"consort0\"><result pre=\"-specific LBP (NSLBP), a \" name0=\"diagnose\" value0=\"diagnosis\" post=\"based on exclusion of a specific cause o\" xpath=\"/html[1]/body[1]/div[16]/div[2]/div[9]/"
				);
	}
	
	@Test
	@Ignore("fails probaby can't find file")
	public void testRegexHarness() throws IOException {
		// SHOWCASE
		String cmd = "-q target/consort0/15_1_511_test/ -i scholarly.html --context 25 40 --r.regex "+
		    NAConstants.MAIN_AMI_DIR+"/regex/consort0.xml";
		
		AMIFixtures.runStandardTestHarness(
				new File(NAConstants.TEST_AMI_DIR+"/regressiondemos/bmc_trials_15_1_511/"), 
				new File("target/consort0/15_1_511_test/"), 
				new RegexPlugin(),
				cmd,
				NAConstants.MAIN_AMI_DIR+"/regex/consort0.xml/");
	}
	
//	@Test
//	public void testSequenceHarness() throws Exception {
//		// SHOWCASE
//		String cmd = "--sq.sequence --context 35 50 --sq.type dnaprimer prot1 -q target/plosone/sequences/ -i scholarly.html"; 
//		AMIFixtures.runStandardTestHarness(
//				new File(NAConstants.TEST_AMI_DIR+"/regressiondemos/journal.pone.0121780/"), 
//				new File("target/plosone/sequences/"), 
//				new SequencePlugin(),
//				cmd,
//				"sequence/dnaprimer/", "sequence/prot1/");
//	}

	

//	@Test
//	public void testSpeciesHarness() throws Exception {
//		// SHOWCASE
//		String cmd = "--sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria -i scholarly.html"; 
// 
//		AMIFixtures.runStandardTestHarness(
//				new File(NAConstants.TEST_AMI_DIR+"/regressiondemos/journal.pone.0119475/"),
//				new File("target/plosone/species/malaria"), 
//				new SpeciesPlugin(),
//				cmd,
//				"species/binomial/", "species/genus/", "species/genussp/");
//	}

	@Test
	public void testWordHarness() throws IOException {
		// SHOWCASE
		String cmd = "-q target/word/16_1_1_test/ -i scholarly.html --context 25 40 --w.words wordLengths wordFrequencies"
				+ " --w.stopwords "+WordSetWrapper.COMMON_ENGLISH_STOPWORDS_TXT;
		AMIFixtures.runStandardTestHarness(
				new File(NAConstants.TEST_AMI_DIR+"/regressiondemos/http_www.trialsjournal.com_content_16_1_1"), 
				new File("target/word/16_1_1_test/"), 
				new WordPlugin(),
				cmd,
				"word/lengths/", "word/frequencies/");
		

	}

	@Test
	public void testMultipleInput() throws Exception {
		// SHOWCASE
		String cmd0 = "--sp.species --context 35 50 --sp.type binomial genus genussp ";
		String cmd1 = " -i scholarly.html"; 

		String[] testFilenames = {
				NAConstants.TEST_AMI_DIR+"/regressiondemos/bmc_trials_15_1_511/",
				NAConstants.TEST_AMI_DIR+"/regressiondemos/http_www.trialsjournal.com_content_16_1_1/",
		};
		String[] copyFilenames = {
				"target/multiple/bmc_trials_15_1_511/",
				"target/multiple/http_www.trialsjournal.com_content_16_1_1/",
//				"target/plosone/species/malaria",
//				"target/plosone/species/malaria",
		};
		if (testFilenames.length != copyFilenames.length) {
			throw new RuntimeException("copy length ("+copyFilenames.length+") != test length ("+testFilenames.length+")");
		}
		int i = 0;
		File[] copyFiles = new File[copyFilenames.length];
		for (String testFilename : testFilenames) {
			File testFile = new File(testFilename);
			if (!testFile.exists()) {
				throw new RuntimeException("testFile does not exist: "+testFile);
			}
			if (!testFile.isDirectory()) {
				throw new RuntimeException("testFile is not a directory: "+testFile);
			}
			try {
				new CTree(testFile);
			} catch (Exception e) {
				throw new RuntimeException(testFile + " is not a ContentMine directory");
			}
			copyFiles[i] = new File(copyFilenames[i]);
//			FileUtils.copyDirectory(testFile, copyFiles[i]);
			i++;
		}
		String cmd = "";
		cmd += cmd0;
		cmd += " -q ";
		for (File copyFile : copyFiles) {
			cmd += " "+copyFile+" ";
		}
		cmd += cmd1;
//		LOG.debug(cmd);
//		cmd = "--sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria target/plosone/species/malaria -i scholarly.html"; 
//		cmd = "--sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria -i scholarly.html";
		String[] args = cmd.split("\\s+");
//		LOG.debug(cmd);
		
		SpeciesPlugin plugin = new SpeciesPlugin(args);
		plugin.runAndOutput();
		
		
	}


}
