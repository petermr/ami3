package org.contentmine.cproject.files;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DebugPrint {
	private static final Logger LOG = Logger.getLogger(DebugPrint.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static void debugPrintln(Level level, String msg) {
		println(level, msg, Level.DEBUG);
	}

	public static void infoPrintln(Level level, String msg) {
		println(level, msg, Level.INFO);
	}

	public static void warnPrintln(Level level, String msg) {
		println(level, msg, Level.WARN);
	}

	public static void errorPrintln(Level level, String msg) {
		println(level, msg, Level.ERROR);
	}

	public static void debugPrint(Level level, String msg) {
		print(level, msg, Level.DEBUG);
	}

	public static void infoPrint(Level level, String msg) {
		print(level, msg, Level.INFO);
	}

	public static void warnPrint(Level level, String msg) {
		print(level, msg, Level.WARN);
	}

	public static void errorPrint(Level level, String msg) {
		print(level, msg, Level.ERROR);
	}



	private static void println(Level level, String msg, Level refLevel) {
		if (level != null && refLevel != null && refLevel.isGreaterOrEqual(level)) {
			System.out.println(">"+level+": "+msg);
//			addLoggingLevel(level, msg);
		}
	}

	private static void print(Level level, String msg, Level refLevel) {
		if (level != null && refLevel != null && refLevel.isGreaterOrEqual(level)) {
			System.out.print(">"+level+": "+msg);
		}
	}

	public static void debugPrint(String msg) {
		System.out.print(">"+msg);
	}

	public static void debugPrintln(String msg) {
		System.out.println(">"+msg);
	}

}
