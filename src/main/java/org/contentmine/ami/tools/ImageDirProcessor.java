package org.contentmine.ami.tools;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineGlobber;


public class ImageDirProcessor {

	public static final Logger LOG = Logger.getLogger(ImageDirProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

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

	public void processImageDirs() {
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
			return;
		}
			
		if (imageDirs != null) {
			System.out.println(" >>>>> imageDirs: "+imageDirs.size());
			Collections.sort(imageDirs);
			for (File imageDir : imageDirs) {
				try {
					processImageDir(imageDir);
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error("Cannot process imageDir: "+imageDir + e.getMessage());
				}
			}
		} else {
			processRawImageDir(rawImageDir);
		}
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
		HasImageDir hasImageDir = (HasImageDir)amiTool;
		File imageFile = hasImageDir.getImageFile(imageDir, inputname);
		amiTool.setInputBasename(inputname);
		if (imageFile == null || !imageFile.exists()) {
			LOG.error("BUG? image file does not exist: "+imageFile);
//			hasImageDir.processImageDir();
		} else {
			hasImageDir.processImageDir(imageFile);
//			hasImageDir.processImageFile(imageFile);
		}
	}


}
