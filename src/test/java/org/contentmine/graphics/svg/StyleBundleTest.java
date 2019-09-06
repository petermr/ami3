package org.contentmine.graphics.svg;

import org.contentmine.graphics.AbstractCMElement;
import org.junit.Test;

public class StyleBundleTest {

	@Test
	public void testCreateBundle() {
		String textXml = 
		"<text xmlns=\"http://www.w3.org/2000/svg\" id=\"text6305\" font-weight=\"bold\" font-family=\"Helvetica\""
		+ " font-size=\"7.0\" y=\"658.945\" x=\"135.925\" svgx:width=\"333.0\""
		+ " svgx:fontName=\"YCWIXW+Helvetica-Bold\" clip-path=\"url(#clipPath1)\""
		+ " fill=\"#000000\" stroke=\"none\" xmlns:svgx=\"http://www.xml-cml.org/schema/svgx\">-</text>";
		AbstractCMElement textElement = SVGElement.readAndCreateSVG(textXml);
	}

}
