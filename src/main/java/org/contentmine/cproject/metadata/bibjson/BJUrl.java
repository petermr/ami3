package org.contentmine.cproject.metadata.bibjson;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BJUrl extends AbstractBibJSON {
	private static final Logger LOG = LogManager.getLogger(BJUrl.class);
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
