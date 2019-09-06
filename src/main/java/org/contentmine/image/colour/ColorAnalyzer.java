package org.contentmine.image.colour;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.RealArray.Filter;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.pixel.PixelList;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import boofcv.alg.color.ColorHsv;
import boofcv.io.image.UtilImageIO;

/** analyzes images for colours.
 * 
 * Typically attempts to find blocks of color and separate different regions.
 * 
 * @author pm286
 *
 */
/**
valuable approach to restricting numbers of colours
http://stackoverflow.com/questions/4057475/rounding-colour-values-to-the-nearest-of-a-small-set-of-colours
http://stackoverflow.com/questions/7530627/hcl-color-to-rgb-and-backward
https://en.wikipedia.org/wiki/Color_quantization

 * 
 * @author pm286
 *
 */

/**
 * from Boofcv
 * 
 * public class ColorHsv
extends java.lang.Object

Color conversion between RGB and HSV color spaces. HSV stands for Hue-Saturation-Value. "Hue" has a range of [0,2*PI] and "Saturation" has a range of [0,1], the two together represent the color. While "Value" has the same range as the input pixels and represents how light/dark the color is. Original algorithm taken from [1] and modified slightly.

NOTE: The hue is represented in radians instead of degrees, as is often done.
NOTE: Hue will be set to NaN if it is undefined. It is undefined when chroma is zero, which happens when the input color is a pure gray (e.g. same value across all color bands).

RGB to HSV:

 min = min(r,g,b)
 max = max(r,g,b)
 delta = max-min  // this is the chroma
 value = max

 if( max != 0 )
   saturation = delta/max
 else
   saturation = 0;
   hue = NaN

 if( r == max )
   hue = (g-b)/delta
 else if( g == max )
   hue = 2 + (b-r)/delta
 else
   hue = 4 + (r-g)/delta

 hue *= 60.0*PI/180.0
 if( hue < 0 )
   hue += 2.0*PI

 

[1] http://www.cs.rit.edu/~ncs/color/t_convert.html 
 *
 */
public class ColorAnalyzer {

