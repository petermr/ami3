package org.contentmine.ami.plugins.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.ami.plugins.search.SearchSearcher;
import org.contentmine.ami.tools.AMIWordsTool;
import org.contentmine.ami.tools.AMIWordsTool.WordMethod;
import org.contentmine.cproject.args.AbstractTool;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.ContentProcessor;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.files.ResultsElementList;
import org.contentmine.cproject.lookup.DefaultStringDictionary;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.xml.XMLUtil;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class WordArgProcessor extends AbstractSearchArgProcessor {
	
	/*** THIS FUNCTIONALITY IS BEING MOVED TO AMIWordsTool */
	public static final Logger LOG = LogManager.getLogger(WordArgProcessor.class);
public final static String FREQUENCIES = "frequencies";
	public final static String WORD_LENGTHS = "wordLengths";
	public final static String WORD_FREQUENCIES = "wordFrequencies"; // deprecated
	public final static String SEARCH = "search";					
	public final static String WORD_SEARCH = "wordSearch";			// deprecated
	public final static List<String> ANALYSIS_METHODS = Arrays.asList(
		new String[]{
				FREQUENCIES,
				WORD_FREQUENCIES,
				WORD_LENGTHS,
				SEARCH,
				WORD_SEARCH
		});
	
	private static final String TFIDF = "tfidf";
	private static final String TFIDF_XML = "tfidf.xml";
	private static final String TFIDF_HTML = "tfidf.html";
	private static final String AGGREGATE_FREQUENCY = "aggregate";
	private static final String AGGREGATE_XML = "aggregate.xml";
	private static final String AGGREGATE_HTML = "aggregate.html";
	private static final String BOOLEAN_FREQUENCY = "booleanFrequency";
	private static final String BOOLEAN_FREQUENCY_XML = "booleanFrequency.xml";
	private static final String BOOLEAN_FREQUENCY_HTML = "booleanFrequency.html";
	private static final String TFIDF_FREQUENCY = "tfidfFrequency";
	private static final String TFIDF_FREQUENCY_XML = "tfidfFrequency.xml";
	private static final String TFIDF_FREQUENCY_HTML = "tfidfFrequency.html";
	static final double MIN_FONT = 10;
	static final double MAX_FONT = 30;
	
	private List<String> chosenWordAggregationMethods = new ArrayList<String>();
	private IntRange wordLengthRange;
	protected List<String> words;
	private List<String> summaryMethods;
	WordResultsElementList frequenciesElementList;
	WordResultsElement aggregatedFrequenciesElement;
	private IntRange wordCountRange;
	private WordResultsElement booleanFrequencyElement;
	private Map<String, ResultsElement> resultsByDictionary;
	private AMIWordsTool wordsTool;
	
	public WordArgProcessor() {
		super();
	}

	public WordArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public WordArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public WordArgProcessor(AbstractTool abstractTool) {
		super(abstractTool);
	}

	/** make these an enum! */
	/** select methods to use
	 * 			FREQUENCIES,
				WORD_FREQUENCIES,
				WORD_LENGTHS,
				SEARCH,
				WORD_SEARCH

	 * @param option list of methods (none gives help)
	 * 
	 * @param argIterator
	 */
	public void parseWords(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpMethods();
		} else {
			chosenWordAggregationMethods = getChosenList(ANALYSIS_METHODS, tokens);
		}
	}

	public void parseWordLengths(ArgumentOption option, ArgIterator argIterator) {
		wordLengthRange =argIterator.getIntRange(option);
		setWordLength(wordLengthRange);
	}

	private void setWordLength(IntRange wordLengthRange) {
		if (wordLengthRange.getMin() < 1 || wordLengthRange.getMax() < 1) {
			throw new RuntimeException("bad word lengths: "+wordLengthRange);
		}
	}

	public void parseWordTypes(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpWordTypes();
		} else {
			chosenWordTypes = getChosenList(WORD_TYPES, tokens);
		}
	}
	
	public void parseMinCount(ArgumentOption option, ArgIterator argIterator) {
		wordCountRange = argIterator.getIntRange(option);
	}
	
	public IntRange getWordCountRange() {
		return wordCountRange;
	}

	public DefaultArgProcessor setWordCountRange(IntRange wordCountRange) {
		this.wordCountRange = wordCountRange;
		return this;
	}
	
	/** called by reflection */
	public void runExtractWords(ArgumentOption option) {
		extractWords(); // drops the mandatory ArgumentOption
	}

	/** extracts words - currently crudely splits the contentstream
	 * OBSOLETE - called from WordArgProcessor
	 * NEW called from AMIWordsTool via WordArgProcessor
	 */
	public void extractWords() {
 		AbstractTool tool = this.getAbstractTool();
		if (tool != null && tool.getVerbosityInt() > 0) {
			LOG.debug(tool + " OBSOLETE EXTRACT WORDS");
		}
		getOrCreateWordCollectionFactory();
		wordCollectionFactory.extractWords();
	}
	
	/** should this be here? */
	public void parseSearch(ArgumentOption option, ArgIterator argIterator) {
		LOG.warn("parseSearch in wrong place?");
		ensureSearcherList();
		List<String> dictionarySources = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createAndAddDictionaries(dictionarySources);
		for (DefaultStringDictionary dictionary : this.getDictionaryList()) {
			AMISearcher wordSearcher = new SearchSearcher(this, dictionary);
			searcherList.add(wordSearcher);
			String dictTitle = dictionary.getTitle();
			wordSearcher.setName(dictTitle);
		}
//		wordSearcher.setDictionaryList(this.getDictionaryList());
	}
	
	/** refactor output option.
	 * 
	 * @param option
	 */
	// called by reflection
	public void outputWords(ArgumentOption option) {
		LOG.trace("OUTPUT WORDS REFLECT");
		outputWords(option.getName());
	}

	public void outputWords(String optionName) {
//		AbstractTool.debug(abstractTool, 0, "outputWordsOld "+optionName, LOG);
		ContentProcessor currentContentProcessor = getOrCreateContentProcessor();
		ResultsElementList resultsElementList = currentContentProcessor.getOrCreateResultsElementList();
		if (resultsElementList.size() == 0) {
			System.out.print("!w");
//			LOG.warn("no words to output");
			return;
		}
		for (int i = 0; i < resultsElementList.size(); i++) {
			File outputDirectory = currentContentProcessor.createResultsDirectoryAndOutputResultsElement(
					optionName, resultsElementList.get(i)/*, CTree.RESULTS_XML*/);
			File htmlFile = new File(outputDirectory, CTree.RESULTS_HTML);
			((WordResultsElement) resultsElementList.get(i)).writeResultsElementAsHTML(htmlFile, this);
		}
	}

	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
