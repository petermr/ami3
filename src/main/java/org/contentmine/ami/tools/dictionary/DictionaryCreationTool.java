package org.contentmine.ami.tools.dictionary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.dictionary.CMJsonDictionary;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.lookups.WikipediaDictionary;
import org.contentmine.ami.tools.AMIDict;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.contentmine.ami.tools.download.CurlDownloader;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectTabColumn;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlTCell;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.html.util.HtmlUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import nu.xom.Attribute;
import nu.xom.Element;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
		name = "create",
		description = {
				"creates dictionaries from text, Wikimedia, etc..",
				"TBD"
				+ ""
		})
public class DictionaryCreationTool extends AbstractAMIDictTool {
	
	static final String BINDING = "binding";
	static final String FLAG_A_Z = ":flag-[a-z]+:";
	public static final Logger LOG = LogManager.getLogger(DictionaryCreationTool.class);

	private static final String HTTPS_EN_WIKIPEDIA_ORG = "https://en.wikipedia.org";
	static final String LITERAL = "literal";
	static final String TERM = "term";
	static final String NAME = "name";
	private static final String SLASH_WIKI_SLASH = "/wiki/";
	static final String SYNONYMS = "synonyms";
	static final String URI = "uri";
	static final String WIKIDATA = "wikidata";
	static final String WIKIDATA_ALT_LABEL = "wikidataAltLabel";
	static final String WIKIDATA_LABEL = "wikidataLabel";
	static final String WIKIPEDIA = "wikipedia";
	
	private final static String WIKIPEDIA_BASE = HTTPS_EN_WIKIPEDIA_ORG + SLASH_WIKI_SLASH;
	
	private static final String HTTP = "http";
	private static final String CM_PREFIX = "CM.";
	private static final String WIKITABLE = "wikitable";

	public final static String WIKIDATA_SPARQL_ENDPOINT = "https://query.wikidata.org/sparql?query=";

//	final static List<String> ALLOWED_NAMES =  Arrays.asList(new String[] {
//			NAME,TERM,WIKIPEDIA,WIKIDATA_ALT_LABEL,WIKIDATA_LABEL 
//	});

	List<String> termList;
	private List<String> nameList;
	private List<String> linkList;
	private String currentTemplateName;
	private WikipediaDictionary wikipediaDictionary;
	private List<RectTabColumn> dataColList;

	@Option(names = {"--datacols"}, 
			split=",",
			arity="1..*",
		    paramLabel = "datacol",
		description = "use these columns (by name) as additional data fields in dictionary. datacols='foo,bar' creates "
				+ "foo='fooval1' bar='barval1' if present. No controlled use or vocabulary and no hyperlinks."
			)
	private String[] dataCols;
	
	// converted from args    
	private List<RectTabColumn> hrefColList;
	
	@Option(names = {"--hrefcols"}, 
			split=",",
			arity="1..*",
		 	paramLabel = "hrefcol",
		description = "external hyperlink column from table; might be Wikidata or remote site(s)"
			)
	
	
	private String[] hrefCols;

	/** moved down */
    @Option(names = {"--informat"}, 
    		arity="1",
    		paramLabel = "input format",
    		description = "input format (${COMPLETION-CANDIDATES})"
    		)
    protected InputFormat informat = InputFormat.list;
    
    @Option(names = {"--linkcol"}, 
    		arity="1",
    		description = "column to extract link to internal pages. main use Wikipedia. Defaults to the 'name' column"
    		)
	public String linkCol;

    @Option(names = {"--namecol"}, 
//    		split=",",
    		arity="1..*",
    		description = "column(s) to extract name; use exact case (e.g. Common name)"
    		)
	public String nameCol;
    
    @Option(names = {"--outformats"}, 
    		arity="1..*",
    		split=",",
    	    paramLabel = "output format",
    		description = "output format (${COMPLETION-CANDIDATES}); default XML"
    		)
    protected DictionaryFileFormat[] outformats = new DictionaryFileFormat[] {DictionaryFileFormat.xml};

	@Option(names = {"--query"}, 
			arity="0..1",
//		    defaultValue="10",
		    paramLabel = "query",
			description = "generate query for cut and paste into EPMC or similar. "
					+ "value sets size of chunks (too large crashes EPMC). If missing, no query generated."
					+ "Not very useful."
			)
	private Integer queryChunk = null;
	
	@Option(names = {"--sparqlmap"},
			split=",",
			description = "maps wikidata/SPARQL name onto AMIDict names. "
			+ "builtin names = id, term, name, wikidataURL, wikidataID, wikipediaURL, wikipediaPage, description, "
			+ "wikidata names are _p[\\d]+_* (properties)  and _q[\\d]+_* (items), "
			+ "other names are _[a-zA-Z]* , everything else is an error."
			+ "updated 2020-08-19. (this is still liable to change)"	
			+ "Mandatory for wikisparql inputs"
			
			) 
	/** WikidataSparql may modify this, so force it to copy it */
	private Map<String, String> sparqlNameByAmiName = new HashMap<>();
	
