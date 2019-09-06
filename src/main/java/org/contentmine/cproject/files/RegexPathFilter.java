package org.contentmine.cproject.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RegexPathFilter implements IOFileFilter {
	private static final Logger LOG = Logger.getLogger(RegexPathFilter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Pattern pattern;

	public RegexPathFilter(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public RegexPathFilter(String filePatternString) {
		pattern = Pattern.compile(filePatternString);
	}
	
	/** this one is  called by FileUtils.listFiles
	 * 
	 */
	public boolean accept(File file) {
		String path = file.getAbsolutePath();
		Matcher matcher = pattern.matcher(path);
		boolean matches = matcher.matches();
		// something wrong here - even when false it seems to list files
//		LOG.debug("matching "+pattern+" to "+path+" // "+matches);
//		if (matches) {
//			return matches;
//		}
		return matches;
	}

	/** this isn't called.*/
	public boolean accept(File dir, String name) {
		LOG.debug("DIR"+dir+"/"+name);
		return false;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	@Override
	public String toString() {
		return pattern == null ? null : pattern.toString();
	}

	/** Horrible brute force hack
	 * (I cannot get FileUtils, etc to process this consistently)
	 */
	public List<File> listFilesAndDirsRecursively(File directory, boolean includeFiles, boolean includeDirs) {
//		Iterator<File> iterator = FileUtils.list
		Iterator<File> iterator = FileUtils.listFilesAndDirs(directory, TrueFileFilter.TRUE, TrueFileFilter.TRUE).iterator();
		List<File> files = new ArrayList<File>();
		while (iterator.hasNext()) {
			File file = iterator.next();
			LOG.trace("fileXX "+file);
			Matcher matcher = pattern.matcher(file.toString());
			if (matcher.matches()) {
				if (includeFiles && !file.isDirectory() ||
				    includeDirs && file.isDirectory()) {
					files.add(file);
				}
			}
		}
		return files;
	}
	
	public List<File> listDirectoriesRecursively(File directory) {
		return listFilesAndDirsRecursively(directory, false, true);
	}
	
	public List<File> listNonDirectoriesRecursively(File directory) {
		return listFilesAndDirsRecursively(directory, true, false);
	}
}
