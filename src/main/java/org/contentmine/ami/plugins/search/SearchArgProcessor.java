package org.contentmine.ami.plugins.search;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.cproject.args.AbstractTool;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.lookup.DefaultStringDictionary;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SearchArgProcessor extends AbstractSearchArgProcessor {
	
	// Dummy at present
	
	public static final Logger LOG = LogManager.getLogger(SearchArgProcessor.class);
	public SearchArgProcessor() {
		super();
	}

	public SearchArgProcessor(String args) {
		this();
		parseArgs(args);
	}

	public SearchArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public SearchArgProcessor(AbstractTool abstractTool) {
		super(abstractTool);
	}

	public void parseSearch(ArgumentOption option, ArgIterator argIterator) {
		ensureSearcherList();
		List<String> dictionarySources = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createAndAddDictionaries(dictionarySources);
		for (DefaultStringDictionary dictionary : this.getDictionaryList()) {
			AMISearcher dictionarySearcher = new SearchSearcher(this, dictionary);
			searcherList.add(dictionarySearcher);
			String dictTitle = dictionary.getTitle();
			dictionarySearcher.setName(dictTitle);
		}
		LOG.trace("PARSE_SEARCH");
	}

	public void runSearch(ArgumentOption option, ArgIterator argIterator) {
		LOG.trace("RUN_SEARCH");
		super.runSearch();
	}

	public void runSearch(ArgumentOption option) {
		LOG.trace("RUN_SEARCH OPTION: "+option);
		super.runSearch();
	}

	public void outputSearch(ArgumentOption option) {
		super.outputSearch(option);
	}


	
}