	@Option(names = {"--sparqlquery"},
			description = "File with wikidata query"
			) 
	private File sparqlQueryFile = null;
	
	
	@Option(names = {"--synonyms"},
			description = "pointers (labels) to synonyms retrived from source. Syntax depends on source type."
					+ "for `sparql` and `synonyms=`wikidataAltLabels` this retrieves a single String with comma-separated "
					+ "synonyms (and maybe extraneous commas)."
					+ " DEPRECATED - will move to `ami update`"
					
			) List<String> synonymList = null;
	
    @Option(names = {"--template"}, 
    		arity="1..*",
    		
    		description = "names of Wikipedia Templates, e.g. Viral_systemic_diseases "
    				+ "(note underscores not spaces). Dictionaries will be created with lowercase"
    				+ "names and all punctuation removed).")
	public List<String> templateNames;

    @Option(names = {"--termcol"}, 
    		arity="1",
    		description = "column(s) to extract term; use exact case (e.g. Term). Could be same as namecol"
    		)
	public String termCol;
    
    @Option(names = {"--termfile"}, 
    		arity="1",
//    		split=",",
    		description = "list of terms in file, line-separated. <basename> will become dictionary name,"
    				+ " i.e. terpenes.txt creates basename=terpenes")
    protected File termfile;

	@Option(names = {"--terms"}, 
			arity="1..*",
			split=",",
			description = "list of terms (entries), space-separated. Requires `inputname` or `dictionary`")
	private List<String> terms;
	private Set<String> termSet;
	

	@Option(names= {"--transformName"},
			split="@",
			description="create new attribute name (key) and populate  transformed map value. Syntax:"
					+ "newAttName@operation(oldAttName,operationValue) where 'operation' is REGEX and operationValue "
					+ "is a regex with captures. More operations may be added later (e.g. delete and append)."
					+ " DEPRECATED. Will move to ami update"
			)
	private Map<String, String> transformationByAmiName = new HashMap<>();
			
	
    @Option(names = {"--wptype"}, 
    		arity="1",
    		description = "type of input (HTML , mediawiki)")
    protected WikiFormat wptype;

    @CommandLine.Mixin
	private AMIDict.GeneralOptionsMixin generalOptionsMixin;
	
	private HtmlTbody tBody;
	static final String CL = "dicc>";
	
	private WikidataSparql wikidataSparql;
	private String sparqlQuery;
//	private Element sparqlXml;

	public DictionaryCreationTool() {
	}

//	@Override
	protected void parseSpecifics() {
		super.parseSpecifics();
		getDictionaryName();
		LOG.info("dictionaryName: "+dictionaryName);
		dictOutformat = (this.outformats == null || this.outformats.length != 1) ? null : this.outformats[0];
		if (sparqlQueryFile != null) {
			getOrCreateWikidataSparql();
			wikidataSparql.sparqlXml = readQuerySearchWikidataForSparqlXml();
		}
		if (informat != null && informat.toString().startsWith("wikisparql")) {
			getOrCreateWikidataSparql();
			parseSparql(wikidataSparql);
		}
		if (this.templateNames != null) {
			createFilenamesForWikimediaInput();
		}
		if (this.testString != null) {
			this.testString = this.testString.replaceAll("%20", " ");
			LOG.debug(SPECIAL, "testString      "+this.testString);
		}
		if (terms != null) termList = new ArrayList<>(terms);
		
		wikiLinkList = (this.wikiLinks == null) ? new ArrayList<WikiLink>() :
		     new ArrayList<WikiLink>(Arrays.asList(this.wikiLinks));
	}

	private Element readQuerySearchWikidataForSparqlXml() {
		Element sparqlXml = null;
		try {
			sparqlQuery = FileUtils.readFileToString(sparqlQueryFile, "UTF-8");
			String sparqlXmlString = WikidataSparql.queryWikidata(sparqlQuery);
			sparqlXml = XMLUtil.parseXML(sparqlXmlString);
		} catch (IOException e) {
			throw new RuntimeException("cannot read query file: "+sparqlQueryFile, e);
		}
		return sparqlXml;
	}

	private void getOrCreateWikidataSparql() {
		if (wikidataSparql == null) {
			wikidataSparql = new WikidataSparql(this);
			wikidataSparql.copy(sparqlNameByAmiName);
		}
	}

	private void parseSparql(WikidataSparql wikidataSparql) {
		if (sparqlNameByAmiName == null) {
			throw new RuntimeException("Must give --sparqlmap for " + informat);
		}
		Multimap<String, String> amiListBySparqlName = ArrayListMultimap.create();
		for (String amiName : sparqlNameByAmiName.keySet()) {
			String sparqlName = sparqlNameByAmiName.get(amiName);
			amiListBySparqlName.put(sparqlName, amiName);
		}
		System.out.println(amiListBySparqlName);
	}
	
	@Override
	public void runSub() {
		resetMissingLinks();

		if (templateNames != null) {
			for (String templateName : templateNames) {
				currentTemplateName = templateName;
				input(createDictionaryName(currentTemplateName));
				createAndWriteDictionary();
			}
	
		} else {
			// single input
			createAndWriteDictionary();
		}
		printMissingLinks();

	}

