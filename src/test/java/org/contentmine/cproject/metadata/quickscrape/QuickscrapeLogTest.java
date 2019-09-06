package org.contentmine.cproject.metadata.quickscrape;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class QuickscrapeLogTest {

	private static final String TOTAL_LOG = "total.log";
	private static final Logger LOG = Logger.getLogger(QuickscrapeLogTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String QUICKSCRAPE_2016_08_25_22_54_LOG = "quickscrape.2016-08-25-22-54.log";
	private static final String QUICKSCRAPE_2016_08_26_09_44_LOG = "quickscrape.2016-08-26-09-44.log";
	
	@Test
	public void testAnalyzeLog() throws IOException {
		QuickscrapeLog quickscrapeLog = QuickscrapeLog.readLog(new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, QUICKSCRAPE_2016_08_25_22_54_LOG));
		Assert.assertEquals(575,  quickscrapeLog.getLines().size());
		quickscrapeLog.analyze();
		List<QSRecord> records = quickscrapeLog.getQSURLRecords();
		Assert.assertEquals(268, records.size());
		List<QSRecord> no200s = quickscrapeLog.getNo200s();
		Assert.assertEquals(1, no200s.size());
	}

	@Test
	public void testAnalyzeCaptures() throws IOException {
		QuickscrapeLog quickscrapeLog = QuickscrapeLog.readLog(new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, QUICKSCRAPE_2016_08_26_09_44_LOG));
		List<QSRecord> records = quickscrapeLog.getQSURLRecords();
		for (QSRecord record : records) {
			LOG.trace("R "+record);
		}
	}

	@Test
	public void testNoCaptures() throws IOException {
		QuickscrapeLog quickscrapeLog = QuickscrapeLog.readLog(new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, QUICKSCRAPE_2016_08_25_22_54_LOG));
		List<QSRecord> records = quickscrapeLog.getNoCaptureRecords();
		Assert.assertEquals(191, records.size());
	}

	@Test
	public void testNo200() throws IOException {
		QuickscrapeLog quickscrapeLog = QuickscrapeLog.readLog(new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, QUICKSCRAPE_2016_08_26_09_44_LOG));
		List<QSRecord> records = quickscrapeLog.getNo200s();
		Assert.assertEquals(66, records.size());
		Multimap<String, String> doisByPrefix = quickscrapeLog.getUrlsByPrefix(records);
		LOG.trace(doisByPrefix);
	}

	@Test
	public void testTotal() throws IOException {
		QuickscrapeLog quickscrapeLog = QuickscrapeLog.readLog(new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, TOTAL_LOG));
		List<QSRecord> allRecords = quickscrapeLog.getQSURLRecords();
		Assert.assertEquals(6524, allRecords.size());
		List<QSRecord> records = quickscrapeLog.getNo200s();
		Assert.assertEquals(675, records.size());
		Multimap<String, String> doisByPrefix = quickscrapeLog.getUrlsByPrefix(records);
		List<List<String>> sortedKeys = CMineUtil.getListsSortedByCount(doisByPrefix);
		for (List<String> key : sortedKeys) {
			LOG.trace(key.size()+": "+key);
		}
	}

}
