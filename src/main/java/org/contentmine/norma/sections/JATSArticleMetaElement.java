package org.contentmine.norma.sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/**

 * 
 * @author pm286
 *
 */
public class JATSArticleMetaElement extends JATSElement implements IsBlock, HasDirectory {
	/**
		<article-meta>
			<article-id pub-id-type="pmcid">3289602</article-id>
			<article-id pub-id-type="publisher-id">PNTD-D-11-00811</article-id>
			<article-id pub-id-type="doi">10.1371/journal.pntd.0001477</article-id>
			<article-categories>
				<subj-group subj-group-type="heading">
					<subject>Research Article</subject>
				</subj-group>
				<subj-group subj-group-type="Discipline-v2">
					<subject>Medicine</subject>
					<subj-group> ...
					</subj-group>
				</subj-group>
			</article-categories>
			<title-group>
				<article-title>Genetic Characterization of Zika Virus Strains:
					Geographic Expansion of the Asian Lineage</article-title>
				<alt-title alt-title-type="running-head">Zika Virus Expansion in Asia</alt-title>
			</title-group>
			<contrib-group>
				<contrib contrib-type="author">
					<name>
						<surname>Haddow</surname>
						<given-names>Andrew D.</given-names>
					</name>
					<xref ref-type="aff" rid="aff1">
						<sup>1</sup>
					</xref>
					<xref ref-type="corresp" rid="cor1">
						<sup>&#x0002a;</sup>
					</xref>
				</contrib>
				...
			</contrib-group>
			<aff id="aff1">
				<label>1</label>
				<addr-line>Institute for Human Infections and Immunity, Center for
					Tropical Diseases, Department of Pathology, University of Texas
					Medical Branch, Galveston, Texas, United States of America</addr-line>
			</aff>
			<aff id="aff2">
				<label>2</label>
				<addr-line>United States Naval Medical Research Unit, No. 2, Phnom
					Penh, Cambodia</addr-line>
			</aff>
			<aff id="aff3">
				<label>3</label>
				<addr-line>National Dengue Control Program, Phnom Penh, Cambodia</addr-line>
			</aff>
			<contrib-group>
				<contrib contrib-type="editor">
					<name>
						<surname>Olson</surname>
						<given-names>Ken E.</given-names>
					</name>
					<role>Editor</role>
					<xref ref-type="aff" rid="edit1" />
				</contrib>
				...
			</contrib-group>
			<aff id="edit1">Colorado State University, United States of America</aff>
			<author-notes>
				<corresp id="cor1">
					&#x0002a; E-mail:
					<email>adhaddow@gmail.com</email>
				</corresp>
				<fn id="fn1" fn-type="current-aff">
					<p>
						<bold>&#x000a4;:</bold>
						Current address: United States Naval Medical Research Unit, No. 6,
						Lima, Peru
					</p>
				</fn>
				<fn fn-type="con">
					<p>Conceived and designed the experiments: ADH AJS. Performed the
						experiments: ADH AJS. Analyzed the data: ADH AJS. Contributed
						reagents/materials/analysis tools: AHD AJS CYY MRK VH RH HG RBT
						SCW. Wrote the paper: ADH AJS RBT SCW.</p>
				</fn>
			</author-notes>
			<pub-date pub-type="collection">
				<month>2</month>
				<year>2012</year>
			</pub-date>
			<pub-date pub-type="epub">
				<day>28</day>
				<month>2</month>
				<year>2012</year>
			</pub-date>
			<volume>6</volume>
			<issue>2</issue>
			<elocation-id>e1477</elocation-id>
			<history>
				<date date-type="received">
					<day>15</day>
					<month>8</month>
					<year>2011</year>
				</date>
				<date date-type="accepted">
					<day>3</day>
					<month>12</month>
					<year>2011</year>
				</date>
			</history>
			<permissions>
				<copyright-statement>This is an open-access article, free of all
					copyright, and may be freely reproduced, distributed, transmitted,
					modified, built upon, or otherwise used by anyone for any lawful
					purpose. The work is made available under the Creative Commons CC0
					public domain dedication.</copyright-statement>
				<copyright-year>2012</copyright-year>
			</permissions>
			<abstract>
				<sec>
					<title>Background</title>
					...
				</sec>
				<sec>
					<title>Methodology/Principal Findings</title>
					...
				</sec>
				...
			</abstract>
			<abstract abstract-type="summary">
				<title>Author Summary</title>
				...
			</abstract>
			<counts>
				<page-count count="7" />
			</counts>
		</article-meta>
	 */

