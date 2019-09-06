package org.contentmine.cproject.metadata.bibjson;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BJUrl extends AbstractBibJSON {
	private static final Logger LOG = Logger.getLogger(BJUrl.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String urlS;

	public BJUrl() {
		
	}

	public BJUrl(String urlS) {
		this.setUrl(urlS);
	}

	public void setUrl(String urlS) {
		this.urlS = urlS;
	}

	public String getUrl() {
		return urlS;
	}

}
