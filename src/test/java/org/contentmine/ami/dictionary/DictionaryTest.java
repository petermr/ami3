package org.contentmine.ami.dictionary;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.gene.HGNCDictionary;
import org.junit.Assert;
import org.junit.Test;

public class DictionaryTest {

	private static final Logger LOG = Logger.getLogger(DictionaryTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testCheckPattern() {
		DefaultAMIDictionary dictionary = new HGNCDictionary();
		Assert.assertNotNull(dictionary.getRegexString());
		dictionary.checkNonMatchingTerms();
	}
}
