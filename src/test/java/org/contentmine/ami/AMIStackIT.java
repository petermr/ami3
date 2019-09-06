package org.contentmine.ami;

import java.io.File;

import org.contentmine.ami.tools.AMIImageTool;
import org.contentmine.ami.tools.AMICleanTool;
import org.contentmine.ami.tools.AMIMakeProjectTool;
import org.contentmine.ami.tools.AMIPDFTool;
import org.contentmine.ami.tools.AMIPixelTool;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Test;

public class AMIStackIT {

	@Test 
	public void testUCLForestMakeprojectIT() {
		String project = "uclforestopen";
		File targetDir = new File(AMIFixtures.TARGET_AMISTACK_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(
				new File(AMIFixtures.TEST_AMISTACK_DIR, project),
				targetDir);
		String cmd = "--cproject "+ targetDir + " --rawfiletypes " + "pdf" + " -vv";
		AMIMakeProjectTool amiMakeProject = new AMIMakeProjectTool();
		amiMakeProject.runCommands(cmd);
		
	}
	
	@Test 
	public void testUCLForestPDFIT() {
		String cmd;
		String project = "uclforestopen";
		File targetDir = new File(AMIFixtures.TARGET_AMISTACK_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(
				new File(AMIFixtures.TEST_AMISTACK_DIR, project),
				targetDir);
		
//		makeProject(targetDir);
		cmd = "--cproject "+ targetDir ;
		AMIPDFTool readPDF = new AMIPDFTool();
		readPDF.runCommands(cmd);
		
		
	}

	@Test 
	// SHOWCASE
	public void testUCLForestSmall() {
		
		String cproject = "/Users/pm286/workspace/uclforest/forestplotssmall/";
		// clean the target directories 
//		new AMICleaner().runCommands("--help");
		new AMICleanTool().runCommands(" --cproject " + cproject + " --dir svg/ pdfimages/ --file scholarly.html");
		
		// convert PDF to SVG and images
//		new AMIProcessorPDF().runCommands("-h");
		new AMIPDFTool().runCommands(" --cproject " + cproject);
				
		// create binarized images; 
//		new AMIBitmap().runCommands("-h");
		new AMIImageTool().runCommands(" --cproject " + cproject);
		
		// analyze bitmaps and create PixelIslands; doesn't yet create output
//		new AMIPixel().runCommands("-h");
		new AMIPixelTool().runCommands(" --cproject " + cproject + " --rings 3");
		
	}

	@Test 
	// SHOWCASE
	public void testUCLForestLarge() {
		
		String cproject = "/Users/pm286/workspace/uclforest/forestplots/";
		new AMICleanTool().runCommands(" --cproject " + cproject + " --dir svg/ pdfimages/ --file scholarly.html");
		new AMIPDFTool().runCommands(" --cproject " + cproject);
		new AMIImageTool().runCommands(" --cproject " + cproject);
		new AMIPixelTool().runCommands(" --cproject " + cproject + " --rings 3");
		
	}

	@Test
	public void testSingleTree() {
		// re-make project - will include a new file (first pass only)
		String cproject = "/Users/pm286/workspace/uclforest/dev";
		new AMIMakeProjectTool().runCommands("--cproject " + cproject + " --rawfiletypes pdf ");
		
		String ctree = cproject + "/" + "higgins";
		
		new AMIPDFTool().runCommands(" --ctree " + ctree);
		new AMIImageTool().runCommands(" --ctree " + ctree);
		new AMIPixelTool().runCommands(" --ctree " + ctree + " --rings 3");
		
	}
	
	private void makeProject(File targetDir) {
		new AMIMakeProjectTool().runCommands("--cproject "+ targetDir + " --rawfiletypes " + "pdf" + " -vv");
	}
	

}
