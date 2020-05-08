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
	public void testDictionaryTop() {
//		String args = "--dictionary src/main/resources/org/contentmine/ami/plugins/dictionary display ";
		String args = "--dictionary /Users/pm286/ContentMine/dictionary/dictionaries display ";
		AMIDict.execute(args);
		
	}
	
	

}
