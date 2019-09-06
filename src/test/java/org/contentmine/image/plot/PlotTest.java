package org.contentmine.image.plot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.OutlineTester;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelListFloodFill;
import org.contentmine.image.pixel.PixelNodeList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.pixel.PixelSegmentList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;

public class PlotTest {

	public static final String BLUE = "blue";

	private static final String CYAN = "cyan";

	public final static Logger LOG = Logger.getLogger(PlotTest.class);

	public final static String SHARK = "0095565.g002";
//	public final static File G002_DIR = new File(ImageAnalysisFixtures.COMPOUND_DIR,
//			"journal.pone." + SHARK);
	public final static File G002_DIR = new File(ImageAnalysisFixtures.COMPOUND_DIR,
			"components");
	public static ImageProcessor DEFAULT_IMAGE_PROCESSOR = null;
	private File PLOT_OUT_DIR;
	private File CCJ_DIR = new File(ImageAnalysisFixtures.DIAGRAMS_DIR, "ccj");

	public final static String[] FILL = new String[] { "orange", "green",
			BLUE, "red", CYAN };

	@Before
	public void setUp() {
		PLOT_OUT_DIR = new File("target/plot/");
		PLOT_OUT_DIR.mkdirs();
		DEFAULT_IMAGE_PROCESSOR = ImageProcessor.createDefaultProcessor();
		;
	}



	@Test
	public void testDefaultSharkPlotCLI() {
		String[] args = { "--input", new File(G002_DIR, "g002.png").toString(), 
				"--output", new File("target/" + SHARK + "/").toString(), };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
		Assert.assertEquals("pixels", 89, plotAnalyzer
				.getOrCreatePixelIslandList().size());
		// thinned by default
		int pixels = plotAnalyzer.getOrCreatePixelIslandList().getPixelList()
				.size();
		Assert.assertTrue("pixels " + pixels, 4855 <= pixels && pixels <= 4875);
	}

	/**
	 * Image from PLoSONE as archetypal X-Y plot.
	 * 
	 * journal.pone.0095565.g002.png. The plot has: * x and y axes (each with
	 * ticks, numbers and title) y - axis has horizontal numbers but rotated
	 * title * points with error bars * best-fit line
	 * 
	 * Note that the antialiasing is severe and occasionally bleeds between
	 * characters
	 *
	 * @throws IOException
	 */
	@Test
	public void testDefaultUnthinnedSharkPlot() throws IOException {
		File g002 = new File(G002_DIR, "g002.png");
		BufferedImage image = UtilImageIO.loadImage(g002.toString());
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setBinarize(true);
		imageProcessor.setThinning(null);
		imageProcessor.readAndProcessFile(g002);
		ImageIOUtil.writeImageQuietly(image, new File("target/" + SHARK
				+ "/raw.png"));
		BufferedImage defaultUnthinnedImage = imageProcessor.getImage();
		ImageIOUtil.writeImageQuietly(defaultUnthinnedImage, new File("target/"
				+ SHARK + "/defaultUnthinnedBinary.png"));
		Assert.assertEquals("pixels", 89, imageProcessor
				.getOrCreatePixelIslandList().size());
		Assert.assertEquals("pixels", 15666, imageProcessor
				.getOrCreatePixelIslandList().getPixelList().size());
	}

	@Test
	/** this is LONG
	 * 
	 */
	@Ignore // LONG
	public void testUnthinnedSharkPlotCLI() {
		String[] args = { "--input", new File(G002_DIR, "g002.png").toString(), // source
																				// image
				"--thinning", "none", // otherwise removes point
				"--output", new File("target/" + SHARK + "/").toString(), };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
		ImageIOUtil.writeImageQuietly(plotAnalyzer.getImage(), new File("target/"
				+ SHARK + "/defaultUnthinnedCLI.png"));
		Assert.assertEquals("pixels", 89, plotAnalyzer
				.getOrCreatePixelIslandList().size());
		// why not as above?
		int pixels = plotAnalyzer.getOrCreatePixelIslandList().getPixelList()
				.size();
//		Assert.assertTrue("pixels " + pixels, 6900 <= pixels && pixels <= 7320);
		Assert.assertTrue("pixels " + pixels, 6900 <= pixels && pixels <= 16000);
	}

