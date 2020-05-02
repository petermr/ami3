package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.CProjectTreeMixin;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.lookups.WikiResult;
import org.contentmine.ami.lookups.WikipediaLookup;
import org.contentmine.ami.tools.dictionary.DictionaryCreationTool;
import org.contentmine.ami.tools.dictionary.DictionaryDisplayTool;
import org.contentmine.ami.tools.dictionary.DictionarySearchTool;
import org.contentmine.ami.tools.dictionary.DictionaryTranslateTool;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Attribute;
import nu.xom.Element;
import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** mainly to manage help and parse dictionaries.
 * 
 * @author pm286
 *
 */

@Command(
		name = "dictionary",
		description = {
				"Manages AMI dictionaries.",
				"Create from Wikipedia:%n"
				+ "   ${COMMAND-FULL-NAME} create --informat wikipage%n"
				+ "    --input https://en.wikipedia.org/wiki/List_of_fish_common_names%n"
				+ "    --dictionary commonfish --directory mydictionary --outformats xml,html%n"
		},
subcommands = {
//		DictionaryCreationTool.class,
//		DictionaryDisplayTool.class,
//		DictionarySearchTool.class,
//		DictionaryTranslateTool.class,

		CommandLine.HelpCommand.class,
		AutoComplete.GenerateCompletion.class,
})

public class AMIDictionaryToolOLD extends AbstractAMITool {
	

	public static final String UTF_8 = "UTF-8";
	private static final Logger LOG = Logger.getLogger(AMIDictionaryToolOLD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String ALL = "ALL";
	public static final String HELP = "HELP";
	public static final String SEARCH = "search";
	private static final String DICTIONARY_TOP_NAME = "dictionary/";
	private static final String DICTIONARIES_NAME = DICTIONARY_TOP_NAME + "dictionaries";
	private static final String SLASH = "/";
    public final static List<String> WIKIPEDIA_STOP_WORDS = Arrays.asList(new String[]{
        	"citation needed",
        	"full citation needed"
        });


	public enum LinkField {
		HREF,
		VALUE,
	}

	public enum Operation {
		create,
		display,
		help,
		search,
		translate,
		;
		public static Operation getOperation(String operationS) {
			for (int i = 0; i < values().length; i++) {
				Operation operation = values()[i];
				if (operation.toString().equalsIgnoreCase(operationS)) {
					return operation;
				}
			}
			return null;
		}
		
	}
	
	/** ugly lowercase, but I don't yet know how to use
	 * 		CommandLine::setCaseInsensitiveEnumValuesAllowed=true
	 * @author pm286
	 *
	 */
	public enum DictionaryFileFormat {
		 xml,
		 html,
		 json,
		 ;
		public static DictionaryFileFormat getFormat(String format) {
			for (DictionaryFileFormat fileFormat : values()) {
				if (fileFormat.toString().equals(format.toString())) {
					return fileFormat;
				}
			}
			return null;
		}
	}
	
	/** ugly lowercase, but I don't yet know how to use
	 * 		CommandLine::setCaseInsensitiveEnumValuesAllowed=true
	 * @author pm286
	 *
	 */
	public enum RawFileFormat {
		 html,
		 pdf,
		 xml,
		 }
	
	public enum InputFormat {
		csv,
		list,
		mediawikitemplate,
		wikicategory,
		wikipage,
		wikitable,
		wikitemplate,
	}
	
	public enum WikiLink {
		wikidata,
		wikipedia,
	}

	public enum WikiFormat {
		html,
		mwk,
	}


    @Parameters(index = "0",
    		arity="0..*",
//    		split=",",
    		description = "primary operation: (${COMPLETION-CANDIDATES}); if no operation, runs help"
    		)
    private Operation operation = Operation.help;

    @Option(names = {"--baseurl"}, 
    		arity="1",
   		    description = "base URL for all wikipedia links "
    		)
    private String baseUrl = "https://en.wikipedia.org/wiki";

    @Option(names = {"--booleanquery"}, 
    		arity="0..1",
   		    description = "generate query as series of chained OR phrases"
    		)
    private boolean booleanQuery = false;
    
    @Option(names = {"--descriptions"}, 
    		arity="1..*",
   		description = "description fields (free form) such as source, author"
    		)
    private String[] description;
    
    @Option(names = {"-d", "--dictionary"}, 
    		arity="1..*",
    		description = "input or output dictionary name/s. for 'create' must be singular; when 'display' or 'translate', any number. "
    				+ "Names should be lowercase, unique. [a-z][a-z0-9._]. Dots can be used to structure dictionaries into"
    				+ "directories. Dictionary names are relative to 'directory'. If <directory> is absent then "
    				+ "dictionary names are absolute.")
    protected List<String> dictionaryList = null;

    @Option(names = {"--directory"}, 
    		arity="1",
    		description = "top directory containing dictionary/s. Subdirectories will use structured names (NYI). Thus "
    				+ "dictionary 'animals' is found in '<directory>/animals.xml', while 'plants.parts' is found in "
    				+ "<directory>/plants/parts.xml. Required for relative dictionary names.")
    protected String dictionaryTopname = null;

    @Option(names = {"--informat"}, 
    		arity="1",
    		paramLabel = "input format",
    		description = "input format (${COMPLETION-CANDIDATES})"
    		)
    protected InputFormat informat;
    
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
    		description = "output format (${COMPLETION-CANDIDATES})"
    		)
    protected DictionaryFileFormat[] outformats = new DictionaryFileFormat[] {DictionaryFileFormat.xml};

