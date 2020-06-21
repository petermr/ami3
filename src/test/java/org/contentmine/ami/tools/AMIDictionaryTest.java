package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.dictionary.DictionaryCreationTool;
import org.contentmine.cproject.util.CMineUtil;
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
		String cmd = "-v"
				+ " --dictionary hummig"
				+ " --directory=target/dictionary/"
				+ " --input=" + categoryString 
				+ " create"
				+ " --informat=wikicategory";
		DictionaryCreationTool dictionaryTool = AMIDict.execute(DictionaryCreationTool.class, cmd);
	}
	
	// CREATE
	@Test
	/** this command fails to read Wikipedia, perhaps because of a format change
	 * 
	 */
	public void testCreateWikipedia() {
		String cmd = " -v"
				+ " --dictionary myterpenes"
				+ " --directory=target/dictionary/create"
				+ " --inputname miniterpenes"
				+ " create"
				+ " --wikilinks wikidata wikipedia"
				+ " --terms thymol menthol borneol junkolol "
				+ " --informat list"
				+ " --outformats xml"		
				;
		DictionaryCreationTool dictionaryTool = AMIDict.execute(DictionaryCreationTool.class, cmd);
	}
	
	// *===============================
	@Test
	public void testRawWikidata() throws Exception {
//		URL url = new URL("https://www.wikidata.org/w/index.php?search=thymol&search=thymol&title=Special%3ASearch&go=Go&ns0=1&ns120=1");
		URL url = new URL("https://www.wikidata.org/w/index.php?search=thymol");
		InputStream is = url.openStream();
		String s;
//		s = IOUtils.toString(is);
		s = IOUtils.toString(is, CMineUtil.UTF8_CHARSET);
		s = s.replaceAll(String.valueOf((char)8206), "");
//		s = IOUtils.toString(is, CMineUtil.CP1252_CHARSET);
//		s = IOUtils.toString(is, CMineUtil.ISO_8859_1_CHARSET);
//		IOUtils.write(s, new FileOutputStream("target/wikipedia/wikidata.html"), CMineUtil.UTF8_CHARSET);
//		IOUtils.write(s, new FileOutputStream("target/wikipedia/wikidata.txt"), CMineUtil.UTF8_CHARSET);
		int idx0 = 0;
		String f;
		f = "â";
		f = "hymol";
//		System.out.println("l "+s.length());
//		s = s.replaceAll(""+(char) 8206, "");
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
