package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** superclass for all web and similar downloaders
 * 
 * downloading occurs in phases, varying according to the target.
 * 
 * 	public QueryManager getOrCreateQueryManager();
	public LandingPageManager getOrCreateLandingPageManager();
	public FulltextManager getOrCreateFulltextManager();
	public FullFileManager getOrCreateFullFileManager();
	
	A QueryManagerissues a query to a remove API or similar search. The result is normally a list of
	hits/results, formalised by a HitList class. The results are often in chunks (pages) determined by the
	host API, sometimes settable by the client. QM can limit the pages and iterate over then or a subset.
	Outcome is preserved by files in cProject/metadata. Thes can be interrogated later by LandingPageManager
	
	A LandingPageManager retrieves the landing page for a hit. Sometimes this is the whole document, but more
	often a set of pointers to other documents (e.g. abstracts, fulltext, supplementary)
	
	A FulltextManager uses the landing page to get initial fulltext. This might be the text on the landing page
	or more often from links on it. Sometimes the fulltext, rather than the landingPage, contains the further 
	pointers, especially when these are in HTML <meta> elements.
	
	FullFileManager can select the various files pointed to from the Landing Page or from the fulltext.
	 
 * 
 * @author pm286
 *
 */
public abstract class AbstractDownloader {
	
