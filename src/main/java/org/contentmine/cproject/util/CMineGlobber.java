package org.contentmine.cproject.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** uses java NIO PathMatcher to glob files
 * 
 * [Note: when * would gives a false comment we use [star]
 *
 * 
 * Returns a PathMatcher that performs match operations on the String representation of Path objects by interpreting a 
 * given pattern. The syntaxAndPattern parameter identifies the syntax and the pattern and takes the form:

     syntax:pattern
     

where ':' stands for itself.

A FileSystem implementation supports the "glob" and "regex" syntaxes, and may support others. The value of the 
syntax component is compared **without regard to case.**

When the syntax is "glob" then the String representation of the path is matched using a limited pattern language 
that resembles regular expressions but with a simpler syntax. For example:

    *.java 	Matches a path that represents a file name ending in .java
    *.* 	Matches file names containing a dot
    *.{java,class} 	Matches file names ending with .java or .class
    foo.? 	Matches file names starting with foo. and a single character extension
    /home/[star]/[star] 	Matches /home/gus/data on UNIX platforms 
    /home/[star][star] 	Matches /home/gus and /home/gus/data on UNIX platforms
    C:\\* 	Matches C:\foo and C:\bar on the Windows platform (note that the backslash is escaped; as a string literal 
    in the Java Language the pattern would be "C:\\\\*")

The following rules are used to interpret glob patterns:

    The * character matches zero or more characters of a name component without crossing directory boundaries.

    The ** characters matches zero or more characters crossing directory boundaries.

    The ? character matches exactly one character of a name component.

    The backslash character (\) is used to escape characters that would otherwise be interpreted as special characters. 
    The expression \\ matches a single backslash and "\{" matches a left brace for example.

    The [ ] characters are a bracket expression that match a single character of a name component out of a set of characters. 
    For example, [abc] matches "a", "b", or "c". The hyphen (-) may be used to specify a range so [a-z] specifies a range that 
    matches from "a" to "z" (inclusive). These forms can be mixed so [abce-g] matches "a", "b", "c", "e", "f" or "g". 
    If the character after the [ is a ! then it is used for negation so [!a-c] matches any character except "a", "b", or "c".

    Within a bracket expression the *, ? and \ characters match themselves. The (-) character matches itself if it is the 
    first character within the brackets, or the first character after the ! if negating.

    The { } characters are a group of subpatterns, where the group matches if any subpattern in the group matches. 
    The "," character is used to separate the subpatterns. Groups cannot be nested.

    Leading period/dot characters in file name are treated as regular characters in match operations. 
    For example, the "*" glob pattern matches file name ".login". The Files.isHidden(java.nio.file.Path) method may be 
    used to test whether a file is considered hidden.

    All other characters match themselves in an implementation dependent manner. This includes characters representing any name-separators.

    The matching of root components is highly implementation-dependent and is not specified.

When the syntax is "regex" then the pattern component is a regular expression as defined by the Pattern class.

For both the glob and regex syntaxes, the matching details, such as whether the matching is case sensitive, are 
implementation-dependent and therefore not specified.
 * @author pm286
 *
 */
public class CMineGlobber {


	private static final Logger LOG = Logger.getLogger(CMineGlobber.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String GLOB = "glob:";
	private static final String REGEX = "regex:";
		
	private String location;
	private String pathString;
	private List<File> fileList;
	private boolean debug = false;
	private boolean useDirectories = false;
	private boolean useFiles = true;
	private boolean recurse = true;


	public CMineGlobber() {
	}

	public CMineGlobber(String glob, File directory) {
		this.setGlob(glob);
		this.setLocation(directory.toString());
	}

	/** matches files against glob or regex.
	 * Note matches whole path, so regexes probably need .* at start
	 * Typical example (that works) 
	 * globber.setRegex(".*\\/fulltext\\-page.*\\.svg");"
	 * 
	 * @param pathString
	 * @param location
	 * @throws IOException
	 */
	public void match(String pathString, String location) throws IOException {
		ensureFileList();
		final String finalPathString = pathString;
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(finalPathString);
		Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				if (debug) {
					LOG.debug("m "+finalPathString+"; p "+path);
				}
				if (pathMatcher.matches(path) && useFiles) {
					fileList.add(path.toFile());
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
		    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
		            throws IOException {
				if (debug) {
					LOG.debug("dir "+finalPathString+"; p "+path);
				}
				if (pathMatcher.matches(path) && useDirectories) {
					fileList.add(path.toFile());
				}
				return recurse ? FileVisitResult.CONTINUE : FileVisitResult.TERMINATE;
		    }

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				if (debug) {
					LOG.debug("m "+finalPathString+"; d "+file);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void ensureFileList() {
		if (fileList == null) {
			fileList = new ArrayList<File>();
		}
	}

	public CMineGlobber setLocation(File location) {
		if (location == null) {
			LOG.warn("null location");
		} else {
			setLocation(location.toString());
		}
		return this;
	}

	public CMineGlobber setLocation(String location) {
		if (location == null) {
			LOG.warn("null location");
		} else {
			if (location.startsWith("/")) {
				LOG.trace("matches against whole file system; might delete system files: BE careful: "+location);			
			}
			this.location = location;
		}
		return this;
	}

	public CMineGlobber setGlob(String pathString) {
		if (pathString == null) {
			LOG.warn("null pathString");
		} else if (pathString.startsWith(REGEX)) {
			setRegex(pathString);
		} else if (!pathString.startsWith(GLOB)) {
			this.pathString = GLOB + pathString;
		} else {
			this.pathString = pathString;
		}
		return this;
	}

	public CMineGlobber setRegex(String pathString) {
		if (pathString == null) {
			LOG.warn("null pathString");
		} else if (pathString.startsWith(GLOB)) {
			setGlob(pathString);
		} else if (!pathString.startsWith(REGEX)) {
			this.pathString = REGEX + pathString;
		} else {
			this.pathString = pathString;
		}
		return this;
	}

	public List<File> listFiles() {
		fileList = new ArrayList<File>();
		if (location != null && pathString != null) {
			try {
				match(pathString, location);
			} catch (IOException e) {
				throw new RuntimeException("Cannot glob: "+pathString+"; "+location);
			}
		}
		return fileList;
	}

	/** globs and deletes files.
	 * use carefully!! (like you would use rm -f)
	 * 
	 * @return files that have been deleted
	 * @throws IOException
	 */
	public List<File> deleteFiles() throws IOException {
		List<File> files = listFiles();
		LOG.trace("DELETE"+files);
		for (File file : files) {
			LOG.trace("deleting: "+file);
			FileUtils.forceDelete(file); 
		}
		LOG.trace("DELETED");
		return files;
	}

	/** creates Globber from directory and glob. Fails quietly.
	 * 
	 * @param directory
	 * @param glob (e.g. * * /word/ * * [spaces only to escape comments]
	 * @return
	 */
	public static List<File> listGlobbedFilesQuietly(File directory, String glob) {
		List<File> files = new ArrayList<File>();
		CMineGlobber globber = new CMineGlobber(glob, directory);
		files = globber.listFiles();
		return files;
	}

	public String getLocation() {
		return location;
	}

	public String getPathString() {
		return pathString;
	}

	public CMineGlobber setDebug(boolean b) {
		this.debug = b;
		return this;
	}

	public CMineGlobber setUseDirectories(boolean b) {
		this.useDirectories = b;
		return this;
	}

	public CMineGlobber setUseFiles(boolean b) {
		this.useFiles = b;
		return this;
	}

	public boolean isRecurse() {
		return recurse;
	}

	/** recurse through directories.
	 * if false , lists only one level
	 * 
	 * @param recurse default is true
	 */
	public CMineGlobber setRecurse(boolean recurse) {
		this.recurse = recurse;
		return this;
	}

	/** get sorted list of child files (not directories).
	 * 
	 * @param dir
	 * @return sorted list 
	 */
	public static List<File> listSortedChildFiles(File dir) {
		List<File> sortedFileList = new ArrayList<File>();
		File[] fileList = dir.listFiles();
		if (fileList != null) {
			for (File file : fileList) {
				if (!file.isDirectory()) {
					sortedFileList.add(file);
				}
			}
		}
		Collections.sort(sortedFileList);
		return sortedFileList;
	}

	/** get sorted list of child directories.
	 * 
	 * @param dir
	 * @return sorted list 
	 */
	public static List<File> listSortedChildDirectories(File dir) {
		if (dir == null) {
			throw new RuntimeException("Null dir: ");
		}
		List<File> sortedDirList = new ArrayList<File>();
		File[] fileList = dir.listFiles();
		if (fileList != null) {
			for (File file : fileList) {
				if (file.isDirectory()) {
					sortedDirList.add(file);
				}
			}
		}
		Collections.sort(sortedDirList);
		return sortedDirList;
	}

	/** get sorted list of child files with given suffix.
	 * 
	 * @param dir
	 * @param suffix (without '.')
	 * @return sorted list 
	 */
	public static List<File> listSortedChildFiles(File dir, String suffix) {
		List<File> fileList = new ArrayList<File>();
		if (dir != null) {
			try {
				fileList = new ArrayList<File>(FileUtils.listFiles(dir, new String[] {suffix}, false));
			} catch (Exception e) {
				throw new RuntimeException("cannot list files: "+dir, e);
			}
		}
		Collections.sort(fileList);
		return fileList;
	}

	/** get sorted list of descendant files with given suffix.
	 * 
	 * @param dir
	 * @param suffix (without '.')
	 * @return sorted list 
	 */
	public static List<File> listSortedDescendantFiles(File dir, String suffix) {
		List<File> listFiles = new CMineGlobber().setGlob("**/*." + suffix).setRecurse(true).setLocation(dir).listFiles();
		Collections.sort(listFiles);
		return listFiles;
	}

}

