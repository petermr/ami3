package org.contentmine.norma.sections;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.contentmine.norma.sections.JATSSectionTagger.SectionTag;
import org.contentmine.norma.util.DottyPlotter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;

import nu.xom.Element;

public class SectionTest {

	private static final Logger LOG = LogManager.getLogger(SectionTest.class);
//	public final static String ZIKA10 = "zika10";
//
//	private static final File ZIKA10DIR = new File(NormaFixtures.TEST_SECTIONS_DIR, ZIKA10);
	
	public final static File PMC3113902 = new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC3113902");
	public final static File PMC3289602 = new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC3289602");
	public final static File PMC3310194 = new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC3310194");
	
	public final static File PMC3113902HTML = new File(PMC3113902, "scholarly.html");
	public final static File PMC3289602HTML = new File(PMC3289602, "scholarly.html");
	public final static File PMC3310194HTML = new File(PMC3310194, "scholarly.html");
	
	public final static File PMC3113902XML = new File(PMC3113902, "fulltext.xml");
	public final static File PMC3289602XML = new File(PMC3289602, "fulltext.xml");
	public final static File PMC3310194XML = new File(PMC3310194, "fulltext.xml");

	@Test
	public void testReadFile() {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(PMC3289602HTML);
	}

	/** tests the file parses and can generate the HTML
	 * 
	 */
	@Test
	public void testAnalyzeRead() {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(PMC3289602HTML);
		HtmlElement htmlElement = tagger.getHtmlElement();
		Assert.assertNotNull(htmlElement);
	}

	/** extract the (many) divs.
	 * They may be nested
	 * 
	 */
	@Test
	public void testAllDivs() {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(PMC3289602HTML);
		HtmlElement htmlElement = tagger.getHtmlElement();
		List<HtmlDiv> divs = HtmlDiv.extractSelfAndDescendantDivs(htmlElement);
		// this should be stable
		Assert.assertEquals("divs "+divs.size(), 121, divs.size());
//		Assert.assertTrue("divs "+divs.size(), divs.size() > 115);
		
	}
	
	/** extract front divs.
	 */
	@Test
	public void testFrontDivChildren() {
		assertDivChildren(
				"front",
				PMC3289602HTML,
				new String[] {"journal-meta", "article-meta"},
				new String[] {},
				0);
		
	}

	/** extract journal-meta divs.
	 */
	@Test
	public void testJournalMetaDivChildren() {
		assertDivChildren(
				"journal-meta",
				PMC3289602HTML,  
				new String[] {"journal-title-group", "publisher"}, 
				new String[] {"nlm-ta", "publisher-id", "pmc", "issn-ppub", "issn-epub"},
				0);
	}
	
	/** extract journal-meta divs.
	 */
	@Test
	public void testArticleMetaDivChildren() {
		assertDivChildren(
			"article-meta",
			PMC3289602HTML,  
			new String[] {"article-categories", "title-group", "contrib-group", "contrib-group",
					"author-notes", "permissions", "abstract", "abstract", "counts"}, 
			new String[] {
				"pmcid", "publisher-id", "doi", 
				"citation_author_institution", "citation_author_institution", "citation_author_institution",
				"citation_author_institution", 
				"pub-date-collection", "pub-date-epub", "volume", "issue", "elocation-id", "history"},
			0);
	}
	
	/** extract journal-meta divs.
	 */
	@Test
	public void testFrontNonAbstract() {
		HtmlElement htmlElement = readJATSHtmlElement(PMC3289602HTML);
	}
	
	
	/** extract body divs.
	 */
	@Test
	public void testBodyDivChildren() {
		assertDivChildren(
				"body",
				PMC3289602HTML,  
				new String[] {"introduction", "methods", "results", "discussion"},
				new String[] {},
				0);
		
	}
	
