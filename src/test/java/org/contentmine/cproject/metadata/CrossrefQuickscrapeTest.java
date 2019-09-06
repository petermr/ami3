package org.contentmine.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** combines Crossref and Quickscrape metadata
 * 
 * strategy includes 
 * uses CSV files.
 * 
 * @author pm286
 *
 */
@Ignore // FIXME // bad Crossref table??

public class CrossrefQuickscrapeTest {

	public static final Logger LOG = Logger.getLogger(CrossrefQuickscrapeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static String CR = MetadataManager.CROSSREF;
	private static String DOI = MetadataManager.DOI;
	private static String QS = MetadataManager.QS;

	@Test
	public void testMergeCrossrefQuickscrape() throws IOException {
		RectangularTable tableQS = RectangularTable.readCSVTable(CMineFixtures.QUICKSCRAPE20160601_CSV, true);
		Assert.assertEquals(451, tableQS.size());
		Assert.assertEquals("[filename_Q, URL_Q, Title_Q, Date_Q, PDFURL_Q, PDFfile_Q, HTMLURL_Q,"
				+ " HTMLfile_Q, XMLURL_Q, XMLfile_Q, DOI, Publisher_Q, Volume_Q, AuthorList_Q, Issue_Q,"
				+ " FirstPage_Q, Description_Q, Abstract_Q, Journal_Q, License_Q, Copyright_Q, ISSN_Q,"
				+ " QuickscrapeMD_Q]", tableQS.getHeader().toString());

		RectangularTable tableCR = RectangularTable.readCSVTable(CMineFixtures.CROSSREF_SRC_20160601_CSV, true);
		Assert.assertEquals(18556, tableCR.size());
		Assert.assertEquals("[URL, Title, Date, PDFURL, DownloadedPDF, HTMLURL, DownloadedHTML, XMLURL,"
				+ " DownloadedXML, DOI, Publisher, Volume, AuthorList, Issue, FirstPage, Description,"
				+ " Abstract, Journal, License, Links, Copyright, ISSN, Keywords, QuickscrapeMD, CrossrefMD,"
				+ " PublisherMD]", tableCR.getHeader().toString());

		String colHeadCR = "DOI";
		
		RectangularTable mergedTable = tableQS.mergeOnUnsortedColumn(tableCR, "DOI");
		mergedTable.writeCsvFile(new File(CMineFixtures.GETPAPERS_TARGET, "20160601merged.csv").toString());
		Assert.assertEquals(451,  mergedTable.size());
		
		RectangularTable publisherTable = mergedTable.extractTable(Arrays.asList(new String[]{"Publisher", "Publisher_Q"}));
		publisherTable.writeCsvFile(new File(CMineFixtures.GETPAPERS_TARGET, "20160601publisher.csv").toString());

		RectangularTable commonTable = mergedTable.extractTable(Arrays.asList(new String[]{
				"Publisher", "Publisher_Q",
				"Volume", "Volume_Q",
				"Journal", "Journal_Q",
				"ISSN", "ISSN_Q",
				"Title", "Title_Q",
				}));
		commonTable.writeCsvFile(new File(CMineFixtures.GETPAPERS_TARGET, "20160601common.csv").toString());

	}
	
	@Test
	public void testFindFollowingValues() throws IOException {
		MetadataManager metadataManager = new MetadataManager();
		RectangularTable qsTable = metadataManager.readMetadataTable(CMineFixtures.QUICKSCRAPE20160601_CSV, QS);
		RectangularTable crTable = metadataManager.readMetadataTable(CMineFixtures.CROSSREF_SRC_20160601_CSV, CR);
		int qsSize = qsTable.size();
		int crSize = crTable.size();
		int row = metadataManager.findLastCorrespondingRow(CR, QS, DOI);
		List<String> values = metadataManager.findFollowingValues(CR, row, DOI);
		Assert.assertTrue("values "+values.size(), values.size() > 18000);
		int consumedCR = crSize - values.size();
		LOG.debug("consumed in CR: "+consumedCR+"; qsTotal: "+qsSize);
		
	}

	@Test
	public void testFindValuesNotIn() throws IOException {
		MetadataManager metadataManager = new MetadataManager();
		RectangularTable qsTable = metadataManager.readMetadataTable(CMineFixtures.QUICKSCRAPE20160601_CSV, QS);
		RectangularTable crTable = metadataManager.readMetadataTable(CMineFixtures.CROSSREF_SRC_20160601_CSV, CR);
		List<String> qsDois = qsTable.getValuesNotIn(crTable, DOI);
//		LOG.debug("QS only "+qsDois.size()+"\n"+qsDois);
		
	}

	@Test
	public void testWriteFollowingValues() throws IOException {
		MetadataManager metadataManager = new MetadataManager();
		RectangularTable qsTable = metadataManager.readMetadataTable(CMineFixtures.QUICKSCRAPE20160601_CSV, QS);
		RectangularTable crTable = metadataManager.readMetadataTable(CMineFixtures.CROSSREF_SRC_20160601_CSV, CR);
		int row = metadataManager.findLastCorrespondingRow(CR, QS, DOI);
		List<String> values = metadataManager.findFollowingValues(CR, row, DOI);
		Assert.assertTrue("values "+values.size(), values.size() > 18000);
	}

	@Test
	public void testFindLastCorrespondingValue() throws IOException {
		MetadataManager metadataManager = new MetadataManager();
		metadataManager.readMetadataTable(CMineFixtures.QUICKSCRAPE20160601_CSV, QS);
		metadataManager.readMetadataTable(CMineFixtures.CROSSREF_SRC_20160601_CSV, CR);
		int row = metadataManager.findLastCorrespondingRow(CR, QS, DOI);
		Assert.assertTrue("row "+row, row > 100);
//		Assert.assertEquals(108, row);
	}

}
