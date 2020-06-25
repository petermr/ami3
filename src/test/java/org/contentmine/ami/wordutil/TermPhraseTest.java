package org.contentmine.ami.wordutil;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.TermPhrase;
import org.junit.Assert;
import org.junit.Test;

public class TermPhraseTest {

	private static final Logger LOG = LogManager.getLogger(TermPhraseTest.class);
@Test
	public void testPhrase() {
		TermPhrase phrase = TermPhrase.createTermPhrase("acid");
		Assert.assertEquals(1, phrase.getWords().size());
		Assert.assertEquals("acid", phrase.getWords().get(0));
	}

	@Test
	public void testPhrase1() {
		TermPhrase phrase = TermPhrase.createTermPhrase("amino acid");
		Assert.assertEquals(2, phrase.getWords().size());
		Assert.assertEquals("amino", phrase.getWords().get(0));
		Assert.assertEquals("acid", phrase.getWords().get(1));
	}
}
