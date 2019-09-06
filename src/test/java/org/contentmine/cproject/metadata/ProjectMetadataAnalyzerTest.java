package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math.stat.inference.TestUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;

public class ProjectMetadataAnalyzerTest {

	private static final Logger LOG = Logger.getLogger(ProjectMetadataAnalyzerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** CREATE List OF CROSSREF METADATA ITEMS.
	 * 
	 * @throws IOException
	 */
	
	public void testCreateCrossrefMetadataList() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS_SRC, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		List<AbstractMetadata> metadataList = projectAnalyzer.getOrCreateMetadataList();
	    // because git doesn't have empty directories
		Assert.assertTrue("mj", metadataList.size() >= 20); 
	}

	/** EXTRACTS KEYS FROM CROSSREF results_json files.
	 * 
	 * 
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore //fails on test ordering

	public void testExtractKeys() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS_SRC, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		List<AbstractMetadata> metadataList = projectAnalyzer.getOrCreateMetadataList();
		Assert.assertNotNull("metadataList: ", metadataList);
		Assert.assertEquals("metadataList size: "+metadataList.size(), 21, metadataList.size());
		Multiset<String> keys = projectAnalyzer.getOrCreateAllKeys();
		Assert.assertEquals("keys "+keys.size(), 478, keys.size());
		Assert.assertEquals("["
			+ "funder x 8, prefix x 20, deposited x 20, subject x 14, link x 19, source x 20,"
			+ " type x 20, title x 2", keys.toString().substring(0,  100));
		Assert.assertEquals("keys entry Set", 25, keys.entrySet().size());
		Assert.assertEquals("[funder x 8, prefix x 20, deposited x 20, subject x 14,"
				+ " link x 19, source x 20, type x 20, title x 20, URL x 20, score x 20,"
				+ " member x 20, reference-count x 20, published-online x 20, issued x 20,"
				+ " DOI x 20, indexed x 20, created x 20, author x 20, ISSN x 20,"
				+ " archive x 19, license x 19, published-print x 19, container-title x 20,"
				+ " subtitle x 20, publisher x 20]", keys.entrySet().toString());
	}

	/** EXTRACTS SHUFFLED URLS FROM GETPAPERS / CROSSREF directory to LIST.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testExtractURLs() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS_SRC, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		List<String> urls = cProject.extractShuffledCrossrefUrls();
		Assert.assertEquals("urls ", "["
				+ "http://dx.doi.org/10.1001/jama.2016.7992,"
				+ " http://dx.doi.org/10.1002/adhm.201600266,"
				+ " http://dx.doi.org/10.1002/adhm.201600181,"
				+ " http://dx.doi.org/10.1002/adhm.201600160,"
				+ " http://dx.doi.org/10.1002/adhm.201600126,"
				+ " http://dx.doi.org/10.1002/adhm.201600114,"
				+ " http://dx.doi.org/10.1002/adhm.201600045,"
				+ " http://dx.doi.org/10.1002/adfm.201601550,"
				+ " http://dx.doi.org/10.1002/adfm.201601123,"
				+ " http://dx.doi.org/10.1002/adfm.201601037,"
				+ " http://dx.doi.org/10.1002/adfm.201600909,"
				+ " http://dx.doi.org/10.1002/adfm.201600856,"
				+ " http://dx.doi.org/10.1002/adfm.201600813,"
				+ " http://dx.doi.org/10.1002/adfm.201504999,"
				+ " http://dx.doi.org/10.1002/adem.201600096,"
				+ " http://dx.doi.org/10.1002/acp.3240,"
				+ " http://dx.doi.org/10.1002/acp.3239,"
				+ " http://dx.doi.org/10.1002/acp.3238,"
				+ " http://dx.doi.org/10.1002/ab.21660,"
				+ " http://dx.doi.org/10.1002/adma.201601115"
				+ "]", urls.toString());
		LOG.trace(urls);
	}

	/** EXTRACTS SHUFFLED URLS FROM GETPAPERS / CROSSREF directory to FILE.
	 * 
	 */

	@Test
	/**
	 * 
	 * @throws IOException
	 */
	public void testExtractURLsToFile() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS_SRC, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		File urlFile = cProject.createAllowedFile(CProject.URL_LIST);
		FileUtils.deleteQuietly(urlFile);
		cProject.extractShuffledUrlsFromCrossrefToFile(urlFile);
		int size = FileUtils.readLines(urlFile).size();
		Assert.assertEquals(""+size, 20, size);
	}

	@Test
	/**
	 * EXTRACT SHUFFLED URLS
	 * 
	 * @throws IOException
	 */
	@Ignore
	public void testDownloadNewURLs() throws IOException {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SMALL);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		File urlFile = cProject.createAllowedFile(CProject.URL_LIST);
		FileUtils.deleteQuietly(urlFile);
		cProject.extractShuffledUrlsFromCrossrefToFile(urlFile);
		
	}


}