	/** extract top divs.
	 */
	@Test
	public void testBackDivChildren() {
		// FIXME
		/** ref-list may not be in correct div order - check XML and stylesheet
		 * 
		 */
		String[] divClass = {"fn-group", "references", "ref-list"};
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(PMC3289602HTML);
		HtmlElement htmlElement = tagger.getHtmlElement();
		
		// div children of back
		String xpath = ".//*[local-name()='div' and @class='back']/*[local-name()='div']";
		List<HtmlElement> divs = HtmlUtil.getQueryHtmlElements(htmlElement, xpath);
		Assert.assertEquals("divs "+divs.size(), divClass.length, divs.size());
		
		// check class attribute names of div children of body
		for (int i = 0; i < divs.size(); i++) {
			HtmlElement div = divs.get(i);
			String classAttValue = div.getClassAttribute();
			if (classAttValue != null) {
				Assert.assertEquals(divClass[i], classAttValue);
			} else {
				String tag = div.getAttributeValue("tag");
				Assert.assertEquals(divClass[i], tag);
			}
		}
		// there are no back children other than divs
		String nonDivXpath = ".//*[local-name()='div' and @class='back']/*[not(local-name()='div')]";
		List<HtmlElement> nonDivs = HtmlUtil.getQueryHtmlElements(htmlElement, nonDivXpath);
		Assert.assertEquals("nonDivs "+nonDivs.size(), 0, nonDivs.size());
		
	}
	
	/** extract body divs.
	 */
	@Test
	public void testFNChildren() {
		assertDivChildren(
				"fn-group",
				PMC3289602HTML,  
				new String[] {"fn-type-conflict", "fn-type-financial-disclosure"},
				new String[] {},
				0);
		
	}
	
	/** extract body divs.
	 */
	@Test
	public void testRefListChildren() {
		HtmlElement htmlElement = readJATSHtmlElement(PMC3289602HTML);
		String section = "ref-list";
		// div children of section
		String ulPath = ".//*[local-name()='div' and @tag='" + section + "']/*[local-name()='ul']";
		List<HtmlElement> ulElements = HtmlUtil.getQueryHtmlElements(htmlElement, ulPath);
		Assert.assertEquals("ul "+ulElements.size(), 1, ulElements.size());
		String liPath = ".//*[local-name()='li' and @tag='ref']";
		List<HtmlElement> liElements = HtmlUtil.getQueryHtmlElements(ulElements.get(0), liPath);
		Assert.assertEquals("li "+liElements.size(), 57, liElements.size());
	}
	
	
	/** extract the spans.
	 * They may be nested
	 * 
	 */
	@Test
	public void testAnalyzeSpans() {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(PMC3289602HTML);
		List<HtmlSpan> spans = tagger.getSpans();
		Assert.assertEquals("spans "+spans.size(), 1222, spans.size());
	}
	
	
	// ===================================

