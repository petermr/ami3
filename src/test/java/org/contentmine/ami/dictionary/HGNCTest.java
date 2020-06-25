package org.contentmine.ami.dictionary;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.dictionary.gene.HGNCDictionary;
import org.junit.Assert;
import org.junit.Test;

public class HGNCTest {

	private static final Logger LOG = LogManager.getLogger(HGNCTest.class);
@Test
	public void testHGNCDictionary() {
		DefaultAMIDictionary dictionary = new HGNCDictionary();
		Assert.assertNotNull(dictionary);
		Assert.assertEquals("size",  40960, dictionary.size());
		Assert.assertTrue("A1BG-AS1", dictionary.contains("A1BG-AS1"));
		Assert.assertFalse("A1BG-AS1x", dictionary.contains("A1BG-AS1x"));
		Assert.assertTrue("BRCA2", dictionary.contains("BRCA2"));
	}
	
	@Test
	public void testCheckPattern() {
		DefaultAMIDictionary dictionary = new HGNCDictionary();
		Assert.assertNotNull(dictionary.getRegexString());
		List<DictionaryTerm> nonMatchingTerms = dictionary.checkNonMatchingTerms();
		Assert.assertEquals("non matching terms: ", 0, nonMatchingTerms.size());
	}



}
