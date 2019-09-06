package org.contentmine.norma;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.contentmine.cproject.files.CTree;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("long")
public class TutorialIT {

	/**
	
	# convert PDF to TXT (
	norma \
	    	-q cmdirs_all/test_pdf_1471-2148-14-70 \
			--transform pdf2txt \
			-i fulltext.pdf \
			-o fulltext.pdf.txt
	ls -lt cmdirs_all/test_pdf_1471-2148-14-70/fulltext.pdf.txt
	
	 */
	@Test
	public void testConvertPDF2TXT() throws Exception {
		File cTreeTop = new File("target/cmdirs_all/test_pdf_1471-2148-14-70");
		if (cTreeTop.exists())FileUtils.forceDelete(cTreeTop);
		FileUtils.copyDirectory(new File(NAConstants.TEST_NORMA_DIR, "regressiondemos/cmdirs_all/test_pdf_1471-2148-14-70"), cTreeTop);
		Assert.assertNotNull("pdf", CTree.getExistingFulltextPDF(cTreeTop));
		FileUtils.forceDelete(CTree.getExistingFulltextPDFTXT(cTreeTop));
		String args = "-q target/cmdirs_all/test_pdf_1471-2148-14-70"
				+ " --transform pdf2txt"
				+ " -i fulltext.pdf"
				+ " -o fulltext.pdf.txt";
		Norma norma = new Norma();
		norma.run(args);
		Assert.assertNotNull("pdf", CTree.getExistingFulltextPDF(cTreeTop));
		Assert.assertNotNull("pdftxt should exist", CTree.getExistingFulltextPDFTXT(cTreeTop));
	}

}
