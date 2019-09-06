package org.contentmine.ami.plugins.search;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.ami.plugins.regex.CompoundRegex;
import org.contentmine.ami.plugins.regex.RegexComponent;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.ami.plugins.word.WordCollectionFactory;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.lookup.DefaultStringDictionary;

import nu.xom.Element;

public class SearchSearcher extends AMISearcher {

	
	public static final Logger LOG = Logger.getLogger(SearchSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	List<RegexComponent> componentList;
	private CompoundRegex compoundRegex;
	Element resultElement;
	private List<String> searchWords;
	private ResultsElement resultsElement;
	
	public SearchSearcher(AMIArgProcessor argProcessor, DefaultStringDictionary dictionary) {
		super(argProcessor, dictionary);
	}

	public static AMISearcher createSearcher(AMIArgProcessor argProcessor, DefaultStringDictionary dictionary) {
		return new SearchSearcher(argProcessor, dictionary);
	}

	void setSearchWords(List<String> searchWords) {
		this.searchWords = searchWords;
	}

	// ====== args ========

	/**
	 * 
	 * @return resultsElements 
	 */
	public ResultsElement searchWordList() {
		List<String> strings = new WordCollectionFactory((AbstractSearchArgProcessor)this.getArgProcessor()).createWordList();
		ResultsElement resultsElement = searchWithDictionary(strings);
		return resultsElement;
	}

	public ResultsElement getResultsElement() {
		return resultsElement;
	}

	

	// ===============
	
	public void debug() {
		LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
	}


}
