package org.contentmine.ami.dictionary;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMI;
import org.contentmine.ami.tools.AMIDict;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.ami.tools.dictionary.DictionaryDisplayTool;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictDisplayTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMIDictDisplayTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final File TARGET = new File("target");
	public static final File DICTIONARY_DIR = new File(TARGET, "dictionary");
	

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
	public void testDictionaryDisplayFieldsInFile() {
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
	
	

}
