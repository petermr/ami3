package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Assert;
import org.junit.Test;

/** test AMIProcessorPDF
 * 
 * @author pm286
 *
 */
public class AMIPDFTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AMIPDFTest.class);
public final static File PDF2SVG2TEST = new File(PDF2SVG2, "text");
	
	@Test
	/** reads UCL corpus as PDFs and creates first pass SVG , images and scholarly html
	 * 
	 */
	public void testForestPlotsSmall() throws Exception {

//		log4j.appender.file=org.apache.log4j.DailyRollingFileAppender
//				log4j.appender.file.File=${user.home}/logs/app.log
//				log4j.appender.file.layout=org.apache.log4j.PatternLayout
//				log4j.appender.file.layout.ConversionPattern=%d [%t] %c %p %m%n
//		String filename = "foo";
//		boolean append = true;
//		Layout layout = new PatternLayout();
//		Appender appender = new FileAppender(layout, filename, append);
		String args = 
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " pdfbox"
				//+ " --rawfiletypes pdfbox" // --rawfiletypes is an option in `ami download`, not of `ami pdfbox`
				+ " --maxpages 20"
				+ " --no-pdfimages"
				+ " --svgpages"  // no parameter, use --no-svgpages to switch this off
				;
//		Assert.assertTrue(new File(args[1]).exists());
		AMIPDFTool amiProcessorPDF = AMI.execute(AMIPDFTool.class, args);
		CProject cProject = amiProcessorPDF.getCProject();
		Assert.assertNotNull("CProject not null", cProject);
	}

	@Test
	/** reads UCL corpus as PDFs and creates first pass SVG , images and scholarly html
	 * 
	 */
	public void testForestPlotsSmallSVG() throws Exception {
		String projectDir = "/Users/pm286/workspace/uclforest/forestplotssmall";
		// delete the existing svg/ directories
//		new AMICleanTool().runCommands("-p " + projectDir + " --dir svg/");
		// and then recreate them
		String args = 
				"-p " + projectDir
				+ " pdfbox"
//				+ " --rawfiletypes pdfbox"
				+ " --maxpages 20"
				+ " --no-pdfimages"
				+ " --svgpages"  // no parameter, use --no-svgpages to switch this off
				;
//		Assert.assertTrue(new File(args[1]).exists());
		
//		AMIPDFTool amiProcessorPDF = new AMIPDFTool();
//		amiProcessorPDF.runCommands(args);
		AMIPDFTool amiPdf = AMI.execute(AMIPDFTool.class, args);

//		CProject cProject = amiProcessorPDF.getCProject();
//		Assert.assertNotNull("CProject not null", cProject);
	}

	@Test
	/** reads UCL corpus as PDFs and creates first pass SVG , images and scholarly html
	 * 
	 */
	public void testForestPlotsSmallSVG1() throws Exception {
		String projectDir = "/Users/pm286/workspace/uclforest/forestplotssmall";
		String args = 
				"-p " + projectDir
				+ " pdfbox"
				//+ " --rawfiletypes pdfbox" // --rawfiletypes is an option in `ami download`, not of `ami pdfbox`
				+ " --maxpages 20"
				+ " --no-pdfimages"
				+ " --svgpages"  // no parameter, use --no-svgpages to switch this off
				;
		AMIPDFTool amiPdf = AMI.execute(AMIPDFTool.class, args);
	}

	@Test
	/** reads UCL corpus as PDFs and creates first pass PDFImages
	 * 
	 */
	public void testForestPlotsSmallPDFImages() throws Exception {
		String projectDir = "/Users/pm286/workspace/uclforest/forestplotssmall";
		// delete the existing pdfimagesdirectories
//		new AMICleaner().runCommands("-p " + projectDir + " --dir pdfimages/");
		// and then recreate them
		String args =
				"-p " + projectDir
				+ " pdfbox"
				//+ " --rawfiletypes pdfbox" // --rawfiletypes is an option in `ami download`, not of `ami pdfbox`
				+ " --maxpages 20"
				+ " --pdfimages"  // no parameter, use --no-pdfimages to switch this off
				+ " --no-svgpages"
				;
		AMI.execute(args);
	}

	@Test
	/** 
	 * convert whole project; 
	 */
	public void testDevProject() throws Exception {
		String args = ""
				+ "-p /Users/pm286/workspace/uclforest/dev/"
				+ " pdfbox"
				+ " --pdfimages" // no parameter, use --no-pdfimages to switch this off
			;
		AMI.execute(args);
	}


	@Test
	/** 
	 * pearson did not emit vectors for page 18
	 * FIXED kludged 
	 */
	public void testVectorBug() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/dev/pearson"
				+ " --forcemake"
				+ " pdfbox"
			;
		AMI.execute(args);
	}

	@Test
	/** 
	 * campbell has implicit Move - not properly treated yet
	 */
	public void testVectorBugCampbell() throws Exception {
		String args = ""
				+ "-t /Users/pm286/workspace/uclforest/dev/campbell"
				+ " --forcemake"
				+ " pdfbox"
			;
		AMI.execute(args);
	}


	@Test
	/** 
	 * this has very long paths and hangs with resource problems.
	 * The maxprimitives truncates these paths (thus losing data)
	 * but allows the job to complete. 
	 * 
	 */
	public void testLargeVector() throws Exception {
		String args = ""
				+ "-p "+CANADA+"/test"
				+ " --forcemake"
				+ " pdfbox"
				+ " --maxprimitives 10000"
			;
		AMI.execute(args);
	}

	@Test
	/** new framework using AbstractPageParser.
	 * 
	 * @throws IOException
	 */
	public void testPDF2Framework() throws IOException {
		
		String args = "-p " + new File(PDF2SVG2, "test")
				+ " --forcemake"
				+ " pdfbox"
				+ " --maxprimitives 10000"
				+ " --debug AMI_TWO"
				;
      
		AMI.execute(args);
//	    ParserDebug parserDebug = ParserDebug.ORIGINAL;
//		parserDebug = ParserDebug.AMI_MEDIUM;
//        int pageSerial = 0;
//        runPageDrawer(root, file, pageSerial, parserDebug, true);

	}

	@Test
	/** .
	 * 
	 * @throws IOException
	 */
	public void testPDFBookChess() throws IOException {
		
		String args = "-t " + new File(NEW_PROJECTS, "chess/")
				+ " --forcemake"
				+ " pdfbox"
				+ " --maxprimitives 10000"
				+ " --debug AMI_TWO"
				;
      
		AMI.execute(args);
		
	}
	
	@Test
	public void testPDF2HTML() {
		File targetDir = new File("target/medrxiv/project");
		File sourceDir = new File(SRC_TEST_AMI, "medrxiv/project");
		System.err.println(">>"+sourceDir.getAbsolutePath());
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String args = "-p " + targetDir
				+ " pdfbox"
				+ " --pdf2html"
				;
      
		AMI.execute(args);
		
	}

	

}