	/**
	 * this captures the axes which were gray.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDefaultUnthinnedThreshold() throws IOException {
		File g002 = new File(G002_DIR, "g002.png");
		BufferedImage image = UtilImageIO.loadImage(g002.toString());
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setBinarize(true);
		imageProcessor.setThinning(null);
		imageProcessor.setThreshold(180); // 140 is about the limit, so increase
		imageProcessor.readAndProcessFile(g002);
		ImageIOUtil.writeImageQuietly(image, new File("target/" + SHARK
				+ "/raw.png"));
		BufferedImage defaultUnthinnedImage = imageProcessor.getImage();
		ImageIOUtil.writeImageQuietly(defaultUnthinnedImage, new File("target/"
				+ SHARK + "/defaultUnthinnedThreshold.png"));
		Assert.assertEquals("pixels", 90, imageProcessor
				.getOrCreatePixelIslandList().size());
		Assert.assertEquals("pixels", 25911, imageProcessor
				.getOrCreatePixelIslandList().getPixelList().size());
	}

	@Test
	/** this is LONG
	 * 
	 */
	@Ignore // LONG
	public void testUnthinnedSharkPlotWithAxesCLI() {
		String[] args = { "--input",
				new File(G002_DIR, "g002.png").toString(), // source image
				"--thinning",
				"none", // otherwise removes point
				"--threshold", "180", "--output",
				new File("target/" + SHARK + "/").toString(), };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
		ImageIOUtil.writeImageQuietly(plotAnalyzer.getImage(), new File("target/"
				+ SHARK + "/defaultUnthinnedWithAxesCLI.png"));
		Assert.assertEquals("pixels", 90, plotAnalyzer.getOrCreatePixelIslandList().size());
		// why not as above?
		// Assert.assertEquals("pixels", 10341,
		// plotAnalyzer.getOrCreatePixelIslandList().getPixelList().size());
		int pixels = plotAnalyzer.getOrCreatePixelIslandList().getPixelList()
				.size();
//		Assert.assertTrue("pixels " + pixels, 10500 <= pixels 	&& pixels <= 10920);
		Assert.assertTrue("pixels " + pixels, 25000 <= pixels 	&& pixels <= 26000);
	}

	/**
	 * good start for processing thinned image.
	 * 
	 */
	@Test
	public void testThinnedSharkPlotWithAxesCLI() {
		String[] args = { "--input",
				new File(G002_DIR, "g002.png").toString(), // source image
				"--thinning", "z", "--threshold", "180", "--output",
				new File("target/" + SHARK + "/").toString(), };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
		ImageIOUtil.writeImageQuietly(plotAnalyzer.getImage(), new File("target/"
				+ SHARK + "/defaultThinnedWithAxesCLI.png"));
		Assert.assertEquals("pixels", 90, plotAnalyzer
				.getOrCreatePixelIslandList().size());
		// why not as above?
		int pixels = plotAnalyzer.getOrCreatePixelIslandList().getPixelList()
				.size();
		Assert.assertTrue("pixels " + pixels, 6805 < pixels && pixels < 6825);
	}

	/**
	 * good start for processing thinned image.
	 * break thinned image into islands
	 * 
	 */
	@Test
	public void testThinnedThresholdSharkFindLargestIslands() {
		String[] args = { "--input",
				new File(G002_DIR, "g002.png").toString(), // source image
				"--thinning", "z", "--threshold", "180", "--output",
				new File("target/" + SHARK + "/").toString(), };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
		PixelIslandList pil = plotAnalyzer.getOrCreatePixelIslandList();
		pil.sortBySizeDescending();
		int[] sizes = { 2133, 1941, 84, 80, 76, 44, 43 };
		for (int i = 0; i < sizes.length; i++) {
			int size = pil.get(i).size();
			Assert.assertTrue("pil" + i + "; " + size, sizes[i] - 10 <= size
					&& size <= sizes[i] + 10);
		}
		// SVG
		SVGSVG.wrapAndWriteAsSVG(pil.get(0).getOrCreateSVGG(), new File(
				"target/" + SHARK + "/line.svg"));
		SVGSVG.wrapAndWriteAsSVG(pil.get(1).getOrCreateSVGG(), new File("target/" + SHARK + "/axes.svg"));
		SVGSVG.wrapAndWriteAsSVG(pil.get(2).getOrCreateSVGG(), new File("target/" + SHARK + "/bar0.svg"));
		SVGSVG.wrapAndWriteAsSVG(pil.get(3).getOrCreateSVGG(), new File("target/" + SHARK + "/bar1.svg"));
		SVGSVG.wrapAndWriteAsSVG(pil.get(4).getOrCreateSVGG(), new File("target/" + SHARK + "/bar2.svg"));
		SVGSVG.wrapAndWriteAsSVG(pil.get(5).getOrCreateSVGG(), new File("target/" + SHARK + "/d.svg"));
		// same as PNG
		ImageIOUtil.writeImageQuietly(pil.get(0).createImage(), new File("target/" + SHARK + "/line.png"));
		ImageIOUtil.writeImageQuietly(pil.get(1).createImage(), new File("target/" + SHARK + "/axes.png"));
		ImageIOUtil.writeImageQuietly(pil.get(2).createImage(), new File("target/" + SHARK + "/bar0.png"));
		ImageIOUtil.writeImageQuietly(pil.get(3).createImage(), new File("target/" + SHARK + "/bar1.png"));
		ImageIOUtil.writeImageQuietly(pil.get(4).createImage(), new File("target/" + SHARK + "/bar2.png"));
		ImageIOUtil.writeImageQuietly(pil.get(5).createImage(), new File("target/" + SHARK + "/d.png"));

		int pixels = plotAnalyzer.getOrCreatePixelIslandList().getPixelList()
				.size();
		Assert.assertTrue("pixels " + pixels, 6805 <= pixels && pixels <= 6825);
	}

