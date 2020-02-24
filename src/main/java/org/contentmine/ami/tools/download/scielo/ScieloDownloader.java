package org.contentmine.ami.tools.download.scielo;

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

/** extracts from biorxiv pages
 * 
 * 
  
 * @author pm286
 *
 */
public class ScieloDownloader extends AbstractDownloader {

	static final Logger LOG = Logger.getLogger(ScieloDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String SCIELO_HOST = "www.scielo.org";
	public static final String SCIELO_BASE = HTTPS + P2H + SCIELO_HOST;
	public static final String SCIELO_SEARCH = SCIELO_BASE + "/search/";
	public static final String SCIELO_HEADER = "/content/";

	
	public ScieloDownloader() {
		init();
	}

	private void init() {
		this.setBase(SCIELO_BASE);
	}

	public ScieloDownloader(CProject cProject) {
		super(cProject);
		init();
	}

	/**
    https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
	 */

	@Override
	protected ResultSet createResultSet(Element element) {
//		<ul class="highwire-search-results-list">
		List<Element> ulList = XMLUtil.getQueryElements(element, 
				"//*[local-name()='ul' and @class='" + "JUNK" + "']");
		
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
		ScieloMetadataEntry metadataEntry = new ScieloMetadataEntry(this);
		metadataEntry.read(contentElement);
		return metadataEntry;
	}

	public String getSearchUrl() {
		return SCIELO_SEARCH;
	}

	protected String getHost() {
		return SCIELO_HOST;
	}

	
	@Override
	protected HtmlElement getSearchResultsList(HtmlBody body) {
		throw new RuntimeException("SCIELO getSearchResultsList NYI");
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		throw new RuntimeException("SCIELO getDOIFromURL NYI");
	}

	@Override
	protected void cleanSearchResultsList(HtmlElement searchResultsList) {
		throw new RuntimeException("SCIELO cleanSearchResultsList NYI");
	}

	@Override
	protected HtmlElement getArticleElement(HtmlHtml htmlHtml) {
		throw new RuntimeException("SCIELO getArticleElement NYI");
	}

	@Override
	protected String getResultSetXPath() {
		throw new RuntimeException("SCIELO getResultSetXPath NYI");
	}

	@Override
	protected AbstractMetadataEntry createSubclassedMetadataEntry() {
		throw new RuntimeException("SCIELO createSubclassedMetadataEntry NYI");
	}


}
