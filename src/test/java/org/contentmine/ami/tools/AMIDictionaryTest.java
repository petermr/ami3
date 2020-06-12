package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.dictionary.DictionaryCreationTool;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictionaryTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMIDictionaryTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final File TARGET = new File("target");
	public static final File DICTIONARY_DIR = new File(TARGET, "dictionary");
	

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
		String args = "create --help";
		AMIDict.execute(args);
	}
		
	@Test
	public void testSubcommands() {
		String args = "create ";
		AMIDict.execute(args);
	}
	
	@Test
	public void testCreateFromWikipediaCategory() {
		String categoryString = "https://en.wikipedia.org/wiki/Category:Human_migration";
		String cmd = "-v --dictionary hummig.xml --directory=target/dictionary/ "
				+ "--input=" + categoryString + " create --informat=wikicategory";
		DictionaryCreationTool dictionaryTool = AMIDict.execute(DictionaryCreationTool.class, cmd);
	}
	
	

}
