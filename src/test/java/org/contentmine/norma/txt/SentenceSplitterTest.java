package org.contentmine.norma.txt;

import java.util.List;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.norma.txt.SentenceSplitter;
import org.junit.Assert;
import org.junit.Test;

public class SentenceSplitterTest {

	;
	private static final Logger LOG = Logger.getLogger(SentenceSplitterTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testSplit() {
		String test = ""
				+ "Hello World! And hello.\n"
				+ "This is a new\n"
				+ " sentence. And so is "
				+ "this one.";
		SentenceSplitter sentenceSplitter = new SentenceSplitter();
		sentenceSplitter.read(test);
		sentenceSplitter.split();
		List<String> sentences = sentenceSplitter.getSentenceList();
		LOG.debug(sentences);
		Assert.assertEquals(4, sentences.size());
		Assert.assertEquals("Hello World!", sentences.get(0));
		Assert.assertEquals(" And hello.", sentences.get(1));
		Assert.assertEquals("\nThis is a new\n sentence.", sentences.get(2));
		Assert.assertEquals(" And so is this one.", sentences.get(3));
	}
}
