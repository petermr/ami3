package org.contentmine.ami.plugins.search;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.ResultsElement;

public class SearchResultsElement extends ResultsElement {

	
	private static final Logger LOG = LogManager.getLogger(SearchResultsElement.class);
private static final String GENE = "gene";
	private SearchResultsElement(String title) {
		super(title);
	}

	public SearchResultsElement() {
		this(GENE);
	}
	
}
