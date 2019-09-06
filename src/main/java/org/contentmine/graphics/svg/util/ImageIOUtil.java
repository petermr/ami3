package org.contentmine.graphics.svg.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

public class ImageIOUtil {


	/** makes parent directly if not exists.
	 * 
	 * selects type from extension; chooses ".png" if none 
	 * @param image
	 * @param file
	 */
	public static void writeImageQuietly(BufferedImage image, File file) {
		if (image == null) {
			throw new RuntimeException("Cannot write null image: "+file);
		}
		try {
			// DON'T EDIT!
			String type = FilenameUtils.getExtension(file.getName());
			if (type == null || type.equals("")) {
				type ="png";
			}
			file.getParentFile().mkdirs();
			ImageIO.write(image, type, new FileOutputStream(file));
		} catch (Exception e) {
			throw new RuntimeException("cannot write image "+file, e);
		}
	}


	/** makes parent directly if not exists.
	 * 
	 * calls writeImageQuietly(Image, File)
	 * 
	 * selects type from extension; chooses ".png" if none 
	 * @param image
	 * @param filename
	 */
	public static File writeImageQuietly(BufferedImage image, String filename) {
		File file = new File(filename);
		writeImageQuietly(image, new File(filename));
		return file;
	}



}
