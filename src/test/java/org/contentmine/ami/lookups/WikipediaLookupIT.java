package org.contentmine.ami.lookups;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.lookups.WikipediaLookup;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.species.SpeciesArgProcessor;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

/** I think the API is outdates so some of these fail, especially for species.
 * 
 * @author pm286
 *
 */
// @Ignore // unless testing Lookup
public class WikipediaLookupIT {

	
	public static final Logger LOG = Logger.getLogger(WikipediaLookupIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	

	@Test
//	@Ignore // LONG
	public void testGetWikidataForDictionariesAndUpdate() throws Exception {
		// /normami/src/main/resources/org/contentmine/ami/plugins/dictionary/invasive.xml
		DefaultAMIDictionary dictionary = new DefaultAMIDictionary();
		dictionary.setDictionaryName("invasive");
		dictionary.setInputDir(NAConstants.PLUGINS_DICTIONARY_DIR);
		dictionary.setOutputDir(NAConstants.LOCAL_DICTIONARIES);
		dictionary.annotateDictionaryWithWikidata(0, 100000);
	}

	@Test
	@Ignore // VERY LONG
	public void testGetWikidataForDictionariesAndUpdate1() throws Exception {
		DefaultAMIDictionary dictionary = new DefaultAMIDictionary();
		dictionary.setDictionaryName("funders");
		dictionary.setInputDir(NAConstants.PLUGINS_DICTIONARY_DIR);
		dictionary.setOutputDir(NAConstants.LOCAL_DICTIONARIES);
		dictionary.annotateDictionaryWithWikidata();
	}

	@Test
	@Ignore // LOOKUP // API FAILS
	public void getWikidataIdForSpecies() throws Exception {
		WikipediaLookup wikipediaLookup = new WikipediaLookup();
		IntArray intArray = wikipediaLookup.getWikidataIDsAsIntArray("Mus musculus");
		Assert.assertEquals("mouse", "(83310)", intArray.toString());
	}
	
	@Test
//	@Ignore // LOOKUP 
	/** we have to work on this as it gives all papers that include this term.
	 * 
	 * @throws Exception
	 */
	public void testGetWikidataHtmlElement() throws Exception {
		WikipediaLookup wikipediaLookup = new WikipediaLookup();
		String[] queryStrings = {"Hydrogen", "larus", "Peter_Murray-Rust", "Chesterton"};
		int[] size = {10, 10, 10, 10};
		int ii = 0;
		for (String query : queryStrings) {
			List<HtmlElement> response = wikipediaLookup.queryWikidata(query);
			int size2 = response.size();
			LOG.debug(size2);
			Assert.assertTrue("size", size2 > size[ii]);
			for (HtmlElement elem : response) {
//				LOG.debug(elem.toXML());
			}
			ii++;
		}
	}

	@Test
//	@Ignore // LOOKUP 
	public void getWikidataXMLForID() throws Exception {
		WikipediaLookup wikipediaLookup = new WikipediaLookup();
		URL url = WikipediaLookup.createWikidataXMLURL("Q83310");
		Element element = wikipediaLookup.getResponseXML(url);
		String property = "P225";
		/**
        <property id="P225">
          <claim _idx="0" type="statement" id="q83310$8F57CD44-BE38-4EA6-B729-36D82E855694" rank="normal">
            <mainsnak snaktype="value" property="P225" datatype="string">
              <datavalue value="Mus musculus" type="string"/>
            </mainsnak>
		 */
		String xpath = "//*[@property='"+property+"']/datavalue/@value";
		String value = XMLUtil.getSingleValue(element, xpath);
		Assert.assertEquals("value", "Mus musculus", value);
	}
	
	/** looks up a list of species.
	 * Q140 = Panthera leo
	 * Q83310 = Mus musculus
	 * Q737838 = Gorilla gorilla
	 * keeps queries and returned IDs in sync.
	 * 
	 */
	
	@Test
	@Ignore // API for species IS BROKEN
	public void getWikidataIdForMultipleSpecies() throws Exception {
		WikipediaLookup defaultLookup = new WikipediaLookup();
		List<String> speciesList = Arrays.asList(new String[] {"Mus musculus", "Gorilla gorilla", "Panthera leo"});
		List<Integer> idList = defaultLookup.getWikidataIDsAsIntegerList(speciesList, WikipediaLookup.WIKIDATA_SPECIES);
		Assert.assertEquals("mouse", "[83310, 737838, 140]", idList.toString());
	}
	
	@Test
	@Ignore // API for species is broken
	public void getWikidataIdForMultipleSpeciesWithMissing() throws Exception {
		WikipediaLookup defaultLookup = new WikipediaLookup();
		List<String> speciesList = Arrays.asList(new String[] {"Mus musculus", "Gorilla gorilla", "Biffo boffo", "Panthera leo"});
		List<Integer> idList = defaultLookup.getWikidataIDsAsIntegerList(speciesList, WikipediaLookup.WIKIDATA_SPECIES);
		Assert.assertEquals("mouse", "[83310, 737838, null, 140]", idList.toString());
	}
	
	@Test
	public void getWikidataIdForMultipleSpeciesWithAllMissing() throws Exception {
		WikipediaLookup defaultLookup = new WikipediaLookup();
		List<String> speciesList = Arrays.asList(new String[] {"Bingo bongo", "Biffo boffo"});
		List<Integer> idList = defaultLookup.getWikidataIDsAsIntegerList(speciesList, WikipediaLookup.WIKIDATA_SPECIES);
		Assert.assertEquals("mouse", "[]", idList.toString());
	}
	
//	@Test
//	public void getWikidataXMLForMutipleID() throws Exception {
//		WikipediaLookup defaultLookup = new WikipediaLookup();
//		URL url = defaultLookup.createWikidataMultipleXMLURL(Arrays.asList(new String[]{"Q140","Q83310"}));
//		LOG.debug(url);
//		Element element = defaultLookup.getWikidataXML(url);
//		XMLUtil.debug(element, "Mus");
//		Assert.assertEquals("Q83310", 
//				"<api success=\"1\"><entities><entity pageid=\"85709\" ns=\"0\" title=\"Q83310\" lastrevid=\"194466801\" modifi",
//				element.toXML().substring(0, 100));
//	}
	
	@Test
	@Ignore // takes too long // Species API is broken
	public void testLookup() throws Exception {
		File target = new File("target/lookup/pone_0115884");
		FileUtils.copyDirectory(new File(AMIFixtures.TEST_PLOSONE_DIR, "journal.pone.0115884/"), target);
		String cmd = "--sp.species --context 35 --sp.type binomial binomialsp "
				+ "-q "+target+" -i scholarly.html "
				+ "--lookup wikipedia";
		AMIArgProcessor argProcessor = new SpeciesArgProcessor();
		argProcessor.parseArgs(cmd);
		argProcessor.runAndOutput();
		// doesn't work
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"binomial\" />"
				);
	}

}
