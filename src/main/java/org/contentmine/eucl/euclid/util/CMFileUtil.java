package org.contentmine.eucl.euclid.util;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import junit.framework.Assert;

/** utilities which don't occur in Apache FileUtils or FilenameUtils
 * 
 * most methods are static but CMFileUtil can also be instantiated (e.g. for recording)
 * 
 * @author pm286
 *
 */
public class CMFileUtil {
	private static final Logger LOG = LogManager.getLogger(CMFileUtil.class);
private BiMap<File, File> newFileByOldFile;
	private List<File> fileList;
	
	public CMFileUtil() {
		
	}

	/**
	 * sorts by embedded integer, in file(names) otherwise identical e.g. sorts
	 * file1foo.svg, , file11foo.svg, file2foo.svg
	 * file1foo.svg, file2foo.svg, file11foo.svg
	 * uses regex of form
	 * [^\d\*]\d+([^\d].*)?
	 * 
	 * the surroundings of the integer defines the pattern.
	 * The first file is used to extract the exact surroundings and
	 * then this is used to test the rest
	 * 
	 * @param files
	 */
	public static List<File> sortUniqueFilesByEmbeddedIntegers(List<File> files) {
		List<File> sortedFiles = new ArrayList<File>();
		Map<String, File> fileByName = new HashMap<String, File>();
		if (files.size() > 0) {
			for (File file : files) {
				String filename = file.getName();
				if (fileByName.keySet().contains(filename)) {
					throw new RuntimeException("Duplicate filename: "+filename);
				}
				fileByName.put(filename, file);
			}
			List<String> filenames = new ArrayList<String>(fileByName.keySet());
			filenames = CMStringUtil.sortUniqueStringsByEmbeddedIntegers(filenames);
			for (String filename : filenames) {
				sortedFiles.add(fileByName.get(filename));
			}
		}
		return sortedFiles;
	}

	/** asserts that file exists and is a directory.
	 * 
	 * @param directory
	 * @throws RuntimeException if false
	 */
	public static void assertExistingDirectory(File directory) {
		if (directory == null || !directory.exists() || !directory.isDirectory()) {
			throw new RuntimeException("file does not exist or is not directory: "+directory);
		}
	}
	/** asserts that file exists and is not a directory.
	 * 
	 * @param file
	 * @throws RuntimeException if false
	 */
	public static void assertExistingNonDirectory(File file) {
		if (file == null || !file.exists() || file.isDirectory()) {
			throw new RuntimeException("file does not exist or is not directory: "+file);
		}
	}

	public static Integer getEmbeddedInteger(File svgFile) {
		
		// TODO Auto-generated method stub
		return null;
	}

	/** "make" logic for file dependencies.
	 * 
	 * returns true if fileToBeCreated is missing or is earlier than any existingEarlierFiles
	 * if (fileToBeCreated is null throw RuntimeException)
	 * 
	 * @param forceMake forces return true
	 * @param fileToBeCreated if null return true as process may create it
	 * @param ignored (was `debug` switch. Ignored now.) // FIXME remove this parameter
	 * @param existingEarlierFiles if null throw exception
	 * 
	 * @return whether file should be "maked"
	 * @throws RuntimeException if existingEarlierFiles are null
	 */
	public static boolean shouldMake(boolean forceMake, File fileToBeCreated, boolean ignored, File... existingEarlierFiles) {
		if (forceMake) {
			return true;
		}
		if (existingEarlierFiles == null) {
			throw new RuntimeException("Null files for make");
		}
		if (fileToBeCreated == null) {
			LOG.debug("null target file); assume it will be created");
			return true;
		}
		LOG.trace("MAKE {} {} from {}", fileToBeCreated, fileToBeCreated.exists(), existingEarlierFiles);
		if (!fileToBeCreated.exists()) {
			LOG.trace("Target {} does not exist", fileToBeCreated);
			return true;
		}
		if (existingEarlierFiles.length == 1 && existingEarlierFiles[0].equals(fileToBeCreated)) {
			LOG.trace("depends on single file {}", fileToBeCreated);
			return false;
		}
			
		for (File existingFile : existingEarlierFiles) {
			if (existingFile != null && !existingFile.exists()) {
				if (FileUtils.isFileNewer(existingFile, fileToBeCreated)) {
					LOG.debug("exist: {}; new {}", existingFile.lastModified(), fileToBeCreated.lastModified());
					LOG.trace("Target {} newer than {}", existingFile, fileToBeCreated);
					return true;
				}
			}
		}
		LOG.trace("Target {} all older than {}", (Arrays.asList(existingEarlierFiles)), fileToBeCreated);
		return false;
	}

