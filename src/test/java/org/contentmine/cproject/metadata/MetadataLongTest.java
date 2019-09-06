package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.crossref.CrossrefMD;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class MetadataLongTest {

	private static final Logger LOG = Logger.getLogger(MetadataLongTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	/** reads a getpapers project and extracts the shuffled urls.
	 * 
	 * shuffledUrls.txt is in cProject directory
	 * 
	 * 
	 * @throws IOException
	 */
	public void testGetShuffledDOIURLs() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int i = 1; // file number
		CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
		File shuffledUrlsOutFilename = new File(cProject.getDirectory(), MetadataManager.SHUFFLED_URLS_TXT);
		Assert.assertEquals("filename ", 
				"/Users/pm286/workspace/cmdev/cproject/../getpapersNew/20160201-articles/shuffledUrls.txt",
				shuffledUrlsOutFilename.getAbsolutePath());
		cProject.extractShuffledUrlsFromCrossrefToFile(shuffledUrlsOutFilename);
		Assert.assertTrue("shuffled: ", shuffledUrlsOutFilename.exists());
		List<String> lines = FileUtils.readLines(shuffledUrlsOutFilename);
		Assert.assertEquals("lines "+lines.size(), 12141,  lines.size());
		lines = lines.subList(0,  10);
		Assert.assertEquals("lines "+lines.size(), "[http://dx.doi.org/10.1002/zoo.21264,"
				+ " http://dx.doi.org/10.1007/s41105-016-0048-8,"
				+ " http://dx.doi.org/10.1016/s2225-4110(16)00008-0,"
				+ " http://dx.doi.org/10.1017/s2045796016000044,"
				+ " http://dx.doi.org/10.1021/mpv013i002_797621,"
				+ " http://dx.doi.org/10.1016/s2225-4110(16)00007-9,"
				+ " http://dx.doi.org/10.1037/tra0000087.supp,"
				+ " http://dx.doi.org/10.1038/srep20371,"
				+ " http://dx.doi.org/10.1039/c6tc00170j,"
				+ " http://dx.doi.org/10.1049/iet-wss.2014.0090]"
				+ "",  lines.toString());
	}

	
	@Test
	/** reads a getpapers project and reads the common metadata.
	 * 
	 * @throws IOException
	 */
	public void testReadCrossrefCommonCSV() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int i = 1; // file number
		CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
		// pre-existing CSV file
		File csvFile = new File(cProject.getDirectory(), "crossref_common.csv");
		MetadataManager metadataManager = new MetadataManager();
		RectangularTable table = metadataManager.readMetadataTable(csvFile, MetadataManager.CROSSREF);
		Assert.assertEquals("[License, Title, DOI, Publisher, Prefix, Date, Keywords]", table.getHeader().toString());
	}

	@Test
	public void testGetURLsByPublisher() throws IOException {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160602);
		Multimap<String, String> map = cProject.extractMetadataItemMap(
				AbstractMetadata.Type.CROSSREF, CrossrefMD.PUBLISHER_PATH, CrossrefMD.URL_PATH);
		List<String> urlList = new ArrayList<String>();
		for (String key : map.keySet()) {
			List<String> urls = new ArrayList<String>(map.get(key));
			urlList.add(urls.get(0)); // get single URL for testing
		}
		Collections.shuffle(urlList);
		File urlFile = new File(CMineFixtures.GETPAPERS_TARGET, "20160602/uniqueUrls.txt");
		LOG.debug(urlFile);
		FileUtils.writeLines(urlFile, urlList, "\n");
//		FileUtils.writeLines(new File("../getpapers/20160602/uniqueUrls2.txt"), urlList, "\n");
	}

	@Test
	//	@Ignore // LONG
		public void testLargeCProjectJSON() {
			File cProjectDir = new File(CMineFixtures.GETPAPERS_SRC, "20160601");
			CProject cProject = new CProject(cProjectDir);
			CTreeList cTreeList = cProject.getOrCreateCTreeList();
			for (CTree cTree : cTreeList) {
				AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, AbstractMetadata.Type.CROSSREF);
				String s = metadata == null ? "?" : metadata.getJsonStringByPath(CrossrefMD.URL_PATH);
			}
			
		}
	
}
