package org.contentmine.image.plot.early.phylo;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelNodeList;
import org.contentmine.image.processing.ZhangSuenThinning;
import org.junit.Test;

import junit.framework.Assert;

public class DarwinTest {
	private static final Logger LOG = Logger.getLogger(DarwinTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** original plot with sepia tones and bleed through.
	 * 
	 */
	@Test
	public void testDarwin0() {
		String fileRoot = "darwin0";
		File darwinFile = new File(SVGHTMLFixtures.EARLY_PHYLO_DIR, fileRoot + ".jpg");
		File targetFile = new File(SVGHTMLFixtures.EARLY_PHYLO_TARGET_DIR, fileRoot);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(darwinFile);
		diagramAnalyzer.writeBinarizedFile(new File(targetFile, fileRoot+".binarized.png"));
	}

	/** original plot with sepia tones and bleed through.
	 * 
	 */
	@Test
	public void testDarwin0big() {
		String fileRoot = "darwin0big";
		File darwinFile = new File(SVGHTMLFixtures.EARLY_PHYLO_DIR, fileRoot + ".png");
		File targetFile = new File(SVGHTMLFixtures.EARLY_PHYLO_TARGET_DIR, fileRoot);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.readAndProcessInputFile(darwinFile);
		diagramAnalyzer.writeBinarizedFile(new File(targetFile, fileRoot+".binarized.png"));
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		pixelIslandList.sortBySizeDescending();
		Assert.assertEquals("darwin size", 171, pixelIslandList.size());
		PixelIsland pixelIsland0 = pixelIslandList.get(0);
		Assert.assertEquals("darwin main tree size", 95803, pixelIsland0.size());
		BufferedImage image = pixelIsland0.createImage();
		ImageIOUtil.writeImageQuietly(image, new File(targetFile, "island0.png"));

	}

	@Test
	public void testDarwinThin() {
		String fileRoot = "darwin";
		File darwinFile = new File(SVGHTMLFixtures.EARLY_PHYLO_DIR, fileRoot + ".jpg");
		File targetFile = new File(SVGHTMLFixtures.EARLY_PHYLO_TARGET_DIR, fileRoot);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(darwinFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreateSortedPixelIslandList();
		Assert.assertEquals("darwin size", 16, pixelIslandList.size());
		SVGSVG.wrapAndWriteAsSVG(pixelIslandList.getOrCreateSVGG(), new File(targetFile, "pixelIslandList.svg"));
		
		PixelIsland pixelIsland0 = pixelIslandList.get(0);
		Assert.assertEquals("darwin main tree size", 2352, pixelIsland0.size());
		SVGSVG.wrapAndWriteAsSVG(pixelIsland0.getOrCreateSVGG(), new File(targetFile, "island0.svg"));
		PixelGraph pixelGraph = new PixelGraph(pixelIsland0);
		pixelGraph.mergeNodesCloserThan(12.);

		PixelNodeList nodeList = pixelGraph.getOrCreateNodeList();
		Assert.assertEquals("nodes", 44,  nodeList.size());
		PixelEdgeList edgeList = pixelGraph.getOrCreateEdgeList();
		Assert.assertEquals("edges",  44, edgeList.size());
		SVGG g = new SVGG();
		pixelGraph.createAndDrawGraph(g);
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetFile, "island0.graph.svg"));
	}

/** this uses a somewhat larger start and has messier nodes.
 * 
 */
	@Test
	public void testDarwin1Thin() {
		String fileRoot = "darwin1";
		File darwinFile = new File(SVGHTMLFixtures.EARLY_PHYLO_DIR, fileRoot + ".png");
		File targetFile = new File(SVGHTMLFixtures.EARLY_PHYLO_TARGET_DIR, fileRoot);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setThinning(new ZhangSuenThinning());
		diagramAnalyzer.readAndProcessInputFile(darwinFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		pixelIslandList.sortBySizeDescending();
		Assert.assertEquals("darwin size", 7, pixelIslandList.size());
		PixelIsland pixelIsland0 = pixelIslandList.get(0);
		Assert.assertEquals("darwin main tree size", 4311, pixelIsland0.size());
		SVGSVG.wrapAndWriteAsSVG(pixelIsland0.getOrCreateSVGG(), new File(targetFile, "island0.svg"));
		PixelGraph pixelGraph = new PixelGraph(pixelIsland0);
		pixelGraph.mergeNodesCloserThan(12.);

		PixelNodeList nodeList = pixelGraph.getOrCreateNodeList();
		Assert.assertEquals("nodes", 58,  nodeList.size());
		PixelEdgeList edgeList = pixelGraph.getOrCreateEdgeList();
		Assert.assertEquals("edges",  58, edgeList.size());
		SVGG g = new SVGG();
		pixelGraph.createAndDrawGraph(g);
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetFile, "island0.graph.svg"));
	}

}
