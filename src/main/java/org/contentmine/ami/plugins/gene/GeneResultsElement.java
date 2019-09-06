package org.contentmine.ami.plugins.gene;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultsElement;

public class GeneResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(GeneResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String GENE = "gene";
	private GeneResultsElement(String title) {
		super(title);
	}

	public GeneResultsElement() {
		this(GENE);
	}
	
}
