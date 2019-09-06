package org.contentmine.ami.plugins.search;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultElement;

public class SearchResultElement extends ResultElement {

	private static final Logger LOG = Logger.getLogger(SearchResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SearchResultElement() {
		super();
	}
	
	public SearchResultElement(String title) {
		super(title);
	}
	
	
	
	
}
