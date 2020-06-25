package org.contentmine.ami;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.ResultsAnalysisImpl;
import org.contentmine.ami.plugins.gene.GeneArgProcessor;
import org.contentmine.ami.plugins.identifier.IdentifierArgProcessor;
import org.contentmine.ami.plugins.regex.RegexArgProcessor;
import org.contentmine.ami.plugins.species.SpeciesArgProcessor;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.DataTablesTool;
import org.contentmine.cproject.util.ResultsAnalysis.SummaryType;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.norma.NAConstants;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore
public class TutorialTest {

	;
	private static final Logger LOG = LogManager.getLogger(TutorialTest.class);
@Test
	@Ignore // uses net
	public void testSpeciesLookup() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/tutorial/plos10"), new File("target/specieslook10"));
		String args = "-q target/specieslook10 -i scholarly.html --sp.species --context 35 50 --sp.type binomial genus genussp --lookup wikipedia genbank";
		AMIArgProcessor speciesArgProcessor = new SpeciesArgProcessor(args);
		speciesArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(speciesArgProcessor, 3, 0, 
				"<results title=\"mend me\">"
				);
	}
	
	@Test
	// TESTED 2016-01-12
//	@Ignore // tests broken (?overwrite)

	public void testRegex() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/tutorial/plos10"), new File("target/regex10"));
		String args = "-q target/regex10/ -i scholarly.html --context 35 50 --r.regex regex/consort0.xml";
		RegexArgProcessor regexArgProcessor = new RegexArgProcessor(args);
		regexArgProcessor.runAndOutput();
		// fix me
//		AMIFixtures.checkResultsElementList(regexArgProcessor, 1, 0, 
//				"<results title=\"consort0\">"
//				+ "<result pre=\"ptococcal meningitis in Taiwan was \" name0=\"diagnose\" value0=\"diagnosed\" "
//				+ "post=\"in 1957 [ 22]. Large clinical case series on crypt\" "
//				+ "xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][7]/*[local-name()='p']["
//				);
		
		/** omit as slightly different outout.
		Fixtures.compareExpectedAndResults(new File(Fixtures.TEST_AMI_DIR, "tutorial/plos10/e0115544"), 
				new File("target/regex10/e0115544"), "regex/consort0", Fixtures.RESULTS_XML);
				*/
	}
	
	@Test
	// EMPTY result, check.
	public void testIdentifier() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/tutorial/plos10"), new File("target/ident10"));
		String args = "-q target/ident10/ -i scholarly.html --context 35 50 --id.identifier --id.regex "+NAConstants.MAIN_AMI_DIR+"/regex/identifiers.xml --id.type bio.ena";
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 1, 0, 
				"<results title=\"bio.ena\" />"
				);
		
	}
	
	@Test
	// EMPTY ?
	public void testIdentifierClin() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/tutorial/plos10"), new File("target/clin10"));
		String args = "-q target/clin10/ -i scholarly.html --context 35 50 --id.identifier --id.regex "
		    +NAConstants.MAIN_AMI_DIR+"/regex/identifiers.xml --id.type clin.nct clin.isrctn";
		IdentifierArgProcessor identifierArgProcessor = new IdentifierArgProcessor(args);
		identifierArgProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 0, 
				"<results title=\"clin.isrctn\" />"
				);
		AMIFixtures.checkResultsElementList(identifierArgProcessor, 2, 1, 
				"<results title=\"clin.nct\" />"
				);
	}
		
	@Test
//	@Ignore // tests broken (?overwrite)
	// TESTED 2016-01-12
	public void testBagOfWords() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/tutorial/plos10"), new File("target/word10"));
		String args = "-q target/word10/"
				+ " -i scholarly.html"
				+ " --context 35 50"
				+ " --w.words wordFrequencies"
				+ " --w.stopwords " + NAConstants.AMI_RESOURCE + "/plugins/word/stopwords.txt";
		AMIArgProcessor wordArgProcessor = new WordArgProcessor(args);
		wordArgProcessor.runAndOutput();
		// fix me
