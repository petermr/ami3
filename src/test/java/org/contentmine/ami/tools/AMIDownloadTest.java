package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractMetadataEntry;
import org.contentmine.ami.tools.download.ResultSet;
import org.contentmine.ami.tools.download.biorxiv.BiorxivDownloader;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlUl;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Ignore;
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
	
    private Object first;
    private Object second;

    private List<Object> list;
    

	@Test
	/** 
	 * run query
	 */
	public void testBiorxivSmall() throws Exception {
		
		File target = new File("target/biorxiv1");
		if (target.exists()) {FileUtils.deleteDirectory(target);}
		MatcherAssert.assertThat(target+" does not exist", !target.exists());
		String args = 
				"-p " + target
				+ " --site biorxiv" // the type of site 
				+ " --query coronavirus" // the query
				+ " --pagesize 1" // size of remote pages (may not always work)
				+ " --pages 1 1" // number of pages
				+ " --fulltext pdf html"
				+ " --resultset raw clean"
//				+ " --limit 500"  // total number of downloaded results
			;
		new AMIDownloadTool().runCommands(args);
		Assert.assertTrue("target exists", target.exists());
		// check for reserved and non-reserved child files
		long fileCount0 = Files.walk(target.toPath(), AbstractMetadata.CPROJECT_DEPTH)
				.sorted()
				.peek(System.out::println)
				.count();
		Assert.assertEquals("files", 1, fileCount0);
		Assert.assertEquals("directory", 1, fileCount0);
		
		long fileCount = Files.walk(target.toPath(), AbstractMetadata.CTREE_DEPTH)
			.sorted()
			.count();
		Assert.assertEquals("files", 3, fileCount); // project metadata and 2 CTrees
		// files only
		fileCount = Files.walk(target.toPath(), AbstractMetadata.CTREE_CHILD_DEPTH)
				.sorted()
				.filter(f -> !f.toFile().isDirectory())
				.peek(System.out::println)
				.count();
		Assert.assertEquals("files", 8, fileCount); 
		fileCount = Files.walk(target.toPath())
				.sorted()
				.filter(f -> f.toFile().isDirectory()) // biorxiv1/, __metadata/ and 1 ctree
				.count();
		Assert.assertEquals("files", 3, fileCount);
	}

	@Test
	/** 
	 * run query
	 * VERY long
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
//	@Ignore
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
// I think this is an outdated Assert.
//		Assert.assertTrue(new File("target/biorxiv/climate/metadata/page1.html").exists());
//		these should work
		Assert.assertTrue(new File("target/biorxiv/climate/__metadata/resultSet3.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/landingPage.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/rawFullText.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/scholarly.html").exists());
		Assert.assertTrue(new File("target/biorxiv/climate/10_1101_2019_12_16_878348v1/scrapedMetadata.html").exists());
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

//	@Test 
//	/** downloads a single curlPair
//	 * 
//	 * @throws Exception
//	 */
//	public void testCurlDownloader() throws Exception {
//			
//		File downloadDir = new File("target/biorxiv/");
//		CurlDownloader curlDownloader = new CurlDownloader();
//		String fileroot = "10.1101/850289v1";
//		
//		CurlPair curlPair = new BiorxivDownloader().createLandingPageCurlPair(downloadDir, fileroot);
//		curlDownloader.addCurlPair(curlPair);
//
//		String result = curlDownloader.run();
//		System.out.println("BIOX ["+result+"]");
//		Assert.assertTrue(curlPair.getFile().getAbsoluteFile()+" exists", curlPair.getFile().exists());
//
//	}
	

