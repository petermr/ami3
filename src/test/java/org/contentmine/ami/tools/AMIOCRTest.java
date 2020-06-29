package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.image.ocr.GOCRConverter;
import org.junit.Test;

/** test OCR.
 * 
 * @author pm286
 *
 */
public class AMIOCRTest extends AbstractAMIImageTest /*AbstractAMITest*/ {
	public static final Logger LOG = LogManager.getLogger(AMIOCRTest.class);
	AMIOCRTool amiOCRTool;
	
	@Test
	/** 
	 * convert single (good) file
	 */
	public void testHelp() throws Exception {
		String args = 
			"-t /Users/pm286/workspace/uclforest/dev/shenderovich -v"
			+ " ocr --html true"
			;
		AMI.execute(args);
	}
	
	@Test
	/** 
	 * convert single (good) file
	 */
	public void testHOCR() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/dev/shenderovich ocr ";
		AMI.execute(args);
	}
	
	@Test
	/** 
	 * convert single (moderate) tree
	 */
	public void testHOCR1() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/dev/buzick ocr"
				+ " --html true"
			;
		AMI.execute(args);
	}
	
	@Test
	/** convert whole project
	 * most files are too low resolution to convert well
	 * 
	 * @throws Exception
	 */
	public void testHOCRProject() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/dev ocr --html true"
				;
		AMI.execute(args);
	}
	
	@Test
	/** scale small text
	 * 
	 * @throws Exception
	 */
	public void testForceScale() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/dev/case ocr"
				+ " --html true"
				+ " --scalefactor 2.0"
				;
		AMI.execute(args);
	}
	
	@Test
	/** scale small text
	 * 
	 * @throws Exception
	 */
	public void testForceScaleProject() throws Exception {
		String args = ""
//				+ "-p /Users/pm286/workspace/uclforest/dev"
				+ "-p /Users/pm286/projects/uclforest/dev"
				+ " ocr"
				+ " --html true"
				+ " --scalefactor 1.7"
				+ " --filename scale1_7"
				;
		AMI.execute(args);
	}
	
	@Test
	/** scale small text
	 * 
	 * @throws Exception
	 */
	public void testScaleMaxsize() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/dev/case"
				+ " --html true"
				+ " --maxsize 700"
				+ " --scaled maxsize"
				;
		AMI.execute(args);
	}
	
	@Test
	/** scale small text
	 * 
	 * @throws Exception
	 */
	public void testScaleMaxsizeProject() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/dev/"
				+ " ocr"
				+ " --html true"
				+ " --maxsize 700"
				+ " --filename maxsize700"
				;
		AMI.execute(args);
	}

	
	@Test
	/** scale small text
	 * 
	 * @throws Exception
	 */
	public void testScaleOCR() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/devtest/case_systematic_review_ar"
//				+ "-t /Users/pm286/workspace/uclforest/devtest/case_systematic_review_ar"
				+ " --inputname raw"
				+ " ocr"
//				+ " --help"
				+ " --html true"
//				+ " --maxsize 700"
//				+ " --filename maxsize700"
//				+ " --filename=foobar"
				+ " --tesseract=/usr/local/bin/tesseract"
                + " --scalefactor 2.0"
				;
		AMI.execute(args);
	}
	
	@Test
	/** scale small text
	 * 
	 * @throws Exception
	 */
	public void testScaleOCRProject() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/devtest/"
				+ " ocr"
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
		AMI.execute(args);
	}
	
	@Test
	/**
	 * reads already processed images and extracts OCR labels
	 * 
	 *
	 */
	public void testBatteryGraph2018() {
		CTree cTree = new CTree(new File(NormaFixtures.TEST_IMAGES_DIR, "ocr/battery"));
		LOG.debug("ctree "+cTree);
		String cmd = " --ctree "+cTree.getDirectory()+""
					+ " --inputname raw"
					+ " ocr"
					+ " --html true"
					+ " --tesseract=/usr/local/bin/tesseract"
	                + " --scalefactor 2.0"
					;
			AMI.execute(cmd);
			
	}

