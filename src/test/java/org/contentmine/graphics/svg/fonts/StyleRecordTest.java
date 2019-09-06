package org.contentmine.graphics.svg.fonts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.StyleBundle;
import org.junit.Test;

import com.google.common.collect.Multiset;

import junit.framework.Assert;

public class StyleRecordTest {
	private static final Logger LOG = Logger.getLogger(StyleRecordTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** get all italic styles (there are 3)
	 * 
	 */
	public void testGetMultipleStyleRecordsByStyle() {
		File infile = new File(SVGHTMLFixtures.G_S_TEXT_DIR, "CM_pdf2svg_BMCCancer_9_page4.svg");
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(infile);
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		StyleRecordSet cssStyleRecordSet = styleRecordFactory.createStyleRecordSet(svgElement);
		StyleRecordSet italicSet = cssStyleRecordSet.getStyleRecordSet(StyleBundle.FONT_STYLE, StyleBundle.ITALIC);
		Assert.assertTrue("contains", italicSet.toString().contains("font-name:MsjdcxAdvTT7329fd89.I;font-size:8.0px;"));
//		Assert.assertEquals("italic", 
//				"{fill:#000000;font-name:MsjdcxAdvTT7329fd89.I;font-size:8.0px;font-style:italic;font-weight:normal;stroke:none;=chars: total: 14; unique: 11; coords: 1 [39.7 x 14], fill:#000000;font-name:TrfkklAdvTT8861b38f.I;font-size:9.8px;font-style:italic;font-weight:normal;stroke:none;=chars: total: 206; unique: 25; coords: 6 [528.1 x 44, 540.0 x 42, 552.1 x 41, 564.1 x 27, 612.0 x 17, 624.1 x 35], fill:#000000;font-name:KchfjlAdvTT83913201.I;font-size:9.2px;font-style:italic;font-weight:normal;stroke:none;=chars: total: 80; unique: 24; coords: 4 [168.1 x 18, 312.1 x 5, 444.1 x 33, 588.1 x 24]}",
//			italicSet.toString());
	}
	
	/** test missing fill attribute
	 * 
	 */
	@Test
	public void testFontFill() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "page1.blue.svg");
		List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgFile));
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		StyleRecordSet cssStyleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
		TypefaceMaps typefaceSet = cssStyleRecordSet.extractTypefaceMaps("fill");
		List<Typeface> typefaceList = typefaceSet.getTypefaceListByFill("#2b5bf9");
		Assert.assertEquals("blue", 2, typefaceList.size());
	}

	/** extraction of equations by text style
	 * 
	 */
	@Test
	public void testExtractEquations() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "styledequations.svg");
		List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgFile));
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
		Assert.assertTrue("contains", styleRecordSet.toString().contains("StoneSerif;font-size:7.0px;font-weight:normal;=chars: total: 1843"));
