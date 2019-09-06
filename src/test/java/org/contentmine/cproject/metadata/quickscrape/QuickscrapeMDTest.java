package org.contentmine.cproject.metadata.quickscrape;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.AbstractMDAnalyzer;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.metadata.crossref.CrossrefMD;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.RectangularTable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** tests CProject created by Downloading
 * 
 * @author pm286
 *
 */
public class QuickscrapeMDTest {
	
	private static final String HYPERLINK_1 = "\")";
	private static final String HYPERLINK_0 = "=HYPERLINK(\"";
	
	public static final Logger LOG = Logger.getLogger(QuickscrapeMDTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	public static File GETPAPERS = new File("../getpapers");
//	public static File GETPAPERS_20160601 = new File(GETPAPERS, "20160601");
//	public static File GETPAPERS_20160601SCRAPED = new File(GETPAPERS_20160601, "quickscrape");
//	public static File GETPAPERS_QUICKSCRAPE_CSV = new File(GETPAPERS, "20160601quickscrape.csv");
//	public static File GETPAPERS_20160602SCRAPED = new File("../getpapers/20160602scraped");
	private static final String FILES_CSV = "files.csv";

	private static List<String> HEADERS = new ArrayList<String>();
	static {
		HEADERS.add("file");
		HEADERS.add( Type.QUICKSCRAPE.getCTreeMDFilename());
		HEADERS.add(CTree.FULLTEXT_HTML);
		HEADERS.add(CTree.FULLTEXT_PDF);
		HEADERS.add(CTree.FULLTEXT_XML);
		HEADERS.addAll(QuickscrapeMD.TERMS);
	}

	@Test

	public void testLargeCProject() {
		AbstractMDAnalyzer quickscrapeAnalyzer = new QuickscrapeAnalyzer(new CProject(CMineFixtures.GETPAPERS_SRC_20160601));
		CTreeList cTreeList = quickscrapeAnalyzer.getCTreeList();
		Assert.assertTrue("ctrees "+cTreeList.size(), cTreeList.size() >= 20);
		
	}
	
	@Test
	@Ignore // LONG
	public void testLargeCProjectJSON() {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160601);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, AbstractMetadata.Type.CROSSREF);
			String s = metadata.getJsonStringByPath(CrossrefMD.URL_PATH);
//			LOG.debug(s);
		}
		
	}

	@Test
	@Ignore // LONG
	public void testCreateJSONSheets() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		for (int day = 1; day <= 7; day++) {
			LOG.debug(day);
			File cProjectDir = new File(CMineFixtures.GETPAPERS_NEW, "2016060"+day);
			CProject cProject = new CProject(cProjectDir);
			CTreeList cTreeList = cProject.getOrCreateCTreeList();
			CrossrefMD.createCrossrefSpreadsheet(cTreeList, new File(CMineFixtures.GETPAPERS_TARGET, "2016060"+day+"/crossRef.csv"));
		}
	}
	
	/** find metadata
	 * @throws IOException 
	 * 
	 */
	@Test
	@Ignore // no longer useful?
	public void testGetHtmlMetadata() throws IOException {
		File outDir = new File(CMineFixtures.TEST_CROSSREF_DIR, "out/");
		Assert.assertTrue(outDir.exists() && outDir.isDirectory());
		CProject cProject = new CProject(outDir);
		Multiset<String> keySet = cProject.getOrCreateHtmlBiblioKeys();
		List<Multiset.Entry<String>> entries = CMineUtil.getEntryListSortedByCount(keySet);
		writeSet(keySet, new File(CMineFixtures.GETPAPERS_TARGET, "htmlKeys.csv"));
		LOG.debug(entries);
	}

	@Test
	@Ignore // LONG
	public void testGetPublishersAndTypes() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		Multiset<String> publisherSet = HashMultiset.create();
		Multiset<String> typeSet = HashMultiset.create();
		int total = 0;
		for (int i = 1; i <= 7; i++) {
			int count = 0;
			File cProjectDir = new File(CMineFixtures.GETPAPERS_NEW, "2016060"+i);
			CProject cProject = new CProject(cProjectDir);
			CTreeList cTreeList = cProject.getOrCreateCTreeList();
			for (CTree cTree : cTreeList) {
				AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, AbstractMetadata.Type.CROSSREF);
				String publisher = metadata.getJsonStringByPath(CrossrefMD.PUBLISHER_PATH);
				publisher = publisher.replaceAll("\\s+", " ");
				publisherSet.add(publisher);
				String type = metadata.getJsonStringByPath(CrossrefMD.TYPE_PATH);
				if (type != null) {
					typeSet.add(type);
				}
				count++;
			}
			LOG.debug(count);
			total += count;
		}
		writeSet(publisherSet, new File(CMineFixtures.GETPAPERS_TARGET, "publisherAll.csv"));
		writeSet(typeSet, new File(CMineFixtures.GETPAPERS_TARGET, "types.csv"));
	}
	
	@Test
	@Ignore // LONG
	public void testUniquePublishers() {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160601);
		Set<String> publisherSet = cProject.extractMetadataItemSet(
				AbstractMetadata.Type.CROSSREF, CrossrefMD.PUBLISHER_PATH);
		LOG.debug(publisherSet.size());
	}

	
	/**
	
	 * @throws IOException
	 */
	@Test
	public void testGetReservedFileSpreadsheet() throws IOException {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160601SCRAPED);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		RectangularTable csvTable = new RectangularTable();
		csvTable.addRow(HEADERS);
		for (CTree cTree : cTreeList) {
			List<String> row = new ArrayList<String>();
			String dirString = cTree.getDirectory().toString();
			dirString = dirString.replaceAll(".*http", "http:/");
			dirString = HYPERLINK_0+dirString.replaceAll("_", "/")+HYPERLINK_1;
			row.add(dirString);
			File resultsJson = cTree.getExistingQuickscrapeMD();
			addFile(row, resultsJson);
			File htmlFile = cTree.getExistingFulltextHTML();
			addFile(row, htmlFile);
			File pdfFile = cTree.getExistingFulltextPDF();
			addFile(row, pdfFile);
			File xmlFile = cTree.getExistingFulltextXML();
			addFile(row, xmlFile);
			addMetadata(row, resultsJson, HEADERS);
			csvTable.addRow(row);
		}
		csvTable.writeCsvFile(new File(CMineFixtures.GETPAPERS_SRC_20160601, FILES_CSV).toString());
	}
	
	private void addMetadata(List<String> row, File resultsJson, List<String> metadataList) {
		JsonObject element = null;
		if (resultsJson == null) {
			LOG.debug("null resultsJson");
		} else {
			try {
				element = (JsonObject) new JsonParser().parse(new FileReader(resultsJson));
			} catch (Exception e) {
				LOG.debug("cannot read file: "+resultsJson, e);
			}
		}
		for (String metadata : metadataList) {
			String value = "";
			if (element != null) {
				JsonElement elem =  element.get(metadata);
				if (elem == null) {
					
				} else if (elem instanceof JsonArray) {
					JsonArray array = (JsonArray) elem;
					value = array.size() == 0 ? "" : array.get(0).toString();
				} else if (elem instanceof JsonObject) {
					JsonArray array = ((JsonObject) elem).getAsJsonArray("value");
					value = array.size() == 0 ? "" : array.get(0).getAsString();
				} else {
					LOG.debug(elem.getClass());
				}
				value = value.substring(0, Math.min(100, value.length()));
			} else {
				value = "";
			}
			row.add(value);
		}
	}

	private void addFile(List<String> row, File file) {
		row.add(file == null ? "" : "Y");
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
	@Ignore // redo with open access papers
	public void testAnalyzeQuickscrape() {
		AbstractMDAnalyzer quickscrapeAnalyzer = new QuickscrapeAnalyzer(new CProject(CMineFixtures.GETPAPERS_SRC_20160601SCRAPED));
		Assert.assertTrue("ctrees "+quickscrapeAnalyzer.getCTreeList().size(), quickscrapeAnalyzer.getCTreeList().size() > 1900);
//		CTreeList cTreeList = quickscrapeAnalyzer.
	}
	
	@Test
	public void testAnalyzeCSV() throws IOException {
		RectangularTable table = RectangularTable.readCSVTable(CMineFixtures.QUICKSCRAPE20160601_CSV, true);
		Assert.assertEquals(451, table.size());
		Assert.assertEquals("["
				+ "filename_Q, URL_Q, Title_Q, Date_Q, PDFURL_Q, PDFfile_Q, HTMLURL_Q, HTMLfile_Q,"
				+ " XMLURL_Q, XMLfile_Q, DOI, Publisher_Q, Volume_Q, AuthorList_Q, Issue_Q, FirstPage_Q,"
				+ " Description_Q, Abstract_Q, Journal_Q, License_Q, Copyright_Q, ISSN_Q, QuickscrapeMD_Q"
				+ "]", table.getHeader().toString());

		String colHead = "DOI";
		List<String> columnValues = table.getColumn(table.getIndexOfColumn(colHead));
		Assert.assertEquals(451, columnValues.size());
		List<Multiset.Entry<String>> multisetList = table.extractSortedMultisetList(colHead);
		Assert.assertEquals(410, multisetList.size());
		List<Multiset.Entry<String>> uniqueMultisetList = table.extractUniqueMultisetList(colHead);
		Assert.assertEquals(408, uniqueMultisetList.size());
		List<Multiset.Entry<String>> duplicateMultisetList = table.extractDuplicateMultisetList(colHead);
		Assert.assertEquals(2, duplicateMultisetList.size());

	}


	
	//===============

	public void writeSet(Multiset<String> set, File file) throws IOException {
		List<Multiset.Entry<String>> entries = CMineUtil.getEntryListSortedByCount(set);
		RectangularTable csvTable = new RectangularTable();
		for (Multiset.Entry<String> entry : entries) {
			List<String> row = new ArrayList<String>();
			row.add(entry.getElement());
			row.add(String.valueOf(entry.getCount()));
			csvTable.addRow(row);
		}
		csvTable.writeCsvFile(file.toString());
	}

	
	


}
