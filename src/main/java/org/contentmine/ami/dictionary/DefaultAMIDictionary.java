package org.contentmine.ami.dictionary;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.lookups.WikipediaLookup;
import org.contentmine.ami.lookups.WikipediaPageInfo;
import org.contentmine.ami.tools.dictionary.DictionarySearchTool;
import org.contentmine.cproject.lookup.DefaultStringDictionary;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlI;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTitle;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.contentmine.norma.NAConstants;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.google.gson.JsonElement;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

/** a simple collection of ids (terms) with additional names if known.
 * 
 * This may expand later depending on what resources we find.
 * 
 * Dictionaries are collections of terms that can be used for annotation or looking up
 * terms. At present they are simply:
 *     {term, (optional)Name}
 * They are originally developed in a scientifc context and a typical pair is
 *     {"ABI3BP", "ABI family member 3 binding protein"}
 *     
 * The dictionary is stored as XML:
 */
//<dictionary title="hgnc">
 
//      <entry term="A1BG" name="alpha-1-B glycoprotein"/>
//      <entry term="A1BG-AS1" name="A1BG antisense RNA 1"/>

/**
 * it is assumed all terms are distinct. Case matters.
 * 
 * Initially the dictionaries are formed from JSON, TSV,  text files etc., but normallised to 
 * this XML
 * 
 * uses a BloomFilter to check whether the Dictionary does NOT contain a term and then looks up
 * in Map<term, name>
 *     
 * @author pm286
 *
 */

/**
 * 
 */
//<?xml version="1.0" encoding="UTF-8"?>
//<dictionary title="cochrane">
//<entry term="Cochrane Library" name="cochrane library" />
//<entry term="Cochrane Reviews" name="cochrane reviews" />
//
//<entry term="adverse events" name="adverse events"/>

//</dictionary>
/**
 * @author pm286
 *
 */
public class DefaultAMIDictionary extends DefaultStringDictionary {
	public static final Logger LOG = LogManager.getLogger(DefaultAMIDictionary.class);

	private static final int PAGE_IMAGE_LINK_COLUMN = 6;
	private static final int CENTRAL_DESCRIPTION_COLUMN = 5;
	private static final int WIKIDATA_LINK_COLUMN = 4;
	public static final String WIKIPEDIA_BASE = "https://en.wikipedia.org";
	public static final String ID = "id";
	public static final String DICTIONARY = "dictionary";
	public static final String DESC = "desc";
	public static final String DESCRIPTION = "description";
//	public static final String ENTRY = "entry";
	public static final String NAME = "name";
	private static final String REGEX = "regex";
	public static final String SYNONYM = "synonym";
//	public static final String TITLE = "title";
	public static final String WIKIDATA_ID = "wikidataID";
	public static final String WIKIDATA_URL = "wikidataURL";
	public static final String WIKIPEDIA_PAGE = "wikipediaPage";
	public static final String WIKIPEDIA_URL = "wikipediaURL";

	/** later these should be read in from args.xml ...
	 * 
	 */
	protected static final File AMI_DIR        = new File(NAConstants.MAIN_AMI_DIR+"/plugins/");
	protected static final File DICTIONARY_DIR = new File(AMI_DIR, "dictionary");
	protected static final File GENE_DIR       = new File(AMI_DIR, "gene");
	protected static final File PLACES_DIR     = new File(AMI_DIR, "places");
	protected static final File SPECIES_DIR    = new File(AMI_DIR, "species");
	public static final File SYNBIO_DIR     = new File(AMI_DIR, "synbio");
	protected static final String UTF_8        = "UTF-8";
	
	private static final String PAGE_CONTENT = "content";
	private static final String IMAGE = "image";

	
	private String dictionaryName;
	protected Map<DictionaryTerm, String> namesByTerm;
	protected InputStream inputStream;
	protected Element dictionaryElement;
	private Funnel<String> stringFunnel;
	private BloomFilter<String> bloomFilter;
	private String dictionarySource;
	private List<DictionaryTerm> dictionaryTermList; 
	private List<DictionaryTerm> stemmedTermList;
	private Set<String> rawTermSet;
	private JsonElement jsonElement;
	private File outputDir;
	private File inputDir; 
	private String baseUrl = "https://en.wikipedia.org";
	
