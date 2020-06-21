package org.contentmine.ami.lookups;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.cproject.lookup.AbstractLookup;
import org.contentmine.cproject.metadata.DataTableLookup;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.html.util.HtmlUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minidev.json.JSONArray;
import nu.xom.Attribute;
import nu.xom.Element;

public class WikipediaLookup extends AbstractLookup implements DataTableLookup {

	private static final String WIKIDATA_SPARQL_QUERY = "https://query.wikidata.org/sparql?query=";
	private static final String DOLLAR_PROPS = "$.props.";
	private static final String STRING = "string";
	private static final String ITEMS = "items";
	private static final String WIKIDATA_GETIDS = "http://wdq.wmflabs.org/api?q=string[";
	private static final String ESC_QUOTE = "%22";
	private static final String ESC_SPACE = "%20";
	private static final String FORMAT_XML = "&format=xml";
	private static final String WIKIDATA_GET_ENTITIES = "https://www.wikidata.org/w/api.php?action=wbgetentities&ids=";
	public static final String WIKIDATA_SPECIES = "225";
// 
	public static final String DESCRIPTION = "description";
	public static final String ITEM = "wikidataItem";
	public static final String LABEL = "label";
	public static final String PROPERTY = "wikidataProperty";
	private static final String WIKIDATA = "wikidata";

	private static Map<String, String> propertyByPID = new HashMap<>();
	private static Map<String, String> pidByProperty = new HashMap<>();
	static {
		addProperty("P356", "DOI");
		addProperty("P698", "PMID");
		addProperty("P932", "PMCID");
		addProperty("P1476","title");
	};
	private static void addProperty(String pid, String propname) {
		propertyByPID.put(propname, pid);
		pidByProperty.put(pid, propname);
	}
	/**
Magnus Manske
	
Mar 14 (9 days ago)
		
to Peter
Hi Peter,

here are some links related to Wikipedia and Wikidata search.

To find a Wikidata item by the Latin species name (here ���Mus musculus���):
http://wdq.wmflabs.org/api?q=string[225:%22Mus%20musculus%22]

Web interface:
https://query.wikidata.org/#SELECT%20%3Fq%20%7B%20%3Fq%20wdt%3AP225%20%22Mus%20musculus%22%20%7D
REST API:
https://query.wikidata.org/sparql?query=SELECT%20%3Fq%20%7B%20%3Fq%20wdt%3AP225%20%22Mus%20Musculus%22%20%7D
SPARQL:
SELECT ?q { ?q wdt:P225 "Mus musculus" }

This returns a JSON structure with an item list, containing one item:
{"status":{"error":"OK","items":1,"querytime":"226ms","parsed_query":"STRING[225:'Mus musculus']"},"items":[83310]}


The item number (83310) refers to the Wikidata item; prepend a ���Q��� for that:
https://www.wikidata.org/wiki/Q83310

To get the XML for that item, use this format:
https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q83310&format=xml

You can do a search for item properties like this:
https://www.wikidata.org/w/api.php?action=wbsearchentities&search=Mus%20musculus&language=en&limit=50&format=xml

Or do a fulltext search. This works on en.wikipedia.org and www.wikidata.org alike:
https://www.wikidata.org/w/api.php?action=query&list=search&srsearch=Mus%20musculus&format=xml


You can see more details about the extensive Wikidata/Wikipedia API here:
https://www.wikidata.org/w/api.php
and for the query API:
http://wdq.wmflabs.org/api_documentation.html


Hope that helps,
Magnus
Peter Murray-Rust <pm286@cam.ac.uk>
	
Mar 14 (9 days ago)
		
	 */
	
	/**
	 * {"status":
	 *   {"error":"OK",
		 *     "items":1,
	 *     "querytime":"222ms",
	 *     "parsed_query":"STRING[225:'Mus musculus']"},
	 *   "items":[83310]
	 * }
	 */
	
	/** 20180928 Magnus
	 * 
	 * https://tools.wmflabs.org/mix-n-match/#/
	 * https://tools.wmflabs.org/mix-n-match/import.php
	 * 
	 * cann moderate through Q or P (Q5 == human, P31 instanceof)
	 * 
	 * see also https://tools.wmflabs.org/openrefine-wikidata/
	 * 
	 * 
	 */
	
