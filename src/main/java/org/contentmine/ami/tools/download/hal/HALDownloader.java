package org.contentmine.ami.tools.download.hal;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.HitList;
import org.contentmine.ami.tools.download.QueryManager.QuerySyntax;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;

import nu.xom.Element;

/** extracts from HAL pages
 * 
 * NYI
 * 
 *
 *https://hal.archives-ouvertes.fr/search/index/?q=ebola
 *    is simplest
 *https://hal.archives-ouvertes.fr/search/index/?q=ebola&page=2
 *    is simplest
 *
 * more complex
 *https://hal.archives-ouvertes.fr/search/index/?
 *q=ebola
 *&docType_s=COMM+OR+DOUV+OR+OTHER+OR+UNDEFINED+OR+REPORT+OR+THESE+OR+HDR+OR+LECTURE+OR+COUV+OR+OUV+OR+POSTER+OR+ART
 *&level0_domain_s=sdv+OR+chim+OR+shs+OR+sde+OR+phys+OR+nlin+OR+spi+OR+math+OR+scco+OR+stat+OR+info
 *&language_s=en+OR+fr+OR+pt
 *&keyword_t=Ebola
 *&submitType_s=notice+OR+file
 *&producedDateY_i=2019
 *
  
 * @author pm286
 *
 */
public class HALDownloader extends AbstractDownloader {

	private static final String ARTICLE = "article";
	private static final String CONTENT = "content/";
	private static final String HIGHWIRE_CITE_EXTRAS = "highwire-cite-extras";
//	private static final String CITE_EXTRAS_DIV = ".//*[local-name()='"+HtmlDiv.TAG+"' and @class='" + HIGHWIRE_CITE_EXTRAS + "']";

	static final Logger LOG = LogManager.getLogger(HALDownloader.class);
private static final String HIGHWIRE_SEARCH_RESULTS_LIST = "highwire-search-results-list";
	
	public static final String HAL_HOST = "hal.archives-ouvertes.fr";
	public static final String HAL_BASE = HTTPS + P2H + HAL_HOST;
	public static final String HAL_SEARCH = HAL_BASE + "/search/index/?";
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
	protected HitList createHitList(Element element) {
//		<ul class="highwire-search-results-list">
		List<Element> ulList = XMLUtil.getQueryElements(element, 
				"//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']");
		
		if (ulList.size() == 0) {
			LOG.debug(element.toXML());
			System.err.println("empty array");
			return new HitList();
		}
		Element ul = ulList.get(0);
		HitList createHitList = super.createHitList(ul);
		return createHitList;
	}

	
	@Override
	/** creates new MetadataEntry populated with contents of contentElement
	 * called when creating (or extending) a HitList
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
	protected String getHitListXPath() {
		throw new RuntimeException("HAL getHitListXPath NYI");
	}

	@Override
	protected AbstractMetadataEntry createSubclassedMetadataEntry() {
		throw new RuntimeException("HAL createSubclassedMetadataEntry NYI");
	}

	@Override 
	protected QuerySyntax getQuerySyntax() {
		return QuerySyntax.AMP_PLUS;
	}

}
