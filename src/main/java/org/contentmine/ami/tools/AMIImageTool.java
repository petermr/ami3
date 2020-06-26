package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.ImageParameterAnalyzer.ImageParameters;
import org.contentmine.ami.tools.image.AnnotatedImage;
import org.contentmine.ami.tools.template.AbstractTemplateElement;
import org.contentmine.ami.tools.template.ImageTemplateElement;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.ImageUtil.SharpenMethod;
import org.contentmine.image.ImageUtil.ThresholdMethod;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.colour.Octree;
import org.contentmine.image.colour.RGBNeighbourMap;

import com.google.common.collect.Multiset;

import nu.xom.Element;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */



@Command(
name = "image",
description = {
		"Transforms image contents but only provides basic filtering (see ami-filter).",
		" Services include %n"
				+ " OBSOLETE - see ami-filter"
				+ "%n identification of duplicate images, and removal<.li>"
				+ "%n rejection of images less than gven size</li>"
				+ "%n rejection of monochrome images (e.g. all white or all black) (NB black and white is 'binary/ized'"
				+ ""

				+ "Then TRANSFORMS contents"
				+ " geometric scaling of images using Imgscalr, with interpolation. Increasing scale on small fonts can help OCR, "
				+ "decreasing scale on large pixel maps can help performance."
				+ ""
				+ "subimages (e.g. colour layers)"
				
				+ "NOTE: a missing option means it is not applied (value null). Generally no defaults"
})
public class AMIImageTool extends AbstractAMITool implements HasImageDir {
	private static final String DESPECKLE = "_ds";

	private static final String BBB = "_b_";
	private static final String ERODE = "_e";
	private static final String IMAGE = "image";
	private static final String OCTREE = "_o";
	private static final String POSTER = "_p";
	private static final String SCALEFACTOR = "_sc_";
	private static final String SHARP4 = "_s4";
	private static final String THRESHOLD = "_thr_";

	private static final Logger LOG = LogManager.getLogger(AMIImageTool.class);
public enum AMIImageType {
		NONE("none", 0, new AMIImageType[]{}),
		RAW("raw", NONE.priority + 1, new AMIImageType[]{}),
		BORDER("border", RAW.priority + 1, AMIImageType.RAW),
		SCALE("scale", RAW.priority + 1, AMIImageType.BORDER, AMIImageType.RAW),
		ROTATE("rotate", SCALE.priority + 1, AMIImageType.SCALE, AMIImageType.RAW),
		SHARPEN("sharpen", ROTATE.priority + 1, AMIImageType.ROTATE, AMIImageType.SCALE, AMIImageType.RAW),
		POSTERIZE("posterize", SHARPEN.priority + 1, AMIImageType.SHARPEN, AMIImageType.ROTATE, AMIImageType.SCALE, AMIImageType.RAW),
		BINARIZE("binarize", POSTERIZE.priority + 1, AMIImageType.SHARPEN,AMIImageType.ROTATE, AMIImageType.SCALE, AMIImageType.RAW),
		ERODE_DILATE("erodeDilate", BINARIZE.priority + 1, AMIImageType.BINARIZE, AMIImageType.SHARPEN, AMIImageType.ROTATE, AMIImageType.SCALE, AMIImageType.RAW),
		;
		private AMIImageType[] imageTypes;
		private String name;
		private int priority;

		/** imageTypes are ordered list of files to be processed in decreasingly level of processing.
		 * They may be of the form rotate_180,.
		 * the tool searches back till it finds the first existing lower level
		 * thus binarize would act on sharpen_1.png rather than raw.png
		 * @param name
		 * @param imageTypes
		 */
		private AMIImageType(String name, int priority, AMIImageType ...imageTypes) {
			this.name = name;
			this.priority = priority;
			this.imageTypes = imageTypes;
		}
		/** image type is determined by leading string in filename.
		 * 
		 * @param filename
		 * @return
		 */
		public final static AMIImageType getImageType(String filename) {
			for (AMIImageType imageType : values()) {
				if (filename != null && filename.startsWith(imageType.name)) {
//					LOG.debug("type "+imageType);
					return imageType;
				}
			}
			return (AMIImageType) null;
		}
		
