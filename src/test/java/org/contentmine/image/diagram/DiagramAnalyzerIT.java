package org.contentmine.image.diagram;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.colour.ColorFrequenciesMap;
import org.contentmine.image.colour.RGBColor;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.junit.Ignore;
import org.junit.Test;

import boofcv.io.image.UtilImageIO;
import junit.framework.Assert;

/** takes far too long
 * 
 * @author pm286
 *
 */
@Ignore
public class DiagramAnalyzerIT {
	private static final Logger LOG = Logger.getLogger(DiagramAnalyzerIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testFunnelPixelRings() {
		for (String filename : new String[] {
				"funnel1.gif","funnel2.jpg","funnel3.png"		}) {
			String [] filenames = filename.split("\\.");
			File imageFile = new File(ImageAnalysisFixtures.FUNNEL_DIR, filenames[0] + "." + filenames[1]);
			SVGG g = DiagramAnalyzerTest.plotRings(imageFile);
			SVGSVG.wrapAndWriteAsSVG(g, new File(DiagramAnalyzerTest.TARGET_FUNNEL, filenames[0] + ".rings.svg"));
		}
	}

	@Test
	/** messy, because linewidth not analyzed.
	 * 
	 */
	public void testDrainSource2() {
		String filename = "drainsource2.png";
		String [] filenames = filename.split("\\.");
		File imageFile = new File(ImageAnalysisFixtures.ELECTRONIC_DIR, filenames[0] + "." + filenames[1]);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals(40, pixelIslandList.size());
		// smaller islands are characters
		for (int i = 0; i < pixelIslandList.size(); i++) {
			PixelIsland pixelIsland = pixelIslandList.get(i);
			SVGSVG.wrapAndWriteAsSVG(pixelIsland.createSVG(), new File(DiagramAnalyzerTest.TARGET_ELECTRONIC, filenames[0] + "."+i+".svg"));
		}
		// extract segments
		DiagramAnalyzerTest.extractSegments(pixelIslandList, 0);
		DiagramAnalyzerTest.extractSegments(pixelIslandList, 1);
	}

	@Test
	/** tricolor diagram
	 *  3 red/blue/black lines overlapping in some places
	 * 
	 */
	public void testTricolor() {
		String fileroot = "tricolor1";
		File imageFile = new File(ImageAnalysisFixtures.BIO_DIR, fileroot+".png");
		int nvalues = 4; // i.e. 16-bit color
		nvalues = 2; // reduce to 2
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		image = ImageUtil.flattenImage(image, nvalues);
		
		File poster0 = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/poster.orig.png");
		ImageIOUtil.writeImageQuietly(image, poster0);
		
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		image = colorAnalyzer.getBinaryImage();
		File file = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+".binary.png");
		ImageIOUtil.writeImageQuietly(image, file);
		
		SVGG g = colorAnalyzer.createColorFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/colors.orig.svg"));
		
		image = colorAnalyzer.mergeMinorColours(image);
		
		colorAnalyzer = new ColorAnalyzer(image);
		g = colorAnalyzer.createColorFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/colors.svg"));
		file = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+".poster.png");
		ImageIOUtil.writeImageQuietly(image, file);
		
		//flatten color map
		image = colorAnalyzer.mergeMinorColours(image);
		image = colorAnalyzer.mergeMinorColours(image);
		image = colorAnalyzer.mergeMinorColours(image);
			
		colorAnalyzer = new ColorAnalyzer(image);
		g = colorAnalyzer.createColorFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/colors.1.svg"));
		file = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/poster.1.png");
		ImageIOUtil.writeImageQuietly(image, file);
	
