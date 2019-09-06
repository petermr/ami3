package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.junit.Test;

import junit.framework.Assert;

public class RGBMatrixTest {
	private static final Logger LOG = Logger.getLogger(RGBMatrixTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test 
	/** rgb values
	 * 
	 */
	public void testRGB() throws IOException {
		/**
		BufferedImage  image = ImageUtil.readImage(new File(ImageAnalysisFixtures.COMPOUND_DIR, "journal.pone.0095816.g002.png"));
		RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
		IntMatrix redMatrix = rgbMatrix.getMatrix(ImageUtil.RED);
		RGBImageMatrix redImageMatrix = new RGBImageMatrix();
		BufferedImage newImage = redMatrix.createImage(image.getType());

		rgbMatrix.invertRgb();
		redImageMatrix = rgbMatrix.getMatrix(ImageUtil.RED);
//		RGBMatrix.debug(red);
 */
 	}
	
	/** apply simple sharpening function to image.
	 * WARNING. The result is not right.
	 * @throws IOException 
	 * 
	 * 
	 */
	@Test
	public void testSharpenImage() throws IOException {
		BufferedImage newImage = null;
		IntArray array = new IntArray(new int[]{1, 1, 1});
		array = ImageUtil.SHARPEN_ARRAY;
//		array = new IntArray(new int[]{-1, 2, -1});
		array = new IntArray(new int[]{-1, 10, -1});
//		array = ImageUtil.IDENT_ARRAY;
//		array = ImageUtil.DOUBLE_ARRAY;
//		array = ImageUtil.SMEAR_ARRAY;
//		array = ImageUtil.EDGE_ARRAY;
		BufferedImage  image = ImageUtil.readImage(new File(ImageAnalysisFixtures.COMPOUND_DIR, "journal.pone.0095816.g002.png"));
		if (image != null) {
			Assert.assertEquals(13, image.getType());
			RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
			RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilter(array);
//			rgbMatrix1 = rgbMatrix;
			newImage = rgbMatrix1.createImage(image.getType());
		}
		ImageIOUtil.writeImageQuietly(newImage, new File("target/sharpen/sharpened.png"));
	}

	/** apply filter to array.
	 * 
	 */
	@Test
	public void testSharpenImageNew() throws IOException {
		BufferedImage newImage = null;
		IntArray array = new IntArray(new int[]{-1, 3, -1});
		BufferedImage  image = ImageUtil.readImage(new File(ImageAnalysisFixtures.COMPOUND_DIR, "journal.pone.0095816.g002.png"));
		if (image != null) {
			Assert.assertEquals(13, image.getType());
			RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
			RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilterNew(array);
			newImage = rgbMatrix1.createImage(image.getType());
		}
		ImageIOUtil.writeImageQuietly(newImage, new File("target/sharpen/sharpenedNew.png"));
	}

	/** apply filter to array.
	 * 
	 */
	@Test
	public void testSharpenImageNew1() throws IOException {
		BufferedImage newImage = null;
//		IntArray array = new IntArray(new int[]{-1, 3, -1});
		IntArray array = new IntArray(new int[]{-1, -3, 0, 2, 5, 2, 0, -3, -1});
		array = new IntArray(new int[]{0, 1, 0});
		BufferedImage  image = ImageUtil.readImage(new File(SVGHTMLFixtures.EARLY_CHEM_DIR, "adrenaline0.png"));
		if (image != null) {
			Assert.assertEquals(6, image.getType());
			RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
			RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilterNew(array);
			rgbMatrix1 = rgbMatrix1.applyFilterNew(array);
			newImage = rgbMatrix1.createImage(RGBImageMatrix.TYPE13);
		}
		ImageIOUtil.writeImageQuietly(newImage, new File(
				SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0Sharp.png"));
	}

	@Test
	/** image is rather fuzzy
	 * 
	 * @throws IOException
	 */
	public void testLaplacianFilterOnImage() throws IOException {
		BufferedImage newImage = null;
		RealMatrix filter = new RealMatrix(
				// sharpen
				new double[][] {
					new double[] {-1./9.,-1./9.,-1./9.,}, 
					new double[] {-1./9., 17./9.,-1./9.,}, 
					new double[] {-1./9.,-1./9.,-1./9.,}, 
				}
				);
		BufferedImage  image = ImageUtil.readImage(new File(SVGHTMLFixtures.EARLY_CHEM_DIR, "adrenaline0.png"));
		if (image != null) {
			RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
			RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilter(filter);
			newImage = rgbMatrix1.createImage(RGBImageMatrix.TYPE13);
			ImageIOUtil.writeImageQuietly(newImage, new File(
					SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0NewSharp.png"));
		}
	}

	@Test
	public void testIdentityFilterOnImage() throws IOException {
		BufferedImage newImage = null;
		RealMatrix filter = new RealMatrix(
				new double[][] {
					new double[] {1.0}, 
				}
				);
		BufferedImage  image = ImageUtil.readImage(new File(SVGHTMLFixtures.EARLY_CHEM_DIR, "adrenaline0.png"));
		if (image != null) {
			RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
			RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilter(filter);
			newImage = rgbMatrix1.createImage(RGBImageMatrix.TYPE13);
			ImageIOUtil.writeImageQuietly(newImage, new File(
					SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0Identity.png"));
		}
	}

	@Test
	public void testSobellFilter() throws IOException {
		BufferedImage newImage = null;
		RealMatrix filter = new RealMatrix(
				new double[][] {
					new double[] {1.0, 0.0, -1.0}, 
					new double[] {2.0, 0.0, -2.0}, 
					new double[] {1.0, 0.0, -1.0}, 
				}
				);
		BufferedImage  image = ImageUtil.readImage(new File(SVGHTMLFixtures.EARLY_CHEM_DIR, "adrenaline0.png"));
		if (image != null) {
			RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
			RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilter(filter);
			newImage = rgbMatrix1.createImage(RGBImageMatrix.TYPE13);
			ImageIOUtil.writeImageQuietly(newImage, new File(
					SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0Sobelx.png"));
		}
	}
	

}
