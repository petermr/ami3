package org.contentmine.ami.dictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectTabColumn;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Test;

public class CMJsonDictionaryTest {

	private static final Logger LOG = LogManager.getLogger(CMJsonDictionaryTest.class);
@Test
	public void testCreateJsonDictionary() {
		CMJsonDictionary cmJsonDictionary = new CMJsonDictionary();
		cmJsonDictionary.setId("dictid");
		CMJsonTerm term = new CMJsonTerm(cmJsonDictionary);
		term.addCMIdentifier("CM.temp1");
		term.addIdentifier("foo", "bar");
		term.addIdentifier("fooze", "baz");
		term.addName("myname");
		term.addTerm("myterm");
		cmJsonDictionary.addTerm(term);
		term = new CMJsonTerm(cmJsonDictionary);
		term.addCMIdentifier("CM.temp2");
		term.addIdentifier("foox", "barx");
		term.addIdentifier("foozle", "bazz");
		term.addName("mynamexx");
		term.addTerm("mytermxx");
		cmJsonDictionary.addTerm(term);
		LOG.debug(">"+cmJsonDictionary);
	}
	
	@Test
	public void testDuplicateContentMineID() {
		CMJsonDictionary cmJsonDictionary = new CMJsonDictionary();
		cmJsonDictionary.setId("dictid");
		CMJsonTerm term = new CMJsonTerm(cmJsonDictionary);
		term.addCMIdentifier("CM.temp1");
		try {
			term.addCMIdentifier("CM.temp2");
			throw new RuntimeException("Cannot add two CM identifiers");
		} catch (Exception e) {
			// OK
		}
	}
	
	@Test
	public void testMissingContentMineID() {
		CMJsonDictionary cmJsonDictionary = new CMJsonDictionary();
		cmJsonDictionary.setId("dictid");
		CMJsonTerm term = new CMJsonTerm(cmJsonDictionary);
		try {
			cmJsonDictionary.addTerm(term);
			throw new RuntimeException("term must have ID");
		} catch (Exception e) {
			// OK
		}
	}
	
	@Test
	public void testMissingName() {
		CMJsonDictionary cmJsonDictionary = new CMJsonDictionary();
		cmJsonDictionary.setId("dictid");
		CMJsonTerm term = new CMJsonTerm(cmJsonDictionary);
		term.addCMIdentifier("CM.temp1");
		try {
			cmJsonDictionary.addTerm(term);
			throw new RuntimeException("term must have Name");
		} catch (Exception e) {
			// OK
		}
	}
	
	@Test
	public void testMissingTerm() {
		CMJsonDictionary cmJsonDictionary = new CMJsonDictionary();
		cmJsonDictionary.setId("dictid");
		CMJsonTerm term = new CMJsonTerm(cmJsonDictionary);
		term.addCMIdentifier("CM.temp1");
		term.addName("myname");
		try {
			cmJsonDictionary.addTerm(term);
			throw new RuntimeException("term must have Term");
		} catch (Exception e) {
			// OK
		}
	}
	
	@Test
	public void convertXMLToJson() {
		File xmlFile = new File(AMIFixtures.TEST_DICTIONARY_DIR, "cochrane.xml");
		DefaultAMIDictionary xmlDict = DefaultAMIDictionary.createSortedDictionary(xmlFile);
		CMJsonDictionary jsonDictionary = CMJsonDictionary.convertXMLToJson(xmlDict);
		Assert.assertEquals(
				"{\"id\":\"CM.cochrane\",\"entries\":["
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane0\"},\"name\":\"cochrane library\",\"term\":\"Cochrane Library\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane1\"},\"name\":\"cochrane reviews\",\"term\":\"Cochrane Reviews\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane2\"},\"name\":\"Cochrane Central Register of Controlled Trials\",\"term\":\"Cochrane Central Register of Controlled Trials\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane3\"},\"name\":\"cochrane\",\"term\":\"Cochrane\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane4\"},\"name\":\"randomize\",\"term\":\"randomize\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane5\"},\"name\":\"meta-analysis\",\"term\":\"meta-analysis\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane6\"},\"name\":\"embase\",\"term\":\"Embase\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane7\"},\"name\":\"medline\",\"term\":\"MEDLINE\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane8\"},\"name\":\"eligibility\",\"term\":\"eligibility\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane9\"},\"name\":\"exclusion\",\"term\":\"exclusion\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane10\"},\"name\":\"outcome\",\"term\":\"outcome\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane11\"},\"name\":\"Review Manager\",\"term\":\"Review Manager\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane12\"},\"name\":\"Eggers\",\"term\":\"Eggers\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane13\"},\"name\":\"Stata\",\"term\":\"Stata\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane14\"},\"name\":\"STATA\",\"term\":\"STATA\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane15\"},\"name\":\"RCT\",\"term\":\"RCT\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane16\"},\"name\":\"pCR\",\"term\":\"pCR\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane17\"},\"name\":\"adverse events\",\"term\":\"adverse events\"}"
				+ "]}",
				jsonDictionary.toString());
	}
	