	private void transformValues() {
		if (transformationByAmiName.size() > 0) {
			for (String amiName : transformationByAmiName.keySet()) {
				DictionaryTransformer dictionaryTransformer = 
						new DictionaryTransformer(amiName, transformationByAmiName.get(amiName));
				dictionaryTransformer.transform(simpleDictionary);
			}
		}
	}
	

	private void createFilenamesForWikimediaInput() {
		createTemplateNames();
		this.informat = WikiFormat.mwk.equals(this.wptype) ? InputFormat.mediawikitemplate : InputFormat.wikitemplate;
		if (parent.getDirectoryTopname() == null) {
			LOG.warn("No directory given, using .");
			parent.setDirectoryTopname(".");
		}
	}

	
	private void createTemplateNames() {
		for (int i = 0; i < this.templateNames.size(); i++) {
			String t = this.templateNames.get(i).trim().replaceAll("\\s+", "_");
			this.templateNames.set(i, t);
		}
	}

    private void printMissingLinks() {
       	printMissingLinks("\nMissing wikipedia: ", missingWikipediaSet);
       	printMissingLinks("\nMissing wikidata: ", missingWikidataSet);
	}

	private void printMissingLinks(String title, Set<String> missingLinkSet) {
		if (missingLinkSet.size() > 0) {
			List<String> missingLinkList = new ArrayList<String>(missingLinkSet);
			Collections.sort(missingLinkList);
			int i = 0;
			LOG.debug(SPECIAL, "\n"+title + ":");
			for (String missingLink : missingLinkList) {
				if (i++ % 8 == 0) {
					System.out.println(); // ? TODO progress?
				}
				LOG.warn(missingLink + "; ");
			}
    	}
	}

	private void resetMissingLinks() {
    	missingWikipediaSet = new HashSet<String>();
    	missingWikidataSet = new HashSet<String>();
	}

	private String createDictionaryName(String templateName) {
		return templateName.toLowerCase().replaceAll("[^A-Za-z0-9_\\-]", "");
	}

	private void createAndWriteDictionary() {
		InputStream inputStream = null;
		if (input() != null) {
			inputStream = getInputStreamFromFile();
			if (dictionaryName == null) dictionaryName = input();
		} else if (terms != null) {
			if (dictionaryName == null) {
				throw new RuntimeException("'terms' requires a 'dictionary' option");
			}
			termList = new ArrayList<>(terms);
		} else if (testString != null) {
			inputStream = new ByteArrayInputStream(testString.getBytes());
			dictionaryName = "test";
		}
	
		if (informat == null) {
			LOG.error("no input format given ");
			showstopperEncountered = true;
			return;
		}

		if (inputStream == null && termList == null && sparqlQuery == null) {
			throw new RuntimeException("'input' or 'inputname' or 'sparqlquery' must be given");
		}
//		simpleDictionary = DefaultAMIDictionary.createDictionaryElementWithTitle(dictionaryName);
		simpleDictionary = new SimpleDictionary(dictionaryName);
		if (sparqlQuery != null) {
			wikidataSparql.readSparqlCreateDictionary(inputStream);
			writeDictionary();
		} else if (InputFormat.wikisparqlxml.equals(informat)) {
			wikidataSparql.readSparqlCreateDictionary(inputStream);
			System.err.println("FIX OUTPUT");
			writeDictionary(dictionaryName);
			
		} else if (InputFormat.wikisparqlcsv.equals(informat)) {
			wikidataSparql.readSparqlCsv(inputStream);
			writeDictionary();
		} else {
			readTerms(inputStream);
			synchroniseTermsAndNames();
			writeNamesAndLinks();
		}
		if (inputStream != null) {
			try { inputStream.close(); } catch (IOException ignored) {}
		}
		
	}

	private void readTerms(InputStream inputStream) {
		LOG.debug(SPECIAL, "readTerms");
		if (termList != null) {
			LOG.info(SPECIAL, "reading terms from CL "+termList.size());
		} else if (informat == null) {
			LOG.error("no input format given ");
			showstopperEncountered = true;
		} else if (InputFormat.csv.equals(informat)) {
			readCSV(inputStream);
		} else if (InputFormat.list.equals(informat)) {
			readList(inputStream);
		} else if (InputFormat.wikisparqlxml.equals(informat)) {
			wikidataSparql.readSparqlCreateDictionary(inputStream);
		} else if (InputFormat.mediawikitemplate.equals(informat) ||
				InputFormat.wikicategory.equals(informat) ||
				InputFormat.wikipage.equals(informat) ||
				InputFormat.wikitable.equals(informat) ||
				InputFormat.wikitemplate.equals(informat)
		) {
			wikipediaDictionary = new WikipediaDictionary();
			readWikipediaPage(wikipediaDictionary, inputStream);
		} else {
			LOG.error("unknown inputformat: " + informat);
			showstopperEncountered = true;
			informat = null;
		}
	}

	private InputStream getInputStreamFromFile() {
		InputStream inputStream;
		String directoryTopname = parent.getDirectoryTopname();
		if (directoryTopname == null) {
			throw new RuntimeException("no directory for output given");
		}
		File fileroot = new File(directoryTopname, input());
		directoryTopname = directoryTopname == null ?
				new File(fileroot, wptype.toString()).toString() : directoryTopname;
		inputStream = openInputStream();
		if (inputStream == null) {
			LOG.error(SPECIAL, "NO INPUT STREAM, check HTTP connection/target or file existence");
		}
		return inputStream;
	}

