package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.template.AbstractTemplateElement;
import org.contentmine.ami.tools.template.ImageTemplateElement;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.ImageUtil.SharpenMethod;
import org.contentmine.image.ImageUtil.ThresholdMethod;

import com.google.common.collect.Multiset;

import nu.xom.Element;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;

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
				+ ""
				+ "%n identification of duplicate images, and removal<.li>"
				+ "%n rejection of images less than gven size</li>"
				+ "%n rejection of monochrome images (e.g. all white or all black) (NB black and white is 'binary/ized'"
				+ ""

				+ "Then TRANSFORMS contents"
				+ " geometric scaling of images using Imgscalr, with interpolation. Increasing scale on small fonts can help OCR, "
				+ "decreasing scale on large pixel maps can help performance."
				+ ""
				+ "NOTE: a missing option means it is not applied (value null). Generally no defaults"
})
public class AMIImageTool extends AbstractAMITool implements HasImageDir {
	private static final String IMAGE = "image";

	private static final Logger LOG = Logger.getLogger(AMIImageTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum ImageToolkit {
		Boofcv,
		Scalr,
		Pmr,
	}
	
	interface AbstractDest {}
	
	
	public enum DuplicateDest implements AbstractDest {
		_delete,
		duplicate,
		;
	}
	
	public enum MonochromeDest implements AbstractDest {
		_delete,
		monochrome,
		;
	}
	
	public enum SmallDest implements AbstractDest {
		_delete,
		small,
		;
	}
	
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
	
	private static final String _DELETE = "_delete";

    // FILTER OPTIONS
	
    @Option(names = {"--borders"},
    		arity = "1..2",
            description = "add borders: 1 == all; 2 : top/bottom, edges, "
            + "4 vals = top, right bottom, left; default NONE")
	private List<Integer> borders = null ;

    @Option(names = {"--duplicate"},
    		defaultValue = "duplicate",
            description = "FILTER: move duplicate images to <duplicate>; default = ${DEFAULT-VALUE}; "+_DELETE+" means delete"
            )
	private DuplicateDest duplicateDirname;

    @Option(names = {"--filter"},
            description = "pre-runs default FILTER (i.e. without args), duplicate, small, monochrome"
            )
	private boolean filter;

    @Option(names = {"--minheight"},
    		arity = "1",
    		defaultValue = "100",
            description = "minimum height (pixels) to accept")
    private int minHeight;

    @Option(names = {"--minwidth"},
    		arity = "1",
    		defaultValue = "100",
            description = "minimum width (pixels) to accept")
    private int minWidth;
    
    @Option(names = {"--monochrome"},
    		arity = "1",
    		defaultValue = "monochrome",
            description = "FILTER: move monochrome images to <monochrome>; default ${DEFAULT-VALUE}; "+_DELETE+" means delete"
            )
	private MonochromeDest monochromeDirname;

    @Option(names = {"--small"},
    		arity = "1",
    		defaultValue = "small",
            description = "FILTER: move small images to <monochrome>; default ${DEFAULT-VALUE}; "+_DELETE+" means delete"
            )
	private SmallDest smallDirname;
    
    // TRANSFORM OPTIONS
    
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
    
    @Option(names = {"--posterize"},
            description = "create a map of colors including posterization. NYI")
    private boolean posterize = false;

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

	@Spec
	CommandSpec spec; // injected by picocli
	@Override
	protected void parseSpecifics() {
		ParseResult parseResult = spec.commandLine().getParseResult();
		for (OptionSpec option : spec.options()) {
			String label = parseResult.hasMatchedOption(option)
					? "(matched)" : "(default)";
			System.out.printf("%s: %s %s%n", option.longestName(), option.getValue(), label);
		}
    	if (verbosity().length > 0) {
			System.out.println("minHeight           " + minHeight);
			System.out.println("minWidth            " + minWidth);
			System.out.println("smalldir            " + smallDirname);
			System.out.println("monochromeDir       " + monochromeDirname);
			System.out.println("duplicateDir        " + duplicateDirname);
	
	    	
			System.out.println("borders             " + borders);
			System.out.println("binarize            " + binarize);
			System.out.println("despeckle           " + despeckle);
			System.out.println("erodeDilate         " + erodeDilate);
			System.out.println("maxheight           " + maxHeight);
			System.out.println("maxwidth            " + maxWidth);
			System.out.println("posterize           " + posterize);
			System.out.println("priority            " + priorityImage);
			System.out.println("rotate              " + rotateAngle);
			System.out.println("scalefactor         " + scalefactor);
			System.out.println("sharpen             " + sharpen);
			System.out.println("template            " + templateFilename);
			System.out.println("threshold           " + threshold);
    	}
		System.out.println();
	}


    @Override
    protected void runSpecifics() {
    	getSharpenMethod();
    	if (processTrees()) { 
    	} else {
//			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
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
		if (getVerbosityInt() > 0) System.out.println("AMIImageTool processTree");
		ImageDirProcessor imageDirProcessor = new ImageDirProcessor(this, cTree);
		processedTree = imageDirProcessor.processImageDirs();
		return processedTree;
	}

	private void processSingleImageFile(File imageFile) {
		if (imageFile == null) {
			LOG.debug("processSingleImageFile: null file");
		} else {
			File imageDir = imageFile.getParentFile();
			processTransformImageDir(imageDir);
		}
	}


	private void processTransformImageDir(File imageDir) {
		if (!imageDir.exists()) {
			LOG.debug("Dir does not exist: "+imageDir);
		} else {
			if (templateFilename != null) {
				templateElement = AbstractTemplateElement.readTemplateElement(imageDir, templateFilename);
			}
			if (templateElement != null) {
				processTemplate();
			} else {
				try {
					runTransform(imageDir, getInputBasename());
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("Bad read: "+imageDir+" ("+e.getMessage()+")");
				}
			}
		}
	}
	

	protected void processTreeTransform() {
		System.out.println("transformImages cTree: "+cTree.getName());
		File pdfImagesDir = cTree.getExistingPDFImagesDir();
		if (pdfImagesDir == null) {
			System.err.println("Cannot find pdfImages for cTree "+cTree.getName());
			return;
		}
		if (getInputBasename() == null) {
			System.out.println("Assuming base: "+RAW);
			setInputBasename(RAW);
		}
		List<File> imageDirs = CMineGlobber.listSortedChildDirectories(pdfImagesDir);
		Collections.sort(imageDirs);
		for (File imageDir : imageDirs) {
			if (imageDir.getName().startsWith(IMAGE)) {
				System.err.print(".");
				processTransformImageDir(imageDir);
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
	
	private void runTransform(File imageDir, String inputBasename2) {
		File imageFile = new File(imageDir, inputBasename2+"."+CTree.PNG);
		if (!imageFile.exists()) {
			System.out.println("non-existent image file: "+AMIImageTool.shortName(imageFile));
			return;
		}
		System.out.println("transforming: "+AMIImageTool.shortName(imageFile));
		BufferedImage image = ImageUtil.readImageQuietly(imageFile);
		String basename = FilenameUtils.getBaseName(imageFile.toString());
		if (image != null) {
			if (rotateAngle != null) {
				image = rotateAndSave(image, imageDir);
			}
			if (scalefactor != null) {
				image = scaleAndSave(image, imageDir);
				basename += "_sc_"+(int)(double)scalefactor;
			}
			if (sharpen != null && !SharpenMethod.NONE.toString().contentEquals(sharpen)) {
				image = sharpenAndSave(image, imageDir);
				basename += "_s4";
			}
			if (borders != null) {
				image = bordersAndSave(image, imageDir);
				basename += "_b_"+borders.toString().replaceAll("(\\[|\\])", "");
			}
			if (erodeDilate) {
				image = erodeDilateAndSave(image, imageDir);
				basename += "_e";
			}
			if (binarize != null || threshold != null) {
				image = binarizeAndSave(image, imageDir);
				if (binarize != null) {
					basename += binarize.name();
				}
				if (threshold != null) {
					basename += "_thr_"+threshold.toString();
				}
			}
			if (posterize) {
				image = posterizeAndSave(image, imageDir);
			}
			if (despeckle) {
				image = despeckleAndSave(image, imageDir);
				basename += "_ds".toString();
				
			}
			File outfile = new File(imageDir, basename+"."+CTree.PNG);
			ImageIOUtil.writeImageQuietly(image, outfile);
		}
	}

	public static String shortName(File imageFile) {
		return imageFile.getParentFile().getName()+"/"+imageFile.getName();
	}

	private BufferedImage posterizeAndSave(BufferedImage image, File imageDir) {
		if (posterize) {
			LOG.warn("posterize NYI");
		}
		return image;
	}

	private BufferedImage erodeDilateAndSave(BufferedImage image, File imageDir) {
		image = ImageUtil.thresholdBoofcv(image, erodeDilate);
		if (verbosity().length > 1) {
			File outputPng = new File(imageDir, "erodeDilate"+"."+CTree.PNG);
			LOG.debug("writing "+outputPng);
//			ImageUtil.writeImageQuietly(image, outputPng);
		}
		return image;
	}

	private BufferedImage despeckleAndSave(BufferedImage image, File imageDir) {
		image = ImageUtil.despeckle(image);
		if (verbosity().length > 1) {
			File outputPng = new File(imageDir, "despeckle"+"."+CTree.PNG);
			LOG.debug("writing "+outputPng);
			ImageUtil.writeImageQuietly(image, outputPng);
		}
		return image;
	}

	/** binarize withotu explict threshold
	 * 
	 * @param image
	 * @return
	 */
	private BufferedImage binarizeAndSave(BufferedImage image, File imageDir) {
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
			LOG.debug("colors0 "+ImageUtil.createHexMultiset(image));
			image = ImageUtil.convertRGB(image, oldRGB, newRGB);
			
			Integer color = ImageUtil.getSingleColor(image);
			LOG.debug("colors "+ImageUtil.createHexMultiset(image));
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

	private BufferedImage sharpenAndSave(BufferedImage image, File imageDir) {
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

	private BufferedImage rotateAndSave(BufferedImage image, File imageDir) {
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

	private BufferedImage scaleAndSave(BufferedImage image, File imageDir) {
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
	
	private BufferedImage bordersAndSave(BufferedImage image, File imageDir) {
		int color = 0x00FFFFFF;
		int xBorder = borders.get(0);
		int yBorder = borders.size() > 1 ? borders.get(1) : xBorder;
		image = ImageUtil.addBorders(image, xBorder, yBorder, color);
		String borderValue = String.valueOf(borders).replaceAll("(\\[|\\])",  "_");
		ImageUtil.writeImageQuietly(image, new File(imageDir, BORDER + " _ " + borderValue + "." + CTree.PNG));
		return image;
	}
	
	// ============== misc ============
	private String truncateToLastDot(String basename) {
		return basename.substring(0, basename.lastIndexOf("."));
	}

	/** HasImageDir methods*/
	@Override
	public void processImageDir(File imageFile) {
//		System.err.println("Single IMAGE FILE "+imageFile);
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





}
