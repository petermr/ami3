package org.contentmine.ami.plugins;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

public class AMIPluginTest {

	@Test
	@Ignore // outputs heklp
	public void testAMIPlugin() {
		String[] args = {"regex"};
		AMIPlugin.main(args);
	}
	
	@Test
	@Ignore // avoid help output
	public void testAMIPluginHelp() {
		String[] args = {"regex", "--help"};
		AMIPlugin.main(args);
	}
	
	@Test
	public void testAMIPluginIO() {
		String[] args = {"regex", "--input", "foo", "--output", "bar"};
		AMIPlugin.main(args);
	}
	
	@Test
	public void testBadXPath() {
		String cmd = "-q "+NAConstants.TEST_AMI_DIR+"/bmc/15_1_511 -i fulltext.xml --xpath //*]"; 
		try {
			new AMIArgProcessor(cmd);
			Assert.fail("Should throw Exception for bad XPath");
		} catch (Exception e) {
			// OK
		}
	}
	
	@Test
	public void testXPath() {
		String cmd = "-q "+NAConstants.TEST_AMI_DIR+"/bmc/15_1_511 -i scholarly.html --xpath //*[@tagx='cpyrt']"; 
		AMIArgProcessor argProcessor = new AMIArgProcessor(cmd);
		argProcessor.runAndOutput();
		List<? extends Element> sectionList = argProcessor.ensureSectionElements();
		Assert.assertEquals("cpyrt section", 1, sectionList.size());
		argProcessor = new AMIArgProcessor(
				"-q "+NAConstants.TEST_AMI_DIR+"/bmc/15_1_511 -i scholarly.html --xpath //*[@tagx='kwd']");
		argProcessor.runAndOutput();
		sectionList = argProcessor.ensureSectionElements();
		Assert.assertEquals("cpyrt section", 7, sectionList.size());
	}
	
	/** bag of words on papers translated from IEEE HTML into scholarly.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testNormaAmiIEEE() throws IOException {
		FileUtils.copyDirectory(new File(""+NAConstants.TEST_AMI_DIR+"/ieee"), new File("target/ieee/words"));
		String cmd = ""
				+ "-q target/ieee/words"
				+ " -i scholarly.html"
				+ " --w.words wordFrequencies"
				+ " --w.stopwords "+NAConstants.AMI_WORDUTIL+"/stopwords.txt";
		AMIArgProcessor argProcessor = new WordArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testWordArgs() throws IOException {
		FileUtils.copyDirectory(new File(""+NAConstants.TEST_AMI_DIR+"/ieee"), new File("target/ieee/words"));
		String[] args = {
				"word", 
				"--ctree", 
				"target/ieee/words",
				"--input", 
				"scholarly.html",
				"--w.words",
				"wordFrequencies",
				"--w.stopwords", 
				NAConstants.PLUGINS_WORD+"/stopwords.txt"
				};
		AMIPlugin.main(args);
	}
}
