package org.contentmine.norma.ncbi;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.norma.NormaArgProcessor;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

public class NCBIHtmlTest {

	private static final Logger LOG = LogManager.getLogger(NCBIHtmlTest.class);
@Test
	public void testNCBIProject() throws IOException {
		File ursus = new File(NormaFixtures.TEST_NORMA_DIR, "shtml/ursus");
		File shtml = new File("target/ursus1/");
		FileUtils.copyDirectory(ursus, shtml);
		DefaultArgProcessor normaArgProcessor = new NormaArgProcessor();
//		String args = "--project "+shtml.toString()+ " -i fulltext.xml  --transform ncbi-jats2html -o scholarly.html";
		String args = "--project "+shtml.toString()+ " -i fulltext.xml  --transform jats2shtml -o scholarly.html";
		LOG.trace(args);
		normaArgProcessor.parseArgs(args);
		normaArgProcessor.runAndOutput();
	}
	
}
