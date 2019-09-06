package org.contentmine.svg2xml;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.svg2xml.paths.Chunk;

import nu.xom.Builder;

public class SVG2XMLFixtures {

	private static final Logger LOG = Logger.getLogger(SVG2XMLFixtures.class);
	
	public static final File TEST_RESOURCES_DIR = new File("src/test/resources");
	public static final File SVG2XML_DIR = new File(TEST_RESOURCES_DIR, "org/contentmine/svg2xml/");
	public static final File PDFS_DIR = new File(SVG2XML_DIR, "pdfs");
	public static final File ACTION_DIR = new File(SVG2XML_DIR, "action");
	public static final File BAR_DIR = new File(SVG2XML_DIR, "bar");
	public static final File COMMAND_DIR= new File(SVG2XML_DIR, "command");
	public static final File CORE_DIR = new File(SVG2XML_DIR, "core");
	public static final File FUNNEL_DIR = new File(SVG2XML_DIR, "funnel");
	public static final File SVG_DIR = new File(SVG2XML_DIR, "svg");
	public static final File BIO_DIR = new File(SVG_DIR, "bio");
	public static final File BMC_DIR = new File(SVG_DIR, "bmc");
	public static final File MDPI_DIR = new File(SVG_DIR, "mdpi");
	public static final File SVG1_DIR = new File(SVG2XML_DIR, "svg");

	public static final File ACTION_SVG_DIR = new File(ACTION_DIR, "svg");
	public static final File ACTION_PDF_DIR = new File(ACTION_DIR, "pdf");
	// not standard
	public static final File CSIRO_DIR = new File("../pdfs/csiro/test");
	public static final File CSIRO_DIR0 = new File("../pdfs/csiro/test0");
	public static final File CSIRO_DIR1 = new File("../pdfs/csiro/test1");
	
	public static final File AJC_PAGE6_PDF = new File(CORE_DIR, "ajc-page6.pdf");
	
	public final static File NOOP_FILE = new File(CORE_DIR, "noopTst.xml");
	public final static File BASIC_FILE = new File(CORE_DIR, "basicTst.xml");
	public static final File INCLUDE_TEST_FILE = new File(CORE_DIR, "includeTst.xml");
	public static final File INFILE_TEST = new File(CORE_DIR, "infileTst.xml");
	public static final File ASSERT_TST = new File(COMMAND_DIR, "assertTst.xml");
	public static final File NO_ASSERT_TST = new File(COMMAND_DIR, "noAssertTst.xml");
	public static final File VARIABLE_TST = new File(COMMAND_DIR, "variableTst.xml");
	public static final File WHITESPACE_CHUNKER_COMMAND = new File(SVG2XMLFixtures.COMMAND_DIR, "whitespaceChunkerTst.xml");
	public static final File WHITESPACE_0_TST = new File(SVG2XMLFixtures.COMMAND_DIR, "pageTst0.xml");
	public static final File PAGE0_SVG = new File(SVG2XMLFixtures.COMMAND_DIR, "test-page0.svg");
	public static final File HARTER3_SVG = new File(SVG2XMLFixtures.COMMAND_DIR, "harter3.svg");
	public static final File HARTER3SMALL_SVG = new File(SVG2XMLFixtures.COMMAND_DIR, "harter3small.svg");
	public static final File AJC6_SVG = new File(SVG2XMLFixtures.COMMAND_DIR, "ajc6.svg");
	public static final File POLICIES_SVG = new File(SVG2XMLFixtures.COMMAND_DIR, "policies.svg");
	public static final File CHUNK_ANALYZE = new File(SVG2XMLFixtures.ACTION_DIR, "chunkAnalyzeTst.xml");
	public static final File CHUNK_ANALYZE0 = new File(SVG2XMLFixtures.ACTION_DIR, "chunkAnalyzeTst0.xml");
	
