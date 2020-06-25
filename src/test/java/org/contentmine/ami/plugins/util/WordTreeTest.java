package org.contentmine.ami.plugins.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.contentmine.ami.wordutil.WordTree;
import org.contentmine.eucl.xml.XMLUtil;

public class WordTreeTest {
	
	private static final Logger LOG = LogManager.getLogger(WordTreeTest.class);
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
