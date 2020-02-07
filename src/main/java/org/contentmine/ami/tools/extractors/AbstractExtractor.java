package org.contentmine.ami.tools.extractors;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIDownloadTool.SearchSite;
import org.contentmine.ami.tools.CurlDownloader;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.norma.NAConstants;

import nu.xom.Element;

/** superclass for all web and similar extractors
 * 
 * @author pm286
 *
 */
public abstract class AbstractExtractor {
	private static final String CITATION_PDF_URL = "citation_pdf_url";
	private static final String ABSTRACT  = "abstract";
	private static final String CITATION_ABSTRACT_HTML_URL = "citation_abstract_html_url";
	private static final String CITATION_FULL_HTML_URL = "citation_full_html_url";
	static final Logger LOG = Logger.getLogger(AbstractExtractor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	static final String PRELOAD_PAGE = "preloadPage";
	private static final String HTML_PAGE = "fulltextPage";

	private String base;
	protected List<AbstractMetadata> metadataList;
	private CProject cProject;
	private CTree currentTree;
	private HtmlHead preloadHead;
	private ArrayList<Integer> pageList;
	private ArrayList<String> queryList;
	private Integer pageSize;
	private SearchSite site;
	private File output;
	private File outputDir;
	private String sortOrder = "relevance-rank"; // will be set by each engine
	private int downloadCount;
	private int downloadLimit = 100;

	
	protected AbstractExtractor() {
	}
		
	public AbstractExtractor(CProject cProject) {
		this();
		this.setCProject(cProject);
		cProject.getOrCreateDirectory();
	}
	public abstract List<AbstractMetadata> extractSearchResultsIntoMetadata(String result);
	protected abstract AbstractMetadata createMetadata(Element parent);

	public static Element parseToHTML(Element element) {
		HtmlElement htmlElement = HtmlElement.create(element);
		return htmlElement;
	}

	public List<AbstractMetadata> getMetadataList() {
		return metadataList;
	}

	protected void setBase(String base) {
		this.base = base;
	}

	public String getBase() {
		return base;
	}

	protected List<AbstractMetadata> createMetadataList(Element ul) {
		metadataList = new ArrayList<>();
		List<Element> entryElementList = createEntryElementList(ul);
		for (Element entryElement : entryElementList) {
			AbstractMetadata metadata = createMetadata(entryElement);
			metadataList.add(metadata);
			String doi = metadata.getCleanedDOIFromURL();
			if (cProject != null) {
				currentTree = cProject.getExistingCTreeOrCreateNew(doi);
				cProject.add(currentTree);
				boolean delete = true;
				try {
					currentTree.setScrapedMetadataElement(HtmlElement.create(entryElement), delete);
				} catch (IOException e) {
					throw new RuntimeException("cannot add metadata: ", e);
				}
			}
		}
		System.out.println("metadata "+metadataList.size());
		return metadataList;
	}

	protected List<Element> createEntryElementList(Element ul) {
		List<Element> entryElementList = new ArrayList<>();
		for (int i = 0; i < ul.getChildElements().size(); i++) {
			Element li = ul.getChildElements().get(i);
			entryElementList.add(li);
		}
		return entryElementList;
	}

	public void downloadHtmlPages(List<AbstractMetadata> metadataList) throws IOException {
		for (AbstractMetadata metadata : metadataList) {
			if (downloadCount >= downloadLimit) {
				LOG.warn("download limit reached " + downloadCount + "/" + downloadLimit);
				break;
			}
			downloadFilesFromMetadata(metadata);
			downloadCount++;
		}
	}

	protected AbstractExtractor setCProject(CProject cProject) {
		this.cProject = cProject;
		cProject.getOrCreateDirectory();
		return this;
	}
	
	public CProject getCProject() {
		return cProject;
	}

	void downloadFilesFromMetadata(AbstractMetadata abstractMetadata) throws IOException {
		String doi = abstractMetadata.getCleanedDOIFromURL();
		File file = new File(this.getCProject().getDirectory(), doi);
		if (file.exists()) {
			System.out.println("skipped: "+doi);
			return;
		}

		HtmlHtml htmlPage = (HtmlHtml) abstractMetadata.extractHtmlPage();
		if (htmlPage != null) {
			doi = abstractMetadata.getCleanedDOIFromURL();
			currentTree = this.getCProject().getExistingCTreeOrCreateNew(doi);
			writePreloadFile(htmlPage);
			preloadHead = htmlPage.getHead();
			writeHtmlFile(CITATION_ABSTRACT_HTML_URL, ABSTRACT );
			writeHtmlFile(CITATION_FULL_HTML_URL, CTree.FULLTEXT);
			writeFile(CITATION_PDF_URL, CTree.FULLTEXT + "." + CTree.PDF);
		}
	}

	protected void writeHtmlFile(String urlString, String filename) throws IOException {
		File file = new File(currentTree.getDirectory(), filename + "." + CTree.HTML);
		downloadAndWriteFile(urlString, file);
	}

	private void downloadAndWriteFile(String urlString, File file) throws IOException {
		if (file.exists()) {
			System.out.println("skipping : "+file.getName());
			return;
		}
		LOG.trace("writing to :"+file.getAbsolutePath());
		String url = preloadHead.getMetaElementValue(urlString);
		if (url == null) {
			System.out.println("null url: "+urlString);
			return;
		}
		CurlDownloader curlDownloader = new CurlDownloader()
				.setOutputFile(file)
				.setUrlString(url);
		curlDownloader.run();
	}

	protected void writeFile(String urlString, String filename) throws IOException {
		File file = new File(currentTree.getDirectory(), filename);
		downloadAndWriteFile(urlString, file);
	}


	private void writePreloadFile(HtmlHtml htmlPage) throws IOException {
		File preloadFile = new File(currentTree.getOrCreateDirectory(), AbstractExtractor.PRELOAD_PAGE + "." + CTree.HTML);
		if (htmlPage != null) {
			if (preloadFile.exists()) {
				System.out.println("skipping preload: "+preloadFile.getParent());
			} else {
				XMLUtil.debug(htmlPage, preloadFile, 1);
			}
		}
	}

	public void downloadHitPages() {
/**
https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
 * @throws IOException 
 */
		String url = site.getSite() + this.createQuery(queryList); // testing
		System.out.println("URL "+url);
		if (outputDir == null) {
			throw new RuntimeException("no output directory");
		}
		outputDir.mkdirs();
		normalizePageList();
		downloadCount = 0;
		for (Integer page = pageList.get(0); page <= pageList.get(1); page++) {
			try {
				downloadHitPage(url, page);
			} catch (IOException e) {
				LOG.error("Could not download hitpages: " + page, e);
				continue;
			}
		}
		
	}

	private void normalizePageList() {
		// no pages, use 1
		if (pageList.size() == 0) {
			pageList.add(1);
		}
		// first page only, set last to first
		if (pageList.size() == 1) {
			pageList.add(pageList.get(0));
		}
		// first page <= 0, set to 1
		if (pageList.get(0) <= 0) {
			pageList.set(0,  1);
			LOG.debug("page list must start from >= 1");
		}
		// upper limit less than start, set to start
		if (pageList.get(1) < pageList.get(0)) {
			LOG.error("page list out of order: "+pageList);
			pageList.set(1, pageList.get(0));
		}
	}

	private void downloadHitPage(String url, Integer page) throws IOException {
		File file = new File(outputDir, "page"+page+"."+"html");
		System.out.println("runing curl :" + url + " to " + file);
		url = addPageNumber(url, page);
		CurlDownloader curlDownloader = new CurlDownloader()
				.setUrlString(url)
				.setOutputFile(file);
		String result = curlDownloader.run();
		// I think this is mainly standard highwire
		List<AbstractMetadata> metadataList = this.extractSearchResultsIntoMetadata(
				FileUtils.readFileToString(file, CMineUtil.UTF8_CHARSET));
		this.downloadHtmlPages(metadataList);
	}

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
			s = addParameter("numresults", String.valueOf(pageSize), s);
			try {
//				s = URLEncoder.encode(s, NAConstants.UTF_8);  / adds "+" instead of "%20"
				s = AbstractExtractor.simpleEncode(s);
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

	public AbstractExtractor setPageList(List<Integer> pageList) {
		this.pageList = new ArrayList<>(pageList);
		return this;
	}

	public AbstractExtractor setPageSize(Integer pagesize) {
		this.pageSize = pagesize;
		return this;
	}

	public AbstractExtractor setQueryList(List<String> queryList) {
		this.queryList = new ArrayList<>(queryList);
		return this;
	}

	public AbstractExtractor setOutput(File output) {
		this.output = output;
		return this;
	}

	public AbstractExtractor setSite(SearchSite site) {
		this.site = site;
		return this;
	}

	public AbstractExtractor setOutputDir(File dir) {
		this.outputDir = dir;
		return this;
	}

	public AbstractExtractor setDownloadLimit(int limit) {
		this.downloadLimit = limit;
		return this;
	}

	public static String replaceDOIPunctuationByUnderscore(String doi) {
		doi = doi == null ? null : doi.replaceAll("[\\.\\/]", "_");
		return doi;
	}

	/** DOIs are publiser specific so need bespoke filter
	 * 
	 * @param fullUrl
	 * @return
	 */
	protected abstract String getDOIFromUrl(String fullUrl);


}
