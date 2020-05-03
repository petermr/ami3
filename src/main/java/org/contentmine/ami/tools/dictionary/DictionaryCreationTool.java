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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.CMJsonDictionary;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.lookups.WikipediaDictionary;
import org.contentmine.ami.tools.AMIDictionaryToolOLD;
import org.contentmine.ami.tools.AbstractAMIDictTool;
import org.contentmine.ami.tools.download.CurlDownloader;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import nu.xom.Attribute;
import nu.xom.Element;
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

	public static final Logger LOG = Logger.getLogger(DictionaryCreationTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String HTTPS_EN_WIKIPEDIA_ORG = "https://en.wikipedia.org";
	private static final String SLASH_WIKI_SLASH = "/wiki/";
	private final static String WIKIPEDIA_BASE = HTTPS_EN_WIKIPEDIA_ORG + SLASH_WIKI_SLASH;

	private List<String> termList;
	private List<String> nameList;
	private List<String> linkList;
	private String currentTemplateName;
	private WikipediaDictionary wikipediaDictionary;
	private Element dictionaryElement;
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
	@Option(names = {"--terms"}, 
			arity="1..*",
			split=",",
			description = "list of terms (entries), comma-separated")
	private List<String> terms;
	private Set<String> termSet;
	@Option(names = {"--query"}, 
			arity="0..1",
		    defaultValue="10",
		    paramLabel = "query",
			description = "generate query for cut and paste into EPMC or similar. "
					+ "value sets size of chunks (too large crashes EPMC). If missing, no query generated."
			)
	private Integer queryChunk = null;
	private HtmlTbody tBody;
	private static final String HTTP = "http";
	private static final String CM_PREFIX = "CM.";
	private static final String WIKITABLE = "wikitable";
	

	public DictionaryCreationTool() {
		
	}

	public void runSub() {
		resetMissingLinks();

		if (templateNames != null) {
			for (String templateName : templateNames) {
				currentTemplateName = templateName;
				input(createDictionaryName(currentTemplateName));
				createDictionary();
			}
	
		} else {
			// single input
			createDictionary();
		}
		printMissingLinks();

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
			System.out.println("\n"+title + ":");
			for (String missingLink : missingLinkList) {
				if (i++ % 8 == 0) {
					System.out.println();
				}
				System.out.print(missingLink + "; ");
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


	private void createDictionary() {
		if (dictionaryTopname == null) {
			throw new RuntimeException("no directory given");
		}
		if (input() == null) {
			throw new RuntimeException("no input given");
		}
		File fileroot = new File(dictionaryTopname, input());
		dictionaryTopname = dictionaryTopname == null ?
				new File(fileroot, wptype.toString()).toString() : dictionaryTopname;
		InputStream inputStream = openInputStream();
		try {
			if (inputStream == null) {
				System.err.println("NO INPUT STREAM, check HTTP connection/target or file existence");
				if (testString != null) {
					inputStream = new ByteArrayInputStream(testString.getBytes());
				}
			}
	
			if (inputStream != null) {
				if (informat == null) {
					addLoggingLevel(Level.ERROR, "no input format given ");
					return;
				} else if (InputFormat.csv.equals(informat)) {
					readCSV(inputStream);
				} else if (InputFormat.list.equals(informat)) {
					readList(inputStream);
				} else if (InputFormat.mediawikitemplate.equals(informat) ||
						InputFormat.wikicategory.equals(informat) ||
						InputFormat.wikipage.equals(informat) ||
						InputFormat.wikitable.equals(informat) ||
						InputFormat.wikitemplate.equals(informat)
				) {
					wikipediaDictionary = new WikipediaDictionary();
					readWikipediaPage(wikipediaDictionary, inputStream);
				} else {
					addLoggingLevel(Level.ERROR, "unknown inputformat: " + informat);
					return;
				}
			} else {
	
			}
		} finally {
			if (inputStream != null) {
				try { inputStream.close(); } catch (IOException ignored) {}
			}
		}
		synchroniseTermsAndNames();
		dictionaryElement = DefaultAMIDictionary.createDictionaryWithTitle(/*dictionaryList.get(0)*/input());
		
		writeNamesAndLinks();
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
			terms = IOUtils.readLines(inputStream, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("cannot read termfile: "+termfile, e);
		}
		if (terms == null) {
			throw new RuntimeException("Could not read terms");
		}
		createSortedTermList();
	}

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
//					System.out.println(">> "+output);
				} else {
					inputStream = super.openInputStream();
				}
			} catch (IOException e) {
				addLoggingLevel(Level.ERROR, "cannot read/open stream: " + input());
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

	private void writeNamesAndLinks() {
		if (nameList == null) {
			LOG.debug("no names to create dictionary");
			return;
		}
		System.out.println("N "+nameList.size()+"; T "+termList.size());
		addEntriesToDictionaryElement();
		createAndAddQueryElement();
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
					dictionaryElement.appendChild(query);
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
			HtmlLi li = new HtmlLi();
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
					System.err.println("NO HREF");
				}
			}
		}
		System.err.println("nameList "+nameList.size()+" // "+nameList+"\n>>> "+linkList);
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
		System.err.println("number of links "+aList.size());
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
		List<HtmlA> aList = AMIDictionaryToolOLD.parseMediaWiki(mwString);
		System.err.println("read A's :" +aList.size());
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
				createFromMediawikiTemplate(IOUtils.toString(inputStream, "UTF-8"));
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
		getOrCreateExistingDictionaryTop();
		if (dictionaryTop == null) {
			throw new RuntimeException("must give directory for dictionaries");
		}
		if (outformats != null) {
			
			String dictionaryName = dictionaryList != null && dictionaryList.size() == 1 ? dictionaryList.get(0) :
				currentTemplateName != null ? createDictionaryName(currentTemplateName) : null;
			if (dictionaryName == null) {
				throw new RuntimeException("cannot create dictionaryName");
			}
			System.err.println(">> "+dictionaryName);
			writeDictionary(dictionaryName);
		}
	}

	private void addEntry(String dictionaryId, int serial, Element entry, String urlValue) {
		String idValue = CM_PREFIX + dictionaryId + DOT + serial;
		System.out.print("+");
		entry.addAttribute(new Attribute(DictionaryTerm.ID, idValue));
		if (urlValue != null) {
			urlValue = trimWikipediaUrlBase(urlValue);
			entry.addAttribute(new Attribute(DictionaryTerm.WIKIPEDIA, urlValue));
		}
		dictionaryElement.appendChild(entry);
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
						System.err.print(" !WP ");
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
	
	private String trimWikipediaUrlBase(String urlValue) {
		if (urlValue.startsWith(SLASH_WIKI_SLASH)) {
			urlValue = urlValue.substring(SLASH_WIKI_SLASH.length());
		} 
		if (urlValue.startsWith(HTTPS_EN_WIKIPEDIA_ORG_WIKI)) {
			urlValue = urlValue.substring(HTTPS_EN_WIKIPEDIA_ORG_WIKI.length());
		} 
		return urlValue;
	}

	private void writeDictionary(String dictionary) {
		System.err.println(">> dict "+dictionary);
		File subDirectory = getOrCreateExistingSubdirectory(dictionary);
		if (subDirectory != null) {
			List<DictionaryFileFormat> outformatList = Arrays.asList(outformats);
			for (DictionaryFileFormat outformat : outformatList) {
				File outfile = getOrCreateDictionary(subDirectory, dictionary, outformat);
				System.out.println("writing dictionary to "+outfile.getAbsolutePath());
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
			XMLUtil.debug(dictionaryElement, fos, 1);
		} else if (outformat.equals(DictionaryFileFormat.json)) {
			String jsonS = createJson(dictionaryElement);
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
		dictionary.readDictionaryElement(dictionaryElement);
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
			System.out.print("\nrows: "+rowList.size()+" ");
			System.out.print((fusedrow > 0) ? "fused rows: "+fusedrow+" " : "");
			System.out.print((splitrow > 0) ? "split rows: "+splitrow+" " : "");
			System.out.println();
		}
		return valueList;
	}

	private String createDictionaryId() {
		return dictionaryList == null || dictionaryList.size() == 0 ? "null" : dictionaryList.get(0);
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

