package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.BiorxivDownloader;
import org.contentmine.ami.tools.download.BiorxivMetadataEntry;
import org.contentmine.ami.tools.download.SDDownloader;
import org.contentmine.ami.tools.download.SDMetadataEntry;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineUtil;

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
		biorxiv(BiorxivDownloader.getSearchUrl(), new BiorxivDownloader(), new BiorxivMetadataEntry()),
		sd(SDDownloader.getSearchUrl(), new SDDownloader(), new SDMetadataEntry()),
		;
		private String site;
		private AbstractDownloader downloader;
		private AbstractMetadataEntry metadata;
		
		private SearchSite(String site, AbstractDownloader downloader, AbstractMetadataEntry metadata) {
			this.site = site;
			this.downloader = downloader;
			this.metadata = metadata;
		}
//		SearchSite(String string, SDDownloader sdDownloader, SDMetadata sdMetadata) {
//			// TODO Auto-generated constructor stub
//		}
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
	}

//    @Option(names = {"--download"},
//    		arity = "1..*",
//            description = "file types to download")
//    private FileTypes fileTypes = new ArrayList<>("html", "pdf");

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
		createDownloader();
		downloader.downloadResultSet();
		try {
			downloader.downloadPages();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("cannot load pages "+ e);
		}
		try {
			downloader.downloadPages();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("cannot load pages "+ e);
		}
    }

	private void createDownloader() {
		downloader = site.createDownloader(this);
//			.setCProject(cProject)
//			.setDownloadLimit(limit)
//			.setPageList(pageList)
//			.setPageSize(pagesize)
//			.setQueryList(queryList)
//			.setSite(site)
//			.setMetadataDir(new File(cProject.getOrCreateDirectory(), metadata))
//			;
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


}
