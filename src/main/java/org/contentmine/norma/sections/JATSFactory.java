package org.contentmine.norma.sections;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.XMLUtils;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlCol;
import org.contentmine.graphics.html.HtmlColgroup;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlI;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlSub;
import org.contentmine.graphics.html.HtmlSup;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;

public class JATSFactory {

	private static final Logger LOG = Logger.getLogger(JATSFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String P = "p";
	public final static String SEC = "sec";
	public final static List<String> STRUCTURE_LIST = Arrays.asList(
		new String[] {
				P,
				SEC,
		}
		);

	public final static String BOLD = "bold";
	public final static String EM = "em";
	public final static String ITALIC = "italic";
	public final static List<String> STYLE_LIST = Arrays.asList(
		new String[] {
				BOLD,
				EM,
				ITALIC,
		}
		);

	public final static String XREF = "xref";
	
	private HtmlHtml htmlElement;
	private JATSDivFactory divFactory;
	private JATSSpanFactory spanFactory;
	

	public JATSFactory() {
		divFactory = new JATSDivFactory(this);
		spanFactory = new JATSSpanFactory(this);
	}

	public HtmlElement createHtml(Element element) {
		htmlElement = HtmlHtml.createUTF8Html();
		htmlElement.appendChild(createHead());
		Element bodyContent = create(element);
		htmlElement.getOrCreateBody().appendChild(bodyContent);
		return htmlElement;
	}

	public HtmlElement createScholarlyHtml(Element element) {
		createHtml(element);
		HtmlElement scholarlyHtmlElement = convertToHtml(htmlElement);
		return scholarlyHtmlElement;
	}

	/**converts the non-html to html
	 * 
	 * @param htmlElement
	 * @return 
	 */
	private HtmlElement convertToHtml(HtmlElement htmlElement) {
		if (htmlElement instanceof HtmlElement) {
			return HtmlElement.create(htmlElement);
		} else {
			HtmlElement newElement = (htmlElement.getChildElements().size() == 0) ? new HtmlSpan() : new HtmlDiv();
			newElement.copyAttributesFrom(htmlElement);
			newElement.copyChildrenFrom(htmlElement);
			newElement.setClassAttribute(htmlElement.getLocalName());
			return newElement;
		}
	}

	private HtmlHead createHead() {
		HtmlHead head = new HtmlHead();
		HtmlStyle htmlStyle = new HtmlStyle();
		String divCssStyle = "div {border-style : solid;}";
		htmlStyle.addCss(divCssStyle);
		String spanCssStyle = 
		  "div {border-style : solid;} \n"+
		  "div.contrib {background : #ddf; display : inline;} \n"+
		  "div.contrib-group {background : #ddf; display : inline;} \n"+
		  "span {background : pink; margin : 1pt;} \n"+
		  "div.addr-line {display : inline ; background : cyan;} \n"+
		  "div.name {display : inline ; background : yellow;} \n"+
		  "div.name:before {content : \"name: \";} \n"+
		  "div.aff {display : inline ; background : green;} \n"+
		  "div.aff:before {content : \"aff: \";} \n"+
		  /** pub-date
		   * 	<pub-date pub-type="epub">
	<day>28</day>
	<month>2</month>
	<year>2012</year>
	</pub-date>

		  */
		  "div.pub-date {background : #eff;} \n"+
		  "div.pub-date:before {content : \"XXX \"attr(pub-type)\": \";} \n"+
		  "";
		htmlStyle.addCss(spanCssStyle);
		head.appendChild(htmlStyle);
		return head;
	}

	/** to add
 app
 app-group
 award-group
 bio
 comment
 custom-meta
 custom-meta-group
 date-in-citation
 def
 def-item
 funding-group
 funding-statement
 inline-formula
 institution-wrap
 kwd-group
 license
 license-p
 list-item
 math
 mfenced
 mfrac
 mixed-citation
 mover
 mrow
 msqrt
 mstyle
 msub
 msubsup
 msup
 mtable
 mtd
 mtr
 munder
 munderover
 named-content
 principal-award-recipient
 sec-meta
 string-name
 supplement
 tfoot
 alt-text
 attrib
 award-id
 break
 chapter-title
 collab
 conf-date
 conf-loc
 conf-name
 contrib-id
 copyright-holder
 country
 degrees
 edition
 email
 equation-count
 ext-link
 fig-count
 funding-source
 hr
 inline-graphic
 institution
 institution-id
 isbn
 issn-l
 issue-id
 kwd
 maligngroup
 malignmark
 meta-name
 meta-value
 mi
 mn
 mo
 monospace
 mroot
 mspace
 mtext
 note
 page-count
 patent
 phone
 prefix
 ref-count
 sc
 season
 self-uri
 series
 size
 strike
 styled-content
 suffix
 table-count
 term
 trans-title
 underline
 uri
 word-count
disp-formula
	/** creates subclassed elements.
	 * normally returns 
	 * 
	 * @param element
	 * @return
	 */
	public Element create(Element element) {
		Element sectionElement = null;
		String tag = element.getLocalName();
//		String namespaceURI = element.getNamespaceURI();
		if (false) {
			
		} else if(JATSAddrLineElement.TAG.equals(tag)) {
			sectionElement = new JATSAddrLineElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAbstractElement.TAG.equals(tag)) {
			sectionElement = new JATSAbstractElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAckElement.TAG.equals(tag)) {
			sectionElement = new JATSAckElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAddressElement.TAG.equals(tag)) {
			sectionElement = new JATSAddressElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAffElement.TAG.equals(tag)) {
			sectionElement = new JATSAffElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAlternativesElement.TAG.equals(tag)) {
			sectionElement = new JATSAlternativesElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAltTextElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSAltTextElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSAltTitleElement.TAG.equals(tag)) {
			sectionElement = new JATSAltTitleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSAnonymousElement.TAG.equals(tag)) {
			sectionElement = new JATSAnonymousElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAppElement.TAG.equals(tag)) {
            sectionElement = new JATSAppElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSAppGroupElement.TAG.equals(tag)) {
            sectionElement = new JATSAppGroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSArrayElement.TAG.equals(tag)) {
			sectionElement = new JATSArrayElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleCategoriesElement.TAG.equals(tag)) {
			sectionElement = new JATSArticleCategoriesElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleElement.TAG.equals(tag)) {
			sectionElement = new JATSArticleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleIdElement.TAG.equals(tag)) {
			sectionElement = new JATSArticleIdElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleMetaElement.TAG.equals(tag)) {
			sectionElement = new JATSArticleMetaElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleTitleElement.TAG.equals(tag)) {
			sectionElement = new JATSArticleTitleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAttribElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSAttribElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSAuthorCommentElement.TAG.equals(tag)) {
            sectionElement = new JATSAuthorCommentElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSAuthorNotesElement.TAG.equals(tag)) {
			sectionElement = new JATSAuthorNotesElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAwardGroupElement.TAG.equals(tag)) {
            sectionElement = new JATSAwardGroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSAwardIdElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSAwardIdElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSBackElement.TAG.equals(tag)) {
			sectionElement = new JATSBackElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSBioElement.TAG.equals(tag)) {
            sectionElement = new JATSBioElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSBodyElement.TAG.equals(tag)) {
			sectionElement = new JATSBodyElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSBoldElement.TAG.equals(tag)) {
			sectionElement = new JATSBoldElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSBoxedTextElement.TAG.equals(tag)) {
			sectionElement = new JATSBoxedTextElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSBreakElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSBreakElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSCaptionElement.TAG.equals(tag)) {
			sectionElement = new JATSCaptionElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSChapterTitleElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSChapterTitleElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSCitationElement.TAG.equals(tag)) {
			sectionElement = new JATSCitationElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSCityElement.TAG.equals(tag)) {
            sectionElement = new JATSCityElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSColElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSColElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSColgroupElement.TAG.equals(tag)) { // block
            sectionElement = new JATSColgroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSCollabElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSCollabElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSCommentElement.TAG.equals(tag)) {
            sectionElement = new JATSCommentElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSConfDateElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSConfDateElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSConfLocElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSConfLocElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSConfNameElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSConfNameElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSConfSponsorElement.TAG.equals(tag)) {
            sectionElement = new JATSConfSponsorElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSContribElement.TAG.equals(tag)) {
			sectionElement = new JATSContribElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSContribGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSContribGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSContribIdElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSContribIdElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSCopyrightHolderElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSCopyrightHolderElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSCopyrightStatementElement.TAG.equals(tag)) {
			sectionElement = new JATSCopyrightStatementElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSCopyrightYearElement.TAG.equals(tag)) {
			sectionElement = new JATSCopyrightYearElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSCorrespElement.TAG.equals(tag)) {
			sectionElement = new JATSCorrespElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSCountElement.TAG.equals(tag)) {
            sectionElement = new JATSCountElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSCountryElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSCountryElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSCountsElement.TAG.equals(tag)) {
			sectionElement = new JATSCountsElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSCustomMetaElement.TAG.equals(tag)) {
            sectionElement = new JATSCustomMetaElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSCustomMetaGroupElement.TAG.equals(tag)) {
            sectionElement = new JATSCustomMetaGroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSDataTitleElement.TAG.equals(tag)) {
			sectionElement = new JATSDataTitleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSDateElement.TAG.equals(tag)) {
			sectionElement = new JATSDateElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDateInCitationElement.TAG.equals(tag)) {
            sectionElement = new JATSDateInCitationElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSDayElement.TAG.equals(tag)) {
			sectionElement = new JATSDayElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDefElement.TAG.equals(tag)) {
            sectionElement = new JATSDefElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSDefItemElement.TAG.equals(tag)) {
            sectionElement = new JATSDefItemElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSDefListElement.TAG.equals(tag)) {
			sectionElement = new JATSDefListElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDegreesElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSDegreesElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSDispFormulaElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSDispFormulaElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDispFormulaGroupElement.TAG.equals(tag)) { // block
            sectionElement = new JATSDispFormulaGroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDispQuoteElement.TAG.equals(tag)) { 
            sectionElement = new JATSDispQuoteElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSEditionElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSEditionElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSElementCitationElement.TAG.equals(tag)) {
			sectionElement = new JATSElementCitationElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSElocationIdElement.TAG.equals(tag)) {
			sectionElement = new JATSElocationIdElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSEmailElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSEmailElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSEquationCountElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSEquationCountElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSEtalElement.TAG.equals(tag)) {
			sectionElement = new JATSEtalElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSExtLinkElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSExtLinkElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSFaxElement.TAG.equals(tag)) {
            sectionElement = new JATSFaxElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSFigElement.TAG.equals(tag)) {
			sectionElement = new JATSFigElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSFigCountElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSFigCountElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSFigGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSFigGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFloatsGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSFloatsGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFnElement.TAG.equals(tag)) {
			sectionElement = new JATSFnElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFnGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSFnGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFpageElement.TAG.equals(tag)) {
			sectionElement = new JATSFpageElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSFree_to_readElement.TAG.equals(tag)) {
            sectionElement = new JATSFree_to_readElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSFrontStubElement.TAG.equals(tag)) {
			sectionElement = new JATSFrontStubElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSFundingGroupElement.TAG.equals(tag)) {
            sectionElement = new JATSFundingGroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSFundingSourceElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSFundingSourceElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSFundingStatementElement.TAG.equals(tag)) {
            sectionElement = new JATSFundingStatementElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSFrontElement.TAG.equals(tag)) {
			sectionElement = new JATSFrontElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGivenNamesElement.TAG.equals(tag)) {
			sectionElement = new JATSGivenNamesElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGlossaryElement.TAG.equals(tag)) {
			sectionElement = new JATSGlossaryElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGovElement.TAG.equals(tag)) {
			sectionElement = new JATSGovElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGraphicElement.TAG.equals(tag)) {
			sectionElement = new JATSGraphicElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSHistoryElement.TAG.equals(tag)) {
			sectionElement = new JATSHistoryElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSHrElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSHrElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSInlineFormulaElement.TAG.equals(tag)) {
            sectionElement = new JATSInlineFormulaElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSInlineGraphicElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSInlineGraphicElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSInlineSupplementaryMaterialElement.TAG.equals(tag)) {
            sectionElement = new JATSInlineSupplementaryMaterialElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSInstitutionElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSInstitutionElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSInstitutionIdElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSInstitutionIdElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSIsbnElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSIsbnElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSIssnLElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSIssnLElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSIssueIdElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSIssueIdElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSIssuePartElement.TAG.equals(tag)) {
            sectionElement = new JATSIssuePartElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSIssueSponsorElement.TAG.equals(tag)) {
            sectionElement = new JATSIssueSponsorElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSInstitutionWrapElement.TAG.equals(tag)) {
            sectionElement = new JATSInstitutionWrapElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSIssnElement.TAG.equals(tag)) {
			sectionElement = new JATSIssnElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSIssueElement.TAG.equals(tag)) {
			sectionElement = new JATSIssueElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSIssueTitleElement.TAG.equals(tag)) {
			sectionElement = new JATSIssueTitleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSItalicElement.TAG.equals(tag)) {
			sectionElement = new JATSItalicElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalMetaElement.TAG.equals(tag)) {
			sectionElement = new JATSJournalMetaElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalIdElement.TAG.equals(tag)) {
			sectionElement = new JATSJournalIdElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalTitleElement.TAG.equals(tag)) {
			sectionElement = new JATSJournalTitleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalTitleGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSJournalTitleGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSKwdElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSKwdElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSKwdGroupElement.TAG.equals(tag)) {
            sectionElement = new JATSKwdGroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSLicenseElement.TAG.equals(tag)) {
            sectionElement = new JATSLicenseElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSLicensePElement.TAG.equals(tag)) {
            sectionElement = new JATSLicensePElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSLicense_refElement.TAG.equals(tag)) {
            sectionElement = new JATSLicense_refElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSListItemElement.TAG.equals(tag)) {
            sectionElement = new JATSListItemElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSLpageElement.TAG.equals(tag)) {
			sectionElement = new JATSLpageElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSLabelElement.TAG.equals(tag)) {
			sectionElement = new JATSLabelElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSListElement.TAG.equals(tag)) {
			sectionElement = new JATSListElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMaligngroupElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMaligngroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMalignmarkElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMalignmarkElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMathElement.TAG.equals(tag)) {
            sectionElement = new JATSMathElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMetaNameElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMetaNameElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMetaValueElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMetaValueElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMfencedElement.TAG.equals(tag)) {
            sectionElement = new JATSMfencedElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMfracElement.TAG.equals(tag)) {
            sectionElement = new JATSMfracElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMiElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMiElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMixedCitationElement.TAG.equals(tag)) {
            sectionElement = new JATSMixedCitationElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMmultiscriptsElement.TAG.equals(tag)) {
            sectionElement = new JATSMmultiscriptsElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMnElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMnElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMoElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMoElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMonospaceElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMonospaceElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMoverElement.TAG.equals(tag)) {
            sectionElement = new JATSMoverElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMpaddedElement.TAG.equals(tag)) {
            sectionElement = new JATSMpaddedElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMprescriptsElement.TAG.equals(tag)) {
            sectionElement = new JATSMprescriptsElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMrootElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMrootElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMrowElement.TAG.equals(tag)) {
            sectionElement = new JATSMrowElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMspaceElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMspaceElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMsqrtElement.TAG.equals(tag)) {
            sectionElement = new JATSMsqrtElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMstyleElement.TAG.equals(tag)) {
            sectionElement = new JATSMstyleElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMsubElement.TAG.equals(tag)) {
            sectionElement = new JATSMsubElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMsubsupElement.TAG.equals(tag)) {
            sectionElement = new JATSMsubsupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMsupElement.TAG.equals(tag)) {
            sectionElement = new JATSMsupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMtableElement.TAG.equals(tag)) {
            sectionElement = new JATSMtableElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMtdElement.TAG.equals(tag)) {
            sectionElement = new JATSMtdElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMtextElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSMtextElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMtrElement.TAG.equals(tag)) {
            sectionElement = new JATSMtrElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMunderElement.TAG.equals(tag)) {
            sectionElement = new JATSMunderElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSMunderoverElement.TAG.equals(tag)) {
            sectionElement = new JATSMunderoverElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
            
		} else if(JATSMediaElement.TAG.equals(tag)) {
			sectionElement = new JATSMediaElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSMmlMathElement.TAG.equals(tag)) {
			sectionElement = new JATSMmlMathElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSMonthElement.TAG.equals(tag)) {
			sectionElement = new JATSMonthElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSNameElement.TAG.equals(tag)) {
			sectionElement = new JATSNameElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSNamedContentElement.TAG.equals(tag)) {
            sectionElement = new JATSNamedContentElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSNoteElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSNoteElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSNotesElement.TAG.equals(tag)) {
			sectionElement = new JATSNotesElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSObjectIdElement.TAG.equals(tag)) {
			sectionElement = new JATSObjectIdElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSOnBehalfOfElement.TAG.equals(tag)) {
            sectionElement = new JATSOnBehalfOfElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSOpenAccessElement.TAG.equals(tag)) {
            sectionElement = new JATSOpenAccessElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

			
		} else if(JATSPElement.TAG.equals(tag)) {
			sectionElement = new JATSPElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSPageCountElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSPageCountElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSPageRangeElement.TAG.equals(tag)) {
			sectionElement = new JATSPageRangeElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSPartTitleElement.TAG.equals(tag)) {
            sectionElement = new JATSPartTitleElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSPatentElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSPatentElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSPermissionsElement.TAG.equals(tag)) {
			sectionElement = new JATSPermissionsElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPersonGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSPersonGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSPhoneElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSPhoneElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSPrefixElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSPrefixElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSPreformatElement.TAG.equals(tag)) {
            sectionElement = new JATSPreformatElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSPrincipalAwardRecipientElement.TAG.equals(tag)) {
            sectionElement = new JATSPrincipalAwardRecipientElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSPubDateElement.TAG.equals(tag)) {
			sectionElement = new JATSPubDateElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPubIdElement.TAG.equals(tag)) {
			sectionElement = new JATSPubIdElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPublisherElement.TAG.equals(tag)) {
			sectionElement = new JATSPublisherElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPublisherLocElement.TAG.equals(tag)) {
			sectionElement = new JATSPublisherLocElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPublisherNameElement.TAG.equals(tag)) {
			sectionElement = new JATSPublisherNameElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSRefCountElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSRefCountElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSRefElement.TAG.equals(tag)) {
			sectionElement = new JATSRefElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			LOG.trace(((JATSRefElement)sectionElement).getPMID());
			
		} else if(JATSReflistElement.TAG.equals(tag)) {
			sectionElement = new JATSReflistElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSRelatedArticleElement.TAG.equals(tag)) {
			sectionElement = new JATSRelatedArticleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSRelatedObjectElement.TAG.equals(tag)) {
            sectionElement = new JATSRelatedObjectElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSRoleElement.TAG.equals(tag)) {
			sectionElement = new JATSRoleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSRomanElement.TAG.equals(tag)) {
            sectionElement = new JATSRomanElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSScElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSScElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSSeasonElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSeasonElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSecElement.TAG.equals(tag)) {
			sectionElement = new JATSSecElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSecMetaElement.TAG.equals(tag)) {
            sectionElement = new JATSSecMetaElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSSelfUriElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSelfUriElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSSeriesElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSeriesElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSSeriesTitleElement.TAG.equals(tag)) {
            sectionElement = new JATSSeriesTitleElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSSizeElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSizeElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
            
		} else if(JATSSourceElement.TAG.equals(tag)) {
			sectionElement = new JATSSourceElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSStateElement.TAG.equals(tag)) {
            sectionElement = new JATSStateElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSStatementElement.TAG.equals(tag)) {
			sectionElement = new JATSStatementElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSStrikeElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSStrikeElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSStringNameElement.TAG.equals(tag)) {
            sectionElement = new JATSStringNameElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSStyledContentElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSStyledContentElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSSubArticleElement.TAG.equals(tag)) {
			sectionElement = new JATSSubArticleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSubjGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSSubjGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSubjectElement.TAG.equals(tag)) {
			sectionElement = new JATSSubjectElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSubElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSubElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSSupElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSSubtitleElement.TAG.equals(tag)) {
			sectionElement = new JATSSubtitleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSuffixElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSuffixElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSupElement.TAG.equals(tag)) {
			sectionElement = new JATSSupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSupplementElement.TAG.equals(tag)) {
            sectionElement = new JATSSupplementElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
            
		} else if(JATSSupplementaryMaterialElement.TAG.equals(tag)) {
			sectionElement = new JATSSupplementaryMaterialElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSurnameElement.TAG.equals(tag)) {
			sectionElement = new JATSSurnameElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSTableElement.TAG.equals(tag)) {
			sectionElement = new JATSTableElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSTableCountElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSTableCountElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSTableWrapElement.TAG.equals(tag)) {
			sectionElement = new JATSTableWrapElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSTableWrapFootElement.TAG.equals(tag)) {
			sectionElement = new JATSTableWrapFootElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
		} else if(JATSTableWrapGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSTableWrapGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSTbodyElement.TAG.equals(tag)) {
            sectionElement = new JATSTbodyElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSTdElement.TAG.equals(tag)) {
            sectionElement = new JATSTdElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSTermElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSTermElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSTexMathElement.TAG.equals(tag)) {
			sectionElement = new JATSTexMathElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSTfootElement.TAG.equals(tag)) {
            sectionElement = new JATSTfootElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSThElement.TAG.equals(tag)) {
            sectionElement = new JATSThElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSTheadElement.TAG.equals(tag)) {
            sectionElement = new JATSTheadElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSTitleElement.TAG.equals(tag)) {
			sectionElement = new JATSTitleElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSTitleGroupElement.TAG.equals(tag)) {
			sectionElement = new JATSTitleGroupElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSTransAbstractElement.TAG.equals(tag)) {
            sectionElement = new JATSTransAbstractElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSTransSourceElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSSourceElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSTransTitleElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSTransTitleElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSTransTitleGroupElement.TAG.equals(tag)) {
            sectionElement = new JATSTransTitleGroupElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSTrElement.TAG.equals(tag)) {
			sectionElement = new JATSTrElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);
			
        } else if(JATSUnderlineElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSUnderlineElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSUriElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSUriElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSVersionElement.TAG.equals(tag)) {
            sectionElement = new JATSVersionElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSVolumeElement.TAG.equals(tag)) {
			sectionElement = new JATSVolumeElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

        } else if(JATSWordCountElement.TAG.equals(tag)) { // inline
            sectionElement = new JATSWordCountElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);
            
		} else if(JATSXrefElement.TAG.equals(tag)) {
			sectionElement = new JATSXrefElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

		} else if(JATSYearElement.TAG.equals(tag)) {
			sectionElement = new JATSYearElement(element);
			((JATSElement)sectionElement).recurseThroughDescendants(element, this);

			//// =============
        } else if(JATSNoneElement.TAG.equals(tag)) {
        	System.err.println("element is <none>???");
            sectionElement = new JATSNoneElement(element);
            ((JATSElement)sectionElement).recurseThroughDescendants(element, this);



			////
        } else {
        	System.err.println("no class for: "+tag);
        }
		
