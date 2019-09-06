package org.contentmine.svg2xml.image;

import java.io.File;

import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.page.PageAnalyzer;
import org.contentmine.svg2xml.pdf.PDFAnalyzer;
import org.junit.Ignore;
import org.junit.Test;

public class ImageTest {
	@Test
	public void testImage() {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(new File(SVG2XMLFixtures.IMAGES_DIR, "multiple-image-page4.svg"));
		pageAnalyzer.splitChunksAndCreatePage();
	}

	@Test
	@Ignore // fails with image conversion
	public void testImagePDF() {
		new PDFAnalyzer().analyzePDFFile(new File(SVG2XMLFixtures.IMAGES_DIR, "multiple-image.pdf"));
	}

}
