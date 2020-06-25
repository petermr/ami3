package org.contentmine.ami;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.svg2xml.PDF2SVGConverterWrapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WorkbenchTest {
	private static final Logger LOG = LogManager.getLogger(WorkbenchTest.class);
	private File targetDir;
@Before
	public void setup() {
		targetDir = CMineTestFixtures.createCleanedCopiedDirectory(
			AMIFixtures.TEST_WORKBENCH_MOSQUITO, new File("target/workbench/mosquitos"));
	}

	@Test
	/** makes project from PDF files in a directory.
	 * 
	 */
	
	public void testMakeProjectFromPDFs() {
		Assert.assertTrue("directory", AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF.isDirectory());
		Assert.assertNotNull(AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF.listFiles());
		Assert.assertEquals(3, AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF.listFiles().length);
		targetDir = CMineTestFixtures.createCleanedCopiedDirectory(
				AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF, new File("target/workbench/mosquitosPDF"));
		CProject mosquitoProject = new CProject(targetDir);
		CTreeList mosquitoTreeList = mosquitoProject.getOrCreateCTreeList();
		Assert.assertEquals("no CTrees yet", 0, mosquitoTreeList.size());
		String cmd = "--project "+targetDir+" --makeProject (\\1)/fulltext.pdf --fileFilter .*/(.*)\\.pdf";
		new Norma().run(cmd);
		mosquitoProject = new CProject(targetDir);
		mosquitoTreeList = mosquitoProject.getOrCreateCTreeList();
		Assert.assertEquals("CTrees after makeProject", 3, mosquitoTreeList.size());
		
	}
	
	/** make project and use as default in commandline.
	 * 
	 */
	@Test
	public void testMakeProjectFromPDFsPDFBox2() {
		Assert.assertTrue("directory", AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF.isDirectory());
		Assert.assertNotNull(AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF.listFiles());
		Assert.assertEquals(3, AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF.listFiles().length);
		targetDir = CMineTestFixtures.createCleanedCopiedDirectory(
				AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF, new File("target/workbench/mosquitosPDF"));
		CProject mosquitoProject = new CProject(targetDir);
		CTreeList mosquitoTreeList = mosquitoProject.getOrCreateCTreeList();
		Assert.assertEquals("no CTrees yet", 0, mosquitoTreeList.size());
		String cmd = "--project "+targetDir+ " --makeProject (\\1)/fulltext.pdf --fileFilter .*/(.*)\\.pdf";
		new Norma().run(cmd);
		mosquitoProject = new CProject(targetDir);
		mosquitoTreeList = mosquitoProject.getOrCreateCTreeList();
		Assert.assertEquals("CTrees after makeProject", 3, mosquitoTreeList.size());
		
	}
	
	@Test
	/** makes project from PDF files in a directory.
	 * uses WorkbenchTest.akeAndTestCProjectFromPDFs 
	 * 
	 */
	
	public void testMakeAndTestCProjectFromPDFs() {
		File testDir = AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF;
		File targetDir = new File("target/workbench/mosquitosPDF");
		Norma norma = WorkbenchTest.makeAndTestCProjectFromPDFs(testDir, targetDir, 3);
		CTreeList cTreeList = norma.getOrCreateCProject().getOrCreateCTreeList();
		Assert.assertEquals("CTrees ", 3,  cTreeList.size());
	}

	@Test
	/** makes SVG from PDF files in a directory.
	 */
	public void testMakeSVGFromPDFs() {
		File testDir = AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF;
		File targetDir = new File("target/workbench/mosquitosPDFSVG/");
		Norma norma = WorkbenchTest.makeAndTestCProjectFromPDFs(testDir, targetDir, 3);
		String cmd = " --project "+targetDir+ " --transform pdf2svg "+" --input fulltext.pdf "+
		     " --outputDir "+targetDir;
		norma.run(cmd);
		
	}

	@Test
	/** makes compact SVG from PDF files in a directory.
	 */
	public void testMakeCompactSVGFromPDFs() {
		File testDir = AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF1;
		File targetDir = new File("target/workbench/mosquitosPDFSVG/");
		CMineTestFixtures.cleanAndCopyDir(testDir, targetDir);
		File targetDirSVG = new File("target/workbench/mosquitosPDFSVGCompact/"); // doesn't work , need to copy
		Norma norma = new Norma();
		targetDirSVG.mkdirs();
		norma = WorkbenchTest.makeAndTestCProjectFromPDFs(testDir, targetDir, /*3*/1);
		String cmd = " --project "+targetDir+ " --transform pdf2svg "+" --input fulltext.pdf "+
			     " --outputDir "+targetDirSVG;
		norma.run(cmd);

		// does not use outputDir properly
		String cmd1 = " --project "+targetDir+ " --transform compactsvg "+
		    " --fileFilter .*/svg/fulltext-page\\d+\\.svg" /* + " --outputDir "+targetDirSVG*/;
		norma.run(cmd1);		
	}
	
	@Test
	/** fails?
	 * 
	 */
	public void testMakeCompactSVGDirectly() {
		PDF2SVGConverterWrapper wrapper = new PDF2SVGConverterWrapper();
		File testDir = AMIFixtures.TEST_WORKBENCH_MOSQUITO_PDF;
		File targetDir = new File("target/workbench/mosquitosPDFSVGCompact1/");
		File targetDir1 = new File("target/workbench/mosquitosPDFSVGCompact11/");
		CMineTestFixtures.cleanAndCopyDir(testDir, targetDir);
//		wrapper.createCompactSVGFromPDF(targetDir, targetDir1);
	}

	@Test
	/** reads existing project with 3 CTrees and 3 SVG files
	 * 
	 */
	public void testReadProject() {
		Assert.assertTrue("directory", AMIFixtures.TEST_WORKBENCH_MOSQUITO.isDirectory());
		Assert.assertNotNull(AMIFixtures.TEST_WORKBENCH_MOSQUITO.listFiles());
		Assert.assertEquals(6, AMIFixtures.TEST_WORKBENCH_MOSQUITO.listFiles().length);
		CProject mosquitoProject = new CProject(AMIFixtures.TEST_WORKBENCH_MOSQUITO);
		CTreeList mosquitoTreeList = mosquitoProject.getOrCreateCTreeList();
		Assert.assertEquals("size", 3, mosquitoTreeList.size());
		
	}
	
	@Test
	/** renames SVG files from compact file syntax to fulltext-pagedd.svg
	 * 
	 * uses move2
	 */
	public void testMoveFiles() {
		String cmd = "--project "+targetDir+" --move2 svg/fulltext-page(\\1).svg "
				+ " --fileFilter .*/fulltext\\-page(\\d+)\\.svg\\.compact\\.svg";
		new Norma().run(cmd);
	}
	
	// ================================
	
	private static Norma makeAndTestCProjectFromPDFs(File pdfDir, File targetDir, int ctreeCount) {
		Assert.assertTrue("directory", pdfDir.isDirectory());
		Assert.assertNotNull(pdfDir.listFiles());
		Assert.assertEquals(ctreeCount, pdfDir.listFiles().length);
		CMineTestFixtures.createCleanedCopiedDirectory(pdfDir, targetDir);
		Norma norma = new Norma();
		CProject cProject = norma.makeProjectFromPDFs(targetDir);
		Assert.assertEquals(ctreeCount, cProject.getOrCreateCTreeList().size());
		return norma;
	}


	
}
