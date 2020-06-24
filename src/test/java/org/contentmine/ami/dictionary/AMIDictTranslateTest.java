package org.contentmine.ami.dictionary;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDict;
import org.contentmine.ami.tools.AbstractAMITest;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictTranslateTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMIDictTranslateTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final File TARGET = new File("target");
	public static final File DICTIONARY_DIR = new File(TARGET, "dictionary");
	public static final File SRC_TEST_DICTIONARY = new File(SRC_TEST_AMI, "dictionary");
	

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
	
	@Test
	public void testHelpSubcommands() {
		String args = "translate --help";
		AMIDict.execute(args);
	}
		
	@Test
	public void testSubcommands() {
		String args = "translate ";
		AMIDict.execute(args);
	}
	

	
	@Test
	public void testTranslateJSONtoXMLAbsolute() {
		String args =
//			"dictionary " +
			"translate" +
           " --dictionary "+SRC_TEST_DICTIONARY+"/alliaceae.json " +
//           "+SRC_TEST_DICTIONARY+"/buxales.json " +
           " --outformats xml"
		;
		AMIDict.execute(args);
	}
	
	@Test
	/**
	 * SMALL
	 */
	public void testTranslateJSONtoXMLAbsoluteWikidata() {
		String args =
//			"dictionary " +
			"translate " +
           " --dictionary "+SRC_TEST_DICTIONARY+"/alliaceae.json " +
//			                SRC_TEST_DICTIONARY+"/buxales.json " +
           " --outformats xml " +
           " --wikilinks wikidata wikipedia"
		;
		AMIDict.execute(args);
		
	}
	
	@Test
	/** MANY redlinks cause errors
	 * https://en.wikipedia.org/w/index.php?title=Buxus_pubiramea&action=edit&redlink=1
	 * TRAP these!
	 */
	public void testTranslateJSONtoXMLAbsoluteWikidataRedlinks() {
		String args =
//			"dictionary " +
			"translate " +
           " --dictionary "+SRC_TEST_DICTIONARY+"/alliaceae.json " +
                            SRC_TEST_DICTIONARY+"/buxalessmall.json " +
           " --outformats xml " +
           " --wikilinks wikidata wikipedia"
		;
		AMIDict.execute(args);
		
	}
}
