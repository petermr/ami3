package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.RectComparator;
import org.contentmine.graphics.svg.RectComparator.RectEdge;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.fonts.StyleRecord;
import org.contentmine.graphics.svg.fonts.StyleRecordFactory;
import org.contentmine.graphics.svg.fonts.StyleRecordSet;
import org.contentmine.graphics.svg.layout.PubstyleManager;
import org.contentmine.graphics.svg.layout.SVGPubstyle;
import org.contentmine.graphics.svg.layout.SVGPubstyle.PageType;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/** analyses pages for components.
 * may extend to compete documents.
 * 
 * @author pm286
 *
 */

public class PageCacheTest {
	static final String FULLTEXT_PAGE = "fulltext-page";
	static final String MAINTEXT = "maintext";
	public static final Logger LOG = Logger.getLogger(PageCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** extraction of equations by text style
	 * 
	 */
	@Test
	public void testDisplayStyles() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "styledequations.svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgElement));
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		styleRecordFactory.setNormalizeFontNames(true);
		StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
		SVGElement g = styleRecordSet.createStyledTextBBoxes(svgTexts);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/demos/", "equations.svg"));
	}
	
	/** extraction of equations by text style
	 * 
	 */
	@Test
	public void testDissectMainPage() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "styledequations.svg");
		File targetDir = new File("target/demos/varga/");
		SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgElement));
		Real2Range cropBox = new Real2Range(new RealRange(13, 513), new RealRange(63, 683));
		Assert.assertEquals("raw", 351, svgTexts.size());
		List<SVGElement> workingTexts = SVGElement.extractElementsContainedInBox(svgTexts, cropBox);
		SVGSVG.wrapAndWriteAsSVG(workingTexts, new File(targetDir, "page7cropped.svg"));
		Assert.assertEquals("cropped", 339, workingTexts.size());
		// get chunks
		//this will come from clipping
		Real2Range cropBoxLeft = new Real2Range(new RealRange(13, 256), new RealRange(63, 683));
		List<SVGText> leftTexts = SVGText.extractTexts(SVGElement.extractElementsContainedInBox(svgTexts, cropBoxLeft));
		SVGSVG.wrapAndWriteAsSVG(leftTexts, new File(targetDir, "page7left.svg"));
		Assert.assertEquals("leftTexts", 98, leftTexts.size());
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		StyleRecordSet leftStyleRecordSet = styleRecordFactory.createStyleRecordSet(leftTexts);
		List<StyleRecord> sortedStyleRecords = leftStyleRecordSet.createSortedStyleRecords();
		Assert.assertEquals("styleRecords", 3, sortedStyleRecords.size());
		// italics
		Assert.assertEquals("record 0", "chars: total: 25; unique: 6; ycoords: 4 [523.7, 534.7, 545.7, 655.6]\n"
				+ " css: fill:#000000;font-size:9.0;font-style:italic;font-weight:normal;font-name:StoneSerif-Italic;", sortedStyleRecords.get(0).toString());
		Assert.assertEquals("record 1", "chars: total: 50; unique: 13; ycoords: 7 ["
				+ "278.7 x 4, 355.7 x 4, 360.9, 377.7 x 6, 382.9, 388.7 x 2, 421.7 x 4"
				+ "]\n"
				+ " css: fill:#000000;font-size:5.85;font-weight:normal;font-name:StoneSerif;", sortedStyleRecords.get(1).toString());
		Assert.assertEquals("record 2", "chars: total: 2600; unique: 50; ycoords: 57 [72.7, 83.7, 94.7, 105.7,"
				+ " 116.7, 127.7, 138.7, 149.7, 160.7, 171.7, 182.7, 193.7, 204.7, 215.7, 226.6, 237.6,"
				+ " 248.6, 259.6, 270.6, 281.6, 281.7 x 2, 292.7, 303.7, 314.7, 325.7, 336.7, 347.7,"
				+ " 358.7 x 4, 369.7 x 3, 380.7 x 4, 391.7 x 3, 402.7, 413.7, 424.7 x 3, 435.7, 446.7,"
				+ " 457.7, 468.7, 479.7, 490.7, 501.7, 512.7, 523.7, 534.7, 545.7 x 2, 556.7, 567.7,"
				+ " 578.7, 589.6, 600.6, 611.6, 622.6, 633.6, 644.6, 655.6 x 2, 666.6, 677.6]\n"
				+ " css: fill:#000000;font-size:9.0;font-weight:normal;font-name:StoneSerif;", sortedStyleRecords.get(2).toString());
		double eps = 0.2;
		List<RealArray> aps = sortedStyleRecords.get(0).createSortedCompressedYCoordAPList(eps);
		Assert.assertEquals("[(523.7,534.7,545.7)]",  aps.toString());
		aps = sortedStyleRecords.get(1).createSortedCompressedYCoordAPList(eps);
		Assert.assertEquals("[(355.7,360.9), (377.7,382.9)]",  aps.toString());
		aps = sortedStyleRecords.get(2).createSortedCompressedYCoordAPList(eps);
		Assert.assertEquals("["
				+ "(72.7,83.7,94.7,105.7,116.7,127.7,138.7,149.7,160.7,171.7,182.7,193.7,204.7,215.7,"
				+ "226.6,237.6,248.6,259.6,270.6,281.6,292.7,303.7,314.7,325.7,336.7,347.7,358.7,"
				+ "369.7,380.7,391.7,402.7,413.7,424.7,435.7,446.7,457.7,468.7,479.7,490.7,501.7,"
				+ "512.7,523.7,534.7,545.7,556.7,567.7,578.7,589.6,600.6,611.6,622.6,633.6,644.6,655.6,666.6,677.6)]",
				aps.toString());
		
		SVGElement g = leftStyleRecordSet.createStyledTextBBoxes(leftTexts);
		// won't work as we don't have lines till they have gone through the Caches
		List<SVGLine> lines = SVGLine.extractSelfAndDescendantLines(svgElement);
		List<SVGLine> lines1 = SVGLine.findHorizontaLines(lines, 0.001);
		g.appendChildren(lines1);
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "page7leftBoxes.svg"));
	}

	/** extraction of equations by text style
	 * 
	 */
	@Test
	public void testPageStyleCache() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "styledequations.svg");
		File targetDir = new File("target/demos/varga/");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		List<SVGText> svgTexts = textCache.extractCurrentTextElementsContainedInBox(new Real2Range(new RealRange(13, 513), new RealRange(63, 683)));
		Assert.assertEquals("raw", 339, svgTexts.size());
		SVGSVG.wrapAndWriteAsSVG(svgTexts, new File(targetDir, "page7cropped.svg"));
		Assert.assertEquals("cropped", 339, svgTexts.size());
		
		Real2Range cropBoxLeft = new Real2Range(new RealRange(13, 256), new RealRange(63, 683));
		List<SVGText> leftTexts = textCache.extractCurrentTextElementsContainedInBox(cropBoxLeft);
		SVGSVG.wrapAndWriteAsSVG(leftTexts, new File(targetDir, "page7left.svg"));
		Assert.assertEquals("leftTexts", 98, leftTexts.size());
		
		List<StyleRecord> sortedStyleRecords = textCache.createSortedStyleRecords();
		Assert.assertEquals("styleRecords", 3, sortedStyleRecords.size());
		// italics
		Assert.assertEquals("record 0", "chars: total: 25; unique: 6; ycoords: 4 [523.7, 534.7, 545.7, 655.6]\n"
				+ " css: fill:#000000;font-size:9.0;font-style:italic;font-weight:normal;font-name:StoneSerif-Italic;", sortedStyleRecords.get(0).toString());
		Assert.assertEquals("record 1", "chars: total: 50; unique: 13; ycoords: 7 ["
				+ "278.7 x 4, 355.7 x 4, 360.9, 377.7 x 6, 382.9, 388.7 x 2, 421.7 x 4]\n"
				+ " css: fill:#000000;font-size:5.9;font-weight:normal;font-name:StoneSerif;", sortedStyleRecords.get(1).toString());
		Assert.assertEquals("record 2", "chars: total: 2600; unique: 50; ycoords: 57 [72.7, 83.7, 94.7, 105.7,"
				+ " 116.7, 127.7, 138.7, 149.7, 160.7, 171.7, 182.7, 193.7, 204.7, 215.7, 226.6, 237.6,"
				+ " 248.6, 259.6, 270.6, 281.6, 281.7 x 2, 292.7, 303.7, 314.7, 325.7, 336.7, 347.7,"
				+ " 358.7 x 4, 369.7 x 3, 380.7 x 4, 391.7 x 3, 402.7, 413.7, 424.7 x 3, 435.7, 446.7,"
				+ " 457.7, 468.7, 479.7, 490.7, 501.7, 512.7, 523.7, 534.7, 545.7 x 2, 556.7, 567.7,"
				+ " 578.7, 589.6, 600.6, 611.6, 622.6, 633.6, 644.6, 655.6 x 2, 666.6, 677.6]\n"
				+ " css: fill:#000000;font-size:9.0;font-weight:normal;font-name:StoneSerif;", sortedStyleRecords.get(2).toString());
		double eps = 0.2;
		List<RealArray> aps = sortedStyleRecords.get(0).createSortedCompressedYCoordAPList(eps);
		Assert.assertEquals("[(523.7,534.7,545.7)]",  aps.toString());
		aps = sortedStyleRecords.get(1).createSortedCompressedYCoordAPList(eps);
		Assert.assertEquals("[(355.7,360.9), (377.7,382.9)]",  aps.toString());
		aps = sortedStyleRecords.get(2).createSortedCompressedYCoordAPList(eps);
		Assert.assertEquals("["
				+ "(72.7,83.7,94.7,105.7,116.7,127.7,138.7,149.7,160.7,171.7,182.7,193.7,204.7,215.7,"
				+ "226.6,237.6,248.6,259.6,270.6,281.6,292.7,303.7,314.7,325.7,336.7,347.7,358.7,"
				+ "369.7,380.7,391.7,402.7,413.7,424.7,435.7,446.7,457.7,468.7,479.7,490.7,501.7,"
				+ "512.7,523.7,534.7,545.7,556.7,567.7,578.7,589.6,600.6,611.6,622.6,633.6,644.6,655.6,666.6,677.6)]",
				aps.toString());
		StyleRecordSet leftStyleRecordSet = textCache.getStyleRecordSet();
		SVGElement g = leftStyleRecordSet.createStyledTextBBoxes(leftTexts);
		
		List<SVGLine> lineList = cache.getOrCreateLineCache().getOrCreateHorizontalLineList();
		g.appendChildren(lineList);
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "page7leftBoxes.svg"));
	}
	

	@Test
	/** reads a file with the bounding boxes of text and creates lines.
	 * 
	 */
	public void testGetRectLinesFromBoxes() {
		
		for (int ipage = 1; ipage <= 9; ipage++) {
		
		File pageFile = new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga/box/"+FULLTEXT_PAGE+ipage+".box.svg");
		AbstractCMElement pageElement = SVGElement.readAndCreateSVG(pageFile);
		List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(pageElement);
		PageCache pageCache = new PageCache();  // just for the test
		List<Real2Range> clipBoxes = pageCache.getDefault2ColumnClipBoxes();
		SVGG g = new SVGG();
		for (Real2Range clipBox : clipBoxes) {
			Multimap<Double, SVGRect> rectByYCoordinate = ArrayListMultimap.create();
			for (SVGRect rect : rectList) {
				rect.format(1);
				Real2Range bbox = rect.getBoundingBox();
				bbox = bbox.format(1);
				LOG.trace(rect+"//"+bbox);
				if (clipBox.includes(bbox)) {
					Double ymax = (Double) Util.format(rect.getBoundingBox().getYMax(), 1); // max since text line
					rectByYCoordinate.put(ymax, rect);
				}
			}
		
			List<Double> yList = new ArrayList<Double>(rectByYCoordinate.keySet());
			Collections.sort(yList);
			Real2Range lastBBox = null;
			Double lastFontSize = null;
			SVGRect lastTotalRect = null;
			for (Double y : yList) {
				List<SVGRect> rowRectList = new ArrayList<SVGRect>(rectByYCoordinate.get(y));
				Double fontSize  = getCommonFontSize(rowRectList);
				Collections.sort(rowRectList, new RectComparator(RectEdge.LEFT_EDGE));
				g.appendChildCopies(rowRectList);
				Real2Range rowBBox = SVGElement.createTotalBox(rowRectList);
				SVGRect totalRect = SVGRect.createFromReal2Range(rowBBox);
				totalRect.setFill("yellow").setOpacity(0.3);
				g.appendChild(totalRect);
				LOG.trace(y+": "+rowRectList);
				Real2Range intersection = rowBBox.getIntersectionWith(lastBBox);
				if (intersection != null) {
					LOG.trace("intersection "+intersection+" // "+lastFontSize+" // "+fontSize);
					SVGRect intersectionRect = SVGRect.createFromReal2Range(intersection);
					intersectionRect.setFill("red").setOpacity(0.3);
					g.appendChild(intersectionRect);
					if (lastFontSize / fontSize < 0.8) {
						lastTotalRect.setFill("blue");
					} else if (lastFontSize / fontSize > 1.2) {
						totalRect.setFill("orange");
					}
				}
				lastBBox = rowBBox;
				lastFontSize = fontSize;
				lastTotalRect = totalRect;
			}
			LOG.trace("==================\n");
		}
		File targetDir = new File("target/demos/varga/");
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "boxes"+ipage+".svg"));
		}
	}

	
	@Test
	/** reads a file with the bounding boxes of text and creates lines.
	 * 
	 */
	public void testGetRectLinesFromSVGAndBoxes() {
		
		int ipage = 7;
		
		File pageFile = new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga/box/"+FULLTEXT_PAGE+ipage+".box.svg");
		AbstractCMElement pageElement = SVGElement.readAndCreateSVG(pageFile);
		List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(pageElement);
		PageCache pageCache = new PageCache();  // just for the test
		List<Real2Range> clipBoxes = pageCache.getDefault2ColumnClipBoxes();
		SVGG g = new SVGG();
		for (Real2Range clipBox : clipBoxes) {
			Multimap<Double, SVGRect> rectByYCoordinate = ArrayListMultimap.create();
			for (SVGRect rect : rectList) {
				rect.format(1);
				Real2Range bbox = rect.getBoundingBox();
				bbox = bbox.format(1);
				LOG.trace(rect+"//"+bbox);
				if (clipBox.includes(bbox)) {
					Double ymax = (Double) Util.format(rect.getBoundingBox().getYMax(), 1); // max since text line
					rectByYCoordinate.put(ymax, rect);
				}
			}
		
			List<Double> yList = new ArrayList<Double>(rectByYCoordinate.keySet());
			Collections.sort(yList);
			Real2Range lastBBox = null;
			List<SVGRect> lastRowRectList;
			Double lastFontSize = null;
			SVGRect lastTotalRect = null;
			for (Double y : yList) {
				List<SVGRect> rowRectList = new ArrayList<SVGRect>(rectByYCoordinate.get(y));
				Double fontSize  = getCommonFontSize(rowRectList);
				Collections.sort(rowRectList, new RectComparator(RectEdge.LEFT_EDGE));
				g.appendChildCopies(rowRectList);
				Real2Range rowBBox = SVGElement.createTotalBox(rowRectList);
				SVGRect totalRect = SVGRect.createFromReal2Range(rowBBox);
				totalRect.setFill("yellow").setOpacity(0.3);
				g.appendChild(totalRect);
				LOG.trace(y+": "+rowRectList);
				Real2Range intersection = rowBBox.getIntersectionWith(lastBBox);
				if (intersection != null) {
					LOG.trace("intersection "+intersection+" // "+lastFontSize+" // "+fontSize);
					SVGRect intersectionRect = SVGRect.createFromReal2Range(intersection);
					intersectionRect.setFill("red").setOpacity(0.3);
					g.appendChild(intersectionRect);
					if (lastFontSize / fontSize < 0.8) {
						lastTotalRect.setFill("blue");
					} else if (lastFontSize / fontSize > 1.2) {
						totalRect.setFill("orange");
					}
				}
				lastBBox = rowBBox;
				lastFontSize = fontSize;
				lastRowRectList = rowRectList;
				lastTotalRect = totalRect;
			}
			LOG.trace("==================\n");
		}
		File targetDir = new File("target/demos/varga/");
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "boxes"+ipage+".svg"));
	}
	

	@Test
	public void testRegexNamedCapture() {
		String s = "B8,N6,N8,N8,N8,N10,N10,";
		Pattern pattern = Pattern.compile("(?<title>(B8,?)+),(?<head>(N6,?))(?<body>(N8,?)+)(?<bottom>(N10,?)+)");
		Matcher matcher = pattern.matcher(s);
		if (matcher.matches()) {
//			LOG.debug(matcher.groupCount());
//			LOG.debug(matcher.group("title"));
//			LOG.debug(matcher.group("head"));
//			LOG.debug(matcher.group("body"));
//			LOG.debug(matcher.group("bottom"));
		}
	}
	
	/** not sure the title is correct!
	 * 
	 */
	@Test
	public void testAdjustTextBoxSizesAutomatically() {
		File svgFile = new File(SVGHTMLFixtures.GR_LAYOUT_DIR, "asgt/middle7.text.svg");
		File targetDir = new File("target/demos/varga/");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		TextCache textCache = cache.getOrCreateTextCache();
		RealRange[] xRanges = new RealRange[] {new RealRange(13, 256), new RealRange(260, 550)};
		RealRange yRange = new RealRange(63, 683);
		SVGG g = new SVGG();
		for (RealRange xRange : xRanges) {
			Real2Range cropBox = new Real2Range(xRange, yRange);
			SVGG gg = textCache.extractYcoordAPs(cropBox);
			g.appendChild(gg);
			gg = textCache.extractStyledTextBBoxes(cropBox);
			gg.setTransform(new Transform2(new Vector2(550., 0.)));
			g.appendChild(gg);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "maintext7.grid.svg"), 1200., 700.);
