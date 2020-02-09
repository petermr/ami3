package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIAssertTool;
import org.contentmine.ami.tools.AMIDisplayTool;
import org.contentmine.ami.tools.AMIFilterTool;
import org.contentmine.ami.tools.AMIForestPlotTool;
import org.contentmine.ami.tools.AMIImageTool;
import org.contentmine.ami.tools.AMIMakeProjectTool;
import org.contentmine.ami.tools.AMIOCRTool;
import org.contentmine.ami.tools.AMIPDFTool;
import org.contentmine.ami.tools.AMIPixelTool;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.ami.tools.AMIForestPlotTool.ForestPlotType;
import org.contentmine.ami.tools.AbstractAMITool.Scope;
import org.contentmine.ami.tools.ocr.LevenshteinDistanceAligment;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIForestPlotTest {
	private static final Logger LOG = Logger.getLogger(AMIForestPlotTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final File FOREST_PLOT_DIR = new File("/Users/pm286/projects/forestplots");
	private static final File STATA_FOREST_PLOT_DIR = new File(FOREST_PLOT_DIR, "stataforestplots");
	private final static String SPSS = "spss";
	private static final File SPSS_DIR = new File(FOREST_PLOT_DIR, SPSS);
	private final static String SPSS_SIMPLE = "spssSimple";
	private static final File SPSS_SIMPLE_DIR = new File(FOREST_PLOT_DIR, SPSS_SIMPLE);
	private final static String SPSS_MULTIPLE = "spssMultiple";
	private static final File SPSS_MULTIPLE_DIR = new File(FOREST_PLOT_DIR, SPSS_MULTIPLE);
	private final static String SPSS_SUBPLOT = "spssSubplot";
	private static final File SPSS_SUBPLOT_DIR = new File(FOREST_PLOT_DIR, SPSS_SUBPLOT);
	private final static String STATA = "stata";
	private static final File STATA_DIR = new File(STATA_FOREST_PLOT_DIR, STATA);
	private final static String STATA_SIMPLE = "stataSimple";
	private static final File STATA_SIMPLE_DIR = new File(FOREST_PLOT_DIR, STATA_SIMPLE);
//	private final static String STATA_TOTAL_EDITED = "stataTotalEdited";
//	private static final File STATA_TOTAL_EDITED_DIR = new File(STATA_FOREST_PLOT_DIR, STATA_TOTAL_EDITED);
	public static final String DEVTEST = SPSS_DIR.toString();
	
	/** common arguments - note leading space */
	private final static String DS = " --despeckle true";
	private final static String EXTLINES_GOCR = " --extractlines gocr";
	private final static String EXTLINES_HOCR = " --extractlines hocr";
	private final static String GOCR = " --gocr /usr/local/bin/gocr";
	private final static String GOCR_ALPHA2NUM = " o 0 O 0 d 0   e 2   q 4 A 4   s 5 S 5 $ 5   d 6 G 6  J 7   T 7   Z 2   a 4 a 0";
	private final static String SHARP4 = " --sharpen sharpen4 ";
	private static final String TEMPLATE_XML = " --template template.xml";
	private final static String TESSERACT = " --tesseract /usr/local/bin/tesseract";
	private final static String THRESH = " --threshold ";

	private static final String PMC5882397 = "PMC5882397";
	private static final String PMC5502154 = "PMC5502154";
	private CProject cProject;
	private CTree cTree;


	@Test
	public void testHelp() {
		new AMIForestPlotTool().runCommands("--help");
	}
	
	@Test
	/** 
	 */
	public void testFilterTrees() throws Exception {
		String args = 
//				"-t "+DEVTEST+"bowmann-perrottetal_2013"
//				"-t "+DEVTEST+"buzick_stone_2014_readalo"
//				"-t "+DEVTEST+"campbell_systematic_revie"
				"-t "+DEVTEST+PMC5502154
//				"-t "+DEVTEST+"mcarthur_etal2012_cochran"
//				"-t "+DEVTEST+"puziocolby2013_co-operati"
//				"-t "+DEVTEST+"torgersonetal_2011dferepo"
//				"-t "+DEVTEST+"zhengetal_2016"
//				+ SHARP4
				+ THRESH + " 180"
				+ " --binarize BLOCK_OTSU"
//				+ " --rotate 270"
				+ " --priority SCALE"
				;
		new AMIForestPlotTool().runCommands(args);
	}


	@Test
	/** 
	 * convert single tree
	 */
	public void testBuzick() throws Exception {
		String[] args = {
				"-t", ""+DEVTEST+"PMC6417514"
			};
		new AMIForestPlotTool().runCommands(args);
	}
	
	@Test
	/** 
	 * convert single tree
	 */
	public void testDonkerPlus() throws Exception {
		String args =
		"-p "+DEVTEST+""
		+ " --includetree"
		+ " donkerdeboerkostons2014_l"
		+ " ergen_canagli_17_"
		+ " fanetal_2017_meta_science"
		+ ""
		+ SHARP4
		+ THRESH + " 180"
//		+ " --binarize GLOBAL_ENTROPY"
		+ " --priority SCALE"
		;
//		new AMIImageTool().runCommands(args);

		args = 	"-p "+DEVTEST+""
				+ " --includetree"
				+ " donkerdeboerkostons2014_l"
				+ " ergen_canagli_17_"
				+ " fanetal_2017_meta_science"
			;
		new AMIForestPlotTool().runCommands(args);
	}
	
	
	@Test
	/** scale small text
	 * 
	 * @throws Exception
	 */
	public void testScaleOCRProject() throws Exception {
		String args = ""
				+ "-p "+DEVTEST+""
				+ " --includetree"
				+ " buzick%"
				+ " case_systematic_review_ar"
				+ " case_systematic_review_ju"
				+ " cole_2014"
				+ " dietrichson%"
				+ " donkerdeboerkostons2014_l"
				+ " ergen_canagli_17_"
				+ " fanetal_2017_meta_science"
//				+ " fauzan03"  // large scanned
				+ " higginshallbaumfieldmosel"
				+ " kunkel_2015"
				+ " marulis_2010-300-35review"
				+ " mcarthur_etal2012_cochran"
				+ " puziocolby2013_co-operati"
				+ " rui2009_meta_detracking"
				+ " shenderovich_2016_pub"
//				+ " tamim-2009-effectsoftechn" // large scanned
				+ " zhengetal_2016"
				+ ""
				+ " --html true"
                + " --scalefactor 2.0"
				;
		new AMIForestPlotTool().runCommands(args);
	}
	

	@Test
	public void testSPSSTable() {
		String plotType = SPSS;
		boolean useTree = true;
//		useTree = false;
		String treename = PMC5502154;
//		extractPlots(plotType, treename, useTree);		
		analyzePlots(plotType, treename, useTree);		
	}

	@Test
	public void testStataTable() {
		String plotType = STATA;
		String treename = PMC5502154;
		boolean useTree = false;
		extractPlots(plotType, treename, useTree);
		
	}
	
	@Test 
	/** splits components of SPSS ForestPlots
	 * 
	 */
	public void testSPSSSplit() {
		
		String source = createSourceFromProjectAndTree("-t",SPSS_DIR, PMC5502154);
		AMIForestPlotTool forestPlotTool = new AMIForestPlotTool();
		String cmd = ""
			+ source
			+ " --split x"
//			+ " --color 0x0"
			+ " --offset -10"
			+ " --minline 300"
		    + "";
		forestPlotTool.runCommands(cmd);

	}

	@Test 
	/** creates columns for SPSS Tables
	 * 
	 */
	@Ignore
	public void testSPSSTableBBoxes() {
		
		String source = createSourceFromProjectAndTree("-t",ForestPlotType.spss);
		AMIForestPlotTool forestPlotTool = new AMIForestPlotTool();
		String cmd = ""
			+ source
			+ " --table"
		    + "";
		System.out.println("ami-forest "+cmd);
		forestPlotTool.runCommands(cmd);

	}

	@Test 
	/** splits SPSS Plots vertically into Table and Graph
	 * 
	 */
	public void testSplitSPSSSimpleTableGraph() {
		

		String source = createSourceFromProjectAndTree("-t",SPSS_SIMPLE_DIR, "PMC5911624");

		AMIForestPlotTool forestPlotTool = new AMIForestPlotTool();
		String cmd = ""
			+ source
			+ TEMPLATE_XML
		    + "";
		System.out.println("ami-forest "+cmd);
		forestPlotTool.runCommands(cmd);

	}

	@Test
	public void testSplitSPSSMultipleTableGraph() {
		
		File projectDir = SPSS_MULTIPLE_DIR;
		CProject cProject = new CProject(projectDir);
		AMIForestPlotTool forestPlotTool = new AMIForestPlotTool();
		String source = "--cproject "+cProject.getDirectory();
		String cmd = ""
			+ source
			+ TEMPLATE_XML
		    + "";
		System.out.println("ami-forest "+cmd);
		forestPlotTool.runCommands(cmd);

	}

	@Test
	public void testSplitSPSSSubplotTableGraph() {
		
		File projectDir = SPSS_SUBPLOT_DIR;
		CProject cProject = new CProject(projectDir);
		AMIForestPlotTool forestPlotTool = new AMIForestPlotTool();
		String source = "--cproject "+cProject.getDirectory();
		String cmd = ""
			+ source
			+ TEMPLATE_XML
		    + "";
		System.out.println("ami-forest "+cmd);
		forestPlotTool.runCommands(cmd);

	}

	@Test
	public void testSPSSImageProcessing() {
		
		String source = createSourceFromProjectAndTree("-t",SPSS_DIR, PMC5502154);
		AMIImageTool imageTool = new AMIImageTool();
		String cmd = ""
			+ source
			+ SHARP4
			+ THRESH + " 150"
			+ " --scalefactor 2.0";
		imageTool.runCommands(cmd);
		AMIForestPlotTool forestPlotTool = new AMIForestPlotTool();
		cmd = ""
			+ source
			+ " --table";
		
		forestPlotTool.runCommands(cmd);
	}

	/** assumes CProject structure but no subdirectories 
	 * */
	@Test
	public void testTotalStata() {
		
		boolean useTree = 
//				true
				false
				;
		String source = createSourceFromProjectAndTree("-t",ForestPlotType.stata);
		
		boolean makeProject = 
//				true
				false
				;
		boolean makePdf = 
//				true
				false
				;
		boolean makeImage = 
				false
//				true
				;
		boolean makeOCR = 
				false
//				true
				;
		boolean makeHOCR = 
				false
//				true
				;
		boolean makeGOCR = 
				false
//				true
				;
		boolean extractLines = true;
		boolean makePixel = 
//				false
				true
				;
		boolean makeForest = 
//				false
				true
				;
		
		/** make the CTrees -no-op if already present 
		 * from commandline:
		 *  ami-makeproject -p /Users/pm286/my/project  --rawfiletypes html,pdf,xml";
		 */
		String makeProjectCmd = " -p " + cProject.getDirectory().getAbsoluteFile() + " --rawfiletypes html,pdf,xml" + " --omit template\\.xml log\\.txt"
				;
		if (makeProject) new AMIMakeProjectTool().runCommands(makeProjectCmd);

		
		// =====PDF======
		/** parse PDFs and extract images // this will contain non-forest images
		 * from commandline:
		 *  ami-pdf -p /Users/pm286/my/project ";
		 */
		String pdfCmd = " -p " + cProject.getDirectory().getAbsoluteFile()  ;
		if (makePdf) new AMIPDFTool().runCommands(pdfCmd);
		
		// =====Image======
		/** enhance images by thresholding and sharpening.
		 * from commandline:
		 *  ami-pdf -p /Users/pm286/my/project --sharpen sharpen4 --threshold 150 despeckle true";
		 * 
		 */

		String imageCmd = ""
				+ source
				+ SHARP4
				+ THRESH + " 120"
				+ DS
				;
		if (makeImage) new AMIImageTool().runCommands(imageCmd);
		imageCmd = ""
				+ source
				+ THRESH + " 120"
				+ DS
				;
		if (makeImage) new AMIImageTool().runCommands(imageCmd);
		imageCmd = ""
				+ source
				+ SHARP4
				+ THRESH + " 200"
				+ DS
				;
		if (makeImage) new AMIImageTool().runCommands(imageCmd);
		imageCmd = ""
				+ source
				+ THRESH + " 200"
				+ DS
				+ " -vvv"
				;
		if (makeImage) new AMIImageTool().runCommands(imageCmd);

		imageCmd = ""
				+ source
				+ THRESH + " 240"
				+ DS
				+ " -vvv"
				;
		if (makeImage) new AMIImageTool().runCommands(imageCmd);

		// =====OCR======
		/** Optical Character Recognition OCR.
		 * from commandline:
		 *  ami-ocr -p /Users/pm286/my/project " --gocr /usr/local/bin/gocr"
		 * 
		 */
		String ocrCmd = ""
				+ source
				+ TESSERACT
				+ EXTLINES_HOCR
		;
		if (makeHOCR) new AMIOCRTool().runCommands(ocrCmd);
		ocrCmd = source + GOCR + EXTLINES_GOCR;
		;
		if (makeGOCR) new AMIOCRTool().runCommands(ocrCmd);

		// =====Pixel get subimage dimensions ======
		String pixelCmd = ""
			+ source
			+ " --projections --yprojection 0.8 --xprojection 0.5"
			+ " --minheight -1 --rings -1 --islands 0"
			+ " --inputname raw_thr_230_ds"
			+ " --subimage statascale y 2 delta 10 projection x"
			+ " --templateinput raw_thr_230_ds/projections.xml"
			+ " --templateoutput template.xml"
			+ " --templatexsl /org/contentmine/ami/tools/stataTemplate.xsl"
			;
		if (makePixel) new AMIPixelTool().runCommands(pixelCmd);

		// =====Forest======

		if (makeForest) new AMIForestPlotTool().runCommands(source + TEMPLATE_XML);


	}
	
	
	@Test
	public void testTemplateXML() {
		String source = createSourceFromProjectAndTree("-t",ForestPlotType.stata);

		// first make template.xml
		String pixelCmd = ""
			+ source
			+ " --projections --yprojection 0.8 --xprojection 0.5"
			+ " --minheight -1 --rings -1 --islands 0"
			+ " --inputname raw_thr_230_ds"
			+ " --subimage statascale y 2 delta 10 projection x"
			+ " --templateinput raw_thr_230_ds/projections.xml"
			+ " --templateoutput template.xml"
			+ " --templatexsl /org/contentmine/ami/tools/stataTemplate.xsl"
			;
		new AMIPixelTool().runCommands(pixelCmd);
		// now use it to section images
		String forestCmd = source + " --segment --template raw_thr_230_ds/template.xml"
				;
		new AMIForestPlotTool().runCommands(forestCmd);
	}
	
	@Test
	public void testHOCROnRawSplitRegions() {
		String source = createSourceFromProjectAndTree("-t",ForestPlotType.stata);

//		String imageCmd = source + " --inputname raw.body.ltable --sharpen sharpen4 --threshold 120 --despeckle true";
//		new AMIImageTool().runCommands(imageCmd);

		new AMIOCRTool().runCommands(source + " --inputname raw.body.ltable" + TESSERACT + EXTLINES_HOCR);
		new AMIOCRTool().runCommands(source + " --inputname raw.body.rtable" + TESSERACT + EXTLINES_HOCR);
		new AMIOCRTool().runCommands(source + " --inputname raw.header" + TESSERACT + EXTLINES_HOCR);
		new AMIOCRTool().runCommands(source + " --inputname raw.scale" + TESSERACT + EXTLINES_HOCR);
	}
	
	@Test
	public void testHOCRandGOCR() {
		String source = createSourceFromProjectAndTree("-t",ForestPlotType.stata);

		String FORCEMAKE = " --forcemake";

		// sharpen subimages
		int thresh = 150;

		new AMIImageTool().runCommands(source + " --inputname raw.header" + SHARP4 +  THRESH + " "+thresh + DS);
		new AMIImageTool().runCommands(source + " --inputname raw.body.ltable" + SHARP4 + THRESH + " "+thresh + DS);
		new AMIImageTool().runCommands(source + " --inputname raw.body.rtable" + SHARP4 +  THRESH + " "+thresh + DS);		

 		new AMIOCRTool().runCommands(source + " --inputname raw.header_s4_thr_"+thresh+"_ds" + TESSERACT + EXTLINES_HOCR + FORCEMAKE);
		new AMIOCRTool().runCommands(source + " --inputname raw.body.ltable_s4_thr_"+thresh+"_ds" + TESSERACT + EXTLINES_HOCR + FORCEMAKE);
		new AMIOCRTool().runCommands(source + " --inputname raw.body.rtable_s4_thr_"+thresh+"_ds" + TESSERACT + EXTLINES_HOCR + FORCEMAKE);

		new AMIOCRTool().runCommands(source + " --inputname raw.header_s4_thr_"+thresh+"_ds" + GOCR + EXTLINES_HOCR + FORCEMAKE);
		new AMIOCRTool().runCommands(source + " --inputname raw.body.ltable_s4_thr_"+thresh+"_ds" + GOCR + EXTLINES_HOCR + FORCEMAKE);
		new AMIOCRTool().runCommands(source + " --inputname raw.body.rtable_s4_thr_"+thresh+"_ds" + GOCR + EXTLINES_HOCR + FORCEMAKE
				+ " --replace" + GOCR_ALPHA2NUM
				);
		
	}
	
	@Test
	/** requires some tests to be run previously (UGH)
	 * 
	 */
	public void testStataStack() {
		String source = createSourceFromProjectAndTree(AbstractAMITool.Scope.PROJECT, ForestPlotType.stata);

		/** make template - requires */
		String thresh = "150";
		String process = "s4_thr_" + thresh + "_ds";
		new AMIPixelTool().runCommands(source
				+ " --projections --yprojection 0.8 --xprojection 0.6 --lines"
				+ " --minheight -1 --rings -1 --islands 0"
				+ " --inputname raw_"+process
				+ " --subimage statascale y LAST delta 10 projection x"
				+ " --templateinput raw_"+process+"/projections.xml"
				+ " --templateoutput template.xml"
				+ " --templatexsl /org/contentmine/ami/tools/stataTemplate1.xsl");

		/** segment image */
		new AMIForestPlotTool().runCommands(source + " --segment --inputname raw_"+process+" --template raw_" + process + "/template.xml");
		String f = "raw_" + process + "/template.xml";
		System.out.println("f"+f);
 		new AMIImageTool().runCommands(source + " --inputnamelist" 
 				+ " raw.header"
 				+ " raw.body.ltable"
 				+ " raw.body.rtable"
 				+ " raw.scale"
 				+ SHARP4 +  THRESH + " "+thresh + DS);
		
 		new AMIOCRTool().runCommands(source + " --inputnamelist"
 				+ " raw.header_"+process
 				+ " raw.body.ltable_"+process
 				+ " raw.body.rtable_"+process
 				+ " raw.scale_"+process
 				+ TESSERACT + EXTLINES_HOCR );

 		new AMIOCRTool().runCommands(source + " --inputnamelist"
 				+ " raw.header_"+process
 				+ " raw.body.ltable_"+process
 				+ " raw.body.rtable_"+process
 				+ " raw.scale_"+process
 				+ GOCR + EXTLINES_GOCR );

		
		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.body.rtable_"+process
				+ " --display .png hocr/hocr.svg gocr/gocr.svg "
				+ " --orientation horizontal"
				+ " --aggregate raw.body.rtable_"+process+".html"
				);
		
		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.body.ltable_"+process
				+ " --display .png hocr/hocr.svg gocr/gocr.svg"
				+ " --orientation horizontal"
				+ " --aggregate raw.body.ltable_"+process+".html"
				);

		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.scale_"+process
				+ " --display .png hocr/hocr.svg gocr/gocr.svg"
				+ " --orientation horizontal"
				+ " --aggregate raw.scale_"+process+".html"
				);
		
		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.header_"+process
				+ " --display .png hocr/hocr.svg gocr/gocr.svg"
				+ " --orientation horizontal"
				+ " --aggregate raw.header_"+process+".html"
				);
		
	}
	
	@Test
	public void testIntegrateHOCRGOCRScale() {
		String source = createSourceFromProjectAndTree(AbstractAMITool.Scope.TREE, ForestPlotType.stata);
		String thresh = "150";
		String process = "s4_thr_" + thresh + "_ds";
		
 		new AMIOCRTool().runCommands(source + ""
// 				+ " --inputname raw.scale_"+process
 				+ " --inputname raw.body.rtable_"+process
// 				+ " --config _config/config.xml "
// 				+ " --whitelist hocr gocr"
// 				+ " --regex "
 				
 				+ " --merge hocr/hocr.boxes.svg gocr/gocr.boxes.svg"
 				);
 		
//		new AMIDisplayTool().runCommands(source + ""
//				+ " --inputname raw.scale_"+process
//				+ " --display .png hocr/hocr.svg gocr/gocr.svg"
//				+ " --orientation vertical"
//				+ " --aggregate raw.scale_"+process+".html"
//				);
		
	}

	@Test
	/** thresh 150 leaves gaps in lines. 
	 * 
	 */
	public void testStataSegmentAndAssert() {
		String treename = "PMC5992663";
		Scope scope = AbstractAMITool.Scope.PROJECT;
//		scope = AbstractAMITool.Scope.TREE;
		String source = createSourceFromProjectAndTree(scope, ForestPlotType.stata , treename);

		String raw = "raw";
		String th120 = "120";
		String th150 = "150";
		String th180 = "180";
		String th230 = "230";
		String sh120 = source + SHARP4 + THRESH + th120 + DS;
		String sh150 = source + SHARP4 + THRESH + th150 + DS;
		String sh180 = source + SHARP4 + THRESH + th180 + DS;
		String sh230 = source + SHARP4 + THRESH + th230 + DS;
		String sh = sh180;
				;
		new AMIImageTool().runCommands(sh);

		/** make template - requires */
		String pr120 = "s4_thr_" + th120 + "_ds";
		String pr150 = "s4_thr_" + th150 + "_ds";
		String pr180 = "s4_thr_" + th180 + "_ds";
		String pr230 = "s4_thr_" + th230 + "_ds";
		String basename = pr180;
		
		new AMIPixelTool().runCommands(source
				+ " --projections --yprojection 0.8 --xprojection 0.5 --lines --mingap 3"
				+ " --minheight -1 --rings -1 --islands 0"
				+ " --inputnamelist raw_"+basename 
				+ " --subimage statascale y LAST delta 10 projection x"
				+ " --templateinput raw_"+basename+"/projections.xml"
				+ " --templateoutput template.xml"
				+ " --templatexsl /org/contentmine/ami/tools/stataTemplate1.xsl"
				);
		
		new AMIAssertTool().runCommands(source
				+ " --inputname raw_"+basename+"/projections.xml"
				+ " --subdirectorytype pdfimages"
				+ " --message horizontal lines"
				+ " --assertType xpath"
				+ " --xpath /projections/*[local-name()='g'%20and%20@class='horizontallines']/*[local-name()='line']"
				+ " --sizes 1 2"
				);
		
		new AMIAssertTool().runCommands(source
				+ " --inputname raw_"+basename+"/projections.xml"
				+ " --subdirectorytype pdfimages"
				+ " --message horizontal lines"
				+ " --assertType xpath"
				+ " --xpath /projections/*[local-name()='g'%20and%20@class='verticallines']/*[local-name()='line'] 1 2 "
				+ " --sizes 1 2"
				+ " --inputname raw_"+basename+"/projections.xml"
				+ " --assert Vertical xpath "
				);
		
		
		/** segment image */
		source = createSourceFromProjectAndTree(scope, ForestPlotType.stata , treename);
		/** use the template above on the current project/trees */
		new AMIForestPlotTool().runCommands(source + " --segment --inputname " + raw + " --template raw_"+basename+"/template.xml");

				
}

	@Test
	public void testAlign() {
		align("abcdef", "abxdef");
		align("abcdef", "abdef");
		align("abdef", "abcdef");
		align("abcdef", "abcxdef");
		align("123abcdef", "123abxdcef");
		align("123456789", "123a456b789");
		align("123456789", "123a46b789");
	}
	
	@Test
	/** warning creates lots of files */ 
	public void testThresholds() {
		String source = createSourceFromProjectAndTree("-t", ForestPlotType.spss);
		String FORCEMAKE = "";
//		String FORCEMAKE = " --forcemake";
		
		// convert PDF (will skip if already done)
		new AMIPDFTool().runCommands(source);
		// scan the probable thresholds
		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 90" + DS);
		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 120" + DS);
		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 150" + DS);
		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 180" + DS);
		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 230" + DS);
		// display what we have got; later create an ami-display command
		new AMIDisplayTool().runCommands(source + ""
//				+ " --inputname raw.body.rtable_s4_thr_120_ds"
				+ " --display "
				+ " raw_s4_thr_90_ds.png"
				+ " raw_s4_thr_150_ds.png"
				+ " raw_s4_thr_180_ds.png"
				+ " raw_s4_thr_230_ds.png"
				+ " --orientation vertical"
				+ " --aggregate raw_s4_120_150_180_ds.html");
	}
	
	
	@Test
	public void testSPSSSimple() {
		String source = createSourceFromProjectAndTree(Scope.TREE, ForestPlotType.spss);
		String FORCEMAKE = "";
//		String FORCEMAKE = " --forcemake";
		
		// convert PDF (will skip if already done)
		new AMIPDFTool().runCommands(source + FORCEMAKE);
		// scan the probable thresholds
//		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 120" + DS);
		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 150" + DS);
//		new AMIImageTool().runCommands(source + SHARP4 + THRESH + " 180" + DS);
		// display what we have got; later create an ami-display command
		new AMIDisplayTool().runCommands(source + ""
				+ " --display "
//				+ " raw_s4_thr_120_ds.png"
				+ " raw_s4_thr_150_ds.png"
//				+ " raw_s4_thr_180_ds.png"
				+ " --orientation vertical"
				+ " --aggregate raw_s4_150_ds.html");

		String basename = "raw_s4_thr_150_ds";
		new AMIPixelTool().runCommands(source
			+ " --projections --yprojection 0.4 --xprojection 0.7 --lines"
			+ " --minheight -1 --rings -1 --islands 0"
			+ " --inputname "+basename
			+ " --templateinput "+basename+"/projections.xml"
			+ " --templateoutput template.xml"
			+ " --templatexsl /org/contentmine/ami/tools/spssTemplate1.xsl");

		new AMIAssertTool().runCommands(source
				+ " --inputname "+basename+"/projections.xml"
				+ " --subdirectorytype pdfimages"
				+ " --type xpath"
				+ " --message  Horizontal lines"
				+ " --xpath /projections/*[local-name()='g'%20and%20@class='horizontallines']/*[local-name()='line'] "
				+ " --size 2"
				);
		
		new AMIAssertTool().runCommands(source
				+ " --inputname "+basename+"/projections.xml"
				+ " --subdirectorytype pdfimages"
				+ " --type xpath"
				+ " --message Vertical lines"
				+ " --xpath /projections/*[local-name()='g'%20and%20@class='verticallines']/*[local-name()='line']"
				+ " --size 1 "
				);
		

		/** segment image */
		String inputname = "raw";
		new AMIForestPlotTool().runCommands(source + " --inputname " +inputname + " --segment --template "+basename+"/template.xml");

		String sharp = SHARP4 + THRESH + " 150" + DS;
		/** sharpen/threshold segment images */
		
		String raw_s4_thr_150_ds_list = ""
			+ " raw.header.tableheads_s4_thr_150_ds " 
			+ " raw.header.graphheads_s4_thr_150_ds "
			+ " raw.body.table_s4_thr_150_ds "
			+ " raw.footer.summary_s4_thr_150_ds "
			+ " raw.footer.scale_s4_thr_150_ds "
			;

		new AMIImageTool().runCommands(source + " --inputnamelist "
				+ " raw.header.tableheads "
				+ " raw.header.graphheads "
				+ " raw.body.table "
				+ " raw.footer.summary "
				+ " raw.footer.scale "
				+ sharp
				);
		String tess = TESSERACT + EXTLINES_HOCR + FORCEMAKE;
		new AMIOCRTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + tess);
		
		String gocr = GOCR + EXTLINES_HOCR + FORCEMAKE;
		new AMIOCRTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + gocr);
		
		new AMIForestPlotTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + " --table "+"hocr/hocr.svg" );
		new AMIForestPlotTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + " --table "+"gocr/gocr.svg" );
		
		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.body.table_s4_thr_150_ds"
				+ " --display ../raw.body.table.png .png hocr/hocr.svg gocr/gocr.svg"
				+ " --orientation vertical"
				+ " --aggregate raw.body.table_s4_thr_150_ds.html"
				);

		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.header.tableheads_s4_thr_150_ds"
				+ " --display ../raw.header.tableheads.png .png hocr/hocr.svg gocr/gocr.svg"
				+ " --orientation vertical"
				+ " --aggregate raw.header.tableheads_s4_thr_150_ds.html"
				);

		/** analyze graph */
		/** lines are written to "projections.xml" */
		/** 0.02 is too small and picks up the squares. It might miss some very short lines */
		new AMIPixelTool().runCommands(source
				+ " --projections --yprojection 0.05  --lines "
				+ " --minheight -1 --rings -1 --islands 0"
				+ " --inputname raw.body.graph_s4_thr_150_ds"
				);
		assertImageDirFileExists("raw.body.graph_s4_thr_150_ds", "lines.svg");
		assertImageDirFileExists("raw.body.graph_s4_thr_150_ds", "projections.xml");
		
		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.body.graph_s4_thr_150_ds"
				+ " --display lines.svg ../raw.body.png"
				+ " --orientation overlap"