//		AMIFixtures.checkResultsElementList(wordArgProcessor, 1, 0, 
//				"<results title=\"frequencies\">"
//				+ "<result title=\"frequency\" word=\"(.)\" count=\"55\" />"
//				+ "<result title=\"frequency\" word=\"cryptococcal\" count=\"48\" />"
//				+ "<result title=\"frequency\" word=\"neoformans\" count=\"47\" />"
//				+ "<result title=\"frequency\" word=\"meningitis\" count=\"41\" />"
//				+ "<result title=\"frequency\" word=\"risk\" count=\"41\""
//				);
//		// can't compare these directly as output needs sorting
//		File targetE0115544 = new File("target/word10/e0115544");
//		AMIFixtures.compareExpectedAndResults(new File(NAConstants.TEST_AMI_DIR, "tutorial/plos10/e0115544"), 
//				targetE0115544, "word/frequencies", AMIFixtures.RESULTS_XML);
	}

	@Test
	// TESTED 2016-01-12
	public void testBagOfWordsNatureNano() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/nature/nnano"), new File("target/nature/nnano"));
		String args = "-q target/nature/nnano/"
				+ " -i scholarly.html"
				+ " --context 35 50"
				+ " --w.words wordFrequencies"
				+ " --w.stopwords "+NAConstants.PLUGINS_WORD+"/stopwords.txt";
		AMIArgProcessor wordArgProcessor = new WordArgProcessor(args);
		wordArgProcessor.runAndOutput();
//		AMIFixtures.checkResultsElementList(wordArgProcessor, 1, 0, 
//				"<results title=\"frequencies\">"
//				+ "<result title=\"frequency\" word=\"carbon\" count=\"77\" />"
//				+ "<result title=\"frequency\" word=\"hybrid\" count=\"61\" />"
//				+ "<result title=\"frequency\" word=\"fibre\" count=\"53\" />"
//				+ "<result title=\"frequency\" word=\"().\" count=\"51\" />"
//				+ "<result title=\"frequency\" word=\"context\" count=\"51\" />"
//				+ "<result t"
//				);
	}
	

	@Test
	// EMPTY?
//	@Ignore
	public void testGene() throws Exception {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/tutorial/plos10"), new File("target/gene10"));
		String args = "-q target/gene10/e0115544 -i scholarly.html --context 35 50 --g.gene --g.type human mouse";
		GeneArgProcessor geneArgProcessor = new GeneArgProcessor(args);
		geneArgProcessor.runAndOutput();
		// fix me
//		AMIFixtures.checkResultsElementList(geneArgProcessor, 2, 0, 
//				"<results title=\"human\"><result pre=\"the most effective model of care ( \" exact=\"DU\" post=\" \" xpath=\"/*[local-name()='html'][1]/*[local-name()='body'][1]/*[local-name()='div'][1]/*[local-name()='div'][6]/*[local-name()='div'][2]/*[local-name()='div'][3]/*[local-name()='div'][1]/*[local-name()='div']["
//				);
//		AMIFixtures.checkResultsElementList(geneArgProcessor, 2, 1, 
//				"<results title=\"mouse\" />"
//				);

		
	}
	
	@Test
	// TESTED 2016-01-12
//	@Ignore // tests broken (?overwrite)
	public void testWordFrequencies() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR+"/tutorial/plos10"), new File("target/word10a"));
			String args = "-q target/word10a/"
					+ " -i fulltext.xml"
					+ " --w.words wordFrequencies"
					+ " -o scholarly.html";
			AMIArgProcessor wordArgProcessor = new WordArgProcessor(args);
			wordArgProcessor.runAndOutput();
			AMIFixtures.checkResultsElementList(wordArgProcessor, 1, 0, 
					"<results title=\"frequencies\">"
					+ "<result title=\"frequency\" word=\"the\" count=\"140\" />"
					+ "<result title=\"frequency\" word=\"and\" count=\"104\" />"
					+ "<result title=\"frequency\" word=\"with\" count=\"59\" />"
					+ "<result title=\"frequency\" word=\"(.)\" count=\"55\" />"
					+ "<result title=\"frequency\" word=\"cryptococcal\" count=\"48\" />"
					+ "<result t"
					);
	}
	
	@Test
	/** this tutorial shows how to aggregate from results.xml files.
	 * first we run a word frequency which generates bagOfWordsWithCounts in results.xml
	 * These are then aggregated to a single wordSnippets file for each cTree.
	 * These are then aggregated to give a summary file for the cProject
	 */