	/**
	ABBREVIATIONS,
	ABSTRACT,
	ACKNOWLEDGEMENT,
	APPENDIX,
	ARTICLE_META,
	AUTHOR_CONTRIB,
	AUTHOR_META,
	BACK,
	CASE_STUDY,
	CONCLUSION,
	CONFLICT,
	DISCUSSION,
	*/

//	@Test
//	@Ignore //NYI // difficult as no tag
//	public void testAbbreviations() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> abbreviations = tagger.getAbbreviations();
//		Assert.assertEquals("divs "+abbreviations.size(), 2, abbreviations.size());  // yes, there ARE 2!
//	}
//
//	@Test
//	public void testAbstracts() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> abstracts = tagger.getAbstracts();
//		// FIXME
////		Assert.assertEquals("divs "+abstracts.size(), 2, abstracts.size());  // yes, there ARE 2!
//	}
//
//	@Test
//	public void testAcknowledgements() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3113902HTML);
//		List<HtmlDiv> acks = tagger.getAcknowledgements();
//		// FIXME
////		Assert.assertEquals("divs "+acks.size(), 1, acks.size());  
//	}
//
//	@Test
//	@Ignore // need regex
//	public void testAppendix() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> appendixs = tagger.getAppendix();
//		Assert.assertEquals("divs "+appendixs.size(), 2, appendixs.size());  
//	}
//
//	@Test
//	public void testArticleMeta() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> articleMeta = tagger.getArticleMeta();
//		// FIXME
////		Assert.assertEquals("divs "+articleMeta.size(), 1, articleMeta.size());  
//	}
//
//	@Test
//	@Ignore // NYI
//	public void testBackMatter() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> abstracts = tagger.getBackMatter();
//		Assert.assertEquals("divs "+abstracts.size(), 2, abstracts.size());  // yes, there ARE 2!
//	}
//
//	@Test
//	@Ignore // NYI
//	public void testCaseStudy() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> abstracts = tagger.getCaseStudies();
//		Assert.assertEquals("divs "+abstracts.size(), 2, abstracts.size());  // yes, there ARE 2!
//	}
//
//	@Test
//	public void testConflicts() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> discussions = tagger.getConflicts();
//		// FIXME
////		Assert.assertEquals("divs "+discussions.size(), 1, discussions.size()); 
//	}
//
//	@Test
//	@Ignore // NYI
//	public void testConclusions() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> discussions = tagger.getConclusions();
//		Assert.assertEquals("divs "+discussions.size(), 1, discussions.size()); 
//	}
//
//	@Test
//	public void testDiscussion() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> discussions = tagger.getDiscussions();
//		// FIXME
////		Assert.assertEquals("divs "+discussions.size(), 1, discussions.size()); 
//	}
//
//	/**
//	FIG,
//	FINANCIAL,
//	FRONT, // frontMatter (not title, article, authors, journal)
//	INTRODUCTION,
//	JOURNAL_META,
//	KEYWORDS,
//	METHODS,
//	OTHER,
//	REF_LIST,
//	RESULTS,
//	SUPPLEMENTAL,
//	TITLE,
//*/
//
//	@Test
//	public void testFigs() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> divs = tagger.getDivsForCSSClass(SectionTagger.SectionTag.FIG);
//		// FIXME
////		Assert.assertEquals("divs "+divs.size(), 2, divs.size());
//	}
//
//	@Test
//	public void testFinancials() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> intros = tagger.getFinancialSupport();
//		// FIXME
////		Assert.assertEquals("divs "+intros.size(), 1, intros.size());  // yes, there ARE 2!
//	}
//
//	@Test
//	public void testFrontMatter() {
//		SectionTagger tagger = new SectionTagger();
////		tagger.readScholarlyHtml(PMC3113902HTML);
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		HtmlHead head = tagger.getFrontMatter();
//		Assert.assertNotNull("head ", head);  
//	}
//
//	@Test
//	public void testIntroductions() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> intros = tagger.getIntroductions();
//		// FIXME
////		Assert.assertEquals("divs "+intros.size(), 1, intros.size());  // yes, there ARE 2!
//	}
//
//	@Test
//	public void testJournalMeta() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> methods = tagger.getJournalMeta();
//		// FIXME
////		Assert.assertEquals("divs "+methods.size(), 1, methods.size()); 
//	}
//
//	@Test
//	@Ignore // NYI
//	public void testKeywords() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> methods = tagger.getKeywords();
//		Assert.assertEquals("divs "+methods.size(), 1, methods.size()); 
//	}

//	@Test
//	public void testOther() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> methods = tagger.getOther();
//		// FIXME
////		Assert.assertEquals("divs "+methods.size(), 1, methods.size()); 
//	}
//
//	public void testRefList() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> discussions = tagger.getRefLists();
//		Assert.assertEquals("divs "+discussions.size(), 1, discussions.size()); 
//	}
//
//	@Test
//	public void testResults() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> results = tagger.getResults();
//		// FIXME
////		Assert.assertEquals("divs "+results.size(), 1, results.size()); 
//	}
//
//	@Test
//	//@Ignore // NYI
//	public void testSupplemental() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3310194HTML);
//		List<HtmlDiv> results = tagger.getSupplemental();
//		// FIXME
////		Assert.assertEquals("divs "+results.size(), 1, results.size()); 
//	}
//
//	public void testSupport() {
//		SectionTagger tagger = new SectionTagger();
//		tagger.readScholarlyHtml(PMC3289602HTML);
//		List<HtmlDiv> discussions = tagger.getFinancialSupport();
//		Assert.assertEquals("divs "+discussions.size(), 1, discussions.size()); 
//	}

	
	@Test
	public void testCreateJATSElement() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.getOrCreateJATSHtml(PMC3289602XML);
		Element jatsElement = tagger.getJATSHtmlElement();
		File file = new File("target/jats/PMC3289602.html");
		XMLUtil.debug(jatsElement, file, 1);
		Assert.assertTrue("jats exists", file.exists());
	}

	@Test
	/** reads article and makes RefList.
	 * 
	 * @throws IOException
	 */
	public void testCreateArticleAndReflist() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.getOrCreateJATSHtml(PMC3289602XML);
		JATSArticleElement jatsArticleElement = tagger.getJATSArticleElement();
		JATSRefListElement reflist = jatsArticleElement.getReflistElement();
		Assert.assertNotNull("reflist not null", reflist);
		List<JATSRefElement> referenceList = reflist.getRefList();
		Assert.assertEquals(57,  referenceList.size());
		List<String> pmidList = reflist.getNonNullPMIDList();
		Assert.assertEquals(45,  pmidList.size());
		Assert.assertEquals("pmids", 
				"[12995440, 14230895, 13337908, 413216, 6304948, 19516034, 6314612, 14175744, 4799154, 6275577,"
				+ " 21529401, 17195954, 18680646, 4833603, 9420202, 19741066, 4395332, 9780042, 4976739, 13138582,"
				+ " 11463123, 18674965, 6274526, 11681215, 13114587, 743766, 6809352, 13163397, 6309104, 5313066,"
				+ " 5302299, 14062273, 6306872, 4538037, 4403105, 489960, 1124969, 13533740, 8099299, 1243735,"
				+ " 14946416, 12995441, 5311064, 2559514, 13556872]", pmidList.toString());
		LOG.trace(pmidList);
		List<String> pmcidList = reflist.getNonNullPMCIDList();
		Assert.assertEquals(0,  pmcidList.size());
		
	}

		
	
	
	@Test
	@Ignore // uses PMR files // fails on citations
	public void testCreateManyIDs() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		File root;