		/**
		 * find File in files that has the highest priority.
		 * iterates over all files to find the one with highest AMIImageType priority,
		 * if priorityLimitType is set, excludes files with priorities above this value.
		 * if priorityLimitType is set to RAW, forces the use of RAW files.
		 * 
		 * @param files
		 * @param priorityLimitType
		 * @return
		 */
		public static File getHighestLevelFile(List<File> files, AMIImageType priorityLimitType) {
			// crude
			int highestPriority = -1;
			int priorityLimit = priorityLimitType == null ? Integer.MAX_VALUE : priorityLimitType.priority;
			File highestFile = null;
			for (File file : files) {
				AMIImageType imageType = getImageType(FilenameUtils.getBaseName(file.getName()));
				int priority = imageType == null ? -1 : imageType.priority;
				if (priority > highestPriority && priority <= priorityLimit) {
					highestPriority = priority;
					highestFile = file;
				}
			}
			return highestFile;
		}
	}
	
	public enum ImageToolkit {
		Boofcv,
		Scalr,
		Pmr,
	}
	public enum InExclusion {
		include,
		exclude,
	}

	
	public enum OutputFile {
		binary("save binary (b/w)"),
		channels("write images for each colour channel"),
		histogram("color frequency histogram"),
		html("output html"),
		neighbours("color neighbour map"),
		octree("save octree"),
		poster("save posterized"),
		;
		private String desc;

		private OutputFile(String desc) {
			this.desc = desc;
		}
		
	}
	
	public enum PanelKey{
		bback("blackbackground"),
		bborder("blackborder"),
		letter("index letter"),
		maxx("max xcoord"),
		maxy("max ycoord"),
		minx("minimum x"),
		miny("minimum y"),
		number("index number"),
		roman("index roman numeral"),
		wback("white background"),
		wgutter("white gutter"),
		xlabel("x label"),
		ylabel("y label"),
		;
		private String title;

		private PanelKey(String title) {
			this.title = title;
		}
		
	}
	
	interface AbstractDest {}
		
	private static final String _DELETE = "_delete";

    // FILTER OPTIONS

    @Option(names = {"--borders"},
    		arity = "1..2",
            description = "add borders: 1 == all; 2 : top/bottom, edges, "
            + "4 vals = top, right bottom, left; default NONE")
	private List<Integer> borders = null ;

    @Option(names = {"--filter"},
            description = "pre-runs default FILTER (i.e. without args), duplicate, small, monochrome"
            )
	private boolean filter;

    @Option(names = {"--exclude"},
    		split = "\\|", splitSynopsisLabel = "|",
            description = "filter on image properties (${COMPLETION-CANDIDATES})"
            )
	private Map<ImageParameters, String> excludeMap;
    
    @Option(names = {"--include"},
    		split = "\\|", splitSynopsisLabel = "|",
            description = "filter on image properties (${COMPLETION-CANDIDATES})"
            )
	private Map<ImageParameters, String> includeMap;
    
    @Option(names = {"--minheight"},
    		arity="1",
    		defaultValue = "100",
            description = "minimum height (pixels) to accept")
    private int minHeight;

    @Option(names = {"--minwidth"},
    		arity = "1",
    		defaultValue = "100",
            description = "minimum width (pixels) to accept")
    private int minWidth;
        
    // TRANSFORM OPTIONS
    
    @Option(names = {"--annotate"},
            description = "create AnnotatedImage for each image or panel")
    private boolean annotate = false;

    @Option(names = {"--binarize"},
    		arity = "1",
            description = "TRANSFORM: create binary (normally black and white); methods local_mean ... (default: ${DEFAULT-VALUE})")
    private ThresholdMethod binarize = null;

    @Option(names = {"--despeckle"},
            description = "TRANSFORM: remove single pixels surrounded by whitespace "
            		+ "run AFTER any other image enhancement. default FALSE")
    private Boolean despeckle = false;

