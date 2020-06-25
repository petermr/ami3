package org.contentmine.ami.plugins;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.search.SearchSearcher;
import org.contentmine.ami.plugins.word.WordResultsElement;
import org.contentmine.ami.plugins.word.WordResultsElementList;
import org.contentmine.cproject.args.AbstractTool;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.ContentProcessor;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.files.ResultsElementList;
import org.contentmine.cproject.lookup.DefaultStringDictionary;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public abstract class AbstractSearchArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = LogManager.getLogger(AbstractSearchArgProcessor.class);
static final double MIN_FONT = 10;
	static final double MAX_FONT = 30;
	
	private Map<String, ResultsElement> resultsByDictionary;
	
	public AbstractSearchArgProcessor() {
		super();
	}

	public AbstractSearchArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public AbstractSearchArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public AbstractSearchArgProcessor(AbstractTool abstractTool) {
		super(abstractTool);
	}


	// =============== METHODS ==============
	
	public abstract void parseSearch(ArgumentOption option, ArgIterator argIterator);

	public List<AMISearcher> createSearcherList(List<String> dictionarySources) {
		ensureSearcherList();
		createAndAddDictionaries(dictionarySources);
		for (DefaultStringDictionary dictionary : this.getDictionaryList()) {
			AMISearcher wordSearcher = new SearchSearcher(this, dictionary);
			searcherList.add(wordSearcher);
			wordSearcher.setName(dictionary.getTitle());
		}
		return searcherList;
	}
	
	/** refactor output option.
	 * 
	 * @param option
	 */
//	@Deprecated 
	// this 
	public void outputWords(ArgumentOption option) {
		ContentProcessor currentContentProcessor = getOrCreateContentProcessor();
		ResultsElementList resultsElementList = currentContentProcessor.getOrCreateResultsElementList();
		for (int i = 0; i < resultsElementList.size(); i++) {
			String optionName = option.getName();
			File outputDirectory = currentContentProcessor.createResultsDirectoryAndOutputResultsElement(
					optionName, resultsElementList.get(i)/*, CTree.RESULTS_XML*/);
			File htmlFile = new File(outputDirectory, CTree.RESULTS_HTML);
			((WordResultsElement) resultsElementList.get(i)).writeResultsElementAsHTML(htmlFile, this);
		}
	}
	
	public void runSearch() {
		ensureResultsByDictionary();
		ensureSearcherList();
		for (AMISearcher searcher : searcherList) {
			SearchSearcher wordSearcher = (SearchSearcher)searcher;
			String title = wordSearcher.getTitle();
			/** this does the searching */
			ResultsElement resultsElement = wordSearcher.searchWordList();
			resultsElement.setTitle(title);
			resultsByDictionary.put(title, resultsElement);
		}
	}
	
	public void outputSearch(ArgumentOption option) {
		outputResultsElements(option.getName());
	}

	private void outputResultsElements(String name) {
		ContentProcessor currentContentProcessor = currentCTree.getOrCreateContentProcessor();
		currentContentProcessor.clearResultsElementList();

		LOG.trace("DBG outputResultsElements "+name);
		if (resultsByDictionary != null) {
			for (String title : resultsByDictionary.keySet()) {
				LOG.trace("DBG    title "+title);
				ResultsElement resultsElement = resultsByDictionary.get(title);
				resultsElement.setTitle(title);
				currentContentProcessor.addResultsElement(resultsElement);
			}
		}
		currentContentProcessor.createResultsDirectoriesAndOutputResultsElement(name);
	}
	

	// =============================

	private void ensureResultsByDictionary() {
		if (resultsByDictionary == null) {
			resultsByDictionary = new HashMap<String, ResultsElement>();
		}
	}


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

}
