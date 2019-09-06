package org.xmlcml.files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.Fixtures;
import org.xmlcml.args.DefaultArgProcessor;

public class QuickscrapeNormaTest {

	
	private static final Logger LOG = Logger.getLogger(QuickscrapeNormaTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static File QUICKSCRAPE_NORMA_DIR = new File("src/test/resources/org/xmlcml/files/");
	public final static File PLOS0115884_DIR = new File(QUICKSCRAPE_NORMA_DIR, "journal.pone.0115884");
	
	@Test
	public void testReadQuickscrapeNorma() {
		QuickscrapeNorma quickscrapeNorma = new QuickscrapeNorma();
		quickscrapeNorma.readDirectory(PLOS0115884_DIR);
		Assert.assertEquals("fileCount", 4, quickscrapeNorma.getReservedFileList().size());
		Assert.assertTrue("XML", quickscrapeNorma.hasFulltextXML());
	}
	
	@Test
	public void testQuickscrapeNorma() throws IOException {
		File container0115884 = new File("target/plosone/0115884/");
		// copy so we don't write back into test area
		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_0115884_DIR, container0115884);
		String[] args = {
			"-q", container0115884.toString(),
		};
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		QuickscrapeNormaList quickscrapeNormaList = argProcessor.getQuickscrapeNormaList();
		Assert.assertEquals(1,  quickscrapeNormaList.size());
		LOG.trace(quickscrapeNormaList.get(0).toString());
	}
}
