package org.contentmine.graphics.svg.plot.spectra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.pdf2svg2.PDFDocumentProcessor;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class VectorSpectraIT {
	private static final Logger LOG = Logger.getLogger(VectorSpectraIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	@Test
	public void testCopyPDF2Target() {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		Assert.assertTrue("spectra "+SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, SVGHTMLFixtures.G_SPECTRA_PLOT_DIR.exists());
		Assert.assertTrue("rsc "+sourceDir, sourceDir.exists());
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		Assert.assertTrue("target "+targetDir, targetDir.exists());
		
	}

	@Test
	public void testMakeProject() {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject(targetDir);
		cProject.makeProject(CTree.PDF, 20);
		Assert.assertTrue("target pdf "+targetDir, new File(targetDir, "c8ob00931g1").exists());
		
	}

	@Test
	
	public void testPDF2SVG() throws Exception {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject(targetDir);
		cProject.makeProject(CTree.PDF, 15);
//		String ctreeS = "c8ob00931g1";
//		String ctreeS = "c8ob00998h1";
		String ctreeS = "c8ob00847g1";
		File ctreeFile = new File(targetDir, ctreeS);
		Assert.assertTrue("target pdf "+targetDir, ctreeFile.exists());
        File file = new File(ctreeFile, "fulltext.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    documentProcessor.getOrCreatePageIncluder().addZeroNumberedIncludePages(45);
	    documentProcessor.readAndProcess(file);
	    documentProcessor.writeSVGPages(targetDir);
    	documentProcessor.writePDFImages(targetDir);
	}

	@Test
	@Ignore // too long
	public void testPDF2SVG3Files() throws Exception {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject();
		String command = "--project " + targetDir + CProject.MAKE_PROJECT_PDF;
		cProject.run(command);
		String[] ctreeNames = {
			"c8ob00931g1",
			"c8ob00998h1",
			"c8ob00847g1",
		};
		for (int i = 0; i < ctreeNames.length; i++) {
			String ctree = ctreeNames[i];
			writeSVGAndPNG(targetDir, ctree);
		}
	}

	@Test
	@Ignore // VERY LONG
	public void testStefan100() throws Exception {
		String fileroot = "journals2";
		File sourceDir = new File("/Users/pm286/workspace/projects/stefan/", fileroot);
		if (!TestUtil.checkForeignDirExists(sourceDir)) {
			LOG.debug(sourceDir+" does not exist");
			return;
		}
		File targetDir = new File("target/projects/stefan", fileroot);
		List<File> files = createStefanFiles(sourceDir, targetDir);
		Assert.assertTrue(files.size() >= 100);
		for (File file : files) {
			LOG.debug("******* "+file+" **********");
			String filename = file.toString();
			if (filename.endsWith(".pdf")) {
				String fileroot1 = FilenameUtils.getBaseName(filename);
			    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
			    documentProcessor.setMinimumImageBox(100, 100);
			    documentProcessor.readAndProcess(file);
			    File outputDir = new File(targetDir, fileroot1);
				documentProcessor.writeSVGPages(outputDir);
		    	documentProcessor.writePDFImages(outputDir);
			}
		}
	}

	@Test
	public void testPDF2SVG2HTML() throws Exception {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject(targetDir);
		String command = "--project " + targetDir + CProject.MAKE_PROJECT_PDF;
		cProject.run(command);
		String[] cTreeNames = {
			"c8ob00931g1",
//			"c8ob00998h1",
//			"c8ob00847g1",
		};
		CTreeList cTreeList = cProject.createCTreeList(Arrays.asList(cTreeNames));
		LOG.debug(targetDir);
		CTree cTree = cTreeList.get(0);
		Assert.assertTrue(cTree.getDirectory().exists());
        File file = cTree.getExistingFulltextPDF();
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    documentProcessor.getOrCreatePageIncluder().addZeroNumberedIncludePages(45);
	    documentProcessor.readAndProcess(file);
	    documentProcessor.writeSVGPages(targetDir);
    	documentProcessor.writePDFImages(targetDir);
	}


	@Test
	public void testSuppdata10() {
		
	}
	

	// ========================================

	private List<File> createStefanFiles(File sourceDir, File targetDir) {
		List<File> files2 = new ArrayList<File>();
		if (!TestUtil.checkForeignDirExists(sourceDir)) {
			LOG.debug(sourceDir+" does not exist");
		} else {
			CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
			File[] files = sourceDir.listFiles();
			files2 = Arrays.asList(files);
			Collections.sort(files2);
		}
		return files2;
	}

	private void writeSVGAndPNG(File targetDir, String ctree) throws IOException {
		File ctreeDir = new File(targetDir, ctree);
		Assert.assertTrue("target pdf "+targetDir, ctreeDir.exists());
		File file = new File(ctreeDir, "fulltext.pdf");
		PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
		documentProcessor.readAndProcess(file);
		documentProcessor.writeSVGPages(ctreeDir);
		documentProcessor.writePDFImages(ctreeDir);
	}


}
