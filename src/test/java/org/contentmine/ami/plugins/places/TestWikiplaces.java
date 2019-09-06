package org.contentmine.ami.plugins.places;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.places.WikidataPlacesDictionary;
import org.junit.Ignore;
import org.junit.Test;

public class TestWikiplaces {

	private static final Logger LOG = Logger.getLogger(TestWikiplaces.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	@Ignore("file too big")
	public void readWikiplacesRaw() {
		new WikidataPlacesDictionary();
	}
}
