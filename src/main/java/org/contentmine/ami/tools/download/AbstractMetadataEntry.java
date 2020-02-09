package org.contentmine.ami.tools.download;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
	private static final Logger LOG = Logger.getLogger(AbstractMetadataEntry.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected AbstractDownloader downloader;
	protected String urlPath;
	protected String doi;
	private Element metadataElement;
	protected HtmlElement metadataEntryElement;

	protected AbstractMetadataEntry() {}
	
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
		
		String content = AMIDownloadTool.runCurlGet(fullUrl);
		Element contentElement = null;
		try {
			contentElement = HtmlUtil.parseCleanlyToXHTML(content);
		} catch (RuntimeException e) {
			LOG.error("Cannot parse: "+e);
		}
		System.out.print(".");
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

}
