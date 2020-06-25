package org.contentmine.ami.wordutil;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.lucene.LuceneUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class StemmingAndHashingTest {
	private static final Logger LOG = LogManager.getLogger(StemmingAndHashingTest.class);
@Test
	public void testWhitespaceStemming() {
		List<String> stemmed = LuceneUtils.applyPorterStemming(LuceneUtils.whitespaceTokenize(LuceneTokenizationTest.goldilocks));
		Assert.assertEquals("stemmed", "["
				+ "Goldilock, "
				+ "and, "
				+ "the, "
				+ "three, "
				+ "bear"
				+ "]",
				stemmed.toString());
		
	}
	
	@Test
	@Ignore
	public void testSynbioDictionaryWords() {
		
	}
	
}
