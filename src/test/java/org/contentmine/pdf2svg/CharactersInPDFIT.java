package org.contentmine.pdf2svg;

import org.contentmine.svg2xml.PDF2SVGConverter;
import org.junit.Ignore;
import org.junit.Test;

public class CharactersInPDFIT {

	@Test
	@Ignore
	/** this has some unusual fonts and probable fails.
	 * 
	 * e.g. 
12856 [main] DEBUG org.apache.fontbox.util.FontManager  - Unsupported font format for external font: /Library/Fonts/STIXSizTwoSymBol.otf
12856 [main] DEBUG org.apache.fontbox.util.FontManager  - Unsupported font format for external font: /Library/Fonts/STIXSizTwoSymReg.otf
	 * will 
	 */
	public void testChars() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "src/test/resources/org/contentmine/pdf2svg/misc/");
	}

	@Test
		@Ignore
		public void testBold() {
			new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mdpi", "src/test/resources/mdpi/materials-05-00027.pdf"
	//			,"-debugFontName" , "KBDOLG+TimesNewRoman"
				);
		}

	@Test
		@Ignore
		public void testCambriaMathFont() {
			new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mdpi", "src/test/resources/mdpi"
	//			,"-debugFontName" , "KBEJAP+CambriaMath"
				);
		}

	// comment out @Ignore to test these
		@Test
		@Ignore
		public void testDingbatsFont() {
			new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "../pdfs/peerj/36.pdf"
	//					,"-debugFontName", "RNMPIC+Dingbats"
					);
			}

}
