package org.contentmine.ami.plugins.dummy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultsElement;

public class DummyResultsElement extends ResultsElement {

	private static final Logger LOG = Logger.getLogger(DummyResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String PHYLO = "phylo";
	private DummyResultsElement(String title) {
		super(title);
	}

	public DummyResultsElement() {
		this(PHYLO);
	}
	
}
