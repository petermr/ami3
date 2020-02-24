package org.contentmine.ami.tools.download.sd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.ResultSet;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;

import nu.xom.Element;

/** extracts from biorxiv pages
 * 
 * 

 * @author pm286
 *
 */
public class SDDownloader extends AbstractDownloader {
	static final Logger LOG = Logger.getLogger(SDDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String SD_BASE = "https://sciencedirect.com/";
	public static final String SD_HOST = "https://sciencedirect.com/";
	public static final String SD_SEARCH = SD_BASE+"/search/";  /* NOT YET CERTAIN */
	
	public SDDownloader() {
		init();
	}

	private void init() {
		this.setBase(SD_BASE);
	}

	/**
    https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
	 */

	/**
	 * <ul class="highwire-search-results-list">
	 <li class="first odd search-result result-jcode-biorxiv search-result-highwire-citation">
	 * @return 
	 */
	public ResultSet createResultSet(String result) {
		/**
		<ol class="search-result-wrapper">
		<li class="ResultItem col-xs-24 push-m" data-doi="10.1016/j.worlddev.2019.104864">
		<div class="result-item-container u-visited-link">
		<div class="result-item-content">
		<div class="OpenAccessArchive hor">
		<span class="article-type u-clr-grey8">Research article</span>
		<span class="access-indicator access-indicator-yes">
		</span>
		...
		*/

		LOG.error("extract search resuts NYI");
		return null;
	}

	protected AbstractMetadataEntry createMetadataEntry(Element liElement) {
		SDMetadataEntry metadata = new SDMetadataEntry(this);
//		metadata.read(liElement);
		LOG.error("NYI");
		return metadata;
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		throw new RuntimeException("NYI");
	}

	@Override
	public String getSearchUrl() {
		return SD_SEARCH;
	}

	@Override
	protected String getHost() {
		return SD_HOST;
	}

//	@Override
//	protected File cleanAndOutputResultSetFile(File file) {
//		throw new RuntimeException("NYI");
//	}
//
//	@Override
//	protected List<String> getCitationLinks() {
//		throw new RuntimeException("NYI");
//	}
//
//	@Override
//	public File cleanAndOutputArticleFile(File file) {
//		throw new RuntimeException("NYI");
//	}
//	
	@Override
	protected HtmlElement getSearchResultsList(HtmlBody body) {
		throw new RuntimeException("SD getSearchResultsList NYI");
	}

	@Override
	protected void cleanSearchResultsList(HtmlElement searchResultsList) {
		throw new RuntimeException("SD cleanSearchResultsList NYI");
	}

	@Override
	protected HtmlElement getArticleElement(HtmlHtml htmlHtml) {
		throw new RuntimeException("SD getArticleElement NYI");
	}

	@Override
	protected String getResultSetXPath() {
		throw new RuntimeException("SD getResultSetXPath NYI");
	}

	@Override
	protected AbstractMetadataEntry createSubclassedMetadataEntry() {
		throw new RuntimeException("SD createSubclassedMetadataEntry NYI");
	}

	
}
