package org.contentmine.cproject.metadata.bibjson;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BJAuthor {

	private static final Logger LOG = Logger.getLogger(BJAuthor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String name;
	public BJAuthor() {
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
