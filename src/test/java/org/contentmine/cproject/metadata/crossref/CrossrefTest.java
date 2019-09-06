package org.contentmine.cproject.metadata.crossref;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.AbstractCM;
import org.contentmine.cproject.metadata.CMDOI;
import org.contentmine.cproject.metadata.CMURL;
import org.contentmine.cproject.metadata.DOIResolver;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * MAINLY ABSTRACTED TO MD
 * 
 * @author pm286
 *
 */
public class CrossrefTest {

	
	private static final Logger LOG = Logger.getLogger(CrossrefTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String QUICKSCRAPE = //  /Users/pm286/.nvm/v0.10.38/bin/quickscrape
			"/usr/local/n/versions/node/6.2.1/bin/quickscrape";
	private static final String GETPAPERS = //  /usr/local/n/versions/node/6.2.1/bin/getpapers
			"/usr/local/n/versions/node/6.2.1/bin/getpapers";
	private static final String LS = "ls > grotqz";

	/**
	 * RUN CROSSREF WITHOUT GETPAPERS
	 * @throws IOException
	 */
	@Test
//	@Ignore // downloads many
	public void testCreateDownloadAgro() throws IOException {
		String fromDate = "2010-05-02";
		String untilDate = "2016-05-03";
		String query = "marchantia";
		File outputDir = new File("target/crossref/"+query);
		CrossrefDownloader.runCrossRefQuery(fromDate, untilDate, query, true, 500, outputDir);
		
		
	}
	
	/**
	 * RUN CROSSREF WITHOUT GETPAPERS
	 * @throws IOException
	 */

	@Test
	@Ignore // downloads many
	public void testCreateDownloadTheoChem() throws IOException {
		String fromDate = "2016-04-03";
		String untilDate = "2016-05-03";
		String query = "theoretical+chemistry";
		CrossrefDownloader.runCrossRefQuery(fromDate, untilDate, query, true, 50, new File("target/xref/theochem"));
	}

	/**
	 * RUN CROSSREF WITHOUT GETPAPERS
	 * @throws IOException
	 */
	@Test
	@Ignore // downloads many
	public void testCreateDownloadGlutamate() throws IOException {
		String fromDate = "2016-04-03";
		String untilDate = "2016-05-03";
		String query = "glutamate";
		CrossrefDownloader.runCrossRefQuery(fromDate, untilDate, query, true, 50, new File("target/xref/glutamate"));
	}

	/** PURPOSE not yet documented */
	@Test
	public void testQuickscrape() throws IOException {
		File scrapers = new File("workspace/journalScrapers/scrapers");
		File outdir = new File("target/crossref");
		System.out.println("quickscrape: "+QUICKSCRAPE);
	    Process process = CMineUtil.runProcess(
	    		new String[]{QUICKSCRAPE, "-q", "http://dx.plos.org/10.1371/journal.pone.0075293", 
	    				"-d", scrapers.toString(), "-o", outdir.toString()}, null);
	}

	/** PURPOSE not yet documented */
	@Test
	public void testGetpapers() throws IOException {
		String query = "aardvark";
		File outdir = new File("target/crossref");
	    Process process = CMineUtil.runProcess(
	    		new String[]{GETPAPERS, "-q", query, 
	    				"-o", outdir.toString()}, null);
	}

	/** PURPOSE not yet documented */
	@Test
	public void testLs() throws IOException {
		String query = "aardvark";
		File outdir = new File("target/crossref");
	    Process process = CMineUtil.runProcess(new String[]{LS}, null);
	}


	/** RESOLVE DOI. may not be necessary.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore // requires net
	public void testResolveDOI() throws Exception {
		String crossRefDOI = "http://dx.doi.org/10.3389/fpsyg.2016.00565";
		String s = new DOIResolver().resolveDOI(crossRefDOI);
//		LOG.debug("DOI "+s);
	}

	/** DIRECT DOWNLOAD OF CROSSREF METADATA for several days*/
	@Test
	@Ignore // downloads
	public void testCreateDaily() throws IOException {
		String fromDate = "2016-05-02";
		String untilDate = "2016-05-03";
		String query = null;
		boolean resolveDois = true;
		boolean skipHyphenDois = true;
		int rows = 10;
		int offset = 0;
		for (int j = 0; j < 5; j++) {
			CrossrefDownloader.runCrossRefDate(fromDate, untilDate, resolveDois, rows, offset, new File("xref/daily/20100601/"));
			offset += rows;
		}
	}

	/** EXTRACT METADATA from CROSSREF Json */
	@Test
	public void testGetMetadataObjectList() throws Exception {
		
		CrossrefMD crossrefMetadata = new CrossrefMD();
		crossrefMetadata.readMetadataArrayFromConcatenatedFile(new File(CMineFixtures.TEST_CROSSREF_SAMPLE, "crossref_results.json"));
		List<JsonObject> metadataObjectList = crossrefMetadata.getMetadataObjectListFromConcatenatedArray();
		Assert.assertEquals("items", 1552, metadataObjectList.size());
	}

	/** EXTRACT METADATA from CROSSREF Json */
	@Test
	public void testGetMetadataKeys() throws Exception {
		
		CrossrefMD crossrefMetadata = new CrossrefMD();
		crossrefMetadata.readMetadataArrayFromConcatenatedFile(new File(CMineFixtures.TEST_CROSSREF_SAMPLE, "crossref_results.json"));
		List<JsonObject> metadataObjectList = crossrefMetadata.getMetadataObjectListFromConcatenatedArray();
		JsonObject object0 = metadataObjectList.get(0);
		Assert.assertEquals("fields", 24, object0.entrySet().size());
	}

	/** GET CROSSREF METADATA FROM JSON
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore //fails on test ordering

	public void testGetCrossrefMetadataFromJson() throws Exception {
		CrossrefMD crossrefMetadata = new CrossrefMD();
		crossrefMetadata.readMetadataArrayFromConcatenatedFile(new File(CMineFixtures.TEST_CROSSREF_SAMPLE, "crossref_results.json"));
		crossrefMetadata.getOrCreateMetadataList();
		Assert.assertEquals("KEYS ", 36356, +crossrefMetadata.getKeysmap().size());
		Assert.assertEquals("unique KEYS ", 35,  crossrefMetadata.getKeysmap().entrySet().size());
		crossrefMetadata.debugStringValues();
		crossrefMetadata.debugNumberValues();
		String allKeys = CMineUtil.getEntriesSortedByCount(crossrefMetadata.getAllKeys()).toString();
		Assert.assertTrue(allKeys.contains("prefix x 1552")  && allKeys.contains("title x 1552"));
		/**
		// omitted as problems with sorting entries
		Assert.assertEquals("allkeys", 
				"[prefix x 1552, deposited x 1552, source x 1552, type x 1552, title x 1552, URL x 1552,"
				+ " score x 1552, member x 1552, reference-count x 1552, issued x 1552, DOI x 1552,"
				+ " indexed x 1552, created x 1552, container-title x 1552, subtitle x 1552, publisher x 1552,"
				+ " ISSN x 1445, author x 1209, page x 1109, published-print x 1060, published-online x 1051,"
				+ " volume x 1036, issue x 875, subject x 777, license x 706, alternative-id x 700, link x 666,"
				+ " update-policy x 227, ISBN x 172, archive x 163, assertion x 146, funder x 126, article-number x 36,"
				+ " editor x 12, update-to x 8]" , allKeys.substring(0, 200));
				*/
		String authors = crossrefMetadata.getAuthorListAsStrings().toString();
		Assert.assertEquals("author", "[Martin K. Heath, Lindsey Brooks D., Ma Jianguo, Nichols Timothy C., Jiang Xiaoning, Dayton Paul A., Lee Ming-Che, Yang Ying-Chin, Chen Yen-Cheng, Chang Bee-Song, Li Yi-Chen, Huang Shih-Che, Miao Xin," , authors.substring(0, 200));
		String funders = crossrefMetadata.getFunderEntriesSortedByCount().toString();
		Assert.assertEquals("funders", "[DEC 2013/09/B/ST5/03391 National Science Centre of Poland x 2, 024.001.035 Ministry of Education and Science 10.13039/501100005992 x 2,  CRO Aviano x 2,  National Science Foundation 10.13039/10000000" , funders.substring(0, 200));
		String licenses = crossrefMetadata.getLicenseEntriesSortedByCount().toString();
		Assert.assertEquals("licenses", "[http://www.elsevier.com/tdm/userlicense/1.0/ x 282, http://doi.wiley.com/10.1002/tdm_license_1 x 162, http://onlinelibrary.wiley.com/termsAndConditions x 154, http://www.springer.com/tdm x 130, http:" , licenses.substring(0, 200));
		String links = crossrefMetadata.getLinkEntriesSortedByCount().toString();
		Assert.assertEquals("links", "[application/pdf http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1111%2Fjoic.12292, application/pdf http://stacks.iop.org/0295-5075/114/i=3/a=30006/pdf, text/plain http://api.elsevier.com/conten" , links.substring(0, 200));

	}