	public static final File CHUNK_ANALYZE_POLICIES = new File(SVG2XMLFixtures.ACTION_DIR, "chunkAnalyzePolicies.xml");
	public static final File TWO_CHUNKS_SVG = new File(SVG2XMLFixtures.ACTION_SVG_DIR, "twoChunks.svg");
	public static final File TWO_CHUNKS1_PDF = new File(SVG2XMLFixtures.ACTION_PDF_DIR, "twoChunks1.pdf");
	public static final File TWO_COLUMNS_PDF = new File(SVG2XMLFixtures.ACTION_PDF_DIR, "twoColumns.pdf");
	public static final File BMC310_PDF = new File(SVG2XMLFixtures.ACTION_PDF_DIR, "bmc11-310.pdf");
	public static final File BMC313_PDF = new File(SVG2XMLFixtures.ACTION_PDF_DIR, "bmc11-313.pdf");
	public static final File SUSCRIPTS_PDF = new File(SVG2XMLFixtures.ACTION_PDF_DIR, "suscripts.pdf");
	public static final File FONT_STYLES_PDF = new File(SVG2XMLFixtures.ACTION_PDF_DIR, "fontStyles.pdf");
	
	public static final File SVG_AJC_DIR = new File(SVG2XMLFixtures.SVG_DIR, "ajc");
	public static final File SVG_AJC_PAGE6_SPLIT_SVG = new File(SVG2XMLFixtures.SVG_AJC_DIR, "ajc_page6_split.svg");

	public final static File TARGET = new File("target");
	public final static File TEST_PDFTOP = new File(TEST_RESOURCES_DIR, "pdfs");
	public final static File EXT_PDFTOP = new File("../pdfs");
	public final static File SVGTOP = new File(TEST_RESOURCES_DIR, "svg");
	
	public final static File BMCINDIR = new File(TEST_PDFTOP, "bmc");
	public final static File BMCOUTDIR = new File(TARGET, "bmc");
	public final static File BMCSVGDIR = new File(SVGTOP, "bmc");
	
	public final static File ELIFEINDIR = new File(TEST_PDFTOP, "elife");
	public final static File ELIFEOUTDIR = new File(TARGET, "elife");
	public final static File ELIFESVGDIR = new File(SVGTOP, "elife");

	public final static File MISCINDIR = new File(TEST_PDFTOP, "misc");
	public final static File MISCOUTDIR = new File(TARGET, "misc");
	public final static File MISCSVGDIR = new File(SVGTOP, "misc");

	public final static File PEERJINDIR = new File(TEST_PDFTOP, "peerj");
	public final static File PEERJOUTDIR = new File(TARGET, "peerj");
	public final static File PEERJSVGDIR = new File(SVGTOP, "peerj");

	public final static File ANYINDIR = new File(TEST_PDFTOP, "any");
	public final static File ANYOUTDIR = new File(TARGET, "any");
	public final static File ANYSVGDIR = new File(SVGTOP, "any");

	public final static File SVG2XMLOUTDIR = new File(TARGET, "svg2xml");
	public final static File HTMLOUTDIR = new File(SVG2XMLOUTDIR, "html");
	
	public static final File ANALYZER_DIR = new File(SVG2XML_DIR, "analyzer/");
	
	//A 4 line chunk (paragraph) with no suscripts
	public static final File PARA1_SVG = new File(SVG2XMLFixtures.ANALYZER_DIR, "1parachunk.svg");
	
	//3 paragraphs
	public static final File PARA_SUSCRIPT_SVG = new File(SVG2XMLFixtures.ANALYZER_DIR, "parasWithSuscripts.svg");
	private static final File LINE1_SVG = new File(SVG2XMLFixtures.ANALYZER_DIR, "singleLine.svg");

	public static File PAGE3RESULTS_SVG = new File(SVG2XMLFixtures.ANALYZER_DIR, "page3results.svg");
	
	//Text stuff	
	public static final File TEXT_DIR = new File(SVG2XML_DIR, "text/");
	public static final File RAWWORDS_SVG = new File(SVG2XMLFixtures.TEXT_DIR, "rawwords.svg");
//	public static final TextLine RAWWORDS_TEXT_LINE = TextStructurer.createTextLine(Fixtures.RAWWORDS_SVG, 0);

