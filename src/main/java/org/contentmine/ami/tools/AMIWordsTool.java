package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.CommandProcessor;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.ami.plugins.word.WordCollectionFactory;
import org.contentmine.ami.plugins.word.WordPluginOption;
import org.contentmine.ami.wordutil.WordSetWrapper;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.OptionFlag;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
running: word([frequencies])[{xpath:@count>20}, {w.stopwords:pmcstop.txt stopwords.txt}]
WS: /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10  
DefaultArgProcessor  - running method: runExtractWords
DefaultArgProcessor  - running method: outputWords
[...7 snips]
DefaultArgProcessor  - running method: runExtractWords
DefaultArgProcessor  - running method: outputWords

filter: word([frequencies])[{xpath:@count>20}, {w.stopwords:pmcstop.txt stopwords.txt}]
DefaultArgProcessor  - running method: runFilter
DefaultArgProcessor  - running method: outputFilter
DefaultArgProcessor  - running method: outputMethod
[...7 snips]
DefaultArgProcessor  - running method: runFilter
DefaultArgProcessor  - running method: outputFilter
DefaultArgProcessor  - running method: outputMethod

DefaultArgProcessor  - running method: finalFilter

summary: word([frequencies])[{xpath:@count>20}, {w.stopwords:pmcstop.txt stopwords.txt}]
DefaultArgProcessor  - running method: runSummaryFile
DefaultArgProcessor  - running method: runDFFile
[...7snipped]
DefaultArgProcessor  - running method: runSummaryFile
DefaultArgProcessor  - running method: runDFFile

DefaultArgProcessor  - running method: finalSummaryFile
DefaultArgProcessor  - running method: finalDFFile
*/

/** analyses bitmaps
 * 
 * @author pm286
 *
 */
@Command(
name = "words",
description = {
		"Analyzes word frequencies."
})
public class AMIWordsTool extends AbstractAMISearchTool {
//public class AMIWordsTool extends AMISearchTool {
	private static final String OPTIONS_JOIN = "~";
	private static final String XPATH_JOIN = ":";
	private static final String STOPWORD_JOIN = "_";
	private static final String W_STOPWORDS = "w.stopwords:";

	private static final Logger LOG = LogManager.getLogger(AMIWordsTool.class);
/** I think only frequencies works!
	 * 
	 * @author pm286
	 *
	 */
	public enum WordMethod {
		frequencies,
		wordLengths,
//		search,
//		wordSearch
		;
		}
		
	/** Word and wordList transformations
	 * 
	 * @author pm286
	 *
	 */
	public enum WordTransform {
		abbreviation(AMIArgProcessor.ABBREVIATION),
		capitalized(AMIArgProcessor.CAPITALIZED),
		ignore(AMIArgProcessor.IGNORE),
		;
		private String amiName;

		private WordTransform(String amiName) {
			this.amiName = amiName;
		}
		public String getAMIName() {
			return amiName;
		}
	}

    @Option(names = {"--abbreviation"},
            description = "extract abbreviations")
    private Boolean abbreviation = false;

    @Option(names = {"--capital"},
            description = "extract capitalised words")
    private Boolean capital = false;

    @Option(names = {"--filter"},
            description = "run filter")
    private Boolean filter = false;

    @Option(names = {"--filterfinal"},
            description = "run filter final (reduce)")
    private Boolean filterFinal = false;

    @Option(names = {"--filtersummary"},
            description = "run filter summary")
    private Boolean filterSummary = false;

    @Option(names = {"--ignorecase"},
            description = "ignore case")
    private Boolean ignoreCase = false;

    @Option(names = {"--methods"},
    		arity = "1..*",
            description = "frequencies and other word targets (${COMPLETION-CANDIDATES}); "
            		+ "201902 only frequencies implemented")
    private List<WordMethod> wordMethods = Arrays.asList(new WordMethod[]{WordMethod.frequencies});

    @Option(names = {"--processtree"},
            description = " use new processTree style of processing")
	private boolean processTree = true;

    @Option(names = {"--stemming"},
            description = "apply stemming")
    private Boolean stemming = false;
	
    @Option(names = {"--stopworddir"},
    		arity = "1",
            description = "Stop word directory (only one allowed) not yet working")
    private List<File> stopwordDir;
	
    @Option(names = {"--stopwords"},
    		arity = "1..*",
            description = "Stop word files for w.stopwords: (ex: pmcstop.txt stopwords.txt)")
    private List<String> stopwordLocations = Arrays.asList(new String[]{"pmcstop.txt", "stopwords.txt"});

    @Option(names = {"--transform"},
    		arity = "1..*",
            description = "methods for word transformation")
    private List<WordTransform> transformList = new ArrayList<>();