	/** "make" logic for file dependencies.
	 * 
	 * returns true if fileToBeCreated is missing or is earlier than any existingEarlierFiles
	 * if (fileToBeCreated is null throw RuntimeException)
	 * 
	 * no debug
	 * 
	 * @param forceMake override make logic (default false => rely on logic)
	 * @param fileToBeCreated
	 * @param existingEarlierFiles
	 * @return whether file should be "maked"
	 * @throws RuntimeException if arguments are null
	 */
	public static boolean shouldMake(boolean forceMake, File fileToBeCreated, File... existingEarlierFiles) {
		boolean debug = false;
		return shouldMake(forceMake, fileToBeCreated, debug, existingEarlierFiles);
	}
	
	
	/** "make" logic for file dependencies.
	 * 
	 * returns true if fileToBeCreated is missing or is earlier than any existingEarlierFiles
	 * if (fileToBeCreated is null throw RuntimeException)
	 * 
	 * no debug
	 * 
	 * @param fileToBeCreated
	 * @param existingEarlierFiles
	 * @return whether file should be "maked"
	 * @throws RuntimeException if arguments are null
	 */
	public static boolean shouldMake(File fileToBeCreated, File... existingEarlierFiles) {
		boolean debug = false;
		boolean forceMake = false;
		return shouldMake(forceMake, fileToBeCreated, debug, existingEarlierFiles);
	}
	
	

	public void add(List<File> fileList) {
		this.fileList = fileList; 
	}
	
	/** converts filenames to lowercase, removes punctuation and optionally truncates.
	 * A-Z => a-z, 0-9.-_ are kept, space => '' everything else => _
	 * if converted filenames are ambiguous, adds numeric values to disambiguate.
	 * 
	 * Example: if compress = 12
	 * abcdefghijklm      => abcdefghijkl
	 * abcdefghijkln      => abcdefghijkl1
	 * abcdefghijklnx     => abcdefghijkl2
	 * abcdefghijklnasasd => abcdefghijkl3
	 * A & Z' 6 7!         ==> a_z_67_
	 * @param files
	 * @param compress
	 * @return
	 * @throws IOException 
	 */
	public Map<File, File> compressFileNames(int maxLength) {
		if (fileList == null) {
			throw new RuntimeException("no files in CMFileUtil");
		}
//		if (maxLength < 1) {
//			return null;
//			throw new RuntimeException("filename lengths must be greater than 0");
//		}
		getOrCreateNewFileByOldFile();
		Multiset<String> basenameSet = HashMultiset.create();
		for (File oldFile : fileList) {
			if (!oldFile.exists()) {
				LOG.error("file does not exist: "+oldFile);
				continue;
			}
			String parent = oldFile.getAbsoluteFile().getParent();
			String oldFilename = oldFile.getAbsolutePath().toString();
			String basename = FilenameUtils.getBaseName(oldFilename);
			String extension = FilenameUtils.getExtension(oldFilename);
			if (maxLength > 0) {
				basename = compressBase(basenameSet, basename, maxLength);
			}
			File newFile = new File(parent, basename+"."+extension);
			String newFilename = newFile.getAbsolutePath().toString();
			try {
				if (maxLength == 0) {
				} else if (newFilename.equalsIgnoreCase(oldFilename)) {
					// differ only in case, so rename and rename back
					newFile = CMFileUtil.convertNameToLowerCase(oldFile);
					if (!newFile.exists()) {
						throw new RuntimeException("BUG: failed to lowercase file: "+oldFile+" to "+newFile);
					}
				} else {
					if (newFile.exists()) {
						FileUtils.forceDelete(newFile);
					}
					FileUtils.moveFile(oldFile, newFile);
				}
			} catch (IOException e) {
 				throw new RuntimeException("cannot rename: "+oldFile, e);
			}
			
			newFileByOldFile.put(oldFile, newFile);
		}
		
		return newFileByOldFile;
	}

	private static String compressBase(Multiset<String> basenameSet, String basename, int maxLength) {
		basename = basename.toLowerCase();
		// strip whitespace
		basename = basename.replaceAll("\\s*", "");
		// removes punctuation and high characters
		basename = basename.replaceAll("[^0-9a-z\\-_\\.]", "_");
		// truncate
		basename = basename.substring(0,  Math.min(maxLength, basename.length()));
		basenameSet.add(basename);
		int count = basenameSet.count(basename);
		// if more than one of same root, add 2...
		return (count == 1 ? basename : basename + count);
	}

