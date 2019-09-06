package org.contentmine.graphics.svg.plot.forest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ColorStore;
import org.contentmine.graphics.svg.util.ColorStore.ColorizerType;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class ForestTest {
	public static final Logger LOG = Logger.getLogger(ForestTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static File inputDir = SVGHTMLFixtures.FOREST_DIR;
	private static File targetDir = new File("target/forest/");
	private static String[] FILL = new String[] { "orange", "green", "blue", "red", "cyan" };
	private File outputFile;


	@Test
	public void testBlueRhombsGreenSquaresSVG() {
		String fileRoot = "blueRhombsGreenSquares";
		ForestPlot forestPlot = new ForestPlot();
		forestPlot.setFileRoot(fileRoot);
//		forestPlot.setOutputDir(new File(targetDir, "forest/"));
		forestPlot.readCacheAndAnalyze(inputDir, fileRoot);
		Assert.assertTrue("exists: "+forestPlot.getPolyListFile(), forestPlot.getPolyListFile().exists());
		Assert.assertEquals("rects", 12, forestPlot.getOrCreateRectList().size());
		Assert.assertEquals("horizontal", 15, forestPlot.getOrCreateHorizontalLineList().size());
		// some of these are glyphs
		Assert.assertEquals("vertical", 11, forestPlot.getOrCreateVerticalLineList().size());
		Assert.assertEquals("rhombs", 7, forestPlot.getOrCreateRhombList().size());
	}

	@Test
	public void testHollowRhombGraySquaresSVG() {
		String fileRoot = "hollowRhombsGraySquares";
		ForestPlot forestPlot = new ForestPlot();
		forestPlot.readCacheAndAnalyze(inputDir, fileRoot);
		Assert.assertEquals("rects", 24, forestPlot.getOrCreateRectList().size());
		Assert.assertEquals("horizontal", 14, forestPlot.getOrCreateHorizontalLineList().size());
		// ??? FIXME maybe 1?
		Assert.assertEquals("rhombs", 0, forestPlot.getOrCreateRhombList().size());
		// some are glyphs
		Assert.assertEquals("vertical", 5, forestPlot.getOrCreateVerticalLineList().size());

	}
	
	@Test
	public void testForestPlotBitmap() {
		File forestFile = new File(ImageAnalysisFixtures.FOREST_DIR, "forest_plot.png");
		Assert.assertTrue("forestFile "+forestFile, forestFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(forestFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals("islands",  187, pixelIslandList.size());
		SVGG g = new SVGG();
		// pixels
		// test cut to first 3 islands
		Iterator<String> iterator = ColorStore.getColorIterator(ColorizerType.CONTRAST);
		int[] sizes = new int[] {2284,181,144};
		int[] nodeCounts = new int[] {24,2,4};
		int[] edgeCounts = new int[] {23,2,3};
		Boolean[] cyclic = new Boolean[] {false,false,false/*,true,true,true,true,true,true,true,true,true,true,true*/};
		for (int isl = 0; isl < /*pixelIslandList.size()*/cyclic.length; isl++) {
			PixelIsland island = pixelIslandList.get(isl);
			PixelGraph graph = new PixelGraph(island);
			Assert.assertEquals("island", sizes[isl], island.size());
			Assert.assertEquals("nodes", nodeCounts[isl], graph.getOrCreateNodeList().size());
			Assert.assertEquals("edges", edgeCounts[isl], graph.getOrCreateEdgeList().size());
			graph.doEdgeSegmentation();
			Assert.assertEquals("cyclic "+isl, cyclic[isl], graph.isSingleCycle());
			SVGG gg = graph.normalizeSVGElements();
			g.appendChild(gg);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/forest", "segments1.svg"));
		
	}
	
	@Test
	@Ignore // plotting is expensive
	/**
	 * horizontal bars with black squares and hollow diamond. Black squares are extracted well at 
	 * ring level 2
	 */
	public void testBlackSquares() {

		for (String fileRoot : new String[] {
				"blue",
				"blurred",
				"forest_plot",
				"gr1",
//				"gr2",
				"PMC2788519",
				"srep36553-f2",
				"srep44789-f5",
				"tableBlue",
				}) {
		
			File forestFile = new File(ImageAnalysisFixtures.FOREST_DIR, fileRoot + ".png");
			// tableBlue.png // blue squares
			// PMC2788519.png // fuzzy
			//
			
			DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
			diagramAnalyzer.setThinning(null);
			diagramAnalyzer.readAndProcessInputFile(forestFile);
			PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
			pixelIslandList.sortBySizeDescending();
	
			for (int isl = 0; isl < 3; isl++) {
				PixelIsland islandD = pixelIslandList.get(isl);
		
				SVGG g = new SVGG();
				PixelRingList pixelRingList = islandD.getOrCreateInternalPixelRings();
				LOG.debug("pixel rings "+pixelRingList.size());
				for (int i = 0; i < pixelRingList.size(); i++) {
					pixelRingList.get(i).plotPixels(g, FILL[i % FILL.length]);
				}
				SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + fileRoot + "/plotLine"+isl+".svg"));
		
				plotPoints(fileRoot, isl, pixelRingList, 3);
				plotPoints(fileRoot, isl, pixelRingList, 2);
				plotPoints(fileRoot, isl, pixelRingList, 1);
				
		
				/** all 5 black squares */
				int ring = 0;
				int ring0 = ring - 1;
				g = new SVGG();
				for (PixelIsland island : pixelIslandList) {
					if (island != null) {
						pixelRingList = island.getOrCreateInternalPixelRings();
						if (pixelRingList != null && pixelRingList.size() > ring) {
							PixelRing outline = pixelRingList.get(ring).getPixelsTouching(pixelRingList.get(ring0));
							if (outline != null) {
								outline.plotPixels(g, "blue");
							}
						}
					}
				}
				SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + fileRoot + "/plotLine"+isl+"AllPoints"+ring0+""+ring+".svg"));
			}
		}
	}

	@Test
	@Ignore
	/**
	 * horizontal bars with black squares and hollow diamond. Black squares are extracted well at 
	 * ring level 2
	 * 
	 * The plotting here is very expensive
	 */
	public void testLightColours() {

		for (String fileRoot : new String[] {
//				"blue",
//				"blurred",
//				"forest_plot",
				"gr1a",
				"gr2a",
//				"PMC2788519",
//				"srep36553-f2",
//				"srep44789-f5",
//				"tableBlue",
				}) {
		
			File forestFile = new File(ImageAnalysisFixtures.FOREST_DIR, fileRoot + ".png");
			// tableBlue.png // blue squares
			// PMC2788519.png // fuzzy
			//
			DiagramAnalyzer diagramAnalyzer = null;
			for (int threshold : new int[]{20, 50, 80, 110, 140, 180, 210, 240}) {
				diagramAnalyzer = new DiagramAnalyzer();
				diagramAnalyzer.setThinning(null);
				diagramAnalyzer.getImageProcessor().setThreshold(threshold);
				diagramAnalyzer.readAndProcessInputFile(forestFile);
				diagramAnalyzer.writeBinarizedFile(new File("target/" + fileRoot +"/binarized+"+threshold+".png"));
			}
			LOG.debug("finished thresholds");
			PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
			pixelIslandList.sortBySizeDescending();
			
	
			for (int isl = 0; isl < 20; isl++) {
				PixelIsland islandD = pixelIslandList.get(isl);
		
				SVGG g = new SVGG();
				PixelRingList pixelRingList = islandD.getOrCreateInternalPixelRings();
				LOG.debug("pixel rings "+pixelRingList.size());
				for (int i = 0; i < pixelRingList.size(); i++) {
					pixelRingList.get(i).plotPixels(g, FILL[i % FILL.length]);
					LOG.debug("ring "+i);
				}
				File file = new File("target/" + fileRoot + "/plotLine"+isl+".svg");
				LOG.debug("plotting "+file);
//				SVGSVG.wrapAndWriteAsSVG(g, file);
				LOG.debug("plotted "+file);
		
				plotPoints(fileRoot, isl, pixelRingList, 3);
				plotPoints(fileRoot, isl, pixelRingList, 2);
				plotPoints(fileRoot, isl, pixelRingList, 1);
				LOG.debug("calculated pixelRings");
				
		
				/** all 5 black squares */
				int ring = 0;
				int ring0 = ring - 1;
				g = new SVGG();
				for (PixelIsland island : pixelIslandList) {
					if (island != null) {
						pixelRingList = island.getOrCreateInternalPixelRings();
						if (pixelRingList != null && pixelRingList.size() > ring) {
							PixelRing outline = pixelRingList.get(ring).getPixelsTouching(pixelRingList.get(ring0));
							if (outline != null) {
								outline.plotPixels(g, "blue");
							}
						}
					}
				}
				file = new File("target/" + fileRoot + "/plotLine"+isl+"AllPoints"+ring0+""+ring+".svg");
//				SVGSVG.wrapAndWriteAsSVG(g, file);
				LOG.debug("wrote svg "+file);
			}
		}
	}


	//============
	
	private void plotPoints(String fileRoot, int isl, PixelRingList pixelRingList, int maxSize) {
		int maxSize0 = maxSize - 1;
		SVGG g = new SVGG();
		if (pixelRingList.size() > maxSize) {
			PixelRing outline = pixelRingList.get(maxSize).getPixelsTouching(pixelRingList.get(maxSize0));
			outline.plotPixels(g, "black");
			// this is the outline of the symbol
			File file = new File("target/" + fileRoot + "/plotLine"+isl+"Points"+maxSize0+""+maxSize+".svg");
//			SVGSVG.wrapAndWriteAsSVG(g, file);
			LOG.debug("plotted points "+file+"/"+FileUtils.sizeOf(file));
		}
	}

}
