package org.contentmine.image.pixel;

import org.apache.log4j.Logger;
import org.contentmine.image.ImageAnalysisFixtures;
import org.junit.Assert;
import org.junit.Test;

public class PixelNucleusFactoryAndSpikesTest {
	
	private final static Logger LOG = Logger.getLogger(PixelNucleusFactoryAndSpikesTest.class);

	@Test
	public void testPixelNucleusListDot() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_DOT_ISLAND());
		Assert.assertEquals("island", "(0,0)",
				ImageAnalysisFixtures.CREATE_DOT_ISLAND().getPixelList().toString());
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateYXSortedNucleusList(2);
		Assert.assertEquals("nucleusList", 1, nucleusList.size());
		Assert.assertEquals("nucleusList: ", "[{(0,0)}]", 
				nucleusList.toString());
	}

	@Test
	public void testPixelNucleusListLine() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_LINE_ISLAND());
		Assert.assertEquals("island", "(2,0)(1,0)(0,1)(-1,2)(0,3)(0,4)",
				ImageAnalysisFixtures.CREATE_LINE_ISLAND().getPixelList().toString());
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateYXSortedNucleusList(2);
		nucleusList.sortYX(3);
		Assert.assertEquals("nucleusList", 2, nucleusList.size());
		Assert.assertEquals("nucleusList: ", "[{(2,0)}{(0,4)}]", 
				nucleusList.toString());
	}

	@Test
	public void testPixelNucleusListCycle() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_CYCLE_ISLAND());
		Assert.assertEquals("island", "(1,0)(0,1)(-1,0)(0,-1)",
				ImageAnalysisFixtures.CREATE_CYCLE_ISLAND().getPixelList().toString());
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateYXSortedNucleusList(2);
		Assert.assertEquals("nucleusList", 1, nucleusList.size());
