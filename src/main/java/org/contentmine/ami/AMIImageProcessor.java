package org.contentmine.ami;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.norma.image.ocr.HOCRConverter;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** simple example of picocli that runs.
 * 
 * @author pm286
 *
 */

@Command(
name = "ami-image", 
aliases = "image",
version = "ami-image 0.1",
description = "Requires a CProject containing fulltext.pdf. (see makeProject). and initial PDF processing (ami-pdf) "
		+ "The input (reseved name directories) is therefore"
		+ "(a) text/characters (in svg/) %n"
		+ "(b) graphics (in SVG) %n"
		+ "(c) images (in pdfimages/)%n%n"
		+ "then%n"
		+ "(A)image processes the images to binarized (black/white)%n"
		+ "(B)optionally separates the umnwanted images (small and monochrome)"
)

@Deprecated // use AMIImageTool 
public class AMIImageProcessor  extends AbstractAMITool {
	private static final Logger LOG = LogManager.getLogger(AMIImageProcessor.class);
//    @Option(names = {"-mh", "--minheight"},
//    		arity = "0..1",
//    		defaultValue = "100",
//            description = "minimum height (pixels) to accept")
//    private int minHeight;
//
//    @Option(names = {"-mw", "--minwidth"},
//    		arity = "0..1",
//    		defaultValue = "100",
//            description = "minimum width (pixels) to accept")
//    private int minWidth;

    @Option(names = {"-th", "--threshold"},
    		arity = "0..1",
    		defaultValue = "180",
            description = "threshold for binarization")
    private int threshold;

//    @Option(names = {"-mo", "--monochrome"},
//    		arity = "0..1",
//    		defaultValue = "true",
//    		description = "discard monochrome images (i.r. only one color)")
//	private boolean discardMonochrome;
    
//    @Option(names = {"-du", "--duplicates"},
//    		arity = "0..1",
//    		defaultValue = "true",
//            description = "discard duplicate images ")
//	private boolean discardDuplicates;
    
    @Option(names = {"-mi", "--maxislands"},
    		arity = "0..1",
    		defaultValue = "6",
            description = "maximum number of pixel islands output ")
	private int maxPixelIslandCount;
    
    @Option(names = {"-mp", "--maxpixels"},
    		arity = "0..1",
    		defaultValue = "100000",
            description = "maximum number of pixels in any island ")
	private int maxPixelIslandSize;

    @Option(names = {"-ri", "--rings"},
    		arity = "0..1",
    		defaultValue = "false",
            description = "extract pixel rings")
	private boolean extractPixelRings;

//	public static final String DUPLICATES = "duplicates/";
//	public static final String MONOCHROME = "monochrome/";
//	public static final String SMALL = "small/";

//	private Multiset<String> duplicateSet;
	
    private AMIImageProcessor() {
    	
    }

    public static void main(String[] args) throws Exception {
    	new AMIImageProcessor().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
//		System.out.println("minHeight           " + minHeight);
//		System.out.println("minWidth            " + minWidth);
		System.out.println("threshold           " + threshold);
//		System.out.println("discardMonochrome   " + discardMonochrome);
//		System.out.println("discardDuplicates   " + discardDuplicates);
		System.out.println("maxPixelIslandCount " + maxPixelIslandCount);
		System.out.println("maxPixelIslandSize  " + maxPixelIslandSize);
		System.out.println("extractPixelRings   " + extractPixelRings);
	}

    @Override
    protected void runSpecifics() {
    	runImages();
    	
    }
    
	/** runs over cProject
	 * 
	 */
	public void runImages() {
		if (cProject != null) {
			CTreeList cTreeList = cProject.getOrCreateCTreeList();
			for (CTree cTree : cTreeList) {
				LOG.debug("tree: "+cTree.getName());
				runImages(cTree);
			}
		} else if (cTree != null) {
			runImages(cTree);
		} else {
			LOG.warn(" no CProject");
		}
	}