	public DefaultAMIDictionary() {
		init();
	}
	
	private void init() {
		stringFunnel = new Funnel<String>() {
			public void funnel(String person, PrimitiveSink into) {
				into.putUnencodedChars(person);
			}
		};
	}
	
	public boolean contains(String string) {
		if (string == null) { 
			return false;
		} else if (!mightContain(string)) {
			return false;
		} else {
			return (rawTermSet == null) ? false : rawTermSet.contains(string);
		}
	}

	public Set<String> getRawTermSet() {
		return rawTermSet;
	}
	
	public Set<String> getRawLowercaseTermSet() {
		Set<String> lowercaseSet = new HashSet<>();
		for (String rawTerm : rawTermSet) {
			lowercaseSet.add(rawTerm.toLowerCase());
		}
		return lowercaseSet;
	}

	private boolean mightContain(String string) {
		return bloomFilter == null ? true : bloomFilter.mightContain(string);
	}

	public int size() {
		return namesByTerm == null ? -1 : namesByTerm.size();
	}
	
	public Map<DictionaryTerm, String> getNamesByTerm() {
		return namesByTerm;
	}

	@Override
	public List<List<String>> getTrailingWords(String key) {
		throw new RuntimeException("NYI");
	}

	protected Element createDictionaryElementFromHashMap(String title) {
		createDictionaryElement(title);
		
		List<DictionaryTerm> dictionaryTerms = Arrays.asList(namesByTerm.keySet().toArray(new DictionaryTerm[0]));
		Collections.sort(dictionaryTerms);
		for (DictionaryTerm dictionaryTerm : dictionaryTerms) {
			String term = dictionaryTerm.getTermPhrase().getString();
			String name = namesByTerm.get(dictionaryTerm);
			Element entry = createEntryElementFromTerm(term);
			entry.addAttribute(new Attribute(DictionaryTerm.NAME, name));
			dictionaryElement.appendChild(entry);
		}
		return dictionaryElement;
	}

	public static Element createEntryElementFromTerm(String term) {
		if (term == null) return null;
		Element entry = new Element(ENTRY);
		entry.addAttribute(new Attribute(DictionaryTerm.TERM, term));
		return entry;
	}

	public Element createDictionaryElement(String title) {
		dictionaryElement = createDictionaryElementWithTitle(title);
		return dictionaryElement;
	}

	public static Element createDictionaryElementWithTitle(String title) {
		Element dictionaryElement = new Element(DICTIONARY);
		if (title != null) {
			dictionaryElement.addAttribute(new Attribute(TITLE, title));
		}
		return dictionaryElement;
	}

	protected void writeXMLFile(File file) {
		try {
			XMLUtil.debug(dictionaryElement, file, 2);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write dictionary file: "+file, e);
		}
	}
	
	public String toXML() {
		return dictionaryElement == null ? null : dictionaryElement.toXML();
	}
	
	public JsonElement createJsonObject() {
		CMJsonDictionary jsonDictionary = null;
		jsonElement = null;
		if (dictionaryElement != null) {
			jsonDictionary = new CMJsonDictionary();
			String id = dictionaryElement.getAttributeValue(ID);
			if (id != null) {
				jsonDictionary.setId(id);
			}
		}
		return jsonElement;
	}