//	@Test 
//	/** download multiple URLs in a single run.
//	 * Still appears to run each sequentially so relatively little performanace gain,
//	 * but maybe worthwhile.
//	 * 
//	 * @throws Exception
//	 */
//	public void testCurlDownloaderMultiple() throws Exception {
//		File downloadDir = new File("target/biorxiv/");
//		CurlDownloader curlDownloader = new CurlDownloader();
//		// these are verbatim from the resultSet file
//		String[] fileroots = {
//			       "/content/10.1101/2020.01.24.917864v1",
//			       "/content/10.1101/850289v1",
//			       "/content/10.1101/641399v2",
//			       "/content/10.1101/844886v1",
//			       "/content/10.1101/709089v1",
//			       "/content/10.1101/823724v1",
//			       "/content/10.1101/827196v1",
//			       "/content/10.1101/823930v1",
//			       "/content/10.1101/821561v1",
//			       "/content/10.1101/819326v1",
//			      };
//		for (String fileroot : fileroots) {
//			curlDownloader.addCurlPair(new BiorxivDownloader().createLandingPageCurlPair(downloadDir, fileroot));
//		}
//		
//		curlDownloader.setTraceFile("target/trace.txt");
//		curlDownloader.setTraceTime(true);
//		String result = curlDownloader.run();
//		LOG.debug("result ["+result+"]");
//
//	}



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
	