	public void runImages(CTree cTree) {
//		File pdfImagesDir = cTree.getExistingPDFImagesDir();
//		if (pdfImagesDir == null || !pdfImagesDir.exists()) {
//			LOG.warn("no pdfImages/ dir");
//		} else {
//			duplicateSet = HashMultiset.create();
//			List<File> imageFiles = new CMineGlobber("**/*.png", pdfImagesDir).listFiles();
//			Collections.sort(imageFiles);
//			for (File imageFile : imageFiles) {
//				BufferedImage image = null;
//				try {
//					image = ImageUtil.readImage(imageFile);
//					if (false) {
////					} else if (moveSmallImageTo(image, imageFile, new File(pdfImagesDir, SMALL))) {
////						LOG.debug("small");
////					} else if (discardMonochrome && moveMonochromeImagesTo(image, imageFile, new File(pdfImagesDir, MONOCHROME))) {
////						LOG.debug("monochrome");
////					} else if (discardDuplicates && moveDuplicateImagesTo(image, imageFile, new File(pdfImagesDir, DUPLICATES))) {
////						LOG.debug("duplicates");
//					};
//				} catch(IOException e) {
//					e.printStackTrace();
//					LOG.debug("failed to read file " + imageFile + "; "+ e);
//				}
//			}
//		}
	}
	
    public static AMIImageProcessor createAIProcessor(CProject cProject) {
		AMIImageProcessor amiIP = null;
		if (cProject != null) {
			amiIP = new AMIImageProcessor();
			amiIP.setCProject(cProject);
		}
		return amiIP;
	}

	public static AMIImageProcessor createAIProcessor(CTree cTree) {
		AMIImageProcessor amiIP = null;
		if (cTree != null) {
			amiIP = new AMIImageProcessor();
			amiIP.setCTree(cTree);
		}
		return amiIP;
	}

	/** runs over cProject
	 * not used by picocli
	 */
	public void runImages(CProject cProject) {
		this.setCProject(cProject);
		runImages();
	}

//	private boolean moveSmallImageTo(BufferedImage image, File srcImageFile, File destDir) throws IOException {
//		boolean createDestDir = true;
//		int width = image.getWidth();
//		int height = image.getHeight();
//		if (width < minWidth || height < minHeight) {
//			try {
//				FileUtils.moveFileToDirectory(srcImageFile, destDir, createDestDir);
//			} catch (FileExistsException fee) {
//				LOG.warn("file exists, BUG?"+srcImageFile);
//			}
//			return true;
//		}
//		return false;
//	}
//
//	private boolean moveMonochromeImagesTo(BufferedImage image, File srcImageFile, File destDir) throws IOException {
//		boolean createDestDir = true;
//		Integer singleColor = ImageUtil.getSingleColor(image);
//		if (singleColor != null) {
//			FileUtils.moveFileToDirectory(srcImageFile, destDir, createDestDir);
//			return true;
//		}
//		return false;
//	}
//
//	private boolean moveDuplicateImagesTo(BufferedImage image, File srcImageFile, File destDir) throws IOException {
//		boolean createDestDir = true;
//		String hash = ""+image.getWidth()+"-"+image.getHeight()+"-"+ImageUtil.createSimpleHash(image);
//		duplicateSet.add(hash);
//		boolean moved = false;
//		if (duplicateSet.count(hash) > 1) {
//			if (srcImageFile != null && srcImageFile.exists() && !srcImageFile.isDirectory()) {
//				try {
//					File destFile = new File(destDir, srcImageFile.getName());
//					if (destFile != null && destFile.exists()) {
//						FileUtils.forceDelete(destFile);
//					}
//					try {
//						FileUtils.moveFileToDirectory(srcImageFile, destDir, createDestDir);
//					} catch (FileNotFoundException fnfe) {
//						LOG.warn("BUG? (FileNotFound) for "+srcImageFile);
//					}
//					moved = true;
//				} catch (FileExistsException fee) {
//					throw new IOException("BUG: file should have been deleted"+srcImageFile, fee);
//				}
//			}
//		}
//		return moved;
//	}

//	public int getMinWidth() {
//		return minWidth;
//	}
//
//	public AMIImageProcessor setMinWidth(int minWidth) {
//		this.minWidth = minWidth;
//		return this;
//	}
//
//	public int getMinHeight() {
//		return minHeight;
//	}
//
//	public AMIImageProcessor setMinHeight(int minHeight) {
//		this.minHeight = minHeight;
//		return this;
//	}

//	public boolean isDiscardDuplicates() {
//		return discardDuplicates;
//	}
//
//	public AMIImageProcessor setDiscardDuplicates(boolean discardDuplicates) {
//		this.discardDuplicates = discardDuplicates;
//		return this;
//	}
//
//	public boolean isDiscardMonochrome() {
//		return discardMonochrome;
//	}
//
//	public AMIImageProcessor setDiscardMonochrome(boolean discardMonochrome) {
//		this.discardMonochrome = discardMonochrome;
//		return this;
//	}

