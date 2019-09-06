package org.contentmine.norma.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.norma.NAConstants;
import org.junit.Test;

/** allows applications to extract filesystems out of jar files.
 * 
 * from Holger with thanks
 * (https://stackoverflow.com/questions/15713119/java-nio-file-path-for-a-classpath-resource/36021165#36021165)
 * 
 * @author pm286
 *
 */
public class NIOResourceManager {
	private static final Logger LOG = Logger.getLogger(NIOResourceManager.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public NIOResourceManager() {
		
	}
	
	interface IOConsumer<T> {
	    void accept(T t) throws IOException;
	}
	
	/**
	public static List<Path> listChildPaths(String resourceName) {
    	final List<Path> pathList = new ArrayList<Path>();
    	try {
			processResource(Object.class.getResource(resourceName).toURI(), new IOConsumer<Path>() {
			    public void accept(Path path) throws IOException {
					try(DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			            for(Path p: ds) {
			                pathList.add(p);
			            }
			        }
			    }
			});
    	} catch (Exception e) {
    		throw new RuntimeException("BUG ", e);
    	}
		Collections.sort(pathList);
		return pathList;
	}
	*/

	/**
	private static void processResource(URI uri, IOConsumer<Path> action) throws IOException {
	    try {
	        Path p=Paths.get(uri);
	        action.accept(p);
	    }
	    catch(FileSystemNotFoundException ex) {
	        try(FileSystem fs = FileSystems.newFileSystem(
	                uri, Collections.<String,Object>emptyMap())) {
	            Path p = fs.provider().getPath(uri);
	            action.accept(p);
	        }
	    }
	}
	*/


}