	@Test
	public void testJsonPrimitive() {
		String json = "{"
				+ "\"string\": \"mystring\","
				+ "\"number\": 42"
				+ "}";
		LOG.debug("JS "+json);
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
		CrossrefMD crossrefMD = new CrossrefMD();
		crossrefMD.analyzeElement(0, jsonObject.get("string"));
		crossrefMD.analyzeElement(0, jsonObject.get("number"));
		
	}

	@Test
	public void testJsonPrimitive1() {
		String json = "{"
				+ "\"indexed\": {"
				+ "  \"date-parts\": ["
		        + "     ["
		        + " 	  2016,"
		        + " 	  6,"
		        + "	      2"
		        + "	    ]"
		        + "	  ],"
		        + "	  \"date-time\": \"2016-06-02T11:40:24Z\","
		        + "	  \"timestamp\": 1464867624071"
		        +	"},"
		        +	"\"reference-count\": 61,"
		        +	"\"publisher\": \"Elsevier BV\""
				+ "}";
		
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
		CrossrefMD crossrefMetadata = new CrossrefMD();
		crossrefMetadata.analyzeElement(0, jsonObject.get("publisher"));
		crossrefMetadata.analyzeElement(0, jsonObject.get("reference-count"));
		crossrefMetadata.analyzeElement(0, jsonObject.get("indexed"));
		
	}
	