	private RectangularTable readCSV(InputStream inputStream) {
		boolean useHeader = true;
		RectangularTable rectangularTable = null;
		try {
			rectangularTable = RectangularTable.readCSVTable(inputStream, useHeader);
		} catch (IOException e) {
			throw new RuntimeException("cannot read table", e);
		}
		if (termCol == null) {
			throw new RuntimeException("must give termCol");
		}
		termList = rectangularTable.getColumn(termCol).getValues();
		if (termList == null) {
			throw new RuntimeException("Cannot find term column");
		}
		nameList = rectangularTable.getColumn(nameCol).getValues();
		if (dataCols != null) {
			dataColList = rectangularTable.getColumnList(dataCols);
			checkColumnsNotNull(dataColList, dataCols);
		}
		if (hrefCols != null) {
			hrefColList = rectangularTable.getColumnList(hrefCols);
			checkColumnsNotNull(hrefColList, hrefCols);
		}
		return rectangularTable;
	}

	private void readList(InputStream inputStream) {
		try {
			terms = IOUtils.readLines(inputStream, CMineUtil.UTF8_CHARSET);
		} catch (IOException e) {
			throw new RuntimeException("cannot read termfile: "+termfile, e);
		}
		if (terms == null) {
			throw new RuntimeException("Could not read terms");
		}
		createSortedTermList();
	}

	// ============= SPARQL INPUT ============
	
//	private static Pattern flagPattern = Pattern.compile(FLAG_A_Z);



	// ============= END SPARQL INPUT ============

	private void createSortedTermList() {
		if (termSet == null || termList == null) {
			termSet = new HashSet<>();
			for (String term : terms) {
				termSet.add(term.toLowerCase());
			}
			termList = new ArrayList<String>(termSet);
			Collections.sort(termList);
		}
	}
	
    @Override
    /**
     * may need to use curl
     */
	protected InputStream openInputStream() {
		InputStream inputStream = null;
		if (currentTemplateName != null) {
			input(createInput(currentTemplateName));
		}
		if (input() != null) {
			try {
				if (input().startsWith("http")) {
					String output = new CurlDownloader().setUrlString(input()).run();
					inputStream = new ByteArrayInputStream(output.getBytes());
				} else {
					inputStream = super.openInputStream();
				}
			} catch (IOException e) {
				LOG.error("cannot read/open stream: " + input());
				showstopperEncountered = true;
			}
		}
		return inputStream;
	}

	private void synchroniseTermsAndNames() {
		if (nameList == null && termList != null) {
    		nameList = termList;
    	} else if (termList == null && nameList != null) {
    		termList = nameList;
    	}
	}

    /** creates subdirectories from filenames with dots.
     *  foo.bar.plugh creates <directoryTop/foo/bar/
     * @return
     */
    protected File getOrCreateExistingSubdirectory(String dictionaryName) {
    	File newDictionaryDir = null;
    	Pattern pattern = Pattern.compile("(.*)\\.[^\\.]*");
    	if (dictionaryName == null) {
    		throw new RuntimeException("null dictionaryName");
    	}
    	getOrCreateExistingDictionaryTop(parent.getDirectoryTopname());
    	if (dictionaryTop != null) {
    		Matcher matcher = pattern.matcher(dictionaryName);
    		if (!matcher.matches()) {
    			return dictionaryTop;
    		}
    		String newDictionaryName  = matcher.group(1).replace(DOT, SLASH);
    		newDictionaryDir = new File(dictionaryTop, newDictionaryName);
    		if (!newDictionaryDir.exists()) {
    			newDictionaryDir.mkdirs();
    		} else if (!newDictionaryDir.isDirectory()) {
    			LOG.error(newDictionaryDir + " must not be directory" );
				showstopperEncountered = true;
    		}
    	}
    	return newDictionaryDir;
    	
    }
    

	private void writeNamesAndLinks() {
		if (nameList == null) {
			LOG.debug("no names to create dictionary");
			return;
		}
		LOG.info(SPECIAL, "names "+nameList.size()+"; terms "+termList.size());
		addEntriesToDictionaryElement();
//		createAndAddQueryElement();
		writeDictionary();
		return;
	}

	private void createAndAddQueryElement() {
		if (queryChunk != null && nameList != null) {
			Element query = null;
			StringBuilder sb = null;
			for (int i = 0; i <nameList.size(); i++) {
				String name = nameList.get(i);
				if (i % queryChunk == 0) {
					query = new Element("query");
					simpleDictionary.getDictionaryElement().appendChild(query);
					if (sb != null) {
						query.appendChild(sb.toString());
					}
					sb = new StringBuilder();
				} else {
					sb.append(" OR ");
				}
				sb.append("('" + name + "')");
			}
			if (sb != null) {
				query.appendChild(sb.toString());
//				dictionaryElement.appendChild(query);
			}
		}
	}

	private void addEntriesToDictionaryElement() {
		List<Element> entryList = createDictionaryListInRandomOrder();
		removeEntriesWithEmptyIdsSortRemoveDuplicates(entryList);
		return;
	}

