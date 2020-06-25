package org.contentmine.ami.dictionary;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIDict;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.ami.tools.dictionary.DictionarySearchTool;
import org.junit.Assert;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictSearchTest extends AbstractAMITest {
	private static final String CEV_OPEN = "/Users/pm286/projects/CEVOpen";
	private static final Logger LOG = LogManager.getLogger(AMIDictSearchTest.class);
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
		String args = "search --help";
		AMIDict.execute(args);
	}
		
	@Test
	public void testSubcommands() {
		String args = "search ";
		AMIDict.execute(args);
	}
	

	
	@Test
	public void testDictionarySearchForWords() {
		String args = ""
				+ " --dictionary "+SRC_TEST_DICTIONARY+"/eoCompound.xml"
				+ " search"
				+ " --search thymol carvacrol"
				+ "";
		AMIDict.execute(args);
	}
	
	@Test
	public void testDictionarySearchForWordsInDictionaryFile() {
		String args = ""
				+ " --dictionary "+SRC_TEST_DICTIONARY+"/eoCompound.xml"
				+ " search"
				+ " --searchfile "+SRC_TEST_DICTIONARY+"/compound_set.txt"
				+ "";
		DictionarySearchTool dictionarySearchTool = AMIDict.execute(DictionarySearchTool.class, args);
		Assert.assertEquals("found", 309, dictionarySearchTool.getOrCreateFoundTerms().size());
		Assert.assertTrue("found", dictionarySearchTool.getOrCreateFoundTerms().contains("pinocarvone"));
		Assert.assertFalse("found", dictionarySearchTool.getOrCreateFoundTerms().contains("Xinocarvone"));
	}

	@Test
	public void testDictionarySearchForWordsInDirectory() {
		String args = ""
				+ "-v --directory "+AMIDict.getDictionaryDirectory()
				+ " search"
				+ " --search abamectin ampicillin"
				+ "";
		DictionarySearchTool dictionarySearchTool = AMIDict.execute(DictionarySearchTool.class, args);
		Assert.assertEquals("found", 2, dictionarySearchTool.getOrCreateFoundTerms().size());
		Assert.assertTrue("found", dictionarySearchTool.getOrCreateFoundTerms().contains("abamectin"));
		Assert.assertFalse("found", dictionarySearchTool.getOrCreateFoundTerms().contains("ivermectin"));
	}

	@Test
	public void testDictionarySearchForWordsInFileInDirectory() {
		String args = ""
				+ " --directory "+AMIDict.getDictionaryDirectory()
				+ " search"
				+ " --searchfile "+SRC_TEST_DICTIONARY+"/compound_set.txt"
				+ "";
		AMIDict.execute(args);
	}


}
