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
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Element;

/** downloads the immediate results of a search to a HitList
 * 
 * @author pm286
 *
 */
public class QueryManager extends AbstractSubDownloader {


private static final Logger LOG = Logger.getLogger(QueryManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private File metadataDir;
	private String sortOrder = "relevance-rank"; // will be set by each engine
	
	public QueryManager(AbstractDownloader abstractDownloader) {
		super(abstractDownloader);
	}

	/** 
	 * called from AMIDownloadTool
	 * @return 
	 * 
	 */
	public List<String> searchAndDownloadHitList() {
	/**
	https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
	 * @throws IOException 
	 */
		List<Integer> pageList = downloadTool.getPageList();
		String url = downloadTool.getSite().getSite() + this.createQuery(downloadTool.getQueryList()); // testing
		System.out.println("URL "+url);
		metadataDir = abstractDownloader.cProject.getOrCreateExistingMetadataDir();
		if (metadataDir == null) {
			throw new RuntimeException("no output directory");
		}
		metadataDir.mkdirs();
		if (pageList.get(0).equals(0)) {
			System.out.println("No pages required");
			return new ArrayList<>();
		}
		downloadCount = 0;
		Integer page0 = pageList.get(0);
		for (Integer page = page0; page <= pageList.get(1); page++) {
			try {
				searchAndDownloadMetadataHitList(url, page);
			} catch (IOException e) {
				LOG.error("Could not download hitpages: " + page, e);
				continue;
			}
			if (page == page0) {
				System.out.println("calculating hits NYI");
//				int totalHits = getTotalHits(hitListList);
			}
		}
		// clean the files
		List<Path> hitListCleanPaths = this.getHitListCleanFiles(metadataDir.toString());
		List<String> hitListList = new ArrayList<>();
		for (Path path : hitListCleanPaths) {
			hitListList.add(path.toFile().toString());
		}
		return hitListList;

		
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
		System.out.println("Query: "+s);
		return s;
	}

	private String addParameter(String name, String value, String s) {
		s += " " + name + "=" + value;
		return s;
	}
		
	/** creates target/biorxiv/testsearch3/__metadata/hitList1.html, etc. for each "page"
	 * 
	 * 
	 * @param url
	 * @param page
	 * @return 
	 * @throws IOException
	 */
	private HitList searchAndDownloadMetadataHitList(String url, Integer page) throws IOException {
		File hitListFile = new File(metadataDir, RESULT_SET + page + "." + "html");
		System.out.println("runing curl :" + url + " to " + hitListFile);
		url = addPageNumber(url, page);
		CurlDownloader curlDownloader = new CurlDownloader()
				.setUrlString(url)
				.setOutputFile(hitListFile);
		curlDownloader.run();
		File cleanHitListfile = cleanAndOutputHitListFile(hitListFile);
		if (cleanHitListfile != null) {
			String hitListContent = FileUtils.readFileToString(cleanHitListfile, CMineUtil.UTF8_CHARSET);
			hitList = this.createHitList1(hitListContent);
			hitList.setUrl(url);
			System.err.println("Results " + hitList.size());
		}
		return hitList;
	}

	/**
	 * called from downloadMetadataHitList
	 * 
	 * <ul class="highwire-search-results-list">
	 <li class="first odd search-result result-jcode-biorxiv search-result-highwire-citation">
	 * @return 
	 */
	private HitList createHitList1(String result) {
		Element element = HtmlUtil.parseCleanlyToXHTML(result);
		return abstractDownloader.createHitList(element);
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

	protected File cleanAndOutputHitListFile(File file) {
		Element element = HtmlUtil.parseCleanlyToXHTML(file);
		HtmlHtml htmlHtml = (HtmlHtml) HtmlElement.create(element);
		HtmlBody body = htmlHtml.getBody();
		if (body == null) {
			System.err.println("null body in cleanAndOutputHitListFile");
			return null;
		}
		HtmlElement searchResultsList = cleanAndDetachSearchResults(body);
		if (searchResultsList == null) {
			hitListErrorMessage();
			return null;
		}
		cleanHtmlRemoveLinkCommentEtc(htmlHtml);
		replaceBodyChildrenByHitList(body, searchResultsList);
		File cleanFile = new File(file.getAbsoluteFile().toString().replace(".html", "." + AbstractSubDownloader.CLEAN + ".html"));
		System.out.println("wrote hitList: "+cleanFile);
		XMLUtil.writeQuietly(htmlHtml, cleanFile, 1);
		return cleanFile;
	}

	protected void hitListErrorMessage() {
		System.err.println("Cannot write hitList");
	}

/** called from AMIDownloadTool
	 * @return 
	 * 
	 * @throws IOException
	 */
	public List<Path> getHitListCleanFiles(String metadataDir) {
/**
 * @throws IOException 
 */
		final Path p = Paths.get(metadataDir);
		List<Path> paths = new ArrayList<>();
		try {
			paths = Files.list(p)
			    .filter(f -> f.toString().matches(".*" + AbstractSubDownloader.RESULT_SET + "\\d+\\." + AbstractSubDownloader.CLEAN + "\\.html"))
			    .sorted()
			    .collect(Collectors.toList());
		} catch (IOException e) {
			System.err.println("no files: "+e.getMessage());
		}
		
		System.err.println(paths);
		return paths;

	}
}