	private List<Element> createDictionaryListInRandomOrder() {
		List<Element> entryList = new ArrayList<Element>();
		for (int i = 0; i < nameList.size(); i++) {
			String term = termList.get(i);
			Element entry = DefaultAMIDictionary.createEntryElementFromTerm(term);
			String value = nameList.get(i);
			if (value != null) {
				entry.addAttribute(new Attribute(DictionaryTerm.NAME, value));
			}
			String link = linkList == null ? null : linkList.get(i);
			if (link != null && !link.equals("")) {
				entry.addAttribute(new Attribute(DictionaryTerm.URL, link));
			}
			if (wikiLinks != null) {
				addWikiLinks(entry);
			}
			entryList.add (entry);
		}
		return entryList;
	}


	/** create from page with hyperlinks
	 * recommended to trim rubbish from HtmlElement first
	 * 
	 * @param htmlElement
	 * @return
	 */
	private HtmlUl createListOfHyperlinks(HtmlElement htmlElement) {
		nameList = new ArrayList<String>();
		linkList = new ArrayList<String>();
		List<HtmlA> aList = HtmlA.extractSelfAndDescendantAs(htmlElement);
		HtmlUl ul = new HtmlUl();
		for (HtmlA a : aList) {
			nameList.add(a.getValue());
			linkList.add(a.getHref());
			HtmlElement li = new HtmlLi();
			li.appendChild(a.copy());
			ul.appendChild(li);
		}
		return ul;
	}

	/** create from category 
	 * Not sure how standard this is
	 * recommended to trim rubbish from HtmlElement first
	 * <div class="mw-category-generated" lang="en" dir="ltr">
	 *   <div id="mw-pages">
	 *     <h2><span id="Pages_in_category"></span>Pages in category "Insect vectors of human pathogens"</h2>
	 *     <p>The following 57 pages are in this category, out of  57 total. This list may not reflect recent changes 
	 *        (<a href="/wiki/Wikipedia:FAQ/Categorization#Why_might_a_category_list_not_be_up_to_date?"
	 *         title="Wikipedia:FAQ/Categorization">learn more</a>).</p>
	 *     <div lang="en" dir="ltr" class="mw-content-ltr">
	 *       <div class="mw-category">
	 *         <div class="mw-category-group">
	 *           <h3>A</h3>
                 <ul>
                   <li><a href="/wiki/Aedes_aegypti" title="Aedes aegypti">Aedes aegypti</a></li>
                   <li><a href="/wiki/Aedes_albopictus" title="Aedes albopictus">Aedes albopictus</a></li>
                   <li><a href="/wiki/Aedes_japonicus" title="Aedes japonicus">Aedes japonicus</a></li>
	 * 
	 * @param htmlElement
	 * @return
	 */
	private void createFromCategory(HtmlElement htmlElement) {
		nameList = new ArrayList<String>();
		termList = new ArrayList<String>();
		linkList = new ArrayList<String>();
		List<HtmlElement> categoryList = HtmlUtil.getQueryHtmlElements(htmlElement, 
				".//*[local-name()='div' and @class='mw-category-generated']//*[local-name()='div' and @class='mw-category']");
		for (HtmlElement category : categoryList) {
			List<HtmlA> aList = HtmlA.extractSelfAndDescendantAs(category);
			HtmlUl ul = new HtmlUl();
			for (HtmlA a : aList) {
				termList.add(a.getValue());
				nameList.add(a.getTitle());
				linkList.add(a.getHref());
			}
		}
		return;
	}
	
	private void addAHrefs(List<? extends Element> aList) {
		nameList = new ArrayList<String>();
		linkList = new ArrayList<String>();
		for (Element a : aList) {
			if (a != null) {
				HtmlA aa = (HtmlA) a;
				String href = aa.getHref();
				String content = aa.getValue();
				if (href != null) {
					nameList.add(content);
					linkList.add(href);
				} else {
					LOG.warn("NO HREF");
				}
			}
		}
		LOG.warn("nameList "+nameList.size()+" // "+nameList+"\n>>> "+linkList);
	}

	private void createFromWikipediaTemplate(HtmlElement htmlElement) {
		// very crude at present just look for dd
		// other links are context (e.g. body parts for disease
		/**
<dd>
<a href="https://en.wikipedia.org/wiki/Sinusitis" title="Sinusitis">Sinusitis</a>
</dd>
		 */
		
		List<String> excludeList = Arrays.asList(new String[] {"history", "purge", "navbar", "mirror", "create"});
		List<Element> aList = XMLUtil.getQueryElements(htmlElement, 
				".//*[local-name() = '"+HtmlDiv.TAG+"' and @id='bodyContent']" +
				"//*[local-name()='"+HtmlA.TAG+"' and @href]");
		LOG.warn("number of links "+aList.size());
		for (int i = aList.size() - 1; i >= 0; i--) {
			HtmlA a = (HtmlA) aList.get(i);
			String value = a.getValue();
			if (excludeList.contains(value)) {
				aList.remove(i);
			}
		}
		addAHrefs(aList);
	}

