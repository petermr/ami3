package org.contentmine.ami.dictionary;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.gene.HGNCDictionary;
import org.junit.Assert;
import org.junit.Test;

public class DictionaryTest {

	private static final Logger LOG = LogManager.getLogger(DictionaryTest.class);
@Test
	public void testCheckPattern() {
		DefaultAMIDictionary dictionary = new HGNCDictionary();
		Assert.assertNotNull(dictionary.getRegexString());
		dictionary.checkNonMatchingTerms();
	}
}