	//Whole paper	
	public static final File PDFS_BMC_DIR = new File(PDFS_DIR, "bmc/");
	public static final File SVG_BMC_DIR = new File(SVG1_DIR, "bmc/");
	public static final File MULTIPLE312_DIR = new File(PDFS_BMC_DIR, "multiple-1471-2148-11-312/");
	public static final File MULTIPLE312_PDF = new File(PDFS_BMC_DIR, "multiple-1471-2148-11-312.pdf");
	public static final File SVG_MULTIPLE312_DIR = new File(SVG_BMC_DIR, "multiple-1471-2148-11-312/");
	public static final File HTML_MULTIPLE312_DIR = new File(MULTIPLE312_DIR, "html/");
	
	public static final File RAW_MULTIPLE312_SVG_PAGE1 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page1.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE2 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page2.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE3 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page3.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE4 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page4.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE5 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page5.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE6 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page6.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE7 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page7.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE8 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page8.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE9 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page9.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE10 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page10.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE11 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page11.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE12 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page12.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE13 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page13.svg");
	public static final File RAW_MULTIPLE312_SVG_PAGE14 = new File(SVG_MULTIPLE312_DIR, "multiple-1471-2148-11-312-page14.svg");

	public static final File SVG_MULTIPLE_2_2_SVG = new File(SVG_MULTIPLE312_DIR, "chunk.g.2.2.svg");
	public static final File SVG_MULTIPLE_2_3_3_SVG = new File(SVG_MULTIPLE312_DIR, "chunk.g.2.3.3.svg");

	public final static String MATH_ROOT = "maths-1471-2148-11-311";
	public static final File MATH311_DIR = new File(PDFS_BMC_DIR, MATH_ROOT+"/");
	public static final File MATH311_PDF = new File(PDFS_BMC_DIR, MATH_ROOT+".pdf");
	public static final File SVG_MATH311_DIR = new File(SVG_BMC_DIR, MATH_ROOT+"/");
	public static final File HTML_MATH311_DIR = new File(MATH311_DIR, "html/");
	
	public static final File RAW_MATH311_SVG_PAGE1 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page1.svg");
	public static final File RAW_MATH311_SVG_PAGE2 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page2.svg");
	public static final File RAW_MATH311_SVG_PAGE3 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page3.svg");
	public static final File RAW_MATH311_SVG_PAGE4 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page4.svg");
	public static final File RAW_MATH311_SVG_PAGE5 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page5.svg");
	public static final File RAW_MATH311_SVG_PAGE6 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page6.svg");
	public static final File RAW_MATH311_SVG_PAGE7 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page7.svg");
	public static final File RAW_MATH311_SVG_PAGE8 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page8.svg");
	public static final File RAW_MATH311_SVG_PAGE9 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page9.svg");
	public static final File RAW_MATH311_SVG_PAGE10 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page10.svg");
	public static final File RAW_MATH311_SVG_PAGE11 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page11.svg");
	public static final File RAW_MATH311_SVG_PAGE12 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page12.svg");
	public static final File RAW_MATH311_SVG_PAGE13 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page13.svg");
	public static final File RAW_MATH311_SVG_PAGE14 = new File(SVG_MATH311_DIR, MATH_ROOT+"-page14.svg");

	public final static String GEO_ROOT = "geotable-1471-2148-11-310";
	public static final File GEO310_DIR = new File(PDFS_BMC_DIR, GEO_ROOT+"/");
	public static final File GEO310_PDF = new File(PDFS_BMC_DIR, GEO_ROOT+".pdf");
	public static final File SVG_GEO310_DIR = new File(SVG_BMC_DIR, GEO_ROOT+"/");
	public static final File HTML_GEO310_DIR = new File(GEO310_DIR, "html/");
	
