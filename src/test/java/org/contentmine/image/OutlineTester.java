package org.contentmine.image;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelNodeList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.pixel.PixelSegmentList;
import org.contentmine.image.plot.PlotTest;
import org.junit.Assert;

public class OutlineTester {
	private static final Logger LOG = Logger.getLogger(OutlineTester.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** parameters*/
    public int[][] expectedRingSizes;
    public int[] nodes;
    public int[] edges;
    public int[] outlines;
    public String dir;
    public String inname;
    public File outdir;
    public File indir ;
    public File imageFile;
    public int islandCount;
	public int mainIslandCount;
	public int[] pixelRingListCount;
	
	private ImageProcessor imageProcessor;
	private PixelIslandList islandList;
	private int currentIslandIndex;
	
	
	public void analyzeAndAssertFile() {
		imageFile = new File(indir, inname+".png");
	
		imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.readAndProcessFile(imageFile);
		islandList = imageProcessor.getOrCreatePixelIslandList();
		Assert.assertEquals(islandCount, islandList.size());
		currentIslandIndex = 0;
		for (; currentIslandIndex < mainIslandCount; currentIslandIndex++) {
			displayAndAssertIslands();
		}
	}
	
	public void displayAndAssertIslands() {
		PixelIsland islandSerial = new PixelIsland(islandList.get(currentIslandIndex));
	
		SVGG g = new SVGG();
		PixelRingList pixelRingList = islandSerial.getOrCreateInternalPixelRings();
		Assert.assertEquals(pixelRingListCount[currentIslandIndex], pixelRingList.size());
		for (int i = 0; i < pixelRingList.size(); i++) {
			pixelRingList.get(i).plotPixels(g, PlotTest.FILL[i % PlotTest.FILL.length]);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(outdir, inname + "AllRings."+currentIslandIndex+".svg"));
	
		PixelRing outline = pixelRingList.get(1).getPixelsTouching(pixelRingList.get(0));
		for (int iRing = 0; iRing < Math.min(expectedRingSizes.length, pixelRingList.size()); iRing++) {
//			LOG.debug("ser "+currentIslandIndex+", iRing "+iRing);
			Assert.assertEquals("ring"+iRing, expectedRingSizes[currentIslandIndex][iRing], pixelRingList.get(iRing).size());
		}
		g = new SVGG();
		outline.plotPixels(g, "blue");
		// this is the outline of the symbol 
		SVGSVG.wrapAndWriteAsSVG(g, new File(outdir, inname+"Outline."+currentIslandIndex+".a.svg"));
	
		// just plots the first ring. may change this?
		g = new SVGG();
		outline = new PixelRing(pixelRingList.get(0).getPixelsWithOrthogonalContactsTo(pixelRingList.get(1), islandSerial));
		outline.plotPixels(g, PlotTest.BLUE);
		PixelIsland outlineIsland = PixelIsland.createSeparateIslandWithClonedPixels(outline, true);
		PixelGraph graph = PixelGraph.createGraph(outlineIsland);
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		Assert.assertEquals("nodes", nodes[currentIslandIndex], nodeList.size());
		PixelEdgeList edgeList = graph.getOrCreateEdgeList();
		Assert.assertEquals("edges", edges[currentIslandIndex], edgeList.size());
		for (PixelEdge edge : edgeList) {
			PixelSegmentList segmentList = edge.getOrCreateSegmentList(2);
			g.appendChild(segmentList.getOrCreateSVG());
		}
		
		SVGSVG.wrapAndWriteAsSVG(g, new File(outdir, inname+"Outline."+currentIslandIndex+".ring1.svg"));
	}

}
