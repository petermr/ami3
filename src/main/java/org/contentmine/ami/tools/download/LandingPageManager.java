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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** downloads the entries of a search/HitList to individual landingPages
 * 
 * @author pm286
 *
 */
/** typical HTML landing page (BioRxiv)
 * script and style and link largely ==SNIP=='ed for clarity
 * 
&lt;!DOCTYPE html&gt;
&lt;html lang="en" dir="ltr" 
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:mml="http://www.w3.org/1998/Math/MathML"&gt;
  &lt;head prefix="og: http://ogp.me/ns# article: http://ogp.me/ns/article# book: http://ogp.me/ns/book#" &gt;
&lt;meta http-equiv="Content-Type" content="text/html; charset=utf-8" /&gt;
&lt;link&gt;s ==SNIP=='ed
&lt;meta name="type" content="article" /&gt;
&lt;meta name="category" content="article" /&gt;
&lt;meta name="HW.identifier" content="/biorxiv/early/2020/01/30/2020.01.30.926477.atom" /&gt;
&lt;meta name="HW.pisa" content="biorxiv;2020.01.30.926477v1" /&gt;
&lt;meta name="DC.Format" content="text/html" /&gt;
&lt;meta name="DC.Language" content="en" /&gt;
&lt;meta name="DC.Title" content="Evolution and variation of 2019-novel coronavirus" /&gt;
&lt;meta name="DC.Identifier" content="10.1101/2020.01.30.926477" /&gt;
&lt;meta name="DC.Date" content="2020-01-30" /&gt;
&lt;meta name="DC.Publisher" content="Cold Spring Harbor Laboratory" /&gt;
&lt;meta name="DC.Rights" content="© 2020, Posted by Cold Spring Harbor Laboratory. This pre-print is available under a Creative Commons License (Attribution-NonCommercial-NoDerivs 4.0 International), CC BY-NC-ND 4.0, as described at http://creativecommons.org/licenses/by-nc-nd/4.0/" /&gt;
&lt;meta name="DC.AccessRights" content="restricted" /&gt;
&lt;meta name="DC.Description" content="Background The current outbreak caused by novel coronavirus (2019-nCoV) in China has become a worldwide concern. As of 28 January 2020, there were 4631 confirmed cases and 106 deaths, and 11 countries or regions were affected.
==SNIP== most of the text
Methods We downloaded ...
Results An isolate ...
Conclusion Our analysis ...

*   CoVs
    :   Coronaviruses
    ==SNIP==
    ESSs
    :   Effective sample sizes" /&gt;
&lt;meta name="DC.Contributor" content="Chenglong Xiong" /&gt;
&lt;meta name="DC.Contributor" content="Lufang Jiang" /&gt;
...
&lt;meta name="article:published_time" content="2020-01-30" /&gt;
&lt;meta name="article:section" content="New Results" /&gt;
&lt;!-- TITLE --&gt;
&lt;meta name="citation_title" content="Evolution and variation of 2019-novel coronavirus" /&gt;
&lt;meta name="citation_abstract" lang="en" content="&lt;p&gt;Background: The current outbreak caused by novel coronavirus (2019-nCoV) in China has become a worldwide concern. As of 28 January 2020, there were 4631 confirmed cases and 106 deaths, and 11 countries or regions were affected. 
Methods: We downloaded the genomes of 2019-nCoVs and similar isolates from the Global Initiative on Sharing Avian Influenza Database (GISAID and nucleotide database of the National Center for Biotechnology Information (NCBI). Lasergene 7.0 and MEGA 6.0 softwares were used to calculate genetic distances of the sequences, to construct phylogenetic trees, and to align amino acid sequences. Bayesian coalescent phylogenetic analysis, implemented in the BEAST software package, was used to calculate the molecular clock related characteristics such as the nucleotide substitution rate and the most recent common ancestor (tMRCA) of 2019-nCoVs.
Results: An isolate numbered EPI_ISL_403928 showed different phylogenetic trees and genetic distances of the whole length genome, the coding sequences (CDS) of ployprotein (P), spike protein (S), and nucleoprotein (N) from other 2019-nCoVs. There are 22, 4, 2 variations in P, S, and N at the level of amino acid residues. The nucleotide substitution rates from high to low are 1.05 × 10-2 (nucleotide substitutions/site/year, with 95% HPD interval being 6.27 × 10-4 to 2.72 × 10-2) for N, 5.34 × 10-3 (5.10 × 10-4, 1.28 × 10-2) for S, 1.69 × 10-3 (3.94 × 10-4, 3.60 × 10-3) for P, 1.65 × 10-3 (4.47 × 10-4, 3.24 × 10-3) for the whole genome, respectively. At this nucleotide substitution rate, the most recent common ancestor (tMRCA) of 2019-nCoVs appeared about 0.253-0.594 year before the epidemic.
Conclusion: Our analysis suggests that at least two different viral strains of 2019-nCoV are involved in this outbreak that might occur a few months earlier before it was officially reported.&lt;/p&gt;" /&gt;
&lt;!-- JOURNAL --&gt;
&lt;meta name="citation_journal_title" content="bioRxiv" /&gt;
&lt;meta name="citation_publisher" content="Cold Spring Harbor Laboratory" /&gt;
&lt;!-- DATE --&gt;
&lt;meta name="citation_publication_date" content="2020/01/01" /&gt;
&lt;meta name="citation_mjid" content="biorxiv;2020.01.30.926477v1" /&gt;
&lt;meta name="citation_id" content="2020.01.30.926477v1" /&gt;
&lt;!-- LINK TO THIS PAGE --&gt;
&lt;meta name="citation_public_url" content="https://www.biorxiv.org/content/10.1101/2020.01.30.926477v1" /&gt;
&lt;!-- LINK TO SEPARATE ABSTRACT --&gt;
&lt;meta name="citation_abstract_html_url" content="https://www.biorxiv.org/content/10.1101/2020.01.30.926477v1.abstract" /&gt;
&lt;!-- LINK TO FULL HTML TEXT --&gt;
&lt;meta name="citation_full_html_url" content="https://www.biorxiv.org/content/10.1101/2020.01.30.926477v1.full" /&gt;
&lt;!-- LINK TO FULL PDF TEXT --&gt;
&lt;meta name="citation_pdf_url" content="https://www.biorxiv.org/content/biorxiv/early/2020/01/30/2020.01.30.926477.full.pdf" /&gt;
&lt;!-- DOI --&gt;
&lt;meta name="citation_doi" content="10.1101/2020.01.30.926477" /&gt;
&lt;meta name="citation_num_pages" content="17" /&gt;
&lt;meta name="citation_article_type" content="Article" /&gt;
&lt;meta name="citation_section" content="New Results" /&gt;
&lt;meta name="citation_firstpage" content="2020.01.30.926477" /&gt;
&lt;!-- AUTHOR and INSTITUTION/s and ORCID maybe EMAIL --&gt;
&lt;meta name="citation_author" content="Chenglong Xiong" /&gt;
&lt;meta name="citation_author_institution" content="Department of Public Health Microbiology, School of Public Health, Fudan University" /&gt;
&lt;meta name="citation_author_institution" content="School of Public Health, Fudan University, Key Laboratory of Public Health Safety" /&gt;
&lt;meta name="citation_author_orcid" content="http://orcid.org/0000-0003-4750-3572" /&gt;
...
&lt;meta name="citation_author_email" content="jiangqw@fudan.edu.cn" /&gt;
&lt;!-- REFERENCES --&gt;
&lt;meta name="citation_reference" content="Wong ACP, Li X, Lau SKP, Woo PCY. Global epidemiology of bat coronaviruses. Viruses. 2019; 11: pii: E174." /&gt;
...
&lt;!-- DATE --&gt;
&lt;meta name="citation_date" content="2020-01-30" /&gt;
&lt;!-- RELATIVE PDF LINK --&gt;
&lt;link rel="alternate" type="application/pdf" title="Full Text (PDF)" href="/content/10.1101/2020.01.30.926477v1.full.pdf" /&gt;
&lt;!-- RELATIVE TXT LINK (actual formatted Unicode) --&gt;
&lt;link rel="alternate" type="text/plain" title="Full Text (Plain)" href="/content/10.1101/2020.01.30.926477v1.full.txt" /&gt;
&lt;!-- RELATIVE PPT LINK (ZERO BYTES ON THIS EXAMPLE, BUT  MAYBE WORKS ELSEWISE) --&gt;
&lt;link rel="alternate" type="application/vnd.ms-powerpoint" title="Powerpoint" href="/content/10.1101/2020.01.30.926477v1.ppt" /&gt;
&lt;!-- ANOTHER DESCRIPTION --&gt;
&lt;meta name="description" content="bioRxiv - the preprint server for biology, operated by Cold Spring Harbor Laboratory, a research and educational institution" /&gt;
&lt;link rel="canonical" href="https://www.biorxiv.org/content/10.1101/2020.01.30.926477v1" /&gt;
&lt;link rel="shortlink" href="https://www.biorxiv.org/node/1127513" /&gt;
    &lt;title&gt;Evolution and variation of 2019-novel coronavirus | bioRxiv&lt;/title&gt;  
&lt;style type="text/css" media="all"&gt; ...
/ * &lt;![CDATA[ * / SNIPPED
 &lt;/style&gt;
  &lt;/head&gt;
  &lt;body class="html not-front not-logged-in page-node page-node- page-node-1127513 node-type-highwire-article context-content hw-default-jcode-biorxiv hw-article-type-article hw-article-category-new-results"&gt;
        &lt;div class="page clearfix page-box-shadows footer-borders panels-page panels-layout-jcore_2col" id="page"&gt;
      &lt;header id="section-header" class="section section-header"&gt;
&lt;!-- BODY SNIPPED --&gt;
    

  &lt;/body&gt;
&lt;/html&gt;

 *
 */
