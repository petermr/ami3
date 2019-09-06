package org.contentmine.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.colour.ColorUtilities;
import org.contentmine.image.colour.RGBColor;
import org.contentmine.image.pixel.MainPixelProcessor;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.processing.Thinning;
import org.contentmine.image.processing.ZhangSuenThinning;
import org.contentmine.image.slice.XSlice;

/**
 * transforms an image independently of future use.
 * 
 * may use a variety of ImageUtil routines
 * 
 * * NOTE: setters return ImageProcessor so they can be chained, e.g.
 * ImageProcessor processor = new
 * ImageProcessor().setThreshold(190).setThinning(null);
 * 
 * 
 * @author pm286
 *
 */
public class ImageProcessor {

	private static final int DEFAULT_THRESHOLD = 129;

	private static final Logger LOG = Logger.getLogger(ImageProcessor.class);

	public static final String DEBUG = "-d";
	public static final String DEBUG1 = "--debug";
	public static final String INPUT = "-i";
	public static final String INPUT1 = "--input";
	public static final String OUTPUT = "-o";
	public static final String OUTPUT1 = "--output";
	public static final String COLOR = "--color";
	public static final String BINARIZE = "-b";
	public static final String BINARIZE1 = "--binarize";
	public static final String THRESH = "-t";
	public static final String THRESH1 = "--threshold";
	public static final String THINNING = "-v";
	public static final String THINNING1 = "--thinning";

	private static final String BINARIZED_PNG = "binarized.png";
	private static final String RAW_IMAGE_PNG = "rawImage.png";
	private static final String TARGET = "target";
	private static final String THINNED_PNG = "thinned.png";

	private boolean binarize;
	private boolean debug = false;
	private BufferedImage image;
	private Thinning thinning;
	private int threshold;
	private MainPixelProcessor mainProcessor;
	private ImageParameters parameters;
	private PixelIslandList islandList = null;
	private XSliceList xSliceList;
	private ColorAnalyzer colorAnalyzer;

	private PixelIsland selectedPixelIsland;
	private BufferedImage binarizedImage;
	private BufferedImage thinnedImage;

	private String base;
	private File inputFile;
	private File inputDir;
	private String inputSuffix;
	private File outputDir;

	private PixelList pixelList;

	public ImageProcessor() {
		setDefaults();
		clearVariables();
	}

	public ImageProcessor(BufferedImage image) {
		this();
		this.image = image;
	}

	public void setDefaults() {
		ensureMainPixelProcessor();
		ensureParameterObject();

		mainProcessor.setDefaults();
		this.setThreshold(getDefaultThreshold());
		this.setThinning(null);
//		this.setThinning(new ZhangSuenThinning());
		this.setOutputDir(getDefaultOutputDirectory());
		this.setBinarize(true);
		this.setThreshold(DEFAULT_THRESHOLD);
		this.setInputSuffix(".png");
	}

	public void clearVariables() {
		mainProcessor.clearVariables();

		image = null;
		inputFile = null;
		// outputDir = null;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public void setBinarize(boolean binarize) {
		this.binarize = binarize;
	}

	public boolean getBinarize() {
		return binarize;
	}

	public ImageProcessor setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

	public boolean getDebug() {
		return debug;
	}

	public ImageProcessor setImage(BufferedImage img) {
		this.image = img;
		return this;
	}

	public BufferedImage getImage() {
		return this.image;
	}

	public void setThinning(Thinning thinning) {
		this.thinning = thinning;
	}
	
	public void setInputSuffix(String inputSuffix) {
		this.inputSuffix = inputSuffix;
	}



	/**
	 * sets threshold.
	 * 
	 * this assumes an image with white background and black lines and
	 * characters.
	 * 
	 * if the antialising bleeds between characters, set the threshold low. Thus
	 * in
	 * 
	 * @param threshold
	 */
	public void setThreshold(int threshold) {
		this.threshold = threshold;
		this.binarize = true;
	}

