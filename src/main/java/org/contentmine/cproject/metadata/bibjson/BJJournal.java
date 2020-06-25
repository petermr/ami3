package org.contentmine.cproject.metadata.bibjson;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BJJournal extends AbstractBibJSON {

	private static final Logger LOG = LogManager.getLogger(BJJournal.class);
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
