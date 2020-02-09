package org.contentmine.ami.tools.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlLink;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** extracts from biorxiv pages
 * 
 * 
LANDING PAGE
<html lang="en" dir="ltr" 
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:mml="http://www.w3.org/1998/Math/MathML">
  <head prefix="og: http://ogp.me/ns# article: http://ogp.me/ns/article# book: http://ogp.me/ns/book#" >
    <!--[if IE]><![endif]-->
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=3, minimum-scale=1, user-scalable=yes" />
<link rel="shortcut icon" href="https://www.biorxiv.org/sites/default/files/images/favicon.ico" type="image/vnd.microsoft.icon" />
<meta name="type" content="article" />
<meta name="HW.identifier" content="/biorxiv/early/2020/02/05/2020.02.02.931162.atom" />
<meta name="HW.pisa" content="biorxiv;2020.02.02.931162v2" />              
<meta name="DC.Format" content="text/html" />
<meta name="DC.Language" content="en" />
<meta name="DC.Title" content="Genomic variance of the 2019-nCoV coronavirus" />   **TITLE**
<meta name="DC.Identifier" content="10.1101/2020.02.02.931162" />                  **DOI**
<meta name="DC.Date" content="2020-02-05" />                                       **DATE**
<meta name="DC.Publisher" content="Cold Spring Harbor Laboratory" />               **PUBL**
<meta name="DC.Rights" content="© 2020, Posted by Cold Spring Harbor Laboratory. This pre-print is available under a Creative Commons License (Attribution-NonCommercial 4.0 International), CC BY-NC 4.0, as described at http://creativecommons.org/licenses/by-nc/4.0/" />
<meta name="DC.AccessRights" content="restricted" />
<meta name="DC.Description" content="There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches." />
<meta name="DC.Contributor" content="Carmine Ceraolo" />                           **AUTH**
<meta name="DC.Contributor" content="Federico M Giorgi" />                         **AUTH**
<meta name="article:published_time" content="2020-02-05" />
<meta name="article:section" content="New Results" />
<meta name="citation_title" content="Genomic variance of the 2019-nCoV coronavirus" />  **TITLE**
                                                                                    **ABSTRACT**
<meta name="citation_abstract" lang="en" content="&lt;p&gt;There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&amp;gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches.&lt;/p&gt;" />
<meta name="citation_journal_title" content="bioRxiv" />   
<meta name="citation_publisher" content="Cold Spring Harbor Laboratory" />          *PUBL**
<meta name="citation_publication_date" content="2020/01/01" />
<meta name="citation_mjid" content="biorxiv;2020.02.02.931162v2" />
<meta name="citation_id" content="2020.02.02.931162v2" />
<meta name="citation_public_url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2" />   **THIS URL**
<meta name="citation_abstract_html_url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2.abstract" />  **ABSTRACT URL**
<meta name="citation_full_html_url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2.full" />    **FULLTEXT**
<meta name="citation_pdf_url" content="https://www.biorxiv.org/content/biorxiv/early/2020/02/05/2020.02.02.931162.full.pdf" />  **PDF**
<meta name="citation_doi" content="10.1101/2020.02.02.931162" />                    **DOI**
<meta name="citation_section" content="New Results" />
<meta name="citation_firstpage" content="2020.02.02.931162" />
<meta name="citation_author" content="Carmine Ceraolo" />
<meta name="citation_author_institution" content="University of Bologna" />         **AUTH INSTITUTION **
<meta name="citation_author_email" content="carmine.ceraolo@studio.unibo.it" />
<meta name="citation_author" content="Federico M Giorgi" />
<meta name="citation_author_institution" content="University of Bologna" />
<meta name="citation_author_email" content="federico.giorgi@unibo.it" />
<meta name="citation_author_orcid" content="http://orcid.org/0000-0002-7325-9908" />
<meta name="twitter:title" content="Genomic variance of the 2019-nCoV coronavirus" />
<meta name="twitter:site" content="@biorxivpreprint" />
<meta name="twitter:card" content="summary" />
<meta name="twitter:image" content="https://www.biorxiv.org/sites/default/files/images/biorxiv_logo_homepage7-5-small.png" />
<meta name="twitter:description" content="There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches." />
<meta name="og-title" property="og:title" content="Genomic variance of the 2019-nCoV coronavirus" />
<meta name="og-url" property="og:url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2" />
<meta name="og-site-name" property="og:site_name" content="bioRxiv" />
<meta name="og-description" property="og:description" content="There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches." />
<meta name="og-type" property="og:type" content="article" />
<meta name="og-image" property="og:image" content="https://www.biorxiv.org/sites/default/files/images/biorxiv_logo_homepage7-5-small.png" />
<meta name="citation_date" content="2020-02-05" />
<link rel="alternate" type="application/pdf" title="Full Text (PDF)" href="/content/10.1101/2020.02.02.931162v2.full.pdf" />
<meta name="description" content="bioRxiv - the preprint server for biology, operated by Cold Spring Harbor Laboratory, a research and educational institution" />
<meta name="generator" content="Drupal 7 (http://drupal.org)" />
<link rel="canonical" href="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2" />
<link rel="shortlink" href="https://www.biorxiv.org/node/1135486" />
    <title>Genomic variance of the 2019-nCoV coronavirus | bioRxiv</title>  
    <link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__jMRAK66KMC1e4TQlwUNn3KiWVDC5AjueUAYEm1xBY_U__KSUjdT4jdcJ4qJz6fKY1K9WkYh1a5EcaWZxt_-zbTis__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" />
