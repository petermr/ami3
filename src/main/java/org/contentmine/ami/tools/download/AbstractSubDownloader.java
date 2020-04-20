package org.contentmine.ami.tools.download;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlForm;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlI;
import org.contentmine.graphics.html.HtmlLink;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlUl;


public abstract class AbstractSubDownloader {
	private static final Logger LOG = Logger.getLogger(AbstractSubDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String HIT_LIST = "hitList";
	public static final String CLEAN = "clean";

	protected AbstractDownloader abstractDownloader;
	protected AMIDownloadTool downloadTool;
	protected HitList hitList;
	protected int downloadCount;


	public AbstractSubDownloader(AbstractDownloader abstractDownloader) {
		super();
		this.abstractDownloader = abstractDownloader;
		this.downloadTool = abstractDownloader.getDownloadTool();
		if (downloadTool == null) {
			throw new RuntimeException("null downloadTool");
		}
//		this.cProject = downloadTool.getCProject();
	}


	public static void cleanHtmlRemoveLinkCommentEtc(HtmlHtml htmlHtml) {
//		LOG.debug("skip");
		XMLUtil.removeElementsByTag(htmlHtml, HtmlStyle.TAG, HtmlForm.TAG); 
		XMLUtil.removeNodesByXPath(htmlHtml, "//*[local-name()='"+HtmlHead.TAG+"']//*[local-name()='"+HtmlLink.TAG+"']"); 
		XMLUtil.removeNodesByXPath(htmlHtml, "//comment()"); 
		XMLUtil.removeEmptyNodes(htmlHtml, HtmlB.TAG, HtmlI.TAG);
	}


	void replaceBodyChildrenByHitList(HtmlBody body, HtmlElement searchResultsList) {
		XMLUtil.removeChildren(body);
		body.appendChild(searchResultsList);
	}


	protected HtmlElement cleanAndDetachSearchResults(HtmlBody body) {
		HtmlElement searchResultsList = (HtmlUl) abstractDownloader.getSearchResultsList(body);
		if (searchResultsList != null) {
		    abstractDownloader.cleanSearchResultsList(searchResultsList);
			searchResultsList.detach();
		}
		return searchResultsList;
	}

}
