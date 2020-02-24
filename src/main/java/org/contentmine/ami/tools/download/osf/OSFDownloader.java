package org.contentmine.ami.tools.download.osf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.ResultSet;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlLink;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** extracts from OSF pages
 * 
 * 
https://osf.io/preprints/discover?page=1001&q=climate%20change
 * @author pm286
 *
 */
public class OSFDownloader extends AbstractDownloader {

/**
https://osf.io/preprints/discover?climate%252Bchange%20sort%3Arelevance-rank%20numresults%3A4 */
	
	static final Logger LOG = Logger.getLogger(OSFDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String OSF_HOST = "osf.io";
	public static final String OSF_BASE = HTTPS + P2H + OSF_HOST;
	public static final String OSF_SEARCH = OSF_BASE + "/preprints/discover?";
	public static final String OSF_HEADER = "/content/";

	
	public OSFDownloader() {
		init();
	}

	private void init() {
		this.setBase(OSF_BASE);
	}

	public OSFDownloader(CProject cProject) {
		super(cProject);
		init();
	}

	/**
	 */

	@Override
	protected ResultSet createResultSet(Element element) {
//		<ul class="highwire-search-results-list">
		List<Element> ulList = XMLUtil.getQueryElements(element, 
				"//*[local-name()='ul' and @class='" + "junk" + "']");
		
		if (ulList.size() == 0) {
			LOG.debug(element.toXML());
			System.err.println("empty array");
			return new ResultSet();
		}
		Element ul = ulList.get(0);
		ResultSet createResultSet = super.createResultSet(ul);
		return createResultSet;
	}

	
	@Override
	/** creates new MetadataEntry populated with contents of contentElement
	 * called when creating (or extending) a ResultSet
	 * 
	 */
	protected AbstractMetadataEntry createMetadataEntry(Element contentElement) {
		OSFMetadataEntry metadataEntry = new OSFMetadataEntry(this);
		metadataEntry.read(contentElement);
		return metadataEntry;
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		if (fullUrl == null) return null;
		String[] parts = fullUrl.split("content");
		return parts[1];
	}

	public String getSearchUrl() {
		return OSF_SEARCH;
	}

	@Override
	protected String getHost() {
		return OSF_HOST;
	}

	@Override
	protected HtmlElement getSearchResultsList(HtmlBody body) {
		throw new RuntimeException("OSF getSearchResultsList NYI");
	}

	@Override
	protected void cleanSearchResultsList(HtmlElement searchResultsList) {
		throw new RuntimeException("OSF cleanSearchResultsList NYI");
	}

	@Override
	protected HtmlElement getArticleElement(HtmlHtml htmlHtml) {
		throw new RuntimeException("OSF getArticleElement NYI");
	}

	@Override
	protected String getResultSetXPath() {
		throw new RuntimeException("OSF getResultSetXPath NYI");
	}

	@Override
	protected AbstractMetadataEntry createSubclassedMetadataEntry() {
		throw new RuntimeException("OSF createSubclassedMetadataEntry NYI");
	}


}