//		root = new File("/Users/pm286/workspace/projects/std");
//		root = new File("/Users/pm286/workspace/projects/trastuzumab");
		// has a broken file
//		root = new File("/Users/pm286/workspace/projects/trials/trialsjournal");
		root = new File("/Users/pm286/workspace/projects/zika");
//		root = new File(NormaFixtures.TEST_PUBSTYLE_DIR, "getpapers/anopheles");
//		root = new File(NormaFixtures.TEST_SECTIONS_DIR, "zika10");
		LOG.debug("root is: "+root+" and exists = "+root.exists());
		if (!root.exists()) return;
		PMCitations citations = new PMCitations();
		for (File file : root.listFiles()) {
			if (!file.isDirectory()) continue;
			for (File file1 : file.listFiles()) {
				if (file1.toString().endsWith("fulltext.xml")) {
					try {
						tagger.getOrCreateJATSHtml(file1);
					} catch (Exception e) {
						LOG.debug("skipped "+file1+"  ||  "+e);
						continue;
					}
					JATSArticleElement jatsArticleElement = tagger.getJATSArticleElement();
					citations.extractCitations(jatsArticleElement);
				}
			}
		}
		int minCitationCount = 4;
		List<PMCitation> citationList = citations.getCitations(minCitationCount);
		List<Multiset.Entry<String>> citationList1 = citations.listCitationEntries(3);
		/**
23563266 x 7
9420202 x 6
19516034 x 5

		 */
		Assert.assertEquals(33, citationList1.size());
		Assert.assertEquals("23563266", citationList1.get(0).getElement());
		Assert.assertEquals(7, citationList1.get(0).getCount());
		for (Multiset.Entry<String> entry : citationList1) {
//			System.out.println(entry);
		}
		DottyPlotter plotter = new DottyPlotter();
		plotter.setNodesep(0.4);
		plotter.setRanksep(3.7);
		plotter.setTitle("jatsref");
		plotter.setOutputFile(new File("target/jats/all1.dot"));
		plotter.setLinkList(citationList);
		plotter.createLinkGraph();
	}

	@Test
	@Ignore // uses PMR files
	public void testCreateManyArticles() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		File root = new File("/Users/pm286/workspace/projects/std");
//		File root = new File("/Users/pm286/workspace/projects/trastuzumab");
		// has a broken file
