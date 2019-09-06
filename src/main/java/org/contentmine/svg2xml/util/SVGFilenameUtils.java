package org.contentmine.svg2xml.util;

import java.io.File;

public class SVGFilenameUtils {

	/** compacts a filename tracking tables or figures hierarchy
	 * Not sure what this does - seems to skip a level of filename
	 * 
	 * @param root
	 * @param file
	 * @return
	 */
	public static File getCompactSVGFilename(File root, File file) {
		File parent = file.getParentFile();
		File ggParent = parent.getParentFile().getParentFile();
		return new File(new File(root, ggParent.getName()), parent.getName()+".svg");
		
	}

}
