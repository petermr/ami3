package org.contentmine.image.pixel;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.ImageUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class PixelIslandTest {

	private final static Logger LOG = Logger.getLogger(PixelIslandTest.class);

	private static final int MAX_PIXEL_ITER = 20;
	private static final double DP_EPSILON = 1.5;
	private final static double EPS = 0.5;
	public static String COLOUR[] = { "red", "blue", "green", "yellow", "cyan",
			"magenta" };

	@Test
	@Ignore
	// non-deterministic?
	public void testAddPixel() {
		PixelList longTList = ImageAnalysisFixtures.LONG_T_LIST;
		PixelIsland island = new PixelIsland(longTList);
		PixelList n0 = longTList.get(0).getOrCreateNeighbours(island);
		Assert.assertEquals("0", 1, n0.size());
		PixelList n1 = longTList.get(1).getOrCreateNeighbours(island);
		Assert.assertEquals("1", 2, n1.size());
		PixelList n2 = longTList.get(2).getOrCreateNeighbours(island);
		Assert.assertEquals("2", 3, n2.size());
		PixelList n3 = longTList.get(3).getOrCreateNeighbours(island);
		Assert.assertEquals("3", 1, n3.size());
		PixelList n4 = longTList.get(4).getOrCreateNeighbours(island);
		Assert.assertEquals("3", 1, n4.size());
	}

	/**
	 * Tests the pixels below.
	 * 
	 * X is right Y is down
	 * 
	 * + + ++ +
	 */
	@Test
	@Ignore
	// non-deterministic?
	public void testAddPixelWithDiagonal() {
		boolean diagonal = true;
		PixelList longTList = ImageAnalysisFixtures.LONG_T_LIST;
		PixelIsland island = new PixelIsland(longTList, diagonal);
		PixelList n0 = longTList.get(0).getOrCreateNeighbours(island);
		Assert.assertEquals("0", 1, n0.size());
		PixelList n1 = longTList.get(1).getOrCreateNeighbours(island);
		Assert.assertEquals("1", 2, n1.size());
		PixelList n2 = longTList.get(2).getOrCreateNeighbours(island);
		Assert.assertEquals("2", 3, n2.size());
		PixelList n3 = longTList.get(3).getOrCreateNeighbours(island);
		Assert.assertEquals("3", 1, n3.size());
		PixelList n4 = longTList.get(4).getOrCreateNeighbours(island);
		Assert.assertEquals("4", 1, n4.size());
	}

	@Test
	public void testgetTerminalPixels() {
		PixelList lineList = ImageAnalysisFixtures.LINE_LIST;
		PixelIsland island = new PixelIsland(lineList);
		PixelList terminalPixels = island.getTerminalPixels();
		Assert.assertEquals("terminal", 2, terminalPixels.size());
	}

	@Test
	public void testgetTerminalPixelsL() {
		PixelList lList = ImageAnalysisFixtures.L_LIST;
		PixelIsland island = new PixelIsland(lList);
		Assert.assertNotNull("island", island);
		PixelList terminalPixels = island.getTerminalPixels();
		Assert.assertEquals("terminal", 2, terminalPixels.size());
	}

	@Test
	public void testgetTerminalPixelsT() {
		PixelList tList = ImageAnalysisFixtures.T_LIST;
		PixelIsland island = new PixelIsland(tList);
		PixelList terminalPixels = island.getTerminalPixels();
		Assert.assertEquals("terminal", 3, terminalPixels.size());
		Assert.assertEquals("0", "(1,1)", terminalPixels.get(0).getInt2()
				.toString());
		Assert.assertEquals("0", "(1,5)", terminalPixels.get(1).getInt2()
				.toString());
		Assert.assertEquals("0", "(3,3)", terminalPixels.get(2).getInt2()
				.toString());
	}

	@Test
	public void testgetTerminalMaltoryzine1() throws Exception {
		BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		FloodFill floodFill = new ImageFloodFill(image);
		floodFill.setDiagonal(true);
		floodFill.fillIslands();
		PixelIsland island = floodFill.getIslandList().get(1);
		Assert.assertEquals("size", 33, island.size());
		// island.cleanChains();
		// Assert.assertEquals("size", 28, island.size());
	}

	@Test
	public void testgetTerminalMaltoryzine0() throws Exception {
		BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		FloodFill floodFill = new ImageFloodFill(image);
		floodFill.setDiagonal(true);
		floodFill.fillIslands();
		PixelIsland island = floodFill.getIslandList().get(0);
		Assert.assertEquals("size", 492, island.size());
		// island.cleanChains();
		// Assert.assertEquals("size", 478, island.size());
	}

//	@Test
//	@Ignore
//	public void testCreatePixelIslandsAndSegments() throws IOException {
//		BufferedImage image = ImageUtil.readImage(Fixtures.MALTORYZINE_THINNED_PNG);
//		FloodFill floodFill = new FloodFill(image);
//		floodFill.setDiagonal(true);
//		floodFill.fill();
//		PixelIslandList islandList = floodFill.getPixelIslandList();
//		Assert.assertEquals("islands", 5, islandList.size());
//		int[] islandsize = { 492, 33, 25, 29, 25 };
//		int[] terminals = { 6, 2, 2, 2, 2 };
//		int[] count2 = { 409, 12, 23, 27, 23 };
//		int[] count3 = { 74, 18, 0, 0, 0 };
//		int[] count4 = { 3, 1, 0, 0, 0 };
//		int[] count5 = { 0, 0, 0, 0, 0 };
//		checkCounts(islandList, islandsize, terminals, count2, count3, count4,
//				count5);
//
//		drawSVG(islandList.get(0), "target/segments/maltoryzine0.svg");
//		drawSVG(islandList.get(1), "target/segments/maltoryzine1.svg");
//		drawSVG(islandList.get(2), "target/segments/maltoryzine2.svg");
//
////		// doesn't seem to do much
////		islandList.get(0).flattenNuclei();
////		islandList.get(1).flattenNuclei();
////		islandList.get(2).flattenNuclei();
////		islandList.get(3).flattenNuclei();
////		islandList.get(4).flattenNuclei();
//
//		checkCounts(islandList, islandsize, terminals, count2, count3, count4,
//				count5);
//
//		PixelIsland island1 = islandList.get(1);
//		PixelList pixelList1 = island1.getPixelList();
//		// for (Pixel pixel : pixelList1) {
//		// LOG.trace("pixel "+pixel.getInt2());
//		// }
//		Pixel pixel32 = pixelList1.get(32);
//		Assert.assertEquals("pixel32", new Int2(206, 66), pixel32.getInt2());
//
//		// obsolete? island1.createSpanningTree(pixel32);
//
//		SVGG gg = new SVGG();
//		int islandj = 0;
//		double dpEpsilon = 2.0;// 0.99 1.5
//		int maxiter = 20;
//		for (PixelIsland island : islandList) {
//			SVGG g = new SVGG();
//			List<SVGPolyline> polylineList = island.createPolylinesIteratively(
//					dpEpsilon, maxiter);
//
//			colourPolylinesAndAddToG(COLOUR, g, polylineList);
//
//			gg.appendChild(g);
//			SVGG islandG = island.plotPixels();
//			SVGSVG.wrapAndWriteAsSVG((SVGG) islandG.copy(), new File(
//					"target/segments/island" + (++islandj) + ".svg"));
//			gg.appendChild(islandG);
//		}
//		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/segments/segments.svg"));
//
//	}

//	private void colourPolylinesAndAddToG(String[] colour, SVGG g,
//			List<SVGPolyline> polylineList) {
//		int i = 0;
//		for (SVGPolyline polyline : polylineList) {
//			polyline.setStrokeWidth(0.5);
//			polyline.setStroke(colour[i]);
//			polyline.setFill("none");
//			g.appendChild(polyline);
//			i = (i + 1) % colour.length;
//			LOG.trace("col " + i);
//		}
//	}

//	@Test
//	@Ignore
//	public void testAnalyzeCharA() throws Exception {
//		SVGG gg = new SVGG();
//		double x = 0;
//		double y = 0;
//		double deltax = 50;
//		double deltay = 50;
//		double xmax = 501;
//		for (int i = 33; i < 64 + 26; i++) {
//			x += deltax;
//			if (x > xmax) {
//				x = 0;
//				y += deltay;
//			}
//			Transform2 t2 = new Transform2(new Vector2(x, y));
//			BufferedImage image0 = createImage("src/main/resources/org/contentmine/image/text/fonts/helvetica/"
//					+ i + ".png");
//			if (image0 == null) {
//				continue;
//			}
//
//			image0 = ImageProcessor.createDefaultProcessor().processImage(image0);
//			PixelProcessor pixelProcessor = new PixelProcessor(image0);
//			PixelIslandList islandList = pixelProcessor.getOrCreatePixelIslandList();
//			islandList.setPixelColor("blue");
//			SVGG g0 = islandList.plotPixels(t2);
//			g0.setOpacity(0.5);
//			gg.appendChild(g0);
////			ImageIOUtil.writeImageQuietly(islandList.getImage(), new File(
////					"target/charRecog/char" + i + ".thin.png"));
//			Assert.assertEquals("islands", 1, islandList.size());
//			List<List<SVGPolyline>> polylineListList = islandList
//					.createPolylinesIteratively(DP_EPSILON /* *0.5 *//* *2 */,
//							MAX_PIXEL_ITER);
//			SVGG g = new SVGG();
//			g.setTransform(t2);
//			for (List<SVGPolyline> polylineList : polylineListList) {
//				colourPolylinesAndAddToG(COLOUR, g, polylineList);
//			}
//			gg.appendChild(g);
//			islandList.setPixelColor("red");
//			SVGG g2 = islandList.plotPixels(t2);
//			g2.setOpacity(0.5);
//			gg.appendChild(g2);
//		}
//		File file = new File("target/charRecog/charAll.svg");
//		file.getParentFile().mkdirs();
//		SVGSVG.wrapAndWriteAsSVG(gg, file);
//	}

//	private BufferedImage createImage(String filename) {
//		BufferedImage image0 = null;
//		File file = null;
//		try {
//			file = new File(filename);
//			image0 = ImageIO.read(file);
//		} catch (IIOException ioe) {
//			// some are missing
//			if (file.exists()) {
//				LOG.debug("cannot create image, file exists: " + filename + ioe);
//			} else {
//				return null;
//			}
//		} catch (Exception e) {
//			LOG.debug("cannot create image: " + filename + e);
//			return image0;
//		}
//		image0 = ImageUtil.addBorders(image0, 1, 1, 0x00ffffff);
////		image0 = ImageUtil.binarize(image0);
//		return image0;
//	}

	// private SVGG plotPixels(Transform2 t2, PixelIslandList islandList, String
	// color) {
	// SVGG g0 = islandList.plotPixels(color);
	// if (t2 != null) g0.setTransform(t2);
	// return g0;
	// }


//	@Test
//	@Ignore
//	// file material deleted
//	public void testTrec() throws Exception {
//		File file = new File(
//				"src/test/resources/org/contentmine/image/trec/images/US06335364-20020101-C00020.TIF");
//		Assert.assertTrue(file.exists());
//		BufferedImage image0 = UtilImageIO
//				.loadImage("src/test/resources/org/contentmine/image/trec/images/US06335364-20020101-C00020.TIF");
//
//		// BufferedImage image0 = ImageUtil.readImage(new
//		// File("src/test/resources/org/contentmine/image/trec/images/US06335364-20020101-C00020.TIF"));
//		Assert.assertNotNull(image0);
//		ImageIOUtil.writeImageQuietly(image0, new File(
//				"target/segments/trec.thin.png"));
//		Thinning thinning = new HilditchThinning();
//		PixelIslandList islandList = PixelIslandList
//				.thinFillAndGetPixelIslandList(image0, thinning);
//		Assert.assertEquals("islands", 1, islandList.size());
//		PixelIsland island = islandList.get(0);
//		SVGG g = new SVGG();
//		List<SVGPolyline> polylineList = island.createPolylinesIteratively(
//				DP_EPSILON /* *2 */, MAX_PIXEL_ITER);
//
//		colourPolylinesAndAddToG(COLOUR, g, polylineList);
//
//		SVGSVG.wrapAndWriteAsSVG(g, new File("target/charRecog/trec.svg"));
//	}

//	private void drawSVG(PixelIsland island, String filename) {
//		SVGG svgg = island.createSVGFromPixelPaths(true);
//		File svgfile = new File(filename);
//		SVGSVG.wrapAndWriteAsSVG(svgg, svgfile);
//		Assert.assertTrue(svgfile.exists());
//	}

	private void checkCounts(PixelIslandList islandList, int[] islandsize,
			int[] terminals, int[] count2, int[] count3, int[] count4,
			int[] count5) {
		for (int i = 0; i < islandList.size(); i++) {

			PixelIsland island = islandList.get(i);
			Assert.assertEquals("island " + i, islandsize[i], island.size());
			Assert.assertEquals("terminal " + i, terminals[i], island
					.getTerminalPixels().size());
			Assert.assertEquals("2-nodes " + i, count2[i], island
					.getPixelsWithNeighbourCount(2).size());
			Assert.assertEquals("3-nodes " + i, count3[i], island
					.getPixelsWithNeighbourCount(3).size());
			Assert.assertEquals("4-nodes " + i, count4[i], island
					.getPixelsWithNeighbourCount(4).size());
			Assert.assertEquals("5-nodes " + i, count5[i], island
					.getPixelsWithNeighbourCount(5).size());
		}
	}

//	@Test
//	@Ignore
//	public void testCreateLinePixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.LINE_PNG);
//		List<PixelPath> pixelPaths = island.getOrCreatePixelPathList();
//		Assert.assertEquals("paths", 1, pixelPaths.size());
//		DouglasPeucker douglasPeucker = new DouglasPeucker(2.0);
//		List<Real2> reduced = douglasPeucker.reduce(pixelPaths.get(0)
//				.getPoints());
//		// LOG.debug(reduced);
//	}
//
//	@Test
//	@Ignore
//	public void testCreateZigzagPixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.ZIGZAG_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		Assert.assertEquals("segmentArray", 1, segmentArrayList.size());
//		island.debugSVG("target/pixelIsland/zigzag.svg");
//	}
//
//	@Test
//	@Ignore
//	public void testCreateHexagonPixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.HEXAGON_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		Assert.assertEquals("segmentArray", 1, segmentArrayList.size());
//		island.debugSVG("target/pixelIsland/hexagon.svg");
//	}
//
//	@Test
//	@Ignore
//	public void testCreateBranch0PixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.BRANCH0_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		Assert.assertEquals("segmentArray", 3, segmentArrayList.size());
//		island.debugSVG("target/pixelIsland/branch0.svg");
//	}
//
//	@Test
//	@Ignore
//	public void testCreateMaltoryzine0PixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.MALTORYZINE0_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		Assert.assertEquals("segmentArray", 6, segmentArrayList.size());
//		island.debugSVG("target/pixelIsland/maltoryzine0.svg");
//	}
//
//	@Test
//	@Ignore
//	public void testCreateMaltoryzinePixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.MALTORYZINE_THINNED_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		Assert.assertEquals("segmentArray", 6, segmentArrayList.size());
//		island.debugSVG("target/pixelIsland/maltoryzine.svg");
//	}
//
//	/**
//	 * this one has a terminal nucleus
//	 * 
//	 * [point: (50,59); neighbours: (51,59) (50,58) (49,58); marked:, point:
//	 * (50,58); neighbours: (49,58) (51,59) (50,59); marked:, point: (51,59);
//	 * neighbours: (50,59) (52,60) (50,58); marked:]
//	 * 
//	 * (49,58) is marked as a spike but not part of the nucleus.
//	 * 
//	 * also (52,60) and (53,60) Finally (54,61) is a terminal and is identified
//	 * as such.
//	 * 
//	 * The search starts at (54,61) and terminates correctly at (49,58)
//	 * 
//	 * @throws IOException
//	 */
//	@Test
//	@Ignore
//	public void testCreateTerminalPixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.TERMINAL_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		Assert.assertEquals("segmentArray", 1, segmentArrayList.size());
//		island.debugSVG("target/pixelIsland/terminalnode.svg");
//	}
//
//	/**
//	 * this one has 2 terminal nuclei
//	 * 
//	 * @throws IOException
//	 */
//	@Test
//	@Ignore
//	public void testCreateTerminalsPixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.TERMINALS_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		island.debugSVG("target/pixelIsland/terminalnodes.svg");
//		Assert.assertEquals("segmentArray", 1, segmentArrayList.size());
//	}
//
//	/**
//	 * this one has a terminal nucleus
//	 * 
//	 * @throws IOException
//	 */
//	@Test
//	@Ignore
//	public void testCreateBranchPixelPaths() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.BRANCH_PNG);
//		List<Real2Array> segmentArrayList = island.createSegments(EPS);
//		Assert.assertEquals("segmentArray", 3, segmentArrayList.size());
//		// debug(segmentArrayList);
//		island.debugSVG("target/pixelIsland/branch.svg");
//	}

//	/**
//	 * this one has a terminal nucleus
//	 * 
//	 * @throws IOException
//	 */
//	@Test
//	@Ignore
//	public void testDehypotenuse() throws IOException {
//		PixelIsland island = createFirstPixelIsland(Fixtures.TERMINAL_PNG);
//		island.removeHypotenuses();
//		List<Nucleus> nucleusList = island.getNucleusList();
//		LOG.trace("NUC " + nucleusList);
//	}

	@Test
	public void testBoundingBox2() throws IOException {
		PixelIsland island = createFirstPixelIsland(ImageAnalysisFixtures.MALTORYZINE0_PNG);
		Real2Range bbox = island.getBoundingBox();
		Assert.assertEquals("bbox", "((13.0,238.0),(34.0,154.0))",
				bbox.toString());
		// LOG.trace(bbox);;
	}

	@Test
	public void testBoundingBoxes() throws IOException {
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_PNG));
		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
		Assert.assertEquals("islands", 5, islands.size());
		for (PixelIsland island : islands) {
			Real2Range bbox = island.getBoundingBox();
			LOG.trace(island + " " + bbox);
		}
		Assert.assertEquals("island", "((12.0,240.0),(32.0,155.0))", islands
				.get(0).getBoundingBox().toString());
	}


	/** large JPG with small fonts. 
	 * 
	 * Extraction of island boxes with small heights (mainly characters, but some horizontal lines)
	 * 
	 * will be sensitive to background noise and thresholds
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore // counts don't work
	public void testLargePhyloJpgChars() throws IOException {
		int heightCount[] = new int[] { 107, 37, 7, 6, 23, 118, 488, 416, 203,
				333, // boxes 0-9
				112, 35, 8, 11, 12, 9, 5, 0, 2, 8, // 10-19
				1, 3, 2, 1, 1, 1 };
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessorAndProcess(ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG));
		PixelIslandList islands = imageProcessor.getOrCreatePixelIslandList();
		PixelIslandList characters = islands.isContainedIn(new RealRange(0.,
				25.), new RealRange(0., 25.));
//		Assert.assertEquals("all chars", 2206, characters.size());
		File charDir = new File("target/chars/");
		plotBoxes(characters, new File(charDir, "charsHeight.svg"));
		Multimap<Integer, PixelIsland> charactersByHeight = characters
				.createCharactersByHeight();
		for (Integer height : charactersByHeight.keySet()) {
			PixelIslandList charsi = new PixelIslandList(
					charactersByHeight.get(height));
			Assert.assertEquals("counts" + height, heightCount[height],
					charsi.size());
			charDir.mkdirs();
			plotBoxes(charsi, new File(charDir, "chars" + height + ".svg"));
		}
	}

	@Test
	/** finds and correlates bracket characters which are ca 21 pixels high.
	 * 
	 * note appear to be brackets or pipes
	 * 
	 *  creates plot in target/brackets/i_j.svg
	 * @throws IOException
	 */
	@Ignore // null pointer
	public void testInterboxCorrelations() throws IOException {
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG));
		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
		PixelIslandList characters = islands.isContainedIn(new RealRange(0.,
				5.), new RealRange(20., 25.));
		Multimap<Integer, PixelIsland> charactersByHeight = characters
				.createCharactersByHeight();
		PixelIslandList brackets = new PixelIslandList(
				charactersByHeight.get(22));
		int nchar = brackets.size();
		Assert.assertEquals("bracket", 4, nchar);
		RealSquareMatrix correlationMatrix = new RealSquareMatrix(4,4);
		for (int i = 0; i < nchar; i++) {
			SVGSVG.wrapAndWriteAsSVG(brackets.get(i).getSVGG(), new File("target/chars/bracket_"+i+".svg"));
			for (int j = 0; j <= i; j++) {
				double cor = brackets.get(i).
						binaryIslandCorrelation(brackets.get(j), "brackets/" + i + "_" + j);
				 correlationMatrix.setElementAt(i,  j, cor);
				 correlationMatrix.setElementAt(j,  i, cor);
			}
		}
		correlationMatrix.format(2);
		Assert.assertEquals("correlation", "{4,4}\n"+
			"(1.0,0.8,0.69,0.89)\n"+
			"(0.8,1.0,0.75,0.8)\n"+
			"(0.69,0.75,1.0,0.73)\n"+
			"(0.89,0.8,0.73,1.0)", correlationMatrix.toString());
	}

