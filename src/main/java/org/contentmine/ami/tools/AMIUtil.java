package org.contentmine.ami.tools;

import java.io.File;
import java.util.Collection;

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
	
	private static int maxValueLength = 200;
	public final static void printNameValue(String name, Object value) {
		StringBuilder sb = new StringBuilder();
		if (value ==  null) {
			sb.append("null");
		} else if (value instanceof Collection) {
			for (Object object : (Collection) value) {
				sb.append(object.toString() + " ");
				if (sb.length() > maxValueLength) break;
			}
		} else {
			sb = new StringBuilder(value.toString());
		}
		System.out.println((name + "                           ").substring(0,25) + sb.toString());
	}


}
