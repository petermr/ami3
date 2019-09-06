package org.contentmine.graphics.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class FilePathGlobber {

	public enum FSType {
		DIR,
		JAR,
		FILE,
		URI
	}

	private static final Logger LOG = Logger.getLogger(FilePathGlobber.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private final static String JAR_STRING = "jar";
	
	private static final String GLOB = "glob:";
	private static final String REGEX = "regex:";
		
	private String location;
	private String pathString;
	private List<File> fileList;
	private Pattern pattern;
	private boolean useDirectories = false;
	private boolean recurse = true;

	private Path root;


	public FilePathGlobber() {
		useDirectories = false;
		recurse = true;
	}
	
	public void setRecurse(boolean b) {
		this.recurse = b;
	}

	public FilePathGlobber(String glob, File directory) {
		this();
		this.setGlob(glob);
		this.setLocation(directory.toString());
	}

	/** matches files against glob or regex.
	 * Note matches whole path, so regexes probably need .* at start
	 * Typical example (that works) 
	 * globber.setRegex(".*\\/fulltext\\-page.*\\.svg");"
	 * 
	 * also capture groups to determine level of descent
	   globber.setRegex("(.*)/svg/.*$"); // should pick up the parent directories of /svg/ 
	 * This is messy - we have to find all the files and then extract the directories using the 
	 * regex and eliminating duplicates. If you don't get the resul you are expecting, please check
	 * the internal logic.
	 * 
	 * @param pathString
	 * @param location
	 * @throws IOException
	 */
	public void match(String pathString, String location) throws IOException {
		ensureFileList();
		final String pathString1 = pathString;
		final Set<String> dirNameSet = new HashSet<String>();
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(pathString);
		Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				LOG.trace("P: "+path+"; "+pathString1);
				if (pathMatcher.matches(path)) {
					boolean addFile = true;
					File file = path.toFile();
					String dirName = null;
					File fileDir = null;
					if (pattern != null) {
						Matcher matcher = pattern.matcher(path.toString());
						LOG.trace("MATCH "+matcher.matches());
						if (matcher.matches()) {
							// assumes the first capture group represent a directory. Messy
							if (useDirectories && matcher.groupCount() >= 1) {
								fileDir = new File(matcher.group(1));
								LOG.trace("FF "+fileDir);
								dirName = fileDir.getName();
							}
						} else {
							addFile = false;
						}
					}
					if (addFile) {
						if (useDirectories && fileDir != null && !dirNameSet.contains(dirName)) {
							// add new directories
							LOG.trace("DIR "+fileDir);
							fileList.add(fileDir);
							dirNameSet.add(dirName);
						} else {
							// add all files anyway
							LOG.trace("FILE "+file);
							fileList.add(file);
						}
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void ensureFileList() {
		if (fileList == null) {
			fileList = new ArrayList<File>();
		}
	}

	public FilePathGlobber setLocation(File location) {
		if (location == null) {
			LOG.warn("null location");
		} else {
			setLocation(location.toString());
		}
		return this;
	}

	public FilePathGlobber setLocation(String location) {
		if (location == null) {
			LOG.warn("null location");
		} else {
			if (location.startsWith("/")) {
				LOG.warn("matches against whole file system; might delete system files: BE careful: "+location);			
			}
			this.location = location;
		}
		return this;
	}

	public FilePathGlobber setGlob(String pathString) {
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

	public FilePathGlobber setRegex(String pathString) {
		if (pathString == null) {
			LOG.warn("null pathString");
		} else if (pathString.startsWith(GLOB)) {
			setGlob(pathString);
		} else if (!pathString.startsWith(REGEX)) {
			this.pathString = REGEX + pathString;
			pattern = Pattern.compile(pathString);
		} else {
			this.pathString = pathString;
		}
		return this;
	}

	public List<File> listFiles() throws IOException {
		fileList = new ArrayList<File>();
		if (location != null && pathString != null) {
			LOG.trace("match "+pathString+"; "+location);
			match(pathString, location);
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
		try {
			FilePathGlobber globber = new FilePathGlobber(glob, directory);
			files = globber.listFiles();
		} catch (IOException e) {
			throw new RuntimeException("Cannot glob files, ", e);
		}
		return files;
	}
	
	@Override
	public String toString() {
		String s = "location: "+location+"; glob: "+pathString;
		return s;
	}

	public FilePathGlobber setUseDirectories(boolean b) {
		this.useDirectories  = b;
		return this;
	}

	/** BasicFileAttributes
FileTime 	creationTime() Returns the creation time.
Object 	fileKey() Returns an object that uniquely identifies the given file, or null if a file key is not available.
boolean 	isDirectory() Tells whether the file is a directory.
boolean 	isOther() Tells whether the file is something other than a regular file, directory, or symbolic link.
boolean 	isRegularFile() Tells whether the file is a regular file with opaque content.
boolean 	isSymbolicLink() Tells whether the file is a symbolic link.
FileTime 	lastAccessTime() Returns the time of last access.
FileTime 	lastModifiedTime() Returns the time of last modification.
long 	size() Returns the size of the file (in bytes).	 */
	/**
	 * 
	 * @param root
	 * @param type
	 * @return
	 */
	// https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file
	public List<Path> createDirOrFileList(Path root, FSType type) {
		this.root = root;
		final List<Path> filePaths = new ArrayList<Path>();
		final List<Path> dirPaths = new ArrayList<Path>();
		final Path rootx = root;
	
	    FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<Path>() {
		    @Override
		    public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs) throws IOException {
		        boolean addDir = dir.getParent().equals(rootx) || recurse;
				if (addDir) {
		        	dirPaths.add(dir);
		        	LOG.trace("added"+dir);
		        }
				FileVisitResult fvr = (addDir || dir.equals(rootx)) ? 
		        		FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
		        return fvr;
		    }
		       
		    @Override
		    public FileVisitResult visitFile(Path visitedFile,BasicFileAttributes fileAttributes)
		        throws IOException {
		        boolean addFile = visitedFile.getParent().equals(rootx) || recurse;
				if (addFile) {
		        	filePaths.add(visitedFile);
		        }
				FileVisitResult fvr = (addFile || recurse) ? 
		        		FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
		        return fvr;
		    }
		};
	    try {
	        Files.walkFileTree(root, simpleFileVisitor);
	    } catch (IOException ioe) {
	        ioe.printStackTrace();
	    }
//	    LOG.debug("DIR "+dirPaths);
//	    LOG.debug("FILE "+filePaths);
		return FSType.DIR.equals(type) ? dirPaths : filePaths;
	}

	public Path getFolderPath(String resource, String pathRoot) throws URISyntaxException, IOException {
		URI uri = this.getClass().getResource(resource).toURI();
		String scheme = uri.getScheme();
		if (JAR_STRING.equalsIgnoreCase(scheme)) {
			// the hashmap is for special filesystems. Ignore for now
			FileSystem fileSystem = FileSystems.newFileSystem(uri, new HashMap<String, String>(), (ClassLoader) null);
			return fileSystem.getPath(pathRoot);
		} else {
		    return Paths.get(uri);
		}
	}


}
