package org.contentmine.image.plot;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.junit.Test;

public class CochraneIT {
	private static final Logger LOG = Logger.getLogger(CochraneIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
		/** create points
		 * 
		 */
		public void testCreatePoints() {
			ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
			imageProcessor.setThinning(null);
	//		imageProcessor.setThreshold(140);
	//		imageProcessor.setBinarize(true);
			File imageFile = new File(ImageAnalysisFixtures.PLOT_DIR, "cochrane/xyplot2.png");
			imageProcessor.readAndProcessFile(imageFile);
			PixelIslandList pixelIslandList = imageProcessor.getOrCreatePixelIslandList();
			List<PixelRingList> pixelRingListList = pixelIslandList.createRingListList();
	//		Assert.assertEquals("characters", 178, points.size());
			PlotTest.drawRings(pixelRingListList, new File("target/plot/points00.svg"));
			PixelRingListComparator pixelRingListComparator = new PixelRingListComparator();
			Collections.sort(pixelRingListList, pixelRingListComparator);
//			pixelRingListList.sort((Comparator<PixelRingList>)pixelRingListComparator);
			Collections.reverse(pixelRingListList);
			for (PixelRingList pixelRingList : pixelRingListList) {
				LOG.trace(pixelRingList.get(0).size());
			}
			PixelRingList pixelRingList = pixelRingListList.get(0);
			SVGG g = null;
			pixelRingList.plotRings(g, new String[] {"red", "cyan", "purple", "yellow", "blue", "pink", "green"});
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/plot/cochrane/rings", "allRings.svg"));
			int i = 0;
	//		for (PixelList pixelRing : pixelRingList) {
	//			g = null;
	//			g = pixelRing.plotPixels(g, "red");
	//			SVGSVG.wrapAndWriteAsSVG(g, new File("target/plot/cochrane/rings", "allRings"+i+".svg"));
	//			LOG.debug("i "+i);
	//			i++;
	//		}
			PixelRing pixelRing10 = pixelRingList.get(10);
			LOG.trace("PR "+pixelRing10);
			PixelIslandList pl;
	//		PixelIslandList ringIslandList = PixelIslandList.;
		}

}
