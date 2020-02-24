package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** downloads the immediate results of a search to a ResultSet
 * 
 * @author pm286
 *
 */
public class FulltextManager extends AbstractSubDownloader {

private static final Logger LOG = Logger.getLogger(FulltextManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public FulltextManager(AbstractDownloader abstractDownloader) {
		super(abstractDownloader);
	}

	public void downloadFullTextAndRelatedFiles() {
		if (downloadTool.getRawFileFormats().size() > 0) {
			boolean force = true;
			CTreeList treeList = this.abstractDownloader.cProject.getOrCreateCTreeList(force);
			AbstractDownloader downloader = this.downloadTool.getDownloader();
			LandingPageManager landingPageManager = abstractDownloader.getOrCreateLandingPageManager();
			for (CTree cTree : treeList) {
				downloader.setCurrentTree(cTree);
				AbstractLandingPage landingPage = null;
				landingPage = landingPageManager.createCleanedLandingPage(cTree);
				if (landingPage != null) {
					try {
						this.downloadLink(downloader, landingPage);
					} catch (Exception e) {
						System.err.println("Cannot get landing page: "+cTree+"; "+e.getMessage());
					}
				}
			}
		}
	}
	
	/** 
	 * 
	 * @param abstractDownloader TODO
	 * @param landingPage
	 * @throws IOException
	 */
	private void downloadLink(AbstractDownloader abstractDownloader, AbstractLandingPage landingPage)
			throws IOException {
		String fullTextLink = landingPage.getHtmlLink();
		System.out.print(" "+fullTextLink.substring(fullTextLink.lastIndexOf("/") + 1));
		CurlDownloader curlDownloader = new CurlDownloader();
		File rawHtmlFile = new File(abstractDownloader.currentTree.getDirectory(), "rawFullText.html");
		curlDownloader.setOutputFile(rawHtmlFile);
		curlDownloader.setUrlString(fullTextLink);
		curlDownloader.run();
		System.out.println("download: "+curlDownloader.getCommandList());
		abstractDownloader.clean(rawHtmlFile);
		cleanAndOutputScholarlyHTML(abstractDownloader, rawHtmlFile);
	}

	private File cleanAndOutputScholarlyHTML(AbstractDownloader abstractDownloader, File file) {
		Element element = HtmlUtil.parseCleanlyToXHTML(file);
		HtmlHtml htmlHtml = (HtmlHtml) HtmlElement.create(element);
		HtmlBody body = htmlHtml.getBody();
		if (body == null) {
			System.err.println("null body");
			return null;
		}
		HtmlElement articleElement = abstractDownloader.getArticleElement(htmlHtml);
		AbstractSubDownloader.cleanHtmlRemoveLinkCommentEtc(htmlHtml);
		articleElement.detach();
		body.appendChild(articleElement);
		File cleanFile = new File(abstractDownloader.currentTree.getDirectory(), CTree.SCHOLARLY_HTML);
		XMLUtil.writeQuietly(htmlHtml, cleanFile, 1);
		return cleanFile;
	}


}