    @Option(names = {"--erodedilate"},
            description = "TRANSFORM: erode 1-pixel layer and then dilate. "
            		+ "Removes minor spikes (default: FALSE ${DEFAULT-VALUE}); generally destroys fine details."
            		+ "(currently doesn't work - wiped out most image")
    private Boolean erodeDilate = false;

    @Option(names = {"--maxheight"},
    		defaultValue = "1000",
            description = "maximum height (pixels) to accept. If larger, scales the image (default: ${DEFAULT-VALUE})")
    private Integer maxHeight;

    @Option(names = {"--maxwidth"},
    		arity = "1",
    		defaultValue = "1000",
            description = "maximum width (pixels) to accept. If larger, scales the image (default: ${DEFAULT-VALUE})")
    private Integer maxWidth;
    
    @Option(names = {"--mediancut"},
//    		arity = "1",
//    		defaultValue = "1000",
            description = "median cut (Heckbert)  NYI")
    private Object what = null;
    
    @Option(names = {"--merge"},
    		arity = "1",
            description = "merge neighbouring map colours for (merge) cycles;"
            		+ "experimental")
	private Integer nMerge = 0;

    @Option(names = {"--minpixels"},
    		arity = "1",
            description = "minimum number of pixels in output image")
	private Integer minPixels = 100;

    @Option(names = {"--octree"},
    		arity = "1",
            description = "levels of quantization using Octree (power of 2)")
    private Integer octreeCount;

    @Option(names = {"--outputfiles"},
    		arity = "1..*",
            description = "output files related to quantization (${COMPLETION-CANDIDATES})")
    private List<OutputFile> outputFiles;

    @Option(names = {"--panels"},
    		arity = "1..*",
            description = "split images into panels")
    private Map<PanelKey, String> panelMap;

    @Option(names = {"--posterize"},
    		arity = "1",
    		fallbackValue = "4",
            description = "flatten colors to set number, must be power-of-2 (default: ${DEFAULT-VALUE}) "
            		+ "to create a map of colors")
    private Integer posterizeCount = null;

    @Option(names = {"--priority"},
    		arity = "1",
    		defaultValue = "RAW",
            description = "force transformations starting with the lowest priority (usually 'raw'). Probably obsolete")
    private AMIImageType priorityImage = AMIImageType.RAW;

    @Option(names = {"--rotate"},
    		arity = "1",
            description = "rotates image anticlockwise by <value> degrees. Currently 90, 180, 270 (default: ${DEFAULT-VALUE})")
    private Integer rotateAngle = null;
    
    @Option(names = {"--scalefactor"},
    		arity = "1",
    		description = "geometrical scalefactor. if missing, no scaling (don't use 1.0) Uses Imgscalr library. default NONE ")
	private Double scalefactor = null;

    @Option(names = {"--sharpen"},
    		arity = "1",
            description = "sharpen image using Laplacian kernel or sharpen4 or sharpen8 (BoofCV)..(default: NONE ${DEFAULT-VALUE})")
    private String sharpen = "none";

    @Option(names = {"--template"},
    		arity = "1",
            description = "use template in each image.*/ dir to process image")
    private String templateFilename = null /*"template.xml"*/;

    @Option(names = {"--thinning"},
    		arity = "1",
    		defaultValue = "null",
            description = "thinning algorithm. Currently under development. (default: ${DEFAULT-VALUE})")
    private String thinning = null;

    @Option(names = {"--threshold"},
    		arity = "1",
            description = "maximum value for black pixels (non-background) (default: NONE ${DEFAULT-VALUE})."
            		+ "between 120 and 180 (200 for lighter grey images) seems useful."
            )
    
    private Integer threshold = null;

    @Option(names = {"--toolkit"},
    		arity = "1",
            description = "Image toolkit to use., "
            		+ "Scalr (Imgscalr), simple but no longer developed. Pmr (my own) when all else fails.(default: ${DEFAULT-VALUE}) (not yet fully worked out)")
    private ImageToolkit toolkit = null;

	public static final String DUPLICATES = "duplicates/";
	public static final String MONOCHROME = "monochrome/";
	public static final String LARGE = "large/";
	public static final String SMALL = "small/";
	private static final String ROT = "rot";
	private static final String RAW = "raw";

