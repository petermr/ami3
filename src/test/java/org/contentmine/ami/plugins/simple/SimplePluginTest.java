package org.contentmine.ami.plugins.simple;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.simple.SimplePlugin;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;

public class SimplePluginTest {

	
	private static final Logger LOG = Logger.getLogger(SimplePluginTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testReadCMDir() {
		CTree cTree = new CTree(AMIFixtures.TEST_BMC_15_1_511_CMDIR);
		Assert.assertTrue("fulltext.xml", cTree.hasExistingFulltextXML());
		Assert.assertTrue("fulltext.html", cTree.hasFulltextHTML());
		Assert.assertTrue("fulltext.pdf", cTree.hasFulltextPDF());
		Assert.assertTrue("results.json", cTree.hasQuickscrapeMD());
		Assert.assertTrue("scholarly.html", cTree.hasScholarlyHTML());
	}
	
	@Test
	@Ignore // plugin argprocessor not yet working
	public void testSimplePlugin() throws IOException {
		CTree cTree = new CTree(AMIFixtures.TEST_BMC_15_1_511_CMDIR);
		File normaTemp = new File("target/bmc/15_1_511_test");
		cTree.copyTo(normaTemp, true);
		String[] args = {
				"-q", normaTemp.toString(),
				"-i", "scholarly.html",
				"-o", "results.xml",
				"--s.simple", "foo", "bar"
		};
		SimplePlugin simplePlugin = new SimplePlugin(args);
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) simplePlugin.getArgProcessor();
		Assert.assertNotNull(argProcessor);
		LOG.debug(argProcessor.getInputList());
		argProcessor.runAndOutput();
		CTree cTreeTemp = new CTree(normaTemp);
		Assert.assertTrue("results.xml", cTreeTemp.hasResultsDir());
	}
	
	/** process multiple Norma outputs.
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore // Simple Plugin with loaded argProcessor not yet working
	public void testMultipleSimplePlugin() throws IOException {
		// this simply generates 7 temporary copies of the cmDirs
		int nfiles = AMIFixtures.TEST_MIXED_DIR.listFiles().length;
		File[] normaTemp = new File[nfiles];
		File test = new File("target/simple/multiple");
		if (test.exists()) FileUtils.deleteQuietly(test);
		for (int i = 0; i < nfiles; i++) {
			CTree cTree = new CTree(new File(AMIFixtures.TEST_MIXED_DIR, "file"+i));
			normaTemp[i] = new File(test, "file"+i);
			cTree.copyTo(normaTemp[i], true);
		}
		// this is the command line with multiple CMDir directory names
		String[] args = {
				"-q", 
				normaTemp[0].toString(),
				normaTemp[1].toString(),
				normaTemp[2].toString(),
				normaTemp[3].toString(),
				normaTemp[4].toString(),
				normaTemp[5].toString(),
				normaTemp[6].toString(),
				"-i", "scholarly.html",
				"-o", "results.xml",
				"--s.simple", "foo", "bar"
		};
		SimplePlugin simplePlugin = new SimplePlugin(args);
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) simplePlugin.getArgProcessor();
		argProcessor.runAndOutput();
		int[] size = {17624,4447,0, 4839,4311,4779,5288}; // file2 has smart quotes; fix HTMLFactory()
		for (int i = 0; i < nfiles; i++) {
			Element rootXML = (Element) XMLUtil.parseQuietlyToDocument(new File(test, "file"+i+"/results.xml")).getRootElement();
			Element resultXML = (Element) rootXML.getChildElements().get(0);
			Assert.assertEquals("file"+i, size[i], (int) new Integer(resultXML.getAttributeValue("wordCount")));
		}
	}
	
	
}