		if (1 == 1) return;
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		for (int i = 0; i < pixelIslandList.size(); i++) {
			PixelIsland pixelIsland = pixelIslandList.get(i);
			SVGSVG.wrapAndWriteAsSVG(pixelIsland.createSVG(), new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot + "/island."+i+".svg"));
		}
		// extract segments
		DiagramAnalyzerTest.extractSegments(pixelIslandList, 0);
		DiagramAnalyzerTest.extractSegments(pixelIslandList, 1);
	}

	@Test
	/** 
	 *  red and blue lines. slight overlap
	 */
	public void testCmap2() {
		String fileroot = "cmap2";
		File indir = ImageAnalysisFixtures.BIO_DIR;
		File targetDir = ImageAnalysisFixtures.TARGET_BIO_DIR;
		
		DiagramAnalyzerTest.flattenAndWriteSubImages(fileroot, indir, targetDir, "png");
		
	}

	@Test
	/** bicolor diagram blue/red
	 *   well-separated lines except at left
	 */
	public void testBicolor() {
		String fileroot = "bicolor";
		File indir = ImageAnalysisFixtures.BIO_DIR;
		File targetDir = ImageAnalysisFixtures.TARGET_BIO_DIR;
		
		DiagramAnalyzerTest.flattenAndWriteSubImages(fileroot, indir, targetDir, "png");
		
	}

	@Test
	/** photo of molecule
	 * the background is gray.
	 * the best result is 7f7f7f which shows the molecule as white!
	 * probably need histogram
	 * 
	 */
	public void testMoleculePhoto() {
		String fileroot = "IMG_20131119a";
		File indir = ImageAnalysisFixtures.LINES_DIR;
		File targetDir = ImageAnalysisFixtures.TARGET_LINES_DIR;
		
		DiagramAnalyzerTest.flattenAndWriteSubImages(fileroot, indir, targetDir, "jpg");
		
	}

	@Test
	/** tricolor diagram
	 */
	public void testCmap0() {
		String fileroot = "cmap1";
		File imageFile = new File(ImageAnalysisFixtures.BIO_DIR, fileroot+".png");
		int nvalues = 2;
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		image = ImageUtil.flattenImage(image, nvalues);
		File poster0 = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/poster.orig.png");
		ImageIOUtil.writeImageQuietly(image, poster0);
		
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		SVGG g = colorAnalyzer.createColorFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/colors.orig.svg"));
		
		image = colorAnalyzer.mergeMinorColours(image);
		image = colorAnalyzer.mergeMinorColours(image);
		image = colorAnalyzer.mergeMinorColours(image);
		
		colorAnalyzer = new ColorAnalyzer(image);
		ColorFrequenciesMap colorFrequencies = colorAnalyzer.getOrCreateColorFrequenciesMap();
		for (RGBColor color : colorFrequencies.keySet()) {
			String hex = color.getHex();
			LOG.trace(hex+": "+colorFrequencies.get(color));
			BufferedImage image1 = colorAnalyzer.getImage(color);
			File hexFile = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/.poster."+hex+".png");
			ImageIOUtil.writeImageQuietly(image1, hexFile);
		}
		g = colorAnalyzer.createColorFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/colors.svg"));
		File file = new File(ImageAnalysisFixtures.TARGET_BIO_DIR, fileroot+"/poster.png");
		ImageIOUtil.writeImageQuietly(image, file);
		
	}

	@Test
	/** 
	 *  red black and blue lines. touch at LH end.
	 */
	public void testCmap1() {
		String fileroot = "cmap1";
		File indir = ImageAnalysisFixtures.BIO_DIR;
		File targetDir = ImageAnalysisFixtures.TARGET_BIO_DIR;
		
		DiagramAnalyzerTest.flattenAndWriteSubImages(fileroot, indir, targetDir, "png");
		
	}

	@Test
	public void testDrainSource() {
		String filename = "drainsource.png";
		String [] filenames = filename.split("\\.");
		File imageFile = new File(ImageAnalysisFixtures.ELECTRONIC_DIR, filenames[0] + "." + filenames[1]);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals(115, pixelIslandList.size());
		PixelIsland pixelIsland = pixelIslandList.get(0);
		PixelGraph graph = new PixelGraph(pixelIsland);
		graph.compactCloseNodes(3);
		LOG.trace(graph);
	}

	@Test
	/**
	 * Single line, segmented OK
	 */
	public void testDrainSource1() {
		String filename = "drainsource1.png";
		String [] filenames = filename.split("\\.");
		File imageFile = new File(ImageAnalysisFixtures.ELECTRONIC_DIR, filenames[0] + "." + filenames[1]);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals(11, pixelIslandList.size());
		for (int i = 0; i < pixelIslandList.size(); i++) {
			PixelIsland pixelIsland = pixelIslandList.get(i);
			SVGSVG.wrapAndWriteAsSVG(pixelIsland.createSVG(), new File(DiagramAnalyzerTest.TARGET_ELECTRONIC, filenames[0] + "."+i+".svg"));
		}
		// extract segments
		DiagramAnalyzerTest.extractSegments(pixelIslandList, 0);
		DiagramAnalyzerTest.extractSegments(pixelIslandList, 1);
		
		// analyze pixels
		int nvalues = 4; // i.e. 16-bit color
		nvalues = 2;
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		image = ImageUtil.flattenImage(image, nvalues);
		ImageIOUtil.writeImageQuietly(image, new File(DiagramAnalyzerTest.TARGET_ELECTRONIC, filenames[0] + "."+"colors"+".png"));
	
	}

	


	// =========================================
	
	



	//======================================
	
	
	
	
}

