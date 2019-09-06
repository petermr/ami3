package org.contentmine.pdf2svg2;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

public class PDFDocumentProcessorTest {
	public static final Logger LOG = Logger.getLogger(PDFDocumentProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testCreator() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg2/",
                "custom-render-demo.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    documentProcessor.readAndProcess(file);
		List<SVGG> svgList = documentProcessor.getOrCreateSVGPageList();
	    File svgFile = new File("target/pdf2svg2/examples/custom.svg");
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
	}

	@Test
	public void testCreator1() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/", "page6.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    List<SVGG> svgList = documentProcessor.readAndProcess(file).getOrCreateSVGPageList();
	    String fileroot = "target/pdf2svg2/examples/page6/";
		File svgFile = new File(fileroot, "page6.svg");
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
	}

	@Test
	
	public void testCreatorBMC() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/bmc/", "1471-2148-11-329.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    List<SVGG> svgList = documentProcessor.readAndProcess(file).getOrCreateSVGPageList();
	    String fileroot = "target/pdf2svg2/bmc/1471-2148-11-329/";
		File svgFile = new File(fileroot, "full.svg");
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
	}

	@Test
	public void testIncludePages() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/bmc/", "1471-2148-11-329.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    documentProcessor.getOrCreatePageIncluder().addZeroNumberedIncludePages(3, 7);
	    Assert.assertEquals("include", "[3, 7]", 
	    		documentProcessor.getOrCreatePageIncluder().getOrCreateZeroNumberedIncludePageList().toString());
	    documentProcessor.readAndProcess(file);
	    
	    String fileroot = "target/pdf2svg2/bmc/1471-2148-11-329/";
		File svgFile = new File(fileroot, "full.svg");
	    List<SVGG> svgList = documentProcessor.readAndProcess(file).getOrCreateSVGPageList();
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
	}

}
