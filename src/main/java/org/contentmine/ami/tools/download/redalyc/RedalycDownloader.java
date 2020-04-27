package org.contentmine.ami.tools.download.redalyc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.HitList;
import org.contentmine.ami.tools.download.QueryManager.QuerySyntax;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlUl;

import nu.xom.Element;

/** extracts from redalyc pages
 * 
 * 

 * @author pm286
 *
 */
public class RedalycDownloader extends AbstractDownloader {

	private static final String ARTICLE = "article";
	private static final String CONTENT = "content/";
	private static final String HIGHWIRE_CITE_EXTRAS = "highwire-cite-extras";
	static final String CITE_EXTRAS_DIV = ".//*[local-name()='"+HtmlDiv.TAG+"' and @class='" + HIGHWIRE_CITE_EXTRAS + "']";

	static final Logger LOG = Logger.getLogger(RedalycDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String HIGHWIRE_SEARCH_RESULTS_LIST = "highwire-search-results-list";
	
	public static final String REDALYC_HOST = "www.redalyc.org";
	public static final String REDALYC_BASE = HTTPS + P2H + REDALYC_HOST;
	public static final String REDALYC_SEARCH = REDALYC_BASE + "/busquedaArticuloFiltros.oa";
	public static final String REDALYC_HEADER = "???";
	public static final String REDALYC_NOT_YET_IMPLEMENTED = "REDALYC NOT YET IMPLEMENTED";

	
	public RedalycDownloader() {
		init();
	}

	private void init() {
		this.setBase(REDALYC_BASE);
	}

	public RedalycDownloader(CProject cProject) {
		super(cProject);
		init();
	}

	@Override
	protected String getHitListXPath() {
		return "//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']";
	}

	
	@Override
	protected RedalycMetadataEntry createSubclassedMetadataEntry() {
		return new RedalycMetadataEntry(this);
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		if (fullUrl == null) return null;
		String[] parts = fullUrl.split(CONTENT);
		return parts[1];
	}

	@Override
	public String getSearchUrl() {
		return REDALYC_SEARCH;
	}

//	@Override
	protected void hitListErrorMessage() {
		System.err.println("Cannot find metadata list: "+getHitListXPath());
	}
	
	@Override
	protected HtmlElement getArticleElement(HtmlHtml htmlHtml) {
		return (HtmlElement) XMLUtil.getFirstElement(htmlHtml, ".//*[local-name()='"+HtmlDiv.TAG+"' and starts-with(@class, '"+ARTICLE+" "+"')]");
	}
	
	@Override
	protected HtmlElement getSearchResultsList(HtmlBody body) {
		return (HtmlUl) XMLUtil.getFirstElement(body, getHitListXPath());
	}
	
	@Override
	protected String getHost() {
		return RedalycDownloader.REDALYC_HOST;
	}

	@Override
	protected String createLocalTreeName(String fileroot) {
		return fileroot.replace("/content/", "");
	}
	
	@Override
	protected void cleanSearchResultsList(HtmlElement searchResultsList) {
		XMLUtil.removeElementsByXPath(searchResultsList, CITE_EXTRAS_DIV);
	}

	/**
	https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
	 */
	@Override
	protected HitList createHitList(Element element) {
	//		<ul class="highwire-search-results-list">
		List<Element> ulList = XMLUtil.getQueryElements(element, 
				getHitListXPath());
		
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
	protected QuerySyntax getQuerySyntax() {
		throw new RuntimeException ("RedalycDownloader NYI");
//		return QuerySyntax.url;
	}





}
