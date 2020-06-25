package org.contentmine.ami;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.ResultsAnalysisImpl;
import org.contentmine.cproject.files.ProjectSnippetsTree;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.DataTablesTool;
import org.contentmine.cproject.util.ResultsAnalysis.SummaryType;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

/** experiments to see if output has relevance or signals.
 * 
 * experimental!
 * 
 * @author pm286
 *
 */
public class ResultsAnalysisTest {

	private static final Logger LOG = LogManager.getLogger(ResultsAnalysisTest.class);
public static final File ZIKA_DIR = new File(AMIFixtures.TEST_RESULTS_DIR, "zika");
	
	/** summarizes counts of all terms.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testSummarizePrimer() throws IOException {
		File targetDir = new File("target/relevance/zika");
		CMineTestFixtures.cleanAndCopyDir(ZIKA_DIR, targetDir);
		Element snippetsTreeXML = XMLUtil.parseQuietlyToDocument(new File(targetDir, "sequence.dnaprimer.snippets.xml")).getRootElement();;
		ProjectSnippetsTree projectSnippetsTree = ProjectSnippetsTree.createProjectSnippetsTree(snippetsTreeXML);
		Assert.assertEquals("snippetsTrees", 2, projectSnippetsTree.getOrCreateSnippetsTreeList().size());
		List<String> filenames = projectSnippetsTree.getOrCreateFilenameList();
		Assert.assertEquals("filenames",  "[target/tutorial/zika/PMC4654492/results/sequence/dnaprimer/results.xml,"
				+ " target/tutorial/zika/PMC4671560/results/sequence/dnaprimer/results.xml]", filenames.toString());
		
	}
	/** summarizes counts of all terms.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testSummarizeAllTypes() throws IOException {
		ProjectSnippetsTree projectsSnippetsTree = CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, "gene.human.snippets.xml");
		Assert.assertEquals("gene snippetsTrees", 10, projectsSnippetsTree.getOrCreateSnippetsTreeList().size());
		projectsSnippetsTree = CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, "sequence.dnaprimer.snippets.xml");
		Assert.assertEquals("dnaprimer snippetsTrees", 2, projectsSnippetsTree.getChildElements().size());
		projectsSnippetsTree = CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, "species.binomial.snippets.xml");
		Assert.assertEquals("binomial snippetsTrees", 76, projectsSnippetsTree.getChildElements().size());
		projectsSnippetsTree = CMineTestFixtures.createProjectSnippetsTree(ZIKA_DIR, "species.genus.snippets.xml");
		Assert.assertEquals("genus snippetsTrees", 64, projectsSnippetsTree.getChildElements().size());
	}
	
	@Test
	public void testResultsAnalysis() throws IOException {
		ResultsAnalysisImpl resultsAnalysis = new ResultsAnalysisImpl();
		resultsAnalysis.addDefaultSnippets(ZIKA_DIR);
		Assert.assertTrue(resultsAnalysis.getProjectSnippetsTreeByPluginOption().size() > 3);
		List<String> cTreeNameList = resultsAnalysis.getSortedCTreeNameList();
/*		
		Assert.assertEquals("projects", "["
				+ "PMC2640145, PMC2819875, PMC3113902, PMC3289602, PMC3310457, PMC3310660, PMC3321795, PMC3321797,"
				+ " PMC3323869, PMC3342053, PMC3369199, PMC3384601, PMC3429392, PMC3499821, PMC3616844, PMC3715421,"
				+ " PMC3731215, PMC3744434, PMC3749491, PMC3765938, PMC3766601, PMC3810876, PMC3854913, PMC3863887,"
				+ " PMC3880869, PMC3888466, PMC3901491, PMC3916288, PMC3944877, PMC3945292, PMC4016539, PMC4023339,"
				+ " PMC4036762, PMC4036769, PMC4042264, PMC4047448, PMC4055592, PMC4093116, PMC4111184, PMC4177891,"
				+ " PMC4191940, PMC4193187, PMC4195678, PMC4239016, PMC4240730, PMC4246241, PMC4270494, PMC4285245,"
				+ " PMC4295104, PMC4295884, PMC4306835, PMC4313662, PMC4338443, PMC4342338, PMC4344295, PMC4347352,"
				+ " PMC4378478, PMC4378499, PMC4381565, PMC4395659, PMC4400050, PMC4409421, PMC4412240, PMC4447349,"
				+ " PMC4461419, PMC4468249, PMC4480070, PMC4491199, PMC4495344, PMC4501423, PMC4520491, PMC4527740,"
				+ " PMC4530765, PMC4553466, PMC4553499, PMC4561566, PMC4568054, PMC4569826, PMC4593454, PMC4629289,"
				+ " PMC4632385, PMC4637084, PMC4651505, PMC4654492, PMC4666598, PMC4671560, PMC4672408, PMC4673751,"
				+ " PMC4681859, PMC4686165]", cTreeNameList.toString());
*/		
	}
	
	@Test
	public void testMakeDataTable() throws IOException {
		DataTablesTool dataTablesTool = DataTablesTool.createBiblioEnabledTable();
		dataTablesTool.setTitle("Zika");
		ResultsAnalysisImpl resultsAnalysis = new ResultsAnalysisImpl(dataTablesTool);
		File inputFile = ZIKA_DIR;
		dataTablesTool.setCellCalculator(resultsAnalysis);
		resultsAnalysis.addDefaultSnippets(inputFile);
		resultsAnalysis.addSnippetsFile(new File(inputFile, "search.tropicalVirus.snippets.xml"));
		resultsAnalysis.setRemoteLink0("../../"+NAConstants.TEST_AMI_DIR+"/zika/");
		resultsAnalysis.setRemoteLink1("/scholarly.html");
		resultsAnalysis.setRowHeadingName("EPMCID");
		resultsAnalysis.setSummaryType(SummaryType.COMMONEST);
		HtmlTable table = resultsAnalysis.makeHtmlDataTable();
		HtmlHtml html = dataTablesTool.createHtmlWithDataTable(table);
		XMLUtil.debug(html, new File("target/resultsAnalysis/datatable.html"), 1);
	}
	
