package org.contentmine.ami;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIProcessor;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

/** tests AMIArgProcessor and AMIProcessor (the main enrty point)
 * 
 * @author pm286
 *
 */
public class AMIArgProcessorTest {

	private static final Logger LOG = LogManager.getLogger(AMIArgProcessorTest.class);
@Test
	public void testVersion() {
		AMIArgProcessor argProcessor = new AMIArgProcessor();
		argProcessor.parseArgs("--version");
	}
	
	// utility method to check first part of resultsElementList


	@Test
	@Ignore // fails command
	public void testAMIProcessor() {
		File indir = AMIFixtures.TEST_PLOSONE_DIR;
		String cmd = " "+indir.getName();
		cmd += " species";
		LOG.debug(cmd);
		
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(indir.getPath());
		amiProcessor.setDebugLevel(Level.DEBUG);
		amiProcessor.run(cmd);
	}

}
