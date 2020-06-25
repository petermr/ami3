package org.contentmine.cproject.files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Unzipper {

	private static final Logger LOG = LogManager.getLogger(Unzipper.class);
private static final int BUFFER_SIZE = 4096;
	private File zipFile;
	private File outDir;
	private ZipInputStream zin;
	private Pattern includePattern;
	private Pattern excludePattern;
	private String zipRootName;
	private List<File> unzippedList;

	private void extractFile(String name) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		File outfile = new File(outDir, name);
		if (unzippedList != null) {
			unzippedList.add(outfile);
		}
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

	/***
	 * Extract zip stream to outdir with complete directory structure.
	 * 
	 * @param inputStream
	 *            Input zip stream
	 * @param outdir
	 *            Output directory
	 * @throws IOException 
	 */
	public void extract(InputStream inputStream, File outdir) throws IOException {
		setOutDir(outdir);
		extractZip(inputStream);
	}

	public void extractZip() throws FileNotFoundException, IOException {
		try (FileInputStream fin = new FileInputStream(zipFile)) {
			extractZip(fin);
		}
	}

	private void extractZip(InputStream in) throws IOException {
		zin = new ZipInputStream(in);
		try (ZipInputStream zipp = zin) { // auto-close
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
		}
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

	/**
	 * Returns the list of unzipped files, if this list was set prior to extracting the zip file or stream.
	 * @return the list of unzipped files, or {@code null}
	 */
	public List<File> getUnzippedList() {
		return unzippedList;
	}

	/**
	 * Sets the list where to collect the unzipped files; must be set prior to extracting the zip file or stream.
	 * @param unzippedList the list to add the unzipped files to; if {@code null} then unzipped files are not collected
	 */
	public void setUnzippedList(List<File> unzippedList) {
		this.unzippedList = unzippedList;
	}
}
