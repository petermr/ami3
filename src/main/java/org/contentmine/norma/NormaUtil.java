package org.contentmine.norma;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.util.CMineUtil;

public class NormaUtil {

	public static final Logger LOG = LogManager.getLogger(NormaUtil.class);
private static final String HTML_START = "<";
	private static final String PDF_MAGIC = "%PDF";

	public static String getStringFromInputFile(File file) {
		String s = null;
		try {
			s = FileUtils.readFileToString(file, CMineUtil.UTF8_CHARSET);
		} catch (Exception e) {
			// consume exception
		}
		return s;
	}

	public static boolean isHtmlContent(String s) {
		return s.startsWith(HTML_START);
	}

	public static boolean isPDFContent(String s) {
		return s.startsWith(PDF_MAGIC);
	}
	
	public static boolean isPDF(String name) {
		return name.toLowerCase().endsWith(InputFormat.PDF.toString().toLowerCase());
	}


}
