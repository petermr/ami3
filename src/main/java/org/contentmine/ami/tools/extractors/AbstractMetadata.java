package org.contentmine.ami.tools.extractors;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;
import nu.xom.ParsingException;

/** metadata from page scraping
 * 
 * @author pm286
 *
 */
public abstract class AbstractMetadata {
	private static final Logger LOG = Logger.getLogger(AbstractMetadata.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected AbstractExtractor extractor;
	protected String urlPath;
	private String doi;

	protected AbstractMetadata(AbstractExtractor extractor) {
		this.extractor = extractor;
	}

	protected abstract String extractDOIFromUrl();
	protected abstract void extractMetadata();

	protected String getFullUrl() {
		return urlPath == null ? null :
			(urlPath.startsWith("/") ? extractor.getBase() + urlPath : urlPath);
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
			contentElement = XMLUtil.parseCleanlyToXML(content);
		} catch (RuntimeException e) {
			LOG.error("Cannot parse: "+e);
		}
		System.out.print(".");
		htmlElement = (contentElement == null) ? null : HtmlElement.create(contentElement);
		return htmlElement;
	}

	public String getCleanedDOIFromURL() {
		String doi = getDOIFromURL();
		doi = AbstractExtractor.replaceDOIPunctuationByUnderscore(doi);
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
