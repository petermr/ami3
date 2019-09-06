package org.contentmine.ami.tools;

import java.io.File;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlUl;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class TEI2HtmlConverter {
	private static final String WHEN = "when";
	private static final String KEY = "key";
	private static final String ORG_NAME = "orgName";
	private static final String SURNAME = "surname";
	private static final String TYPE = "type";
	private static final String FORENAME = "forename";
	private static final String AFFILIATION = "affiliation";
	private static final String FROM = "from";
	private static final String TO = "to";
	private static final String ISSUE = "issue";
	private static final String PAGE = "page";
	private static final String VOLUME = "volume";
	private static final String UNIT = "unit";
	private static final String PUB_PLACE = "pubPlace";
	private static final String PERS_NAME = "persName";
	private static final String BODY_BACK = "bodyBack";
	private static final String BIBL_SCOPE = "biblScope";
	private static final String IMPRINT = "imprint";
	private static final String NOTE = "note";
	private static final String TABLE = "table";
	private static final String GRAPHIC = "graphic";
	private static final String FIG_DESC = "figDesc";
	private static final String LABEL = "label";
	private static final String FORMULA = "formula";
	private static final String P = "p";
	private static final String FIGURE = "figure";
	private static final String AUTHOR = "author";
	private static final String TITLE = "title";
	private static final String MONOGR = "monogr";
	private static final String ANALYTIC = "analytic";
	private static final String BIBL_STRUCT = "biblStruct";
	private static final String LIST_BIBL = "listBibl";
	private static final String HEAD = "head";
	private static final Logger LOG = Logger.getLogger(TEI2HtmlConverter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	

	private static final String REFERENCES = "references";
	private static final String DIV = "div";
	private static final String BACK = "back";
	private static final String SOURCE_DESC = "sourceDesc";
	private static final String BODY = "body";
	private static final String APP_INFO = "appInfo";
	private static final String DATE = "date";
	private static final String AVAILABILITY = "availability";
	private static final String PUBLISHER = "publisher";
	private static final String PUBLICATION_STMT = "publicationStmt";
	private static final String TITLE_STMT = "titleStmt";
	private static final String PROFILE_DESC = "profileDesc";
	private static final String FILE_DESC = "fileDesc";
	private static final String ENCODING_DESC = "encodingDesc";
	private static final String TEXT = "text";
	private static final String TEI_HEADER = "teiHeader";
	private static final String TEI = "TEI";
	private HtmlHead htmlHead;

	/**
	<TEI xmlns="http://www.tei-c.org/ns/1.0" 
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
			xsi:schemaLocation="http://www.tei-c.org/ns/1.0 /Users/pm286/workspace/grobid/grobid-0.5.3/grobid-home/schemas/xsd/Grobid.xsd"
			 xmlns:xlink="http://www.w3.org/1999/xlink">
			 */
	

	public HtmlHtml createHtmlElement(File xmlFile) {
		Element teiElement = XMLUtil.parseQuietlyToDocument(xmlFile).getRootElement();
		HtmlHtml htmlHtml = createHtmlHtml(teiElement);
		htmlHead = htmlHtml.getOrCreateHead();
		HtmlStyle htmlStyle = htmlHead.addCssStyle(""
				+ "*   {font-family:helvetica;}"
				+ "div {background : #ffffee;"
				+ "     margin-top : 3px;"
				+ "     margin-left : 2px;"
				+ "     border : 1px solid black;}"
				+ "div.head {font-size:24;"
				+ "    font-weight:bold;"
				+ "    background:white;}"
				+ "li {background : yellow}"
				+ ".titleStmt {background : #ffeeff;"
				+ "    font-size:32px;"
				+ "    font-weight:bold;}"
				+ ".sourceDesc {background : #dddddd;}"
				+ ".profileDesc {background : #eeeeff;}"
				+ "    ref {background:#ddffdd;}"
				);
		htmlStyle.addCss("li {background : yellow}");
		return htmlHtml;
	}


	public HtmlHtml createHtmlHtml(Element element) {
		String tag = element.getLocalName();
		HtmlHtml htmlHtml = null;
		if (tag.equals(TEI)) {
			htmlHtml = new HtmlHtml();
			Elements childElements = element.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				Element childElement = childElements.get(i);
				tag = childElement.getLocalName();
				HtmlElement subElement;
				if (tag.equals(TEI_HEADER)) {
					subElement = processTeiHeader(childElement);
				} else if (tag.equals(TEXT)) {
					subElement = processText(childElement);
				} else {
					subElement = createDivWithValue(childElement);
				}
				htmlHtml.appendChild(subElement);
			}
		} else {
			System.err.println("wrong toplevel:"+tag);
		}
		return htmlHtml;
	}


	 /*
		<teiHeader xml:lang="en">
			<encodingDesc>
				<appInfo>
					<application version="0.5.3" ident="GROBID" when="2019-01-09T17:27+0000">
						<ref target="https://github.com/kermitt2/grobid">GROBID - A machine learning software for extracting information from scholarly documents</ref>
					</application>
				</appInfo>
			</encodingDesc>
			<fileDesc>
				<titleStmt>
					<title level="a" type="main">Academic Benefits of Peer Tutoring: A Meta-Analytic Review of Single-Case Research</title>
				</titleStmt>
				<publicationStmt>
					<publisher/>
					<availability status="unknown"><licence/></availability>
				</publicationStmt>
				
				<sourceDesc>
					<biblStruct>
						<analytic>
							<author>
								<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">Lisa</forename><surname>Bowman-Perrott</surname></persName>
								<affiliation key="aff0">
									<orgName type="institution" key="instit1">Texas A&amp;M University</orgName>
									<orgName type="institution" key="instit2">University of Kansas</orgName>
									<orgName type="institution" key="instit3">Texas A&amp;M University</orgName>
								</affiliation>
							</author>
							...
							<title level="a" type="main">Academic Benefits of Peer Tutoring: A Meta-Analytic Review of Single-Case Research</title>
						</analytic>
						<monogr>
							<imprint>
								<date/>
							</imprint>
						</monogr>
					</biblStruct>
				</sourceDesc>
				
			</fileDesc>
			
			<profileDesc>
				<textClass>
					<keywords>
						<term>1967</term>
						<term>Cohen, Kulik, &amp; Kulik, 1982</term>
						<term>Del- quadri, Greenwood, Whorton, Carta, &amp; Hall, 1986</term>
						<term>Mastropieri, Spencer, Scruggs, &amp; Tal-</term>
					</keywords>
				</textClass>
				
				<abstract>
					<p>Peer tutoring is an instructional strategy that involves students helping each other learn 
					content through repetition of key concepts. This meta-analysis examined effects of peer tutoring 
					across 26 single-case research experiments for 938 students in Grades 1-12. The TauU effect size for 
					195 phase contrasts was 0.75 with a confidence interval of CI 95 񮽙 0.71 to 0.78, indicating that 
					moderate to large academic benefits can be attributed to peer tutoring. Five potential moderators 
					of these effects were examined: dosage, grade level, reward, disability status, and content area. 
					This is the first peer tutoring meta-analysis in nearly 30 years to examine outcomes for elementary 
					and secondary students, and extends previous peer tutoring meta-analyses by examining disability as 
					a potential moderator. Findings suggest that peer tutoring is an effective intervention regardless of 
					dosage, grade level, or disability status. Among students with disabilities, those with emotional and 
					behavioral disorders benefitted most. Implications are discussed. The peer tutoring research base 
					spans more than 40 years and convincingly demonstrates an evidence-based practice (Cloward,</p>
				</abstract>
				
			</profileDesc>
		</teiHeader>
		*/
	

	private HtmlDiv processTeiHeader(Element teiHeader) {
		Elements childElements = teiHeader.getChildElements();
		HtmlDiv div = new HtmlDiv();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlDiv subDiv;
			if (tag.equals(ENCODING_DESC)) {
				subDiv = processEncodingDesc(childElement);
			} else if (tag.equals(FILE_DESC)) {
				subDiv = processFileDesc(childElement);
			} else if (tag.equals(PROFILE_DESC)) {
				subDiv = processProfileDesc(childElement);
			} else {
				subDiv = createDivWithValue(childElement);
				System.err.println("unknown tagin teiHeader: "+tag);
			}
			div.appendChild(subDiv);
		}
		return div;
	}
	
	/**
	<encodingDesc>
		<appInfo>
			<application version="0.5.3" ident="GROBID" when="2019-01-09T17:27+0000">
				<ref target="https://github.com/kermitt2/grobid">GROBID - A machine learning software for extracting information from scholarly documents</ref>
			</application>
		</appInfo>
	</encodingDesc>
	 * @param encodingElement
	 */
	private HtmlDiv processEncodingDesc(Element element) {
		Elements childElements = element.getChildElements();
		HtmlDiv div = createEmptyDivWithClass(element);
		for (int i = 0; i < childElements.size(); i++) {
			HtmlElement subDiv;
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			if (tag.equals(APP_INFO)) {
				subDiv = processAppInfo(childElement);
			} else {
				subDiv = createDivWithValue(childElement);
			}
			div.appendChild(subDiv);
		}
		return div;
	}

	private HtmlDiv processAppInfo(Element element) {
		return createDivWithValue(element);
	}


	/**
<fileDesc>
	<titleStmt>
		<title level="a" type="main">Academic Benefits of Peer Tutoring: A Meta-Analytic Review of Single-Case Research</title>
	</titleStmt>
	<publicationStmt>
		<publisher/>
		<availability status="unknown"><licence/></availability>
	</publicationStmt>
	
	<sourceDesc>
		<biblStruct>
			<analytic>
				<author>
					<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">Lisa</forename><surname>Bowman-Perrott</surname></persName>
					<affiliation key="aff0">
						<orgName type="institution" key="instit1">Texas A&amp;M University</orgName>
						<orgName type="institution" key="instit2">University of Kansas</orgName>
						<orgName type="institution" key="instit3">Texas A&amp;M University</orgName>
					</affiliation>
				</author>
				...
				<title level="a" type="main">Academic Benefits of Peer Tutoring: A Meta-Analytic Review of Single-Case Research</title>
			</analytic>
			<monogr>
				<imprint>
					<date/>
				</imprint>
			</monogr>
		</biblStruct>
	</sourceDesc>
	
</fileDesc>
*/
	private HtmlDiv processFileDesc(Element element) {
		Elements childElements = element.getChildElements();
		HtmlDiv div = createEmptyDivWithClass(element);
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlDiv subDiv;
			if (tag.equals(TITLE_STMT)) {
				subDiv = processTitleStmt(childElement);
			} else if (tag.equals(PUBLICATION_STMT)) {
				subDiv = processPublicationStmt(childElement);
			} else if (tag.equals(SOURCE_DESC)) {
				subDiv = processSourceDesc(childElement);
			} else {
				subDiv = createDivWithValue(childElement);
			}
			div.appendChild(subDiv);
		}
		return div;
	}

	private HtmlDiv processTitleStmt(Element element) {
		return createDivWithValue(element);
	}

	/**
	<publisher />
	<availability status="unknown"><licence /></availability>
	<date type="published" when="2016-11-15">Available online 15 November 2016</date>
	*/
	private HtmlDiv processPublicationStmt(Element element) {
		
//		System.out.println(">publisher> "+getSingleChildValue(childElement, PUBLISHER));
//		System.out.println(">availability> "+getSingleChildValue(childElement, AVAILABILITY));
//		System.out.println(">date> "+getSingleChildValue(childElement, DATE));
		Elements childElements = element.getChildElements();
		HtmlDiv div = createEmptyDivWithClass(element);
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement subDiv;
			if (tag.equals(PUBLISHER)) {
				subDiv = createSpan(childElement);
			} else if (tag.equals(PUBLICATION_STMT)) {
				subDiv = processPublicationStmt(childElement);
			} else if (tag.equals(SOURCE_DESC)) {
				subDiv = processSourceDesc(childElement);
			} else {
				subDiv = createDivWithValue(childElement);
			}
			div.appendChild(subDiv);
		}
		return div;
	}


	private HtmlDiv processSourceDesc(Element element) {
		return createDivWithValue(element);
	}


	private HtmlDiv processProfileDesc(Element element) {
		return createDivWithValue(element);
	}

	/**
	<text xml:lang="en">
		<body>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Single-Case Research, Effect Size, and Confidence Intervals</head><p>Single-case research methods can "provide a rigorous experimental evaluation" of the efficacy of an intervention ( <ref type="bibr" coords="2,189.00,568.47,57.02,9.16;2,60.00,580.54,59.42,9.16">Kratochwill et al., 2010, p. 2)</ref>. As such, single-case research has been used to identify a range of interventions used in schools, as this method of inquiry can help identify practices that are evidencebased ( <ref type="bibr" coords="2,90.58,628.81,73.66,9.16" target="#b25">Horner et al., 2005</ref>). The use of effect size in single-case research allows for a determination of the size or magnitude of academic or behavioral change. Determining the size of the effect, as well as a functional relation, is critical in light of accountability for instructional practices and multitier models of early intervention (see Council for Exceptional <ref type="bibr" coords="2,258.00,125.34,64.66,9.16">Children, 2008</ref>; National Association of <ref type="bibr" coords="2,258.00,137.42,113.47,9.16">School Psychologists, 2010)</ref>.</p><p>Data from single-case studies of schoolbased practices are being summarized more as new methods are being developed that can address positive baseline trends and that require few assumptions about the data <ref type="bibr" coords="2,412.09,197.82,31.93,9.16;2,258.00,209.90,139.28,9.16" target="#b52">(Parker, Vannest, Davis, &amp; Sauber, 2011)</ref>. Although many studies using single-case research designs may be found in the peer tutoring literature, neither individual nor aggregated effect sizes with corresponding confidence intervals have been published to date. This is a significant shortcoming, as effect sizes aid in summarizing data across studies. Further, confidence intervals are needed for accurate interpretation of effect size data <ref type="bibr" coords="2,381.11,318.62,62.90,9.16" target="#b13">(Cooper, 2011;</ref><ref type="bibr" coords="2,258.00,330.70,82.44,9.16" target="#b30">Hunter et al., 1982;</ref><ref type="bibr" coords="2,344.59,330.70,67.43,9.16" target="#b61">Thompson, 2002</ref><ref type="bibr" coords="2,420.67,330.70,18.66,9.16" target="#b62">Thompson, , 2007</ref> and are required by the American Psychological Association (APA; American Psychological <ref type="bibr" coords="2,278.37,366.94,78.43,9.16">Association, 2010;</ref><ref type="bibr" coords="2,362.72,366.94,81.30,9.16;2,258.00,379.02,162.51,9.16">Wilkinson &amp; APA Task Force on Statistical Inference, 1999</ref>). An effect size with confidence intervals tells about the relative size of an effect compared to other treatments, and provides a standard metric for comparison and aggregation. Further, in an era of evidence-based practices, it provides data that are more readily understood. The use of a common effect size metric with single-case research, as with group designs, is essential to allow for the aggregation of results across studies.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Effect Size Metrics Used in Previous Single-Case Research</head><p>There are at least eight commonly used nonoverlap effect size metrics (indices) in single-case research. They include percentage of nonoverlapping data <ref type="bibr" coords="2,350.04,592.62,26.11,9.16">(PND;</ref><ref type="bibr" coords="2,382.45,592.62,61.57,9.16;2,258.00,604.69,103.30,9.16">Scruggs, Mas- tropieri, &amp; Casto, 1987)</ref>, percentage of all nonoverlapping data <ref type="bibr" coords="2,345.04,616.75,33.33,9.16">(PAND;</ref><ref type="bibr" coords="2,382.18,616.75,61.84,9.16;2,258.00,628.82,104.95,9.16" target="#b50">Parker, Hagan- Burke, &amp; Vannest, 2007)</ref>, nonoverlap of all pairs <ref type="bibr" coords="2,279.65,640.89,26.11,9.16">(NAP;</ref><ref type="bibr" coords="2,307.98,640.89,131.75,9.16" target="#b51">Parker, Vannest, &amp; Brown, 2009)</ref>, extended celeration line (ECL; <ref type="bibr" coords="2,386.83,652.96,57.19,9.16;2,258.00,665.03,35.04,9.16">White &amp; Har- ing, 1980</ref>), improvement rate difference (IRD; <ref type="bibr" coords="3,60.00,77.01,71.19,9.16" target="#b51">Parker et al., 2009</ref>), percent of data exceeding the median (PEM; <ref type="bibr" coords="3,140.24,89.26,37.02,9.16" target="#b39">Ma, 2006</ref>), Pearson's Phi ( <ref type="bibr" coords="3,64.20,101.51,76.29,9.16" target="#b50">Parker et al., 2007)</ref>, and Kendall's TauU for nonoverlap between groups with baseline trend control (Tau novlap ; Parker, Vannest, <ref type="bibr" coords="3,231.01,126.01,14.99,9.16;3,60.00,138.26,84.30,9.16">Da- vis, &amp; Sauber, 2011)</ref>. Although a full review of these indices is beyond the scope of this article (see <ref type="bibr" coords="3,106.85,162.76,71.39,9.16" target="#b52">Parker et al., 2011</ref>), a brief review of limitations of each compared to TauU follows.</p><p>Although not intended by its originators as an effect size, PND has been used this way in several meta-analyses. Limitations of PND include its unknown distribution qualities (with the consequent inability to provide a standard error or confidence intervals) and its insensitivity to treatment effects. As such, it is not recommended for meta-analyses (see <ref type="bibr" coords="3,232.69,285.26,13.33,9.16;3,60.00,297.51,92.74,9.16">Al- lison &amp; Gorman, 1993;</ref><ref type="bibr" coords="3,155.23,297.51,76.75,9.16" target="#b25">Horner et al., 2005)</ref>. A limitation of PND, PAND, NAP, IRD, PEM, and Phi, is that they cannot control for positive baseline trend. Of the eight methods, only ECL and TauU can do that. However, ECL controls for only linear trend, whereas TauU controls for any shape of increase, known as monotonic trend. The weakest statistical power among these eight indices is shown by PEM, followed by ECL (it cannot be ascertained with PND). Medium statistical power is obtained by Phi and IRD; strongest statistical power is by TauU and NAP. Limitations of ECL and PEM also include their assumption that the median value is a reliable summary of Phase A. This assumption is only correct for data sets demonstrating measures of central tendency. TauU does not make this assumption.</p><p>Another criterion for a good effect size is that it should discriminate among results from different studies. A nondiscriminating index will lump all low and/or all high results together. Worst discrimination is shown by PEM, followed by PND. Best discrimination is by TauU, with baseline trend control. Phi also has good discriminating ability. TauU is also robust to autocorrelation (with little impact on standard error; <ref type="bibr" coords="3,160.97,628.26,76.42,9.16" target="#b52">Parker et al., 2011</ref>). Finally, TauU is well suited to very short phases. It does not require the minimum four to six expected data points per cell that nonparametric methods based on cross-tabulation do (e.g., IRD, Phi).</p><p>The limitations of TauU include it being a relatively new effect size measure, so there are relatively few publications from which its sizes can be judged. A second limitation is shared by all nonoverlap indices but Phi: nonoverlap effect sizes are not directly comparable to the familiar Pearson's r or <ref type="bibr" coords="3,410.68,173.65,33.33,9.16;3,258.00,185.73,26.66,9.16" target="#b10">Cohen's (1988)</ref> d. However, TauU's strengths (statistical power, low N requirement, distributionfree, good discrimination ability among studies, control of unwanted baseline trend, a known sampling distribution) are such that it appears to be the best nonparametric index at present and is even competitive with parametric indices.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Results</head></div>
...
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Discussion</head><p>This meta-analysis is the first peer tutoring meta-analysis to examine achievement outcomes for elementary and secondary students across peer tutoring studies using singlecase research designs. The overall effect size was found to be moderately large, indicating that greater academic gains were achieved by students engaged in peer tutoring interventions than nonpeer tutoring instructional arrangements.</p><p>Moderator analyses revealed a statistically significant effect for the use of rewards. Peer tutoring interventions that used rewards had a larger effect size than those that did not. This finding points to the importance of the use of reward on academic outcomes, especially for middle and high school students. Findings from several peer tutoring studies support its use with older students as a means of motivation. This seems to be the case particularly for students who have experienced academic difficulties (see <ref type="bibr" coords="11,366.27,628.57,77.76,9.16;11,258.00,640.72,41.97,9.16" target="#b7">Bowman-Perrott et al., 2007;</ref><ref type="bibr" coords="11,306.95,640.72,91.47,9.16" target="#b35">Kamps et al., 2008;</ref><ref type="bibr" coords="11,405.39,640.72,38.61,9.16;11,258.00,652.87,116.80,9.16" target="#b46">Mitchem, Young, West, &amp; Benyo, 2001</ref>). This finding is also consistent with previous research at the  elementary school level (e.g., <ref type="bibr" coords="12,181.87,77.01,64.13,9.16;12,60.00,89.01,21.53,9.16" target="#b55">Rohrbeck et al., 2003)</ref>, as the use of reward contingencies produced a statistically significant effect. In four studies, data for elementary and secondary students were not disaggregated. The two studies from <ref type="bibr" coords="12,148.05,137.01,97.96,9.16" target="#b22">Greenwood et al. (1984)</ref> reported combined data for students in Grades 3-6, <ref type="bibr" coords="12,82.39,161.01,121.61,9.16" target="#b45">Mayfield and Vollmer (2007)</ref> presented data for students in Grades 3-11, and the spelling class data from <ref type="bibr" coords="12,157.36,185.01,88.67,9.16;12,60.00,197.01,26.66,9.16" target="#b7">Bowman-Perrott et al. (2007)</ref> were reported for students in Grades 5-12 together. Mayfield and Vollmer implemented peer tutoring in group homes and students' homes. <ref type="bibr" coords="12,122.13,233.01,123.89,9.16" target="#b7">Bowman-Perrott et al. (2007)</ref> conducted peer tutoring in an alternative school that was part of a residential treatment facility. In these instances, the way students were grouped in their natural learning environments did not follow traditional grade-level groupings. Data for these studies were not included in grade-level analyses because they would not provide insight into the effect of peer tutoring on students at the elementary versus secondary level. Results demonstrated that peer tutoring was effective for both elementary and secondary students, and that grade level did not moderate its effectiveness. Although individual grade level analyses could not be conducted because data were not disaggregated by grade level in the majority of the studies, participants tended to represent certain grade levels. For studies involving elementary school students, the average grade level was fourth grade, followed by third grade. Studies focusing on secondary students tended to include sixth-graders most often, followed by ninth-graders.</p><p>Consistent with the <ref type="bibr" coords="12,176.61,521.01,69.40,9.16;12,60.00,533.01,26.66,9.16" target="#b55">Rohrbeck et al. (2003)</ref> study, the findings of the current metaanalysis showed no difference in student outcomes for studies with dosage amounts above and below the median value. Perhaps the core components of peer tutoring (e.g., increased opportunities to respond, error correction procedures) are sufficient to make an impact on student outcomes with as few as 280 min of exposure to the intervention and as many as just over 1,000 min. The finding that students with or at risk for disabilities demonstrated greater academic gains than their peers without disabilities or at-risk status may be reflective of the benefit students received from the additional support (e.g., more opportunities to respond) afforded by peer tutoring. This may be especially likely, as all of these students were identified as being below grade level in a given content area.</p><p>Twenty-three of the 26 studies included participants with identified disabilities or who were determined to be at risk for being identified as having a disability because of poor academic performance. Although differences were not statistically significant, practical significance can be attributed to the finding that the effect size was larger for students with or at risk for disabilities (.76) than for students without disabilities or who were not at risk <ref type="bibr" coords="12,258.00,281.01,18.05,9.16">(.65)</ref>. Results support evidence that aspects of peer tutoring interventions such as repetition of key concepts and opportunities to respond are particularly beneficial for students in need of additional academic supports. Of the 23 studies that included students with or at risk for disabilities, only 11 disaggregated achievement outcomes by disability category. With regard to disability status, only data for students identified as having a LD or EBD as their primary disability were analyzed, as only one study disaggregated data for students with autism, one for students with MR, and one for students with other health impairments. It is important to note, however, that the number of studies for analyses of students with LD and EBD were small; caution should be used in considering these results.</p><p>Ten studies focused on reading, six on spelling, six on math, three on vocabulary, and three on social studies. Spelling outcomes were measured by students' correct spelling of words and the percentage of words capitalized correctly. Reading measures included (a) nonsense word fluency, (b) errors per minute, (c) sight word acquisition, and (d) comprehension. Math measures consisted of (a) correctly multiplying decimals, (b) changing decimals to fractions, (c) calculating percentages, and (d) adding and subtracting time. Vocabulary included the percent of vocabulary words correct. Finally, social studies included history; specific learning outcomes were not reported. <ref type="bibr" coords="12,60.00,34.48,184.49,8.18">School Psychology Review, 2013, Volume 42, No. 1</ref> In the present analysis, reading yielded a large to moderate effect size (ES 񮽙 0.77), compared with the effect sizes reported by <ref type="bibr" coords="13,196.09,101.17,49.91,9.16;13,60.00,113.25,26.66,9.16" target="#b11">Cohen et al. (1982)</ref> of 0.29, <ref type="bibr" coords="13,124.41,113.25,74.69,9.16" target="#b12">Cook et al. (1985)</ref> of 0.30 for tutors and 0.49 for tutees, and <ref type="bibr" coords="13,184.31,125.33,61.71,9.16;13,60.00,137.41,26.66,9.16" target="#b55">Rohrbeck et al. (2003)</ref> at 0.26 for reading. The effect size obtained for math in this meta-analysis (ES 񮽙 0.86) was also larger than that reported by <ref type="bibr" coords="13,72.49,173.65,76.63,9.16" target="#b11">Cohen et al. (1982;</ref><ref type="bibr" coords="13,151.62,171.14,44.42,13.00">ES 񮽙 0.60)</ref>, <ref type="bibr" coords="13,202.42,173.65,43.59,9.16;13,60.00,185.73,26.11,9.16" target="#b12">Cook et al. (1985;</ref><ref type="bibr" coords=""></ref> ES 񮽙 0.67 and 0.85 for tutors and tutees, respectively), and <ref type="bibr" coords="13,175.88,197.81,70.13,9.16;13,60.00,209.89,26.11,9.16" target="#b55">Rohrbeck et al. (2003;</ref><ref type="bibr" coords="13,90.72,207.38,48.67,13.00">ES 񮽙 0.22)</ref>. Although social studies had a smaller effect in this meta-analysis (ES 񮽙 0.57), it was larger than that reported by <ref type="bibr" coords="13,74.40,246.13,95.12,9.16" target="#b55">Rohrbeck et al. (2003;</ref><ref type="bibr" coords="13,173.92,243.62,48.23,13.00">ES 񮽙 0.49)</ref>. The obtained moderate effect size for spelling (ES 񮽙 0.74) was larger than those reported by Cook et al. (ES 񮽙 0.01 and 0.51 for tutors and tutees, respectively) and <ref type="bibr" coords="13,174.88,294.45,71.13,9.16;13,60.00,306.53,26.66,9.16" target="#b55">Rohrbeck et al. (2003)</ref> (ES 񮽙 0.21). Finally, vocabulary had a large effect (ES 񮽙 0.92). Because previous meta-analysis reported data for writing, language, literacy, or a combination of related content areas, there is no vocabulary effect size with which to compare the present findings. The effect sizes for content areas should be considered with caution, as a small number of studies were available for analysis. This is particularly true for vocabulary and social studies.</p><p>As previously mentioned, treatment fidelity data were reported in 16 of the 26 studies (62%). <ref type="bibr" coords="13,103.34,463.50,89.60,9.16" target="#b55">Rohrbeck et al. (2003)</ref> reported that 68% of studies in their meta-analysis reported these data. It is important to consider the potential impact of treatment fidelity on study outcomes. It was not examined as a potential moderator because fidelity was high in the studies that reported it. Therefore, comparing studies with low fidelity to those with high fidelity was not possible. It is important to report these data to help understand the degree to which teachers and students accurately implement peer tutoring interventions.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Limitations</head><p>The findings of this meta-analysis should be considered in light of the following limitations. One limitation is the lack of disaggregated disability data in some studies, limiting our sample for these analyses. Similarly, data were not disaggregated by grade level in most of the articles, so could not present results and recommendations by grade level. Another limitation is the potential variability that was introduced by how academic gains were measured across studies (e.g., curriculum-based measures vs. standardized tests) and peer tutoring type (e.g., cross-age vs. same age). A third potential limitation is that the effect sizes for the content areas may change with a larger pool of studies (especially for vocabulary and social studies). A final limitation is that caution should be used in comparing TauU to Cohen's d effect sizes. For example, the conversion from Cohen's d to TauU for the <ref type="bibr" coords="13,326.35,282.38,82.37,9.16" target="#b35">Kamps et al. (2008)</ref> study is an approximation. Future research can help address some of these limitations to further add to the peer tutoring literature.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Implications for Research</head><p>The findings of this meta-analysis underscore several recommendations that can inform future research. The first is the need to report treatment fidelity in peer tutoring studies. Knowing whether high versus low levels of fidelity promote greater academic gains would be beneficial in informing practice. The second is that treatment fidelity could serve as a moderator of student outcomes. This should be investigated by grade and across content areas. In addition, it would be helpful to addresses practical questions such as the following: (a) Is there a minimum number of hours or sessions needed for students to gain the most benefit from peer tutoring? (b) Does the type of academic outcome measurement (e.g., criterion-referenced vs. norm-referenced) moderate the effect of peer tutoring? (c) Does the comorbidity of LD and EBD affect student outcomes? (d) What do outcomes look like for students with autism and other disabilities? The last question is important, as data in these analyses were limited to students with LD and EBD because the number of cases for students with other disabilities (e.g., autism) was too small to investigate.</p><p>The peer tutoring literature would also benefit from studies that disaggregate data by grade (e.g., first grade) because the effects of peer tutoring, and moderators of those effects, may vary within each of the grades. Analyzing data by grade level (viz, elementary or secondary) would be beneficial. For example, it would be helpful to know whether first-graders may benefit more than fifth-graders. This would also prove useful in further analyzing potential moderators. For instance, the use of rewards may be significant with sixth-graders but not eighth-grade students.</p><p>Another recommendation is that future single-case peer tutoring studies should apply strong designs in keeping with recent standards (e.g., <ref type="bibr" coords="14,109.87,270.29,100.65,9.16" target="#b37">Kratochwill et al., 2010)</ref>. Unfortunately, 11 single-case studies were excluded from these analyses because of weak designs (n 񮽙 6) and because interobserver agreement standards were not met (n 񮽙 5; <ref type="bibr" coords="14,188.33,318.61,57.69,9.16;14,60.00,330.69,34.20,9.16" target="#b37">Kratochwill et al., 2010</ref>). Among those excluded for these reasons were the two studies that focused on science in middle and high school classrooms. Thus, there is a need to investigate students' outcomes in this core content area. Further, more studies are needed across content areas, as the small number of studies in some of the content areas prevented a thorough analysis of students' academic outcomes.</p><p>Future research should examine the relation between academic and behavioral outcomes for students engaged in peer tutoring. This would be particularly useful in light of the finding that students with EBD benefitted most from peer tutoring. It would be interesting to examine the benefit students receive from peer tutoring with regard to behaviors. Finally, as a new effect size measure, this meta-analysis should be replicated using TauU as new single-case studies investigating peer tutoring are published. Given the many advantages of TauU, it holds great promise for aggregating single-case research data.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Implications for Practice</head><p>This meta-analysis provides evidence for the use of peer tutoring as an evidencebased instructional practice based on the most current, high-quality standards for single-case research. Social validity data across peer tutoring studies revealed that teachers find it easy to implement within their existing teaching routine and structure. As such, teacher training programs and in-service training for practicing teachers should include peer tutoring. This is particularly important in an era of increased accountability for implementing evidence-based practices and the implementation of multitiered early intervention supports.</p><p>Peer tutoring is an effective intervention for students with disabilities. This is especially noteworthy for students with EBD who have been consistently identified in the literature as performing below grade level, and for whom academic deficits are part of the federal definition and criteria for identification in Individuals with <ref type="bibr" coords="14,300.86,293.02,139.00,9.16">Disabilities Education Act (2004)</ref>. Problem behaviors, a characteristic of students with EBD, adversely affect academic performance (see Individuals with Disabilities Education <ref type="bibr" coords="14,287.24,341.02,43.27,9.16">Act, 2004)</ref>. Results showed that students with EBD, who by nature of their disability demonstrate problem behaviors, are most likely to benefit academically from a peer tutoring instructional format. It is an intervention that is highly recommended for their peers without disabilities as well.</p><p>Peer tutoring is effective in promoting academic gains across content areas, and is effective for elementary, middle, and high school students. The use of rewards appear to benefit older students as a motivator.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Footnotes</head>
<p>*References marked with an asterisk indicate studies included in the meta-analysis.</p>
<p>Lisa Bowman-Perrott, PhD, is an assistant professor in the Department of Educational Psychology, Special Education, at Texas A&amp;M University. Her primary research interests are academic and behavioral interventions for students with or at risk for emotional and behavioral disorders, including peer tutoring.</p>
<p>Heather S. Davis is a doctoral student in the Special Education program at Texas A&amp;M University. Her research interests are identifying and providing evidence-based behavioral interventions to children exhibiting challenging behavior and training teachers and parents on implementing evidence-based behavioral interventions in school and home settings.</p>
<p>Kimber J. Vannest, PhD, is an associate professor in the Department of Educational Psychology, Special Education, at Texas A&amp;M University. Her research interests are in determining effective interventions for children and youth with or at risk for emotional and behavioral disorders, including teacher behaviors and measurement.</p>
<p>Lauren Williams, MEd, is a special education teacher for students with communication, academic, and social learning deficits. Her primary interest is in working with students with emotional and behavioral disorders; she is working toward certification as a boardcertified behavior analyst.</p>
<p>Charles R. Greenwood, PhD, is director of the Juniper Gardens Children's Project (JGCP), senior scientist in the Schiefelbusch Institute for Life Span Studies, and a professor in the Department of Applied Behavioral Science and the Department of Special Education at the University of Kansas. He is the developer of ClassWide Peer Tutoring, a class-wide instructional approach for teaching basic academic skills.</p>
<p>Richard Parker, PhD, is recently retired from Texas A&amp;M University Educational Psychology Department. His continued research interest is single-case research methodology.</p></div>
<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_0" validated="false" coords="8,60.00,74.50,349.62,599.67">
  <head>'s d 񮽙 (M 1 񮽙 M 2 )/񮽙 pooled where 񮽙pooled 񮽙 񮽙[(񮽙1 2 񮽙񮽙2 2 ) / 2]. The Cohen's d effect size was also obtained from WinPepi along with Cohen's d SE (Abramson, 2011). Second, Co- hen's d was transformed to Tau using the formula Tau 񮽙 1 񮽙 (1 񮽙 d/3.464) 2</head>
  <label></label>
  <figDesc coords="8,236.49,604.17,9.52,9.16;8,60.00,613.83,35.88,13.00;8,95.88,621.04,3.75,5.95;8,105.08,613.83,22.11,13.00;8,127.18,621.04,3.75,5.95;8,130.93,616.34,12.22,9.16;8,143.15,621.04,20.42,5.95;8,169.01,613.83,76.98,13.00;8,60.00,626.00,27.77,13.00;8,87.77,626.71,3.75,5.95;8,91.52,626.00,19.44,13.00;8,110.96,626.71,3.75,5.95;8,114.71,628.51,131.30,9.16;8,60.00,640.68,186.02,9.16;8,60.00,652.85,186.01,9.16;8,60.00,665.01,186.01,9.16;8,258.00,74.50,147.87,13.00;8,405.87,75.21,3.75,5.95">
  </figDesc>
  
  <table></table>
  </figure>

<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_1" validated="false" coords="9,258.00,395.28,186.04,278.89"><head>SE 񮽙 0.03, CI 95 񮽙 0.69 to 0.81) as those with values at or above the median (ES 񮽙 0.75, SE 񮽙 0.02, CI 95 񮽙 0.70 to 0.79). The obtained reliable difference values were z 񮽙 0, p 񮽙 1.69, SE 񮽙 0.03, CI 95 񮽙 0.63 to 0.73; n 񮽙 13). Further analysis by grade level re- vealed that middle and high school students (ES 񮽙 0.83, SE 񮽙 0.08, CI 95 񮽙 0.68 to 0.98) benefit from the use of rewards more than elementary school students (ES 񮽙 0.70, SE 񮽙 0.03, CI 95 񮽙 0.65 to 0.75). Reliable difference values were z 񮽙 4.44, p 񮽙 .001.</head><label></label><figDesc coords="9,353.68,395.28,90.32,9.16;9,258.00,407.37,186.01,9.16;9,258.00,419.47,186.00,9.16;9,258.00,429.05,78.51,13.00;9,336.51,436.25,7.50,5.95;9,346.69,429.05,97.30,13.00;9,258.00,443.65,186.00,9.16;9,258.00,453.23,110.03,13.00;9,368.03,460.43,7.50,5.95;9,378.41,453.23,65.57,13.00;9,258.00,467.82,186.00,9.16;9,258.00,477.39,57.19,13.00;9,295.70,545.78,74.18,13.00;9,369.87,552.99,7.50,5.95;9,380.56,545.79,63.44,13.00;9,258.00,557.86,186.00,13.00;9,258.00,572.45,186.02,9.16;9,258.00,582.02,111.53,13.00;9,369.53,589.23,7.50,5.95;9,380.16,582.03,63.82,13.00;9,258.00,596.61,186.01,9.16;9,258.00,606.18,186.00,13.00;9,258.00,618.26,63.99,13.00;9,321.99,625.47,7.50,5.95;9,334.34,618.26,109.67,13.00;9,258.00,630.34,177.69,13.00;9,282.00,652.48,162.04,9.61;9,258.00,665.01,186.02,9.16;10,60.00,641.01,178.51,9.16;10,238.51,645.71,7.50,5.95;10,60.00,650.50,186.02,13.00;10,60.00,665.01,186.01,9.16;10,258.00,641.01,178.51,9.16;10,436.51,645.71,7.50,5.95;10,258.00,650.50,186.01,13.00;10,258.00,665.01,186.00,9.16">) had an Inter Quartile Range of 480. Studies with a dosage value below the median had the same effect size (.75,Disability/at-risk status. Studies in- volving students identified with or at risk for disabilities had an effect size of 0.76 (CI 95 񮽙 0.72 to 0.79). By comparison, studies that did not involve students with or at risk for disabilities had an effect size of 0.65 (CI 95 񮽙 0.51 to 0.79). The effect size for students with LD and EBD was 0.75 and 0.76, respec-</figDesc><table coords="9,258.00,497.47,186.02,61.32">Use of rewards. Studies (n 񮽙 13) that 
employed the use of rewards (e.g., rewards vs. 
no rewards) had a higher ES (.75, SE 񮽙 0.02, 
CI 95 񮽙 0.71 to 0.79) than those that did not 
(ES 񮽙 0.</table></figure>
<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_2" validated="false" coords="11,60.00,313.82,185.50,200.98"><head>Table 2 Reliable</head><label>2</label><figDesc coords="11,134.06,313.82,37.89,10.37;11,81.82,326.82,39.73,10.37"></figDesc><table coords="11,60.00,326.82,185.50,187.98">Difference Between 
Moderator Levels 

Moderator 

Effect 
Size 

Standard 
...
.03 
4.44* 
.001 

Note. *p 񮽙 .001, two-tailed test. 

</table></figure>
<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_3" validated="false" coords="11,60.00,76.32,384.02,210.63"><head>Table 1 Summary of Effect Size Results for Moderator Variables Moderator k (Studies) n (Participants)</head><label>1</label><figDesc coords="11,233.06,76.32,37.89,10.37;11,112.40,89.32,279.27,10.37;11,71.75,135.71,37.99,8.24;11,148.36,124.71,4.00,8.14;11,134.34,135.71,32.50,8.24;11,201.45,124.71,4.50,8.14;11,179.69,135.71,48.49,8.24"></figDesc><table coords="11,60.00,113.21,384.02,173.74">Mean 
Effect Size 

Standard 
Error 

95% CI 

Lower Limit Upper Limit 

Grade level 
Elementary 
12 
136 
.69 
....

.51 
.79 </table></figure>
		</body>
		*/
	


		/**
		<back>
			<div type="references">

				<listBibl>

<biblStruct coords="14,258.00,567.41,186.02,7.33;14,268.00,576.41,176.01,7.33;14,268.00,585.41,176.02,7.33;14,268.00,594.41,176.02,7.33;14,268.00,603.41,42.22,7.33" xml:id="b0">
	<analytic>
		<title level="a" type="main">WINPEPI updated: Computer programs for epidemiologists, and their teaching potential</title>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">J</forename><forename type="middle">H</forename><surname>Abramson</surname></persName>
		</author>
		<ptr target="http://www.epiperspectives.com/content/8/1/1" />
	</analytic>
	<monogr>
		<title level="j">Epidemiologic Perspectives &amp; Innovations</title>
		<imprint>
			<biblScope unit="volume">8</biblScope>
			<biblScope unit="issue">1</biblScope>
			<date type="published" when="2011" />
		</imprint>
	</monogr>
</biblStruct>

<biblStruct coords="14,258.00,639.41,186.02,7.33;14,268.00,648.41,176.01,7.33;14,268.00,657.41,176.01,7.33;14,268.00,666.41,108.27,7.33" xml:id="b2">
	<analytic>
		<title level="a" type="main">Probabilistic index: An intuitive non-parametric approach to measuring the size of treatment effects</title>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">L</forename><surname>Acion</surname></persName>
		</author>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">J</forename><forename type="middle">J</forename><surname>Peterson</surname></persName>
		</author>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">S</forename><surname>Temple</surname></persName>
		</author>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">S</forename><surname>Amdt</surname></persName>
		</author>
	</analytic>
	<monogr>
		<title level="j">Statistics in Medicine</title>
		<imprint>
			<biblScope unit="volume">25</biblScope>
			<biblScope unit="page" from="591" to="602" />
			<date type="published" when="2006" />
		</imprint>
	</monogr>
</biblStruct>


				</listBibl>
			</div>
		</back>
		
	</text>	 */

	private HtmlElement processText(Element element) {
		HtmlDiv bodyBack = createEmptyDivWithClass(element);
		bodyBack.setClassAttribute(BODY_BACK);
		Elements childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlDiv div = null;
			if (tag.equals(BODY)) {
				div = processBody(childElement);
			} else if (tag.equals(BACK)) {
				div = processBack(childElement);
			} else {
				div = processUnknown(childElement, BODY_BACK);
			}
			bodyBack.appendChild(div);
		}
		return bodyBack;
	}


	/**
		<body>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Single-Case Research, Effect Size, and Confidence Intervals</head><p>Single-case research methods can "provide a rigorous experimental evaluation" of the efficacy of an intervention ( <ref type="bibr" coords="2,189.00,568.47,57.02,9.16;2,60.00,580.54,59.42,9.16">Kratochwill et al., 2010, p. 2)</ref>. As such, single-case research has been used to identify a range of interventions used in schools, as this method of inquiry can help identify practices that are evidencebased ( <ref type="bibr" coords="2,90.58,628.81,73.66,9.16" target="#b25">Horner et al., 2005</ref>). The use of effect size in single-case research allows for a determination of the size or magnitude of academic or behavioral change. Determining the size of the effect, as well as a functional relation, is critical in light of accountability for instructional practices and multitier models of early intervention (see Council for Exceptional <ref type="bibr" coords="2,258.00,125.34,64.66,9.16">Children, 2008</ref>; National Association of <ref type="bibr" coords="2,258.00,137.42,113.47,9.16">School Psychologists, 2010)</ref>.</p><p>Data from single-case studies of schoolbased practices are being summarized more as new methods are being developed that can address positive baseline trends and that require few assumptions about the data <ref type="bibr" coords="2,412.09,197.82,31.93,9.16;2,258.00,209.90,139.28,9.16" target="#b52">(Parker, Vannest, Davis, &amp; Sauber, 2011)</ref>. Although many studies using single-case research designs may be found in the peer tutoring literature, neither individual nor aggregated effect sizes with corresponding confidence intervals have been published to date. This is a significant shortcoming, as effect sizes aid in summarizing data across studies. Further, confidence intervals are needed for accurate interpretation of effect size data <ref type="bibr" coords="2,381.11,318.62,62.90,9.16" target="#b13">(Cooper, 2011;</ref><ref type="bibr" coords="2,258.00,330.70,82.44,9.16" target="#b30">Hunter et al., 1982;</ref><ref type="bibr" coords="2,344.59,330.70,67.43,9.16" target="#b61">Thompson, 2002</ref><ref type="bibr" coords="2,420.67,330.70,18.66,9.16" target="#b62">Thompson, , 2007</ref> and are required by the American Psychological Association (APA; American Psychological <ref type="bibr" coords="2,278.37,366.94,78.43,9.16">Association, 2010;</ref><ref type="bibr" coords="2,362.72,366.94,81.30,9.16;2,258.00,379.02,162.51,9.16">Wilkinson &amp; APA Task Force on Statistical Inference, 1999</ref>). An effect size with confidence intervals tells about the relative size of an effect compared to other treatments, and provides a standard metric for comparison and aggregation. Further, in an era of evidence-based practices, it provides data that are more readily understood. The use of a common effect size metric with single-case research, as with group designs, is essential to allow for the aggregation of results across studies.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Effect Size Metrics Used in Previous Single-Case Research</head><p>There are at least eight commonly used nonoverlap effect size metrics (indices) in single-case research. They include percentage of nonoverlapping data <ref type="bibr" coords="2,350.04,592.62,26.11,9.16">(PND;</ref><ref type="bibr" coords="2,382.45,592.62,61.57,9.16;2,258.00,604.69,103.30,9.16">Scruggs, Mas- tropieri, &amp; Casto, 1987)</ref>, percentage of all nonoverlapping data <ref type="bibr" coords="2,345.04,616.75,33.33,9.16">(PAND;</ref><ref type="bibr" coords="2,382.18,616.75,61.84,9.16;2,258.00,628.82,104.95,9.16" target="#b50">Parker, Hagan- Burke, &amp; Vannest, 2007)</ref>, nonoverlap of all pairs <ref type="bibr" coords="2,279.65,640.89,26.11,9.16">(NAP;</ref><ref type="bibr" coords="2,307.98,640.89,131.75,9.16" target="#b51">Parker, Vannest, &amp; Brown, 2009)</ref>, extended celeration line (ECL; <ref type="bibr" coords="2,386.83,652.96,57.19,9.16;2,258.00,665.03,35.04,9.16">White &amp; Har- ing, 1980</ref>), improvement rate difference (IRD; <ref type="bibr" coords="3,60.00,77.01,71.19,9.16" target="#b51">Parker et al., 2009</ref>), percent of data exceeding the median (PEM; <ref type="bibr" coords="3,140.24,89.26,37.02,9.16" target="#b39">Ma, 2006</ref>), Pearson's Phi ( <ref type="bibr" coords="3,64.20,101.51,76.29,9.16" target="#b50">Parker et al., 2007)</ref>, and Kendall's TauU for nonoverlap between groups with baseline trend control (Tau novlap ; Parker, Vannest, <ref type="bibr" coords="3,231.01,126.01,14.99,9.16;3,60.00,138.26,84.30,9.16">Da- vis, &amp; Sauber, 2011)</ref>. Although a full review of these indices is beyond the scope of this article (see <ref type="bibr" coords="3,106.85,162.76,71.39,9.16" target="#b52">Parker et al., 2011</ref>), a brief review of limitations of each compared to TauU follows.</p><p>Although not intended by its originators as an effect size, PND has been used this way in several meta-analyses. Limitations of PND include its unknown distribution qualities (with the consequent inability to provide a standard error or confidence intervals) and its insensitivity to treatment effects. As such, it is not recommended for meta-analyses (see <ref type="bibr" coords="3,232.69,285.26,13.33,9.16;3,60.00,297.51,92.74,9.16">Al- lison &amp; Gorman, 1993;</ref><ref type="bibr" coords="3,155.23,297.51,76.75,9.16" target="#b25">Horner et al., 2005)</ref>. A limitation of PND, PAND, NAP, IRD, PEM, and Phi, is that they cannot control for positive baseline trend. Of the eight methods, only ECL and TauU can do that. However, ECL controls for only linear trend, whereas TauU controls for any shape of increase, known as monotonic trend. The weakest statistical power among these eight indices is shown by PEM, followed by ECL (it cannot be ascertained with PND). Medium statistical power is obtained by Phi and IRD; strongest statistical power is by TauU and NAP. Limitations of ECL and PEM also include their assumption that the median value is a reliable summary of Phase A. This assumption is only correct for data sets demonstrating measures of central tendency. TauU does not make this assumption.</p><p>Another criterion for a good effect size is that it should discriminate among results from different studies. A nondiscriminating index will lump all low and/or all high results together. Worst discrimination is shown by PEM, followed by PND. Best discrimination is by TauU, with baseline trend control. Phi also has good discriminating ability. TauU is also robust to autocorrelation (with little impact on standard error; <ref type="bibr" coords="3,160.97,628.26,76.42,9.16" target="#b52">Parker et al., 2011</ref>). Finally, TauU is well suited to very short phases. It does not require the minimum four to six expected data points per cell that nonparametric methods based on cross-tabulation do (e.g., IRD, Phi).</p><p>The limitations of TauU include it being a relatively new effect size measure, so there are relatively few publications from which its sizes can be judged. A second limitation is shared by all nonoverlap indices but Phi: nonoverlap effect sizes are not directly comparable to the familiar Pearson's r or <ref type="bibr" coords="3,410.68,173.65,33.33,9.16;3,258.00,185.73,26.66,9.16" target="#b10">Cohen's (1988)</ref> d. However, TauU's strengths (statistical power, low N requirement, distributionfree, good discrimination ability among studies, control of unwanted baseline trend, a known sampling distribution) are such that it appears to be the best nonparametric index at present and is even competitive with parametric indices.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Results</head></div>
...
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Discussion</head><p>This meta-analysis is the first peer tutoring meta-analysis to examine achievement outcomes for elementary and secondary students across peer tutoring studies using singlecase research designs. The overall effect size was found to be moderately large, indicating that greater academic gains were achieved by students engaged in peer tutoring interventions than nonpeer tutoring instructional arrangements.</p><p>Moderator analyses revealed a statistically significant effect for the use of rewards. Peer tutoring interventions that used rewards had a larger effect size than those that did not. This finding points to the importance of the use of reward on academic outcomes, especially for middle and high school students. Findings from several peer tutoring studies support its use with older students as a means of motivation. This seems to be the case particularly for students who have experienced academic difficulties (see <ref type="bibr" coords="11,366.27,628.57,77.76,9.16;11,258.00,640.72,41.97,9.16" target="#b7">Bowman-Perrott et al., 2007;</ref><ref type="bibr" coords="11,306.95,640.72,91.47,9.16" target="#b35">Kamps et al., 2008;</ref><ref type="bibr" coords="11,405.39,640.72,38.61,9.16;11,258.00,652.87,116.80,9.16" target="#b46">Mitchem, Young, West, &amp; Benyo, 2001</ref>). This finding is also consistent with previous research at the  elementary school level (e.g., <ref type="bibr" coords="12,181.87,77.01,64.13,9.16;12,60.00,89.01,21.53,9.16" target="#b55">Rohrbeck et al., 2003)</ref>, as the use of reward contingencies produced a statistically significant effect. In four studies, data for elementary and secondary students were not disaggregated. The two studies from <ref type="bibr" coords="12,148.05,137.01,97.96,9.16" target="#b22">Greenwood et al. (1984)</ref> reported combined data for students in Grades 3-6, <ref type="bibr" coords="12,82.39,161.01,121.61,9.16" target="#b45">Mayfield and Vollmer (2007)</ref> presented data for students in Grades 3-11, and the spelling class data from <ref type="bibr" coords="12,157.36,185.01,88.67,9.16;12,60.00,197.01,26.66,9.16" target="#b7">Bowman-Perrott et al. (2007)</ref> were reported for students in Grades 5-12 together. Mayfield and Vollmer implemented peer tutoring in group homes and students' homes. <ref type="bibr" coords="12,122.13,233.01,123.89,9.16" target="#b7">Bowman-Perrott et al. (2007)</ref> conducted peer tutoring in an alternative school that was part of a residential treatment facility. In these instances, the way students were grouped in their natural learning environments did not follow traditional grade-level groupings. Data for these studies were not included in grade-level analyses because they would not provide insight into the effect of peer tutoring on students at the elementary versus secondary level. Results demonstrated that peer tutoring was effective for both elementary and secondary students, and that grade level did not moderate its effectiveness. Although individual grade level analyses could not be conducted because data were not disaggregated by grade level in the majority of the studies, participants tended to represent certain grade levels. For studies involving elementary school students, the average grade level was fourth grade, followed by third grade. Studies focusing on secondary students tended to include sixth-graders most often, followed by ninth-graders.</p><p>Consistent with the <ref type="bibr" coords="12,176.61,521.01,69.40,9.16;12,60.00,533.01,26.66,9.16" target="#b55">Rohrbeck et al. (2003)</ref> study, the findings of the current metaanalysis showed no difference in student outcomes for studies with dosage amounts above and below the median value. Perhaps the core components of peer tutoring (e.g., increased opportunities to respond, error correction procedures) are sufficient to make an impact on student outcomes with as few as 280 min of exposure to the intervention and as many as just over 1,000 min. The finding that students with or at risk for disabilities demonstrated greater academic gains than their peers without disabilities or at-risk status may be reflective of the benefit students received from the additional support (e.g., more opportunities to respond) afforded by peer tutoring. This may be especially likely, as all of these students were identified as being below grade level in a given content area.</p><p>Twenty-three of the 26 studies included participants with identified disabilities or who were determined to be at risk for being identified as having a disability because of poor academic performance. Although differences were not statistically significant, practical significance can be attributed to the finding that the effect size was larger for students with or at risk for disabilities (.76) than for students without disabilities or who were not at risk <ref type="bibr" coords="12,258.00,281.01,18.05,9.16">(.65)</ref>. Results support evidence that aspects of peer tutoring interventions such as repetition of key concepts and opportunities to respond are particularly beneficial for students in need of additional academic supports. Of the 23 studies that included students with or at risk for disabilities, only 11 disaggregated achievement outcomes by disability category. With regard to disability status, only data for students identified as having a LD or EBD as their primary disability were analyzed, as only one study disaggregated data for students with autism, one for students with MR, and one for students with other health impairments. It is important to note, however, that the number of studies for analyses of students with LD and EBD were small; caution should be used in considering these results.</p><p>Ten studies focused on reading, six on spelling, six on math, three on vocabulary, and three on social studies. Spelling outcomes were measured by students' correct spelling of words and the percentage of words capitalized correctly. Reading measures included (a) nonsense word fluency, (b) errors per minute, (c) sight word acquisition, and (d) comprehension. Math measures consisted of (a) correctly multiplying decimals, (b) changing decimals to fractions, (c) calculating percentages, and (d) adding and subtracting time. Vocabulary included the percent of vocabulary words correct. Finally, social studies included history; specific learning outcomes were not reported. <ref type="bibr" coords="12,60.00,34.48,184.49,8.18">School Psychology Review, 2013, Volume 42, No. 1</ref> In the present analysis, reading yielded a large to moderate effect size (ES 񮽙 0.77), compared with the effect sizes reported by <ref type="bibr" coords="13,196.09,101.17,49.91,9.16;13,60.00,113.25,26.66,9.16" target="#b11">Cohen et al. (1982)</ref> of 0.29, <ref type="bibr" coords="13,124.41,113.25,74.69,9.16" target="#b12">Cook et al. (1985)</ref> of 0.30 for tutors and 0.49 for tutees, and <ref type="bibr" coords="13,184.31,125.33,61.71,9.16;13,60.00,137.41,26.66,9.16" target="#b55">Rohrbeck et al. (2003)</ref> at 0.26 for reading. The effect size obtained for math in this meta-analysis (ES 񮽙 0.86) was also larger than that reported by <ref type="bibr" coords="13,72.49,173.65,76.63,9.16" target="#b11">Cohen et al. (1982;</ref><ref type="bibr" coords="13,151.62,171.14,44.42,13.00">ES 񮽙 0.60)</ref>, <ref type="bibr" coords="13,202.42,173.65,43.59,9.16;13,60.00,185.73,26.11,9.16" target="#b12">Cook et al. (1985;</ref><ref type="bibr" coords=""></ref> ES 񮽙 0.67 and 0.85 for tutors and tutees, respectively), and <ref type="bibr" coords="13,175.88,197.81,70.13,9.16;13,60.00,209.89,26.11,9.16" target="#b55">Rohrbeck et al. (2003;</ref><ref type="bibr" coords="13,90.72,207.38,48.67,13.00">ES 񮽙 0.22)</ref>. Although social studies had a smaller effect in this meta-analysis (ES 񮽙 0.57), it was larger than that reported by <ref type="bibr" coords="13,74.40,246.13,95.12,9.16" target="#b55">Rohrbeck et al. (2003;</ref><ref type="bibr" coords="13,173.92,243.62,48.23,13.00">ES 񮽙 0.49)</ref>. The obtained moderate effect size for spelling (ES 񮽙 0.74) was larger than those reported by Cook et al. (ES 񮽙 0.01 and 0.51 for tutors and tutees, respectively) and <ref type="bibr" coords="13,174.88,294.45,71.13,9.16;13,60.00,306.53,26.66,9.16" target="#b55">Rohrbeck et al. (2003)</ref> (ES 񮽙 0.21). Finally, vocabulary had a large effect (ES 񮽙 0.92). Because previous meta-analysis reported data for writing, language, literacy, or a combination of related content areas, there is no vocabulary effect size with which to compare the present findings. The effect sizes for content areas should be considered with caution, as a small number of studies were available for analysis. This is particularly true for vocabulary and social studies.</p><p>As previously mentioned, treatment fidelity data were reported in 16 of the 26 studies (62%). <ref type="bibr" coords="13,103.34,463.50,89.60,9.16" target="#b55">Rohrbeck et al. (2003)</ref> reported that 68% of studies in their meta-analysis reported these data. It is important to consider the potential impact of treatment fidelity on study outcomes. It was not examined as a potential moderator because fidelity was high in the studies that reported it. Therefore, comparing studies with low fidelity to those with high fidelity was not possible. It is important to report these data to help understand the degree to which teachers and students accurately implement peer tutoring interventions.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Limitations</head><p>The findings of this meta-analysis should be considered in light of the following limitations. One limitation is the lack of disaggregated disability data in some studies, limiting our sample for these analyses. Similarly, data were not disaggregated by grade level in most of the articles, so could not present results and recommendations by grade level. Another limitation is the potential variability that was introduced by how academic gains were measured across studies (e.g., curriculum-based measures vs. standardized tests) and peer tutoring type (e.g., cross-age vs. same age). A third potential limitation is that the effect sizes for the content areas may change with a larger pool of studies (especially for vocabulary and social studies). A final limitation is that caution should be used in comparing TauU to Cohen's d effect sizes. For example, the conversion from Cohen's d to TauU for the <ref type="bibr" coords="13,326.35,282.38,82.37,9.16" target="#b35">Kamps et al. (2008)</ref> study is an approximation. Future research can help address some of these limitations to further add to the peer tutoring literature.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Implications for Research</head><p>The findings of this meta-analysis underscore several recommendations that can inform future research. The first is the need to report treatment fidelity in peer tutoring studies. Knowing whether high versus low levels of fidelity promote greater academic gains would be beneficial in informing practice. The second is that treatment fidelity could serve as a moderator of student outcomes. This should be investigated by grade and across content areas. In addition, it would be helpful to addresses practical questions such as the following: (a) Is there a minimum number of hours or sessions needed for students to gain the most benefit from peer tutoring? (b) Does the type of academic outcome measurement (e.g., criterion-referenced vs. norm-referenced) moderate the effect of peer tutoring? (c) Does the comorbidity of LD and EBD affect student outcomes? (d) What do outcomes look like for students with autism and other disabilities? The last question is important, as data in these analyses were limited to students with LD and EBD because the number of cases for students with other disabilities (e.g., autism) was too small to investigate.</p><p>The peer tutoring literature would also benefit from studies that disaggregate data by grade (e.g., first grade) because the effects of peer tutoring, and moderators of those effects, may vary within each of the grades. Analyzing data by grade level (viz, elementary or secondary) would be beneficial. For example, it would be helpful to know whether first-graders may benefit more than fifth-graders. This would also prove useful in further analyzing potential moderators. For instance, the use of rewards may be significant with sixth-graders but not eighth-grade students.</p><p>Another recommendation is that future single-case peer tutoring studies should apply strong designs in keeping with recent standards (e.g., <ref type="bibr" coords="14,109.87,270.29,100.65,9.16" target="#b37">Kratochwill et al., 2010)</ref>. Unfortunately, 11 single-case studies were excluded from these analyses because of weak designs (n 񮽙 6) and because interobserver agreement standards were not met (n 񮽙 5; <ref type="bibr" coords="14,188.33,318.61,57.69,9.16;14,60.00,330.69,34.20,9.16" target="#b37">Kratochwill et al., 2010</ref>). Among those excluded for these reasons were the two studies that focused on science in middle and high school classrooms. Thus, there is a need to investigate students' outcomes in this core content area. Further, more studies are needed across content areas, as the small number of studies in some of the content areas prevented a thorough analysis of students' academic outcomes.</p><p>Future research should examine the relation between academic and behavioral outcomes for students engaged in peer tutoring. This would be particularly useful in light of the finding that students with EBD benefitted most from peer tutoring. It would be interesting to examine the benefit students receive from peer tutoring with regard to behaviors. Finally, as a new effect size measure, this meta-analysis should be replicated using TauU as new single-case studies investigating peer tutoring are published. Given the many advantages of TauU, it holds great promise for aggregating single-case research data.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Implications for Practice</head><p>This meta-analysis provides evidence for the use of peer tutoring as an evidencebased instructional practice based on the most current, high-quality standards for single-case research. Social validity data across peer tutoring studies revealed that teachers find it easy to implement within their existing teaching routine and structure. As such, teacher training programs and in-service training for practicing teachers should include peer tutoring. This is particularly important in an era of increased accountability for implementing evidence-based practices and the implementation of multitiered early intervention supports.</p><p>Peer tutoring is an effective intervention for students with disabilities. This is especially noteworthy for students with EBD who have been consistently identified in the literature as performing below grade level, and for whom academic deficits are part of the federal definition and criteria for identification in Individuals with <ref type="bibr" coords="14,300.86,293.02,139.00,9.16">Disabilities Education Act (2004)</ref>. Problem behaviors, a characteristic of students with EBD, adversely affect academic performance (see Individuals with Disabilities Education <ref type="bibr" coords="14,287.24,341.02,43.27,9.16">Act, 2004)</ref>. Results showed that students with EBD, who by nature of their disability demonstrate problem behaviors, are most likely to benefit academically from a peer tutoring instructional format. It is an intervention that is highly recommended for their peers without disabilities as well.</p><p>Peer tutoring is effective in promoting academic gains across content areas, and is effective for elementary, middle, and high school students. The use of rewards appear to benefit older students as a motivator.</p></div>
<div xmlns="http://www.tei-c.org/ns/1.0"><head>Footnotes</head>
<p>*References marked with an asterisk indicate studies included in the meta-analysis.</p>
<p>Lisa Bowman-Perrott, PhD, is an assistant professor in the Department of Educational Psychology, Special Education, at Texas A&amp;M University. Her primary research interests are academic and behavioral interventions for students with or at risk for emotional and behavioral disorders, including peer tutoring.</p>
<p>Heather S. Davis is a doctoral student in the Special Education program at Texas A&amp;M University. Her research interests are identifying and providing evidence-based behavioral interventions to children exhibiting challenging behavior and training teachers and parents on implementing evidence-based behavioral interventions in school and home settings.</p>
<p>Kimber J. Vannest, PhD, is an associate professor in the Department of Educational Psychology, Special Education, at Texas A&amp;M University. Her research interests are in determining effective interventions for children and youth with or at risk for emotional and behavioral disorders, including teacher behaviors and measurement.</p>
<p>Lauren Williams, MEd, is a special education teacher for students with communication, academic, and social learning deficits. Her primary interest is in working with students with emotional and behavioral disorders; she is working toward certification as a boardcertified behavior analyst.</p>
<p>Charles R. Greenwood, PhD, is director of the Juniper Gardens Children's Project (JGCP), senior scientist in the Schiefelbusch Institute for Life Span Studies, and a professor in the Department of Applied Behavioral Science and the Department of Special Education at the University of Kansas. He is the developer of ClassWide Peer Tutoring, a class-wide instructional approach for teaching basic academic skills.</p>
<p>Richard Parker, PhD, is recently retired from Texas A&amp;M University Educational Psychology Department. His continued research interest is single-case research methodology.</p></div>
<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_0" validated="false" coords="8,60.00,74.50,349.62,599.67">
  <head>'s d 񮽙 (M 1 񮽙 M 2 )/񮽙 pooled where 񮽙pooled 񮽙 񮽙[(񮽙1 2 񮽙񮽙2 2 ) / 2]. The Cohen's d effect size was also obtained from WinPepi along with Cohen's d SE (Abramson, 2011). Second, Co- hen's d was transformed to Tau using the formula Tau 񮽙 1 񮽙 (1 񮽙 d/3.464) 2</head>
  <label></label>
  <figDesc coords="8,236.49,604.17,9.52,9.16;8,60.00,613.83,35.88,13.00;8,95.88,621.04,3.75,5.95;8,105.08,613.83,22.11,13.00;8,127.18,621.04,3.75,5.95;8,130.93,616.34,12.22,9.16;8,143.15,621.04,20.42,5.95;8,169.01,613.83,76.98,13.00;8,60.00,626.00,27.77,13.00;8,87.77,626.71,3.75,5.95;8,91.52,626.00,19.44,13.00;8,110.96,626.71,3.75,5.95;8,114.71,628.51,131.30,9.16;8,60.00,640.68,186.02,9.16;8,60.00,652.85,186.01,9.16;8,60.00,665.01,186.01,9.16;8,258.00,74.50,147.87,13.00;8,405.87,75.21,3.75,5.95">
  </figDesc>
  
  <table></table>
  </figure>

<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_1" validated="false" coords="9,258.00,395.28,186.04,278.89"><head>SE 񮽙 0.03, CI 95 񮽙 0.69 to 0.81) as those with values at or above the median (ES 񮽙 0.75, SE 񮽙 0.02, CI 95 񮽙 0.70 to 0.79). The obtained reliable difference values were z 񮽙 0, p 񮽙 1.69, SE 񮽙 0.03, CI 95 񮽙 0.63 to 0.73; n 񮽙 13). Further analysis by grade level re- vealed that middle and high school students (ES 񮽙 0.83, SE 񮽙 0.08, CI 95 񮽙 0.68 to 0.98) benefit from the use of rewards more than elementary school students (ES 񮽙 0.70, SE 񮽙 0.03, CI 95 񮽙 0.65 to 0.75). Reliable difference values were z 񮽙 4.44, p 񮽙 .001.</head><label></label><figDesc coords="9,353.68,395.28,90.32,9.16;9,258.00,407.37,186.01,9.16;9,258.00,419.47,186.00,9.16;9,258.00,429.05,78.51,13.00;9,336.51,436.25,7.50,5.95;9,346.69,429.05,97.30,13.00;9,258.00,443.65,186.00,9.16;9,258.00,453.23,110.03,13.00;9,368.03,460.43,7.50,5.95;9,378.41,453.23,65.57,13.00;9,258.00,467.82,186.00,9.16;9,258.00,477.39,57.19,13.00;9,295.70,545.78,74.18,13.00;9,369.87,552.99,7.50,5.95;9,380.56,545.79,63.44,13.00;9,258.00,557.86,186.00,13.00;9,258.00,572.45,186.02,9.16;9,258.00,582.02,111.53,13.00;9,369.53,589.23,7.50,5.95;9,380.16,582.03,63.82,13.00;9,258.00,596.61,186.01,9.16;9,258.00,606.18,186.00,13.00;9,258.00,618.26,63.99,13.00;9,321.99,625.47,7.50,5.95;9,334.34,618.26,109.67,13.00;9,258.00,630.34,177.69,13.00;9,282.00,652.48,162.04,9.61;9,258.00,665.01,186.02,9.16;10,60.00,641.01,178.51,9.16;10,238.51,645.71,7.50,5.95;10,60.00,650.50,186.02,13.00;10,60.00,665.01,186.01,9.16;10,258.00,641.01,178.51,9.16;10,436.51,645.71,7.50,5.95;10,258.00,650.50,186.01,13.00;10,258.00,665.01,186.00,9.16">) had an Inter Quartile Range of 480. Studies with a dosage value below the median had the same effect size (.75,Disability/at-risk status. Studies in- volving students identified with or at risk for disabilities had an effect size of 0.76 (CI 95 񮽙 0.72 to 0.79). By comparison, studies that did not involve students with or at risk for disabilities had an effect size of 0.65 (CI 95 񮽙 0.51 to 0.79). The effect size for students with LD and EBD was 0.75 and 0.76, respec-</figDesc><table coords="9,258.00,497.47,186.02,61.32">Use of rewards. Studies (n 񮽙 13) that 
employed the use of rewards (e.g., rewards vs. 
no rewards) had a higher ES (.75, SE 񮽙 0.02, 
CI 95 񮽙 0.71 to 0.79) than those that did not 
(ES 񮽙 0.</table></figure>
<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_2" validated="false" coords="11,60.00,313.82,185.50,200.98"><head>Table 2 Reliable</head><label>2</label><figDesc coords="11,134.06,313.82,37.89,10.37;11,81.82,326.82,39.73,10.37"></figDesc><table coords="11,60.00,326.82,185.50,187.98">Difference Between 
Moderator Levels 

Moderator 

Effect 
Size 

Standard 
...
.03 
4.44* 
.001 

Note. *p 񮽙 .001, two-tailed test. 

</table></figure>
<figure xmlns="http://www.tei-c.org/ns/1.0" type="table" xml:id="tab_3" validated="false" coords="11,60.00,76.32,384.02,210.63"><head>Table 1 Summary of Effect Size Results for Moderator Variables Moderator k (Studies) n (Participants)</head><label>1</label><figDesc coords="11,233.06,76.32,37.89,10.37;11,112.40,89.32,279.27,10.37;11,71.75,135.71,37.99,8.24;11,148.36,124.71,4.00,8.14;11,134.34,135.71,32.50,8.24;11,201.45,124.71,4.50,8.14;11,179.69,135.71,48.49,8.24"></figDesc><table coords="11,60.00,113.21,384.02,173.74">Mean 
Effect Size 

Standard 
Error 

95% CI 

Lower Limit Upper Limit 

Grade level 
Elementary 
12 
136 
.69 
....

.51 
.79 </table></figure>
		</body>
		
	 * @param bodyElement
	 */
	private HtmlDiv processBody(Element element) {
		Elements childElements = element.getChildElements();
		HtmlDiv div = createEmptyDivWithClass(element);
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement divSpan;
			if (tag.equals(DIV)) {
				divSpan = processDiv(childElement);
			} else if (tag.equals(FIGURE)) {
				divSpan = processFigure(childElement);
			} else if (tag.equals(NOTE)) {
				divSpan = processNote(childElement);
			} else {
				divSpan = createDivWithValue(childElement);
			}
			div.appendChild(divSpan);
		}
		return div;
	}

	private HtmlDiv processDiv(Element element) {
		Elements childElements = element.getChildElements();
		HtmlDiv div = new HtmlDiv();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement divSpan;
			if (tag.equals(HEAD)) {
				divSpan = processDivHead(childElement);
			} else if (tag.equals(P)) {
				divSpan = processP(childElement);
			} else if (tag.equals(FORMULA)) {
				divSpan = processFormula(childElement);
			} else {
				divSpan = createDivWithValue(childElement);
			}
			div.appendChild(divSpan);
		}
		return div;
	}


	private HtmlElement processDivHead(Element element) {
		HtmlDiv div = new HtmlDiv();
		div.setClassAttribute("head");
		div.setValue(element.getValue());
		return div;
	}


	private HtmlElement processP(Element element) {
		HtmlElement pElement = new HtmlP(); 
		XMLUtil.copyAttributes(element, pElement);
		XMLUtil.transferChildren(element, pElement);
		return pElement;
	}

	private HtmlElement processFormula(Element element) {
		return createDivWithValue(element);
	}

	private HtmlElement processFigure(Element element) {
		return createDivWithValue(element);
	}

	/**
		<back>
			<div type="references">

				<listBibl>

<biblStruct coords="14,258.00,567.41,186.02,7.33;14,268.00,576.41,176.01,7.33;14,268.00,585.41,176.02,7.33;14,268.00,594.41,176.02,7.33;14,268.00,603.41,42.22,7.33" xml:id="b0">
	<analytic>
		<title level="a" type="main">WINPEPI updated: Computer programs for epidemiologists, and their teaching potential</title>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">J</forename><forename type="middle">H</forename><surname>Abramson</surname></persName>
		</author>
		<ptr target="http://www.epiperspectives.com/content/8/1/1" />
	</analytic>
	<monogr>
		<title level="j">Epidemiologic Perspectives &amp; Innovations</title>
		<imprint>
			<biblScope unit="volume">8</biblScope>
			<biblScope unit="issue">1</biblScope>
			<date type="published" when="2011" />
		</imprint>
	</monogr>
</biblStruct>

<biblStruct coords="14,258.00,639.41,186.02,7.33;14,268.00,648.41,176.01,7.33;14,268.00,657.41,176.01,7.33;14,268.00,666.41,108.27,7.33" xml:id="b2">
	<analytic>
		<title level="a" type="main">Probabilistic index: An intuitive non-parametric approach to measuring the size of treatment effects</title>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">L</forename><surname>Acion</surname></persName>
		</author>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">J</forename><forename type="middle">J</forename><surname>Peterson</surname></persName>
		</author>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">S</forename><surname>Temple</surname></persName>
		</author>
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">S</forename><surname>Amdt</surname></persName>
		</author>
	</analytic>
	<monogr>
		<title level="j">Statistics in Medicine</title>
		<imprint>
			<biblScope unit="volume">25</biblScope>
			<biblScope unit="page" from="591" to="602" />
			<date type="published" when="2006" />
		</imprint>
	</monogr>
</biblStruct>


				</listBibl>
			</div>
		</back>
		
	* @param backElement
	 */

	private HtmlDiv processBack(Element backElement) {
		Elements childElements = backElement.getChildElements();
		HtmlDiv backDiv = new HtmlDiv();
		backDiv.setClassAttribute("back");
		HtmlElement divSpan;
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			if (tag.equals(DIV) && REFERENCES.equals(childElement.getAttributeValue(TYPE))) {
				divSpan = processReferences(childElement);
			} else if (tag.equals(HEAD)) {
				divSpan = processBackHead(childElement);
			} else if (tag.equals(LABEL)) {
				divSpan = processLabel(childElement);
			} else if (tag.equals(FIG_DESC)) {
				divSpan = processFigDesc(childElement);
			} else if (tag.equals(GRAPHIC)) {
				divSpan = processGraphic(childElement);
			} else if (tag.equals(TABLE)) {
				divSpan = processTable(childElement);
			} else if (tag.equals(NOTE)) {
				divSpan = processNote(childElement);
			} else if (tag.equals(DIV)) {
				divSpan = processDiv(childElement);
			} else {
				divSpan = processUnknown(childElement, "back");
			}
			backDiv.appendChild(divSpan);
		}
		return backDiv;
	}

	/**
	<listBibl>

<biblStruct coords="14,258.00,567.41,186.02,7.33;14,268.00,576.41,176.01,7.33;14,268.00,585.41,176.02,7.33;14,268.00,594.41,176.02,7.33;14,268.00,603.41,42.22,7.33" xml:id="b0">
<analytic>

				</listBibl>
			</div>
		</back>

	 */

	private HtmlElement processListBibl(Element element) {
		HtmlUl ul = new HtmlUl(); 
		Elements childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement el;
			if (tag.equals(BIBL_STRUCT)) {
				el = processBiblStruct(childElement);
			} else if (tag.equals(NOTE)) {
				el = processNote(childElement);
			} else {
				el = createDivWithValue(childElement);
				System.err.println("unknown tagin ListBibl: "+tag);
			}
			ul.appendChild(el);
		}
		return ul;
	}


	private HtmlLi processBiblStruct(Element biblStructElement) {
		HtmlLi li = new HtmlLi();
		Elements childElements = biblStructElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement divSpan;
			if (tag.equals(ANALYTIC)) {
				divSpan = processAnalytic(childElement);
			} else if (tag.equals(MONOGR)) {
				divSpan = processMonogr(childElement);
			} else if (tag.equals(NOTE)) {
				divSpan = processNote(childElement);
			} else {
				divSpan = createDivWithValue(childElement);
			}
			li.appendChild(divSpan);
		}
		return li;
	}

	private HtmlDiv processAnalytic(Element element) {
		HtmlDiv div = createEmptyDivWithClass(element);
		Elements childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement divSpan;
			if (tag.equals(TITLE)) {
				divSpan = createSpan(childElement);
			} else if (tag.equals(AUTHOR)) {
				divSpan = processAuthor(childElement);
			} else {
				divSpan = createDivWithValue(childElement);
			}
			div.appendChild(divSpan);
		}
		return div;
	}

	/**
		<author>
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">L</forename><surname>Acion</surname></persName>
		</author>
	 * @param childElement
	 */
	private HtmlElement processAuthor(Element authElement) {
		HtmlDiv div = new HtmlDiv();
		Elements childElements = authElement.getChildElements();
		
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement divSpan;
			if (tag.equals(PERS_NAME)) {
				divSpan = processPersName(childElement);
			} else if (tag.equals(AFFILIATION)) {
				divSpan = processAffiliation(childElement);
			} else {
				divSpan = processUnknownTag(tag, AUTHOR);
			}
			div.appendChild(divSpan);
		}
		return div;
	}


	/**
	<affiliation key="aff0">
		<orgName type="institution" key="instit1">Texas A&amp;M University</orgName>
		<orgName type="institution" key="instit2">University of Kansas</orgName>
		<orgName type="institution" key="instit3">Texas A&amp;M University</orgName>
	</affiliation>
	 * @param childElement
	 * @return
	 */
	private HtmlDiv processAffiliation(Element element) {
		HtmlDiv div = new HtmlDiv();
		Elements childElements = element.getChildElements();
		
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement divSpan;
			if (tag.equals(ORG_NAME)) {
				divSpan = processOrgName(childElement);
			} else {
				divSpan = processUnknownTag(tag, AUTHOR);
			}
			div.appendChild(divSpan);
		}
		return div;
	}
	
