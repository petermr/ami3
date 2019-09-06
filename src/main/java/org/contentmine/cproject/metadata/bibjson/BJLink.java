package org.contentmine.cproject.metadata.bibjson;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BJLink extends AbstractBibJSON {

	private static final Logger LOG = Logger.getLogger(BJLink.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String url;
	
	public BJLink(String url) {
		this.url = url;
	}



}
