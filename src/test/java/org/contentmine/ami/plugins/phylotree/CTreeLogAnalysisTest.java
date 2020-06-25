package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.phylotree.SpeciesAnalyzer;
import org.contentmine.cproject.args.log.CMineLog;

/** analysis of CTreeLog output which might later go in argProcessor.summary actions.
 * 
 * @author pm286
 *
 */
@Ignore("long and uses foreign directories")
public class CTreeLogAnalysisTest {

	public static final Logger LOG = LogManager.getLogger(CTreeLogAnalysisTest.class);
public final static File BATCH1 = new File(AMIFixtures.TEST_PHYLO_DIR, "batch1/");
	public final static File IJSEM = new File(AMIFixtures.TEST_PHYLO_DIR, "phylotree");
	
	@Test
	public void testReadLog() {
		File dir0 = BATCH1;
		int count = 185;
		File logFile = new File("target/phylo/"+CMineLog.CMINE_LOG);
		summarizeInLog(dir0, count, logFile);
	}

	@Test
	public void testReadLogAB500() {
		summarizeInLog(new File(IJSEM, "500A"), 502);
		summarizeInLog(new File(IJSEM, "500B"), 502);
		summarizeInLog(new File(IJSEM, "500C"), 502);
		summarizeInLog(new File(IJSEM, "500D"), 502);
		summarizeInLog(new File(IJSEM, "500E"), 502);
		summarizeInLog(new File(IJSEM, "500F"), 502);
		summarizeInLog(new File(IJSEM, "500G"), 502);
		summarizeInLog(new File(IJSEM, "500H"), 502);
		summarizeInLog(new File(IJSEM, "500J"), 502);
	}
	
	@Test
	public void testExtractSpecies() throws IOException {
		SpeciesAnalyzer speciesAnalyzer = new SpeciesAnalyzer();
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500A"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500B"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500C"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500D"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500E"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500F"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500G"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "500H"));
		speciesAnalyzer.extractAndAddSpeciesFromDirectory(new File(IJSEM, "336J"));
		speciesAnalyzer.analyzeTrees();
		speciesAnalyzer.writeGenusByValues(new File(IJSEM, "genusByValue.txt"));
		speciesAnalyzer.writeGenusByCount(new File(IJSEM, "genusByCount.txt"));
		speciesAnalyzer.writeSpeciesByValues(new File(IJSEM, "speciesByValue.txt"));
		speciesAnalyzer.lookupWikidataSpeciesByCount(new File(IJSEM, "wikidata.txt"));
		Map<String, String> wikiBySpecies = speciesAnalyzer.getWikidataBySpecies();
	}

	// ===========================================
	

	private void summarizeInLog(File dir0, int count) {
		File logFile = new File(dir0, CMineLog.CMINE_LOG);
		summarizeInLog(dir0, count, logFile);
	}

	private void summarizeInLog(File dir0, int count, File logFile) {
		Assert.assertTrue(dir0.exists() && dir0.isDirectory());
		File[] files = dir0.listFiles();
		CMineLog cMineLog = new CMineLog(logFile);
		Assert.assertEquals(String.valueOf(dir0), count, files.length);
		cMineLog.summarizeInLog(files);
	}
}
