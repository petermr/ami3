package org.contentmine.ami.wordutil;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.wordutil.LuceneUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class StemmingAndHashingTest {
	private static final Logger LOG = Logger.getLogger(StemmingAndHashingTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