	/*
<?xml version="1.0"?>
<api batchcomplete="">
<continue sroffset="10" continue="-||" />
<query>
<searchinfo totalhits="87915" />
<search>
<p ns="0" title="Q83310" pageid="85709" wordcount="0" snippet="species of mammal" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305579" pageid="16952952" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305624" pageid="16952997" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305594" pageid="16952966" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15304656" pageid="16952129" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305590" pageid="16952963" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305599" pageid="16952972" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305608" pageid="16952981" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305576" pageid="16952949" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
<p ns="0" title="Q15305616" pageid="16952989" wordcount="0" snippet="Mus musculus chromosome" timestamp="2018-09-14T10:08:19Z" />
</search>
</query>
</api>

<?xml version="1.0"?>
<api batchcomplete="">
<continue sroffset="10" continue="-||" />
<query>
<searchinfo totalhits="17" />
<search>
<p ns="0" title="Cryptogenic species" pageid="5603283" size="1673" wordcount="163" snippet="A &lt;span class=&quot;searchmatch&quot;&gt;cryptogenic&lt;/span&gt; &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt; (&amp;quot;&lt;span class=&quot;searchmatch&quot;&gt;cryptogenic&lt;/span&gt;&amp;quot; being derived from Greek &amp;quot;κρυπτός&amp;quot;, meaning hidden, and &amp;quot;γένεσις&amp;quot;, meaning origin) is a &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt; whose origins are" timestamp="2017-10-06T17:30:03Z" />
<p ns="0" title="Cryptogenic" pageid="24844911" size="499" wordcount="20" snippet="&lt;span class=&quot;searchmatch&quot;&gt;Cryptogenic&lt;/span&gt; refers to something of obscure or unknown origin. It is commonly used to refer to: &lt;span class=&quot;searchmatch&quot;&gt;Cryptogenic&lt;/span&gt; disease &lt;span class=&quot;searchmatch&quot;&gt;Cryptogenic&lt;/span&gt; &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt;" timestamp="2016-04-28T08:03:54Z" />
<p ns="0" title="List of invasive plant species in Florida" pageid="42200242" size="27351" wordcount="1167" snippet="matrella  The &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt; below are &lt;span class=&quot;searchmatch&quot;&gt;cryptogenic&lt;/span&gt;, of indeterminate native or non-native origin in Florida:   Pistia stratiotes  Invasive &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt; in the United" timestamp="2018-09-14T02:28:01Z" />
<p ns="0" title="Aurelia aurita" pageid="373206" size="15393" wordcount="1741" snippet="and molecular genetic analyses identify multiple introductions of &lt;span class=&quot;searchmatch&quot;&gt;cryptogenic&lt;/span&gt; &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt;&amp;quot;. Proc. Natl. Acad. Sci. USA. 102 (34): 11968–11973. doi:10.1073/pnas" timestamp="2018-08-16T09:22:11Z" />
<p ns="0" title="Glossary of invasion biology terms" pageid="3353280" size="30720" wordcount="4138" snippet="that live together in a particular environment (Allaby 1998).   &lt;span class=&quot;searchmatch&quot;&gt;Cryptogenic&lt;/span&gt; &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt; &lt;span class=&quot;searchmatch&quot;&gt;Species&lt;/span&gt; that are neither clearly native nor exotic (Cohen and Carlton" timestamp="2018-04-04T13:00:12Z" />
<p ns="0" title="Ecology of the San Francisco Estuary" pageid="14999741" size="56203" wordcount="7638" snippet="Series. 66:81-94. Carlton, J. (1996). &amp;quot;Biological Invasions and &lt;span class=&quot;searchmatch&quot;&gt;Cryptogenic&lt;/span&gt; &lt;span class=&quot;searchmatch&quot;&gt;Species&lt;/span&gt;. &amp;quot; Ecology 77(6). Cole, B. E., and J. E. Cloern. 1987. An empirical" timestamp="2018-01-23T15:12:29Z" />
<p ns="0" title="Pollicipes pollicipes" pageid="21580073" size="8723" wordcount="713" snippet="2557: 29–38.    Dan Minchin (2007). &amp;quot;A checklist of alien and &lt;span class=&quot;searchmatch&quot;&gt;cryptogenic&lt;/span&gt; aquatic &lt;span class=&quot;searchmatch&quot;&gt;species&lt;/span&gt; in Ireland&amp;quot; (PDF). Aquatic Invasions. 2 (4): 341–366. doi:10" timestamp="2018-03-27T20:43:41Z" />
<p ns="0" title="Cryptic bat rabies" pageid="40096409" size="4451" wordcount="558" snippet="mucosa of naturally infected bats. Science 175, 1255–1256.   Gibbons RV. &lt;span class=&quot;searchmatch&quot;&gt;Cryptogenic&lt;/span&gt; rabies, bats, and the question of aerosol transmission. Ann Emerg Med" timestamp="2018-02-18T18:47:48Z" />
<p ns="0" title="Keratin 18" pageid="916383" size="9758" wordcount="1112" snippet="epithelial tissues of the body. Mutations in this gene have been linked to &lt;span class=&quot;searchmatch&quot;&gt;cryptogenic&lt;/span&gt; cirrhosis. Two transcript variants encoding the same protein have been" timestamp="2017-10-27T00:50:06Z" />
<p ns="0" title="Hypersensitivity pneumonitis" pageid="2303500" size="17406" wordcount="1274" snippet="usual interstitial pneumonia, non-specific interstitial pneumonia and &lt;span class=&quot;searchmatch&quot;&gt;cryptogenic&lt;/span&gt; organizing pneumonia, among others.The prognosis of some idiopathic interstitial" timestamp="2018-09-13T04:38:14Z" />
</search>
</query>
</api>

view-source:https://www.wikidata.org/w/api.php?action=query&list=search&srsearch=Cryptogenic%20species&format=xml	 */
	
