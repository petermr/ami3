package org.contentmine.ami.tools.download.biorxiv;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.HitList;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlUl;

import nu.xom.Element;

public abstract class CSHRxivDownloader extends AbstractDownloader {

	private static final Logger LOG = Logger.getLogger(CSHRxivDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected static final String ARTICLE = "article";
	protected static final String CONTENT = "content/";
	protected static final String HIGHWIRE_CITE_EXTRAS = "highwire-cite-extras";
	static final String CITE_EXTRAS_DIV = ".//*[local-name()='"+HtmlDiv.TAG+"' and @class='" + HIGHWIRE_CITE_EXTRAS + "']";

	protected static final String HIGHWIRE_SEARCH_RESULTS_LIST = "highwire-search-results-list";
	public static final String URL_HEADER = "/content/";

	public CSHRxivDownloader(CProject cProject) {
		super(cProject);
	}

	public CSHRxivDownloader() {
	}

	@Override
	protected String getHitListXPath() {
		return "//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']";
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		if (fullUrl == null) return null;
		String[] parts = fullUrl.split(CONTENT);
		return parts[1];
	}

	protected void hitListErrorMessage() {
		System.err.println("Cannot find metadata list: "+getHitListXPath());
	}

	@Override
	protected HtmlElement getArticleElement(HtmlHtml htmlHtml) {
		return (HtmlElement) XMLUtil.getFirstElement(htmlHtml, 
				".//*[local-name()='"+HtmlDiv.TAG+"' and starts-with(@class, '"+ARTICLE+" "+"')]");
	}

	@Override
	protected HtmlElement getSearchResultsList(HtmlBody body) {
		return (HtmlUl) XMLUtil.getFirstElement(body, getHitListXPath());
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

	/** compute pagenumber to download.
	 * This is because biorxiv uses ZERO counting. The default here is ONE-based counting
	 * 
	 * Override to return zero-based counting
	 * 
	 * @param page
	 * @return
	 */
	@Override
	public Integer computePageNumber(Integer page) {
		return page - 1;
	}

	@Override
	protected void cleanSearchResultsList(HtmlElement searchResultsList) {
		XMLUtil.removeElementsByXPath(searchResultsList, CITE_EXTRAS_DIV);
	}

	@Override
	protected String createLocalTreeName(String fileroot) {
		return fileroot.replace(URL_HEADER, "");
	}
}