		if (sectionElement != null) {
			// OK
		} else if(JATSSpanFactory.isSpan(tag)) {
			System.err.println("Unimplemented span/inline tag: "+tag);
			sectionElement = spanFactory.setElement(element).createAndRecurse();
			
		} else if(JATSDivFactory.isDiv(tag)) {
			System.err.println("Unimplemented div/block tag: "+tag);
			sectionElement = divFactory.setElement(element).createAndRecurse();
			
		} else if(isXref(tag)) {
			sectionElement = createXref(tag, element);
			
		} else if(isStructure(tag)) {
			sectionElement = createHtml(tag, element);
			
		} else if(isStyle(tag)) {
			sectionElement = createHtml(tag, element);
			
		} else if(isTable(tag)) {
			sectionElement = createTable(tag, element);
			
		} else if(isHtml(tag)) {
			sectionElement = createHtml(tag, element);
			
		} else {
			String msg = null;			
			if (element.getChildElements().size() == 0) {
				msg = "Unknown JATS Span "+tag;
				sectionElement = spanFactory.setElement(element).createAndRecurse();
			} else {
				msg = "Unknown JATS Div "+tag;
				sectionElement = divFactory.setElement(element).createAndRecurse();
			}
			if (msg != null) {
				LOG.warn(msg);
			}
		}
		if (sectionElement == null) {
			LOG.warn("NULL SECTION");
//		} else if (sectionElement instanceof HtmlElement) {
//			// do nothing
//			LOG.debug("HTML");
//		} else {
//			// turn JatsElements into Html
//			HtmlElement newElement = sectionElement.getChildElements().size() == 0 ? new HtmlSpan() : new HtmlDiv();
//			newElement.copyAttributesFrom(sectionElement);
//			newElement.copyChildrenFrom(sectionElement);
//			sectionElement = newElement;
		}