	private static final Logger LOG = Logger.getLogger(ColorAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String COUNT = "count";
	private static final String AVERAGE = "average";
	private static final String MINPIXEL = "minpixel";
	private static final String MAXPIXEL = "maxpixel";
	
	private BufferedImage currentImage;
	private BufferedImage inputImage;
	private BufferedImage flattenedImage;
	private int height;
	private int width;
	private File outputDirectory;
	private File inputFile;
	private int intervalCount;
	private IntSet sortedFrequencyIndex;
	private IntArray colorValues;
	private IntArray colorCounts;
	private Int2Range xyRange;
	private List<PixelList> pixelListList;
	/** limits on pixel counts for images to be output
	 * 
	 */
	private int maxPixelSize = 100000;
	private int minPixelSize = 100;
	private int startPlot = 1;
	private int endPlot = 100;
	private int count = 0;
	private boolean flatten;
	private Multiset<RGBColor> colorSet;
	private RGBNeighbourMap rgbNeighbourMap;
	private ColorFrequenciesMap colorFrequenciesMap;
	private BufferedImage saturateImage;
	private int minChroma;
	private boolean saturate;
	private String outputImageFilename;
	private int minGray;
	private int maxGray;
	private int maxGrayChroma;
	private boolean includeGray;
	private BufferedImage grayImage;

	public int getMinGray() {
		return minGray;
	}

	public ColorAnalyzer setMinGray(int minGray) {
		this.minGray = minGray;
		return this;
	}

	public int getMaxGray() {
		return maxGray;
	}

	public ColorAnalyzer setMaxGray(int maxGray) {
		this.maxGray = maxGray;
		return this;
	}

	public int getMaxGrayChroma() {
		return maxGrayChroma;
	}

	public ColorAnalyzer setMaxGrayChroma(int maxGrayChroma) {
		this.maxGrayChroma = maxGrayChroma;
		return this;
	}

	public boolean isIncludeGray() {
		return includeGray;
	}

	public ColorAnalyzer setIncludeGray(boolean includeGray) {
		this.includeGray = includeGray;
		return this;
	}

	public ColorAnalyzer() {
		setDefaults();
	}
	
	private void setDefaults() {
		// pixel-svg options 
		// flattening
		this.setStartPlot(1);
		this.setMaxPixelSize(1000000);
		this.setIntervalCount(4);
		this.setEndPlot(15);
		this.setMinPixelSize(300);
		// saturate/gray
		this.setMinGray(32);
		this.setMaxGray(224);
		this.setMaxGrayChroma(32);

	}

	public int getMinChroma() {
		return minChroma;
	}

	public ColorAnalyzer setMinChroma(int minChroma) {
		this.minChroma = minChroma;
		return this;
	}

/**
 * 	
//		ColourAnalyzer colorAnalyzer = new ColourAnalyzer();
//		colorAnalyzer.readImage(new File(Fixtures.PROCESSING_DIR, filename+".png"));
//		colorAnalyzer.setStartPlot(1);
//		colorAnalyzer.setMaxPixelSize(1000000);
//		colorAnalyzer.setIntervalCount(4);
//		colorAnalyzer.setEndPlot(15);
//		colorAnalyzer.setMinPixelSize(300);
//		colorAnalyzer.flattenImage();
//		colorAnalyzer.setOutputDirectory(new File("target/"+filename));
//		colorAnalyzer.analyzeFlattenedColours();
 */
	public ColorAnalyzer(BufferedImage image) {
		this();
		readImage(image);
	}

	public boolean isSaturate() {
	return saturate;
}

public ColorAnalyzer setSaturate(boolean saturate) {
	this.saturate = saturate;
	return this;
}

	/** read and deep copy and process image.
	 * deep copy so image will not be modified
	 * @param image
	 */
	public void readImageDeepCopy(BufferedImage image) {
		BufferedImage image1 = ImageUtil.deepCopy(image);
		readImage(image1);
	}

	/** read and process image.
	 * shallow copy so image may be modified
	 * @param image
	 */
	public void readImage(BufferedImage image) {
		clearVariables();
		setInputImage(image);
		this.height = image.getHeight(null);
		this.width = image.getWidth(null);
		getOrCreateColorSet();
		this.xyRange = new Int2Range(new IntRange(0, width), new IntRange(0, height));
	}

	private void clearVariables() {
		currentImage = null;
		inputImage = null;
		flattenedImage = null;
		height = 0;
		width = 0;
		xyRange = null;
		outputDirectory = null;
		inputFile = null;
		intervalCount = 0;
		sortedFrequencyIndex = null;
		colorValues = null;
		colorCounts = null;
		pixelListList = null;
		count = 0;
		flatten = false;
		colorSet = null;
		rgbNeighbourMap = null;
		colorFrequenciesMap = null;
	}

	public ColorAnalyzer setInputImage(Image image) {
		this.inputImage = (BufferedImage) image;
		this.currentImage = inputImage;
		return this;
	}
	
	public Multiset<RGBColor> getOrCreateColorSet() {
		if (colorSet == null || colorSet.size() == 0) {
			this.colorSet = HashMultiset.create();
			for (int jy = 0; jy < currentImage.getHeight(); jy++) {
				for (int ix = 0; ix < currentImage.getWidth(); ix++) {
					RGBColor color = new RGBColor(currentImage.getRGB(ix, jy));
					colorSet.add(color);
				}
			}
		}
		return colorSet;
	}

	public BufferedImage getInputImage() {
		return inputImage;
	}

	public BufferedImage getCurrentImage() {
		return currentImage;
	}

	public BufferedImage getFlattenedImage() {
		return flattenedImage;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public ColorAnalyzer setXYRange(Int2Range xyRange) {
		this.xyRange = xyRange;
		return this;
	}

	public ColorAnalyzer setStartPlot(int start) {
		this.startPlot = start;
		return this;
	}

	public ColorAnalyzer setEndPlot(int end) {
		this.endPlot = end;
		return this;
	}
	
	public ColorAnalyzer setMinPixelSize(int minPixel) {
		this.minPixelSize = minPixel;
		return this;
	}

	public ColorAnalyzer setMaxPixelSize(int maxPixel) {
		this.maxPixelSize = maxPixel;
		return this;
	}

	public BufferedImage readImage(File file) throws IOException {
		this.inputFile = file;
		if (!file.exists()) {
			throw new IOException("Image file does not exist: "+inputFile);
		}
		inputImage = ImageUtil.readImage(inputFile);
		if (inputImage == null) {
			throw new RuntimeException("Image file could not be read: "+inputFile);
		}
		currentImage = inputImage;
		return currentImage;
		
	}
	
	public File getInputFile() {
		return inputFile;
	}

	public ColorAnalyzer setIntervalCount(int nvals) {
		this.intervalCount = nvals;
		return this;
	}
	
	public BufferedImage sharpenImage(BufferedImage image) {
		BufferedImage newImage = null;
		RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(image);
		RGBImageMatrix filtered = rgbMatrix.applyFilter(ImageUtil.SHARPEN_ARRAY);
		return newImage;
	}

	public void flattenImage() {
		ImageIOUtil.writeImageQuietly(currentImage, new File("target/flatten/before.png"));
		this.flattenedImage = ImageUtil.flattenImage(currentImage, intervalCount);
		currentImage = flattenedImage;
		ImageIOUtil.writeImageQuietly(currentImage, new File("target/flatten/after.png"));
	}
	
	public void analyzeFlattenedColours() {
		getOrCreateColorSet();
		createSortedFrequencies();
		createPixelListsFromColorValues();
		writeSVGAndPNG();
	}

	private void writeSVGAndPNG() {
		if (outputDirectory != null) {
			writePixelListsAsSVG();
			outputImageFilename = "main.png";
			writeMainImage(outputImageFilename);
		}
	}

	private void writeMainImage(String outputName) {
		ImageIOUtil.writeImageQuietly(currentImage, new File(outputDirectory, outputName));
	}

	private void writePixelListsAsSVG() {
		for (int i = 0; i < pixelListList.size(); i++) {
			String hexColorS = Integer.toHexString(colorValues.elementAt(i));
			hexColorS = ColorUtilities.padWithLeadingZero(hexColorS);
			PixelList pixelList = pixelListList.get(i);
			int size = pixelList.size();
			if (size <= maxPixelSize && size >= minPixelSize) {
				if (i >= startPlot && i <= endPlot) {
					SVGG g = new SVGG();
					pixelList.plotPixels(g, "#"+hexColorS);
					// use maximum values for width as we don't want to shift origin
					int xmax = pixelList.getIntBoundingBox().getXRange().getMax();
					int ymax = pixelList.getIntBoundingBox().getYRange().getMax();
					File file = new File(outputDirectory, i+"_"+hexColorS+".svg");
					LOG.trace("output pixels "+file);
					SVGSVG.wrapAndWriteAsSVG(g, file, xmax, ymax);
				}
			}
		}
	}

	
	private void createPixelListsFromColorValues() {
		pixelListList = new ArrayList<PixelList>();
		for (int i = 0; i < colorValues.size(); i++) {
			int colorValue = colorValues.elementAt(i);
			int colorCount = colorCounts.elementAt(i);
			String hex = Integer.toHexString(colorValue);
			PixelList pixelList = PixelList.createPixelList(currentImage, colorValue);
			pixelListList.add(pixelList);
		}
	}

	private void createSortedFrequencies() {
		colorValues = new IntArray();
		colorCounts = new IntArray();
		for (Entry<RGBColor> entry : colorSet.entrySet()) {
			int ii = entry.getElement().getRGBInteger();
			colorValues.addElement(ii);
			colorCounts.addElement(entry.getCount());
			int size = colorValues.size();
		}
		this.sortedFrequencyIndex = colorCounts.indexSortDescending();
		colorCounts = colorCounts.getReorderedArray(sortedFrequencyIndex);
		colorValues = colorValues.getReorderedArray(sortedFrequencyIndex);
	}

	public ColorAnalyzer setOutputDirectory(File file) {
		this.outputDirectory = file;
		outputDirectory.mkdirs();
		return this;
		
	}
	
	

	public ColorAnalyzer defaultPosterize() {
		setStartPlot(1);
		setMaxPixelSize(1000000);
		setIntervalCount(4);
		setEndPlot(15);
		setMinPixelSize(300);
		flattenImage();
		analyzeFlattenedColours();
		return this;
	}

	public void parse(List<String> values) {
		int ival = 0;
		while (ival < values.size()) {
			String value = values.get(ival++);
			if (COUNT.equalsIgnoreCase(value)) {
				count = Integer.parseInt(values.get(ival++));
				this.setIntervalCount(count);
			} else if (AVERAGE.equalsIgnoreCase(value)) {
				flatten = true;
			} else if (MINPIXEL.equalsIgnoreCase(value)) {
				int minPixel = Integer.parseInt(values.get(ival++));
				this.setMinPixelSize(minPixel);
			} else if (MAXPIXEL.equalsIgnoreCase(value)) {
				int maxPixel = Integer.parseInt(values.get(ival++));
				this.setMaxPixelSize(maxPixel);
			} else {
				throw new RuntimeException("unknown arg/param in ColorAnalyzer: "+value);
			}
		}
	}

	public void run() {
		if (flatten) {
			currentImage = ImageUtil.averageImageColors(currentImage);
		}
		this.flattenImage();
		this.analyzeFlattenedColours();

	}

	/** get a list of colours sorted by grayscales.
	 * if colours are not gray, uses avergeGray value.
	 * @return order list of entries (colors with counts)
	 */
	public List<Entry<RGBColor>> createGrayscaleHistogram() {
		Multiset<RGBColor> set = this.getOrCreateColorSet();
		List<Entry<RGBColor>> colorList = new ArrayList<Entry<RGBColor>>(set.entrySet());
		Collections.sort(colorList, new GrayScaleEntryComparator());
		return colorList;
	}
		
	public SVGG createColorFrequencyPlot() {
		Multiset<RGBColor> set = this.getOrCreateColorSet();
		RGBHistogram rgbHistogram = new RGBHistogram(set);


		SVGG g = rgbHistogram.plot();
		return g;
	}

	public RGBNeighbourMap getOrCreateNeighbouringColorMap() {
		if (rgbNeighbourMap == null) {
			getOrCreateColorFrequenciesMap();
			rgbNeighbourMap = new RGBNeighbourMap(colorSet);
		}
		return rgbNeighbourMap;
	}

	/** frequencies of colours.
	 * count indexed by rgbValue
	 * @return
	 */
	public ColorFrequenciesMap getOrCreateColorFrequenciesMap() {
		if (colorFrequenciesMap == null) {
			colorFrequenciesMap = ColorFrequenciesMap.createMap(colorSet);
		}
		return colorFrequenciesMap;
	}

	public BufferedImage mergeMinorColours(BufferedImage image) {
		readImage(image);
		getOrCreateNeighbouringColorMap();
		BufferedImage newImage = ImageUtil.deepCopy(image);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				RGBColor rgbColor = new RGBColor(image.getRGB(i, j));
				RGBColor rgbColor1 = rgbNeighbourMap.getMoreFrequentRGBNeighbour(colorFrequenciesMap, rgbColor);
				newImage.setRGB(i, j, rgbColor1.getRGBInteger());
			}
		}
		return newImage;
	}