//	@Test
//	public void testMakeDataTableCommonest() throws IOException {
//		analyzeResults(ZIKA_DIR, CellType.COMMONEST, new File("target/resultsAnalysis/commonesttable.html"));
//	}
//	
//	@Test
//	public void testMakeDataTableCount() throws IOException {
//		analyzeResults(ZIKA_DIR, CellType.COUNT, new File("target/resultsAnalysis/counttable.html"));
//	}
//
//	@Test
//	public void testMakeDataTableEntries() throws IOException {
//		analyzeResults(ZIKA_DIR, CellType.ENTRIES, new File("target/resultsAnalysis/entrieestable.html"));
//	}
//	
	private void analyzeResults(File projectDir, SummaryType cellType, File outfile) throws IOException {
		List<SummaryType> cellTypes = Arrays.asList(new SummaryType[]{SummaryType.COMMONEST});
		DataTablesTool dataTablesTool = DataTablesTool.createBiblioEnabledTable();
		dataTablesTool.setTitle("Zika");
		ResultsAnalysisImpl resultsAnalysis = new ResultsAnalysisImpl(dataTablesTool);
		resultsAnalysis.addDefaultSnippets(projectDir);

		resultsAnalysis.setRemoteLink0("../../"+NAConstants.TEST_AMI_DIR+"/zika/");
		resultsAnalysis.setRemoteLink1("/scholarly.html");
		resultsAnalysis.setRowHeadingName("EPMCID");
		resultsAnalysis.setSummaryTypes(cellTypes);
		HtmlTable table = resultsAnalysis.makeHtmlDataTable();
		HtmlHtml html = dataTablesTool.createHtmlWithDataTable(table);
		XMLUtil.debug(html, outfile, 1);
	}
	
	
	
	// ============================


	//==================================

	
}
