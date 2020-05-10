package org.contentmine.ami.dictionary;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDict;
import org.contentmine.ami.tools.AMIDictionaryToolOLD;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.contentmine.ami.tools.AbstractAMIDictTool.DictionaryFileFormat;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.ami.tools.dictionary.DictionarySearchTool;
import org.contentmine.ami.tools.download.CurlDownloader;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictSearchTest extends AbstractAMITest {
	private static final String CEV_OPEN = "/Users/pm286/projects/CEVOpen";
	private static final Logger LOG = Logger.getLogger(AMIDictSearchTest.class);
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
		Assert.assertTrue("found", dictionarySearchTool.getFoundTerms().contains("pinocarvone"));
		Assert.assertFalse("found", dictionarySearchTool.getFoundTerms().contains("Xinocarvone"));
	}

	@Test
	public void testDictionarySearchForWordsInDirectory() {
		String args = ""
				+ "-v --directory "+AMIDict.getDictionaryDirectory()
				+ " search"
				+ " --search thymol carvacrol"
				+ "";
		AMIDict.execute(args);
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
