package org.contentmine.norma.pubstyle.rs;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.NormaFixtureRunner;
import org.contentmine.norma.NormaFixtures;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.saxon.TransformerFactoryImpl;

//@Ignore // no open -access papers
public class RSTest {
	private static final Logger LOG = LogManager.getLogger(RSTest.class);
static String PUB0 = "rs";
	static String PUB = "rs";
	static String PUB1 = PUB+"/clean";
	static File TARGET = new File(NormaFixtures.TARGET_PUBSTYLE_DIR, PUB);
	static File TARGET1 = new File(NormaFixtures.TARGET_PUBSTYLE_DIR, PUB1);
	// static File TEST = new File(NormaFixtures.TEST_PUBSTYLE_DIR, PUB); // change back to this when open
	static File TEST = new File(NormaFixtures.EXAMPLES_DIR, PUB); // change to 
	//static File TEST1 = new File(TEST, "ccby"); // change back to this when open
	static File TEST1 = new File(TEST, "closed");

	@Test
	@Ignore // closed access papers
	public void testHtml2Scholarly() {
		NormaFixtures.copyToTargetRunHtmlTidy(TEST1, TARGET); 
	}

	@Test
	@Ignore // closed access papers
	public void testHtml2Scholarly2StepConversion() {
		new NormaFixtureRunner().copyToTargetRunTidyTransformWithStylesheetSymbolRoot(TEST1, TARGET, PUB0);
	}
	
	@Test
	@Ignore // closed access papers

	public void testHtml2Scholarly2StepConversionClean() throws IOException {
		new NormaFixtureRunner().tidyTransformAndClean(TEST1, TARGET1, PUB);
	}
	
	
	@Test
	@Ignore
	public void testBadStylesheet() throws Exception {
		File CTREE_DIR = new File("target/pubstyle/rs/277_1686_1309");
		File infile = new File(CTREE_DIR, "fulltext.xhtml");
		Source source = new StreamSource(infile);
		File outfile = new File(CTREE_DIR, "schol1.html");
		Result result = new StreamResult(outfile);
		Transformer transformer = new TransformerFactoryImpl().newTransformer(
				new StreamSource("src/main/resources/org/contentmine/norma/pubstyle/rs/toHtml.xsl"));
		transformer.transform(source, result);
	}
	
}
