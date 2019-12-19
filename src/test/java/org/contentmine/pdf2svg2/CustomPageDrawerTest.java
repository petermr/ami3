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
      File file = new File(PDF2SVG2, "lichtenburg19a.pdf");
      Assert.assertTrue(file.exists());
      
      PDDocument doc = PDDocument.load(file);
      PageDrawerRunner pageDrawerRunner = new PageDrawerRunner();
      PDFRenderer renderer = pageDrawerRunner.createPDFRenderer(doc, DrawerType.ORIGINAL);
//      PDFRenderer renderer = new MyPDFRenderer(doc);
      BufferedImage image = renderer.renderImage(1);
      ImageIO.write(image, "PNG", new File(PDF2SVG2, "lichtenburg19a.png"));
      doc.close();
	}

}