	/** UNDOCUMENTED */
	@Test 
	@Ignore // misses pubfilter.txt
	public void testCrossRefURLs2DOI2FilterCSVFiles() throws IOException {
		int MAX = 99999;
		String fromDate = "2016-05-01";
		int start = 0;
		int delta = 100;
		File pubFilterFile = new File(CMineFixtures.TEST_CROSSREF_DIR, "pubFilter.txt");
		File projectTop = new File(CMineFixtures.TEST_CROSSREF_DIR, "daily/"+fromDate+"/");
		List<List<String>> rows = new ArrayList<List<String>>();
		rows.add(Arrays.asList(new String[] {"trees", "child", "dois", "urls", "filter", "first"}));

		List<String> doiPrefixList = new ArrayList<String>();
		List<String> urlPrefixList = new ArrayList<String>();
		List<String> pubPrefixList = new ArrayList<String>();
		List<Pair<Pattern, String>> filterList = readFilter(pubFilterFile);
		
		for (; start < MAX; start+=delta) {
			String subPath = createSubPath0(fromDate, start, delta);
			File cProjectDir = new File(projectTop, subPath);
			if (!cProjectDir.exists()) {
				LOG.debug("break");
				break;
			}
			CProject cProject = new CProject(cProjectDir);
			List<String> row = new ArrayList<String>();
			row.add(String.valueOf(cProject.getOrCreateCTreeList().size()));
			row.add(String.valueOf(cProject.getAllChildDirectoryList().size()));
			
			List<String> doiNames = FileUtils.readLines(new File(projectTop, subPath+".dois.txt"));
			List<CMDOI> doiList = CMDOI.readDois(doiNames);
			doiPrefixList.addAll(AbstractCM.getPrefixList(doiList));
			row.add(String.valueOf(doiNames.size()));
			
			List<String> urlNames = FileUtils.readLines(new File(projectTop, subPath+".urls.txt"));
			List<CMURL> urlList = CMURL.readUrls(urlNames);
			for (String urlPrefix : AbstractCM.getPrefixList(urlList)) {
				if (urlPrefix != null) {
					urlPrefixList.add(urlPrefix);
				}
			}
			row.add(String.valueOf(urlNames.size()));

			for (String urlPrefix : AbstractCM.getPrefixList(urlList)) {
				if (urlPrefix != null) {
					pubPrefixList.add(normalizePub(urlPrefix, filterList));
				}
			}
			row.add(String.valueOf(urlNames.size()));

			try {
				row.add(String.valueOf(FileUtils.readLines(new File(projectTop, subPath+".urls.filter.txt")).size()));
			} catch (Exception e) {
				LOG.warn(e);
			}
			row.add(doiNames.size() > 0 ? doiNames.get(0) : "-");
			row.add(urlNames.size() > 0 ? urlNames.get(0) : "-");
			rows.add(row);
		}
		File targetCSVTest = new File("target/csvtest/");
		targetCSVTest.mkdirs();
		RectangularTable csvTable = new RectangularTable();
		csvTable.setRows(rows);
		csvTable.writeCsvFile(new File(targetCSVTest, "projects.csv").toString());
		
		RectangularTable doiCounter = new RectangularTable();
		doiCounter.createMultisetAndOutputRowsWithCounts(doiPrefixList, new File(targetCSVTest, "doiCount.csv").toString());
		RectangularTable urlCounter = new RectangularTable();
		urlCounter.createMultisetAndOutputRowsWithCounts(urlPrefixList, new File(targetCSVTest, "urlCount.csv").toString());
		RectangularTable pubCounter = new RectangularTable();
		pubCounter.createMultisetAndOutputRowsWithCounts(pubPrefixList, new File(targetCSVTest, "pubCount.csv").toString());
		
	}
	


