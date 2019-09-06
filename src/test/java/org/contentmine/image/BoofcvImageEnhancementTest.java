package org.contentmine.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;

public class BoofcvImageEnhancementTest {
	private static final Logger LOG = Logger.getLogger(BoofcvImageEnhancementTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/**
	 * Demonstration of various ways an image can be "enhanced".  Image enhancement typically refers to making it easier
	 * for people to view the image and pick out its details.
	 *
	 * @author Peter Abeles
	 */

	private static File outdir = new File("target/image/");

	/**
	 * Histogram adjustment algorithms aim to spread out pixel intensity values uniformly across the allowed range.
	 * This if an image is dark, it will have greater contrast and be brighter.
	 */
	// NOT TESTED
	public static void histogram(BufferedImage buffered) {
//			BufferedImage buffered = UtilImageIO.loadImage(UtilIO.pathExample(imagePath));
		GrayU8 gray = ConvertBufferedImage.convertFrom(buffered,(GrayU8)null);
		GrayU8 adjusted = gray.createSameShape();

		int size = 256;
		int histogram[] = new int[size];
		int transform[] = new int[256];
//
//		ListDisplayPanel panel = new ListDisplayPanel();

		ImageStatistics.histogram(gray,0, histogram);
		EnhanceImageOps.equalize(histogram, transform);
		EnhanceImageOps.applyTransform(gray, transform, adjusted);
//		panel.addImage(ConvertBufferedImage.convertTo(adjusted, null), "Global");

		EnhanceImageOps.equalizeLocal(gray, 50, adjusted, /*size,*/ histogram, new int[0]);
//		panel.addImage(ConvertBufferedImage.convertTo(adjusted,null),"Local");

//		panel.addImage(ConvertBufferedImage.convertTo(gray, null), "Original");

	}

	/**
	 * When an image is sharpened the intensity of edges are made more extreme while flat regions remain unchanged.
	 * @throws IOException 
	 */
	public static void sharpen(BufferedImage buffered) throws IOException {
		
//			BufferedImage buffered = UtilImageIO.loadImage(UtilIO.pathExample(imagePath));
		GrayU8 gray = ConvertBufferedImage.convertFrom(buffered,(GrayU8)null);
		GrayU8 adjusted = gray.createSameShape();


//		ListDisplayPanel panel = new ListDisplayPanel();

		EnhanceImageOps.sharpen4(gray, adjusted);
		BufferedImage sharpen4 = ConvertBufferedImage.convertTo(adjusted,null);
		ImageIO.write(sharpen4, "png", new File(outdir, "sharpen4.png"));
//			panel.addImage(sharpen4,"Sharpen-4");

		EnhanceImageOps.sharpen8(gray, adjusted);
		BufferedImage sharpen8 = ConvertBufferedImage.convertTo(adjusted,null);
		ImageIO.write(sharpen8, "png", new File(outdir, "sharpen8.png"));
//			panel.addImage(ConvertBufferedImage.convertTo(adjusted,null),"Sharpen-8");

		BufferedImage grayImage = ConvertBufferedImage.convertTo(gray,null);
		ImageIO.write(grayImage, "png", new File(outdir, "original.png"));
//		panel.addImage(gray,"Original");

//			panel.setPreferredSize(new Dimension(gray.width,gray.height));
//			mainPanel.addItem(panel, "Sharpen");
	}

	public static void main( String args[] ) throws IOException
	{
		outdir.mkdirs();
		File imageFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_PLOT_DIR, "plos/journal.pone.0094172/g002-2/figure.png");
		BufferedImage bufferedImage = ImageUtil.readImage(imageFile);
		histogram(bufferedImage);
		LOG.debug("histogram computed");
		sharpen(bufferedImage);
		LOG.debug("sharpen computed");
//		ShowImages.showWindow(mainPanel,"Enhancement",true);
		LOG.debug("displaying??");
	}

}