public class LandingPageManager extends AbstractSubDownloader {


	private static final Logger LOG = LogManager.getLogger(LandingPageManager.class);
	private List<String> landingPageFilerootList = new ArrayList<>();
	private List<String> cTreeNameList;

	public LandingPageManager(AbstractDownloader abstractDownloader) {
		super(abstractDownloader);
	}

	public void downloadLandingPages() {
		landingPageFilerootList = new ArrayList<>();
		cTreeNameList = new ArrayList<>();
		if (downloadTool.hitListList.size() > 0) {
			for (String hitListFilename : downloadTool.hitListList) {
				System.out.println("download files in hitList "+hitListFilename);
				downloadLandingPagesForHitList(new File(hitListFilename));
			}
		} else {
			System.err.println("NO RESULT SETS");
		}
	}
	
	/** an entry point from runSpecifics()
	 * 
	 * @param filename
	 */
	private void downloadLandingPagesForHitList(File hitListFile) {
		System.out.println("result set: " + hitListFile);
//		abstractDownloader.setCProject(cProject);
	
		HitList hitList = abstractDownloader.createHitList(hitListFile);
		List<String> fileroots = hitList.getCitationLinks();
		landingPageFilerootList.addAll(fileroots);
		String result = null;
		try {
			result = this.downloadLandingPagesWithCurl(fileroots);
		} catch (IOException e) {
			throw new RuntimeException("Cannot extract hitList "+hitListFile, e);
		}
		System.out.println("--------\n+downloaded "+fileroots.size()+" files for "+hitListFile+"\n--------");
	}