/**
	<orgName type="institution" key="instit2">University of Kansas</orgName>
*/

	private HtmlSpan processOrgName(Element element) {
		HtmlSpan span = createSpan(element);
		Elements childElements = element.getChildElements();
		if (childElements.size() > 0) {
			System.err.println("unexepected orgName children");
		} else {
			for (int i = 0; i < element.getAttributeCount(); i++) {
				Attribute att = element.getAttribute(i);
				String attname = att.getLocalName();
				String attval = att.getValue();
				HtmlSpan subSpan = new HtmlSpan();
				if (attname.equals(TYPE)) {
					subSpan.setClassAttribute(attname);
					subSpan.setValue(attname+":"+attval);
				} else if (attname.equals(KEY)) {
					subSpan.setClassAttribute(KEY);
					subSpan.setValue(attname+":"+attval);
				} else {
					subSpan = processUnknownAttribute(attname, element);
				}
				span.appendChild(subSpan);
			}
		}
		return span;
	}



	/**
			<persName xmlns="http://www.tei-c.org/ns/1.0"><forename type="first">L</forename><surname>Acion</surname></persName>
	 * @param persnameElement
	 * @return
	 */

	private HtmlSpan processPersName(Element persnameElement) {
		HtmlSpan span = new HtmlSpan();
//		System.out.println("PERSNAME "+persnameElement.toXML());
		Elements childElements = persnameElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlSpan subSpan = new HtmlSpan();
			if (tag.equals(FORENAME)) {
				subSpan.setClassAttribute(FORENAME+":"+childElement.getAttributeValue(TYPE));
				subSpan.setValue(childElement.getValue()+".");
			} else if (tag.equals(SURNAME)) {
				subSpan.setClassAttribute(SURNAME);
				subSpan.setValue(childElement.getValue());
			} else {
				subSpan = processUnknownTag(tag, PERS_NAME);
			}
			span.appendChild(subSpan);
		}
		return span;
	}


	private HtmlDiv processMonogr(Element monogrElement) {
		Elements childElements = monogrElement.getChildElements();
		HtmlDiv div = new HtmlDiv();
		div.setClassAttribute(MONOGR);
		HtmlElement childDiv = null;
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			if (tag.equals(TITLE)) {
				childDiv = createDivWithValue(childElement);
			} else if (tag.equals(AUTHOR)) {
				childDiv = processAuthor(childElement);
			} else if (tag.equals(IMPRINT)) {
				childDiv = processImprint(childElement);
			} else {
				childDiv = createDivWithValue(childElement);
			}
			div.appendChild(childDiv);
		}
		return div;
	}



	private HtmlSpan processBackHead(Element element) {
		return createSpan(element);
	}

	private HtmlSpan processLabel(Element element) {
		return createSpan(element);
	}

	private HtmlSpan processFigDesc(Element element) {
		return createSpan(element);
	}

	private HtmlSpan processGraphic(Element element) {
		return createSpan(element);
	}

	/**
<imprint>
			<biblScope unit="volume">30</biblScope>
			<biblScope unit="page" from="371" to="386" />
			<date type="published" when="2004" />
		</imprint>	 * @param imprintElement
	 */
	private HtmlElement processImprint(Element element) {
		HtmlSpan span = new HtmlSpan();
		Elements childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			HtmlSpan subSpan;
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			if (tag.equals(BIBL_SCOPE)) {
				subSpan = processBiblScope(childElement);
			} else if (tag.equals(DATE)) {
				subSpan = processDate(childElement);
			} else if (tag.equals(PUBLISHER)) {
				subSpan = createSpan(childElement);
			} else if (tag.equals(PUB_PLACE)) {
				subSpan = createSpan(childElement);
			} else {
				subSpan = createSpan(childElement);
				System.err.println("unknown tag in Imprint: "+tag);
			}
		}
		return span;
	}
	
	/**
<imprint>
			<biblScope unit="volume">30</biblScope>
			<biblScope unit="page" from="371" to="386" />
			<date type="published" when="2004" />
		</imprint>	 * @param imprintElement
	 */
	private HtmlElement processReferences(Element element) {
		HtmlDiv div = createEmptyDivWithClass(element);
		Elements childElements = element.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			String tag = childElement.getLocalName();
			HtmlElement el;
			if (tag.equals(LIST_BIBL)) {
				el = processListBibl(childElement);
			} else {
				el = createDivWithValue(childElement);
			}
			div.appendChild(el);
		}
		return div;
	}

	/**
			<date type="published" when="2004" />
	 * @param childElement
	 * @return
	 */
	private HtmlSpan processDate(Element dateElement) {
		HtmlSpan span = new HtmlSpan();
		for (int i = 0; i < dateElement.getAttributeCount(); i++) {
			Attribute att = dateElement.getAttribute(i);
			String attName = att.getLocalName();
			String attval = att.getValue();
			String value = attval;
			HtmlSpan subSpan = new HtmlSpan();
			if (attName.equals(UNIT)) {
			} else if (attName.equals(TYPE) || attName.equals(WHEN)) {
				subSpan.setClassAttribute(attval);
				subSpan.setValue(attval+": "+value);
			} else {
				System.err.println("unknown date attribute: "+attName);
				subSpan.setClassAttribute(attval);
				subSpan.setValue(attval+"?? "+value);
			}
			span.appendChild(subSpan);
		}
		return span;
	}


	/**
			<biblScope unit="volume">30</biblScope>
			<biblScope unit="page" from="371" to="386" />
			<date type="published" when="2004" />
	 * @param childElement
	 * @return
	 */

	private HtmlSpan processBiblScope(Element element) {
		HtmlSpan span = new HtmlSpan();
		String childVal = element.getValue();
		for (int i = 0; i < element.getAttributeCount(); i++) {
			Attribute att = element.getAttribute(i);
			String attName = att.getLocalName();
			String attval = att.getValue();
			String value = attval;
			HtmlSpan subSpan = new HtmlSpan();
			if (attName.equals(UNIT)) {
				if (attval.equals(VOLUME)) {
					value = childVal;
				} else if(attval.equals(PAGE)) {
					// values are in "from" and "to" attributes
				} else if(attval.equals(ISSUE)) {
					value = childVal;
				} else {
					System.err.println("unknown biblScope@Unit value: "+attval);
				}
				subSpan.setClassAttribute(attval);
				subSpan.setValue(attval+": "+value);
			} else if (attName.equals(TO) || attName.equals(FROM)) {
				subSpan.setClassAttribute(attval);
				subSpan.setValue(attval+": "+value);
			} else {
				System.err.println("unknown biblScope@ attribute: "+attName);
				subSpan.setClassAttribute(attval);
				subSpan.setValue(attval+"?? "+value);
			}
			span.appendChild(subSpan);
		}
		return span;
	}


	private HtmlSpan processNote(Element noteElement) {
		return createSpan(noteElement);
	}

	private HtmlDiv processTable(Element element) {
		return createDivWithValue(element);
	}



	private HtmlSpan processUnknownTag(String tag, String parentTag) {
		HtmlSpan subSpan;
		System.err.println("unknown tag in " + parentTag + ": "+tag);
		subSpan = new HtmlSpan();
		subSpan.setValue("unknown: "+tag+" in "+parentTag);
		return subSpan;
	}

	private HtmlDiv createDivWithValue(Element element) {
		HtmlDiv div = createEmptyDivWithClass(element);
		div.setValue(element.getValue());
		return div;
	}


	private HtmlDiv createEmptyDivWithClass(Element element) {
		HtmlDiv div = new HtmlDiv();
		div.setClassAttribute(element.getLocalName());
		return div;
	}
	
	private HtmlSpan createSpan(Element element) {
		HtmlSpan span = new HtmlSpan();
		span.setClassAttribute(element.getLocalName());
		span.setValue(element.getValue());
		return span;
		
	}
	
	private HtmlDiv processUnknown(Element element, String parentTag) {
		HtmlDiv div = new HtmlDiv();
		XMLUtil.copyAttributes(element, div);
		XMLUtil.transferChildren(element, div);
		return div;
	}


	private HtmlSpan processUnknownAttribute(String attname, Element element) {
		HtmlSpan span = new HtmlSpan();
		span.setValue(attname + " in : " + element.toXML());
		return span;
	}




// ==============================================


	private String getSingleChildValue(Element childElement, String tag) {
		return XMLUtil.getSingleValue(childElement, "./*[local-name()='" + tag + "']");
	}



}