	@Test
	/** 2D x-y plot of shark data.
	 * 
	 * @throws IOException
	 */
	public void testSharkPlot() throws IOException {
		File g002 = new File(G002_DIR, "g002.png");
		BufferedImage image = UtilImageIO.loadImage(g002.toString());
		ImageProcessor imageProcessor = ImageProcessor
				.createDefaultProcessorAndProcess(image);
		ImageIOUtil.writeImageQuietly(imageProcessor.getImage(), new File(
				"target/" + SHARK + "/defaultThinnedBinary.png"));
		LOG.trace(g002);
		GrayU8 inputImage = ConvertBufferedImage.convertFrom(image,
				(GrayU8) null);
		UtilImageIO.saveImage(ConvertBufferedImage.convertTo(inputImage, null),
				new File(PLOT_OUT_DIR, "plotEcho.png").toString());

		GrayU8 binary = new GrayU8(inputImage.getWidth(),
				inputImage.getHeight());
		int threshold = 220; // axes are quite light gray
		// ThresholdImageOps.threshold(inputImage, binary, threshold, false);
		BufferedImage outImage = VisualizeBinaryData.renderBinary(binary, false, null);
//		BufferedImage outImage = ConvertBufferedImage.extractBuffered(binary);
		UtilImageIO.saveImage(outImage,
				new File(PLOT_OUT_DIR, "plotBinary.png").toString());

		PixelIslandList islands = ImageProcessor
				.createDefaultProcessorAndProcess(
						new File(G002_DIR, "g002.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("plot islands", 89, islands.size());

		PixelIslandList axes = ImageProcessor.createDefaultProcessorAndProcess(
				new File(G002_DIR, "axes.png")).getOrCreatePixelIslandList();
		Assert.assertEquals("axes", 1, axes.size());

		PixelIslandList errorbar = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "errorbar.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("errorbar", 1, errorbar.size());
		PixelIslandList xvalues = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "xnumbers.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("xvalues", 11, xvalues.size());
		PixelIslandList xtitle = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "xtitle.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("xtitle", 33, xtitle.size());
		PixelIslandList yvalues = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "ynumbers.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("yvalues", 9, yvalues.size());
		imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setThreshold(30); // to avoid bleeding between "r" and
											// "v" in "observed"
		imageProcessor.processImageFile(new File(G002_DIR, "ytitle.png"));
		PixelIslandList ytitle = imageProcessor.getOrCreatePixelIslandList();
		Assert.assertEquals("ytitle", 33, ytitle.size());
	}

	@Test
	public void testAxes0() throws IOException {
		File axesPng = new File(G002_DIR, "axes.png");
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.processImageFile(axesPng);
		PixelIsland axes = imageProcessor.getOrCreatePixelIslandList().get(0);
		Assert.assertEquals("pixels", 7860, axes.size());
		PixelIsland axesThin = new PixelIsland(axes);
		axesThin.setDiagonal(true);
		axesThin.findRidge();
		PixelList edgeList = axesThin.getPixelsWithValue(1);
		Assert.assertEquals("1:", 3938, edgeList.size());
		PixelList list2 = axesThin.growFrom(edgeList, 1);
		Assert.assertEquals("2:", 3922, list2.size());
		PixelList list3 = axesThin.growFrom(list2, 2);
		Assert.assertEquals("3:", 0, list3.size());
	}

	@Test
	/** extract error bars with symbols and color the eroded shells.
	 * 
	 * @throws IOException
	 */
	public void testErrorBar() throws IOException {
		File rawfile = new File(G002_DIR, "errorbar.png");
		String base = FilenameUtils.getBaseName(rawfile.toString());
		DEFAULT_IMAGE_PROCESSOR.setThinning(null);
		DEFAULT_IMAGE_PROCESSOR.readAndProcessFile(rawfile);
		File newfile = new File("target/" + SHARK + "/" + base + "_raw.png");
		ImageIOUtil.writeImageQuietly(DEFAULT_IMAGE_PROCESSOR.getImage(), newfile);
		PixelIslandList islandList = DEFAULT_IMAGE_PROCESSOR
				.getOrCreatePixelIslandList();
		ImageIOUtil.writeImageQuietly(islandList.createImageAtOrigin(), new File(
				"target/" + SHARK + "/" + base + "_second.png"));
		Assert.assertNotNull(islandList);
		PixelIsland errorbar = islandList.get(0);
		Assert.assertEquals("pixels", 290, errorbar.size());
		PixelIsland errorIsland = new PixelIsland(errorbar);
		SVGG g = new SVGG();
		errorIsland.setDiagonal(true);
		errorIsland.findRidge();
		PixelList list1 = errorIsland.getPixelsWithValue(1);
		Assert.assertEquals("1:", 198, list1.size());
		list1.plotPixels(g, FILL[0]);
		PixelList list2 = errorIsland.growFrom(list1, 1);
		Assert.assertEquals("2:", 47, list2.size());
		list2.plotPixels(g, FILL[1]);
		PixelList list3 = errorIsland.growFrom(list2, 2);
		Assert.assertEquals("3:", 30, list3.size());
		list3.plotPixels(g, FILL[2]);
		PixelList list4 = errorIsland.growFrom(list3, 3);
		Assert.assertEquals("4:", 14, list4.size());
		list4.plotPixels(g, FILL[3]);
		PixelList list5 = errorIsland.growFrom(list4, 4);
		Assert.assertEquals("5:", 1, list5.size());
		list5.plotPixels(g, FILL[4]);
		PixelList list6 = errorIsland.growFrom(list5, 5);
		Assert.assertEquals("6:", 0, list6.size());

		// this is all the rings
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/errorbarAllRings.svg"));

		g = new SVGG();
		PixelList list12 = list2.getPixelsTouching(list1);
		Assert.assertEquals("12", 64, list12.size());
		list12.plotPixels(g, "blue");
		// this is the outline of the symbol
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/errorbarOutline.svg"));

		SVGG gg = new SVGG();
		PixelIslandList thinned = ImageProcessor
				.createDefaultProcessorAndProcess( new File(G002_DIR, "errorbar.png"))
				.getOrCreatePixelIslandList();
		PixelRingList ringList = thinned.get(0).getOrCreateInternalPixelRings();
		ringList.plotPixels(gg, new String[] { "red", BLUE });
		// this is a thinned error bar
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/" + SHARK + "/errorbar2.svg"));
	}

	@Test
	public void testErrorBar1() throws IOException {
		File rawfile = new File(G002_DIR, "errorbar.png");
		DEFAULT_IMAGE_PROCESSOR.setThinning(null);
		DEFAULT_IMAGE_PROCESSOR.readAndProcessFile(rawfile);
		PixelIslandList islandList = DEFAULT_IMAGE_PROCESSOR
				.getOrCreatePixelIslandList();
		PixelIsland errorIsland = new PixelIsland(islandList.get(0));

		SVGG g = new SVGG();
		PixelRingList pixelRingList = errorIsland.getOrCreateInternalPixelRings();
		for (int i = 0; i < pixelRingList.size(); i++) {
			pixelRingList.get(i).plotPixels(g, FILL[i]);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/errorbar1AllRings.svg"));

		PixelRing outline = pixelRingList.get(1).getPixelsTouching(pixelRingList.get(0));
		Assert.assertEquals("outline0", 198, pixelRingList.get(0).size());
		Assert.assertEquals("outline1", 47, pixelRingList.get(1).size());
		Assert.assertEquals("outline", 64, outline.size());
		g = new SVGG();
		outline.plotPixels(g, "blue");
		// this is the outline of the symbol 
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/errorbar1Outline.svg"));

		// just plots the diamond
		g = new SVGG();
		outline = new PixelRing(pixelRingList.get(0).getPixelsWithOrthogonalContactsTo(pixelRingList.get(1), errorIsland));
		outline.plotPixels(g, BLUE);
		PixelIsland outlineIsland = PixelIsland.createSeparateIslandWithClonedPixels(outline, true);
		PixelGraph graph = PixelGraph.createGraph(outlineIsland);
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		Assert.assertEquals("nodes", 1, nodeList.size());
		PixelEdgeList edgeList = graph.getOrCreateEdgeList();
		Assert.assertEquals("edges", 1, edgeList.size());
		for (PixelEdge edge : edgeList) {
			PixelSegmentList segmentList = edge.getOrCreateSegmentList(2);
//			Assert.assertEquals("segments "+segmentList, 1, segmentList.size());
			g.appendChild(segmentList.getOrCreateSVG());
		}
		
		
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/errorPointOutline.svg"));
	}

	@Test
	public void testThinnedErrorBar() {

		SVGG gg = new SVGG();
		PixelIslandList thinned = ImageProcessor
				.createDefaultProcessorAndProcess(
						new File(G002_DIR, "errorbar.png"))
				.getOrCreatePixelIslandList();
		PixelRingList pixelRingList = thinned.get(0).getOrCreateInternalPixelRings();
		pixelRingList.plotPixels(gg, PixelRingList.DEFAULT_COLOURS);
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/" + SHARK
				+ "/thinnedErrorbar.svg"));
	}

	@Test
	public void testSmallD() throws IOException {
		DEFAULT_IMAGE_PROCESSOR.setThinning(null);
		DEFAULT_IMAGE_PROCESSOR.setThreshold(180);
		DEFAULT_IMAGE_PROCESSOR.readAndProcessFile(new File(G002_DIR,
				"g002.png"));
		PixelIsland islandD = DEFAULT_IMAGE_PROCESSOR
				.getOrCreatePixelIslandList().get(7);

		SVGG g = new SVGG();
		PixelRingList pixelRingList = islandD.getOrCreateInternalPixelRings();
		for (int i = 0; i < pixelRingList.size(); i++) {
			pixelRingList.get(i).plotPixels(g, FILL[i]);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/islandD.svg"));

	}

	@Test
	public void testPlotLine() throws IOException {
		DEFAULT_IMAGE_PROCESSOR.setThinning(null);
		DEFAULT_IMAGE_PROCESSOR.setThreshold(180);
		DEFAULT_IMAGE_PROCESSOR.readAndProcessFile(new File(G002_DIR, "g002.png"));
		PixelIsland islandD = DEFAULT_IMAGE_PROCESSOR.getOrCreatePixelIslandList().get(0);

		SVGG g = new SVGG();
		PixelRingList pixelRingList = islandD.getOrCreateInternalPixelRings();
		for (int i = 0; i < pixelRingList.size(); i++) {
			pixelRingList.get(i).plotPixels(g, FILL[i]);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/plotLine.svg"));

		g = new SVGG();
		PixelRing outline = pixelRingList.get(3).getPixelsTouching(pixelRingList.get(2));
		outline.plotPixels(g, "black");
		// this is the outline of the symbol
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/plotLinePoints23.svg"));
		
		g = new SVGG();
		outline = pixelRingList.get(2).getPixelsTouching(pixelRingList.get(1));
		outline.plotPixels(g, "blue");
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/plotLinePoints12.svg"));
		
		g = new SVGG();
		outline = pixelRingList.get(1).getPixelsTouching(pixelRingList.get(0));
		outline.plotPixels(g, "purple");
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/plotLinePoints01.svg"));
		
		g = new SVGG();
		for (PixelIsland island : DEFAULT_IMAGE_PROCESSOR.getOrCreatePixelIslandList()) {
			if (island != null) {
				pixelRingList = island.getOrCreateInternalPixelRings();
				if (pixelRingList != null && pixelRingList.size() > 2) {
					outline = pixelRingList.get(2).getPixelsTouching(pixelRingList.get(1));
					outline.plotPixels(g, "blue");
				}
			}
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + SHARK + "/plotLineAllPoints12.svg"));
	}
	
	@Test
	@Ignore ("something wrong")
	public void testImagePoints() {
		File infile = null;
		File outfile = null;
		
//		infile = new File(G002_DIR, "g002.png");
//		outfile = new File("target/" + SHARK + "/plotLineAllPoints12a.svg");
//		int ring0 = 1;
//		extractPointsAndPlot(infile, outfile, ring0, 180, 26);
//		
////		infile = new File(Fixtures.COMPOUND_DIR, "journal.pone.0094179.g002.png");
//		outfile = new File("target/" + SHARK + "/plotLineAllPoints12b.svg");
//		extractPointsAndPlot(infile, outfile, ring0 + 1, 180, 4);
//
//		infile = new File(Fixtures.COMPOUND_DIR, "journal.pone.0094179.g002.png");
//		outfile = new File("target/" + "0094179" + "/plotLineAllPoints12a.svg");
//		extractPointsAndPlot(infile, outfile, 3, 180, 5);
//
//		// large gray squares
//		infile = new File(Fixtures.COMPOUND_DIR, "journal.pone.0095375.g003.png");
//		outfile = new File("target/" + "0095375.g003" + "/plotLineAllPoints12a.svg");
//		extractPointsAndPlot(infile, outfile, 12, 200, 3);
//		
//				
//		infile = new File(Fixtures.COMPOUND_DIR, "journal.pone.0095794.g009.png");
//		new File("target/" + "0095794.g009").mkdirs();
//		outfile = new File("target/" + "0095794.g009" + "/plotLineAllPoints12a.svg");
//		extractPointsAndPlot(infile, outfile, 2, 180, 5);
		
//		infile = new File(ImageAnalysisFixtures.DIAGRAMS_DIR, "plot/rscopen/JV_6.gif");
		infile = new File(ImageAnalysisFixtures.DIAGRAMS_DIR, "plot/rscopen/jv/figure6/figure.gif");
		new File("target/" + "image1").mkdirs();
		outfile = new File("target/" + "image1" + "/plotLineAllPoints12a.svg");
//		extractPointsAndPlot(infile, outfile, 0, 230, 23);
		extractPointsAndPlot(infile, outfile, 1, 235, 122);
//		extractPointsAndPlot(infile, outfile, 2, 230, 2);
		
//		infile = new File(Fixtures.COMPOUND_DIR, "journal.pone.0165252.g006.png");
//		new File("target/" + "0165252.g006").mkdirs();
//		outfile = new File("target/" + "0165252.g006" + "/plotLineAllPoints12a.svg");
//		extractPointsAndPlot(infile, outfile, 1, 180, 281);
		
	}

	private void extractPointsAndPlot(File infile, File outfile, int ring0, int thresh, int nPoints) {
		DEFAULT_IMAGE_PROCESSOR.setThinning(null);
		DEFAULT_IMAGE_PROCESSOR.setThreshold(thresh);
		DEFAULT_IMAGE_PROCESSOR.readAndProcessFile(infile);
		int ring1 = ring0 + 1;
		PixelIslandList pixelIslandList =  DEFAULT_IMAGE_PROCESSOR.getOrCreatePixelIslandList();
		for (PixelIsland island : pixelIslandList) {
			if (island != null) {
				PixelRingList pixelRingList = island.getOrCreateInternalPixelRings();
				if (pixelRingList != null && pixelRingList.size() > ring1) {
					PixelRing list0 = pixelRingList.get(ring0);
					PixelRing list1 = pixelRingList.get(ring1);
					PixelRing outline = list1.getPixelsTouching(list0);
					PixelListFloodFill pixelListFloodFill = new PixelListFloodFill(outline);
					pixelListFloodFill.fillIslands();
					PixelIslandList pixelIslandList1 = pixelListFloodFill.getIslandList();
					List<PixelIsland> outlineList = pixelIslandList1.getList();
					for (PixelIsland outline1 : outlineList) {
						pixelIslandList.addOutline(outline1.getPixelList());
					}
				}
			}
		}
		List<PixelList> outlineList = pixelIslandList.getOrCreateOutlineList(); 
		Assert.assertEquals("outlines: "+nPoints, nPoints, outlineList.size());
		SVGG gg = new SVGG();
		int i = 0;
		for (PixelList outline : outlineList) {
			SVGG g = new SVGG();
			outline.plotPixels(g, FILL[i++ % FILL.length]);
			gg.appendChild(g);
			
		}
		Real2Array coords = new Real2Array();
		for (PixelList outline : outlineList) {
			Int2Range bbox = outline.getIntBoundingBox();
			Int2 centroid = new Int2(bbox.getXRange().getMidPoint(), bbox.getYRange().getMidPoint());
			Real2 centroidxy = new Real2(centroid);
			coords.add(centroidxy);
		}
		SVGSVG svg = new SVGSVG();
		svg.setWidth(DEFAULT_IMAGE_PROCESSOR.getBinarizedImage().getWidth());
		svg.setHeight(DEFAULT_IMAGE_PROCESSOR.getBinarizedImage().getHeight());
		svg.appendChild(gg);
		XMLUtil.outputQuietly(svg, outfile, 1);
	}

	@Test
	public void testAxes1() throws IOException {
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.processImageFile(new File(G002_DIR, "axes.png"));
		PixelIsland axes = imageProcessor.getOrCreatePixelIslandList().get(0);
		Assert.assertEquals("pixels", 7860, axes.size());
		SVGG gg = new SVGG();
		PixelRingList ringList = axes.getOrCreateInternalPixelRings();
		assertSizes(ringList, new int[] { 3938, 3922 });
		ringList.plotPixels(gg, new String[] { "red", "green" });
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/plot/axes.svg"));
	}

	@Test
	public void testXTitle() throws IOException {
		PixelIslandList titleChars = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "xtitle.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("characters", 33, titleChars.size());
		PixelIslandList thinned = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "xtitle.png"))
				.getOrCreatePixelIslandList();
		List<PixelRingList> pixelRingListList = thinned.createRingListList();
		drawRings(pixelRingListList, new File("target/plot/xaxes.svg"));

	}