//	@Test
//	/** correlates every character with every other.
//	 * very crude. Finds sets of highly correlated characters and labels first one on
//	 * diagram
//	 * @throws IOException
//	 */
//	@Ignore
//	// in tests
//	public void testLargePhyloJpgCharsReconstruct1() throws IOException {
//		double correlation = 0.75;
//		String colors[] = { "red", "blue", "green", "yellow", "purple", "cyan",
//				"brown", "pink", "lime", "orange" };
//		PixelProcessor pixelProcessor = new PixelProcessor(ImageUtil.readImage(Fixtures.LARGE_PHYLO_JPG));
//		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
//		PixelIslandList characters = islands.isContainedIn(new RealRange(0.,
//				15.), new RealRange(0., 12.));
//		Multimap<Integer, PixelIsland> charactersByHeight = characters
//				.createCharactersByHeight();
//		PixelIslandList chars = new PixelIslandList(charactersByHeight.get(10));
//		Collections.sort(chars.getList(), new PixelIslandComparator(
//				PixelIslandComparator.ComparatorType.TOP,
//				PixelIslandComparator.ComparatorType.LEFT));
//		int nchar = chars.size();
//		Assert.assertEquals("10-high characters", 304, nchar);
//		List<List<Integer>> groupList = new ArrayList<List<Integer>>();
//		Set<Integer> usedSet = new HashSet<Integer>();
//		SVGG allg = new SVGG();
//		for (int i = 0; i < Math.min(2000, nchar); i++) {
//			if (usedSet.contains(i))
//				continue;
//			usedSet.add(i);
//			SVGG g = new SVGG();
//			List<Integer> newList = new ArrayList<Integer>();
//			newList.add(i);
//			groupList.add(newList);
//			for (int j = i + 1; j < nchar; j++) {
//				if (usedSet.contains(j))
//					continue;
//				double cor = Util.format(
//						chars.get(i).binaryIslandCorrelation(chars.get(j),
//								"dummy"), 2);
//				if (cor > correlation) {
//					newList.add(j);
//					usedSet.add(j);
//					// System.out.print(i+" "+j+": "+cor+" ");
//					g.appendChild(chars.get(j).createSVGFromPixelPaths(true));
//				}
//			}
//			Collections.sort(newList);
//			SVGG gk = null;
//			for (Integer k : newList) {
//				gk = chars.get(k).createSVGFromPixelPaths(true);
//				g.appendChild(gk);
//			}
//			gk = new SVGG(gk);
//			SVGText text = new SVGText(gk.getBoundingBox().getCorners()[0],
//					String.valueOf(newList.get(0)));
//			gk.appendChild(text);
//			text.setOpacity(0.6);
//			text.setFontSize(20.0);
//			text.setFill("green");
//			allg.appendChild(gk);
//			SVGSVG.wrapAndWriteAsSVG(g,
//					new File("target/charsCorr/" + newList.get(0) + ".svg"));
//			// System.out.println();
//		}
//		SVGSVG.wrapAndWriteAsSVG(allg, new File("target/charsCorr/All.svg"));
//		Collections.sort(groupList, new ListComparator());
//		for (List<Integer> listi : groupList) {
//			// System.out.println(listi);
//		}
//
//	}

	@Test
	/** correlates every character with every other.
	 * very crude. Finds sets of highly correlated characters and labels first one on
	 * diagram
	 * @throws IOException
	 */
	@Ignore
	public void testLargePhyloJpgCharsCorrelateA() throws IOException {
		// "A"s selected manually
		int[] charsA = { 90, 274, 97, 98, 133, 202, 283, 136, 143, 1, 2 // dummies
		};
		BufferedImage rawImage = ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG);
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(rawImage);
		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
		PixelIslandList characters = islands.isContainedIn(new RealRange(0.,
				15.), new RealRange(0., 12.));
		Multimap<Integer, PixelIsland> charactersByHeight = characters
				.createCharactersByHeight();
		PixelIslandList chars = new PixelIslandList(charactersByHeight.get(10));
		Collections.sort(chars.getList(), new PixelIslandComparator(
				PixelComparator.ComparatorType.TOP,
				PixelComparator.ComparatorType.LEFT));
		PixelIslandList islandsA = new PixelIslandList();
		File clipDir = new File("target/clip/");
		clipDir.mkdirs();
		for (int charA : charsA) {
			PixelIsland island = chars.get(charA);
			islandsA.add(island);
			Int2Range ibbox = island.getIntBoundingBox();
			BufferedImage subImage1 = org.contentmine.image.ImageUtil.clipSubImage(
					rawImage, ibbox);
			if (subImage1 == null) {
				LOG.error("null subImage");
			} else {
				LOG.trace(ibbox);
				File file = new File(clipDir, charA + ".png");
				try {
					ImageIOUtil.writeImageQuietly(subImage1, file);
				} catch (Exception e) {
					LOG.error("couldn't write character: " + charA + " " + e);
					continue;
				}
			}
		}
		int nchar = islandsA.size();
		// System.out.println("size: "+nchar);
		for (int i = 0; i < nchar; i++) {
			for (int j = i; j < nchar; j++) {
				double cor = Util.format(
						islandsA.get(i).binaryIslandCorrelation(
								islandsA.get(j), "/charsA_" + i + "-" + j), 2);
				// System.out.print(i+"-"+j+": "+cor+" ");
			}
			// System.out.println();
		}
	}

	/**
	 * correlates a few grayscale characters.
	 * 
	 * uses "A"
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testCorrelateGrayCharacters() throws IOException {
		int[] charsA = { 90, 274, 97, 98, 133, 202, 283, 136, 143,
		// 1,2 // dummies
		};
		BufferedImage rawImage = ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG);
		Assert.assertNotNull("rawImage not null", rawImage);
		PixelIslandList islandsA = createAs(charsA);
		List<BufferedImage> subImageList = new ArrayList<BufferedImage>();
		File charsAADir = new File("target/charsAA/");
		for (int i = 0; i < charsA.length; i++) {
			Int2Range ibbox = islandsA.get(i).getIntBoundingBox();
			BufferedImage subImage1 = org.contentmine.image.ImageUtil.clipSubImage(
					rawImage, ibbox);
			Assert.assertNotNull("subImage1 not null", subImage1);
			Assert.assertNotNull("charsA[i] not null", charsA[i]);
			subImageList.add(subImage1);
			File file = new File(charsAADir, +charsA[i] + ".png");
			ImageIOUtil.writeImageQuietly(subImage1, file);
		}
		int nchar = islandsA.size();
		// System.out.println("size: "+nchar);
		for (int i = 0; i < nchar; i++) {
			for (int j = 0; j <= i; j++) {
				double cor = Util.format(org.contentmine.image.ImageUtil
						.correlateGrayAndPlot(subImageList.get(i),
								subImageList.get(j), "charsA/" + i + "__" + j),
						2);
				// System.out.print(i+"-"+j+": "+cor+" ");
			}
			// System.out.println();
		}
	}

	@Test
	/** find all As of size 10.
	 * 
	 */
	public void testFindCharsA() throws IOException {
		BufferedImage rawImage = ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG);
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(rawImage);
		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
		extractCharactersAndCorrelate(rawImage, islands, "65", 0.27);
	}

	@Test
	/** find all As of size 10.
	 * 
	 */
	public void testFindCharsAny() throws IOException {
		BufferedImage rawImage = ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG);
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(rawImage);
		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
		extractCharactersAndCorrelate(rawImage, islands, "65", 0.30);
		extractCharactersAndCorrelate(rawImage, islands, "A10b", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "a10sb", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "B10", 0.70);
		extractCharactersAndCorrelate(rawImage, islands, "B10b", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "C10", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "C10b", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "D10", 0.60);
		extractCharactersAndCorrelate(rawImage, islands, "D10b", 0.40);
		extractCharactersAndCorrelate(rawImage, islands, "d10sb", 0.40);
		extractCharactersAndCorrelate(rawImage, islands, "E10", 0.50);
		extractCharactersAndCorrelate(rawImage, islands, "G10", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "G10b", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "g10sb", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "H10", 0.52);
		extractCharactersAndCorrelate(rawImage, islands, "L10b", 0.50);
		extractCharactersAndCorrelate(rawImage, islands, "O10b", 0.27);
		// // extractCharacters(rawImage, islands, "P10", 0.27); // isn't any
		extractCharactersAndCorrelate(rawImage, islands, "P10b", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "R10", 0.70);
		extractCharactersAndCorrelate(rawImage, islands, "S10", 0.50);
		extractCharactersAndCorrelate(rawImage, islands, "S10b", 0.27);
		extractCharactersAndCorrelate(rawImage, islands, "T10", 0.50);
		extractCharactersAndCorrelate(rawImage, islands, "T10b", 0.50);
		extractCharactersAndCorrelate(rawImage, islands, "y10sb", 0.60);
	}

	@Test
	@Ignore // this takes a long time
	public void extractCharsToImages() throws IOException {
		BufferedImage rawImage = ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG);
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG));
		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
		for (int h = 5; h < 10; h++) {
			PixelIslandList characters = islands.isContainedIn(new RealRange(
					/* w - 1, w + 1 */0, 15), new RealRange(h - 1, h + 1));
			int c = 0;
			for (PixelIsland island : characters) {
				Int2Range i2r = island.getIntBoundingBox();
				IntRange ix = i2r.getXRange();
				IntRange iy = i2r.getYRange();
				Int2Range i2ra = new Int2Range(new IntRange(ix.getMin(),
						ix.getMax() + 1), new IntRange(iy.getMin(),
						iy.getMax() + 1));
				BufferedImage image = ImageUtil.clipSubImage(rawImage, i2ra);
				ImageIOUtil.writeImageQuietly(image, "target/rawChars/" + h + "/"
						+ c + ".png");
				c++;
			}
		}
	}

	@Test
	@Ignore // graphs may not be support
	public void testCreatePixelIslandListFromStringChar4() {
		double size = 30.;
		String string = "4";
		String font = Font.SANS_SERIF;
		PixelIslandList pixelIslandList = PixelIslandList.createPixelIslandListFromString(size, string, font);
		PixelGraph graph = new PixelGraph(pixelIslandList.get(0));
		SVGSVG.wrapAndWriteAsSVG(graph.drawEdgesAndNodes(PixelGraph.COLOURS), new File("target/glyph/char4.svg"));
		
	}
	
	@Test
	public void testFillSingleHoles() {
		PixelIsland island = new PixelIsland();
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,0));
		Pixel centre = new Pixel(0,0);
		PixelList filled = island.fillSingleHoles();
		Assert.assertEquals("filled", 1, filled.size());
	}

	@Test
	public void testFindEmptyPixels() {
		PixelIsland island = new PixelIsland();
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		PixelList empty = island.getEmptyPixels();
		Assert.assertEquals("empty", 2, empty.size());
	}

	@Test
	public void testFindStubPixels() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		PixelList stubs = island.getOrCreateOrthogonalStubList();
		Assert.assertEquals("empty", 1, stubs.size());
	}

	@Test
	public void testTrimStubs() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		PixelList stubs = island.trimOrthogonalStubs();
		Assert.assertEquals("stubs ", 1, stubs.size());
		Assert.assertEquals("after trimming ", 3, island.size());
	}

	@Test
	public void testCreateSeparateIslandWithClonedPixels() {
		PixelIsland originalIsland = new PixelIsland();
		originalIsland.setDiagonal(true);
		Pixel[][] pixels = new Pixel[5][];
		for (int i = 0; i < 5; i++) {
			pixels[i] = new Pixel[5];
			for (int j = 0; j < 5; j++) {
				pixels[i][j] = new Pixel(i, j);
				originalIsland.addPixelWithoutComputingNeighbours(pixels[i][j]);
			}
		}
		PixelList originalList = originalIsland.getPixelList();
		Assert.assertEquals("original", 25, originalList.size());
		Assert.assertEquals("originalNeighbours00", 3, pixels[0][0].getOrCreateNeighbours(originalIsland).size());
		Assert.assertEquals("originalNeighbours02", 5, pixels[0][2].getOrCreateNeighbours(originalIsland).size());
		Assert.assertEquals("originalNeighbours33", 8, pixels[3][3].getOrCreateNeighbours(originalIsland).size());
		
		PixelIsland cloneIsland = PixelIsland.createSeparateIslandWithClonedPixels(originalList, true);
		PixelList cloneList = cloneIsland.getPixelList();
		Assert.assertEquals("clone", 25, cloneList.size());
		Assert.assertEquals("cloneNeighbours00", 3, cloneIsland.getPixelByCoord(new Int2(0, 0)).getOrCreateNeighbours(cloneIsland).size());
		Assert.assertEquals("cloneNeighbours02", 5, cloneIsland.getPixelByCoord(new Int2(0, 2)).getOrCreateNeighbours(cloneIsland).size());
		Assert.assertEquals("cloneNeighbours33", 8, cloneIsland.getPixelByCoord(new Int2(3, 3)).getOrCreateNeighbours(cloneIsland).size());
		
		originalIsland.remove(pixels[0][1]);
		Assert.assertEquals("original", 24, originalList.size());
		Assert.assertEquals("originalNeighbours00", 2, pixels[0][0].getOrCreateNeighbours(originalIsland).size());
		Assert.assertEquals("originalNeighbours02", 4, pixels[0][2].getOrCreateNeighbours(originalIsland).size());
		Assert.assertEquals("originalNeighbours33", 8, pixels[3][3].getOrCreateNeighbours(originalIsland).size());
		
		Assert.assertEquals("clone", 25, cloneList.size());
		Assert.assertEquals("cloneNeighbours00", 3, cloneIsland.getPixelByCoord(new Int2(0, 0)).getOrCreateNeighbours(cloneIsland).size());
		Assert.assertEquals("cloneNeighbours02", 5, cloneIsland.getPixelByCoord(new Int2(0, 2)).getOrCreateNeighbours(cloneIsland).size());
		Assert.assertEquals("cloneNeighbours33", 8, cloneIsland.getPixelByCoord(new Int2(3, 3)).getOrCreateNeighbours(cloneIsland).size());
		
	}
	
	
	// =============================================================

	private PixelIsland createLargeY() {
		PixelIsland island = new PixelIsland();
		island.setDiagonal(true);
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(-1,-1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,1));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(0,2));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(1,0));
		island.addPixelAndComputeNeighbourNeighbours(new Pixel(2,0));
		return island;
	}
	
	private void extractCharactersAndCorrelate(BufferedImage rawImage,
			PixelIslandList islands, String charname, double correlationCutoff)
			throws IOException {
		LOG.trace("charname " + charname);
		BufferedImage image = ImageUtil.readImage(new File(
				"src/test/resources/org/contentmine/image/text/chars/" + charname
						+ ".png"));
		int w = image.getWidth();
		int h = image.getHeight();
		LOG.trace("charname " + w + " " + h);
		extractCharacterImagesAndCorrelate(rawImage, charname,
				correlationCutoff, image, w, h, islands);
	}

	private void extractCharacterImagesAndCorrelate(BufferedImage rawImage,
			String charname, double correlationCutoff,
			BufferedImage characterImage, int selectionWidth,
			int selectionHeight, PixelIslandList islands) throws IOException {
		PixelIslandList charactersOfCorrectSize = islands.isContainedIn(
				new RealRange(selectionWidth - 1, selectionWidth + 1),
				new RealRange(selectionHeight - 1, selectionHeight + 1));
		int i = 0;
		for (PixelIsland characterIsland : charactersOfCorrectSize) {
			BufferedImage subImage = characterIsland.clipSubimage(rawImage);
			// if charname is not null saves overlaid images (arrgh!)
			double corr = ImageUtil.correlateGrayAndPlot(characterImage, subImage,
					null /** "charACorr/"+charname+"/"+i */
			);
			if (corr > correlationCutoff) {
				LOG.trace("corr " + i + " " + Util.format(corr, 2));
				ImageIOUtil.writeImageQuietly(subImage, "target/charACorr/"
						+ charname + "/" + i + ".png");
			}
			i++;
		}
	}

	private PixelIslandList createAs(int[] charsA) throws IOException {
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG));
		PixelIslandList islands = pixelProcessor.getOrCreatePixelIslandList();
		PixelIslandList characters = islands.isContainedIn(new RealRange(0.,
				15.), new RealRange(0., 12.));
		Multimap<Integer, PixelIsland> charactersByHeight = characters
				.createCharactersByHeight();
		PixelIslandList chars = new PixelIslandList(charactersByHeight.get(10));
		Collections.sort(chars.getList(), new PixelIslandComparator(
				PixelComparator.ComparatorType.TOP,
				PixelComparator.ComparatorType.LEFT));
		PixelIslandList islandsA = new PixelIslandList();
		for (int charA : charsA) {
			PixelIsland island = chars.get(charA);
			islandsA.add(island);
		}
		return islandsA;
	}

	static void plotBoxes(PixelIslandList islands, File file) {
		SVGG g = new SVGG();
		for (PixelIsland island : islands) {
			Real2Range bbox = island.getBoundingBox();
			LOG.trace(island + " " + island.size() + " " + bbox);
			SVGRect rect = new SVGRect(bbox);
			g.appendChild(rect);
//			SVGG gg = island.createSVGFromPixelPaths(true);
			SVGG gg = island.createSVGFromEdges();
			int n = gg.getChildCount();
			for (int i = n - 1; i >= 0; i--) {
				SVGElement ggg = (SVGElement) gg.getChild(i);
				ggg.detach();
				g.appendChild(ggg);
			}
		}
		file.getParentFile().mkdirs();
		SVGSVG.wrapAndWriteAsSVG(g, file);
	}

	private void debug(List<Real2Array> segmentArrayList) {
		for (Real2Array coords : segmentArrayList) {
			System.out.println(coords);
		}
	}

	private PixelIsland createFirstPixelIsland(File file) throws IOException {
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(ImageIO.read(file));
		return pixelProcessor.getOrCreatePixelIslandList().get(0);
	}

}