//	@Test
//	/**
//	 * as above, but download landing pages
//	 */
//	public void testCreateCTreeLandingPagesFromResultSetIT() throws IOException {
//		File targetDir = new File("target/biorxiv/climate");
//		CMineTestFixtures.cleanAndCopyDir(CLIMATE_DIR, targetDir);
//		
//		CProject cProject = new CProject(targetDir).cleanAllTrees();
//		File metadataDir = cProject.getOrCreateExistingMetadataDir();
//		AbstractDownloader biorxivDownloader = new BiorxivDownloader().setCProject(cProject);
//		ResultSet resultSet = biorxivDownloader.createResultSet(new File(metadataDir, "resultSet1.clean.html"));
//		List<String> fileroots = resultSet.getCitationLinks();
//		CurlDownloader curlDownloader = new CurlDownloader();
//		for (String fileroot : fileroots) {
//			curlDownloader.addCurlPair(biorxivDownloader.createLandingPageCurlPair(cProject.getDirectory(), fileroot));
//		}
//		
//		curlDownloader.setTraceFile("target/trace.txt");
//		curlDownloader.setTraceTime(true);
//		String result = curlDownloader.run();
//		LOG.debug("result ["+result+"]");
//
////		Assert.assertEquals("Ctree count", 10, cProject.getOrCreateCTreeList().size());
//		
//	}
	
	@Test
	/** issues a search  and turns results into resultSet
	 * 
	 * LONG 68 s
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
	public void testSections() {
		File projectDir = new File(DOWNLOAD_DIR,  "testsearch4");
		Assert.assertTrue(projectDir.toString(), projectDir.exists());
		String command = ""
				+ "-p "+projectDir+""
				+ ""
				;
		AMISectionTool sectionTool = new AMISectionTool();
		sectionTool.runCommands(command);
	}

	@Test
	public void testSearch() {
		File projectDir = new File(DOWNLOAD_DIR,  "testsearch99");
		Assert.assertTrue(projectDir.toString(), projectDir.exists());
		String command = ""
				+ "-p "+projectDir+""
				+ " --dictionary country disease funders species"
				;
		AMISearchTool searchTool = new AMISearchTool();
		searchTool.runCommands(command);
	}

	@Test
	/** issues a search  and turns results into resultSet
	 * 
	 * LONG 60
	 */
	public void testBiorxivSearchResultSetLargeIT() throws IOException {
		int pagesize = 3;
		int pages = 2;
		File targetDir = new File("target/biorxiv/testsearch" + pagesize);
		FileUtils.deleteQuietly(targetDir);
		CProject cProject = new CProject(targetDir).cleanAllTrees();
		cProject.cleanAllTrees();
		cProject.getOrCreateExistingMetadataDir();
		String args = 
				"-p " + cProject.toString()
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata __metadata"
// filetypes to download				
				+ " --rawfiletypes html pdf"
				+ " --pagesize " + pagesize
				+ " --pages 1 " + pages
//				+ " --limit " + (pagesize * pages)
//				+ " --resultset resultSet1.clean.html"
			;
		AMIDownloadTool downloadTool = new AMIDownloadTool();
		downloadTool.runCommands(args);

	}

	
	@Test
	/** issues a search  and turns results into resultSet
	 * 
	 */
	@Ignore // HTML DTD problem 
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
//		https://hal-sde.archives-ouvertes.fr/search/index/?q=permafrost&submit=&docType_s%5B%5D=ART&docType_s%5B%5D=COMM&docType_s%5B%5D=OUV&docType_s%5B%5D=COUV&docType_s%5B%5D=DOUV&docType_s%5B%5D=OTHER&docType_s%5B%5D=UNDEFINED&docType_s%5B%5D=REPORT&docType_s%5B%5D=THESE&docType_s%5B%5D=HDR&docType_s%5B%5D=LECTURE&submitType_s%5B%5D=file
			
	}
	
	// ============== Scielo =========
	/** extract result set. Very messy. seems that each result consists of two tables,
	 * the second table contains another table.
	 * 
   <center>
    <table width="600" border="0" cellpadding="0" cellspacing="0">
     <tbody>
      <tr>
       <td>
        <hr width="600"/>
        <font face="Verdana" size="1">
         <b>1 / 280</b>
        </font>
       </td>
      </tr>
     </tbody>
    </table>
   </center>
   <center>
    <table width="600" border="0" cellpadding="0" cellspacing="0">
     <tbody>
      <tr>
       <td align="left" width="115" valign="top" rowspan="6">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
         <tbody>
          <tr>
           <td width="28%">
            <input type="checkbox" name="listChecked" value="^m13555628^h4"/>
           </td>
           <td width="72%">
            <font face="verdana" size="1">
             <i>select</i>
            </font>
           </td>
          </tr>
          <tr>
           <td width="28%">
            <input type="image" name="toprint^m13555628" src="/iah/I/image/toprint.gif" border="0"/>
           </td>
           <td width="72%">
            <font face="verdana" size="1">
             <i>to print</i>
            </font>
           </td>
          </tr>
         </tbody>
        </table>
       </td>
       <td width="485">
        <!-- formato de apresentacao da base -->
        <table>
         <tbody>
== BIB == <tr>
           <td width="15%"> </td>
           <td>
            <font class="isoref" size="-1">Salgueiro, João Hipólito Paiva de Britto et al. 
             <font class="negrito" size="-1">Influence of oceanic-atmospheric interactions on extreme events of daily rainfall in the Sub-basin 39 located in Northeastern Brazil</font>. 
== BIB ==    <i>RBRH</i>, Dec 2016, vol.21, no.4, p.685-693. ISSN 2318-0331
             <br/>
            </font>
            <div align="left">
             <font class="isoref" size="-1">
              <font face="Symbol" color="#000080" size="1">·</font>
*==ABSTR EN== <a class="isoref" href="http://www.scielo.br/scielo.php?script=sci_abstract&amp;pid=S2318-03312016000400685&amp;lng=en&amp;nrm=iso&amp;tlng=en">abstract in english</a>
             </font> | 
             <a class="isoref" href="http://www.scielo.br/scielo.php?script=sci_abstract&amp;pid=S2318-03312016000400685&amp;lng=en&amp;nrm=iso&amp;tlng=pt">portuguese</a>
             <font face="Symbol" color="#000080" size="1">·</font>
*==URL EN==  <a class="isoref" href="http://www.scielo.br/scielo.php?script=sci_arttext&amp;pid=S2318-03312016000400685&amp;lng=en&amp;nrm=iso">text in english</a>
            </div>
           </td>
          </tr>
         </tbody>
        </table>
       </td>
      </tr>
     </tbody>
    </table>
   </center>
*/

	
	/** Download next page(s) ...
        <input type="image" name="Page1" src="/iah/I/image/1red.gif" width="6" height="15" border="0"/>
        <input type="image" name="Page2" src="/iah/I/image/2.gif" width="6" height="15" border="0"/>
...
        <input type="image" name="Page10" src="/iah/I/image/1.gif" width="6" height="15" border="0"/>
        <input type="image" name="Page10" src="/iah/I/image/0.gif" width="6" height="15" border="0"/>
        <input type="image" name="Page11" src="/iah/I/image/right.gif" border="0" width="17" height="17"/>
        <input type="image" name="Page28" src="/iah/I/image/last.gif" border="0" width="17" height="17"/>
	 */
	
	@Test 
	public void testCreateResultSet() {
		File resultSetClean1 = new File("src/test/resources/org/contentmine/ami/tools/download/scielo/resultSet1.mid.html");
		Assert.assertTrue("resultSet1.mid", resultSetClean1.exists());
		Element resultSet1mid = XMLUtil.parseQuietlyToRootElement(resultSetClean1);
/**
  <center>
    <table width="600" border="0" cellpadding="0" cellspacing="0">
     <tbody>
      <tr>
       <td align="left" width="115" valign="top" rowspan="6">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
         <tbody>
          <tr>
           <td width="28%">
            <input type="checkbox" name="listChecked" value="^m13555628^h4"/>
           </td>
           <td width="72%">
           
                      <td width="28%">
            <input type="checkbox" name="listChecked" value="^m13554408^h5"/>
           </td>

*/
//		tbody xmlns="">
//		   <tr>
//		    <td width="15%"> </td>
//		    <td>
//		     <font class="isoref" 
		List<Element> biblioList = XMLUtil.getQueryElements(resultSet1mid, ".//tbody/tr/td//.[font[@class='negrito']]");
		Assert.assertEquals("biblio", 10, biblioList.size());
		Element resultSetUl = new HtmlUl();
		for (Element biblio : biblioList) {
			HtmlLi li = new HtmlLi();
			resultSetUl.appendChild(li);
			biblio.detach();
			li.appendChild(biblio);
		}
		XMLUtil.writeQuietly(resultSetUl, new File("target/scielo/ul.html"), 1);
		
		System.out.println("B "+biblioList.size());
		
	}