//				+ " --aggregate raw.header.tableheads_s4_thr_150_ds.html"
				);

	}

	@Test
	public void testSPSSTable1() {
		String source = createSourceFromProjectAndTree("-t", ForestPlotType.spss);
		
		/** attempt to layout table
		 * 
		 */
		new AMIForestPlotTool().runCommands(source + ""
				+ " --inputname raw.body.table_s4_thr_150_ds"
				+ " --table hocr/hocr.svg"
				+ " --tableType hocr"
				);

		new AMIForestPlotTool().runCommands(source + ""
				+ " --inputname raw.body.table_s4_thr_150_ds"
				+ " --table hocr/gocr.svg"
				+ " --tableType gocr"
				);

		new AMIPixelTool().runCommands(source + ""
				+ " --projections --yprojection 0.02  --lines"
				+ " --minheight -1 --rings -1 --islands 0"
				+ " --inputname raw.body.graph_s4_thr_150_ds"
				);

		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.body.graph_s4_thr_150_ds"
				+ " --display ../raw.header.tableheads.png .png hocr/hocr.svg gocr/gocr.svg"
				+ " --orientation vertical"
				+ " --aggregate raw.header.tableheads_s4_thr_150_ds.html"
				);

		/** compare how hocr and gocr tables correspond */
		/** DEVELOP THIS !!*/
		new AMIDisplayTool().runCommands(source + ""
				+ " --inputname raw.body.table_s4_thr_150_ds"
				+ " --display ../raw.body.table.png hocr/hocr.boxes.svg .png gocr/gocr.boxes.svg hocr/hocr.boxes.svg"
				+ " --orientation vertical"
				+ " --aggregate raw.body.table_s4_thr_150_ds.html"
				);


	}
	
	@Test
	public void testSegmentSharpenedStata() {
		String source = createSourceFromProjectAndTree("-p", ForestPlotType.stata);
	}
	
	
	@Test
	public void testRemoveLines() {
		String source = createSourceFromProjectAndTree("-p", ForestPlotType.stata);

		/** calculate projections and lines */
		new AMIPixelTool().runCommands(source
				+ " --projections --yprojection 0.05 --xprojection 0.5 --lines "
				+ " --minheight -1 --rings -1 --islands 0"
				+ " --inputname raw.body.graph_s4_thr_150_ds"
				);

		/** remove lines */
		new AMIPixelTool().runCommands(source 
				+ " --inputname raw.body.graph_s4_thr_150_ds"
				+ " --minheight -1 --rings -1 --islands 0"
				+ " --removelines raw.body.graph_s4_thr_150_ds/projections.xml"
				+ " --overlap"
				);
		/** aggregate results */
		new AMIDisplayTool().runCommands(source 
				+ " --inputname raw.body.graph_s4_thr_150_ds"
				+ " --display .png projections.remove.png"
				+ " --orientation horizontal"
				+ " --aggregate raw.body.graph_s4_thr_150_ds.html"


				);

	}

	@Test
	public void testRegression() {
		testSPSSSimple();
		testStataSegmentAndAssert();
		testStataStack();
	}
	
	/** delete a single tree and run the complete stack over it
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testSPSSSimpleClean() throws IOException {
		String source = createSourceFromProjectAndTree(Scope.TREE, ForestPlotType.spss);
/** uncomment to start from scratch 
 * 		resetCTree(source, "PMC5502154/");
 */
		new AMIPDFTool().runCommands(source);
		new AMIFilterTool().runCommands(source + " --small small --duplicate duplicate --monochrome monochrome");
		String inputname = "raw";
		new AMIImageTool().runCommands(source + " --inputname "+inputname+ " "+ SHARP4 + THRESH + " 150" + DS);

		String basename = "raw_s4_thr_150_ds";
		new AMIPixelTool().runCommands(source
			+ " --projections --yprojection 0.4 --xprojection 0.7 --lines"
			+ " --minheight -1 --rings -1 --islands 0"
			+ " --inputname "+basename
			+ " --templateinput "+basename+"/projections.xml"
			+ " --templateoutput template.xml"
			+ " --templatexsl /org/contentmine/ami/tools/spssTemplate1.xsl");
		
		/** segment image */
		inputname = "raw";
		new AMIForestPlotTool().runCommands(source + " --inputname " +inputname + " --segment --template "+basename+"/template.xml");

		String sharp = SHARP4 + THRESH + " 150" + DS;
		/** sharpen/threshold segment images */

		String raw_list = ""
		+ " raw.header.tableheads "
		+ " raw.header.graphheads "
		+ " raw.body.table "
		+ " raw.footer.summary "
		+ " raw.footer.scale "
		;

		new AMIImageTool().runCommands(source + " --inputnamelist "
				+ raw_list + " "
				+ sharp
				);

		String raw_s4_thr_150_ds_list = ""
				+ " raw.header.tableheads_s4_thr_150_ds " 
				+ " raw.header.graphheads_s4_thr_150_ds "
				+ " raw.body.table_s4_thr_150_ds "
				+ " raw.footer.summary_s4_thr_150_ds "
				+ " raw.footer.scale_s4_thr_150_ds "
				;
