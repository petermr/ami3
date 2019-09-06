package org.contentmine.pdf2svg.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;
import org.junit.Ignore;
import org.junit.Test;


public class TextStripperTest {

	@Test
	@Ignore("no files")
	public void testTextStripper() throws IOException {
        String pdfFile = "demos/ebola/roadmapsitrep_14Nov2014_eng.pdf";
        File outputDir = new File("target/ebola/");
        File outputFile = new File(outputDir, "14Nov.txt");
        extractAsTextFileWithDefaults(pdfFile, outputFile);
    }

	public static void main(String[] args) throws Exception {
		// this has bad character widths
		runTextStripper("demos/gandhi/sample.pdf", new File("target/gandhi/stripper.txt"));
	}
	private static void runTextStripper(String pdfFile, File outputFile ) throws IOException {
        extractAsTextFileWithDefaults(pdfFile, outputFile);
    }

	private static void extractAsTextFileWithDefaults(String pdfFile, File outputFile) throws IOException {
		boolean toHTML = true;
        boolean force = false;
        boolean sort = true;
        boolean separateBeads = true;
        String encoding = "UTF-8";
        outputFile.getParentFile().mkdirs();
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        toHTML = false;
        sort = true;
        PDDocument document = PDDocument.load(new File(pdfFile));
        Writer output = new OutputStreamWriter(new FileOutputStream(outputFile), encoding);
        PDFTextStripper stripper = null;
        if (toHTML) {
        	//stripper = new org.apache.pdfbox.tools.PDFText2HTML();//Don't know why this doesn't link...
            stripper = new PDFText2HTML("UTF-8");
        } else {
            stripper = new PDFTextStripper();
        }
        stripper.setForceParsing(force);
        stripper.setSortByPosition(sort);
        stripper.setShouldSeparateByBeads(separateBeads);
        stripper.setStartPage(startPage);
        stripper.setEndPage(endPage);
        stripper.writeText(document, output);
        output.close();
	}
}