//	https://www.infoq.com/articles/headless-selenium-browsers/
		

	@Test
	public void testAMISearch() {
		File testSearch3Dir = new File(DOWNLOAD_DIR, "testsearch3");
		Assert.assertTrue(testSearch3Dir.exists());
		CProject cProject = new CProject(testSearch3Dir);
		String cmd = ""
				+ "-p " + cProject + ""
				+ " --dictionary country"
				+ "";
		new AMISearchTool().runCommands(cmd);
		CTree cTree = cProject.getCTreeByName("10_1101_2020_01_12_903427v1");
		Assert.assertTrue(cTree.getDirectory().exists());
	}


	@Test
	public void testDownloadAndSearchLongIT() {
		File testSearch3Dir = new File(DOWNLOAD_DIR, "testsearch50");
		CProject cProject = new CProject(testSearch3Dir);
		int pagesize = 50;
		int pages = 1;
		String args = 
				"-p " + cProject.toString()
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --metadata __metadata"
				+ " --rawfiletypes html"
				+ " --pagesize " + pagesize
				+ " --pages 1 " + pages
			;
		AMIDownloadTool downloadTool = new AMIDownloadTool();
		downloadTool.runCommands(args);
		String cmd = ""
				+ "-p " + cProject + ""
				+ " --dictionary country disease funders"
				+ "";
		new AMISearchTool().runCommands(cmd);
//		CTree cTree = cProject.getCTreeByName("10_1101_2020_01_12_903427v1");
//		Assert.assertTrue(cTree.getDirectory().exists());
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
