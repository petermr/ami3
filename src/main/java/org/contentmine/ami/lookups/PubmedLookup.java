package org.contentmine.ami.lookups;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.lookup.AbstractLookup;

public class PubmedLookup extends AbstractLookup {

	
	private static final Logger LOG = LogManager.getLogger(PubmedLookup.class);
public PubmedLookup() {
	}

	public String lookup(String genbankId) throws IOException {
//		LOG.error(" Pubmed lookup NYI");
		return null;
	}

		
}