	private static final Logger LOG = Logger.getLogger(WikipediaLookup.class);
	private DefaultAMIDictionary dictionary;
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private int start = 0;
	private int end = Integer.MAX_VALUE;
	private int maxAlternative = 3;
	
	public WikipediaLookup() {
	}

	public WikipediaLookup(DefaultAMIDictionary dictionary) {
		this();
		this.dictionary = dictionary;
	}

	@Override
	public String lookup(String speciesName) throws IOException {
		if (speciesName != null) {
			LOG.debug("WPSP: "+speciesName);
			IntArray wikidataIntArray = getWikidataIDsAsIntArray(speciesName);
			String result = wikidataIntArray.toString();
			// remove all brackets
			result = result == null ? null : result.replaceAll("[\\(\\)]", "");
			return (result == null || result.trim().equals("")) ? null : result; 
		} else {
			LOG.error("null species");
			return null;
		}
	}
	
	public IntArray getWikidataIDsAsIntArray(String speciesName) throws IOException {
		JsonElement jsonElement = this.getWikidataSpeciesJSONElement(speciesName);
		return getIdentifierArray(jsonElement, ITEMS);
	}
	
	public URL createWikidataLookupURL(String query) {
		query = query.trim();
		query = query.replace(" ", "%20");
		String urlString = 
//				"https://www.wikidata.org/w/index.php?search=&search="+query+"&title=Special:Search&go=Go&searchToken=1gq68j12xhd4disfwojgu2si4";
		"https://www.wikidata.org/w/index.php?search=&search="+query+"&title=Special:Search&go=Go";
		URL url = createUrl(urlString);
		return url;
	}
	
