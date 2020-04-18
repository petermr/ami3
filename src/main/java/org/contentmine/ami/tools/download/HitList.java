package org.contentmine.ami.tools.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.xml.XMLUtil;

public class HitList {
	private static final Logger LOG = Logger.getLogger(HitList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<AbstractMetadataEntry> metadataEntryList;
	private String urlString;
	private String base;
	private URL url;
	
	public HitList() {
		this.metadataEntryList = new ArrayList<>();
	}

	public HitList(List<AbstractMetadataEntry> metadataEntryList) {
		this.metadataEntryList = new ArrayList<>(metadataEntryList);
	}

	public int size() {
		return metadataEntryList.size();
	}

	public void setUrl(String urlString) {
		this.urlString = urlString;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new RuntimeException("malformed URL "+urlString, e);
		}
		this.base = createBaseFromUrl();
	}

	private String createBaseFromUrl() {
//		System.err.println("url "+url);
//		System.err.println("protocol "+url.getProtocol());
//		System.err.println("host "+url.getHost());
//		System.err.println("file "+url.getFile());
//		System.err.println("query "+url.getQuery());
//		System.err.println("auth "+url.getAuthority());
//		System.err.println("path "+url.getPath());
		base = url.getProtocol() + "://" + url.getHost();
//		System.err.println("BASE "+base);
		return this.base;
	}
	
	public String getBase() {
		return base;
	}

	public List<AbstractMetadataEntry> getMetadataEntryList() {
		return metadataEntryList;
	}

	public void createCTrees(CProject cProject) {
		CTreeList cTreeList = new CTreeList();
//		File metadataDir = cProject.getOrCreateExistingMetadataDir();
		for (AbstractMetadataEntry metadataEntry : metadataEntryList) {
			String doi = metadataEntry.getDOI();
			doi = AbstractDownloader.replaceDOIPunctuationByUnderscore(doi);
			CTree cTree = cProject.getOrCreateExistingCTree(doi);
			cTreeList.add(cTree);
		}
	}

	/**
	  <ul class="highwire-search-results-list">
	  <!-- entry -->
	   <li class="first odd search-result result-jcode-biorxiv search-result-highwire-citation">
	    <div class="highwire-article-citation highwire-citation-type-highwire-article" data-pisa="biorxiv;2020.01.24.917864v1" data-pisa-master="biorxiv;2020.01.24.917864" data-seqnum="11" data-apath="/biorxiv/early/2020/01/24/2020.01.24.917864.atom" id="biorxivearly2020012420200124917864atom">
	     <div class="highwire-cite highwire-cite-highwire-article highwire-citation-biorxiv-article-pap-list clearfix">
	      <span class="highwire-cite-title">
	       <a href="/content/10.1101/2020.01.24.917864v1" class="highwire-cite-linked-title" data-icon-position="" data-hide-link-title="0">
	        <span class="highwire-cite-title">A systematic review of scientific research focused on farmers in agricultural adaptation to climate change (2008-2017)</span>
	       </a>
	      </span>
	      <div class="highwire-cite-authors">
	       <span class="highwire-citation-authors">
	        <span class="highwire-citation-author first" data-delta="0">
	         <span class="nlm-given-names">Yao</span>
	         <span class="nlm-surname">Yang</span>
	        </span>, 
	        ...
	       </span>
	      </div>
	      <div class="highwire-cite-metadata">
	       <span class="highwire-cite-metadata-journal highwire-cite-metadata">bioRxiv </span>
	       <span class="highwire-cite-metadata-pages highwire-cite-metadata">2020.01.24.917864; </span>
	       <span class="highwire-cite-metadata-doi highwire-cite-metadata">
	        <span class="doi_label">doi:</span> https://doi.org/10.1101/2020.01.24.917864 
	       </span>
	      </div> 
	     </div>
	    </div>
	   </li>
	   <!-- entry -->
	   <li class="even search-result result-jcode-biorxiv search-result-highwire-citation">
	    <div class="highwire-article-citation highwire-citation-type-highwire-article" data-pisa="biorxiv;850289v1" data-pisa-master="biorxiv;850289" data-seqnum="12" data-apath="/biorxiv/early/2019/11/21/850289.atom" id="biorxivearly20191121850289atom">
	     <div class="highwire-cite highwire-cite-highwire-article highwire-citation-biorxiv-article-pap-list clearfix">
	      <span class="highwire-cite-title">
	       <a href="/content/10.1101/850289v1" class="highwire-cite-linked-title" data-icon-position="" data-hide-link-title="0">
	        <span class="highwire-cite-title">Amplitude and timescale of metacommunity trait-lag response to climate change</span>
	       </a>
	      </span>
	      <div class="highwire-cite-authors">
	       <span class="highwire-citation-authors">
	       ...
		 */
		public List<String> getCitationLinks() {
			List<String> citationList = new ArrayList<>();
			for (AbstractMetadataEntry metadataEntry : metadataEntryList) {
				String citationLink = metadataEntry.getCitationLink();
				citationList.add(citationLink);
			}
			return citationList;
		}

}
