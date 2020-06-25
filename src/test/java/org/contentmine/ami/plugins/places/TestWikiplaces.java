package org.contentmine.ami.plugins.places;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.places.WikidataPlacesDictionary;
import org.junit.Ignore;
import org.junit.Test;

public class TestWikiplaces {

	private static final Logger LOG = LogManager.getLogger(TestWikiplaces.class);
@Test
	@Ignore("file too big")
	public void readWikiplacesRaw() {
		new WikidataPlacesDictionary();
	}
}