	public BufferedImage processImageFile(File file) {
		
		if (file == null) {
			throw new RuntimeException("Image file is null: "
					+ file);
		} else if (!file.exists()) {
			if (inputDir != null) {
				file = new File(inputDir, file.getName());
			}
			if (!file.exists()) {
				LOG.error(file.getAbsolutePath().toString());
				throw new RuntimeException("Image file is missing: "
						+ file);
			}
		} else if (file.isDirectory()) {
				throw new RuntimeException("Image file is directory: "
						+ file);
		}
		try {
			inputFile = file;
			image =ImageUtil.readImage(file);
			LOG.trace("read image: "+image);
			if (debug) ImageIO.write(image, "png", new File("target/imageTest.png"));
			image = processImage(image);
			if (debug) ImageIO.write(image, "png", new File("target/imageTest1.png"));
			LOG.trace("processed image");
			return image;
		} catch (Exception e) {
			throw new RuntimeException("Bad image: " + file, e);
		}
	}

	public File getInputFile() {
		return inputFile;
	}

	public BufferedImage processImage(BufferedImage img) {
		islandList = null;
		mainProcessor.clearVariables();
		binarizedImage = null;
		thinnedImage = null;
		this.setImage(img);
		if (debug) {
			LOG.debug("raw "+ImageUtil.createString(image));
			String filename = TARGET + "/" + base + "/" + RAW_IMAGE_PNG;
			ImageIOUtil.writeImageQuietly(this.image, filename);
		}
		if (this.binarize) {
			ColorUtilities.convertTransparentToWhite(image);
			this.image = ImageUtil.boofCVBinarization(this.image, threshold);
			if (debug) LOG.debug("binarized "+ImageUtil.createString(image));
			this.binarizedImage = this.image;
			if (debug) {
				String filename = TARGET + "/" + base + "/" + BINARIZED_PNG;
				ImageIOUtil.writeImageQuietly(this.image, filename);
			}
		}
		if (thinning != null) {
			image = ImageUtil.thin(this.image, thinning);
			this.thinnedImage = this.image;
			if (debug) {
				String filename = TARGET + "/" + base + "/" + THINNED_PNG;
				ImageIOUtil.writeImageQuietly(this.image, filename);
			}
		}
		return this.image;
	}

	public Thinning getThinning() {
		return thinning;
	}

	public int getThreshold() {
		return threshold;
	}

	/**
	 * creates default processor.
	 * 
	 * currently sets binarize=true, thinning=ZhangSuenThinning(), threshold=128
	 * But use getters to query actual values
	 * 
	 * @return
	 */
	public static ImageProcessor createDefaultProcessor() {
		ImageProcessor imageProcessor = new ImageProcessor();
		// defaults are standard
		return imageProcessor;
	}

	/**
	 * creates default processor and processes image.
	 * 
	 * currently sets binarize=true, thinning=ZhangSuenThinning(), threshold=128
	 * But use getters to query actual values
	 * 
	 * @return 
	 * @throws RuntimeException if null image
	 */
	public static ImageProcessor createDefaultProcessorAndProcess(BufferedImage image) {
		if (image == null) {
			throw new RuntimeException("null image ");
		}
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessor();
		imageProcessor.processImage(image);
		return imageProcessor;
	}

	/**
	 * creates default processor and processes image.
	 * 
	 * uses createDefaultProcessorAndProcess(BufferedImage image)
	 * 
	 * @return
	 */
	public static ImageProcessor createDefaultProcessorAndProcess(File imageFile) {
		if (imageFile == null) {
			throw new RuntimeException("null image file");
		} else if (!imageFile.exists()) {
			throw new RuntimeException("file does not exist " + imageFile);
		} else if (imageFile.isDirectory()) {
			throw new RuntimeException("File is directory " + imageFile);
		} else {
			try {
				return ImageProcessor.createDefaultProcessorAndProcess(ImageUtil.readImage(imageFile));
			} catch (Exception e) {
				throw new RuntimeException("image file exists but cannot read as image: "
						+ imageFile, e);
			}
		}
	}