	private static final String BORDER = "border";
	private static final String SCALE = "scale";

	private Multiset<String> duplicateSet;
	private SharpenMethod sharpenMethod;
	private AbstractTemplateElement templateElement;

	private File imageDir;
	private HtmlDiv imageDiv;
	private Set<Long> commonImageHashSet;
	private Map<BufferedImage, ImageParameterAnalyzer> parameterAnalyzerByImage;

	private Map<BufferedImage, AnnotatedImage> annotatedImageByImage;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIImageTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIImageTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIImageTool().runCommands(args);
    }

	@Override
	protected void parseSpecifics() {
		super.parseSpecifics();
	}


    @Override
    protected void runSpecifics() {
    	getSharpenMethod();
    	if (processTrees()) { 
    	} else {
//			LOG.error("must give cProject or cTree");
	    }
    }

	protected void runPrevious() {
		if (filter) {
    		new AMIFilterTool().setCProject(cProject).setCTree(cTree).runCommands();
    	}
	}

	private void getSharpenMethod() {
		sharpenMethod = SharpenMethod.getMethod(sharpen);
	}

	protected boolean processTree() {
		processedTree = true;
		LOG.info("AMIImageTool processTree");
		ImageDirProcessor imageDirProcessor = new ImageDirProcessor(this, cTree);
		processedTree = imageDirProcessor.processImageDirs();
		return processedTree;
	}

	private void processSingleImageFile(File imageFile) {
		if (imageFile == null) {
			LOG.debug("processSingleImageFile: null file");
		} else {
			imageDir = imageFile.getParentFile();
			processTransformImageDir();
			if (annotate) {
				AnnotatedImage a;
				File annotateFile = new File(imageDir, FilenameUtils.getBaseName(imageFile.toString()) + ".annot" + "." + "html");
				try {
					FileUtils.write(annotateFile, "annotate", CMineUtil.UTF_8);
					LOG.info(">ann>");
				} catch (IOException e) {
					LOG.error("cannot write "+annotateFile);
				}
			}
		}
	}


	private void processTransformImageDir() {
		if (!imageDir.exists()) {
			LOG.debug("Dir does not exist: "+imageDir);
		} else {
			String filename = imageDir.getName();
			String grandParentName = imageDir.getParentFile().getParentFile().getName();
			LOG.warn("{}//{}", grandParentName, filename);
			if (templateFilename != null) {
				templateElement = AbstractTemplateElement.readTemplateElement(imageDir, templateFilename);
			}
			if (templateElement != null) {
				processTemplate();
			} else {
				try {
					runTransform(getInputBasename());
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("Bad read: "+imageDir+" ("+e.getMessage()+")");
				}
			}
		}
	}
	

	protected void processTreeTransform() {
		LOG.warn("transformImages cTree: {}", cTree.getName());
		File pdfImagesDir = cTree.getExistingPDFImagesDir();
		if (pdfImagesDir == null) {
			LOG.warn("Cannot find pdfImages for cTree {}", cTree.getName());
			return;
		}
		if (getInputBasename() == null) {
			LOG.warn("Assuming base: {}", RAW);
			setInputBasename(RAW);
		}
		List<File> imageDirs = CMineGlobber.listSortedChildDirectories(pdfImagesDir);
		Collections.sort(imageDirs);
		for (File imageDir : imageDirs) {
			if (imageDir.getName().startsWith(IMAGE)) {
				System.err.print("."); // TODO progress indicator util
				processTransformImageDir();
			}
			continue;
		}
	}

	private void processTemplate() {
		List<Element> imageElements = XMLUtil.getQueryElements(templateElement, "./*[local-name()='"+ImageTemplateElement.TAG+"']");
		for (int i = 0; i < imageElements.size(); i++) {
			((ImageTemplateElement) imageElements.get(i)).process();
		}
	}

	// ================= transform ===============
	
	private void runTransform(String inputBasename) {
		File imageFile = new File(imageDir, inputBasename + "." + CTree.PNG);
		if (!imageFile.exists()) {
			LOG.warn("non-existent image file: {}", AMIImageTool.shortName(imageFile));
			return;
		}
		LOG.warn("transforming: {}", AMIImageTool.shortName(imageFile));
		BufferedImage image = ImageUtil.readImageQuietly(imageFile);
		String basename = FilenameUtils.getBaseName(imageFile.toString());
		if (image != null) {
			imageDiv = new HtmlDiv(); 
			imageDiv.setAttribute("file", imageFile.toString());
			if (rotateAngle != null) {
				image = rotateAndSave(image);
			}
			if (scalefactor != null) {
				image = scaleAndSave(image);
				basename += SCALEFACTOR+(int)(double)scalefactor;
			}
			if (sharpen != null && !SharpenMethod.NONE.toString().contentEquals(sharpen)) {
				image = sharpenAndSave(image);
				basename += SHARP4;
			}
			if (borders != null) {
				image = bordersAndSave(image);
				basename += BORDER +borders.toString().replaceAll("(\\[|\\])", "");
			}
			if (erodeDilate) {
				image = erodeDilateAndSave(image);
				basename += ERODE;
			}
			if (binarize != null || threshold != null) {
				image = binarizeAndSave(image);
				if (binarize != null) {
					basename += binarize.name();
				}
				if (threshold != null) {
					basename += THRESHOLD+threshold.toString();
				} 
			}
			
			if (nMerge > 0) {
				ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
				image = colorAnalyzer.repeatedlyMergeMinorColors(image, nMerge);
			}

			if (octreeCount != null) {
				image = octreeAndSave(image);
				basename += OCTREE+octreeCount;
			}
			if (posterizeCount != null) {
				image = posterizeAndSave(image);
				basename += POSTER+posterizeCount;
			}
			if (despeckle) {
				image = despeckleAndSave(image);
				basename += DESPECKLE.toString();
				
			}
			File outfile = new File(imageDir, basename + "." + CTree.PNG);
			ImageIOUtil.writeImageQuietly(image, outfile);
			File htmlFile = new File(imageDir, "images" + "." + CTree.HTML);
			
			LOG.warn("htmlFile {}", htmlFile);
			XMLUtil.writeQuietly(imageDiv, htmlFile, 1);
		}
	}

	public static String shortName(File imageFile) {
		return imageFile.getParentFile().getName()+"/"+imageFile.getName();
	}

	private BufferedImage octreeAndSave(BufferedImage image) {
		if (octreeCount != null) {
			ColorAnalyzer analyzer = new ColorAnalyzer(image);
			Octree octree = new Octree()
					.readImage(image)
					.setColourCount(octreeCount)
					.quantize();
			BufferedImage outImage = octree.getOutImage();
			outputColourAnalysis(outImage);

		}
		return image;
	}

	private BufferedImage posterizeAndSave(BufferedImage image) {
		if (posterizeCount != null) {
			ColorAnalyzer analyzer = new ColorAnalyzer(image);
			analyzer.setIntervalCount(posterizeCount);
			image = ImageUtil.flattenImage(image, posterizeCount);
			outputColourAnalysis(image);
		}
		return image;
	}

	private BufferedImage erodeDilateAndSave(BufferedImage image) {
		image = ImageUtil.thresholdBoofcv(image, erodeDilate);
		if (verbosity().length > 1) {
			File outputPng = new File(imageDir, "erodeDilate"+"."+CTree.PNG);
			LOG.debug("writing {}", outputPng);
//			ImageUtil.writeImageQuietly(image, outputPng);
		}
		return image;
	}

	private BufferedImage despeckleAndSave(BufferedImage image) {
		image = ImageUtil.despeckle(image);
		if (verbosity().length > 1) {
			File outputPng = new File(imageDir, "despeckle"+"."+CTree.PNG);
			LOG.debug("writing {}", outputPng);
			ImageUtil.writeImageQuietly(image, outputPng);
		}
		return image;
	}

	/** binarize withotu explict threshold
	 * 
	 * @param image
	 * @return
	 */
	private BufferedImage binarizeAndSave(BufferedImage image) {
		int[] oldRGB = {0x000d0d0d};
		int[] newRGB = {0x00ffffff};
		
		// binarization follows sharpen
		String type = null;
		if (image == null) {
			throw new RuntimeException("null image in binarize");
		} else if (binarize != null) {
			image = ImageUtil.boofCVThreshold(image, binarize); // this fails
			image = ImageUtil.thresholdBoofcv(image, erodeDilate);
			image = ImageUtil.removeAlpha(image);
			image = ImageUtil.magnifyToWhite(image);
			LOG.debug("colors0 {}", ImageUtil.createHexMultiset(image));
			image = ImageUtil.convertRGB(image, oldRGB, newRGB);
			
			Integer color = ImageUtil.getSingleColor(image);
			LOG.debug("colors {}", ImageUtil.createHexMultiset(image));
			if (color != null) {
				throw new RuntimeException("Single color: "+color+" Corrupt conversion?");
			}
			type = binarize.toString().toLowerCase();
			// debug
		} else if (threshold != null) {
			image = ImageUtil.boofCVBinarization(image, threshold);
			type = "threshold"+"_"+threshold;
		}
		if (image != null) {
//			ImageUtil.writeImageQuietly(image, new File(imageDir, type+"."+CTree.PNG));
		}
		return image;
	}

	private BufferedImage sharpenAndSave(BufferedImage image) {
		BufferedImage resultImage = null;
		if (ImageToolkit.Boofcv.equals(toolkit)) {
			resultImage = ImageUtil.sharpenBoofcv(image, sharpenMethod);
		} else if (SharpenMethod.LAPLACIAN.toString().equals(sharpen)) {
			resultImage = ImageUtil.laplacianSharpen(image);
		} else if (SharpenMethod.SHARPEN4.toString().equals(sharpen)) {
			resultImage = ImageUtil.sharpen(image, SharpenMethod.SHARPEN4);
		} else if (SharpenMethod.SHARPEN8.toString().equals(sharpen)) {
			resultImage = ImageUtil.sharpen(image, SharpenMethod.SHARPEN8);
		} else if (SharpenMethod.NONE.toString().equals(sharpen)) {
			resultImage = image;
		} 
		if (resultImage != null ) {
//			ImageUtil.writeImageQuietly(resultImage, new File(imageDir, sharpenMethod+"."+CTree.PNG));
		}
		return resultImage;
	}

	private BufferedImage rotateAndSave(BufferedImage image) {
		if (rotateAngle != null && rotateAngle % 90 == 0) {
			// can't find a boofcv rotate
			if (false && ImageToolkit.Boofcv.equals(toolkit)) {
//				image = ImageUtil.getRotatedImage(image, rotateAngle);
			} else if (true || ImageToolkit.Scalr.equals(toolkit)) {
				image = ImageUtil.getRotatedImageScalr(image, rotateAngle);
			}
			ImageUtil.writeImageQuietly(image, new File(imageDir, ROT + "_"+rotateAngle+"."+CTree.PNG));
		}
		return image;
	}

	private BufferedImage scaleAndSave(BufferedImage image) {
		Double scale = scalefactor != null ? scalefactor :
			ImageUtil.getScaleToFitImageToLimits(image, maxWidth, maxHeight);
		if (!Real.isEqual(scale,  1.0,  0.0000001)) {
			if (ImageToolkit.Scalr.equals(toolkit)) {
				image = ImageUtil.scaleImageScalr(scale, image);
			} else if (ImageToolkit.Boofcv.equals(toolkit)) {
				throw new RuntimeException("Boofcv scale NYI");
			} else if (scale > 1.9){
				int intScale = (int) Math.round(scale);
				image = ImageUtil.scaleImage(image, intScale, intScale);
			}
			String scaleValue = String.valueOf(scalefactor).replace(".",  "_");
			ImageUtil.writeImageQuietly(image, new File(imageDir, SCALE + " _ " + scaleValue + "." + CTree.PNG));
		}
		return image;
	}
	
	private BufferedImage bordersAndSave(BufferedImage image) {
		int color = 0x00FFFFFF;
		int xBorder = borders.get(0);
		int yBorder = borders.size() > 1 ? borders.get(1) : xBorder;
		image = ImageUtil.addBorders(image, xBorder, yBorder, color);
		String borderValue = String.valueOf(borders).replaceAll("(\\[|\\])",  "_");
		ImageUtil.writeImageQuietly(image, new File(imageDir, BORDER + " _ " + borderValue + "." + CTree.PNG));
		return image;
	}
	
	private void outputColourAnalysis(BufferedImage image) {
		
		if (outputFiles.contains(OutputFile.binary)) {
			ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
			colorAnalyzer.writeBinaryImage(getOutputDir());
		}
		
		if (outputFiles.contains(OutputFile.channels)) {
			ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
			colorAnalyzer = new ColorAnalyzer(image);
			colorAnalyzer.writeImagesForColors(getOutputDir(), minPixels);
		}
		
		if (outputFiles.contains(OutputFile.histogram)) {
			ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
			SVGG g = colorAnalyzer.createColorFrequencyPlot();
			SVGSVG.wrapAndWriteAsSVG(g, new File(getOutputDir(), "histogram.svg"));
		}
		
		if (outputFiles.contains(OutputFile.neighbours)) {
			ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
			RGBNeighbourMap neighbourMap = colorAnalyzer.getOrCreateNeighbouringColorMap();
			LOG.warn("neighbours: {}", neighbourMap);
//			SVGG g = colorAnalyzer.getOrCreateNeighbouringColorMap();
//			SVGSVG.wrapAndWriteAsSVG(g, new File(getOutputDir(), "neighbours.svg"));
		}
		
		if (outputFiles.contains(OutputFile.octree)) {
			File file = new File(getOutputDir(), "octree.png");
			ImageIOUtil.writeImageQuietly(image, file);
		}
		
		if (outputFiles.contains(OutputFile.poster)) {
			File file = new File(getOutputDir(), "poster.png");
			ImageIOUtil.writeImageQuietly(image, file);
		}
		
		if (outputFiles.contains(OutputFile.html)) {
			Collection<File> files = FileUtils.listFiles(getOutputDir(), new String[] {"png"}, false);
			HtmlUl ul = new HtmlUl();
			for (File file : files) {
				String basename = FilenameUtils.getBaseName(file.toString());
				String color = basename.indexOf("channel.") == -1 ? "white" : "#" + basename.split("\\.")[1];
				String grandparent = file.getParentFile().getParentFile().getName();
				LOG.debug(grandparent + " // " + basename);
				ul.addFluent(new HtmlLi()
					.setAttribute("file",  file.getAbsolutePath().toString())
					.addFluent(new HtmlP(basename)
						.addFluent(new HtmlSpan("__")
						.setStyle("background-color:"+color)))
					.addFluent((HtmlImg) new HtmlImg()
						.setSrc(file.getName())
						.setAttribute("width", "50%")
						.setStyle("border:1px solid "+"black")));
			}
			XMLUtil.writeQuietly(ul, new File(getOutputDir(), "channels.html"), 1);
			imageDiv.addFluent(ul);
		}
		
		
	}

	// ============== misc ============
	private String truncateToLastDot(String basename) {
		return basename.substring(0, basename.lastIndexOf("."));
	}

	/** HasImageDir methods*/
	@Override
	public void processImageDir(File imageFile) {
//		LOG.info("Single IMAGE FILE "+imageFile);
		processSingleImageFile(imageFile);
	}

	@Override
	public void processImageDir() {
		processSingleImageFile(null);
		LOG.warn("process ImageDir()");
	}

	@Override
	public File getImageFile(File imageDir, String inputname) {
		File imageFile = inputname != null ? new File(imageDir, inputname+"."+CTree.PNG) :
			AbstractAMITool.getRawImageFile(imageDir);
		return imageFile;
	}


	private File getOutputDir() {
		return new File(imageDir, getOutput());
	}

	public Map<ImageParameters, String> getIncludeMap() {
		return includeMap;
	}

	public Map<ImageParameters, String> getExcludeMap() {
		return excludeMap;
	}
	
	public boolean isAnnotate() {
		return annotate;
	}

	/** if either --include or --exclude has "match" key then create a CommonImageHashSet
	 * the images are hashed because I don't think BufferedImages have a good equals()
	 * 
	 * The set can be used to compare an image for ex/inclusion
	 * 
	 * @return set of Hashes using ImageUtils.createSimpleHash(image)
	 * 
	 */ 
	public Set<Long> getOrCreateCommonImageHashSet() {
		if (commonImageHashSet == null) {
			commonImageHashSet = new HashSet<>();
			Map<ImageParameters, String> map = getParametersMap();
			if (map != null) {
				String commonImageDir = map.get(ImageParameters.match);
				if (commonImageDir != null) {
					addImagesInDirectoryToCommonImageHashSet(commonImageDir);
				}
			}
		}
		return commonImageHashSet;
	}

	/** get either includeMap or excluseMap or null
	 * 
	 */
	private Map<ImageParameters, String> getParametersMap() {
		Map<ImageParameters, String> map = null;
		if (includeMap != null || excludeMap != null) {
			map = includeMap != null ? includeMap : excludeMap;
		}
		return map;
	}

	private void addImagesInDirectoryToCommonImageHashSet(String commonImageDir) {
		File file = new File(commonImageDir);
		LOG.warn("ff {}|{}|{}", file, file.exists(), file.isDirectory());
		if (file.isDirectory()) {
			List<File> imageFiles = CMineGlobber.listGlobbedFilesQuietly(file, "**/*.png");
			for (File imageFile : imageFiles) {
				BufferedImage image = ImageUtil.readImageQuietly(imageFile);
				commonImageHashSet.add(ImageUtil.createSimpleHash(image));
			}
		}
	}

	/** does an image fit parameters?
	 * for filtering out/in images 
	 * currently uses --include or --exclude arguments
	 * and keys from ImageParameters enum
	 *
	  	minheight("Minimum height of image (pixels)"),
	  	maxheight("Maximum height of image (pixels)"),
	  	minwidth("Minimum width of image (pixels)"),
	  	maxwidth("Maximum width of image (pixels)"),
	  	minpixf("Minimum fraction of non-background pixels"),
	  	maxpixf("Maximum fraction of non-background pixels"),
	  	minpix("Minimum number of non-background pixels"),
	  	maxpix("Maximum number of non-background pixels"),
	  	strings("Strings in image"),

	 * @param image
	 * @return
	 */
	
	public boolean fitsParameters(InExclusion inexclusion, BufferedImage image) {
		if (inexclusion == null || image == null) return false;
		boolean include = InExclusion.include.equals(inexclusion);
		Map<ImageParameters, String> map = include ? includeMap : excludeMap;
		if (map == null) return false;
		ImageParameterAnalyzer imageParameterAnalyzer = new ImageParameterAnalyzer().setMap(map);
		getOrCreateParameterAnalyzerByImage().put(image, imageParameterAnalyzer);
		boolean matches = imageParameterAnalyzer.matches(getOrCreateAnnotatedImage(image));
		LOG.warn("matches {}", matches);
		return include == matches;
	}

	private Map<BufferedImage, ImageParameterAnalyzer> getOrCreateParameterAnalyzerByImage() {
		if (parameterAnalyzerByImage == null) {
			parameterAnalyzerByImage = new HashMap<>();
		}
		return parameterAnalyzerByImage;
	}

	public AnnotatedImage getOrCreateAnnotatedImage(BufferedImage image) {
		AnnotatedImage annotatedImage = getOrCreateAnnotatedImageByImage().get(image);
		if (annotatedImage == null) {
			annotatedImage = AnnotatedImage.createAnnotatedImage(image);
			annotatedImageByImage.put(image, annotatedImage);
		}
		return annotatedImage;
				
	}

	private Map<BufferedImage, AnnotatedImage> getOrCreateAnnotatedImageByImage() {
		if (annotatedImageByImage == null) {
			annotatedImageByImage = new HashMap<>();
		}
		return annotatedImageByImage;
	}

	
}
