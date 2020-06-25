package org.contentmine.ami.plugins.gene;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.gene.GeneArgProcessor;
import org.contentmine.ami.plugins.gene.GenePlugin;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.util.NormaTestFixtures;
import org.junit.Ignore;
import org.junit.Test;

public class GeneArgProcessorTest {

	
	
	private static final Logger LOG = LogManager.getLogger(GeneArgProcessorTest.class);
static File GENE_DIR = new File(NAConstants.TEST_AMI_DIR, "gene/");

	
	@Test
	@Ignore // copy output files
	public void testGeneHarness() throws Exception {
		// SHOWCASE
		String cmd = "--g.gene --context 35 50 --g.type human -q target/plosone/gene/ -i scholarly.html"; 
 
		AMIFixtures.runStandardTestHarness(
			new File(NAConstants.TEST_AMI_DIR+"/plosone/journal.pone.0008887/"), 
			new File("target/plosone/gene/"), 
			new GenePlugin(),
			cmd,
			"gene/human/");
	}

	@Test
	public void testGenePlos() throws Exception {
		File targetDir = new File("target/plosone/gene1/");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0008887"), targetDir);
		String cmd = "--g.gene --context 35 50 --g.type human -q "+targetDir+" -i scholarly.html"; 
 
		GeneArgProcessor argProcessor = new GeneArgProcessor();
		argProcessor.parseArgs(cmd);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"human\"><result pre=\"the hepatocyte nuclear factor 4α"
				+ " ( \" exact=\"HNF4A\" post=\") gene, a well-known diabetes candidate gene not"
				+ " p\" xpath=\"/html[1]/body[1]/div[1]/div[7]/p[4]\""
				);
	}

	@Test
	public void testGeneDictionary() {
		File large = new File(AMIFixtures.TEST_PATENTS_DIR, "US08979");
		NormaTestFixtures.runNorma(large, "project", "uspto2html"); // writes to test dir
		String args = "-i scholarly.html --g.gene "
				+ "--c.dictionary "+NAConstants.GENE_HGNC+"/hgnc.xml"
				+ " --project "+large; 
		GeneArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"hgnc\"><result pre=\"Theodore Columbus OH US Agents Agent: [addressbook]:"
				+ " Patterson &amp;amp; Sheridan,\" exact=\"LLP\" post=\"unknown Assignees Assignee: [addressbook]:"
				+ " Fater S.p.A. 03 Pescara IT\" xpath=\"/html[1]/body[1]/div[1]/"
				);

	}
	
	@Test
	@Ignore // too large and requires word processor
	public void testGeneDictionaryLarge() {
		File large = new File("../patents/US08979");
		if (!large.exists()) return; // only on PMR machine
//		WordTest.runNorma(large);
		String args = "-i scholarly.html --g.gene "
				+ "--c.dictionary "+NAConstants.GENE_HGNC+"/hgnc.xml"
				+ " --project "+large; 
		GeneArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"hgnc\">"
				);

	}
	
	/** this works when run singly.
	 * 
	 * suspect the test requires setup()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSimpleGeneArgProcessor() throws Exception {
		File newDir = new File("target/gene/simple/");
		FileUtils.copyDirectory(new File(GENE_DIR, "simple"), newDir);
		String args = "--g.gene --context 35 50 --g.type human -q "+newDir+" -i scholarly.html"; 
		AMIArgProcessor argProcessor = new GeneArgProcessor(args);
		argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"human\"><result pre=\"This is \" exact=\"BRCA1\" post=\" and"
				+ " BRCA2, not FOOBAR.\" xpath=\"/html[1]/body[1]/p[3]\" name=\"human\" /><result"
				+ " pre=\"This is BRCA1 and \" exact=\"BRCA2\" post=\", not FOOBAR.\" xpath=\"/html[1]/"
				);
	}




	
}