		return sectionElement;
		
	}
	
	

	/**
	 * 
	 *        
       <div ref-type="aff" rid="aff1" class="xref">
        <sup>1</sup>
       </div>

	 * @param tag
	 * @param element
	 * @return
	 */
	private Element createXref(String tag, Element element) {
		HtmlA htmlA = null;
		
		if (XREF.equals(tag)) {
			htmlA = new HtmlA();
			if (element.getValue().trim().length() == 0) {
				htmlA.setValue("*");
			} else {
				processAttributesAndChildren(element, htmlA);
			}
			htmlA.setHref("#"+element.getAttributeValue("rid"));
		}
		return htmlA;
	}
	

	public Element createHtml(String tag, Element element) {
		Element htmlElement = null;
		
		if (BOLD.equals(tag)) {
			htmlElement = new HtmlB();
		} else if (EM.equals(tag)) {
			htmlElement = new HtmlI();
		} else  if (ITALIC.equals(tag)) {
			htmlElement = new HtmlI();
		} else  if (HtmlSub.TAG.equals(tag)) {
			htmlElement = new HtmlSub();
		} else  if (HtmlSup.TAG.equals(tag)) {
			htmlElement = new HtmlSup();
		} else  if (SEC.equals(tag)) {
			htmlElement = new HtmlDiv();
		} else  if (P.equals(tag)) {
			htmlElement = new HtmlP();
		} else {
			if (element.getChildElements().size() == 0) {
				htmlElement = new HtmlSpan();
			} else {
				htmlElement = new HtmlDiv();
			}
			AbstractCMElement.setClassAttributeValue(htmlElement, element.getLocalName());
		}
		if (htmlElement == null) {
			LOG.warn("NULL HTML: "+element);
		}
		
		processAttributesAndChildren(element, htmlElement);
		return htmlElement;
	}

	private Element createTable(String tag, Element element) {
		Element htmlElement = null;
		
		if (HtmlTable.TAG.equals(tag)) {
			htmlElement = new HtmlTable();
		} else if (HtmlTbody.TAG.equals(tag)) {
			htmlElement = new HtmlTbody();
		} else if (HtmlCol.TAG.equals(tag)) {
			htmlElement = new HtmlCol();
		} else if (HtmlColgroup.TAG.equals(tag)) {
			htmlElement = new HtmlColgroup();
		} else if (HtmlTh.TAG.equals(tag)) {
			htmlElement = new HtmlTh();
		} else if (HtmlThead.TAG.equals(tag)) {
			htmlElement = new HtmlThead();
		} else if (HtmlTd.TAG.equals(tag)) {
			htmlElement = new HtmlTd();
		} else if (HtmlTr.TAG.equals(tag)) {
			htmlElement = new HtmlTr();
		}
		if (htmlElement == null) {
			LOG.warn("NULL TABLE");
		}
		processAttributesAndChildren(element, htmlElement);
		return htmlElement;
	}

	private void processAttributesAndChildren(Element element, Element htmlElement) {
		if (htmlElement != null) {
			XMLUtil.copyAttributes(element, htmlElement);
			processChildren(element, htmlElement);
		}
	}

	private static boolean isXref(String tag) {
		return 
				
		XREF.equals(tag) 
		;
		
	}
	
	private static boolean isHtml(String tag) {
		return 
				
		HtmlSub.TAG.equals(tag) ||
		HtmlSup.TAG.equals(tag) 
		;
		
	}

	private static boolean isTable(String tag) {
		return 
				
				HtmlCol.TAG.equals(tag) ||
				HtmlColgroup.TAG.equals(tag) ||
				HtmlTable.TAG.equals(tag) ||
				HtmlTbody.TAG.equals(tag) ||
				HtmlTd.TAG.equals(tag) ||
				HtmlTh.TAG.equals(tag) ||
				HtmlThead.TAG.equals(tag) ||
				HtmlTr.TAG.equals(tag)
				;
	}

	private static boolean isStructure(String tag) {
		return STRUCTURE_LIST.contains(tag);
	}

	private static boolean isStyle(String tag) {
		return STYLE_LIST.contains(tag);
	}

	void processChildren(Element element, Element sectionElement) {
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				Element jatsChild = this.create((Element)child);
				if (jatsChild == null) {
					LOG.warn("NULL "+((Element)child).getLocalName());
				} else if (sectionElement != null) {	
					sectionElement.appendChild(jatsChild);
				}
			} else {
				if (sectionElement != null) {
					sectionElement.appendChild(child.copy());
				}
			}
		}
	}

	private Element readElement(InputStream is) {
		Document doc = XMLUtils.parseWithoutDTD(is);
		Element element = doc == null ? null : doc.getRootElement();
		return element;
	}

	public JATSArticleElement createJATSArticleElememt(InputStream is) {
		Element inputElement = readElement(is);
		return createJATSArticleElement(inputElement);
	}

	public JATSArticleElement createJATSArticleElement(Element inputElement) {
		Element jatsElement = this.create(inputElement);
		return (jatsElement == null || !(jatsElement instanceof JATSArticleElement)) ? null 
				: (JATSArticleElement) jatsElement;
	}

	public JATSArticleElement readArticle(File inputFile) {
		if (inputFile == null) {
			throw new RuntimeException("null input JATS");
		}
		Element inputElement = XMLUtils.parseWithoutDTD(inputFile).getRootElement();
		JATSArticleElement articleElement = createJATSArticleElement(inputElement);
		return articleElement;
	}

}
