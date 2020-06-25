package org.contentmine.norma.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.pdf.PDFSplitter;
import org.junit.Ignore;
import org.junit.Test;

public class SplitPDFTest {
	
	private static final Logger LOG = LogManager.getLogger(SplitPDFTest.class);
@Test
	@Ignore // complex
	public void testSplitter() throws IOException {
		File file = new File("../../projects/unesco/235406e/fulltext.pdf");
		PDFSplitter splitter = new PDFSplitter();
		splitter.loadFile(file);
		splitter.splitDocs(200);
//		splitter.splitDoc(100,  200);
//		splitter.saveFile(new File("../../projects/unesco/235406e/fulltext_100_200.pdf"));
	}


}