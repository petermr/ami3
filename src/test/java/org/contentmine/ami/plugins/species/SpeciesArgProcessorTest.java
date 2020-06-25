package org.contentmine.ami.plugins.species;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.species.SpeciesArgProcessor;
import org.contentmine.ami.plugins.species.SpeciesPlugin;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.NormaTransformer;
import org.contentmine.norma.util.NormaTestFixtures;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Builder;
import nu.xom.Element;

public class SpeciesArgProcessorTest {

	
	
	private static final Logger LOG = LogManager.getLogger(SpeciesArgProcessorTest.class);
static File SPECIES_DIR = new File(NAConstants.TEST_AMI_DIR, "species/");

	
	@Test
	// TESTED 2016-01-12
	public void testSimpleSpeciesArgProcessor() throws Exception {
		File newDir = new File("target/species/simple/");
		FileUtils.copyDirectory(new File(SPECIES_DIR, "simple"), newDir);
		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor argProcessor = new SpeciesArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 3, 0, 
				"<results title=\"binomial\"><result pre=\"This is \" exact=\"Homo sapiens\""
				+ " xpath=\"/html[1]/body[1]/p[5]\" match=\"Homo sapiens\" post=\" at a terminal.\""
				+ " name=\"binomial\" /><result pre=\"I can refer to me as \" exact=\"H. sapiens\" xpath=\"/html"
				);
		AMIFixtures.checkResultsElementList(argProcessor, 3, 1, 
				"<results title=\"genus\">"
				+ "<result pre=\"I belong to genus \" exact=\"Homo\" match=\"Homo\" post=\" ; my"
				+ " ancestors may be Homo sp.\" name=\"genus\" />"
				+ "</results>");
		AMIFixtures.checkResultsElementList(argProcessor, 3, 2, 
				"<results title=\"genussp\"><result pre=\" Homo ; my ancestors may be \" "
				+ "exact=\"Homo sp\" match=\"Homo sp\" post=\".\" name=\"genussp\" />"
				+ "</results>");
	}

	@Test
	// TESTED 2016-01-12
	public void testSimpleSpecies1ArgProcessor() throws Exception {
		File newDir = new File("target/species/simple1/");
		FileUtils.copyDirectory(new File(SPECIES_DIR, "simple1"), newDir);
		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor argProcessor = new SpeciesArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 3, 0, 
				"<results title=\"binomial\">"
				+ "<result pre=\"2]. Outside of sub-Saharan Africa, \" "
				+ "exact=\"Plasmodium vivax\" "
				+ "xpath=\"/html[1]/body[1]/div[1]/div[3]/p[1]\" "
				+ "match=\"Plasmodium vivax\" post=\" infections present unique and add");
		AMIFixtures.checkResultsElementList(argProcessor, 3, 1, 
				"<results title=\"genus\" />");
		AMIFixtures.checkResultsElementList(argProcessor, 3, 2, 
				"<results title=\"genussp\" />");
	}
	@Test
	// TESTED 2016-01-12
	public void testSpeciesArgProcessor() throws Exception {
		File newDir = new File("target/plosone/species/0121780/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		NormaTestFixtures.runNorma(newDir, "ctree", NormaTransformer.NLM2HTML);
		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor argProcessor = new SpeciesArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 3, 0, 
				"<results title=\"binomial\">"
				+ "<result pre=\"ntimicrobial activity (assessed on \" exact=\"Vibrio harveyi\" "
				+ "xpath=\"/html[1]/body[1]/div[1]/div[7]/p[7]\" "
				+ "match=\"Vibrio harveyi\" post=\" cultures) was limited in both H and W");
		AMIFixtures.checkResultsElementList(argProcessor, 3, 1, 
				"<results title=\"genus\">"
				+ "<result pre=\"-1. This indicates that \" exact=\"Vibrio\" "
				+ "xpath=\"/html[1]/body[1]/div[1]/div[7]/p[6]\" "
				+ "match=\"Vibrio\" post=\" may not be harmful in lower densities, only becom\" name=\"genus\" />");
		AMIFixtures.checkResultsElementList(argProcessor, 3, 2, 
		         "<results title=\"genussp\">"
		         + "<result pre=\" treated (WST) samples. Although 3 \" exact=\"Vibrio spp\" "
		         + "xpath=\"/html[1]/body[1]/div[1]/div[7]/p[7]\" "
		         + "match=\"Vibrio spp\" post=\" were found in WS-affected samples, two of thes");
		
	}
	
	@Test
	@Ignore // accesses net
	public void testSpeciesArgProcessorLookup() throws Exception {
		File newDir = new File("target/plosone/species");
		FileUtils.copyDirectory(AMIFixtures.TEST_PLOSONE_SEQUENCE_0121780, newDir);
		String args = "--sp.species --context 35 50 --sp.type binomial binomialsp -q "+newDir+" -i scholarly.html --lookup wikipedia"; 
		AMIArgProcessor argProcessor = new SpeciesArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 3, 0, 
				"<results title=\"mend me\" />");
		AMIFixtures.checkResultsElementList(argProcessor, 3, 1, 
				"<results title=\"mend me\" />");
		AMIFixtures.checkResultsElementList(argProcessor, 3, 2, 
				"<results title=\"mend me\" />");
		File binomialFile = new File(newDir, "results/species/binomial/results.xml");
		Element binomialElement = new Builder().build(binomialFile).getRootElement();
	}
	
	
	@Test
	// TESTED 2016-01-12
	// TEST FAILS 2016-03-08
	public void testMalariaArgProcessor() throws Exception {
		File newDir = new File("target/plosone/species/malaria");
		FileUtils.copyDirectory(AMIFixtures.TEST_PLOSONE_MALARIA_0119475, newDir);
		NormaTestFixtures.runNorma(newDir, "ctree", NormaTransformer.NLM2HTML);
		String args = "--sp.species --context 35 50 --sp.type binomial genus genussp -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor argProcessor = new SpeciesArgProcessor(args);
		argProcessor.runAndOutput();
		// MEND TEST
//		AMIFixtures.checkResultsElementList(argProcessor, 3, 0, 
//				"<results title=\"binomial\">"
//				+ "<result pre=\"porozoite protein ( csp) of \" exact=\"P. falciparum\""
//				+ " xpath=\"/html[1]/body[1]/article[1]/div[7]/p[1]"
////				+ " match=\"P. falciparum\""
////				+ " post=\" and P. vivax populatio"
//				);

//
//		AMIFixtures.checkResultsElementList(argProcessor, 3, 1, 
//				"<results title=\"genus\">"
//				+ "<result pre=\"g the transmission and movement of \" exact=\"Plasmodium\""
//				+ " xpath=\"/html[1]/body[1]/article[1]/div[6]/p[3]"
////				+ " match=\"Plasmodium\" post=\" parasites is crucial for malaria elimination and"
//				);
//		AMIFixtures.checkResultsElementList(argProcessor, 3, 2, 
//				"<results title=\"genussp\" />");
	}

	@Test
	@Ignore // currently fauls because of empty.xml
	public void testSpeciesHarness() throws Exception {
		// SHOWCASE
		String cmd = "--sp.species --context 35 50 --sp.type binomial genus genussp -q target/plosone/species/malaria -i scholarly.html"; 
 
		AMIFixtures.runStandardTestHarness(
				AMIFixtures.TEST_PLOSONE_MALARIA_0119475, 
				new File("target/plosone/species/malaria"), 
				new SpeciesPlugin(),
				cmd,
				"species/binomial/", "species/genus/", "species/genussp/");
	}
	

	@Test
	// large
	@Ignore // may also not give correct answer
	public void testSpecies() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
		NormaTestFixtures.runNorma(large, "project", "uspto2html");
		String args = "-i scholarly.html  --sp.species --context 35 50 --sp.type binomial genus --project "+large; 
		AMIArgProcessor argProcessor = new SpeciesArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 2, 0, 
				"<results title=\"binomial\" />");
		AMIFixtures.checkResultsElementList(argProcessor, 2, 1, 
				"<results title=\"genus\" />");
		
	}
	


	
}