	/** stores entries in hashMap (term, name) and also creates BloomFilter.
	 * 
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public void readDictionary(File file)  {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot read dictionary file "+file);
		}
		readDictionary(fis);
		return;
	}

	protected void readDictionary(InputStream is) {
		dictionaryElement = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		readDictionaryElement(dictionaryElement);
	}

	public DefaultAMIDictionary readDictionaryElement(Element dictionaryElement) {
		this.dictionaryElement = dictionaryElement;
		namesByTerm = new HashMap<DictionaryTerm, String>();
		rawTermSet = new HashSet<String>();
		dictionaryTermList = new ArrayList<DictionaryTerm>();
		Elements elements = dictionaryElement.getChildElements();
		bloomFilter = BloomFilter.create(stringFunnel, elements.size());
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			if (ENTRY.equals(element.getLocalName())) {
				DictionaryTerm dictionaryTerm = DictionaryTerm.createDictionaryTerm(element);
				dictionaryTermList.add(dictionaryTerm);
				String name = dictionaryTerm.getName();
				if (name == null) {
					continue;
				}
				namesByTerm.put(dictionaryTerm, name);
				String term = dictionaryTerm.getTerm();
				if (term == null) {
					throw new RuntimeException("Null term: "+element.toXML());
				}
				bloomFilter.put(term);
				rawTermSet.add(term);
			} else if (DESC.equals(element.getLocalName())) {
				// add desc here
			}
		}
		return this;
	}


	public List<DictionaryTerm> getDictionaryTermList() {
		return dictionaryTermList;
	}

	public List<DictionaryTerm> getTermsSortedBySize() {
		getDictionaryTermList();
		List<DictionaryTerm> terms = new ArrayList<DictionaryTerm>();
		if (dictionaryTermList == null || dictionaryTermList.size() == 0) {
			LOG.error("NO terms extracted");
		} else {
			getMaximumTermSize();
			if (namesByTerm != null) {
				terms = new ArrayList<DictionaryTerm>(Arrays.asList(namesByTerm.keySet().toArray(new DictionaryTerm[0])));
				Collections.sort(terms);
			}
		}
		return terms;
	}

	public List<TermPhrase> getTermPhraseList() {
		List<TermPhrase> termPhraseList = new ArrayList<TermPhrase>();
	getDictionaryTermList();
		Iterator<DictionaryTerm> termIterator = namesByTerm.keySet().iterator();
		while (termIterator.hasNext()) {
			termPhraseList.add(termIterator.next().getTermPhrase());
		}
		return termPhraseList;
	}

	/** get maximum number of words in term
	 *
	 * @return
	 */
	public int getMaximumTermSize() {
		getDictionaryTermList();
		int maxLength = 0;
		for (DictionaryTerm dictionaryTerm : dictionaryTermList) {
			if (dictionaryTerm.size() > maxLength) {
				maxLength = dictionaryTerm.size();
			}
		}
		return maxLength;
	}

	public void sortAlphabetically() {
		ensureDictionaryTerms();
		Collections.sort(dictionaryTermList);
	}

	private void ensureDictionaryTerms() {
		if (dictionaryTermList == null) {
			dictionaryTermList = new ArrayList<DictionaryTerm>();
		}
	}
	
	public List<DictionaryTerm> getStemmedList() {
		if (stemmedTermList == null) {
			if (dictionaryTermList != null) {
				stemmedTermList = new ArrayList<DictionaryTerm>();
				for (DictionaryTerm dictionaryTerm : dictionaryTermList) {
					
				}
			}
		}
		return stemmedTermList;
	}

	public static DefaultAMIDictionary createSortedDictionary(File dictionaryFile) {
		DefaultAMIDictionary dictionary = new DefaultAMIDictionary();
		dictionary.readDictionary(dictionaryFile);
		dictionary.sortAlphabetically();
		return dictionary;
	}
	
	public String toString() {
		String s = "";
		s += "dictionarySource: "+dictionarySource;
		s += "; terms: "+getTermsSortedBySize().size();
		s += "; title:  "+getTitle();
		return s;

	}
	
	public String getURL() {
		return dictionaryElement == null ? null : dictionaryElement.getAttributeValue(DictionaryTerm.URL);
		
	}
	
	public String getRegexString() {
		return dictionaryElement == null ? null : dictionaryElement.getAttributeValue(DefaultAMIDictionary.REGEX);
		
	}
	
