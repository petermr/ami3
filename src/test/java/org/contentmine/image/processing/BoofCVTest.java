package org.contentmine.image.processing;

import java.io.File;

import org.apache.log4j.Logger;

public class BoofCVTest {

	private static Logger LOG = Logger.getLogger(BoofCVTest.class);

	private File BOOFCV_OUT_DIR;
//	
//	
//	@Before
//	public void setUp() {
//		BOOFCV_OUT_DIR = new File("target/boofcv/");
//		BOOFCV_OUT_DIR.mkdirs();
//	}
//	@Test
//	@Ignore // takes 3 secs
//	public void testInt2() {
//		BufferedImage image = UtilImageIO.loadImage(new File(ImageAnalysisFixtures.PROCESSING_DIR, "postermol.png").toString());
//		ImageUInt8 imageInt2 = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
//		BufferedImage outImage = ConvertBufferedImage.convertTo(imageInt2,null);
//		UtilImageIO.saveImage(outImage, new File(BOOFCV_OUT_DIR, "postermol.png").toString());
//	}
//
//	@Test
//	public void testBinarize() {
//		BufferedImage image = UtilImageIO.loadImage(new File(ImageAnalysisFixtures.PROCESSING_DIR, "postermol.png").toString());
//		ImageUInt8 input = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
//		ImageUInt8 binary = new ImageUInt8(input.getWidth(), input.getHeight());
////		Creates a binary image by thresholding the input image. Binary must be of type ImageUInt8.
//		BufferedImage binaryImage = null;
//		for (int i = 50; i < 130; i+= 20) {
//			ThresholdImageOps.threshold(input, binary, i, true);
//			binaryImage = VisualizeBinaryData.renderBinary(binary,null);
//			UtilImageIO.saveImage(binaryImage, new File(BOOFCV_OUT_DIR, "postermolBinary"+i+".png").toString());
//		}
//		int best = 70;
//		ThresholdImageOps.threshold(input, binary, best, true);
////		Apply an erode operation on the binary image, writing over the original image reference.
//		ImageUInt8 erode8 = BinaryImageOps.erode8(binary,1,null);
//		erode8 = BinaryImageOps.erode8(erode8,1,null);
//		outputBinary(erode8, new File(BOOFCV_OUT_DIR, "postermolErode"+best+"_8.png"));
//		
//		ImageUInt8 output = new ImageUInt8(input.getWidth(), input.getHeight());
////		Apply an erode operation on the binary image, saving results to the output binary image.
////		BinaryImageOps.erode8(binary,output);
////		Apply an erode operation with a 4-connect rule.
//		BinaryImageOps.erode4(binary,1,output);
//		ImageUInt8 erode4 = BinaryImageOps.erode4(binary,1,null);
//		outputBinary(erode4, new File(BOOFCV_OUT_DIR, "postermolErode"+best+"_4.png"));
////		int numBlobs = BinaryImageOps.labelBlobs4(binary,blobs);
////		Detect and label blobs in the binary image using a 4-connect rule. blobs is an image of type ImageSInt32.
////		BufferedImage visualized = VisualizeBinaryData.renderLabeled(blobs, numBlobs, null);
////		Renders the detected blobs in a colored image.
//		BufferedImage outputImage = VisualizeBinaryData.renderBinary(output,null);
////		Renders the binary image as a black white image.		
//	}
//	
//	@Test
//	public void testMoreBinary() {
//		// load and convert the image into a usable format
//		BufferedImage image = UtilImageIO.loadImage(new File(ImageAnalysisFixtures.PROCESSING_DIR, "postermol.png").toString());
// 
//		// convert into a usable format
//		ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
//		ImageUInt8 binary = new ImageUInt8(input.width,input.height);
//		ImageSInt32 label = new ImageSInt32(input.width,input.height);
// 
//		// the mean pixel value is often a reasonable threshold when creating a binary image
//		double mean = ImageStatistics.mean(input);
// 
//		// create a binary image by thresholding
//		ThresholdImageOps.threshold(input,binary,(float)mean,true);
// 
//		// remove small blobs through erosion and dilation
//		// The null in the input indicates that it should internally declare the work image it needs
//		// this is less efficient, but easier to code.
//		ImageUInt8 filtered = BinaryImageOps.erode8(binary,1,null);
//		filtered = BinaryImageOps.dilate8(filtered,1,null);
//		outputBinary(filtered, new File(BOOFCV_OUT_DIR, "dilate8.png"));
// 
//		// Detect blobs inside the image using an 8-connect rule
//		List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, label);
// 
//		// colors of contours
//		int colorExternal = 0xFFFFFF;
//		int colorInternal = 0xFF2020;
// 
//		// display the results
////		BufferedImage visualBinary = VisualizeBinaryData.renderBinary(binary, null);
////		BufferedImage visualFiltered = VisualizeBinaryData.renderBinary(filtered, null);
////		BufferedImage visualLabel = VisualizeBinaryData.renderLabeled(label, contours.size(), null);
////		BufferedImage visualContour = VisualizeBinaryData.renderContours(contours,colorExternal,colorInternal,
////				input.width,input.height,null);
// 
//		// these are not suitable for tests
//		
////		ShowImages.showWindow(visualBinary,"Binary Original");
////		ShowImages.showWindow(visualFiltered,"Binary Filtered");
////		ShowImages.showWindow(visualLabel,"Labeled Blobs");
////		ShowImages.showWindow(visualContour,"Contours");
//	}
// 
//	@Test
//	@Ignore
//	public void testBlobs() {
//		BufferedImage image = UtilImageIO.loadImage(new File(ImageAnalysisFixtures.PROCESSING_DIR, "postermol.png").toString());
//		ImageUInt8 input = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
//		ImageUInt8 binary = new ImageUInt8(input.getWidth(), input.getHeight());
////		Creates a binary image by thresholding the input image. Binary must be of type ImageUInt8.
//		BufferedImage binaryImage = null;
//		for (int i = 10; i < 150; i+= 10) {
//			ThresholdImageOps.threshold(input, binary, i, true);
//			binaryImage = VisualizeBinaryData.renderBinary(binary,null);
//			UtilImageIO.saveImage(binaryImage, new File(BOOFCV_OUT_DIR, "postermolBinary"+i+".png").toString());
//		}
//		int best = 70;
//		ThresholdImageOps.threshold(input, binary, best, true);
////		ImageSInt32 blobs = new ImageSInt32();
////		??
////		int numBlobs = BinaryImageOps.labelBlobs4(binary,blobs);
////		Detect and label blobs in the binary image using a 4-connect rule. blobs is an image of type ImageSInt32.
////		BufferedImage visualized = VisualizeBinaryData.renderLabeled(blobs, numBlobs, null);	
//	}
//	
//	@Test
//	public void testNatprod() {
//		BufferedImage image = UtilImageIO.loadImage(new File(ImageAnalysisFixtures.PROCESSING_DIR, "natprod1.png").toString());
//		ImageUInt8 input = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
//		ImageUInt8 binary = new ImageUInt8(image.getWidth(), input.getHeight());
//		for (int i = 70; i <= 130; i+= 20) {
//			ThresholdImageOps.threshold(input, binary, i, true);
//			BufferedImage binaryImage = VisualizeBinaryData.renderBinary(binary,null);
//			ColorUtilities.flipWhiteBlack(binaryImage);
//			UtilImageIO.saveImage(binaryImage, new File(BOOFCV_OUT_DIR, "natprod1_"+i+".png").toString());
//		}
//	}
//	
//	private static void outputBinary(ImageUInt8 image, File file) {
//		BufferedImage binaryImage = VisualizeBinaryData.renderBinary(image,null);
//		UtilImageIO.saveImage(binaryImage, file.toString());
//	}
	
	
	
	

	
}
