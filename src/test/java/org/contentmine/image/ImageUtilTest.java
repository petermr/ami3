package org.contentmine.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Multiset;

public class ImageUtilTest {
	private static final Logger LOG = Logger.getLogger(ImageUtilTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** clip rectangle out of image.
	 * 
	 * @throws IOException
	 */
	public void testClipSubImage() throws IOException {
		BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		Rectangle rect = new Rectangle(20, 50, 60, 85); // x0, y0, w, h
		Raster raster = image.getData(rect);
		Assert.assertEquals(60, raster.getWidth());
		Assert.assertEquals(85, raster.getHeight());
		BufferedImage subImage = new BufferedImage(raster.getWidth(), raster.getHeight(), image.getType());
		subImage.setData(raster);
		ImageIOUtil.writeImageQuietly(subImage, "target/subimage/subImage.png");
	}
	
	@Test
	/** clip a rectangle out of an image.
	 * 
	 * @throws IOException
	 */
	public void testClipSub() throws IOException {
		BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		Int2Range boundingBox = new Int2Range(new IntRange(20, 80), new IntRange(50, 135));
		BufferedImage subImage = ImageUtil.clipSubImage(image, boundingBox);
		ImageIOUtil.writeImageQuietly(subImage, "target/subimage/subImage1.png");
	}

	@Test
	/** shifts image by dx dy.
	 * 
	 */
	public void testReadGrayImage() throws IOException {
		BufferedImage image = ImageUtil.readImage(new File(ImageAnalysisFixtures.CHAR_DIR, "65.png"));
		IntMatrix matrix = ImageUtil.getGrayMatrix(image);
//		System.out.println(matrix);
	}

	@Test
	/** shifts image by dx dy.
	 * 
	 */
	public void testShiftGrayImage() throws IOException {
		BufferedImage image = ImageUtil.readImage(new File(ImageAnalysisFixtures.CHAR_DIR, "65.png"));
		BufferedImage shiftedImage = ImageUtil.shiftImage(image, 0.1, 0.2);
		ImageIOUtil.writeImageQuietly(shiftedImage, "target/shiftscale/shiftedImage.png");
	}

	@Test
	/** scales image.
	 * 
	 */
	public void testScaleAndInterpolate() throws IOException {
		BufferedImage image = ImageUtil.readImage(new File(ImageAnalysisFixtures.CHAR_DIR, "65.png"));
		BufferedImage shiftedImage = ImageUtil.scaleAndInterpolate(image, 17, 13);
		ImageIOUtil.writeImageQuietly(shiftedImage, "target/shiftscale/scaledImage.png");
	}
	
	@Test
	/** invert colors
	 * 
	 */
	public void testInvertRGB() {
		int blackRgb = ImageUtil.setRgb(0, 0, 0);
		int blackFlip = ImageUtil.invertRgb(blackRgb);
		Assert.assertEquals(0x00ffffff, blackFlip);
		int whiteRgb = ImageUtil.setRgb(255, 255, 255);
		int whiteFlip = ImageUtil.invertRgb(whiteRgb);
		Assert.assertEquals(0x00000000, whiteFlip);
		int redRgb = ImageUtil.setRgb(255, 0, 0);
		int redFlip = ImageUtil.invertRgb(redRgb);
		Assert.assertEquals(0x0000ffff, redFlip);
	}
	
	@Test
	public void testScaleImagePMR() throws IOException {
		File imageFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_PLOT_DIR, "plos/journal.pone.0094172/g002-2/figure.png");
		Assert.assertEquals("size",  1460543, FileUtils.sizeOf(imageFile));
		Assert.assertTrue("" + imageFile + " exists", imageFile.exists());
		BufferedImage image = ImageUtil.readImage(imageFile);
		Assert.assertEquals("width",  2976, image.getWidth());
		int scalex = 2;
		int scaley = 2;
		BufferedImage newImage = ImageUtil.scaleImage(image, scalex, scaley);
		File newDir = new File("target/image/plos/journal.pone.0094172/g002-2/");
		newDir.mkdirs();
		File outputFile = new File(newDir, "figure.png");
		ImageIO.write(newImage, CTree.PNG, outputFile);
		Assert.assertEquals("size",  1705925, FileUtils.sizeOf(outputFile));
		BufferedImage newImage1 = ImageUtil.readImage(outputFile);
		Assert.assertEquals("width",  5952, newImage1.getWidth());
	}
	
