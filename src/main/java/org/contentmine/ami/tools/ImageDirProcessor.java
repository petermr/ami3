package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.image.AnnotatedImage;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.image.ImageUtil;

import jline.internal.Log;


public class ImageDirProcessor {

	public static final Logger LOG = LogManager.getLogger(ImageDirProcessor.class);
	private CTree cTree;
	public AbstractAMITool amiTool;
	private List<File> imageDirs;

	public ImageDirProcessor() {
	}

	public ImageDirProcessor(AbstractAMITool amiTool) {
		this();
		this.amiTool = amiTool;
		
	}

	public ImageDirProcessor(AbstractAMITool amiTool, CTree cTree) {
		this(amiTool);
		this.cTree = cTree;
	}

	public boolean processImageDirs() {
		imageDirs = null;
		File rawImageDir = null;
		File pdfImagesDir = cTree.getExistingPDFImagesDir();
		if (pdfImagesDir != null && pdfImagesDir.exists()) {
			imageDirs = cTree.getPDFImagesImageDirectories();
		}
		if (imageDirs == null) {
			rawImageDir = cTree.getExistingImageDir();
		}
		if (imageDirs == null && rawImageDir == null) {
			LOG.warn("no pdfimages/ dir and no image/ dir");
			return  false;
		}
			
		if (imageDirs != null) {
			LOG.warn(" >>>>> imageDirs: "+imageDirs.size());
			Collections.sort(imageDirs);
			for (File imageDir : imageDirs) {
				processDir(imageDir);
			}
		} else {
			processRawImageDir(rawImageDir);
		}
		return true;
	}

	private void processDir(File imageDir) {
		File imageFile = new File(imageDir, getInputFilename());
		String fileString = imageFile.getParentFile().getParentFile().getParentFile().getName()+
				"//"+imageFile.getParentFile().getName();
		if (excludeImage(imageFile)) {
			System.err.println("skipped: "+fileString);
		} else {
			System.err.println("*****OK..."+fileString);
			try {
				processImageDir(imageDir);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error("Cannot process imageDir: "+imageDir + e.getMessage());
			}
		}
	}

	private String getInputFilename() {
		return "raw" + "." + "png";
	}

	private boolean excludeImage(File imageFile) {
		BufferedImage image = null;
		try {
			image = ImageUtil.readImageQuietly(imageFile);
		} catch (Exception e) {
			System.err.println("Cannot read file: "+e.getMessage());
			return false;
		}
		// image in commonImageSet
		if (!(amiTool instanceof AMIImageTool)) {
			LOG.warn("amiOCR does not exclude images");
			return false;
		}
		AMIImageTool amiImageTool = (AMIImageTool)amiTool;
		AnnotatedImage annotatedImage = amiImageTool.getOrCreateAnnotatedImage(image);
		
		// exclude?
		if (amiImageTool.getExcludeMap() != null) {
			if (amiImageTool.getOrCreateCommonImageHashSet()
				.contains(ImageUtil.createSimpleHash(image))) {
				System.out.println("Exclude common file: "+imageFile.getParentFile().getName());
				return true;
			}
			if (amiImageTool.fitsParameters(AMIImageTool.InExclusion.exclude, image)) {
				System.out.println("Fit exclusion params: "+imageFile.getParentFile().getName());
				return true;
			}
		}

		// include?
		if (amiImageTool.getIncludeMap() != null) {
			if (amiImageTool.fitsParameters(AMIImageTool.InExclusion.include, image)) {
				System.out.println("Fitted include params: "+imageFile.getParentFile().getName());
				return true;
			}
		}
		return false;
	}

	private void processRawImageDir(File rawImageDir) {
		if (rawImageDir == null || !rawImageDir.exists()) {
			throw new RuntimeException("cannot find imageDir: "+rawImageDir);
		}
		List<File> imageDirs = CMineGlobber.listSortedChildDirectories(rawImageDir);
		for (File imageDir : imageDirs) {
			processImageDir(imageDir);
		}
	}

	private void processImageDir(File imageDir) {
		List<String> inputnameList = amiTool.getInputBasenameList();
//		LOG.debug("processing ImageDir: "+imageDir+"//"+inputnameList);
		if (inputnameList != null && inputnameList.size() > 0) {
			for (String inputname : inputnameList) {
				processInputName(imageDir, inputname);
			}
		} else {
			processInputName(imageDir, amiTool.getInputBasename());
		}
	}

	/** this calls back to amiTool */
	private void processInputName(File imageDir, String inputname) {
		if (inputname == null) {
			Log.warn("no inputname to processImage");
			return;
		}
		HasImageDir hasImageDir = (HasImageDir)amiTool;
		File imageFile = hasImageDir.getImageFile(imageDir, inputname);
		amiTool.setInputBasename(inputname);
		if (imageFile == null || !imageFile.exists()) {
			LOG.error("BUG? image file does not exist: "+imageFile);
		} else {
			hasImageDir.processImageFile(imageFile);
		}
	}


}