	public static final File RAW_GEO310_SVG_PAGE1 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page1.svg");
	public static final File RAW_GEO310_SVG_PAGE2 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page2.svg");
	public static final File RAW_GEO310_SVG_PAGE3 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page3.svg");
	public static final File RAW_GEO310_SVG_PAGE4 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page4.svg");
	public static final File RAW_GEO310_SVG_PAGE5 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page5.svg");
	public static final File RAW_GEO310_SVG_PAGE6 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page6.svg");
	public static final File RAW_GEO310_SVG_PAGE7 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page7.svg");
	public static final File RAW_GEO310_SVG_PAGE8 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page8.svg");
	public static final File RAW_GEO310_SVG_PAGE9 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page9.svg");
	public static final File RAW_GEO310_SVG_PAGE10 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page10.svg");
	public static final File RAW_GEO310_SVG_PAGE11 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page11.svg");
	public static final File RAW_GEO310_SVG_PAGE12 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page12.svg");
	public static final File RAW_GEO310_SVG_PAGE13 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page13.svg");
	public static final File RAW_GEO310_SVG_PAGE14 = new File(SVG_GEO310_DIR, GEO_ROOT+"-page14.svg");

	public final static String TREE_ROOT = "tree-1471-2148-11-313";
	public static final File TREE313_DIR = new File(PDFS_BMC_DIR, TREE_ROOT+"/");
	public static final File TREE313_PDF = new File(PDFS_BMC_DIR, TREE_ROOT+".pdf");
	public static final File SVG_TREE313_DIR = new File(SVG_BMC_DIR, TREE_ROOT+"/");
	public static final File HTML_TREE313_DIR = new File(TREE313_DIR, "html/");

	public static final File BMC174_PDF = new File(PDFS_BMC_DIR, "174.pdf");

	public static final File ROBERTS_PDF = new File(PDFS_DIR, "roberts.pdf");

	public static final File PDFS_MDPI_DIR = new File(PDFS_DIR, "mdpi/");
	public static final File MDPI_02982_PDF = new File(PDFS_MDPI_DIR, "11-02982.pdf");

	public static final File PDFS_NATURE_DIR = new File(PDFS_DIR, "nature/");
	public static final File NATURE_12352_PDF = new File(PDFS_NATURE_DIR, "nature12352.pdf");

	public static final File PDFS_PEERJ_DIR = new File(PDFS_DIR, "peerj/");
	public static final File PEERJ_50_PDF = new File(PDFS_PEERJ_DIR, "50.pdf");

	public static final File PDFS_PLOS_DIR = new File(PDFS_DIR, "plosone/");
	public static final File PLOS_0049149_PDF = new File(PDFS_PLOS_DIR, "0049149.pdf");
	
	public static final File PDFS_ELS_DIR = new File(PDFS_DIR, "els/");
	public static final File ELS_1917_PDF = new File(PDFS_ELS_DIR, "1-s2.0-S1055790313001917-main.pdf");
	
	public static final File PDFS_CELL_DIR = new File(PDFS_DIR, "cell/");
	public static final File CELL_8994_PDF = new File(PDFS_CELL_DIR, "PIIS0092867413008994.pdf");

	public static final File PATHS_DIR = new File(SVG2XML_DIR, "paths");
	public static final File PATHS_SIMPLE_TREE_SVG = new File(SVG2XMLFixtures.PATHS_DIR, "simpleTree.svg");
	
	public static final File TREE_DIR = new File(SVG2XML_DIR, "tree");
	public static final File TREE_CLUSTER1_SVG = new File(SVG2XMLFixtures.TREE_DIR, "page4panel1Cluster1.svg");
	public static final File TREE_CLUSTER1A_SVG = new File(SVG2XMLFixtures.TREE_DIR, "page4panel1Cluster1a.svg");
	public static final File TREE_CLUSTER2A_SVG = new File(SVG2XMLFixtures.TREE_DIR, "page4panel1Cluster2a.svg");
	public static final File TREE_PANEL1_SVG = new File(SVG2XMLFixtures.TREE_DIR, "panel1.svg");
	public static final File TREE_8_2_SVG = new File(SVG2XMLFixtures.TREE_DIR, "image.g.8.2.svg");
	public static final File TREE_8_2_SMALL_SVG = new File(SVG2XMLFixtures.TREE_DIR, "image.g.8.2small.svg");
	public static final File TREE_3_2_A_SVG = new File(SVG2XMLFixtures.TREE_DIR, "image.g.3.2a.svg");