//	@Test
//	/**
//	 * reads already processed images and extracts OCR labels
//	 * 
//	 *
//	 */
//	public void testBatteryGraph2020() {
//		CTree cTree = new CTree(new File(NormaFixtures.TEST_IMAGES_DIR, "ocr/battery"));
//		LOG.debug("ctree "+cTree);
//		String cmd = " --ctree "+cTree.getDirectory()+""
//					+ " --inputname raw"
//					+ " ocr"
//					+ " --html true"
//					+ " --tesseract=/usr/local/bin/tesseract"
//	                + " --scalefactor 2.0"
//					;
//			AMI.execute(cmd);
//			
//	}


	@Test
	public void testSPSS() {
		File projectDir = new File("/Users/pm286/projects/forestplots/spss");
//		CTree cTree = new CTree(new File(projectDir, "PMC5502154"));
		CProject cProject = new CProject(projectDir);
//		AbstractAMITool ocrTool = new AMIOCRTool();

//		String cmd = "--ctree "+cTree.getDirectory();
		String cmd = "--cproject "+cProject.getDirectory();
//		cmd += " --inputname raw_sc_2_s4_b_10_thr_150";
//		cmd += " --outputname raw_sc_2_s4_b_10_thr_150";
		cmd += " --inputname raw_s4_b_10_thr_180";
		cmd += " --outputname raw_s4_b_10_thr_180";
				
//		LOG.debug(cmd);
//		ocrTool.runCommands(cmd);
		AMI.execute(cmd);
	
	}
	
	@Test
	public void testGOCRa() throws Exception {
		File projectDir = new File("/Users/pm286/projects/forestplots/spssSimple");
		CTree cTree = new CTree(new File(projectDir, "PMC5502154"));
		File pdfImageDir = new File(cTree.getDirectory(), "pdfimages/");
		File imageDir = new File(pdfImageDir, "image.4.3.96_553.569_697/");
		File inputFile = new File(imageDir, "raw.png");

		File gocrXmlFile = new File(imageDir, "raw.gocr.xml");
		GOCRConverter gocrConverter = new GOCRConverter(null);
		gocrConverter.createGOCRElement(inputFile, gocrXmlFile);
		boolean glyphs = true;
		SVGElement svgElement = gocrConverter.createSVGElementWithGlyphs(imageDir, glyphs);
		File svgFile = new File(imageDir, "raw.gocr.svg");
		SVGSVG.wrapAndWriteAsSVG(svgElement, svgFile);	
		
		if (false) {
			gocrConverter.createMaps(svgElement);
			// not yet working well
//			gocrConverter.correlateImagesForGlyphs();
		}

	}
	
	@Test
	public void testGOCR() throws Exception {
		File projectDir = new File("/Users/pm286/projects/forestplots/spssSimple");
		CProject cProject = new CProject(projectDir);
		CTree cTree = new CTree(new File(projectDir, "PMC5502154"));
		AbstractAMITool ocrTool = new AMIOCRTool();
		String cmd = ""
				+ "--cproject "+cProject.getDirectory()
				+ " ocr"
				+ " --gocr /usr/local/bin/gocr"
				+ " --html false"
		;
		LOG.debug(cmd);
		ocrTool.runCommands(cmd);

	}

	@Test
	public void testGOCRBasename() throws Exception {
		File projectDir = new File("/Users/pm286/projects/forestplots/spssSimple");
		CProject cProject = new CProject(projectDir);
		CTree cTree = new CTree(new File(projectDir, "PMC5502154"));
		AbstractAMITool ocrTool = new AMIOCRTool();
		String cmd = ""
				+ "--cproject "+cProject.getDirectory()
				+ " --gocr /usr/local/bin/gocr"
				+ " --html false"
				+ " --inputname raw.table.body"
		;
		LOG.debug(cmd);
		ocrTool.runCommands(cmd);

	}

	@Test
	public void testGOCRSharpen() throws Exception {
		File projectDir = new File("/Users/pm286/projects/forestplots/spssSimple");
		CProject cProject = new CProject(projectDir);
		String imageBaseCmd = ""
				+ "--cproject "+cProject.getDirectory()
				+ " --sharpen sharpen4"
				+ " --threshold 180"
				;
		;
		new AMIImageTool().runCommands(imageBaseCmd + " --inputname " + "raw.table.header");
		new AMIImageTool().runCommands(imageBaseCmd + " --inputname " + "raw.table.body");
		new AMIImageTool().runCommands(imageBaseCmd + " --inputname " + "raw.graph.header");
		new AMIImageTool().runCommands(imageBaseCmd + " --inputname " + "raw.graph.footer");
		
		AbstractAMITool ocrTool = new AMIOCRTool();
		String ocrBaseCmd = "--cproject "+cProject.getDirectory()+" --gocr /usr/local/bin/gocr --html false";

		new AMIOCRTool().runCommands(ocrBaseCmd + " --inputname raw.graph.footer_s4_b_10_thr_180");
		new AMIOCRTool().runCommands(ocrBaseCmd + " --inputname raw.graph.header_s4_b_10_thr_180");
		new AMIOCRTool().runCommands(ocrBaseCmd + " --inputname raw.table.footer_s4_b_10_thr_180");
		new AMIOCRTool().runCommands(ocrBaseCmd + " --inputname raw.table.body_s4_b_10_thr_180");

	}

	@Test
	public void testGOCRTesseract() throws Exception {
		File projectDir = new File("/Users/pm286/projects/forestplots/spssSimple");
		CProject cProject = new CProject(projectDir);
		File cTreeDirectory = new File(projectDir, "PMC5502154");
		CTree cTree = new CTree(cTreeDirectory);
		String cProjectArg = "--cproject "+cProject.getDirectory();
		String cTreeArg = "--ctree "+cTree.getDirectory();
		boolean useTree = 
				false
//				true
				;
		String source = (useTree) ? cTreeArg : cProjectArg;
		String sharpenCmd = ""
				+ source
				+ " --sharpen sharpen4"
				+ " --threshold 180"
				+ " --despeckle true"
				;
		;

		String raw = "raw";
		String rawS4Thr180 = "raw_s4_b_10_thr_180";
		if (false /*|| true*/) {
			new AMIImageTool().runCommands(sharpenCmd + " --inputname " + "raw");
			
			String gocrBaseCmd = source +" --gocr /usr/local/bin/gocr --html false";
			new AMIOCRTool().runCommands(gocrBaseCmd + " --inputname " + raw);
			new AMIOCRTool().runCommands(gocrBaseCmd + " --inputname " + rawS4Thr180);
	
			String hocrBaseCmd = source+" --tesseract /usr/local/bin/tesseract --html true";
			new AMIOCRTool().runCommands(hocrBaseCmd + " --inputname "+raw);
			new AMIOCRTool().runCommands(hocrBaseCmd + " --inputname "+rawS4Thr180);
		}

		File pdfImageDir = cTree.getExistingPDFImagesDir();
		File baseImageDir = new File(pdfImageDir, "image.4.3.96_553.569_697");
		File processedImageDir = new File(baseImageDir, rawS4Thr180);
		LOG.debug("image: "+processedImageDir);
		
		
		File gocrSVGFile = new AMIOCRTool().processGOCR(processedImageDir);		
		File hocrSVGFile = new AMIOCRTool().processHOCR(processedImageDir);
		
		if (hocrSVGFile != null || gocrSVGFile != null) {
		}
	}

	/**
	 * 
	 */
	@Test
	public void testBattery2020() {
		File targetDir = new File("target/battery10");
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		String cmd = " --cproject "+targetDir+""
					+ " --inputname raw"
					+ " ocr"
					+ " --html true"
					+ " --tesseract=/usr/local/bin/tesseract"
	                + " --scalefactor 2.0"
					;
			AMI.execute(cmd);
	}
	
	/**
	 * 
	 */
	@Test
	public void testBattery2020PreIntegrationIT() {
		File targetDir = new File("target/ocr/battery10/");
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		/** clean all previous searches */
		boolean process = true;
		if (process) {
		AMI.execute("-p " + targetDir 
				+ " clean"
				+ " **/pdfimages **/svg **/results **/sections **/tei"
				+ " **/search.* **/word.*"
				+ " word.*"
				);
		AMI.execute("-p " + targetDir + " -v " + " pdfbox ");
		AMI.execute("-p " + targetDir + " -v " + " filter --small small --duplicate duplicate --monochrome monochrome");
		}
		AMI.execute("-p " + targetDir + " -v " + " --inputname " + " raw " + " image --posterize 16"); 
		
		AMI.execute(" -p "+targetDir+""
					+ " --inputname raw"
					+ " ocr"
					+ " --html true"
					+ " --tesseract=/usr/local/bin/tesseract"
	                + " --scalefactor 2.0")
					;
	}
	
	/**
	 * 
	 * single commandline to go from projects -> OCR
	 */
	@Test
	public void testBattery2020IntegrationIT() {
		File targetDir = new File("target/ocr/battery10/");
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		/** clean all previous searches */
		AMI.execute(""
			+ "-p " + targetDir 
			+ " --inputname " + " raw " // needed for posterize
			+ "-v "
			+ ""				// cleans all non-essential subdirectories and files
			+ " clean"
			+ " **/pdfimages **/svg **/results **/sections **/tei"
			+ " **/search.* **/word.*"
			+ " word.*"
			+ ""				//extracts text and images
			+ " pdfbox "
			+ ""				// filters small and duplicate images
			+ " filter --small small --duplicate duplicate --monochrome monochrome"
			+ ""				// "flattens colours to 16
			+ " image --posterize 4"
			+ ""
			+ " ocr"			// OCR using tesseract
			+ " --html true --tesseract=/usr/local/bin/tesseract --scalefactor 2.0"
			);
		/** one the commandline, omitting "clean" and with defaults and properties/config 
		 * this could be:
ami -p <targetDir> --inputname raw -v pdfbox filter -sdm image --posterize 16 ocr "
		 * 
		 */
	}

	@Test
	public void testObjectOCR() {
		// this is roughly equivalent to:
// ami 
//	--input src/test/resources/org/contentmine/ami/battery10/PMC3463005/pdfimages/image.3.2.81_514.571_735/raw.png 
// ocr		
//	 --html true --tesseract=/usr/local/bin/tesseract --scalefactor 2.0
		
				 
		this
		.setAMITestProjectName("battery10")
		.setTreeName("PMC3463005")
		.setImageDirName("image.3.2.81_514.571_735")
		.setImageName("raw")
		.assertCanReadFile(this.getImageFile() + " input", this.getImageFile(), 100)
		.assertTrue("msg",getImageFile().toString().endsWith(
			"battery10/PMC3463005/pdfimages/image.3.2.81_514.571_735/raw90.png"))
		.readImage()
		.ocr()
		    .setTesseractPath("/usr/local/bin/tesseract")
		    .setScale(2.0)
		    .run()
		.assertCanReadFile("hocr.html", new File(this.getImageDir(), "raw/hocr/hocr.html"), 100)
		;
	}
	
	@Test
	public void testReviewOCR1() {
		this.createHocr("battery10", "PMC3463005", "image.6.2.86_509.389_714", "raw", 1.0);
	}

	@Test
	public void testReviewOCRCorner() {
		this.createHocr("battery10", "PMC3463005", "image.6.2.corner", "raw", 1.0);
	}

	@Test
	public void testReviewOCRCTree() {
		this.createHocrGlob("battery10", "**/*/image*/raw.png", 1.0);
		// and create captions?
		AMI.execute(""
			+ "-p " + TEST_BATTERY10
			+ " section")
			;

		
	}
	
	

	// =====================================================
	private void createHocrGlob(String projectName, String fileGlob, double scale) {
		List<File> imageFiles = 
		this
		.setAMITestProjectName(projectName)
		.runFileGlob(fileGlob);
		for (File file : imageFiles) {
			this.setImageFile(file)
//			.assertCanReadFile(this.getImageFile() + " input", this.getImageFile(), 100)
			.readImage() 
			.ocr()
			    .setTesseractPath("/usr/local/bin/tesseract")
			    .setScale(scale)
			    .run()
//			.assertCanReadFile("hocr.html", new File(this.getImageDir(), "raw/hocr/hocr.html"), 100)
			;
			LOG.info(this.getOutputFile());
		}
	}
	
	private void createHocr(String projectName, String treeName, String imageDirName, String root, double scale) {
		this
		.setAMITestProjectName(projectName)
		.setTreeName(treeName)
		.setImageDirName(imageDirName)
		.setImageName(root)
		.assertCanReadFile(this.getImageFile() + " input", this.getImageFile(), 100)
		.assertTrue("msg",getImageFile().toString().endsWith(imageDirName+"/raw.png"))
		.readImage()
		.ocr()
		    .setTesseractPath("/usr/local/bin/tesseract")
		    .setScale(scale)
		    .run()
		.assertCanReadFile("hocr.html", new File(this.getImageDir(), "raw/hocr/hocr.html"), 100)
		;
		LOG.info(this.getOutputFile());
	}
	
	// =======================================
	
	private AMIOCRTest setScale(double scale) {
		checkAMIOCRTool();
		amiOCRTool.setScale(scale);
		return this;
	}

	private AbstractAMITest run() {
		checkAMIOCRTool();
		amiOCRTool.runOCR(imageFile);
		return this;
	}

	private AbstractAMITest setHtml(boolean b) {
		checkAMIOCRTool();
		amiOCRTool.setHtml(b);
		return this;
	}

	private AMIOCRTest setTesseractPath(String tesseractPath) {
		checkAMIOCRTool();
		amiOCRTool.setTesseractPath(tesseractPath);
		return this;
	}

	protected AbstractAMITest forcemake() {
		checkAMIOCRTool();
		amiOCRTool.setForceMake(true);
		return this;
	}

	// ======================================
	
	

	private AbstractAMITest setMethod(String path) {
		checkAMIOCRTool();
		if (path.indexOf("tesseract") != 1) {
			amiOCRTool.setTesseractPath(path);
		}
		return (AbstractAMITest) this;
	}
	
	void checkAMIOCRTool() {
		if (amiOCRTool == null) {
			throw new RuntimeException(" must create OCRTool (use ocr())");
		}
	}



	// =================delegates=====================
	@Override
	protected AMIOCRTest setAMITestProjectName(String projectName) {
		return (AMIOCRTest) super.setAMITestProjectName(projectName);
	}

	@Override
	protected AMIOCRTest setTreeName(String treeName) {
		return (AMIOCRTest) super.setTreeName(treeName);
	}

	protected AMIOCRTest setImageName(String root) {
		return (AMIOCRTest) super.setImageName(root);
	}

	@Override
	protected AMIOCRTest assertTrue(String msg, boolean condition) {
		return (AMIOCRTest) super.assertTrue(msg, condition);
	}

	@Override
	protected AMIOCRTest assertEquals(String msg, Object expected, Object actual) {
		return (AMIOCRTest) super.assertEquals(msg, expected, actual);
	}

	@Override
	protected AMIOCRTest assertCanReadFile(String msg, File file, long minFileSize) {
		return (AMIOCRTest) super.assertCanReadFile(msg, file, minFileSize);
	}

	@Override
	protected AMIOCRTest setImageDirName(String imageDirName) {
		return (AMIOCRTest) super.setImageDirName(imageDirName);
	}
	
	@Override
	protected AMIOCRTest readImage() {
		return (AMIOCRTest) super.readImage();
	}
	
	@Override
	protected AMIOCRTest writeImage(String type) {
		return (AMIOCRTest) super.writeImage(type);
	}

	@Override
	protected AMIOCRTest setImageFile(File file) {
		return (AMIOCRTest) super.setImageFile(file);
	}


//	@Override
//	protected AMIOCRTest runFileGlob(String glob) {
//		return (AMIOCRTest) super.runFileGlob(glob);
//	}

	// ====== local ===========
//	--html true --tesseract=/usr/local/bin/tesseract --scalefactor 2.0"
	protected AMIOCRTest ocr() {
		checkImage();
		amiOCRTool = new AMIOCRTool();
		return (AMIOCRTest) this;
	}

	
	// ===============================
	
	public File getPdfImageDir() {
		return pdfImageDir;
	}

	public File getImageDir() {
		return imageDir;
	}

	protected File getImageFile() {
		checkImageFile();
		return imageFile;
	}

	public BufferedImage getImage() {
		return image;
	}


	// ======================================
	
	
	
}
