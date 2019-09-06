package org.contentmine.svg2xml.pdf;

import java.io.File;

import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Ignore;
import org.junit.Test;

public class PDFAnalyzerTest {

	@Test
	public void Dummy() {
	}

	@Test
	@Ignore
	public void testPDFAnalyzerPDFWithSVG() {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.analyzePDFFile(SVG2XMLFixtures.MULTIPLE312_PDF);
	}

	/** this is better with PDFAnalyzer.main(String[] )
	 * 
	 * @param filename
	 */
	public static void analyzePDF(String filename) {
		File file = new File(filename);
		if (file.exists() && !file.isDirectory()) {
			new PDFAnalyzer().analyzePDFFile(file);
		} else {
			throw new RuntimeException("File must exist and not be directory: "+filename);
		}
	}
	
}
