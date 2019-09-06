package org.contentmine.cproject.metadata.crossref;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.JsonUtils;
import org.contentmine.cproject.util.RectangularTable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class CrossrefMD extends AbstractMetadata {



	private static final Logger LOG = Logger.getLogger(CrossrefMD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** this is a mess. the original name was too general.
	 * 
	 */
	private static final String CTREE_RESULT_JSON = "crossref_result.json";
	private static final String CPROJECT_RESULT_JSON = "crossref_results.json";
	
	public static final String AUTHOR = "author";
	public static final String CHAIR = "chair";
	public static final String CLINICAL_TRIAL_NUMBER = "clinical-trial-number";
	public static final String CONTAINER_TITLE = "container-title";
	public static final String CREATED = "created";
	public static final String DATE_PARTS = "date-parts";
	private static final String DATE_TIME = "date-time";
	public static final String DOI = "DOI";
	public static final String FUNDER = "funder";
	public static final String INDEXED = "indexed";
	public static final String ISSN = "ISSN";
	public static final String ISSUE = "issue";
	public static final String LICENSE = "license";
	public static final String LINK = "link";
	public static final String PAGE = "page";
	public static final String PREFIX = "prefix";
	public static final String PUBLISHER = "publisher";
	public static final String REFERENCE_COUNT = "reference-count";
	public static final String SUBJECT = "subject";
	public static final String TITLE = "title";
	public static final String TRANSLATOR = "translator";
	public static final String TYPE = "type";
	public static final String URL = "URL";
	public static final String VOLUME = "volume";
	
	public static String[] IN_KEYS = {
			AUTHOR,
			CONTAINER_TITLE,
			CREATED,
			DOI,
			FUNDER,
			INDEXED,
			ISSN,
			ISSUE,
			LICENSE,
			LINK,
			PAGE,
			PREFIX,
			PUBLISHER,
			REFERENCE_COUNT,
			SUBJECT,
			TITLE,
			TYPE,
			URL,
			VOLUME,
	};
	public final static List<String> INCLUDE_KEYS = Arrays.asList(IN_KEYS);
	public final static List<String> SINGLE_FROM_ARRAY = 
			Arrays.asList(new String[]{TITLE, CONTAINER_TITLE, ISSN});
	public final static List<String> PERSONS = 
			Arrays.asList(new String[]{AUTHOR, CHAIR, TRANSLATOR});
	public final static List<String> ARRAYS = Arrays.asList(new String[]{SUBJECT});
	
	public static String[] EX_KEYS = {
	        "alternative-id",
	        "archive",
	        "article-number",
	        "assertion",
	        CLINICAL_TRIAL_NUMBER,
	        DATE_TIME,
	        "deposited",
	        "editor",
	        "issued",
	        "ISBN",
	        "member",
	        "published-online",
	        "published-print",
	        "score",
	        "source",
	        "subtitle",
	        "timestamp",
	        "update-policy",
	        "update-to",
	};
	public final static List<String> EXCLUDE_KEYS = Arrays.asList(EX_KEYS);
	
	public static final String PUBLISHER_PATH = "$.publisher";
	public static final String TITLE_PATH = "$.title";
	public static final String TYPE_PATH = "$.type";
	public static final String URL_PATH = "$.URL";

//	public static List<String> getHeaders() {
//		List<String> headers = Arrays.asList(
//			new String[] {
//				"URL",
//				"Title",
//				"Date",
//				"PDFURL",
//				"DownloadedPDF",
//				"HTMLURL",
//				"DownloadedHTML",
//				"XMLURL",
//				"DownloadedXML",
//				"DOI",
//				"Publisher",
//				"Volume",
//				"AuthorList",
//				"Issue",
//				"FirstPage",
//				"Description",
//				"Abstract",
//				"Journal",
//				"License",
//				"Links",
//				"Copyright",
//				"ISSN",
//				"Keywords",
//				"QuickscrapeMD",
//				"CrossrefMD",
//				"PublisherMD",
//			});
//		return headers;
//	}
	



	public static AbstractMetadata createMetadata() {
		return new CrossrefMD();
	}

	private CRFunderList funderList;
	private CRLicenseList licenseList;
	private CRLinkList linkList;

	private String dateTime;
	private Map<String, CRPersonList> personListByKeyMap;
	private HashSet<String> arrayNames;
	private HashSet<String> keyNames;
	
	public CrossrefMD() {
		super();
		init();
	}

	private void init() {
		this.arrayNames = new HashSet<String>();
		this.keyNames = new HashSet<String>();
	}

	public boolean analyzeSpecificObject(JsonElement value) {
		getOrCreatePersonListByKeyMap();
		ensureFunderList();
		ensureLicenseList();
		ensureLinkList();
		ensureStringValueMap();
		ensureStringListValueMap();
		boolean analyzed = true;
		if (DATE_PARTS.equals(currentKey)) {
			dateTime = CrossrefDateTime.createFrom(value);
			if (dateTime == null) {
				LOG.warn("bad dateTime");
			}
		} else if (PERSONS.contains(currentKey)) {
			getOrCreatePersonListByKeyMap();
			CRPersonList personListFromValue = CRPersonList.createFrom(value);
			CRPersonList personListForCurrentKey = getOrCreatePersonListForCurrentKey();
			personListForCurrentKey.addAll(personListFromValue.getPersonList());
		} else if (FUNDER.equals(currentKey)) {
			ensureFunderList();
			CRFunderList funders = CRFunderList.createFrom(value);
//			LOG.debug("FUNDERS: "+funders);
			funderList.addAll(funders);
		} else if (LICENSE.equals(currentKey)) {
			ensureLicenseList();
			CRLicenseList licenses = CRLicenseList.createFrom(value);
			licenseList.addAll(licenses);
		} else if (LINK.equals(currentKey)) {
			ensureLinkList();
			CRLinkList links = CRLinkList.createFrom(value);
//			LOG.debug("LINK: "+links);
			linkList.addAll(links);
		} else if (CREATED.equals(currentKey)) {
			if (!(value instanceof JsonObject)) {
				dateTime = null;
				LOG.warn("bad/null date-time");
			} else {
				dateTime = ((JsonObject)value).get(DATE_TIME).getAsString();
			}
		} else if (EXCLUDE_KEYS.contains(currentKey)) {
//			LOG.debug("EXCLUDE "+currentKey+"; "+value);
			analyzed = true; // because omitted
		} else if (SINGLE_FROM_ARRAY.contains(currentKey)) {
			analyzed = true;
			String value1 = JsonUtils.getFirstStringValue(value.getAsJsonArray());
//			LOG.trace("KV: "+currentKey+": "+value1);
			addString(value1);
		} else if (ARRAYS.contains(currentKey)) {
//			LOG.debug("ARRAYS "+currentKey+"; "+value);
			analyzed = true;
			List<String> strings = JsonUtils.getStringList(value.getAsJsonArray());
			addStringList(strings);
		} else if (value.isJsonArray()){
			if (!this.arrayNames.contains(currentKey)) {
				this.arrayNames.add(currentKey);
				LOG.debug("ARRAY "+currentKey+"; "+value+ "; further messages terminated");
			}
		} else if (INCLUDE_KEYS.contains(currentKey) && value.isJsonPrimitive()) {
			String s = ((JsonPrimitive)value).getAsString();
			addString(s);
			analyzed = true;
		} else if (INCLUDE_KEYS.contains(currentKey)) {
//			LOG.debug("INCLUDE "+currentKey+"; "+value);
			analyzed = false; // because still to be processed
		} else {
			analyzed = false;
			if (!this.keyNames.contains(currentKey)) {
				this.keyNames.add(currentKey);
				LOG.warn("**********Unrecognised key*********** in ARRAY "+currentKey+"; "+value+ "; further messages terminated");
			}

//			throw new RuntimeException("Unrecognised key "+currentKey);
		}
		return analyzed;

	}

	private CRPersonList getOrCreatePersonListForCurrentKey() {
		CRPersonList personList = personListByKeyMap.get(currentKey);
		if (personList == null) {
			personList = new CRPersonList();
			personListByKeyMap.put(currentKey, personList);
		}
		return personList;
	}

//	static Set<String> keys  = new HashSet<String>();
	
	private Map<String, CRPersonList> getOrCreatePersonListByKeyMap() {
		if (personListByKeyMap == null) {
			personListByKeyMap = new HashMap<String, CRPersonList>();
		}
		return personListByKeyMap;
	}

	private void addStringList(List<String> strings) {
		ensureStringListValueMap();
		stringListValueMap.put(currentKey, strings);
//		if (keys.add(currentKey)) {
//			LOG.debug(keys);
//		}
//		LOG.debug("SL "+stringListValueMap);
	}

	private void addString(String value) {
		ensureStringValueMap();
		stringValueMap.put(currentKey, value);
	}

	private void ensureStringValueMap() {
		if (stringValueMap == null) {
			stringValueMap = ArrayListMultimap.create();
		}
	}
	
	public Multimap<String, String> getStringValueMap() {
		return stringValueMap;
	}
	
	public Map<String, Multiset<String>> getStringMultimapByKey() {
		ensureStringValueMap();
		Map<String, Multiset<String>> multimapByKey = new HashMap<String, Multiset<String>>();
		for (String key : stringValueMap.keySet()) {
			List<String> values = new ArrayList<String>(stringValueMap.get(key)); 
			Multiset<String> multiset = HashMultiset.create();
			multiset.addAll(values);
			multimapByKey.put(key, multiset);
		}
		LOG.debug("X"+stringListValueMap.keySet());

		return multimapByKey;
	}

	private void ensureStringListValueMap() {
		if (stringListValueMap == null) {
			stringListValueMap = ArrayListMultimap.create();
		}
	}

	public Multimap<String, List<String>> getStringListValueMap() {
		return stringListValueMap;
	}

//	private void ensurePersonList() {
//		if (personList == null) {
//			authorList = new CRPersonList();
//		}
//	}

	public CRPersonList getCRAuthorList() {
		return this.getOrCreatePersonList(AUTHOR);
	}

	private void ensureFunderList() {
		if (funderList == null) {
			funderList = new CRFunderList();
		}
	}

	public CRFunderList getFunderList() {
		return funderList;
	}

	private void ensureLicenseList() {
		if (licenseList == null) {
			licenseList = new CRLicenseList();
		}
	}

	public CRLicenseList getLicenseList() {
		return licenseList;
	}

	private void ensureLinkList() {
		if (linkList == null) {
			linkList = new CRLinkList();
		}
	}
	

	public CRLinkList getLinkList() {
		return linkList;
	}

	public static RectangularTable createCrossrefSpreadsheet(CTreeList cTreeList, File csvFile) {

//		ScraperSet scraperSet = new ScraperSet(ScraperTest.SCRAPER_DIR);
//		Map<File, JsonElement> elementsByFile = scraperSet.getJsonElementByFile();
//		List<Multiset.Entry<String>> elements = scraperSet.getOrCreateScraperElementsByCount();
//		List<String> headings = new ArrayList<String>();
//		for (Multiset.Entry<String> entry : elements) {
//			headings.add(entry.getElement());
//		}
		RectangularTable csvTable = new RectangularTable();
//		csvTable.addRow(headings);
//		List<File> files = new ArrayList<File>(elementsByFile.keySet());
//		for (File file : files) {
//			List<String> row = new ArrayList<String>();
//			for (int i = 0; i < headings.size(); i++) {
//				row.add("");
//			}
//			JsonElement element = elementsByFile.get(file);
//			JsonElement elements1 = element.getAsJsonObject().get(ScraperSet.ELEMENTS);
//			Set<Map.Entry<String, JsonElement>> entries = elements1.getAsJsonObject().entrySet();
//			for (Map.Entry<String, JsonElement> entry : entries) {
//				String name = entry.getKey();
//				int idx = headings.indexOf(name);
//				if (idx ==  -1) {
//					LOG.error("bad key "+name);
//				}
//				row.set(idx, name);
//			}
//			csvTable.addRow(row);
//		}
//		csvTable.writeCsvFile();
		return csvTable;
	}

//	public Multiset<String> getAuthorSet() {
//		Multiset<String> authorSet = HashMultiset.create();
//		authorSet.addAll(getAuthorList().getAuthorList());
//		return authorSet;
//	}

	public List<Multiset.Entry<String>> getPersonEntriesSortedByCount(String personType) {
		CRPersonList personList = getOrCreatePersonList(personType);
		return personList == null ? null : personList.getEntriesSortedByCount();
	}

	private CRPersonList getOrCreatePersonList(String personType) {
		CRPersonList personList = getOrCreatePersonListByKeyMap().get(personType);
		return personList;
	}

	public List<Multiset.Entry<String>> getFunderEntriesSortedByCount() {
		ensureFunderList();
		return funderList.getEntriesSortedByCount();
	}

	public List<Multiset.Entry<String>> getLicenseEntriesSortedByCount() {
		ensureLicenseList();
		return licenseList.getEntriesSortedByCount();
	}

	public List<Multiset.Entry<String>> getLinkEntriesSortedByCount() {
		ensureLinkList();
		return linkList.getEntriesSortedByCount();
	}

	@Override
	public String getURL() {
		Collection<String> c = stringValueMap == null ? null : stringValueMap.get(URL);
		return c == null || c.size() == 0 ? null : new ArrayList<String>(c).get(0);
		
	}

	@Override
	public String hasQuickscrapeMetadata() {
		return "N";
	}

	@Override
	public String hasCrossrefMetadata() {
		return "N";
	}

	@Override
	public String hasPublisherMetadata() {
		return "N";
	}

	@Override
	public String getTitle() {
		return getSingleString(TITLE);
	}

	@Override
	public String getDate() {
		return dateTime;
	}

	@Override
	public String getFulltextPDFURL() {
		return null;
	}

	@Override
	public String hasDownloadedFulltextPDF() {
		return "N";
	}

	@Override
	public String getFulltextHTMLURL() {
		return null;
	}

	@Override
	public String hasDownloadedFulltextHTML() {
		return "N";
	}

	@Override
	public String getFulltextXMLURL() {
		return null;
	}

	@Override
	public String hasDownloadedFulltextXML() {
		return "N";
	}

	@Override
	public String getDOI() {
		return getSingleString(DOI);
	}
	
	@Override
	public String getPublisher() {
		return getSingleString(PUBLISHER);
	}

	@Override
	public String getReferenceCount() {
		return getSingleString(REFERENCE_COUNT);
	}

	@Override
	public String getVolume() {
		return getSingleString(VOLUME);
	}

	@Override
	public String getPrefix() {
		return getSingleString(PREFIX);
	}

	@Override
	public List<String> getAuthorListAsStrings() {
		CRPersonList authorList = getOrCreatePersonListByKeyMap().get(AUTHOR);
		return authorList == null ? null : authorList.getPersonsAsStrings();
	}

	@Override
	public String getIssue() {
		return getSingleString(ISSUE);
	}

	@Override
	public String getFirstPage() {
		return getSingleString(PAGE);
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getAbstract() {
		return null;
	}

	@Override
	public String getJournal() {
		return getSingleString(CONTAINER_TITLE);
	}

	@Override
	public String getLicense() {
		return licenseList.toString();
	}

//	@Override
	public List<String> getLicenseStringList() {
		List<String> licenseListS = getListFromEntries(licenseList.getEntriesSortedByCount());
		return licenseListS;
	}

	private static List<String> getListFromEntries(List<Entry<String>> entriesSortedByCount) {
		List<String> strings = new ArrayList<String>();
		for (Entry<String> entry : entriesSortedByCount) {
			String s = entry.toString();
			strings.add(s);
		}
		return strings;
	}

	@Override
	public String getLinks() {
		return linkList.toString();
	}

	@Override
	public String getCopyright() {
		return null;
	}

	@Override
	public String getISSN() {
		return getSingleString(ISSN);
	}

	@Override
	public String getType() {
		return getSingleString(TYPE);
	}

	@Override
	public String getAbstractURL() {
		return null;
	}

	@Override
	public String getAuthorEmail() {
		return null;
	}

	@Override
	public String getAuthorInstitution() {
		return null;
	}

	@Override
	public String getCitations() {
		return getSingleString(REFERENCE_COUNT);
	}

	@Override
	public String getCreator() {
		return null;
	}

	@Override
	public String getFulltextPublicURL() {
		return null;
	}

	@Override
	public String getKeywords() {
		List<List<String>> values = getStringListValues(stringListValueMap, SUBJECT);
		return values.toString();
	}

	@Override
	public String getLanguage() {
		return null;
	}
	
	@Override
	public String getLastPage() {
		return null;
	}

	@Override
	public String getPublicURL() {
		return getSingleString(URL);
	}

	@Override
	public String getRights() {
		return null;
	}
	
	// helper methods
	
	private String getSingleString(String key) {
		Collection<String> values = stringValueMap.get(key);
		return (values == null || values.size() == 0) ? null : (String) values.toArray()[0];
	}


	private static List<List<String>> getStringListValues(Multimap<String,List<String>> stringListValueMap, String key) {
		List<List<String>> values = new ArrayList<List<String>>();
		if (stringListValueMap != null) {
			Collection<List<String>> values0 = stringListValueMap.get(key);
			values = values0 == null ? new ArrayList<List<String>>() : new ArrayList<List<String>>(values0);
		}
		return values;
	}

	public String toString() {
		String s = "";
		s += "funderList: "+funderList+"\n";
		s += " licenseList: "+licenseList+"\n";
		s += " linkList: "+linkList+"\n";
		s += " dateTime: "+dateTime+"\n";
		return s;
	}

	@Override
	protected String getCTreeMetadataFilename() {
		return CTREE_RESULT_JSON;
	}

	@Override
	protected String getCProjectMetadataFilename() {
		return CPROJECT_RESULT_JSON;
	}


}
