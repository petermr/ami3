package org.contentmine.ami.lookups;

import java.io.IOException;
import java.net.URL;
import java.text.Normalizer.Form;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.xml.serialize.OutputFormat;
import org.contentmine.cproject.lookup.AbstractLookup;

public class RRIDLookup extends AbstractLookup {

	
	private static final String HTTPS_SCICRUNCH_ORG_RESOURCES_ANTIBODIES_SEARCH_Q = "https://scicrunch.org/resources/Antibodies/search?q=";
	private static final String HTTPS_SCICRUNCH_ORG_RESOLVER = "https://scicrunch.org/resolver/";
	private static final Logger LOG = LogManager.getLogger(RRIDLookup.class);
public RRIDLookup() {
	}

	/*
	https://scicrunch.org/resources/Antibodies/search?q=
	*/

	public String lookup(String rrid) throws IOException {
		urlString = getHtmlUrl(rrid);
		return getResponse();
	}

	public String getHtmlUrl(String rrid) {
		String urlS = HTTPS_SCICRUNCH_ORG_RESOLVER+rrid;
		return urlS;
	}
	
	/**
	 * e.g. AB_570435
	 * 
	 * @param rrid
	 * @return
	 * @throws IOException
	 */
		
	public String lookupAntibody(String rrid) throws IOException {
		urlString = getAntibodyUrl(rrid);
		return getResponse(url);
	}

	public String getAntibodyUrl(String rrid) {
		String urlS = HTTPS_SCICRUNCH_ORG_RESOURCES_ANTIBODIES_SEARCH_Q+rrid;
		return urlS;
	}
		
}
