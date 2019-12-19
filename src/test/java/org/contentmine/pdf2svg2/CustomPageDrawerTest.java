package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.pdf2svg2.PageDrawerRunner.DrawerType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CustomPageDrawerTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(CustomPageDrawerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testPDF2SVG() throws IOException {
//      String root = "custom-render-demo"; // pageSserial=0
      String root = "lichtenburg19a"; // pageSerial=1 or 5
	  File file = new File(PDF2SVG2, root + ".pdf");
      Assert.assertTrue(file.exists());
      
//      DrawerType drawerType = DrawerType.ORIGINAL;
      DrawerType drawerType = DrawerType.AMI;
 //     int pageSerial = 0;
      int pageSerial = 1;
      runPageDrawer(root, file, pageSerial, drawerType);
	}

	private void runPageDrawer(String root, File inputPdf, int pageSerial, DrawerType drawerType) throws IOException {
		PDDocument doc = PDDocument.load(inputPdf);
		PageDrawerRunner pageDrawerRunner = new PageDrawerRunner();
		PDFRenderer renderer = pageDrawerRunner.createPDFRenderer(doc, drawerType);
		BufferedImage image = renderer.renderImage(pageSerial);
		File outputPng = new File(PDF2SVG2, root+".png");
		ImageIO.write(image, "PNG", outputPng);
		LOG.debug("wrote PNG "+outputPng);
		doc.close();
	}
}
