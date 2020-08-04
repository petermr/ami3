package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.norma.sections.AbbreviationExtractor;
import org.contentmine.norma.sections.AcknowledgementExtractor;
import org.contentmine.norma.sections.AppendixExtractor;
import org.contentmine.norma.sections.ArticleAbstractExtractor;
import org.contentmine.norma.sections.ArticleMetaExtractor;
import org.contentmine.norma.sections.AuthorContribExtractor;
import org.contentmine.norma.sections.BackMatterExtractor;
import org.contentmine.norma.sections.CaseStudyExtractor;
import org.contentmine.norma.sections.ConclusionsExtractor;
import org.contentmine.norma.sections.ConflictsExtractor;
import org.contentmine.norma.sections.DiscussionExtractor;
import org.contentmine.norma.sections.FiguresExtractor;
import org.contentmine.norma.sections.FinancialSupportExtractor;
import org.contentmine.norma.sections.FrontMatterExtractor;
import org.contentmine.norma.sections.IntroductionExtractor;
import org.contentmine.norma.sections.JATSArticleElement;
import org.contentmine.norma.sections.JATSBackElement;
import org.contentmine.norma.sections.JATSBodyElement;
import org.contentmine.norma.sections.JATSFactory;
import org.contentmine.norma.sections.JATSFrontElement;
import org.contentmine.norma.sections.JATSSectionTagger;
import org.contentmine.norma.sections.JATSSectionTagger.SectionTag;
import org.contentmine.norma.sections.JournalMetaExtractor;
import org.contentmine.norma.sections.KeywordsExtractor;
import org.contentmine.norma.sections.MethodExtractor;
import org.contentmine.norma.sections.RefListExtractor;
import org.contentmine.norma.sections.ResultsExtractor;
import org.contentmine.norma.sections.SectionElement;
import org.contentmine.norma.sections.SectionTest;
import org.contentmine.norma.sections.SubtitlesExtractor;
import org.contentmine.norma.sections.SupplementalExtractor;
import org.contentmine.norma.sections.TablesExtractor;
import org.contentmine.norma.sections.TitlesExtractor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Element;

/**
 * 
 * @author pm286
 *
 */
public class AMISectionToolTest extends AbstractAMITest {
	

	private static final File TARGET_SECTION = new File("target/section/");
	private static final Logger LOG = LogManager.getLogger(AMISectionToolTest.class);
@Test
	public void testHelp() {
		String cmd = " section --help";
			;
		AMI.execute(AMISectionTool.class, cmd);
	}
	

@Test
public void testAllSections() {

	File targetDir = TARGET_SECTION;
	CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
	String cmd = "-vv -p "+targetDir+" clean **/sections/**";
	AMI.execute(cmd);
	
	cmd = ""
			+ "-vv -p " + targetDir
			+ " section"
			+ " --sections ALL "
		;
	AMI.execute(AMISectionTool.class, cmd);
}

