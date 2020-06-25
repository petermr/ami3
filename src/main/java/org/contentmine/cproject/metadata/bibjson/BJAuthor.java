package org.contentmine.cproject.metadata.bibjson;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BJAuthor {

	private static final Logger LOG = LogManager.getLogger(BJAuthor.class);
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