    @Option(names = {"--search"}, 
    		arity="1..*",
    	    paramLabel = "search",
    		description = "search dictionary for these terms (experimental)"
    		)
    protected List<String> searchTerms;
        
    @Option(names = {"--searchfile"}, 
    		arity="1..*",
    	    paramLabel = "searchfile",
    		description = "search dictionary for terms in these files (experimental)"
    		)
    protected List<String> searchTermFilenames;
        
    @Option(names = {"--splitcol"}, 
    		arity="1",
    		paramLabel="input separator",
    		defaultValue=",",
    		description = "character to split input values; (default: ${DEFAULT-VALUE})"
    		)
    private String splitCol=",";
        
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
    		description = "list of terms in file, line-separated")
    protected File termfile;

    @Option(names = {"--title"}, 
    		arity="1",
    		description = "title for dictionary to be used if not already in source")
    private String title;

    @Option(names = {"--urlref"}, 
    		arity="1..*",
    		split=",",
    		description = "for non-structured pages I think")
    private String[] urlref;

    @Option(names = {"--wptype"}, 
    		arity="1",
    		description = "type of input (HTML , mediawiki)")
    protected WikiFormat wptype;
    
    @Mixin CProjectTreeMixin proTree;

	@Option(names = {"--testString"},
			description = {
					"String input for debugging; semantics depend on task"})
	protected String testString = null;

	@Option(names = {"--wikilinks"}, 
			arity="0..*",
			defaultValue = "wikipedia,wikidata",
			split = ",",
			description = "try to add link to Wikidata and/or Wikipedia page of same name. ")
	protected WikiLink[] wikiLinks = null;/* new WikiLink[]{WikiLink.wikipedia, WikiLink.wikidata}*/
	
	protected HashSet<String> missingWikidataSet;
	public Set<String> missingWikipediaSet;
