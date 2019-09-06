package org.contentmine.cproject.metadata.bibjson;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BJJournal extends AbstractBibJSON {

	private static final Logger LOG = Logger.getLogger(BJJournal.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String name;

	public BJJournal() {
		
	}

	public BJJournal(String name) {
		this.setName(name);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