	public static void drawRings(List<PixelRingList> pixelRingListList, File outfile) {
		SVGG g = new SVGG();
		for (PixelRingList pixelRingList : pixelRingListList) {
			SVGG gg = pixelRingList.plotPixels(g,
					new String[] { "red", "blue", "yellow", "cyan", "green", "orange" });
		}
		SVGSVG.wrapAndWriteAsSVG(g, outfile);
	}

	@Test
	public void testXNumbers() throws IOException {
		PixelIslandList xNumbers = ImageProcessor
				.createDefaultProcessorAndProcess( new File(G002_DIR, "xnumbers.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("characters", 11, xNumbers.size());
		List<PixelRingList> pixelRingListList = xNumbers.createRingListList();
		drawRings(pixelRingListList, new File("target/plot/xNumbers.svg"));
	}

	@Test
	public void testYTitle() throws IOException {
		PixelIslandList yTitle = ImageProcessor
				.createDefaultProcessorAndProcess( new File(G002_DIR, "ytitle.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("characters", 32, yTitle.size());
		List<PixelRingList> pixelRingListList = yTitle.createRingListList();
		drawRings(pixelRingListList, new File("target/plot/ytitle.svg"));
	}

	@Test
	public void testYNumbers() throws IOException {
		PixelIslandList yNumbers = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "ynumbers.png"))
				.getOrCreatePixelIslandList();
		Assert.assertEquals("characters", 9, yNumbers.size());
		List<PixelRingList> pixelRingListList = yNumbers.createRingListList();
		drawRings(pixelRingListList, new File("target/plot/ynumbers.svg"));
	}

	@Test
	public void testPoints() throws IOException {
		PixelIslandList points = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "points.png"))
				.getOrCreatePixelIslandList();
		List<PixelRingList> pixelRingListList = points.createRingListList();
		Assert.assertEquals("characters", 4, points.size());
		drawRings(pixelRingListList, new File("target/plot/points.svg"));
	}

