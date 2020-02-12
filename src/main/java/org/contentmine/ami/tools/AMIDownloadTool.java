package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractLandingPage;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.BiorxivDownloader;
import org.contentmine.ami.tools.download.BiorxivLandingPage;
import org.contentmine.ami.tools.download.BiorxivMetadataEntry;
import org.contentmine.ami.tools.download.CurlDownloader;
import org.contentmine.ami.tools.download.ResultSet;
import org.contentmine.ami.tools.download.SDDownloader;
import org.contentmine.ami.tools.download.SDLandingPage;
import org.contentmine.ami.tools.download.SDMetadataEntry;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */

@Command(
name = "ami-download", 
aliases = "download",
version = "ami-download 0.1",
description = "downloads content from remote site. Maybe a wrapper for getpapers, curl, etc."
)


public class AMIDownloadTool extends AbstractAMITool {
	private static final String GET = "GET";
	private static final String CURL_X = "-X";
	private static final String CURL = "curl";

	private static final Logger LOG = Logger.getLogger(AMIDownloadTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum SearchSite {
		biorxiv(BiorxivDownloader.getSearchUrl(),
				new BiorxivDownloader(),
				new BiorxivMetadataEntry(),
				new BiorxivLandingPage()
				),
		hal(HALDownloader.getSearchUrl(),
				new HALDownloader(),
				new HALMetadataEntry(),
				new HALLandingPage()
				),
		sd(SDDownloader.getSearchUrl(),
				new SDDownloader(), 
				new SDMetadataEntry(), 
				new SDLandingPage()),
		;
		private String site;
		private AbstractDownloader downloader;
		private AbstractMetadataEntry metadata;
		private AbstractLandingPage landingPage;
		
		private SearchSite(String site, 
				AbstractDownloader downloader, 
				AbstractMetadataEntry metadata, 
				AbstractLandingPage landingPage) {
			this.site = site;
			this.downloader = downloader;
			this.metadata = metadata;
			this.landingPage = landingPage;
		}
		public String getSite() {
			return site;
		}

		public AbstractDownloader createDownloader(AMIDownloadTool downloadTool) {
			AbstractDownloader newDownloader = null;
			try {
				newDownloader = (AbstractDownloader) Class.forName(downloader.getClass().getName()).newInstance();
				newDownloader.setDownloadTool(downloadTool);
			} catch (Exception e) {
				throw new RuntimeException("BUG: ", e);
			}
			return newDownloader;
		}

		public AbstractMetadataEntry createMetadata() {
			AbstractMetadataEntry newMetadata = null;
			try {
				newMetadata = (AbstractMetadataEntry) Class.forName(metadata.getClass().getName()).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("BUG: ", e);
			}
			return newMetadata;
		}
		
		public AbstractLandingPage createLandingPage() {
			AbstractLandingPage newLandingPage = null;
			try {
				newLandingPage = (AbstractLandingPage) Class.forName(landingPage.getClass().getName()).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("BUG: ", e);
			}
			return newLandingPage;
		}
	}

    @Option(names = {"--limit"},
    		arity = "1",
            description = "max hits to download (default 200)")
    private Integer limit = 200;

    @Option(names = {"--metadata"},
    		arity = "1",
            description = "directory for metadata pages")
    private String metadata = "metadata";

    @Option(names = {"--pages"},
    		arity = "1..2",
            description = "start and optional end hitpage. If only one value download single hitpage, default 1 ")
    private List<Integer> pageList = new ArrayList<>();

    @Option(names = {"--pagesize"},
    		arity = "1",
            description = "size of hit page, no default (set by service)")
    private Integer pagesize = null;

    @Option(names = {"--query"},
    		arity = "1..*",
            description = "query to issue (may need escaping)")
    private List<String> queryList = null;

    @Option(names = {"--resultset"},
    		arity = "1..*",
            description = "resultSets to download (experimental)")
    private List<String> resultsSetList = new ArrayList<>();

    @Option(names = {"--site"},
    		arity = "1",
            description = "site to search")
    private SearchSite site = null;

    private File dictionaryFile;
	private InputStream dictionaryInputStream;

	private HttpClient client;
	private AbstractDownloader downloader;
	
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIDownloadTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIDownloadTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIDownloadTool().runCommands(args);
    }


    @Override
	protected boolean parseGenerics() {
    	makeCProjectDirectory = true;
    	super.parseGenerics();
    	if (cProject == null) {
    		throw new RuntimeException("Must give project");
    	}
    	
    	if (output == null) {
    		output = "scraped/";
    		LOG.info("set output to: " + output);
    	}
		System.out.println("fileformats     " + rawFileFormats);
		System.out.println("project         " + cProject);
		System.out.println();
		return true;
	}

    @Override
	protected void parseSpecifics() {
		System.out.println("limit         " + limit);
		System.out.println("pages         " + pageList);
		System.out.println("pagesize      " + pagesize);
		System.out.println("query         " + queryList);
		System.out.println("site          " + site);
		System.out.println();
	}
    