/* comment out for speed? why does the make not work?
		String FORCEMAKE = "";
		String tess = TESSERACT + EXTLINES_HOCR + FORCEMAKE;
		new AMIOCRTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + tess);
		
		String gocr = GOCR + EXTLINES_HOCR + FORCEMAKE;
		new AMIOCRTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + gocr);
		*/
		
		/** analyse OCR output in boxes */
		new AMIForestPlotTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + " --table "+"hocr/hocr.svg" + " --tableType hocr" );
		new AMIForestPlotTool().runCommands(source + " --inputnamelist " + raw_s4_thr_150_ds_list + " --table "+"gocr/gocr.svg" + " --tableType gocr");

		return;
	}

	/** delete a single tree and run the complete stack over it
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testSPSSStataSimpleClean() throws IOException {
		// un/comment to switch Stata <=> spss
//		ForestPlotType fpType = ForestPlotType.spss;
		ForestPlotType fpType = ForestPlotType.stata;
		
		// un/comment to switch project <=> tree
		Scope scope = Scope.TREE;
//		Scope scope = Scope.PROJECT;
		
		String source = createSourceFromProjectAndTree(scope, fpType);
		String TREENAME = ForestPlotType.spss.equals(fpType) ? "PMC5502154/" : "PMC5882397/";
		String THRESH = ForestPlotType.spss.equals(fpType) ? "150" : "150";
		String TEMPLATE_XSL = ForestPlotType.spss.equals(fpType) ? "spssTemplate1" : "stataTemplate1";
		String SUBIMAGE = ForestPlotType.spss.equals(fpType) ? "" : " --subimage statascale y LAST delta 10 projection x";
		String YPROJECT = ForestPlotType.spss.equals(fpType) ? "0.4" : "0.8";
		String XPROJECT = ForestPlotType.spss.equals(fpType) ? "0.7" : "0.6";
		
		String RAW = "raw";
		String SHARPENED    = "s4_"+"thr_"+THRESH+"_ds";
		String SHARPBASE = RAW+"_"+SHARPENED;

// uncomment to start from scratch (for debugging)
// copies new TREE + fulltext.pdf
 		resetCTree(source, TREENAME);
 
		/** =================================== */
		/** process PDFs into svg and pdfimages */
		/** =================================== */
		new AMIPDFTool().runCommands(source);
		
		/** ======================================= */
		/** filter small, duplicates and monochrome */
		/** ======================================= */
		new AMIFilterTool().runCommands(source + " --small small --duplicate duplicate --monochrome monochrome");
		
		/** ================= */
		/** sharpen raw image */
		/** ================= */
		new AMIImageTool().runCommands(source + " --inputname "+RAW+ " "+ SHARP4 + " --threshold " + THRESH + " " + DS);

		/** ============================ */
		/** segment raw image into panels*/
		/** ============================ */
		new AMIPixelTool().runCommands(source
				+ " --projections --yprojection " + YPROJECT + "  --xprojection "+XPROJECT + " --lines"
				+ " --minheight -1 --rings -1 --islands 0"
				+ " --inputname "+SHARPBASE
				+ SUBIMAGE
				+ " --templateinput "+SHARPBASE+"/projections.xml"
				+ " --templateoutput template.xml"
				+ " --templatexsl /org/contentmine/ami/tools/" + TEMPLATE_XSL + ".xsl");

		/** ============================ */
		/** segment raw image into panels*/
		/** ============================ */
		new AMIForestPlotTool().runCommands(source + " --inputname " +RAW + " --segment --template "+SHARPBASE+"/template.xml");

		/** =========================== */
		/** sharpen segmented panels */
		/** =========================== */
		String SHARPEN = SHARP4 + " --threshold "+THRESH + DS;

		String RAW_LIST = ForestPlotType.spss.equals(fpType) ? ""
			+ " raw.header.tableheads "
			+ " raw.header.graphheads "
			+ " raw.body.table "
			+ " raw.footer.summary "
			+ " raw.footer.scale "
		: ""
			+ " raw.header"
			+ " raw.body.ltable"
			+ " raw.body.rtable"
			+ " raw.scale"
		;

		new AMIImageTool().runCommands(source + " --inputnamelist " + RAW_LIST + " " + SHARPEN );

		/** ============================== */
		/** OCR sharpened segmented panels */
		/** ============================== */
		String SHARP_LIST = ForestPlotType.spss.equals(fpType) ? ""
				+ " raw.header.tableheads_"+SHARPENED 
				+ " raw.header.graphheads_"+SHARPENED 
				+ " raw.body.table_"+SHARPENED 
				+ " raw.footer.summary_"+SHARPENED 
				+ " raw.footer.scale_"+SHARPENED 
				: ""
 				+ " raw.header_"+SHARPENED
 				+ " raw.body.ltable_"+SHARPENED
 				+ " raw.body.rtable_"+SHARPENED
 				+ " raw.scale_"+SHARPENED
 				;