	public List<DictionaryTerm> checkNonMatchingTerms() {
		String regexString = getRegexString();
		List<DictionaryTerm> nonMatchingTermList = new ArrayList<DictionaryTerm>();
		if (regexString == null) {
			LOG.warn("cannot find regex");
		} else {
			Pattern pattern = Pattern.compile(regexString);
			if (dictionaryTermList != null) {
				for (DictionaryTerm term : dictionaryTermList) {
					String s =  term.getTermPhrase().getString();
					Matcher matcher = pattern.matcher(s);
					if (!matcher.matches()) {
						nonMatchingTermList.add(term);
					}
				}
			}
		}		
		return nonMatchingTermList;
	}
	
	public String getTitle() {
		return dictionaryElement == null ? null : dictionaryElement.getAttributeValue(DefaultAMIDictionary.TITLE);
	}

	public Element getDictionaryElement() {
		return dictionaryElement;
	}

	public void setDictionaryName(String name) {
		this.dictionaryName = name;
	}

	public void setOutputDir(File dictionaryDir) {
		this.outputDir = dictionaryDir;
	}

	public void setInputDir(File dictionaryDir) {
		this.inputDir = dictionaryDir;
	}

	/**
	 * defaults to infinite range
	 * @throws IOException
	 */
	public void annotateDictionaryWithWikidata() throws IOException {
		this.annotateDictionaryWithWikidata(0,  Integer.MAX_VALUE);
	}

	public void annotateDictionaryWithWikidata(int start, int end) throws IOException {
		if (inputDir == null) {
			throw new RuntimeException("no input dictionaryDir");
		}
		File inputDictionary = new File(inputDir, getDictionaryName()+".xml");
		if (!inputDictionary.exists() || inputDictionary.isDirectory()) {
			throw new RuntimeException("input dictionaryDir " + inputDictionary + " does not exist");
		}
		Element inputHtml = XMLUtil.parseQuietlyToDocument(inputDictionary).getRootElement();
		WikipediaLookup wikipediaLookup = new WikipediaLookup(this);
		wikipediaLookup.setRange(start, end);
		wikipediaLookup.lookupEntriesAndAnnotate(inputHtml);
	}

	public File getOutputDir() {
		return outputDir;
	}

	public String getDictionaryName() {
		if (dictionaryName == null) {
			dictionaryName = getTitle();
		}
		return dictionaryName;
	}

	/** Not yet finished */
	public HtmlDiv createHtmlElement() {
		HtmlDiv div = null;
		if (dictionaryElement != null) {
			div = new HtmlDiv();
			addTitle(div);
			addDescriptionsToHtml(div);
			div.appendChild(addEntryListToHtml());
		}
//		LOG.debug("div "+div.toXML());
		return div;
		
	}

	private HtmlTable addEntryListToHtml() {
		HtmlTable table = new HtmlTable();
		List<String> reservedAttNames = Arrays.asList(
				DictionaryTerm.ID,
				DictionaryTerm.TERM,
				DictionaryTerm.NAME,
				DictionaryTerm.URL,
				WIKIDATA_URL,
				PAGE_CONTENT,
				IMAGE
				);
		List<Element> entryList = XMLUtil.getQueryElements(dictionaryElement, ENTRY);
		if (entryList.size() > 0) {
			table = new HtmlTable();
			List<String> nonReservedAttNames = getSortedNonReservedAttNames(reservedAttNames, entryList);
			createAndAddThead(table, reservedAttNames, nonReservedAttNames);
			createAndAddTbody(table, reservedAttNames, entryList, nonReservedAttNames);
		}
		return table;
	}

	private void createAndAddTbody(HtmlTable table, List<String> reservedAttNames, List<Element> entryList,
			List<String> nonReservedAttNames) {
		HtmlTbody tBody = table.getOrCreateTbody();
		for (Element entry : entryList) {
			System.err.print("+"); // TODO progress indicator
			HtmlTr tr = new HtmlTr();
			tBody.appendChild(tr);
			List<HtmlTd> tdList = addEmptyHeader(reservedAttNames, nonReservedAttNames, tr);
			addReservedFields(entry, tdList);
			addNonReservedFields(nonReservedAttNames, entry, tdList);
		}
	}

