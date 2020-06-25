package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.lookups.WikiResult;
import org.contentmine.ami.lookups.WikipediaLookup;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;

/** mainly to manage help and parse dictionaries.
 * 
 * @author pm286
 *
 */


@Command(mixinStandardHelpOptions = true, version = "ami-dict 1.0.0")
public abstract class AbstractAMIDictTool implements Callable<Void> {
	

	public static final String UTF_8 = "UTF-8";

	/** Marker used to mark log statements that replaced call to the (now removed) `amiDebug` method. */
	public static final Marker SPECIAL = MarkerManager.getMarker("amiDebug");
	private static final Logger LOG = LogManager.getLogger(AbstractAMIDictTool.class);

	// injected by picocli
	@ParentCommand
	protected AMIDict parent;
	
	@Spec CommandSpec spec; // injected by picocli

	
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

		if (showstopperEncountered) {
			LOG.fatal("processing halted due to argument errors");
		} else {
			runGenerics();
			runSpecifics();
		}
	}

	protected boolean parseGenerics() {
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
		AMIUtil.printHeader(this, System.out, "Generic");
	}

	protected void printSpecificHeader() {
		AMIUtil.printHeader(this, System.out, "Specific");
	}

	/** override */
	protected abstract void parseSpecifics();

	public int getVerbosityInt() {
		return verbosity().length;
	}

	/**
	 * prints generic values from abstract superclass.
	 * at present 
	 */
	private void printGenericValues() {
    	if (verbosity().length > 0) {
			printOptionValues(System.out);
		} else {
			System.out.println("-v to see generic values");
		}
	}

