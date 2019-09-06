package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.fonts.StyleRecord;
import org.contentmine.graphics.svg.fonts.StyleRecordSet;
import org.contentmine.graphics.svg.text.SVGTextLine;
import org.contentmine.graphics.svg.text.SVGTextLineList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multimap;

public class TextCacheTest {


private static final Logger LOG = Logger.getLogger(TextCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	/**
	 * bug where svgx:char changes, hopefully now fixed.
	 * 
	 */
	@Test
	public void testAsciiCompact() {
		ComponentCache componentCache = new ComponentCache();
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_FONTS_DIR, "bugascii.svg"));
		TextCache textCache = new TextCache(componentCache);
		textCache.setUseCompactOutput(true);
		textCache.extractTexts(svgElement);
		List<SVGText> texts = textCache.getOrCreateOriginalTextList();
		Assert.assertEquals("compacted from ascii", 
				"[[3-9]((304.1,535.0))]]", texts.toString());

	}
	
	/**
	 * bug where svgx:char changes, hopefully now fixed.
	 * 
	 */
	@Test
	// FIXME
	public void testCompactBug() {
		ComponentCache componentCache = new ComponentCache();
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_FONTS_DIR, "bug.svg"));
		TextCache textCache = new TextCache(componentCache);
		textCache.setUseCompactOutput(true);
		textCache.extractTexts(svgElement);
		List<SVGText> texts = textCache.getOrCreateOriginalTextList();
		// note confusing to read since there are square brackets in stream!
		Assert.assertEquals("compacted from ascii", 
				"[[3((304.1,535.0))], [–((309.2,535.0))], [9]((313.7,535.0))]]", texts.toString());

	}
	
	/**
	 * bug where svgx:char changes, hopefully now fixed.
	 * 
	 */
	@Test
	public void testExtendBug() {
		ComponentCache componentCache = new ComponentCache();
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_FONTS_DIR, "bug.svg"));
		TextCache textCache = new TextCache(componentCache);
		textCache.setUseCompactOutput(false);
		textCache.extractTexts(svgElement);
		List<SVGText> texts = textCache.getOrCreateOriginalTextList();
		// note confusing to read since there are square brackets in stream!
		Assert.assertEquals("uncompact", 
				"[[3((304.1,535.0))], [–((309.2,535.0))], [9((313.7,535.0))], []((318.9,535.0))]]",
				texts.toString());

	}
	
	@Test
	public void testStyles() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		StyleRecordSet styleRecordSet = textCache.getOrCreateHorizontalStyleRecordSet();
		Assert.assertEquals(2, styleRecordSet.size());
		Multimap<Double, StyleRecord> styleRecordByFontSize = styleRecordSet.getStyleRecordByFontSize();
		Assert.assertEquals("sizes", 2, styleRecordByFontSize.size());
	}
		
	@Test
	public void testLines() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		// assume that y-coords will be the most important structure
		StyleRecordSet horizontalStyleRecordSet =
				textCache.getOrCreateHorizontalStyleRecordSet();
		Double largestFont = horizontalStyleRecordSet.getLargestFontSize();
		Assert.assertNotNull("largest font not null", largestFont);
		Assert.assertEquals(6.0, largestFont, 0.1);
		List<SVGTextLine> textLineList = textCache.getTextLinesForFontSize(largestFont);
		Assert.assertEquals(19, textLineList.size());
	}
		
	@Test
	public void testIndents() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache componentCache = new ComponentCache();
		componentCache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = componentCache.getOrCreateTextCache();
		SVGTextLineList textLineList = textCache.getTextLinesForLargestFont();
		Assert.assertEquals(19, textLineList.size());
		RealArray indents = textLineList.calculateIndents(1);
		Assert.assertEquals(""
				+ "(269.7,269.7,278.2,269.7,278.2,269.7,269.7,278.2,269.7,269.7,"
				+ "269.7,269.7,269.7,279.7,269.7,269.7,269.7,269.7,269.7)", 
				indents.toString());
	}

	/**
	 * this is just the largest font so the answer looks sparse.
	 * 
	 */
	@Test
	
	public void testJoinLinesAtIndents() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		File targetDir = new File("target/math/demos/varga/");
		ComponentCache componentCache = new ComponentCache();
		componentCache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = componentCache.getOrCreateTextCache();
		TextLineFormatter currentLineFormatter = textCache.getCurrentLineFormatter();
		SVGTextLineList textLineList = currentLineFormatter.createAndJoinIndentedTextLineList();
		Assert.assertEquals(16, textLineList.size());