	/** SPARQL lookup
	 * e.g. 
	 * "#DOI lookup.\n" +
                "SELECT ?item " +
                "WHERE {" +
                "  ?item wdt:P356 \"10.1186/1472-6882-6-3\" ." +
                "}";
                
                
	 * 
	 results in:
<sparql xmlns="http://www.w3.org/2005/sparql-results#">
	<head>
		<variable name="item" />
	</head>
	<results>
		<result>
			<binding name="item">
				<uri>http://www.wikidata.org/entity/Q25257418</uri>
			</binding>
		</result>
	</results>
</sparql>
	 
	 * @param query un-encoded
	 * @return XML 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public Element createSparqlLookup(String query) throws IOException {
		Element element = null;
		if (query != null) {
			query = query.trim();
			try {
				// encode everything, spaces become "+"
				query = URLEncoder.encode(query, /*CMineUtil.UTF8_CHARSET.toString()*/ "UTF-8");
				// change + to %20
				query = query.replaceAll("\\+", "%20");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("BUG", e);
			}
			String urlString = WIKIDATA_SPARQL_QUERY + query;
			try {
				URL url = new URL(urlString);
				element = XMLUtil.parseQuietlyToRootElement(url.openStream());
			} catch (MalformedURLException e) {
				throw new RuntimeException("BUG", e);
			}
		}
		return element;
	}
	
	public static String createTriple(String subj, String property, String obj) {
		String triple = "";
		if (subj == null || property == null || obj == null) {
			LOG.error("null args");
		}
		String pid = property.startsWith("P") ? property : pidByProperty.get(property);
		if (pid == null) {
			LOG.error("unknown property: " + property);
		} 
		triple = subj + " " + property + " " + obj + " " + ".";
		return triple;
	}
	
	
	/** don't think this works anymore ; check REST API */
	public IntArray getWikidataIDsAsIntArray(List<String> speciesNames) throws IOException {
		JsonElement jsonElement = this.getWikidataSpeciesJSONElement(speciesNames);
		return getIdentifierArray(jsonElement, ITEMS);
	}

	/** this doesn't work any more, check API*/
	public List<Integer> getWikidataIDsAsIntegerList(List<String> names, String property) throws IOException {
		List<Integer> idList = new ArrayList<Integer>();
		JsonElement jsonElement = this.getWikidataSpeciesJSONElement(names);
		if (jsonElement != null) {
			String jsonPath = DOLLAR_PROPS+property;
			JSONArray jsonArray = (JSONArray) CMineUtil.getObjectForJsonPath(jsonElement.toString(), jsonPath);
			Map<String, Integer> namesToIdMap = new HashMap<String, Integer>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONArray jsonArray1 = (JSONArray) jsonArray.get(i);
				if (STRING.equals(jsonArray1.get(1).toString())) {
					Integer id = (Integer)jsonArray1.get(0);
					String value = (String) jsonArray1.get(2);
					namesToIdMap.put(value, id);
				}
			}
			for (String name : names) {
				idList.add(namesToIdMap.get(name));
			}
		} else {
			LOG.error("Null response from Wikidata; check REST API"+names);
		}
		return idList;
	}

	/** creates URL to retrieve data for an id.
	 * 
	 * @param wikidataId
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL createWikidataXMLURL(String wikidataId) throws MalformedURLException {
		String urlString = WIKIDATA_GET_ENTITIES;
		urlString += wikidataId;
		urlString += FORMAT_XML;
		return new URL(urlString);
	}
    
	private JsonElement getWikidataSpeciesJSONElement(String speciesName) throws IOException {
		URL url = createWikidataSpeciesLookupURL(speciesName);
		LOG.debug(url);
		return getJsonFromUrl(url);
	}

	/**
	 * Search results

Jump to navigation Jump to search
To search for Wikidata items by their title on a given site, use Special:ItemByTitle. To search by label in a given language, use Special:ItemDisambiguation.


Search
Results 1 – 21 of 547
Content pages
Multimedia
Translations
Everything
Advanced
Larus (Q1887740)
genus of birds
26 statements, 46 sitelinks - 21:40, 11 September 2018
European Herring Gull (Q28236) : Larus argentatus
species of bird
71 statements, 70 sitelinks - 21:40, 11 September 2018
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public HtmlBody getWikidataHtmlBody(String query) throws IOException {
		URL url = createWikidataLookupURL(query);
		HtmlBody body = getHtmlBodyFromUrl(url);
		return body;
	}

	private JsonElement getJsonFromUrl(URL url) throws IOException {
		String json = this.getResponse(url);
		LOG.debug("Json "+json);
	    JsonParser parser = new JsonParser();
	    return parser.parse(json);
	}
	private HtmlBody getHtmlBodyFromUrl(URL url) throws IOException {
		boolean htmlFactory = true;
		htmlFactory = false;
		HtmlHtml htmlElement = null;
		try {
			if (htmlFactory) {
				String htmlS = this.getResponse(url);
				FileUtils.write(new File("target/dictionary/wikipedia/wikidata.txt"), htmlS, CMineUtil.UTF8_CHARSET);
				htmlElement = (HtmlHtml) HtmlUtil.readTidyAndCreateElement(htmlS);
	//			htmlElement = (HtmlHtml) new HtmlFactory().parse(XMLUtil.parseXML(htmlS));
			} else {
//				System.err.println("Reading from "+url);
				InputStream is = url.openStream();
				htmlElement = null;
				htmlElement = (HtmlHtml) HtmlUtil.readTidyAndCreateElement(is);
				is.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("cannot parse HTML "+e, e);
		}
		return htmlElement.getBody();
	}

//	<ul class='mw-search-results'>

	/** this doesn't work any more */
	private JsonElement getWikidataSpeciesJSONElement(List<String> names) throws IOException {
		URL url = createWikidataSpeciesLookupURL(names);
		// http://wdq.wmflabs.org/api?q=string[225:%22Mus%20musculus%22,225:%22Gorilla%20gorilla%22,225:%22Panthera%20leo%22]&props=225
		String json = this.getResponse(url);
	    JsonParser parser = new JsonParser();
	    JsonElement element = null;
	    try {
	    	element = parser.parse(json);
	    } catch (JsonSyntaxException e) {
	    	// expected for empty returns (bug in Json)
	    }
	    return element;
	}

	public List<HtmlElement> queryWikidata(String query) {
		HtmlBody htmlBody = null;
		List<HtmlElement> liList = new ArrayList<HtmlElement>();
		try {
			htmlBody = getWikidataHtmlBody(query);
		} catch (IOException e) {
			LOG.debug("URL "+e);
			return liList;
		}
		boolean outputQuery = false; // needs to link into Tools
		if (outputQuery) {
			String filename = query.replaceAll("\\s+", "_");
			File file = new File("target/wikipedia/query/" + filename + ".html");
			System.out.println("wrote query result (" + query + ") "+file);
			XMLUtil.writeQuietly(htmlBody, file, 1);
		}
		/**
	  <div class="searchresults">
		  <p class="mw-search-createlink"> </p>
		  <ul class="mw-search-results">
		 */
		
		List<Element> elements = XMLUtil.getQueryElements(htmlBody, 
				".//*[local-name()='div' and @class='searchresults']"
				+ "/*[local-name()='ul' and @class='mw-search-results']");
		HtmlUl searchResultsUl = elements.size() == 0 ? null : (HtmlUl) elements.get(0);
		
		/**
	<ul class="mw-search-results" xmlns="http://www.w3.org/1999/xhtml">
	<li>
	<div class="mw-search-result-heading">
	 <a href="/wiki/Q1887740" title="‎Larus‎ | ‎genus of birds‎" data-serp-pos="0">
		<span class="wb-itemlink">
		  <span class="wb-itemlink-label" lang="en" dir="ltr">
		    <span class="searchmatch">Larus</span>
		  </span>
		  <span class="wb-itemlink-id">(Q1887740)</span>
		</span>
	 </a>
	</div>
	<div class="searchresult">
	 <span class="wb-itemlink-description">genus of birds</span>
	</div>
	<div class="mw-search-result-data">26 statements, 46 sitelinks - 22:29, 11 September 2018</div>
	</li>
		 */
		if (searchResultsUl != null) {
			liList = AbstractCMElement.getChildElements(searchResultsUl, HtmlLi.TAG); 
			for (HtmlElement li : liList) {
				// seems a no-op
//				HtmlA a = (HtmlA)li.getChild(0).getChild(0);
			}
		} else {
		}
		return liList;
	}

	public void lookupEntriesAndAnnotate(Element toBeLookedUpInWikidata) throws IOException {
		Element outputDictionary = new Element(DefaultAMIDictionary.DICTIONARY);
		addEntriesToDictionaryAccordingToWikipediaLookup(toBeLookedUpInWikidata, outputDictionary);
		XMLUtil.debug(outputDictionary, new File(dictionary.getOutputDir(), dictionary.getDictionaryName()+".xml"), 1);
	}

	private void addEntriesToDictionaryAccordingToWikipediaLookup(Element wikipediaLookupHtml, Element outputDictionary) throws IOException {
		int size = wikipediaLookupHtml.getChildElements().size();
		for (int i = start; i < Math.min(size, end); i++) {
			Element dictionaryElement = wikipediaLookupHtml.getChildElements().get(i);
			String query = dictionaryElement.getAttributeValue(DictionaryTerm.TERM);
			if (query == null) {
				LOG.debug("missing term: " + dictionaryElement.toXML());
				continue;
			}
			String item = dictionaryElement.getAttributeValue(ITEM);
			String property = dictionaryElement.getAttributeValue(PROPERTY);
			if (item != null && !item.equals("")) {
				LOG.debug("skipped existing item: "+item);
				outputDictionary.appendChild(dictionaryElement);
			} else if (property != null && !property.equals("")) {
				LOG.debug("skipped exisiting property: "+property);
				outputDictionary.appendChild(dictionaryElement);
			} else {
				query = query.replaceAll(" ",  "_");
				LOG.debug(query);
				List<HtmlElement> liList = queryWikidata(query);
				addToOutput(outputDictionary, liList, dictionaryElement, maxAlternative);
			}
		}
	}

	/**
	<li>
	<div class="mw-search-result-heading">
	 <a href="/wiki/Q1887740" title="‎Larus‎ | ‎genus of birds‎" data-serp-pos="0">
		<span class="wb-itemlink">
		  <span class="wb-itemlink-label" lang="en" dir="ltr">
		    <span class="searchmatch">Larus</span>
		  </span>
		  <span class="wb-itemlink-id">(Q1887740)</span>
		</span>
	 </a>
	</div>
	<div class="searchresult">
	 <span class="wb-itemlink-description">genus of birds</span>
	</div>
	</li>
	 * 
	 * @param outputDictionary
	 * @param liList
	 * @param dictionaryElement
	 */
	public void addToOutput(Element outputDictionary, List<HtmlElement> liList, Element dictionaryElement, int maxAlternative) {
		Element newDictionaryElement = (Element) dictionaryElement.copy();
		if (liList.size() == 0) {
			// remove existing attribute
			Attribute wikidata = newDictionaryElement.getAttribute(WikipediaLookup.WIKIDATA);
			if (wikidata != null) {
				newDictionaryElement.removeAttribute(wikidata);
			}
		} else if (liList.size() == 1) {
			WikiResult wikiResult = WikiResult.extractWikiResult((HtmlElement) liList.get(0));
			newDictionaryElement.addAttribute(new Attribute(WikipediaLookup.WIKIDATA, wikiResult.getQString()));
			LOG.debug("added single match: " + wikiResult.getQString());
		} else {
			List<WikiResult> wikiResultList = WikiResult.extractWikiResultList((List<HtmlElement>) liList);
			// only output the first few
			if (wikiResultList.size() > maxAlternative) {
				wikiResultList = wikiResultList.subList(0, maxAlternative);
			}
			for (WikiResult wikiResult : wikiResultList) {
				newDictionaryElement.appendChild(wikiResult.toXML());
			}
		}
		outputDictionary.appendChild(newDictionaryElement);
	}

	/** create URL to lookup a species.
	 * 
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	private static URL createWikidataSpeciesLookupURL(String name) {
		URL url =  createWikidataLookupURL(WIKIDATA_SPECIES, name);
		return url;
	}
    
	/** create URL to lookup many species.
	 * 
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	private static URL createWikidataSpeciesLookupURL(List<String> names) {
		URL url =  createWikidataLookupURL(WIKIDATA_SPECIES, names);
		return url;
	}
    
	/** creates a search URL from a Wikipedia property and a name.
	 * 
	 * @param property (e.g. 225 for species)
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL createWikidataLookupURL(String property, String name) {
		name = name.replaceAll(" ", ESC_SPACE);
		String urlString = WIKIDATA_GETIDS+property+":"+ESC_QUOTE+name+ESC_QUOTE+"]";
		URL url = createUrl(urlString);
		return url;
	}

	private static URL createUrl(String urlString) {
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Bad url for wikidata: +url", e);
		}
		return url;
	}
	
	/** creates a search URL from a Wikipedia property and a name.
	 * 
	 * @param property (e.g. 225 for species)
	 * @param name
	 * @return
	 * @throws MalformedURLException
	 */
	public static URL createWikidataLookupURL(String property, List<String> names) {
		String urlString = WIKIDATA_GETIDS;
		int i = 0;
		for (String name : names) {
			name = name.replaceAll(" ", ESC_SPACE);
			if (i++ > 0) urlString += ",";
			urlString += property+":"+ESC_QUOTE+name+ESC_QUOTE;
		}
		urlString += "]&props="+property;
		LOG.trace("URL: "+urlString);
		URL url = createUrl(urlString);
		return url;
	}

	public void setRange(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getMaxAlternative() {
		return maxAlternative;
	}

	public void setMaxAlternative(int maxAlternative) {
		this.maxAlternative = maxAlternative;
	}

	/** extracts "Wikidata Item" from wikipedia page.
	 * 
	 * @param wikipediaPage
	 * @return list of HTMLA containing URL and QNumber. Not sure why it's a list 
	 */
	public List<HtmlElement> createWikidataFromTermLookup(HtmlElement wikipediaPage) {
		List<HtmlElement> wikidata = null;
		if (wikipediaPage != null) {
			WikipediaPageInfo wikipediaPageInfo = WikipediaPageInfo.createPageInfo(wikipediaPage);
			
			HtmlA a = wikipediaPageInfo == null ? null : wikipediaPageInfo.getLinkToWikidataItem();
			wikidata = new ArrayList<HtmlElement>();
			if (a != null) wikidata.add(a);
		}
		return wikidata;
	}
	
	

	/** gets single Q number from list of HTMLA elements from wikidata search.
	 * 
	 * <li class="mw-search-result">
	 *   <div class="mw-search-result-heading">
	 *     <a href="/wiki/Q16916208" title="‎Alphacryptovirus‎ | ‎genus of viruses‎" data-serp-pos="0">
	 *       <span class="wb-itemlink">
	 *         <span class="wb-itemlink-label" lang="en" dir="ltr">
	 *           <span class="searchmatch">Alphacryptovirus</span>
	 *         </span> 
	 *         <span class="wb-itemlink-id">(Q16916208)</span>
	 *       </span>
	 *     </a>
	 *   </div>
	 *   <div class="searchresult">
	 *     <span class="wb-itemlink-description">genus of viruses</span>
	 *   </div> 
	 *   <div class="mw-search-result-data">7 statements, 0 sitelinks - 23:20, 9 February 2019</div>
	 * </li>

	 * @param wikidata
	 * @return
	 */
//	public static String getQNumberFromSearchResults(List<HtmlElement> orderedHits) {
//		String q = null;
//		if (orderedHits != null && orderedHits.size() > 0) {
//			List<WikiResult> wikiResultList = WikiResult.extractWikiResultList((List<HtmlElement>) orderedHits);
//			WikiResult wikiResult = wikiResultList.get(0);
//			q = wikiResult.getQString();
//		}
//		return q;
//	}

	public static WikiResult getFirstWikiResultFromSearchResults(List<HtmlElement> orderedHits) {
		WikiResult wikiResult = null;
		if (orderedHits != null && orderedHits.size() > 0) {
			List<WikiResult> wikiResultList = WikiResult.extractWikiResultList((List<HtmlElement>) orderedHits);
			wikiResult = wikiResultList.get(0);
		}
		return wikiResult;
	}

    /**
     * These attempted to retrieve multiple species to avoid bandwidth but the id<->species map is lost.
     * 
     */
//	private IntArray getWikidataIDsAsIntArray(List<String> names) throws MalformedURLException, IOException {
//	JsonElement jsonElement = this.getWikidataJSONElement(names);
//	LOG.debug(jsonElement);
//	return getIdentifierArray(jsonElement);
//}


//	private JsonElement getWikidataJSONElement(List<String> names) throws MalformedURLException, IOException {
//	URL url = createWikidataSpeciesLookupURL(names);
//	LOG.debug(url);
//	String json = this.getString(url);
//    JsonParser parser = new JsonParser();
//    return parser.parse(json);
//}

//	public URL createWikidataMultipleXMLURL(List<String> idList) throws MalformedURLException {
//		String urlString = WIKIDATA_GET_SPECIES;
//		int i = 0;
//		for (String id : idList) {
//			if (i++ > 0) {
//				urlString += ",";
//			}
//			urlString += id;
//		}
//		urlString += FORMAT_XML;
//		return new URL(urlString);
//	}
    
//	private URL createWikidataSpeciesLookupURL(List<String> names) throws MalformedURLException {
//		return createWikidataLookupURL(WIKIDATA_SPECIES, names);
//	}
    
//	private URL createWikidataLookupURL(String property, List<String> names) throws MalformedURLException {
//		String urlString = WIKIDATA_GETIDS;
//		int i = 0;
//		for (String name : names) {
//			name = name.replaceAll(" ", ESC_SPACE);
//			if (i++ > 0) {
//				urlString += ",";
//			}
//			urlString += property+":"+ESC_QUOTE+name+ESC_QUOTE;
//		}
//		urlString += "]";
//		return new URL(urlString);
//	}
    
	
		
}
