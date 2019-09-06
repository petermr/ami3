package org.contentmine.image.diagram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.image.ImageUtil;

import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.filter.derivative.DerivativeType;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.alg.misc.GPixelMath;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertImage;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.core.image.border.BorderType;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;

/**
 * Demonstrates how to convert between different BoofCV image types.
 *
 * @author Peter Abeles
 */
public class ExampleImageConvert {

	// image loaded from a file
	BufferedImage image;
	// gray scale image with element values from 0 to 255
	GrayU8 gray;
	// Derivative of gray image.  Elements are 16-bit signed integers
	GrayS16 derivX,derivY;
//	private ListDisplayPanel panel;
	private File root;

	public ExampleImageConvert() {
		// TODO Auto-generated constructor stub
	}
	
	void createPanel() {
//		panel = new ListDisplayPanel();
//		root = new File("/Users/pm286/workspace/uclforest/forestplots/campbell/pdfimages");

	}
	
	void convert(String imageName) {
		// Converting between BoofCV image types is easy with ConvertImage.  ConvertImage copies
		// the value of a pixel in one image into another image.  When doing so you need to take
		// in account the storage capabilities of these different class types.

		// Going from an unsigned 8-bit image to unsigned 16-bit image is no problem
//		GrayU16 imageU16 = new GrayU16(gray.width,gray.height);
//		ConvertImage.convert(gray,imageU16);

		// You can convert back into the 8-bit image from the 16-bit image with no problem
		// in this situation because imageU16 does not use the full range of 16-bit values
//		ConvertImage.convert(imageU16,gray);

//		// Here is an example where you over flow the image after converting
//		// There won't be an exception or any error messages but the output image will be corrupted
//		GrayU8 imageBad = new GrayU8(derivX.width,derivX.height);
//		ConvertImage.convert(derivX,imageBad);

		// One way to get around this problem rescale and adjust the pixel values so that they
		// will be within a valid range.
		int width = derivX.width;
		int height = derivX.height;
		GrayS16 scaledAbs = new GrayS16(width,height);
		GPixelMath.abs(derivX,scaledAbs);
		GPixelMath.multiply(scaledAbs, 255.0 / ImageStatistics.max(scaledAbs), scaledAbs);

	    GrayU8 binary = new GrayU8(gray.getWidth(), gray.getHeight());
//		ConvertImage.convert(gray,binary);
		GrayF32 imageF32 = new GrayF32(gray.width,gray.height);
		ConvertImage.convert(gray,imageF32);

	    float threshold = 100.f;
	    ThresholdImageOps.threshold(imageF32, binary, threshold, false);
		// If you just want to see the values of a 16-bit image there are built in utility functions
		// for visualizing their values too
	    BufferedImage imageOut = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		ConvertBufferedImage.convertTo(binary, imageOut);
//		BufferedImage colorX = VisualizeImageData.colorizeSign(derivX, null, -1);

		// Let's see what all the bad image looks like
		// ConvertBufferedImage is similar to ImageConvert in that it does a direct coversion with out
		// adjusting the pixel's value
//		BufferedImage outBad = new BufferedImage(imageBad.width,imageBad.height,BufferedImage.TYPE_INT_RGB);
//		BufferedImage outScaled = new BufferedImage(imageBad.width,imageBad.height,BufferedImage.TYPE_INT_RGB);

//		panel.addImage(ConvertBufferedImage.convertTo(scaledAbs,outScaled),"Scaled");
//		panel.addImage(gray,"gray"+imageName);
//		panel.addImage(imageOut,"bin"+imageName);
//		panel.addImage(colorX,"Col"+imageName);
//		panel.addImage(ConvertBufferedImage.convertTo(imageBad,outBad),"Bad");
	}

	/**
	 * Load and generate images
	 */
	public void createImages(String imageName) {
//		image = UtilImageIO.loadImage(UtilIO.pathExample("standard/barbara.jpg"));
		try {
			image = ImageUtil.readImage(new File(root, imageName));
		} catch (RuntimeException e) {
			return;
		}

		gray = ConvertBufferedImage.convertFromSingle(image, null, GrayU8.class);
		derivX = GeneralizedImageOps.createSingleBand(GrayS16.class, gray.getWidth(), gray.getHeight());
		derivY = GeneralizedImageOps.createSingleBand(GrayS16.class, gray.getWidth(), gray.getHeight());

		GImageDerivativeOps.gradient(DerivativeType.SOBEL, gray, derivX, derivY, BorderType.EXTENDED);
	}

	public static void main( String args[] ) {
		ExampleImageConvert app = new ExampleImageConvert();
		
		app.runApp();
	}

	private void runApp() {
		this.createPanel();
		List<File> imageFiles = CMineGlobber.listSortedChildFiles(root, CTree.PNG);
		for (File imageFile : imageFiles) {
			String imageName = FilenameUtils.getName(imageFile.toString());
			this.createImages(imageName);
			this.convert(imageName);
		}
//		ShowImages.showWindow(panel,"Image Convert",true);

	}
}