	@Test
	public void testAggregateCrossrefDOIPrefixes() {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160601);
//		cProject.normalizeDOIBasedDirectoryCTrees();
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		
		List<String> doiPrefixList = cProject.getDOIPrefixList();
//		LOG.debug("DOIPREFIX "+doiPrefixList);
	}
	
	// =========================
	
	private String normalizePub(String urlPrefix, List<Pair<Pattern, String>> filterList) {
		for (Pair<Pattern, String> filter : filterList) {
			Pattern pattern = filter.getLeft();
			Matcher matcher = pattern.matcher(urlPrefix);
			if (matcher.find()) {
				urlPrefix = urlPrefix.replaceAll(pattern.toString(), filter.getRight());
			}
		}
		return urlPrefix;
	}

	private List<Pair<Pattern, String>> readFilter(File pubFilterFile) throws IOException {
		List<String> lines = FileUtils.readLines(pubFilterFile);
		List<Pair<Pattern, String>> filterList = new ArrayList<Pair<Pattern, String>>();
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			if (parts.length == 0 || parts.length > 2) {
				LOG.warn("filter requires 1/2 parts");
				continue;
			}
			String replace = parts.length == 1 ? "" : parts[1];
//			LOG.debug(">>"+replace);
			Pattern pattern = Pattern.compile(parts[0]);
			Pair<Pattern, String> filter = new MutablePair<Pattern, String>(pattern, replace);
			filterList.add(filter);
		}
		return filterList;
	}

	private String createSubPath(String fromDate, int count, int delta) {
		String s1 = createSubPath0(fromDate, count, delta);
		String s2 = fromDate+"/"+s1;
		LOG.debug("SUBPATH "+s1);
		return s2;
	}

	private String createSubPath0(String fromDate, int count, int delta) {
		String fromDateMin = fromDate.replaceAll("\\-", "");
		String s1 = fromDateMin+"_"+count+"_"+delta;
		return s1;
	}
}
