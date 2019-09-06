package org.contentmine.cproject.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.schema.FileSet;

/** traverses the actual files in an existing CProject 
 * do not confuse with CProjectSchema which traverses the filenames in the schema.
 * 
 * @author pm286
 *
 */
public class CContainerTraverser {
	private static final Logger LOG = Logger.getLogger(CContainerTraverser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<File> directoryList;
	private List<File> fileList;
	protected CContainer cContainer;

	public CContainerTraverser(CContainer cContainer) {
		this.cContainer = cContainer;
	}
	
	private void ensureSortedFileAndDirectoryChildren() {
		File directory = cContainer.getDirectory();
		ensureSortedFileAndDirectoryChildren(directory);
	}

	public void ensureSortedFileAndDirectoryChildren(File directory) {
		if (fileList == null || directoryList == null) {
			directoryList = new ArrayList<File>();
			fileList = new ArrayList<File>();
			File[] ff = directory.listFiles();
			if (ff != null) {
				List<File> files = Arrays.asList(ff);
				Collections.sort(files);
				for (File file : files) {
					if (file.isDirectory()) {
						directoryList.add(file);
					} else {
						fileList.add(file);
					}
				}
			}
			Collections.sort(fileList);
			Collections.sort(directoryList);
		}
	}

	public List<File> getOrCreateSortedDirectoryList() {
		ensureSortedFileAndDirectoryChildren();
		return directoryList;
	}
	

	public List<File> getOrCreateSortedFileList() {
		ensureSortedFileAndDirectoryChildren();
		return fileList;
	}
	
}
