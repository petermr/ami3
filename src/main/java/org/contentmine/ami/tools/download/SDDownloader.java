package org.contentmine.ami.tools.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;

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
	public static final String SD_SEARCH = SD_BASE+"/search/";  /* NOT YET CERTAIN */
	
	public SDDownloader() {
		init();
	}

	private void init() {
		this.setBase(SD_BASE);
	}

//	public SDDownloader(CProject cProject) {
//		super(cProject);
//		init();
//	}

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

	public static String getSearchUrl() {
		return SD_SEARCH;
	}

	@Override
	protected File cleanAndOutputResultSetFile(File file) {
		throw new RuntimeException("NYI");
	}

	@Override
	protected List<String> getCitationLinks() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ol[class="search-result-wrapper"]
	
}
