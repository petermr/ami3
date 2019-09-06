package org.contentmine.cproject.metadata;

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
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.metadata.crossref.CrossrefMD;
import org.contentmine.cproject.metadata.quickscrape.QuickscrapeMD;
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
public class MetadataTest {
	
	public static final Logger LOG = Logger.getLogger(MetadataTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String HYPERLINK_1 = "\")";
	private static final String HYPERLINK_0 = "=HYPERLINK(\"";
	
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
//	@Ignore // LONG
	/** CREATE CROSSREF CSV from GETPAPERS PROJECT.
	 * 
	 * @throws IOException
	 */
	public void testCreateJSONSheets() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int day = 1;
		File cProjectDir = new File(CMineFixtures.GETPAPERS_NEW, "2016060"+day);
		CProject cProject = new CProject(cProjectDir);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		CrossrefMD.createCrossrefSpreadsheet(cTreeList, new File(CMineFixtures.GETPAPERS_TARGET, "2016060"+day+"/crossRef.csv"));
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
		LOG.trace(entries);
	}

	@Test
	/** EXTRACT PUBLISHERS and TYPES FROM CROSSREF.
	 * 
	 * @throws IOException
	 */
	public void testGetPublishersAndTypes() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		Multiset<String> publisherSet = HashMultiset.create();
		Multiset<String> typeSet = HashMultiset.create();
		int i = 0;
		int count = 0;
		File cProjectDir = new File(CMineFixtures.GETPAPERS_NEW, "2016060"+i);
		CProject cProject = new CProject(cProjectDir);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getCTreeMetadata(cTree, AbstractMetadata.Type.CROSSREF);
			if (metadata != null) {
				String publisher = metadata.getJsonStringByPath(CrossrefMD.PUBLISHER_PATH);
				publisher = publisher.replaceAll("\\s+", " ");
				publisherSet.add(publisher);
				String type = metadata.getJsonStringByPath(CrossrefMD.TYPE_PATH);
				if (type != null) {
					typeSet.add(type);
				}
			}
			count++;
		}
		writeSet(publisherSet, new File(CMineFixtures.GETPAPERS_TARGET, "publisherAll.csv"));
		writeSet(typeSet, new File(CMineFixtures.GETPAPERS_TARGET, "quickscrape/types.csv"));
	}
	
	@Test
//	@Ignore
	public void testUniquePublishers() {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160602);
		Set<String> publisherSet = cProject.extractMetadataItemSet(
				AbstractMetadata.Type.CROSSREF, CrossrefMD.PUBLISHER_PATH);
		LOG.trace(publisherSet.size());
	}

	
	/**
	
	 * @throws IOException
	 */
	// EMPTY DIRECTORY
//	@Test
//	public void testGetReservedFileSpreadsheet() throws IOException {
//		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160602SCRAPED);
//		CTreeList cTreeList = cProject.getCTreeList();
//		Assert.assertEquals(12,  cTreeList.size());
//		RectangularTable csvTable = new RectangularTable();
//		csvTable.addRow(HEADERS);
//		for (CTree cTree : cTreeList) {
//			List<String> row = new ArrayList<String>();
//			String dirString = cTree.getDirectory().toString();
//			dirString = dirString.replaceAll(".*http", "http:/");
//			dirString = HYPERLINK_0+dirString.replaceAll("_", "/")+HYPERLINK_1;
//			row.add(dirString);
//			File resultsJson = cTree.getExistingResultsJSON();
//			addFile(row, resultsJson);
//			File htmlFile = cTree.getExistingFulltextHTML();
//			addFile(row, htmlFile);
//			File pdfFile = cTree.getExistingFulltextPDF();
//			addFile(row, pdfFile);
//			File xmlFile = cTree.getExistingFulltextXML();
//			addFile(row, xmlFile);
//			addMetadata(row, resultsJson, HEADERS);
//			csvTable.addRow(row);
//		}
//		csvTable.writeCsvFile(new File(CMineFixtures.GETPAPERS_TARGET, FILES_CSV).toString());
//	}
	
	private void addMetadata(List<String> row, File resultsJson, List<String> metadataList) {
		JsonObject element = null;
		try {
			element = (JsonObject) new JsonParser().parse(new FileReader(resultsJson));
		} catch (Exception e) {
			LOG.error("cannot read file: "+resultsJson, e);
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
					LOG.trace(elem.getClass());
				}
				value = value.substring(0, Math.min(100, value.length()));
			}
			row.add(value);
		}
	}

	private void addFile(List<String> row, File file) {
		row.add(file == null ? "" : "Y");
	}

//	@Test
	// EMPTY
//	public void testMetadataValues() {
//	
//		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC_20160602SCRAPED);
//		Multimap<CTree, File> htmlMap = cProject.extractCTreeFileMapContaining(CTree.FULLTEXT_HTML);
//		for (CTree cTree : htmlMap.keySet()) {
//			AbstractMetadata metadata = AbstractMetadata.getMetadata(cTree, AbstractMetadata.Type.QUICKSCRAPE);
//			LOG.debug("MD "+metadata);
//			if (metadata != null) {
//				String urlString = metadata.getJsonMapStringByPath(CrossrefMD.TITLE_PATH);
//				LOG.debug("Arrays "+urlString);
//			}
//		}
//	}

	
	//===============

	private void writeSet(Multiset<String> set, File file) throws IOException {
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