	public String getBase() {
		if (base == null && inputFile != null) {
			base = FilenameUtils.getBaseName(inputFile.toString());
		}
		return base;
	}

	private static int getDefaultThreshold() {
		return DEFAULT_THRESHOLD;
	}

	public void readAndProcessFile() {
		readAndProcessFile(inputFile);
	}

	public void readAndProcessFile(File file) {
		if (file != null && !file.isDirectory()) {
			this.setInputFile(file);
			processImageFile(file);
		}

	}

	public void setInputFile(File file) {
		this.inputFile = file;
	}

	public BufferedImage processImageFile() {
		readImageFile();
		processImage(image);
		return image;
	}

	public void processImage() {
		if (image != null) {
			if (colorAnalyzer != null) {
				colorAnalyzer.setInputImage(image);
				colorAnalyzer.setOutputDirectory(outputDir);
				colorAnalyzer.run();
			} else {
				image = processImage(image);
			}
		}
	}

	public void readImageFile() {
		if (image == null) {
			if (inputFile == null || !inputFile.exists()) {
				throw new RuntimeException("File does not exist: " + inputFile);
			}
			if (getBase() == null) {
				setBase(FilenameUtils.getBaseName(inputFile.toString()));
			}
				image = ImageUtil.readImage(inputFile);
				LOG.trace("read image " + image);
		}
	}

	public void debug() {
		System.err.println("input:     "
				+ ((inputFile == null) ? "null" : inputFile.getAbsolutePath()));
		System.err.println("output:    "
				+ ((outputDir == null) ? "null" : outputDir.getAbsolutePath()));
		System.err.println("threshold: " + threshold);
		System.err.println("thinning:  " + thinning);
		mainProcessor.debug();
	}

	public MainPixelProcessor getPixelProcessor() {
		return this.mainProcessor;
	}

	/**
	 * creates a default ImageProcessor and immediately processes Image.
	 * 
	 * @param image
	 * @return
	 */
	public static ImageProcessor readAndProcess(BufferedImage image) {
		ImageProcessor imageProcessor = new ImageProcessor(image);
		imageProcessor.processImage(image);
		return imageProcessor;
	}

	public PixelIslandList getOrCreatePixelIslandList() {
		if (islandList == null) {
			ensureMainPixelProcessor();
			// this is messy - the super thinning should have been done earlier
			islandList = mainProcessor.getOrCreatePixelIslandList(thinning != null);
			if (islandList == null) {
				LOG.debug("ERROR Could not create islandList");
				islandList = new PixelIslandList();
			} else {
				islandList.setMainProcessor(mainProcessor);
			}
		}
		return islandList;
	}

	/** creates list via pixelIslands
	 * 
	 * @return
	 */
	public PixelList getOrCreatePixelList() {
		if (pixelList == null) {
			getOrCreatePixelIslandList();
			pixelList = new PixelList();
			for (PixelIsland pixelIsland : islandList) {
				pixelList.addAll(pixelIsland.getPixelList());
			}
		}
		return pixelList;
	}

	public MainPixelProcessor ensureMainPixelProcessor() {
		ensureParameterObject();
		if (mainProcessor == null) {
			mainProcessor = new MainPixelProcessor(this);
			mainProcessor.setParameters(this.parameters);
		}
		return mainProcessor;
	}

	private void ensureParameterObject() {
		if (this.parameters == null) {
			parameters = new ImageParameters();
		}
	}

	public static File getDefaultOutputDirectory() {
		return new File(TARGET);
	}

	public void setOutputDir(File file) {
		if (file == null) {
			throw new RuntimeException("Null output directory");
		}
		this.outputDir = file;
		outputDir.mkdirs();
	}

	public File getOutputDir() {
		return outputDir;
	}