	public static final File FONT_DIR = new File(SVG2XML_DIR, "font");
	public static final File BMC_RUNNING_NORMAL_SVG = new File(SVG2XMLFixtures.FONT_DIR, "bmc.running.normal.svg");
	public static final File IMAGE_3_2_SVG = new File(SVG2XMLFixtures.FONT_DIR, "image.g.3.2.svg");

	public static final File FIGURE_DIR = new File(SVG2XML_DIR, "figure");
	public static final File FIGURE_PAGE_3_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "page3.svg");
	public static final File TREE_G_8_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "tree.g.8.2.svg");
	public static final File MATHS_G_6_6_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "maths.g.6.6.svg");
	public static final File MATHS_G_6_8_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "maths.g.6.8.svg");
	public static final File MATHS_G_7_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "maths.g.7.2.svg");
	public static final File HISTOGRAM_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "histogram.svg");
	public static final File XAXIS_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "xaxis.svg");
	public static final File MULTIPLE_G_7_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "multiple.g.7.2.svg");
	public static final File MULTIPLE_G_9_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "multiple.g.9.2.svg");
	public static final File LINEPLOTS_10_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "lineplots.g.10.2.svg");
	public static final File SCATTERPLOT_FIVE_7_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "scatterplot5.g.7.2.svg");
	public static final File SCATTERPLOT_7_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "scatterplot.g.7.2.svg");
	public static final File SCATTERPLOTRED_7_2_SVG = new File(SVG2XMLFixtures.FIGURE_DIR, "scatterplotred.g.7.2.svg");

	public static final File FLOW_DIR = new File(SVG2XML_DIR, "SVG2XML_DIRflow");

	// this uses outline fonts for graphics...
	public final static String GRAPHIC_TEXT_ROOT = "insect-1471-2148-11-315";
	public static final File GRAPHIC_TEXT_315_PDF = new File(PDFS_BMC_DIR, GRAPHIC_TEXT_ROOT+".pdf");
	
	public final static String SCATTERPLOTS_ROOT = "scatterplots-1471-2148-11-322";
	public static final File SCATTERPLOTS_322_PDF = new File(PDFS_BMC_DIR, SCATTERPLOTS_ROOT+".pdf");
	
	public final static String LINEPLOTS_ROOT = "lineplots-1471-2148-11-327";
	public static final File LINEPLOTS_327_PDF = new File(PDFS_BMC_DIR, LINEPLOTS_ROOT+".pdf");

	/** TABLES */
	public static final File TABLE_DIR = new File(SVG2XML_DIR, "table");
	public static final File TABLE_PDF_DIR = new File(TABLE_DIR, "pdf");
	public static final File TABLE_TYPE_DIR = new File(TABLE_DIR, "types");
	public static final File TABLE_TYPE_APA_DIR = new File(TABLE_TYPE_DIR, "apa");
	public static final File TABLE_TYPE_APAROT_DIR = new File(TABLE_TYPE_DIR, "aparot");
	public static final File TABLE_TYPE_AUTHOR_DIR = new File(TABLE_TYPE_DIR, "author");
	public static final File TABLE_TYPE_BANDED_DIR = new File(TABLE_TYPE_DIR, "banded");
	public static final File TABLE_TYPE_GRIDDED_DIR = new File(TABLE_TYPE_DIR, "gridded");
	public static final File TABLE_TYPE_LEFTBAR_DIR = new File(TABLE_TYPE_DIR, "leftbar");
	public static final File TABLE_TYPE_PANEL_DIR = new File(TABLE_TYPE_DIR, "panel");
	public static final File TABLE_TYPE_RULES_DIR = new File(TABLE_TYPE_DIR, "rules");
	public static final File TABLE_TYPE_TEXT_DIR = new File(TABLE_TYPE_DIR, "text");
	public static final File[] TABLE_TYPES = { 
		TABLE_TYPE_APA_DIR,
		TABLE_TYPE_APAROT_DIR,
		TABLE_TYPE_AUTHOR_DIR,
		TABLE_TYPE_BANDED_DIR,
		TABLE_TYPE_GRIDDED_DIR,
		TABLE_TYPE_LEFTBAR_DIR,
		TABLE_TYPE_PANEL_DIR,
		TABLE_TYPE_RULES_DIR,
	};
	
	public static final File DK_PAGE1_SVG = new File(SVG2XMLFixtures.TABLE_DIR, "dk.page1.svg");
	public static final File BERICHT_PAGE6_SVG = new File(SVG2XMLFixtures.TABLE_DIR, "bericht.page6.svg");
	public static final File BERICHT_PAGE22_SVG = new File(SVG2XMLFixtures.TABLE_DIR, "bericht.page22.svg");
	// ========================

	public static final File BUILDER_DIR = new File(SVG2XML_DIR, "builder");

	public static final File IMAGES_DIR = new File(SVG2XML_DIR, "images");
	public static final File IMAGE_G_2_2_SVG = new File(IMAGES_DIR, "image.g.2.2.svg");
	public static final File IMAGE_G_2_2_PNG = new File(IMAGES_DIR, "image.g.2.2.png");
	public static final File IMAGE_G_3_2_SVG = new File(IMAGES_DIR, "image.g.3.2.svg");
	public static final File IMAGE_G_8_0_SVG = new File(IMAGES_DIR, "image.g.8.0.svg");
	public static final File IMAGE_G_8_2_SVG = new File(IMAGES_DIR, "image.g.8.2.svg");

	public static final File MOLECULE_DIR = new File(SVG2XML_DIR, "molecules");
	public static final File IMAGE_2_11_SVG = new File(MOLECULE_DIR, "image.g.2.11.svg");
	public static final File IMAGE_2_11_NO2_SVG = new File(MOLECULE_DIR, "image.g.2.11.no2.svg");
	public static final File IMAGE_2_11_HO_SVG = new File(MOLECULE_DIR, "image.g.2.11.ho.svg");
	public static final File IMAGE_2_13_SVG = new File(MOLECULE_DIR, "image.g.2.13.svg");
	public static final File IMAGE_2_15_SVG = new File(MOLECULE_DIR, "image.g.2.15.svg");
	public static final File IMAGE_2_16_SVG = new File(MOLECULE_DIR, "image.g.2.16.svg");
	public static final File IMAGE_2_18_SVG = new File(MOLECULE_DIR, "image.g.2.18.svg");
	public static final File IMAGE_2_23_SVG = new File(MOLECULE_DIR, "image.g.2.23.svg");
	public static final File IMAGE_2_25_SVG = new File(MOLECULE_DIR, "image.g.2.25.svg");
	public static final File IMAGE_5_11_SVG = new File(MOLECULE_DIR, "image.g.5.11.svg");
	public static final File IMAGE_5_12_SVG = new File(MOLECULE_DIR, "image.g.5.12.svg");
	public static final File IMAGE_5_13_SVG = new File(MOLECULE_DIR, "image.g.5.13.svg");
	public static final File IMAGE_5_14_SVG = new File(MOLECULE_DIR, "image.g.5.14.svg");
	public static final File IMAGE_02_00100_65_SVG = new File(MOLECULE_DIR, "02.00100.g.6.5.svg");

	public static final File PLOT_DIR = new File(SVG2XML_DIR, "plot");

	//==================================================	
	
	public static void drawChunkBoxes(List<Chunk> finalChunkList) {
		for (Chunk chunk : finalChunkList) {
			SVGElement bbox = chunk.createGraphicalBoundingBox();
			if (bbox != null) {
				chunk.appendChild(bbox);
			}
		}
	}
	
	public final static AbstractCMElement createSVGElement(File file) {
		AbstractCMElement svgElement =  null;
		try {
			svgElement = SVGElement.readAndCreateSVG(new Builder().build(file).getRootElement());
		} catch (Exception e) {
			throw new RuntimeException("Cannot create SVGElement", e);
		}
		return svgElement;
	}
	
	public static SVGSVG createSVGPage(File svgFile) {
		SVGSVG svgPage = null;
		try {
			svgPage = (SVGSVG) SVGElement.readAndCreateSVG(new Builder().build(svgFile).getRootElement());
		} catch (Exception e){
			throw new RuntimeException("Cannot create SVG: ", e);
		}
		return svgPage;
	}


}