	public BiMap<File, File> getOrCreateNewFileByOldFile() {
		if (newFileByOldFile == null) {
			newFileByOldFile = HashBiMap.create();
		}
		return newFileByOldFile;
	}

	public BiMap<File, File> getOrCreateOldFileByNewFile() {
		getOrCreateNewFileByOldFile();
		return newFileByOldFile.inverse();
	}

//	/** converts name to lowerCase.
//	 * The name (after the final slash) is lowercased
//	 * This is difficult on MAC and probably windows as it is
//	 * case-insensitive. We add a junk suffix,
//	 * rename the file, and then re-rename to the lowercase version.
//	 * 
//	 * A.txt => A.txt.<junk> => a.txt
//	 * (junk includes a random number)
//	 * 
//	 * @param fileUpper
//	 * @return
//	 */
//	public static File convertNameToLowerCase0(File fileUpper) throws IOException {
//		String fileUpperAbsolute = fileUpper.getAbsolutePath();
//		String path = FilenameUtils.getPath(fileUpperAbsolute);
//		String name = FilenameUtils.getName(fileUpperAbsolute);
//		//random suffix
//		String suffix = ".junk"+(int)((Integer.MAX_VALUE)*Math.random());
//		String lowerName = name.toLowerCase();
//		File fileUpperJunk = new File(fileUpperAbsolute+suffix);
//		if (fileUpperJunk.exists()) FileUtils.forceDelete(fileUpperJunk);
//		FileUtils.moveFile(fileUpper, fileUpperJunk);
//		File fileLower = new File(path, lowerName);
//		String fileLowerAbsolute = fileLower.getAbsolutePath();
//		if (fileLower.exists() /*&& !fileLowerAbsolute.equalsIgnoreCase(fileUpperAbsolute)*/) {
//			LOG.debug("deleting "+fileLowerAbsolute+" (not "+fileUpperAbsolute+")");
//			FileUtils.forceDelete(fileLower);
//		}
//		LOG.debug("mving "+fileUpperJunk+" to "+fileLower);
//		FileUtils.moveFile(fileUpperJunk, fileLower);
//		if (!fileLower.exists()) {
//			throw new RuntimeException("move failed");
//		}
//		
//		return fileLower;
//	}

	/** converts name to lowerCase.
	 * The name (after the final slash) is lowercased
	 * This is difficult on MAC and probably windows as it is
	 * case-insensitive. We add a junk suffix,
	 * rename the file, and then re-rename to the lowercase version.
	 * 
	 * A.txt => A.txt.<junk> => a.txt
	 * (junk includes a random number)
	 * 
	 * @param fileUpper
	 * @return
	 */
	public static File convertNameToLowerCase(File fileUpper) throws IOException {
		String name = FilenameUtils.getName(fileUpper.getName());
		File parent = fileUpper.getParentFile();
		//random suffix
		String suffix = ".junk"+(int)((Integer.MAX_VALUE)*Math.random());
		String lowerName = name.toLowerCase();
		File fileUpperJunk = new File(parent, name+suffix);
		FileUtils.moveFile(fileUpper, fileUpperJunk);
		File fileLower = new File(parent, lowerName);
		FileUtils.moveFile(fileUpperJunk, fileLower);
		if (!fileLower.exists()) {
			throw new RuntimeException("move failed");
		}
		
		return fileLower;
	}

	public Set<File> getNewKeySet() {
		return getOrCreateOldFileByNewFile().keySet();
	}

	public Set<File> getOldKeySet() {
		return getOrCreateNewFileByOldFile().keySet();
	}

	/**
	 * forces copying of file, if exists and deleting target if required
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void forceCopy(File srcFile, File destFile) throws IOException {
		if (srcFile.exists()) {
			forceDelete(destFile);
			FileUtils.copyFile(srcFile, destFile);
		}
	}

	/**
	 * force delete, avoiding message and exceptions
	 * @param file
	 * @throws IOException
	 */
	public static void forceDelete(File file) throws IOException {
		if (file.exists()) {
			FileUtils.forceDelete(file);
		}
	}