	static final Logger LOG = Logger.getLogger(AbstractDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String CITATION_PDF_URL = "citation_pdf_url";
	public static final String ABSTRACT  = "abstract";
	public static final String CITATION_ABSTRACT_HTML_URL = "citation_abstract_html_url";
	public static final String CITATION_FULL_HTML_URL = "citation_full_html_url";
	public static final String HTML_PAGE = "fulltextPage";
	public static final String HTTPS = "https";
	public static final String P2H = "://";
	public static final String LANDING_PAGE = "landingPage";

	private String base;
	protected List<AbstractMetadataEntry> metadataEntryList;
	protected CProject cProject;
	protected CTree currentTree;
	private HtmlHead preloadHead;
	
	protected AMIDownloadTool downloadTool;
	private QueryManager queryDownloadManager;
	private LandingPageManager landingPageManager;
	private FulltextManager fulltextManager;
	private FullFileManager fullFileManager;
	
	protected HitList hitList;
	
	protected AbstractDownloader() {
	}
		
	public AbstractDownloader(CProject cProject) {
		this();
		this.setCProject(cProject);
		cProject.getOrCreateDirectory();
	}
	
	// ABSTRACT METHODS
	/** DOIs are publisher specific so need bespoke filter
	 * 
	 * @param fullUrl
	 * @return
	 */
	protected abstract String getDOIFromUrl(String fullUrl);
	protected abstract String getHost();
	public abstract String getSearchUrl();
	protected abstract HtmlElement getSearchResultsList(HtmlBody body);
	protected abstract void cleanSearchResultsList(HtmlElement searchResultsList);
	/** the main article */
	protected abstract HtmlElement getArticleElement(HtmlHtml htmlHtml);
	protected abstract String getHitListXPath();
	protected abstract AbstractMetadataEntry createSubclassedMetadataEntry();



	// ======

	public AbstractDownloader setCProject(CProject cProject) {
		this.cProject = cProject;
		return this;
	}
	
	public static Element parseToHTML(Element element) {
		HtmlElement htmlElement = HtmlElement.create(element);
		return htmlElement;
	}

	public List<AbstractMetadataEntry> getMetadataEntryList() {
		return metadataEntryList;
	}

	protected void setBase(String base) {
		this.base = base;
	}

	public String getBase() {
		return base;
	}

	/** 
	 * usually called by createHitList in subclassed Downloader
	 * 
	 * @param containerElement
	 * @return
	 */
	protected HitList createHitList(Element containerElement) {
		metadataEntryList = new ArrayList<>();
		List<Element> entryElementList = createEntryElementList(containerElement);
		for (Element entryElement : entryElementList) {
			AbstractMetadataEntry metadataEntry = createMetadataEntry(entryElement);
			metadataEntryList.add(metadataEntry);
			String doi = metadataEntry.getCleanedDOIFromURL();
			addMetadataToCTree(entryElement, doi);
		}
		System.out.println("metadataEntries "+metadataEntryList.size());
		hitList = new HitList(metadataEntryList);
		return hitList;
	}

	/** used in creating HitList
	 * 
	 * @param entryElement
	 * @param doi
	 */
	private void addMetadataToCTree(Element entryElement, String doi) {
		getOrCreateCProject();
		if (cProject == null) {
			LOG.info("No cProject so no tree");
			return;
		}
		currentTree = cProject.getExistingCTreeOrCreateNew(doi);
		boolean delete = true;
		try {
			currentTree.setScrapedMetadataElement(HtmlElement.create(entryElement), delete);
		} catch (IOException e) {
			throw new RuntimeException("cannot add metadata: ", e);
		}
	}

	private CProject getOrCreateCProject() {
		if (cProject == null) {
			if (downloadTool != null) {
				cProject = downloadTool.getCProject();
			}
			if (cProject == null) {
				System.err.println("No CProject; set explicitly or use DownloadTool");
			}
		}
		return cProject;
		
	}

	/** creates entryElements from children of containerElements
	 * Typically containerElement is a <ul> and the children are <li>
	 * might be overridden
	 * 
	 * used in creating HitList
	 * 
	 * @param containerElement
	 * @return
	 */
	private List<Element> createEntryElementList(Element containerElement) {
		List<Element> entryElementList = new ArrayList<>();
		for (int i = 0; i < containerElement.getChildElements().size(); i++) {
			Element entryElement = containerElement.getChildElements().get(i);
			entryElementList.add(entryElement);
		}
		return entryElementList;
	}

	public static String simpleEncode(String s) {
		s = s.replaceAll("%", "%25");
		s = s.replaceAll(" ", "%20");
		s = s.replaceAll("\\+", "%2B");
		s = s.replaceAll("=", "%3A");
		return s;
	}

	public static String replaceDOIPunctuationByUnderscore(String doi) {
		doi = doi == null ? null : doi.replaceAll("[\\.\\/]", "_");
		return doi;
	}


	/** called from AMIDownloadTool
	 * 
	 * @param downloadTool
	 */
	public void setDownloadTool(AMIDownloadTool downloadTool) {
		this.downloadTool = downloadTool;
	}

	/**
	 * called from downloadTool
	 * @param file
	 * @return
	 */
	public HitList createHitList(File file) {
		if (!file.exists()) {
			throw new RuntimeException("file does not exist: "+file);
		}
		HtmlHtml html = (HtmlHtml) HtmlElement.create(HtmlUtil.parseCleanlyToXHTML(file));
		return createHitList(html);
	}

	/** maybe move to DownloadTool
	 * 
	 * @param content
	 * @return
	 */
	public String clean(String content) {
		content = cleanAmpersand(content);
		return content;
	}

	void clean(File htmlFile) {
		try {
			String content = FileUtils.readFileToString(htmlFile, CMineUtil.UTF8_CHARSET);
			content = clean(content);
			FileUtils.write(htmlFile, content, CMineUtil.UTF8_CHARSET);
		} catch (Exception e) {
			throw new RuntimeException("cannot write clean file: "+htmlFile, e);
		}
	}

	public CProject getCProject() {
		return cProject;
	}

	public void setcProject(CProject cProject) {
		this.cProject = cProject;
	}

	public void setCurrentTree(CTree cTree) {
		this.currentTree = cTree;
	}
	
	public CTree getCurrentCTree() {
		return currentTree;
	}

	/** override this if the treename contains parts of directory structure
	 * 
	 * @param fileroot
	 * @return
	 */
	protected String createLocalTreeName(String fileroot) {
		return fileroot;
	}


	public URL createURL(String fileroot) {
		URL url = null;
		try {
			url = new URL(AbstractDownloader.HTTPS, getHost(), fileroot);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Cannot create URL", e);
		}
		return url;
	}

	public String cleanAmpersand(String content) {
		// really tacky, but this is a general bug
		
		content = content.replaceAll(" & ", " &#38; ");
		return content;
	}

	protected List<String> getCitationLinks() {
		return hitList == null ? new ArrayList<>() : hitList.getCitationLinks();
	}

	protected AbstractMetadataEntry createMetadataEntry(Element contentElement) {
		AbstractMetadataEntry metadataEntry = createSubclassedMetadataEntry();
		metadataEntry.read(contentElement);
		return metadataEntry;
	}

	public QueryManager getOrCreateQueryManager() {
		if (queryDownloadManager == null) {
			queryDownloadManager = new QueryManager(this);
		}
		return queryDownloadManager;
		
	}

	public LandingPageManager getOrCreateLandingPageManager() {
		if (landingPageManager == null) {
			landingPageManager = new LandingPageManager(this);
		}
		return landingPageManager;
		
	}

	public FulltextManager getOrCreateFulltextManager() {
		if (fulltextManager == null) {
			fulltextManager = new FulltextManager(this);
		}
		return fulltextManager;
		
	}

	public FullFileManager getOrCreateFullFileManager() {
		if (fullFileManager == null) {
			fullFileManager = new FullFileManager(this);
		}
		return fullFileManager;
		
	}

	public AMIDownloadTool getDownloadTool() {
		return downloadTool;
	}

}
