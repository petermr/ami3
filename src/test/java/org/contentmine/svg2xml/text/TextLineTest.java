package org.contentmine.svg2xml.text;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.text.TextCoordinate;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.RawWords;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Element;


public class TextLineTest {

	private final static Logger LOG = Logger.getLogger(TextLineTest.class);

	// ==========================================================================
	
		// FIXTURES
		
		public static final String PAGE_STRING =
		"<svg xmlns='http://www.w3.org/2000/svg'>"+ 
		 "<g xmlns:svgx=\"http://www.xml-cml.org/schema/svgx\">"+
		  "<text svgx:width='531.0' x='495.426' y='39.605' font-size='7.97'>P</text>"+
		  "<text svgx:width='479.0' x='499.658' y='39.605' font-size='7.97'>a</text>"+
		  "<text svgx:width='552.0' x='503.476' y='39.605' font-size='7.97'>g</text>"+
		  "<text svgx:width='500.0' x='507.876' y='39.605' font-size='7.97'>e</text>"+
		  "<text svgx:width='510.0' x='514.475' y='39.605' font-size='7.97'>3</text>"+
		  "<text svgx:width='541.0' x='521.162' y='39.605' font-size='7.97'>o</text>"+
		  "<text svgx:width='291.0' x='525.474' y='39.605' font-size='7.97'>f</text>"+
		  "<text svgx:width='510.0' x='530.463' y='39.605' font-size='7.97'>1</text>"+
		  "<text svgx:width='510.0' x='534.528' y='39.605' font-size='7.97'>4</text>"+
		 "</g>"+
		"</svg>";
		public final static Element PAGE_ELEMENT = XMLUtil.parseXML(PAGE_STRING);
		public final static SVGElement PAGE_CHUNK = SVGElement.readAndCreateSVG(PAGE_ELEMENT);
		
		public final static TextStructurer PAGE_TEXT_STRUCTURER = 
				TextStructurer.createTextStructurerWithSortedLines(PAGE_CHUNK);
		public final static TextLine PAGE_TEXT_LINE = PAGE_TEXT_STRUCTURER.getLinesInIncreasingY().get(0);
				

		public static final String PAGE_STRING1 =
		"<svg xmlns='http://www.w3.org/2000/svg'>"+ 
		 "<g xmlns:svgx=\"http://www.xml-cml.org/schema/svgx\">"+
		  "<text svgx:width='556.0' x='82.484' y='251.045' font-size='7.399'>1</text>"+
		  "<text svgx:width='556.0' x='86.65' y='251.045' font-size='7.399'>6</text>"+
		  "<text svgx:width='556.0' x='147.669' y='251.045' font-size='7.399'>1</text>"+
		  "<text svgx:width='556.0' x='151.835' y='251.045' font-size='7.399'>7</text>"+
		  "<text svgx:width='556.0' x='212.854' y='251.045' font-size='7.399'>1</text>"+
		  "<text svgx:width='556.0' x='217.021' y='251.045' font-size='7.399'>8</text>"+
		  "<text svgx:width='556.0' x='278.039' y='251.045' font-size='7.399'>1</text>"+
		  "<text svgx:width='556.0' x='282.206' y='251.045' font-size='7.399'>9</text>"+
		 "</g>"+
		"</svg>";
		public final static Element PAGE_ELEMENT1 = XMLUtil.parseXML(PAGE_STRING1);
		public final static SVGElement PAGE_CHUNK1 = SVGElement.readAndCreateSVG(PAGE_ELEMENT1);
		
		public final static TextStructurer PAGE_TEXT_STRUCTURER1 = 
				TextStructurer.createTextStructurerWithSortedLines(PAGE_CHUNK1);
		public final static TextLine PAGE_TEXT_LINE1 = PAGE_TEXT_STRUCTURER1.getLinesInIncreasingY().get(0);
				
