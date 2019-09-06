package org.contentmine.cproject.files;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.image.ImageUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** manages images extracted from a document.
 * ImageManager initially deals with a single CTree. Later we may create an ImageManagerList for
 * the CProject.
 * @author pm286
 *
 */
public class ImageManager {
	private static final Logger LOG = Logger.getLogger(ImageManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private CTree cTree;
	private Map<BufferedImage, File> filesByImage;
	private Map<BufferedImage, ImageProperties> propertiesByImage;
	private Map<File, BufferedImage> imagesByFile;
	private Multiset<String> md5HashSet;
	// these lists are NOT guaranteed to be in same order.
	private List<File> imageFileList;
	private List<BufferedImage> imageList;
	private File imageDir;
	private boolean omitSmallImages;
	
	public ImageManager(CTree cTree) {
		this.cTree = cTree;
	}

	public List<File> getOrCreateImageFileList() {
		getOrCreateImageMaps();
		return imageFileList;
	}

	public List<BufferedImage> getOrCreateImageList() {
		getOrCreateImageMaps();
		return imageList;
	}

	public Map<BufferedImage, File> getOrCreateFilesByImage() {
		getOrCreateImageMaps();
		return filesByImage;
	}

	public Map<File, BufferedImage> getOrCreateImagesByFile() {
		getOrCreateImageMaps();
		return imagesByFile;
	}

	private void getOrCreateImageMaps() {
		if (imageFileList == null || imagesByFile == null || imageList == null || filesByImage == null) {
			imageDir = cTree.getExistingImageDir();
			imagesByFile = new HashMap<File, BufferedImage>();
			filesByImage = new HashMap<BufferedImage, File>();
			propertiesByImage = new HashMap<BufferedImage, ImageProperties>();
			imageList = new ArrayList<BufferedImage>();
			imageFileList = new ArrayList<File>();
			md5HashSet = HashMultiset.create();
			if (imageDir != null) {
				imageFileList = new ArrayList<File>(
						FileUtils.listFiles(imageDir, new String[]{CTree.PNG}, false));
				for (File imageFile : imageFileList) {
					BufferedImage image = ImageUtil.readImage(imageFile);
					imageList.add(image);
					imageFileList.add(imageFile);
					imagesByFile.put(imageFile, image);
					filesByImage.put(image, imageFile);
					ImageProperties properties = new ImageProperties(imageFile, image);
					propertiesByImage.put(image, properties);
					
				}
			}
		}
	}

	public File getImageDir() {
		return imageDir;
	}

	public void tidyImages() {
		this.getOrCreateImageMaps();
		
		for (BufferedImage image : propertiesByImage.keySet()) {
			ImageProperties imageProperties = propertiesByImage.get(image);
			int height = imageProperties.getHeight();
			if (height == 1) {
				LOG.debug("1-pixel height");
			}
			if (omitSmallImages && imageProperties.isSmallImage()) {
				File file = imageProperties.getFile();
				LOG.debug("deleting small file: " + file);
				FileUtils.deleteQuietly(file);
			}
		}
	}

	public boolean isOmitSmallImages() {
		return omitSmallImages;
	}

	public void setOmitSmallImages(boolean omitSmallImages) {
		this.omitSmallImages = omitSmallImages;
	}
	

}