	@Test
	public void testNodes() throws IOException {
		PixelIslandList points = ImageProcessor
				.createDefaultProcessorAndProcess(new File(G002_DIR, "points.png"))
				.getOrCreatePixelIslandList();
		List<PixelRingList> pixelRingListList = points.createRingListList();
		Assert.assertEquals("characters", 4, points.size());
		drawRings(pixelRingListList, new File("target/plot/points3.svg"));
	}

	// characters should be done in another project (bring in JavaOCR)

	@Test
	// this one is very weak
	@Ignore
	public void test0095375() throws IOException {
		plotRingsAndThin(new File(ImageAnalysisFixtures.COMPOUND_DIR,
				"journal.pone.0095375.g003.png"), new File(
				"target/plot/0093575.svg"), new File(
				"target/plot/0093575_2.svg"));
	}

	@Test
	@Ignore
	// out of memory (chop up diagram?
	public void test0095807ManySubDiagrams() throws IOException {
		plotRingsAndThin(new File(ImageAnalysisFixtures.COMPOUND_DIR,
				"journal.pone.0095807.g003.png"), new File(
				"target/plot/0095807.svg"), new File(
				"target/plot/0095807_2.svg"));
	}

	@Test
	public void test0095816MultiColourAndSuperscripts() throws IOException {
		plotRingsAndThin(new File(ImageAnalysisFixtures.COMPOUND_DIR,
				"journal.pone.0095816.g002.png"), new File(
				"target/plot/0095816.svg"), new File(
				"target/plot/0095816_2.svg"));
	}

