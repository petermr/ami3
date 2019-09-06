package org.contentmine.ami;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIProcessor;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.EntityAnalyzer;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.OccurrenceType;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.SubType;
import org.contentmine.ami.tools.AMIOCRTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.junit.Ignore;
import org.junit.Test;

/** these run to complete stack from PDF to co-occurrence and other tasks.
 *  
 * @author pm286
 *
 */
// @Ignore // FOR TESTS, REMOVE LATER
public class AMIIntegrationDemosIT {
	public static final Logger LOG = Logger.getLogger(AMIIntegrationDemosIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testMakeBiorxiv() {
		
		boolean skipCleanCopy = true;
		boolean skipSVG = true;
		boolean skipHtml = false;
		boolean skipRun = false;
		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR, "biorxiv/all39");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, "biorxiv/all39");
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		if (!skipCleanCopy) CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		
		new CProject().run("--project "+targetDir+" --makeProject (\\1)/fulltext.pdf --fileFilter .*/(.*)\\.pdf");
		if (!skipSVG) {
			CProject cProject = new CProject(targetDir);
			cProject.convertPDFOutputSVGFilesImageFiles();
		}
		if (!skipHtml) {
			CProject cProject = new CProject(targetDir);
			cProject.convertPSVGandWriteHtml();
		}
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " species(binomial)"
		+ " gene(human) "
		+ " search(auxin)"
		+ " search(plantDevelopment)"
//		+ " search(pectin)"
+ " search(plantparts)"
+ " search(country)"
+ " search(funders)"
		+ " search(synbio)"

	    ;
		File projectDir = targetDir;
		if (!skipRun) {
			try {
				CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
			} catch (IOException e) {
				throw new RuntimeException("Cannot run command: "+cmd, e);
			}
		}
		
		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(projectDir);
		
