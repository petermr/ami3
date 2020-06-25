package org.contentmine.ami.lookups;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlH2;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlScript;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlSup;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.norma.input.html.HtmlCleaner;
import org.contentmine.norma.input.html.HtmlCleaner.HtmlClass;
import org.contentmine.norma.input.html.HtmlCleaner.TagPosition;

import nu.xom.Element;

/** dictionary created by hyperlinks in Wikipedia pages
 * 
 * @author pm286
 *
 */
public class WikipediaDictionary {
	public static final Logger LOG = LogManager.getLogger(WikipediaDictionary.class);
public static final String AMBOX_REFIMPROVE = "ambox-Refimprove";
	public static final String CATLINKS = "catlinks";
	public static final String CITE_REF = "cite_ref";
	public static final String EDIT_SECTION = "Edit section: ";
	public static final String EXTERNAL_LINKS = "External_links";
	public static final String FOOTER = "footer";
	public static final String FURTHER_READING = "Further_reading";
	public static final String ID = "id";
	public static final String MW_HIDDEN_CATLINKS = "mw-hidden-catlinks";
	public static final String MW_JUMP_LINK = "mw-jump-link";
	public static final String MW_NAVIGATION = "mw-navigation";
	public static final String NAVBOX = "navbox";
	public static final String PRINTFOOTER = "printfooter";
	public static final String READER_HEADER = "reader-header";
	public static final String REFERENCES = "References";
	public static final String SEE_ALSO = "See_also";
	public static final String SITE_SUB = "siteSub";
	public static final String TOC = "toc";
	public final static String WIKIPEDIA = "wikipedia";
	public final static String WIKITABLE = "wikitable";
	
	public AbstractAMITool amiDictionary;
	private HtmlElement htmlElement;

