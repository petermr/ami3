package org.contentmine.image.ocr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real2RangeList;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.colour.RGBImageMatrix;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelIslandList;
import org.junit.Test;

import junit.framework.Assert;


public class HOCRTestIT {
	public static final Logger LOG = Logger.getLogger(HOCRTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testText() throws IOException {
		String fileRoot = "introText";
		File targetDir = new File(SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, fileRoot);
		File chemFile = new File(SVGHTMLFixtures.EARLY_CHEM_DIR, fileRoot + ".png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		// get the blocks
		diagramAnalyzer.setThinning(null);
		int[] thresholds = new int[]{100,105,110,115,120,125};
		diagramAnalyzer.scanThresholdsAndWriteBinarizedFiles(targetDir, thresholds, chemFile);
		int threshold = 115;
		File htmlOutfile = new File(targetDir, "hocrFile.html");
		OCRProcessor ocrProcessor = new OCRProcessor();
		HOCRReader hocrReader = ocrProcessor.createHOCRReaderAndProcess(new File(targetDir, "binarized"+threshold+".png"), htmlOutfile);
	}



}
