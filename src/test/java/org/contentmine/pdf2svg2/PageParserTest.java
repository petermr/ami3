package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.pdf2svg2.PageParserRunner.ParserDebug;
import org.junit.Assert;
import org.junit.Test;

public class PageParserTest extends AbstractAMITest {
	public static final Logger LOG = LogManager.getLogger(PageParserTest.class);
@Test
	public void testCustom() throws IOException {
      String root = "custom-render-demo"; // pageSserial=0
	  File file = new File(PDF2SVG2, root + ".pdf");
      Assert.assertTrue(file.exists());
      
//      DrawerType drawerType = DrawerType.ORIGINAL;
      ParserDebug drawerType = ParserDebug.AMI_MEDIUM;
        int pageSerial = 0;
      runPageParser(root, file, pageSerial, drawerType, true);
	}

	@Test
	/** simple coloured text */
	public void testTextColours() throws IOException {
	      String root = "textColours"; // 
//	      root = "AB"; // 
      int pageSerial = 0; // title
//    pageSerial = 1; // plots
      pageSerial = -1; // analyze all
	  runPageParser(root, pageSerial, ParserDebug.AMI_FULL);
	}

	@Test
	/** successive primitives */
	public void testStrokeAndFillColours() throws IOException {
	  String root = "circles"; // 
      int pageSerial = 0; // title
	  runPageParser(root, pageSerial, ParserDebug.AMI_FULL);
//	  if (true) return;
	  root = "primitives"; // 
      pageSerial = 0; // title
	  runPageParser(root, pageSerial, ParserDebug.AMI_FULL);
	}

	@Test
	public void testLichtenburg() throws IOException {
      String root = "lichtenburg19a"; // pageSerial=1 or 5
      int pageSerial = 0; // title
//    pageSerial = 1; // plots
      pageSerial = -1; // analyze all
	  runPageParser(root, pageSerial, ParserDebug.AMI_BRIEF);
	}

	private void runPageParser(String root, int pageSerial, ParserDebug parserDebug) throws IOException {
		File file = new File(PDF2SVG2, root + ".pdf");
		  Assert.assertTrue("file should exist: "+file, file.exists());
		  runPageParser(root, file, pageSerial, parserDebug, false);
	}

	/**
	 * 
	 * @param root
	 * @param inputPdf
	 * @param pageSerial page to draw (0-based); -1 runs all
	 * @param drawerType
	 * @param debug
	 * @throws IOException
	 */
	private void runPageParser(String root, File inputPdf, int pageIndex, ParserDebug parserDebug, boolean debug) throws IOException {
		PageParserRunner pageParserRunner = new PageParserRunner(inputPdf, parserDebug, debug);
		boolean tidySVG = true;
		pageParserRunner.setTidySVG(true);
		pageParserRunner.runPages(root, pageIndex);
	}
}
