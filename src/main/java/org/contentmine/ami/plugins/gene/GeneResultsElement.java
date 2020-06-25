package org.contentmine.ami.plugins.gene;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.ResultsElement;

public class GeneResultsElement extends ResultsElement {

	
	private static final Logger LOG = LogManager.getLogger(GeneResultsElement.class);
	private static final String GENE = "gene";
	private GeneResultsElement(String title) {
		super(title);
	}

	public GeneResultsElement() {
		this(GENE);
	}
	
}
