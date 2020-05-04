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
import org.contentmine.ami.tools.AbstractAMIDictTool.DictionaryFileFormat;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.ami.tools.AbstractAMITool;
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
public class AMIDictTranslateTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMIDictTranslateTest.class);
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
           " --dictionary src/test/resources/org/contentmine/ami/dictionary/alliaceae.json " +
			                "src/test/resources/org/contentmine/ami/dictionary/buxales.json " +
           " --outformats xml"
		;
		AMIDict.execute(args);
	}
	
	@Test
	/** MANY redlinks cause errors
	 * https://en.wikipedia.org/w/index.php?title=Buxus_pubiramea&action=edit&redlink=1
	 * TRAP these!
	 */
	public void testTranslateJSONtoXMLAbsoluteWikidata() {
		String args =
//			"dictionary " +
			"translate " +
           " --dictionary src/test/resources/org/contentmine/ami/dictionary/alliaceae.json " +
//			                "src/test/resources/org/contentmine/ami/dictionary/buxales.json " +
           " --outformats xml " +
           " --wikilinks wikidata wikipedia"
		;
		AMIDict.execute(args);
		
	}
	
	@Test
	@Ignore // CEVOpen
	public void testDictionarySearch() {
		String args =
				"dictionary " +
				"search"
				+ " --dictionary "+CEV+"/dictionary/compound/compound.xml"
//				+ " --search thymol carvacrol"
				+ " --searchfile "+CEV_SEARCH+"/oil186/__tables/compound_set.txt"
				+ "";
		AMIDict.execute(args);
	}

}
