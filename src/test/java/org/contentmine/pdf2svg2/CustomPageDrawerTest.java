package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.pdf2svg2.PageDrawerRunner.DrawerType;
import org.junit.Assert;
import org.junit.Test;

public class CustomPageDrawerTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(CustomPageDrawerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCustom() throws IOException {
      String root = "custom-render-demo"; // pageSserial=0
	  File file = new File(PDF2SVG2, root + ".pdf");
      Assert.assertTrue(file.exists());
      
//      DrawerType drawerType = DrawerType.ORIGINAL;
      DrawerType drawerType = DrawerType.AMI_MEDIUM;
        int pageSerial = 0;
      runPageDrawer(root, file, pageSerial, drawerType, true);
	}

	@Test
	public void testLichtenburg() throws IOException {
//      String root = "custom-render-demo"; // pageSserial=0
      String root = "lichtenburg19a"; // pageSerial=1 or 5
	  File file = new File(PDF2SVG2, root + ".pdf");
      Assert.assertTrue(file.exists());
      
//      DrawerType drawerType = DrawerType.ORIGINAL;
//      DrawerType drawerType = DrawerType.AMI_BRIEF;
      
      DrawerType drawerType = DrawerType.AMI_MEDIUM;
        int pageSerial = 0; // title
//      pageSerial = 1; // plots
//      pageSerial = -1; // analyze all
      runPageDrawer(root, file, pageSerial, drawerType, false);
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
	private void runPageDrawer(String root, File inputPdf, int pageSerial, DrawerType drawerType, boolean debug) throws IOException {
		PageDrawerRunner pageDrawerRunner = new PageDrawerRunner(inputPdf, drawerType, debug);
		boolean tidySVG = true;
//		tidySVG = false;
		pageDrawerRunner.setTidySVG(tidySVG);
		if (pageSerial < 0) {
			while(true) {
				try {
					runPageDrawer(root, ++pageSerial, pageDrawerRunner);
				} catch (IllegalArgumentException e) {
					System.out.println("quit");
					break;
				}
			}
		} else {
			runPageDrawer(root, pageSerial, pageDrawerRunner);
		}
//		doc.close();
	}

	private void runPageDrawer(String root, int pageSerial, PageDrawerRunner pageDrawerRunner) throws IOException , IllegalArgumentException{
		pageDrawerRunner.processPage(pageSerial);
		
		File outputPng = new File(PDF2SVG2, root+"."+pageSerial+".png");
		ImageIO.write(pageDrawerRunner.getImage(), "PNG", outputPng);
		LOG.debug("wrote PNG "+outputPng);
		SVGElement svgElement = pageDrawerRunner.getSVG();
		SVGSVG.wrapAndWriteAsSVG(svgElement, new File(PDF2SVG2, root+"."+pageSerial+".svg"));
	}
}
