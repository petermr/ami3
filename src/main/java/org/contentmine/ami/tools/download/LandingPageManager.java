package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
public class LandingPageManager extends AbstractSubDownloader {


private static final Logger LOG = Logger.getLogger(LandingPageManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public LandingPageManager(AbstractDownloader abstractDownloader) {
		super(abstractDownloader);
	}

	public void downloadLandingPages() {
		if (downloadTool.resultSetList.size() > 0) {
			for (String resultSetFilename : downloadTool.resultSetList) {
				System.out.println("download files in resultSet "+resultSetFilename);
				downloadLandingPagesForResultSet(new File(resultSetFilename));
			}
		} else {
			System.err.println("NO RESULT SETS");
		}
	}
	
	/** an entry point from runSpecifics()
	 * 
	 * @param filename
	 */
	private void downloadLandingPagesForResultSet(File resultSetFile) {
		System.out.println("result set: " + resultSetFile);
//		abstractDownloader.setCProject(cProject);
	
		ResultSet resultSet = abstractDownloader.createResultSet(resultSetFile);
		List<String> fileroots = resultSet.getCitationLinks();
		String result = null;
		try {
			result = this.downloadLandingPagesWithCurl(fileroots);
		} catch (IOException e) {
			throw new RuntimeException("Cannot extract resultSet "+resultSetFile, e);
		}
		System.out.println("downloaded "+fileroots.size()+" files");
	}

	private HtmlHtml getLandingPageHtml(String content) {
		System.out.println("content "+content.length());
		HtmlHtml html = (HtmlHtml) HtmlElement.create(content);
		return html;
	}

	private String getLandingPageText(CTree cTree) {
		File landingPageFile = new File(cTree.getDirectory(), AbstractDownloader.LANDING_PAGE + "." + "html");
		String content = null;
		try {
			content = FileUtils.readFileToString(landingPageFile, CMineUtil.UTF8_CHARSET);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read "+landingPageFile, e);
		}
		return content;
	}
	
	/** maybe move to Downloader?
	 * 
	 * @param cTree
	 * @return
	 */
	AbstractLandingPage createCleanedLandingPage(CTree cTree) {
		String content = getLandingPageText(cTree);
		HtmlHtml landingPageHtml = null;
		try {
			content = abstractDownloader.clean(content);
			landingPageHtml = getLandingPageHtml(content);
		} catch (Exception e) {
			System.err.println("Bad parse ("  +cTree + ")"+e);
			return null;
		}
		AbstractLandingPage landingPage = this.createLandingPage();
		landingPage.readHtml(landingPageHtml);
		return landingPage;
	}

	private AbstractLandingPage createLandingPage() {
		return downloadTool == null ? null : downloadTool.getSite().createNewLandingPageObject();
	}


	/** 
	 * 
	 * @param abstractDownloader TODO
	 * @param fileroots
	 * @return
	 * @throws IOException
	 */
	private String downloadLandingPagesWithCurl(List<String> fileroots) throws IOException {
		CurlDownloader curlDownloader = new CurlDownloader();
		System.out.println("download with curl to <tree>scrapedMetadata.html" + fileroots);
		int size = fileroots.size();
		for (String fileroot : fileroots) {
			curlDownloader.addCurlPair(this.createLandingPageCurlPair(abstractDownloader.cProject.getDirectory(), fileroot));
		}
		
		curlDownloader.setTraceFile("target/trace.txt");
		curlDownloader.setTraceTime(true);
		System.out.println("running batched up curlDownloader for "+size+" landingPages, takes ca 1-5 sec/page ");
		String result = curlDownloader.run();
		System.out.println("ran curlDownloader for "+size+" landingPages ");
		// normally empty
		return result;
	}

	/** creates a file/url pair for use by curl
	 * manages all transformations
	 * 
	 * @param downloadDir
	 * @param fileroot
	 * @return
	 */
	private CurlPair createLandingPageCurlPair(File downloadDir, String fileroot) {
		File urlfile = this.createLandingPageFile(downloadDir, fileroot);
		URL url = abstractDownloader.createURL(fileroot);
		return new CurlPair(urlfile, url);
	}

	private File createLandingPageFile(File downloadDir, String fileroot) {
		String localTreeName = abstractDownloader.createLocalTreeName(fileroot);
		File cTreeDir = new File(downloadDir, 
				AbstractDownloader.replaceDOIPunctuationByUnderscore(localTreeName));
		cTreeDir.mkdirs();
		File urlfile = new File(cTreeDir, AbstractDownloader.LANDING_PAGE + "." + "html");
		return urlfile;
	}



}
