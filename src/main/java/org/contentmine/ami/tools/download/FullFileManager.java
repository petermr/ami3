package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.cproject.files.CContainer;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;

public class FullFileManager extends AbstractSubDownloader {
	private static final Logger LOG = LogManager.getLogger(FullFileManager.class);
private HtmlHead preloadHead;

	public FullFileManager(AbstractDownloader abstractDownloader) {
		super(abstractDownloader);
		LOG.warn("formats "+abstractDownloader.getDownloadTool().getFulltextFormats());
	}
	
	/** ENTRY POINT */
	
	public void downloadHtmlPages(List<AbstractMetadataEntry> metadataEntryList) {
		for (AbstractMetadataEntry metadataEntry : metadataEntryList) {
			if (downloadCount >= downloadTool.getDownloadLimit()) {
				LOG.warn("download limit reached " + downloadCount + "/" + downloadTool.getDownloadLimit());
				break;
			}
			try {
				downloadRequestedFiles(metadataEntry);
				downloadCount++;
			} catch (IOException e) {
				LOG.error("Cannot download metadata Entry targets "+e.getMessage());
			}
		}
	}


	private void downloadRequestedFiles(AbstractMetadataEntry metadataEntry) throws IOException {
		String doi = metadataEntry.getCleanedDOIFromURL();
		File file = new File(abstractDownloader.cProject.getDirectory(), doi);
		if (file.exists()) {
			System.out.println("skipped: "+doi);
//			return;
		}

		HtmlHtml htmlPage = (HtmlHtml) metadataEntry.extractHtmlPage();
		if (htmlPage != null) {
			doi = metadataEntry.getCleanedDOIFromURL();
			abstractDownloader.currentTree = abstractDownloader.cProject.getExistingCTreeOrCreateNew(doi);
			writeHitListFileFOUR(htmlPage);
			preloadHead = htmlPage.getHead();
			
			writeDownloadedHtmlFile(AbstractDownloader.CITATION_ABSTRACT_HTML_URL, AbstractDownloader.ABSTRACT );
			writeDownloadedHtmlFile(AbstractDownloader.CITATION_FULL_HTML_URL, CTree.FULLTEXT);
			if (true /*fulltextFormats.contains*/ ) {
				File file1 = new File(abstractDownloader.currentTree.getDirectory(), CTree.FULLTEXT + "." + CTree.PDF);
				downloadAndWriteFile(AbstractDownloader.CITATION_PDF_URL, file1);
			}
			//view-source:file:///Users/pm286/workspace/cmdev/ami3/target/biorxiv/testsearch3/10_1101_2020_01_16_909614v1/hitList.html
		}
	}
	
	private void writeDownloadedHtmlFile(String urlString, String filename) throws IOException {
		File downloadedFile = new File(abstractDownloader.currentTree.getDirectory(), filename + "." + CTree.HTML);
		downloadAndWriteFile(urlString, downloadedFile);
	}

	private void downloadAndWriteFile(String urlString, File downloadedFile) throws IOException {
		if (downloadedFile.exists()) {
			System.out.println("skipping existing : "+downloadedFile.getName());
			return;
		}
		System.out.println("writing to :"+downloadedFile.getAbsolutePath());
		String url = preloadHead.getMetaElementValue(urlString);
		if (url == null) {
			System.out.println("null url: "+urlString);
			return;
		}
		CurlDownloader curlDownloader = new CurlDownloader()
				.setOutputFile(downloadedFile)
				.setUrlString(url);
		curlDownloader.run();
	}

	private void writeHitListFileFOUR(HtmlHtml htmlPage) throws IOException {
		File hitListFile = new File(abstractDownloader.currentTree.getOrCreateDirectory(), AbstractSubDownloader.HIT_LIST + "." + CTree.HTML);
		downloadTool.getResultsSetList().add(hitListFile.toString());
		if (htmlPage != null) {
			if (hitListFile.exists()) {
				System.out.println("skipping (existing) hitList: "+hitListFile.getParent());
			} else {
				XMLUtil.writeQuietly(htmlPage, hitListFile, 1);
			}
		}
	}
	
}