	public WikipediaDictionary() {
		
	}

	
	public void clean(HtmlElement htmlElement) {
		this.htmlElement = htmlElement;
		omitTop();
		omitJumpToNav();
		omitHead();
		omitScripts();
		omitImprove();
		omitHeader();
		omitCitationRefs();
		omitEditSource();
		omitSeeAlso();
		omitCitations();
		omitNavboxes();
		omitNavigation();
		omitFooter();
		omitPrintFooter();
		omitHiddenCategories();
		omitCatlinks();
		omitToc();
		omitExternalLinks();
		omitFurtherReading();
		try {
			XMLUtil.debug(htmlElement, new File("target/dictionary/htmlElement.html"), 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	<div id="toc">
	 * 
	 */
	
	private void omitToc() {
		new HtmlCleaner(htmlElement).setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		.setEqualsAttribute(HtmlClass.ID, TOC).clean();;	
		
	}
	/**
<div id="catlinks"> ..
*/
	private void omitCatlinks() {
		new HtmlCleaner(htmlElement).setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		.setEqualsAttribute(HtmlClass.ID, CATLINKS).clean();;	
	}
	
	/**
<div class="printfooter"> Retrieved from "
     <a dir="ltr" href="https://en.wikipedia.org/w/index.php?title=Protein_structure&amp;oldid=866213750">https://en.wikipedia.org/w/index.php?title=Protein_structure&amp;oldid=866213750</a>" 
    </div>	 */
	private void omitPrintFooter() {
		new HtmlCleaner(htmlElement).setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		.setEqualsAttribute(HtmlClass.CLASS, PRINTFOOTER).clean();;	
	}
	
	/**
<div id="mw-hidden-catlinks" class="mw-hidden-catlinks mw-hidden-cats-hidden">Hidden categories: 
      <ul>
       <li>
        <a href="/wiki/Category:Use_dmy_dates_from_September_2010" title="Category:Use dmy dates from September 2010">Use dmy dates from September 2010</a>
       </li>
       </li>
      </ul>
     </div>	 */
	private void omitHiddenCategories() {
		new HtmlCleaner(htmlElement).setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		.setEqualsAttribute(HtmlClass.ID, MW_HIDDEN_CATLINKS).clean();;
	}
	
	/**
<ul id="footer-info">
    <li id="footer-info-lastmod"> This page was last edited on 29 October 2018, at 00:39
     <span class="anonymous-show">Â&nbsp;(UTC)</span>.
    </li>
    <li id="footer-info-copyright">Text is available under the 
     <a rel="license" href="//en.wikipedia.org/wiki/Wikipedia:Text_of_Creative_Commons_Attribution-ShareAlike_3.0_Unported_License">Creative Commons Attribution-ShareAlike License</a>
     <a rel="license" href="//creativecommons.org/licenses/by-sa/3.0/" style="display:none;">; additional terms may apply. By using this site, you agree to the 
     </a><a href="//foundation.wikimedia.org/wiki/Terms_of_Use">Terms of Use</a> and 
     <a href="//foundation.wikimedia.org/wiki/Privacy_policy">Privacy Policy</a>. WikipediaÂ® is a registered trademark of the 
     <a href="//www.wikimediafoundation.org/">Wikimedia Foundation, Inc.</a>, a non-profit organization.
    </li>
   </ul>	 */
	/** does this work???
	 * 
	 */
	private void omitFooter() {
		new HtmlCleaner(htmlElement).setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		.setEqualsAttribute(HtmlClass.ID, FOOTER).clean();;
	}


	/**
<div id="mw-navigation">
   <h2>Navigation menu</h2>
   <div id="mw-head">
  </div>	 */
	private void omitNavigation() {
		new HtmlCleaner(htmlElement).setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		.setEqualsAttribute(HtmlClass.ID, MW_NAVIGATION).clean();;
	}


	/**
	<a id="top">
    <div id="siteSub" class="noprint">From Wikipedia, the free encyclopedia</div>
    </a>
    */
	public void omitTop() {
		new HtmlCleaner(htmlElement).setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		.setContainsAttribute(HtmlClass.ID, SITE_SUB).clean();
	}
	
	/**
<div id="jump-to-nav">
<a id="top"></a>
<a class="mw-jump-link" href="#mw-head">Jump to navigation</a>
<a class="mw-jump-link" href="#p-search">Jump to search</a>
...
  </div> 
 

</div></div>	 */
	private void omitJumpToNav() {
		new HtmlCleaner(htmlElement).setTag(HtmlA.TAG, TagPosition.DESCENDANT)
		.setEqualsAttribute(HtmlClass.CLASS, MW_JUMP_LINK).clean();;
	}
    
	private void omitHead() {
		omitAllElementsWithTag(HtmlHead.TAG);
	}

	private void omitScripts() {
		omitAllElementsWithTag(HtmlScript.TAG);
	}

/**	<table class="plainlinks metadata ambox ambox-content ambox-Refimprove" role="presentation"> ...*/
	
	public void omitImprove() {
		new HtmlCleaner(htmlElement)
  		    .setTag(HtmlTable.TAG, TagPosition.DESCENDANT)
		    .setContainsAttribute(HtmlClass.CLASS, AMBOX_REFIMPROVE).clean();
	}
	
	private void omitAllElementsWithTag(String tag) {
		new HtmlCleaner(htmlElement).setTag(tag, TagPosition.DESCENDANT).clean();
	}
	


	/** Header
<div class="header reader-header reader-show-element" dir="ltr">
<a class="domain reader-domain" href="https://en.wikipedia.org/wiki/Neglected_tropical_diseases">en.wikipedia.org</a>
<div class="domain-border"></div>
<h1 class="reader-title">Neglected tropical diseases - Wikipedia</h1>
<div class="credits reader-credits"></div>
<div class="meta-data">
<div class="reader-estimated-time" dir="ltr" style="text-align: left;">69-88 minutes</div>
</div>
</div>  
*/

	private void omitHeader() {
		HtmlElement element = (HtmlElement) XMLUtil.getSingleElement(
				htmlElement, ".//*[local-name()='" + HtmlDiv.TAG + "' and contains(@class,'" + READER_HEADER + "')]");
		if (element != null) {
			element.detach();
		} else {
			LOG.trace("failed to find header"); // I don't think it's universally present
		}
	}

	/**
<h2><span class="mw-headline" id="See_also">See also</span><span class="mw-editsection"><span class="mw-editsection-bracket">[</span><a href="/w/index.php?title=Neglected_tropical_diseases&amp;veaction=edit&amp;section=46" class="mw-editsection-visualeditor" title="Edit section: See also">edit</a><span class="mw-editsection-divider">&nbsp;| </span><a href="/w/index.php?title=Neglected_tropical_diseases&amp;action=edit&amp;section=46" title="Edit section: See also">edit source</a><span class="mw-editsection-bracket">]</span></span></h2>	 
	<ul><li><a href="/wiki/Contagious_disease" title="Contagious disease">Contagious disease</a></li>
	<li><a href="/wiki/Fecal-oral_transmission" class="mw-redirect" title="Fecal-oral transmission">Fecal-oral transmission</a></li>
	<li><a href="/wiki/Orphan_diseases" class="mw-redirect" title="Orphan diseases">Orphan diseases</a></li></ul>
	*/

	private void omitSeeAlso() {
		HtmlElement element = (HtmlElement) XMLUtil.getSingleElement(
				htmlElement, ".//*[local-name()='" + HtmlH2.TAG + "' and *[local-name()='" + HtmlSpan.TAG + "' and @id='" + SEE_ALSO + "']]");
		HtmlElement ul = (HtmlElement) XMLUtil.getFollowingSiblingElement(element);
		if (ul != null) ul.detach();
	}
		
	/** sup
	   * <sup id="cite_ref-:9_66-0"><a href="#cite_note-:9-66">[66]</a></sup>
	   */
	private void omitCitationRefs() {
		List<Element> citeRefList = XMLUtil.getQueryElements(
				htmlElement, ".//*[local-name()='" + HtmlSup.TAG + "' and starts-with(@id, '" + CITE_REF + "')]");
		for (Element citeRef : citeRefList) {
			LOG.trace("deleted citation ref");
			citeRef.detach();
		}
	}
	
	/** Edit/source
<span><span>[</span><a href="https://en.wikipedia.org/w/index.php?title=Neglected_tropical_diseases&amp;veaction=edit&amp;section=3"
 title="Edit section: Chagas disease">edit</a><span>&nbsp;| </span>
 <a href="https://en.wikipedia.org/w/index.php?title=Neglected_tropical_diseases&amp;action=edit&amp;section=3" title="Edit section: Chagas disease">edit source</a><span>]</span></span>	 */
	private void omitEditSource() {
		List<Element> editList = XMLUtil.getQueryElements(
				htmlElement, ".//*[local-name()='" + HtmlSpan.TAG + "' and *[local-name()='" + HtmlA.TAG + "' and starts-with(@title, '" + EDIT_SECTION + "')]]");
		for (Element edit : editList) {
			LOG.trace("deleted edit source");
			edit.detach();
		}
	}
	
	/**
	 * 	<h2><span class="mw-headline" id="References">References</span><span class="mw-editsection"><span class="mw-editsection-bracket">[</span><a href="/w/index.php?title=Neglected_tropical_diseases&amp;veaction=edit&amp;section=47" class="mw-editsection-visualeditor" title="Edit section: References">edit</a><span class="mw-editsection-divider">&nbsp;| </span><a href="/w/index.php?title=Neglected_tropical_diseases&amp;action=edit&amp;section=47" title="Edit section: References">edit source</a><span class="mw-editsection-bracket">]</span></span></h2>
<div class="reflist columns references-column-width" style="-moz-column-width: 30em; -webkit-column-width: 30em; column-width: 30em; list-style-type: decimal;">
<ol class="references">
<li id="cite_note-Hotez-1"><span class="mw-cite-backlink">^ <a href="#cite_ref-Hotez_1-0"><span class="cite-accessibility-label">Jump up to: </span><sup><i><b>a</b></i></sup></a> <a href="#cite_ref-Hotez_1-1"><sup><i><b>b</b></i></sup></a> <a href="#cite_ref-Hotez_1-2"><sup><i><b>c</b></i></sup></a></span> <span class="reference-text"><cite class="citation journal">Hotez PJ (November 2013). <a rel="nofollow" class="external text" href="http://dx.plos.org/10.1371/journal.pntd.0002570">"NTDs V.2.0: "blue marble health"—neglected tropical disease control and elimination in a shifting health policy landscape"</a>. <i>PLoS Negl Trop Dis</i>. <b>7</b> (11): e2570. <a href="/wiki/Digital_object_identifier" title="Digital object identifier">doi</a>:<a rel="nofollow" class="external text" href="//doi.org/10.1371%2Fjournal.pntd.0002570">10.1371/journal.pntd.0002570</a>. <a href="/wiki/PubMed_Central" title="PubMed Central">PMC</a>&nbsp;<span class="cs1-lock-free" title="Freely accessible"><a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pmc/articles/PMC3836998">3836998</a></span>. <a href="/wiki/PubMed_Identifier" class="mw-redirect" title="PubMed Identifier">PMID</a>&nbsp;<a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pubmed/24278496">24278496</a>.</cite><span title="ctx_ver=Z39.88-2004&amp;rft_val_fmt=info%3Aofi%2Ffmt%3Akev%3Amtx%3Ajournal&amp;rft.genre=article&amp;rft.jtitle=PLoS+Negl+Trop+Dis&amp;rft.atitle=NTDs+V.2.0%3A+%22blue+marble+health%22%E2%80%94neglected+tropical+disease+control+and+elimination+in+a+shifting+health+policy+landscape&amp;rft.volume=7&amp;rft.issue=11&amp;rft.pages=e2570&amp;rft.date=2013-11&amp;rft_id=%2F%2Fwww.ncbi.nlm.nih.gov%2Fpmc%2Farticles%2FPMC3836998&amp;rft_id=info%3Apmid%2F24278496&amp;rft_id=info%3Adoi%2F10.1371%2Fjournal.pntd.0002570&amp;rft.au=Hotez+PJ&amp;rft_id=http%3A%2F%2Fdx.plos.org%2F10.1371%2Fjournal.pntd.0002570&amp;rfr_id=info%3Asid%2Fen.wikipedia.org%3ANeglected+tropical+diseases" class="Z3988"></span><style data-mw-deduplicate="TemplateStyles:r861714446">.mw-parser-output cite.citation{font-style:inherit}.mw-parser-output q{quotes:"\"""\"""'""'"}.mw-parser-output code.cs1-code{color:inherit;background:inherit;border:inherit;padding:inherit}.mw-parser-output .cs1-lock-free a{background:url("//upload.wikimedia.org/wikipedia/commons/thumb/6/65/Lock-green.svg/9px-Lock-green.svg.png")no-repeat;background-position:right .1em center}.mw-parser-output .cs1-lock-limited a,.mw-parser-output .cs1-lock-registration a{background:url("//upload.wikimedia.org/wikipedia/commons/thumb/d/d6/Lock-gray-alt-2.svg/9px-Lock-gray-alt-2.svg.png")no-repeat;background-position:right .1em center}.mw-parser-output .cs1-lock-subscription a{background:url("//upload.wikimedia.org/wikipedia/commons/thumb/a/aa/Lock-red-alt-2.svg/9px-Lock-red-alt-2.svg.png")no-repeat;background-position:right .1em center}.mw-parser-output .cs1-subscription,.mw-parser-output .cs1-registration{color:#555}.mw-parser-output .cs1-subscription span,.mw-parser-output .cs1-registration span{border-bottom:1px dotted;cursor:help}.mw-parser-output .cs1-hidden-error{display:none;font-size:100%}.mw-parser-output .cs1-visible-error{font-size:100%}.mw-parser-output .cs1-subscription,.mw-parser-output .cs1-registration,.mw-parser-output .cs1-format{font-size:95%}.mw-parser-output .cs1-kern-left,.mw-parser-output .cs1-kern-wl-left{padding-left:0.2em}.mw-parser-output .cs1-kern-right,.mw-parser-output .cs1-kern-wl-right{padding-right:0.2em}</style> <span style="position:relative; top: -2px;"><a href="/wiki/Open_access" title="open access publication – free to read"><img alt="open access publication – free to read" src="//upload.wikimedia.org/wikipedia/commons/thumb/7/77/Open_Access_logo_PLoS_transparent.svg/9px-Open_Access_logo_PLoS_transparent.svg.png" srcset="//upload.wikimedia.org/wikipedia/commons/thumb/7/77/Open_Access_logo_PLoS_transparent.svg/14px-Open_Access_logo_PLoS_transparent.svg.png 1.5x, //upload.wikimedia.org/wikipedia/commons/thumb/7/77/Open_Access_logo_PLoS_transparent.svg/18px-Open_Access_logo_PLoS_transparent.svg.png 2x" data-file-width="640" data-file-height="1000" width="9" height="14"></a></span></span>
</li>
<li id="cite_note-2"><span class="mw-cite-backlink"><b><a href="#cite_ref-2" aria-label="Jump up" title="Jump up">^</a></b></span> <span class="reference-text"><cite class="citation journal">Hotez PJ, Kamath A (2009).  Cappello, Michael, ed. <a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pmc/articles/PMC2727001">"Neglected Tropical Diseases in Sub-Saharan Africa: Review of Their Prevalence, Distribution, and Disease Burden"</a>. <i>PLoS Negl Trop Dis</i>. <b>3</b> (8): e412. <a href="/wiki/Digital_object_identifier" title="Digital object identifier">doi</a>:<a rel="nofollow" class="external text" href="//doi.org/10.1371%2Fjournal.pntd.0000412">10.1371/journal.pntd.0000412</a>. <a href="/wiki/PubMed_Central" title="PubMed Central">PMC</a>&nbsp;<span class="cs1-lock-free" title="Freely accessible"><a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pmc/articles/PMC2727001">2727001</a></span>. <a href="/wiki/PubMed_Identifier" class="mw-redirect" title="PubMed Identifier">PMID</a>&nbsp;<a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pubmed/19707588">19707588</a>.</cite><span title="ctx_ver=Z39.88-2004&amp;rft_val_fmt=info%3Aofi%2Ffmt%3Akev%3Amtx%3Ajournal&amp;rft.genre=article&amp;rft.jtitle=PLoS+Negl+Trop+Dis&amp;rft.atitle=Neglected+Tropical+Diseases+in+Sub-Saharan+Africa%3A+Review+of+Their+Prevalence%2C+Distribution%2C+and+Disease+Burden&amp;rft.volume=3&amp;rft.issue=8&amp;rft.pages=e412&amp;rft.date=2009&amp;rft_id=%2F%2Fwww.ncbi.nlm.nih.gov%2Fpmc%2Farticles%2FPMC2727001&amp;rft_id=info%3Apmid%2F19707588&amp;rft_id=info%3Adoi%2F10.1371%2Fjournal.pntd.0000412&amp;rft.au=Hotez+PJ%2C+Kamath+A&amp;rft_id=%2F%2Fwww.ncbi.nlm.nih.gov%2Fpmc%2Farticles%2FPMC2727001&amp;rfr_id=info%3Asid%2Fen.wikipedia.org%3ANeglected+tropical+diseases" class="Z3988"></span><link rel="mw-deduplicated-inline-style" href="mw-data:TemplateStyles:r861714446"> <span style="position:relative; top: -2px;"><a href="/wiki/Open_access" title="open access publication – free to read"><img alt="open access publication – free to read" src="//upload.wikimedia.org/wikipedia/commons/thumb/7/77/Open_Access_logo_PLoS_transparent.svg/9px-Open_Access_logo_PLoS_transparent.svg.png" srcset="//upload.wikimedia.org/wikipedia/commons/thumb/7/77/Open_Access_logo_PLoS_transparent.svg/14px-Open_Access_logo_PLoS_transparent.svg.png 1.5x, //upload.wikimedia.org/wikipedia/commons/thumb/7/77/Open_Access_logo_PLoS_transparent.svg/18px-Open_Access_logo_PLoS_transparent.svg.png 2x" data-file-width="640" data-file-height="1000" width="9" height="14"></a></span></span>
</li>
...
<li id="cite_note-:4-119"><span class="mw-cite-backlink"><b><a href="#cite_ref-:4_119-0" aria-label="Jump up" title="Jump up">^</a></b></span> <span class="reference-text"><cite class="citation journal">King, Charles H.; Bertino, Anne-Marie (2008-01-01). <a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pmc/articles/PMC2267491">"Asymmetries of poverty: why global burden of disease valuations underestimate the burden of neglected tropical diseases"</a>. <i>PLoS Neglected Tropical Diseases</i>. <b>2</b> (3): e209. <a href="/wiki/Digital_object_identifier" title="Digital object identifier">doi</a>:<a rel="nofollow" class="external text" href="//doi.org/10.1371%2Fjournal.pntd.0000209">10.1371/journal.pntd.0000209</a>. <a href="/wiki/International_Standard_Serial_Number" title="International Standard Serial Number">ISSN</a>&nbsp;<a rel="nofollow" class="external text" href="//www.worldcat.org/issn/1935-2735">1935-2735</a>. <a href="/wiki/PubMed_Central" title="PubMed Central">PMC</a>&nbsp;<span class="cs1-lock-free" title="Freely accessible"><a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pmc/articles/PMC2267491">2267491</a></span>. <a href="/wiki/PubMed_Identifier" class="mw-redirect" title="PubMed Identifier">PMID</a>&nbsp;<a rel="nofollow" class="external text" href="//www.ncbi.nlm.nih.gov/pubmed/18365036">18365036</a>.</cite><span title="ctx_ver=Z39.88-2004&amp;rft_val_fmt=info%3Aofi%2Ffmt%3Akev%3Amtx%3Ajournal&amp;rft.genre=article&amp;rft.jtitle=PLoS+Neglected+Tropical+Diseases&amp;rft.atitle=Asymmetries+of+poverty%3A+why+global+burden+of+disease+valuations+underestimate+the+burden+of+neglected+tropical+diseases&amp;rft.volume=2&amp;rft.issue=3&amp;rft.pages=e209&amp;rft.date=2008-01-01&amp;rft_id=%2F%2Fwww.ncbi.nlm.nih.gov%2Fpmc%2Farticles%2FPMC2267491&amp;rft.issn=1935-2735&amp;rft_id=info%3Apmid%2F18365036&amp;rft_id=info%3Adoi%2F10.1371%2Fjournal.pntd.0000209&amp;rft.aulast=King&amp;rft.aufirst=Charles+H.&amp;rft.au=Bertino%2C+Anne-Marie&amp;rft_id=%2F%2Fwww.ncbi.nlm.nih.gov%2Fpmc%2Farticles%2FPMC2267491&amp;rfr_id=info%3Asid%2Fen.wikipedia.org%3ANeglected+tropical+diseases" class="Z3988"></span><link rel="mw-deduplicated-inline-style" href="mw-data:TemplateStyles:r861714446"></span>
</li>
</ol></div>
	 */
 	private void omitCitations() {
		Element h2Refs = XMLUtil.getSingleElement(
			htmlElement, ".//*[local-name()='" + HtmlH2.TAG + "' and *[local-name()='" + HtmlSpan.TAG + "' and contains(.,'" + REFERENCES + "')]]");
		HtmlElement div = (HtmlElement) XMLUtil.getFollowingSiblingElement(h2Refs);
		if (div != null) {
			h2Refs.detach();
			div.detach();
		}
	}
	 	
 	/*
 	 * 	<h2>
    <span class="mw-headline" id="External_links">External links</span>
   </h2>
   <ul>
       <li>
        <a href="/wiki/File:Commons-logo.svg" class="image">
         <img alt="" src="//upload.wikimedia.org/wikipedia/en/thumb/4/4a/Commons-logo.svg/12px-Commons-logo.svg.png" class="noviewer" srcset="//upload.wikimedia.org/wikipedia/en/thumb/4/4a/Commons-logo.svg/18px-Commons-logo.svg.png 1.5x, //upload.wikimedia.org/wikipedia/en/thumb/4/4a/Commons-logo.svg/24px-Commons-logo.svg.png 2x" data-file-width="1024" data-file-height="1376" width="12" height="16">
        </a> Media related to 
        <a href="https://commons.wikimedia.org/wiki/Category:Protein_structures" class="extiw" title="commons:Category:Protein structures">Protein structures </a> at Wikimedia Commons
       </li>
      </ul>
   */
	 public void omitExternalLinks() {
		omitHeaderAndFollowingSibling(HtmlH2.TAG, HtmlSpan.TAG, ID, EXTERNAL_LINKS);
	 }

	 	/*
	 	 * 	<h2>
	    <span class="mw-headline" id="External_links">External links</span>
	   </h2>
	   <ul>
	       <li>
	        <a href="/wiki/File:Commons-logo.svg" class="image">
	         <img alt="" src="//upload.wikimedia.org/wikipedia/en/thumb/4/4a/Commons-logo.svg/12px-Commons-logo.svg.png" class="noviewer" srcset="//upload.wikimedia.org/wikipedia/en/thumb/4/4a/Commons-logo.svg/18px-Commons-logo.svg.png 1.5x, //upload.wikimedia.org/wikipedia/en/thumb/4/4a/Commons-logo.svg/24px-Commons-logo.svg.png 2x" data-file-width="1024" data-file-height="1376" width="12" height="16">
	        </a> Media related to 
	        <a href="https://commons.wikimedia.org/wiki/Category:Protein_structures" class="extiw" title="commons:Category:Protein structures">Protein structures </a> at Wikimedia Commons
	       </li>
	      </ul>
	   */
		 public void omitFurtherReading() {
			omitHeaderAndFollowingSibling(HtmlH2.TAG, HtmlSpan.TAG, ID, FURTHER_READING);
		 }


		private void omitHeaderAndFollowingSibling(String tag0, String tag1, String attName, String attVal) {
			Element h2Refs = XMLUtil.getSingleElement(
					htmlElement, ".//*[local-name()='" + tag0 + "' and *[local-name()='" + tag1 + "' and contains(@" + attName + ",'" + attVal + "')]]");
			HtmlElement ul = (HtmlElement) XMLUtil.getFollowingSiblingElement(h2Refs);
			if (ul != null) {
				h2Refs.detach();
				ul.detach();
			}
		}

	 /** Categories doesn't have clear semantics, so use navbox
<div role="navigation" class="navbox" aria-labelledby="Diseases_of_poverty" style="padding:3px">
<table class="nowraplinks collapsible autocollapse navbox-inner mw-collapsible mw-made-collapsible" style="border-spacing:0;background:transparent;color:inherit">
<tbody><tr><th scope="col" class="navbox-title" colspan="2"><span class="mw-collapsible-toggle mw-collapsible-toggle-default" role="button" tabindex="0"><a class="mw-collapsible-text">hide</a></span><div class="plainlinks hlist navbar mini"><ul><li class="nv-view"><a href="/wiki/Template:Diseases_of_poverty" title="Template:Diseases of poverty"><abbr title="View this template" style=";;background:none transparent;border:none;-moz-box-shadow:none;-webkit-box-shadow:none;box-shadow:none; padding:0;">v</abbr></a></li><li class="nv-talk"><a href="/wiki/Template_talk:Diseases_of_poverty" title="Template talk:Diseases of poverty"><abbr title="Discuss this template" style=";;background:none transparent;border:none;-moz-box-shadow:none;-webkit-box-shadow:none;box-shadow:none; padding:0;">t</abbr></a></li><li class="nv-edit"><a class="external text" href="//en.wikipedia.org/w/index.php?title=Template:Diseases_of_poverty&amp;action=edit"><abbr title="Edit this template" style=";;background:none transparent;border:none;-moz-box-shadow:none;-webkit-box-shadow:none;box-shadow:none; padding:0;">e</abbr></a></li></ul></div><div id="Diseases_of_poverty" style="font-size:114%;margin:0 4em"><a href="/wiki/Diseases_of_poverty" title="Diseases of poverty">Diseases of poverty</a></div></th></tr><tr><th scope="row" class="navbox-group" style="width:1%"><a href="/wiki/Diseases_of_poverty" title="Diseases of poverty">Diseases of poverty</a></th><td class="navbox-list navbox-odd hlist" style="text-align:left;border-left-width:2px;border-left-style:solid;width:100%;padding:0px"><div style="padding:0em 0.25em">
<ul>
<li><a href="/wiki/HIV/AIDS" title="HIV/AIDS">AIDS</a></li>
<li><a href="/wiki/Diarrhea" title="Diarrhea">Diarrheal diseases</a></li></ul>
</div></td></tr><tr><th scope="row" class="navbox-group" style="width:1%"><a class="mw-selflink selflink">Neglected diseases</a></th><td class="navbox-list navbox-even hlist" style="text-align:left;border-left-width:2px;border-left-style:solid;width:100%;padding:0px"><div style="padding:0em 0.25em">
<ul>
<li><a href="/wiki/Cholera" title="Cholera">Cholera</a></li>
<li><a href="/wiki/Trachoma" title="Trachoma">Trachoma</a></li></ul>
</div></td></tr><tr><th scope="row" class="navbox-group" style="width:1%">Miscellaneous</th><td class="navbox-list navbox-odd hlist" style="text-align:left;border-left-width:2px;border-left-style:solid;width:100%;padding:0px"><div style="padding:0em 0.25em">
<ul><li><a href="/wiki/Malnutrition" title="Malnutrition">Malnutrition</a></li>
<li><a href="/wiki/Priority_review_voucher" class="mw-redirect" title="Priority review voucher">Priority review voucher</a></li></ul>
</div></td></tr></tbody></table></div>	  */
	 public void omitNavboxes() {
		 new HtmlCleaner(htmlElement)
		 .setTag(HtmlDiv.TAG, TagPosition.DESCENDANT)
		 .setEqualsAttribute(HtmlClass.CLASS, NAVBOX).clean();
	 }

}
