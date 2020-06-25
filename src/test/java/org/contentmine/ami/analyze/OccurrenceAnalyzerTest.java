package org.contentmine.ami.analyze;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.EntityAnalyzer;
import org.contentmine.ami.plugins.OccurrenceAnalyzer;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.OccurrenceType;
import org.contentmine.ami.plugins.OccurrenceAnalyzer.SubType;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.norma.Norma;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class OccurrenceAnalyzerTest {
	private static final Logger LOG = LogManager.getLogger(OccurrenceAnalyzerTest.class);
List<String> OBESITY_DICTIONARIES = Arrays.asList(
			"word", "gene", "country", "disease", "funders", "inn", "obesity", "poverty");

	@Test
	public void  testCUCSmall() throws IOException {

		boolean runme = false;
		String fileroot = "cucSmall";
		File rawDir = new File(AMIFixtures.TEST_PLANT_DIR, fileroot);
		File projectDir = new File(AMIFixtures.TARGET_PLANT_DIR, fileroot);
		if (runme) {
			CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
			LOG.debug("copied raw");
		}
		if (runme) {
			String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project "+projectDir;
			new Norma().run(args);
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
				
		OccurrenceAnalyzer speciesAnalyzer = entityAnalyzer.createAndAddOccurrenceAnalyzer(OccurrenceType.BINOMIAL)
				.setMaxCount(20);
		File binomialCsvFile = speciesAnalyzer.createFileByType(CTree.CSV);
		FileUtils.deleteQuietly(binomialCsvFile);
		speciesAnalyzer.writeCSV();
		Assert.assertTrue(binomialCsvFile+" exists", binomialCsvFile.exists());

		OccurrenceAnalyzer geneAnalyzer = entityAnalyzer.createAndAddOccurrenceAnalyzer(OccurrenceType.GENE, SubType.HUMAN)
				.setMaxCount(12);
		File geneCsvFile = geneAnalyzer.createFileByType(CTree.CSV);
		FileUtils.deleteQuietly(geneCsvFile);
		geneAnalyzer.writeCSV();
		Assert.assertTrue(geneCsvFile+" exists", geneCsvFile.exists());

		OccurrenceAnalyzer auxinAnalyzer = entityAnalyzer.createAndAddOccurrenceAnalyzer("auxin").setMaxCount(6);
		File auxinCsvFile = auxinAnalyzer.createFileByType(CTree.CSV);
		FileUtils.deleteQuietly(auxinCsvFile);
		auxinAnalyzer.writeCSV();
		Assert.assertTrue(auxinCsvFile+" exists", auxinCsvFile.exists());

		// ====================
		
		
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.createAllCooccurrences();
							
	}
	
	@Test
	public void  testCUCEPMC() throws IOException {
		String fileroot = "cuc";
		boolean copyToTarget = false;
		File inputDir = AMIFixtures.TEST_PLANT_DIR;
		File rawDir = new File(inputDir, fileroot);
		File outputDir =  AMIFixtures.TARGET_PLANT_DIR;
		File projectDir = new File(outputDir, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(projectDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(false);
		entityAnalyzer.analyzePlantCoocurrences();
	}
	
	@Test
	@Ignore
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
	@Ignore
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
	@Ignore
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
	@Ignore 
	public void  testZika() throws IOException {
		String fileroot = "zika2018";
		boolean copyToTarget = false;
		File inputDir = new File(AMIFixtures.TEST_PROJECTS_DIR, fileroot);
		File outputDir =  new File(AMIFixtures.TARGET_PROJECTS_DIR, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(inputDir, outputDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(outputDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(false);
		entityAnalyzer.analyzeMosquitoCoocurrences();
	}
	
	@Test
	@Ignore // too long
	public void  testZikaSmall() throws IOException {
		String fileroot = "zikaSmall";
		boolean copyToTarget = true;
		File sourceDir = new File(AMIFixtures.TEST_PROJECTS_DIR, fileroot);
		if (!TestUtil.checkForeignDirExists(sourceDir)) return;
		File targetDir =  new File(AMIFixtures.TARGET_PROJECTS_DIR, fileroot);
		if (copyToTarget) {
			CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
			LOG.debug("copied raw");
		}

		EntityAnalyzer entityAnalyzer = EntityAnalyzer.createEntityAnalyzer(targetDir);
		entityAnalyzer.setWriteCSV(true);
		entityAnalyzer.setForceRun(true);
		entityAnalyzer.analyzeMosquitoCoocurrences();
	}
}