//		AbstractTool.debug(abstractTool, 1, "parseSummary", LOG);
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			LOG.error("parseSummary needs a list of actions");
		} else {
			summaryMethods = tokens;
		}
	}
	
	public void finalSummary(ArgumentOption option) {
//		AbstractTool.debug(abstractTool, 1, "finalSummary", LOG);
		WordResultsElementList frequenciesElementList = this.aggregateOverCMDirList(getPlugin(), WordArgProcessor.FREQUENCIES);
		getOrCreateWordCollectionFactory();
		for (String method : summaryMethods) {
			runSummaryMethod(frequenciesElementList, wordCollectionFactory, method);
		}
	}

	private void runSummaryMethod(WordResultsElementList frequenciesElementList,
			WordCollectionFactory wordCollectionFactory, String method) {
//		AbstractTool.debug(abstractTool, 1, "runSummaryMethod", LOG);
		if (AGGREGATE_FREQUENCY.equals(method) && summaryFileName != null) {
			aggregatedFrequenciesElement = wordCollectionFactory.createAggregatedFrequenciesElement(frequenciesElementList);
			writeResultsElement(new File(summaryFileName, AGGREGATE_XML), aggregatedFrequenciesElement);
			aggregatedFrequenciesElement.writeResultsElementAsHTML(new File(summaryFileName, AGGREGATE_HTML), this);
		} else if (BOOLEAN_FREQUENCY.equals(method) && summaryFileName != null) {
			booleanFrequencyElement = wordCollectionFactory.createBooleanFrequencies(this, frequenciesElementList);
			writeResultsElement(new File(summaryFileName, BOOLEAN_FREQUENCY_XML), booleanFrequencyElement);
			booleanFrequencyElement.writeResultsElementAsHTML(new File(summaryFileName, BOOLEAN_FREQUENCY_HTML), this);
		} else if (TFIDF_FREQUENCY.equals(method) && summaryFileName != null) {
			WordResultsElement tfidfFrequencyElement = wordCollectionFactory.createTFIDFFrequencies(this, frequenciesElementList);
			writeResultsElement(new File(summaryFileName, TFIDF_XML), tfidfFrequencyElement);
			tfidfFrequencyElement.writeResultsElementAsHTML(new File(summaryFileName, TFIDF_HTML), this);
		}
	}

	public void outputSearch(ArgumentOption option) {
		outputResultsElements(option.getName());
//		LOG.debug("OUTPUT SEARCH");
	}

	public void outputResultsElements(String name) {
//		AbstractTool.debug(abstractTool, 1, "outputResultsElements "+name, LOG);
		LOG.debug("outputResultsElements "+name);
		ContentProcessor currentContentProcessor = currentCTree.getOrCreateContentProcessor();
		currentContentProcessor.clearResultsElementList();

		for (String title : resultsByDictionary.keySet()) {
			ResultsElement resultsElement = resultsByDictionary.get(title);
			resultsElement.setTitle(title);
			currentContentProcessor.addResultsElement(resultsElement);
		}
		currentContentProcessor.createResultsDirectoriesAndOutputResultsElement(name);
	}
	


	private static void writeResultsElement(File outputFile, ResultsElement resultsElement) {
		try {
			outputFile.getParentFile().mkdirs();
			XMLUtil.debug(resultsElement, new FileOutputStream(outputFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file "+outputFile, e);
		}
	}
	
	// =============================

	public WordResultsElementList aggregateOverCMDirList(String pluginName, String methodName) {
		WordResultsElementList resultsElementList = new WordResultsElementList();
		for (CTree cTree : cTreeList) {
			ResultsElement resultsElement = cTree.getResultsElement(pluginName, methodName);
			if (resultsElement == null) {
				LOG.error("Null results element, skipped "+cTree.getDirectory());
			} else {
				WordResultsElement wordResultsElement = new WordResultsElement(cTree.getResultsElement(pluginName, methodName));
				resultsElementList.add(wordResultsElement);
			}
		}
		return resultsElementList;
	}

	private void helpMethods() {
		System.err.println("ANALYSIS METHODS");
		for (String method : ANALYSIS_METHODS) {
			System.err.println("  "+method);
		}
	}
	
	private void helpWordTypes() {
		System.err.println("WORD TYPES");
		for (String type : WORD_TYPES) {
			System.err.println("  "+type);
		}
	}

	public IntRange getWordLengthRange() {
		return wordLengthRange;
	}

	public List<String> getChosenWordAggregationMethods() {
		return chosenWordAggregationMethods;
	}
	
	public void add(String method) { 
		getOrCreateWordAggregationMethods();
		if (!chosenWordAggregationMethods.contains(method)) {
			chosenWordAggregationMethods.add(method);
		}
	}

	private List<String> getOrCreateWordAggregationMethods() {
		if (chosenWordAggregationMethods == null) {
			chosenWordAggregationMethods = new ArrayList<String>();
		}
		return chosenWordAggregationMethods;
	}

	public void addChosenMethod(WordMethod wordMethod) {
		
	}

	public void setWordsTool(AMIWordsTool amiWordsTool) {
		this.wordsTool = amiWordsTool;
	}
	
	public AMIWordsTool getWordsTool() {
		return this.wordsTool;
	}

}
