package org.contentmine.ami.wordutil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.wordutil.WordTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;
import nu.xom.Node;

public class PrePostTest {

	private static final String MATCH = "match";
	private static final Logger LOG = Logger.getLogger(PrePostTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testTree() {
		Element snippetsElement = createSnippetsElement();
		List<Element> resultElementList = XMLUtil.getQueryElements(snippetsElement, "//result");
		Assert.assertEquals(222,  resultElementList.size());
		
	}
	
	@Test
	public void testPostTree() throws IOException {
		Element snippetsElement = createSnippetsElement();
		List<Node> nodes = XMLUtil.getQueryNodes(snippetsElement, "//result/"+"@post");
		List<Node> postList = nodes;
		Assert.assertEquals(222,  postList.size());
		WordTree wordTree = new WordTree();
		for (Node post : postList) {
			wordTree.addPostString(post.getValue().trim());
		}
		wordTree.trimInsignificantSingletons();
		XMLUtil.debug(wordTree, new File("target/junkPost.xml"), 2);
	}

	@Test
	public void testPreTree() throws IOException {
		Element snippetsElement = createSnippetsElement();
		List<Node> preList = XMLUtil.getQueryNodes(snippetsElement, "//result/"+"@pre");
		Assert.assertEquals(222,  preList.size());
		WordTree wordTree = new WordTree();
		for (Node pre : preList) {
			wordTree.addPreString(pre.getValue().trim());
		}
		wordTree.trimInsignificantSingletons();
		XMLUtil.debug(wordTree, new File("target/junkPre.xml"), 2);
	}

	
	private Element createSnippetsElement() {
		File snippetsFile = new File(AMIFixtures.TEST_WORD_DIR, "genusSnippets.xml");
		Element snippetsElement = XMLUtil.parseQuietlyToDocument(snippetsFile).getRootElement();
		return snippetsElement;
	}
	
	
}