//    DictionaryData dictionaryData;
	protected DictionaryFileFormat dictInformat;
	protected DictionaryFileFormat dictOutformat;
	private   List<String> descriptionList;
	protected List<String> inputList;
	protected File dictionaryTop;
	protected List<WikiLink> wikiLinkList;

	public AMIDictionaryToolOLD() {
		initDict();
	}
	
	private void initDict() {
	}
	
	public static void main(String args) {
		main(args.trim().split("\\s+"));
	}
	
	public static void main(String[] args) {
        AMIDictionaryToolOLD amiDictionary = new AMIDictionaryToolOLD();
		amiDictionary.runCommands(args);
	}

	@Override
	protected void parseSpecifics() {
		if (templateNames != null) {
//			dictionaryList = new ArrayList<>();
			createTemplateNames();
			informat = WikiFormat.mwk.equals(wptype) ? InputFormat.mediawikitemplate : InputFormat.wikitemplate;
			if (dictionaryTopname == null) {
				System.err.println("No directory given, using .");
				dictionaryTopname = ".";
			}
//			createInputList();
		}
		if (testString != null) {
			testString = testString.replaceAll("%20", " ");
			System.out.println("testString      "+testString);
		}
		dictOutformat = (outformats == null || outformats.length != 1) ? null : outformats[0];
		wikiLinkList = (wikiLinks == null) ? new ArrayList<WikiLink>() :
		     new ArrayList<WikiLink>(Arrays.asList(wikiLinks));
		descriptionList = (description == null) ? new ArrayList<String>() :
		     new ArrayList<String>(Arrays.asList(description));
		printDebug();
	}

	protected void runSub() {
		System.err.println("Overload this in subDictionaryTool "+this.getClass());
	}
	
	private void createTemplateNames() {
		for (int i = 0; i < templateNames.size(); i++) {
			String t = templateNames.get(i).trim().replaceAll("\\s+", "_");
			templateNames.set(i, t);
		}
	}
	@Override
	protected void runSpecifics() {
        runDictionary();
	}

	private void runDictionary() {
		if (getOrCreateExistingDictionaryTop() == null) {
			LOG.warn("No dictionary directory; aborted");
			return;
		}
		if (Operation.display.equals(operation)) {
			new DictionaryDisplayTool().runSub();
		} else if (Operation.help.equals(operation)) {
			new DictionaryDisplayTool().help(Arrays.asList(new String[] {}));
		} else if (Operation.create.equals(operation)) {
			new DictionaryCreationTool().runSub();
		} else if (Operation.search.equals(operation)) {
			new DictionarySearchTool().runSub();
		} else if (Operation.translate.equals(operation)) {
			new DictionaryTranslateTool().runSub();
		} else {
			System.err.println("no operation given: "+operation);
		}
	}

	protected File getOrCreateExistingDictionaryTop(String dictionaryTopname) {
    	if (dictionaryTopname != null) {
    		dictionaryTop = new File(dictionaryTopname);
    	}
    	return getOrCreateExistingDictionaryTop();
    }

    protected File getOrCreateExistingDictionaryTop() {
    	if (dictionaryTop == null) {
    		getOrCreateExistingContentMineDir();
    		if (contentMineDir != null) {
    			dictionaryTop = new File(contentMineDir, DICTIONARIES_NAME);
    		}
    	}
	   	if (dictionaryTop != null) {
	    	if (!dictionaryTop.exists()) {
	    		dictionaryTop.mkdirs();
	    	} else if (!dictionaryTop.isDirectory()) {
	    		addLoggingLevel(Level.ERROR, dictionaryTop + " must be a directory");
	    	}
    	}
	   	return dictionaryTop;
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
    	getOrCreateExistingDictionaryTop(dictionaryTopname);
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
    			addLoggingLevel(Level.ERROR, newDictionaryDir + " must not be directory" );
    		}
    	}
    	return newDictionaryDir;
    	
    }
    

	public static DefaultAMIDictionary readDictionary(File file) {
		DefaultAMIDictionary amiDictionary = new DefaultAMIDictionary();
		amiDictionary.readDictionary(file);
		return amiDictionary;
	}



