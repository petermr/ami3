package org.contentmine.pdf2svg.util;

import java.io.File;

public class Util {

	public final static Integer MAX_LEN = 60;
	/** renames any long file to something shorter
	 * highly heuristic - used when file names are too long for windows
	 * 
	 * @param dir
	 */
	public final static void renameLongFileNames(File dir) {
		if (dir == null || !dir.isDirectory()) return;
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				try {
				String name = file.getName();
				int suff = name.lastIndexOf(".");
				String suffix = name.substring(suff);
				String name1 = name.substring(0, suff);
				name1 = name1.replaceAll(" ", "");
				if (name1.length() > MAX_LEN) {
					name1 = name1.substring(0, MAX_LEN);
				}
				file.renameTo(new File(dir, name1+suffix));
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 1) {
			Util.renameLongFileNames(new File(args[0]));
		}
	}
}
