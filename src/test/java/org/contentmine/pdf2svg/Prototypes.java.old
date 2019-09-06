package org.contentmine.pdf2svg;

import org.contentmine.pdf2svg.PDF2SVGConverter;

public class Prototypes {

	public static void main(String[] args) {
//		phytochem1();
//		carnosic();
		funnel();
	}

	private static void phytochem1() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/phytochem", "src/test/resources/elsevier/S1874390014000469.pdf"
		);
	
	}
	private static void carnosic() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/phytochem", "src/test/resources/elsevier/carnosic.pdf"
		);
	
	}
	private static void funnel() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/funnel", "demos/sage/Sbarra-454-74.pdf"
		);
	
	}
	private static void BMC() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/BMC", Fixtures.PDF2SVG_DIR+"misc/10.1186_1471-2431-13-190/fulltext.pdf"
				);
	}
}
