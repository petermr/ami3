package org.contentmine.graphics.svg.plot.forest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
	public static final Logger LOG = LogManager.getLogger(ForestTest.class);
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
	// needs debugging
	// I don't think this is a suitable bitmap to start with - maybe got swapped in 
	public void testForestPlotBitmap() {
		File forestFile = new File(ImageAnalysisFixtures.FOREST_DIR, "forest_plot.png");
		Assert.assertTrue("forestFile "+forestFile, forestFile.exists());
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setMaxIsland(300);
		diagramAnalyzer.getOrCreateGraphList(forestFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals("islands",  187, pixelIslandList.size());
		SVGG g = new SVGG();
		// pixels
		// test cut to first 3 islands
		Iterator<String> iterator = ColorStore.getColorIterator(ColorizerType.CONTRAST);
		LOG.debug("PIXELIL "+pixelIslandList);
		int[] sizes = new int[] {6396,181,144};
		int[] nodeCounts = new int[] {1,2,4};
		int[] edgeCounts = new int[] {23,2,3};
		/* 187[6396; ((353,889),(33,623))]
		[1907; ((590,697),(390,442))]
				[1686; ((610,745),(319,355))]
				[692; ((605,683),(495,576))]
				[371; ((988,1013),(15,43))]
				[360; ((1019,1039),(16,43))]
				[236; ((601,624),(687,714))][224; ((173,197),(529,546))][224; ((83,107),(529,546))][224; ((52,76),(529,546))][214; ((632,649),(687,714))][205; ((793,811),(633,660))][205; ((631,649),(633,660))][205; ((470,488),(633,660))][190; ((1020,1034),(523,546))][182; ((1088,1104),(523,546))][170; ((598,614),(633,660))][172; ((1060,1074),(523,546))][172; ((951,965),(523,546))][172; ((922,936),(523,546))][164; ((760,776),(633,660))][154; ((14,30),(328,351))][150; ((30,53),(259,276))][150; ((30,53),(115,132))][152; ((13,26),(523,546))][152; ((1019,1033),(397,420))][152; ((951,965),(253,276))][150; ((145,160),(529,552))][147; ((257,271),(529,546))][147; ((32,46),(529,546))][150; ((36,50),(334,357))][145; ((222,235),(529,546))][145; ((113,126),(529,546))][143; ((290,304),(529,546))][143; ((203,217),(529,546))][138; ((438,453),(633,660))][141; ((1086,1100),(110,133))][140; ((222,236),(399,422))][140; ((1019,1033),(325,348))][140; ((1019,1033),(253,276))][140; ((253,267),(253,276))][140; ((234,248),(253,276))][140; ((215,229),(253,276))][140; ((233,247),(182,205))][140; ((214,228),(182,205))][140; ((234,248),(109,132))][140; ((215,229),(109,132))][138; ((203,218),(399,422))][138; ((184,199),(399,422))][138; ((187,202),(328,351))][138; ((168,183),(328,351))][138; ((990,1005),(253,276))][138; ((1019,1034),(184,207))][138; ((990,1005),(110,133))][132; ((992,1005),(523,546))][124; ((32,45),(398,422))][124; ((82,95),(252,276))][124; ((82,95),(108,132))][119; ((1087,1100),(397,420))][119; ((1020,1033),(110,133))][118; ((1108,1114),(521,552))][120; ((26,41),(188,205))][116; ((12,25),(253,276))][116; ((12,25),(109,132))][114; ((951,965),(325,348))][114; ((1057,1071),(253,276))][114; ((1057,1071),(184,207))][114; ((252,266),(182,205))][114; ((951,965),(110,133))][113; ((979,985),(521,552))][110; ((205,220),(328,350))][110; ((1085,1100),(184,206))][113; ((11,27),(399,422))][112; ((165,178),(399,422))][112; ((1057,1070),(397,420))][112; ((922,935),(397,420))][112; ((149,162),(328,351))][112; ((1057,1070),(325,348))][112; ((922,935),(325,348))][112; ((1086,1099),(253,276))][112; ((922,935),(184,207))][112; ((1057,1070),(110,133))][106; ((120,132),(405,422))][106; ((103,115),(334,351))][106; ((150,162),(259,276))][106; ((149,161),(188,205))][106; ((150,162),(115,132))][104; ((241,252),(529,546))][98; ((52,65),(406,422))][101; ((79,93),(405,422))][98; ((992,1003),(397,420))][98; ((953,964),(397,420))][101; ((63,77),(334,351))][98; ((992,1003),(325,348))][101; ((110,124),(259,276))][98; ((924,935),(253,276))][98; ((198,209),(253,276))][101; ((109,123),(188,205))][101; ((66,80),(188,205))][98; ((47,60),(188,205))][98; ((992,1003),(184,207))][98; ((953,964),(184,207))][98; ((197,208),(182,205))][101; ((110,124),(115,132))][98; ((924,935),(110,133))][98; ((255,266),(109,132))][98; ((198,209),(109,132))][90; ((1087,1100),(325,348))][85; ((277,286),(529,546))][85; ((133,142),(529,546))][76; ((85,95),(188,205))][74; ((13,20),(182,205))][73; ((1106,1111),(396,425))][76; ((979,984),(395,426))][73; ((1106,1111),(324,353))][76; ((979,984),(323,354))][73; ((1106,1111),(252,281))][76; ((979,984),(251,282))][73; ((1106,1111),(183,212))][76; ((979,984),(182,213))][73; ((1106,1111),(109,138))][76; ((979,984),(108,139))][71; ((139,141),(398,422))][71; ((122,124),(327,351))][71; ((169,171),(252,276))][71; ((168,170),(181,205))][71; ((169,171),(108,132))][63; ((98,105),(402,422))][63; ((82,89),(331,351))][63; ((129,136),(256,276))][63; ((68,75),(256,276))][63; ((128,135),(185,205))][63; ((129,136),(112,132))][63; ((68,75),(112,132))][47; ((60,62),(260,276))][47; ((60,62),(116,132))][32; ((643,645),(579,590))][32; ((1038,1043),(542,551))][29; ((643,645),(474,484))][29; ((643,645),(453,463))][29; ((643,645),(369,379))][29; ((643,645),(348,358))][29; ((643,645),(306,316))][29; ((643,645),(285,295))][29; ((643,645),(243,253))][29; ((643,645),(222,232))][29; ((643,645),(201,211))][29; ((643,645),(180,190))][29; ((643,645),(159,169))][29; ((643,645),(138,148))][29; ((643,645),(96,106))][29; ((643,645),(75,85))][29; ((643,645),(54,64))][29; ((643,645),(33,43))][20; ((1037,1042),(417,425))][20; ((1037,1042),(345,353))][20; ((1037,1042),(273,281))][20; ((1037,1042),(204,212))][20; ((1037,1042),(130,138))][16; ((1080,1083),(542,546))][16; ((1011,1014),(542,546))][16; ((942,945),(542,546))][16; ((784,787),(656,660))][16; ((622,625),(656,660))][16; ((461,464),(656,660))][8; ((148,150),(419,422))][8; ((1077,1079),(417,420))][8; ((1010,1012),(417,420))][8; ((942,944),(417,420))][8; ((131,133),(348,351))][8; ((1077,1079),(345,348))][8; ((1010,1012),(345,348))][8; ((942,944),(345,348))][8; ((1077,1079),(273,276))][8; ((1010,1012),(273,276))][8; ((942,944),(273,276))][8; ((178,180),(273,276))][8; ((1077,1079),(204,207))][8; ((1010,1012),(204,207))][8; ((942,944),(204,207))][8; ((177,179),(202,205))][8; ((1077,1079),(130,133))][8; ((1010,1012),(130,133))][8; ((942,944),(130,133))][8; ((178,180),(129,132))][5; ((60,62),(253,255))][5; ((60,62),(109,111))]
*/
		Boolean[] cyclic = new Boolean[] {false,false,false/*,true,true,true,true,true,true,true,true,true,true,true*/};
		for (int isl = 0; isl < /*pixelIslandList.size()*/cyclic.length; isl++) {
			PixelIsland island = pixelIslandList.get(isl);
			PixelGraph graph = new PixelGraph(island);
			System.out.println("is "+island.size()+ "/ nodes" + graph.getOrCreateNodeList().size() + "/edges "+  graph.getOrCreateEdgeList().size());
/*
 * 			Assert.assertEquals("island", sizes[isl], island.size());
			Assert.assertEquals("nodes", nodeCounts[isl], graph.getOrCreateNodeList().size());
			Assert.assertEquals("edges", edgeCounts[isl], graph.getOrCreateEdgeList().size());
			*/
			graph.doEdgeSegmentation();
//			Assert.assertEquals("cyclic "+isl, cyclic[isl], graph.isSingleCycle());
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
