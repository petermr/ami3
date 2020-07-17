package org.contentmine.ami;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.CHESConstants;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.ContentProcessor;
import org.contentmine.cproject.files.ResultsElementList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;

public class AMIFixtures {

	
	private static final Logger LOG = LogManager.getLogger(AMIFixtures.class);
public final static File TEST_RESOURCES_DIR    = new File(CHESConstants.SRC_TEST_RESOURCES);
	public final static File TARGET_DIR    = new File("target");
	
	public static final String AMISTACK       = "amistack/";
	public static final File TEST_AMISTACK_DIR = new File(NAConstants.TEST_AMI_DIR, AMISTACK);
	public static final File TARGET_AMISTACK_DIR = new File(TARGET_DIR, AMISTACK);

	public final static File TEST_BMC_DIR          = new File(NAConstants.TEST_AMI_DIR, "bmc");
	public final static File TEST_BMC_15_1_511_CMDIR = new File(AMIFixtures.TEST_BMC_DIR, "15_1_511");
	public final static File TEST_TRIALS_16_1_1 = new File(AMIFixtures.TEST_BMC_DIR, "http_www.trialsjournal.com_content_16_1_1");
	
	public final static File TEST_DICTIONARY_DIR    = new File(NAConstants.TEST_AMI_DIR, "dictionary");
	
	public final static File TEST_GRAPHCHEM_DIR    = new File(NAConstants.TEST_AMI_DIR, "graphchem");
	public final static File TEST_GRAPHCHEM_ASPERGILLUS    = new File(AMIFixtures.TEST_GRAPHCHEM_DIR, "aspergillus_9");
	
	public final static File TEST_MIXED_DIR        = new File(NAConstants.TEST_AMI_DIR, "mixed");

	public final static File TEST_PATENTS_DIR      = new File(NAConstants.TEST_AMI_DIR, "patents");

