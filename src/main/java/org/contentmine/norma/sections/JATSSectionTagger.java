package org.contentmine.norma.sections;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.files.HtmlTagger;
import org.contentmine.cproject.files.ResourceLocation;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.norma.NAConstants;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import nu.xom.Element;
import nu.xom.Elements;

/** sections in JATS and similar documents
 * 
 * note: some sections are hardcoded in JATS (have reserved vocabulary tags).
 * examples are abstract
 * Others such as "materials and methods" are free text which require community agreement
 * (is an introduction the same as background??)
 * 
 * Note that some apparently simple tags ("title) are deeply nested - hopefully consistently
 * @author pm286
 *
 */
/** sections can be in XML or HTML. Initially they were in HTML which confused things.
 * XML should be the basic type 
 * 
 * @author pm286
 *
 */
public class JATSSectionTagger implements HtmlTagger {

	public class createTagger extends JATSSectionTagger {

	}

	private static final String TAG = "tag";
	private static final Logger LOG = Logger.getLogger(JATSSectionTagger.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum SectionType {
		HTML,
		XML
	}
	
	
	/**
	 * 
	 * SENAY KAFKAS tags
	 *  @ClassList=(
"Introduction&Background",
"Materials&Methods",
"Results",
"Discussion",
"Conclusion&FutureWork",
"CaseStudyReport",
"Acknowledgement&Funding",
"AuthorContribution",
"CompetingInterest",
"SupplementaryData",
"Abbreviations",
"Keywords",
"References",
"Appendix",
"Figures",
"Tables",
"Other",
"Back_NoRef"
);
	 * 
	 */

	public final static List<String> REMOVE_TAGS = Arrays.asList(new String[] {
		"article-meta",
		"journal-meta",
		"front",
		
		// floats in body
		"fig",
		"table-wrap",
		"body",
		// back
		"ref-list",
		"back",
	});
	
	/** these are JATS reserved names
	 *   
	abstract x 2, addr-line x 3, aff x 4, alt-title, alternatives x 3, article, article-categories, article-id x 3, 
	    article-meta, article-title x 56, author-notes,
	back, body, bold x 21,
	caption x 5, collab, comment x 5, contrib x 10, contrib-group x 2, copyright-statement, copyright-year, corresp, counts, 
	date x 2, day x 3,
	element-citation x 57, elocation-id, email, etal x 15, "
	fig x 2, fn x 13, fn-group, fpage x 54, front,
	given-names x 197, graphic x 5,
	history,
	issn x 2, issue x 2, italic x 25, "
	journal-id x 3, journal-meta, journal-title, journal-title-group, 
	label x 69, lpage x 54, 
	month x 4, 
	name x 199, 
	object-id x 5, "
	page-count, permissions, person-group x 56, pub-date x 2, pub-id x 45, publisher, publisher-loc x 4, publisher-name x 4, 
	ref x 57, ref-list, role, 
	sec x 18, size, source x 56, subj-group x 5, subject x 5, suffix, sup x 11, surname x 199, 
	table-wrap x 3, table-wrap-foot x 3, title x 25, title-group, 
	volume x 51, 
	year x 60"

// the JATS taglist
<abbrev> <abbrev-journal-title> <abstract> <access-date> <ack> <addr-line> <address> <aff> <aff-alternatives> <alt-text> <alt-title> 
   <alternatives> <annotation> <anonymous> <app> <app-group> <array> <article> <article-categories> <article-id> <article-meta> 
   <article-title> <attrib> <author-comment> <author-notes> <award-group> <award-id> <back>
<bio> <body> <bold> <boxed-text> <break> <caption>
<chapter-title> <chem-struct> <chem-struct-wrap> <citation-alternatives> <code> <colgroup> <collab> <collab-alternatives> <comment> 
   <compound-kwd> <compound-kwd-part> <compound-subject> <compound-subject-part> <conf-acronym> <conf-date> <conf-loc> <conf-name> 
   <conf-num> <conf-sponsor> <conf-theme> <conference> <contrib> <contrib-group> <contrib-id> <copyright-holder> <copyright-statement> 
   <copyright-year> <corresp> <country> <count> <counts> <custom-meta> <custom-meta-group> <date>
<date-in-citation> <day> <def> <def-head> <def-item> <def-list> <degrees> <disp-formula> <disp-formula-group> <disp-quote> 
<edition> <element-citation> <elocation-id> <email> <equation-count> <era> <etal> <ext-link>
<fax> <fig> <fig-count> <fig-group> <fixed-case> <floats-group> <fn> <fn-group> <fpage> <front> <front-stub> <funding-group> 
  <funding-source> <funding-statement> <given-names>
<glossary> <glyph-data> <glyph-ref> <gov> <graphic>
<history> <hr>
<inline-formula> <inline-graphic> <inline-supplementary-material> <institution> <institution-id> <institution-wrap> <isbn> 
  <issn-l> <issn> <issue> <issue-id> <issue-part> <issue-sponsor> <issue-title> <italic> <journal-id>
<journal-meta> <journal-subtitle> <journal-title> <journal-title-group>
<kwd> <kwd-group>
<label> <license> <license-p> <list> <list-item> <long-desc> <lpage>
<media> <meta-name> <meta-value> <milestone-end> <milestone-start> <mixed-citation> <mml:math> <monospace> <month>
<name> <name-alternatives><named-content><nested-kwd> <nlm-citation> <note> <notes>
<object-id> <on-behalf-of> <open-access> <overline>
<p> <page-count> <page-range> <part-title> <patent> <permissions> <person-group> <phone> <prefix> <preformat> <price> 
  <principal-award-recipient> <principal-investigator> <private-char> <product> <pub-date> <pub-id> <publisher> <publisher-loc> 
  <publisher-name> <rb>
<ref> <ref-count> <ref-list> <related-article> <related-object> <response> <role> <roman> <rt> <ruby>
<sans-serif> <sc> <season> <sec> <sec-meta> <self-uri> <series> <series-text> <series-title> <sig> <sig-block> <size> <source> 
  <speaker> <speech> <statement> <std> <std-organization> <strike> <string-date> <string-name> <styled-content> <sub> <sub-article> 
  <subj-group> <subject> <subtitle> <suffix> <sup> <supplement> <supplementary-material> <surname> <table>
<table-count> <table-wrap> <table-wrap-foot> <table-wrap-group> <target> <tbody> <td> <term> <term-head> <tex-math> <textual-form> 
  <tfoot> <th> <thead> <time-stamp> <title> <title-group> <tr> <trans-abstract> <trans-source> <trans-subtitle> <trans-title> 
  <trans-title-group> <underline>
<uri>
<verse-group> <verse-line> <volume> <volume-id> <volume-series>
<word-count>
<xref>
<year>
	 * @author pm286
	 *
	 */
	// mainly hardcoded. May change
	public enum SectionTag {
		ABBREVIATION("abbrev", new String[]{"abbrev"}, "Authors abbreviations", "abbreviations?"),
		ABSTRACT("abstract", new String[]{"abstract"}, "Abstract or summary", "abstract"),
		ACK_FUND("acknowledge", new String[]{"ack"}, "Acknowledgements including funders", "(Acknowledge?ments?|Fund(ers)?|ing)"),
		/*
         <div class="app-group" title="app-group">
            <div class="app" title="app">
               <div class="title" tagx="title" title="title">Appendix A</div>
               <div class="Appendix A" title="Appendix A">
		 * 
		 */
		APPENDIX("appendix", new String[]{"app"}, "Appendix", "app-group"),
		
		ARTICLE("article", new String[]{"article"}, "Html meta", "article"),
		
		ARTICLE_META("articleMeta", new String[]{"article-meta"}, "Html meta", ""),
		ARTICLE_TITLE("articleTitle", new String[]{""}, "Article title", "title"),
		CONTRIB("contrib", new String[]{"contrib"}, "Contributors", "Contributors"),
		AUTH_CONT("authorContrib", new String[]{""}, "Author contributions", "Author contributions"),
		BACK("back", new String[]{"back"}, "Backmatter", "Back"),
		BODY("body", new String[]{"body"}, "Body", ""),
		CASE("caseStudy", new String[]{""}, "Case study", "Case stud(y|ies)"),
		CONCL("conclusion", new String[]{""}, "Conclusions", "Conclusions"),
		COMP_INT("conflict", new String[]{""}, "Conflict of interests", "(Conflicts of interest|Competing interests)"),
		DISCUSS("discussion", new String[]{"discussion"}, "Discussion", "Discussion"),
		FINANCIAL("financial", new String[]{""}, "Financial?", "Financial"),
		FIG("figure", new String[]{"figure"}, "Figure (often caption)", "Fig(ure)?"),
		FRONT("front", new String[]{"front"}, "Frontmatter", "front"),
		INTRO("introduction", new String[]{""}, "Introduction", "Introduction|Background"),
		JOURNAL_META("jrnlmeta", new String[]{""}, "", ""),
      		JOURNAL_TITLE("jrnltitle", new String[]{""}, "Journal title", "title"),
      		PUBLISHER_NAME("publtitle", new String[]{""}, "Publisher name", "publisher"),
		KEYWORD("keyword", new String[]{"kwd"}, "Author keywords", "keywords"),
		METHODS("methods", new String[]{""}, "Methods and materials", "methods|methods(and|&)materials|experimental"),
		OTHER("other", new String[]{""}, "Sections not in list", ""),
		PMCID("pmcid", new String[]{""}, "PMCID", "pmcid"),
		REF("references", new String[]{""}, "References/citations", "references|citations"),
		RESULTS("results", new String[]{""}, "Results", "results"),
		SUPPL("suppdata", new String[]{""}, "Supplementary material/supporting information", "(Supplementary|supporting)(material|information)"),
		TABLE("table", new String[]{""}, "Table", ""),
		SUBTITLE("subtitle", new String[]{""}, "Subtitle of article", "subtitle"),
		TITLE("title", new String[]{""}, "Title of article", "title"),
		/** a reserved word for all the sections */
		
		ALL("all", new String[]{""}, "all sections", "all"),
		AUTO("auto", new String[]{"auto"}, "Heuristic tree", "auto")
		;
		private String description;
		private Pattern pattern;
		private String name;
		private String[] tags;
		private String singleTag;
		
		private SectionTag(String name, String[] tags, String description, String regex) {
			this.name = name;
			this.tags = tags;
			this.singleTag = tags.length == 1 ?  tags[0] : null;
			this.description = description;
			this.pattern = Pattern.compile(regex);
		}
		
		private static Map<String, SectionTag> tagByTagName = new HashMap<String, SectionTag>();
		private static List<SectionTag> allTags = new ArrayList<>();
		static {
			for (SectionTag sectionTag : values()) {
				tagByTagName.put(sectionTag.toString(), sectionTag);
				if (!ALL.equals(sectionTag)) {
					allTags.add(sectionTag);
				}
			}
		}
		private SectionTag() {
		}

		public static SectionTag getSectionTag(String tagName) {
			return tagByTagName.get(tagName);
		}

		public static List<SectionTag> getAllTags() {
			return allTags;
		}
		
		public String getDescription() {
			return description;
		}

		public String[] getNames() {
			String[] names = new String[]{this.toString().toLowerCase()};
//			LOG.debug("N "+names[0]);
			return names;
		}

		public String[] getTags() {
			return tags;
		}

		public String getSingleTag() {
			return singleTag;
		}
		
		public String getName() {
			return name;
		}
		
	};
	
	public enum FloatSection {
		FIG("fig"),
		TABLEWRAP("table-wrap")
		;
		private String tag;

		private FloatSection(String tag) {
			this.tag = tag;
		}
		/** get FloatSection by tag 
		 * @return null if not found */
		public static FloatSection getFloatSection(String tag) {
			for (FloatSection floatSection : values()) {
				if (floatSection.tag.equals(tag)) return floatSection;
			}
			return null;
		}
	};

	public final static String[] CLASSTAGS = {
		"abstract",  // 11
		"ack",  // 7
		"addr-line",  // 6
		"aff",  // 27
		"alt-title",  // 5
		"alternatives",  // 6
		"app",  // 2
		"app-group",
		"article",  // 9
		"article-categories",  // 9
		"article-id",  // 30
		"article-meta",  // 9
		"article-title",  // 424
		"author-notes",  // 7
		"back",  // 9
		"bio",  // 4
		"body",  // 9
		"bold",  // 168
		"caption",  // 68
		"citation",  // 71
		"collab", 
		"comment",  // 14
		"contrib",  // 65
		"contrib-group",  // 11
		"copyright-holder",  // 2
		"copyright-statement",  // 6
		"copyright-year",  // 5
		"corresp",  // 7
		"country",  // 2
		"counts",  // 3
		"date",  // 12
		"day",  // 19
		"edition",  // 2
		"element-citation",  // 108
		"elocation-id",  // 3
		"email",  // 20
		"equation-count",
		"etal",  // 71
		"fig",  // 41
		"fig-count",
		"fn",  // 31
		"fn-group",  // 5
		"fpage",  // 414
		"front",  // 9
		"given-names",  // 1857
		"graphic",  // 47
		"history",  // 6
		"institution",  // 2
		"issn",  // 14
		"issue",  // 9
		"issue-title",  // 8
		"italic",  // 952
		"journal-id",  // 22
		"journal-meta",  // 9
		"journal-title",  // 9
		"journal-title-group",  // 7
		"kwd",  // 36
		"kwd-group",  // 5
		"label",  // 232
		"license",  // 3
		"license-p",  // 2
		"list",  // 8
		"list-item",  // 25
		"lpage",  // 409
		"media",  // 13
		"mixed-citation",  // 285
		"month",  // 23
		"name",  // 1688
		"named-content",  // 717
		"object-id",  // 12
		"page-count",  // 3
		"permissions",  // 6
		"person-group",  // 425
		"pub-date",  // 16
		"pub-id",  // 455
		"publisher",  // 9
		"publisher-loc",  // 12
		"publisher-name",  // 21
		"ref",  // 464
		"ref-count",
		"ref-list",  // 9
		"role",  // 12
		"sec",  // 164
		"size",
		"source",  // 461
		"string-name",  // 175
		"subj-group",  // 38
		"subject",  // 40
		"suffi //",  // 5
		"sup",  // 69
		"supplementary-material",  // 13
		"surname",  // 1863
		"table-count",
		"table-wrap",  // 10
		"table-wrap-foot",  // 7
		"title",  // 199
		"title-group",  // 9
		"volume",  // 404
		"word-count",
		"year",  // 485
	};
	
	private static final SectionTag[] MAJOR_SECTIONS_ARRAY =
		{
			SectionTag.ABBREVIATION,
			SectionTag.ABSTRACT,
			SectionTag.ACK_FUND,
			SectionTag.APPENDIX,
			SectionTag.ARTICLE_META,
				SectionTag.CONTRIB,
			SectionTag.AUTH_CONT,
			SectionTag.BACK,
			SectionTag.CASE,
			SectionTag.CONCL,
			SectionTag.COMP_INT,
			SectionTag.DISCUSS,
			SectionTag.FINANCIAL,
			SectionTag.FIG,
			SectionTag.FRONT, // frontMatter (not title, article, authors, journal)
			SectionTag.INTRO,
			SectionTag.JOURNAL_META,
			SectionTag.KEYWORD,
			SectionTag.METHODS,
			SectionTag.OTHER,
			SectionTag.PMCID,
			SectionTag.REF,
			SectionTag.RESULTS,
			SectionTag.SUPPL,
			SectionTag.TABLE,
			SectionTag.SUBTITLE,
		};
	
	
	public static Multimap<String, String> BODY_SECTIONS_MAP = ArrayListMultimap.create();
	public static  Map<String, String> SECTION_BY_SYNONYM = new HashMap<String, String>();
	private static MapEntry[] mapEntries = {
		    new MapEntry("ack", "Acknowledgements", "Authors' contributions"),
		    new MapEntry("contributions", "Contributions", "Authors' contributions"),
	    new MapEntry("introduction", "Background", "Introduction"),
	    new MapEntry("conflict",  "Conflict of interest", "Conflicts of interest", 
	    		                  "Conflict of Interest Statement", "Competing interests"),
	    new MapEntry("conclusion","Conclusion",	"Conclusions"),
	    new MapEntry("discussion", "Discussion"),
	    new MapEntry("methods", "Materials and Methods", "Methods and Materials", "Materials", "Methods"),
	    new MapEntry("results",	"Results",	"Results and Discussion"),
	    new MapEntry("supplementary", "Supplementary Material",	"Supporting Information"),
	};
	
	static {
		for (MapEntry entry : mapEntries) {
			BODY_SECTIONS_MAP.putAll(entry.key, entry.valueList);
		}
		for (String key : BODY_SECTIONS_MAP.keys()) {
			Collection<String> values = BODY_SECTIONS_MAP.get(key);
			for (String value : values) {
				SECTION_BY_SYNONYM.put(value.toLowerCase(),  key);
			}
		}
	};
/**
	CIITA Transactivation and Epigenetic Activities
	Clinical Manifestations
	Diagnosis
	Dynamics of Transmission
	Epigenetic Regulation of MHC2TA Transcription
	Extinction of MHC-II Expression in Cancer
	Long-Range Promoter Interactions
	Public Health Implications
	Taxonomy
	The Study
	Transcriptional Regulation of MHC Genes
	Virology and Pathogenesis
	*/
	
	
	public static final List<SectionTag> MAJOR_SECTIONS = Arrays.asList(MAJOR_SECTIONS_ARRAY);
	public static final String PUB_ID = "pub-id";
	public static final String HELP = "help";
	
	private HtmlElement htmlElement;
	private HtmlElement jatsHtmlElement;
	private Element scholarlyHtmlElement;
	public static final String DEFAULT_SECTION_TAGGER_RESOURCE = NAConstants.NORMA_RESOURCE+"/pubstyle/sectionTagger.xml";
	static final String SECTION = "section.";
	private Element tagsElement;
	private Map<SectionTag, TagElement> tagElementsByTag;
	private JATSArticleElement jatsArticleElement;
	private Multiset<String> tagClassMultiset;
	private List<List<HtmlDiv>> divListList;
	private CTree cTree;
	private CProject cProject;
	private Element rawXmlElement;
	private SectionType sectionType = SectionType.XML;
	


	
	public JATSSectionTagger() {
		
	}

	public JATSSectionTagger(CProject cProject) {
		this();
		this.cProject = cProject;
	}

	public HtmlElement readScholarlyHtml(File scholarlyHtmlFile) {
		testNotNullAndExists(scholarlyHtmlFile);
		HtmlFactory htmlFactory = new HtmlFactory();
		htmlElement = htmlFactory.parse(XMLUtil.parseQuietlyToDocument(scholarlyHtmlFile).getRootElement());
		return htmlElement;
	}

	public HtmlElement getHtmlElement() {
		LOG.debug("X "+htmlElement == null ? "null" : htmlElement.toXML());
		return htmlElement;
	}

	public List<HtmlTable> getHtmlTables() {
		HtmlElement htmlElement = getHtmlElement();
		return HtmlTable.extractSelfAndDescendantTables(htmlElement);
	}

	public List<HtmlDiv> getDivs() {
		HtmlElement htmlElement = getHtmlElement();
		return HtmlDiv.extractSelfAndDescendantDivs(htmlElement);
	}

	public List<HtmlSpan> getSpans() {
		HtmlElement htmlElement = getHtmlElement();
		return HtmlSpan.extractSelfAndDescendantSpans(htmlElement);
	}
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

	public List<HtmlDiv> getAbbreviations() {
		return getDivsForCSSClass(SectionTag.ABBREVIATION);
	}

	public List<HtmlDiv> getAbstracts() {
		return getDivsForCSSClass(SectionTag.ABSTRACT);
	}

	public List<HtmlDiv> getAcknowledgements() {
		return getDivsForCSSClass(SectionTag.ACK_FUND);
	}

	public List<HtmlDiv> getAppendix() {
		return getDivsForCSSClass(SectionTag.APPENDIX);
	}

	public List<HtmlDiv> getArticleMeta() {
		return getDivsForCSSClass(SectionTag.ARTICLE_META);
	}

	public List<HtmlDiv> getAuthorContrib() {
		return getDivsForCSSClass(SectionTag.AUTH_CONT);
	}

//	public List<HtmlDiv> getAuthorMeta() {
//		return getDivsForCSSClass(SectionTag.AUTHOR_META);
//	}

	public List<HtmlDiv> getBackMatter() {
		return getDivsForCSSClass(SectionTag.BACK);
	}

	public HtmlDiv getBack() {
		return getSingleDivForCSSClass(SectionTag.BACK.getSingleTag());
	}

	public HtmlDiv getBody() {
		return getSingleDivForCSSClass(SectionTag.BODY.getSingleTag());
	}

	public List<HtmlDiv> getCaseStudies() {
		return getDivsForCSSClass(SectionTag.CASE);
	}
	
	public List<HtmlDiv> getConclusions() {
		return getDivsForCSSClass(SectionTag.CONCL);
	}

	public List<HtmlDiv> getConflicts() {
		return getDivsForCSSClass(SectionTag.COMP_INT);
	}

	public List<HtmlDiv> getDiscussions() {
		return getDivsForCSSClass(SectionTag.DISCUSS);
	}
	
	/**
			FIG,
			FINANCIAL,
			FRONT, // frontMatter (not title, article, authors, journal)
			INTRODUCTION,
			JOURNAL_META,
			KEYWORDS,
			METHODS,
			OTHER,
			REF_LIST,
			RESULTS,
			SUPPLEMENTAL,
			TABLE,
			TITLE,
	 */

	public List<HtmlDiv> getFigures() {
		return getDivsForCSSClass(SectionTag.FIG);
	}

	public List<HtmlDiv> getFinancialSupport() {
		return getDivsForCSSClass(SectionTag.FINANCIAL);
	}

	@Deprecated
	public HtmlHead getFrontMatter() {
		HtmlHead head = (HtmlHead) HtmlElement.getSingleChildElement(htmlElement, HtmlHead.TAG);
		return head;
	}

	public HtmlDiv getFront() {
		return getSingleDivForCSSClass(SectionTag.FRONT.getSingleTag());
	}

	public List<HtmlDiv> getIntroductions() {
		return getDivsForCSSClass(SectionTag.INTRO);
	}

	public List<HtmlDiv> getJournalMeta() {
		return getDivsForCSSClass(SectionTag.JOURNAL_META);
	}

	public List<HtmlDiv> getKeywords() {
		return getDivsForCSSClass(SectionTag.KEYWORD);
	}

	public List<HtmlDiv> getMethods() {
		return getDivsForCSSClass(SectionTag.METHODS);
	}

	public List<HtmlDiv> getOther() {
		return getDivsForCSSClass(SectionTag.OTHER);
	}

	public List<HtmlDiv> getRefLists() {
		return getDivsForCSSClass(SectionTag.REF);
	}

	public List<HtmlDiv> getResults() {
		return getDivsForCSSClass(SectionTag.RESULTS);
	}

	public List<HtmlDiv> getSubtitles() {
		return getDivsForCSSClass(SectionTag.SUBTITLE);
	}

	public List<HtmlDiv> getSupplemental() {
		return getDivsForCSSClass(SectionTag.SUPPL);
	}

	public List<HtmlDiv> getTables() {
		return getDivsForCSSClass(SectionTag.TABLE);
	}

	public List<HtmlDiv> getTitles() {
		return getDivsForCSSClass(SectionTag.TITLE);
	}

	public List<HtmlDiv> getDivsForCSSClass(SectionTag sectionTag) {
		return getDivsForCSSClass(sectionTag.getNames());
	}
	
	public HtmlDiv getSingleDivForCSSClass(String name) {
		String xpath = createXPath("div", name);
		HtmlElement htmlElement = (HtmlElement) scholarlyHtmlElement;
		List<HtmlDiv> divs = HtmlDiv.extractDivs(htmlElement, xpath);
		if (divs.size() > 1) {
			throw new RuntimeException("more than one: name " + name + "; expected 0/1");
		}
		return (divs.size() == 0) ? null : divs.get(0);
	}
	
	public List<HtmlDiv> getDivsForCSSClass(String ... names) {
		String xpath = createXPath("div", names);
		HtmlElement htmlElement = (HtmlElement) jatsHtmlElement;
		List<HtmlDiv> divs = HtmlDiv.extractDivs(htmlElement, xpath);
		return divs;
	}

	public List<HtmlSpan> getSpansForCSSClass(String ... names) {
		String xpath = createXPath("span", names);
		List<HtmlSpan> spans = HtmlSpan.extractSpans(htmlElement, xpath);
		return spans;
	}
	
	// ==============================
	
	private String createXPath(String tag, String ...names) {
		if (names == null || names.length == 0) {
			throw new RuntimeException("get"+tag+" forCSSClass must have at least one arg");
		}
		String xpath = "//*[local-name()='"+tag+"' and (@class='"+names[0]+"'";

		for (int i = 1; i < names.length; i++) {
			xpath += " or @class='"+names[i]+"'";
		}
		xpath +=")]";
		LOG.trace("XPATH: "+xpath);
		return xpath;
	}

	/**
	 * 
	 * @param jatsXml
	 * @return 
	 * @throws RuntimeException // null JATS 
	 */
	public HtmlElement getOrCreateJATSHtml(File jatsXmlFile) throws RuntimeException {
		if (htmlElement == null) {
			if (jatsXmlFile == null) {
				throw new RuntimeException("Null JATS XML");
			}
			rawXmlElement = getOrCreateRawXMLElement(jatsXmlFile);
			htmlElement = getOrCreateJATSHtmlElement();
		}
		return htmlElement;
	}

	private Element getOrCreateRawXMLElement(File jatsXmlFile) {
		if (rawXmlElement == null) {
			if (jatsXmlFile != null) {
				rawXmlElement = XMLUtil.parseQuietlyToDocumentWithoutDTD(jatsXmlFile).getRootElement();
			}
		}
		return rawXmlElement;
	}

	public HtmlElement getOrCreateJATSHtmlElement() {
		if (jatsHtmlElement == null) {
			JATSFactory jatsFactory = new JATSFactory();
			jatsHtmlElement = jatsFactory.createHtml(rawXmlElement);
			scholarlyHtmlElement = jatsFactory.createScholarlyHtml(rawXmlElement);
			
			HtmlElement bodyHtmlElement = ((HtmlHtml)jatsHtmlElement).getBody();
			jatsArticleElement = (JATSArticleElement) bodyHtmlElement.getChild(0);
		}
		return jatsHtmlElement;
	}

	public Element getJATSHtmlElement() {
		return jatsHtmlElement;
	}

	public JATSArticleElement /*HtmlDiv*/ getJATSArticleElement() {
		return jatsArticleElement;
	}

	public Element readSectionTags(String resource) {
		ResourceLocation location = new ResourceLocation();
		InputStream is = location.getInputStreamHeuristically(resource);
		tagsElement = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		return tagsElement;
	}

	/** reads element with contains the tag definitions
	 * from DEFAULT_SECTION_TAGGER_RESOURCE
	 * NAConstants.NORMA_RESOURCE+"/pubstyle/sectionTagger.xml
	 * @return
	 */
	public Element readSectionTags() {
		return readSectionTags(DEFAULT_SECTION_TAGGER_RESOURCE);
	}

	/** reads section tags and creates tagElementsByTag
	 * 
	 * @return
	 */
	public Map<SectionTag, TagElement> getOrCreateMap() {
		Element root = readSectionTags();
		tagElementsByTag = new HashMap<SectionTag, TagElement>();
		for (int i = 0; i < root.getChildElements().size(); i++) {
			Element child = root.getChildElements().get(i);
			String localName = child.getLocalName();
			if (localName.equals(JATSSectionTagger.HELP)) {
				continue;
			} else if (!localName.equals(TAG)) {
				LOG.error("Bad tag: "+localName);
			}
			TagElement tagElement = new TagElement(root.getChildElements().get(i));
			SectionTag tag = tagElement.getTag();
			tagElementsByTag.put(tag, tagElement);
		}
		return tagElementsByTag;
	}

	public TagElement get(SectionTag tag) {
		return (tagElementsByTag == null || tag == null) ? null : tagElementsByTag.get(tag.toString());
	}

	/** looks up in getOrCreateMap
	 * 
	 * @param tag
	 * @return
	 */
	public TagElement getTagElement(SectionTag tag) {
		Map<SectionTag, TagElement> tagElementByTag = getOrCreateMap();
		LOG.trace(tagElementByTag);
		TagElement tagElement = tagElementByTag.get(tag);
		return tagElement;
	}

	public String getXPath(SectionTag tag) {
		if (tag == null) return null;
		TagElement tagElement = getTagElement(tag);
		String xpath = tagElement == null ? null : tagElement.getXpath();
		return xpath;
	}

	public List<Element> getSections(SectionTag sectionTag) {
		return getSections(sectionTag, sectionType);
	}

	public List<Element> getSections(SectionTag sectionTag, SectionType sectionType) {
		List<Element> sections = new ArrayList<>();
		Element element = getOrCreateMLFromCTree();
		if (element != null) {
			String xpath = getXPath(sectionTag);
			if (SectionType.HTML.equals(sectionType)) {
				sections = getHtmlSections(xpath);
			} else if (SectionTag.AUTO.equals(sectionTag)) {
				removeAndWriteSections(element);
				sections = recursiveDescent(element, 0);
			} else if (SectionTag.ARTICLE.equals(sectionTag)) {
				removeAndWriteSections(element);
				sections = Arrays.asList(new Element[]{element});
			} else {
				sections = getTitledXMLSections(xpath);
			}
		}
		return sections;
	}
	
	private void removeAndWriteSections(Element element) {
		List<String> removeTags = REMOVE_TAGS;
		for (String tag : removeTags) {
			removeAndWriteElement(element, tag);
		}
	}
	
	private void removeAndWriteElement(Element element, String sectionTag) {
		boolean deleteExisting = true;
		File sectionDir = cTree.makeSectionDir(sectionTag, deleteExisting);
		List<Element> removables = XMLUtil.getQueryElements(element, "//*[local-name()='"+sectionTag+"']");
		for (int i = 0; i < removables.size(); i++) {
			Element removable = removables.get(i);
			removable.detach();
			File file = new File(sectionDir, "elem_" + i + ".xml");
			try {
				List<Element> childElements = XMLUtil.getChildElements(removable);
				if (childElements.size() == 0) {
					LOG.debug("empty element "+sectionTag);
				} else {
					XMLUtil.debug(removable, file, 1);
				}
			} catch (IOException e) {
				System.err.println("Cannot write "+file+"; "+e);
			}
		}
	}

	private List<Element> recursiveDescent(Element element, int level) {
		String tag = element.getLocalName();
		System.out.print(Util.spaces(2*level)+"<"+tag+">");
		List<Element> childElements = XMLUtil.getChildElements(element);
		if (childElements.size() == 0) {
			System.out.println(""+Util.truncateAndAddEllipsis(element.getValue(), 20));
		} else {
			System.out.println();
			for (Element childElement : childElements) {
				recursiveDescent(childElement, level+1);
			}
		}
		return null;
	}
	
	/** sec-type
	Suggested usage
	Recommended section type values are:
	cases
		
	Cases/Case Reports
	conclusions
		
	Conclusions/Comment
	discussion
		
	Discussion/Interpretation
	intro
		
	Introduction/Synopsis/Overview
	materials
		
	Materials
	methods
		
	Methods/Methodology/Procedures
	results
		
	Results/Statement of Findings
	subjects
		
	Subjects/Participants/Patients
	supplementary-material
		
	Supplementary materials

	/** get or create XML or HTML from CTree file 
	 * @return HTMLElement or Element 
	 * */
	private Element getOrCreateMLFromCTree() {
		File existingFulltextXML = cTree.getExistingFulltextXML();
		Element element = null;
		if (existingFulltextXML != null && sectionType != null) {
			if (SectionType.HTML.equals(sectionType)) {
				element = this.getOrCreateJATSHtml(existingFulltextXML);
			} else if (SectionType.XML.equals(sectionType)) {
				element = this.getOrCreateRawXMLElement(existingFulltextXML);
			} else {
				throw new RuntimeException("Bad sectionType: "+sectionType);
			}
		}
		return element;
	}

	/** Pull method for getting sections.
	 * 
	 * @param xpath
	 * @return list of sections conforming to XPath
	 */
	private List<Element> getTitledXMLSections(String xpath) {
		List<Element> titledSections = new ArrayList<Element>();
		if (xpath != null && rawXmlElement != null) {
			xpath = createXMLTitleXPathFromHtmlTitleXPath(xpath);
			titledSections = XMLUtil.getQueryElements(rawXmlElement, xpath);
			for (Element section : titledSections) {
				String sectionS = section.toXML();
					System.out.println("XML "+" || "+sectionS.substring(0, Math.min(100, sectionS.length())));
			}
		}
		return titledSections;
	}

	/** terrible kludge. The xpath in read in from file and applies 
	 * to HTML. This converts it to XML-aware xpath.
	 * Basically [@class='title' => [local-name='title'
	 * @param xpath
	 * @return
	 */
	private String createXMLTitleXPathFromHtmlTitleXPath(String xpath) {
		return xpath.replace("@class", "local-name()");
	}
	
	private List<Element> getHtmlSections(String xpath) {
		List<Element> sections = new ArrayList<Element>();
		if (xpath != null) {
			Element jatsElement = getJATSHtmlElement();
			sections = XMLUtil.getQueryElements(jatsElement, xpath);
				for (Element section : sections) {
					String sectionS = section.toXML();
//					System.out.println("CLASS "+section.getAttributeValue("class")+" || "+sectionS.substring(0, Math.min(100, sectionS.length())));
				}
		}
		return sections;
	}
	
	public List<SectionTag> getSortedTags() {
		List<JATSSectionTagger.SectionTag> keys = new ArrayList<JATSSectionTagger.SectionTag> (tagElementsByTag.keySet());
		removeNulls(keys);
		Collections.sort(keys);
		return keys;
	}
	
	private void removeNulls(List<SectionTag> keys) {
		for (int i = keys.size() - 1; i >= 0; i--) {
			if (keys.get(i) == null) {
				keys.remove(i);
			}
		}
	}

	// ================================
	
	private void testNotNullAndExists(File scholarlyHtmlFile) {
		if (scholarlyHtmlFile == null) {
			throw new RuntimeException("null scholarlyHtml");
		} else if (!scholarlyHtmlFile.exists()) {
			throw new RuntimeException(scholarlyHtmlFile+" is not an existing file");
		} else if (scholarlyHtmlFile.isDirectory()) {
			throw new RuntimeException(scholarlyHtmlFile+" is a directory");
		}
	}

	public List<Element> getAllSections() {
		return getHtmlSections(".//*[@class]");
	}

	public Multiset<String> getOrCreateTagClassMultiset() {
		if (tagClassMultiset == null) {
			tagClassMultiset = HashMultiset.create();
			List<Element> tagElements = getAllSections();
			for (Element tagElement : tagElements) {
				String tagClass= tagElement.getAttributeValue(HtmlElement.CLASS);
				tagClassMultiset.add(tagClass);
			}
		}
		return tagClassMultiset;
	}

	/** at present this is just to create a Multiset from the project.
	 * each tree is read, tag multiset created and added to the tagMultiset in 'this'
	 * @param cProject
	 */
	public void readJATS(CProject cProject) {
		tagClassMultiset = HashMultiset.create();
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			JATSSectionTagger treeSectionTagger = new JATSSectionTagger();
			treeSectionTagger.readJATS(cTree);
			tagClassMultiset.addAll(getOrCreateTagClassMultiset());
		}
	}

