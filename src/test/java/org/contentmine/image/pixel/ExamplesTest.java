package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.testutil.TestUtils;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ColorStore;
import org.contentmine.graphics.svg.util.ColorStore.ColorizerType;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.processing.ZhangSuenThinning;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** useful examples which act as demo and regression tests.
 * 
 * @author pm286
 *
 */
public class ExamplesTest {

	private ImageProcessor DEFAULT_PROCESSOR;

	private final static Logger LOG = Logger.getLogger(ExamplesTest.class);
	private static File EXAMPLE_DIR = new File("target/examples/");
	
	@Before
	public void setup() {
		DEFAULT_PROCESSOR = new ImageProcessor();
	}

	/** Skeleton of a molecular structure diagram (without element symbols)
	 * 
	 * one large island representing 12 nodes and 12 edges.
	 * 
	 * then 4 smaller islands which are straight lines defining double bonds
	 * SHOWCASE
	 */
	@Test
	public void testMaltoryzineEdges() {
		BufferedImage image = DEFAULT_PROCESSOR.processImageFile(ImageAnalysisFixtures.MALTORYZINE_BINARY_PNG);
		PixelIslandList thinnedIslandList = PixelIslandList.createSuperThinnedPixelIslandList(image);
		SVGSVG.wrapAndWriteAsSVG(thinnedIslandList.getOrCreateSVGG(), new File("target/examples/thinnedMaltoryzine.svg"));
		PixelIslandList superThinnedIslandList = PixelIslandList.createSuperThinnedPixelIslandList(image);
		SVGSVG.wrapAndWriteAsSVG(superThinnedIslandList.getOrCreateSVGG(), new File("target/examples/superThinnedMaltoryzine.svg"));
		superThinnedIslandList.sortBySizeDescending();
		PixelIsland largest = superThinnedIslandList.get(0);
		SVGSVG.wrapAndWriteAsSVG(largest.getOrCreateSVGG(), new File("target/examples/maltoryzineLargest.svg"));
		// largest island is 12-node, 12-edge framework
		PixelNucleusFactory factory = new PixelNucleusFactory(superThinnedIslandList.get(0));
		Assert.assertEquals("nuclei", ""
				+ "12: "
				+ "[{(45,34)}{(106,34)}{(113,34)}{(99,124)}{(45,154)}{(238,57)}{(46,56)(45,56)(45,55)}{(77,74)(77,75)(76,74)(78,74)}{(106,57)(107,57)(106,58)(106,56)}{(113,57)(113,56)(112,57)}{(78,112)(77,112)(77,111)}{(45,130)(45,131)(46,130)(44,130)}]",
				""+factory.getOrCreateNucleusList().size()+": "+factory.getOrCreateNucleusList().toString());
		factory.createNodesAndEdges();
		// 12 edges between nodes. Some have kinks
		String pixelList0 = "pixelList: (45,34)(45,35)(45,36)(45,37)(45,38)(45,39)(45,40)(45,41)(45,42)(45,43)(45,44)(45,45)(45,46)(45,47)(45,48)(45,49)(45,50)(45,51)(45,52)(45,53)(45,54)(45,55); nodeList: [<(45,34)><(45,56)>]pixelList: (106,34)(106,35)(106,36)(106,37)(106,38)(106,39)(106,40)(106,41)(106,42)(106,43)(106,44)(106,45)(106,46)(106,47)(106,48)(106,49)(106,50)(106,51)(106,52)(106,53)(106,54)(106,55)(106,56); nodeList: [<(106,34)><(106,57)>]pixelList: (113,34)(113,35)(113,36)(113,37)(113,38)(113,39)(113,40)(113,41)(113,42)(113,43)(113,44)(113,45)(113,46)(113,47)(113,48)(113,49)(113,50)(113,51)(113,52)(113,53)(113,54)(113,55)(113,56); nodeList: [<(113,34)><(113,57)>]pixelList: (99,124)(98,124)(97,123)(96,123)(95,122)(94,122)(93,121)(92,120)(91,120)(90,119)(89,119)(88,118)(87,118)(86,117)(85,116)(84,116)(83,115)(82,115)(81,114)(80,113)(79,113)(78,112); nodeList: [<(99,124)><(77,112)>]pixelList: (45,154)(45,153)(45,152)(45,151)(45,150)(45,149)(45,148)(45,147)(45,146)(45,145)(45,144)(45,143)(45,142)(45,141)(45,140)(45,139)(45,138)(45,137)(45,136)(45,135)(45,134)(45,133)(45,132)(45,131); nodeList: [<(45,154)><(45,130)>]pixelList: (238,57)(237,57)(236,58)(235,58)(234,59)(233,59)(232,60)(231,61)(230,61)(229,62)(228,62)(227,63)(226,64)(225,64)(224,65)(223,65)(222,66)(221,66)(220,67)(219,68)(218,68)(217,69)(216,69)(215,70)(214,70)(213,71)(212,72)(211,72)(210,73)(209,73)(208,74)(207,74)(206,74)(205,74)(204,73)(203,72)(202,72)(201,71)(200,71)(199,70)(198,69)(197,69)(196,68)(195,68)(194,67)(193,67)(192,66)(191,65)(190,65)(189,64)(188,64)(187,63)(186,63)(185,62)(184,61)(183,61)(182,60)(181,60)(180,59)(179,58)(178,58)(177,57)(176,57)(175,56)(174,56)(173,57)(172,57)(171,58)(170,59)(169,59)(168,60)(167,60)(166,61)(165,61)(164,62)(163,63)(162,63)(161,64)(160,64)(159,65)(158,66)(157,66)(156,67)(155,67)(154,68)(153,68)(152,69)(151,70)(150,70)(149,71)(148,71)(147,72)(146,73)(145,73)(144,74)(143,74)(142,74)(141,74)(140,73)(139,73)(138,72)(137,72)(136,71)(135,70)(134,70)(133,69)(132,69)(131,68)(130,68)(129,67)(128,66)(127,66)(126,65)(125,65)(124,64)(123,63)(122,63)(121,62)(120,62)(119,61)(118,61)(117,60)(116,59)(115,59)(114,58)(113,57); nodeList: [<(238,57)><(113,57)>]pixelList: (46,56)(47,57)(48,57)(49,58)(50,59)(51,59)(52,60)(53,60)(54,61)(55,62)(56,62)(57,63)(58,63)(59,64)(60,64)(61,65)(62,66)(63,66)(64,67)(65,67)(66,68)(67,68)(68,69)(69,70)(70,70)(71,71)(72,71)(73,72)(74,73)(75,73)(76,74); nodeList: [<(45,56)><(77,74)>]pixelList: (45,56)(44,57)(43,57)(42,58)(41,58)(40,59)(39,60)(38,60)(37,61)(36,61)(35,62)(34,63)(33,63)(32,64)(31,64)(30,65)(29,65)(28,66)(27,67)(26,67)(25,68)(24,68)(23,69)(22,69)(21,70)(20,71)(19,71)(18,72)(17,72)(16,73)(15,74)(14,74)(13,75)(13,76)(13,77)(13,78)(13,79)(13,80)(13,81)(13,82)(13,83)(13,84)(13,85)(13,86)(13,87)(13,88)(13,89)(13,90)(13,91)(13,92)(13,93)(13,94)(13,95)(13,96)(13,97)(13,98)(13,99)(13,100)(13,101)(13,102)(13,103)(13,104)(13,105)(13,106)(13,107)(13,108)(13,109)(13,110)(13,111)(13,112)(14,113)(15,113)(16,114)(17,114)(18,115)(19,116)(20,116)(21,117)(22,117)(23,118)(24,118)(25,119)(26,120)(27,120)(28,121)(29,121)(30,122)(31,122)(32,123)(33,124)(34,124)(35,125)(36,125)(37,126)(38,126)(39,127)(40,128)(41,128)(42,129)(43,129)(44,130); nodeList: [<(45,56)><(45,130)>]pixelList: (77,75)(77,76)(77,77)(77,78)(77,79)(77,80)(77,81)(77,82)(77,83)(77,84)(77,85)(77,86)(77,87)(77,88)(77,89)(77,90)(77,91)(77,92)(77,93)(77,94)(77,95)(77,96)(77,97)(77,98)(77,99)(77,100)(77,101)(77,102)(77,103)(77,104)(77,105)(77,106)(77,107)(77,108)(77,109)(77,110)(77,111); nodeList: [<(77,74)><(77,112)>]pixelList: (78,74)(79,74)(80,73)(81,73)(82,72)(83,72)(84,71)(85,70)(86,70)(87,69)(88,69)(89,68)(90,68)(91,67)(92,66)(93,66)(94,65)(95,65)(96,64)(97,63)(98,63)(99,62)(100,62)(101,61)(102,61)(103,60)(104,59)(105,59)(106,58); nodeList: [<(77,74)><(106,57)>]pixelList: (107,57)(108,57)(109,57)(110,56)(111,57)(112,57); nodeList: [<(106,57)><(113,57)>]pixelList: (77,112)(76,113)(75,114)(74,114)(73,115)(72,115)(71,116)(70,117)(69,117)(68,118)(67,118)(66,119)(65,119)(64,120)(63,121)(62,121)(61,122)(60,122)(59,123)(58,123)(57,124)(56,125)(55,125)(54,126)(53,126)(52,127)(51,127)(50,128)(49,129)(48,129)(47,130)(46,130); nodeList: [<(77,112)><(45,130)>]";
		Assert.assertEquals("edges", ""
				+ pixelList0,
				factory.getEdgeList().toString());

		// edges without nodes
		int[] edgeCounts = new int[] {
				12,1,1,1,1
		};
		int[] nodeCounts = new int[] {
				12,2,2,2,2
		};
		String[] edges = {
				pixelList0,
				"pixelList: (20,78)(20,79)(19,80)(19,81)(19,82)(19,83)(19,84)(19,85)(19,86)(19,87)(19,88)(19,89)(19,90)(19,91)(19,92)(19,93)(19,94)(19,95)(19,96)(19,97)(19,98)(19,99)(19,100)(19,101)(19,102)(19,103)(19,104)(19,105)(19,106)(19,107); nodeList: [<(20,78)><(19,107)>]",
				"pixelList: (179,51)(180,51)(181,52)(182,53)(183,53)(184,54)(185,54)(186,55)(187,55)(188,56)(189,57)(190,57)(191,58)(192,58)(193,59)(194,60)(195,60)(196,61)(197,61)(198,62)(199,62)(200,63)(201,64)(202,64)(203,65)(204,65)(205,66)(206,66); nodeList: [<(179,51)><(206,66)>]",
				"pixelList: (70,109)(69,109)(68,110)(67,111)(66,111)(65,112)(64,112)(63,113)(62,113)(61,114)(60,115)(59,115)(58,116)(57,116)(56,117)(55,117)(54,118)(53,119)(52,119)(51,120)(50,120)(49,121)(48,121)(47,122)(46,123)(45,123); nodeList: [<(70,109)><(45,123)>]",
				"pixelList: (45,64)(46,64)(47,65)(48,65)(49,66)(50,66)(51,67)(52,68)(53,68)(54,69)(55,69)(56,70)(57,70)(58,71)(59,72)(60,72)(61,73)(62,73)(63,74)(64,74)(65,75)(66,76)(67,76)(68,77)(69,77)(70,78); nodeList: [<(45,64)><(70,78)>]",
				};

		Assert.assertEquals("pixelIslandList.size", 5, superThinnedIslandList.size());
		for (int i = 0; i < superThinnedIslandList.size(); i++) {
			PixelIsland island = superThinnedIslandList.get(i);
			SVGSVG.wrapAndWriteAsSVG(largest.getOrCreateSVGG(), new File("target/examples/maltoryzineIsland"+i+".svg"));
			factory = new PixelNucleusFactory(island);
		    factory.createNodesAndEdges();
			Assert.assertEquals("edgeListSize "+i, edgeCounts[i], factory.getEdgeList().size());
			Assert.assertEquals("nodeListSize "+i, nodeCounts[i], factory.getOrCreateNodeListFromNuclei().size());
			Assert.assertEquals("edges "+i, edges[i], factory.getEdgeList().toString());
		}
		
	}
	
	
	