//		LOG.debug("joined: "+textLineList);
		SVGG g = textLineList.createSVGElement();
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "equations7.svg"), 1200., 800.);
	}

	@Test
	public void testMinorFontSizes() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		List<Double> minorFontSizes = textCache.getMinorFontSizes();
		Assert.assertEquals(1, minorFontSizes.size());
	}

	@Test
	@Ignore // not yet ready
	public void testMinorFontTextLines() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		SVGTextLineList textLineList = textCache.getTextLinesForLargestFont();
		List<Double> yCoords = textLineList.getYCoords();
		List<Double> minorFontSizes = textCache.getMinorFontSizes();
		for (Double fontSize : minorFontSizes) {
			SVGTextLineList textLines = textCache.getTextLinesForFontSize(fontSize);
			for (SVGTextLine textLine : textLines) {
				
				
			}
		}
		StyleRecordSet horizontalStyleRecordSet =
				textCache.getOrCreateHorizontalStyleRecordSet();
		Double largestFont = horizontalStyleRecordSet.getLargestFontSize();
		List<SVGTextLine> textLineList0 = textCache.getTextLinesForFontSize(largestFont);
		List<SVGTextLine> minorTextLineList = textCache.getTextLinesForMinorFontSizes();
		List<Double> minorFontSizes0 = horizontalStyleRecordSet.getMinorFontSizes();
		
		Assert.assertEquals(1, minorFontSizes.size());
	}
		
	@Test
	public void testAddSuscript0() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equation1.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		textCache.getSuscriptFormatter().addSuscripts(textCache);
		SVGTextLineList textLines = textCache.getOrCreateTextLines();
		SVGSVG.wrapAndWriteAsSVG(textLines.getTextLineList(), new File(new File("target/math/demos/varga/"), "equation1.svg"));
		
	}

	@Test
	public void testAddSuscripts() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		textCache.getSuscriptFormatter().addSuscripts(textCache);
		SVGTextLineList textLines = textCache.getOrCreateTextLines();
		SVGSVG.wrapAndWriteAsSVG(textLines.getTextLineList(), new File(new File("target/math/demos/varga/"), "suscripts.svg"));
		
	}

	@Test
	public void testAddSuscriptsAndJoin0() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations2.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		textCache.getSuscriptFormatter().addSuscripts(textCache);
		SVGTextLineList textLines = textCache.getOrCreateTextLines();
		SVGSVG.wrapAndWriteAsSVG(textLines.getTextLineList(), new File(new File("target/math/demos/varga/"), "wrappedLines02.svg"));
//		int ndecimal = 1; 
//		double minimumOffsetInFontSize = 1.3;
		SVGTextLineList textLineList = textCache.getCurrentLineFormatter().joinFollowingIndentedLines(
				/*textLines, */ /*ndecimal, minimumOffsetInFontSize, */textCache.getLargestCurrentFont());
		List<SVGTextLine> textLines1 = textLineList.getTextLineList();
		SVGSVG.wrapAndWriteAsSVG(textLines1, new File(new File("target/math/demos/varga/"), "wrappedLines12.svg"));
	}

	@Test
	public void testAddSuscriptsAndJoin() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		textCache.getSuscriptFormatter().addSuscripts(textCache);
		SVGTextLineList textLines = textCache.getOrCreateTextLines();
		SVGSVG.wrapAndWriteAsSVG(textLines.getTextLineList(), new File(new File("target/math/demos/varga/"), "wrappedLines0.svg"));
