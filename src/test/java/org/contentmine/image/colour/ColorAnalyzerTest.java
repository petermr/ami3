package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import boofcv.io.image.UtilImageIO;
import junit.framework.Assert;

/** classifies the colours used in a diagram.
 * 
 * @author pm286
 *
 */
public class ColorAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(ColorAnalyzerTest.class);
	
	
	@Test
		public void testPosterize() {
			int nvalues = 4; // i.e. 16-bit color
			nvalues = 2;
			BufferedImage image = ImageUtil.readImage(new File(ImageAnalysisFixtures.PROCESSING_DIR, "phylo.jpg"));
			ImageUtil.flattenImage(image, nvalues);
			ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
			Multiset<RGBColor> set = colorAnalyzer.getOrCreateColorSet();
			for (Entry<RGBColor> entry : set.entrySet()) {
//				System.out.println(entry+"  "+entry.getCount()); 
			}
			ImageIOUtil.writeImageQuietly(image, new File("target/posterize.png"));
		}

	@Test
	/** LONG
	 * 
	 * @throws IOException
	 */
	@Ignore // LONG
	public void testPosterizePhylo() throws IOException {
		testPosterize0("phylo");
	}

	@Test
	@Ignore
	public void testPosterize22249() throws IOException {
		testPosterize0("22249");
	}

	@Test
	@Ignore
	public void testPosterize36933() throws IOException {
		testPosterize0("36933");
	}

	@Test
	public void testPosterizeSpect2() throws IOException {
		testPosterize0("spect2");
	}

	@Test
	/** photo of molecule
	 * the background is gray.
	 * create histogram based on greyvalues
	 * 
	 */
	public void testMoleculePhotoHistogram() {
		File moleculeFile = new File(ImageAnalysisFixtures.LINES_DIR, "IMG_20131119a.jpg");
		File targetDir = ImageAnalysisFixtures.TARGET_LINES_DIR;
		BufferedImage image = UtilImageIO.loadImage(moleculeFile.toString());
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		BufferedImage grayImage = colorAnalyzer.getGrayscaleImage();
		
		for (Integer nvalues : new Integer[]{4,8,16,32,64,128}) {
			BufferedImage imageOut = ImageUtil.flattenImage(grayImage, nvalues);
			colorAnalyzer.readImageDeepCopy(imageOut);
			SVGG g = colorAnalyzer.createGrayScaleFrequencyPlot();
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/histogram/postermol"+nvalues+".hist.svg"));
//			ImageIOUtil.writeImageQuietly(imageOut, new File("target/histogram/postermol"+nvalues+".png"));
		}
		
	}
	
	
	@Test
	/** photo of molecule
	 * the background is gray.
	 * automatic histogram
	 * 
	 */
	public void testMoleculePhotoAutoHistogram() {
		File moleculeFile = new File(ImageAnalysisFixtures.LINES_DIR, "IMG_20131119a.jpg");
		File targetDir = ImageAnalysisFixtures.TARGET_LINES_DIR;
		BufferedImage image = UtilImageIO.loadImage(moleculeFile.toString());
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		BufferedImage grayImage = colorAnalyzer.getGrayscaleImage();
		int nvalues = 128;
		BufferedImage imageOut = ImageUtil.flattenImage(grayImage, nvalues);
		colorAnalyzer.readImageDeepCopy(imageOut);
		BufferedImage filterImage = colorAnalyzer.applyAutomaticHistogram(imageOut);
		SVGG g = colorAnalyzer.createGrayScaleFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/histogram/postermol"+nvalues+".autohist.svg"));
		ImageIOUtil.writeImageQuietly(filterImage, new File("target/histogram/postermol"+".filter"+".png"));
	}
	
	@Test
	/** xy plot with two lines, blue and black.
	 * 
	 */
	public void testCochrane2Lines() {
		File imageFile = new File(ImageAnalysisFixtures.PLOT_DIR, "cochrane/xyplot2.png");
		File targetDir = ImageAnalysisFixtures.TARGET_PLOT_DIR;
		BufferedImage image = UtilImageIO.loadImage(imageFile.toString());
		Assert.assertNotNull("image exists", image);
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		BufferedImage grayImage = colorAnalyzer.getGrayscaleImage();
		int nvalues = 32;
//		int nvalues = 128;
		BufferedImage imageOut = ImageUtil.flattenImage(grayImage, nvalues);
		colorAnalyzer.readImageDeepCopy(imageOut);
		BufferedImage filterImage = colorAnalyzer.applyAutomaticHistogram(imageOut);
		SVGG g = colorAnalyzer.createGrayScaleFrequencyPlot();
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "cochrane/xyplot2.svg"));
		ImageIOUtil.writeImageQuietly(filterImage, new File(targetDir, "cochrane/xyplot2.png"));
	}
	
	


	
	// ================================

	static void testPosterize0(String filename) throws IOException {
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
		colorAnalyzer.readImage(new File(ImageAnalysisFixtures.PROCESSING_DIR, filename+".png"));
		colorAnalyzer.setOutputDirectory(new File("target/"+filename));
		LOG.debug("colorAnalyze "+filename);
		colorAnalyzer.defaultPosterize();
	}

	

}
