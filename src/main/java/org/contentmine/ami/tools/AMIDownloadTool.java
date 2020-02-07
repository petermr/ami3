package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.extractors.AbstractExtractor;
import org.contentmine.ami.tools.extractors.AbstractMetadata;
import org.contentmine.ami.tools.extractors.BiorxivExtractor;
import org.contentmine.cproject.files.CProject;
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
		biorxiv(BiorxivExtractor.BIORXIV_SEARCH),
		europepmc("https://europepmc.org/search?query="),
		;
		private String site;
		private SearchSite(String site) {
			this.site = site;
		}
		public String getSite() {
			return site;
		}
	}

    @Option(names = {"--limit"},
    		arity = "1",
            description = "max hits to download (default 200)")
    private Integer limit = 200;

    @Option(names = {"--pages"},
    		arity = "1..2",
            description = "start and optional end hitpage. If only one value download single hitpage, default 1")
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
	private AbstractExtractor extractor;
	
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
    	try {
			runDownload();
		} catch (IOException e) {
			throw new RuntimeException("cannot download: ", e);
		}
    }

	private boolean runDownload() throws IOException {
		extractor = null;
		if (SearchSite.biorxiv.equals(site)) {
			extractor = new BiorxivExtractor(cProject);
		}
		if (extractor != null) {
			extractor.setDownloadLimit(limit);
			extractor.setPageList(pageList);
			extractor.setPageSize(pagesize);
			extractor.setQueryList(queryList);
			extractor.setSite(site);
			File projectDirectory = cProject.getOrCreateDirectory();
			extractor.setOutputDir(new File(projectDirectory, output));
			extractor.downloadHitPages();
		}
		return true;
	}


	private void httpClientGet(String url) throws ClientProtocolException, IOException {
		if (client == null) {	
			client = HttpClientBuilder.create().build();    
		}
	    HttpResponse response = client.execute(new HttpGet(url));
	    int statusCode = response.getStatusLine().getStatusCode();
	    if (HttpStatus.SC_OK == statusCode) {
	    	throw new RuntimeException("Bad Http status: "+statusCode);
	    }
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
}
