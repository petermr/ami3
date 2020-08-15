package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AbstractAMIDictTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AbstractAMIDictTest.class);
	
	public static final File DICTIONARY_DIR = new File(SRC_TEST_AMI, "dictionary");
	protected static final File TARGET_DICTIONARY = new File(TARGET, "dictionary/");
	

	@Test
	public void testHelpBare() {
		String args = null;
		AMIDict.execute(args);
	}
	
	@Test
	public void testHelp() {
		String args = "--help";
		AMIDict.execute(args);
	}
	
}