	@Test
	public void test004179CirclePlots() throws IOException {
		plotRingsAndThin(new File(ImageAnalysisFixtures.COMPOUND_DIR,
				"journal.pone.0094179.g002.png"), new File(
				"target/plot/0094179_2.svg"), new File(
				"target/plot/0094179_2_2.svg"));
	}

	@Test
	@Ignore
	// the resultant tree is not connected. Why? and binarization give a single
	// pixelisland??
	public void test004172Phylo() throws IOException {
		plotRingsAndThin(new File(ImageAnalysisFixtures.COMPOUND_DIR,
				"journal.pone.0094172.g002-2.png"), new File(
				"target/plot/0094172_2.svg"), new File(
				"target/plot/0094172_2_2.svg"));
	}

	@Test
	@Ignore
	// file not transferred
	public void test004172PhyloA() throws IOException {
		plotRingsAndThin(new File(ImageAnalysisFixtures.COMPOUND_DIR,
				"journal.pone.0094172.g002-2a.png"), new File(
				"target/plot/0094172_2a.svg"), new File(
				"target/plot/0094172_2a_2.svg"));
	}

	@Test
	public void testPlot005565_002a() {
		String[] args = { "--input",
				new File(G002_DIR, "errorbar.png").toString(), // source image
				"--thinning", "none", // otherwise removes point
				"--island", "0", // take the largest island (no magic, if not
									// the first you have to work out which)
				"--output", "target/0055965_002" };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
	}