	public void readJATS(CTree cTree) {
		this.setCTree(cTree);
		File existingFulltextXML = cTree.getExistingFulltextXML();
		if (existingFulltextXML != null) {
			this.getOrCreateJATSHtml(existingFulltextXML);
			this.getOrCreateTagClassMultiset();
		}
	}

	/** at present this is just to create a Multiset from the project.
	 * each tree is read, tag multiset created and added to the tagMultiset in 'this'
	 * @param cProject
	 */
	public List<List<HtmlDiv>> getAbbreviations(CProject cProject) {
		List<List<HtmlDiv>> divListList = new ArrayList<List<HtmlDiv>>();
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			JATSSectionTagger treeSectionTagger = new JATSSectionTagger();
			treeSectionTagger.getOrCreateJATSHtml(cTree.getExistingFulltextXML());
			List<HtmlDiv> divList = getAbbreviations();
			divListList.add(divList);
		}
		return divListList;
	}

	/** at present this is just to create a Multiset from the project.
	 * each tree is read, tag multiset created and added to the tagMultiset in 'this'
	 * @param cProject
	 */
	public List<List<HtmlDiv>> getAbstracts(CProject cProject) {
		divListList = new ArrayList<List<HtmlDiv>>();
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			JATSSectionTagger treeSectionTagger = new JATSSectionTagger();
			treeSectionTagger.getOrCreateJATSHtml(cTree.getExistingFulltextXML());
			List<HtmlDiv> divList1 = new ArrayList<HtmlDiv>();
			divList1 = treeSectionTagger.getAbstracts();
			List<HtmlDiv> divList = divList1;
			divListList.add(divList);
		}
		return divListList;
	}

	/** creates a list of list of divs using the particular extractor on a CProject
	 * @param cProject
	 */
	public static List<List<HtmlDiv>> getDivList(CProject cProject, DivListExtractor extractor) {
		List<List<HtmlDiv>> divListList = new ArrayList<List<HtmlDiv>>();
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			JATSSectionTagger tagger = JATSSectionTagger.createAndPopulateTagger(cTree);
			List<HtmlDiv> divList = tagger.getDivList(extractor);
			divListList.add(divList);
		}
		return divListList;
	}

	/** get a list of Divs of given type.
	 * assume we have read a cTree
	 * @param extractor
	 * @return null if no cTree else list of divs of extractor type
	 */
	public List<HtmlDiv> getDivList(DivListExtractor extractor) {
		List<HtmlDiv> divList = null;
		if (cTree != null) {
			File existingFulltextXML = cTree.getExistingFulltextXML();
			if (existingFulltextXML != null) {
				JATSSectionTagger tagger = new JATSSectionTagger();
				tagger.getOrCreateJATSHtml(existingFulltextXML);
				divList = extractor.getDivList(tagger);
			}
		}
		return divList;
	}


	/** get a single Div of given type.
	 * assume we have read a cTree
	 * @param extractor
	 * @return null if no cTree or dov.size() != 1 else list of divs of extractor type
	 */
	public HtmlDiv getSingleDiv(SectionExtractor extractor) {
		HtmlDiv div = null;
		if (cTree != null) {
			File existingFulltextXML = cTree.getExistingFulltextXML();
			if (existingFulltextXML != null) {
				JATSSectionTagger tagger = new JATSSectionTagger();
				tagger.getOrCreateJATSHtml(existingFulltextXML);
				div = extractor.getSingleDiv(tagger);
			}
		}
		return div;
	}


	/** get a single HtmlElement.
	 * assume we have read a cTree
	 * @param extractor
	 * @return null if no cTree else list of divs of extractor type
	 */
	public HtmlElement getHtmlElement(HtmlElementExtractor extractor) {
		HtmlElement htmlElement = null;
		if (cTree != null) {
			JATSSectionTagger tagger = JATSSectionTagger.createAndPopulateTagger(cTree);
			tagger.getOrCreateJATSHtmlElement();
			htmlElement = extractor.getHtmlElement(tagger);
		}
		return htmlElement;
	}

	public static JATSSectionTagger createAndPopulateTagger(CTree cTree) {
		JATSSectionTagger tagger = null;
		if (cTree != null) {
			tagger = new JATSSectionTagger();
			tagger.readJATS(cTree);
		}
		return tagger;
	}

	public void setCTree(CTree cTree) {
		this.cTree = cTree;
	}

	public void addAbstractAsFile() {
		List<HtmlDiv> divList = this.getDivList(new ArticleAbstractExtractor());
		if (divList != null && divList.size() > 0) {
			if (divList.size() > 1) {
				LOG.debug(this.cTree.getName() + ": more than 1 abstract: "+divList.size());
			}
			cTree.writeFulltextHtmlToChildDirectory(divList.get(0), CTree.ABSTRACT);
		}
	}

	public void addProjectAbstractsAsFiles() {
		if (cProject != null) {
			for (CTree cTree : cProject.getOrCreateCTreeList()) {
				JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
				treeTagger.addAbstractAsFile();
			}
		}
	}

	public void addFrontAsFile() {
		FrontExtractor frontExtractor = new FrontExtractor(cTree);
		extractAndOutputSections(frontExtractor);
	}

	public void addProjectFrontsAsFiles() {
		if (cProject != null) {
			for (CTree cTree : cProject.getOrCreateCTreeList()) {
				JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
				treeTagger.addFrontAsFile();
			}
		}
	}

	public void addBackAsFile() {
		BackExtractor backExtractor = new BackExtractor(cTree);
		extractAndOutputSections(backExtractor);
	}

	public void addProjectBacksAsFiles() {
		if (cProject != null) {
			for (CTree cTree : cProject.getOrCreateCTreeList()) {
				JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
				treeTagger.addBackAsFile();
			}
		}
	}

	public void addBodyAsFile() {
		BodyExtractor bodyExtractor = new BodyExtractor(cTree);
		extractAndOutputSections(bodyExtractor);
	}

	private void extractAndOutputSections(SectionExtractor extractor) {
		HtmlDiv div = extractor.getSingleDivAndAddAsFile(this, extractor.getSection());
		extractor.extractAndOutputSections(div, extractor.getDirectoryName());
	}

	public HtmlDiv getSingleDivAndAddAsFile(SectionExtractor extractor, String sectionName) {
		return extractor.getSingleDivAndAddAsFile(this, sectionName);
	}

	public void addProjectBodysAsFiles() {
		if (cProject != null) {
			for (CTree cTree : cProject.getOrCreateCTreeList()) {
				JATSSectionTagger treeTagger = JATSSectionTagger.createAndPopulateTagger(cTree);
				treeTagger.addBodyAsFile();
			}
		}
	}

	public SectionType getSectionType() {
		return sectionType;
	}

	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}




}
class MapEntry {
	String key;
	List<String> valueList;
	public MapEntry(String key, String ... values) {
		this.key = key;
		this.valueList = Arrays.asList(values);
	}
}