	@Test
	/** extracts SVGLines from pixels of molecule.
	 * 
	 * SHOWCASE of most functionality
	 * 
	 */
	public void testEverything() {
		String TITLE = "maltoryzine";
		double tolerance = 2.0; //this takes care of the bends
		/* this section is mainly visual */
		// no thinning
		ImageProcessor imageProcessor = new ImageProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.readAndProcessFile(ImageAnalysisFixtures.MALTORYZINE_BINARY_PNG);
		ImageIOUtil.writeImageQuietly(imageProcessor.getImage(), new File(EXAMPLE_DIR, TITLE+".raw.png"));
		// thinning
		imageProcessor = new ImageProcessor();
		imageProcessor.setThinning(new ZhangSuenThinning());
		imageProcessor.readAndProcessFile(ImageAnalysisFixtures.MALTORYZINE_BINARY_PNG);
		ImageIOUtil.writeImageQuietly(imageProcessor.getImage(), new File(EXAMPLE_DIR, TITLE+".zsThinned.png"));
		PixelIslandList islandList = imageProcessor.getOrCreatePixelIslandList();
		SVGSVG.wrapAndWriteAsSVG(islandList.getOrCreateSVGG(), new File(EXAMPLE_DIR, TITLE+".zsThinned.svg"));
		// super thinning
		imageProcessor = new ImageProcessor();
		
//		imageProcessor.setThinning(null);
		BufferedImage image = DEFAULT_PROCESSOR.processImageFile(ImageAnalysisFixtures.MALTORYZINE_BINARY_PNG);
		PixelIslandList superThinnedIslandList = PixelIslandList.createSuperThinnedPixelIslandList(image);
		SVGSVG.wrapAndWriteAsSVG(superThinnedIslandList.getOrCreateSVGG(), new File(EXAMPLE_DIR, TITLE+".superThinned.svg"));
		
		islandList.sortBySizeDescending();
		Assert.assertEquals("largest island pixels", 476, islandList.get(0).size());
		int[] islandEdges = new int[] {12,1,1,1,1};
		int[][] segmentCounts = new int[][] {
			new int[] {1,1,1,1,1,4,1,3,1,1,1,1},
			new int[]{1},
			new int[]{1},
			new int[]{1},
			new int[]{1},
		};
		int[][] childCounts = new int[][] {
			new int[] {1,1,1,1,1,4,1,3,1,1,1,1},
			new int[]{1},
			new int[]{1},
			new int[]{1},
			new int[]{1},
		};
		SVGLine[][][] lines = new SVGLine[][][] {
			new SVGLine[][] {
				new SVGLine[] {new SVGLine(new Real2(22.5,17.0),new Real2(22.5,27.5))},
				 new SVGLine[] {new SVGLine(new Real2(53.0,17.0),new Real2(53.5,28.5))},
				 new SVGLine[] {new SVGLine(new Real2(56.5,17.0),new Real2(56.5,28.0))},
				 new SVGLine[] {new SVGLine(new Real2(49.5,62.0),new Real2(39.0,56.0))},
				 new SVGLine[] {new SVGLine(new Real2(22.5,77.0),new Real2(22.5,65.5))},
				 new SVGLine[] {
					new SVGLine(new Real2(119.0,28.5),new Real2(104.0,37.0)),
					new SVGLine(new Real2(104.0,37.0),new Real2(87.5,28.0)),
					new SVGLine(new Real2(87.5,28.0),new Real2(72.0,37.0)),
					new SVGLine(new Real2(72.0,37.0),new Real2(56.5,28.5))
				 },
				 new SVGLine[] {new SVGLine(new Real2(23.0,28.0),new Real2(38.5,37.5))},
				 new SVGLine[] {
				new SVGLine(new Real2(22.5,28.0),new Real2(6.5,37.5)),
				new SVGLine(new Real2(6.5,37.5),new Real2(6.5,56.0)),
				new SVGLine(new Real2(6.5,56.0),new Real2(22.0,65.0))
				 },
				 new SVGLine[] {new SVGLine(new Real2(38.5,37.5),new Real2(38.5,55.5))},
				 new SVGLine[] {new SVGLine(new Real2(39.0,37.0),new Real2(53.0,29.0))},
				new SVGLine[] {new SVGLine(new Real2(53.5,28.5),new Real2(56.0,28.5))},
				new SVGLine[] {new SVGLine(new Real2(38.5,56.0),new Real2(23.0,65.0))},
			},
			new SVGLine[][] {
				new SVGLine[] {new SVGLine(new Real2(10.0,39.0),new Real2(9.5,53.5))},
			},
			new SVGLine[][] {
				new SVGLine[] {new SVGLine(new Real2(89.5,25.5),new Real2(103.0,33.0))}
			},
			new SVGLine[][] {
				new SVGLine[] {new SVGLine(new Real2(35.0,54.5),new Real2(22.5,61.5))}
			},
			new SVGLine[][] {
				new SVGLine[] {new SVGLine(new Real2(22.5,32.0),new Real2(35.0,39.0))},
			}
		};
		Real2Array[][] polylines = {
				new Real2Array[] {
			Real2Array.createFromPairs("45.0 34.0 45.0 55.0", " "),
			Real2Array.createFromPairs("106.0 34.0 107.0 57.0", " "),
			Real2Array.createFromPairs("113.0 34.0 113.0 56.0", " "),
			Real2Array.createFromPairs("99.0 124.0 78.0 112.0", " "),
			Real2Array.createFromPairs("45.0 154.0 45.0 131.0", " "),
			Real2Array.createFromPairs("238.0 57.0 208.0 74.0 175.0 56.0 144.0 74.0 113.0 57.0", " "),
			Real2Array.createFromPairs("46.0 56.0 77.0 75.0", " "),
			Real2Array.createFromPairs("45.0 56.0 13.0 75.0 13.0 112.0 45.0 131.0", " "),
			Real2Array.createFromPairs("77.0 75.0 77.0 111.0", " "),
			Real2Array.createFromPairs("78.0 74.0 106.0 58.0", " "),
			Real2Array.createFromPairs("107.0 57.0 112.0 57.0", " "),
			Real2Array.createFromPairs("77.0 112.0 46.0 130.0", " "),
				},
			new Real2Array[] {
				Real2Array.createFromPairs("20.0 78.0 19.0 107.0", " "),
			},
			new Real2Array[] {
					Real2Array.createFromPairs("179.0 51.0 206.0 66.0", " "),
			},
			new Real2Array[] {
			Real2Array.createFromPairs("70.0 109.0 45.0 123.0", " "),
			},
			new Real2Array[] {
			Real2Array.createFromPairs("45.0 64.0 70.0 78.0", " "),
			},
		};
		double[][][] distances = {
			new double[][] {
				new double[] {21.0},
				new double[] {23.0},
				new double[] {22.0},
				new double[] {24.2},
				new double[] {23.0},
				new double[] {34.5,37.6,35.8,35.4},
				new double[] {36.4},
				new double[] {37.2,37.0,37.2},
				new double[] {36.0},
				new double[] {35.0},
				new double[] {5.0},
				new double[] {37.2},
			},
			
			new double[][] {new double[] {29.0}},
			new double[][] {new double[] {30.9}},
			new double[][] {new double[] {28.7}},
			new double[][] {new double[] {28.7}},
		};
		double[][][] angles = {
				new double[][] {
					new double[] {},
					new double[] {},
					new double[] {},
					new double[] {},
					new double[] {},
					new double[] {-1.015,1.025,-1.028},
					new double[] {},
					new double[] {1.035,1.035},
					new double[] {},
					new double[] {},
					new double[] {},
					new double[] {},
				},
				
				new double[][] {new double[] {}},
				new double[][] {new double[] {}},
				new double[][] {new double[] {}},
				new double[][] {new double[] {}},
		};

		
		SVGG svgg = new SVGG();
		Iterator<String> iterator = ColorStore.getColorIterator(ColorizerType.CONTRAST);
		double deltaCoord = 2.0; // large because of indeterminacy
		for (int is = 0; is < islandList.size(); is++) {
			PixelIsland island = islandList.get(is);
			PixelNucleusFactory factory = new PixelNucleusFactory(island);
			PixelEdgeList edgeList = factory.getEdgeList();
			Assert.assertEquals(islandEdges[is], edgeList.size());
			for (int iseg = 0; iseg < edgeList.size(); iseg++) {
				PixelEdge edge = edgeList.get(iseg);
				PixelSegmentList segmentList = PixelSegmentList.createSegmentList(edge.getPixelList(), tolerance);
				SVGPolyline polyline = segmentList.getOrCreatePolyline();
				Real2Array xy = polyline.getReal2Array();
				Real2Array xyExp = polylines[is][iseg];
				Assert.assertTrue("naked polyline "+is+"/"+iseg+": "+xyExp+"!="+xy, xyExp.isEqualTo(xy, deltaCoord));
				polyline.setCSSStyle("fill:none;stroke:"+iterator.next()+";stroke-width:2.0;");
				svgg.appendChild(polyline);
				
				// check distances and angles
				checkDistancesAndAngles(distances[is][iseg], angles[is][iseg], is, iseg, polyline);
				Assert.assertEquals(segmentCounts[is][iseg], segmentList.size());
				SVGG g = segmentList.getSVGG();
				int childCount = g.getChildCount();
				Assert.assertEquals("childcount "+is+";"+iseg, childCounts[is][iseg], childCount);
				for (int ichild = 0; ichild < childCount; ichild++) {
					SVGLine line = (SVGLine) g.getChild(ichild);
					line.setTransform(Transform2.applyScale(0.5)); // no idea why
					line.applyTransformAttributeAndRemove();
					SVGLine expLine = lines[is][iseg][ichild];
					Assert.assertTrue(""+is+","+iseg+","+ichild+"x", expLine.getXY(0).isEqualTo(line.getXY(0), deltaCoord));
					Assert.assertTrue(""+is+","+iseg+","+ichild+"y", expLine.getXY(1).isEqualTo(line.getXY(1), deltaCoord));
				}
				svgg.appendChild(g);
			}
		}
		SVGSVG.wrapAndWriteAsSVG(svgg, new File("target/examples/maltoryzine.svg"));
	}

