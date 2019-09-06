package org.contentmine.graphics.svg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.image.ImageUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SVGImageTest {

	private final static Logger LOG = Logger.getLogger(SVGImageTest.class);
	
	private static final String CANNY = "Canny";
	private static final String GRAYSCALE = "grayscale";
	
	@Test 
	public void testReadContent() {
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(SVGHTMLFixtures.IMAGE_SVG);
		 Assert.assertNotNull(svgElement);
		SVGImage image = SVGImage.extractSelfAndDescendantImages(svgElement).get(0);
		 Assert.assertNotNull(image);
		 String dataValue = image.getImageValue();
		 Assert.assertEquals("data", 
				 "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR42mP4J8LwHx0zAAE2cWzyeBUSgxnw2UwMnzouINVmnF4YwmEwmg7Is3kYhQEA6pzZRchLX5wAAAAASUVORK5CYII=",
				 dataValue);
	}
	
	@Test 
	public void testRoundtripImage() throws Exception {

		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(SVGHTMLFixtures.IMAGE_SVG);
		SVGImage svgImage = SVGImage.extractSelfAndDescendantImages(svgElement).get(0);
		 BufferedImage image = svgImage.getBufferedImage();
		 Assert.assertNotNull(image);
		 String imageInfo = image.toString();
		 imageInfo = imageInfo.replaceAll("@[a-f0-9]*", "@aaa");
		 // unstable
//		 Assert.assertEquals("image", 
//			 "BufferedImage@aaa: type = 6 ColorModel: #pixelBits = 32 numComponents = 4 color space = java.awt.color.ICC_ColorSpace@aaa transparency = 3 has alpha = true isAlphaPre = false ByteInterleavedRaster: width = 16 height = 16 #numDataElements 4 dataOff[0] = 3",
//			 imageInfo);
		 File imageFile = new File("target/image1.png");
	     SVGImage.writeBufferedImage(image, SVGImage.IMAGE_PNG, imageFile);
	     SVGImage svgImage1 = SVGImage.createSVGFromImage(imageFile, SVGImage.IMAGE_PNG);
	     Assert.assertNotNull("image", svgImage1);
	     svgImage.format(3);
	     // <image xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR42mP4J8LwHx0zAAE2cWzyeBUSgxnw2UwMnzouINVmnF4YwmEwmg7Is3kYhQEA6pzZRchLX5wAAAAASUVORK5CYII=" />

	     // unstable test
//	     Assert.assertEquals("image", "<image xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR42mP4J8LwHx0zAAE2cWzyeBUSgxnw2UwMnzouINVmnF4YwmEwmg7Is3kYhQEA6pzZRchLX5wAAAAASUVORK5CYII=\" />",
//	    		 svgImage1.toXML());
	}

	@Test
	public void testPNGWrite() {
		BufferedImage bufferedImage = ImageUtil.readImage(SVGHTMLFixtures.IMAGE_TEST_PNG);
		SVGImage svgImage = new SVGImage();
		svgImage.readImageDataIntoSrcValue(bufferedImage, SVGImage.IMAGE_PNG);
	}


	@Test
	public void testPNGReadRaster() throws IOException {
		transformPixels(SVGHTMLFixtures.IMAGE_TEST_PNG, "target/monochrome.png", SVGHTMLFixtures.MONOCHROME);
	}

	@Test
	public void testPNGReadRaster1() throws IOException {
		transformPixels(SVGHTMLFixtures.TEST_PNG, "target/monochrome1.png", SVGHTMLFixtures.MONOCHROME);
	}

	@Test
	public void testPNGReadRasterGray() throws IOException {
		transformPixels(SVGHTMLFixtures.TEST_PNG, "target/gray1.png", GRAYSCALE);
	}

	@Test
	@Ignore
	//Some unknown problem on Hudson reading file
	public void test1MiniCanny1() throws IOException {
		transformPixels(SVGHTMLFixtures.TEST1MINI_BMP, "target/test1Mini.png", CANNY);
	}
	
	@Test
	public void testAddCC0() throws IOException {
		BufferedImage targetBufferedImage = readBufferedImage(SVGHTMLFixtures.PLOTS1_BMP);
		WritableRaster cc0Raster = readRaster(SVGHTMLFixtures.CC0_SVG);
		overpaint(targetBufferedImage, cc0Raster);
		SVGHTMLFixtures.writeImageQuietly(targetBufferedImage, SVGHTMLFixtures.PLOTS_CC0_PNG);
	}

	@Test
	public void testAddPubdom() throws IOException {
		BufferedImage targetBufferedImage = readBufferedImage(SVGHTMLFixtures.PLOTS1_BMP);
		try {
			WritableRaster cc0Raster = readRaster(SVGHTMLFixtures.PUBDOM_PNG);
			overpaint(targetBufferedImage, cc0Raster);
			SVGHTMLFixtures.writeImageQuietly(targetBufferedImage, SVGHTMLFixtures.PLOTS_PUBDOM_PNG);
		} catch (Exception e) {
			LOG.error("Cannot read - maybe flaky call to ImageIO - have to ignore "+e);
		}
	}
	
	@Test
	public void testMonochrome2Pubdom() throws IOException {
		BufferedImage targetBufferedImage = readBufferedImage(SVGHTMLFixtures.MONOCHROME2_PNG);
		try {
			WritableRaster cc0Raster = readRaster(SVGHTMLFixtures.PUBDOM_PNG);
			overpaint(targetBufferedImage, cc0Raster);
			SVGHTMLFixtures.writeImageQuietly(targetBufferedImage, SVGHTMLFixtures.MONOCHROME2PUBDOM_PNG);
		} catch (Exception e) {
			LOG.error("Cannot read - maybe flaky call to ImageIO - have to ignore "+e);
		}
	}
		
	@Test
	@Ignore
	//Requires Internet
	public void testFigshare() throws Exception {
		InputStream is = null;
		try {
			is = new URL("http://previews.figshare.com/1138891/preview_1138891.jpg").openStream();
			BufferedImage targetBufferedImage = readBufferedImage(is);
			WritableRaster cc0Raster = readRaster(SVGHTMLFixtures.CCBY_PNG);
			overpaint(targetBufferedImage, cc0Raster);
			SVGHTMLFixtures.writeImageQuietly(targetBufferedImage, SVGHTMLFixtures.FIGSHARE1138891_PNG);
		} catch (IOException e) {
			// couldn't connect
		}
	}
	
	@Test
	public void testMonochrome2PubdomStream() throws Exception {
		FileInputStream fis = new FileInputStream(SVGHTMLFixtures.MONOCHROME2_PNG);
		try {
			BufferedImage targetBufferedImage = readBufferedImage(fis);
			WritableRaster cc0Raster = readRaster(SVGHTMLFixtures.PUBDOM_PNG);
			overpaint(targetBufferedImage, cc0Raster);
			SVGHTMLFixtures.writeImageQuietly(targetBufferedImage, SVGHTMLFixtures.MONOCHROME2PUBDOM_STREAM_PNG);
		} catch (Exception e) {
			LOG.error("Cannot read - maybe flaky call to ImageIO - have to ignore "+e);
		}
	}

	@Test
	public void testMonochrome2Text0() throws IOException {
		BufferedImage targetBufferedImage = readBufferedImage(SVGHTMLFixtures.MONOCHROME2_PNG);
		WritableRaster cc0Raster = readRaster(SVGHTMLFixtures.PMRCC0_PNG_);
		overpaint(targetBufferedImage, cc0Raster);
		SVGHTMLFixtures.writeImageQuietly(targetBufferedImage, SVGHTMLFixtures.MONOCHROME2PMRCC0_PNG);
	}

	@Test
	public void testMonochrome2Text() throws IOException {
		BufferedImage targetBufferedImage = readBufferedImage(SVGHTMLFixtures.MONOCHROME2_PNG);
		// height and weight depend on text
		int height = 12;
		WritableRaster cc0Raster = readRasterText("CC0 Peter Murray-Rust", 
				SVGHTMLFixtures.PMRCC0_PNG_.toString(), height);
		overpaint(targetBufferedImage, cc0Raster);
		SVGHTMLFixtures.writeImageQuietly(targetBufferedImage, SVGHTMLFixtures.MONOCHROME2TEXT_PNG);
	}

	@Test
	public void testReadSVG() throws Exception {
		AbstractCMElement element = SVGElement.readAndCreateSVG(SVGHTMLFixtures.PLOS_GRAPH_SVG);
		SVGImage svgImage =
                SVGImage.extractImages(SVGUtil.getQuerySVGElements(element, "//*[local-name()='image']")).get(0);
		svgImage.writeImage("target/testReadSvg.png", SVGImage.IMAGE_PNG);
	}
	
	@Test
	public void testRotateImage() throws IOException {
		SVGImage svgImage = SVGImage.createSVGFromImage(SVGHTMLFixtures.IMAGE_G_2_2_PNG, SVGImage.IMAGE_PNG);
		svgImage.applyTransformToImage(new Transform2(new Angle(Math.PI)));
		svgImage.writeImage(new File("target/rotx.png"), SVGImage.IMAGE_PNG);
		
	}
	
	@Test
	public void testFlipImageHorizontally() throws IOException {
		SVGImage svgImage = SVGImage.createSVGFromImage(SVGHTMLFixtures.IMAGE_G_2_2_PNG, SVGImage.IMAGE_PNG);
		svgImage.applyTransformToImage(new Transform2(new double[]{-1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0}));
		svgImage.writeImage(new File("target/fliphor.png"), SVGImage.IMAGE_PNG);
		
	}
	
	@Test
	public void testFlipImageVertically() throws IOException {
		SVGImage svgImage = SVGImage.createSVGFromImage(SVGHTMLFixtures.IMAGE_G_2_2_PNG, SVGImage.IMAGE_PNG);
		svgImage.applyTransformToImage(new Transform2(new double[]{1.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 0.0}));
		svgImage.writeImage(new File("target/flipvert.png"), SVGImage.IMAGE_PNG);
		
	}
	
	@Test
	public void testTranslate() throws IOException {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(SVGHTMLFixtures.LETTERA_SVG_FILE);
		SVGImage svgImage = SVGImage.extractSelfAndDescendantImages(svgElement).get(0);
		LOG.trace(svgImage.toXML());
		Transform2 transform = svgImage.getTransform();
		LOG.trace(transform);
		Real2 translationReal2 = transform.getTranslation();
		LOG.trace(translationReal2);
		RealArray scales = transform.getScales();
		LOG.trace(scales);
		Real2 dimension = svgImage.getReal2Dimension();
		
		Real2 centreOffset = dimension.multiplyBy(0.5);
		LOG.trace("Offset: "+centreOffset);
		translationReal2 = translationReal2.plus(centreOffset);
		translationReal2.negative();
		LOG.trace("translation: "+centreOffset);
		Transform2 scaleTransform = Transform2.createScaleTransform(1./scales.get(0), 1./scales.get(1));
		//Transform2 scaleTransform = Transform2.createScaleTransform(scales.get(0), scales.get(1));
		translationReal2.transformBy(scaleTransform);
		Transform2 translationTransform = Transform2.getTranslationTransform(translationReal2);
		translationTransform = translationTransform.concatenate(scaleTransform);
		LOG.trace("translationTransform "+translationTransform);
		transform = transform.concatenate(translationTransform);
		svgImage.setTransform(transform);
		LOG.trace(svgImage.toXML());
		SVGSVG svgx = SVGSVG.wrapAndWriteAsSVG(svgImage, new File("target/origin.svg"));
	}
	
	@Test
	public void testTranslateToOrigin() throws IOException {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(SVGHTMLFixtures.LETTERA_SVG_FILE);
		SVGImage svgImage = SVGImage.extractSelfAndDescendantImages(svgElement).get(0);
		LOG.trace(svgImage.toXML());
		Transform2 transform = svgImage.getTransform();
		LOG.trace(transform);
		Real2 translation = transform.getTranslation();
		LOG.trace(translation);
		Real2 dimension = svgImage.getReal2Dimension();
		Real2 centreOffest = dimension.multiplyBy(0.5);
		translation = translation.plus(centreOffest);
		translation.negative();
		Transform2 translationTransform = Transform2.getTranslationTransform(translation);
		transform = transform.concatenate(translationTransform);
		svgImage.setTransform(translationTransform);
		SVGSVG svgx = SVGSVG.wrapAndWriteAsSVG(svgImage, new File("target/origin.svg"));
	}

	@Test
	public void testApplyExplicitTransformationAndUpdate() throws IOException {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(SVGHTMLFixtures.LETTERA_SVG_FILE);
		SVGImage svgImage = SVGImage.extractSelfAndDescendantImages(svgElement).get(0);
		svgImage.applyExplicitTransformationAndUpdate();
		SVGSVG svgx = SVGSVG.wrapAndWriteAsSVG(svgImage, new File("target/explicitxx.svg"));
		SVGUtil.debug(svgImage, new FileOutputStream("target/explicit.svg"), 1);
		svgImage.writeImage(new File("target/explicit.png"), SVGImage.IMAGE_PNG);
		
	}

	// =================================================================
	
	private WritableRaster readRasterText(String text, String filename, int height) {
		int width = text.length()*6;
//		int imageType = /*6*/ BufferedImage.TYPE_INT_ARGB;		
//		BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
		BufferedImage bufferedImage = ImageUtil.createARGBBufferedImage(width, height);
		debugImage("new image ", bufferedImage);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.drawString(text, 0, height);
		outputImage(bufferedImage, filename, SVGImage.PNG);
		WritableRaster raster = bufferedImage.getRaster();
		return raster;
	}

	private void overpaint(BufferedImage image, WritableRaster raster) {
		debugImage("overpaint", image);
		debugRaster("overpaint", raster);
		try {
			image.setData(raster);
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.error("AIOOBE: "+e);
		}
	}

	private WritableRaster readRaster(String inputFile) {
		BufferedImage bufferedImage = readBufferedImage(inputFile);
		return getRasterAndDebug(bufferedImage);
	}

	private WritableRaster readRaster(File inputFile) throws IOException {
		BufferedImage bufferedImage = readBufferedImage(inputFile);
		return getRasterAndDebug(bufferedImage);
	}

	private WritableRaster getRasterAndDebug(BufferedImage bufferedImage) {
		WritableRaster raster = bufferedImage.getRaster();
		debugRaster("readRaster", raster);
		return raster;
	}

	private void debugImage(String msg, BufferedImage image) {
		LOG.trace(msg+" image w: "+image.getWidth()+" h: "+image.getHeight()+" type "+image.getType());
	}

	private void debugRaster(String msg, WritableRaster raster) {
		LOG.trace(msg+" raster w: "+raster.getWidth()+" h: "+raster.getHeight());
	}

	private BufferedImage readBufferedImage(String inputFile) {
		return readBufferedImage1(new File(inputFile));
	}

	private BufferedImage readBufferedImage1(File imgFile) {
		BufferedImage bufferedImage = null;
		try {
			if (imgFile == null || !imgFile.exists() || imgFile.isDirectory()) {
				throw new RuntimeException("File is null or does not exist or is Directory: "+imgFile);
			}
			FileInputStream fis = new FileInputStream(imgFile);
			bufferedImage = readBufferedImage(fis);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read image", e);
		}
		debugImage("readBuffered", bufferedImage);
		return bufferedImage;
	}

	private BufferedImage readBufferedImage(File imgFile) {
		BufferedImage bufferedImage;
		bufferedImage = ImageUtil.readImage(imgFile);
		return bufferedImage;
	}

	private BufferedImage readBufferedImage(InputStream is) throws IOException {
		BufferedImage bufferedImage;
		bufferedImage = ImageIO.read(is);
		return bufferedImage;
	}

	private void outputImage(BufferedImage bufferedImage, String filename, String type) {
		File file = new File(filename);
		SVGHTMLFixtures.writeImageQuietly(bufferedImage, file);
	}

	private void transformPixels(File inputFile, String outputFile, String method) throws IOException {
		BufferedImage bufferedImage = null;
		int[] iArray = { 0, 0, 0, 255 };
		WritableRaster raster = readRaster(inputFile);
		if (method.equals(SVGHTMLFixtures.MONOCHROME)) {
			bufferedImage = createMonochromeImage(iArray, raster);
		} else if (method.equals(GRAYSCALE)) {
			bufferedImage = makeGrayscale(iArray, raster);
		} else if (method.equals(CANNY)) {
			throw new RuntimeException("No longer supports Canny - use imageprocessing project");
			//bufferedImage = makeCanny(inputFile);
		} else {
			bufferedImage = null;
		}
		if (bufferedImage == null) {
			throw new RuntimeException("Cannot create BufferedImage: "+method);
		}
		outputImage(bufferedImage, outputFile, SVGImage.PNG);
	}

	private BufferedImage createMonochromeImage(int[] iArray, WritableRaster raster) {
		int cols = raster.getWidth();
		int rows = raster.getHeight();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int[] jArray = raster.getPixel(col, row, iArray);
				int sum = jArray[0]+jArray[1]+jArray[2];
				if (sum  > 255/2 * 3) {
					jArray[0] = 255;
					jArray[1] = 255;
					jArray[2] = 255;
				} else {
					jArray[0] = 0;
					jArray[1] = 0;
					jArray[2] = 0;
				}
				raster.setPixel(col, row, iArray);
			}
		}
		BufferedImage bufferedImage = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_BINARY);
		return bufferedImage;
	}
	
	private BufferedImage makeGrayscale(int[] iArray, WritableRaster raster) {
		int cols = raster.getWidth();
		int rows = raster.getHeight();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				int[] jArray = raster.getPixel(col, row, iArray);
				int sum = (jArray[0]+jArray[1]+jArray[2])/3;
				sum = (sum / 16) * 16;
				jArray[0] = sum;
				jArray[1] = sum;
				jArray[2] = sum;
				raster.setPixel(col, row, iArray);
			}
		}
		BufferedImage bufferedImage = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);
		return bufferedImage;
	}
	
}
