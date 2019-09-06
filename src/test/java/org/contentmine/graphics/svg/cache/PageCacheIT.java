package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.layout.SuperPixelArrayManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.fonts.StyleRecordFactory;
import org.contentmine.graphics.svg.fonts.StyleRecordSet;
import org.contentmine.graphics.svg.util.SuperPixelArray;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore("This really should be in POM or CL")
public class PageCacheIT {
	private static final Logger LOG = Logger.getLogger(PageCacheIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final double PARALLEL_DSPLAY_X = 550.;
	boolean rightPage;
	boolean leftPage;
	/** a page with a page header, two tables and some text
	 * get spanning rects
	 * 
	 */
	@Test
	public void testFindWhitespace() {
		ComponentCache componentCache = new ComponentCache();
		componentCache.extractAndDisplayComponents(new File(SVGHTMLFixtures.TABLE_PAGE_DIR, "page6.svg"), new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, "page6.svg"));
		TextCache textCache = componentCache.getOrCreateTextCache();
		AbstractCMElement g = textCache.createCompactedTextsAndReplace();
		Assert.assertEquals("bounding boxes", 118/*131*/, componentCache.getBoundingBoxList().size());
		double dx = 5;
		double dy = 5;
		SVGG gg = componentCache.createWhitespaceG(dx, dy);
		SVGSVG.wrapAndWriteAsSVG(gg, new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, "whitespace6.svg"));
	}
	/** a page with a page header, two tables and some text
	 * get spanning rects
	 */
	@Test
	public void testPage6Rects() {
		ComponentCache componentCache = new ComponentCache();
		List<? extends SVGElement> componentList = componentCache.extractAndDisplayComponents(
				new File(SVGHTMLFixtures.TABLE_PAGE_DIR, "page6.svg"), new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, "page6.svg"));
		Assert.assertEquals("components", /*2995*/ 2982, componentList.size());
		RectCache rectCache = componentCache.getOrCreateRectCache();
		Assert.assertEquals("rects", 3, rectCache.getOrCreateRectList().size());
		List<SVGRect> spanningRectList = rectCache.getOrCreateHorizontalPanelList();
		Assert.assertEquals("panels", 3, spanningRectList.size());
	}
	/** a page with a page header, two tables and some text
	 * get spanning rects
	 * 
	 */
	@Test
	public void testPage6Texts() {
		ComponentCache componentCache = new ComponentCache();
		List<? extends SVGElement> componentList = componentCache.extractAndDisplayComponents(
				new File(SVGHTMLFixtures.TABLE_PAGE_DIR, "page6.svg"), new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, "page6.svg"));
		Assert.assertEquals("components", 2982/*2995*/, componentList.size());
		TextCache textCache = componentCache.getOrCreateTextCache();
		List<SVGText> textList = textCache.getOrCreateOriginalTextList();
		Assert.assertEquals("components", 2964, textList.size());
		SVGG g = textCache.createCompactedTextsAndReplace();
		List<SVGText> convertedTextList = SVGText.extractSelfAndDescendantTexts(g);
		Assert.assertEquals("compacted", 100, convertedTextList.size());
		textList = textCache.getOrCreateOriginalTextList();
		Assert.assertEquals("compacted", 100, textList.size());
		SVGSVG.wrapAndWriteAsSVG(g, new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, "texts6.svg"));
	}
	/** analyses a group of papers and outputs diagrams of the whitespace.
	 * 
	 */
	@Test
	public void testArticlesWhitespace() {
		File[] journalDirs = SVGHTMLFixtures.G_S_TABLE_DIR.listFiles();
		for (File journalDir : journalDirs) {
			System.out.print("*");
			String root = journalDir.getName();
			File outDir = new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, root);
			File svgDir = new File(journalDir, "svg");
			if (svgDir.listFiles() == null) continue;
			for (File svgFile : svgDir.listFiles()) {
				System.out.print(".");
				String basename = FilenameUtils.getBaseName(svgFile.toString());
				ComponentCache componentCache = new ComponentCache();
				componentCache.extractAndDisplayComponents(svgFile, new File(outDir, basename+".convert.svg"));
				TextCache textCache = componentCache.getOrCreateTextCache();
				AbstractCMElement g = textCache.createCompactedTextsAndReplace();
				SVGG gg = componentCache.createWhitespaceG(5, 5);
				SVGSVG.wrapAndWriteAsSVG(gg, new File(outDir, basename+".textline.svg"));
			}
		}		
	}
	/** analyze group of articles for superpixels.
	 * 
	 */
	@Test
	@Ignore
	public void testSuperPixelArrayForArticles() {
		// these are non-compact
//		File[] journalDirs = SVGHTMLFixtures.G_S_TABLE_DIR.listFiles();
		CProject project = new CProject(new File("src/test/resources/closed/temp/"));
		boolean draw = false;
		File outDir0 = new File("target/closed/temp/");
		CTreeList cTreeList = project.getOrCreateCTreeList();
		Assert.assertEquals(3,  cTreeList.size());
		for (CTree cTree : cTreeList) {
			SuperPixelArrayManager spaManager = cTree.createSuperPixelArrayManager();
			File outDir = new File(outDir0, cTree.getName());
			spaManager.getOrCreateLeftPageSPA().draw(new SVGG(), new File(outDir, "leftPixels.svg"), true);
			spaManager.getOrCreateRightPageSPA().draw(new SVGG(), new File(outDir, "rightPixels.svg"), true);

		}
	}
	
	@Test
	@Ignore("too long and missing file")
	public void testArticleWhitespace() {
		String root = "10.1136_bmjopen-2016-011048";
		File outDir = new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, root);
		File journalDir = new File(SVGHTMLFixtures.G_S_TABLE_DIR, root);
		File svgDir = new File(journalDir, "svg");
		for (File svgFile : svgDir.listFiles()) {
			System.out.print(".");
			String basename = FilenameUtils.getBaseName(svgFile.toString());
			ComponentCache componentCache = new ComponentCache();
			componentCache.extractAndDisplayComponents(svgFile, new File(outDir, basename+".convert.svg"));
			TextCache textCache = componentCache.getOrCreateTextCache();
			AbstractCMElement g = textCache.createCompactedTextsAndReplace();
			SVGG gg = componentCache.createWhitespaceG(5, 5);
			SVGSVG.wrapAndWriteAsSVG(gg, new File(outDir, basename+".textline.svg"));
		}
		
	}
	@Test
	@Ignore("missing file")
	public void testSuperPixelArray() {
		String root = "10.1136_bmjopen-2016-011048";
		File outDir = new File(SVGHTMLFixtures.TARGET_TABLE_CACHE_DIR, root);
		File journalDir = new File(SVGHTMLFixtures.G_S_TABLE_DIR, root);
		File svgDir = new File(journalDir, "svg");
		SuperPixelArray leftPixelArray = null;
		SuperPixelArray rightPixelArray = null;
		boolean left = true;
		boolean right = false;
		File[] listFiles = svgDir.listFiles();
		if (listFiles == null) {
			throw new RuntimeException("no files in "+svgDir);
		}
		for (File svgFile : listFiles) {
			right = !right;
			left = !left;
			System.out.print(".");
			String basename = FilenameUtils.getBaseName(svgFile.toString());
			AbstractCMElement svgElement = SVGElement.readAndCreateSVG(svgFile);
			ComponentCache componentCache = new ComponentCache();
			componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
			TextCache textCache = componentCache.getOrCreateTextCache();
			textCache.createCompactedTextsAndReplace();
			Real2Range bbox = Real2Range.createTotalBox(componentCache.getBoundingBoxList());
			LOG.trace(">> "+bbox+" "+componentCache.getBoundingBoxList().size());
			SuperPixelArray superPixelArray = new SuperPixelArray(new Int2Range(bbox));
			superPixelArray.setPixels(1, componentCache.getBoundingBoxList());
			SVGG g = new SVGG();
			superPixelArray.draw(g, new File(outDir, basename+".superPixels.svg"));
			if (left) {
				leftPixelArray = superPixelArray.plus(leftPixelArray);
			}
			if (right) {
				rightPixelArray = superPixelArray.plus(rightPixelArray);
			}
			
		}
		leftPixelArray.draw(new SVGG(), new File(outDir, "leftPixels.svg"), true);
		rightPixelArray.draw(new SVGG(), new File(outDir, "rightPixels.svg"), true);
	}
	/** extraction of equations by text style
	 * 
	 */
	@Test
	public void testDisplayPage() {
		File svgFile = new File(SVGHTMLFixtures.G_S_FONTS_DIR, "styledequations.svg");
		ComponentCache cache = new ComponentCache();
		cache.readGraphicsComponentsAndMakeCaches(svgFile);
		List<SVGText> svgTexts = cache.getOrCreateTextCache().getOrCreateOriginalTextList();
		StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
		StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
		SVGElement g = styleRecordSet.createStyledTextBBoxes(svgTexts);
		List<SVGLine> horizontalLines = cache.getOrCreateLineCache().getOrCreateHorizontalLineList();
		g.appendChildren(horizontalLines);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/demos/", "equations.svg"));
	}
	@Test
	public void testPages() {
		for (int i = 1; i <= 9; i++) {
			File svgFile = new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga/compact/fulltext-page"+i+".svg");
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
			SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, PageCacheTest.MAINTEXT+i+".grid.svg"), 1200., 700.);
		}
	}
	@Test
		public void testPagesPlos() {
			File targetDir = new File("target/demos/timmermans/");
			RealRange leftXRange = new RealRange(13, 300);
			RealRange rightXRange = new RealRange(200, 600);
			RealRange yRange = new RealRange(63, 683);
			File cTreeDir = new File(SVGHTMLFixtures.G_S_PAGE_DIR, "TimmermansPLOS");
			List<Real2Range> boxes = Arrays.asList(new Real2Range[] {
	//				new Real2Range(leftXRange, yRange),
					new Real2Range(rightXRange, yRange),
			});
			analyzeCTree(targetDir, boxes, cTreeDir);
		}
	private void analyzeCTree(File targetDir, List<Real2Range> boxes, File cTreeDir) {
		for (int i = 1; i <= 99; i++) {
			File svgFile = new File(cTreeDir, PageCacheTest.FULLTEXT_PAGE+i+".svg");
			if (!svgFile.exists()) {
				LOG.info("exited after non-existsnt :"+svgFile);
				break;
			}
			ComponentCache cache = new ComponentCache();
			cache.readGraphicsComponentsAndMakeCaches(svgFile);
			TextCache textCache = cache.getOrCreateTextCache();
			SVGG g = new SVGG();
			for (Real2Range box : boxes) {
				SVGG gg = textCache.extractYcoordAPs(box);
				g.appendChild(gg);
				gg = textCache.extractStyledTextBBoxes(box);
				gg.setTransform(new Transform2(new Vector2(PARALLEL_DSPLAY_X, 0.)));
				g.appendChild(gg);
			}
			SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, PageCacheTest.MAINTEXT+i+".grid.svg"), 1200., 700.);
		}
	}

}