//		File root = new File("/Users/pm286/workspace/projects/trials/trialsjournal");
//		File root = new File("/Users/pm286/workspace/projects/zika");
//		File root = new File(NormaFixtures.TEST_PUBSTYLE_DIR, "getpapers/anopheles");
//		File root = new File(NormaFixtures.TEST_SECTIONS_DIR, "zika10");
		LOG.debug("root is: "+root+" and exists = "+root.exists());
		if (!root.exists()) return;
		for (File file : root.listFiles()) {
			if (!file.isDirectory()) continue;
			for (File file1 : file.listFiles()) {
				if (file1.toString().endsWith("fulltext.xml")) {
					try {
						tagger.getOrCreateJATSHtml(file1);
					} catch (Exception e) {
						LOG.debug("skipped "+file1+"  ||  "+e);
						continue;
//						throw new RuntimeException();
					}
					Element jatsElement = tagger.getJATSHtmlElement();
					File outfile = new File("target/jats/"+FilenameUtils.getPath(file1.toString())+"scholarly.html");
//					LOG.debug(outfile);
					XMLUtil.debug(jatsElement, outfile, 1);
				}
			}
		}
	}

	@Test
	public void testSectionJATS() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.getOrCreateJATSHtml(PMC3289602XML);
		LOG.trace(PMC3113902XML);
		TagElement tagElement = tagger.getTagElement(SectionTag.ABSTRACT);
		List<String> regexList = tagElement.getRegexList();
		String xpath = tagElement.getXpath();
		LOG.trace(xpath);
		Element jatsElement = tagger.getJATSHtmlElement();
		List<Element> sections = XMLUtil.getQueryElements(jatsElement, xpath);
		Assert.assertEquals("abstracts", 2, sections.size()); // yes there are 2
		LOG.debug(sections);

	}

	@Test
	public void testGetSections() throws IOException {
		// will read all the tags in NAConstants.NORMA_RESOURCE+"/pubstyle/sectionTagger.xml
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.getOrCreateJATSHtml(PMC3289602XML);
		new File("target/jats/").mkdirs();
		XMLUtil.debug(tagger.getJATSHtmlElement(),new FileOutputStream("target/jats/PMC3289602a.html"), 1);
		LOG.trace(PMC3113902XML);
		String xml = FileUtils.readFileToString(PMC3113902XML, CMineUtil.UTF8_CHARSET);
		List<Element> sections;
		sections = tagger.getSections(SectionTag.SUBTITLE);
		Assert.assertEquals("intro", 25, sections.size()); 
		sections = tagger.getSections(SectionTag.ARTICLE_TITLE);
		Assert.assertEquals("articleTitle", 1, sections.size());
		String title = sections.get(0).getValue().replaceAll("\\s+", " ");
		Assert.assertEquals("title", "Genetic Characterization of Zika Virus Strains: Geographic Expansion of the Asian Lineage", title);
		sections = tagger.getSections(SectionTag.ARTICLE_META);
		Assert.assertEquals("articleMeta", 1, sections.size()); 
		sections = tagger.getSections(SectionTag.JOURNAL_META);
		Assert.assertEquals("journalMeta", 1, sections.size()); 
		sections = tagger.getSections(SectionTag.JOURNAL_TITLE);
		Assert.assertEquals("journalTitle", 1, sections.size());
		title = sections.get(0).getValue().replaceAll("\\s+", " ");
		Assert.assertEquals("title", "PLoS Neglected Tropical Diseases", title);
		sections = tagger.getSections(SectionTag.PMCID);
		Assert.assertEquals("pmcid", 1, sections.size());
//		Assert.assertEquals("pmcid", "3289602", sections.get(0).getValue());
		sections = tagger.getSections(SectionTag.CONTRIB);
		// this includes an editor
		Assert.assertEquals("contrib", 10, sections.size());
		sections = tagger.getSections(SectionTag.TABLE);
		Assert.assertEquals("table", 3, sections.size());
		sections = tagger.getSections(SectionTag.FIG);
		Assert.assertEquals("fig", 2, sections.size());
		sections = tagger.getSections(SectionTag.METHODS);
		Assert.assertEquals("methods", 1, sections.size());
		sections = tagger.getSections(SectionTag.RESULTS);
		Assert.assertEquals("results", 1, sections.size());
		sections = tagger.getSections(SectionTag.ACK_FUND);
		Assert.assertEquals("fig", 1, sections.size());
		sections = tagger.getSections(SectionTag.REF);
		Assert.assertEquals("ref", 57, sections.size());
		
	}

	@Test
	public void testGetUnknownSections() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.getOrCreateJATSHtml(PMC3289602XML);
		new File("target/jats/").mkdirs();
		XMLUtil.debug(tagger.getJATSHtmlElement(),new FileOutputStream("target/jats/PMC3289602a.html"), 1);
		LOG.trace(PMC3113902XML);
		String xml = FileUtils.readFileToString(PMC3113902XML, CMineUtil.UTF8_CHARSET);
		List<Element> sections;
		sections = tagger.getSections(SectionTag.SUBTITLE);
		Assert.assertEquals("intro", 25, sections.size()); 
		sections = tagger.getSections(SectionTag.ARTICLE_TITLE);
		Assert.assertEquals("articleTitle", 1, sections.size());
		String title = sections.get(0).getValue().replaceAll("\\s+", " ");
		Assert.assertEquals("title", "Genetic Characterization of Zika Virus Strains: Geographic Expansion of the Asian Lineage", title);
		sections = tagger.getSections(SectionTag.ARTICLE_META);
		Assert.assertEquals("articleMeta", 1, sections.size()); 
		sections = tagger.getSections(SectionTag.JOURNAL_META);
		Assert.assertEquals("journalMeta", 1, sections.size()); 
		sections = tagger.getSections(SectionTag.JOURNAL_TITLE);
		Assert.assertEquals("journalTitle", 1, sections.size());
		title = sections.get(0).getValue().replaceAll("\\s+", " ");
		Assert.assertEquals("title", "PLoS Neglected Tropical Diseases", title);
		sections = tagger.getSections(SectionTag.PMCID);
		Assert.assertEquals("pmcid", 1, sections.size());
