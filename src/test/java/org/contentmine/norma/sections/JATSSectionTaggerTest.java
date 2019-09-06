package org.contentmine.norma.sections;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.util.XMLUtils;
import org.contentmine.graphics.html.HtmlElement;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

/** runs JATSSectionTagger and extracts components of document
 * 
 * @author pm286
 *
 */
public class JATSSectionTaggerTest {
	private static final Logger LOG = Logger.getLogger(JATSSectionTaggerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void readHTML0() {
		Assert.assertTrue(AMIFixtures.TEST_SET_MARCHANTIA20_DIR + " should exist", AMIFixtures.TEST_SET_MARCHANTIA20_DIR.exists());
		
		String fileroot = "PMC2089061";
		File inputFile = new File(AMIFixtures.TEST_SET_MARCHANTIA20_DIR, fileroot);
		Assert.assertTrue("should exists", inputFile.exists());
	}
	@Test
	public void testReadHTML() {
		String fileroot = "PMC2089061";
		File inputFile = new File(new File(AMIFixtures.TEST_SET_MARCHANTIA20_DIR, fileroot), "scholarly.html");
		Assert.assertTrue(inputFile + " should exist", inputFile.exists());
		JATSSectionTagger tagger = new JATSSectionTagger();
		HtmlElement topHtml = tagger.readScholarlyHtml(inputFile);
		Assert.assertNotNull("html", topHtml);
		
	}

	@Test
	public void testReadJATS() {
		String fileroot = "PMC2089061";
		File inputFile = new File(new File(AMIFixtures.TEST_SET_MARCHANTIA20_DIR, fileroot), "fulltext.xml");
		Assert.assertTrue(inputFile + " should exist", inputFile.exists());
		JATSFactory factory = new JATSFactory();
		JATSArticleElement articleElement = factory.readArticle(inputFile);
		Assert.assertNotNull("article", articleElement);
		
	}

	@Test
	public void testReadFrontBodyBack() {
		String fileroot = "PMC2089061";
		File inputFile = new File(new File(AMIFixtures.TEST_SET_MARCHANTIA20_DIR, fileroot), "fulltext.xml");
		Assert.assertTrue(inputFile + " should exist", inputFile.exists());
		JATSFactory factory = new JATSFactory();
		JATSArticleElement articleElement = factory.readArticle(inputFile);
		JATSFrontElement frontElement = articleElement.getFront();
		Assert.assertNotNull("front", frontElement);
		JATSBodyElement bodyElement = articleElement.getBody();
		Assert.assertNotNull("body", bodyElement);
		JATSBackElement backElement = articleElement.getBack();
		Assert.assertNotNull("back", backElement);
	}

	@Test
	public void testReadJournalArticleMeta() {
		String fileroot = "PMC2089061";
		File inputFile = new File(new File(AMIFixtures.TEST_SET_MARCHANTIA20_DIR, fileroot), "fulltext.xml");
		Assert.assertTrue(inputFile + " should exist", inputFile.exists());
		JATSFactory factory = new JATSFactory();
		JATSArticleElement articleElement = factory.readArticle(inputFile);
		JATSFrontElement frontElement = articleElement.getFront();
		JATSJournalMetaElement journalMetaElement = frontElement.getJournalMeta();
		Assert.assertNotNull("journalMeta", journalMetaElement);
		JATSArticleMetaElement articleMetaElement = frontElement.getArticleMeta();
		Assert.assertNotNull("articleMeta", articleMetaElement);
	}

	@Test
	public void testReadAbstractAbstract() {
		String fileroot = "PMC2089061";
		File inputFile = new File(new File(AMIFixtures.TEST_SET_MARCHANTIA20_DIR, fileroot), "fulltext.xml");
		Assert.assertTrue(inputFile + " should exist", inputFile.exists());
		JATSFactory factory = new JATSFactory();
		JATSArticleElement articleElement = factory.readArticle(inputFile);
		JATSFrontElement frontElement = articleElement.getFront();
		JATSJournalMetaElement journalMetaElement = frontElement.getJournalMeta();
		Assert.assertNotNull("journalMeta", journalMetaElement);
		JATSArticleMetaElement articleMetaElement = frontElement.getArticleMeta();
		Assert.assertNotNull("articleMeta", articleMetaElement);
		JATSAbstractElement abstractElement = articleMetaElement.getAbstract();
		Assert.assertNotNull("abstract", articleMetaElement);
	}

}
