package org.contentmine.ami.dictionary;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.tools.AMIDict;
import org.contentmine.ami.tools.AbstractAMIDictTest;
import org.contentmine.ami.tools.AbstractAMITest;
import org.junit.jupiter.api.Test;

import nu.xom.Element;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictUpdateTest extends AbstractAMIDictTest {

	private static final Logger LOG = LogManager.getLogger(AMIDictUpdateTest.class);
	private static final File TARGET_DIR = new AMIDictUpdateTest().createAbsoluteTargetDir();

	private static final String ENTRY = "entry";
	private static final String DICTIONARY = "dictionary";
	private static final String DICT1 = "dict1";
	private static final String DICT2 = "dict2";
	private static final String TITLE = "title";

	private Element dict1;
	private Element dict2;
	

	// ================
	
	@Test
	public void testHelpSubcommands() {
		String args = "update --help";
		AMIDict.execute(args);
	}
		
	@Test
	public void testSubcommands() {
		String args = "update";
		AMIDict.execute(args);
	}
	
	@Test
	public void testQuotes() {
		String args = "-vv update --merge \"foo*[a='zz']bar\"" + " --dictionary=testa,testb";
		AMIDict.execute(args);
	}
	
	@Test
	public void testDelete() {
		String dictionary = "testa";
		File outputDir = new File("target/dictionary/update");
		String args = "-vv --outputDir="+outputDir
				+ " update"
				+ " --delete"
				+ " entry@wikidata=Q123456781,@wikidata,@description"
				+ " entry@wikidata=Q123456782,@wikidata"
				+ " entry@wikidata=Q123456783,entry"
				+ " entry@term=term5,@term,@description"
				+ " --dictionary="+dictionary+" --directory=" + TEST_DICTIONARY + "";
		AMIDict.execute(args);
		AbstractAMITest.writeXMLElementAndCompare(TEST_DICTIONARY, dictionary, outputDir);
	}
	
	@Test
	public void testReplace() {
		String args = "-v update"
				+ " --replace entry@wikidata=Q123456782,@wikidata=Q42 --dictionary=testa,testb --directory=" + TEST_DICTIONARY + "";
		AMIDict.execute(args);
	}
	
	@Test
	public void testMerge() {
	String args = "-v update"
//			+ " --merge=entries,attributes"
			+ " --merge"
			+ " --control=replace"
//			+ " --strategy=or xpath=//entry"
			+ " --dictionary=testa,testb"
			+ " --directory=" + TEST_DICTIONARY + "";
	AMIDict.execute(args);
	}
	
	
	@Test
	public void testCreateFromWikidataQueryMapTransform() throws IOException {
		String dictionary = "disease4out";
		File outputDir = TARGET_DIR;
		String cmd = "-vvv"
				+ " --dictionary " + dictionary
				+ " --directory=" + outputDir
				+ " update"
				+ " --transformName wikidataID=EXTRACT(wikidataURL,.*/(.*))"
				+ ""
				+ " --synonyms=wikidataAltLabel"
				;
		AMIDict.execute(cmd);
		AbstractAMITest.writeXMLElementAndCompare(TEST_DICTIONARY, dictionary, outputDir);
	}


	/** flags
<U+E0066><U+E0072><U+E0072><U+E0065><U+E007F>
https://en.wikipedia.org/wiki/Tags_(Unicode_block)
	 */
	
}