	private void createFromMediawikiTemplate(String mwString) {
		LOG.trace("mwstring :" +mwString);
		List<HtmlA> aList = AbstractAMIDictTool.parseMediaWiki(mwString);
		LOG.warn("read A's :" +aList.size());
		addAHrefs(aList);
	}


	private void createFromEmbeddedWikipediaTable(HtmlElement htmlElement) {
		List<HtmlTable> tableList = HtmlTable.extractSelfAndDescendantTables(htmlElement);

		nameList = new ArrayList<String>();
		linkList = new ArrayList<String>();
		for (HtmlTable table : tableList) {
			String classAttribute = table.getClassAttribute();
			if (classAttribute == null) {
				LOG.debug("table has no class attribute ");
				continue;
			}
//			LOG.debug(">>"+table.toXML());
			if (classAttribute.contains(WIKITABLE)) {
				addTableNamesAndHrefs(table);
			}
		}
	}
	
	private void addTableNamesAndHrefs(HtmlTable table) {
		tBody = table.getTbody();
		String colName = nameCol.trim();
		int nameColIndex = tBody.getColumnIndex(colName);
		if (nameColIndex < 0) {LOG.debug("cannot find column: "+colName);
			return;
		}
		List<String> names = this.getColumnValues(nameColIndex);
		int linkColIndex = tBody.getColumnIndex(linkCol.trim());
		List<String> hrefs = this.getColumnHrefs(LinkField.HREF, linkColIndex, HTTPS_EN_WIKIPEDIA_ORG);
		if (names.size() == 0) {
			LOG.trace("no names found");
			return;
		}
		//remove first element as it's the columnn heading
		removeFirstElementsOfColumns(names, hrefs);

		if (names.size() != hrefs.size()) {
			LOG.warn("names and hrefs do not balance");
		}
		addUniqueNamesAndHrefs(names, hrefs);
	}
	

	private void addUniqueNamesAndHrefs(List<String> names, List<String> hrefs) {
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			if (nameList.contains(name)) {
				LOG.trace("dup: " + name);
				continue;
			}
			nameList.add(names.get(i));
			linkList.add(hrefs.get(i));
		}
	}
	
	public void readWikipediaPage(WikipediaDictionary wikipediaDictionary, InputStream inputStream) {
		if (InputFormat.mediawikitemplate.equals(this.informat)) {
			try {
				createFromMediawikiTemplate(IOUtils.toString(inputStream, CMineUtil.UTF8_CHARSET));
			} catch (IOException e) {
				throw new RuntimeException("Cannot read stream", e);
			}
		} else {
			HtmlElement htmlElement = readHtmlElement(inputStream);
			wikipediaDictionary.clean(htmlElement);
			readWikipediaPage(htmlElement);
		}
	}

	private void readWikipediaPage(HtmlElement htmlElement) {
		if (false) {
		} else if (InputFormat.wikicategory.equals(this.informat)) {
			createFromCategory(htmlElement);
		} else if (InputFormat.wikitable.equals(this.informat)) {
			if (this.nameCol == null) {
				LOG.error("Must give 'nameCol' for wikitable");
			} else {
				linkCol = linkCol == null ? nameCol : linkCol;
				createFromEmbeddedWikipediaTable(htmlElement);
			}
		} else if (InputFormat.wikitemplate.equals(this.informat)) {
			createFromWikipediaTemplate(htmlElement);
		} else {
			LOG.debug("extracting hyperlinks");
			createListOfHyperlinks(htmlElement);
		}
	}


	private HtmlElement readHtmlElement(InputStream inputStream) {
		HtmlElement htmlElement = null;
		try {
			HtmlElement rootElement = HtmlUtil.readTidyAndCreateElement(inputStream);
			boolean ignoreNamespaces = true;
			htmlElement = HtmlElement.create(rootElement, false, ignoreNamespaces);
		} catch (Exception e) {
			throw new RuntimeException("cannot find/parse URL ", e);
		}
		return htmlElement;
	}
	
	/** writes the formatted versions of a single dictionary.
	 *  
	 */
	private void writeDictionary() {
		String dictionaryNameRoot = getDictionaryNameList() != null && getDictionaryNameList().size() == 1 ?
				getDictionaryNameList().get(0) :
				currentTemplateName != null ? createDictionaryName(currentTemplateName) : null;
				
		getOrCreateExistingDictionaryTop();
		if (dictionaryTop == null) {
			throw new RuntimeException("must give directory for dictionaries");
		} else {
			dictionaryName = dictionaryNameRoot;
		}
		if (outformats != null) {
			if (dictionaryName == null) {
				
//				dictionaryName = parent.getDictionaryList() != null && parent.getDictionaryList().size() == 1 ?
//					parent.getDictionaryList().get(0) :
//					currentTemplateName != null ? createDictionaryName(currentTemplateName) : null;
					
				dictionaryName = currentTemplateName != null ? createDictionaryName(currentTemplateName) : null;
				if (dictionaryName == null) {
					throw new RuntimeException("cannot create dictionaryName");
				}
			} 
			writeDictionary(dictionaryName);
		}
	}

	

