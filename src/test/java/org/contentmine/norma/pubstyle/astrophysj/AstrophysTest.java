package org.contentmine.norma.pubstyle.astrophysj;

import java.io.File;
import java.io.FileOutputStream;

import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.input.pdf.PDF2XHTMLConverter;
import org.junit.Ignore;
import org.junit.Test;

public class AstrophysTest {

	@Test
	@Ignore // too long
	public void testReadPDF() throws Exception {
		PDF2XHTMLConverter converter = new PDF2XHTMLConverter();
		HtmlElement htmlElement = converter.readAndConvertToXHTML(new File(NormaFixtures.TEST_ASTROPHYS_DIR, "0004-637X_754_2_85.pdf"));
		new File("target/astrophys/").mkdirs();
		XMLUtil.debug(htmlElement, new FileOutputStream("target/astrophys/285.html"), 1);
	}
	
}
