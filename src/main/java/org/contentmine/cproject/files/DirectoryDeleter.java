package org.contentmine.cproject.files;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utility that can be used with the
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html">FileVisitor</a> NIO API.
 * <p>Example:</p>
 * <pre>
 *     Path dir = Paths.get("delete/this/dir");
 *     Files.walkFileTree(start, new DirectoryDeleter());
 * </pre>
 */
public class DirectoryDeleter extends SimpleFileVisitor<Path> {

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Files.delete(file);
		return FileVisitResult.CONTINUE;
	}
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
		if (e == null) {
			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		} else {
			// directory iteration failed
			throw e;
		}
	}
}
