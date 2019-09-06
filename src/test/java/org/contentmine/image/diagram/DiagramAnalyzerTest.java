package org.contentmine.image.diagram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ColorStore;
import org.contentmine.graphics.svg.util.ColorStore.ColorizerType;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.colour.ColorFrequenciesMap;
import org.contentmine.image.colour.RGBColor;
import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.pixel.PixelSegment;
import org.contentmine.image.pixel.PixelSegmentList;
import org.junit.Test;

import boofcv.io.image.UtilImageIO;
import junit.framework.Assert;

public class DiagramAnalyzerTest {
	public static final Logger LOG = Logger.getLogger(DiagramAnalyzerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static String FUNNEL = "funnel";
	public static File TARGET_FUNNEL = new File(CMineFixtures.TARGET_DIR, FUNNEL+"/");
	public static String ELECTRONIC = "electronic";
	public static File TARGET_ELECTRONIC = new File(CMineFixtures.TARGET_DIR, ELECTRONIC+"/");
	public static String DIAGRAM_ANALYZER = "diagramAnalyzer";
	public static File TARGET_DIAGRAM_ANALYZER = new File(CMineFixtures.TARGET_DIR, DIAGRAM_ANALYZER);
	
	@Test
	public void testFunnelSegments() {
		
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		String filename[] = (FUNNEL + "1.gif").split("\\.");
//		String filename[] = "funnel2.jpg".split("\\.");
//		String filename[] = "funnel3.png".split("\\.");
		File imageFile = new File(ImageAnalysisFixtures.FUNNEL_DIR, filename[0] + "." + filename[1]);
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals("islands",  14, pixelIslandList.size());
		SVGG g = new SVGG();
		// pixels
		Iterator<String> iterator = ColorStore.getColorIterator(ColorizerType.CONTRAST);
		int[] sizes = new int[] {3560,71,48,48,48,48,47,47,47,47,47,47,47,47};
		int[] nodeCounts = new int[] {33,4,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		int[] edgeCounts = new int[] {36,4,1,1,1,1,1,1,1,1,1,1,1,1};
		int[][] edgeSegmentCounts = new int[][] {
			new int[]{1,2,1,1,1,2,1,1,1,2,1,1,1,4,1,6,4,3,5,1,4,2,6,2,5,3,3,3,3,3,3,3,3,5,8,5},
			new int[]{1,1,4,3},
			new int[]{8},
	        new int[]{9},
	        new int[]{8},
	        new int[]{9},
	        new int[]{8},
	        new int[]{8},
	        new int[]{8},
	        new int[]{8},
	        new int[]{9},
	        new int[]{8},
	        new int[]{8},
	        new int[]{10},
			};
		Boolean[] cyclic = new Boolean[] {false,false,true,true,true,true,true,true,true,true,true,true,true,true};
		for (int isl = 0; isl < pixelIslandList.size(); isl++) {
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
		SVGSVG.wrapAndWriteAsSVG(g, new File(TARGET_FUNNEL, filename[0] + ".segments1.svg"));
	}

	@Test
	public void testFunnelSegments2a() {
		for (String filename : new String[]{/*"funnel1.gif", */"funnel3.png"}) {
			DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
			thinAndElements(filename, diagramAnalyzer);
		}
	}

	private void thinAndElements(String filename, DiagramAnalyzer diagramAnalyzer) {
		String filefix[] = filename.split("\\.");
		File imageFile = new File(ImageAnalysisFixtures.FUNNEL_DIR, filefix[0] + "." + filefix[1]);
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		SVGG g = new SVGG();
		// pixels
		for (int isl = 0; isl < pixelIslandList.size(); isl++) {
			PixelIsland island = pixelIslandList.get(isl);
			PixelGraph graph = new PixelGraph(island);
			graph.doEdgeSegmentation();
			graph.isSingleCycle();
			SVGG gg = graph.normalizeSVGElements();
			g.appendChild(gg);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(TARGET_FUNNEL, filefix[0] + ".segments2a.svg"));
	}

	@Test
	public void testCrossing() {
		String filename = "crossing1.png";
		String [] filenames = filename.split("\\.");
		File imageFile = new File(ImageAnalysisFixtures.FUNNEL_DIR, filenames[0] + "." + filenames[1]);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals(4, pixelIslandList.size());
		PixelIsland pixelIsland = pixelIslandList.get(0);
		PixelGraph graph = new PixelGraph(pixelIsland);
		graph.compactCloseNodes(3);
		LOG.trace(graph);
		Assert.assertEquals(4,  graph.getOrCreateNodeList().size());
		Assert.assertEquals(5,  graph.getOrCreateEdgeList().size());
		PixelIslandList newIslandLists = graph.resolveCyclicCrossing();
		SVGSVG.wrapAndWriteAsSVG(pixelIsland.createSVG(), new File(TARGET_FUNNEL, filenames[0] + ".svg"));

	}

	@Test
	/** black gray red darkgreen
	 *  4 well-separated lines
	 */
	public void testJV7() {
		if (true) {
			LOG.error("***************MISSING FILES FIXME**************");
			return;
		}
		String fileroot = "JV_7";
		File indir = new File(ImageAnalysisFixtures.PLOT_DIR, "rscopen");
		File targetDir = new File(ImageAnalysisFixtures.TARGET_PLOT_DIR, "rscopen/");		
		flattenAndWriteSubImages(fileroot, indir, targetDir, "png");
		
	}
	
	@Test
	/** 4 clean antialiased lines
	 *  fairly well-separated lines
	 */
	public void testJV4() {
		if (true) {
			LOG.error("***************MISSING FILES FIXME**************");
			return;
		}
		String fileroot = "JV_4";
		File indir = new File(ImageAnalysisFixtures.PLOT_DIR, "rscopen");
		File targetDir = new File(ImageAnalysisFixtures.TARGET_PLOT_DIR, "rscopen/");
		
		flattenAndWriteSubImages(fileroot, indir, targetDir, "gif");
		
	}

	@Test
	/** 
	 *  red black and blue lines. touch at LH end.
	 */
	public void testTricoloRaw() {
		String fileroot = "tricolor.raw";
		File indir = ImageAnalysisFixtures.BIO_DIR;
		File targetDir = ImageAnalysisFixtures.TARGET_BIO_DIR;
		
		flattenAndWriteSubImages(fileroot, indir, targetDir, "png");
		
	}
	


	// relies on previous test
	@Test
	public void testMergeImages() {
		String fileroot = "tricolor.raw";
		File indir = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot);
		File outdir = indir;
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
		
		// red
		File imageFile1 = new File(indir, "poster.#7f0000.png");
		File imageFile2 = new File(indir, "poster.#ff7f7f.png");
		BufferedImage mergeImage = colorAnalyzer.mergeImages(imageFile1, imageFile2);
		ImageIOUtil.writeImageQuietly(mergeImage, new File(outdir, "merge_7f0000_ff7f7f.png"));
		mergeImage = colorAnalyzer.mergeImages(imageFile1, imageFile2);
		
		// blue
		imageFile1 = new File(indir, "poster.#007f7f.png");
		imageFile2 = new File(indir, "poster.#7f7fff.png");
		mergeImage = colorAnalyzer.mergeImages(imageFile1, imageFile2);
		ImageIOUtil.writeImageQuietly(mergeImage, new File(outdir, "merge_007f7f_7f7fff.png"));
		mergeImage = colorAnalyzer.mergeImages(imageFile1, imageFile2);
		
		// black
		imageFile1 = new File(indir, "poster.#7f7f7f.png");
		imageFile2 = new File(indir, "poster.#000000.png");
		mergeImage = colorAnalyzer.mergeImages(imageFile1, imageFile2);
		ImageIOUtil.writeImageQuietly(mergeImage, new File(outdir, "merge_7f7f7f_ffffff.png"));
		mergeImage = colorAnalyzer.mergeImages(imageFile1, imageFile2);
	}

	@Test
	/** create a DiagramAnalyzer from PixelList
	 * all pixels will be black (000000)
	 * 
	 */
	public void testCreateFromPixels() {
		int width = 5;
		int height = 7;	
		PixelList pixelList = new PixelList();
		for (int y = 0; y < height - 2; y++) {
			int x = y;
			pixelList.add(new Pixel(x, y ));
			pixelList.add(new Pixel(x, y+1 ));
			pixelList.add(new Pixel(x, y+2 ));
		}
		SVGSVG.wrapAndWriteAsSVG(pixelList.getOrCreateSVG(), new File(TARGET_DIAGRAM_ANALYZER, "diagonal.svg"));
		DiagramAnalyzer diagramAnalyzer = DiagramAnalyzer.createDiagramAnalyzer(width, height, pixelList);
		BufferedImage image = diagramAnalyzer.getImage();
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sb.append("   "+Integer.toHexString(image.getRGB(x, y)).substring(0,  1));
			}
			sb.append("\n");
		}
		Assert.assertEquals(
			"   0   f   f   f   f\n"
+ "   0   0   f   f   f\n"
+ "   0   0   0   f   f\n"
+ "   f   0   0   0   f\n"
+ "   f   f   0   0   0\n"
+ "   f   f   f   0   0\n"
+ "   f   f   f   f   0\n",
sb.toString());
		LOG.debug("\n"+sb);
		ImageIOUtil.writeImageQuietly(image, new File(TARGET_DIAGRAM_ANALYZER, "diagonal.png"));

	}


