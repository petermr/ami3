package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.processing.HilditchThinning;
import org.contentmine.image.processing.ZhangSuenThinning;

public class AbstractAMIImageTest extends AbstractAMITest {


	private static final Logger LOG = LogManager.getLogger(AbstractAMIImageTest.class);

	protected File pdfImageDir;
	protected File imageDir;
	protected File layerDir;
	protected File imageFile;
	protected BufferedImage image;

	private String imageSuffix = CTree.PNG;
	
	// ================= simple getters =========
	public File getPdfImageDir() {
		return pdfImageDir;
	}
	public File getImageDir() {
		return imageDir;
	}
	protected File getImageFile() {
		checkImageFile();
		return imageFile;
	}
	public BufferedImage getImage() {
		return image;
	}
	public File getSVGFile() {
		return svgFile;
	}
	
	protected File getOutputFile() {
		return outputFile;
	}
	
	// ============ checkers =============
	protected void checkPdfimagesDir() {
		checkIsReadableDirectory(pdfImageDir, "directory under " + cTree.getDirectory());
	}
	
	private static void checkIsReadableDirectory(File dir, String message) {
		if (dir == null) {
			throw new RuntimeException("null directory " + message);
		}
		if (!Files.isReadable(dir.toPath())) {
			throw new RuntimeException(message + ": missing/unreadable " + dir);
		}
		if (!dir.isDirectory()) {
			throw new RuntimeException(message + ": not a directory " + dir);
		}
	}
	
	protected void checkImageDir() {
		if (imageDir == null) {
			throw new RuntimeException("missing image directory under " + pdfImageDir);
		}
	}
	
	protected void checkImageFile() {
		if (imageFile == null || !imageFile.exists() || imageFile.isDirectory()) {
			throw new RuntimeException("missing image file under layer " + layerDir);
		}
	}
	
	protected void checkImage() {
		if (image == null) {
			throw new RuntimeException(" no image");
		}
	}
	
    // ========= user-facing modules ===============
	// OVERWRITE BY subclass to change return
	
	protected AbstractAMIImageTest setImageDirNames() {
		checkCTree();
		pdfImageDir = cTree.getExistingPDFImagesDir();
		checkPdfimagesDir();
		if (true) throw new RuntimeException("NYI");
//		checkImageDir(imageDirName);
		return this;
	}
	
	protected AbstractAMIImageTest setImageDirName(String imageDirName) {
		checkCTree();
		pdfImageDir = cTree.getExistingPDFImagesDir();
		checkPdfimagesDir();
		checkImageDir(imageDirName);
		return this;
	}
	

	private void checkImageDir(String imageDirName) {
		imageDir = new File(pdfImageDir, imageDirName);
		checkImageDir();
	}
	
	protected AbstractAMIImageTest setImageName(String root) {
		checkImageDir();
		imageFile = new File(imageDir, root + "." + imageSuffix);
		LOG.debug(imageFile);
		return this;
	}

	protected AbstractAMIImageTest readImages() {
		for (File file : globbedFiles) {
			imageFile = file;
			readImage();
		}
		return this;
	}
	
	protected AbstractAMIImageTest readImage() {
		checkImageFile();
		image = ImageUtil.readImage(imageFile);
		checkImage();
		return this;
	}
	
	protected AbstractAMITest writeImage(String type) {
		checkImage();
		outputFile = new File(imageFile.toString()+"."+type+"." + CTree.PNG);
		ImageUtil.writeImageQuietly(image, outputFile);
		return this;
	}
	
	protected AbstractAMIImageTest binarize(int thresh) {
		checkImage();
		image = ImageUtil.boofCVBinarization(image, thresh);
		return this;
	}
	
	protected AbstractAMITest hilditchThin() {
		checkImage();
		image = ImageUtil.thin(image, new HilditchThinning(image));
		return this;
	}
	
	protected AbstractAMIImageTest zhangSuenThin() {
		checkImage();
		image = ImageUtil.thin(image, new ZhangSuenThinning(image));
		return this;
	}
	
	protected AbstractAMIImageTest setImageFile(File file) {
		this.imageFile = file;
		return this;
	}
	
	// not sure whether these are needed
	
//	@Override
//	protected AbstractAMIImageTest runFileGlob(String glob) {
//		return (AbstractAMIImageTest) super.runFileGlob(glob);
//	}


//	@Override
//	protected AbstractAMITest setAMITestProjectName(String projectName) {
//		return (AbstractAMITest) super.setAMITestProjectName(projectName);
//	}

	@Override
	protected AbstractAMIImageTest setTreeName(String treeName) {
		return (AbstractAMIImageTest) super.setTreeName(treeName);
	}

}
