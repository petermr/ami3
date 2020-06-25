package org.contentmine.cproject.metadata.bibjson;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BJLink extends AbstractBibJSON {

	private static final Logger LOG = LogManager.getLogger(BJLink.class);
private String url;
	
	public BJLink(String url) {
		this.url = url;
	}



}
