package org.contentmine.image.pixel;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ColorStore;
import org.contentmine.graphics.svg.util.ColorStore.ColorizerType;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class PixelListTest {

	private final static Logger LOG = Logger.getLogger(PixelListTest.class);
	
	private static PixelList CREATE_TEST_ISLAND() {
		PixelList pixelList = new PixelList();
		pixelList.add(new Pixel(1, 11));
		pixelList.add(new Pixel(2, 11));
		pixelList.add(new Pixel(4, 11));
		pixelList.add(new Pixel(5, 11));
		pixelList.add(new Pixel(1, 12));
		pixelList.add(new Pixel(2, 12));
		pixelList.add(new Pixel(4, 12));
		pixelList.add(new Pixel(1, 13));
		pixelList.add(new Pixel(3, 13));
		pixelList.add(new Pixel(5, 13));
		pixelList.add(new Pixel(1, 14));
		pixelList.add(new Pixel(5, 14));
		pixelList.add(new Pixel(2, 15));
		pixelList.add(new Pixel(3, 15));
		pixelList.add(new Pixel(4, 15));
		pixelList.add(new Pixel(5, 16));
		return pixelList;
	}

	static PixelList CREATE_DIAMOND() {
		PixelList pixelList = new PixelList();
		pixelList.add(new Pixel(2, 12));
		pixelList.add(new Pixel(1, 13));
		pixelList.add(new Pixel(2, 14));
		pixelList.add(new Pixel(3, 15));
		pixelList.add(new Pixel(4, 16));
		pixelList.add(new Pixel(5, 15));
		pixelList.add(new Pixel(5, 14));
		pixelList.add(new Pixel(6, 13));
		pixelList.add(new Pixel(5, 12));
		pixelList.add(new Pixel(4, 11));
		pixelList.add(new Pixel(3, 11));
		return pixelList;
	}

	static PixelList CREATE_TWO_ISLANDS() {
		PixelList pixelList = CREATE_DIAMOND();
		pixelList.add(new Pixel(12, 2));
		pixelList.add(new Pixel(11, 3));
		pixelList.add(new Pixel(12, 4));
		pixelList.add(new Pixel(13, 5));
		pixelList.add(new Pixel(14, 4));
		pixelList.add(new Pixel(15, 5));
		pixelList.add(new Pixel(15, 4));
		pixelList.add(new Pixel(16, 3));
		pixelList.add(new Pixel(15, 2));
		pixelList.add(new Pixel(14, 1));
		pixelList.add(new Pixel(13, 1));
		return pixelList;
	}

	static PixelList CREATE_DIAMOND_SPIRO() {
		PixelList spiro = CREATE_DIAMOND();
		spiro.add(new Pixel(1,16));
		spiro.add(new Pixel(2,16));
		return spiro;
	}


	@Test
	public void testRemoveMinorIslands() {
		PixelList pixelList = new PixelList();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				pixelList.add(new Pixel(i, j));
			}
		}
		pixelList.add(new Pixel(10, 10));
		
		pixelList.add(new Pixel(20, 20));
		pixelList.add(new Pixel(20, 21));
		
		pixelList.add(new Pixel(30, 30));
		pixelList.add(new Pixel(30, 31));
		pixelList.add(new Pixel(30, 32));
		
		PixelIsland island = new PixelIsland(pixelList);
		
		Assert.assertEquals("pixels", 22, pixelList.size());
		pixelList.removeMinorIslands(1);
		Assert.assertEquals("pixels", 21, pixelList.size());
		pixelList.removeMinorIslands(2);
		Assert.assertEquals("pixels", 19, pixelList.size());
		pixelList.removeMinorIslands(3);
		Assert.assertEquals("pixels", 16, pixelList.size());
		
	}
	
	@Test
	public void testCreateBinary() {
		PixelList list = new PixelList();
		list.add(new Pixel(1, 10));
		list.add(new Pixel(2, 10));
		list.add(new Pixel(3, 10));
		list.add(new Pixel(1, 11));
		list.add(new Pixel(1, 12));
		list.add(new Pixel(2, 12));
		list.add(new Pixel(2, 13));
		int[][] binary = list.createBinary();
		IntMatrix matrix = new IntMatrix(binary);
		Assert.assertEquals("{3,4}\n(1,1,1,0)\n(1,0,1,1)\n(1,0,0,0)", matrix.toString());
		PixelList list1 = new PixelList();
	}
	
	@Test
	public void testCreateInterior() {
		PixelList pixelList = CREATE_TEST_ISLAND();
		
		int[][] binary = pixelList.createBinary();
		IntMatrix matrix = new IntMatrix(binary);
		Assert.assertEquals("{5,6}\n(1,1,1,1,0,0)\n(1,1,0,0,1,0)\n(0,0,1,0,1,0)\n(1,1,0,0,1,0)\n(1,0,1,1,0,1)", matrix.toString());
		
		PixelListFloodFill pixelListFloodFill = new PixelListFloodFill(pixelList);
		PixelList interiorPixelList = pixelListFloodFill.createInteriorPixelList();
		Assert.assertEquals("interior", 5, interiorPixelList.size());
		SVGSVG.wrapAndWriteAsSVG(interiorPixelList.getOrCreateSVG(), new File("target/pixels/interiorTest.svg"));
		
	}

	@Test
	public void testCreateOutline() {
		PixelList diamond = CREATE_DIAMOND();
		SVGSVG.wrapAndWriteAsSVG(diamond.getOrCreateSVG(), new File("target/pixels/diamondTest.svg"));
		PixelList extremes = diamond.findExtremePixels();
		Assert.assertEquals("extremes", "(3,11)(6,13)(4,16)(1,13)", extremes.toString());
		
	}

	@Test
	public void testAnalyzeOutline() {
		PixelList diamond = CREATE_DIAMOND();
		PixelOutliner outliner = new PixelOutliner(diamond);
		outliner.createOutline();
	}

	/**
	 * A-D-B is a straight line C and D "bulge out"
A straight 69:(39,114)(39,113)(39,112)(39,111)(39,110)(39,109)...(39,53)(39,52)(39,51)(39,50)(39,49)(39,48)(39,47)(39,46); nodeList: [<(39,114)><(39,46)>]
B straight 31: (39,1)(39,2)(39,3)(39,4)(39,5)(39,6)...(39,25)(39,26)(39,27)(39,28)(39,29)(40,30)(39,31); nodeList: [<(39,1)><(39,31)>]
C large circular arc 29: (39,31)(40,30)(41,30)(42,30)(43,30)(44,31)(45,31)(46,32)(47,33)(48,34)(48,35)(49,36)(49,37)(49,38)(49,39)(49,40)(49,41)(49,42)(48,43)(48,44)(47,45)(46,46)(45,47)(44,47)(43,47)(42,47)(41,47)(40,47)(39,46); nodeList: [<(39,31)><(39,46)>]
D small circular arc 20: (39,31)(38,32)(37,32)(36,33)(35,34)(34,35)(34,36)(33,37)(33,38)(33,39)(33,40)(33,41)(34,42)(34,43)(35,44)(35,45)(36,46)(37,46)(38,46)(39,46); nodeList: [<(39,31)><(39,46)>]
E straight 16: (39,31)(38,32)(39,33)(39,34)(39,35)(39,36)(39,37)(39,38)(39,39)(39,40)(39,41)(39,42)(39,43)(39,44)(39,45)(39,46); nodeList: [<(39,31)><(39,46)>]
	 */
	@Test
	public void testCreateCurvature0() {
		Iterator<String> iterator = ColorStore.createColorizer(ColorizerType.CONTRAST).getColorIterator();
		String filename = "crossing1.png";
		String [] filenames = filename.split("\\.");
		File imageFile = new File(ImageAnalysisFixtures.FUNNEL_DIR, filenames[0] + "." + filenames[1]);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals(4, pixelIslandList.size());
		// largest is a "phi-like" shape of line crossing circle
		PixelIsland pixelIsland = pixelIslandList.get(0);
		PixelGraph graph = pixelIsland.copyGraphAndTidy();
		PixelEdgeList edgeList = graph.getOrCreateEdgeList();
		SVGG g = new SVGG();
		for (int i = 0; i < edgeList.size(); i++) {
			PixelEdge edge = edgeList.get(i);
			PixelList pixelList = edge.getPixelList();
//			LOG.debug(pixelList.get(0)+"/"+pixelList.get(pixelList.size()-1));
			RealArray radiansPerPixel = pixelList.calculateCurvatureRadiansPerPixel().format(2);
			List<Real2> directionRadians = edge.getPixelList().calculateDirectionInRadians();
			SVGElement edgeSVG = pixelList.getOrCreateSVG();
			edgeSVG.setStrokeWidth(0.6);
			edgeSVG.setFill(iterator.next());
			edgeSVG.setStroke(iterator.next());
			edgeSVG.setOpacity(0.3);
			g.appendChild(edgeSVG);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/funnel/crossing1.lines.svg"));
		// extract circle
	}

	@Test
	@Ignore // FAILS - check this
	public void testCreateSegments() {
		int[] edgeSizes = {69, 31, 29, 20, 16};
		String[] segmentEdges = new String[] {
				"pixelList: (39,114)(39,106)(39,97)(39,88)(39,80)(39,72)(39,63)(39,54)(39,46); nodeList: [<(39,114)><(39,46)>]",
				"pixelList: (39,1)(39,5)(39,9)(39,12)(39,16)(39,20)(39,23)(39,27)(39,31); nodeList: [<(39,1)><(39,31)>]",
				"pixelList: (39,31)(43,30)(46,32)(48,35)(49,39)(48,43)(46,46)(43,47)(39,46); nodeList: [<(39,31)><(39,46)>]",
				"pixelList: (39,31)(37,32)(34,35)(33,37)(33,40)(34,42)(35,44)(37,46)(39,46); nodeList: [<(39,31)><(39,46)>]",
				"pixelList: (39,31)(39,33)(39,35)(39,37)(39,39)(39,40)(39,42)(39,44)(39,46); nodeList: [<(39,31)><(39,46)>]",
		};

		PixelEdgeList edgeList = getSortedCrossing1PixelEdges();
		int nsegments = 8;
		for (int i = 0; i < edgeList.size(); i++) {
			PixelEdge edge = edgeList.get(i);
			Assert.assertEquals("size", edgeSizes[i], edge.size());
			PixelEdge segmentedEdge = edge.createSegmentedEdge(nsegments);
			Assert.assertEquals("segment count", nsegments, segmentedEdge.size() -1 ); // subtract 1 for fence post correction
			Assert.assertEquals("edge "+i, segmentEdges[i], segmentedEdge.toString());
		}
	}

	@Test
	public void testCurvaturesStraight() {
		PixelEdgeList edgeList = getSortedCrossing1PixelEdges();
		PixelEdge edge0 = edgeList.get(0); // should be straight
		int pixelLength = 6;
		int nsegments = edge0.getPixelList().size()/pixelLength;
		PixelEdge segmentedEdge = edge0.createSegmentedEdge(nsegments);
		debugVectors(segmentedEdge, nsegments);
	}

	@Test
	public void testCurvaturesBent() {
		PixelEdgeList edgeList = getSortedCrossing1PixelEdges();
		PixelEdge edge1 = edgeList.get(2); // should be bent
		int pixelLength = 4;
		int nsegments = edge1.getPixelList().size()/pixelLength;
		PixelEdge segmentedEdge = edge1.createSegmentedEdge(nsegments);
		debugVectors(segmentedEdge, nsegments);
	}
	
	@Test
	/** joins two parts of a circle
	 * 
	 */
	public void testJoinAcyclicPaths() {
		PixelEdgeList edgeList = getSortedCrossing1PixelEdges();
		PixelEdge edge0 = edgeList.get(1); // should be straight
		PixelNode node00 = edge0.getNodes().get(0);
		PixelNode node01 = edge0.getNodes().get(1);
		PixelEdge edge1 = edgeList.get(4); // should be straight
		PixelNode node10 = edge1.getNodes().get(0);
		PixelNode node11 = edge1.getNodes().get(1);
		PixelEdge edge = edge0.join(edge1, node01, node10);
		
	}

	@Test
	/** joins three parts of a line
	 * 
	 */
	public void testJoinLineFragments() {
		PixelEdgeList edgeList = getSortedCrossing1PixelEdges();
		PixelEdge edge0 = new PixelEdge(edgeList.get(0)); // should be straight
		PixelEdge edge1 = new PixelEdge(edgeList.get(1)); // should be straight
		PixelEdge edge4 = new PixelEdge(edgeList.get(4)); // should be straight
		Assert.assertEquals("edge0", "pixelList: (39,114)(39,113)(39,112)(39,111)(39,110)(39,109)(39,108)(39,107)(39,106)(39,105)(39,104)(39,103)(39,102)(39,101)(39,100)(39,99)(39,98)(39,97)(39,96)(39,95)(39,94)(39,93)(39,92)(39,91)(39,90)(39,89)(39,88)(39,87)(39,86)(39,85)(39,84)(39,83)(39,82)(39,81)(39,80)(39,79)(39,78)(39,77)(39,76)(39,75)(39,74)(39,73)(39,72)(39,71)(39,70)(39,69)(39,68)(39,67)(39,66)(39,65)(39,64)(39,63)(39,62)(39,61)(39,60)(39,59)(39,58)(39,57)(39,56)(39,55)(39,54)(39,53)(39,52)(39,51)(39,50)(39,49)(39,48)(39,47)(39,46); nodeList: [<(39,114)><(39,46)>]", edge0.toString());
		Assert.assertEquals("edge4", "pixelList: (39,1)(39,2)(39,3)(39,4)(39,5)(39,6)(39,7)(39,8)(39,9)(39,10)(39,11)(39,12)(39,13)(39,14)(39,15)(39,16)(39,17)(39,18)(39,19)(39,20)(39,21)(39,22)(39,23)(39,24)(39,25)(39,26)(39,27)(39,28)(39,29)(40,30)(39,31); nodeList: [<(39,1)><(39,31)>]", edge1.toString());
		Assert.assertEquals("edge1", "pixelList: (39,31)(38,32)(39,33)(39,34)(39,35)(39,36)(39,37)(39,38)(39,39)(39,40)(39,41)(39,42)(39,43)(39,44)(39,45)(39,46); nodeList: [<(39,31)><(39,46)>]", edge4.toString());
		PixelEdge edge = edge0.join(edge4);
		Assert.assertEquals("edge", "pixelList: (39,46)(39,45)(39,44)(39,43)(39,42)(39,41)(39,40)(39,39)(39,38)(39,37)(39,36)(39,35)(39,34)(39,33)(38,32)(39,31); nodeList: [<(39,46)><(39,31)>]", edge4.toString());
		try {
			edge = edge0.join(edge1);
		} catch (RuntimeException e) {
			Assert.assertEquals(e.getMessage(), "Cannot find joining node");
		}
		edge = edge.join(edge1);
		Assert.assertEquals("edge", "pixelList: (39,46)(39,45)(39,44)(39,43)(39,42)(39,41)(39,40)(39,39)(39,38)(39,37)(39,36)(39,35)(39,34)(39,33)(38,32)(39,31); nodeList: [<(39,46)><(39,31)>]", edge4.toString());
		
	}

	@Test
	/** joins two cyclic fragments
	 * 
	 */
	public void testJoinCyclicFragments() {
		PixelEdgeList edgeList = getSortedCrossing1PixelEdges();
		PixelEdge edge2 = new PixelEdge(edgeList.get(2)); // should be curved
		PixelEdge edge3 = new PixelEdge(edgeList.get(3)); // should be curved
		Assert.assertEquals("edge2", "pixelList: (39,31)(40,30)(41,30)(42,30)(43,30)(44,31)(45,31)(46,32)(47,33)(48,34)(48,35)(49,36)(49,37)(49,38)(49,39)(49,40)(49,41)(49,42)(48,43)(48,44)(47,45)(46,46)(45,47)(44,47)(43,47)(42,47)(41,47)(40,47)(39,46); nodeList: [<(39,31)><(39,46)>]", edge2.toString());
		Assert.assertEquals("edge2", 29, edge2.size());
		Assert.assertEquals("edge3", "pixelList: (39,31)(38,32)(37,32)(36,33)(35,34)(34,35)(34,36)(33,37)(33,38)(33,39)(33,40)(33,41)(34,42)(34,43)(35,44)(35,45)(36,46)(37,46)(38,46)(39,46); nodeList: [<(39,31)><(39,46)>]", edge3.toString());
		Assert.assertEquals("edge3", 20, edge3.size());
		PixelEdge edge = edge2.join(edge3);
		Assert.assertEquals("edge", "pixelList: (39,31)(40,30)(41,30)(42,30)(43,30)(44,31)(45,31)(46,32)(47,33)(48,34)(48,35)(49,36)(49,37)(49,38)(49,39)(49,40)(49,41)(49,42)(48,43)(48,44)(47,45)(46,46)(45,47)(44,47)(43,47)(42,47)(41,47)(40,47)(39,46)(38,46)(37,46)(36,46)(35,45)(35,44)(34,43)(34,42)(33,41)(33,40)(33,39)(33,38)(33,37)(34,36)(34,35)(35,34)(36,33)(37,32)(38,32)(39,31); nodeList: [<(39,31)><(39,31)>]", edge.toString());
		Assert.assertEquals("edge", 20 + 29 - 1, edge.size());
		Assert.assertEquals("edge nodes", 2, edge.getNodes().size());
		edge = edge.cyclise();
		Assert.assertEquals("edge", 20 + 29 - 1 - 1, edge.size());
		Assert.assertEquals("edge nodes", 1, edge.getNodes().size());
		Assert.assertEquals("node 0", "<(39,31)>", edge.getNodes().get(0).toString());
	}
	
	@Test
	/** separates line and circle and draws SVG.
	 * 
	 */
	@Ignore // not yet fixed
	public void testSeparationOfOverlap() {
		SVGG g = new SVGG();
		PixelEdgeList edgeList = getSortedCrossing1PixelEdges();
		SVGElement gg = edgeList.getOrCreateSVG();
		gg.setStroke("black");
		gg.setStrokeWidth(0.5);
		g.appendChild(gg);
		
		PixelEdge edge0 = new PixelEdge(edgeList.get(0));
		PixelEdge edge4 = new PixelEdge(edgeList.get(4));
		PixelEdge edge1 = new PixelEdge(edgeList.get(1));
		PixelEdge line = edge0.join(edge4);
		line = line.join(edge1);
		SVGElement gline = line.getOrCreateSVG();
		gline.setStroke("red");
		gline.setStrokeWidth(0.3);
		gline.setOpacity(0.4);
		g.appendChild(gline.copy());
		gline.setOpacity(0.4);
		PixelSegmentList segmentList = line.getOrCreateSegmentList(1.0);
		if (segmentList.size() == 1) {
			SVGLine svgLine = segmentList.get(0).getSVGLine();
			svgLine.setStrokeWidth(4.0);
			svgLine.setStroke("cyan");
			svgLine.setOpacity(0.4);
			g.appendChild(svgLine);
		}
		
		SVGSVG.wrapAndWriteAsSVG(gline, new File("target/pixelList/line.svg"));
		
		PixelEdge edge2 = new PixelEdge(edgeList.get(2));
		PixelEdge edge3 = new PixelEdge(edgeList.get(3));
		PixelEdge cycle = edge2.join(edge3);
		cycle = cycle.cyclise();
		double segmentCreationTolerance = 1.0;
		PixelSegmentList cyclicSegmentList = cycle.getOrCreateSegmentList(segmentCreationTolerance);
		SVGElement svgPse = cyclicSegmentList.getSVGG();
		svgPse.setStrokeWidth(0.5);
		svgPse.setStroke("yellow");
		SVGCircle circle = cyclicSegmentList.createCircle(/*segmentCreationTolerance*/3.0);
		SVGElement gcycle = cycle.getOrCreateSVG();
		gcycle.setStroke("pink");
		gcycle.setStrokeWidth(3.0);
		g.appendChild(gcycle.copy());
		g.appendChild(svgPse);
		gcycle.setOpacity(0.4);
		gcycle.setOpacity(0.4);
		g.appendChild(gcycle);
		SVGSVG.wrapAndWriteAsSVG(gcycle, new File("target/pixelList/cycle.svg"));
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/pixelList/lineCycle.svg"));
	}

	private void debugVectors(PixelEdge segmentedEdge, int nsegments) {
		Pixel pixel0 = segmentedEdge.get(0);
		for (int i = 1; i < nsegments; i++) {
			Pixel pixeli = segmentedEdge.get(i);
			Int2 vector = pixeli.subtract(pixel0);
			Real2 rVector = new Real2(vector);
			LOG.trace("0->"+i+": "+rVector+"; "+Util.format(rVector.getAngle(),2));
			Int2 vector_1 = pixeli.subtract(segmentedEdge.get(i - 1));
			Real2 rVector_1 = new Real2(vector_1);
			LOG.trace("    "+(i-1)+"->"+i+": "+rVector_1+"; "+Util.format(rVector_1.getAngle(),2));
			if (i > 1) {
				Int2 vector02 = pixeli.subtract(segmentedEdge.get(i - 2));
				Real2 rVector02 = new Real2(vector02);
				LOG.trace("        "+(i-2)+"->"+i+": "+rVector02+"; "+Util.format(rVector02.getAngle(),2));
			}
		}
	}

	// =====================================
	
	private PixelEdgeList getSortedCrossing1PixelEdges() {
		File imageFile = new File(ImageAnalysisFixtures.FUNNEL_DIR, "crossing1.png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		PixelIsland phiIsland = pixelIslandList.get(0);
		PixelGraph graph = phiIsland.copyGraphAndTidy();
		PixelEdgeList edgeList = graph.getOrCreateEdgeList();
		edgeList.sort(ComparatorType.SIZE);
		edgeList.reverse();
		return edgeList;
	}

}
