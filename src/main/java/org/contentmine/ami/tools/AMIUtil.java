package org.contentmine.ami.tools;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AMIUtil {
	private static final Logger LOG = LogManager.getLogger(AMIUtil.class);
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
	
	public static void printHeader(Object obj, PrintStream stream, String type) {
		stream.println();
		stream.println(type + " values (" + obj.getClass().getSimpleName() + ")");
		stream.println("================================");
	}



}