	@Test
	public void convertJsonToXML() throws IOException {
		File jsonFile = new File(AMIFixtures.TEST_DICTIONARY_DIR, "cochrane.json");
//		CMJsonDictionary cmJsonDictionary = CMJsonDictionary.readJsonDictionary(FileUtils.readFileToString(jsonFile, CMineUtil.UTF8_CHARSET));
		CMJsonDictionary cmJsonDictionary = CMJsonDictionary.readJsonDictionary(
				IOUtils.toString(new FileInputStream(jsonFile), CMineUtil.UTF8_CHARSET));
		Assert.assertNotNull("null jsonDictionary", cmJsonDictionary);
		DefaultAMIDictionary amiDictionary = CMJsonDictionary.convertJsonToXML(cmJsonDictionary);
		Assert.assertNotNull("null amiDictionary", amiDictionary);
		Assert.assertEquals("ami",  
				"<dictionary id=\"CM.cochrane\" name=\"cochrane\">"
				+ "<entry id=\"CM.cochrane0\" name=\"cochrane library\" term=\"Cochrane Library\" />"
				+ "<entry id=\"CM.cochrane1\" name=\"cochrane reviews\" term=\"Cochrane Reviews\" />"
				+ "<entry id=\"CM.cochrane2\" name=\"Cochrane Central Register of Controlled Trials\" term=\"Cochrane Central Register of Controlled Trials\" />"
				+ "<entry id=\"CM.cochrane3\" name=\"cochrane\" term=\"Cochrane\" />"
				+ "<entry id=\"CM.cochrane4\" name=\"randomize\" term=\"randomize\" />"
				+ "<entry id=\"CM.cochrane5\" name=\"meta-analysis\" term=\"meta-analysis\" />"
				+ "<entry id=\"CM.cochrane6\" name=\"embase\" term=\"Embase\" /><entry id=\"CM.cochrane7\" name=\"medline\" term=\"MEDLINE\" />"
				+ "<entry id=\"CM.cochrane8\" name=\"eligibility\" term=\"eligibility\" />"
				+ "<entry id=\"CM.cochrane9\" name=\"exclusion\" term=\"exclusion\" />"
				+ "<entry id=\"CM.cochrane10\" name=\"outcome\" term=\"outcome\" />"
				+ "<entry id=\"CM.cochrane11\" name=\"Review Manager\" term=\"Review Manager\" />"
				+ "<entry id=\"CM.cochrane12\" name=\"Eggers\" term=\"Eggers\" />"
				+ "<entry id=\"CM.cochrane13\" name=\"Stata\" term=\"Stata\" />"
				+ "<entry id=\"CM.cochrane14\" name=\"STATA\" term=\"STATA\" />"
				+ "<entry id=\"CM.cochrane15\" name=\"RCT\" term=\"RCT\" />"
				+ "<entry id=\"CM.cochrane16\" name=\"pCR\" term=\"pCR\" />"
				+ "<entry id=\"CM.cochrane17\" name=\"adverse events\" term=\"adverse events\" />"
				+ "</dictionary>",
				amiDictionary.toXML());
	}
	