	@Test
	public void testAbstractMethods() {

		CTree cTree = new CTree(new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC3289602"));
		File targetDir = new File(TARGET_SECTION, cTree.getName());
		CMineTestFixtures.cleanAndCopyDir(cTree.getDirectory(), targetDir);
		String cmd = "-vv clean **/PMC*/sections/**";
		AMI.execute(cmd);
		
		cmd = ""
				+ "-t " + targetDir
				+ " section"
				+ " --sections "
				+ " " + SectionTag.ABSTRACT
				+ " " + SectionTag.METHODS
			;
		AMI.execute(AMISectionTool.class, cmd);
	}
	
	@Test
	public void testALLSections() {
		String cmd = ""
//				+ "-t " + new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC3113902") + ""
				+ "-p " + AMIFixtures.TEST_ZIKA10_DIR
				+ " --forcemake"
				+ " section"
				+ " --sections ALL"
				+ " --sectiontype XML"
			;
		AMI.execute(AMISectionTool.class, cmd);
	}
	
	
	@Test
	/** selects some of the sections and then cut out the XML sections
	 * 
	 */
	public void testTransformToHtml() {
		String cmd = ""
				+ "-t " + new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC3289602") + ""
//				+ "-p " + AMIFixtures.TEST_ZIKA10_DIR
				+ " -v"
				+ " --forcemake"
				+ " section"
				+ " --sections "
				+ " " + SectionTag.ALL
				+ " --sectiontype XML"
				+ " --summary fig"
				+ " --html nlm2html"
//				+ " --write false"
			;
		AMI.execute(AMISectionTool.class, cmd);
//		AMI.execute(AMISectionTool.class, cmd);
	}


	@Test
	public void testALL() {
		File testZikaDir = AMIFixtures.TEST_ZIKA10_DIR;
		String cmd = ""
				+ " -p " + testZikaDir
				+ " --outputDir foo "
				+ " section"
				+ " --sections ALL"
			;
		AMI.execute(AMISectionTool.class, cmd);
	}
	
	@Test
	public void testCMIP_OILSummary() {
//		File dir = CMIP200;
		File dir = OIL186;
		String cmd = ""
				+ " -p " + dir 
				+ " section"
				+ " --summary table fig supplementary"
				+ " --forcemake"
			;
		AMI.execute(cmd);
	}
	
	@Test
	/** writes sections 
	 * 
	 */
	public void testReadFrontBodyBack() {
		File inputFile = new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC3289602/fulltext.xml");
		CTree cTree = new CTree(inputFile.getParentFile());
		Assert.assertTrue(inputFile + " should exist", inputFile.exists());
		JATSFactory factory = new JATSFactory();
		JATSArticleElement articleElement = factory.readArticle(inputFile);
		JATSFrontElement frontElement = articleElement.getFront();
//		System.out.println(frontElement.debugString(0));
		Assert.assertNotNull("front", frontElement);
		JATSBodyElement bodyElement = articleElement.getBody();
		System.out.println(bodyElement.debugString(0));
		bodyElement.writeSections(cTree);
		Assert.assertNotNull("body", bodyElement);
		JATSBackElement backElement = articleElement.getBack();
		System.out.println(backElement.debugString(0));
		Assert.assertNotNull("back", backElement);
	}

	@Test
	/** writes sections 
	 * 
	 */
	public void testArticle() {
		File inputFile = new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC320490/fulltext.xml");
		File parentFile = inputFile.getParentFile();
		CTree cTree = new CTree(parentFile);
		Assert.assertTrue(inputFile + " should exist", inputFile.exists());
		JATSFactory factory = new JATSFactory();
		JATSArticleElement articleElement = factory.readArticle(inputFile);
		articleElement.writeSections(cTree);
//		JATSFrontElement frontElement = articleElement.getFront();
////		System.out.println(frontElement.debugString(0));
//		Assert.assertNotNull("front", frontElement);
//		JATSBodyElement bodyElement = articleElement.getBody();
//		System.out.println(bodyElement.debugString(0));
//		bodyElement.writeSections(cTree);
//		Assert.assertNotNull("body", bodyElement);
//		JATSBackElement backElement = articleElement.getBack();
//		System.out.println(backElement.debugString(0));
//		Assert.assertNotNull("back", backElement);
	}

	@Test
	public void testFrontBodyBackProject() {
		String cmd = ""
				+ "-t " + new File(AMIFixtures.TEST_ZIKA10_DIR, "PMC320490") + ""
//				+ "-p " + AMIFixtures.TEST_ZIKA10_DIR
				+ " -v"
				+ " --forcemake"
				+ " --sections "
				+ " " + SectionTag.ARTICLE
//				+ " " + SectionTag.FRONT
//				+ " " + SectionTag.BODY
//				+ " " + SectionTag.BACK
				+ " --sectiontype XML"
				+ " --html nlm2html"
//				+ " --write false"
			;
		AMI.execute(AMISectionTool.class, cmd);
	}

	@Test
	public void testAUTO() {
		String cmd = ""
				+ "-p " + AMIFixtures.TEST_ZIKA10_DIR
				+ " -vv"
				+ " --forcemake"
				+ " section"
				+ " --extract tab fig"
			;
		AMI.execute(cmd);
	}

	@Test
	public void testAUTO1() {
		String cmd = ""
				+ "-p " + TEST_BATTERY10
				+ " -v"
				+ " --forcemake"
				+ " section"
				+ " --extract tab fig"
				+ " --summary fig tab"  
			;
		AMI.execute(cmd);
	}

	@Test
	public void testBoldParas() {
		String cmd = ""
//				+ "-t " + new File(AMIFixtures.TEST_OIL5_DIR, "PMC4391421")
				+ "-p " + AMIFixtures.TEST_OIL5_DIR
				+ " --forcemake"
				+ " --boldsections"

			;
		AMI.execute(AMISectionTool.class, cmd);
	}

	@Test
	public void testSummary() {
		String cmd = ""
				+ "-p " + "/Users/pm286/workspace/projects/CEV/searches/oil186"
				+ " --forcemake"
				+ " sections"
				+ " --extract table fig"
				+ " --summary fig table "
			;

		AMI.execute(AMISectionTool.class, cmd);
	}



	@Test
	public void testCEVBug() {
		File targetDir = new File("target/cevbug/");
		File testDir = new File("/Users/pm286/workspace/projects/CEV/oil186");
//		File testDir = new File("/Users/pm286/workspace/projects/quantumchem/qchem100");
		CMineTestFixtures.cleanAndCopyDir(testDir, targetDir);
		String cmd = ""
				+ "-p " + targetDir
				+ " --maxTrees 25"
				+" --sections ALL"
//				+ " -v"
			;
		AMI.execute(AMISectionTool.class, cmd);
	}
	

	/**
	 * name x 199, surname x 199, given-names x 197, label x 69, year x 60, element-citation x 57, ref x 57, source x 56, person-group x 56, 
	 * article-title x 56, fpage x 54, lpage x 54, volume x 51, pub-id x 45, italic x 25, title x 25, bold x 21, sec x 18, etal x 15, fn x 13, sup x 11,
	 *  contrib x 10, subject x 5, caption x 5, object-id x 5, graphic x 5, subj-group x 5, comment x 5, month x 4, aff x 4, publisher-loc x 4, 
	 *  publisher-name x 4, article-id x 3, table-wrap-foot x 3, day x 3, journal-id x 3, alternatives x 3, addr-line x 3, table-wrap x 3, date x 2, 
	 *  fig x 2, issue x 2, contrib-group x 2, issn x 2, pub-date x 2, abstract x 2, counts, title-group, body, suffix, permissions, alt-title, 
	 *  history, article, elocation-id, collab, size, corresp, publisher, page-count, article-meta, role, back, journal-title-group, article-categories, 
	 *  ref-list, copyright-statement, email, author-notes, journal-meta, fn-group, copyright-year, journal-title, front]
	 * @throws IOException
	 */
	@Test
	// Historical
	public void testArticleSections() throws IOException {
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.getOrCreateJATSHtml(SectionTest.PMC3289602XML);
		new File("target/jats/").mkdirs();
		XMLUtil.debug(tagger.getJATSHtmlElement(),new FileOutputStream("target/jats/PMC3289602a.html"), 1);
//
//		String xml = FileUtils.readFileToString(SectionTest.PMC3113902XML, CMineUtil.UTF8_CHARSET);
		Multiset<String> tagClassSet = tagger.getOrCreateTagClassMultiset();
		Iterable<Entry<String>> entriesSortedByCount = MultisetUtil.getEntriesSortedByCount(tagClassSet);
//		DebugPrint.debugPrint("tags "+entriesSortedByCount);
		Assert.assertEquals("["
				+ "name x 199, surname x 199, given-names x 197, label x 69, year x 60, element-citation x 57, ref x 57, source x 56, person-group x 56, "
				+ "article-title x 56, fpage x 54, lpage x 54, volume x 51, pub-id x 45, italic x 25, title x 25, bold x 21, sec x 18, etal x 15, fn x 13, "
				+ "sup x 11, contrib x 10, subject x 5, caption x 5, object-id x 5, graphic x 5, subj-group x 5, comment x 5, month x 4, aff x 4, "
				+ "publisher-loc x 4, publisher-name x 4, article-id x 3, table-wrap-foot x 3, day x 3, journal-id x 3, alternatives x 3, addr-line x 3, "
				+ "table-wrap x 3, date x 2, fig x 2, issue x 2, contrib-group x 2, issn x 2, pub-date x 2, abstract x 2, counts, title-group, body, "
				+ "suffix, permissions, alt-title, history, article, elocation-id, collab, size, corresp, publisher, page-count, article-meta, role, back, "
				+ "journal-title-group, article-categories, ref-list, copyright-statement, email, author-notes, journal-meta, fn-group, copyright-year, "
				+ "journal-title, front"
				+ "]", entriesSortedByCount.toString());
		Iterable<Entry<String>> entriesSortedByValue= MultisetUtil.getEntriesSortedByValue(tagClassSet);
		Assert.assertEquals("["
				+ "abstract x 2, addr-line x 3, aff x 4, alt-title, alternatives x 3, article, article-categories, article-id x 3, article-meta, "
				+ "article-title x 56, author-notes, back, body, bold x 21, caption x 5, collab, comment x 5, contrib x 10, contrib-group x 2, "
				+ "copyright-statement, copyright-year, corresp, counts, date x 2, day x 3, element-citation x 57, elocation-id, email, etal x 15, "
				+ "fig x 2, fn x 13, fn-group, fpage x 54, front, given-names x 197, graphic x 5, history, issn x 2, issue x 2, italic x 25, "
				+ "journal-id x 3, journal-meta, journal-title, journal-title-group, label x 69, lpage x 54, month x 4, name x 199, object-id x 5, "
				+ "page-count, permissions, person-group x 56, pub-date x 2, pub-id x 45, publisher, publisher-loc x 4, publisher-name x 4, ref x 57, "
				+ "ref-list, role, sec x 18, size, source x 56, subj-group x 5, subject x 5, suffix, sup x 11, surname x 199, table-wrap x 3, "
				+ "table-wrap-foot x 3, title x 25, title-group, volume x 51, year x 60"
				+ "]", entriesSortedByValue.toString());
		
		List<Element> sections;
		sections = tagger.getSections(SectionTag.ABSTRACT);
		LOG.debug(XMLUtil.toString(sections));
		LOG.debug("====");;
		sections = tagger.getSections(SectionTag.ARTICLE_TITLE);
		LOG.debug(XMLUtil.toString(sections));
//		sections = tagger.getSections(SectionTag.SECTIONED_ABSTRACT);
		LOG.debug(XMLUtil.toString(sections));
		sections = tagger.getAllSections();
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
	public void testCProjectSections() throws IOException {
		CProject cProject = new CProject(AMIFixtures.TEST_ZIKA10_DIR);
		JATSSectionTagger tagger = new JATSSectionTagger();
		tagger.readJATS(cProject);
		Multiset<String> tagClassSet = tagger.getOrCreateTagClassMultiset();
		
		Iterable<Entry<String>> entriesSortedByCount = MultisetUtil.getEntriesSortedByCount(tagClassSet);
		LOG.debug(DebugPrint.MARKER, "tags "+entriesSortedByCount);
		/**
		Assert.assertEquals("["
				+ "surname x 1863, given-names x 1857, name x 1688, italic x 952, named-content x 717, year x 485, ref x 464, source x 461,"
				+ " pub-id x 455, person-group x 425, article-title x 424, fpage x 414, lpage x 409, volume x 404, mixed-citation x 285,"
				+ " label x 232, title x 199, string-name x 175, bold x 168, sec x 164, ext-link x 109, element-citation x 108, etal x 71,"
				+ " citation x 71, sup x 69, caption x 68, contrib x 65, graphic x 47, fig x 41, subject x 40, subj-group x 38,"
				+ " kwd x 36," //
				+ " fn x 31,"
				+ " article-id x 30,"
				+ " aff x 27," //
				+ " list-item x 25, month x 23, journal-id x 22,"
				+ " publisher-name x 21,"
				+ " email x 20, day x 19,"
				+ " pub-date x 16," //
				+ " issn x 14, comment x 14, media x 13,"
				+ " supplementary-material x 13," //
				+ " role x 12, object-id x 12, publisher-loc x 12, date x 12,"
				+ " abstract x 11,"
				+ " contrib-group x 11, table-wrap x 10, issue x 9, ref-list x 9, journal-meta x 9, title-group x 9, body x 9,"
				+ " article x 9,"
				+ " publisher x 9, article-meta x 9, back x 9, article-categories x 9,"
				+ " journal-title x 9, front x 9, list x 8,"
				+ " issue-title x 8, table-wrap-foot x 7, journal-title-group x 7, ack x 7, corresp x 7, author-notes x 7, permissions x 6,"
				+ " alternatives x 6, addr-line x 6, history x 6, copyright-statement x 6, kwd-group x 5, fn-group x 5, copyright-year x 5,"
				+ " suffix x 5, alt-title x 5, bio x 4, counts x 3, elocation-id x 3,"
				+ " license x 3," //
				+ " page-count x 3," //
				+ " license-p x 2, app x 2,"
				+ " copyright-holder x 2, country x 2, institution x 2, edition x 2, app-group, ref-count, collab, size, fig-count, table-count,"
				+ " equation-count, word-count"
				+ "]", entriesSortedByCount.toString());
		*/
		List<List<HtmlDiv>> sections = tagger.getAbstracts(cProject);
		Assert.assertEquals(9, sections.size());
//		for (List<HtmlDiv> divList : sections) {
//			LOG.debug(divList);
//		}
	}
		
//	@Test
//	public void testExtractAbstract() {
//		CProject cProject = new CProject(AMIFixtures.TEST_ZIKA10_DIR);
//		CTree cTree = cProject.getCTreeByName("PMC3310660");
//		Assert.assertNotNull(cTree);
//		JATSSectionTagger tagger = new JATSSectionTagger();
//		tagger.readJATS(cTree);
//		Iterable<Entry<String>> tagSet = MultisetUtil.getEntriesSortedByCount(tagger.getOrCreateTagClassMultiset());
//		Assert.assertEquals("[given-names x 232, name x 232, surname x 232, label x 72, year x 53, article-title x 52, element-citation x 51,"
//				+ " source x 51, ref x 51, person-group x 51, volume x 50, fpage x 49, pub-id x 48, lpage x 46, etal x 36, subject x 27,"
//				+ " sup x 26, title x 25, subj-group x 25, italic x 24, caption x 17, sec x 17, contrib x 16, named-content x 12,"
//				+ " bold x 10, fn x 10, object-id x 7, graphic x 7, media x 5, supplementary-material x 5, fig x 4, aff x 4, article-id x 3,"
//				+ " day x 3, journal-id x 3, month x 3, alternatives x 3, addr-line x 3, table-wrap x 3, date x 2, table-wrap-foot x 2,"
//				+ " issue x 2, contrib-group x 2, pub-date x 2, comment x 2, counts, title-group, ack, body, suffix, permissions,"
//				+ " alt-title, history, article, elocation-id, issn, corresp, publisher, page-count, article-meta, role, back,"
//				+ " journal-title-group, article-categories, ref-list, copyright-statement, email, author-notes, journal-meta,"
//				+ " fn-group, abstract, publisher-loc, copyright-year, publisher-name, journal-title, front]",
//				tagSet.toString());
//		
//		//Using a concrete class 
//		class LocalAbstractExtractor implements DivListExtractor {
//			public List<HtmlDiv> getDivList(JATSSectionTagger sectionTagger) { 
//				List<HtmlDiv> divList = new ArrayList<HtmlDiv>();
//				divList = sectionTagger.getAbstracts();
//				return divList;				List<HtmlDiv> divList1 = tagger.getDivList(new LocalAbstractExtractor());
//		Assert.assertEquals(1,  divList1.size());
//		Assert.assertEquals("<div xmlns=\"http://www.w3.org/1999/xhtml\" class=\"abstract\"><p>Rabbits are widely used in biomedical research, yet techniques for their precise genetic modification are lacking. We demonstrate that zinc finger nucleases (ZFNs) introduced into fertilized oocytes can inactivate a chosen gene by mutagenesis and also mediate precise homologous recombination with a DNA gene-targeting vector to achieve the first gene knockout and targeted sequence replacement in rabbits. Two ZFN pairs were designed that target the rabbit immunoglobulin M (IgM) locus within exons 1 and 2. ZFN mRNAs were microinjected into pronuclear stage fertilized oocytes. Founder animals carrying distinct mutated IgM alleles were identified and bred to produce offspring. Functional knockout of the immunoglobulin heavy chain locus was confirmed by serum IgM and IgG deficiency and lack of IgM<span class=\"sup\">+</span> and IgG<span class=\"sup\">+</span> B lymphocytes. We then tested whether ZFN expression would enable efficient targeted sequence replacement in rabbit oocytes. ZFN mRNA was co-injected with a linear DNA vector designed to replace exon 1 of the IgM locus with ∼1.9 kb of novel sequence. Double strand break induced targeted replacement occurred in up to 17% of embryos and in 18% of fetuses analyzed. Two major goals have been achieved. First, inactivation of the endogenous IgM locus, which is an essential step for the production of therapeutic human polyclonal antibodies in the rabbit. Second, establishing efficient targeted gene manipulation and homologous recombination in a refractory animal species. ZFN mediated genetic engineering in the rabbit and other mammals opens new avenues of experimentation in immunology and many other research fields.</p></div>", divList1.get(0).toXML());
//
//		//Using an anonymous class
//		List<HtmlDiv> divList2 = tagger.getDivList(new DivListExtractor() {
//			public List<HtmlDiv> getDivList(JATSSectionTagger tagger) {
//				List<HtmlDiv> divList = new ArrayList<HtmlDiv>();
//				divList = tagger.getAbstracts();
//				return divList;
//					});
//		
//		//Using lambdas
//		//List<HtmlDiv> divList3 = tagger.getListOfDivs(cTree, (tagger,tree) -> tagger.getAbstracts(tree));
//	}
		
	
	/** a lot of these need mending...
	 * 
	 */
		
	@Test
	public void extractSeveralItems() {
		CProject cProject = new CProject(AMIFixtures.TEST_ZIKA10_DIR);
		CTree cTree = cProject.getCTreeByName("PMC3310660");
		Assert.assertNotNull(cTree);
		JATSSectionTagger tagger = JATSSectionTagger.createAndPopulateTagger(cTree);

		List<HtmlDiv> abstractList = tagger.getDivList(new ArticleAbstractExtractor());
		Assert.assertEquals(1,  abstractList.size());
        List<HtmlDiv> abbreviationList = tagger.getDivList(new AbbreviationExtractor());
		Assert.assertEquals(0,  abbreviationList.size());
        List<HtmlDiv> acknowledgementList = tagger.getDivList(new AcknowledgementExtractor());
		Assert.assertEquals(0,  acknowledgementList.size());
        List<HtmlDiv> appendixList = tagger.getDivList(new AppendixExtractor());
		Assert.assertEquals(0,  appendixList.size());
        List<HtmlDiv> articleMetaList = tagger.getDivList(new ArticleMetaExtractor());
		Assert.assertEquals(0,  articleMetaList.size());
        List<HtmlDiv> authorContribList = tagger.getDivList(new AuthorContribExtractor());
		Assert.assertEquals(0,  authorContribList.size());
//        List<HtmlDiv> authorMetaList = tagger.getDivList(new AuthorMetaExtractor());
//		Assert.assertEquals(1,  authorMetaList.size());
        List<HtmlDiv> backMatterList = tagger.getDivList(new BackMatterExtractor());
		Assert.assertEquals(0,  backMatterList.size());
        List<HtmlDiv> caseStudyList = tagger.getDivList(new CaseStudyExtractor());
		Assert.assertEquals(0,  caseStudyList.size());
        List<HtmlDiv> conclusionList = tagger.getDivList(new ConclusionsExtractor());
		Assert.assertEquals(0,  conclusionList.size());
        List<HtmlDiv> conflictList = tagger.getDivList(new ConflictsExtractor());
		Assert.assertEquals(0,  conflictList.size());
        List<HtmlDiv> discussionList = tagger.getDivList(new DiscussionExtractor());
		Assert.assertEquals(0,  discussionList.size());
        List<HtmlDiv> figureList = tagger.getDivList(new FiguresExtractor());
		Assert.assertEquals(4,  figureList.size());
        List<HtmlDiv> financialSupportList = tagger.getDivList(new FinancialSupportExtractor());
		Assert.assertEquals(0,  financialSupportList.size());
        HtmlElement frontMatterHtml = tagger.getHtmlElement(new FrontMatterExtractor());
		Assert.assertNull(frontMatterHtml);
        List<HtmlDiv> introductionList = tagger.getDivList(new IntroductionExtractor());
		Assert.assertEquals(0,  introductionList.size());
        List<HtmlDiv> journalMetaList = tagger.getDivList(new JournalMetaExtractor());
		Assert.assertEquals(0,  journalMetaList.size());
        List<HtmlDiv> keywordList = tagger.getDivList(new KeywordsExtractor());
		Assert.assertEquals(0,  keywordList.size());
        List<HtmlDiv> methodList = tagger.getDivList(new MethodExtractor());
		Assert.assertEquals(0,  methodList.size());
//        List<HtmlDiv> otherList = tagger.getDivList(new OtherExtractor());
//		Assert.assertEquals(0,  otherList.size());
        List<HtmlDiv> refList = tagger.getDivList(new RefListExtractor());
		Assert.assertEquals(0,  refList.size());
        List<HtmlDiv> resultList = tagger.getDivList(new ResultsExtractor());
		Assert.assertEquals(0,  resultList.size());
        List<HtmlDiv> subtitleList = tagger.getDivList(new SubtitlesExtractor());
		Assert.assertEquals(0,  subtitleList.size());
		List<HtmlDiv> supplementaList = tagger.getDivList(new SupplementalExtractor());
		Assert.assertEquals(0,  supplementaList.size());
        List<HtmlDiv> tableList = tagger.getDivList(new TablesExtractor());
		Assert.assertEquals(0,  tableList.size());
        List<HtmlDiv> titleList = tagger.getDivList(new TitlesExtractor());        
		Assert.assertEquals(25,  titleList.size());
	}
	
	@Test
	public void testAddAbstracts() {
		File targetDir = new File("target/sections/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		CProject cProject = new CProject(targetDir);
		CTree cTree = cProject.getCTreeByName("PMC3310660");
		Assert.assertNotNull(cTree);
		JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
		treeTagger.addAbstractAsFile();
		File abstractFile = cTree.getAbstractFile();
		Assert.assertTrue("abstract should exist", CTree.isExistingFile(abstractFile));
		JATSSectionTagger projectTagger = new JATSSectionTagger(cProject);
		projectTagger.addProjectAbstractsAsFiles();
		
	}
	
	@Test
	public void testAddFronts() {
		File targetDir = new File("target/sections/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		CProject cProject = new CProject(targetDir);
		CTree cTree = cProject.getCTreeByName("PMC3310660");
		Assert.assertNotNull(cTree);
		JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
		treeTagger.addFrontAsFile();
		File frontDir = cTree.getFrontDir();
		Assert.assertTrue("front should exist", CTree.isExistingDirectory(frontDir));
		JATSSectionTagger projectTagger = new JATSSectionTagger(cProject);
		projectTagger.addProjectFrontsAsFiles();
	}
	
	@Test
	public void testAddBacks() {
		File targetDir = new File("target/sections/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		CProject cProject = new CProject(targetDir);
		CTree cTree = cProject.getCTreeByName("PMC3310660");
		Assert.assertNotNull(cTree);
		JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
		treeTagger.addBackAsFile();
		File backDir = cTree.getBackDir();
		Assert.assertTrue("back should exist", CTree.isExistingDirectory(backDir));
		JATSSectionTagger projectTagger = new JATSSectionTagger(cProject);
		projectTagger.addProjectBacksAsFiles();
	}
	
	@Test
	public void testAddAll() {
		File targetDir = new File("target/sections/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		CProject cProject = new CProject(targetDir);
		CTree cTree = cProject.getCTreeByName("PMC3310660");
		JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
		treeTagger.addAbstractAsFile();
		treeTagger.addBackAsFile();
		treeTagger.addBodyAsFile();
		treeTagger.addFrontAsFile();
		File frontDir = cTree.getFrontDir();
		Assert.assertTrue("front should exist", CTree.isExistingDirectory(frontDir));
		JATSSectionTagger projectTagger = new JATSSectionTagger(cProject);
		projectTagger.addProjectAbstractsAsFiles();
		projectTagger.addProjectBacksAsFiles();
		projectTagger.addProjectBodysAsFiles();
		projectTagger.addProjectFrontsAsFiles();
	}

	@Test
	public void testOmar1() {
		File targetDir = new File("target/sections/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_OMAR_DIR, targetDir);
	}
	
	@Test
	/** overlap all sectionTrees.
	 * 
	 */
	public void testHyperTree() throws IOException {
		File targetDir = new File("target/sections/");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		CProject cProject = new CProject(targetDir);
		SectionElement hypertree = SectionElement.createAndPopulateHypertree(cProject);
		XMLUtil.debug(hypertree, new File("target/hypertree/zika.xml"), 1);
	}

	@Test
	/** overlap all sectionTrees.
	 * 
	 */
	public void testClimateHyperTreeIT() throws IOException {
		CProject cProject = new CProject(AbstractAMITest.CLIMATE200SECTIONS);
		File file = new File(AbstractAMITest.CLIMATE200SECTIONS, "climate200.xml");
		SectionElement hypertree = SectionElement.createAndPopulateHypertree(cProject);
		System.out.println(file);
		XMLUtil.debug(hypertree, file, 1);
	}

	@Test
	/** overlap all sectionTrees.
	 * 
	 */
	public void testOilHyperTreeIT() throws IOException {
		LOG.debug(AbstractAMITest.OIL186);
		CProject cProject = new CProject(AbstractAMITest.OIL186);
		SectionElement hypertree = SectionElement.createAndPopulateHypertree(cProject);
		XMLUtil.debug(hypertree, new File(AbstractAMITest.OIL186, "hypertree.xml"), 1);
	}
	
	@Test
	/** overlap all sectionTrees.
	 * 
	 */
	public void testViralEpidem950IT() throws IOException {
		
		LOG.info(AbstractAMITest.VIRAL950);
		CProject cProject = new CProject(AbstractAMITest.VIRAL950);
		String cmd;
		cmd = "-v -p " + cProject.getDirectory()
		        + " search"
		        + " --dictionary country funders drugs"
		        ;
		AMI.execute(cmd);
		cmd = "-v -p " + cProject.getDirectory()
				+ " section"
				+ " --summary all"
				+ " --hypertree mincount=2"
				;
//		AMI.execute(cmd);
		cmd = " -vvv -p " + cProject.getDirectory()
			+ " table"
			+ " --summarytable __table/summary.html"
			+ " --tabledir sections/tables"
			;
		AMI.execute(cmd);

	}

	private static final File PRIYA_1_PART = new File("/Users/pm286/projects/openVirus/miniproject/disease/1-part");
	@Test
	/** overlap all sectionTrees.
	 * 
	 */
	public void testViralEpidemPriyaIT() throws IOException {
		
		LOG.info(PRIYA_1_PART);
		CProject cProject = new CProject(PRIYA_1_PART);
		String cmd;
		cmd = "-v -p " + cProject.getDirectory()
		        + " search"
		        + " --dictionary country funders drugs"
		        ;
//		AMI.execute(cmd);
		cmd = "-v -p " + cProject.getDirectory()
				+ " section"
				+ " --summary all"
				+ " --hypertree mincount=2"
				;
		System.out.println(cmd);
		AMI.execute(cmd);
		cmd = " -vvv -p " + cProject.getDirectory()
			+ " table"
			+ " --summarytable __table/summary.html"
			+ " --tabledir sections/tables"
			;
		AMI.execute(cmd);

	}


	// ================= BUGS ===========
	
}
		
	
	