//		Assert.assertEquals("nucleusList: ", "[{(1,0)}]", // position is arbitrary
//				nucleusList.toString());
	}

	@Test
	public void testPixelNucleusListY() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_Y_ISLAND());
		Assert.assertEquals("island", "(0,0)(0,1)(0,2)(0,3)(-1,-1)(-2,-2)(-3,-3)(1,-1)(2,-2)(3,-3)",
				ImageAnalysisFixtures.CREATE_Y_ISLAND().getPixelList().toString());
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateYXSortedNucleusList(2);
		nucleusList.sortYX(3);
		Assert.assertEquals("nucleusList", 4, nucleusList.size());
		Assert.assertEquals("nucleusList: ", "[{(-3,-3)}{(3,-3)}{(0,0)}{(0,3)}]", 
				nucleusList.toString());
	}

	@Test
	public void testPixelNucleusListDoubleY() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_DOUBLE_Y_ISLAND());
		Assert.assertEquals("island", "(0,0)(0,1)(0,2)(1,3)(-1,3)(2,4)(-2,4)(3,5)(-3,5)(0,-1)(0,-2)(1,-3)(-1,-3)(2,-4)(-2,-4)(3,-5)(-3,-5)",
				ImageAnalysisFixtures.CREATE_DOUBLE_Y_ISLAND().getPixelList().toString());
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateYXSortedNucleusList(3);
		nucleusList.sortYX(3);
		Assert.assertEquals("nucleusList", 6, nucleusList.size());
		Assert.assertEquals("nucleusList: ", "[{(-3,-5)}{(3,-5)}{(0,-2)}{(0,2)}{(-3,5)}{(3,5)}]", 
				nucleusList.toString());
	}

	@Test
	public void testPixelNucleusListSpikedHexagon() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_TRISPIKED_HEXAGON_ISLAND());
		Assert.assertEquals("island", "(0,0)(0,1)(0,2)(1,3)(-1,3)(1,4)(-1,4)(-2,5)(0,5)(2,5)(-3,6)(3,6)(-4,7)(4,7)",
				ImageAnalysisFixtures.CREATE_TRISPIKED_HEXAGON_ISLAND().getPixelList().toString());
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateYXSortedNucleusList(2);
		nucleusList.sortYX(3);
		Assert.assertEquals("nucleusList", 6, nucleusList.size());
		Assert.assertEquals("nucleusList: ", "[{(0,0)}{(0,2)}{(-1,4)}{(1,4)}{(-4,7)}{(4,7)}]", 
				nucleusList.toString());
	}

	@Test
	/**
	 * +
	 *  ++    ++
	 *    ++++
	 *     +
	 *     +
	 *     +
	 */
	public void testPixelNucleusListT() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_T_ISLAND());
		Assert.assertEquals("island", "(-1,-1)(0,0)(1,0)(2,1)(3,1)(4,1)(5,1)(6,0)(7,0)(3,2)(3,3)(3,4)(3,5)",
				ImageAnalysisFixtures.CREATE_T_ISLAND().getPixelList().toString());
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateYXSortedNucleusList(2);
		nucleusList.sortYX(3);
		LOG.trace("nucleusList "+nucleusList.size()+"; "+nucleusList);
		Assert.assertEquals("nucleusList", 4, nucleusList.size());
		Assert.assertEquals("nucleusList: ", ""
				+ "[{(-1,-1)}{(3,1)(3,2)(2,1)(4,1)}{(7,0)}{(3,5)}]", 
				nucleusList.toString());
	}

	@Test
	public void testSpikesDot() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_DOT_ISLAND());
		Assert.assertEquals("dot", "::", nucleusFactory.createYXSortedSpikePixelList().toString());
	}

	@Test
	public void testSpikesLine() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_LINE_ISLAND());
		Assert.assertEquals("line", "(1,0)(0,3)", nucleusFactory.createYXSortedSpikePixelList().toString());
	}

	@Test
	public void testSpikesCycle() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_CYCLE_ISLAND());
		Assert.assertEquals("cycle", "(-1,0)(1,0)", nucleusFactory.createYXSortedSpikePixelList().toString());
	}

	@Test
	public void testSpikesY() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_Y_ISLAND());
		Assert.assertEquals("Y", "(-2,-2)(2,-2)(-1,-1)(1,-1)(0,1)(0,2)", nucleusFactory.createYXSortedSpikePixelList().toString());
	}

	@Test
	public void testSpikesDoubleY() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_DOUBLE_Y_ISLAND());
		Assert.assertEquals("doubleY", "(-2,-4)(2,-4)(-1,-3)(1,-3)(0,-1)(0,1)(-1,3)(1,3)(-2,4)(2,4)", 
				nucleusFactory.createYXSortedSpikePixelList().toString());
	}

	@Test
	public void testSpikesSpikedHexagon() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_TRISPIKED_HEXAGON_ISLAND());
		nucleusFactory.getOrCreateYXSortedNucleusList(2);
		Assert.assertEquals("spikedHexagon", "(0,1)(0,1)(-1,3)(-1,3)(1,3)(1,3)(-2,5)(0,5)(0,5)(2,5)(-3,6)(3,6)", 
				nucleusFactory.createYXSortedSpikePixelList().toString());
	}
	
	@Test
	public void testSpikesT() {
		PixelNucleusFactory nucleusFactory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_T_ISLAND());
		Assert.assertEquals("T", "(0,0)(1,0)(6,0)(5,1)(3,3)(3,4)",     // ?????
				nucleusFactory.createYXSortedSpikePixelList().toString());
	}

	// -----------
	@Test
	public void testJoinSpikesDot() {
		PixelNucleusFactory factory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_DOT_ISLAND());
	    factory.createNodesAndEdges();
		Assert.assertEquals("edges", "", 
				factory.getEdgeList().toString());
	}

	@Test
	public void testJoinSpikesLine() {
		PixelNucleusFactory factory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_LINE_ISLAND());
	    factory.createNodesAndEdges();
		Assert.assertEquals("edges", "pixelList: (2,0)(1,0)(0,1)(-1,2)(0,3)(0,4); nodeList: [<(2,0)><(0,4)>]", 
				factory.getEdgeList().toString());
	}

	@Test
	public void testJoinSpikesCycle() {
		PixelNucleusFactory factory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_CYCLE_ISLAND());
	    factory.createNodesAndEdges();
		Assert.assertEquals("edges", "pixelList: (0,-1)(1,0)(0,1)(-1,0)(0,-1); nodeList: [<(0,-1)><(0,-1)>]", 
				factory.getEdgeList().toString());
	}

	@Test
	public void testJoinSpikesY() {
		PixelNucleusFactory factory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_Y_ISLAND());
	    factory.createNodesAndEdges();
		Assert.assertEquals("edges", "pixelList: (0,3)(0,2)(0,1)(0,0); nodeList: [<(0,3)><(0,0)>]pixelList: (-3,-3)(-2,-2)(-1,-1)(0,0); nodeList: [<(-3,-3)><(0,0)>]pixelList: (3,-3)(2,-2)(1,-1)(0,0); nodeList: [<(3,-3)><(0,0)>]", 
				factory.getEdgeList().toString());
	}

	@Test
	public void testJoinSpikesDoubleY() {
		PixelNucleusFactory factory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_DOUBLE_Y_ISLAND());
	    factory.createNodesAndEdges();
		Assert.assertEquals("edges", "pixelList: (3,5)(2,4)(1,3)(0,2); nodeList: [<(3,5)><(0,2)>]pixelList: (-3,5)(-2,4)(-1,3)(0,2); nodeList: [<(-3,5)><(0,2)>]pixelList: (3,-5)(2,-4)(1,-3)(0,-2); nodeList: [<(3,-5)><(0,-2)>]pixelList: (-3,-5)(-2,-4)(-1,-3)(0,-2); nodeList: [<(-3,-5)><(0,-2)>]pixelList: (0,2)(0,1)(0,0)(0,-1)(0,-2); nodeList: [<(0,2)><(0,-2)>]", 
				factory.getEdgeList().toString());
	}

	@Test
	public void testJoinSpikesSpikedHexagon() {
		PixelNucleusFactory factory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_TRISPIKED_HEXAGON_ISLAND());
	    factory.createNodesAndEdges();
		Assert.assertEquals("edges", "pixelList: (0,2)(0,1)(0,0); nodeList: [<(0,2)><(0,0)>]pixelList: (-4,7)(-3,6)(-2,5)(-1,4); nodeList: [<(-4,7)><(-1,4)>]pixelList: (4,7)(3,6)(2,5)(1,4); nodeList: [<(4,7)><(1,4)>]pixelList: (1,4)(1,3)(0,2); nodeList: [<(1,4)><(0,2)>]pixelList: (-1,4)(-1,3)(0,2); nodeList: [<(-1,4)><(0,2)>]pixelList: (1,4)(0,5)(-1,4); nodeList: [<(1,4)><(-1,4)>]",
				factory.getEdgeList().toString());
	}

	@Test
	public void testJoinSpikesT() {
		PixelNucleusFactory factory = new PixelNucleusFactory(ImageAnalysisFixtures.CREATE_T_ISLAND());
	    factory.createNodesAndEdges();
		Assert.assertEquals("edges", "pixelList: (-1,-1)(0,0)(1,0)(2,1); nodeList: [<(-1,-1)><(3,1)>]pixelList: (7,0)(6,0)(5,1)(4,1); nodeList: [<(7,0)><(3,1)>]pixelList: (3,5)(3,4)(3,3)(3,2); nodeList: [<(3,5)><(3,1)>]", 
				factory.getEdgeList().toString());
	}

	
	// ====================================
	
	private void debugSpikes(PixelNucleusList nucleusList) {
		for (int i = 0; i < nucleusList.size(); i++) {
			PixelNucleus nucleus = nucleusList.get(i);
			PixelList nucleusPixelList = nucleus.createSpikePixelList();
			LOG.trace(nucleusPixelList);
		}
		LOG.trace("========");
	}

	public void debugIterator(PixelSet spikeSet) {
		int maxCount = 1000000;
		PixelSet set = new PixelSet(spikeSet);
		while (!set.isEmpty() && maxCount-- > 0) {
			Pixel pixel = set.next();
			set.remove(pixel);
			LOG.debug("it> "+pixel);
		}
		LOG.debug(spikeSet.toString());
	}


}
