package org.contentmine.ami.analyze;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.AMIProcessor;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.EntityAnalyzer;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.OccurrenceType;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.SubType;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.junit.Ignore;
import org.junit.Test;

// @Ignore // uncomment for development
public class OccurrenceAnalyzerIT {
	private static final Logger LOG = LogManager.getLogger(OccurrenceAnalyzerIT.class);
List<String> OBESITY_DICTIONARIES = Arrays.asList(
			"word", "gene", "country", "disease", "funders", "inn", "obesity", "poverty");

	/** complete AMI run over full Marchantia set.
	 * NOT in Github
	 * @throws IOException
	 */
	@Test
	@Ignore // untill we test
	public void  testMarchantiaEPMC() throws IOException {
		
		boolean runme = true;
		File JUPYTER_DIR = new File("/Users/pm286/workspace/jupyter/demos/");
		File TARGET_JUPYTER_DIR = new File("target/jupyter/demos/");
		String fileroot = "marchantia";
		File rawDir = new File(JUPYTER_DIR, fileroot);
		File projectDir = new File(TARGET_JUPYTER_DIR, fileroot);
		if (runme) {
			CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		}
		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
		+ " species(binomial)"
		+ " gene(human) "
		+ " search(auxin)"
		+ " search(plantDevelopment)"
		+ " search(pectin)"
		+ " search(plantparts)"
		+ " search(synbio)"

	    ;
		if (runme) {
			CommandProcessor.main((projectDir+" "+cmd).split("\\s+"));
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
	public void  testObesity() throws IOException {
		String fileroot = "obesity";
		boolean copyToTarget = true;
		boolean forceRun = true;
		File inputDir = new File(new File(AMIFixtures.TEST_PROJECTS_DIR, fileroot), fileroot); // nested
		File outputDir =  new File(AMIFixtures.TARGET_PROJECTS_DIR, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(inputDir, outputDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(outputDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(forceRun);
		entityAnalyzer.analyzeCoocurrences(OBESITY_DICTIONARIES);
	}
	
	@Test
	@Ignore // too long
	public void  testObesityLarge() throws IOException {
		String fileroot = "obesityLarge";
		boolean copyToTarget = true;
		boolean forceRun = true;
		File inputDir = new File(new File(AMIFixtures.TEST_PROJECTS_DIR, "obesity"), fileroot); // nested
		File outputDir =  new File(AMIFixtures.TARGET_PROJECTS_DIR, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(inputDir, outputDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(outputDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(forceRun);
		entityAnalyzer.analyzeCoocurrences(OBESITY_DICTIONARIES);
	}
	@Test
	public void  testObesityPoverty() throws IOException {
		String fileroot = "obesityPoverty";
		boolean copyToTarget = true;
		boolean forceRun = true;
		File inputDir = new File(new File(AMIFixtures.TEST_PROJECTS_DIR, "obesity"), fileroot); // nested
		File outputDir =  new File(AMIFixtures.TARGET_PROJECTS_DIR, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(inputDir, outputDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(outputDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(forceRun);
		entityAnalyzer.analyzeCoocurrences(OBESITY_DICTIONARIES);

	}
	@Test
	public void  testObesityEcuador() throws IOException {
		String fileroot = "ecuador";
		boolean copyToTarget = true;
		boolean forceRun = true;
		File inputDir = new File(new File(AMIFixtures.TEST_PROJECTS_DIR, "obesity"), fileroot); // nested
		File outputDir =  new File(AMIFixtures.TARGET_PROJECTS_DIR, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(inputDir, outputDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(outputDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(forceRun);
		entityAnalyzer.analyzeCoocurrences(OBESITY_DICTIONARIES);

	}
	@Test
	public void  testObesityRefugee() throws IOException {
		String fileroot = "obesityRefugee";
		boolean copyToTarget = true;
		boolean forceRun = true;
		File inputDir = new File(new File(AMIFixtures.TEST_PROJECTS_DIR, "obesity"), fileroot); // nested
		File outputDir =  new File(AMIFixtures.TARGET_PROJECTS_DIR, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(inputDir, outputDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(outputDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(forceRun);
		entityAnalyzer.analyzeCoocurrences(OBESITY_DICTIONARIES);
	}

	@Test
	public void testWAFlavivirus() {
		String projectName = "waFlavi";
		File sourceDir = new File(AMIFixtures.PMR_PROJECT_DIR,  "zikawa/flaviviruswa");
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir = new File(AMIFixtures.TARGET_TOTAL_INT_DIR, projectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(targetDir);
		amiProcessor.makeProject();
		List<String> facetList = Arrays.asList(new String[]{"species", "insecticide", "country", "funders", "drugs", "tropicalVirus"});
		amiProcessor.runSearchesAndCooccurrence(facetList);
	}
}
