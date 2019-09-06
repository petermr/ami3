package org.contentmine.cproject.metadata.crossref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.metadata.AbstractMDAnalyzer;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.MetadataManager;
import org.contentmine.cproject.metadata.MetadataObjects;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class CrossrefLongTest {

	private static final Logger LOG = Logger.getLogger(CrossrefLongTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** ALTERNATIVE TO GETPAPERS */
	@Test
	@Ignore // NET
	// uses net to call CrossRef
	/**
	 * This will normally be done by getpapers
	 * 
	 * searches with query "psychology" and Type.JOURNAL_ARTICLE between dates 2016-05-02 and 2016-05-03
	 * downloads as CSV file
	 * 
	 */
	public void testQueryCrossRefAndDownloadResults() throws IOException {
		CrossrefDownloader downLoader = new CrossrefDownloader();
		downLoader.getOrCreateFilter().setFromPubDate("2016-05-02");
		downLoader.getOrCreateFilter().setUntilPubDate("2016-05-03");
		downLoader.getOrCreateFilter().setType(CrossrefDownloader.Type.JOURNAL_ARTICLE);
		downLoader.setQuery("psychology");
		downLoader.setRows(1000);
		URL url = downLoader.getURL();
		LOG.debug("URL: "+url);
		List<String> urlList = downLoader.getUrlList();
		LOG.debug("downloaded: "+urlList.size());
		File targetDir = new File("target/pubstyle/xref");
		targetDir.mkdirs();
		IOUtils.writeLines(urlList, "\n", new FileOutputStream(new File(targetDir, "psych20160502.csv")));
	
	}

	/** ANALYZES METADATA FROM CPROJECT.
	 * 
	 * NOT FINISHED
	 * 
	 * @throws IOException
	 */
	@Test
	// NOT finished
	public void testGetMetadata() throws IOException {
		AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(CMineFixtures.GETPAPERS_SRC_20160601);
		
		Map<CTree, AbstractMetadata> metadataByCTree = crossrefAnalyzer.getOrCreateMetadataMapByCTreeMap(AbstractMetadata.Type.CROSSREF);
//		Iterator<Map.Entry<CTree,AbstractMetadata>> iterator = metadataByCTree.entrySet().iterator();
//		Assert.assertTrue("has next ", iterator.hasNext());
//		Map.Entry<CTree, AbstractMetadata> entry0 = metadataByCTree.entrySet().iterator().next();
//		Assert.assertEquals(20, metadataByCTree.entrySet().size());
		List<String> rowList = CMineFixtures.createSortedStringList(metadataByCTree);
		
		Assert.assertEquals("row0",""
		+"dir: "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/getpapers/20160601/10.1001jama.2016.7992\n"
		+CHESConstants.SRC_TEST_CPROJECT_TOP+"/getpapers/20160601/10.1001jama.2016.7992/crossref_result.json\n"
		+"=funderList: 0: []\n"
		+" licenseList: 0: []\n"
		+" linkList: 0: []\n"
		+" dateTime: 2016-06-01T23:24:00Z\n",
		rowList.get(0).toString());
		
		Assert.assertEquals("row1",""
				+"dir: "+CHESConstants.SRC_TEST_CPROJECT_TOP+"/getpapers/20160601/10.1002ab.21660\n"
				+""+CHESConstants.SRC_TEST_CPROJECT_TOP+"/getpapers/20160601/10.1002ab.21660/crossref_result.json\n"
				+"=funderList: 0: []\n"
				+" licenseList: 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\n"
				+" linkList: 1: [application/pdf http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1002%2Fab.21660]\n"
				+" dateTime: 2016-06-01T04:27:39Z\n",
		rowList.get(1).toString());
		
	}

	/** CREATE CSV FILE FROM CPROJECT
	 * 
	 */
	@Test
	
	/** starts with cProject created from crossref output.
	 * i.e. directories all contain a single crossref_result.json file.
	 * 
	 * this summarizes the metadata into a communal table and writes the CSV file
	 * 
	 * 
	 * @throws IOException
	 */
	public void testReadProjectAndWriteCSV() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		List<String> headers = new ArrayList<String>(Arrays.asList(new String[] {
				AbstractMetadata.HEAD_LICENSE,
				AbstractMetadata.HEAD_TITLE,
				AbstractMetadata.HEAD_DOI,
				AbstractMetadata.HEAD_PUBLISHER,
				AbstractMetadata.HEAD_PREFIX,
				AbstractMetadata.HEAD_DATE,
				AbstractMetadata.HEAD_KEYWORDS,
		}));
		int i = 1;
		MetadataObjects allMetadataObjects = new MetadataObjects();
		CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW+"/2016020"+i+"-articles"));
		AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
		crossrefAnalyzer.addRowsToTable(headers, AbstractMetadata.Type.CROSSREF);
		crossrefAnalyzer.createMultisets();
		crossrefAnalyzer.writeDOIs(new File(CMineFixtures.GETPAPERS_TARGET, "crossref_a_"+i+".doi.txt"));
		File file = new File(CMineFixtures.GETPAPERS_TARGET, "/crossref_a_"+i+".csv");
		crossrefAnalyzer.writeCsvFile(file);
		int size = cProject.size();
		LOG.debug("wrote: "+file+": "+size);
		// aggregate
		MetadataObjects metadataObjects = crossrefAnalyzer.getMetadataObjects();
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getPublisherMultiset(), new File(CMineFixtures.GETPAPERS_TARGET, "publishers_a_"+i+".txt").toString());
		allMetadataObjects.addAll(metadataObjects);
		allMetadataObjects.writeStringKeys("target/metadata/stringKeys_a.txt");
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getPublisherMultiset(),  new File(CMineFixtures.GETPAPERS_TARGET, "publishers_a7.txt"));
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getFinalLicenseSet(),  new File(CMineFixtures.GETPAPERS_TARGET, "licenses_a7.txt"));
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getPrefixSet(),  new File(CMineFixtures.GETPAPERS_TARGET, "prefix_a7.txt"));
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getFinalKeywordSet(),  new File(CMineFixtures.GETPAPERS_TARGET, "keyword_a7.txt"));
		
	}

	/** CREATES A CSV FILE FROM CROSSREF DIRECTORY.
	 * 
	 * and READS IT
	 * 
	 * @throws IOException
	 */
	@Test
	// PMR_ONLY
	public void testCreateCrossrefCommonCSV() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		List<String> headers = AbstractMetadata.COMMON_HEADERS;
		int i = 1;
		File articles = new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles");
		CProject cProject = new CProject(articles);
		AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
		crossrefAnalyzer.addRowsToTable(headers, AbstractMetadata.Type.CROSSREF);
		File csvFile= new File(CMineFixtures.GETPAPERS_TARGET, "crossref/common.csv");
		FileUtils.deleteQuietly(csvFile);
		crossrefAnalyzer.writeCsvFile(csvFile);
		Assert.assertTrue(csvFile.exists());
		// check file
		RectangularTable table = RectangularTable.readCSVTable(csvFile, true);
		Assert.assertEquals(12141, table.size());
	}
	
	/** EXTRACT DOIS FROM CROSSREF CSV file.
	 * 
	 * @throws IOException
	 */
	@Test
	// PMR only
	public void testMultipleDOIS() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		Multiset<String> allDois = HashMultiset.create();
		int i = 1;
		File file = new File(CMineFixtures.GETPAPERS_NEW, "crossref_a_"+i+".csv");
		RectangularTable table = RectangularTable.readCSVTable(file, true);
		List<String> dois = table.getColumn(MetadataManager.DOI);
		allDois.addAll(dois);
		String doisS = CMineUtil.getEntriesSortedByCount(allDois).toString();
		Assert.assertEquals(12141, allDois.size());
	}

}
