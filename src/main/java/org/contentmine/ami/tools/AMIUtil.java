package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class AMIUtil {
	private static final Logger LOG = Logger.getLogger(AMIUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String CM_HOME = System.getProperty("user.home");
	
	public static File getFileWithExpandedVariables(String filename) {
		filename = filename.replaceAll("\\$\\{CM_HOME\\}", CM_HOME);
		File file = new File(filename);
		return file;
	}
	
}
