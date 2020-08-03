package org.contentmine.ami.tools.download.biorxiv;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

/** extracts from biorxiv pages
 * 
 * 
LANDING PAGE
&lt;html lang="en" dir="ltr" 
  xmlns="http://www.w3.org/1999/xhtml"
  xmlns:mml="http://www.w3.org/1998/Math/MathML"&gt;
  &lt;head prefix="og: http://ogp.me/ns# article: http://ogp.me/ns/article# book: http://ogp.me/ns/book#" &gt;
    &lt;!--[if IE]&gt;&lt;![endif]--&gt;
&lt;meta http-equiv="Content-Type" content="text/html; charset=utf-8" /&gt;
&lt;meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=3, minimum-scale=1, user-scalable=yes" /&gt;
&lt;link rel="shortcut icon" href="https://www.biorxiv.org/sites/default/files/images/favicon.ico" type="image/vnd.microsoft.icon" /&gt;
&lt;meta name="type" content="article" /&gt;
&lt;meta name="HW.identifier" content="/biorxiv/early/2020/02/05/2020.02.02.931162.atom" /&gt;
&lt;meta name="HW.pisa" content="biorxiv;2020.02.02.931162v2" /&gt;              
&lt;meta name="DC.Format" content="text/html" /&gt;
&lt;meta name="DC.Language" content="en" /&gt;
&lt;meta name="DC.Title" content="Genomic variance of the 2019-nCoV coronavirus" /&gt;   **TITLE**
&lt;meta name="DC.Identifier" content="10.1101/2020.02.02.931162" /&gt;                  **DOI**
&lt;meta name="DC.Date" content="2020-02-05" /&gt;                                       **DATE**
&lt;meta name="DC.Publisher" content="Cold Spring Harbor Laboratory" /&gt;               **PUBL**
&lt;meta name="DC.Rights" content="© 2020, Posted by Cold Spring Harbor Laboratory. This pre-print is available under a Creative Commons License (Attribution-NonCommercial 4.0 International), CC BY-NC 4.0, as described at http://creativecommons.org/licenses/by-nc/4.0/" /&gt;
&lt;meta name="DC.AccessRights" content="restricted" /&gt;
&lt;meta name="DC.Description" content="There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches." /&gt;
&lt;meta name="DC.Contributor" content="Carmine Ceraolo" /&gt;                           **AUTH**
&lt;meta name="DC.Contributor" content="Federico M Giorgi" /&gt;                         **AUTH**
&lt;meta name="article:published_time" content="2020-02-05" /&gt;
&lt;meta name="article:section" content="New Results" /&gt;
&lt;meta name="citation_title" content="Genomic variance of the 2019-nCoV coronavirus" /&gt;  **TITLE**
                                                                                    **ABSTRACT**
&lt;meta name="citation_abstract" lang="en" content="&lt;p&gt;There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&amp;gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches.&lt;/p&gt;" /&gt;
&lt;meta name="citation_journal_title" content="bioRxiv" /&gt;   
&lt;meta name="citation_publisher" content="Cold Spring Harbor Laboratory" /&gt;          *PUBL**
&lt;meta name="citation_publication_date" content="2020/01/01" /&gt;
&lt;meta name="citation_mjid" content="biorxiv;2020.02.02.931162v2" /&gt;
&lt;meta name="citation_id" content="2020.02.02.931162v2" /&gt;
&lt;meta name="citation_public_url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2" /&gt;   **THIS URL**
&lt;meta name="citation_abstract_html_url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2.abstract" /&gt;  **ABSTRACT URL**
&lt;meta name="citation_full_html_url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2.full" /&gt;    **FULLTEXT**
&lt;meta name="citation_pdf_url" content="https://www.biorxiv.org/content/biorxiv/early/2020/02/05/2020.02.02.931162.full.pdf" /&gt;  **PDF**
&lt;meta name="citation_doi" content="10.1101/2020.02.02.931162" /&gt;                    **DOI**
&lt;meta name="citation_section" content="New Results" /&gt;
&lt;meta name="citation_firstpage" content="2020.02.02.931162" /&gt;
&lt;meta name="citation_author" content="Carmine Ceraolo" /&gt;
&lt;meta name="citation_author_institution" content="University of Bologna" /&gt;         **AUTH INSTITUTION **
&lt;meta name="citation_author_email" content="carmine.ceraolo@studio.unibo.it" /&gt;
&lt;meta name="citation_author" content="Federico M Giorgi" /&gt;
&lt;meta name="citation_author_institution" content="University of Bologna" /&gt;
&lt;meta name="citation_author_email" content="federico.giorgi@unibo.it" /&gt;
&lt;meta name="citation_author_orcid" content="http://orcid.org/0000-0002-7325-9908" /&gt;
&lt;meta name="twitter:title" content="Genomic variance of the 2019-nCoV coronavirus" /&gt;
&lt;meta name="twitter:site" content="@biorxivpreprint" /&gt;
&lt;meta name="twitter:card" content="summary" /&gt;
&lt;meta name="twitter:image" content="https://www.biorxiv.org/sites/default/files/images/biorxiv_logo_homepage7-5-small.png" /&gt;
&lt;meta name="twitter:description" content="There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches." /&gt;
&lt;meta name="og-title" property="og:title" content="Genomic variance of the 2019-nCoV coronavirus" /&gt;
&lt;meta name="og-url" property="og:url" content="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2" /&gt;
&lt;meta name="og-site-name" property="og:site_name" content="bioRxiv" /&gt;
&lt;meta name="og-description" property="og:description" content="There is rising global concern for the recently emerged novel Coronavirus (2019-nCov). Full genomic sequences have been released by the worldwide scientific community in the last few weeks in order to understand the evolutionary origin and molecular characteristics of this virus. Taking advantage of all the genomic information currently available, we constructed a phylogenetic tree including also representatives of other coronaviridae, such as Bat coronavirus (BCoV) and SARS. We confirm high sequence similarity (&gt;99%) between all sequenced 2019-nCoVs genomes available, with the closest BCoV sequence sharing 96.2% sequence identity, confirming the notion of a zoonotic origin of 2019-nCoV. Despite the low heterogeneity of the 2019-nCoV genomes, we could identify at least two hyper-variable genomic hotspots, one of which is responsible for a Serine/Leucine variation in the viral ORF8-encoded protein. Finally, we perform a full proteomic comparison with other coronaviridae, identifying key aminoacidic differences to be considered for antiviral strategies deriving from previous anti-coronavirus approaches." /&gt;
&lt;meta name="og-type" property="og:type" content="article" /&gt;
&lt;meta name="og-image" property="og:image" content="https://www.biorxiv.org/sites/default/files/images/biorxiv_logo_homepage7-5-small.png" /&gt;
&lt;meta name="citation_date" content="2020-02-05" /&gt;
&lt;link rel="alternate" type="application/pdf" title="Full Text (PDF)" href="/content/10.1101/2020.02.02.931162v2.full.pdf" /&gt;
&lt;meta name="description" content="bioRxiv - the preprint server for biology, operated by Cold Spring Harbor Laboratory, a research and educational institution" /&gt;
&lt;meta name="generator" content="Drupal 7 (http://drupal.org)" /&gt;
&lt;link rel="canonical" href="https://www.biorxiv.org/content/10.1101/2020.02.02.931162v2" /&gt;
&lt;link rel="shortlink" href="https://www.biorxiv.org/node/1135486" /&gt;
    &lt;title&gt;Genomic variance of the 2019-nCoV coronavirus | bioRxiv&lt;/title&gt;  
    &lt;link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__jMRAK66KMC1e4TQlwUNn3KiWVDC5AjueUAYEm1xBY_U__KSUjdT4jdcJ4qJz6fKY1K9WkYh1a5EcaWZxt_-zbTis__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" /&gt;
&lt;link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/all/modules/highwire/highwire/highwire.style.highwire.css?q592sk" media="all" /&gt;
&lt;link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__dpm0KDJn7oKSDxkJw3jhATwn0_pEtlw6kn8ze-Kh9Z4__eRjyMOAvxlD1OnK8R0sTWY9qUfl_uvfpH5kYveuHwlY__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" /&gt;
&lt;link type="text/css" rel="stylesheet" href="//cdn.jsdelivr.net/qtip2/2.2.1/jquery.qtip.min.css" media="all" /&gt;
&lt;link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__q48SDxVDWut_cH6dTPyQe7HjADuSLLmJVFztuiJEFrU__DJqddQGjm5dXCHsaiwNriGytKN5eevIQqyjdq65TZtE__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" /&gt;
&lt;style type="text/css" media="all"&gt;
&lt;/style&gt;
// links to CSS files
&lt;link type="text/css" rel="stylesheet" href="https://www.biorxiv.org/sites/default/files/advagg_css/css__ElJr3PIJEvw3qLXc1cnYiLj2G4KgDPSXFOfm6Phf8hw__JdWGm15cDWjsK6KrFlQVXQix9YgNeYysf22XZHj-Y-c__QJacVmveFyLIBSPkp21xL0ZvuDW9DMBrD2HB1P4Ry5I.css" media="all" /&gt;
...
// Scripts clipped
  &lt;/head&gt;
  
 * @author pm286
 *
 */
