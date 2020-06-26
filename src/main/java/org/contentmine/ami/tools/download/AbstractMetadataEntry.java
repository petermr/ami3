package org.contentmine.ami.tools.download;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** metadata from page scraping
 * 
 * @author pm286
 *
 */
public abstract class AbstractMetadataEntry {
	private static final Logger LOG = LogManager.getLogger(AbstractMetadataEntry.class);
protected AbstractDownloader downloader;
	protected String urlPath;
	protected String doi;
//	private Element metadataElement;
	protected HtmlElement metadataEntryElement;

	protected AbstractMetadataEntry() {
	}
	
	protected AbstractMetadataEntry(AbstractDownloader downloader) {
		this.downloader = downloader;
	}

	protected abstract String extractDOIFromUrl();
	protected abstract void extractMetadata();
	protected abstract String getDOI();
	protected abstract List<String> getAuthors();


	protected String getFullUrl() {
		return urlPath == null ? null :
			(urlPath.startsWith("/") ? downloader.getBase() + urlPath : urlPath);
	}
	
	protected String getDOIFromURL() {
		doi = urlPath == null ? null : extractDOIFromUrl();
		return doi;
	}

	public HtmlElement downloadHtmlPageContent() throws IOException {
		HtmlElement htmlElement = null;
		// this is currently the slow step because it's done for each page
		String fullUrl = getFullUrl();
		
		String content = CurlDownloader.runCurlGet(fullUrl);
		Element contentElement = null;
		try {
			contentElement = HtmlUtil.parseCleanlyToXHTML(content);
		} catch (RuntimeException e) {
			LOG.error("Cannot parse: "+e);
		}
		System.err.print("."); // TODO progress util
		htmlElement = (contentElement == null) ? null : HtmlElement.create(contentElement);
		return htmlElement;
	}

	public String getCleanedDOIFromURL() {
		String doi = getDOIFromURL();
		doi = AbstractDownloader.replaceDOIPunctuationByUnderscore(doi);
		return doi;
	}

	public HtmlElement extractHtmlPage() {
		HtmlElement contentElement = null;
		try {
			contentElement = downloadHtmlPageContent();
		} catch (IOException e) {
			LOG.error("could not download page: "+e.getMessage());
		}
		return contentElement;
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

	public abstract String getCitationLink();

	protected void read(Element element) {
		metadataEntryElement = HtmlElement.create(element);
		extractMetadata();
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metadataEntryElement == null) ? 0 : metadataEntryElement.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractMetadataEntry other = (AbstractMetadataEntry) obj;
		if (metadataEntryElement == null) {
			if (other.metadataEntryElement != null)
				return false;
		} else if (!metadataEntryElement.equals(other.metadataEntryElement))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("u "+urlPath+"; d: "+doi+"; ");
//		sb.append("\n");
		sb.append("md "+metadataEntryElement.toXML().length());
		return sb.toString();
	}
}
