package org.contentmine.pdf2svg2;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.svg2xml.pdf.SVGDocumentProcessor;
import org.junit.Test;

import junit.framework.Assert;

public class BiorxivIT {
	private static final Logger LOG = Logger.getLogger(BiorxivIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testBiorxivMarchantiaAll() throws Exception {
		File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
		File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
//		String[] files = {
//		"103861",
//		"188912",
//		"199992",
////		"220731", // this one is long and complex
//		"247593",
//		"277350",
//		"329839",
//		"362400",
//		"363937",
//		"385682"
//	};
		
		/** this is now a cProject structure. */
		boolean skipSVG = true;
		CProject cProject = null;
		CTreeList cTreeList = null;
		if (!skipSVG) {
			CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
			cProject = new CProject(targetDir);
			cTreeList = cProject.getOrCreateCTreeList();
			for (CTree cTree : cTreeList) {
				LOG.debug("******* "+cTree+" **********");
				List<File> svgFiles = cTree.getExistingSVGFileList();
			    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
			    documentProcessor.setMinimumImageBox(100, 100);
			    documentProcessor.readAndProcess(cTree.getExistingFulltextPDF());
			    File outputDir = targetDir;
				documentProcessor.writeSVGPages(outputDir);
		    	documentProcessor.writePDFImages(outputDir);
			}
		}
		cProject = new CProject(targetDir);
		cTreeList = cProject.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			List<File> svgFiles = cTree.getExistingSVGFileList();
			SVGDocumentProcessor svgDocumentProcessor = new SVGDocumentProcessor();
			svgDocumentProcessor.readSVGFilesIntoSortedPageList(svgFiles);
			HtmlHtml html = svgDocumentProcessor.readAndConvertToHtml(svgFiles);
			File htmlFile = new File(new File(targetDir, cTree.getName()), "scholarly.html");
			XMLUtil.debug(html, htmlFile, 1);
		}

	}

	@Test
		public void testBiorxivMarchantia2HTML1() throws Exception {
			File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
			File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
			CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
			String fileroot = "103861";
			CTree cTree = new CTree(new File(targetDir, fileroot));
			List<File> svgFiles = cTree.getExistingSVGFileList();
			Assert.assertEquals(29,  svgFiles.size());
			SVGDocumentProcessor svgDocumentProcessor = new SVGDocumentProcessor();
			svgDocumentProcessor.readSVGFilesIntoSortedPageList(svgFiles);
			HtmlHtml html = svgDocumentProcessor.readAndConvertToHtml(svgFiles);
			File htmlFile = new File(targetDir, fileroot+".html");
	//		LOG.debug(htmlFile);
			XMLUtil.debug(html, htmlFile, 1);
			
		}

	@Test
	public void testBiorxivMarchantia2HTMLAll() throws Exception {
		File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
		File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject project = new CProject(targetDir);
		CTreeList cTreeList = project.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			List<File> svgFiles = cTree.getExistingSVGFileList();
			SVGDocumentProcessor svgDocumentProcessor = new SVGDocumentProcessor();
			svgDocumentProcessor.readSVGFilesIntoSortedPageList(svgFiles);
			HtmlHtml html = svgDocumentProcessor.readAndConvertToHtml(svgFiles);
			File htmlFile = new File(targetDir, cTree.getName()+".html");
			XMLUtil.debug(html, htmlFile, 1);
		}
	}
}