	private void checkDistancesAndAngles(double[] distances, double[] angles, int is, int iseg, SVGPolyline polyline) {
		// note deltas can be large due to indeterminate parsing of pixels
		RealArray obsDistanceArray = polyline.getDistances();
		obsDistanceArray = obsDistanceArray.format(1);
		
		TestUtils.assertEquals("distances ["+is+"]["+iseg+"]", new RealArray(distances), obsDistanceArray, 1.0);
		RealArray obsAngleArray = polyline.getAngles();
		obsAngleArray = obsAngleArray.format(3);
		TestUtils.assertEquals("angles ["+is+"]["+iseg+"]", new RealArray(angles), obsAngleArray, 1.0);
//		String s =  ""+distances+""+angles;
//		Assert.assertEquals("distangl ["+is+"]["+iseg+"]", distangles[is][iseg], s);
	}
	
	/** simple phylotree. Here we are just counting nodes and edges.
	 * 
	 * PARTIAL SHOWCASE
	 * 
	 */
	@Test
	public void testExtractPhyloTree() {
		BufferedImage image = DEFAULT_PROCESSOR.processImageFile(ImageAnalysisFixtures.PHYLO_14811_2_PNG);
		PixelIslandList islandList = PixelIslandList.createSuperThinnedPixelIslandList(image);
		islandList.sortBySizeDescending();
		PixelNucleusFactory factory = new PixelNucleusFactory(islandList.get(0));
		PixelEdgeList edgeList = factory.getEdgeList();
		Assert.assertEquals("edges", 17, edgeList.size());
		Assert.assertEquals("nodes", 18, factory.getOrCreateNodeListFromNuclei().size());
		SVGSVG.wrapAndWriteAsSVG(edgeList.getOrCreateSVG(), new File("target/phylo/14811_2.svg"));
	}
	
}
