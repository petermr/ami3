package org.contentmine.svg2xml;

import java.io.File;

public class TestUtils {

	/** does a name match a file?
	 * 
	 * @param file
	 * @param filename
	 * @return
	 */
	public static boolean fileEquals(File file, String filename) {
		return file != null && filename != null && file.getPath().equals(new File(filename).getPath());
	}

}
