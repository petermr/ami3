package org.contentmine.cproject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.metadata.AbstractMetadata;


public class CMineFixtures {
	
	private static final String SRC_TEST_RESOURCES = "src/test/resources";
	public static final Logger LOG = Logger.getLogger(CMineFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static File TEST_CMINE_DIR = new File(SRC_TEST_RESOURCES + "/" + CHESConstants.ORG_CM +"/"+"cproject");
	public final static File TEST_FILES_DIR = new File(TEST_CMINE_DIR, "files");
	public final static File TEST_OPEN_DIR = new File(TEST_CMINE_DIR, "open");
	
	public final static File TEST_DOWNLOAD_DIR = new File(TEST_CMINE_DIR, "download");
	
	public final static File TEST_MISC_DIR = new File(TEST_FILES_DIR, "misc");
	public final static File TEST_PROJECTS_DIR = new File(TEST_FILES_DIR, "projects");
	public final static File TEST_RESULTS_DIR = new File(TEST_FILES_DIR, "results");
	public final static File TEST_SVG_DIR = new File(TEST_FILES_DIR, "svg");
	public final static File TEST_TABLES_DIR = new File(TEST_FILES_DIR, "tables");
	public final static File TEST_PDF_SVG_DIR = new File(TEST_SVG_DIR, "pdfSvg");
	
	public static final File TEST_CROSSREF_DIR = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "crossref");
	public static final File TEST_CROSSREF_SAMPLE = new File(CMineFixtures.TEST_CROSSREF_DIR, "sample");
	public static final File TEST_QUICKSCRAPE_DIR = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "quickscrape");
	public static final File TEST_SAMPLE = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "sample");

	public static final File TARGET_DIR = new File("target");

	public static final File GETPAPERS_SRC = new File(TEST_CMINE_DIR, "getpapers");
	public static final File GETPAPERS_TARGET = new File(TARGET_DIR,  "getpapers");
	public static final File QUICKSCRAPE20160601_CSV = new File(GETPAPERS_SRC, "20160601quickscrape.csv");
	public static final File PMR_CLOSED = new File("../closed");
	public static final File GETPAPERS_OPEN = new File(TEST_CMINE_DIR, "open");
	
	public static final File CROSSREF_SRC_20160601_CSV = new File(GETPAPERS_SRC, "20160601crossref_1.csv");
	public static final File CROSSREF_SRC_20160601_MERGED_CSV = new File(GETPAPERS_SRC, "20160601merged.csv");

	public static File GETPAPERS_SRC_20160601 = new File(GETPAPERS_SRC, "20160601");
	public static File GETPAPERS_SRC_20160602 = new File(GETPAPERS_SRC, "20160602");
	public static File GETPAPERS_SRC_20160601SCRAPED = new File(GETPAPERS_SRC_20160601, "quickscrape");
	public static File GETPAPERS_SRC_20160602SCRAPED = new File(GETPAPERS_SRC, "20160602scraped");

	public static final String QUICKSCRAPE_MD = "quickscrapeMD";

	public static final File CROSSREF_SRC_A_1_CSV =  new File(GETPAPERS_SRC, "crossref_a_1.csv");
	
	// PMR only; test with existence
	public static final File GETPAPERS_NEW = new File("../getpapersNew");
	public static final File SCRAPER_DIR = new File("../../journal-scrapers/scrapers");
	public static final File GETPAPERS_SMALL = new File(GETPAPERS_NEW, "201601small");
	public static final File OPEN = new File("../open");

	
	public static boolean exist(File file) {
		boolean exist = true;
		if (file != null && !file.exists()) {
			LOG.trace("skipped local test: "+file);
			exist = false;
		}
		return exist;
	}

	/** make sorted list of entries for testing.
	 * 
	 * @param entrySet
	 * @return
	 */
	public static List<String> createSortedStringList(Set<Entry<CTree, AbstractMetadata>> entrySet) {
		List<String> stringList = new ArrayList<String>();
		if (entrySet != null) {
			for (Entry<CTree, AbstractMetadata> entry : entrySet) {
				stringList.add(entry.toString());
			}
			Collections.sort(stringList);
		}
		return stringList;
	}

	public static List<String> createSortedStringList(Map<CTree, AbstractMetadata> metadataByCTree) {
		return metadataByCTree == null ? new ArrayList<String>() : createSortedStringList(metadataByCTree.entrySet());
	}

}