	@Test
	public void testBinarize() {
		File imageFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_PLOT_DIR, "plos/journal.pone.0094172/g002-2/figure.png");
		BufferedImage image = ImageUtil.readImage(imageFile);
		Multiset<String> hexMultiset = ImageUtil.createHexMultiset(image);
//		LOG.debug("pre-binarized: "+hexMultiset);
		BufferedImage newImage = ImageUtil.boofCVBinarization(image, 180);
		hexMultiset = ImageUtil.createHexMultiset(newImage);
//		LOG.debug("binarized: "+hexMultiset);
//		ImageUtil.writePngQuietly(newImage, new File("target/binarized.png"));
	}
	
	@Test
	public void testGetProjections() {
		File imageFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "PMC5502154.png");
		BufferedImage image = ImageUtil.readImage(imageFile);
		ImageLineAnalyzer lineAnalyzer = new ImageLineAnalyzer(image);
		IntArray blackArray = lineAnalyzer.projectOnto(Axis2.X, 0);
		LOG.debug(blackArray.getSubArray(990, 1010));
		LOG.debug(blackArray.getSubArray(670, 700));
		blackArray = lineAnalyzer.projectOnto(Axis2.Y, 0);
		LOG.debug(blackArray.getSubArray(0, 150));
	}

	@Test
	public void testGetDividers() {
		File imageFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "PMC5502154.png");
		BufferedImage image = ImageUtil.readImage(imageFile);
		ImageLineAnalyzer lineAnalyzer = new ImageLineAnalyzer(image);
		IntArray blackVertical = lineAnalyzer.projectOnto(Axis2.X, 0);
		Map<Integer, Integer> largeVertical = blackVertical.getMapOfValuesOver((int)(0.8*image.getHeight()));
		LOG.debug("vertical:" +largeVertical );
		IntArray blackHorizontal = lineAnalyzer.projectOnto(Axis2.Y, 0);
		double PLOT_SCALE_FRACT = 0.25;
		Map<Integer, Integer> largeHorizontal = blackHorizontal.getMapOfValuesOver((int)(PLOT_SCALE_FRACT*image.getWidth()));
		LOG.debug("horizontal:" +largeHorizontal );
	}

	@Test
	public void testReplaceHorizDivider() {
		File imageFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "PMC5502154.png");
		BufferedImage image = ImageUtil.readImage(imageFile);
		IntArray intArray = new IntArray(new int[] {33,34});
		ImageLineAnalyzer lineAnalyzer = new ImageLineAnalyzer(image);
		lineAnalyzer.replaceRow(intArray);
		ImageUtil.writeImageQuietly(image, new File("target/removeLines.png"));
	}

	@Test
	public void testJoinVerticalImages() {
		File imageFile0 = new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "image.13.1.84_525.183_302.png");
		File imageFile1 = new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "image.13.2.84_525.302_420.png");
		List<BufferedImage> imageList = new ArrayList<>();
		imageList.add(ImageUtil.readImage(imageFile0));
		imageList.add(ImageUtil.readImage(imageFile1));
		BufferedImage image2 = ImageUtil.joinImagesVertically(imageList);
		ImageUtil.writeImageQuietly(image2, new File("target/joinedImages.png"));
	}
	
	@Test
	public void testSplitImages() {
		File imageFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "PMC5502154.png");
		BufferedImage image = ImageUtil.readImage(imageFile);
		// split into Top and Bottom
		List<Integer> borders = Arrays.asList(new Integer[] {34});
		ImageLineAnalyzer lineAnalyzer = new ImageLineAnalyzer(image);
		List<BufferedImage> imageList = lineAnalyzer.splitImageAlong(Axis2.X, borders);
		int nimage = 0;
		for (BufferedImage img : imageList) {
			ImageUtil.writeImageQuietly(img, new File("target/splitImages"+(nimage++)+".png"));
		}
		// split bottom into L and R
		borders = Arrays.asList(new Integer[] {750});
		BufferedImage bottomImage = imageList.get(1);
		lineAnalyzer.setImage(bottomImage);
		List<BufferedImage> imageList1 = lineAnalyzer.splitImageAlong(Axis2.Y, borders);
		LOG.debug(imageList1.size()+"//"+imageList1.get(0)+"//"+imageList1.get(1));
		for (BufferedImage img : imageList1) {
			ImageUtil.writeImageQuietly(img, new File("target/splitImages"+(nimage++)+".png"));
		}
		
		
	}

	@Test
	public void testMultipleVerticalImages() {
		BufferedImage image = ImageUtil.readImage(
				new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "PMC6397911_multiple.png"));
		image = ImageUtil.boofCVBinarization(image, 160);
		double FOREST_SCALE_FRACT = 0.90;
		int HEADER_SIZE = 50;
		ImageLineAnalyzer lineAnalyzer = new ImageLineAnalyzer(image);
		List<BufferedImage> imageList1 = lineAnalyzer.splitAtMajorLines(Axis2.X, FOREST_SCALE_FRACT, HEADER_SIZE);
		LOG.debug(imageList1.size()+"//"+imageList1.get(0)+"//"+imageList1.get(1));
		for (int i = 1; i < imageList1.size(); i++) {
			BufferedImage img = imageList1.get(i);
			ImageUtil.writeImageQuietly(img, new File("target/splitImagesX_"+(i)+".png"));
		}
		
	}

	@Test
	public void testVerticalSplit() {
		BufferedImage image = ImageUtil.readImage(
				new File(ImageAnalysisFixtures.TEST_DIAGRAM_FOREST_DIR, "PMC5502154.png"));
		int colour = 0; // black
		int offset = -10;
		Axis2 axis = Axis2.X;
		int minLength = 300;
		
		ImageLineAnalyzer lineAnalyzer = new ImageLineAnalyzer(image);
		List<BufferedImage> imageList = lineAnalyzer.splitAtLeftOfBottomLine(colour, minLength, offset, axis);
		
		int i = 0;
		for (BufferedImage splitImage : imageList) {
			ImageUtil.writeImageQuietly(splitImage, new File("target/splitImagesY_"+(i++)+".png"));
		}
	}


}
