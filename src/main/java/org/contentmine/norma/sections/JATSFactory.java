package org.contentmine.norma.sections;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

	private static final Logger LOG = LogManager.getLogger(JATSFactory.class);
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
		Element jatsElement = null;
		String tag = element.getLocalName();
		if (false) {
			
		} else if(JATSAddrLineElement.TAG.equals(tag)) {
			jatsElement = new JATSAddrLineElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAbstractElement.TAG.equals(tag)) {
			jatsElement = new JATSAbstractElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAckElement.TAG.equals(tag)) {
			jatsElement = new JATSAckElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAddressElement.TAG.equals(tag)) {
			jatsElement = new JATSAddressElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAffElement.TAG.equals(tag)) {
			jatsElement = new JATSAffElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSAlternativesElement.TAG.equals(tag)) {
			jatsElement = new JATSAlternativesElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAltTextElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSAltTextElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSAltTitleElement.TAG.equals(tag)) {
			jatsElement = new JATSAltTitleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSAnonymousElement.TAG.equals(tag)) {
			jatsElement = new JATSAnonymousElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAppElement.TAG.equals(tag)) {
            jatsElement = new JATSAppElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSAppGroupElement.TAG.equals(tag)) {
            jatsElement = new JATSAppGroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSArrayElement.TAG.equals(tag)) {
			jatsElement = new JATSArrayElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleCategoriesElement.TAG.equals(tag)) {
			jatsElement = new JATSArticleCategoriesElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleElement.TAG.equals(tag)) {
			jatsElement = new JATSArticleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleIdElement.TAG.equals(tag)) {
			jatsElement = new JATSArticleIdElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleMetaElement.TAG.equals(tag)) {
			jatsElement = new JATSArticleMetaElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSArticleTitleElement.TAG.equals(tag)) {
			jatsElement = new JATSArticleTitleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAttribElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSAttribElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSAuthorCommentElement.TAG.equals(tag)) {
            jatsElement = new JATSAuthorCommentElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSAuthorNotesElement.TAG.equals(tag)) {
			jatsElement = new JATSAuthorNotesElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSAwardGroupElement.TAG.equals(tag)) {
            jatsElement = new JATSAwardGroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSAwardIdElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSAwardIdElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSBackElement.TAG.equals(tag)) {
			jatsElement = new JATSBackElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSBioElement.TAG.equals(tag)) {
            jatsElement = new JATSBioElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSBodyElement.TAG.equals(tag)) {
			jatsElement = new JATSBodyElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSBoldElement.TAG.equals(tag)) {
			jatsElement = new JATSBoldElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSBoxedTextElement.TAG.equals(tag)) {
			jatsElement = new JATSBoxedTextElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSBreakElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSBreakElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSCaptionElement.TAG.equals(tag)) {
			jatsElement = new JATSCaptionElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSChapterTitleElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSChapterTitleElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSCitationElement.TAG.equals(tag)) {
			jatsElement = new JATSCitationElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSCityElement.TAG.equals(tag)) {
            jatsElement = new JATSCityElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSColElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSColElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSColgroupElement.TAG.equals(tag)) { // block
            jatsElement = new JATSColgroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSCollabElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSCollabElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSCommentElement.TAG.equals(tag)) {
            jatsElement = new JATSCommentElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSConfDateElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSConfDateElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSConfLocElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSConfLocElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSConfNameElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSConfNameElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSConfSponsorElement.TAG.equals(tag)) {
            jatsElement = new JATSConfSponsorElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSContribElement.TAG.equals(tag)) {
			jatsElement = new JATSContribElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSContribGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSContribGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSContribIdElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSContribIdElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSCopyrightHolderElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSCopyrightHolderElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSCopyrightStatementElement.TAG.equals(tag)) {
			jatsElement = new JATSCopyrightStatementElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSCopyrightYearElement.TAG.equals(tag)) {
			jatsElement = new JATSCopyrightYearElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSCorrespElement.TAG.equals(tag)) {
			jatsElement = new JATSCorrespElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSCountElement.TAG.equals(tag)) {
            jatsElement = new JATSCountElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSCountryElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSCountryElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSCountsElement.TAG.equals(tag)) {
			jatsElement = new JATSCountsElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSCustomMetaElement.TAG.equals(tag)) {
            jatsElement = new JATSCustomMetaElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSCustomMetaGroupElement.TAG.equals(tag)) {
            jatsElement = new JATSCustomMetaGroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSDataTitleElement.TAG.equals(tag)) {
			jatsElement = new JATSDataTitleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSDateElement.TAG.equals(tag)) {
			jatsElement = new JATSDateElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDateInCitationElement.TAG.equals(tag)) {
            jatsElement = new JATSDateInCitationElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSDayElement.TAG.equals(tag)) {
			jatsElement = new JATSDayElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDefElement.TAG.equals(tag)) {
            jatsElement = new JATSDefElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSDefItemElement.TAG.equals(tag)) {
            jatsElement = new JATSDefItemElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSDefListElement.TAG.equals(tag)) {
			jatsElement = new JATSDefListElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDegreesElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSDegreesElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSDispFormulaElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSDispFormulaElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDispFormulaGroupElement.TAG.equals(tag)) { // block
            jatsElement = new JATSDispFormulaGroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSDispQuoteElement.TAG.equals(tag)) { 
            jatsElement = new JATSDispQuoteElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSEditionElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSEditionElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSElementCitationElement.TAG.equals(tag)) {
			jatsElement = new JATSElementCitationElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSElocationIdElement.TAG.equals(tag)) {
			jatsElement = new JATSElocationIdElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSEmailElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSEmailElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSEquationCountElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSEquationCountElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSEtalElement.TAG.equals(tag)) {
			jatsElement = new JATSEtalElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSExtLinkElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSExtLinkElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSFaxElement.TAG.equals(tag)) {
            jatsElement = new JATSFaxElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSFigElement.TAG.equals(tag)) {
			jatsElement = new JATSFigElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSFigCountElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSFigCountElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSFigGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSFigGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFloatsGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSFloatsGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFnElement.TAG.equals(tag)) {
			jatsElement = new JATSFnElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFnGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSFnGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSFpageElement.TAG.equals(tag)) {
			jatsElement = new JATSFpageElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSFree_to_readElement.TAG.equals(tag)) {
            jatsElement = new JATSFree_to_readElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSFrontStubElement.TAG.equals(tag)) {
			jatsElement = new JATSFrontStubElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSFundingGroupElement.TAG.equals(tag)) {
            jatsElement = new JATSFundingGroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSFundingSourceElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSFundingSourceElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSFundingStatementElement.TAG.equals(tag)) {
            jatsElement = new JATSFundingStatementElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSFrontElement.TAG.equals(tag)) {
			jatsElement = new JATSFrontElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGivenNamesElement.TAG.equals(tag)) {
			jatsElement = new JATSGivenNamesElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGlossaryElement.TAG.equals(tag)) {
			jatsElement = new JATSGlossaryElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGovElement.TAG.equals(tag)) {
			jatsElement = new JATSGovElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSGraphicElement.TAG.equals(tag)) {
			jatsElement = new JATSGraphicElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSHistoryElement.TAG.equals(tag)) {
			jatsElement = new JATSHistoryElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSHrElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSHrElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSInlineFormulaElement.TAG.equals(tag)) {
            jatsElement = new JATSInlineFormulaElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSInlineGraphicElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSInlineGraphicElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSInlineSupplementaryMaterialElement.TAG.equals(tag)) {
            jatsElement = new JATSInlineSupplementaryMaterialElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSInstitutionElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSInstitutionElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSInstitutionIdElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSInstitutionIdElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSIsbnElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSIsbnElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSIssnLElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSIssnLElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSIssueIdElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSIssueIdElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSIssuePartElement.TAG.equals(tag)) {
            jatsElement = new JATSIssuePartElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSIssueSponsorElement.TAG.equals(tag)) {
            jatsElement = new JATSIssueSponsorElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSInstitutionWrapElement.TAG.equals(tag)) {
            jatsElement = new JATSInstitutionWrapElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSIssnElement.TAG.equals(tag)) {
			jatsElement = new JATSIssnElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSIssueElement.TAG.equals(tag)) {
			jatsElement = new JATSIssueElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSIssueTitleElement.TAG.equals(tag)) {
			jatsElement = new JATSIssueTitleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSItalicElement.TAG.equals(tag)) {
			jatsElement = new JATSItalicElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalMetaElement.TAG.equals(tag)) {
			jatsElement = new JATSJournalMetaElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalIdElement.TAG.equals(tag)) {
			jatsElement = new JATSJournalIdElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalTitleElement.TAG.equals(tag)) {
			jatsElement = new JATSJournalTitleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSJournalTitleGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSJournalTitleGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSKwdElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSKwdElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSKwdGroupElement.TAG.equals(tag)) {
            jatsElement = new JATSKwdGroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSLicenseElement.TAG.equals(tag)) {
            jatsElement = new JATSLicenseElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSLicensePElement.TAG.equals(tag)) {
            jatsElement = new JATSLicensePElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSLicense_refElement.TAG.equals(tag)) {
            jatsElement = new JATSLicense_refElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSListItemElement.TAG.equals(tag)) {
            jatsElement = new JATSListItemElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSLpageElement.TAG.equals(tag)) {
			jatsElement = new JATSLpageElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSLabelElement.TAG.equals(tag)) {
			jatsElement = new JATSLabelElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSListElement.TAG.equals(tag)) {
			jatsElement = new JATSListElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMaligngroupElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMaligngroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMalignmarkElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMalignmarkElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMathElement.TAG.equals(tag)) {
            jatsElement = new JATSMathElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMetaNameElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMetaNameElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMetaValueElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMetaValueElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMfencedElement.TAG.equals(tag)) {
            jatsElement = new JATSMfencedElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMfracElement.TAG.equals(tag)) {
            jatsElement = new JATSMfracElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMiElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMiElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMixedCitationElement.TAG.equals(tag)) {
            jatsElement = new JATSMixedCitationElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMmultiscriptsElement.TAG.equals(tag)) {
            jatsElement = new JATSMmultiscriptsElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMnElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMnElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMoElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMoElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMonospaceElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMonospaceElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMoverElement.TAG.equals(tag)) {
            jatsElement = new JATSMoverElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMpaddedElement.TAG.equals(tag)) {
            jatsElement = new JATSMpaddedElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMprescriptsElement.TAG.equals(tag)) {
            jatsElement = new JATSMprescriptsElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMrootElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMrootElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMrowElement.TAG.equals(tag)) {
            jatsElement = new JATSMrowElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMspaceElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMspaceElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMsqrtElement.TAG.equals(tag)) {
            jatsElement = new JATSMsqrtElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMstyleElement.TAG.equals(tag)) {
            jatsElement = new JATSMstyleElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMsubElement.TAG.equals(tag)) {
            jatsElement = new JATSMsubElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMsubsupElement.TAG.equals(tag)) {
            jatsElement = new JATSMsubsupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMsupElement.TAG.equals(tag)) {
            jatsElement = new JATSMsupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMtableElement.TAG.equals(tag)) {
            jatsElement = new JATSMtableElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMtdElement.TAG.equals(tag)) {
            jatsElement = new JATSMtdElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMtextElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSMtextElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMtrElement.TAG.equals(tag)) {
            jatsElement = new JATSMtrElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMunderElement.TAG.equals(tag)) {
            jatsElement = new JATSMunderElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSMunderoverElement.TAG.equals(tag)) {
            jatsElement = new JATSMunderoverElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
            
		} else if(JATSMediaElement.TAG.equals(tag)) {
			jatsElement = new JATSMediaElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSMmlMathElement.TAG.equals(tag)) {
			jatsElement = new JATSMmlMathElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSMonthElement.TAG.equals(tag)) {
			jatsElement = new JATSMonthElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSNameElement.TAG.equals(tag)) {
			jatsElement = new JATSNameElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSNamedContentElement.TAG.equals(tag)) {
            jatsElement = new JATSNamedContentElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSNoteElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSNoteElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSNotesElement.TAG.equals(tag)) {
			jatsElement = new JATSNotesElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSObjectIdElement.TAG.equals(tag)) {
			jatsElement = new JATSObjectIdElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSOnBehalfOfElement.TAG.equals(tag)) {
            jatsElement = new JATSOnBehalfOfElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSOpenAccessElement.TAG.equals(tag)) {
            jatsElement = new JATSOpenAccessElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

			
		} else if(JATSPElement.TAG.equals(tag)) {
			jatsElement = new JATSPElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSPageCountElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSPageCountElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSPageRangeElement.TAG.equals(tag)) {
			jatsElement = new JATSPageRangeElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSPartTitleElement.TAG.equals(tag)) {
            jatsElement = new JATSPartTitleElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSPatentElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSPatentElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSPermissionsElement.TAG.equals(tag)) {
			jatsElement = new JATSPermissionsElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPersonGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSPersonGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSPhoneElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSPhoneElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSPrefixElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSPrefixElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSPreformatElement.TAG.equals(tag)) {
            jatsElement = new JATSPreformatElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSPrincipalAwardRecipientElement.TAG.equals(tag)) {
            jatsElement = new JATSPrincipalAwardRecipientElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSPubDateElement.TAG.equals(tag)) {
			jatsElement = new JATSPubDateElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPubIdElement.TAG.equals(tag)) {
			jatsElement = new JATSPubIdElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPublisherElement.TAG.equals(tag)) {
			jatsElement = new JATSPublisherElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPublisherLocElement.TAG.equals(tag)) {
			jatsElement = new JATSPublisherLocElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSPublisherNameElement.TAG.equals(tag)) {
			jatsElement = new JATSPublisherNameElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSRefCountElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSRefCountElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSRefElement.TAG.equals(tag)) {
			jatsElement = new JATSRefElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			LOG.trace(((JATSRefElement)jatsElement).getPMID());
			
		} else if(JATSRefListElement.TAG.equals(tag)) {
			jatsElement = new JATSRefListElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSRelatedArticleElement.TAG.equals(tag)) {
			jatsElement = new JATSRelatedArticleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSRelatedObjectElement.TAG.equals(tag)) {
            jatsElement = new JATSRelatedObjectElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSRoleElement.TAG.equals(tag)) {
			jatsElement = new JATSRoleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSRomanElement.TAG.equals(tag)) {
            jatsElement = new JATSRomanElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSScElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSScElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSSeasonElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSeasonElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSecElement.TAG.equals(tag)) {
			jatsElement = new JATSSecElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSecMetaElement.TAG.equals(tag)) {
            jatsElement = new JATSSecMetaElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSSelfUriElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSelfUriElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSSeriesElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSeriesElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSSeriesTitleElement.TAG.equals(tag)) {
            jatsElement = new JATSSeriesTitleElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSSizeElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSizeElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
            
		} else if(JATSSourceElement.TAG.equals(tag)) {
			jatsElement = new JATSSourceElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSStateElement.TAG.equals(tag)) {
            jatsElement = new JATSStateElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSStatementElement.TAG.equals(tag)) {
			jatsElement = new JATSStatementElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSStrikeElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSStrikeElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSStringNameElement.TAG.equals(tag)) {
            jatsElement = new JATSStringNameElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSStyledContentElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSStyledContentElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSSubArticleElement.TAG.equals(tag)) {
			jatsElement = new JATSSubArticleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSubjGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSSubjGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSubjectElement.TAG.equals(tag)) {
			jatsElement = new JATSSubjectElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSubElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSubElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSSupElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSSubtitleElement.TAG.equals(tag)) {
			jatsElement = new JATSSubtitleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSuffixElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSuffixElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSupElement.TAG.equals(tag)) {
			jatsElement = new JATSSupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSSupplementElement.TAG.equals(tag)) {
            jatsElement = new JATSSupplementElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
            
		} else if(JATSSupplementaryMaterialElement.TAG.equals(tag)) {
			jatsElement = new JATSSupplementaryMaterialElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSSurnameElement.TAG.equals(tag)) {
			jatsElement = new JATSSurnameElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSTableElement.TAG.equals(tag)) {
			jatsElement = new JATSTableElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSTableCountElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSTableCountElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSTableWrapElement.TAG.equals(tag)) {
			jatsElement = new JATSTableWrapElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSTableWrapFootElement.TAG.equals(tag)) {
			jatsElement = new JATSTableWrapFootElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
		} else if(JATSTableWrapGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSTableWrapGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSTbodyElement.TAG.equals(tag)) {
            jatsElement = new JATSTbodyElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSTdElement.TAG.equals(tag)) {
            jatsElement = new JATSTdElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSTermElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSTermElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSTexMathElement.TAG.equals(tag)) {
			jatsElement = new JATSTexMathElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSTfootElement.TAG.equals(tag)) {
            jatsElement = new JATSTfootElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSThElement.TAG.equals(tag)) {
            jatsElement = new JATSThElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSTheadElement.TAG.equals(tag)) {
            jatsElement = new JATSTheadElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSTitleElement.TAG.equals(tag)) {
			jatsElement = new JATSTitleElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSTitleGroupElement.TAG.equals(tag)) {
			jatsElement = new JATSTitleGroupElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSTransAbstractElement.TAG.equals(tag)) {
            jatsElement = new JATSTransAbstractElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSTransSourceElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSSourceElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSTransTitleElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSTransTitleElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSTransTitleGroupElement.TAG.equals(tag)) {
            jatsElement = new JATSTransTitleGroupElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSTrElement.TAG.equals(tag)) {
			jatsElement = new JATSTrElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);
			
        } else if(JATSUnderlineElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSUnderlineElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSUriElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSUriElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSVersionElement.TAG.equals(tag)) {
            jatsElement = new JATSVersionElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSVolumeElement.TAG.equals(tag)) {
			jatsElement = new JATSVolumeElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

        } else if(JATSWordCountElement.TAG.equals(tag)) { // inline
            jatsElement = new JATSWordCountElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);
            
		} else if(JATSXrefElement.TAG.equals(tag)) {
			jatsElement = new JATSXrefElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

		} else if(JATSYearElement.TAG.equals(tag)) {
			jatsElement = new JATSYearElement(element);
			((JATSElement)jatsElement).recurseThroughDescendants(element, this);

			//// =============
        } else if(JATSNoneElement.TAG.equals(tag)) {
        	System.err.println("element is <none>???");
            jatsElement = new JATSNoneElement(element);
            ((JATSElement)jatsElement).recurseThroughDescendants(element, this);



			////
        } else {
        	System.err.println("no class for: "+tag);
        }
		
		// THESE MAY BE OBSOLETE
		if (jatsElement != null) {
			// OK
		} else if(JATSSpanFactory.isSpan(tag)) {
			System.err.println("Unimplemented span/inline tag: "+tag);
			jatsElement = spanFactory.setElement(element).createAndRecurse();
			
		} else if(JATSDivFactory.isDiv(tag)) {
			System.err.println("Unimplemented div/block tag: "+tag);
			jatsElement = divFactory.setElement(element).createAndRecurse();
			
		} else if(isXref(tag)) {
			jatsElement = createXref(tag, element);
			
		} else if(isStructure(tag)) {
			jatsElement = createHtml(tag, element);
			
		} else if(isStyle(tag)) {
			jatsElement = createHtml(tag, element);
			
		} else if(isTable(tag)) {
			jatsElement = createTable(tag, element);
			
		} else if(isHtml(tag)) {
			jatsElement = createHtml(tag, element);
			
		} else {
			String msg = null;			
			if (element.getChildElements().size() == 0) {
				msg = "Unknown JATS Span "+tag;
				jatsElement = spanFactory.setElement(element).createAndRecurse();
			} else {
				msg = "Unknown JATS Div "+tag;
				jatsElement = divFactory.setElement(element).createAndRecurse();
			}
			if (msg != null) {
				LOG.warn(msg);
			}
		}
		if (jatsElement == null) {
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

		return jatsElement;
		
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