	@Test
	@Ignore // file does not exist
	public void testCCJ() {

		String[] args = { "--input",
				new File(CCJ_DIR, "1752-153X-7-29-7-l.jpg").toString(), // source
																		// image
				// "--thinning", "none", // otherwise removes point
				"--island", "0", // take the largest island (no magic, if not
									// the first you have to work out which)
				"--output", "target/ccj/1752-153X-7-29-7-l.svg" };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
	}

	@Test
	@Ignore // too long and broken
	public void testDrainSource1OutlinesArgs() {
		// MUST use thinning for graphs ATM.
		String[] args = { "--input",
				new File(ImageAnalysisFixtures.ELECTRONIC_DIR, "drainsource1.png").toString(), // source image
				"--thinning", "none", // otherwise removes point
				"--island", "0", // take the largest island (no magic, if not
									// the first you have to work out which)
				// output doesn't work ATM
				"--output", "target/electronic/drainsource1.plot.png" };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
	}
	
	@Test
	@Ignore // file does not exist
	public void testCCJ1() {

		String[] args = { "--input",
				new File(CCJ_DIR, "1752-153X-7-107-3-l.jpg").toString(), // source
																			// image
				"--threshold", "200",
				// "--thinning", "none", // otherwise removes point
				"--island", "0", // take the largest island (no magic, if not
									// the first you have to work out which)
				"--output", "target/ccj/1752-153X-7-107-3-l.svg" };
		DiagramAnalyzer plotAnalyzer = new PlotAnalyzer();
		plotAnalyzer.parseArgsAndRun(args);
	}

