package org.contentmine.pdf2svg2;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.junit.Test;

import junit.framework.Assert;

public class BiorxivTest {
	private static final Logger LOG = Logger.getLogger(BiorxivTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testBiorxivMarchantia1() throws Exception {
		File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
		File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String fileroot = "103861";
		File file = new File(targetDir, fileroot+".full.pdf");
		LOG.debug(file);
		Assert.assertTrue("target pdf "+targetDir, file.exists());
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    documentProcessor.readAndProcess(file);
	    documentProcessor.writeSVGPages(new File(targetDir, fileroot));
    	documentProcessor.writePDFImages(targetDir);
	}
	
	

}