	public void convertImageAndWriteHOCRFiles(CTree cTree, File outputDir) {
		File imageDir = new File(cTree.getExistingPDFImagesDir(), CTree.DERIVED); 
		List<File> imageFiles = new CMineGlobber().setRegex(".*\\.png").setLocation(imageDir).listFiles();
		for (File imageFile : imageFiles) {
			String base = FilenameUtils.getBaseName(imageFile.toString());
			File outputBase = new File(outputDir, base);
			HOCRConverter imageToHOCRConverter = new HOCRConverter();
			imageToHOCRConverter.writeHOCRFile(imageFile, outputBase);
		}
	}


	public int getMaxPixelIslandCount() {
		return maxPixelIslandCount ;
	}

	public AbstractAMITool setMaxPixelIslandCount(int maxPixelIslandCount) {
		this.maxPixelIslandCount = maxPixelIslandCount;
		return this;
	}

	public int getMaxPixelIslandSize() {
		return maxPixelIslandSize ;
	}

	public AMIImageProcessor setMaxPixelIslandSize(int maxPixelIslandSize) {
		this.maxPixelIslandSize = maxPixelIslandSize;
		return this;
	}

	public void writeImageFilesForTree(File derivedImagesDir, List<File> imageFiles) {
	
		int maxPixelIslandCount = getMaxPixelIslandCount();
		int maxPixelIslandSize = getMaxPixelIslandSize();
		
	
		for (File imageFile : imageFiles) {
			String imageRoot = FilenameUtils.getBaseName(imageFile.toString());
			LOG.debug("root "+imageRoot);
			if (true) throw new RuntimeException("CHANGE derived ImageDir");
			File derivedImageDir = new File(derivedImagesDir, imageRoot+"/");
			
			DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
			diagramAnalyzer.setThinning(null);
			diagramAnalyzer.readAndProcessInputFile(imageFile);
			BufferedImage bufferedImage = diagramAnalyzer.getImageProcessor().getBinarizedImage();
			ImageIOUtil.writeImageQuietly(bufferedImage, new File(derivedImageDir, "binarized.png"));
			PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreateSortedPixelIslandList();
			for (int i = 0; i < Math.min(maxPixelIslandCount, pixelIslandList.size()); i++) {
				PixelIsland pixelIsland = pixelIslandList.get(i);
				if (pixelIsland.size() > maxPixelIslandSize) {
					LOG.debug("Skipped island: "+i+" ("+pixelIsland.size()+")");
					continue;
				}
				File pixelRingFile = new File(derivedImageDir, "pixelIsland"+i+"."+CTree.SVG);
				LOG.debug("wrote "+pixelRingFile);
				SVGSVG.wrapAndWriteAsSVG(pixelIsland.getOrCreateSVGG(), pixelRingFile);
			}
			LOG.debug("end of pixels");
		}
	}

	public void writeImageFilesForProject(CProject cProject) {
		for (CTree cTree : cProject.getOrCreateCTreeList()) {
//			File derivedImagesDir = cTree.getOrCreateDerivedImagesDir();
			if (true) throw new RuntimeException ("Mend this - new imagedir structure");
			List<File> imageDirs = cTree.getPDFImagesImageDirectories();
			for (File imageDir : imageDirs) {
				File imageFile = AbstractAMITool.getRawImageFile(imageDir);
//				Collections.reverse(imageFiles);
				
//				writeImageFilesForTree(imagesDir, imageFiles);
			}
		}
	}

}
