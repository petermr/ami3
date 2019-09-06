package org.contentmine.ami.lookups;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.lookup.AbstractLookup;

public class PubmedLookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(PubmedLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public PubmedLookup() {
	}

	public String lookup(String genbankId) throws IOException {
//		LOG.error(" Pubmed lookup NYI");
		return null;
	}

		
}