public class MedrxivDownloader extends CSHRxivDownloader {

	
	public static final String MEDRXIV_HOST = "www.medrxiv.org";
	public static final String MEDRXIV_BASE = HTTPS + P2H + MEDRXIV_HOST;
	public static final String MEDRXIV_SEARCH = MEDRXIV_BASE + "/search/";
	public static final String BIORXIV_HEADER = "/content/";

	
	public MedrxivDownloader() {
		init();
	}

	private void init() {
		this.setBase(MEDRXIV_BASE);
	}

	public MedrxivDownloader(CProject cProject) {
		super(cProject);
		init();
	}

//	@Override
//	protected String getHitListXPath() {
//		return "//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']";
//	}

	
	@Override
	protected BiorxivMetadataEntry createSubclassedMetadataEntry() {
		return new BiorxivMetadataEntry(this);
	}

//	@Override
//	protected String getDOIFromUrl(String fullUrl) {
//		if (fullUrl == null) return null;
//		String[] parts = fullUrl.split(CONTENT);
//		return parts[1];
//	}

	@Override
	public String getSearchUrl() {
		return MEDRXIV_SEARCH;
	}

//	@Override
//	protected void hitListErrorMessage() {
//		System.err.println("Cannot find metadata list: "+getHitListXPath());
//	}
	
//	@Override
//	protected HtmlElement getArticleElement(HtmlHtml htmlHtml) {
//		return (HtmlElement) XMLUtil.getFirstElement(htmlHtml, 
//				".//*[local-name()='"+HtmlDiv.TAG+"' and starts-with(@class, '"+ARTICLE+" "+"')]");
//	}
	
//	@Override
//	protected HtmlElement getSearchResultsList(HtmlBody body) {
//		return (HtmlUl) XMLUtil.getFirstElement(body, getHitListXPath());
//	}
	
