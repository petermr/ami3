package org.contentmine.cproject.files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Unzipper {

	private static final Logger LOG = Logger.getLogger(Unzipper.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	

	private static final int BUFFER_SIZE = 4096;
	private File zipFile;
	private File outDir;
	private ZipInputStream zin;
	private Pattern includePattern;
	private Pattern excludePattern;
	private String zipRootName;
//	private List<File> zipRootList;

	private void extractFile(String name) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		File outfile = new File(outDir, name);
		if (!outfile.isDirectory()) {
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outfile));
			int count = -1;
			while ((count = zin.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
			out.close();
		}
	}

	private File mkdirs(File outdir, String path) {
		File d = new File(outdir, path);
		if (!d.exists()) {
			d.mkdirs();
		}
		return d;
	}

	private String dirpart(String name) {
		int s = name.lastIndexOf(File.separatorChar);
		return s == -1 ? null : name.substring(0, s);
	}

	/***
	 * Extract zipfile to outdir with complete directory structure
	 * 
	 * @param zipfile
	 *            Input .zip file
	 * @param outdir
	 *            Output directory
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void extract(File zipfile, File outdir) throws IOException {
		setZipFile(zipfile);
		setOutDir(outdir);
		extractZip();
	}

	public void extractZip() throws FileNotFoundException, IOException {
		zin = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry;
		File file = null;
		while ((entry = zin.getNextEntry()) != null) {
//			LOG.debug("entry "+entry);
			String name = entry.getName();
			String zipRoot1 = dirpart(name);
			if (zipRoot1 != null) {
				if (zipRootName == null) {
					zipRootName = zipRoot1;
				} else if (zipRootName.equals(zipRoot1)) {
					
				} else {
					throw new RuntimeException("duplicate zipRoot");
				}
			}
			if (matches(name)) {
				file = extractFile(entry, name);
			}
			if (entry.isDirectory()) {
//				LOG.debug("dir "+entry);
			}
		}
		zin.close();
	}
	
	private boolean matches(String name) {
		boolean matches = true;
		if (includePattern != null) {
			matches = includePattern.matcher(name).matches();
		} else if (excludePattern != null) {
			matches = !excludePattern.matcher(name).matches();
		}
		return matches;
	}

	private File extractFile(ZipEntry entry, String name) throws IOException {
//		LOG.debug("name "+name);
		File file = null;
		if (entry.isDirectory()) {
			file = mkdirs(outDir, name);
		}
		/*
		 * this part is necessary because file entry can come before
		 * directory entry where is file located i.e.: /foo/foo.txt
		 * /foo/
		 */
		String dir = dirpart(name);
		if (dir != null) {
			file = mkdirs(outDir, dir);
		}

		extractFile(name);
		return file;
	}

	public void setOutDir(File outDir) {
		this.outDir = outDir;
		if (outDir == null) {
			throw new RuntimeException("Null outDir");
		} else if (!outDir.exists()) {
			outDir.mkdirs();
		} else if (!outDir.isDirectory()) {
			throw new RuntimeException("outDir must be a directory");
		}
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}
	
	public Pattern getIncludePattern() {
		return includePattern;
	}

	public void setIncludePatternString(String includePatternString) {
		this.includePattern = includePatternString == null ? null : Pattern.compile(includePatternString);
	}

	public Pattern getExcludePattern() {
		return excludePattern;
	}

	public void setExcludePatternString(String excludePatternString) {
		this.excludePattern = excludePatternString == null ? null : Pattern.compile(excludePatternString);
	}

	public String getZipRootName() {
		if (zipRootName != null && !zipRootName.endsWith("/")) {
			zipRootName += "/";
		}
		return zipRootName;
	}

}