		public static final String PAGE_STRING2 =
		"<svg xmlns='http://www.w3.org/2000/svg'>"+ 
		 "<g xmlns:svgx=\"http://www.xml-cml.org/schema/svgx\">"+
		 "<text svgx:width='667.0' x='147.06' y='262.165' font-size='7.399' >P</text>"+
		  "<text svgx:width='556.0' x='152.14' y='262.165' font-size='7.399' >h</text>"+
		  "<text svgx:width='556.0' x='156.762' y='262.165' font-size='7.399' >e</text>"+
		  "<text svgx:width='556.0' x='160.917' y='262.165' font-size='7.399' >n</text>"+
		  "<text svgx:width='556.0' x='165.538' y='262.165' font-size='7.399' >o</text>"+
		  "<text svgx:width='278.0' x='169.694' y='262.165' font-size='7.399' >t</text>"+
		  "<text svgx:width='500.0' x='171.54' y='262.165' font-size='7.399' >y</text>"+
		  "<text svgx:width='556.0' x='175.237' y='262.165' font-size='7.399' >p</text>"+
		  "<text svgx:width='222.0' x='179.4' y='262.165' font-size='7.399' >i</text>"+
		  "<text svgx:width='500.0' x='181.232' y='262.165' font-size='7.399' >c</text>"+
		  "<text svgx:width='278.0' x='184.928' y='262.165' font-size='7.399' > </text>"+
		  "<text svgx:width='278.0' x='186.322' y='262.165' font-size='7.399' >t</text>"+
		  "<text svgx:width='556.0' x='188.169' y='262.165' font-size='7.399' >a</text>"+
		  "<text svgx:width='333.0' x='192.324' y='262.165' font-size='7.399' >r</text>"+
		  "<text svgx:width='500.0' x='194.63' y='262.165' font-size='7.399' >s</text>"+
		  "<text svgx:width='556.0' x='198.326' y='262.165' font-size='7.399' >u</text>"+
		  "<text svgx:width='500.0' x='202.955' y='262.165' font-size='7.399' >s</text>"+
		  "<text svgx:width='278.0' x='206.652' y='262.165' font-size='7.399' > </text>"+
		  "<text svgx:width='333.0' x='208.046' y='262.165' font-size='7.399' >(</text>"+
		  "<text svgx:width='833.0' x='210.351' y='262.165' font-size='7.399' >m</text>"+
		  "<text svgx:width='833.0' x='216.364' y='262.165' font-size='7.399' >m</text>"+
		  "<text svgx:width='333.0' x='222.376' y='262.165' font-size='7.399' >)</text>"+
		"</g>"+
	   "</svg>";
		public final static Element PAGE_ELEMENT2 = XMLUtil.parseXML(PAGE_STRING2);
		public final static SVGElement PAGE_CHUNK2 = SVGElement.readAndCreateSVG(PAGE_ELEMENT2);
		public final static TextStructurer PAGE_TEXT_STRUCTURER2 = 
				TextStructurer.createTextStructurerWithSortedLines(PAGE_CHUNK2);
		public final static TextLine PAGE_TEXT_LINE2 = PAGE_TEXT_STRUCTURER2.getTextLineList().get(0);
		

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 *
	 */
	@Ignore
	public void insertSpaceFactorTest() {

		TextLine textLine5 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());

		textLine5 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		double spaceFactor = 0.0;
		textLine5.insertSpaces(spaceFactor);
		Assert.assertEquals("spaceFactor: "+spaceFactor, "activation energy. Taking the natural logarithm of this equa-", textLine5.getLineContent());

		testScalefactor(0.10, 5, "activation energy. Taking the natural logarithm of this equa-");
		testScalefactor(0.36, 5, "activation energy. Taking the natural logarithm of this equa-");
		testScalefactor(0.39, 5, "activation energy. Taking the natural logarithm of this equa-");
		testScalefactor(0.42, 5, "activation energy. Taking the natural logarithm of this equa-");
//		testScalefactor(0.43, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
//		testScalefactor(0.45, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
//		testScalefactor(0.50, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
		testScalefactor(1.00, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");
		testScalefactor(10.0, 5, "activationenergy.Takingthenaturallogarithmofthisequa-");

	}