/* comment out for speed? why does the make not work? */
		String FORCEMAKE = " --forcemake";
		String TESS_CMD = TESSERACT + EXTLINES_HOCR + FORCEMAKE;
		new AMIOCRTool().runCommands(source + " --inputnamelist " + SHARP_LIST + TESS_CMD);
		
		String GOCR_CMD = GOCR + EXTLINES_HOCR + FORCEMAKE;
		new AMIOCRTool().runCommands(source + " --inputnamelist " + SHARP_LIST + GOCR_CMD);
		
		/** =========================== */
		/** analyse OCR output in boxes */
		/** =========================== */
		new AMIForestPlotTool().runCommands(source + " --inputnamelist " + SHARP_LIST + " --table "+"hocr/hocr.svg" + " --tableType hocr" );
		new AMIForestPlotTool().runCommands(source + " --inputnamelist " + SHARP_LIST + " --table "+"gocr/gocr.svg" + " --tableType gocr");

		return;
	}


	private void resetCTree(String source, String pdfFilename) throws IOException {
		String filename = source.split("\\s+")[1];
		CTree cTree = new CTree(filename);
		CProject cProject = cTree. getOrCreateProject();
		cProject.deleteCTree(cTree);
		copyTreeIntoProject(cProject, pdfFilename);
		return;
	}

	// ========================================

	private void copyTreeIntoProject(CProject cProject, String pdfFilename) throws IOException {
		File srcDir = new File(cProject.getDirectory(), "_original/" + pdfFilename);
		File destDir = new File(cProject.getDirectory(), pdfFilename);
		LOG.debug("copy "+srcDir+" to "+destDir);
		FileUtils.copyDirectory(srcDir, destDir);
	}