	@Test
	public void testDrainSource1OutlinesCode() {
		OutlineTester outlineTester = new OutlineTester();
		
		outlineTester.expectedRingSizes = new int[][] {
			new int[]{7400,7375,7343},
			new int[]{3174,3165,1},
		};
		outlineTester.nodes = new int[] {4,3,0,0};
		outlineTester.edges = new int[] {4,3,0,0};
		outlineTester.outlines = new int[] {7388, 3174};
		
		outlineTester.dir = "electronic";
		outlineTester.inname = "drainsource1";
		outlineTester.outdir = ImageAnalysisFixtures.TARGET_ELECTRONIC_DIR;
		outlineTester.indir = ImageAnalysisFixtures.ELECTRONIC_DIR;
		
		outlineTester.islandCount = 11;
		outlineTester.analyzeAndAssertFile();
	
	}
	
	

	// =========================

	static void plotRingsAndThin(File infile, File outfile1, File outfile2)
			throws IOException {
		PixelIslandList plot = ImageProcessor.createDefaultProcessorAndProcess(
				infile).getOrCreatePixelIslandList();
		List<PixelRingList> pixelRingListList = plot.createRingListList();
		drawRings(pixelRingListList, outfile2);
	}

	private static void assertSizes(PixelRingList ringList, int[] sizes) {
		Assert.assertNotNull("ringList", ringList);
		Assert.assertNotNull("sizes", sizes);
		Assert.assertEquals("ring count", sizes.length, ringList.size());
	}

}