//		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "maintext.boxes.svg"));
	}
		
	@Test
	public void testBMCTextWithPubstyle() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle bmcStyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
//		LOG.debug(bmcStyle.toXML());
//		Pubstyle pubstyle = pubstyleManager.guessPubstyleFromFirstPage(new File(SVGHTMLFixtures.CORPUS_DIR, 
//				"mosquitos/12936_2017_Article_1948/svg/fulltext-page2.svg"));
		Assert.assertEquals("bmc", bmcStyle.getPubstyleName());
		SVGElement page1 = bmcStyle.getRawPage(PageType.P1);
		Assert.assertNotNull(page1);
		LOG.trace(page1.toXML());

	}
	
	/** read single PSF/SVG page and translate to HTML
	 * 
	 */
	@Test
	public void testReadSinglePageGVSU() {
		
		File buildDir = SVGHTMLFixtures.G_S_TEXT_BUILD_DIR;
		String fileroot = "Devereux1950page.1";
		File targetDir = new File(SVGHTMLFixtures.TARGET_TEXT_BUILD_DIR, fileroot);
//		LOG.debug(targetDir);
		File svgFile = new File(buildDir, fileroot+".svg");
		PageCache pageCache = new PageCache();
		pageCache.readGraphicsComponentsAndMakeCaches(svgFile);
		HtmlDiv div = pageCache.createHTMLFromTextList();
		File htmlFile = new File(targetDir, "page1.html");
//		LOG.debug("HT "+htmlFile);
		HtmlHtml.wrapAndWriteAsHtml(div, htmlFile);

	}


	
	// ============================
	
	private Double getCommonFontSize(List<SVGRect> rowRectList) {
		Double fontSize = null;
		for (SVGRect rect : rowRectList) {
			String s = rect.getChildElements().get(0).getValue();
			try {
				fontSize = Double.parseDouble(s.split("\\s*;\\s*")[0]);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("s "+s, nfe);
			}
		}
		return fontSize;
	}
}
