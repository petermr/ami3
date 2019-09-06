package org.contentmine.image;

import java.io.FileOutputStream;

import org.contentmine.graphics.svg.SVGUtil;
import org.junit.Test;

public class SVGGeneratorTest {
	
	@Test
	public void testHtml2SVG() throws Exception {
		SVGGenerator svgGenerator = new SVGGenerator();
		svgGenerator.readHtml(ImageAnalysisFixtures.JOURNAL_HTML);
		SVGUtil.debug(svgGenerator.getSVG(), new FileOutputStream("target/journal.svg"), 1);
	}
}
