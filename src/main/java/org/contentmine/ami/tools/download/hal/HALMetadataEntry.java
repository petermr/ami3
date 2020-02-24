package org.contentmine.ami.tools.download.hal;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;

import nu.xom.Element;

/** holds metadata for biorxiv search result
 * 
 * <li class="first odd search-result result-jcode-biorxiv search-result-highwire-citation">
  <div class="highwire-article-citation highwire-citation-type-highwire-article" data-pisa="biorxiv;2020.02.02.931162v1" data-pisa-master="biorxiv;2020.02.02.931162" data-apath="/biorxiv/early/2020/02/04/2020.02.02.931162.atom" id="biorxivearly2020020420200202931162atom">
	<div class="highwire-cite highwire-cite-highwire-article highwire-citation-biorxiv-article-pap-list clearfix">

	<!-- Title AND link to content -->
      <span class="highwire-cite-title">
      <a href="/content/10.1101/2020.02.02.931162v1" class="highwire-cite-linked-title" data-icon-position="" data-hide-link-title="0"><span class="highwire-cite-title">Phylogenomic analysis of the 2019-nCoV coronavirus</span></a>    </span>
  
	<!-- Authors -->
      <div class="highwire-cite-authors"><span class="highwire-citation-authors"><span class="highwire-citation-author first" data-delta="0"><span class="nlm-given-names">Carmine</span> <span class="nlm-surname">Ceraolo</span></span>, <span class="highwire-citation-author" data-delta="1"><span class="nlm-given-names">Federico M</span> <span class="nlm-surname">Giorgi</span></span></span>
      </div>
  
	  <!-- DOI -->
      <div class="highwire-cite-metadata"><span class="highwire-cite-metadata-journal highwire-cite-metadata">bioRxiv </span><span class="highwire-cite-metadata-pages highwire-cite-metadata">2020.02.02.931162; </span><span class="highwire-cite-metadata-doi highwire-cite-metadata"><span class="doi_label">doi:</span> https ://doi.org/10.1101/2020.02.02.931162 </span>
      </div>
  
	  <!-- I think this is just a button for saving citations - skip it -->
      <div class="highwire-cite-extras">
		  <div id="hw-make-citation-0" class="hw-make-citation" data-encoded-apath=";biorxiv;early;2020;02;04;2020.02.02.931162.atom" data-seqnum="0"><a href="/highwire-save-citation/saveapath/%3Bbiorxiv%3Bearly%3B2020%3B02%3B04%3B2020.02.02.931162.atom/nojs/0" id="link-save-citation-toggle-0" class="link-save-citation-save use-ajax hw-link-save-unsave-catation link-icon" title="Save"><i class="icon-plus" /> <span class="title">Add to Selected Citations</span></a>
          </div>
	  </div>
    </div>
  </div>
</li>

 * @author pm286
 *
 */
public class HALMetadataEntry extends AbstractMetadataEntry {
	
	
	private static final String HIGHWIRE_CITE_TITLE = "highwire-cite-title";
	private static final Logger LOG = Logger.getLogger(HALMetadataEntry.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public HALMetadataEntry() {
		super();
	}
		
	public HALMetadataEntry(AbstractDownloader downloader) {
		super(downloader);
	}

	protected void extractMetadata() {
		getTitleAndContentLink();
		getAuthors();
		getDOI();
	}

	protected String getDOI() {
		return doi;
	}

	protected List<String> getAuthors() {
		System.err.println("getAuthors(); NYI");
		return null;
	}
	/**
	<!-- Title AND link to content -->
      <span class="highwire-cite-title">
      <a href="/content/10.1101/2020.02.02.931162v1" class="highwire-cite-linked-title" 
        data-icon-position="" data-hide-link-title="0">
        <span class="highwire-cite-title">Phylogenomic analysis of the 2019-nCoV coronavirus</span>
       </a>
       </span>
	 */
	private void getTitleAndContentLink() {
		HtmlSpan span = (HtmlSpan) XMLUtil.getFirstElement(metadataEntryElement, ".//*[local-name()='span' and @class='" + HIGHWIRE_CITE_TITLE + "']");
		if (span == null) {
			System.out.println("null span "+metadataEntryElement.toXML());
		} else {
			urlPath = ((HtmlA) XMLUtil.getSingleChild(span, HtmlA.TAG)).getHref();
		}
	}

	public void read(Element li) {
		metadataEntryElement = HtmlElement.create(li);
		extractMetadata();
	}

	protected String extractDOIFromUrl() {
		return urlPath.replace("/content/", "");
	}

	/**
	   <li class="first odd search-result result-jcode-biorxiv search-result-highwire-citation">
	    <div class="highwire-article-citation highwire-citation-type-highwire-article" data-pisa="biorxiv;2020.01.24.917864v1" data-pisa-master="biorxiv;2020.01.24.917864" data-seqnum="11" data-apath="/biorxiv/early/2020/01/24/2020.01.24.917864.atom" id="biorxivearly2020012420200124917864atom">
	     <div class="highwire-cite highwire-cite-highwire-article highwire-citation-biorxiv-article-pap-list clearfix">
	      <span class="highwire-cite-title">
	       <a href="/content/10.1101/2020.01.24.917864v1" class="highwire-cite-linked-title" data-icon-position="" data-hide-link-title="0">
	        <span class="highwire-cite-title">A systematic review of scientific research focused on farmers in agricultural adaptation to climate change (2008-2017)</span>
	       </a>
	 * @return
	 * gets the pointer to the "citation", often a landing page (here the value of @href)
	 */


	@Override
	public String getCitationLink() {
		return XMLUtil.getSingleValue(metadataEntryElement, ".//*[local-name()='"+HtmlA.TAG+"']/@href");
	}

}
