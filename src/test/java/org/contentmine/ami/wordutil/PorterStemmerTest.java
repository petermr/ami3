package org.contentmine.ami.wordutil;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.wordutil.PorterStemmer;
import org.junit.Assert;
import org.junit.Test;


public class PorterStemmerTest {

	private static final Logger LOG = Logger.getLogger(PorterStemmerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testPorterStemmer1() {
		PorterStemmer porterStemmer = new PorterStemmer();
		String result = porterStemmer.stem("cats");
		Assert.assertEquals("single", "cat", result);
	}

	@Test
	public void testPorterStemmerMany() {
		String[] testWords = "This assesses the times we try and tried to annotate these studies".split("\\s+");
		List<String> stemmedList = new ArrayList<String>();
		PorterStemmer porterStemmer = new PorterStemmer();
		
		for (String word : testWords) {
			String stemmedWord = porterStemmer.stem(word);
			stemmedList.add(stemmedWord);
		}
		Assert.assertEquals("single", "["
				+ "Thi, "
				+ "assess, "
				+ "the, "
				+ "time, "
				+ "we, "
				+ "try, "
				+ "and, "
				+ "tri, "
				+ "to, "
				+ "annot, "
				+ "these, "
				+ "studi"
				+ "]", stemmedList.toString());
	}

}