//		int ndecimal = 1; 
//		double minimumOffsetInFontSize = 1.3;
		SVGTextLineList textLineList = textCache.getCurrentLineFormatter().joinFollowingIndentedLines(
				/*textLines, */ /*ndecimal, minimumOffsetInFontSize, */textCache.getLargestCurrentFont());
		List<SVGTextLine> textLines1 = textLineList.getTextLineList();
		SVGSVG.wrapAndWriteAsSVG(textLines1, new File(new File("target/math/demos/varga/"), "wrappedLines.svg"));
		
	}

	@Test
	public void testAddSuscriptsAndJoin1() {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		textCache.pushFormatter(TextLineFormatter.createEquationFormatter(textCache));
		SVGTextLineList textLineList = textCache.getCurrentLineFormatter().addSuscriptsAndJoinWrappedLines();
		textCache.popFormatter();
		SVGSVG.wrapAndWriteAsSVG(textLineList.getTextLineList(), new File(new File("target/math/demos/varga/"), "wrappedLines7.svg"));
		
	}

	@Test
	public void testCreateHTML2() throws IOException {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations2.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		SVGTextLineList textLineList = textCache.getCurrentLineFormatter().addSuscriptsAndJoinWrappedLines();
		HtmlElement htmlElement = textCache.createHtmlElement();
		XMLUtil.debug(htmlElement, new File("target/html/equations2.html"), 1);
	}

	@Test
	public void testCreateHTML() throws IOException {
		File svgFile = new File(SVGHTMLFixtures.G_S_MATH_DIR, "equations7.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		SVGTextLineList textLineList = textCache.getCurrentLineFormatter().addSuscriptsAndJoinWrappedLines();
		HtmlElement htmlElement = textCache.createHtmlElement();
		XMLUtil.debug(htmlElement, new File("target/html/equations7.html"), 1);

	}
	
	@Test
	public void testCreateHTMLPage2() throws IOException {
		File svgFile = new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga/compact/page2.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		SVGTextLineList textLineList = textCache.getCurrentLineFormatter().addSuscriptsAndJoinWrappedLines();
		HtmlElement htmlElement = textCache.createHtmlElement();
		XMLUtil.debug(htmlElement, new File("target/html/page2a.html"), 1);

	}

	@Test
	public void testCreateWrappedColumn() throws IOException {
		HtmlElement div = new HtmlDiv();
		int page = 7;
		File svgFile = new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga/compact/fulltext-page"+page+".svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		RealRange yr = new RealRange(33, 698);
		HtmlElement htmlElementL = textCache.createHtmlFromBox(new RealRange(0, 260), yr);
		div.appendChild(htmlElementL);
		XMLUtil.debug(div, new File("target/html/page"+page+"Wrapped.html"), 1);

	}

	/** read OCR'ed text and extract sentences.
	 * 
	 */
	@Test
	public void testReadPagesGVSUDevereux() {
		File buildDir = new File("src/test/resources/closed/gvsu/");
		if (!TestUtil.checkForeignDirExists(buildDir)) return;
		String fileroot = "Devereux1950";
		File targetDir = new File(SVGHTMLFixtures.TARGET_TEXT_BUILD_DIR, fileroot);
		File directory = new File(buildDir, fileroot);
		DocumentCache documentCache = DocumentCache.createDocumentCache(directory);
		List<PageCache> pageCacheList = documentCache.getOrCreatePageCacheList();
		Assert.assertEquals(12,  pageCacheList.size());
		HtmlElement html = documentCache.convertSVGPages2HTML();
		Assert.assertNotNull("html not null", html);
		File htmlFile = new File(targetDir, "fulltext.html");
		HtmlHtml.wrapAndWriteAsHtml(html, htmlFile);
		long size = FileUtils.sizeOf(htmlFile);
		Assert.assertTrue("htmlFile "+size, size > 1000);

	}

	/** read OCR'ed text and extract sentences.
	 * 
	 */
	@Test
	public void testReadAllPagesGVSU() {
		
//		File buildDir = new File("src/test/resources/closed/gvsu/");
//		String fileroot = "Devereux1950";
//		File targetDir = new File(SVGHTMLFixtures.TARGET_TEXT_BUILD_DIR, fileroot);
//		File svgDir = new File(buildDir, fileroot+"/"+"svg/");
//		List<File> files = Arrays.asList(svgDir.listFiles());
//		Collections.sort(files);
//		List<SVGText> textList = new ArrayList<SVGText>();
//		ComponentCache documentCache = new ComponentCache();
//		for (File svgFile : files) {
//			if (svgFile.toString().endsWith(".svg")) {
//				ComponentCache componentCache = new ComponentCache();
//				componentCache.readGraphicsComponentsAndMakeCaches(svgFile);
//				TextCache textCache = componentCache.getOrCreateTextCache();
//				
//				List<SVGText> textList1 = textCache.getOrCreateCurrentTextList();
//				textList.addAll(textList1);
//			}
//		}
//// this is an interim kludge		
//		HtmlHtml html = new TextChunkCache((ComponentCache)null).createHtmlFromPage(textList);
//		File htmlFile = new File(targetDir, "fulltext.html");
//		LOG.debug("HT "+htmlFile);
//		HtmlHtml.wrapAndWriteAsHtml(html, htmlFile);

	}

	


}
