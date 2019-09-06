package org.contentmine.cproject.files;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.contentmine.eucl.euclid.util.HashFunction;
import org.contentmine.image.ImageUtil;

/** DTO for images
 * 
 * @author pm286
 *
 */
public class ImageProperties {
	
	private File file;
	private BufferedImage image;
	private int minHeight;
	private int minWidth;
	private long minFileSize;
	private String md5;

	public ImageProperties(File file, BufferedImage image) {
		this.image = image;
		this.file = file;
		setDefaults();
		getOrCreateMD5Hash();
	}

	private void setDefaults() {
		// not sure these are useful - maybe need to be larger
		minHeight   = 10;
		minWidth    = 10;
		minFileSize = 100;
	}

	public long getFileSize() {
		return FileUtils.sizeOf(file);
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public File getFile() {
		return file;
	}

	public BufferedImage getImage() {
		return image;
	}

	public boolean isSmallImage() {
		boolean isSmallImage = getHeight() < minHeight || getWidth() < minWidth || getFileSize() < minFileSize;
		return isSmallImage;
	}

	public String getOrCreateMD5Hash() {
		if (md5 == null) {
			String imageString = ImageUtil.createString(image);
			md5 = HashFunction.getMD5Hash(imageString);
		}
		return md5;
	}

}