	private HtmlHtml getLandingPageHtml(String content) {
		System.out.println("content "+content.length());
		HtmlHtml html = (HtmlHtml) HtmlElement.create(content);
		return html;
	}

	private String getLandingPageText(CTree cTree) {
		File landingPageFile = new File(cTree.getDirectory(), AbstractDownloader.LANDING_PAGE + "." + "html");
		String content = null;
		if (landingPageFile.exists()) {
			try {
				content = FileUtils.readFileToString(landingPageFile, CMineUtil.UTF8_CHARSET);
			} catch (IOException e) {
				System.err.println("Cannot read "+landingPageFile + e.getMessage());
			}
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
		if (content == null) {
			System.err.println("no landing page for: "+cTree);
			return null;
		}
		AbstractLandingPage landingPage = null;
		HtmlHtml landingPageHtml = null;
		try {
			content = abstractDownloader.clean(content);
			landingPageHtml = getLandingPageHtml(content);
		} catch (Exception e) {
			System.err.println("Bad parse ("  +cTree + ")"+e);
			return null;
		}
		landingPage = this.createLandingPage();
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
		File directory = abstractDownloader.cProject.getDirectory();
		LOG.debug(directory);
		for (String fileroot : fileroots) {
			CurlPair curlPair = this.createLandingPageCurlPair(directory, fileroot);
//			System.out.println("curl pair: "+curlPair);
			curlDownloader.addCurlPair(curlPair);
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
		String cTreeName = AbstractDownloader.replaceDOIPunctuationByUnderscore(localTreeName);
		File cTreeDir = new File(downloadDir, cTreeName);
		cTreeDir.mkdirs();
		cTreeNameList.add(cTreeName);
		File urlfile = new File(cTreeDir, AbstractDownloader.LANDING_PAGE + "." + "html");
		return urlfile;
	}

//	public List<String> getLandingPageFilerootList() {
//		return landingPageFilerootList;
//	}
	
	public List<String> getCTreeNameList() {
		return cTreeNameList;
	}

	public int size() {
		return landingPageFilerootList == null ? 0 : landingPageFilerootList.size();
	}



}