	private void createAndAddThead(HtmlTable table, List<String> reservedAttNames, List<String> nonReservedAttNames) {
		HtmlThead thead = table.getOrCreateThead();
		HtmlTr headTr = thead.getOrCreateChildTr();
		for (String reserved : reservedAttNames) {
			headTr.appendChild(HtmlTh.createAndWrapText(reserved));
		}
		for (String nonReserved : nonReservedAttNames) {
			headTr.appendChild(HtmlTh.createAndWrapText(nonReserved));
		}
	}

	private void addNonReservedFields(List<String> nonReservedAttNames, Element entry, List<HtmlTd> tdList) {
		for (int i = 0; i < entry.getAttributeCount(); i++) {
			Attribute att = entry.getAttribute(i);
			String attName = att.getLocalName();
			if (nonReservedAttNames.contains(attName)) {
				int idx = nonReservedAttNames.indexOf(attName);
				if (idx != -1) {
					tdList.get(idx).setValue(att.getValue());
				}
			}
		}
	}

	private void addReservedFields(Element entry, List<HtmlTd> tdList) {
		String id = entry.getAttributeValue(DictionaryTerm.ID);
		String term = entry.getAttributeValue(DictionaryTerm.TERM);
		String name = entry.getAttributeValue(DictionaryTerm.NAME);
		String urlS = entry.getAttributeValue(DictionaryTerm.URL);
		if (id != null) tdList.get(0).setValue(id);
		if (name != null) tdList.get(2).setValue(name);
		if (term != null) {
			if (urlS == null) {
				tdList.get(1).setValue(term);
			} else {
				HtmlA a = new HtmlA();
				a.setHref(getBaseUrl()+urlS);
				a.setValue(term);
				tdList.get(1).appendChild(a);
			}
		}
		if (urlS != null) {
			urlS = WIKIPEDIA_BASE + urlS;
			tdList.get(3).setValue(urlS);
			HtmlElement pageElement = null;
			try {
				pageElement = HtmlUtil.readAndCreateElement(urlS);
				String s = pageElement.toXML();
				WikipediaPageInfo pageInfo = WikipediaPageInfo.createPageInfo(pageElement);
				if (pageInfo == null) {
					LOG.error("Cannot read pageInfo");
					return;
				}
				HtmlA wikiDataLink = pageInfo.getLinkToWikidataItem();
				if (wikiDataLink != null) {
					tdList.get(WIKIDATA_LINK_COLUMN).appendChild(wikiDataLink);
				}
				String centraLdescription = pageInfo.getCentralDescription();
				if (centraLdescription != null) {
					tdList.get(CENTRAL_DESCRIPTION_COLUMN).appendChild(centraLdescription);
				}
				HtmlImg pageImage = pageInfo.getPageImage();
				if (pageImage != null) {
					tdList.get(PAGE_IMAGE_LINK_COLUMN).appendChild(pageImage.copy());
				}
//				<tr id="mw-wikibase-pageinfo-entity-id"><td style="vertical-align: top;">Wikidata item ID</td><td><a class="extiw wb-entity-link external" href="https://www.wikidata.org/wiki/Special:EntityPage/Q720467">Q720467</a></td></tr>
//				<tr id="mw-wikibase-pageinfo-description-central"><td style="vertical-align: top;">Central description</td><td>family of insects</td></tr>
//				<tr id="mw-pageimages-info-label"><td style="vertical-align: top;">Page image</td><td><a href="/wiki/File:Simulium_trifasciatum_adult_(British_Entomology_by_John_Curtis-_765).png" class="image">
//				    <img alt="Simulium trifasciatum adult (British Entomology by John Curtis- 765).png" src="//upload.wikimedia.org/wikipedia/commons/thumb/7/76/Simulium_trifasciatum_adult_%28British_Entomology_by_John_Curtis-_765%29.png/220px-Simulium_trifasciatum_adult_%28British_Entomology_by_John_Curtis-_765%29.png" width="220" height="178" data-file-width="333" data-file-height="270" /></a></td></tr>
				
			} catch (Exception e) {
				System.err.print(" !! "); // TODO progress indicator
				LOG.trace("Cannot create page: "+urlS+" "+e.getMessage());
			}
		}
		return;
		
	}