//	protected List<String> getDictionaryList() {
//		return getDictionaryList() == null ? null : getDictionaryList();
//	}

	private void addEntry(String dictionaryId, int serial, Element entry, String urlValue) {
		String idValue = CM_PREFIX + dictionaryId + DOT + serial;
		System.err.print("+"); // TODO progress indicator
		entry.addAttribute(new Attribute(DictionaryTerm.ID, idValue));
		addTrimmedWikipediaURL(entry, urlValue);
		simpleDictionary.getDictionaryElement().appendChild(entry);
	}

	private static void addTrimmedWikipediaURL(Element entry, String urlValue) {
		if (urlValue != null) {
			urlValue = trimWikipediaUrlBase(urlValue);
			entry.addAttribute(new Attribute(DictionaryTerm.WIKIPEDIA, urlValue));
		}
	}

	private void removeEntriesWithEmptyIdsSortRemoveDuplicates(List<Element> entryList) {
		String dictionaryId = createDictionaryId(); 
		Collections.sort(entryList, new EntryComparator());
		int i = 0;
		String lastTerm = null;
		for (Element entry : entryList) {
			String term = entry.getAttributeValue(DictionaryTerm.TERM);
			// add entry if new meaningful term
			if (term != null && !term.trim().equals("")  && !term.equals(lastTerm)) {
				if (WIKIPEDIA_STOP_WORDS.contains(term)) {
					LOG.debug("skipped wikipedia: "+term);
					continue;
				}
				Attribute urlAtt = entry.getAttribute(DictionaryTerm.URL);
				if (urlAtt != null) {
					String urlValue = urlAtt.getValue();
					if (!urlValue.equals("")) {
						if (urlValue.startsWith(SLASH_WIKI_SLASH) || urlValue.startsWith(HTTPS_EN_WIKIPEDIA_ORG_WIKI)) {
							addEntry(dictionaryId, i++, entry, urlValue);
						} else if (InputFormat.mediawikitemplate.equals(informat)){
							urlValue = "https://en.wikipedia.org/wiki/"+urlValue;
							addEntry(dictionaryId, i++, entry, urlValue);
						}
					} else {
						System.err.print(" !WP "); // TODO progress indicator
						LOG.trace("skipped non-wikipedia link: "+urlAtt);
					}
				} else {
//					LOG.debug("no links in: "+entry.toXML());
					addEntry(dictionaryId, i++, entry, null);
				}
				lastTerm = term;
			}
		}
	}
	
	private static String trimWikipediaUrlBase(String urlValue) {
		if (urlValue.startsWith(SLASH_WIKI_SLASH)) {
			urlValue = urlValue.substring(SLASH_WIKI_SLASH.length());
		} 
		if (urlValue.startsWith(HTTPS_EN_WIKIPEDIA_ORG_WIKI)) {
			urlValue = urlValue.substring(HTTPS_EN_WIKIPEDIA_ORG_WIKI.length());
		} 
		return urlValue;
	}

	private void writeDictionary(String dictionaryName) {
		// this is slightly messy - 
		transformValues();
		simpleDictionary.getDictionaryElement().addAttribute(new Attribute(DefaultAMIDictionary.TITLE, dictionaryName));
		File subDirectory = getOrCreateExistingSubdirectory(dictionaryName);
		if (subDirectory != null) {
			List<DictionaryFileFormat> outformatList = Arrays.asList(outformats);
			for (DictionaryFileFormat outformat : outformatList) {
//				File outfile = getOrCreateDictionary(subDirectory, dictionary, outformat);
				File outfile = getOrCreateDictionary(subDirectory, dictionaryName, outformat);
				LOG.info(SPECIAL, "writing dictionary to "+outfile.getAbsolutePath());
				try {
					outputDictionary(outfile, outformat);
				} catch (IOException e) {
					throw new RuntimeException("cannot write file "+outfile, e);
				}
			}
		}
	}
	
	private File getOrCreateDictionary(File subDirectory, String dictionaryname, DictionaryFileFormat outformat) {
		File dictionaryFile = null;
		if (subDirectory != null && subDirectory.exists() && subDirectory.isDirectory() ) {
			int idx = dictionaryname.indexOf(DOT);
			dictionaryname = idx == -1 ? dictionaryname : dictionaryname.substring(idx + 1);
			dictionaryFile = new File(subDirectory, dictionaryname + DOT + outformat);
		}
		return dictionaryFile;
	}

	private void outputDictionary(File outfile, DictionaryFileFormat outformat) throws IOException {
		LOG.trace("writing dictionary to "+outfile.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(outfile);
		if (outformat.equals(DictionaryFileFormat.xml)) {
			XMLUtil.debug(simpleDictionary.getDictionaryElement(), fos, 1);
		} else if (outformat.equals(DictionaryFileFormat.json)) {
			String jsonS = createJson(simpleDictionary.getDictionaryElement());
			IOUtils.write(jsonS, fos, UTF_8);
		} else if (outformat.equals(DictionaryFileFormat.html)) {
			HtmlDiv div = createHtml();
			if (div != null) {
				String xmlS = div.toXML();
				IOUtils.write(xmlS, fos, UTF_8);
			}
		}
		try {
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("cannot close ", e);
		}
		return;
	}

	/** 
	 // FIXME messy to use DefaultAMIDictionary
	 * 
	 * @return
	 */
	private HtmlDiv createHtml() {
		DefaultAMIDictionary dictionary = new DefaultAMIDictionary();
		dictionary.readDictionaryElement(simpleDictionary.getDictionaryElement());
		HtmlDiv div = dictionary.createHtmlElement();
		return div;
	}

	private void checkColumnsNotNull(List<RectTabColumn> colList, String[] colNames) {
		for (int i = 0; i < colList.size(); i++) {
			if (colList.get(i) == null) {
				LOG.warn("Cannot find column: "+colNames[i]);
			}
		}
	}

	private String createInput(String templateName) {
		input("https://en.wikipedia.org/w/index.php?title=Template:" + templateName);
		if (WikiFormat.mwk.equals(wptype)) {
			input(input() + "&action=edit");
		}
		return input();
	}

//	private void createInputList() {
//		inputList = new ArrayList<>();
//		for (String templateName : templateNames) {
//			createInput(templateName);
//			inputList.add(input());
//		}
//		input(null);
//	}

	/** get column links (hrefs)
	 * skips header
	 * 
	 * @param base if not empty represents the base for the URL
	 * @param colIndex
	 * @return list of values
	 */
	private List<String> getColumnHrefs(LinkField field, int colIndex, String base) {
		List<String> valueList = addCells(colIndex, field, base);
		return valueList;
	}

	/** get column values (as text)
	 * skips header
	 * 
	 * @param colIndex
	 * @return list of values
	 */
	private List<String> getColumnValues(int colIndex) {
		return addCells(colIndex, LinkField.VALUE, (String) null);
	}

	private void removeFirstElementsOfColumns(List<String> names, List<String> hrefs) {
		names.remove(0);
		if (hrefs.size() == 0) {
			LOG.trace("no links found");
			hrefs = new ArrayList<String>(names.size());
		} else {
			hrefs.remove(0);
		}
	}

    private List<String> addCells(int colIndex, LinkField field, String base) {
		List<String> valueList = new ArrayList<String>();
		if (colIndex >= 0) {
			tBody.getOrCreateChildTrs();
			int ncols = -1;
			int splitrow = 0;
			int fusedrow = 0;
			List<HtmlTr> rowList = tBody.getRowList();
			for (int i = 0; i < rowList.size(); i++) {
				HtmlTr row = tBody.getRowList().get(i);
				// unfortunately Th is also found
				List<HtmlTCell> cellChildren = row.getTCellChildren();
				int size = cellChildren.size();
				if (size == 0) {
					// skip header and empty rows
					continue;
				}
				if (ncols == -1) {
					ncols = size;
				} else if (size > ncols) {
					System.err.print(" ?>"+i);
					splitrow++;
					continue;
				} else if (size < ncols) {
					System.err.print(" ?<"+i);
					fusedrow++;
					continue;
				}
				List<String> linkFields = addValueFromContentOrHref((HtmlElement)cellChildren.get(colIndex), field, base);
				valueList.addAll(linkFields);
			}
			LOG.warn("\nrows: "+rowList.size()+" " +
					((fusedrow > 0) ? "fused rows: "+fusedrow+" " : "") +
					((splitrow > 0) ? "split rows: "+splitrow+" " : ""));
		}
		return valueList;
	}

	private String createDictionaryId() {
		return getDictionaryNameList() == null || getDictionaryNameList().size() == 0 ? "null" : getDictionaryNameList().get(0);
	}

	/**
	 * 
	 * @param child
	 * @param type either "value" or "href"
	 * @param base if _
	 * @param valueList
	 */
	private List<String> addValueFromContentOrHref(HtmlElement child, LinkField field, String base) {
		List<HtmlA> aList = HtmlA.extractSelfAndDescendantAs(child);
		List<String> resultList = new ArrayList<String>();
		for (HtmlA a : aList) {
			String value = null;
			if (LinkField.VALUE.equals(field)) {
				value = a.getValue().trim();
			} else {
				value = a.getHref();
				if (value != null && !value.startsWith(HTTP)) {
					value = base + value;
				}
			}
			resultList.add(value);
		}
		return resultList;
	}

	/** prettyPrinting is done here.
	 * 
	 * @param dictionaryElement
	 * @return
	 */
	private String createJson(Element dictionaryElement) {
		CMJsonDictionary cmJsonDictionary = CMJsonDictionary.createCMJsonDictionary(dictionaryElement);
		// this may be overkill
		JsonObject json = new JsonParser().parse(cmJsonDictionary.toString()).getAsJsonObject();
	    return Util.prettyPrintJson(json);
	}








	
}
/** compare entries by their lower-case terms
 * 
 */
class EntryComparator implements Comparator<Element> {

	@Override
	public int compare(Element o1, Element o2) {
		if (o1 == null || o2 == null) {
			return 0;
		}
		String term1 = o1.getAttributeValue("term");
		String term2 = o2.getAttributeValue("term");
		if (term1 == null) {
			return (term2 == null) ? 0 : 1;
		} else {
			return term1.toLowerCase().compareTo(term2.toLowerCase());
		}
	}
	
}