//		Assert.assertEquals(""
//				+ "{fill:#000000;font-name:StoneSerif;font-size:7.0px;font-weight:normal;=chars: total: 1843; unique: 53;"
//				+ " coords: 32 [149.4, 158.4, 167.4, 176.4, 185.4, 194.4, 203.4, 212.4, 221.4, 230.4, 239.4, 248.5, 257.5,"
//				+ " 266.5, 275.5, 284.5, 293.5, 302.5, 563.5, 572.4 x 2, 572.5, 581.4, 590.4 x 3, 599.4, 608.4, 617.4 x 2,"
//				+ " 626.4, 635.4 x 2, 644.4, 653.4, 662.4, 671.4 x 2],"
//				+ " fill:#ffffff;font-name:StoneSerif-Semibold;font-size:12.0px;font-weight:bold;=chars: total: 1; unique: 1;"
//				+ " coords: 1 [26.0],"
//				+ " fill:#000000;font-name:StoneSerif;font-size:9.0px;font-weight:normal;=chars: total: 2779; unique: 50;"
//				+ " coords: 57 [72.7 x 2, 83.7 x 2, 94.7 x 2, 105.7 x 2, 116.7, 127.7, 138.7, 149.7, 160.7, 171.7, 182.7,"
//				+ " 193.7, 204.7, 215.7, 226.6, 237.6, 248.6, 259.6, 270.6, 281.6, 281.7 x 2, 292.7, 303.7, 314.7, 325.7,"
//				+ " 336.7, 347.7, 358.7 x 4, 369.7 x 3, 380.7 x 4, 391.7 x 3, 402.7, 413.7, 424.7 x 3, 435.7, 446.7, 457.7,"
//				+ " 468.7, 479.7, 490.7, 501.7, 512.7, 523.7, 534.7, 545.7 x 2, 556.7, 567.7, 578.7, 589.6, 600.6, 611.6,"
//				+ " 622.6, 633.6, 644.6, 655.6 x 2, 666.6, 677.6],"
//				+ " fill:#000000;font-name:StoneSans-Semibold;font-size:12.0px;font-weight:bold;=chars: total: 2; unique: 1;"
//				+ " coords: 1 [135.7 x 2],"
//				+ " fill:red;font-name:Universal-GreekwithMathPi;font-size:7.0px;font-weight:normal;stroke:red;=chars: total: 287; unique: 34;"
//				+ " coords: 1 [590.4],"
//				+ " fill:#ffffff;font-name:StoneSerif-Semibold;font-size:9.0px;font-weight:bold;=chars: total: 6; unique: 6;"
//				+ " coords: 1 [26.0],"
//				+ " fill:#000000;font-name:StoneSerif;font-size:3.9px;font-weight:normal;=chars: total: 563; unique: 25;"
//				+ " coords: 20 [326.3 x 3, 329.8 x 4, 341.3 x 6, 352.8, 364.3 x 4, 375.8, 387.3 x 5, 398.8 x 4, 410.3,"
//				+ " 421.8 x 5, 433.3 x 5, 444.8 x 5, 456.3 x 5, 467.8 x 5, 479.3 x 2, 490.8 x 5, 502.3 x 5, 513.8 x 5, 525.3 x 5, 536.8 x 2],"
//				+ " fill:#000000;font-name:StoneSans;font-size:6.0px;font-weight:normal;=chars: total: 65; unique: 32;"
//				+ " coords: 2 [708.7, 716.7 x 3],"
//				+ " fill:#000000;font-name:StoneSerif-Italic;font-size:9.0px;font-style:italic;font-weight:normal;=chars: total: 25; unique: 6;"
//				+ " coords: 4 [523.7, 534.7, 545.7, 655.6],"
//				+ " fill:#000000;font-name:StoneSerif;font-size:10.5px;font-weight:normal;=chars: total: 3; unique: 1;"
//				+ " coords: 1 [711.8],"
//				+ " fill:#000000;font-name:StoneSans-Semibold;font-size:9.0px;font-weight:bold;=chars: total: 17; unique: 11;"
//				+ " coords: 1 [135.7 x 2],"
//				+ " fill:#000000;font-name:StoneSerif;font-size:5.85px;font-weight:normal;=chars: total: 50; unique: 13;"
//				+ " coords: 7 [278.7 x 4, 355.7 x 4, 360.9, 377.7 x 6, 382.9, 388.7 x 2, 421.7 x 4],"
//				+ " fill:#000000;font-name:Cochin-Bold;font-size:5.751px;font-weight:bold;=chars: total: 14; unique: 10;"
//				+ " coords: 1 [708.7 x 2],"
//				+ " fill:#000000;font-name:StoneSans;font-size:7.0px;font-weight:normal;=chars: total: 77; unique: 33;"
//				+ " coords: 1 [29.7],"
//				+ " fill:#000000;font-name:StoneSerif;font-size:4.55px;font-weight:normal;=chars: total: 44; unique: 15;"
//				+ " coords: 5 [574.2 x 2, 588.1, 619.2, 633.1 x 2, 673.2],"
//				+ " fill:#000000;font-name:Cochin-Bold;font-size:7.668px;font-weight:bold;=chars: total: 2; unique: 2;"
//				+ " coords: 1 [708.7 x 2],"
//				+ " fill:#000000;font-name:StoneSerif-SemiboldItalic;font-size:7.0px;font-style:italic;font-weight:bold;=chars: total: 33; unique: 17;"
//				+ " coords: 1 [149.4],"
//				+ " fill:#000000;font-name:StoneSerif;font-size:6.0px;font-weight:normal;=chars: total: 665; unique: 39;"
//				+ " coords: 19 [328.3 x 7, 339.8 x 9, 351.3 x 2, 362.8 x 6, 374.3 x 2, 385.8 x 7, 397.3 x 6, 408.8 x 2,"
//				+ " 420.3 x 7, 431.8 x 6, 443.3 x 6, 454.8 x 7, 466.3 x 6, 477.8 x 4, 489.3 x 7, 500.8 x 7, 512.3 x 7, 523.8 x 7, 535.3 x 3]}", 
//				styleRecordSet.toString());
		TypefaceMaps typefaceMaps = styleRecordSet.extractTypefaceMaps("equationsPage");
		List<Typeface> typefaceList = typefaceMaps.getSortedTypefaceList();
		
		Assert.assertEquals("["
				+ "Cochin-Bold: weights: [bold]; styles: []; strokes: []; fills: [#000000]; fontSizes: [7.668, 5.751];\n"
				+ ", StoneSans-Semibold: weights: [bold]; styles: []; strokes: []; fills: [#000000]; fontSizes: [12.0, 9.0];\n"
				+ ", StoneSans: weights: [normal]; styles: []; strokes: []; fills: [#000000]; fontSizes: [6.0, 7.0];\n"
				+ ", StoneSerif-Italic: weights: [normal]; styles: [italic]; strokes: []; fills: [#000000]; fontSizes: [9.0];\n"
				+ ", StoneSerif-Semibold: weights: [bold]; styles: []; strokes: []; fills: [#ffffff]; fontSizes: [12.0, 9.0];\n"
				+ ", StoneSerif-SemiboldItalic: weights: [bold]; styles: [italic]; strokes: []; fills: [#000000]; fontSizes: [7.0];\n"
				+ ", StoneSerif: weights: [normal]; styles: []; strokes: []; fills: [#000000]; fontSizes: [10.5, 9.0, 5.85, 7.0, 4.55, 6.0, 3.9];\n"
				+ ", Universal-GreekwithMathPi: weights: [normal]; styles: []; strokes: [red]; fills: [red]; fontSizes: [7.0];\n"
				+ "]", typefaceList.toString());
	}

	/** extraction of equations by text style
	 * 
	 */
	@Test
	public void testDisplayStyles() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "styledequations.svg");
		List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgFile));
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
		SVGElement g = styleRecordSet.createStyledTextBBoxes(svgTexts);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/demos/", "equations.svg"));
	}

	/** extraction of equations by text style
	 * 
	 */
	@Test
	public void testDisplayStylesDocument() {
		List<File> svgFiles = SVGElement.extractSVGFiles(new File(SVGHTMLFixtures.G_S_PAGE_DIR, "bmc"));
		for (File svgFile : svgFiles) {
			String name = svgFile.getName();
			List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgFile));
			StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
			StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
			SVGElement g = styleRecordSet.createStyledTextBBoxes(svgTexts);
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/demos/bmc/", name));
		}
	}

	/** processing a directory of compact SVG.
	 * 
	 */
	@Test
	public void testDisplayStylesDocument1() {
		List<File> svgFiles = SVGElement.extractSVGFiles(new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga/compact"));
		for (File svgFile : svgFiles) {
			String name = svgFile.getName();
			List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgFile));
			StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
			StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
			SVGElement g = styleRecordSet.createStyledTextBBoxes(svgTexts);
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/demos/varga/compact/", name));
		}
	}

	/** subset of page containing equations with sub abd superscripts.
	 * 
	 */
	@Test
	public void testStyleArrays() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "styledequations.svg");
		List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgFile));
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
		List<StyleRecord> sortedStyles = styleRecordSet.createSortedStyleRecords();
		for (StyleRecord styleRecord : sortedStyles) {
			Multiset<Double> yCoordinateSet = styleRecord.getOrCreateYCoordinateSet();
			List<Double> yCoords = new ArrayList<Double>(yCoordinateSet.elementSet());
			RealArray yCoordinateArray = new RealArray(yCoords);
			yCoordinateArray.sortAscending();
			Multiset<Double> diffs = yCoordinateArray.createDoubleDifferenceMultiset(1);
			LOG.trace(styleRecord.getFontName()+" "+diffs+" "+yCoordinateArray);
		}
	}

}