	@Override
	protected String getHost() {
		return MedrxivDownloader.MEDRXIV_HOST;
	}

//	@Override
//	protected String createLocalTreeName(String fileroot) {
//		return fileroot.replace("/content/", "");
//	}
	
//	@Override
//	protected void cleanSearchResultsList(HtmlElement searchResultsList) {
//		XMLUtil.removeElementsByXPath(searchResultsList, CITE_EXTRAS_DIV);
//	}

//	/**
//	https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
//	 */
//	@Override
//	protected HitList createHitList(Element element) {
//	//		<ul class="highwire-search-results-list">
//		List<Element> ulList = XMLUtil.getQueryElements(element, 
//				getHitListXPath());
//		
//		if (ulList.size() == 0) {
//			LOG.debug(element.toXML());
//			System.err.println("empty array");
//			return new HitList();
//		}
//		Element ul = ulList.get(0);
//		HitList createHitList = super.createHitList(ul);
//		return createHitList;
//	}

//	/** compute pagenumber to download.
//	 * This is because biorxiv uses ZERO counting. The default here is ONE-based counting
//	 * 
//	 * Override to return zero-based counting
//	 * 
//	 * @param page
//	 * @return
//	 */
//	@Override
//	public Integer computePageNumber(Integer page) {
//		return page - 1;
//	}





}