    @Override
    protected void runSpecifics() {
		downloader = createDownloader();
		downloader.downloadResultSet();
		try {
			downloader.downloadPages();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("cannot load pages "+ e);
		}
		if (resultsSetList.size() > 0) {
			for (String resultSetFilename : resultsSetList) {
				extractResultSets(resultSetFilename);
			}
		}
		if (rawFileFormats.size() > 0) {
			try {
				this.downloadCTrees();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Cannot downloadTrees");
			}
		}
    }

	private AbstractDownloader createDownloader() {
		downloader = site.createDownloader(this);
		downloader.setCProject(cProject)
//			.setDownloadLimit(limit)
//			.setPageList(pageList)
//			.setPageSize(pagesize)
//			.setQueryList(queryList)
//			.setSite(site)
//			.setMetadataDir(new File(cProject.getOrCreateDirectory(), metadata))
			;
		return downloader;
	}
	
	public static String runCurlGet(String url) throws IOException {
		String[] command = new String[] {CURL, CURL_X, GET, url};
		String result = runCurl(command);
		return result;
	}

	private static String runCurl(String[] command) throws IOException {
		
		System.out.println("running "+Arrays.asList(command));
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		String result = String.join("\n", IOUtils.readLines(process.getInputStream(), CMineUtil.UTF8_CHARSET));
		int exitCode = process.exitValue();
		if (exitCode != 0) {
			System.err.println("EXITCode: "+exitCode);
		}
		return result;
	}

	public File getMetadataDir() {
		return new File(cProject.getOrCreateDirectory(), metadata);
	}

	public int getDownloadLimit() {
		return limit;
	}
	
	private void normalizePageList() {
		// no pages, use 1
		if (pageList.size() == 0) {
			pageList.add(1);
		}
		// first page == 0 , signal for no pages
		if (pageList.size() == 1 && pageList.get(0).equals(0)) return;
		
		// first page only, set last to first
		if (pageList.size() == 1) {
			pageList.add(pageList.get(0));
		}
		// first page <= 0, set to 1
		if (pageList.get(0) <= 0) {
			pageList.set(0,  1);
			System.err.println("page list must start from >= 1");
		}
		// upper limit less than start, set to start
		if (pageList.get(1) < pageList.get(0)) {
			System.err.println("page list out of order: "+pageList);
			pageList.set(1, pageList.get(0));
		}
	}

	public CTree getCTree(String doi) {
		CTree cTree = cProject.getExistingCTreeOrCreateNew(doi);
		cProject.add(cTree);
		return cTree;
	}

	public int getPageSize() {
		return pagesize;
	}

	public List<Integer> getPageList() {
		return pageList;
	}

	public List<String> getQueryList() {
		return queryList;
	}

	public SearchSite getSite() {
		return site;
	}

	private void extractResultSets(String filename) {
		downloader.setCProject(cProject);
	
		File metadataDir = cProject.getOrCreateExistingMetadataDir();
		ResultSet resultSet = downloader.createResultSet(new File(metadataDir, filename));
		List<String> fileroots = resultSet.getCitationLinks();
		String result = null;
		try {
			result = downloadWithCurl(fileroots);
		} catch (IOException e) {
			throw new RuntimeException("Cannot extract resultSet "+filename);
		}
		LOG.debug("result ["+result+"]");
	}

	private String downloadWithCurl(List<String> fileroots) throws IOException {
		CurlDownloader curlDownloader = new CurlDownloader();
		for (String fileroot : fileroots) {
			curlDownloader.addCurlPair(BiorxivDownloader.createCurlPair(cProject.getDirectory(), fileroot));
		}
		
		curlDownloader.setTraceFile("target/trace.txt");
		curlDownloader.setTraceTime(true);
		String result = curlDownloader.run();
		return result;
	}

	public HtmlHtml getLandingPageHtml(String content) {
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
	
	public AbstractLandingPage getLandingPage(CTree cTree) {
		String content = getLandingPageText(cTree);
		HtmlHtml landingPageHtml = null;
		try {
			content = downloader.clean(content);
			landingPageHtml = getLandingPageHtml(content);
		} catch (Exception e) {
			System.err.println("Bad parse ("  +cTree + ")"+e);
			return null;
		}
		AbstractLandingPage landingPage = downloader.createLandingPage();
		landingPage.readHtml(landingPageHtml);
		return landingPage;
	}

	public AbstractDownloader getDownloader() {
		return downloader;
	}
	
	public void downloadCTrees() throws IOException {
		boolean force = true;
		CTreeList treeList = cProject.getOrCreateCTreeList(force);
		AbstractDownloader downloader = getDownloader();
		for (CTree cTree : treeList) {
			downloader.setCurrentTree(cTree);
			AbstractLandingPage landingPage = getLandingPage(cTree);
			if (landingPage != null) {
				downloader.downloadLink(landingPage);
			}
		}
	}

}
