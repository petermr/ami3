package org.contentmine.ami.plugins.word;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.files.ResultsElementList;

public class WordResultsElementList extends ResultsElementList {

	
	private static final Logger LOG = Logger
			.getLogger(WordResultsElementList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public WordResultsElementList() {
		super();
	}

	public int getSingleCountsOfWord(String word) {
		int count = 0;
		for (ResultsElement resultsElement : resultsElementList) {
			WordResultsElement wordResultsElement = (WordResultsElement) resultsElement;
			if (wordResultsElement.contains(word)) count++;
		}
		return count;
	}
	

}
