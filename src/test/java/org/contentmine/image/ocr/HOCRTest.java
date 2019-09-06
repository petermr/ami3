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


public class HOCRTest {
	public static final Logger LOG = Logger.getLogger(HOCRTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testFormula() throws IOException {
		String fileRoot = "adrenaline0";
		File targetDir = new File(SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, fileRoot);
		File chemFile = new File(SVGHTMLFixtures.EARLY_CHEM_DIR, fileRoot + ".png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		// get the blocks
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.scanThresholdsAndWriteBinarizedFiles(targetDir, new int[]{100,105,110,115,120,125}, chemFile);
		int threshold = 120;
		diagramAnalyzer.setThreshold(threshold);
		diagramAnalyzer.readAndProcessInputFile(chemFile);
		File binarizedFile = new File(targetDir, "binarized"+threshold+".png");
		diagramAnalyzer.writeBinarizedFile(binarizedFile);
		File htmlOutfile = new File(targetDir, "hocrFile.html");
		OCRProcessor ocrProcessor = new OCRProcessor();
		HOCRReader hocrReader = ocrProcessor.createHOCRReaderAndProcess(binarizedFile, htmlOutfile);
	}

	/** analyses the sepia image of chemical formulae.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFormulaRaw() throws IOException {
		String fileRoot = "adrenaline0";
		File targetDir = new File(SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, fileRoot);
		File chemFile = new File(SVGHTMLFixtures.EARLY_CHEM_DIR, fileRoot + ".png");
		File htmlOutfile = new File(targetDir, "hocrFile.html");
		OCRProcessor ocrProcessor = new OCRProcessor();
		HOCRReader hocrReader = ocrProcessor.createHOCRReaderAndProcess(chemFile, htmlOutfile);
		
	}

	/** apply simple sharpening function to image.
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testHOCRSharpen() {
		BufferedImage newImage = null;
		String fileRoot = "adrenaline0";
		IntArray array = new IntArray(new int[]{1, 1, 1});
//		array = ImageUtil.SHARPEN_ARRAY;
//		array = ImageUtil.IDENT_ARRAY;
//		array = ImageUtil.DOUBLE_ARRAY;
//		array = ImageUtil.SMEAR_ARRAY;
		array = ImageUtil.EDGE_ARRAY;
		File targetDir = new File(SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, fileRoot);
		File chemFile = new File(SVGHTMLFixtures.EARLY_CHEM_DIR, fileRoot + ".png");
		BufferedImage image = ImageUtil.readImage(chemFile);
		if (image != null) {
			RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
			RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilter(array);
//			rgbMatrix1 = rgbMatrix;
			newImage = rgbMatrix1.createImage(image.getType());
		}
		ImageIOUtil.writeImageQuietly(newImage, new File(targetDir, "sharpen.png"));
	}

	@Test
	/** gets some junk but also the text.
	 * 
	 * @throws IOException
	 */
	public void testCharactersSVG() throws IOException {
		String fileRoot = "adrenaline0";
		File targetDir = new File(SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, fileRoot);
		File chemFile = new File(SVGHTMLFixtures.EARLY_CHEM_DIR, fileRoot + ".png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		// get the blocks
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.scanThresholdsAndWriteBinarizedFiles(targetDir, new int[]{100,105,110,115,120,125}, chemFile);
		int threshold = 120;
		diagramAnalyzer.setThreshold(threshold);
		diagramAnalyzer.readAndProcessInputFile(chemFile);
		File binarizedFile = new File(targetDir, "binarized"+threshold+".png");
		diagramAnalyzer.writeBinarizedFile(binarizedFile);
		File htmlOutfile = new File(targetDir, "hocrFile.html");
		OCRProcessor ocrProcessor = new OCRProcessor();
		HOCRReader hocrReader = ocrProcessor.createHOCRReaderAndProcess(binarizedFile, htmlOutfile);
		if (hocrReader == null) {
			LOG.error("Tesseract not installed");
			return;
		}
		SVGSVG svg = (SVGSVG) hocrReader.getOrCreateSVG();
		SVGSVG.wrapAndWriteAsSVG(svg, new File(targetDir, "hocr.svg"));
	}


	@Test
	/** gets some junk but also the text.
	 * 
	 * @throws IOException
	 */
	public void testBBoxes() throws IOException {
		String fileRoot = "adrenaline0";
		File targetDir = new File(SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, fileRoot);
		File chemFile = new File(SVGHTMLFixtures.EARLY_CHEM_DIR, fileRoot + ".png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		// get the blocks
		diagramAnalyzer.setThinning(null);
//		diagramAnalyzer.scanThresholdsAndWriteBinarizedFiles(targetDir, new int[]{100,105,110,115,120,125}, chemFile);
		int threshold = 120;
		diagramAnalyzer.setThreshold(threshold);
		diagramAnalyzer.readAndProcessInputFile(chemFile);
		File binarizedFile = new File(targetDir, "binarized"+threshold+".png");
		diagramAnalyzer.writeBinarizedFile(binarizedFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		SVGG g = new SVGG();
		g.appendChild(pixelIslandList.getOrCreateSVGG().copy());
		Real2RangeList bboxList = pixelIslandList.getOrCreateBoundingBoxList();
		bboxList.setStrokeList(CHESConstants.DEFAULT_COLOR_LIST);
		bboxList.setAddNumbers(true);
		LOG.debug("height "+bboxList.getCommonestIntegerHeight());
		g.appendChild(bboxList.createSVG());
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "islands.svg"));
	}

	@Test
	/** gets some junk but also the text.
	 * 
	 * @throws IOException
	 */
	public void testCharactersY() throws IOException {
		String fileRoot = "yvalues";
		File targetDir = new File(SVGHTMLFixtures.EARLY_PLOT_TARGET_DIR, fileRoot);
		File valueFile = new File(SVGHTMLFixtures.EARLY_PLOT_DIR, fileRoot + ".png");
		File htmlOutfile = new File(targetDir, "hocrFile.html");
		OCRProcessor ocrProcessor = new OCRProcessor();
		HOCRReader hocrReader = ocrProcessor.createHOCRReaderAndProcess(valueFile, htmlOutfile);
		if (hocrReader == null) {
			LOG.error("Tesseract not installed");
			return;
		}
		SVGSVG svg = (SVGSVG) hocrReader.getOrCreateSVG();
		File yvalues = new File(targetDir, "yvalues.svg");
		SVGSVG.wrapAndWriteAsSVG(svg, yvalues);
		Assert.assertTrue("yvalues exists", yvalues.exists());
	}


}
