package org.contentmine.ami.tools;

import java.io.File;

/**
 * manages subdirectory of images under CTree (e.g. pdfimages)
 * 
 * These are routines called from ImageDirProcessor
 * 
 * @author pm286
 *
 */
public interface HasImageDir {

	void processImageDir(File imageFile);

	void processImageDir();

	File getImageFile(File imageDir, String inputname);
	

}