//	private void copyPDFIntoProject(CProject cProject, String pdfFilename) throws IOException {
//		FileUtils.copyFile(new File(cProject.getDirectory(), "_original/" + pdfFilename), new File(cProject.getDirectory(), pdfFilename));
//	}

	private void assertImageDirFileExists(String basename, String finalPath) {
		File tree = cTree.getPDFImagesImageDirectories().get(0);
		File file = new File(new File(tree, basename), finalPath);
		Assert.assertTrue(finalPath, file.exists());
	}

	private String createSourceFromProjectAndTree(Scope scope, ForestPlotType type, String treename) {
		return createSourceFromProjectAndTree(scope.getAbbrev(), type, treename);
	}
	
	private String createSourceFromProjectAndTree(String treeOrProject, ForestPlotType type) {
		return createSourceFromProjectAndTree(treeOrProject, type, (String) null);
	}
	
	private String createSourceFromProjectAndTree(Scope scope, ForestPlotType type) {
		return createSourceFromProjectAndTree(scope.getAbbrev(), type);
	}



	private String createSourceFromProjectAndTree(String treeOrProject, ForestPlotType type, String treename) {
		if (type == null) return null;
		if (type.equals(ForestPlotType.stata)) {
			if (treename == null) {
				treename = PMC5882397;
			}
			return createSourceFromProjectAndTree(treeOrProject, STATA_SIMPLE_DIR, treename);
		}
		if (type.equals(ForestPlotType.spss)) {
			if (treename == null) {
				treename = PMC5502154;
			}
			return createSourceFromProjectAndTree(treeOrProject, SPSS_SIMPLE_DIR, treename);
		}
		return null;
	}

	private String createSourceFromProjectAndTree(String treeOrProject, File projectDir, String treename) {
		cProject = new CProject(projectDir);
		cTree = new CTree(new File(projectDir, treename));
		String source = "-t".equals(treeOrProject) ? "-t "+cTree.getDirectory() : "-p "+cProject.getDirectory();
		return source;

	}

	private void align(String s1, String s2) {
		LevenshteinDistanceAligment<Character> align01 =
                LevenshteinDistanceAligment.createAlignment(s1, s2);
		System.out.println();
        System.out.println("Levenshtein distance = " + align01.getAlignment());
        System.out.println("Levenshtein distance = " + align01.getDistance());
        System.out.println("           " + s1);
        System.out.println("Alignment: " + align01.getAlignmentString());
        System.out.println("           " + s2);
	}

	
	private void extractPlots(String plotType, String treename, boolean useTree) {
		File projectDir = STATA.equals(plotType) ? STATA_DIR : SPSS_DIR;
		
		CTree cTree = new CTree(new File(projectDir, treename));
		CProject cProject = new CProject(projectDir);
		AbstractAMITool ocrTool = new AMIOCRTool();

		String source = useTree ? "--ctree "+cTree.getDirectory() : "--cproject "+cProject.getDirectory();
		ocrTool.runCommands(source + EXTLINES_GOCR);
	}
	
	private void analyzePlots(String plotType, String treename, boolean useTree) {
		File projectDir = STATA.equals(plotType) ? STATA_DIR : SPSS_DIR;
		
		CTree cTree = new CTree(new File(projectDir, treename));
		CProject cProject = new CProject(projectDir);
		AMIForestPlotTool forestPlotTool = new AMIForestPlotTool();
		String source = useTree ? "--ctree "+cTree.getDirectory() : "--cproject "+cProject.getDirectory();
		String cmd = ""
			+ source
//		    + " --plottype " + plotType
//		    + " --hocr=true"
		    + "";
		forestPlotTool.runCommands(cmd);
	}
	


}
