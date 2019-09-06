package org.contentmine.cproject.metadata.quickscrape;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.metadata.AbstractMDAnalyzer;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.crossref.CrossrefMD;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class QuickscrapeLongTest {

	public static final Logger LOG = Logger.getLogger(QuickscrapeLongTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
		// SHOWCASE
	@Ignore // until we have some HTML files
		public void testCreateWriteQuickscrapeCSV() throws IOException {
//		LOG.debug("file: "+CMineFixtures.GETPAPERS_SRC_20160601SCRAPED);
//		AbstractMDAnalyzer quickscrapeAnalyzer = new QuickscrapeAnalyzer(new CProject(CMineFixtures.GETPAPERS_SRC_20160601SCRAPED));
		File file = new File(CMineFixtures.OPEN, "crossref.json");
		AbstractMDAnalyzer quickscrapeAnalyzer = new QuickscrapeAnalyzer(new CProject(CMineFixtures.GETPAPERS_SRC_20160601SCRAPED));
		Multimap<CTree, File> htmlMap = quickscrapeAnalyzer.getOrCreateCTreeFileMap(CTree.FULLTEXT_HTML);
		Assert.assertTrue("ctrees with html: "+htmlMap.size(), htmlMap.size() > 450);
		Map<CTree, AbstractMetadata> metadataByCTree = quickscrapeAnalyzer.getOrCreateMetadataMapByCTreeMap(AbstractMetadata.Type.QUICKSCRAPE);
		RectangularTable csvTable = new RectangularTable();
		csvTable.setTruncate(60);
		List<String> headers = Arrays.asList(
				new String[] {
					"filename_Q",
					"URL_Q",
					"Title_Q",
					"Date_Q",
					"PDFURL_Q",
					"PDFfile_Q",
					"HTMLURL_Q",
					"HTMLfile_Q",
					"XMLURL_Q",
					"XMLfile_Q",
					"DOI",			// common column
					"Publisher_Q",
					"Volume_Q",
					"AuthorList_Q",
					"Issue_Q",
					"FirstPage_Q",
					"Description_Q",
					"Abstract_Q",
					"Journal_Q",
					"License_Q",
					"Copyright_Q",
					"ISSN_Q",
					"QuickscrapeMD_Q",
//					"CrossrefMD",
//					"PublisherMD",
				});
		csvTable.addRow(headers);
		for (CTree cTree : metadataByCTree.keySet()) {
			AbstractMetadata metadata = metadataByCTree.get(cTree);
//			metadata.setCTree(cTree);
			if (metadata != null) {
				csvTable.clearRow();
			    csvTable.addCell(metadata.getProjectCTreeName());
			    csvTable.addCell(metadata.getURL());
			    csvTable.addCell(metadata.getTitle());
			    csvTable.addCell(metadata.getDate());
			    csvTable.addCell(metadata.getFulltextPDFURL());
			    csvTable.addCell(metadata.hasDownloadedFulltextPDF());
			    csvTable.addCell(metadata.getFulltextHTMLURL());
			    csvTable.addCell(metadata.hasDownloadedFulltextHTML());
			    csvTable.addCell(metadata.getFulltextXMLURL());
			    csvTable.addCell(metadata.hasDownloadedFulltextXML());
			    csvTable.addCell(metadata.getDOI());
			    csvTable.addCell(metadata.getPublisher());
			    csvTable.addCell(metadata.getVolume());
			    csvTable.addCell(metadata.getAuthorListAsStrings());
			    csvTable.addCell(metadata.getIssue());
			    csvTable.addCell(metadata.getFirstPage());
			    csvTable.addCell(metadata.getDescription());
			    csvTable.addCell(metadata.getAbstract());
			    csvTable.addCell(metadata.getJournal());
			    csvTable.addCell(metadata.getLicense());
			    csvTable.addCell(metadata.getCopyright());
			    csvTable.addCell(metadata.getISSN());
				csvTable.addCell(metadata.hasQuickscrapeMetadata());
//			    csvTable.addCell(metadata.hasCrossrefMetadata());
//			    csvTable.addCell(metadata.hasPublisherMetadata());
				csvTable.addCurrentRow();
			}
		}
//		csvTable.writeCsvFile(new File(CMineFixtures.GETPAPERS_SRC, "20160601quickscrape.csv"));
		csvTable.writeCsvFile(new File(CMineFixtures.GETPAPERS_TARGET, "20160601quickscrape.csv"));
//		
//		Map<CTree, String> titleByCTreeMap = quickscrapeAnalyzer.createValueByCTreeMap(CrossrefMD.TITLE_PATH);
	}

	@Test
		public void testQuickscrapeGetURLsByPublisher() throws IOException {
			CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160601);
			Multimap<String, String> map = cProject.extractMetadataItemMap(
					AbstractMetadata.Type.CROSSREF, CrossrefMD.PUBLISHER_PATH, CrossrefMD.URL_PATH);
			List<String> urlList = new ArrayList<String>();
			for (String key : map.keySet()) {
				List<String> urls = new ArrayList<String>(map.get(key));
				urlList.add(urls.get(0)); // get single URL for testing
			}
			Collections.shuffle(urlList);
			FileUtils.writeLines(new File(CMineFixtures.GETPAPERS_TARGET, "20160602/uniqueUrls.txt"), urlList, "\n");
		}

	//	@Test
	//	// SHOWCASE 
	//	public void testWriteCSV1() throws IOException {
	//		List<String> headers = Arrays.asList(new String[] {
	//				AbstractMetadata.LICENSE,
	//				AbstractMetadata.TITLE,
	//				AbstractMetadata.DOI,
	//		});
	//		int i = 1;
	//		int last = 7;
	//		last = 1;
	//		int totalSize = 0;
	//		MetadataObjects metadataObjects = new MetadataObjects();
	//		CSVTable csvTable = metadataObjects.getOrCreateCSVTable(headers);
	//		for (; i <= last; i++) {
	//			CProject cProject = new CProject(new File(GETPAPERS+"/2016020"+i+"-articles"));
	//			AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
	//			metadataObjects.setMetadataAnalyzer(crossrefAnalyzer);
	//			crossrefAnalyzer.getOrCreateMetadataMapByCTreeMap(AbstractMetadata.Type.CROSSREF);
	//			CTreeList cTreeList = cProject.getCTreeList();
	//			for (CTree cTree : cTreeList) {
	//				crossrefAnalyzer.addMetadataRowToCSVTable1(cTree, headers);
	//			}
	//			crossrefAnalyzer.writeDOIs(GETPAPERS+"/crossref_a_"+i+".doi.txt");
	//			String filename = GETPAPERS+"/crossref_a_"+i+".csv";
	//			int size = cTreeList.size();
	//			totalSize += size;
	//			LOG.debug("wrote: "+filename+": "+size);
	//			csvTable.writeCsvFile(filename);
	//		}
	//		LOG.debug("total: "+totalSize);
	//		metadataObjects.writeStringKeys("target/metadata/stringKeys_a.txt");
	//		LOG.debug("KL "+metadataObjects.getStringListKeys());
	//		metadataObjects.writeMultisetSortedByCount(metadataObjects.getPublisherMultiset(), GETPAPERS+"/publishers_a1.txt");
	//		metadataObjects.writeMultisetSortedByCount(metadataObjects.getFinalLicenseSet(), GETPAPERS+"/licenses_a1.txt");
	//		metadataObjects.writeMultisetSortedByCount(metadataObjects.getPrefixSet(), GETPAPERS+"/prefix_a1.txt");
	//		metadataObjects.writeMultisetSortedByCount(metadataObjects.getFinalKeywordSet(), GETPAPERS+"/keyword_a1.txt");
	//		
	//	}
	
		@Test
		// SHOWCASE
		/** gets metadata from <meta> tags */
		public void testGetQuickscrapeMetaTagdata() {
			QuickscrapeAnalyzer quickscrapeAnalyzer = new QuickscrapeAnalyzer(new CProject(CMineFixtures.GETPAPERS_SRC_20160601SCRAPED));
			Multiset<String> metaTagSet = quickscrapeAnalyzer.getHTMLMetaTagNameSet();
			List<Multiset.Entry<String>> tags = CMineUtil.getEntryListSortedByCount(metaTagSet);
			LOG.debug(tags);
		}

}
