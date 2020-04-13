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

	public void downloadFullTextAndRelatedFilesFromLandingPages() {
		if (downloadTool == null) {
			throw new RuntimeException("null downloadTool");
		}
		if (downloadTool.getFulltextFormats().size() == 0) {
			System.out.println("=======no output formats");
			return;
		}
		boolean force = true;
		CTreeList treeList = this.abstractDownloader.cProject.getOrCreateCTreeList(force);
		System.out.println("========\n CTrees "+treeList.size()+"\n========");
		AbstractDownloader downloader = this.downloadTool.getDownloader();
		LandingPageManager landingPageManager = abstractDownloader.getOrCreateLandingPageManager();
		List<String> cTreeNameList = landingPageManager.getCTreeNameList();
		System.out.println("LP "+cTreeNameList);
		for (String cTreeName : cTreeNameList) {
			CTree cTree = abstractDownloader.getCProject().getCTreeByName(cTreeName);
			downloader.setCurrentTree(cTree);
			AbstractLandingPage landingPage = landingPageManager.createCleanedLandingPage(cTree);
			if (landingPage != null) {
				try {
					this.downloadLink(downloader, landingPage);
				} catch (Exception e) {
					System.err.println("Cannot get landing page: "+cTree+"; "+e.getMessage());
				}
			} else {
				System.out.println("Null landingpage "+cTree.getName());
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
		System.out.println("curlDownload: "+curlDownloader.getCommandList());
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
		AbstractSubDownloader.cleanHtmlRemoveLinkCommentEtc(htmlHtml);
		HtmlElement articleElement = abstractDownloader.getArticleElement(htmlHtml);
		articleElement.detach();
		XMLUtil.removeChildren(body);
		body.appendChild(articleElement);
		File scholarlyFile = new File(abstractDownloader.currentTree.getDirectory(), CTree.SCHOLARLY_HTML);
		XMLUtil.writeQuietly(htmlHtml, scholarlyFile, 1);
		return scholarlyFile;
	}


}