	/** uses default spaceFactor
	 *
	 */
	@Test
	public void insertSpaceTest() {

		TextLine textLine5 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		textLine5 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("control", "activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
		textLine5.insertSpaces();
		Assert.assertEquals("default spaceFactor", "activation energy. Taking the natural logarithm of this equa-", textLine5.getLineContent());
	}

	private static void testScalefactor(double spaceFactor, int lineNumber, String expected) {
		TextLine textLine5;
		textLine5 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, lineNumber);
		textLine5.insertSpaces(spaceFactor);
		Assert.assertEquals("spaceFactor: "+spaceFactor, expected, textLine5.getLineContent());
	}

	@Test
	/** note this uses high characters (MINUS &#8722) instead of HYPHEN-MINUS)
	 *
	 */
	public void addSpacesTest() {
		TextLine textLine5 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 5);
		Assert.assertEquals("activationenergy.Takingthenaturallogarithmofthisequa-", textLine5.getLineContent());
	}

	@Test
	public void testFontSizeSetLine0() {
		TextLine textLine0 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 0);
		Set<TextCoordinate> fontSizeSet = textLine0.getFontSizeSet();
		Assert.assertNotNull("line0 set", fontSizeSet);
		Assert.assertEquals("line0 size", 1, fontSizeSet.size());
		Assert.assertEquals("line0 fontSize", 7.07, fontSizeSet.iterator().next().getDouble(), 0.01);
	}

	@Test
	public void testFontSizeSetLine5() {
		TextLine textLine5 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 5);
		Set<TextCoordinate> fontSizeSet = textLine5.getFontSizeSet();
		Assert.assertNotNull("line5 set", fontSizeSet);
		Assert.assertEquals("line5 size", 1, fontSizeSet.size());
		Assert.assertEquals("line5 fontSize", 9.465, fontSizeSet.iterator().next().getDouble(), 0.01);
	}

	@Test
	public void testgetSimpleFontFamilyMultiset8() {
		TextLine textLine8 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 8);
		Multiset<String> fontFamilyMultiset = textLine8.getFontFamilyMultiset();
		Assert.assertNotNull("fontFamilyMultiset", fontFamilyMultiset);
		Assert.assertEquals("single", 45, fontFamilyMultiset.size());
		Assert.assertEquals("single", 1, fontFamilyMultiset.entrySet().size());
		LOG.trace(textLine8);
	}
	
	@Test
	public void testgetSimpleFontFamilyMultiset0() {
		TextLine textLine0 = TextLineTest.getTextLine(SVG2XMLFixtures.PARA_SUSCRIPT_SVG, 0);
		Multiset<String> fontFamilyMultiset = textLine0.getFontFamilyMultiset();
		Assert.assertNotNull("fontFamilyMultiset", fontFamilyMultiset);
		Assert.assertEquals("single", 4, fontFamilyMultiset.size());
		Set<Entry<String>> entrySet = fontFamilyMultiset.entrySet();
		Assert.assertEquals("single", 2, entrySet.size());
		Assert.assertEquals(2, fontFamilyMultiset.count("TimesNewRoman"));
		Assert.assertEquals(0, fontFamilyMultiset.count("Zark"));
		Assert.assertEquals(2, fontFamilyMultiset.count("MTSYN"));
		Iterator<Entry<String>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String> entry = iterator.next();
			LOG.trace(entry.getElement()+" "+entry.getCount());
		}
		LOG.trace(textLine0);
	}
	
	@Test
	public void testGetRawWords() {
		RawWords rawWords = PAGE_TEXT_LINE.getRawWords();
		Assert.assertEquals("rawWords", 4, rawWords.size());
	}

	@Test
	public void testGetRawWords2() {
		RawWords rawWords = PAGE_TEXT_LINE2.getRawWords();
		// there are explicit soaces to single word
		Assert.assertEquals("rawWords", 1, rawWords.size());
		LOG.trace(rawWords.get(0));
	}



	// =====================================================
	
	private static TextLine getTextLine(File file, int lineNumber) {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(file);
		List<TextLine> textLines = textContainer.getLinesInIncreasingY();
		TextLine textLine = textLines.get(lineNumber);
		return textLine;
	}


}
