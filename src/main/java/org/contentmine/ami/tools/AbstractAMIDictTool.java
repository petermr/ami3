package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.CProjectTreeMixin;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.lookups.WikiResult;
import org.contentmine.ami.lookups.WikipediaLookup;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Attribute;
import nu.xom.Element;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/** mainly to manage help and parse dictionaries.
 * 
 * @author pm286
 *
 */

/**
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

//		CommandLine.HelpCommand.class,
//		AutoComplete.GenerateCompletion.class,
})
*/

@Command(mixinStandardHelpOptions = true, version = "ami-dict 1.0.0")
public class AbstractAMIDictTool implements Callable<Void> {
	

	public static final String UTF_8 = "UTF-8";
	private static final Logger LOG = Logger.getLogger(AbstractAMIDictTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	// injected by picocli
	@ParentCommand
	AMIDict parent;
	
	@Override
	public Void call() throws Exception {
		runCommands();
		return null;
	}

	/**
	 * assumes arguments have been preset (e.g. by set commands).
	 * Use at own risk
	 */
	public void runCommands() {
		printGenericHeader();
		parseGenerics();

		printSpecificHeader();
		parseSpecifics();

		if (level != null && !Level.WARN.isGreaterOrEqual(level)) {
			System.err.println("processing halted due to argument errors, level:" + level);
		} else {
			runGenerics();
			runSpecifics();
		}
	}

	protected boolean parseGenerics() {
		parent.setLogging();
		printGenericValues();
		return true;
	}

	/**
	 * subclass this if you want to process CTree and CProject differently
	 */
	protected boolean runGenerics() {
		return true;
	}

	protected void printGenericHeader() {
		System.out.println();
		System.out.println("Generic values (" + this.getClass().getSimpleName() + ")");
		System.out.println("================================");
	}

	protected void printSpecificHeader() {
		System.out.println();
		System.out.println("Specific values (" + this.getClass().getSimpleName() + ")");
		System.out.println("================================");
	}

	/**
	 * prints generic values from abstract superclass.
	 * at present cproject, ctree and filetypes
	 */
	private void printGenericValues() {
		if (verbosity().length > 0) {
//			System.out.println("input basename      " + getInputBasename());
//			System.out.println("input basename list " + getInputBasenameList());
//			System.out.println("cproject            " + (cProject == null ? "" : cProject.getDirectory().getAbsolutePath()));
//			System.out.println("ctree               " + (cTree == null ? "" : cTree.getDirectory().getAbsolutePath()));
//			System.out.println("cTreeList           " + prettyPrint(cTreeList));
//			System.out.println("excludeBase         " + excludeBase());
//			System.out.println("excludeTrees        " + excludeTrees());
//			System.out.println("forceMake           " + getForceMake());
//			System.out.println("includeBase         " + includeBase());
//			System.out.println("includeTrees        " + includeTrees());
//			System.out.println("log4j               " + (log4j() == null ? "" : new ArrayList<String>(Arrays.asList(log4j()))));
//			System.out.println("verbose             " + verbosity().length);
		} else {
			System.out.println("-v to see generic values");
		}
	}



	/**
	 * creates toplevel ContentMine directory in which all dictionaries and other tools
	 * will be stored. By default this is "ContentMine" under the users home directory.
	 * It is probably not a good idea to store actual projects here, but we will eveolve the usage.
	 *
	 * @return null if cannot create directory
	 */
	protected File getOrCreateExistingContentMineDir() {
		if (contentMineDir == null) {
			// null means cannot be created
		} else if (contentMineDir.exists()) {
			if (!contentMineDir.isDirectory()) {
				LOG.error(contentMineDir + " must be a directory");
				contentMineDir = null;
			}
		} else {
			LOG.info("Creating " + CONTENT_MINE_HOME + " directory: " + contentMineDir);
			try {
				contentMineDir.mkdirs();
			} catch (Exception e) {
				LOG.error("Cannot create " + contentMineDir);
				contentMineDir = null;
			}
		}
		return contentMineDir;
	}

	public void runCommands(String cmd) {
		String[] args = cmd == null ? new String[]{} : cmd.trim().split("\\s+");
		runCommands(args);
	}

	/**
	 * parse commands and pass to CommandLine
	 * calls CommandLine.call(this, args)
	 *
	 * @param args
	 */
	public void runCommands(String[] args) {
		init();
//		// add help
		args = args.length == 0 ? new String[]{"--help"} : args;
		new CommandLine(this).execute(args);
	}

	private void init() {
		
	}

	/** NOT USED?
    @Parameters(index = "0"	,
    		arity="0..*",
    		description = "primary operation: (${COMPLETION-CANDIDATES}); if no operation, runs help"
    		)
    protected Operation operation = Operation.help;
    */

	/** NOT used?
    @Option(names = {"--baseurl"}, 
    		arity="1",
   		    description = "base URL for all wikipedia links "
    		)
    protected String baseUrl = "https://en.wikipedia.org/wiki";
    */

	/**
    @Option(names = {"--booleanquery"}, 
    		arity="0..1",
   		    description = "generate query as series of chained OR phrases"
    		)
    protected boolean booleanQuery = false;
    */
    /**
    @Option(names = {"--descriptions"}, 
    		arity="1..*",
   		description = "description fields (free form) such as source, author"
    		)
    protected String[] description;
    */
    
	/** Toplevel */
    @Option(names = {"-d", "--dictionary"}, 
    		arity="1..*",
    		description = "input or output dictionary name/s. for 'create' must be singular; when 'display' or 'translate', any number. "
    				+ "Names should be lowercase, unique. [a-z][a-z0-9._]. Dots can be used to structure dictionaries into"
    				+ "directories. Dictionary names are relative to 'directory'. If <directory> is absent then "
    				+ "dictionary names are absolute.")
    protected List<String> dictionaryList = null;
	
    /** both create and translate */
    @Option(names = {"--directory"}, 
    		arity="1",
    		description = "top directory containing dictionary/s. Subdirectories will use structured names (NYI). Thus "
    				+ "dictionary 'animals' is found in '<directory>/animals.xml', while 'plants.parts' is found in "
    				+ "<directory>/plants/parts.xml. Required for relative dictionary names.")
    protected String dictionaryTopname = null;

    /**
    @Option(names = {"--urlref"}, 
    		arity="1..*",
    		split=",",
    		description = "for non-structured pages I think")
    protected String[] urlref;
*/
    
//    @Mixin CProjectTreeMixin proTree;

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
	
	public static final String ALL = "ALL";
	public static final String HELP = "HELP";
	public static final String SEARCH = "search";
	private static final String DICTIONARY_TOP_NAME = "dictionary/";
	private static final String DICTIONARIES_NAME = DICTIONARY_TOP_NAME + "dictionaries";
	protected static final String SLASH = "/";
    public final static List<String> WIKIPEDIA_STOP_WORDS = Arrays.asList(new String[]{
        	"citation needed",
        	"full citation needed"
        });
	public static final String DOT = ".";
	public static final String WIKIDATA = "wikidata";
	public static final String WIKIPEDIA = "wikipedia";
	public static final String HTTPS_EN_WIKIPEDIA_ORG_WIKI = "https://en.wikipedia.org/wiki/";
	
	protected static File HOME_DIR = new File(System.getProperty("user.home"));
	protected static String CONTENT_MINE_HOME = "ContentMine";
	protected static File DEFAULT_CONTENT_MINE_DIR = new File(HOME_DIR, CONTENT_MINE_HOME);



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


	protected HashSet<String> missingWikidataSet;
	public Set<String> missingWikipediaSet;
	protected DictionaryFileFormat dictInformat;
	protected DictionaryFileFormat dictOutformat;
	private   List<String> descriptionList;
	protected List<String> inputList;
	protected File dictionaryTop;
	protected List<WikiLink> wikiLinkList;
	protected File contentMineDir = DEFAULT_CONTENT_MINE_DIR;
	private Level level;
	protected boolean useAbsoluteNames;

	public AbstractAMIDictTool() {
		initDict();
	}
	
	private void initDict() {
	}
	
	public static void main(String args) {
		main(args.trim().split("\\s+"));
	}
	
	public static void main(String[] args) {
        AbstractAMIDictTool amiDictionary = new AbstractAMIDictTool();
		amiDictionary.runCommands(args);
	}
	
	private void combineLevel(Level level) {
		if (level == null) {
			LOG.warn("null level");
		} else if (this.level == null) {
			this.level = level;
		} else if (level.isGreaterOrEqual(this.level)) {
			this.level = level;
		}
	}
	
	protected void addLoggingLevel(Level level, String message) {
		combineLevel(level);
		if (level.isGreaterOrEqual(Level.WARN)) {
			System.err.println(this.getClass().getSimpleName() + ": " + level + ": " + message);
		}
	}

	protected void parseSpecifics() {
		
	}

	protected void runSub() {
		System.err.println("Overload this in subDictionaryTool "+this.getClass());
	}
	
//	@Override
	protected void runSpecifics() {
        runDictionary();
	}

	private void runDictionary() {
//		if (getOrCreateExistingDictionaryTop() == null) {
//			LOG.warn("No dictionary directory; aborted");
//			return;
//		}
		// runs subCommands
		runSub();
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


	public static DefaultAMIDictionary readDictionary(File file) {
		DefaultAMIDictionary amiDictionary = new DefaultAMIDictionary();
		amiDictionary.readDictionary(file);
		return amiDictionary;
	}

	private void printDebug() {
//		System.out.println("baseUrl       "+this.baseUrl);
//		System.out.println("booleanQuery  "+this.booleanQuery);
//		System.out.println("descriptions  "+this.description);
//		System.out.println("dataCols      "+dataCols);
		System.out.println("dictionary    "+(this.dictionaryList == null ? "null" : Arrays.asList(this.dictionaryList)));
//		System.out.println("dictionaryTop     "+this.dictionaryTopname);
//		System.out.println("href          "+href);
//		System.out.println("hrefCols      "+hrefCols);
		System.out.println("inputs        "+inputList);
		System.out.println("input         "+input());
//		System.out.println("informat      "+this.informat);
		System.out.println("dictInformat  "+dictInformat);
//		System.out.println("linkCol       "+this.linkCol);
		//System.out.println("log4j         "+makeArrayList(log4j));
//		System.out.println("nameCol       "+this.nameCol);
//		System.out.println("operation     "+this.operation);
//		System.out.println("outformats    "+makeArrayList(this.outformats));
//		System.out.println("query         "+queryChunk);
//		System.out.println("search        "+this.searchTerms);
//		System.out.println("searchfile    "+this.searchTermFilenames);
//		System.out.println("splitCol      "+this.splitCol);
//		System.out.println("templatea     "+this.templateNames);
//		System.out.println("termCol       "+this.termCol);
//		System.out.println("terms         "+(termList == null ? null : "("+termList.size()+") "+termList));
//		System.out.println("termfile      "+this.termfile);
//		System.out.println("urlref        "+this.urlref);
		System.out.println("wikiLinks     "+this.wikiLinks);
//		System.out.println("wptype        "+this.wptype);
		
	}

	protected boolean[] verbosity() {
		return parent.loggingOptions.verbosity;
	}


	private List<?> makeArrayList(Object[] list) {
		return list == null ? null : Arrays.asList(list);
	}
		
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

	protected String input() {
		return parent.generalOptions.input;
	}

	protected void input(String newValue) {
		parent.generalOptions.input = newValue;
	}

	protected InputStream openInputStream() {
		InputStream inputStream = null;
		if (input() != null) {
			try {
				if (parent.generalOptions.input.startsWith("http")) {
					inputStream = new URL(input()).openStream();
				} else {
					File inputFile = new File(input());
					if (!inputFile.exists()) {
						throw new RuntimeException("inputFile does not exist: " + inputFile.getAbsolutePath());
					}
					inputStream = new FileInputStream(inputFile);
				}
			} catch (IOException e) {
				addLoggingLevel(Level.ERROR, "cannot read/open stream: " + input());
			}
		}
		return inputStream;
	}

	protected File createDirectory() {
		File directory = null;
		useAbsoluteNames = false;
		if (dictionaryTopname != null) {
			directory = new File(dictionaryTopname);
		} else if (dictionaryList != null && dictionaryList.size() > 0){
			directory = new File(dictionaryList.get(0)).getParentFile();
			useAbsoluteNames = true;
		} else {
			addLoggingLevel(Level.ERROR, "Must give either 'directory' or existing absolute filenames of dictionaries");
		}
		return directory;
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

}
