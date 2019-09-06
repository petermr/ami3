package org.contentmine.ami.plugins.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.contentmine.ami.wordutil.WordTree;
import org.contentmine.eucl.xml.XMLUtil;

public class WordTreeTest {
	
	private static final Logger LOG = Logger.getLogger(WordTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testWordTree() throws IOException {
		List<String> ss = Arrays.asList( new String[] {
				"a b c",
				"a c",
				"a b d",
				"b c x",
				"c x",
				"a b c d",
				"b c x",
		});
		WordTree wordTree = new WordTree("root");
		wordTree.addStrings(ss);
		wordTree.trimInsignificantSingletons();
		XMLUtil.debug(wordTree, new File("target/junk.xml"), 4);
	}

}
