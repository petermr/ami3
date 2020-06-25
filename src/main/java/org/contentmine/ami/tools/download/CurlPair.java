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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CurlPair {
	private static final Logger LOG = LogManager.getLogger(CurlPair.class);
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
