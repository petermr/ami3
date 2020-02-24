package org.contentmine.ami.tools.download.hal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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

/** extracts from HAL pages
 * 
 * 
  
 * @author pm286
 *
 */
public class HALDownloader extends AbstractDownloader {

	private static final String ARTICLE = "article";
	private static final String CONTENT = "content/";
	private static final String HIGHWIRE_CITE_EXTRAS = "highwire-cite-extras";
	private static final String CITE_EXTRAS_DIV = ".//*[local-name()='"+HtmlDiv.TAG+"' and @class='" + HIGHWIRE_CITE_EXTRAS + "']";

	static final Logger LOG = Logger.getLogger(HALDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String HIGHWIRE_SEARCH_RESULTS_LIST = "highwire-search-results-list";
	
	public static final String HAL_HOST = "www.HAL.org";
	public static final String HAL_BASE = HTTPS + P2H + HAL_HOST;
	public static final String HAL_SEARCH = HAL_BASE + "/search/";
	public static final String HAL_HEADER = "/content/";

	
	public HALDownloader() {
		init();
	}

	private void init() {
		this.setBase(HAL_BASE);
	}

	public HALDownloader(CProject cProject) {
		super(cProject);
		init();
	}

	/**
    https://www.HAL.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
	 */

	@Override
	protected ResultSet createResultSet(Element element) {
//		<ul class="highwire-search-results-list">
		List<Element> ulList = XMLUtil.getQueryElements(element, 
				"//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']");
		
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
		HALMetadataEntry metadataEntry = new HALMetadataEntry(this);
		metadataEntry.read(contentElement);
		return metadataEntry;
	}

	public String getSearchUrl() {
		return HAL_SEARCH;
	}

	@Override
	protected String getHost() {
		return HAL_HOST;
	}

	@Override
	protected HtmlElement getSearchResultsList(HtmlBody body) {
		throw new RuntimeException("HAL getSearchResultsList NYI");
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		throw new RuntimeException("HAL getDOIFromURL NYI");
	}

	@Override
	protected void cleanSearchResultsList(HtmlElement searchResultsList) {
		throw new RuntimeException("HAL cleanSearchResultsList NYI");
	}

	@Override
	protected HtmlElement getArticleElement(HtmlHtml htmlHtml) {
		throw new RuntimeException("HAL getArticleElement NYI");
	}

	@Override
	protected String getResultSetXPath() {
		throw new RuntimeException("HAL getResultSetXPath NYI");
	}

	@Override
	protected AbstractMetadataEntry createSubclassedMetadataEntry() {
		throw new RuntimeException("HAL createSubclassedMetadataEntry NYI");
	}

	
}