//	@Ignore // too large
	public void testSummarizeCounts() throws IOException {
		/** create a clean version in target/
		 * there are 6 ctrees
		 */
		String args;
		File projectDir = new File("target/tutorial/patents/summary");
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_PATENTS_DIR, "US08979"), projectDir);
		

		/** run AMI-words to create a bagofwords as normal in ${ctree}/results/word/frequencies/results.xml
		 */ 
		args = "--project "+projectDir+" -i scholarly.html"
				+ " --w.words wordFrequencies --w.stopwords "+NAConstants.PLUGINS_WORD+"/stopwords.txt ";
		new WordArgProcessor(args).runAndOutput();
		
		/** take these results.xml files and aggregate into a single ${ctree}/wordSnippets.xml
		 * it selects all words with a count > 20
		 */
		args = "--project "+projectDir+" --filter file(**/word/**/results.xml)xpath(//result[@count>20]) -o wordSnippets.xml" ;
		new DefaultArgProcessor(args).runAndOutput(); 

		/** aggregate the ${ctree}/wordSnippets.xml to ${cproject}/wordSnippets.xml
		 * 
		 * because the wordSnippets holds its value in the "word" attribute, we use an xpath that
		 * returns the attribute. The software will then look for a sibling "count" attribute
		 * and use this. (messy, and we'll mend it)
		 * 
		 * The result is a global ${cproject}/wordCount.xml
		 * 
		 */
		args = "--project "+projectDir+" -i wordSnippets.xml --xpath //result/@word --summaryfile wordCount.xml";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor(args); 
		argProcessor.runAndOutput(); 
	}
	
	
	@Test
	/** this tutorial shows how to aggregate from results.xml files.
	 * Files are all getpapers with "anopheles" query 
	 * first we run ami-species which generates results.xml
	 * These are then aggregated to a single speciesSnippets.xml file for each cTree.
	 * These are then aggregated to give a summary speciesSnippets.xml for the cProject
	 */
	public void testSummarizeAnopheles() throws IOException {
//		/** create a clean version in target/
//		 * there are 20+ ctrees
//		 */
//		String args;
//		File projectDir = new File("target/tutorial/anopheles");
//		CMineTestFixtures.cleanAndCopyDir(new File(NAConstants.TEST_AMI_DIR, "anopheles"), projectDir);
//		
//		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
//		/** run AMI-species to create results.xml as normal in ${ctree}/results/species/binomial/results.xml
//		 */ 
//		new SpeciesArgProcessor(
//				"--project "+projectDir+" -i scholarly.html --sp.species --sp.type binomial genus ").runAndOutput();
//		commandProcessor.runSpecies("binomial genus");
//		commandProcessor.runGene("human");
//		commandProcessor.runSequence("dna");
//		new WordArgProcessor("--project "+projectDir+" -i scholarly.html --w.words wordFrequencies"
//						+ " --w.stopwords "+NAConstants.SLASH_AMI_RESOURCE+"plugins/word/stopwords.txt").runAndOutput();
//
//		/** take these results.xml files and aggregate into a single ${ctree}/wordSnippets.xml
//		 * it selects all words with a count > 20
//		 */
//		commandProcessor.runFilterResultsXMLOptions("species/binomial species/genus gene/human sequence/dna sequence/dnaprimer"); 
//		new DefaultArgProcessor("--project "+projectDir+" --filter file(**/word/frequencies/results.xml)xpath(//result[@count>20]) -o wordCount.xml").runAndOutput(); 
//
//		new DefaultArgProcessor("--project "+projectDir+" -i wordCount.xml         --xpath //result/@word  --summaryfile wordCount.xml").runAndOutput(); 
//
	}
	
	@Test
	// ADVERT
	
	/** this tutorial shows how to aggregate from results.xml files.
	 * Files are all getpapers with "Zika" query 
	 * first we run ami-species which generates results.xml
	 * These are then aggregated to a single speciesSnippets.xml file for each cTree.
	 * These are then aggregated to give a summary speciesSnippets.xml for the cProject
	 */
	public void testSummarizeZika() throws IOException {
//		/** create a clean version in target/
//		 * there are 20+ ctrees
//		 */
//		File projectDir = new File("target/tutorial/zika");
//		File rawDir = new File(NAConstants.TEST_AMI_DIR, "zika");
//		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
//		CommandProcessor commandProcessor = new CommandProcessor(rawDir);
//
//		/** run AMI-species to create results.xml as normal in ${ctree}/results/species/binomial/results.xml
//		 */ 
//		commandProcessor.runSpecies("binomial genus");
//		commandProcessor.runGene("human");
//		commandProcessor.runSequence("dna dnaprimer");
//		new WordArgProcessor("--project "+projectDir+" -i scholarly.html --w.words wordFrequencies"
//						+ " --w.stopwords "+NAConstants.PLUGINS_WORD+"/stopwords.txt").runAndOutput();
//
//		/** take these results.xml files and aggregate into a single ${ctree}/wordSnippets.xml
//		 * it selects all words with a count > 20
//		 */
//		commandProcessor.runFilterResultsXMLOptions("species/binomial species/genus gene/human sequence/dna sequence/dnaprimer"); 
//		
//		commandProcessor.runFilterResultsXML("word", "frequencies", "//result[@count>20]"); 
//		commandProcessor.runFilterResultsXML("species", "binomial", "//result[contains(@pre,'ZIKV')]", "zikvSnippets.xml"); 
//
//		/** aggregate the ${ctree}/binomialSnippets.xml to ${cproject}/binomialSnippets.xml
//		 * 
//		 * The result is a global ${cproject}/binomialSnippets.xml
//		 * 
//		 */
//		commandProcessor.runSummaryAndCountOptions("binomial genus human dna dnaprimer"); 
//		
//		new DefaultArgProcessor("--project "+projectDir+" -i frequenciesSnippets.xml --xpath //result/@word  --summaryfile frequenciesCount.xml").runAndOutput(); 
//
//		new DefaultArgProcessor("--project "+projectDir+" -i zikvSnippets.xml  --xpath //result/@match --summaryfile zikvCount.xml").runAndOutput(); 
		
	}

	private void runDefault(String project) throws IOException {
		File rawDir = new File("../projects/"+project);
		File projectDir = new File("target/tutorial/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(""
				+ "species(binomial,genus) "
				+ " gene(human)"
				+ " word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"
				+ " sequence(dnaprimer) ");
		DataTablesTool dataTablesTool = DataTablesTool.createBiblioEnabledTable();
		dataTablesTool.setTitle(project);
		ResultsAnalysisImpl resultsAnalysis = new ResultsAnalysisImpl(dataTablesTool);
		resultsAnalysis.addDefaultSnippets(projectDir);
		resultsAnalysis.setRowHeadingName("EPMCID");
		for (SummaryType cellType : ResultsAnalysisImpl.SUMMARY_TYPES) {
			resultsAnalysis.setSummaryType(cellType);
			HtmlTable table = resultsAnalysis.makeHtmlDataTable();
			HtmlHtml html = dataTablesTool.createHtmlWithDataTable(table);
			File outfile = new File(projectDir, cellType.toString()+"."+CProject.DATA_TABLES_HTML);
			XMLUtil.debug(html, outfile, 1);
		}
	}

	@Test
	@Ignore // PMR only
	public void testMicrocephaly()  throws IOException {
		String project = "microcephaly";
		File projectDir = new File("target/tutorial/"+project);
		File rawDir = new File("/Users/pm286/workspace/projects/", project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
//		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"; 
		String cmd = "sequence(dnaprimer) gene(human) "
		+ "word(search)w.search:"+NAConstants.PLUGINS_DICTIONARY_DIR+"/tropicalVirus.xml";
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(cmd);

	}

	@Test
	@Ignore // PMR only
	public void testPsychologyStats()  throws IOException {
		File rawDir = new File("./xref/"+"daily");
		String project = "20160501_0_100";
		File projectDir = new File("target/daily/"+project+"/");
		CMineTestFixtures.cleanAndCopyDir(rawDir, projectDir);
		
//		String cmd = "word(frequencies)xpath:@count>20~w.stopwords:pmcstop.txt_stopwords.txt"; 
		String cmd = "sequence(dnaprimer) gene(human) "
		+ "word(search)w.search:"+NAConstants.PLUGINS_DICTIONARY_DIR+"/statistics.xml";
		CommandProcessor commandProcessor = new CommandProcessor(projectDir);
		commandProcessor.processCommands(cmd);

	}



}
