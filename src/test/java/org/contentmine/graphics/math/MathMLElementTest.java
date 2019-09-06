package org.contentmine.graphics.math;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.junit.Test;

public class MathMLElementTest {
	private static final Logger LOG = Logger.getLogger(MathMLElementTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testMathML() throws IOException {
		File mathFile = new File(SVGHTMLFixtures.GR_MATHML_DIR, "math.mml");
		MathMLElement element = MathMLElement.create(XMLUtil.parseQuietlyToDocument(mathFile).getRootElement());
		XMLUtil.debug(element, new File("target/mathml/test/element.xml"), 1);

	}
	

}
