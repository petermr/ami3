package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractDownloader;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.BiorxivDownloader;
import org.contentmine.ami.tools.download.CurlDownloader;
import org.contentmine.ami.tools.download.CurlPair;
import org.contentmine.ami.tools.download.ResultSet;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlLink;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

/** test OCR.
 * 
 * @author pm286
 *
 */
public class AMIDownloadTest extends AbstractAMITest {
	public static final Logger LOG = Logger.getLogger(AMIDownloadTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static File DOWNLOAD_DIR = new File(SRC_TEST_TOOLS, "download");
	private static File BIORXIV_DIR = new File(DOWNLOAD_DIR, "biorxiv");
	private static File CLIMATE_DIR = new File(BIORXIV_DIR, "climate");
	
	@Test
	/** 
	 * run query
	 */
	public void testBiorxiv() throws Exception {
		String args = 
				"-p target/biorxiv"
				+ " --site biorxiv"
				+ " --query coronavirus"
				+ " --pagesize 40"
				+ " --pages 1 10"
				+ " --limit 500"
			;
		new AMIDownloadTool().runCommands(args);
	}

	@Test
	/** 
	 * run query
	 */
	public void testBiorxivClimate() throws Exception {
		String args = 
				"-p target/biorxiv/climate"
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata metadata"
				+ " --rawfiletypes html"
				+ " --pagesize 10"
				+ " --pages 1 3"
				+ " --limit 100"
			;
		new AMIDownloadTool().runCommands(args);
		Assert.assertTrue(new File("target/biorxiv/climate/metadata/page1.html").exists());
	}

	// extract fulltext with div[class~="fulltext-view"]
	
	
	/** to test that we can run curl from java
	 * 
	 * @throws Exception
	 */
	@Test 
	public void testCurlREST() throws Exception {
			
		String result = runCurlProcess("https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=coronavirus");
		System.out.println("EBI "+result);

		result = runCurlProcess("https://www.biorxiv.org/search/coronavirus");
		System.out.println("BIOX "+result);
		
//		https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank

	}

	@Test 
	/** downloads a single curlPair
	 * 
	 * @throws Exception
	 */
	public void testCurlDownloader() throws Exception {
			
		File downloadDir = new File("target/biorxiv/");
		CurlDownloader curlDownloader = new CurlDownloader();
		String fileroot = "10.1101/850289v1";
		
		CurlPair curlPair = BiorxivDownloader.createCurlPair(downloadDir, fileroot);
		curlDownloader.addCurlPair(curlPair);

		String result = curlDownloader.run();
		System.out.println("BIOX ["+result+"]");
		Assert.assertTrue(curlPair.getFile().getAbsoluteFile()+" exists", curlPair.getFile().exists());

	}
	

	@Test 
	/** download multiple URLs in a single run.
	 * Still appears to run each sequentially so relatively little performanace gain,
	 * but maybe worthwhile.
	 * 
	 * @throws Exception
	 */
	public void testCurlDownloaderMultiple() throws Exception {
		File downloadDir = new File("target/biorxiv/");
		CurlDownloader curlDownloader = new CurlDownloader();
		// these are verbatim from the resultSet file
		String[] fileroots = {
			       "/content/10.1101/2020.01.24.917864v1",
			       "/content/10.1101/850289v1",
			       "/content/10.1101/641399v2",
			       "/content/10.1101/844886v1",
			       "/content/10.1101/709089v1",
			       "/content/10.1101/823724v1",
			       "/content/10.1101/827196v1",
			       "/content/10.1101/823930v1",
			       "/content/10.1101/821561v1",
			       "/content/10.1101/819326v1",
			      };
		for (String fileroot : fileroots) {
			curlDownloader.addCurlPair(BiorxivDownloader.createCurlPair(downloadDir, fileroot));
		}
		
		curlDownloader.setTraceFile("target/trace.txt");
		curlDownloader.setTraceTime(true);
		String result = curlDownloader.run();
		LOG.debug("result ["+result+"]");

	}



	@Test
	/**
	 * 
	 */
	public void testCreateUnpopulatedCTreesFromResultSet() throws IOException {
		File targetDir = new File("target/biorxiv/climate");
		CMineTestFixtures.cleanAndCopyDir(CLIMATE_DIR, targetDir);
		
		CProject cProject = new CProject(targetDir);
		File metadataDir = cProject.getOrCreateExistingMetadataDir();
		/** reads existing resultSet file to create object */
		ResultSet resultSet = new BiorxivDownloader().setCProject(cProject).createResultSet(new File(metadataDir, "resultSet1.clean.html"));
		// result set had default 10 entries
		List<AbstractMetadataEntry> metadataEntryList = resultSet.getMetadataEntryList();
		Assert.assertEquals("metadata", 10, +metadataEntryList.size());
		// metadata directory had 3 results sets, each raw and clean
		Assert.assertEquals(6, metadataDir.listFiles().length);
		// remove all existing CTrees for the test
		cProject.cleanAllTrees();
		Assert.assertEquals(0,  cProject.getOrCreateCTreeList().size());
		// this is the __metadata directory
		Assert.assertEquals(1,  cProject.getDirectory().listFiles().length);
		// create trees from result set
		resultSet.createCTrees(cProject);
		Assert.assertEquals("Ctree count", 10, cProject.getOrCreateCTreeList().size());
		
		
	}
	
	@Test
	/**
	 * as above, but download landing pages
	 */
	public void testCreateCTreeLandingPagesFromResultSetIT() throws IOException {
		File targetDir = new File("target/biorxiv/climate");
		CMineTestFixtures.cleanAndCopyDir(CLIMATE_DIR, targetDir);
		
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		File metadataDir = cProject.getOrCreateExistingMetadataDir();
		AbstractDownloader biorxivDownloader = new BiorxivDownloader().setCProject(cProject);
		ResultSet resultSet = biorxivDownloader.createResultSet(new File(metadataDir, "resultSet1.clean.html"));
		List<String> fileroots = resultSet.getCitationLinks();
		CurlDownloader curlDownloader = new CurlDownloader();
		for (String fileroot : fileroots) {
			curlDownloader.addCurlPair(BiorxivDownloader.createCurlPair(cProject.getDirectory(), fileroot));
		}
		
		curlDownloader.setTraceFile("target/trace.txt");
		curlDownloader.setTraceTime(true);
		String result = curlDownloader.run();
		LOG.debug("result ["+result+"]");

//		Assert.assertEquals("Ctree count", 10, cProject.getOrCreateCTreeList().size());
		
	}
	
	@Test
	/** issues a search  and turns results into resultSet
	 * 
	 */
	public void testBiorxivSearchResultSetIT() throws IOException {
		File targetDir = new File("target/biorxiv/testsearch4");
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		String args = 
				"-p " + cProject.toString()
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata __metadata"
				+ " --rawfiletypes html"
				+ " --pagesize 4"
				+ " --pages 1 1"
				+ " --limit 4"
				+ " --resultset resultSet1.clean.html"
			;
		AMIDownloadTool downloadTool = new AMIDownloadTool();
		downloadTool.runCommands(args);
		Assert.assertTrue(new File(targetDir, "__metadata/resultSet1.html").exists());
		Assert.assertTrue(new File(targetDir, "__metadata/resultSet1.clean.html").exists());
		CTreeList cTreeList = new CProject(targetDir).getOrCreateCTreeList();
		Assert.assertEquals(4, cTreeList.size());
		File directory0 = cTreeList.get(0).getDirectory();
		Assert.assertTrue(new File(directory0, "landingPage.html").exists());
		Assert.assertTrue(new File(directory0, "rawFullText.html").exists());
		Assert.assertTrue(new File(directory0, "scholarly.html").exists());
		Assert.assertTrue(new File(directory0, "scrapedMetadata.html").exists());
	}

	@Test
	/** issues a search  and turns results into resultSet
	 * 
	 */
	public void testBiorxivSearchResultSetLargeIT() throws IOException {
		File targetDir = new File("target/biorxiv/testsearch");
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		String args = 
				"-p " + cProject.toString()
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata __metadata"
				+ " --rawfiletypes html pdf"
				+ " --pagesize 1000"
				+ " --pages 1 1"
				+ " --limit 1000"
				+ " --resultset resultSet1.clean.html"
			;
		AMIDownloadTool downloadTool = new AMIDownloadTool();
		downloadTool.runCommands(args);

	}

	
	@Test
	/** issues a search  and turns results into resultSet
	 * 
	 */
	public void testHALSearchResultSet() throws IOException {
		File targetDir = new File("target/hal/testsearch4");
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		String args = 
				"-p " + cProject.toString()
				+ " --site hal"
				+ " --query permafrost"
				+ " --metadata __metadata"
				+ " --rawfiletypes html"
				+ " --pagesize 4"
				+ " --pages 1 1"
				+ " --limit 4"
				+ " --resultset resultSet1.clean.html"
			;
		AMIDownloadTool downloadTool = new AMIDownloadTool();
		downloadTool.runCommands(args);
		Assert.assertTrue(new File(targetDir, "__metadata/resultSet1.html").exists());
		Assert.assertTrue(new File(targetDir, "__metadata/resultSet1.clean.html").exists());
		CTreeList cTreeList = new CProject(targetDir).getOrCreateCTreeList();
		Assert.assertEquals(4, cTreeList.size());
		File directory0 = cTreeList.get(0).getDirectory();
		Assert.assertTrue(new File(directory0, "landingPage.html").exists());
		Assert.assertTrue(new File(directory0, "rawFullText.html").exists());
		Assert.assertTrue(new File(directory0, "scholarly.html").exists());
		Assert.assertTrue(new File(directory0, "scrapedMetadata.html").exists());
		https://hal-sde.archives-ouvertes.fr/search/index/?q=permafrost&submit=&docType_s%5B%5D=ART&docType_s%5B%5D=COMM&docType_s%5B%5D=OUV&docType_s%5B%5D=COUV&docType_s%5B%5D=DOUV&docType_s%5B%5D=OTHER&docType_s%5B%5D=UNDEFINED&docType_s%5B%5D=REPORT&docType_s%5B%5D=THESE&docType_s%5B%5D=HDR&docType_s%5B%5D=LECTURE&submitType_s%5B%5D=file
			
	}


	

	

	


	// ====private====
	
	private String runCurlProcess(String url) throws IOException {
		String[] command = new String[] {"curl", "-X", "GET", url};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		String result = String.join("\n", IOUtils.readLines(process.getInputStream(), CMineUtil.UTF8_CHARSET));
		int exitCode = process.exitValue();
		return result;
	}
	

}
