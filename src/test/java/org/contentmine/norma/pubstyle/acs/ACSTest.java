package org.contentmine.norma.pubstyle.acs;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.NormaFixtureRunner;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

public class ACSTest {
	
	private static final Logger LOG = LogManager.getLogger(ACSTest.class);
static String PUB0 = "acs";
	static String PUB = "acs";
	static String PUB1 = "acs/clean";
	static File TARGET = new File(NormaFixtures.TARGET_PUBSTYLE_DIR, PUB);
	static File TARGET1 = new File(NormaFixtures.TARGET_PUBSTYLE_DIR, PUB1);
	static File TEST = new File(NormaFixtures.TEST_PUBSTYLE_DIR, PUB); // change back to this when open
	static File TEST1 = new File(TEST, "acsopen"); // change back to this when open

	@Test
	public void testHtml2Scholarly() {
		NormaFixtures.copyToTargetRunHtmlTidy(TEST1, TARGET); 
	}

	@Test
	public void testHtml2Scholarly2StepConversion() {
		new NormaFixtureRunner().copyToTargetRunTidyTransformWithStylesheetSymbolRoot(TEST1, TARGET, PUB0);
	}
	@Test
	public void testHtml2Scholarly2StepConversionClean() throws IOException {
		new NormaFixtureRunner().tidyTransformAndClean(TEST1, TARGET1, PUB);
	}

}