//		Assert.assertEquals("pmcid", "3289602", sections.get(0).getValue());
		sections = tagger.getSections(SectionTag.CONTRIB);
		// this includes an editor
		Assert.assertEquals("contrib", 10, sections.size());
		sections = tagger.getSections(SectionTag.TABLE);
		Assert.assertEquals("table", 3, sections.size());
		sections = tagger.getSections(SectionTag.FIG);
		Assert.assertEquals("fig", 2, sections.size());
		sections = tagger.getSections(SectionTag.METHODS);
		Assert.assertEquals("methods", 1, sections.size());
		sections = tagger.getSections(SectionTag.RESULTS);
		Assert.assertEquals("results", 1, sections.size());
		sections = tagger.getSections(SectionTag.ACK_FUND);
		Assert.assertEquals("fig", 1, sections.size());
		sections = tagger.getSections(SectionTag.REF);
		Assert.assertEquals("ref", 57, sections.size());
		
	}

	// ===========================================
	
	@Test
	public void testTables() {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(PMC3289602HTML);
		List<HtmlTable> tables = tagger.getHtmlTables();
		Assert.assertEquals("tables "+tables.size(), 3, tables.size());
	}

	@Test
	public void testAnalyzeSpanPubId() {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(PMC3289602HTML);
		List<HtmlSpan> spans = tagger.getSpansForCSSClass(JATSSectionTagger.PUB_ID);
		Assert.assertEquals("spans "+spans.size(), 45, spans.size());
	}
	
	// utils =================================
	
	private void assertDivChildren(
			String section,
			File inputFile,
			String[] divClassAttValues,
			String[] spanClassAttValues,
			int otherChildrenCount) {
		HtmlElement htmlElement = readJATSHtmlElement(inputFile);
		// div children of section
		String divXpath = ".//*[local-name()='div' and @class='" + section + "']/*[local-name()='div']";
		List<HtmlElement> divChildren = assertChildElements("divs", divClassAttValues, htmlElement, divXpath);
		// section span children
		String spanXpath = ".//*[local-name()='div' and @class='"+ section + "']/*[local-name()='span']";
		List<HtmlElement> spanChildren = assertChildElements("spans", spanClassAttValues, htmlElement, spanXpath);
		// other children
		String otherXPath = ".//*[local-name()='div' and @class='"+ section + "']/*[not(local-name()='span' or local-name()='div')]";
		List<HtmlElement> otherChildren = HtmlUtil.getQueryHtmlElements(htmlElement, otherXPath);
		Assert.assertEquals("others "+otherChildren.size(), otherChildrenCount, otherChildren.size());
		for (int i = 0; i < otherChildren.size(); i++) {
			HtmlElement other = otherChildren.get(i);
			Assert.assertEquals(spanClassAttValues[i], other.getClassAttribute());
		}
	}

	private HtmlElement readJATSHtmlElement(File inputFile) {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readScholarlyHtml(inputFile);
		HtmlElement htmlElement = tagger.getHtmlElement();
		return htmlElement;
	}

	private List<HtmlElement> assertChildElements(String title, String[] classAttValues, HtmlElement htmlElement, String xpath) {
		List<HtmlElement> childElements = HtmlUtil.getQueryHtmlElements(htmlElement, xpath);
		Assert.assertEquals(title + ": " + childElements.size(), classAttValues.length, childElements.size());
		// check classAtt of div children of section
		for (int i = 0; i < childElements.size(); i++) {
			HtmlElement div = childElements.get(i);
			Assert.assertEquals(classAttValues[i], div.getClassAttribute());
		}
		return childElements;
	}
	




}