	public void usage() {
		System.err.println("  imageanalysis options:");
		System.err.println("       " + INPUT + " " + INPUT1
				+ "        input file (directory not yet supported)");
		System.err.println("       " + OUTPUT + " " + OUTPUT1
				+ "        output directory; def="
				+ getDefaultOutputDirectory());
		System.err.println("       " + BINARIZE + " " + BINARIZE1
				+ "        set binarize on");
		System.err.println("       " + DEBUG + " " + DEBUG1
				+ "        set debug on");
		System.err.println("       " + THRESH + " " + THRESH1
				+ "    threshold (default: " + getDefaultThreshold() + ")");
		System.err.println("       " + THINNING + " " + THINNING1
				+ "    thinning ('none', 'z' (ZhangSuen))");
	}

	protected void parseArgs(ArgIterator argIterator) {
		if (argIterator.size() == 0) {
			usage();
		} else {
			while (argIterator.hasNext()) {
				if (debug) {
					LOG.trace(argIterator.getCurrent());
				}
				parseArgAndAdvance(argIterator);
			}
		}
		if (debug) {
			this.debug();
		}
	}

	public boolean parseArgAndAdvance(ArgIterator argIterator) {
		boolean found = true;
		ensureMainPixelProcessor();
		String arg = argIterator.getCurrent();
		if (debug) {
//			LOG.debug(arg);
		}
		if (false) {

		} else if (arg.equals(ImageProcessor.DEBUG)
				|| arg.equals(ImageProcessor.DEBUG1)) {
			debug = true;
			argIterator.setDebug(true);
			argIterator.next();

		} else if (arg.equals(BINARIZE) || arg.equals(BINARIZE1)) {
			this.setBinarize(true);
			argIterator.next();
		} else if (arg.equals(INPUT) || arg.equals(INPUT1)) {
			String value = argIterator.getSingleValue();
			if (value != null) {
				setInputFile(new File(value));
			}
		} else if (arg.equals(OUTPUT) || arg.equals(OUTPUT1)) {
			String value = argIterator.getSingleValue();
			if (value != null) {
				setOutputDir(new File(value));
			}
		} else if (arg.equals(COLOR)) {
			List<String> values = argIterator.getValues();
			parsePoster(values);
		} else if (arg.equals(THINNING) || arg.equals(THINNING1)) {
			String value = argIterator.getSingleValue();
			if (value != null) {
				setThin(value);
			}
		} else if (arg.equals(THRESH) || arg.equals(THRESH1)) {
			Integer value = argIterator.getSingleIntegerValue();
			if (value != null) {
				setThreshold(value);
			}
		} else {
			found = mainProcessor.processArg(argIterator);
			if (!found) {
				LOG.trace("skipped unknown token: " + argIterator.getLast());
				argIterator.next();
			}
		}
		return found;
	}

	private void parsePoster(List<String> values) {
		ensureColorAnalyzer();
		colorAnalyzer.parse(values);
	}

	private void ensureColorAnalyzer() {
		if (colorAnalyzer == null) {
			colorAnalyzer = new ColorAnalyzer();
		}
	}

	private void setThin(String thinningS) {
		if (thinningS == null) {
			throw new RuntimeException(
					"no thinning argument [for none use 'none']");
		} else if (thinningS.equalsIgnoreCase("none")) {
			setThinning(null);
		} else if (thinningS.equalsIgnoreCase("z")) {
			setThinning(new ZhangSuenThinning());
		} else {
			LOG.error("unknown thinning argument: " + thinningS);
		}
	}

	public static void main(String[] args) throws Exception {
		ImageProcessor imageProcessor = new ImageProcessor();
		ArgIterator argIterator = new ArgIterator(args);
		imageProcessor.processArgsAndRun(argIterator);
	}

	private void processArgsAndRun(ArgIterator argIterator) {
		if (argIterator.size() == 0) {
			this.usage();
		} else {
			this.parseArgs(argIterator);
			this.runCommands();
		}
	}

