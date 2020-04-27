package org.contentmine.ami.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.CProjectTreeMixin;
import org.contentmine.ami.dictionary.CMJsonDictionary;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.lookups.WikiResult;
import org.contentmine.ami.lookups.WikipediaDictionary;
import org.contentmine.ami.lookups.WikipediaLookup;
import org.contentmine.ami.tools.dictionary.DictionaryDisplayTool;
import org.contentmine.ami.tools.download.CurlDownloader;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
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
import org.contentmine.norma.NAConstants;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import nu.xom.Attribute;
import nu.xom.Element;
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
		})
public class AMIDictionaryTool extends AbstractAMITool {
	

	private static final String UTF_8 = "UTF-8";
	private static final Logger LOG = Logger.getLogger(AMIDictionaryTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String HTTPS_EN_WIKIPEDIA_ORG_WIKI = "https://en.wikipedia.org/wiki/";
	private static final String SLASH_WIKI_SLASH = "/wiki/";
	private static final String CM_PREFIX = "CM.";
	
	public static final String ALL = "ALL";
	public static final String FULL = "FULL";
	public static final String HELP = "HELP";
	public static final String LIST = "LIST";
	public static final String SEARCH = "search";
	private static final String DICTIONARY_TOP_NAME = "dictionary/";
	private static final String DICTIONARIES_NAME = DICTIONARY_TOP_NAME + "dictionaries";
	private static final String DOT = ".";
	private static final String SLASH = "/";

	private static final String HTTPS_EN_WIKIPEDIA_ORG = "https://en.wikipedia.org";
	public final static String WIKIPEDIA_BASE = HTTPS_EN_WIKIPEDIA_ORG + SLASH_WIKI_SLASH;
	private static final String WIKIPEDIA = "wikipedia";
	private static final String WIKIDATA = "wikidata";
	private static final String WIKITABLE = "wikitable";

	private static final String HTTP = "http";

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
    
    @Option(names = {"--datacols"}, 
    		split=",",
    		arity="1..*",
    	    paramLabel = "datacol",
   		description = "use these columns (by name) as additional data fields in dictionary. datacols='foo,bar' creates "
   				+ "foo='fooval1' bar='barval1' if present. No controlled use or vocabulary and no hyperlinks."
    		)
    private String[] dataCols;
    
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
    private List<String> dictionaryList = null;

    @Option(names = {"--directory"}, 
    		arity="1",
    		description = "top directory containing dictionary/s. Subdirectories will use structured names (NYI). Thus "
    				+ "dictionary 'animals' is found in '<directory>/animals.xml', while 'plants.parts' is found in "
    				+ "<directory>/plants/parts.xml. Required for relative dictionary names.")
    private String dictionaryTopname = null;

    @Option(names = {"--hrefcols"}, 
    		split=",",
    		arity="1..*",
    	 	paramLabel = "hrefcol",
   		description = "external hyperlink column from table; might be Wikidata or remote site(s)"
    		)
    private String[] hrefCols;

    @Option(names = {"--informat"}, 
    		arity="1",
    		paramLabel = "input format",
    		description = "input format (${COMPLETION-CANDIDATES})"
    		)
    private InputFormat informat;
    
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
    private DictionaryFileFormat[] outformats = new DictionaryFileFormat[] {DictionaryFileFormat.xml};

    @Option(names = {"--query"}, 
    		arity="0..1",
    	    defaultValue="10",
    	    paramLabel = "query",
    		description = "generate query for cut and paste into EPMC or similar. "
    				+ "value sets size of chunks (too large crashes EPMC). If missing, no query generated."
    		)
    private Integer queryChunk = null;
        
    @Option(names = {"--search"}, 
    		arity="1..*",
    	    paramLabel = "search",
    		description = "search dictionary for these terms (experimental)"
    		)
    private List<String> searchTerms;
        
    @Option(names = {"--searchfile"}, 
    		arity="1..*",
    	    paramLabel = "searchfile",
    		description = "search dictionary for terms in these files (experimental)"
    		)
    private List<String> searchTermFilenames;
        
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
    
    @Option(names = {"--terms"}, 
    		arity="1..*",
    		split=",",
    		description = "list of terms (entries), comma-separated")
    private List<String> terms;

    @Option(names = {"--termfile"}, 
    		arity="1",
//    		split=",",
    		description = "list of terms in file, line-separated")
    private File termfile;

    @Option(names = {"--title"}, 
    		arity="1",
    		description = "title for dictionary to be used if not already in source")
    private String title;

    @Option(names = {"--urlref"}, 
    		arity="1..*",
    		split=",",
    		description = "for non-structured pages I think")
    private String[] urlref;

    @Option(names = {"--wikilinks"}, 
    		arity="0..*",
    		defaultValue = "wikipedia,wikidata",
    		split = ",",
    		description = "try to add link to Wikidata and/or Wikipedia page of same name. ")
    private WikiLink[] wikiLinks = null;/* new WikiLink[]{WikiLink.wikipedia, WikiLink.wikidata}*/;

    @Option(names = {"--wptype"}, 
    		arity="1",
    		description = "type of input (HTML , mediawiki)")
    private WikiFormat wptype;
    
    @Mixin CProjectTreeMixin proTree;

	@Option(names = {"--testString"},
			description = {
					"String input for debugging; semantics depend on task"})
	protected String testString = null;

    public final static List<String> WIKIPEDIA_STOP_WORDS = Arrays.asList(new String[]{
        	"citation needed",
        	"full citation needed"
        });

    DictionaryData dictionaryData;
// converted from args    
	private List<String> termList;
	private List<String> nameList;
	private List<String> linkList;
	private List<RectTabColumn> hrefColList;
	private List<RectTabColumn> dataColList;
	private Element dictionaryElement;
	private HtmlTbody tBody;
	private WikipediaDictionary wikipediaDictionary;

	protected List<File> files;
	private DictionaryFileFormat dictInformat;
	private DictionaryFileFormat dictOutformat;
	private CMJsonDictionary cmJsonDictionary;
	private DefaultAMIDictionary xmlDictionary;
	private List<WikiLink> wikiLinkList;
	private HashSet<String> missingWikipediaSet;
	private HashSet<String> missingWikidataSet;
	private List<String> descriptionList;
	private Set<String> termSet;
	private List<String> inputList;
	private String currentTemplateName;
	protected File dictionaryTop;
	protected int maxEntries = 0;


	public AMIDictionaryTool() {
		initDict();
	}
	
	private void initDict() {
	}
	
	public static void main(String args) {
		main(args.trim().split("\\s+"));
	}
	public static void main(String[] args) {
        AMIDictionaryTool amiDictionary = new AMIDictionaryTool();
        amiDictionary.initDictionaryData();
		amiDictionary.runCommands(args);
	}

	private void initDictionaryData() {
		dictionaryData = new DictionaryData();
		
        dictionaryData.dataCols    = dataCols;
        dictionaryData.dictionary  = dictionaryList;
        dictionaryData.dictionaryTopname   = dictionaryTopname;
//        dictionaryData.href        = href;
        dictionaryData.hrefCols    = hrefCols;
        dictionaryData.informat    = informat;
        dictionaryData.input       = input();
        dictionaryData.linkCol     = linkCol;
        dictionaryData.nameCol     = nameCol;
        dictionaryData.operation   = operation;
        dictionaryData.outformats  = outformats;
        dictionaryData.termCol     = termCol;
        dictionaryData.terms       = terms;
        dictionaryData.wikiLinks    = wikiLinks;

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

	private void createTemplateNames() {
		for (int i = 0; i < templateNames.size(); i++) {
			String t = templateNames.get(i).trim().replaceAll("\\s+", "_");
			templateNames.set(i, t);
		}
	}
	private String createDictionaryName(String templateName) {
		return templateName.toLowerCase().replaceAll("[^A-Za-z0-9_\\-]", "");
	}

	private void createInputList() {
		inputList = new ArrayList<>();
		for (String templateName : templateNames) {
			createInput(templateName);
			inputList.add(input());
		}
		input(null);
	} 

	private String createInput(String templateName) {
		input("https://en.wikipedia.org/w/index.php?title=Template:" + templateName);
		if (WikiFormat.mwk.equals(wptype)) {
			input(input() + "&action=edit");
		}
		return input();
	}

	@Override
	protected void runSpecifics() {
        runDictionary();
	}

	private void runDictionary() {
		resetMissingLinks();
		if (getOrCreateExistingDictionaryTop() == null) {
			LOG.warn("No dictionary directory; aborted");
			return;
		}
		if (Operation.display.equals(operation)) {
			new DictionaryDisplayTool().displayDictionaries();
		} else if (Operation.create.equals(operation)) {
			createDictionaryList();
		} else if (Operation.search.equals(operation)) {
			searchDictionary();
		} else if (Operation.translate.equals(operation)) {
			translateDictionaries();
		} else {
			System.err.println("no operation given: "+operation);
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


	private void createDictionaryList() {
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

	private void searchDictionary() {
		DefaultAMIDictionary amiDictionary = AMIDictionaryTool.readDictionary(new File(dictionaryList.get(0)));
		Set<String> rawTermSet = amiDictionary.getRawLowercaseTermSet();
		
		if (searchTerms != null) {
			searchDictionaryForTerms(rawTermSet, searchTerms);
		} else if (searchTermFilenames != null) {
			searchTermsInFiles(rawTermSet, searchTermFilenames);	
		}
	}
	
	public static DefaultAMIDictionary readDictionary(File file) {
		DefaultAMIDictionary amiDictionary = new DefaultAMIDictionary();
		amiDictionary.readDictionary(file);
		return amiDictionary;
	}



	private void searchTermsInFiles(Set<String> rawTermSet, List<String> searchTermFilenames) {
		List<String> allSearchTerms = new ArrayList<>();
		for (String searchTermFilename : searchTermFilenames) {
			File file = new File(searchTermFilename);
			try {
				List<String> searchTerms0 = FileUtils.readLines(file, Charset.forName(UTF_8));
				allSearchTerms.addAll(searchTerms0);
			} catch (Exception e) {
				throw new RuntimeException("Cannot read "+file);
			}
		}
		searchDictionaryForTerms(rawTermSet, allSearchTerms);
	}

	private static void searchDictionaryForTerms(Set<String> rawTermSet, List<String> searchTerms) {
		rawTermSet = Util.toLowercase(rawTermSet);
		for (String searchTerm : searchTerms) {
			if (rawTermSet.contains(searchTerm)) {
				System.out.println("found: "+searchTerm);
			} else {
				System.err.println("Cannot find term in dictionary "+searchTerm);
			}
		}
	}

	private void translateDictionaries() {
		File directory = null;
		boolean useAbsoluteNames = false;
		if (dictionaryTopname != null) {
			directory = new File(dictionaryTopname);
		} else if (dictionaryList != null && dictionaryList.size() > 0){
			directory = new File(dictionaryList.get(0)).getParentFile();
			useAbsoluteNames = true;
		} else {
			addLoggingLevel(Level.ERROR, "Must give either 'directory' or existing absolute filenames of dictionaries");
			return;
		}
		for (String dictionaryS : dictionaryList) {
			String basename = FilenameUtils.getBaseName(dictionaryS);
			File dictionaryFile = (useAbsoluteNames) ? new File(dictionaryS) : new File(directory, dictionaryS);
			if (!dictionaryFile.exists()) {
    			addLoggingLevel(Level.ERROR, "File does not exist: "+dictionaryFile);
				continue;
			}
			dictInformat = DictionaryFileFormat.getFormat(FilenameUtils.getExtension(dictionaryS));
			if (dictInformat.equals(dictOutformat)) {
    			addLoggingLevel(Level.WARN, "dictionary input and output formats identical; no action");
				continue;
			}
			File dictOutfile = new File(dictionaryFile.getParentFile(), basename + "." + dictOutformat);
			convertDictionaries(dictionaryFile, dictInformat, dictOutfile, dictOutformat);
		}
		
	}


	private void convertDictionaries(
			File infile, DictionaryFileFormat informat, File outfile, DictionaryFileFormat outformat) {
		if (DictionaryFileFormat.json.equals(informat)) {
			convertJsonDictionaryToXML(infile, outfile);
			
		} else if (DictionaryFileFormat.xml.equals(informat)) {
			xmlDictionary = DefaultAMIDictionary.createSortedDictionary(infile);
			cmJsonDictionary = CMJsonDictionary.convertXMLToJson(xmlDictionary);
		}
	}

	private void convertJsonDictionaryToXML(File infile, File outfile) {
		String inString = null;
		try {
			inString = FileUtils.readFileToString(infile, UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read Json: " + infile, e);
		}
		cmJsonDictionary = CMJsonDictionary.readJsonDictionary(inString);
		xmlDictionary = CMJsonDictionary.convertJsonToXML(cmJsonDictionary);
		if (xmlDictionary != null) {
			addWikiLinksToDictionary(xmlDictionary);
		}
		outputXMLDictionary(outfile);
	}

	private void addWikiLinksToDictionary(DefaultAMIDictionary xmlDictionary) {
		List<Element> entryList = xmlDictionary.getEntryList();
		for (Element entry : entryList) {
			
			addWikiLinks(entry);
		}
	}

	private void outputXMLDictionary(File outfile) {
		if (xmlDictionary != null) {
			try {
				XMLUtil.debug(xmlDictionary.getDictionaryElement(), outfile, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write XML dictionary "+outfile, e);
			}
		}
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

	private void checkColumnsNotNull(List<RectTabColumn> colList, String[] colNames) {
		for (int i = 0; i < colList.size(); i++) {
			if (colList.get(i) == null) {
				LOG.warn("Cannot find column: "+colNames[i]);
			}
		}
	}

	private List<Integer> getColIndexList(List<String> headers, String[] colNamesArray) {
		List<Integer> hrefIndexList = new ArrayList<Integer>();
		for (String colName : colNamesArray) {
			int colIndex = headers.indexOf(colName);
			if (colIndex == -1) {
				LOG.error("Unknown column heading: " + colName);
			}
			hrefIndexList.add(new Integer(colIndex));
		}
		return hrefIndexList;
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

	private void addWikiLinks(Element entry) {
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

	private HtmlElement addWikipediaPage(Element entry, String term) {
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

	private HtmlElement addWikipedia(Element entry) {
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

	private String trimWikipediaUrlBase(String urlValue) {
		if (urlValue.startsWith(SLASH_WIKI_SLASH)) {
			urlValue = urlValue.substring(SLASH_WIKI_SLASH.length());
		} 
		if (urlValue.startsWith(HTTPS_EN_WIKIPEDIA_ORG_WIKI)) {
			urlValue = urlValue.substring(HTTPS_EN_WIKIPEDIA_ORG_WIKI.length());
		} 
		return urlValue;
	}

	private String createDictionaryId() {
		return dictionaryList == null || dictionaryList.size() == 0 ? "null" : dictionaryList.get(0);
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

	public Element getDictionaryElement() {
		return dictionaryElement;
	}
    
	private void printDebug() {
		System.out.println("baseUrl       "+baseUrl);
		System.out.println("booleanQuery  "+booleanQuery);
		System.out.println("descriptions  "+description);
		System.out.println("dataCols      "+dataCols);
		System.out.println("dictionary    "+(dictionaryList == null ? "null" : Arrays.asList(dictionaryList)));
		System.out.println("dictionaryTop     "+dictionaryTopname);
//		System.out.println("href          "+href);
		System.out.println("hrefCols      "+hrefCols);
		System.out.println("inputs        "+inputList);
		System.out.println("input         "+input());
		System.out.println("informat      "+informat);
		System.out.println("dictInformat  "+dictInformat);
		System.out.println("linkCol       "+linkCol);
		//System.out.println("log4j         "+makeArrayList(log4j));
		System.out.println("nameCol       "+nameCol);
		System.out.println("operation     "+operation);
		System.out.println("outformats    "+makeArrayList(outformats));
		System.out.println("query         "+queryChunk);
		System.out.println("search        "+searchTerms);
		System.out.println("searchfile    "+searchTermFilenames);
		System.out.println("splitCol      "+splitCol);
		System.out.println("templatea     "+templateNames);
		System.out.println("termCol       "+termCol);
		System.out.println("terms         "+(termList == null ? null : "("+termList.size()+") "+termList));
		System.out.println("termfile      "+termfile);
		System.out.println("title         "+title);
		System.out.println("urlref        "+urlref);
		System.out.println("wikiLinks     "+wikiLinkList);
		System.out.println("wptype        "+wptype);
		
	}

	private List<?> makeArrayList(Object[] list) {
		return list == null ? null : Arrays.asList(list);
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
		List<HtmlA> aList = AMIDictionaryTool.parseMediaWiki(mwString);
		System.err.println("read A's :" +aList.size());
		addAHrefs(aList);
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
	
    /** get column values (as text)
     * skips header
     * 
     * @param colIndex
     * @return list of values
     */
	private List<String> getColumnValues(int colIndex) {
		return addCells(colIndex, LinkField.VALUE, (String) null);
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
	

	private void removeFirstElementsOfColumns(List<String> names, List<String> hrefs) {
		names.remove(0);
		if (hrefs.size() == 0) {
			LOG.trace("no links found");
			hrefs = new ArrayList<String>(names.size());
		} else {
			hrefs.remove(0);
		}
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

	// ================== LIST ===================

	
	
//	private File getDictionaryDir() {
//		return dictionaryDir;
//	}
	
	private File getXMLDictionaryDir(File dictionaryDir) {
		return new File(dictionaryDir, "xml/");
	}

	public int getMaxEntries() {
		return maxEntries;
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

//	public String getDirectory() {
//		return directory;
//	}
//
//	public void setDirectory(String directory) {
//		this.directory = directory;
//	}


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
class Data {
    String ss;	
}
