package org.contentmine;

import java.util.Arrays;
import java.util.List;

public class CHESConstants {







	/** note no preceeding slash */
	public static final String ORG_CM = "org/contentmine";
	
	public static final String SRC_MAIN_RESOURCES = "src/main/resources";
	public static final String SRC_MAIN_TOP       = SRC_MAIN_RESOURCES + "/" + ORG_CM;
	
	public static final String SRC_TEST_RESOURCES = "src/test/resources";
	public final static String SRC_TEST_TOP = SRC_TEST_RESOURCES + "/" + ORG_CM;

	// subprojects
	private static final String CPROJECT = "cproject";
	private static final String EUCL     = "eucl";
	private static final String FONT     = "font";
	private static final String IMAGE    = "image";
	private static final String GRAPHICS = "graphics";
	private static final String PDF2SVG  = "pdf2svg";
	private static final String SVG2XML  = "svg2xml";

	public static final String ORG_CM_CPROJECT = ORG_CM + "/" + CPROJECT;
	public static final String ORG_CM_EUCL     = ORG_CM + "/" + EUCL;
	public static final String ORG_CM_FONT     = ORG_CM + "/" + FONT;
	public static final String ORG_CM_GRAPHICS = ORG_CM + "/" + GRAPHICS;
	public static final String ORG_CM_IMAGE    = ORG_CM + "/" + IMAGE;
	public static final String ORG_CM_PDF2SVG  = ORG_CM + "/" + PDF2SVG;
	public static final String ORG_CM_SVG2XML  = ORG_CM + "/" + SVG2XML;
	
	
	public static final String SRC_GRAPHICS_RESOURCES = SRC_MAIN_TOP+"/"+GRAPHICS;
	
	public final static String SRC_TEST_CPROJECT_TOP = SRC_TEST_TOP + "/" + CPROJECT;

	public final static String[] DEFAULT_COLORS = {
			"red",
			"green",
			"blue",
//			"yellow",
			"cyan",
			"magenta",
			"brown",
			"orange",
//			"pink",
			"purple",
	};
	public final static List<String> DEFAULT_COLOR_LIST = Arrays.asList(DEFAULT_COLORS);


}
