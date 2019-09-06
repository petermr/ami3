package org.contentmine.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.colour.ColorUtilities;
import org.contentmine.image.processing.HilditchThinning;
import org.contentmine.image.processing.Thinning;
import org.contentmine.image.processing.ZhangSuenThinning;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConfigLength;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayU8;

public class ImageUtil {
	private static final double NO_CORRELATION = -0.01;
	private static final Logger LOG = Logger.getLogger(ImageUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** rotates round vertical axis
	 * rotations are ANTICLOCKWISE DEGREES
	 * @author pm286
	 *
	 */
	public enum Rotation {
		ROT0(0, null),
		ROT90(90, Scalr.Rotation.CW_90),
		ROT180(180, Scalr.Rotation.CW_180),
		ROT270(270, Scalr.Rotation.CW_270),
		;
		private int degrees;
		private Scalr.Rotation scalrRotation;

		private Rotation(int degrees, Scalr.Rotation scalrRot) {
			this.degrees = degrees;
			this.scalrRotation = scalrRot;
		}
		/**
		 * 
		 * @param angle
		 * @return null if not 0/90/180/270/360
		 */
		public static Rotation getRotation(int angle) {
			if (angle % 360 == 90) {
				return ROT90;
			}
			if (angle % 360 == 180) {
				return ROT180;
			}
			if (angle% 360 == 270) {
				return ROT270;
			}
			if (angle %360 == 0 ) {
				return ROT0;
			}
			return null;
		}
		public Scalr.Rotation getScalrRotation() {
			return scalrRotation;
		}
	}
	
	public enum ThresholdMethod {
		GLOBAL_MEAN("global_mean"),
		GLOBAL_OTSU("global_otsu"),
		GLOBAL_ENTROPY("entropy"),
		LOCAL_MEAN("local_mean"),
		BLOCK_MIN_MAX("min_max"),
		BLOCK_MEAN("block_mean"),
		BLOCK_OTSU("block_otsu"),
		LOCAL_GAUSSIAN("gaussian"),
		LOCAL_SAUVOLA("sauvola"),
		LOCAL_NICK("nick"),
		;
		private String method;
		private ThresholdMethod(String method) {
			this.method = method;
		}
		public static ThresholdMethod getMethod(String binarize) {
			for (ThresholdMethod thresholdMethod : values()) {
				if (thresholdMethod.method.equals(binarize)) {
					return thresholdMethod;
				}
			}
			return null;
		}
		public String toString() {
			return method;
		}
	}

	public enum SharpenMethod {
		LAPLACIAN("laplacian"),
		SHARPEN4("sharpen4"),
		NONE("none"),
		SHARPEN8("sharpen8"),
		;
		private String method;

		private SharpenMethod(String method) {
			this.method = method;
		}
		public String toString() {
			return method;
		}
		public static SharpenMethod getMethod(String methodName) {
			for (SharpenMethod sharpenMethod : values()) {
				if (sharpenMethod.method.equals(methodName)) {
					return sharpenMethod;
				}
			}
			throw new IllegalArgumentException("unknown method: "+methodName);
		}
	}

	
	public static final IntArray SMEAR_ARRAY = new IntArray(new int[]{1,3,6,10,20,10,6,3,1});
	public static final IntArray DOUBLE_ARRAY = new IntArray(new int[]{1,2,1});
	public static final IntArray IDENT_ARRAY = new IntArray(new int[]{1});
	public static final IntArray EDGE_ARRAY = new IntArray(new int[]{1,0,-1});
	public static final IntArray SHARPEN_ARRAY = new IntArray(new int[]{-10, 20, -10});

	public static int RED_INDEX = 0;
	public static int GREEN_INDEX = 1;
	public static int BLUE_INDEX = 2;
	public static int[] RGB = {RED_INDEX, GREEN_INDEX, BLUE_INDEX};

	public static BufferedImage zhangSuenThin(BufferedImage image) {
		Thinning thinningService = new ZhangSuenThinning(image);
		thinningService.doThinning();
		image = thinningService.getThinnedImage();
		return image;
	}

	public static BufferedImage hilditchThin(BufferedImage image) {
		Thinning thinningService = new HilditchThinning(image);
		thinningService.doThinning();
		image = thinningService.getThinnedImage();
		return image;
	}

	public static BufferedImage thin(BufferedImage image, Thinning thinning) {
		thinning.createBinary(image);
		thinning.doThinning();
		image = thinning.getThinnedImage();
		return image;
	}

	/** thresholds an image
	 * uses BoofCV 0.32 or later
	 * NOT YET TESTED
	 * 
	 * @param image
	 * @param threshold 
	 * @return thresholded BufferedImage
	 */
	
	/* WAS Boofcv 0.17
	public static BufferedImage boofCVBinarization(BufferedImage image, int threshold) {
		ImageUInt8 input = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
		ImageUInt8 binary = new ImageUInt8(input.getWidth(), input.getHeight());
		ThresholdImageOps.threshold(input, binary, threshold, false);
		BufferedImage outputImage = VisualizeBinaryData.renderBinary(binary,null);
		return outputImage;
	}
	The changes are ImageUInt8 => GrayU8 (etc.) 
	VisualizeBinaryData.renderBinary(binary,null) => ConvertBufferedImage.extractBuffered(binary)
	
	It compiles - but haven't yet run it.
	NOT YET TESTED
	 */
	public static BufferedImage PMRBinarization(BufferedImage image, int threshold) {
		GrayU8 input8 = ConvertBufferedImage.convertFrom(image,(GrayU8)null);
//		GrayF32 input32 = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
		GrayU8 binary8 = new GrayU8(input8.getWidth(), input8.getHeight());
		GrayF32 binary32 = new GrayF32(input8.getWidth(), input8.getHeight());
		boolean down =false;
		ThresholdImageOps.threshold(input8, binary8, threshold, down);
//		GThresholdImageOps.threshold(input32, binary8, threshold, down);
		BufferedImage outputImage =	ConvertBufferedImage.convertTo(binary32, null);
		return outputImage;
	}
	
	/** This does NOT get the correct result and rerquires a kludge which could fail anytime
	 * 
	 * @param image
	 * @param threshold
	 * @return
	 */
	public static BufferedImage boofCVBinarization(BufferedImage image, int threshold) {
		GrayU8 input8 = ConvertBufferedImage.convertFrom(image,(GrayU8)null);
		GrayF32 input32 = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
		GrayU8 binary8 = new GrayU8(input8.getWidth(), input8.getHeight());
//		GrayF32 binary32 = new GrayF32(input8.getWidth(), input8.getHeight());
		boolean down = false;
		ThresholdImageOps.threshold(input8, binary8, threshold, down);
//		GThresholdImageOps.threshold(input32, binary8, threshold, down);
		
		BufferedImage outputImage = null;
//		outputImage =	ConvertBufferedImage.convertTo(binary8, null);
		boolean invert = false;
		outputImage = VisualizeBinaryData.renderBinary(binary8, invert, null);
		// 
//		outputImage = ImageUtil.removeAlpha(outputImage);
//		outputImage = ImageUtil.convertRGB(outputImage, 
//				new int[] {0x0d0d0d}, new int[] {0xffffff});
		return outputImage;
	}
	
	public static BufferedImage boofCVThreshold(BufferedImage image, ThresholdMethod method) {
		
		if (image == null || method == null) return null;
		// convert into a usable format
		GrayF32 input = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
		GrayU8 binary = new GrayU8(input.width,input.height);

//		BufferedImage binaryImage = null;
		boolean down = false;
		if (false) {
		// Global Methods
		} else if (method.equals(ThresholdMethod.GLOBAL_MEAN)) {
			GThresholdImageOps.threshold(input, binary, ImageStatistics.mean(input), down);
		} else if (method.equals(ThresholdMethod.GLOBAL_OTSU)) {
			GThresholdImageOps.threshold(input, binary, GThresholdImageOps.computeOtsu(input, 0, 255), down);
		} else if (method.equals(ThresholdMethod.GLOBAL_ENTROPY)) {
			GThresholdImageOps.threshold(input, binary, GThresholdImageOps.computeEntropy(input, 0, 255), down);

		// Local method
//		} else if (method.equals(ThresholdMethod.LOCAL_MEAN)) {
//			GThresholdImageOps.localMean(input, binary, ConfigLength.fixed(57), 1.0, down, null, null, null);
		} else if (method.equals(ThresholdMethod.BLOCK_MIN_MAX)) {
			GThresholdImageOps.blockMinMax(input, binary, ConfigLength.fixed(21), 1.0, down, 15 );
		} else if (method.equals(ThresholdMethod.BLOCK_MEAN)) {
			GThresholdImageOps.blockMean(input, binary, ConfigLength.fixed(21), 1.0, down );
		} else if (method.equals(ThresholdMethod.BLOCK_OTSU)) {
			GThresholdImageOps.blockOtsu(input, binary, false,ConfigLength.fixed(21),0.5, 1.0, down );
		} else if (method.equals(ThresholdMethod.LOCAL_GAUSSIAN)) {
			GThresholdImageOps.localGaussian(input, binary,  ConfigLength.fixed(85), 1.0, down, null, null);
		} else if (method.equals(ThresholdMethod.LOCAL_SAUVOLA)) {
			GThresholdImageOps.localSauvola(input, binary,  ConfigLength.fixed(11), 0.30f, down);
		} else if (method.equals(ThresholdMethod.LOCAL_NICK)) {
			GThresholdImageOps.localNick(input, binary,  ConfigLength.fixed(11), -0.2f, down);
		} else {
			LOG.error("unknown ThresholdMethod: "+method);
		}

		boolean invert = false;
		BufferedImage outputImage = VisualizeBinaryData.renderBinary(binary, invert, null);
//		BufferedImage outputImage = VisualizeBinaryData.renderBinary(binary, false, null);
//		ConvertBufferedImage.convertTo(binary, null);
		// GThresh
//		ImageUtil.invertImage(outputImage);
//
//		binaryImage = binary == null ? null : new BufferedImage(input.width, input.height, image.getType());
//		return binaryImage == null ? null : ConvertBufferedImage.convertTo(binary, binaryImage);
		return outputImage;
	}


	/** Sharpens image
	 * From Boofcv
	 * When an image is sharpened the intensity of edges are made more extreme while flat regions remain unchanged.
	 * @param image
	 * @param method
	 * @return converted image (or original if no action)
	 */
	public static BufferedImage sharpen(BufferedImage image, SharpenMethod method) {
		
		if (image == null || method == null) return null;
		// convert into a usable format
//		GrayF32 input = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
//		GrayU8 binary = new GrayU8(input.width,input.height);

		GrayU8 gray = ConvertBufferedImage.convertFrom(image, (GrayU8)null);
		GrayU8 adjusted = gray.createSameShape();
		BufferedImage sharpenedImage = null;

		if (false) {
			
		} else if (method.equals(SharpenMethod.SHARPEN4)) {
			EnhanceImageOps.sharpen4(gray, adjusted);
			sharpenedImage = ConvertBufferedImage.convertTo(adjusted,null);
		} else if (method.equals(SharpenMethod.SHARPEN8)) {
			EnhanceImageOps.sharpen8(gray, adjusted);
			sharpenedImage = ConvertBufferedImage.convertTo(adjusted,null);
		} else {
			sharpenedImage = ConvertBufferedImage.convertTo(gray,null);
		}

		return sharpenedImage;
	}

	
	/** extracts a subimage translated to 0,0.
	 * 
	 * clip to bounding box inclusive? or edge of image
	 * 
	 * @param image 
	 * @return null if clip is outside size of image
	 */
	public static BufferedImage clipSubImage(BufferedImage image, Int2Range boundingBox) {
		if (boundingBox == null) {
			throw new IllegalArgumentException("null bbox");
		}
		BufferedImage subImage = null;
		IntRange xRange = boundingBox.getXRange();
		IntRange yRange = boundingBox.getYRange();
		if (xRange == null || yRange == null) {
			throw new IllegalArgumentException("invalid boundingBox: "+String.valueOf(boundingBox));
		}
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		int xMin = xRange.getMin();
		int yMin = yRange.getMin();
		int clipWidth = xRange.getRange() + 1; // min and max inclusive
		clipWidth = Math.min(clipWidth, imageWidth - xMin);
		int clipHeight = yRange.getRange() + 1;
		clipHeight = Math.min(clipHeight, imageHeight - yMin);
		if (clipWidth > 0 && clipHeight > 0) {
			subImage = new BufferedImage(clipWidth, clipHeight, image.getType());
			for (int i = 0; i < clipWidth; i++) {
				int xx = i + xMin;
				for (int j = 0; j < clipHeight; j++) {
					int yy = j + yMin;
					int rgb = image.getRGB(xx, yy);
					subImage.setRGB(i, j,  rgb);
				}
			}
		}
		return subImage;
	}

	/** correlation includes plotting
	 * horribly messy
	 * 
	 * @param image
	 * @param image2
	 * @param title
	 * @return
	 */
	public static double correlateGrayAndPlot(BufferedImage image,
			BufferedImage image2, String title) {
		double cor = 0.0;
		int xrange = Math.min(image.getWidth(), image2.getWidth());
		int yrange = Math.min(image.getHeight(), image2.getHeight());
		SVGG g = new SVGG();
		double total = 0;
		double sum = 0;
		Real2 centre = new Real2(0.0, 0.0);
		Real2 centre2 = new Real2(0.0, 0.0);
		double sumGray = 0.0;
		double sumGray2 = 0.0;
		for (int i = 0; i < xrange; i++) {
			for (int j = 0; j < yrange; j++) {
				int gray = getGray(image, i,j);
				int gray2 = getGray(image2, i,j);
				if (gray < 0 || gray2 < 0) {
					throw new RuntimeException("bad gray value "+Integer.toHexString(gray)+" "+Integer.toHexString(gray2));
				}
				int diff = Math.abs(gray - gray2);
				int max = Math.max(gray, gray2);
				total += max;
				int score = max - 2 * diff;
				sum += score;
				centre.plusEquals(new Real2(i * gray, j * gray));
				sumGray += gray;
				centre2.plusEquals(new Real2(i * gray2, j * gray2));
				sumGray2 += gray2;
				SVGRect rect = createRedVsBlueRect(i, j, gray, gray2);
				g.appendChild(rect);
			}
		}
//		double scale = 1./(double)(xrange * yrange);
		centre.multiplyEquals(1./sumGray);
		centre2.multiplyEquals(1./sumGray2);
		if (title != null) {
			LOG.trace(centre.format(1)+" >> "+centre2.format(1)+" "+centre.subtract(centre2).format(1));
			File file = new File("target/corrPixels/"+title+".svg");
			file.getParentFile().mkdirs();
			SVGSVG.wrapAndWriteAsSVG(g, file);
		}
		cor = sum / total;
		return cor;
	}

	/** correlation includes plotting
	 * messy
	 * 
	 * @param image
	 * @param image2
	 * @param title
	 * @return
	 */
	public static double correlateGray(BufferedImage image, BufferedImage image2) {
		double cor = 0.0;
		int xrange = Math.min(image.getWidth(), image2.getWidth());
		int yrange = Math.min(image.getHeight(), image2.getHeight());
		double total = 0;
		double sum = 0;
		for (int i = 0; i < xrange; i++) {
			for (int j = 0; j < yrange; j++) {
				int gray = getGray(image, i,j);
				int gray2 = getGray(image2, i,j);
				if (gray < 0 || gray2 < 0) {
					throw new RuntimeException("bad gray value "+Integer.toHexString(gray)+" "+Integer.toHexString(gray2));
				}
				int diff = Math.abs(gray - gray2);
				int max = Math.max(gray, gray2);
				total += max;
				int score = max - 2 * diff;
				sum += score;
			}
		}
		cor = sum / total;
		return cor;
	}

	private static SVGRect createRedVsBlueRect(int i, int j, int gray, int gray2) {
		SVGRect rect = new SVGRect((double)i, (double)j, 1.0, 1.0);
		Color color = new Color(255-gray, 0, 255-gray2);
		String colorS = "#"+Integer.toHexString(color.getRGB()).substring(2);
		rect.setFill(colorS);
		rect.setStroke("none");
		return rect;
	}

	/** gets gray value.
	 * 
	 * this is messier than I thought - need a formal library.
	 * 
	 * range 0-> ff
	 * @param rgb assumed to be grayscale (r==g==b)
	 * @return gray or -1 if not a gray color
	 */
	public static int getGray(int rgb) {
		int gray = -1; // no color
		if (rgb == 0) {
			gray = 255; // assume transparent?
		} else {
			int alpha = getAlpha(rgb);
			int r = getRed(rgb);
			int g = getGreen(rgb);
			int b = getBlue(rgb);
			if (r == 0 && g == 0 && b == 0) {
				gray = 255 - alpha; // black seems to be #ff000000
			} else if (r == g && g == b) {
				gray = r; // omit transparent 
			} else {
				throw new RuntimeException("unprocessable value: "+Integer.toHexString(rgb));
			}
		}
		if (gray == -1) {
			throw new RuntimeException("unprocessed value: "+Integer.toHexString(rgb));
		}
		if (gray != 0) {
//			System.out.print(gray+" ");
		}
		return gray;
	}

	public static int getBlue(int rgb) {
		int blue = rgb;
		return (blue & 0xff) ;
	}

	public static int getGreen(int rgb) {
		int green = rgb >> 8;
		return (green & 0xff);
	}

	public static int getRed(int rgb) {
		int red = rgb >> 16;
		return (red & 0xff) ;
	}

	private static int getAlpha(int rgb) {
		int alpha = rgb >> 24;
		return (alpha & 0xff) ;
	}
	
	public static int getGray(BufferedImage image, int x, int y) {
		return getGray(image.getRGB(x, y));
	}

	/** extracts matrix from grayImage.
	 * 
	 * @param image
	 * @return matrix (null if not a gray image)
	 */
	public static IntMatrix getGrayMatrix(BufferedImage image) {
		int cols = image.getWidth();
		int rows = image.getHeight();
		IntMatrix matrix = new IntMatrix(rows, cols, 0);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int gray = ImageUtil.getGray(image, j, i);
				matrix.setElementAt(i,  j, gray);
			}
		}
		return matrix;
	}

	/** creates image from RealMatrix..
	 * 
	 * @param matrix values must be 0<=val<=255
	 * @return images values are clipped to 0<v<255 without warning 
	 */
	public static BufferedImage putGrayMatrix(IntMatrix matrix) {
		int cols = matrix.getCols();
		int rows = matrix.getRows();
		LOG.trace("rc "+rows+" "+cols);
		if (rows <= 0 || cols <= 0) return null;
		BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				LOG.trace(i+" "+j);
				int gray = (int) matrix.elementAt(i, j);
				if (gray < 0) {
					gray = 0;
				} else if (gray > 255) {
					gray = 255;
				}
				int grayColor = 256*256*gray + 256 * gray + gray; 
				image.setRGB(j, i, grayColor);
			}
		}
		return image;
	}

	public static BufferedImage shiftImage(BufferedImage image, double deltax, double deltay) {
		IntMatrix matrix = ImageUtil.getGrayMatrix(image);
		RealMatrix realMatrix = new RealMatrix(matrix);
		RealMatrix shiftedMatrix = realMatrix.createMatrixWithOriginShifted(deltax, deltay);
		IntMatrix matrix0 = new IntMatrix(shiftedMatrix);
		BufferedImage shiftedImage = ImageUtil.putGrayMatrix(matrix0);
		return shiftedImage;
	}

	/**
	@deprecated use Imgscalr instead {@link ImageUtil#scaleImage(int, int, BufferedImage)}
	*/
	@Deprecated
	public static BufferedImage scaleAndInterpolate(BufferedImage image,
			int newRows, int newCols) {
		RealMatrix matrix = new RealMatrix(ImageUtil.getGrayMatrix(image));
		RealMatrix shiftedMatrix = matrix.scaleAndInterpolate(newRows, newCols);
		BufferedImage shiftedImage = ImageUtil.putGrayMatrix(new IntMatrix(shiftedMatrix));
		return shiftedImage;
	}

	/** uses Imgscalr to scale.
	 * 
	 * @param width
	 * @param height
	 * @param genImage
	 * @return
	 */
	public static BufferedImage scaleImageScalr(int width, int height,
			BufferedImage genImage) {
		BufferedImage scaledGenImage = Scalr.resize(genImage, Method.ULTRA_QUALITY, Mode.FIT_EXACT, width,
		        height);
		return scaledGenImage;
	}

	public static BufferedImage addBorders(BufferedImage image0, int xmargin, int ymargin, int color) {
		if (image0 == null) {
			return null;
		}
		BufferedImage image = new BufferedImage(image0.getWidth() + 2*xmargin,  image0.getHeight()+2*ymargin,
				image0.getType());
		// set to colour
		for (int i = 0; i < image0.getWidth() + 2 * xmargin; i++) {
			for (int j = 0; j < image0.getHeight() + 2 * ymargin; j++) {
				image.setRGB(i, j, color);
			}
		}
		// copy
		for (int i = 0; i < image0.getWidth(); i++) {
			for (int j = 0; j < image0.getHeight(); j++) {
				image.setRGB(i + xmargin, j + ymargin, image0.getRGB(i, j));
			}
		}
		return image;
	}

	/** average colours locally.
	 * 
	 * 
	 * @param image
	 * @param 
 	 * @return new BufferedImage
	 */
	public static BufferedImage averageImageColors(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage image1 = createARGBBufferedImage(width, height);
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				int rgbij = image.getRGB(i, j);
				if (!isWhite(ImageUtil.getRGBChannels(rgbij), 200)) {
					int[] rgbx =  averageOverNeighbours(image, i, j);
					rgbij = ImageUtil.setRgb(rgbx[0], rgbx[1], rgbx[2]);
				}
				image1.setRGB(i, j, rgbij); 
			}
		}
		return image1;
	}

	private static int[] averageOverNeighbours(BufferedImage image, int i, int j) {
		int count = 0;
		int[] rgbs = new int[3];
		for (int k = -1; k <= 1; k++) {
			for (int l = -1; l <= 1; l++) {
				int rgb = image.getRGB(i + k, j + l);
				int[] rgbx = ImageUtil.getRGBChannels(rgb);
				if (!isWhite(rgbx, 250)) {
					for (int m = 0; m < 3; m++) {
						rgbs[m] += rgbx[m];
					}
					count++;
				}
			}
		}
		if (count != 0) {
			for (int m = 0; m < 3; m++) {
				rgbs[m] /= count;
			}
		} else {
			rgbs = ImageUtil.getRGBChannels(image.getRGB(i, j));
		}
		return rgbs;
	}


	private static boolean isWhite(int[] rgbx, int thresh) {
		return rgbx[0] > thresh && rgbx[1] > thresh && rgbx[2] > thresh;
	}

	/** flatten colours in image.
	 * 
	 * uses ImageUtil.flattenPixel
	 * 
	 * creates nvalues of single colour with min 0 and max 255. thus  
	 * 
	 * @param image
	 * @param nvalues number of discrete (integer) values of r or g or b. 
 	 *        currently 2, 4, 8, 16, 32, 64, 128 (maybe alter this later)
 	 * @return new BufferedImage
	 */
	public static BufferedImage flattenImage(BufferedImage image, int nvalues) {

		if (nvalues != 2 && nvalues != 4 && nvalues != 8 && nvalues != 16 &&
		    nvalues != 32 && nvalues != 64 && nvalues != 128) {
			throw new RuntimeException("Bad value of nvalues, should be power of 2 within 2 - 128");
		}
		int delta = 256 / nvalues;
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage image1 = createARGBBufferedImage(width, height);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				image1.setRGB(i, j, 0);
				flattenPixel(image, i, j, delta, image1);
			}
		}
		return image1;
	}

	/** creates image with BufferedImage.TYPE_INT_ARGB.
	 * 
	 * @param width
	 * @param height
	 * @return null if width or height < 1
	 */
	public static BufferedImage createARGBBufferedImage(int width, int height) {
		return width < 1 || height < 1 ? null : new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/** flattens pixel to range of values.
	 * 
	 * @param image
	 * @param i
	 * @param j
	 * @param delta distance between values (power of 2)
	 */
	public static void flattenPixel(BufferedImage image, int i, int j, int delta, BufferedImage image1) {
		int rgb = image.getRGB(i, j);
		int r = getRed(rgb);
		int g = getGreen(rgb);
		int b = getBlue(rgb);
		
		r = flattenChannel(r, delta);
		g = flattenChannel(g, delta);
		b = flattenChannel(b, delta);
		
		int col = (r << 16) | (g << 8) | b;
		image1.setRGB(i, j, col);
	}

	/**
	 * 
	 * @param r r/g/b channel (0-255)
	 * @param delta distance between allowed values (power of 2)
	 * @return nearest fencepost value (0 - 255) at intervals of delta
	 */
	public static int flattenChannel(int r, int delta) {
		int rr = r + delta/2; // round to nearest fencepost
		rr = (rr / delta) * delta;
		return rr == 0 ? 0 : rr - 1;
	}

	public static int setRgb(int red, int green, int blue) {
		int rgb = red * (256 * 256) + green * 256 + blue;
		return rgb;
	}

	public static int setRgb(float[] rgbf) {
		int rf = (int)rgbf[0];
		int gf = (int)rgbf[1];
		int bf = (int)rgbf[2];
		return ImageUtil.setRgb(rf, gf, bf);
	}


	/** flip to (255-r), (255.g), (255,b)
	 * 
	 * @param rgb
	 * @return
	 */
	public static int invertRgb(int rgb) {
		int flip = setRgb(255 - getRed(rgb), 255 - getGreen(rgb), 255 - getBlue(rgb));
		return flip;
	}
	
	/**
	 * toggles a -> 255-a in all channels
	 * 
	 * @param image is altered
	 */
	public static void invertImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = image.getRGB(x, y);
				rgb = invertRgb(rgb);
				image.setRGB(x, y, rgb);
			}
		}
	}

	
	public static int[] getRGBChannels(int rgb) {
		int red = getRed(rgb);
		int green = getGreen(rgb);
		int blue = getBlue(rgb);
		int[] channels = new int[] {red, green, blue};
		return channels;
	}

	public static String debugRGB(int rgb) {
		int[] channels = ImageUtil.getRGBChannels(rgb);
		return "r="+channels[RED_INDEX]+",g="+channels[GREEN_INDEX]+",b="+channels[BLUE_INDEX];
	}
	
	/** deep copy image.
	 * 
	 * thanks to https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
	 * 
	 * @param bi input image
	 * @return deep copy
	 */
	public static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/** creates a clipped image.
	 * 
	 * note if newWidth and newHeight access regions outside the size of oldImage they are ignored
	 * if xoff < 0 , xoff reset to 0
	 * if xoff > oldImage.width no copying
	 * if oldImage is w 600, h 800
	 * if xoff = 200 and newWidth = 300 then pixels 200...499 are copied
	 * if xoff = 200 and newWidth = 500 then pixels 200...599 are copied

	 * any undefined areas are set to 0
	 * 
	 * @param oldImage to copy from
	 * @param xoff xvalue to start copying from
	 * @param yoff yvalue to start copying from
	 * @param newWidth width of copied image
	 * @param newHeight height of copied image
	 * @return
	 */
	public static BufferedImage createClippedImage(
			BufferedImage oldImage, int xoff, int yoff, int newWidth, int newHeight) {
		xoff = xoff < 0 ? 0 : xoff;
		yoff = yoff < 0 ? 0 : yoff;
		int type = oldImage.getType();
		int oldWidth = oldImage.getWidth();
		int oldHeight = oldImage.getHeight();
		BufferedImage image = ImageUtil.createImage(newWidth, newHeight, 0, type);
		for (int i = 0; i < Math.min(oldWidth - xoff, newWidth); i++) {
			for (int j = 0; j < Math.min(oldHeight - yoff, newHeight); j++) {
				int rgb = oldImage.getRGB(i + xoff, j + yoff);
				image.setRGB(i,  j,  rgb);
			}
		}
		return image;
	}
	
	/**
	 * creates image of given size and RGB fill
	 * 
	 * @param width > 0
	 * @param height > 0
	 * @param rgb
	 * @param imageType
	 * @return null if zero size image
	 */
	public static BufferedImage createImage(int width, int height, int rgb, int imageType) {
		BufferedImage image = null;
		if (width > 0 || height > 0) { 
			image = new BufferedImage(width, height, imageType);
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					image.setRGB(i, j, rgb);
				}
			}
		}
		return image;
	}
	
	public static void setImageWhite(BufferedImage image) {
		for (int j = 0; j < image.getHeight(); j++) {
			for (int i = 0; i < image.getWidth(); i++) {
				image.setRGB(i, j, ColorUtilities.ARGB_WHITE);
			}
		}
	}

	/** set box to given color
	 * ranges are INCLUSIVE
	 * 
	 * @param image
	 * @param range
	 * @param rgb
	 */
	public static void setImageColor(BufferedImage image, Int2Range range, int rgb) {
		if (range == null) return;
		IntRange xRange = range.getXRange();
		int x0 = Math.max(0, xRange.getMin());
		int x1 = Math.min(image.getWidth(), xRange.getMax());
		IntRange yRange = range.getYRange();
		int y0 = Math.max(0, yRange.getMin());
		int y1 = Math.min(image.getHeight(), yRange.getMax());
		for (int i = x0; i <= x1; i++) {
			for (int j = y0; j <= y1; j++) {
				image.setRGB(i, j, rgb);
			}
		}
	}

	public static String createString(BufferedImage image) {
		int blackCount = 0;
		int whiteCount = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j <image.getHeight(); j++) {
				int rgb = image.getRGB(i, j);
				int rgbNoAlpha = rgb & 0x00ffffff;
				if (rgbNoAlpha == 0) {
					blackCount++;
				} else if (rgbNoAlpha == 0xffffff) {
					whiteCount++;
				}
			}
		}
		String s = "width: " + image.getWidth() + "; height: " + image.getHeight() 
		    + "; white: " + whiteCount + "; black: " + blackCount;
		return s;
	}

	/** convenience wrapper to throw quietly and announce file name.
	 * traps things that ImageIO does not
	 * (non-existence, wrong sort of file, etc..)
	 * 
	 * @param sourceFile
	 * @return
	 */
	public static BufferedImage readImage(File file) {
		BufferedImage image = null;
		if (file == null) {
			throw new RuntimeException("null file");
		}
		if (!file.exists()) {
			throw new RuntimeException("image file does not exist: "+file);
		}
		if (file.isDirectory()) {
			throw new RuntimeException("cannot read directory: "+file);
		}
		try {
			image = ImageIO.read(file);
		} catch (IndexOutOfBoundsException e) {
			throw new RuntimeException("Cannot read image, probably corrupt file: "+file);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read image: "+file);
		}
		return image;
	}

	/** convenience wrapper
	 * 
	 * @param image
	 * @param file
	 */
	public static boolean writeImageQuietly(BufferedImage image, File file, String format) {
		if (file == null) {
			throw new RuntimeException("file is null");
		}
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		boolean wrote = false;
		try {
			wrote = ImageIO.write(image, format, file);
//			LOG.debug("wrote "+wrote);
		} catch (IOException e) {
			throw new RuntimeException("cannot write image: " + file, e);
		}
		return wrote;
	}

	/** convenience wrapper
	 * 
	 * @param image
	 * @param file
	 */
	public static boolean writePngQuietly(BufferedImage image, File file) {
		return writeImageQuietly(image, file, "png");
	}

	public static Integer getSingleColor(BufferedImage image) {
		Integer color = null;
		int count = 0;
		for (int irow = 0; irow < image.getHeight(); irow++) {
			for (int jcol = 0; jcol < image.getWidth(); jcol++) {
				int col = image.getRGB(jcol, irow);
				// alpha might be random in some images
				col &= 0x00ffffff;
				count++;
				if (color == null) {
					color = col;
				} else if (color != col) {
					LOG.trace("> "+count+"; "+Integer.toHexString(color)+"/"+Integer.toHexString(col));
					return null;
				}
			}
		}
		return color;
	}

	public static long createSimpleHash(BufferedImage image) {
		long l = 0;
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j <image.getHeight(); j++) {
				int rgb = image.getRGB(i, j);
				l += rgb * 31 + i * 17 + j * 7;
			}
		}
		return l;
	}
	
	// from stackoverflow
	// https://stackoverflow.com/questions/4756268/how-to-resize-the-buffered-image-n-graphics-2d-in-java/4756906#4756906
	// not tested.Uses the graphics to expand and interpolate
	// creates "terrible images"
	public BufferedImage scaleImage(BufferedImage img, int width, int height,
	        Color background) {
	    int imgWidth = img.getWidth();
	    int imgHeight = img.getHeight();
	    if (imgWidth*height < imgHeight*width) {
	        width = imgWidth*height/imgHeight;
	    } else {
	        height = imgHeight*width/imgWidth;
	    }
	    BufferedImage newImage = new BufferedImage(width, height,
	            BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = newImage.createGraphics();
	    try {
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        g.setBackground(background);
	        g.clearRect(0, 0, width, height);
	        g.drawImage(img, 0, 0, width, height, null);
	    } finally {
	        g.dispose();
	    }
	    return newImage;
	}
	
	public enum Resizer {
	    NEAREST_NEIGHBOR {
	        @Override
	        public BufferedImage resize(BufferedImage source,
	                int width, int height) {
	            return commonResize(source, width, height,
	                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	        }
	    },
	    BILINEAR {
	        @Override
	        public BufferedImage resize(BufferedImage source,
	                int width, int height) {
	            return commonResize(source, width, height,
	                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        }
	    },
	    BICUBIC {
	        @Override
	        public BufferedImage resize(BufferedImage source,
	                int width, int height) {
	            return commonResize(source, width, height,
	                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        }
	    },
	    PROGRESSIVE_BILINEAR {
	        @Override
	        public BufferedImage resize(BufferedImage source,
	                int width, int height) {
	            return progressiveResize(source, width, height,
	                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        }
	    },
	    PROGRESSIVE_BICUBIC {
	        @Override
	        public BufferedImage resize(BufferedImage source,
	                int width, int height) {
	
	
	            return progressiveResize(source, width, height,
	                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	        }
	    },
	    AVERAGE {
	        @Override
	        public BufferedImage resize(BufferedImage source,
	                int width, int height) {
	            Image img2 = source.getScaledInstance(width, height,
	                    Image.SCALE_AREA_AVERAGING);
	            BufferedImage img = new BufferedImage(width, height,
	                    source.getType());
	            Graphics2D g = img.createGraphics();
	            try {
	                g.drawImage(img2, 0, 0, width, height, null);
	            } finally {
	                g.dispose();
	            }
	            return img;
	        }
	    };

	    public abstract BufferedImage resize(BufferedImage source,
	            int width, int height);
	
	    private static BufferedImage progressiveResize(BufferedImage source,
	            int width, int height, Object hint) {
	        int w = Math.max(source.getWidth()/2, width);
	        int h = Math.max(source.getHeight()/2, height);
	        BufferedImage img = commonResize(source, w, h, hint);
	        while (w != width || h != height) {
	            BufferedImage prev = img;
	            w = Math.max(w/2, width);
	            h = Math.max(h/2, height);
	            img = commonResize(prev, w, h, hint);
	            prev.flush();
	        }
	        return img;
	    }
	
	    private static BufferedImage commonResize(BufferedImage source,
	            int width, int height, Object hint) {
	        BufferedImage img = new BufferedImage(width, height,
	                source.getType());
	        Graphics2D g = img.createGraphics();
	        try {
	            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
	            g.drawImage(source, 0, 0, width, height, null);
	        } finally {
	            g.dispose();
	        }
	        return img;
	    }
	};

	/** creates a larger version of the image.
	 * at present each pixel is replaced in a new image by 2*2 identical pixels. 
	 * This might be expanded to have some interpolation
	 * @param image
	 * @return larger image
	 */
	@Deprecated //"Imgscalr allows interpolation"
	public static BufferedImage scaleImage(BufferedImage image, int scalex, int scaley) {
		if (image == null) return null;
		int width = image.getWidth();
		int height = image.getHeight();
		int newWidth = scalex * width;
		int newHeight = scaley * height;
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, image.getType());
		for (int x = 0; x < width; x++) {
			int newx = scalex * x;
			for (int y = 0; y < height; y++) {
				int rgb = image.getRGB(x, y);
				int newy = scaley * y;
				for (int dx = 0; dx < scalex; dx++) {
					for (int dy = 0; dy < scaley; dy++) {
						newImage.setRGB(newx + dx, newy + dy, rgb);
					}
				}
			}
		}
		return newImage;
	}
	
	/** from ImgScalr
	 * 
	 * 
	 */
	public static BufferedImage laplacianSharpen(BufferedImage image) {
//		Kernel kernel = new Kernel(3, 3, new float[]{
//				-1.0f, -1.0f, -1.0f,
//				-1.0f,  8.0f, -1.0f,
//				-1.0f, -1.0f, -1.0f,
//				});
		Kernel kernel = new Kernel(3, 3, new float[]{
				 1.0f,  1.0f,  1.0f,
				 1.0f, -8.0f,  1.0f,
				 1.0f,  1.0f,  1.0f,
				});
		ConvolveOp laplacian = new ConvolveOp(kernel);
		image = Scalr.apply(image, laplacian);
		return image;
	}

	
	// ============================= Scalr ======================
	/** scale image with ImgScalr
	 * 
	 * @param scalefactor
	 * @param image
	 * @return
	 */
	public static BufferedImage scaleImageScalr(Double scalefactor, BufferedImage image) {
		if (image != null) {
			int height = (int) (image.getHeight() * scalefactor);
			int width = (int) (image.getWidth() * scalefactor);
			image = scaleImageScalr(width , height, image);
		}
		return image;
	}

	/** rotate an image
	 * 
	 * @param image
	 * @param rotation (if 0, returns image unchanged)
	 * @return
	 */
	public static BufferedImage getRotatedImageScalr(BufferedImage image, int degrees) {
		Rotation rotation = Rotation.getRotation(degrees);
		return getRotatedImageScalr(image, rotation);
	}
	
	/** rotate an image
	 * uses Scalr
	 * 
	 * @param image
	 * @param rotation (if 0, returns image unchanged)
	 * @return
	 */
	public static BufferedImage getRotatedImageScalr(BufferedImage image, Rotation rotation) {
		BufferedImage newImage = null;
		if (image == null) {
		} else if (rotation == null || Rotation.ROT0.equals(rotation)) {
			newImage = image;
		} else {
			Scalr.Rotation scalrRotation = rotation.getScalrRotation();
			newImage = Scalr.rotate(image, scalrRotation);
		}
		return newImage;
	}
	
	// ================= Boofcv ==================
	
	// this needs editing
	public void rawBoofcv(BufferedImage image) {
	// raw Boofcv code

		// convert into a usable format
		GrayF32 input32 = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
		int width = input32.width;
		int height = input32.height;
		GrayU8 binary8 = new GrayU8(width, height);
		GrayS32 label32 = new GrayS32(width, height);
	
		// Select a global threshold using Otsu's method.
		double threshold = GThresholdImageOps.computeOtsu(input32, 0, 255);
	
		// Apply the threshold to create a binary image
		ThresholdImageOps.threshold(input32,binary8,(float)threshold,true);
	
		// remove small blobs through erosion and dilation
		// The null in the input indicates that it should internally declare the work image it needs
		// this is less efficient, but easier to code.
		GrayU8 filtered8 = BinaryImageOps.erode8(binary8, 1, null);
		filtered8 = BinaryImageOps.dilate8(filtered8, 1, null);
	
		// Detect blobs inside the image using an 8-connect rule
		List<Contour> contours = BinaryImageOps.contour(filtered8, ConnectRule.EIGHT, label32);
	
		// colors of contours
		int colorExternal = 0xFFFFFF;
		int colorInternal = 0xFF2020;
	
		// display the results
//		BufferedImage visualBinary = VisualizeBinaryData.renderBinary(binary8, false, null);
//		BufferedImage visualFiltered = VisualizeBinaryData.renderBinary(filtered8, false, null);
//		BufferedImage visualLabel = VisualizeBinaryData.renderLabeledBG(label32, contours.size(), null);
//		BufferedImage visualContour = VisualizeBinaryData.renderContours(contours, colorExternal, colorInternal,
//				width, height, null);
	
	}

	public static BufferedImage sharpenBoofcv(BufferedImage bufferedImage, SharpenMethod sharpen) {
		GrayU8 gray = ConvertBufferedImage.convertFrom(bufferedImage,(GrayU8)null);
		GrayU8 adjusted = gray.createSameShape();
		
		BufferedImage result = null;
		switch (sharpen) {
			case SHARPEN4: {
				EnhanceImageOps.sharpen4(gray, adjusted);
				result = ConvertBufferedImage.convertTo(adjusted,null);
				break;
			}
			case SHARPEN8: {
				EnhanceImageOps.sharpen8(gray, adjusted);
				result = ConvertBufferedImage.convertTo(adjusted,null);
				break;
			}
			default: {
				throw new IllegalArgumentException("bad or unimplemented method for Boofcv: "+sharpen);
			}
		}
		return result;
	}
	
	/** computes a threshold using Otsu's method
	 * 
	 * @param image to threshold. should be grayscale? not overwritten
	 * @param erodeDilate if true does an erode-dilate cycle (removes protruding bits)
	 * @return new image
	 */
	public static BufferedImage thresholdBoofcv(BufferedImage image, boolean erodeDilate) {

		// convert into a usable format
		GrayF32 input = ConvertBufferedImage.convertFromSingle(image, null, GrayF32.class);
		GrayU8 binary = new GrayU8(input.width,input.height);
	
		// Select a global threshold using Otsu's method.
		double threshold = GThresholdImageOps.computeOtsu(input, 0, 255);
	
		// Apply the threshold to create a binary image
		ThresholdImageOps.threshold(input,binary,(float)threshold,true);
	
		// remove small blobs through erosion and dilation
		// The null in the input indicates that it should internally declare the work image it needs
		// this is less efficient, but easier to code.
		if (erodeDilate) {
			GrayU8 filtered = BinaryImageOps.erode8(binary, 1, null);
			filtered = BinaryImageOps.dilate8(filtered, 1, null);
			binary = filtered;
		}
		
		BufferedImage resultImage = ConvertBufferedImage.convertTo(binary, null);
		resultImage = ImageUtil.convertRGB(
				resultImage, new int[] {0xd0d0d, 0xff0d0d0d}, new int[] {0xffffff, 0xffffff});
		ImageUtil.invertImage(resultImage);
		return resultImage;
	}
	
	// ======================= IO =====================
	/** convenience wrapper for ImageIO
	 * 
	 * tests for existence (ImageIO throws opaque error)
	 * 
	 * @param inputBaseFile
	 * @return
	 */
	public static BufferedImage readImageQuietly(File inputBaseFile) {
		BufferedImage image = null;
		image = ImageUtil.readImage(inputBaseFile);
		return image;
	}
	
	/** convenience wrapper for ImageIO
	 * 
	 * @param image
	 * @param outputPng
	 */
	public static void writeImageQuietly(BufferedImage image, File outputPng) {
		if (image == null) {
			LOG.error("Cannot write null image");
			return;
		}
		try {
			ImageIO.write(image, CTree.PNG, outputPng);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write image: "+outputPng, e);
		}
	}

    // =================  geometric scale ===============
	/**
	 * scale image to fit limits
	 * returns smallest scale to fit image to both limits, i.e. new image will defintely fit
	 * either if either parameter is null, uses the other
	 * if both are null returns 1.0
	 * 
	 * @param image
	 * @param maxWidth 
	 * @param maxHeight
	 * @return
	 */
	public static Double getScaleToFitImageToLimits(BufferedImage image, Integer maxWidth, Integer maxHeight) {
		Double scale = 1.0;
		if (maxWidth != null || maxHeight != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			if (maxWidth != null && width > maxWidth || maxHeight != null && height > maxHeight) {
				double scalex = ((double) maxWidth / (double) width);
				double scaley = ((double) maxHeight / (double) height);
				scale = Math.max(scalex,  scaley);
			}
		};
		return scale;
	}

	public static Multiset<Integer> createMultiset(BufferedImage image) {
		Multiset<Integer> multiset = HashMultiset.create();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				Integer color = image.getRGB(i, j);
				multiset.add(color);
			}
		}
		return multiset;
	}


	public static Multiset<String> createHexMultiset(BufferedImage image) {
		Multiset<String> multiset = HashMultiset.create();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				Integer color = image.getRGB(i, j);
				multiset.add(Integer.toHexString(color));
			}
		}
		return multiset;
	}
	
	/** replaces old value with new value.
	 * 
	 * @param image
	 * @param oldRGB array of old values
	 * @param newRGB array of new values
	 * @return
	 */
	public static BufferedImage convertRGB(BufferedImage image, int[] oldRGB, int[] newRGB) {
		BufferedImage newImage = new BufferedImage(image.getWidth(),  image.getHeight(),
				image.getType());
		if (oldRGB.length != newRGB.length) {
			throw new RuntimeException("Unequal lengths for old "+oldRGB.length+" and new "+newRGB.length);
		}
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int color = image.getRGB(i, j) & 0x00FFFFFF;
				for (int ii = 0; ii < oldRGB.length; ii++) {
					color = (color == oldRGB[ii]) ? newRGB[ii] : color;
				}
				newImage.setRGB(i, j, color);
			}
		}
		return newImage;
		
	}

	
	/** replaces old value with new value.
	 * 
	 * @param image
	 * @param o
	 * @return
	 */
	public static BufferedImage removeAlpha(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(),  image.getHeight(),
				image.getType());
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int color = image.getRGB(i, j);
				color &= 0x00ffffff;
				if (color != 0) {
//					System.out.println("col: "+Integer.toHexString(color));
				}
				newImage.setRGB(i, j, color);
			}
		}
		return newImage;
		
	}

	/** replaces non-zero value with white.
	 * 
	 * @param image
	 * @param o
	 * @return
	 */
	public static BufferedImage magnifyToWhite(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(),  image.getHeight(),
				image.getType());
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int color = image.getRGB(i, j);
				color &= 0x00ffffff;
				color = (color != 0) ? 0x00ffffff : color;
				newImage.setRGB(i, j, color);
			}
		}
		return newImage;
		
	}
	
	/** counts pixels of given color in vector.
	 * alpha channel is removed in all comparisons
	 * @param vector normally row or column of image
	 * @param color 
	 * @return
	 */
	public static int countColor(int[] vector, int color) {
		int sum = 0;
		for (int i = 0; i < vector.length; i++) {
			int rgb = vector[i] & 0x00ffffff;
			if (rgb == color) {
				sum++;
			}
		}
		return sum;
	}

	/** images welded bottom to top in current order.
	 * i.e. image[i].bottom == image[i+1].top
	 * 
	 * @param imageList
	 * @return null if no input or adjacent images vary in width or type
	 */
	public static BufferedImage joinImagesVertically(List<BufferedImage> imageList) {
		BufferedImage resultImage = null;
		if (imageList.size() == 0) {
			return null;
		} else if (imageList.size() == 1) {
			return imageList.get(0);
		}
		int totalHeight = 0;
		int currentWidth = -1;
		int currentType = -Integer.MIN_VALUE;
		// check width and type and create height
		for (BufferedImage image : imageList) {
			int width = image.getWidth();
			if (currentWidth == -1) {
				currentWidth = width;
			} else if (currentWidth != width) {
				LOG.error("variable width; cannot join "+width+" to "+currentWidth);
				return null;
			}
			int type = image.getType();
			if (currentType == -Integer.MIN_VALUE) {
				currentType = type;
			} else if (currentType != type) {
				LOG.error("variable type; cannot join "+type+" to "+currentType);
				return null;
			}
			totalHeight += image.getHeight();
		}
		// create stitched
		resultImage = new BufferedImage(currentWidth, totalHeight, currentType);
		int currentY = 0;
		for (int img = 0; img < imageList.size(); img++) {
			BufferedImage image = imageList.get(img);
			for (int i = 0; i < currentWidth; i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					int rgb = image.getRGB(i, j);
					resultImage.setRGB(i, j + currentY, rgb);
				}
			}
			currentY += image.getHeight();
		}
		return resultImage;
	}

	/**  correlates binary images.
	 * includes heuristics 
	 * translates to common c of g
	 * correlation is 2 * hit / miss // since a miss scores twice
	 * if heights or widths differ by more than 2, return -0.99
	 * 
	 * 
	 * 
	 * @param imagei
	 * @param imagej
	 * @return
	 */
	public static double correlateBlack(BufferedImage imagei, BufferedImage imagej) {
		int MAXDH = 2;
		int MAXDW = 2;
		int heighti = imagei.getHeight();
		int heightj = imagej.getHeight();
		int dh = Math.abs(heighti - heightj);
		int widthi = imagei.getWidth();
		int widthj = imagej.getWidth();
		int dw = Math.abs(widthi - widthj);
		if (dw > MAXDW || dh > MAXDH) return NO_CORRELATION;
		Int2 delta = deltaCofG(imagei, imagej);
		int black = 0;
		int white = 0;
		int miss = 0;
		for (int xi = 0; xi < imagei.getWidth(); xi++) {
			int xj = xi + delta.getX();
			if (xj < 0 || xj >= imagej.getWidth()) continue;
			for (int yi = 0; yi < imagei.getHeight(); yi++) {
				int yj = yi + delta.getY();
				if (yj < 0 || yj >= imagej.getHeight()) continue;
				boolean blacki = ImageUtil.isBlack(imagei, xi, yi);
				boolean blackj = ImageUtil.isBlack(imagej, xj, yj);
				if (!blacki && !blackj) {
					white++;
				} else if (blacki && blackj) {
					black++;
				} else {
					miss++;
				}
			}
		}
		return (double) 2* black / (double) (2 * black + miss);
	}

	public static Int2 deltaCofG(BufferedImage imagei, BufferedImage imagej) {
		Int2 cgi = ImageUtil.getCofGBlack(imagei);
		Int2 cgj = ImageUtil.getCofGBlack(imagej);
		Int2 delta = cgi.subtract(cgj);
		return delta;
	}

	private static Int2 getCofGBlack(BufferedImage image) {
		Real2 cg = new Real2(0,0);
		int count = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if (isBlack(image, x, y)) {
					cg = cg.plus(new Real2(x, y));
					count++;
				}
			}
		}
		if (count == 0) return null;
		double f = 1. / (double) count;
		cg = cg.multiplyBy(f);
		Int2 cgi = new Int2((int)cg.getX(), (int)cg.getY());
		return cgi;
	}

	/** is given pixel black?
	 * 
	 * @param image
	 * @param ix
	 * @param jy
	 * @return
	 */
	public static boolean isBlack(BufferedImage image, int x, int y) {
		if (!isInside(image, x, y)) {
			throw new RuntimeException(x+"/"+y+" outside "+image.getWidth()+","+image.getHeight());
		}
		return (image.getRGB(x, y) & 0x00ffffff) == 0;
	}

	/** is given pixel black or outside?
	 * 
	 * @param image
	 * @param ix
	 * @param jy
	 * @return
	 */
	public static boolean isBlackOrOutside(BufferedImage image, int x, int y) {
		if (!isInside(image, x, y)) {
			return true;
		}
		return (image.getRGB(x, y) & 0x00ffffff) == 0;
	}

	/** is point inside image?
	 * 
	 * @param image
	 * @param x
	 * @param y
	 * @return
	 */
	private static boolean isInside(BufferedImage image, int x, int y) {
		return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
	}

	public static BufferedImage despeckle(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(),  image.getHeight(),
				image.getType());
		int i = 0;
		int j = 0;
		// completely internal pixels
		for (i = 1; i < image.getWidth() - 1 ; i++) {
			for (j = 1; j < image.getHeight() - 1; j++) {
				int color = image.getRGB(i, j) & 0x00ffffff;
				if (isSpeckleCentre(image, i, j)) {
					newImage.setRGB(i, j, 0x00ffffff); // set white
				} else {
					newImage.setRGB(i, j, color); // transcribe
				}
			}
		}
		j = 0;
		for (i = 0; i < image.getWidth(); i++) {
			despeckleEdge(image, newImage, i, j);
		}
		j = image.getHeight() - 1;
		for (i = 0; i < image.getWidth(); i++) {
			despeckleEdge(image, newImage, i, j);
		}
		i = 0;
		for (j = 0; j < image.getHeight(); j++) {
			despeckleEdge(image, newImage, i, j);
		}
		i = image.getWidth() - 1;
		for (j = 0; j < image.getHeight(); j++) {
			despeckleEdge(image, newImage, i, j);
		}
		return newImage;
	}

	private static void despeckleEdge(BufferedImage image, BufferedImage newImage, int i, int j) {
		int color = image.getRGB(i, j) & 0x00ffffff;
		if (isSpeckleEdge(image, i, j)) {
			newImage.setRGB(i, j, 0x00ffffff); // set white
		} else {
			newImage.setRGB(i, j, color); // transcribe
		}
	}

	private static boolean isSpeckleEdge(BufferedImage image, int i, int j) {
		if (!isBlack(image, i, j)) return false;
		
		if (isBlackOrOutside(image, i - 1, j - 1)) return false;
		if (isBlackOrOutside(image, i - 1, j)) return false;
		if (isBlackOrOutside(image, i - 1, j + 1)) return false;
		if (isBlackOrOutside(image, i, j - 1)) return false;
		if (isBlackOrOutside(image, i, j + 1)) return false;
		if (isBlackOrOutside(image, i +1, j - 1)) return false;
		if (isBlackOrOutside(image, i + 1, j)) return false;
		if (isBlackOrOutside(image, i + 1, j + 1)) return false;
		
		return true;
	}

	private static boolean isSpeckleCentre(BufferedImage image, int i, int j) {
		if (!isBlack(image, i, j)) return false;
		
		if (isBlack(image, i - 1, j - 1)) return false;
		if (isBlack(image, i - 1, j)) return false;
		if (isBlack(image, i - 1, j + 1)) return false;
		if (isBlack(image, i, j - 1)) return false;
		if (isBlack(image, i, j + 1)) return false;
		if (isBlack(image, i +1, j - 1)) return false;
		if (isBlack(image, i + 1, j)) return false;
		if (isBlack(image, i + 1, j + 1)) return false;
		
		return true;
	}

	
}