	// =========================================
	
	static void flattenAndWriteSubImages(String fileroot, File indir, File targetDir, String suffix) {
		File imageFile = new File(indir, fileroot+"."+suffix);
		File outdir = new File(targetDir, fileroot+"/");
		Assert.assertTrue(""+imageFile+" exists", imageFile.exists());
		int nvalues = 2;
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		if (image == null) {
			throw new RuntimeException("null image");
		}
		image = ImageUtil.flattenImage(image, nvalues);
		File poster0 = new File(outdir, "poster.orig.png");
		ImageIOUtil.writeImageQuietly(image, poster0);
		
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		// write binary image
		BufferedImage image1 = colorAnalyzer.getBinaryImage();
		File file = new File(outdir, "binary.png");
		ImageIOUtil.writeImageQuietly(image1, file);
		
		colorAnalyzer.readImage(image);
		SVGG g = colorAnalyzer.createColorFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(outdir, "colors.orig.svg"));
		
		image = colorAnalyzer.mergeMinorColours(image);
		image = colorAnalyzer.mergeMinorColours(image);
		image = colorAnalyzer.mergeMinorColours(image);
		
		colorAnalyzer = new ColorAnalyzer(image);
		ColorFrequenciesMap colorFrequencies = colorAnalyzer.getOrCreateColorFrequenciesMap();
		for (RGBColor color : colorFrequencies.keySet()) {
			String hex = color.getHex();
			LOG.trace(hex+": "+colorFrequencies.get(color));
			BufferedImage image2 = colorAnalyzer.getImage(color);
			File hexFile = new File(outdir, "poster."+hex+".png");
			ImageIOUtil.writeImageQuietly(image2, hexFile);
		}
		g = colorAnalyzer.createColorFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(outdir, "colors.svg"));
		file = new File(outdir, "poster.png");
		ImageIOUtil.writeImageQuietly(image, file);
	}


	//======================================
	
	static void extractSegments(PixelIslandList pixelIslandList, int serial) {
		PixelEdge edge = pixelIslandList.get(serial).getOrCreateGraph().getOrCreateEdgeList().get(0);
		PixelEdge edge1 = edge.cyclise();
		edge = edge1 == null ? edge : edge1;
		LOG.trace("edge "+edge);
		LOG.trace("cycle "+edge.isCyclic());
		LOG.trace("node "+edge.getNodes().size()+"; "+edge.getNodes());
		PixelSegmentList segmentList = edge.getOrCreateSegmentList(1.0);
		LOG.trace("S: "+segmentList.size()+"; "+segmentList);
		SVGElement g = segmentList.getOrCreateSVG();
		for (PixelSegment segment : segmentList) {
			plotPoint(g, segment, 0);
		}
		plotPoint(g, segmentList.getLast(), 1);
		SVGSVG.wrapAndWriteAsSVG(g, new File(TARGET_ELECTRONIC, "edge" + "."+serial+".svg"));
	}

	private static void plotPoint(SVGElement g, PixelSegment segment, int serial) {
		SVGLine line = segment.getSVGLine();
		SVGCircle c = new SVGCircle(line.getXY(serial), 3.0);
		g.appendChild(c);
	}
	
	static SVGG plotRings(File imageFile) {
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.readAndProcessFile(imageFile);
		PixelIslandList islandList = imageProcessor.getOrCreatePixelIslandList();
		SVGG g = new SVGG();
		// pixels
		Iterator<String> iterator = ColorStore.getColorIterator(ColorizerType.CONTRAST);
		for (PixelIsland island : islandList) {
			PixelRingList pixelRingList = island.getOrCreateInternalPixelRings();
			for (PixelRing pixelRing : pixelRingList) {
				SVGG gg = pixelRing.getOrCreateSVG();
				gg.setCSSStyle("stroke-width:1.0;stroke:"+iterator.next()+";");
//				g.appendChild(gg);
			}
			PixelRing outline = pixelRingList.getOuterPixelRing();
			if (outline != null) {
				SVGG gg = outline.getOrCreateSVG();
				gg.setCSSStyle("stroke-width:0.2;stroke:"+"black"+"; fill: none;");
				g.appendChild(gg);
			}
		}
		return g;
	}
	
	
}

