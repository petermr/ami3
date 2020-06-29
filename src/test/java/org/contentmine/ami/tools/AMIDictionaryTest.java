package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.dictionary.DictionaryCreationTool;
import org.contentmine.cproject.util.CMineUtil;
import org.junit.Test;


/** tests AMIDictionary
 * 
 * @author pm286
 *
 */
public class AMIDictionaryTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AMIDictionaryTest.class);
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
		String cmd = "-v"
				+ " --dictionary hummig"
				+ " --directory=target/dictionary/"
				+ " --input=" + categoryString 
				+ " create"
				+ " --informat=wikicategory";
		AbstractAMIDictTool dictionaryTool = AMIDict.execute(DictionaryCreationTool.class, cmd);
	}
	
	// CREATE
	@Test
	/** creates  mini dictionary with wikipedia and wikidata links where possible
	 * 
	 */
	public void testCreateWikipedia() {
		String cmd = " "
				+ " -vvvv"
				+ " --dictionary myterpenes"
				+ " --directory=target/dictionary/create"
				+ " --inputname miniterpenes"
				+ " create"
				+ " --wikilinks wikidata wikipedia"
				+ " --terms thymol "
				+ " menthol borneol"
				+ " junkolol "
				+ " --informat list"
				+ " --outformats xml"		
				;
		AbstractAMIDictTool dictionaryTool = AMIDict.execute(DictionaryCreationTool.class, cmd);
	}
	
	// *===============================
	@Test
	// debugging encoding - will probably scrap
	public void testRawWikidata() throws Exception {
		URL url = new URL("https://www.wikidata.org/w/index.php?search=thymol");
		InputStream is = url.openStream();
		String s;
		s = IOUtils.toString(is, CMineUtil.UTF8_CHARSET);
		// remove LRM character 
		s = s.replaceAll(String.valueOf((char)8206), "");
		int idx0 = 0;
		String f;
		f = "â";
		f = "hymol";
		while (true) { 
			int idx = s.substring(idx0).indexOf(f);
			if (idx == -1) {
				break;
			}
			idx0 += idx + 1;
			System.out.println(idx+"/"+idx0);
			String substring = s.substring(idx0-35, idx0+35);
			
			System.out.println(substring);
			for (int i = 0; i < substring.length(); i++) {
				char charAt = substring.charAt(i);
				System.out.print(charAt+ "|"+(int)charAt+"|");
			}
			System.out.println();
		}
//		System.out.println(s);
	}


}