    @Option(names = {"--xpath"},
    		arity = "1..*",
            description = "sections to analyze, name=xpath")
    private Map<String, String> xpathMap = new HashMap<>();
    /**
    @Option(names = "-x", arity = "0..1",
            defaultValue = "-1", fallbackValue = "-2",
            description = "Option with optional parameter. Default: ${DEFAULT-VALUE}, " +
                          "if specified without parameter: ${FALLBACK-VALUE}")
    int x;
*/
    
	private String wordCmd;
	private WordArgProcessor wordArgProcessor;
	private WordCollectionFactory wordCollectionFactory;
	private List<WordSetWrapper> stopWordSetList;
	

    /**
    @Option(names = {"--word"},
    		arity = "1..*",
            description = "options to analyze 'word' analysis; includes 'frequencies'")
    private List<String> wordList = Arrays.asList(new String[]{"frequencies"});

    word([frequencies])[{xpath:@count>20}, {w.stopwords:pmcstop.txt stopwords.txt}]
     */
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIWordsTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIWordsTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIWordsTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
//    	makeStopwordOptions();
    	makeStopwordSetList();
    	super.parseSpecifics();
	}

    private List<WordSetWrapper> makeStopwordSetList() {
		wordArgProcessor = getOrCreateSearchProcessor();
    	wordArgProcessor.setStopwords(stopwordLocations);
    	stopWordSetList = wordArgProcessor.getStopwordSetList();
    	return stopWordSetList;
	}

	@Override
    protected void runSpecifics() {
		wordArgProcessor = getOrCreateSearchProcessor();
		wordArgProcessor.setWordsTool(this);
		abstractSearchArgProcessor = wordArgProcessor;
		
    	populateArgProcessorFromCLI();
    	createWordListInWordCollectionFactory();
    	
    	if (processTree && processTrees()) { 
    	} else {
    		buildCommandFromBuiltinsAndFacets();
//			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }

    }

	protected void createWordListInWordCollectionFactory() {
		super.createWordListInWordCollectionFactory();
	}

	protected void populateArgProcessorFromCLI() {
		wordArgProcessor.setStemming(stemming);
    	wordArgProcessor.setStopwords(stopwordLocations);
    	wordArgProcessor.setWordCountRange(wordCountRange);
    	for (WordMethod wordMethod : wordMethods) {
    		wordArgProcessor.addChosenMethod(wordMethod);
    	}
	}

	
	protected String buildCommandFromBuiltinsAndFacets() {
		String wordTargets = makeWordTargets();
    	String wordOptions = makeWordOptions();
		wordCmd = wordTargets + wordOptions;
    	LOG.debug("WORD>>> "+wordCmd);
    	return wordCmd;
	}

	protected boolean processTree() {
		processedTree = true;
		LOG.info("AMIWords processTree");
		wordArgProcessor.setCTree(cTree);
		processedTree = extractWords();
		// this is the original, phased out; will run twice unnecessarily because runs project
//		runWords();
//		runWordsNew();
		return processedTree;
	}
	
	/** ================================================= 
	 * @return */


	private boolean extractWords() {
		processedTree = true;
		wordArgProcessor.setCTree(cTree);
		wordArgProcessor.extractWords();
		wordArgProcessor.outputWords("word");
		return processedTree;
	}

	public WordArgProcessor getOrCreateSearchProcessor() {
		if (wordArgProcessor == null) {
			wordArgProcessor = new WordArgProcessor(this);
		}
		return wordArgProcessor;
	}

	protected void processProject() {
		runProjectSearch();
	}
	protected void runProjectSearch() {
		runWordsNew();
	}
	/** probably needs refactoring */
	public void runWordsNew() {
		
		try {
			
			CommandProcessor commandProcessor = new CommandProcessor(cProject.getDirectory());
//			private void createPluginOptionNew(String commandTag, List<String> subOptions, List<OptionFlag> optionFlags) {
			List<String> subOptions = Arrays.asList(new String[]{"fff"});
			List<OptionFlag> optionFlags = Arrays.asList(new OptionFlag[]{new OptionFlag("foo", "value")});
			commandProcessor.parseCommandsNew(WordPluginOption.TAG, subOptions, optionFlags);
			// this runs commands and filters results
			LOG.debug("running command: "+wordCmd);
			commandProcessor.runCommands(this);
			commandProcessor.createDataTables();
		} catch (IOException e) {
			throw new RuntimeException("Cannot run command: "+wordCmd, e);
		}
	}
		
	public void runWords() {
		
		try {
			
			CommandProcessor commandProcessor = new CommandProcessor(cProject.getDirectory());
			List<String> cmdList = Arrays.asList(wordCmd.trim().split("\\s+"));
			LOG.debug("cmdList {}", cmdList);
			commandProcessor.parseCommands(cmdList);
			// this runs commands and filters results
			LOG.debug("running command: "+wordCmd);
			commandProcessor.runLegacyPluginOptions(this);
			// runs runExtractWords
			// runs outputWords
			//
			// then
			//running method: runFilter
			// xpath goes into this
			// DefaultArgProcessor  - filterCTree file(**/word/frequencies/results.xml)xpath(//result[@count>70])
			// DefaultArgProcessor  - running method: outputFilter
			// DefaultArgProcessor  - outputFile PMC3113902/word.frequencies.snippets.xml
			// DefaultArgProcessor  - running method: outputMethod
			
//			String cmd = " --project /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10
//			    -i scholarly.html --w.words frequencies --w.stopwords pmcstop.txt stopwords.txt";
//			new WordArgProcessor(cmd).runAndOutput();
	/**
			commandProcessor.runNormaIfNecessary();
			for (AMIPluginOption pluginOption : commandProcessor.pluginOptions) {
				LOG.info("running: "+pluginOption);
				try {
					pluginOption.run();
//	protected void run() {
//		StringBuilder commandString = createCoreCommandStringBuilder();
//		commandString.append(" --w.words "+optionString);
//		String sw = getOptionFlagString("w.stopwords", " ");
//		commandString.append(sw);
//		LOG.debug("WORD "+commandString);
//		LOG.warn("WS: "+projectDir+"  ");
 // --project /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10 -i scholarly.html --w.words frequencies --w.stopwords pmcstop.txt stopwords.txt
WS: /
//		new WordArgProcessor(commandString.toString()).runAndOutput();
//	}
					
				} catch (Exception e) {
					LOG.error("cannot run command: "+pluginOption +"; " + e.getMessage());
					continue;
				}
				LOG.warn("filter: "+pluginOption);
				pluginOption.runFilterResultsXMLOptions();
				LOG.warn("summary: "+pluginOption);
				pluginOption.runSummaryAndCountOptions(); 
			}
			LOG.trace(commandProcessor.pluginOptions);	 
	*/
			commandProcessor.createDataTables();
		} catch (IOException e) {
			throw new RuntimeException("Cannot run command: "+wordCmd, e);
		}
	}
	
	// ======================================================
	
	private String makeWordTargets() {
		String wordString = null;
		if (wordMethods != null && wordMethods.size() > 0) {
			wordString = "word(";
			for (WordMethod method : wordMethods) {
				wordString += method + " ";			
				wordArgProcessor.add(method.toString());
			}
			wordString = wordString.trim() + ")";
		}
		return wordString;
	}

	private String makeWordOptions() {
		return makeXPathOptions() + OPTIONS_JOIN + makeStopwordOptions();
	}

	private String makeXPathOptions() {
		String xPathString = null;
		if (wordCountRange != null) {
			xPathString = "" + "xpath" + XPATH_JOIN + "@count" + ">" + wordCountRange.getMin() + "";
		}
		return xPathString;		
	}

	private String makeStopwordOptions() {
		String stopwordOptions = null;
		// this is awful
		if (stopwordLocations != null && stopwordLocations.size() > 0) {
			stopwordOptions = W_STOPWORDS;
			for (int i = 0; i < stopwordLocations.size(); i++) {
				if (i > 0) {
					stopwordOptions += STOPWORD_JOIN;
				}
				stopwordOptions += stopwordLocations.get(i);
			}
		}
		return stopwordOptions;
	}

	public List<WordMethod> getWordMethods() {
		return wordMethods;
	}

	public boolean isAbbreviation() {
		return abbreviation;
	}

	public boolean isCapital() {
		return capital;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public List<String> getStopWordsLocationsList() {
		return stopwordLocations;
	}

	public List<WordSetWrapper> getStopWordsSetList() {
		getStopWordsLocationsList();
		return stopWordSetList;
	}

	public boolean isStemming() {
		return stemming;
	}

	public List<String> extractWords(CTree currentCTree) {
		List<String> extractedWords = /*(xpathList.size() > 0) ? 
				extractXPathWords(currentCTree) : */currentCTree.extractWords();
		return extractedWords;
	}

	private Multimap<String, Multiset<String>> extractXPathWords(CTree currentCTree) {
		FileInputStream fis = null;
		File existingFulltextXML = currentCTree.getExistingFulltextXML();
		Element fullTextXML = XMLUtil.parseQuietlyToRootElementWithoutDTD(existingFulltextXML);
		Multimap<String, Multiset<String>> wordMultisetByKey = HashMultimap.create();
		for (String key : xpathMap.keySet()) {
			String xpath = xpathMap.get(key);
			Multiset<String> wordMultiset = HashMultiset.create();
			List<String> texts = XMLUtil.getQueryValues(fullTextXML, xpath);
			for (String text : texts) {
				List<String> words = Util.splitWords(text);  
				wordMultiset.addAll(words);
			}
			wordMultisetByKey.put(key, wordMultiset);
		}
		return wordMultisetByKey;
	}

}
