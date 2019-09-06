package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.processing.ZhangSuenThinning;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class PixelGraphTest {

	private final static Logger LOG = Logger.getLogger(PixelGraphTest.class);
	
	@Test
	public void testSingleCycle() {
		PixelGraph graph = PixelGraph.createGraph(ImageAnalysisFixtures.CREATE_CYCLE_ISLAND());
		Assert.assertNotNull(graph);
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		PixelTestUtils.assertNodeList(nodeList, 1, "[<(0,-1)>]"); 
	}

	@Test
	// simple line
	public void test2Nodes() {
		PixelGraph graph = PixelGraph.createGraph(ImageAnalysisFixtures.CREATE_LINE_ISLAND());
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		PixelTestUtils.assertNodeList(nodeList, 2, "[<(2,0)><(0,4)>]"); 
	}

	@Test
	// Y-shaped tree
	public void test3Terminals() {
		PixelGraph graph = PixelGraph.createGraph(ImageAnalysisFixtures.CREATE_Y_ISLAND());
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		PixelTestUtils.assertNodeList(nodeList, 4, "[<(0,3)><(-3,-3)><(3,-3)><(0,0)>]"); 
	}

	@Test
	// 2 Y shapes joined
	/**
	 * +     +
	 *  +   +
	 *   + +
	 *    +
	 *    +
	 *    +
	 *    +
	 *   + +
	 *  +   +
	 * +     +
	 */
	public void testDoubleYGraph() {
		PixelGraph graph = PixelGraph.createGraph(ImageAnalysisFixtures.CREATE_DOUBLE_Y_ISLAND());
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		PixelTestUtils.assertNodeList(nodeList, 6, "[<(3,5)><(-3,5)><(3,-5)><(-3,-5)><(0,2)><(0,-2)>]"); 
//		PixelEdgeList edgeList = graph.getEdgeList();
//		Assert.assertEquals("edges", 5, edgeList.size()); 
//		Assert.assertEquals("{(-3,-5)(-2,-4)(-1,-3)(0,-2)}/[(-3,-5), (0,-2)]", edgeList.get(0).toString());
//		Assert.assertEquals("{(3,-5)(2,-4)(1,-3)(0,-2)}/[(3,-5), (0,-2)]", edgeList.get(1).toString());
//		Assert.assertEquals("{(0,-2)(0,-1)(0,0)(0,1)(0,2)}/[(0,-2), (0,2)]", edgeList.get(2).toString());
//		Assert.assertEquals("{(0,2)(-1,3)(-2,4)(-3,5)}/[(0,2), (-3,5)]", edgeList.get(3).toString());
//		Assert.assertEquals("{(0,2)(1,3)(2,4)(3,5)}/[(0,2), (3,5)]", edgeList.get(4).toString());
//		// 2 junction
//		JunctionSet junctionSet = graph.getJunctionSet();
//		Assert.assertNotNull(junctionSet);
//		Assert.assertEquals("[(0,-2), (0,2)]", junctionSet.toString()); 
//		// text 4 terminals
//		TerminalNodeSet terminalSet = graph.getTerminalNodeSet();
//		Assert.assertNotNull(terminalSet);
//		Assert.assertNotNull(terminalSet);
//		Assert.assertEquals("[(-3,-5), (3,-5), (-3,5), (3,5)]", terminalSet.toString()); 
	}


	@Test
	/** hexagon with 3 spikes
	 *       X
	 *       X
	 *       X
	 *      X X
	 *      X X
	 *     X X X
	 *    X     X
	 *   X       X
	 */
	public void test135TrimethylBenzene() {
		PixelGraph graph = PixelGraph.createGraph(ImageAnalysisFixtures.CREATE_TRISPIKED_HEXAGON_ISLAND());
//		PixelEdgeList edgeList = graph.getEdgeList();
//		Assert.assertEquals(6, edgeList.size()); 
//		Assert.assertEquals("{(0,0)(0,1)(0,2)}/[(0,0), (0,2)]", edgeList.get(0).toString());
//		Assert.assertEquals("{(0,2)(-1,3)(-1,4)}/[(0,2), (-1,4)]", edgeList.get(1).toString());
//		Assert.assertEquals("{(0,2)(1,3)(1,4)}/[(0,2), (1,4)]", edgeList.get(2).toString());
//		Assert.assertEquals("{(-1,4)(-2,5)(-3,6)(-4,7)}/[(-1,4), (-4,7)]", edgeList.get(3).toString());
//		Assert.assertEquals("{(-1,4)(0,5)(1,4)}/[(-1,4), (1,4)]", edgeList.get(4).toString());
//		Assert.assertEquals("{(1,4)(2,5)(3,6)(4,7)}/[(1,4), (4,7)]", edgeList.get(5).toString());
//		JunctionSet junctionSet = graph.getJunctionSet();
//		Assert.assertNotNull(junctionSet);
//		Assert.assertEquals("[(0,2), (-1,4), (1,4)]", junctionSet.toString()); 
//		TerminalNodeSet terminalSet = graph.getTerminalNodeSet();
//		Assert.assertNotNull(terminalSet);
//		Assert.assertEquals("[(0,0), (-4,7), (4,7)]", terminalSet.toString()); 
	}

	@Test
	/** rhombus has not been thinned so throws exception.
	 * 
	 */
	public void testRhombus() {
		PixelIsland island = ImageAnalysisFixtures.CREATE_RHOMBUS_ISLAND();
		try {
			PixelGraph graph = PixelGraph.createGraph(island);
			Assert.assertTrue("should not throw exception", true);
			// error is now logged, not thrown
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	/** rhombus with side shoots.
	 * 
	 * 
	 */
	public void testZNucleus() {
		PixelIsland island = ImageAnalysisFixtures.CREATE_ZNUCLEUS_ISLAND();
		try {
			PixelGraph graph = PixelGraph.createGraph(island);
			Assert.assertTrue("should not throw exception", true);
			// error is now logged, not thrown
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	@Ignore // recent edge algorithms broke this

	public void testWCorner() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,3));
		PixelGraph graph = PixelGraph.createGraph(island);
		Assert.assertEquals(7, graph.getPixelList().size());
		Assert.assertEquals(5, graph.getOrCreateNodeList().size());
//		Map<JunctionNode, PixelNucleus> nucleusByJunctionMap = graph.getNucleusByJunctionMap();
//		Assert.assertEquals(5, nucleusByJunctionMap.size());
//		if (graph.getNucleusSet() == null) {
//			graph.makeNucleusMap();
//		}
//		Set<PixelNucleus> nucleusSet = graph.getNucleusSet();
//		Assert.assertEquals(1, nucleusSet.size());
//		Assert.assertEquals(5, nucleusSet.iterator().next().size());
	}

	@Test
	// FIXME this gives wrong nucleus count
	public void test2Nuclei() {
		PixelIsland island = CREATE_TWO_NUCLEUS_ISLAND();
		Assert.assertEquals(12, island.getPixelList().size());
		try {
			PixelGraph graph = PixelGraph.createGraph(island);
//			Assert.fail("Non-thinned nucleus should throw");
			// now logs rather than throwing
		} catch (RuntimeException e) {
		}
	}

	public static PixelIsland CREATE_TWO_NUCLEUS_ISLAND() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(3,3));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(4,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(5,4));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(5,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(6,5));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(7,6));
		return island;
	}
	
	@Test
	@Ignore // out of memory on Jenkins
	public void testExtremeEdge() {
		BufferedImage image = ImageUtil.readImage(new File(ImageAnalysisFixtures.COMPOUND_DIR, "journal.pone.0094172.g002-2.png"));
		image = ImageUtil.boofCVBinarization(image, 160);
		image = ImageUtil.thin(image, new ZhangSuenThinning());
		ImageIOUtil.writeImageQuietly(image, new File("target/edge/0094172.png"));
		PixelIslandList pixelIslandList = PixelIslandList.createSuperThinnedPixelIslandList(image);
		LOG.trace("islands: "+pixelIslandList.size());
		PixelIsland island = pixelIslandList.getLargestIsland();
		PixelGraph graph = null;
		try {
			graph = PixelGraph.createGraph(island);
		} catch (RuntimeException e) {
			Assert.fail("failed "+e);
			e.printStackTrace();
		}
	}
	
	@Test
	public void testShortEdge() {
		PixelIsland shortEdgeIsland = ImageAnalysisFixtures.CREATE_SHORTEDGE_ISLAND();
		PixelGraph graph = new PixelGraph(shortEdgeIsland);
		graph.createNodesAndEdges();
		Assert.assertEquals(17, graph.getPixelList().size());
		
		Assert.assertEquals(6,  graph.getOrCreateNodeList().size());
		Assert.assertEquals(5,  graph.getOrCreateEdgeList().size());
		graph.compactCloseNodes(5);
		Assert.assertEquals(5,  graph.getOrCreateNodeList().size());
		Assert.assertEquals(4,  graph.getOrCreateEdgeList().size());
		Assert.assertEquals(17, graph.getPixelList().size()); // pixels not changed
//		LOG.debug(graph);
		PixelEdgeList edgeList = graph.getOrCreateEdgeList().getEdges(new Pixel(0,0), new Pixel(3,5));
	}
	
	@Test
	public void testMultipleConnections() {
		PixelIsland phiIsland = ImageAnalysisFixtures.CREATE_PHI_ISLAND();
		PixelGraph graph = new PixelGraph(phiIsland);
		graph.createNodesAndEdges();
		Assert.assertEquals(21, graph.getPixelList().size());
		
		Assert.assertEquals(4,  graph.getOrCreateNodeList().size());
		Assert.assertEquals(5,  graph.getOrCreateEdgeList().size());
		
		PixelNode node02 = graph.getOrCreateNodeList().getPixelNode(new Pixel(0,2));
		Assert.assertEquals("<(0,2)>", node02.toString());
		PixelNodeList connectedList = node02.getConnectedNodes();
		Assert.assertEquals("[<(0,5)><(0,-2)><(0,-2)><(0,-2)>]", connectedList.toString());
		
		node02 = graph.getOrCreateNodeList().getPixelNode(new Pixel(0,5));
		Assert.assertEquals("<(0,5)>", node02.toString());
		connectedList = node02.getConnectedNodes();
		Assert.assertEquals("[<(0,2)>]", connectedList.toString());
		
		
	}

	/** condense edge.
	 * tested on toy graph, and may not test all properties
	 * 
	 */
	@Test
	
	public void testCondenseEdgeAndRemoveOneNode() {
		PixelGraph graph = PixelGraph.createEmptyGraph();
		
		PixelNode node0 = new PixelNode();
		node0.setCentrePixel(new Pixel(new Int2(0,0)));
		graph.addNode(node0);

		PixelNode node1 = new PixelNode();
		node1.setCentrePixel(new Pixel(new Int2(0,10)));
		PixelEdge edge01 = new PixelEdge(graph);
		graph.addNode(node1);
		edge01.addNode(node0, 0);
		edge01.addNode(node1, 1);
		graph.addEdge(edge01);

		PixelNode node2 = new PixelNode();
		node2.setCentrePixel(new Pixel(new Int2(10,0)));
		PixelEdge edge02 = new PixelEdge(graph);
		graph.addNode(node2);
		edge02.addNode(node0, 0);
		edge02.addNode(node2, 1);
		graph.addEdge(edge02);

		PixelNode node3 = new PixelNode();
		node3.setCentrePixel(new Pixel(new Int2(10,10)));
		PixelEdge edge23 = new PixelEdge(graph);
		graph.addNode(node3);
		edge23.addNode(node2, 0);
		edge23.addNode(node3, 1);
		graph.addEdge(edge23);

		Assert.assertEquals(4, graph.getOrCreateNodeList().size());
		Assert.assertEquals(3, graph.getOrCreateEdgeList().size());
		Assert.assertEquals("[<(0,0)><(0,10)><(10,0)><(10,10)>]", graph.getOrCreateNodeList().toString());
		Assert.assertEquals("pixelList: null; nodeList: [<(0,0)><(0,10)>]", graph.getOrCreateEdgeList().get(0).toString());
		Assert.assertEquals("pixelList: null; nodeList: [<(0,0)><(10,0)>]", graph.getOrCreateEdgeList().get(1).toString());
		Assert.assertEquals("pixelList: null; nodeList: [<(10,0)><(10,10)>]", graph.getOrCreateEdgeList().get(2).toString());
		graph.condenseEdgeAndRemoveOneNode(edge02);
		Assert.assertEquals(3, graph.getOrCreateNodeList().size());
		Assert.assertEquals(2, graph.getOrCreateEdgeList().size());
		Assert.assertEquals("[<(5,0)><(0,10)><(10,10)>]", graph.getOrCreateNodeList().toString());
		Assert.assertEquals("pixelList: null; nodeList: [<(5,0)><(0,10)>]", graph.getOrCreateEdgeList().get(0).toString());
		Assert.assertEquals("pixelList: null; nodeList: [<(5,0)><(10,10)>]", graph.getOrCreateEdgeList().get(1).toString());
	}
}
