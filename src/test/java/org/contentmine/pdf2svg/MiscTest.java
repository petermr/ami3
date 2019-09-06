package org.contentmine.pdf2svg;

import org.contentmine.svg2xml.PDF2SVGConverter;
import org.junit.Ignore;
import org.junit.Test;


public class MiscTest {

	@Test
	@Ignore
	public void testHelp() {
		new PDF2SVGConverter().run();
	}
	
	@Test
	@Ignore
	public void testBMC() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/bmc", "src/test/resources/bmc/1471-2148-11-329.pdf"
		);
	}
	
}