	public final static File TEST_PLOSONE_DIR      = new File(NAConstants.TEST_AMI_DIR, "plosone");
	public final static File TEST_PLOSONE_0115884  = new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0115884");
	public final static File TEST_PLOSONE_SEQUENCE_0121780  = new File(AMIFixtures.TEST_PLOSONE_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_PLOSONE_MALARIA_0119475  = new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0119475");

	public final static File TEST_IJSEM_DIR      = new File(NAConstants.TEST_AMI_DIR, "ijsem");
	public final static File TEST_IJSEM_0115884  = new File(AMIFixtures.TEST_IJSEM_DIR, "journal.pone.0115884");
	public final static File TEST_IJSEM_SEQUENCE_0121780  = new File(AMIFixtures.TEST_IJSEM_DIR, "plosjournal.pone.0121780_sequence");
	public final static File TEST_IJSEM_MALARIA_0119475  = new File(AMIFixtures.TEST_IJSEM_DIR, "journal.pone.0119475");

	public final static File TEST_PHYLO_DIR          = new File(NAConstants.TEST_AMI_DIR, "phylo");
	
	public static final String PLANT = "plant";
	public final static File TEST_PLANT_DIR          = new File(NAConstants.TEST_AMI_DIR, PLANT);
	public final static File TARGET_PLANT_DIR        = new File(AMIFixtures.TARGET_DIR, PLANT);

	public static final String TOTAL_INT = "totalIntegration";
	public final static File TEST_TOTAL_INT_DIR          = new File(NAConstants.TEST_AMI_DIR, TOTAL_INT);
	public final static File TARGET_TOTAL_INT_DIR        = new File(AMIFixtures.TARGET_DIR, TOTAL_INT);

	public final static File TEST_PLOT_DIR    = new File(NAConstants.TEST_NORMA_DIR, "plot");

	public final static File TEST_RESULTS_DIR        = new File(NAConstants.TEST_AMI_DIR, "results/");
	public final static File TEST_RRID_DIR           = new File(NAConstants.TEST_AMI_DIR, "rrid/");
	public final static File TEST_OIL5_DIR           = new File(NAConstants.TEST_AMI_DIR, "oil5/");
	public final static File TEST_OMAR_DIR           = new File(NAConstants.TEST_AMI_DIR, "omar/");
	public final static File TEST_TOOLS_DIR           = new File(NAConstants.TEST_AMI_DIR, "tools/");
	public final static File TEST_ZIKA1_DIR           = new File(NAConstants.TEST_AMI_DIR, "zika1/");
	public final static File TEST_ZIKA2_DIR           = new File(NAConstants.TEST_AMI_DIR, "zika2/");
	public final static File TEST_ZIKA10_DIR           = new File(NAConstants.TEST_AMI_DIR, "zika10/");
	public final static File TEST_ZIKA10A_DIR           = new File(NAConstants.TEST_AMI_DIR, "zika10a/");

	public final static File TEST_SET_DIR            = new File(NAConstants.TEST_NORMA_DIR, "testSets");
	public final static File TEST_SET_MARCHANTIA_DIR = new File(AMIFixtures.TEST_SET_DIR, "marchantia");
	public final static File TEST_SET_MARCHANTIA20_DIR = new File(AMIFixtures.TEST_SET_DIR, "marchantia20");
	public final static File TARGET_SET_DIR          = new File(AMIFixtures.TARGET_DIR, "testSets");
	public final static File TARGET_SET_MARCHANTIA_DIR = new File(AMIFixtures.TARGET_SET_DIR, "marchantia");
	public final static File TARGET_SET_MARCHANTIA20_DIR = new File(AMIFixtures.TARGET_SET_DIR, "marchantia20");

	public static final String SPECTRA = "spectra";
	public static final File SPECTRA_PLOT_DIR = new File(TEST_PLOT_DIR, SPECTRA);
	public static final File SPECTRA_PLOT_TARGET_DIR = new File(TARGET_DIR, SPECTRA);

	public final static File TEST_TUTORIAL_DIR       = new File(NAConstants.TEST_AMI_DIR, "tutorial/");
	


	public static final File TEST_WORD_DIR           = new File(NAConstants.TEST_AMI_DIR, "word");
	public static final File TEST_WORD_EXAMPLES      = new File(TEST_WORD_DIR, "examples");
	
	public static final File TEST_WORKBENCH_DIR      = new File(NAConstants.TEST_AMI_DIR, "workbench");
	public static final File TEST_WORKBENCH_MOSQUITO_PDF = new File(TEST_WORKBENCH_DIR, "mosquitosPDF");
	public static final File TEST_WORKBENCH_MOSQUITO_PDF1 = new File(TEST_WORKBENCH_DIR, "mosquitosPDF1");
	public static final File TEST_WORKBENCH_MOSQUITO_PDF_SVG =
			new File(TEST_WORKBENCH_DIR, "mosquitosPDFSVG");
	public static final File TEST_WORKBENCH_MOSQUITO = new File(TEST_WORKBENCH_DIR, "mosquitos");

	public final static File TEST_PROJECTS_DIR       = new File("/Users/pm286/workspace/projects/");
	public final static File TARGET_PROJECTS_DIR         = new File(AMIFixtures.TARGET_DIR, "projects/");

	public static final File TARGET_EXAMPLES_TEMP_16_1_1  = new File("target/examples_16_1_1");

	public static final String RESULTS_XML = "results.xml";
	private static final String RESULTS_DIR = "results/";
	private static final String EXPECTED_DIR = "expected/";
	private static final String TARGET_TEST = "target/test/";
	
	public static final File PMR_TOP = new File(System.getProperty("user.home"));
	public static final File PMR_PROJECT_DIR = new File(PMR_TOP, "workspace/projects");
	public static final File PMR_STEFAN_DIR = new File(PMR_PROJECT_DIR, "stefan");
	
	public static final File PMR_MARCHANTIA_DIR = new File(PMR_PROJECT_DIR, "marchantia");



	/** runs tests and compares expected and actual output.
	 * 
	 * @param cTreeDirectory contentMine directory
	 * @param newDir directory (will create)
	 * @param plugin plugin to use
	 * @param pluginAndOptions directories for output (e.g. species/binomial/)
	 * @throws IOException
	 */
	public static void runStandardTestHarness(File cTreeDirectory, File newDir, AMIPlugin plugin, String args, String ... pluginAndOptions)
			throws IOException {
		LOG.trace("++++++++++++++++++++++   harness   +++++++++++++++++++++++");
		LOG.trace("newDir exists: "+newDir+"; e: "+newDir.exists()+"; d "+newDir.isDirectory());
		CTree cTree = new CTree(cTreeDirectory);
		if (newDir.exists()) FileUtils.deleteDirectory(newDir);
		cTree.copyTo(newDir, true);
		
		Assert.assertFalse("exists? "+RESULTS_XML, cTree.hasResultsDir());
		DefaultArgProcessor argProcessor = (DefaultArgProcessor) plugin.getArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		List<File> files = new ArrayList<File>(FileUtils.listFiles(newDir, null, true));
		LOG.trace("FILES after: "+files);
		LOG.trace("==========================="+argProcessor+"=============================");
		LOG.trace("results exists? "+new File(newDir,"results").exists());
		
		for (String pluginAndOption : pluginAndOptions) {
			AMIFixtures.compareExpectedAndResults(cTree.getDirectory(), newDir, pluginAndOption, RESULTS_XML);
		}
	}

	/** compares results.xml files in expected and actual directories.
	 * 
	 * @param expectedCM cmDirectory (must contain expected/)
	 * @param resultsCM cmDiecrory (must contain results/)
	 * @param pluginAndOption e.g. "species/binomial/"
	 * @throws IOException
	 */
	public static void compareExpectedAndResults(File expectedCM, File resultsCM, String pluginAndOption, String testFilename) throws IOException {
		
		File expectedFile = new File(new File(new File(expectedCM, EXPECTED_DIR), pluginAndOption), testFilename);
		Assert.assertTrue("expected file should exist ("+pluginAndOption+"): "+expectedFile, expectedFile.exists());
		File resultsFile = new File(new File(new File(resultsCM, RESULTS_DIR), pluginAndOption), testFilename);
		Assert.assertTrue("results file should exist ("+pluginAndOption+"): "+resultsFile, resultsFile.exists());
		String msg = XMLUtil.equalsCanonically(
	    		expectedFile, 
	    		resultsFile,
	    		true);
		if (msg != null) {
			LOG.debug("bad compare: "+expectedFile+"; "+ FileUtils.readFileToString(expectedFile, CMineUtil.UTF8_CHARSET) +"\n"+
					""+resultsFile+"; "+FileUtils.readFileToString(resultsFile, CMineUtil.UTF8_CHARSET));
		}
	    Assert.assertNull("message: "+msg, msg);
	}
	
	

	// utility method to check first part of resultsElementList
	
	// FIXME better test
	public static void checkResultsElementList(AMIArgProcessor argProcessor, int size, int elem, String start) {
		if (argProcessor == null) {
			LOG.error("null argProcessor");
			return;
		}
		CTree currentTree = argProcessor.getCurrentCTree();
		if (currentTree == null) {
			LOG.warn("Null CTree");
			return;
		}
		ContentProcessor contentProcessor = argProcessor.getOrCreateContentProcessor();
		ResultsElementList reList = contentProcessor.getOrCreateResultsElementList();
		reList.sortByTitle();
		Assert.assertEquals(size, reList.size());
		if (elem < size) {
			String results = reList.get(elem).toXML();
			if (!results.startsWith(start)) {
				String ss = results.substring(0,  Math.min(300,  results.length()));
				// replace " apos by \"
				String sss = ss.replaceAll("\"", "\\\\\\\"");
				ss = "<results title=\"frequencies\">";
				LOG.trace("start (escaped) \n"+sss);
//				Assert.fail("results assertion failure: starts with: "+ss);
			}
		}
	}

	public static CommandProcessor createDefaultDirectoriesAndProcessor(String projectName) throws IOException {
		File rawDir = new File(NAConstants.TEST_AMI_DIR, projectName);
		File projectDir = new File(AMIFixtures.TARGET_TEST, projectName);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		LOG.debug("wrote clean copy: "+projectDir);
		return commandProcessor;
	}

	

	
}
