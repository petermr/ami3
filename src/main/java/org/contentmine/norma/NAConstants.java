package org.contentmine.norma;


import java.io.File;

import org.contentmine.CHESConstants;

public class NAConstants {

	public static final String UTF_8 = "UTF-8";

	public final static String NORMA   = "norma";
	public final static String AMI     = "ami";
	
	public static final String ARGS_XML = "args.xml";
	public static final String DOT_XML = ".xml";
	public static final String HTML_TAGGER_XML = "htmlTagger.xml";

	// === local ===
	public final static File   HOME_DIR = new File(System.getProperty("user.home"));
	public final static File   CONTENTMINE_DIR = new File(HOME_DIR, "ContentMine/");
	public final static File   WORKSPACE_DIR = new File(HOME_DIR, "workspace/");
	public final static File   DICTIONARY_DIR = new File(CONTENTMINE_DIR, "dictionary/");
	
	public final static File   LIB_DIR = new File("/usr/local/");
	public final static File   CONTENTMINE_LIB = new File(LIB_DIR, "contentmine");

	// === code ===
	public final static File   CMDEV_DIR = new File(WORKSPACE_DIR, "cmdev/");
	public final static File   NORMAMI_DIR = new File(CMDEV_DIR, "normami/");

	public static final File   NORMAMI_MAIN_RESOURCE_DIR = new File(NAConstants.NORMAMI_DIR, CHESConstants.SRC_MAIN_RESOURCES);
	
	// === norma ===
	public final static String ORG_CM_NORMA = CHESConstants.ORG_CM + "/" + NORMA;

	public static final String POM_XML = "pom.xml";
	public static final File   ORIGINAL_POM_XML = new File(NAConstants.NORMAMI_DIR, NAConstants.POM_XML);
	public static final File   SRC_MAIN_RESOURCES_POM_XML = new File(NORMAMI_MAIN_RESOURCE_DIR, NAConstants.POM_XML);

	public static final String POM_XML_RESOURCE = "/" + POM_XML;
	public static final String NORMA_RESOURCE = "/" + ORG_CM_NORMA;

	public final static String IMAGES   = "images";
	public static final String PUBSTYLE = "pubstyle";
	
	public final static String IMAGES_RESOURCE   = NORMA_RESOURCE + "/" + IMAGES;
	public final static String OCR_RESOURCE = IMAGES_RESOURCE + "/" + "ocr/";
	public static final String PUBSTYLE_RESOURCE = NORMA_RESOURCE + "/"+ PUBSTYLE;

	public final static File MAIN_NORMA_DIR = new File(CHESConstants.SRC_MAIN_RESOURCES + "/" + CHESConstants.ORG_CM + "/" + NORMA + "/");
	public final static File TEST_NORMA_DIR = new File(CHESConstants.SRC_TEST_RESOURCES + "/" + CHESConstants.ORG_CM + "/" + NORMA + "/");

	// === ami ===
	
	public final static String ORG_CM_AMI   = CHESConstants.ORG_CM + "/" + AMI;
	public static final String AMI_RESOURCE = ORG_CM_AMI;
	public static final String SLASH_AMI_RESOURCE = "/" + AMI_RESOURCE;

	public final static File MAIN_AMI_DIR = new File(CHESConstants.SRC_MAIN_RESOURCES + "/" + CHESConstants.ORG_CM + "/" + AMI + "/");
	public final static File TEST_AMI_DIR = new File(CHESConstants.SRC_TEST_RESOURCES + "/" + CHESConstants.ORG_CM + "/" + AMI + "/");
	
	public final static String AMI_WORDUTIL   = NAConstants.SLASH_AMI_RESOURCE+"/wordutil";
	
	public final static String PLUGINS_RESOURCE   = NAConstants.SLASH_AMI_RESOURCE+"/plugins";
	public final static String DICTIONARY_RESOURCE= NAConstants.PLUGINS_RESOURCE+"/dictionary";
	public final static String PLUGINS_GENE       = NAConstants.PLUGINS_RESOURCE+"/gene";
	public final static String PLUGINS_PLACES     = NAConstants.PLUGINS_RESOURCE+"/places";
	public final static String PLUGINS_SPECIES    = NAConstants.PLUGINS_RESOURCE+"/species";
	public final static String PLUGINS_STATISTICS = NAConstants.PLUGINS_RESOURCE+"/statistics";
	public final static String PLUGINS_SYNBIO     = NAConstants.PLUGINS_RESOURCE+"/synbio";
	public final static String PLUGINS_WORD       = NAConstants.PLUGINS_RESOURCE+"/word";
	
	public final static File PLUGINS_DIR   =      new File(NAConstants.MAIN_AMI_DIR, "plugins");
//	public final static File DICTIONARY_DIR = new File(NAConstants.CONTENTMINE_DIR, "dictionary");
	// will be phased out
	public final static File PLUGINS_DICTIONARY_DIR = new File(NAConstants.PLUGINS_DIR, "dictionary");
//	public final static File PLUGINS_GENE       = NAConstants.PLUGINS_DIR+"/gene";
//	public final static File PLUGINS_PLACES     = NAConstants.PLUGINS_DIR+"/places";
//	public final static File PLUGINS_SPECIES    = NAConstants.PLUGINS_DIR+"/species";
//	public final static File PLUGINS_STATISTICS = NAConstants.PLUGINS_DIR+"/statistics";
//	public final static File PLUGINS_SYNBIO     = NAConstants.PLUGINS_DIR+"/synbio";
//	public final static File PLUGINS_WORD       = NAConstants.PLUGINS_DIR+"/word";
	
	public static final String GENE_HGNC          = NAConstants.PLUGINS_GENE+"/hgnc";

	public static final File LOCAL_DIR = new File("local/");
	public static final File LOCAL_DOCUMENTS = new File(NAConstants.LOCAL_DIR, "doc/");
	public static final File LOCAL_DICTIONARIES = new File(NAConstants.LOCAL_DIR, "dict/");
	public static final File LOCAL_COMMANDS = new File(NAConstants.LOCAL_DIR, "cmd/");

} 
