package org.contentmine.ami.plugins.search;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultsElement;

public class SearchResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(SearchResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String GENE = "gene";
	private SearchResultsElement(String title) {
		super(title);
	}

	public SearchResultsElement() {
		this(GENE);
	}
	
}