//	private List<Integer> getColIndexList(List<String> headers, String[] colNamesArray) {
//		List<Integer> hrefIndexList = new ArrayList<Integer>();
//		for (String colName : colNamesArray) {
//			int colIndex = headers.indexOf(colName);
//			if (colIndex == -1) {
//				LOG.error("Unknown column heading: " + colName);
//			}
//			hrefIndexList.add(new Integer(colIndex));
//		}
//		return hrefIndexList;
//	}

	private void printDebug() {
		System.out.println("baseUrl       "+baseUrl);
		System.out.println("booleanQuery  "+booleanQuery);
		System.out.println("descriptions  "+description);
//		System.out.println("dataCols      "+dataCols);
		System.out.println("dictionary    "+(dictionaryList == null ? "null" : Arrays.asList(dictionaryList)));
		System.out.println("dictionaryTop     "+dictionaryTopname);
//		System.out.println("href          "+href);
//		System.out.println("hrefCols      "+hrefCols);
		System.out.println("inputs        "+inputList);
		System.out.println("input         "+input());
		System.out.println("informat      "+informat);
		System.out.println("dictInformat  "+dictInformat);
		System.out.println("linkCol       "+linkCol);
		//System.out.println("log4j         "+makeArrayList(log4j));
		System.out.println("nameCol       "+nameCol);
		System.out.println("operation     "+operation);
		System.out.println("outformats    "+makeArrayList(outformats));
//		System.out.println("query         "+queryChunk);
		System.out.println("search        "+searchTerms);
		System.out.println("searchfile    "+searchTermFilenames);
		System.out.println("splitCol      "+splitCol);
		System.out.println("templatea     "+templateNames);
		System.out.println("termCol       "+termCol);
//		System.out.println("terms         "+(termList == null ? null : "("+termList.size()+") "+termList));
		System.out.println("termfile      "+termfile);
		System.out.println("title         "+title);
		System.out.println("urlref        "+urlref);
		System.out.println("wikiLinks     "+wikiLinkList);
		System.out.println("wptype        "+wptype);
		
	}

	private List<?> makeArrayList(Object[] list) {
		return list == null ? null : Arrays.asList(list);
	}
	
	
	
    

	// ================== LIST ===================

	
	protected void addWikiLinks(Element entry) {
		try {
			WikipediaLookup wikipediaLookup = new WikipediaLookup();
			HtmlElement wikipediaPage = null;
			List<HtmlElement> wikidata = null;
			String term = entry.getAttributeValue(DictionaryTerm.TERM);
			try {
				wikipediaPage = addWikipediaPage(entry, term);
			} catch (RuntimeException e) {
				LOG.error("cannot parse wikipedia page for: " + term + "; " + e.getMessage());
			}
			if (wikiLinkList.contains(WikiLink.wikidata)) {
				if (term != null) {
					wikidata = wikipediaLookup.queryWikidata(term);
				} else if (wikipediaPage != null) {
					wikidata = (wikidata != null) ? wikidata : wikipediaLookup.createWikidataFromTermLookup(wikipediaPage);
					wikidata = (wikidata != null) ? wikidata : wikipediaLookup.queryWikidata(term);
				}
				WikiResult wikiResult = WikipediaLookup.getFirstWikiResultFromSearchResults(wikidata);
				if (wikiResult != null) {
					XMLUtil.addNonNullAttribute(entry, WIKIDATA, wikiResult.getQString());
					XMLUtil.addNonNullAttribute(entry, DictionaryTerm.NAME, wikiResult.getLabel());
					XMLUtil.addNonNullAttribute(entry, DictionaryTerm.DESCRIPTION, wikiResult.getDescription());
				} else {
					missingWikidataSet.add(term);
				}
			}
		} catch (Exception e) {
			System.err.println("Cannot add entry: "+e.getMessage());
		}
	}

	public HtmlElement addWikipediaPage(Element entry, String term) {
		HtmlElement wikipediaPage = null;
		if (wikiLinkList.contains(WikiLink.wikipedia)) {
			wikipediaPage = addWikipedia(entry);
		}
		if (wikipediaPage == null) {
			System.err.print("!");
			missingWikipediaSet.add(term);
		} else {
			entry.addAttribute(new Attribute(WIKIPEDIA, term));
			System.out.print(".");
		}
		return wikipediaPage;
	}

	public HtmlElement addWikipedia(Element entry) {
		HtmlElement wikipediaPage = null;
		try {
			String name = entry.getAttributeValue("name");
			if (name != null) {
				name = name.replace(" ", "_");
				URL wikipediaUrl = new URL(HTTPS_EN_WIKIPEDIA_ORG_WIKI + name);
				InputStream is = wikipediaUrl.openStream();
				Element element = XMLUtil.parseQuietlyToRootElement(is);
				wikipediaPage = element == null ? null : HtmlElement.create(element);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("bad URL ", e);
		} catch (IOException e) {
			// maybe skip? cannot find page
		}
		return wikipediaPage;
	}

	/**
				+ "{{Navbox\n" + 
				" | name = Viral systemic diseases\n" + 
				" | title = [[Infection|Infectious diseases]] – [[Viral disease|viral systemic diseases]] ([[ICD-10 Chapter I: Certain infectious and parasitic diseases#A80–B34 – Viral infections|A80–B34]], [[List of ICD-9 codes 001–139: infectious and parasitic diseases#Human immunodeficiency virus (HIV) infection (042–044)|042–079]])\n" + 
				" | state = {{{state<includeonly>|autocollapse</includeonly>}}}\n" + 
				" | listclass = hlist\n" + 
	 * @param mw
	 * @return
	 */
	public static List<HtmlA> parseMediaWiki(String mw) {
		List<HtmlA> aList = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\[\\[([^\\]]*)\\]\\]");
		Matcher m = pattern.matcher(mw);
		int start = 0;

		while (m.find(start)) {
			String group = m.group(1).trim();
			String[] aStrings = group.split("\\|");
			String href = aStrings[0].trim();
			href = href.replaceAll("\\s+", "_"); // Wikipedia's word separator
			String content = aStrings.length == 1 ? href : aStrings[1].trim();
			HtmlA a = new HtmlA();
			a.setHref(href).setValue(content);
			aList.add(a);
			start = m.end();
		}
		return aList;
	}

	public static final String DOT = ".";
	public static final String WIKIDATA = "wikidata";
	public static final String WIKIPEDIA = "wikipedia";
	public static final String HTTPS_EN_WIKIPEDIA_ORG_WIKI = "https://en.wikipedia.org/wiki/";
	

}
//class Data {
//    String ss;	
//}