	private List<HtmlTd> addEmptyHeader(List<String> reservedAttNames, List<String> nonReservedAttNames, HtmlTr tr) {
		List<HtmlTd> tdList = new ArrayList<HtmlTd>();
		for (int i = 0; i < reservedAttNames.size() + nonReservedAttNames.size(); i++) {
			HtmlTd td = new HtmlTd();
			tr.appendChild(td);
			tdList.add(td);
		}
		return tdList;
	}

	private String getBaseUrl() {
		return baseUrl;
	}

	private List<String> getSortedNonReservedAttNames(List<String> reservedAttNames, List<Element> entryList) {
		Set<String> attNameSet = new HashSet<String>();
		for (Element entry : entryList) {
			for (int i = 0; i < entry.getAttributeCount(); i++) {
				attNameSet.add(entry.getAttribute(i).getLocalName());
			}
		}
		attNameSet.removeAll(reservedAttNames);
		List<String> attNames = new ArrayList<String>(attNameSet);
		Collections.sort(attNames);
		return attNames;
	}

	private HtmlSpan createNonReservedSpan(Attribute att, String attName) {
		HtmlSpan span = new HtmlSpan();
		HtmlI it = new HtmlI();
		it.appendChild(attName);
		span.appendChild(it);
		span.appendChild(": ");
		HtmlB b = new HtmlB();
		b.appendChild(att.getValue());
		span.appendChild(b);
		return span;
	}

	private void addTitle(HtmlDiv div) {
		String titleString = dictionaryElement.getAttributeValue(TITLE);
		if (titleString != null) {
			HtmlTitle title = new HtmlTitle(titleString);
			div.appendChild(title);
		}
	}

	private void addDescriptionsToHtml(HtmlDiv div) {
		List<Element> descList = XMLUtil.getQueryElements(dictionaryElement, DESC);
		if (descList.size() > 0) {
			HtmlCaption caption = new HtmlCaption();
			for (Element desc : descList) {
				String descString = desc.getValue();
				HtmlP para = new HtmlP(descString);
				caption.appendChild(para);
			}
			div.appendChild(caption);

		}
	}
	
	public List<Element> getEntryList() {
		List<Element> entryList = XMLUtil.getQueryElements(dictionaryElement, "./*[local-name()='" + ENTRY + "']");
		return entryList;
	}

	public List<String> getLowercaseTerms() {
		List<DictionaryTerm> terms = getTermsSortedBySize();
		LOG.debug("terms "+terms);
		return null;
	}

	public Set<String> searchDictionaryForTerms(Set<String> searchTerms) {
		Set<String> rawTermSet = getRawLowercaseTermSet();
		Set<String> foundSet = new HashSet<>();
		Set<String> missedSet = new HashSet<>();
		for (String searchTerm : searchTerms) {
			if (rawTermSet.contains(searchTerm)) {
				foundSet.add(searchTerm);
			} else {
				missedSet.add(searchTerm);
			}
		}
		return foundSet;
	}

	public Set<String> searchTermsInFiles(List<String> searchTermFilenames) {
		Set<String> allSearchTerms = new HashSet<>();
		for (String searchTermFilename : searchTermFilenames) {
			allSearchTerms.addAll(extractSearchTerms(searchTermFilename));
		}
		Set<String> foundTerms = searchDictionaryForTerms(allSearchTerms);
		return foundTerms;
	}

	private Set<String> extractSearchTerms(String searchTermFilename) {
		Set<String> allSearchTerms = new HashSet<>();
		File file = new File(searchTermFilename);
		try {
			List<String> searchTerms0 = FileUtils.readLines(file, Charset.forName(DictionarySearchTool.UTF_8));
			allSearchTerms.addAll(searchTerms0);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read "+file);
		}
		return allSearchTerms;
	}
	
}