		entityAnalyzer.createAndAddOccurrenceAnalyzer(OccurrenceType.BINOMIAL).setMaxCount(25);
		entityAnalyzer.createAndAddOccurrenceAnalyzer(OccurrenceType.GENE, SubType.HUMAN).setMaxCount(30);
		entityAnalyzer.createAndAddOccurrenceAnalyzer("auxin");
		entityAnalyzer.writeCSVFiles();
		
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.createAllCooccurrences();

	}
	
	@Test
	public void testMakeCrystalSuppDataHTTP() {
		boolean skipCleanCopy = false;
		boolean skipSVG = true;
		boolean skipHtml = false;
		boolean skipRun = false;

		List<String> urlSList = Arrays.asList(new String[] {
		"https://pubs.acs.org/doi/suppl/10.1021/om049188b/suppl_file/om049188bsi20041020_053156.pdf",
//			-- born-digital, contains crystal info w/o coordinates.
		"https://pubs.acs.org/doi/suppl/10.1021/om049188b/suppl_file/om049188bsi20050104_114539.pdf",
//			-- born-digital, contains crystal info, including selectable CIF.
		"https://pubs.acs.org/doi/suppl/10.1021/om040132r/suppl_file/om040132r_s.pdf",
//			-- scanned, contains crystal info, including coordinates.
		"https://pubs.acs.org/doi/suppl/10.1021/om0489711/suppl_file/om0489711si20041230_042826.pdf",
//			-- born-digital, contains crystal info, including selectable coordinates.
		"https://pubs.acs.org/doi/suppl/10.1021/om040128f/suppl_file/om040128f_s.pdf",
//			-- scanned, poor quality, contains crystal info, including coordinates.
		});

		File sourceDir = null;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, "andrius");
		CProject cProject = CProject.makeProjectFromURLs(targetDir, urlSList, 
				CProject.HTTP_ACS_SUPPDATA);
		if (1==1)return;
		cProject.convertPDFOutputSVGFilesImageFiles();
		cProject.convertPSVGandWriteHtml();
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " search(crystal)"
		+ " search(country)"
		+ " search(funders)"

	    ;
		File projectDir = targetDir;
		try {
			CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
		} catch (IOException e) {
			throw new RuntimeException("Cannot run command: "+cmd, e);
		}
		
		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(projectDir);
		
		entityAnalyzer.createAndAddOccurrenceAnalyzer(OccurrenceType.BINOMIAL).setMaxCount(25);
		entityAnalyzer.createAndAddOccurrenceAnalyzer(OccurrenceType.GENE, SubType.HUMAN).setMaxCount(30);
		entityAnalyzer.createAndAddOccurrenceAnalyzer("auxin");
		entityAnalyzer.writeCSVFiles();
		
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.createAllCooccurrences();

		
	}
	
	@Test
	public void testMakeCrystalSuppDataACS() {

		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR, "acsSupp");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, "acsSupp");
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		amiProcessor.convertPDFOutputSVGFilesImageFiles();
		amiProcessor.convertPSVGandWriteHtml();
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " search(crystal)"
		+ " search(country)"
		+ " search(funders)"
		;
		amiProcessor.run(cmd);
		List<String> facetList = Arrays.asList(new String[]{"crystal", "country", "funders"});
		amiProcessor.defaultAnalyzeCooccurrence(facetList);

		
	}
	
	@Test
	/** over an hour
	 * 
	 */
	public void testPicocliStefan() {
		File sourceDir = new File(AMIFixtures.PMR_STEFAN_DIR,  "journals2a");
		LOG.debug(sourceDir.getAbsolutePath());
//		new AMIMakeProjectTool().runCommands(" -p " + sourceDir + " --rawfiletypes pdf ");
//		new AMIPDFTool().runCommands(" -p " + sourceDir);
//		new AMIGrobidTool().runCommands(" -p " + sourceDir);
		int threshold = 180;
//		String treename = "c8ob00452h1";
        String treename = "c8ob01231h1"/*, "c8ob01328d"*/; 
		String cmd0 = " -p  "+ sourceDir;
//		cmd0 = " -t "+ sourceDir+"/"+treename;
//		new AMIImageTool().runCommands(cmd0 +" --monochrome monochrome --small small --duplicate duplicate --sharpen sharpen4 --threshold "+threshold);
		treename = "c8ob00755a1";
		new AMIOCRTool().runCommands(" -t " + sourceDir + "/" + treename + " --html true ");
		
	}
	
	@Test
	// LONG
	public void testMakeCrystalSuppDataRSC() {

		String projectName = "rscSupp";
//		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR,  "stefan/journals2");
		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR,  "rscSupp");
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "country", "funders", "nmrspectroscopy", "compchem", "nmrspectroscopy", "solvents"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}

	@Test
	public void testRSCMain() {

		String projectName = "rscMain";
		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR, projectName);
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
//		amiProcessor.setIncludeCTrees("c7ob02709e");
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "country", "funders", "elements", "magnetism", "compchem", "nmrspectroscopy"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}


	@Test
	// LONG
	public void testArxivFerroelectric() {

		String projectName = "ferroelectric";
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR,  "ferroelectric/arxiv20180902");
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
//		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
//		CProject cProject = new CProject(targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(sourceDir);
//        amiProcessor.setSkipConvertPDFs(true);		
		List<String> facetList = Arrays.asList(new String[]{"elements", "crystal", "country", "magnetism", "compchem", "funders"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}
	
	@Test 
	public void testCommandLine() {
		
//		AMIProcessor.main(new String[]{});
//		AMIProcessor.main(new String[]{"help"});
//		AMIProcessor.main(new String[]{"help", "insecticide", "auxin", "grot", "inn"});
		AMIProcessor.main(new String[]{"help", "dictionaries"});
		
//		AMIProcessor.main(new String[]{"marchantia"});
//		AMIProcessor.main(new String[]{"marchantia", "country", "plantParts"});
	}

	@Test
	// not sure these are full papers
	public void testScieloBotBras() {

		String projectName = "actabotbras";
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, "actabotbras/html");
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{
				"species", "country", "funders", "gene", "plantparts", "insecticide"});
		amiProcessor.convertHTMLsToProjectAndRunCooccurrence(facetList);
	}

	@Test
	@Ignore // too long
	public void testACSOpen() {

		String projectName = "acsopen";
		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR, projectName);
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "country", "magnetism", "compchem", "nmrspectroscopy", "funders", "solvents"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}
	
	@Test
	@Ignore
	public void testACSOpenMain() {
		String projectName = "acsopenmain";
		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR, projectName);
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "country", "magnetism", "compchem", "nmrspectroscopy", "funders", "solvents"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}

	@Test
	@Ignore
	public void testACSOpenSmall() {

		String projectName = "acsopensmall";
		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR, projectName);
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "country", "magnetism", "compchem", "nmrspectroscopy", "funders", "solvents"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}

	@Test
	public void testACSOpenProblems() {

		String projectName = "acsopenproblems";
		File sourceDir = new File(AMIFixtures.TEST_TOTAL_INT_DIR, projectName);
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "country", "magnetism", "compchem", "nmrspectroscopy", "funders", "solvents"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}

	@Test
	public void testSutherland() {

		String projectName = "sutherland";
		List<String> facetList = Arrays.asList(new String[]{
				"species", "country", "funders"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, "sutherland/group");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisPDF(facetList, sourceDir, targetDir);
	}

	@Test
	public void testSutherlandBirds() {

		String projectName = "sutherland/birdConservationSmall";
		List<String> facetList = Arrays.asList(new String[]{
				"species",
				"country",
				"funders",
				"wildlife",
//				"wetlands"
				});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisXML(facetList, sourceDir, targetDir);
	}

	@Test
	public void testThesis() {

		String projectName = "oatd";
		List<String> facetList = Arrays.asList(new String[]{
				"species", "gene", "plantparts", "country", "auxin", "plantDevelopment", "insecticide", "elements",
				"funders", "invasive", "phytochemicals2"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, "thesis/oatd/pdf/");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisPDF(facetList, sourceDir, targetDir);
	}

	@Test
	// horrible - think it has characters as images
	public void testThesis1() {

		String projectName = "andrius";
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "elements", "compchem", "country", "distributions", "funders"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, "thesis/andrius/");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisPDF(facetList, sourceDir, targetDir);
	}

	@Test
	public void testThesisMake() {

		// Assumes target left from previous!
		String projectName = "oatd";
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, "thesis/"+projectName);
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
//		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{
				"species", "gene", "plantparts", "country", "auxin", "plantDevelopment", "insecticides", "elements",
				"funders", "invasive", "phytochemicals2"});
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}
	
	@Test
	public void testInstruments() {
		String projectName = "instruments";
		List<String> facetList = Arrays.asList(new String[]{
				"elements", "drugs", "country", "instrumentManufacturer"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, "instruments/all/");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisXML(facetList, sourceDir, targetDir);

	}

	@Test
	public void testVilniusWorkshop() {

		String projectName = "vilnius/smallmolecule";
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "elements", "compchem", "country", "nmrspectroscopy", "inn", "solvents", "funders"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
//        File targetDir = sourceDir;
		completeAnalysisXML(facetList, sourceDir, targetDir);
	}

	@Test
	public void testVilnius200() {

		String projectName = "vilnius/materials200COD";
		List<String> facetList = Arrays.asList(new String[]{
				"crystal", "elements", "compchem", "country", "nmrspectroscopy", "inn", "solvents", "funders"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
//        File targetDir = sourceDir;
		completeAnalysisPDF(facetList, sourceDir, targetDir);
	}

	@Test
	public void testLantana() {

		String projectName = "gita/lantana";
		List<String> facetList = Arrays.asList(new String[]{
				"species", "gene", "invasive", "country", "funders", "phytochemicals2", "plantparts", "monoterpene"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName+"/india");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName+"/india1");
		completeAnalysisXML(facetList, sourceDir, targetDir);
	}

	@Test
	public void testLantanaSmall() {

		String projectName = "gita/lantana";
		List<String> facetList = Arrays.asList(new String[]{
				"species", "gene", "invasive", "country", "funders", "phytochemicals2", "plantparts", "monoterpene"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName+"/indiasmall");
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName+"/indiasmall1");
		completeAnalysisXML(facetList, sourceDir, targetDir);
	}

	@Test
	public void testCrispr() {

		String projectName = "vilnius/crispr";
		List<String> facetList = Arrays.asList(new String[]{
				"species", "gene", "disease", "country", "funders", "crispr", "inn", });
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName+"1");
		completeAnalysisXML(facetList, sourceDir, targetDir);
	}

	@Test
	public void testCrisprPDF() {

		String projectName = "vilnius/crisprpdf/small/";
		List<String> facetList = Arrays.asList(new String[]{
				"species", "gene", "disease", "funders", "crispr" });
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisPDF(facetList, sourceDir, targetDir);
	}

	@Test
	public void testTPS() {

		String projectName = "gita/terpenesynthase/";
//		String projectName = "gita/lantana/indiasmall/";
		List<String> facetList = Arrays.asList(new String[]{
				 "phytochemicals2", "tps", "country", "monoterpene", "sesquiterpene", "diterpene", "triterpene", "species", "gene"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisXML(facetList, sourceDir, targetDir);
	}
	
	@Test
	public void testChlamy() {

		String projectName = "gita/chlamy/";
//		String projectName = "gita/lantana/indiasmall/";
		List<String> facetList = Arrays.asList(new String[]{
				 "country", "photosynth", "plantparts", "species", "gene"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisXML(facetList, sourceDir, targetDir);
	}
	
	@Test
	public void testDirectoryFormation() {

		String projectName = "gita/lantana/indiaverysmall/";
		List<String> facetList = Arrays.asList(new String[]{
				 "country", "photosynth", "plantparts", "species", "gene"});
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR, projectName);
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		completeAnalysisXML(facetList, sourceDir, sourceDir);
	}
	

	// ==================================================================
	
	private void completeAnalysisPDF(List<String> facetList, File sourceDir, File targetDir) {
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.convertPDFsToProjectAndRunCooccurrence(facetList);
	}

	private void completeAnalysisXML(List<String> facetList, File sourceDir, File targetDir) {
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		if (!sourceDir.equals(targetDir)) {
			CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		}
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.convertJATSXMLandWriteHtml();
		amiProcessor.convertHTMLsToProjectAndRunCooccurrence(facetList);
	}



}
