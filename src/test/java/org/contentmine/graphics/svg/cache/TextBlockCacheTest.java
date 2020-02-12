package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGTextBuilder;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.SVGTextLine;
import org.contentmine.graphics.svg.text.SVGTextLineList;
import org.junit.Test;

import junit.framework.Assert;

/**
 * tests aggregation of <text> into blocks
 * 
 * @author pm286
 *
 */
public class TextBlockCacheTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(TextBlockCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** a single page with no font changes or suscripts
	 * 
	 */
	@Test
	public void testSingleFontPage() {
		File svgFile = new File(PDF2SVG2, "test/Hariharan/svg/fulltext-page.1.svg");
		ComponentCache componentCache = ComponentCache.readAndCreateComponentCache(svgFile);
		TextCache textCache = componentCache.getOrCreateTextCache();
//		System.out.println("TC "+textCache);
		List<SVGText> currentTextList = textCache.getOrCreateCurrentTextList();
//		System.out.println("texts "+currentTextList);
		// these fail
		SVGTextLineList textLineList = textCache.getOrCreateTextLines();
		Assert.assertEquals("text line count ", 46,  textLineList.size());
		SVGTextLine textLine0 = textLineList.get(0);
		Assert.assertEquals("line 0", "<g xmlns=\"http://www.w3.org/2000/svg\""
				+ " class=\"textLine\"><text xmlns:svgx=\"http://www.xml-cml.org/schema/svgx\""
				+ " svgx:width=\"0.51,0.73,0.57,0.51,0.4,0.51,0.4,0.57,0.77,0.51,0.4,0.29,0.57,0.51,0.4,0.51,"
				+ "0.57,0.29,0.7,0.51,0.57,0.29,0.51,0.77,0.4,0.57,0.46,0.29,0.51,0.51,0.46,0.46,0.29,0.76,0.51,"
				+ "0.41,0.41,0.81,0.29,0.4,0.41,0.57,0.29,0.46,0.54,0.29,0.53,0.29,0.4,0.46,0.57,0.57,0.4,0.51,"
				+ "0.94,0.51,0.29,0.29,0.54\" y=\"18.1\""
				+ " x=\"34.0,67.1,73.6,78.7,83.3,86.9,91.5,95.1,103.3,110.2,114.8,118.4,121.0,126.1,130.7,"
				+ "134.3,138.9,144.1,149.7,155.7,160.3,165.4,168.0,175.7,182.6,186.2,191.6,195.7,198.2,198.2,"
				+ "202.8,206.9,211.0,216.6,223.4,228.0,231.7,238.4,245.6,248.2,251.8,255.4,260.5,263.1,266.9,"
				+ "271.8,277.4,282.2,284.7,288.3,292.4,297.5,302.6,306.2,313.9,322.4,327.0,329.5,332.1\""
				+ " style=\"fill:rgb(0,0,0);font-family:CMR9;font-size:9.0px;stroke:none;\">2BharathHariharan,PabloArbel´aez,RossGirshick,JitendraMalik</text></g>", textLine0.toXML());
		
	}

	@Test
	public void testSingleFontPageLineSeparations() {
		File svgFile = new File(PDF2SVG2, "test/Hariharan/svg/fulltext-page.1.svg");
		ComponentCache componentCache = ComponentCache.readAndCreateComponentCache(svgFile);
		TextCache textCache = componentCache.getOrCreateTextCache();
		List<SVGText> textList = textCache.getOrCreateCurrentTextList();
		SVGSVG.wrapAndWriteAsSVG(textList, new File(PDF2SVG2, "test/Hariharan/svg/page.1/textList.svg"));
		SVGTextBuilder builder = new SVGTextBuilder();
		builder.readTextList(textList);
		HtmlElement htmlElement = builder.getOrCreateHtml();
		
	}

}
