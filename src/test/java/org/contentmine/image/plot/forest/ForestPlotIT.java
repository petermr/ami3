package org.contentmine.image.plot.forest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.plot.PixelRingListComparator;
import org.contentmine.image.plot.PlotTest;
import org.junit.Test;

import boofcv.io.image.UtilImageIO;
import junit.framework.Assert;

public class ForestPlotIT {
	private static final Logger LOG = Logger.getLogger(ForestPlotIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** blue posterized
	 * 
	 */
	public void testBlue1() throws IOException {
		String fileRoot = "blue";
		String plotType = "forest";
		File imageFile = new File(ImageAnalysisFixtures.DIAGRAMS_DIR, plotType + "/"+fileRoot+".png");
		LOG.trace("image "+imageFile);
		Assert.assertTrue("file exists "+imageFile, imageFile.exists());
		
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
		colorAnalyzer.readImage(imageFile);
		colorAnalyzer.setOutputDirectory(new File("target/"+fileRoot+"1"));
		LOG.trace("colorAnalyze "+imageFile);
		colorAnalyzer.defaultPosterize();
	
	}

	@Test
		/** blue and black flattened to black
		 * 
		 */
		public void testBlueBlack() {
			String fileRoot = "blue";
			String plotType = "forest";
			File imageFile = new File(ImageAnalysisFixtures.DIAGRAMS_DIR, plotType + "/"+fileRoot+".png");
			Assert.assertTrue("file exists "+imageFile, imageFile.exists());
			File targetDir = new File(ImageAnalysisFixtures.TARGET_DIR, plotType);
			BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
			Assert.assertNotNull("image read", image);
			ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
			imageProcessor.setThinning(null);
			imageProcessor.setBinarize(true);
			imageProcessor.processImage(image);
			BufferedImage image1 = imageProcessor.getBinarizedImage();
			ImageIOUtil.writeImageQuietly(image1, new File(targetDir, fileRoot+"/raw.png"));
			PixelIslandList pixelIslandList = imageProcessor.getOrCreatePixelIslandList();
			List<PixelRingList> pixelRingListList = pixelIslandList.createRingListList();
	//		Assert.assertEquals("characters", 178, points.size());
			PlotTest.drawRings(pixelRingListList, new File(targetDir, fileRoot+"/points00.svg"));
			PixelRingListComparator pixelRingListComparator = new PixelRingListComparator();
			Collections.sort(pixelRingListList, pixelRingListComparator);
//			pixelRingListList.sort(new PixelRingListComparator());
			Collections.reverse(pixelRingListList);
			for (PixelRingList pixelRingList : pixelRingListList) {
				LOG.trace(pixelRingList.get(0).size());
			}
			PixelRingList pixelRingList = pixelRingListList.get(0);
			SVGG g = null;
			pixelRingList.plotRings(g, new String[] {"red", "cyan", "purple", "yellow", "blue", "pink", "green"});
			SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, fileRoot+"/allRings.svg"));
			for (int i = 0; i < pixelRingList.size(); i+=5) {
				PixelRing pixelRing = pixelRingList.get(i);
				g = null;
				g = pixelRing.plotPixels(g, "red");
				SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, fileRoot+"/allRings"+i+".svg"));
			}
			PixelRing pixelRing10 = pixelRingList.get(10);
			PixelIslandList pl;
	//		PixelIslandList ringIslandList = PixelIslandList.;
	
		}

}
