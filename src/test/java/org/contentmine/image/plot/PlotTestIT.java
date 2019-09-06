package org.contentmine.image.plot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRingList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import boofcv.io.image.UtilImageIO;

public class PlotTestIT {

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

	/**
	 * Image from PLoSONE as archetypal X-Y plot.
	 * 
	 * 
	 * journal.pone.0095565.g002.png. The plot has: * x and y axes (each with
	 * ticks, numbers and title) y - axis has horizontal numbers but rotated
	 * title
	 * points with error bars 
	 * best-fit line
	 * 
	 * Note that the antialiasing is severe and occasionally bleeds between
	 * characters
	 *
	 * @throws IOException
	 */
	@Test
	public void testDefaultSharkPlot() throws IOException {
		File g002 = new File(G002_DIR, "g002.png");
		BufferedImage image = UtilImageIO.loadImage(g002.toString());
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessorAndProcess(image);
		ImageIOUtil.writeImageQuietly(image, new File("target/" + SHARK + "/raw.png"));
		BufferedImage defaultBinaryImage = imageProcessor.getImage();
		ImageIOUtil.writeImageQuietly(defaultBinaryImage, new File("target/"
				+ SHARK + "/defaultBinary.png"));
		// by default it thins it
		Assert.assertEquals("pixels", 89, imageProcessor
				.getOrCreatePixelIslandList().size());
		int pixels = imageProcessor.getOrCreatePixelIslandList().getPixelList()
				.size();
	}


	

	// =========================

	static void plotRingsAndThin(File infile, File outfile1, File outfile2)
			throws IOException {
		PixelIslandList plot = ImageProcessor.createDefaultProcessorAndProcess(
				infile).getOrCreatePixelIslandList();
		List<PixelRingList> pixelRingListList = plot.createRingListList();
		PlotTest.drawRings(pixelRingListList, outfile2);
	}

	private static void assertSizes(PixelRingList ringList, int[] sizes) {
		Assert.assertNotNull("ringList", ringList);
		Assert.assertNotNull("sizes", sizes);
		Assert.assertEquals("ring count", sizes.length, ringList.size());
	}

}
