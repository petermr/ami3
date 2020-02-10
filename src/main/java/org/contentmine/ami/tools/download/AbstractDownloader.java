package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDownloadTool;
import org.contentmine.ami.tools.AMIDownloadTool.SearchSite;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** superclass for all web and similar downloaderss
 * 
 * @author pm286
 *
 */
public abstract class AbstractDownloader {
	
	static final Logger LOG = Logger.getLogger(AbstractDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String CITATION_PDF_URL = "citation_pdf_url";
	private static final String ABSTRACT  = "abstract";
	private static final String CITATION_ABSTRACT_HTML_URL = "citation_abstract_html_url";
	private static final String CITATION_FULL_HTML_URL = "citation_full_html_url";
	static final String RESULT_SET = "resultSet";
	private static final String HTML_PAGE = "fulltextPage";
	public static final String CLEAN = "clean";
	public static final String HTTPS = "https";
	public static final String P2H = "://";
	public static final String LANDING_PAGE = "landingPage";

	private String base;
	protected List<AbstractMetadataEntry> metadataEntryList;
	private CProject cProject;
	private CTree currentTree;
	private HtmlHead preloadHead;
//	private ArrayList<Integer> pageList;
//	private ArrayList<String> queryList;
//	private Integer pageSize;
	private SearchSite site;
	private File metadataDir;
	private String sortOrder = "relevance-rank"; // will be set by each engine
	private int downloadCount;
//	private int downloadLimit = 100;
	private AMIDownloadTool downloadTool;
	protected ResultSet resultSet;

//	.setCProject(cProject)
//	.setDownloadLimit(limit)
//	.setPageList(pageList)
//	.setPageSize(pagesize)
//	.setQueryList(queryList)
//	.setSite(site)
//	.setMetadataDir(new File(cProject.getOrCreateDirectory(), metadata))

	
	protected AbstractDownloader() {
	}
		
	public AbstractDownloader(CProject cProject) {
		this();
		this.setCProject(cProject);
		cProject.getOrCreateDirectory();
	}
	protected abstract AbstractMetadataEntry createMetadataEntry(Element parent);

	public AbstractDownloader setCProject(CProject cProject) {
		this.cProject = cProject;
		return this;
	}
	
	public static Element parseToHTML(Element element) {
		HtmlElement htmlElement = HtmlElement.create(element);
		return htmlElement;
	}

	public List<AbstractMetadataEntry> getMetadataList() {
		return metadataEntryList;
	}

	protected void setBase(String base) {
		this.base = base;
	}

	public String getBase() {
		return base;
	}

//	@Override
	/** 
	 * 
	 * @param containerElement
	 * @return
	 */
	protected ResultSet createResultSet(Element containerElement) {
		metadataEntryList = new ArrayList<>();
		List<Element> entryElementList = createEntryElementList(containerElement);
		for (Element entryElement : entryElementList) {
			AbstractMetadataEntry metadataEntry = createMetadataEntry(entryElement);
			metadataEntryList.add(metadataEntry);
			String doi = metadataEntry.getCleanedDOIFromURL();
			addMetadataToCTree(entryElement, doi);
		}
		System.out.println("metadataEntries "+metadataEntryList.size());
		resultSet = new ResultSet(metadataEntryList);
		return resultSet;
	}

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
	 * @param containerElement
	 * @return
	 */
	protected List<Element> createEntryElementList(Element containerElement) {
		List<Element> entryElementList = new ArrayList<>();
		for (int i = 0; i < containerElement.getChildElements().size(); i++) {
			Element entryElement = containerElement.getChildElements().get(i);
			entryElementList.add(entryElement);
		}
		return entryElementList;
	}

	public void downloadHtmlPages(List<AbstractMetadataEntry> metadataEntryList) throws IOException {
		List<CurlPair> curlPairList = new ArrayList<>();
		for (AbstractMetadataEntry metadataEntry : metadataEntryList) {
			if (downloadCount >= downloadTool.getDownloadLimit()) {
				LOG.warn("download limit reached " + downloadCount + "/" + downloadTool.getDownloadLimit());
				break;
			}
			downloadRequestedFiles(metadataEntry);
			downloadCount++;
		}
	}

	public CProject getCProject() {
		return downloadTool != null ? downloadTool.getCProject() : cProject;
	}

	void downloadRequestedFiles(AbstractMetadataEntry metadataEntry) throws IOException {
		String doi = metadataEntry.getCleanedDOIFromURL();
		File file = new File(this.getCProject().getDirectory(), doi);
		if (file.exists()) {
			System.out.println("skipped: "+doi);
			return;
		}

		HtmlHtml htmlPage = (HtmlHtml) metadataEntry.extractHtmlPage();
		if (htmlPage != null) {
			doi = metadataEntry.getCleanedDOIFromURL();
			currentTree = this.getCProject().getExistingCTreeOrCreateNew(doi);
			writeResultSetFile(htmlPage);
			preloadHead = htmlPage.getHead();
			
			writeDownloadedHtmlFile(CITATION_ABSTRACT_HTML_URL, ABSTRACT );
			writeDownloadedHtmlFile(CITATION_FULL_HTML_URL, CTree.FULLTEXT);
			writeDownloadedFile(CITATION_PDF_URL, CTree.FULLTEXT + "." + CTree.PDF);
		}
	}

	protected void writeDownloadedHtmlFile(String urlString, String filename) throws IOException {
		File downloadedFile = new File(currentTree.getDirectory(), filename + "." + CTree.HTML);
		downloadAndWriteFile(urlString, downloadedFile);
	}

	private void downloadAndWriteFile(String urlString, File downloadedFile) throws IOException {
		if (downloadedFile.exists()) {
			System.out.println("skipping : "+downloadedFile.getName());
			return;
		}
		LOG.trace("writing to :"+downloadedFile.getAbsolutePath());
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

	protected void writeDownloadedFile(String urlString, String filename) throws IOException {
		File file = new File(currentTree.getDirectory(), filename);
		downloadAndWriteFile(urlString, file);
	}


	private void writeResultSetFile(HtmlHtml htmlPage) throws IOException {
		File resultSetFile = new File(currentTree.getOrCreateDirectory(), RESULT_SET + "." + CTree.HTML);
		if (htmlPage != null) {
			if (resultSetFile.exists()) {
				System.out.println("skipping (existing) resultSet: "+resultSetFile.getParent());
			} else {
				XMLUtil.writeQuietly(htmlPage, resultSetFile, 1);
			}
		}
	}

	public void downloadResultSet() {
/**
https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
 * @throws IOException 
 */
		List<Integer> pageList = downloadTool.getPageList();
		String url = site.getSite() + this.createQuery(downloadTool.getQueryList()); // testing
		System.out.println("URL "+url);
		if (metadataDir == null) {
			throw new RuntimeException("no output directory");
		}
		metadataDir.mkdirs();
//		normalizePageList();
		if (pageList.get(0).equals(0)) {
			System.out.println("No pages required");
			return;
		}
		downloadCount = 0;
		for (Integer page = pageList.get(0); page <= pageList.get(1); page++) {
			try {
				downloadMetadataResultSet(url, page);
			} catch (IOException e) {
				LOG.error("Could not download hitpages: " + page, e);
				continue;
			}
		}
		
	}

	private void downloadMetadataResultSet(String url, Integer page) throws IOException {
		File resultSetFile = new File(metadataDir, RESULT_SET + page + "." + "html");
		System.out.println("runing curl :" + url + " to " + resultSetFile);
		url = addPageNumber(url, page);
		CurlDownloader curlDownloader = new CurlDownloader()
				.setUrlString(url)
				.setOutputFile(resultSetFile);
		curlDownloader.run();
		File cleanResultSetfile = cleanAndOutputResultSetFile(resultSetFile);
		
		String resultSetContent = FileUtils.readFileToString(cleanResultSetfile, CMineUtil.UTF8_CHARSET);
		resultSet = this.createResultSet(resultSetContent);
		resultSet.setUrl(url);
		System.err.println("Results " + resultSet.size());
	}
	
	protected abstract File cleanAndOutputResultSetFile(File file);

	/** adds ?page=n at end
	 * 
	 * @param url
	 * @param page
	 * @return
	 */
	private String addPageNumber(String url, Integer page) {
		return url == null ? null : url + "?page=" + page;
	}

	/** default is to create a space-separated string and the URLEncode it
	 * 
	 * coronavirus numresults=25 sort=relevance-rank?page=1
	 * ENCODES TO
	 * https://www.biorxiv.org/search/coronavirus%20numresults%3A25%20sort%3Arelevance-rank?page=1
	 * 
	 * @param queryList list of space-separated query components (brackets not yet supported)
	 * @return
	 */
	private String createQuery(List<String> queryList) {
		String s = "";
		if (queryList != null) {
			// originally a plus but will be encoded further
			s = String.join("%2B", queryList);
			s = addParameter("sort", sortOrder , s);
			s = addParameter("numresults", String.valueOf(downloadTool.getPageSize()), s);
			try {
//				s = URLEncoder.encode(s, NAConstants.UTF_8);  / adds "+" instead of "%20"
				s = AbstractDownloader.simpleEncode(s);
			} catch (Exception e) {
				throw new RuntimeException("cannot encode: ", e);
			}
		}
		System.out.println(s);
		return s;
	}

	public static String simpleEncode(String s) {
		s = s.replaceAll("%", "%25");
		s = s.replaceAll(" ", "%20");
		s = s.replaceAll("\\+", "%2B");
		s = s.replaceAll("=", "%3A");
		return s;
	}

	private String addParameter(String name, String value, String s) {
		s += " " + name + "=" + value;
		return s;
	}

//	public AbstractDownloader setPageList(List<Integer> pageList) {
//		this.pageList = new ArrayList<>(pageList);
//		return this;
//	}

//	public AbstractDownloader setPageSize(Integer pagesize) {
//		this.pageSize = pagesize;
//		return this;
//	}

//	public AbstractDownloader setQueryList(List<String> queryList) {
//		this.queryList = new ArrayList<>(queryList);
//		return this;
//	}
//
	public AbstractDownloader setSite(SearchSite site) {
		this.site = site;
		return this;
	}

//	public AbstractDownloader setMetadataDir(File dir) {
//		this.metadataDir = dir;
//		return this;
//	}
//
//	public AbstractDownloader setDownloadLimit(int limit) {
//		this.downloadLimit = limit;
//		return this;
//	}

	public static String replaceDOIPunctuationByUnderscore(String doi) {
		doi = doi == null ? null : doi.replaceAll("[\\.\\/]", "_");
		return doi;
	}

	/** DOIs are publisher specific so need bespoke filter
	 * 
	 * @param fullUrl
	 * @return
	 */
	protected abstract String getDOIFromUrl(String fullUrl);

	public void downloadPages() throws IOException {
/**
 * @throws IOException 
 */
		final Path p = Paths.get(downloadTool.getMetadataDir().toString());
		List<Path> paths = Files.list(p)
		    .filter(f -> f.toString().matches(".*" + RESULT_SET + "\\d+\\." + CLEAN + "\\.html"))
		    .sorted()
		    .collect(Collectors.toList());
		System.err.println(paths);

	}

	public void setDownloadTool(AMIDownloadTool downloadTool) {
		this.downloadTool = downloadTool;
	}

	public ResultSet createResultSet(File file) {
		if (!file.exists()) {
			throw new RuntimeException("file does not exist: "+file);
		}
		HtmlHtml html = (HtmlHtml) HtmlElement.create(HtmlUtil.parseCleanlyToXHTML(file));
		return createResultSet(html);
	}

	/**
	 * <ul class="highwire-search-results-list">
	 <li class="first odd search-result result-jcode-biorxiv search-result-highwire-citation">
	 * @return 
	 */
	public ResultSet createResultSet(String result) {
		Element element = HtmlUtil.parseCleanlyToXHTML(result);
		return createResultSet(element);
	}

	/** gets files roots from URLs
	 * not sure whether this is a Downloader or ResultSet responsibility
	 * 
	 * @return
	 */
	protected abstract List<String> getCitationLinks();



}