	/**
	 * force delete, avoiding message and exceptions
	 * @param file
	 */
	public static void forceDeleteQuietly(File file) {
		try {
			forceDelete(file);
		} catch (IOException e) {
			System.err.println("cannot delete file: "+file+" ("+e.getMessage()+")");
		}
	}
	
	/**
	 * force delete, avoiding message and exceptions
	 * @param fileList
	 */
	public static void forceDeleteQuietly(List<File> fileList) {
		if (fileList != null) {
			for (File file : fileList) {
				forceDeleteQuietly(file);
			}
		}
	}
	
	/** force move, by testing for existence and copying/deleting
	 * 
	 * @param imageFile
	 * @param newImgFile
	 * @throws IOException
	 */
	public static void forceMove(File srcFile, File destFile) throws IOException {
//		System.out.println("S "+srcFile+ "=> "+destFile);
//		System.out.println("del "+destFile);
		CMFileUtil.forceDelete(destFile);
//		System.out.println("CP "+srcFile+ "=> "+destFile);
		CMFileUtil.forceCopy(srcFile, destFile);
//		System.out.println("del "+srcFile);
		CMFileUtil.forceDelete(srcFile);
	}

	/** force move file to directory
	 * predelete destFile if necessary
	 * 
	 * @param srcFile
	 * @param destDir
	 * @throws IOException
	 */
	public static void forceMoveFileToDirectory(File srcFile, File destDir) throws IOException {
		if (srcFile.exists() && !srcFile.isDirectory()) {
			File destFile = new File(destDir, srcFile.getName());
			forceMove(srcFile, destFile);
		}
	}

	/** physically creates directory 
	 * */
	public static void createDirectory(File dir, boolean delete) {
		if (dir == null) {
			throw new RuntimeException("Null directory");
		}
		if (delete && dir.exists()) {
			try {
				FileUtils.forceDelete(dir);
			} catch (IOException e) {
				throw new RuntimeException("Cannot delete directory: "+dir, e);
			}
		}
		try {
			FileUtils.forceMkdir(dir);
		} catch (IOException e) {
			throw new RuntimeException("Cannot make directory: "+dir+" already exists");
		} // maybe 
	}

	/**
	 * @author Remko Popka
	 * @param temp
	 * @return
	 * @throws IOException
	 */
	public static List<Path> listFully(Path temp) throws IOException {
		List<Path> after = new ArrayList<>();
		Files.walkFileTree(temp, new SimpleFileVisitor<Path>() {
			// Invoke the pattern matching method on each file.
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				after.add(file);
				return CONTINUE;
			}

			// Invoke the pattern matching method on each directory.
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				after.add(dir);
				return CONTINUE;
			}
		});
		return after;
	}

	/** create a string representation of a directory tree
	 * ignores hidden files
	 * @param directory
	 * @return representation
	 */
	public static String createTree(File directory) {
		return createTree(directory, true);
	}
	
	/** create a string representation of a directory tree
	 * 
	 * @param directory
	 * @param ignoreHidden ignore hidden files (e.g. start with ".")
	 * @return representation
	 */
	public static String createTree(File directory, boolean ignoreHidden) {
		StringBuilder sb = new StringBuilder();
		createTree(sb, directory, 0, ignoreHidden);
		return sb.toString();
	}
	private static void createTree(StringBuilder sb, File directory, int level, boolean ignoreHidden) {
		addSpaces(sb, level);
		level++;
		sb.append(directory.getName()+"/\n");
		File[] fileArray = directory.listFiles();
		if (fileArray != null) {
			List<File> files = Arrays.asList(fileArray);
			Collections.sort(files);
			for (File file : files) {
				if (!file.isHidden() || ignoreHidden) {
					if (file.isDirectory()) {
						createTree(sb, file, level, ignoreHidden);
					} else {
						addSpaces(sb, level);
						sb.append(file.getName()+"\n");
					}
				}
			}
		}
	}
	
	private static void addSpaces(StringBuilder sb, int count) {
		for (int i = 0; i < count; i++) {
			sb.append(".");
		}
	}

	/** 
	 * for file /a/b/c/d.e return ["a", "b", "c", "d.e"]
	 * @param file
	 * @return
	 */
	public static List<String> getFileComponents(File file) {
		List<String> components = new ArrayList<>();
		components.add(file.getName());
		File parent = file.getParentFile();
		while (parent != null) {
			components.add(parent.getName());
			parent = parent.getParentFile();
		}
		Collections.reverse(components);
		return components;
			
	}




}
