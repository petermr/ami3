package org.contentmine.ami.plugins.dummy;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.ResultsElement;

public class DummyResultsElement extends ResultsElement {

	private static final Logger LOG = LogManager.getLogger(DummyResultsElement.class);
private static final String PHYLO = "phylo";
	private DummyResultsElement(String title) {
		super(title);
	}

	public DummyResultsElement() {
		this(PHYLO);
	}
	
}