<link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/all/modules/highwire/highwire/highwire.style.highwire.css?q592sk" media="all" />
<link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__dpm0KDJn7oKSDxkJw3jhATwn0_pEtlw6kn8ze-Kh9Z4__eRjyMOAvxlD1OnK8R0sTWY9qUfl_uvfpH5kYveuHwlY__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" />
<link type="text/css" rel="stylesheet" href="//cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" media="all" />
<link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__q48SDxVDWut_cH6dTPyQe7HjADuSLLmJVFztuiJEFrU__DJqddQGjm5dXCHsaiwNriGytKN5eevIQqyjdq65TZtE__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" />
<style type="text/css" media="all">
</style>
// links to CSS files
<link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__ElJr3PIJEvw3qLXc1cnYiLj2G4KgDPSXFOfm6Phf8hw__JdWGm15cDWjsK6KrFlQVXQix9YgNeYysf22XZHj-Y-c__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" />
...
// Scripts clipped
  </head>
  
 * @author pm286
 *
 */
public class BiorxivDownloader extends AbstractDownloader {

	private static final String HIGHWIRE_CITE_EXTRAS = "highwire-cite-extras";
	private static final String CITE_EXTRAS_DIV = ".//*[local-name()='"+HtmlDiv.TAG+"' and @class='" + HIGHWIRE_CITE_EXTRAS + "']";

	static final Logger LOG = Logger.getLogger(BiorxivDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String HIGHWIRE_SEARCH_RESULTS_LIST = "highwire-search-results-list";
	
	public static final String BIORXIV_HOST = "www.biorxiv.org";
	public static final String BIORXIV_BASE = HTTPS + P2H + BIORXIV_HOST;
	public static final String BIORXIV_SEARCH = BIORXIV_BASE + "/search/";
	public static final String BIORXIV_HEADER = "/content/";

	
	public BiorxivDownloader() {
		init();
	}

	private void init() {
		this.setBase(BIORXIV_BASE);
	}

//	public BiorxivDownloader(CProject cProject) {
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
	@Override
	public ResultSet createResultSet(String result) {
		Element element = HtmlUtil.parseCleanlyToXHTML(result);
		List<Element> ulList = XMLUtil.getQueryElements(element, 
				".//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']");
		if (ulList.size() == 0) {
			System.err.println("empty array");
			return new ResultSet();
		}
		Element ul = ulList.get(0);
		return createResultSet(ul);
	}

	
	@Override
	/** creates new MetadataEntry populated with contents of contentElement
	 * called when creating (or extending) a ResultSet
	 * 
	 */
	protected AbstractMetadataEntry createMetadataEntry(Element contentElement) {
		BiorxivMetadataEntry metadataEntry = new BiorxivMetadataEntry(this);
		metadataEntry.read(contentElement);
		return metadataEntry;
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		if (fullUrl == null) return null;
		String[] parts = fullUrl.split("content/");
		return parts[1];
	}

	public static String getSearchUrl() {
		return BIORXIV_SEARCH;
	}

	@Override
	protected File cleanAndOutputResultSetFile(File file) {
		Element element = HtmlUtil.parseCleanlyToXHTML(file);
		HtmlHtml htmlHtml = (HtmlHtml) HtmlElement.create(element);
		HtmlBody body = htmlHtml.getBody();
		if (body == null) {
			System.err.println("null body");
			return null;
		}
		HtmlUl searchResultsList = cleanAndDetachSearchResults(body);
		if (searchResultsList == null) {
			System.err.println("Cannout find metadata list: "+("//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']"));
			return null;
		}
		XMLUtil.removeElementsByTag(htmlHtml, HtmlLink.TAG, HtmlStyle.TAG); 
		XMLUtil.removeNodesByXPath(htmlHtml, "//comment()"); 
		XMLUtil.removeChildren(body);
		body.appendChild(searchResultsList);
		File cleanFile = new File(file.getAbsoluteFile().toString().replace(".html", "." + AbstractDownloader.CLEAN + ".html"));
		XMLUtil.writeQuietly(htmlHtml, cleanFile, 1);
		return cleanFile;
	}

	private HtmlUl cleanAndDetachSearchResults(HtmlBody body) {
		HtmlUl searchResultsList;
		// <ul class="highwire-search-results-list">
		searchResultsList = (HtmlUl) XMLUtil.getFirstElement(body, "//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']");
	    XMLUtil.removeElementsByXPath(searchResultsList, CITE_EXTRAS_DIV);
		searchResultsList.detach();
		return searchResultsList;
	}

	
	public static File createLandingPageFile(File downloadDir, String fileroot) {
		File ctreedir = new File(downloadDir, 
				AbstractDownloader.replaceDOIPunctuationByUnderscore(fileroot.replace("/content/", "")));
		ctreedir.mkdirs();
		File urlfile = new File(ctreedir, AbstractDownloader.LANDING_PAGE + "." + "html");
		return urlfile;
	}

	public static URL createURL(String fileroot) {
		URL url = null;
		try {
			url = new URL(AbstractDownloader.HTTPS, BiorxivDownloader.BIORXIV_HOST, fileroot);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Cannot create URL", e);
		}
		return url;
	}

	/** creates a file/url pair for use bu curl
	 * manages all transformations
	 * 
	 * @param downloadDir
	 * @param fileroot
	 * @return
	 */
	public static CurlPair createCurlPair(File downloadDir, String fileroot) {
		File urlfile = BiorxivDownloader.createLandingPageFile(downloadDir, fileroot);
		URL url = BiorxivDownloader.createURL(fileroot);
		return new CurlPair(urlfile, url);
	}

	
}
