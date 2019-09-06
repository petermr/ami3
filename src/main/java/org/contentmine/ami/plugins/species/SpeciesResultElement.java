package org.contentmine.ami.plugins.species;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultElement;

public class SpeciesResultElement extends ResultElement {

	private static final Logger LOG = Logger.getLogger(SpeciesResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SpeciesResultElement() {
		super();
	}
	
	public SpeciesResultElement(String title) {
		super(title);
	}
	
	
	
	
}
