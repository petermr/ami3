package org.contentmine.image.plot.forest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.colour.RGBColor;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import boofcv.io.image.UtilImageIO;
import junit.framework.Assert;

public class ForestPlotTest {
	private static final Logger LOG = Logger.getLogger(ForestPlotTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** gray - will need some processing
	 * 
	 */
	public void testPMC2788519Medium() {
		String fileRoot = "PMC2788519";
		String plotType = "forest";
		File imageFile = new File(ImageAnalysisFixtures.DIAGRAMS_DIR, plotType+"/"+ fileRoot + ".png");
		Assert.assertTrue("file exists "+imageFile, imageFile.exists());
//		Assert.assertEquals("image size", 22, (int)FileUtils.sizeOf(imageFile));
		File targetDir = new File(ImageAnalysisFixtures.TARGET_DIR, plotType);
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		Assert.assertNotNull("image read", image);
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.setThreshold(180);
//		imageProcessor.setBinarize(false);
		imageProcessor.setBinarize(true);
		imageProcessor.processImage(image);
		BufferedImage image1 = imageProcessor.getBinarizedImage();
		ImageIOUtil.writeImageQuietly(image1, new File(targetDir, fileRoot + "/rawgray.png"));
	}
	
	@Test
	/** blue posterized
	 * 
	 */
	public void testBlue() {
		String fileRoot = "blue";
		String plotType = "forest";
		File imageFile = new File(ImageAnalysisFixtures.DIAGRAMS_DIR, plotType + "/"+fileRoot+".png");
		LOG.trace("image file "+imageFile);
		Assert.assertTrue("file exists "+imageFile, imageFile.exists());
		int nvalues = 4; // i.e. 16-bit color
		nvalues = 2;
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		File file = new File("target/"+plotType+"/"+fileRoot+"/"+"posterize.png");
		LOG.trace("posterized file "+file);
		ImageIOUtil.writeImageQuietly(image, file);

		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		Multiset<RGBColor> colorSet = colorAnalyzer.getOrCreateColorSet();
		List<RGBColor> colorList = new ArrayList<RGBColor>(colorSet);
		
		Multiset<Object> colorSet0 = HashMultiset.create();
		colorSet0.addAll(colorList);
		Iterable<Multiset.Entry<Object>> colorIterable = MultisetUtil.getEntriesSortedByCount(colorSet0);
		for (Entry<Object> entry : colorIterable) {
			int count = entry.getCount(); 
			int rgb = ((RGBColor) entry.getElement()).getRGBInteger();
			int v = rgb % 0x010101;
			if (v == 0) {
//				LOG.debug("GRAY");
			} else {
//				System.out.println(entry+"/"+count); 
			}
		}
		ImageIOUtil.writeImageQuietly(image, new File("target/posterize.png"));

	}


}
