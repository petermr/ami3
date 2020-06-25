package org.contentmine.ami;

import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIProcessor;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Test;

/** tests ami commandline
 * 
 * @author pm286
 *
 */

public class AMIProcessorTest {
	private static final Logger LOG = LogManager.getLogger(AMIProcessorTest.class);
@Test
	public void testReadPOM() {
		Assert.assertTrue("pom " + NAConstants.NORMAMI_DIR, NAConstants.NORMAMI_DIR.exists());
		AMIProcessor.updatePOMinMainResources();
		Assert.assertTrue("pom exists " + NAConstants.SRC_MAIN_RESOURCES_POM_XML, NAConstants.SRC_MAIN_RESOURCES_POM_XML.exists());
		AMIProcessor.listCommands();
	}
}