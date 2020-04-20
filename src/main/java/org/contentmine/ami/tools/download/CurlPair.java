package org.contentmine.ami.tools.download;
/** supports repeated output fields in curl
 * holds a URL to retrieve and a file to write to
 * 
 * this is a pure DTO with no intelligence
 * 
 * @author pm286
 *
 */

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CurlPair {
	private static final Logger LOG = Logger.getLogger(CurlPair.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private URL url;
	private File outputFile;

	public CurlPair(File outputFile, URL url) {
		this.outputFile = outputFile;
		this.url = url;
//		this.filename = AbstractDownloader.replaceDOIPunctuationByUnderscore(basename) + "." + extension;
		
	}


	/** 
	 * @return string equivalents of file and url
	 */
	public List<String> toList() {
		List<String> list = new ArrayList<>();
		list.add(outputFile.toString());
		list.add(url.toString());
		return list;
	}


	public File getFile() {
		return outputFile;
	}
	
	@Override
	public String toString() {
		String s = url+": "+outputFile;
		return s;
	}

}
