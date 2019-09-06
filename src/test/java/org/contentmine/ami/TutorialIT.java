package org.contentmine.ami;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.species.SpeciesArgProcessor;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.Norma;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("FIXME")
public class TutorialIT {

	;
	private static final Logger LOG = Logger.getLogger(TutorialIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** for cleaning XSLT
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCheckNorma() throws IOException {
		String project = "zika10";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File(NAConstants.TEST_AMI_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project "+projectDir;
		new Norma().run(args);
	
	}

	@Test
	public void testHindawiSample() throws IOException {
		File rawDir = new File("../../hindawi/sample");
		File projectDir = new File("target/tutorial/hindawi/sample");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
				+ "species(binomial,genus) "
				+ " gene(human)"
				+ " word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
				+ " word(search)w.search:"+NAConstants.PLUGINS_DICTIONARY_DIR+"/tropicalVirus.xml"
				+ " word(search)w.search:"+NAConstants.PLUGINS_PLACES+"/wikiplaces.xml"
				+ " sequence(dnaprimer) ");
	}

	@Test
		// TESTED 2016-01-12
	//	@Ignore // tests broken (?overwrite)
		public void testSpecies() throws Exception {
			CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR, "tutorial/plos10"), new File("target/species10"));
			String args = "-q target/species10 -i scholarly.html --sp.species --context 35 50 --sp.type binomial genus genussp";
			AMIArgProcessor speciesArgProcessor = new SpeciesArgProcessor(args);
			speciesArgProcessor.runAndOutput();
			// fails - check me
	//		AMIFixtures.checkResultsElementList(speciesArgProcessor, 3, 0, 
	//				"<results title=\"binomial\"><result pre=\" \" "
	//				+ "exact=\"Cryptococcus neoformans\" "
	//				+ "xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][7]/*[local-name()='p'][10]\" "
	//				+ "match=\"Cryptococcus neoformans\" "
	//				+ "post=\" is a ubiquitous environmental fungus that can cau\" n"
	//				);
	//		AMIFixtures.compareExpectedAndResults(new File(NAConstants.TEST_AMI_DIR, "tutorial/plos10/e0115544"), 
	//				new File("target/species10/e0115544"), "species/binomial", AMIFixtures.RESULTS_XML);
		}

	@Test
	public void testCommandProcessor() throws IOException {
		String project = "zika10";
		File rawDir = new File(NAConstants.TEST_AMI_DIR, project);
		File projectDir = new File("target/tutorial/zika");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
				+ "species(binomial,genus) "
				+ " gene(human)"
				+ " word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
				+ " word(search)w.search:"+NAConstants.PLUGINS_DICTIONARY_DIR+"/tropicalVirus.xml"
				+ " word(search)w.search:"+NAConstants.PLUGINS_PLACES+"/wikiplaces.xml"
				+ " sequence(dnaprimer) ");
	}

	@Test
		public void testNewCommands() throws IOException {
			String project = "zika10";
			File projectDir = new File("target/tutorial/"+project);
			File rawDir = new File(NAConstants.TEST_AMI_DIR, project);
			CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
			
			
			
	//		String cmd = "species(binomial,genus)";
	//		String cmd = "gene(human)";
			
			String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"; 
	//		String cmd = "word(search)w.search:"+NAConstants.DICTIONARY_DIR+"/tropicalVirus.xml"; //
	//		String cmd = "word(search)w.search:"+NAConstants.PLUGINS_PLACES+"/wikiplaces.xml"; //
	//		String cmd = "sequence(dnaprimer) ";
	//				+ "word(search)w.search:"+NAConstants.DICTIONARY_DIR+"/tropicalVirus.xml";
					
					
	//		String cmd = "species(binomial,genus) gene(human) sequence(dnaprimer) word(search)w.search:"+NAConstants.DICTIONARY_DIR+"/tropicalVirus.xml word(frequencies)xpath:@count>20~stopwords:pmcstop.txt_stopwords.txt"; 
			CommandProcessor commandProcessor = new CommandProcessor(projectDir);
			commandProcessor.processCommands(cmd);
	
		}



}
