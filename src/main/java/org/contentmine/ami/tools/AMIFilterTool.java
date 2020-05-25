package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

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
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.ImageUtil.SharpenMethod;
import org.contentmine.image.ImageUtil.ThresholdMethod;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import boofcv.alg.feature.orientation.impl.ImplOrientationAverageGradientIntegral;
import nu.xom.Element;
import nu.xom.Elements;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */



@Command(
name = "filter",
description = {
		"FILTERs images (initally from PDFimages), but does not transform the contents.",
		"Might later be extended to other data types."
		+ "Works at level of raw *.png. Does not transform or split the png."
		+ " Services include %n"
		+ ""
		+ "%n identification of duplicate images, and removal<.li>"
		+ "%n rejection of images less than gven size</li>"
		+ "%n rejection of monochrome images (e.g. all white or all black) (NB black and white is 'binary/ized'"
		+ ""
})
public class AMIFilterTool extends AbstractAMITool /*implements HasImageDir*/ {
	private static final String IMAGE = "image";

	private static final Logger LOG = Logger.getLogger(AMIFilterTool.class);
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

    @Option(names = {"-d", "--duplicate"},
    		arity = "0..1",
    		fallbackValue = "duplicate",
            description = "FILTER: move duplicate images to <duplicate>; fallback = ${FALLBACK-VALUE}; "+_DELETE+" means delete")
	private DuplicateDest duplicateDirname;

    @Option(names = {"--maxheight"},
    		arity = "0..1",
    		defaultValue = "1000",
            description = "maximum height (pixels) to accept. If larger, scales the image (default: ${DEFAULT-VALUE})")
    private Integer maxHeight;

    @Option(names = {"--maxwidth"},
    		arity = "1",
    		defaultValue = "1000",
            description = "maximum width (pixels) to accept. If larger, scales the image (default: ${DEFAULT-VALUE})")
    private Integer maxWidth;
    
    @Option(names = {"--minheight"},
    		arity = "0..1",
    		defaultValue = "100",
            description = "minimum height (pixels) to accept")
    private int minHeight;

    @Option(names = {"--minwidth"},
    		arity = "0..1",
    		defaultValue = "100",
            description = "minimum width (pixels) to accept")
    private int minWidth;
    
    @Option(names = {"-m", "--monochrome"},
    		arity = "0..1",
    		fallbackValue = "monochrome",
            description = "FILTER: move monochrome images to <monochrome>; fallback ${FALLBACK-VALUE}; "+_DELETE+" means delete"
            )
	private MonochromeDest monochromeDirname;

    @Option(names = {"-s", "--small"},
    		arity = "0..1",
    		fallbackValue = "small",
            description = "FILTER: move small images to <monochrome>; fallback ${FALLBACK-VALUE}; "+_DELETE+" means delete"
            )
	private SmallDest smallDirname ;
    

	public static final String DUPLICATES = "duplicates/";
	public static final String MONOCHROME = "monochrome/";
	public static final String LARGE = "large/";
	public static final String SMALL = "small/";
	private static final String ROT = "rot";
	private static final String RAW = "raw";

	private Multiset<String> duplicateSet;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIFilterTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIFilterTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	LOG.info("supersedes AMIImageFilter");
    	new AMIFilterTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
    	super.parseSpecifics();
	}

    public String getSpecifics() {
    	return getOptionsValue();
    }

    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	protected boolean processTree() {
		processedTree = processTreeFilter();
		return processedTree;
	}

	protected boolean processTreeFilter() {
		File pdfImagesDir = cTree.getExistingPDFImagesDir();
		if (pdfImagesDir == null || !pdfImagesDir.exists()) {
			LOG.warn("no pdfimages/ dir");
		} else {
			duplicateSet = HashMultiset.create();
			List<File> imageFiles = CMineGlobber.listSortedChildFiles(pdfImagesDir, CTree.PNG);
			Collections.sort(imageFiles);
			for (File imageFile : imageFiles) {
				System.err.print(".");
				processImageFileFilter(pdfImagesDir, imageFile);
			}
		}
		return processedTree;
	}

	private void processImageFileFilter(File pdfImagesDir, File imageFile) {
		String basename = FilenameUtils.getBaseName(imageFile.toString());
		BufferedImage image = null;
		try {
			image = ImageUtil.readImage(imageFile);
			// this has to cascade in order; they can be reordered if required
			if (false) {
			} else if (moveSmallImageTo(image, imageFile, smallDirname, pdfImagesDir)) {
				System.out.println("small: "+basename);
			} else if (moveMonochromeImagesTo(image, imageFile, monochromeDirname, pdfImagesDir)) {
				System.out.println("monochrome: "+basename);
			} else if (moveDuplicateImagesTo(image, imageFile, duplicateDirname, pdfImagesDir)) {
				System.out.println("duplicate: "+basename);
			} else {
				// move file to <pdfImagesDir>/<basename>/raw.png
				moveImageToRawPng(pdfImagesDir, imageFile, basename);
			}
		} catch(IndexOutOfBoundsException e) {
			LOG.error("BUG: failed to read: "+imageFile);
		} catch(IOException e) {
			e.printStackTrace();
			LOG.debug("failed to read file " + imageFile + "; "+ e);
		}
	}

	private void moveImageToRawPng(File pdfImagesDir, File imageFile, String basename) {
		File imgDir = new File(pdfImagesDir, basename);
		File newImgFile = new File(imgDir, RAW + "." + CTree.PNG);
		try {
			CMFileUtil.forceMove(imageFile, newImgFile);
		} catch (Exception ioe) {
			throw new RuntimeException("cannot rename "+imageFile+" to "+newImgFile, ioe); 
		}
	}

	// ================= filter ============
	private boolean moveSmallImageTo(BufferedImage image, File srcImageFile, AbstractDest destDirname, File destDir) throws IOException {
		if (destDirname != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			if (width < minWidth || height < minHeight) {
				copyOrDelete(srcImageFile, destDirname, destDir);
				return true;
			}
		}
		return false;
	}

	private boolean moveMonochromeImagesTo(BufferedImage image, File srcImageFile, AbstractDest destDirname, File destDir) throws IOException {
		if (destDirname != null) {
			Integer singleColor = ImageUtil.getSingleColor(image);
			if (singleColor != null && srcImageFile.exists()) {
				copyOrDelete(srcImageFile, destDirname, destDir);
				return true;
			}
		}
		return false;
	}

	private boolean moveDuplicateImagesTo(BufferedImage image, File srcImageFile, AbstractDest destDirname, File destDir) throws IOException {
		if (destDirname != null) {
			String hash = ""+image.getWidth()+"-"+image.getHeight()+"-"+ImageUtil.createSimpleHash(image);
			if (duplicateSet != null) {
				duplicateSet.add(hash);
				if (duplicateSet.count(hash) > 1) {
					copyOrDelete(srcImageFile, destDirname, destDir);
					return true;
				}
			}
		}
		return false;
	}

	private void copyOrDelete(File srcImageFile, AbstractDest destDirname, File destDir) throws IOException {
		if (_DELETE.equals(destDirname.toString())) {
			CMFileUtil.forceDelete(srcImageFile);
		} else {
			File fullDestDir = new File(destDir, destDirname.toString());
			fullDestDir.mkdirs();
			CMFileUtil.forceMoveFileToDirectory(srcImageFile, fullDestDir);
		}
	}


	public static String shortName(File imageFile) {
		return imageFile.getParentFile().getName()+"/"+imageFile.getName();
	}

	
	// ============== misc ============
	private String truncateToLastDot(String basename) {
		return basename.substring(0, basename.lastIndexOf("."));
	}


}