//	/**
//	 * Prints all options for this command with their value (either user-specified or the default)
//	 * to the specified stream.
//	 * @param stream the stream to write options to
//	 */
//	protected void printOptionValues(PrintStream stream) {
//		ParseResult parseResult = spec.commandLine().getParseResult();
//		for (OptionSpec option : spec.options()) {
//			String label = parseResult.hasMatchedOption(option)
//					? "(matched)" : "(default)";
//			stream.printf("%s: %s %s%n", option.longestName(), option.getValue(), label);
//		}
//	}
//	
	/**
	 * Prints all options for this command with their value (either user-specified or the default)
	 * to the specified stream.
	 * @param stream the stream to write options to
	 */
	protected void printOptionValues(PrintStream stream) {
		ParseResult parseResult = spec.commandLine().getParseResult();
		for (OptionSpec option : spec.options()) {
			String label = parseResult.hasMatchedOption(option)
					? "(matched)" : "(default)";
			stream.printf("%-20s: %1s %9s%n", option.longestName(), label.substring(1,  2), option.getValue());
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

    
//    @Mixin CProjectTreeMixin proTree;

	@Option(names = {"--testString"},
			description = {
					"String input for debugging; semantics depend on task"})
	protected String testString = null;

	@Option(names = {"--wikilinks"}, 
			arity="0..*",
			defaultValue = "wikipedia",
			split = ",",
			description = "try to add link to Wikidata and/or Wikipedia page of same name. ")
	protected WikiLink[] wikiLinks = null;/* new WikiLink[]{WikiLink.wikipedia, WikiLink.wikidata}*/
	
	public static final String ALL = "ALL";
	public static final String HELP = "HELP";
	public static final String SEARCH = "search";
	private static final String DICTIONARY_TOP_NAME = "";
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
	public static final String XML = "xml";
	
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
		;
		private WikiLink() {
			
		}
		public String toString() {
			return ">>"+this.name();
		}
	}

	public enum WikiFormat {
		html,
		mwk,
	}
	
	public enum FieldType {
		ATTRIBUTE,
		ELEMENT
	}
		
	public enum DictionaryField {
		description(FieldType.ELEMENT),
		entry(FieldType.ELEMENT),
		name(FieldType.ATTRIBUTE),
		synonym(FieldType.ELEMENT),
		term(FieldType.ATTRIBUTE),
		title(FieldType.ATTRIBUTE),
		wikidata(FieldType.ATTRIBUTE),
		wikipedia(FieldType.ATTRIBUTE),;
		
		private FieldType fieldType;

		private DictionaryField(FieldType fieldType) {
			this.fieldType = fieldType;
		}

		public FieldType getType() {
			return fieldType;
		}
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
	protected boolean showstopperEncountered;
	protected boolean useAbsoluteNames;

	public AbstractAMIDictTool() {
		initDict();
	}
	
	private void initDict() {
	}

	protected void runSub() {
		System.err.println("Overload this in subDictionaryTool "+this.getClass());
	}
	
//	@Override
	protected void runSpecifics() {
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
    		if (parent.directory != null) {
    			dictionaryTop = parent.directory;
    		} else {
	    		getOrCreateExistingContentMineDir();
				if (contentMineDir != null) {
	    			dictionaryTop = new File(contentMineDir, DICTIONARIES_NAME);
	    		}
    		}
    	}
	   	if (dictionaryTop != null) {
	    	if (!dictionaryTop.exists()) {
	    		dictionaryTop.mkdirs();
	    	} else if (!dictionaryTop.isDirectory()) {
	    		LOG.error(dictionaryTop + " must be a directory");
				showstopperEncountered = true;
	    	}
    	}
	   	return dictionaryTop;
	}


	public static DefaultAMIDictionary readDictionary(File file) {
		DefaultAMIDictionary amiDictionary = new DefaultAMIDictionary();
		amiDictionary.readDictionary(file);
		return amiDictionary;
	}

	protected boolean[] verbosity() {
		return parent.loggingOptions.verbosity;
	}

	protected String input() {
		return parent.generalOptions.input;
	}

	protected void input(String newValue) {
		parent.generalOptions.input = newValue;
	}

	protected String inputName() {
		return parent.generalOptions.inputBasename;
	}

	protected void inputName(String newValue) {
		parent.generalOptions.inputBasename = newValue;
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
				LOG.error("cannot read/open stream: " + input());
				showstopperEncountered = true;
			}
		}
		return inputStream;
	}

	protected File createDirectory() {
		File directory = null;
		useAbsoluteNames = false;
		if (parent.directory != null) {
			directory = parent.directory;
		} else if (parent.getDictionaryList() != null && parent.getDictionaryList().size() > 0){
			directory = new File(parent.getDictionaryList().get(0)).getParentFile();
			useAbsoluteNames = true;
		} else {
			LOG.error("Must give either 'directory' or existing absolute filenames of dictionaries");
			showstopperEncountered = true;
		}
		return directory;
	}

	protected List<File> collectDictionaryFiles(File dictionaryHead) {
		if (dictionaryHead == null) {
			System.err.println("null dictionaryhead");
			return null;
		}
		
		System.out.println("dictionaries from "+dictionaryHead);
		
		File[] listFiles = dictionaryHead.listFiles();
		if (listFiles == null) {
			LOG.error("no dictionary files; terminated");
			return new ArrayList<>();
		}
		List<File> newFiles = collectDictionaryFiles(listFiles);
		listDictionaryInfo(newFiles);
		return newFiles;
	}

	public List<File> collectDictionaryFiles(File[] listFiles) {
		List<File> newFiles = new ArrayList<File>();
		List<File> files = Arrays.asList(listFiles);
		for (File file : files) {
			String filename = file.toString();
			if (XML.equals(FilenameUtils.getExtension(filename))) {
				System.out.println("       <"+FilenameUtils.getName(file.toString())+">");
				newFiles.add(file);
			} else if (file.isDirectory()) {
				System.out.println("\n[dir "+file+"]\n");
				newFiles.addAll(collectDictionaryFiles(file));
			}
		}
		Collections.sort(newFiles);
//		System.out.println(">>>"+newFiles.size());
		return newFiles;
	}

	private void listDictionaryInfo(List<File> newFiles) {
		for (File file : newFiles) {
			listDictionaryInfo(file);
		}
	}

	public Element listDictionaryInfo(File file) {
		String dictionaryName = FilenameUtils.getBaseName(file.toString());
		Element dictionaryElement = null;
		try {
			dictionaryElement = XMLUtil.parseQuietlyToRootElement(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Cannot find "+file);
		}
		listDictionaryInfo(dictionaryName, dictionaryElement);
		return dictionaryElement;
	}

	protected void listDictionaryInfo(String dictionaryName, Element dictionaryElement) {
		// NO-OP overridden in DisplayTool
	}

	// WIKIMEDIA =========================
	
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
					String description = wikiResult.getDescription();
					if (description != null) {
						XMLUtil.addNonNullAttribute(entry, DictionaryTerm.DESCRIPTION, description);
					}
				} else {
					missingWikidataSet.add(term);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot add entry: "+e.getMessage());
		}
	}

	public HtmlElement addWikipediaPage(Element entry, String term) {
		HtmlElement wikipediaPage = null;
		if (wikiLinkList.contains(WikiLink.wikipedia)) {
			wikipediaPage = addWikipedia(entry);
		}
		if (wikipediaPage == null) {
			System.err.print("!WP");
			missingWikipediaSet.add(term);
		} else {
			entry.addAttribute(new Attribute(WIKIPEDIA, term));
			System.out.print(".");
		}
		return wikipediaPage;
	}

	public HtmlElement addWikipedia(Element entry) {
		HtmlElement wikipediaPage = null;
		URL wikipediaUrl = null;
		try {
			String name = entry.getAttributeValue("name");
			if (name != null) {
				name = name.replace(" ", "_");
				wikipediaUrl = new URL(HTTPS_EN_WIKIPEDIA_ORG_WIKI + name);
				LOG.info(SPECIAL, "WP url: " + wikipediaUrl);
				InputStream is = wikipediaUrl.openStream();
				Element element = HtmlUtil.readTidyAndCreateElement(is);
//				debugWikipediaPage(name, element);
				wikipediaPage = element == null ? null : HtmlElement.create(element);
			}
		} catch (RuntimeException e) {
			System.err.println("cannot parse "+wikipediaUrl);
			throw e;
		} catch (MalformedURLException e) {
			throw new RuntimeException("bad URL ", e);
		} catch (IOException e) {
			LOG.error(SPECIAL, "wikimedia IO exception: " + e.getMessage());
			throw new RuntimeException(e);
		} catch (Exception e) {
			LOG.error(SPECIAL, "wikimedia Exception: " + e.getMessage());
			throw new RuntimeException(e);
		}
		return wikipediaPage;
	}

	private void debugWikipediaPage(String name, Element element) {
		File xmlFile = new File("target/wikipedia/"+name+".html");
		LOG.info(SPECIAL, "writing debug wikipedia page "+xmlFile);
		XMLUtil.writeQuietly(element, xmlFile, 1);
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