	static final Logger LOG = LogManager.getLogger(JATSArticleMetaElement.class);
static final String TAG = "article-meta";
	
	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
		JATSDivFactory.ABSTRACT,
		JATSDivFactory.AFF,
		JATSDivFactory.ARTICLE_CATEGORIES,
		JATSDivFactory.AUTHOR_NOTES,
		JATSDivFactory.CONFERENCE,
		JATSDivFactory.CONTRIB,
		JATSDivFactory.CONTRIB_GROUP,
		JATSDivFactory.COUNTS,
		JATSDivFactory.CUSTOM_META_GROUP,
		JATSDivFactory.CUSTOM_META_WRAP,
		JATSDivFactory.FUNDING_GROUP,
		JATSDivFactory.HISTORY,
		JATSDivFactory.KWD_GROUP,
		JATSDivFactory.LICENSE,
		JATSDivFactory.PERMISSIONS,
		JATSDivFactory.PUB_DATE,
		JATSDivFactory.RELATED_ARTICLE,
		JATSDivFactory.SUPPLEMENT,
		JATSDivFactory.TITLE_GROUP,
		JATSDivFactory.TRANS_ABSTRACT,
			
		JATSSpanFactory.ARTICLE_ID,
		JATSSpanFactory.COPYRIGHT_STATEMENT,
		JATSSpanFactory.COPYRIGHT_YEAR,
		JATSSpanFactory.ELOCATION_ID,
		JATSSpanFactory.EXT_LINK,
		JATSSpanFactory.FPAGE,
		JATSSpanFactory.ISSUE,
		JATSSpanFactory.ISSUE_ID,
		JATSSpanFactory.ISSUE_TITLE,
		JATSSpanFactory.LPAGE,
		JATSSpanFactory.SELF_URI,
		JATSSpanFactory.VOLUME,
	});

	private List<JATSAffElement> affList;

	private JATSAbstractElement abstractx;
	private JATSHistoryElement history;
	private JATSTitleGroupElement titleGroup;
	private JATSContribGroupElement contribGroup;
	private JATSCountsElement counts;
	
	public JATSArticleMetaElement() {
		super(TAG);
	}
	
	public JATSArticleMetaElement(Element element) {
		super(element);
	}
	
	protected List<String> getAllowedChildNames() {
		return ALLOWED_CHILD_NAMES;
	}

	/**
	    <article-id pub-id-type="pmcid">3289602</article-id>
	 * @return
	 */
	public String getPMCID() {
		String xpath = "*[local-name()='"+JATSArticleIdElement.TAG+"' "
				+ "and @"+JATSArticleIdElement.PUB_ID_TYPE+"='"+JATSArticleIdElement.PMCID+"']";
		String val = XMLUtil.getSingleValue(this, xpath);
		return val;
	}

	/**
    <article-id pub-id-type="pmcid">3289602</article-id>
	 * @return
	 */
	public String getPublisherID() {
		String xpath = "*[local-name()='"+JATSArticleIdElement.TAG+"' "
				+ "and @"+JATSArticleIdElement.PUB_ID_TYPE+"='"+JATSArticleIdElement.PMCID+"']";
		String val = XMLUtil.getSingleValue(this, xpath);
		return val;
	}

/**
	<article-id pub-id-type="publisher-id">PNTD-D-11-00811</article-id>
	<article-id pub-id-type="doi">10.1371/journal.pntd.0001477</article-id>
	*/

	
	protected void applyNonXMLSemantics() {
		makeAffListAndResolve();
		setHistory((JATSHistoryElement) this.getSingleChild(JATSHistoryElement.TAG));
	}

	private void makeAffListAndResolve() {
		affList = new ArrayList<JATSAffElement>();
		String tag = JATSAffElement.TAG;
		List<Element> elements = XMLUtil.getQueryElements(this, "*[local-name()='"+tag+"']");
		for (Element element : elements) {
			affList.add((JATSAffElement)element);
		}
		
	}

	public JATSAbstractElement getAbstract() {
		return abstractx;
	}
	
	public String directoryName() {
		return this.TAG;
	}

	public JATSHistoryElement getHistory() {
		return history;
	}

	public void setHistory(JATSHistoryElement history) {
		this.history = history;
	}

	public JATSTitleGroupElement getOrCreateSingleTitleGroupChild() {
		titleGroup = (JATSTitleGroupElement) getSingleChild(JATSTitleGroupElement.TAG);
		if (titleGroup ==  null) {
			titleGroup = new JATSTitleGroupElement();
			this.appendElement(titleGroup);
		}
		return titleGroup;
	}

	public JATSContribGroupElement getOrCreateSingleContribGroupChild() {
		contribGroup = (JATSContribGroupElement) getSingleChild(JATSContribGroupElement.TAG);
		if (contribGroup ==  null) {
			contribGroup = new JATSContribGroupElement();
			this.appendElement(contribGroup);
		}
		return contribGroup;
	}

	public JATSCountsElement getOrCreateSingleCountsChild() {
		counts = (JATSCountsElement) getSingleChild(JATSCountsElement.TAG);
		if (counts ==  null) {
			counts = new JATSCountsElement();
			this.appendElement(counts);
		}
		return counts;
	}




}