	@Test
	public void testReadMixMatch() throws IOException {
		boolean useHeader = true;
		File mapping = new File(AMIFixtures.TEST_DICTIONARY_DIR, "mixmatch.tsv");
		RectangularTable table = RectangularTable.readCSVTable(mapping, useHeader);
		Assert.assertEquals("size", 60174, table.size());
		Assert.assertEquals("[ext_id, q]", table.getHeader().toString());
		RectTabColumn ids = table.getColumn("ext_id");
		Assert.assertEquals("ext", "["
				+ "CM.cochrane13,"
				+ " CM.cochrane14,"
				+ " CM.cochrane17,"
				+ " CM.cochrane5,"
				+ " CM.cochrane6,"
				+ " CM.cochrane8,"
				+ " CM.cochrane9,"
				+ " CM.disease0,"
				+ " CM.disease1,"
				+ " CM.disease10,"
				+ " CM.disease100,"
				+ " CM.disease1000,"
				+ " CM.disease1002,",
				    ids.toString().substring(0, 188));
		RectTabColumn qs = table.getColumn("q");
		Assert.assertEquals("q", "["
				+ "1204300,"
				+ " 1204300,"
				+ " 4686716,"
				+ " 815382,"
				+ " 5323355,"
				+ " 3587788,"
				+ " 2445801,"
				+ " 4557543,"
				+ " 3297103,"
				+ " 2256736,"
				+ " 317158", qs.toString().substring(0, 96));
		File jsonFile = new File(AMIFixtures.TEST_DICTIONARY_DIR, "cochrane.json");
		CMJsonDictionary cmJsonDictionary = CMJsonDictionary.readJsonDictionary(FileUtils.readFileToString(jsonFile, CMineUtil.UTF8_CHARSET));
		Assert.assertNotNull("null jsonDictionary", cmJsonDictionary);

		
	}

	@Test
	public void testAddMixMatchIds() throws IOException {
		boolean useHeader = true;
		File mapping = new File(AMIFixtures.TEST_DICTIONARY_DIR, "mixmatch.tsv");
		RectangularTable table = RectangularTable.readCSVTable(mapping, useHeader);
		File jsonFile = new File(AMIFixtures.TEST_DICTIONARY_DIR, "cochrane.json");
		CMJsonDictionary cmJsonDictionary = CMJsonDictionary.readJsonDictionary(FileUtils.readFileToString(jsonFile, CMineUtil.UTF8_CHARSET));
		Assert.assertNotNull("null jsonDictionary", cmJsonDictionary);
		cmJsonDictionary.addMixMatchIds(table);
		Assert.assertEquals("wikidata", 
				"{\"id\":\"CM.cochrane\",\"entries\":["
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane0\"},\"term\":\"Cochrane Library\",\"name\":\"cochrane library\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane1\"},\"term\":\"Cochrane Reviews\",\"name\":\"cochrane reviews\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane2\"},\"term\":\"Cochrane Central Register of Controlled Trials\",\"name\":\"Cochrane Central Register of Controlled Trials\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane3\"},\"term\":\"Cochrane\",\"name\":\"cochrane\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane4\"},\"term\":\"randomize\",\"name\":\"randomize\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane5\",\"wikidata\":\"Q815382\"},\"term\":\"meta-analysis\",\"name\":\"meta-analysis\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane6\",\"wikidata\":\"Q5323355\"},\"term\":\"Embase\",\"name\":\"embase\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane7\"},\"term\":\"MEDLINE\",\"name\":\"medline\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane8\",\"wikidata\":\"Q3587788\"},\"term\":\"eligibility\",\"name\":\"eligibility\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane9\",\"wikidata\":\"Q2445801\"},\"term\":\"exclusion\",\"name\":\"exclusion\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane10\"},\"term\":\"outcome\",\"name\":\"outcome\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane11\"},\"term\":\"Review Manager\",\"name\":\"Review Manager\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane12\"},\"term\":\"Eggers\",\"name\":\"Eggers\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane13\",\"wikidata\":\"Q1204300\"},\"term\":\"Stata\",\"name\":\"Stata\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane14\",\"wikidata\":\"Q1204300\"},\"term\":\"STATA\",\"name\":\"STATA\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane15\"},\"term\":\"RCT\",\"name\":\"RCT\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane16\"},\"term\":\"pCR\",\"name\":\"pCR\"},"
				+ "{\"identifiers\":{\"contentmine\":\"CM.cochrane17\",\"wikidata\":\"Q4686716\"},\"term\":\"adverse events\",\"name\":\"adverse events\"}],"
				+ "\"name\":\"cochrane\"}",
				cmJsonDictionary.toString());
		
	}
	

}
