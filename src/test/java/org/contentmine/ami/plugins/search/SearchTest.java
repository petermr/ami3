package org.contentmine.ami.plugins.search;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

public class SearchTest {

	private static final Logger LOG = LogManager.getLogger(SearchTest.class);
@Test
	public void testWordSearch() throws IOException {
		File projectDir = new File("target/word/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, projectDir);
		String args = 
				"-q "+projectDir+
				" --sr.search searchwords/adjectives.xml searchwords/prepositions.xml " ;
		AMIArgProcessor argProcessor = new SearchArgProcessor(args);
		LOG.trace("stem "+argProcessor.getStemming());
		argProcessor.runAndOutput();
		File searchDir = new File(projectDir, 
				"http_www.trialsjournal.com_content_16_1_1/results/search");
		File adjectiveFile = new File(searchDir, 
				"adjectives/results.xml");
		String adjectives = XMLUtil.parseQuietlyToDocument(adjectiveFile).toXML();
		Assert.assertEquals("adjectives: ", 
				"<?xml version=\"1.0\"?>\n"
				+ "<results title=\"adjectives\">\n"
				+ " <result pre=\"made a quit attempt in the previous 12 months. Another\" "
				+     "exact=\"significant\" post=\"difference was that those invited through SSS were more\" />\n"
		 		+ " <result pre=\"19818578 Outcome criteria in smoking cessation trials: proposal for a\" "
		 		+     "exact=\"common\" post=\"standard West R Hajek P Stead L Stapleton J\" />\n"
				+ "</results>\n", 
				adjectives);
		File prepositionFile = new File(searchDir, 
				"prepositions/results.xml");
		Element prepositions = XMLUtil.parseQuietlyToDocument(prepositionFile).getRootElement();
		Assert.assertEquals("prepositions", 142,  prepositions.getChildElements().size());

	}

	@Test
	public void testWordSearchStem() throws IOException {
		File projectDir = new File("target/word/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, projectDir);
		String args = 
				"-q "+projectDir+
				" --sr.search searchwords/adjectives.xml searchwords/prepositions.xml --w.stem true " ;
		AMIArgProcessor argProcessor = new SearchArgProcessor(args);
		argProcessor.runAndOutput();
		File searchDir = new File(projectDir, 
				"http_www.trialsjournal.com_content_16_1_1/results/search");
		File adjectiveFile = new File(searchDir, 
				"adjectives/results.xml");
		String adjectives = XMLUtil.parseQuietlyToDocument(adjectiveFile).toXML();
		Assert.assertEquals("adjectives: ", 
				"<?xml version=\"1.0\"?>\n"
				+ "<results title=\"adjectives\">\n"
				+ " <result pre=\"19818578 Outcom criteria in smoke cessat trials: propos for a\" exact=\"common\" post=\"standard West R Hajek P Stead L Stapleton J\" />\n"
				+ "</results>\n", 
				adjectives);
		File prepositionFile = new File(searchDir, 
				"prepositions/results.xml");
		Element prepositions = XMLUtil.parseQuietlyToDocument(prepositionFile).getRootElement();
		Assert.assertEquals("prepositions",
				"<results title=\"prepositions\">\n"
			+ " <result pre=\" 1745-6215-16-1 1745-6215 Methodolog Lesson learn\" exact=\"from\" post=\"recruit socioeconom disadvantag smoker into a pilot random control\" />\n"
            + " <result pre=\"wish to quit. Method Smoker were recruit through mail invit\" exact=\"from\" post=\"three primari care p",
            prepositions.toXML().substring(0, 300));
	}


	@Test
	public void testCompoundWordSearch() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
		String args = 
			"-q "+AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1.toString()+
			" --sr.search searchwords/trials.xml " ;
		AMIArgProcessor argProcessor = new SearchArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
		    "<results title=\"trials\"><result pre=\"from recruiting socioeconomically disadvantaged smokers into a pilot "
		    + "randomized controlled\" exact=\"trial\" post=\"to explore the role of Exercise Assisted Reduction then\" />"
		    + "<result pre=\"recruitment. This was done as part of a pilot two-arm\" exact=\"trial\" post=\"of t");
	
	}

}