	/** extracts the image corresponding to the color.
	 * all other colors are set to WHITE
	 * 
	 * @param color
	 * @return
	 */
	public BufferedImage getImage(RGBColor color) {
		BufferedImage newImage = ImageUtil.deepCopy(inputImage);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				RGBColor rgbColor = new RGBColor(inputImage.getRGB(i, j));
				if (!rgbColor.equals(color)) {
					newImage.setRGB(i, j, RGBColor.HEX_WHITE);
				}
			}
		}
		return newImage;
	}

	/** output all pixels as black unless white.
	 * 
	 * @return
	 */
	public BufferedImage getBinaryImage() {
		BufferedImage newImage = ImageUtil.deepCopy(inputImage);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				RGBColor rgbColor = new RGBColor(inputImage.getRGB(i, j));
				if (rgbColor.equals(RGBColor.RGB_WHITE)) {
					newImage.setRGB(i, j, RGBColor.HEX_WHITE);
				} else {
					newImage.setRGB(i, j, RGBColor.HEX_BLACK);
				}
			}
		}
		return newImage;
	}

	/** calculate grayscale.
	 * 
	 * @return
	 */
	public BufferedImage getGrayscaleImage() {
		BufferedImage newImage = ImageUtil.deepCopy(inputImage);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				RGBColor rgbColor = new RGBColor(inputImage.getRGB(i, j));
				RGBColor grayColor = rgbColor.calculateAverageGray();
				newImage.setRGB(i, j, grayColor.getRGBInteger());
			}
		}
		return newImage;
	}

	/** calculate grayscale.
	 * 
	 * @return
	 */
	public SVGG getGrayscaleColors() {
		BufferedImage newImage = ImageUtil.deepCopy(inputImage);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				RGBColor rgbColor = new RGBColor(inputImage.getRGB(i, j));
				RGBColor grayColor = rgbColor.calculateAverageGray();
				newImage.setRGB(i, j, grayColor.getRGBInteger());
			}
		}
		return this.createColorFrequencyPlot();
	}

	public SVGG createGrayScaleFrequencyPlot() {
		List<Entry<RGBColor>> colorList = this.createGrayscaleHistogram();
		RGBHistogram rgbHistogram = new RGBHistogram(colorList);
		// set params
		SVGG g = rgbHistogram.plot();
		return g;
	}

	public BufferedImage applyAutomaticHistogram(BufferedImage image) {
		double peakFraction = 0.5;
		double cutoffFactor = 0.75;
		List<Entry<RGBColor>> colorList = this.createGrayscaleHistogram();
		IntArray counts = extractCounts(colorList);
		RealArray realCountArray = new RealArray(counts);
		// goes from black 0 to white ffffff
		RealArray firstFilter = RealArray.getFilter(2, Filter.GAUSSIAN_FIRST_DERIVATIVE);
		RealArray firstDerivative = realCountArray.applyFilter(firstFilter);
		double maxFirstDerivative = firstDerivative.getMax();
		int cutoffIndex = (int) firstDerivative.findFirstLocalMaximumafter(0, maxFirstDerivative * peakFraction);
		cutoffIndex *= cutoffFactor; // purely empirical
		RGBColor graycutoff = colorList.get(cutoffIndex).getElement();
		ImageProcessor imageProcessor = new ImageProcessor(image);
		BufferedImage filterImage = imageProcessor.setPixelsAbove(graycutoff, RGBColor.HEX_WHITE);
		
//		RealArray secondFilter = RealArray.getFilter(3, Filter.GAUSSIAN_SECOND_DERIVATIVE);
//		RealArray secondDerivative = realCountArray.applyFilter(secondFilter);
////		LOG.debug(secondDerivative.format(0));

//		LOG.debug(realCountArray);
		return filterImage;
	}

	private IntArray extractCounts(List<Entry<RGBColor>> colorList) {
		IntArray countArray = new IntArray();
//		LOG.debug(colorList.get(0));
		for (int i = 0; i < colorList.size(); i++) {
			Entry<RGBColor> entry = colorList.get(i);
			countArray.addElement(entry.getCount());
		}
		return countArray;
	}

	public BufferedImage mergeImage(BufferedImage mergeImage) {
		BufferedImage newImage = ImageUtil.deepCopy(inputImage);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				RGBColor inputColor = new RGBColor(inputImage.getRGB(i, j));
				RGBColor newColor = new RGBColor(mergeImage.getRGB(i, j));
				if (inputColor.equals(RGBColor.RGB_WHITE)) {
					inputColor = newColor;
				}
				newImage.setRGB(i, j, inputColor.getRGBInteger());
			}
		}
		return newImage;
	}

	public BufferedImage mergeImages(File imageFile1, File... imageFiles) {
		BufferedImage image1 = UtilImageIO.loadImage(imageFile1.toString());
		readImage(image1);
		
		for (File imageFile : imageFiles) {
			BufferedImage image = UtilImageIO.loadImage(imageFile.toString());//
			BufferedImage newImage = mergeImage(image);
			readImage(newImage);
		}
		return getInputImage();
	}

	/**
	 * convert rgb to HSV.
	 */
	public static float[] rgb2hsv( int rgb ) {
		// remove alpha
		rgb &= 0x00ffffff;
		float[] hsv = new float[3];
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
//		int rgbnew = ImageUtil.setRgb(r, g, b);
//		if(rgb != rgbnew) {
//			System.out.println(Integer.toHexString(rgb)+" != "+Integer.toHexString(rgbnew));
//		}
		ColorHsv.rgbToHsv(r, g, b, hsv);
		float[] rgbf = new float[3];
		ColorHsv.hsvToRgb(hsv[0], hsv[1], hsv[2], rgbf);
		ImageUtil.setRgb(rgbf);
		return hsv;

	}

	/**
	 * gets chroma as max(r,g,b) - min(r,g,b)
	 */
	public static int getChroma( int rgb ) {
		rgb &= 0x00FFFFFF;
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8)  & 0xFF;
		int b = rgb & 0xFF;
		int max = Math.max(r, Math.max(g, b));
		int min = Math.min(r, Math.min(g, b));
		int chroma = max - min;
		return chroma;

	}
	
	public void isGray(int rgb) {
		
	}

	/** finds non-gray colouyrs and optionally saturates them
	 * requires setSaturate and setMinChroma to select appropriate values
	 * calculates HSV from RGB and sets them to saturated if > minChroma
	 * Still being developed
	 * @return new coloured image
	 */
	public BufferedImage applySaturation() {
		int width = currentImage.getWidth();
		int height = currentImage.getHeight();
		saturateImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		float[] rgbf = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = currentImage.getRGB(x, y) & 0x00FFFFFF; // remove alpha
				float[] hsvpoint = ColorAnalyzer.rgb2hsv(rgb);
				int chroma = ColorAnalyzer.getChroma(rgb);
				if (chroma > minChroma) {
					// is non-gray
					// maximum saturation
					if (saturate) {
						hsvpoint[1] = 1.0f;
					}
				} else {
					// white? seems to work
					hsvpoint[0] = 0.0f;
					hsvpoint[1] = 0.0f;
//					hsvpoint[2] = 1.0f; //???
				}
				ColorHsv.hsvToRgb(hsvpoint[0], hsvpoint[1], hsvpoint[2], rgbf);
				int rgbnew = ImageUtil.setRgb(rgbf);
				if (rgb != rgbnew) {
//					System.out.println(Integer.toHexString(rgb)+" "+Integer.toHexString(rgbnew));
				}
				saturateImage.setRGB(x, y, rgbnew);
			}
		}
		return saturateImage;
	}

	public void writeSaturateImage(File outfile) {
		if (saturateImage != null) {
			ImageIOUtil.writeImageQuietly(saturateImage, outfile);
			LOG.debug("wrote: "+outfile);
		}
	}

	public BufferedImage applyGray() {
		int width = currentImage.getWidth();
		int height = currentImage.getHeight();
		grayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		float[] rgbf = new float[3];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				boolean usedHsv = true;
				int rgb = currentImage.getRGB(x, y) & 0x00FFFFFF; // remove alpha
				float[] hsvpoint = ColorAnalyzer.rgb2hsv(rgb);
				int chroma = ColorAnalyzer.getChroma(rgb);
				// this gives a BLACK background?
				if ((chroma < maxGrayChroma && includeGray) || 
					(chroma > maxGrayChroma && !includeGray)) {
					// include pixel as-is else
					if (saturate) {
						hsvpoint[1] = 1.0f;
					}
				} else {
//					// set to white? seems to work
//					hsvpoint[0] = 0.0f; // arbitrary H
//					hsvpoint[1] = 0.0f; // saturation S=0, so white?
////					hsvpoint[2] = 0.5f; // not sure what it does, both 0 and 1 
										// create black
					usedHsv = false;
				}
				int rgbnew = 0x00ffffff;
				if (usedHsv) {
					ColorHsv.hsvToRgb(hsvpoint[0], hsvpoint[1], hsvpoint[2], rgbf);
					rgbnew = ImageUtil.setRgb(rgbf);
				}
				grayImage.setRGB(x, y, rgbnew);
			}
		}
		return grayImage;
	}

	public void writeGrayImage(File outfile) {
		if (grayImage != null) {
			ImageIOUtil.writeImageQuietly(grayImage, outfile);
			LOG.debug("wrote: "+outfile);
		}
	}




}
