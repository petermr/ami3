package org.contentmine.ami.dictionary;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.tools.AMIDict;
import org.contentmine.ami.tools.AbstractAMIDictTest;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictDisplayTest extends AbstractAMIDictTest {
	private static final Logger LOG = LogManager.getLogger(AMIDictDisplayTest.class);
	private static final File TARGET_DIR = new AMIDictDisplayTest().createAbsoluteTargetDir();
	
	@Test
	public void testHelpSubcommands() {
		String args = "--help";
		AMIDict.execute(args);
	}
		
	@Test
	public void testSubcommands() {
		String args = "display";
		AMIDict.execute(args);
	}
	
	@Test
	public void testMultipleSubcommands() {
		String args = "search";
		AMIDict.execute(args);
	}
	
	@Test
	public void testDictionaryDisplayHelp() {
		String args = "display --help";
		AMIDict.execute(args);
		
	}
	
	@Test
	public void testDictionaryDisplayLocal() {
		String dict = "--directory /Users/pm286/ContentMine/dictionaries";
		String args = dict + " display";
		AMIDict.execute(args);
		
	}
	
	
	@Test
	public void testDictionaryDisplayFieldsInFileIT() {
		String dict = "--directory src/main/resources/org/contentmine/ami/plugins/dictionary";
		String args = dict + " -v" + " display --fields=term,name,title,wikidata,wikipedia";
		AMIDict.execute(args);
		
	}
	
	@Test
	public void testDictionaryDisplayResource() {
		String dict = "--dictionary /org/contentmine/ami/plugins/dictionary";
		String args = dict + " -v" + " display";
		AMIDict.execute(args);		
	}
	
	@Test
	public void testDictionaryValidate() {
		String dictionary = "country";
		File directory = TEST_DICTIONARY;
		String args = ""
				+ " --dictionary " + dictionary
				+ " --directory " + directory
				+ " -vv"
				+ " display"
				+ " --fields term name description"
				+ " --validate"
				;
		AMIDict.execute(args);
		
	}

	@Test
	public void testDictionaryValidateShowBugs() {
		String dictionary = "bugs";
		File directory = TEST_DICTIONARY;
		String args = ""
				+ " --dictionary " + dictionary
				+ " --directory " + directory
				+ " -vv"
				+ " display"
				+ " --fields id"
				+ " --validate"
				;
		AMIDict.execute(args);
		
	}


}
