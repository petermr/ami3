package org.contentmine.cproject.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.commons.io.FileUtils;
import org.contentmine.eucl.euclid.util.CMFileUtil;

import jline.internal.Log;

/** merges files into directories.
 * 
 * @author pm286
 *
 */
public class DirectoryMerger {

	private File fromParentFile;
	private File toParentFile;
	private int maxFileIndex;
	private List<String> fromParentDirs;

	public DirectoryMerger() {
		
	}
	
	public DirectoryMerger setFromParent(File parentFile) {
		this.fromParentFile = parentFile;
		this.fromParentDirs = CMFileUtil.getFileComponents(fromParentFile);
		return this;
	}
	
	public DirectoryMerger setToParent(File parentFile) {
		this.toParentFile = parentFile;
		return this;
	}

	public DirectoryMerger merge(List<File> mergeFiles) {
			for (File mergeFile : mergeFiles) {
				List<String> mergeDirs = CMFileUtil.getFileComponents(mergeFile);
				mergeDirs = alignSameTree(fromParentDirs, mergeDirs);
				System.out.println(mergeDirs);
				mergeDescendantFiles(mergeDirs, toParentFile, mergeFile);
//				}
			}
//		}
		return this;
	}

	private List<String> alignSameTree(List<String> fromParentDirs, List<String> mergeDirs) {
		if (mergeDirs.size() < fromParentDirs.size()) {
			throw new RuntimeException("incompatible trees "+mergeDirs+" != " +fromParentDirs);
		}
		int fromSize = fromParentDirs.size();
		for (int i = 0; i < fromSize; i++) {
			if (!mergeDirs.get(i).contentEquals(fromParentDirs.get(i))) {
				throw new RuntimeException("trees fail match at ["+i+"] "+mergeDirs+" || "+fromParentDirs);
			}
		}
		return mergeDirs.subList(fromSize, mergeDirs.size());
	}

	private void mergeDescendantFiles(List<String> mergeDirs, File toDir, File mergeFile) {
		File currentDir = toDir;
		for (int i = 0; i < mergeDirs.size() - 1; i++) {
			String mergeDir = mergeDirs.get(i);
			File nextDir = new File(currentDir, mergeDir);
//			if (!nextDir.exists()) {
//				nextDir.mkdirs();
//			}
			currentDir = nextDir;
		}
		File lastFile = new File(currentDir, mergeDirs.get(mergeDirs.size() - 1));
		try {
			FileUtils.copyFile(mergeFile, lastFile);
		} catch (IOException e) {
			throw new RuntimeException("can't copy "+mergeFile, e);
		}
	}
	private boolean existsRoot(File toDir, String addName) {
		File[] toFiles = toDir.listFiles();
		boolean exists = false;
		if (toFiles != null) {
			maxFileIndex = 0;
			for (File toFile : toFiles) {
				String toFileName = toFile.getName();
				String toFileBase = getBaseRoot(toFileName);
				if (toFileBase.equals(addName)) {
					int fileIndex = getFileIndex(addName);
					if (fileIndex > maxFileIndex) maxFileIndex = fileIndex;
					exists = true;
				}
			}
		}
		return exists;
	}
	private Integer getFileIndex(String addName) {
		String[] fields = addName.split("_");
		return fields.length == 1 ? null : new Integer(fields[0]);
	}
	
	private void addIncrementedFile(File fromFile, File toDir) {
		
	}
	private String getBaseRoot(String name) {
		String baseName = name.replaceAll("^.*_", "");
		return baseName;
	}
}
