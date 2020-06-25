package org.contentmine.ami.wordutil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.tools.lucene.LuceneUtils;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/** basic Lucene operations which may be used directly
 * or expanded into new tools
 * 
 * 
 * @author pm286
 *
 */
public class LuceneTokenizationTest {

	private static final Logger LOG = LogManager.getLogger(LuceneTokenizationTest.class);
File targetDir = new File("target/plos10/e0115544/");
	private String value;
	private Analyzer whitespaceAnalyzer;
	public static String goldilocks = "Goldilocks and the three bears";
	
	@Before
	public void setUp() throws IOException {
		CMineTestFixtures.cleanAndCopyDir(new File(AMIFixtures.TEST_TUTORIAL_DIR, "plos10/e0115544"), targetDir);
		File shtmlFile = new CTree(targetDir).getExistingScholarlyHTML();
		value = XMLUtil.parseQuietlyToDocument(shtmlFile).getValue();
		whitespaceAnalyzer = new WhitespaceAnalyzer();

	}

	/** WARNING Standard analyzer removes stopwords and flattens to lowercase!
	 * 
	 */
	@Test
	public void testStandardAnalyzer() {

		Analyzer analyzer = new StandardAnalyzer();
        List<String> result = LuceneUtils.tokenize(goldilocks, analyzer);
        Assert.assertEquals("["
        		+ "goldilocks, "
        		+ "three, "
        		+ "bears"
        		+ "]", result.toString());
	}
	
	@Test
	public void testWhitespaceTokenize() {
		
        goldilocks = "Goldilocks and the three bears";
        List<String> result = LuceneUtils.whitespaceTokenize(goldilocks);
        Assert.assertEquals("["
        		+ "Goldilocks, "
        		+ "and, "
        		+ "the, "
        		+ "three, "
        		+ "bears"
        		+ "]", result.toString());
	}

	@Test
	public void testWhitespaceTokenizeLarge() {
		List<String> result = LuceneUtils.whitespaceTokenize(value);
//		LOG.debug(result);
		Assert.assertTrue("e0115544", result.toString().startsWith(
				"[Better, Informing, Decision, Making, with, "));
		Assert.assertEquals(8946,  result.size());
		
	}
	
	@Test
	public void testShingles() throws IOException {
		List<String> shingles = LuceneUtils.createWhitespaceShingleStream(goldilocks, 2, 2);
		Assert.assertEquals("["
				+ "Goldilocks and, "
				+ "and the, "
				+ "the three, "
				+ "three bears]",
				shingles.toString());
		shingles = LuceneUtils.createWhitespaceShingleStream(goldilocks, 3, 3);
		Assert.assertEquals("["
				+ "Goldilocks and the, "
				+ "and the three, "
				+ "the three bears]",
				shingles.toString());
		shingles = LuceneUtils.createWhitespaceShingleStream(goldilocks, 2, 3);
		Assert.assertEquals("["
				+ "Goldilocks and, "
				+ "Goldilocks and the, "
				+ "and the, "
				+ "and the three, "
				+ "the three, "
				+ "the three bears, "
				+ "three bears]",
		shingles.toString());
	}
	
}
