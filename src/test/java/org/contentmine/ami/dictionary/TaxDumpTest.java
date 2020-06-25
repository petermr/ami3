package org.contentmine.ami.dictionary;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.DictionaryTerm;
import org.contentmine.ami.dictionary.gene.JAXDictionary;
import org.contentmine.ami.dictionary.species.TaxDumpGenusDictionary;
import org.junit.Assert;
import org.junit.Test;

public class TaxDumpTest {

	private static final Logger LOG = LogManager.getLogger(TaxDumpTest.class);
@Test
	public void testTaxDumpDictionary() {
		DefaultAMIDictionary dictionary = new TaxDumpGenusDictionary();
		Assert.assertEquals(91556,  dictionary.size());
		Assert.assertTrue("Bacillus", dictionary.contains("Bacillus"));
	}

	@Test
	public void testCheckPattern() {
		DefaultAMIDictionary dictionary = new TaxDumpGenusDictionary();
		Assert.assertNotNull("missing regex", dictionary.getRegexString());
		List<DictionaryTerm> nonMatchingTerms = dictionary.checkNonMatchingTerms();
		Assert.assertEquals("non matching terms: ", 0, nonMatchingTerms.size());
	}


}
