package org.contentmine.image.plot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRingList;
import org.junit.Assert;
import org.junit.Test;

import boofcv.io.image.UtilImageIO;

public class CochraneTest {

	private static final Logger LOG = Logger.getLogger(CochraneTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testExtract2LinePlot() {
		File imageFile = new File(ImageAnalysisFixtures.PLOT_DIR, "cochrane/xyplot2.png");
		File targetDir = ImageAnalysisFixtures.TARGET_PLOT_DIR;
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setBinarize(true);
		imageProcessor.processImage(image);
		BufferedImage image1 = imageProcessor.getBinarizedImage();
		ImageIOUtil.writeImageQuietly(image1, new File(targetDir, "cochrane/xyplot2.png"));

	}

	@Test
	public void testExtract2LinePlotNoThin() {
		File imageFile = new File(ImageAnalysisFixtures.PLOT_DIR, "cochrane/xyplot2.png");
		File targetDir = ImageAnalysisFixtures.TARGET_PLOT_DIR;
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.setBinarize(true);
		imageProcessor.processImage(image);
		BufferedImage image1 = imageProcessor.getBinarizedImage();
		ImageIOUtil.writeImageQuietly(image1, new File(targetDir, "cochrane/nothin/xyplot2.png"));

	}

	@Test
	/** create points
	 * 
	 */
	public void testCreatePointsOld() {
		PixelIslandList points = ImageProcessor
				.createDefaultProcessorAndProcess(new File(ImageAnalysisFixtures.COMPOUND_DIR, "components/points.png"))
				.getOrCreatePixelIslandList();
		List<PixelRingList> pixelRingListList = points.createRingListList();
		Assert.assertEquals("characters", 4, points.size());
		PlotTest.drawRings(pixelRingListList, new File("target/plot/points0.svg"));
	}

	


}
