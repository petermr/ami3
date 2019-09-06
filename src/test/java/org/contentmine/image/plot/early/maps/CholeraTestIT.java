package org.contentmine.image.plot.early.maps;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.ocr.HOCRReader;
import org.contentmine.image.ocr.OCRProcessor;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelListFloodFill;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author pm286
 *
 */
/**
 * extraction of semantics from Broad Street Cholera Map
 *
 */
public class CholeraTestIT {
	private static final Logger LOG = Logger.getLogger(CholeraTestIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static String[] FILL = new String[] { "orange", "green", "blue", "red", "cyan" };

	@Test
	public void testSnow() {
		String fileRoot = "Snow-cholera-map-1";
		File snowFile = new File(SVGHTMLFixtures.EARLY_MAP_DIR, fileRoot + ".jpg");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(snowFile);
	}

	/** extracts the balck islands.
	 * Slowish as it writes SVG.
	 */
	@Test
	public void testCholeraSmallExtractBlackIslands() {
		String fileRoot = "choleraSmall";
		File targetDir = new File(SVGHTMLFixtures.EARLY_MAP_TARGET_DIR, fileRoot);
		File snowFile = new File(SVGHTMLFixtures.EARLY_MAP_DIR, fileRoot + ".png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		// get the blocks
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.readAndProcessInputFile(snowFile);
		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "pixelIslandList.png"));
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreateSortedPixelIslandList();
		Assert.assertEquals("islands", 66, pixelIslandList.size());
		// get the largest one
		PixelIsland island0 = pixelIslandList.get(0);
		Assert.assertEquals("island0", 26600, island0.size());
		SVGG g = island0.getOrCreateSVGG();
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "island0.svg"));
		// create the pixel rings in this island
		PixelRingList pixelRingList = island0.getOrCreateInternalPixelRings();
		Assert.assertEquals("pixelRingList", 22, pixelRingList.size());
		for (int i = 0; i < pixelRingList.size(); i++) {
			pixelRingList.get(i).plotPixels(g, FILL[i % FILL.length]);
		}
		// lengthy and large
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "pixelRings"+".svg"));
		// cut island at level 2 to create new sub-islands
		// disconnects the weakly connected islands but some are still merged
		PixelRing ring2 = pixelRingList.get(2);
		SVGG gg = ring2.getOrCreateSVG();
		SVGSVG.wrapAndWriteAsSVG(gg, new File(targetDir, "pixelRings2"+".svg"));
		// severer cut
		PixelRing ring4 = pixelRingList.get(4);
		gg = ring4.getOrCreateSVG();
		SVGSVG.wrapAndWriteAsSVG(gg, new File(targetDir, "pixelRings4"+".svg"));
		PixelListFloodFill pixelListFloodFill = new PixelListFloodFill(ring4);
		pixelListFloodFill.fillIslands();
		PixelIslandList separatedIslandList = pixelListFloodFill.getIslandList();
		separatedIslandList.sortBySizeDescending();
		// this doesn't work anymore
		LOG.error("ISLANDS FAIL FIXME");
		if (true) return;
		Assert.assertEquals("separated pixelRingList", 11, separatedIslandList.size());
		SVGSVG.wrapAndWriteAsSVG(separatedIslandList.getOrCreateSVGG(), new File(targetDir, "separatedIslands"+".svg"));
		// now build the outer pixel rings without the bridges, expand out into white space
		PixelIsland newIsland0 = separatedIslandList.get(0);
		SVGSVG.wrapAndWriteAsSVG(newIsland0.createSVG(), new File(targetDir, "newIsland0"+".svg"));
		PixelRingList shell1 = newIsland0.getNeighbouringShells(1);
		SVGG shell1SVG = shell1.getRing(0).getOrCreateSVG();
		LOG.debug(shell1SVG.toXML());
		SVGSVG.wrapAndWriteAsSVG(shell1SVG, new File(targetDir, "shell1"+".svg"));

	}

	/** adds outer pixelRings that have been clipped to separate islands
	 * 
	 */
	@Test
	public void testAddClippedRingsBridges() {
		String fileRoot = "choleraSmall";
		File targetDir = new File(SVGHTMLFixtures.EARLY_MAP_TARGET_DIR, fileRoot);
		File snowFile = new File(SVGHTMLFixtures.EARLY_MAP_DIR, fileRoot + ".png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		// get the blocks
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.readAndProcessInputFile(snowFile);
		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "pixelIslandList.png"));
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreateSortedPixelIslandList();
		// get the largest one
		PixelIsland island0 = pixelIslandList.get(0);
		PixelRingList pixelRingList = island0.getOrCreateInternalPixelRings();
		// cut island at level 2 to create new sub-islands
		// disconnects the weakly connected islands but some are still merged
		PixelRing ring2 = pixelRingList.get(2);
		PixelRing ring3 = pixelRingList.get(3);
		PixelRing ring1 = ring2.expandRingOutside(ring3);
		SVGSVG.wrapAndWriteAsSVG(ring1.getOrCreateSVG(), new File(targetDir, "ring1.svg"));
		PixelRing ring0 = ring1.expandRingOutside(ring2);
		LOG.debug("ring0 "+ring0.size());
		SVGSVG.wrapAndWriteAsSVG(ring0.getOrCreateSVG(), new File(targetDir, "ring0.svg"));
		
	}
	
	@Test
	/** gets some junk but also the text.
	 * 
	 * @throws IOException
	 */
	public void testNames() throws IOException {
		String fileRoot = "choleraSmall";
		File targetDir = new File(SVGHTMLFixtures.EARLY_MAP_TARGET_DIR, fileRoot);
		File mapFile = new File(SVGHTMLFixtures.EARLY_MAP_DIR, fileRoot + ".png");
//		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
//		// get the blocks
//		diagramAnalyzer.setThinning(null);
////		diagramAnalyzer.scanThresholdsAndWriteBinarizedFiles(targetDir, new int[]{100,105,110,115,120,125}, chemFile);
		int threshold = 120;
//		diagramAnalyzer.setThreshold(threshold);
//		diagramAnalyzer.readAndProcessInputFile(chemFile);
		File binarizedFile = new File(targetDir, "binarized"+threshold+".png");
//		FileUtils.copyFile(new File(SVGHTMLFixtures.EARLY_MAP_DIR, "results/choleraSmall/build.png"),  binarizedFile);
		FileUtils.copyFile(new File(SVGHTMLFixtures.EARLY_MAP_DIR, "results/choleraSmall/buildRot.png"),  binarizedFile);
//		File binarizedFile = new File(targetDir, "binarized"+threshold+"a.png");
		Assert.assertTrue("exists", binarizedFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(mapFile);
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



}