	void runCommands() {
		ensureMainPixelProcessor();
		if (this.image == null) {
			if (inputFile != null) {
				processImageFile();
			} else {
				throw new RuntimeException("no image file to process");
			}
		}
		
		islandList = mainProcessor.getOrCreatePixelIslandList();
		islandList.sortBySizeDescending();
		int selectedIslandIndex = mainProcessor.getSelectedIslandIndex();
		selectedPixelIsland = (selectedIslandIndex == -1) ? null : islandList.get(selectedIslandIndex);
	}

	public void parseArgs(String[] args) {
		ArgIterator argIterator = new ArgIterator(args);
		while (argIterator.hasNext()) {
			parseArgAndAdvance(argIterator);
		}
	}

	public void parseArgsAndRun(String[] args) {
		this.parseArgs(args);
		this.runCommands();
	}


	public void parseArgs(String argString) {
		if (argString != null) {
			parseArgs(argString.trim().split("\\s+"));
		}
	}

	public void parseArgsAndRun(String argString) {
		if (argString != null) {
			parseArgsAndRun(argString.trim().split("\\s+"));
		}
	}

	public ImageParameters getParameters() {
		return parameters;
	}

	public void setParameters(ImageParameters parameters) {
		this.parameters = parameters;
	}

	public XSliceList getOrCreateXSliceList() {
		if (xSliceList == null) {
			SVGG g = new SVGG();
			xSliceList = new XSliceList();
			int width = image.getWidth();
			for (int x = 0; x < width; x++) {
				XSlice xSlice = XSlice.getBinarySlice(image, x);
				if (xSlice.size() > 0) {
					xSliceList.add(xSlice);
					SVGG gg = xSlice.getSVGG();
					g.appendChild(gg);
				}
			}
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/neuro/text.svg"));
		}
		return xSliceList;
	}

	public PixelIsland getPixelIsland() {
		return selectedPixelIsland;
	}

	public BufferedImage getBinarizedImage() {
		return binarizedImage;
	}

	public BufferedImage getThinnedImage() {
		return thinnedImage;
	}

	/** filter image by replacing with color.
	 * 
	 * @param graycutoff
	 * @param replacementColor
	 * @return
	 */
	public BufferedImage setPixelsBelow(RGBColor graycutoff, int replacementColor) {
		BufferedImage newImage = ImageUtil.deepCopy(image);
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				RGBColor gray = new RGBColor(image.getRGB(i, j)).calculateAverageGray();
				if (gray.getRGBInteger() - graycutoff.getRGBInteger() < 0) {
					newImage.setRGB(i, j, replacementColor);
				}
			}
		}
		return newImage;
	}

	/** filter image by replacing with color.
	 * 
	 * @param graycutoff
	 * @param replacementColor
	 * @return
	 */
	public BufferedImage setPixelsAbove(RGBColor graycutoff, int replacementColor) {
		BufferedImage newImage = ImageUtil.deepCopy(image);
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				RGBColor gray = new RGBColor(image.getRGB(i, j)).calculateAverageGray();
				if (gray.getRGBInteger() - graycutoff.getRGBInteger() > 0) {
					newImage.setRGB(i, j, replacementColor);
				}
			}
		}
		return newImage;
	}

	public void setInputDir(File inputDir) {
		this.inputDir = inputDir;
	}
	
	public File getInputDir() {
		return inputDir;
	}

	public String getInputSuffix() {
		return inputSuffix;
	}

	public BufferedImage flipBinary() {
		BufferedImage newImage = ImageUtil.deepCopy(image);
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int oldColor = image.getRGB(i, j);
				oldColor &= 0x00ffffff;
				int newColor = oldColor ^ 0xffffff;
				newImage.setRGB(i, j, newColor);
			}
		}
		return newImage;
	}

	public MainPixelProcessor getMainProcessor() {
		return mainProcessor;
	}

}
