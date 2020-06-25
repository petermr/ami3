package org.contentmine.ami.plugins.word;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.files.ResultsElementList;

public class WordResultsElementList extends ResultsElementList {

	
	private static final Logger LOG = LogManager.getLogger(WordResultsElementList.class);
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
