package org.contentmine.ami.tools;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.AbstractCache;
import org.contentmine.graphics.svg.cache.AbstractCache.CacheType;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.LineBox;
import org.contentmine.graphics.svg.cache.LineBoxCache;
import org.contentmine.graphics.svg.cache.LineCache;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class AMIGraphicsTest extends AbstractAMITest {

	private static final Logger LOG = Logger.getLogger(AMIGraphicsTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** mainly for development of code*/
	public void testSinglePage1() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "fulltext-page.1.svg");
		File outdir = new File(svgDir, "page.1/");
		
		analyzeSinglePage(svgFile, outdir, 4);
	}

	@Test
	/** mainly for development of code*/
	public void testSinglePage4() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "fulltext-page.4.svg");
		File outdir = new File(svgDir, "page.4/");
		
		analyzeSinglePage(svgFile, outdir, 14);
	}

	@Test
	@Ignore // bug solved
	/** mainly for development of code*/
	public void testSinglePage4_1_1() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "panel.4.1.1.svg");
		File outdir = new File(svgDir, "panel.4.1.1/");
		
		analyzeSinglePage(svgFile, outdir, 1);
	}

	@Test
	@Ignore // bug solved
	/** mainly for development of code*/
	public void testSinglePage4_1_12() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "panel.4.1.12.svg");
		File outdir = new File(svgDir, "panel.4.1.12/");
		
		analyzeSinglePage(svgFile, outdir, 2);
	}

	@Test
	@Ignore // bug solved
	/** mainly for development of code*/
	public void testSinglePage4_1_123() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "panel.4.1.123.svg");
		File outdir = new File(svgDir, "panel.4.1.123/");
		
		analyzeSinglePage(svgFile, outdir, /*3 right */ 2);
	}

	@Test
	@Ignore // bug solved
	/** mainly for development of code*/
	public void testSinglePage4_1_123min() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "panel.4.1.123min.svg");
		File outdir = new File(svgDir, "panel.4.1.123min/");
		
		analyzeSinglePage(svgFile, outdir, 3);
	}


	private void analyzeSinglePage(File svgFile, File outdir, int lineBoxCount) {
		ComponentCache componentCache = ComponentCache.readAndCreateComponentCache(svgFile);
		LineCache lineCache = componentCache.getOrCreateLineCache();
//		LOG.debug(lineCache);
		List<AbstractCache> cacheList = componentCache.getCaches(Arrays.asList(new CacheType[] {CacheType.linebox}));
		Assert.assertEquals("cacheList", 1, cacheList.size());
		LineBoxCache lineboxCache = (LineBoxCache) cacheList.get(0);
		Assert.assertEquals("lineboxes", lineBoxCount, lineboxCache.getOrCreateLineBoxList().size());
		SVGElement svgElement = lineboxCache.getOrCreateConvertedSVGElement();
		List<SVGElement> lineboxes = SVGUtil.getQuerySVGElements(svgElement, "/*[local-name()='g']/*[local-name()='g' and @class='linebox']");
		lineboxes.forEach(lb -> lb.insertGraphicalBoundingBox().setFill("pink").setStroke("blue").setStrokeWidth(1.0).setOpacity(0.3));
			
		outdir.mkdirs();
		File outSvgFile = new File(outdir, "linebox.svg");
		LOG.debug(outSvgFile);
		if (svgElement.getChildElements().size() > 0) {
			SVGSVG.wrapAndWriteAsSVG(svgElement, outSvgFile);
		}
		assertExists(outSvgFile);
	}


	@Test
	/** mainly for development of code*/
	public void testSinglePanel() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "panel.1.1.svg");
		ComponentCache componentCache = ComponentCache.readAndCreateComponentCache(svgFile);
		LineCache lineCache = componentCache.getOrCreateLineCache();
		LOG.debug(lineCache);
		List<AbstractCache> cacheList = componentCache.getCaches(Arrays.asList(new CacheType[] {CacheType.linebox}));
		Assert.assertEquals("cacheList", 1, cacheList.size());
		LineBoxCache lineboxCache = (LineBoxCache) cacheList.get(0);
		Assert.assertEquals("caches", 1, lineboxCache.getOrCreateLineBoxList().size());
		SVGElement svgElement = lineboxCache.getOrCreateConvertedSVGElement();
		List<SVGElement> lineboxes = SVGUtil.getQuerySVGElements(svgElement, "/*[local-name()='g']/*[local-name()='g' and @class='linebox']");
		Assert.assertEquals("caches", 1, lineboxes.size());
			
		File outdir = new File(svgDir, "panel.1.1/");
		outdir.mkdirs();
		File outSvgFile = new File(outdir, "linebox.svg");
		LOG.debug(outSvgFile);
		if (svgElement.getChildElements().size() > 0) {
			SVGSVG.wrapAndWriteAsSVG(svgElement, outSvgFile);
		}
		assertExists(outSvgFile);
	}

	@Test
	/** mainly for development of code*/
	public void testTwoPanel() {
		File svgDir = new File(PDF2SVG2, "problems/lichtenburg19a/svg/");
		File svgFile = new File(svgDir, "panel.1.12.svg");
		ComponentCache componentCache = ComponentCache.readAndCreateComponentCache(svgFile);
		LineCache lineCache = componentCache.getOrCreateLineCache();
		LOG.debug(lineCache);
		List<AbstractCache> cacheList = componentCache.getCaches(Arrays.asList(new CacheType[] {CacheType.linebox}));
		Assert.assertEquals("cacheList", 1, cacheList.size());
		LineBoxCache lineboxCache = (LineBoxCache) cacheList.get(0);
		List<LineBox> lineBoxList = lineboxCache.getOrCreateLineBoxList();
		Assert.assertEquals("caches", 2, lineBoxList.size());
		SVGElement svgElement = lineboxCache.getOrCreateConvertedSVGElement();
		List<SVGElement> lineboxes = SVGUtil.getQuerySVGElements(svgElement, "/*[local-name()='g']/*[local-name()='g' and @class='linebox']");
		Assert.assertEquals("caches", 2, lineboxes.size());
			
		File outdir = new File(svgDir, "panel.1.12/");
		outdir.mkdirs();
		
		checkLineBox(lineBoxList.get(0), new File(outdir, "linebox0.svg"));
		checkLineBox(lineBoxList.get(1), new File(outdir, "linebox1.svg"));
			

	}


	private void checkLineBox(LineBox lineBox, File outSvgFile) {
		LOG.debug(outSvgFile);
		SVGElement svgElement = lineBox.getSVGElement();
		if (svgElement.getChildElements().size() > 0) {
			SVGSVG.wrapAndWriteAsSVG(svgElement, outSvgFile);
		}
		assertExists(outSvgFile);
	}

	@Test
	public void testSingleTreeCache() {
		File testProject = new File(PDF2SVG2, "problems/lichtenburg19a");
		File file = new File(testProject, "svg/page.1/line.svg");
		file.delete();
		Assert.assertTrue(file+" deleted", !file.exists());
		String cmd = "-t "+testProject
				+ " --forcemake"
				+ " -v"
	//			+ " --log4j org.contentmine.graphics.svg.cache.LineBoxCache Level.INFO"
				+ " --cache line polyline polygon shape text linebox"
				;
		new AMIGraphicsTool().runCommands(cmd);
//		System.out.println("created file "+file);
		Assert.assertTrue(file+" exists", file.exists() && file.getAbsolutePath().endsWith("src/test/resources/org/contentmine/ami/pdf2svg2/problems/lichtenburg19a/svg/page.1/line.svg"));
	}

	@Test
	public void testCompleteProjectCache() {
		File testProject = new File(PDF2SVG2, "test");
		String cmd = "-p "+testProject
				+ " --forcemake"
				+ " -v"
				+ " --log4j org.contentmine.graphics.svg.cache.LineBoxCache Level.INFO"
				+ " --cache line polyline polygon shape text linebox"
				;
		new AMIGraphicsTool().runCommands(cmd);
		assertExists(new File(PDF2SVG2, "test/lichtenburg19a/svg/page.1/linebox.svg"));
		assertExists(new File(PDF2SVG2, "test/lichtenburg19a/svg/page.4/polygon.svg"));
	}


	private void assertExists(File file) {
		Assert.assertTrue(file+" exists", file.exists());
	}
	
	@Test
	/** runs the whole corpus
	 * LONG
	 */
	public void testOIL186CacheIT() {
		File testProject = AbstractAMITest.OIL186;
		String cmd = "-p "+testProject
				+ " -v"
				+ " --log4j org.contentmine.ami.tools.AMIGraphicsTool INFO"
				+ " --cache line polyline polygon shape text linebox"
				;
		new AMIGraphicsTool().runCommands(cmd);
	}
	
	@Test
	/** this hangs the program. test to find out where.
	 * maybe test should be in CacheTests.
	 */
	public void testLargeSVG() {
		File largeSVG = new File(SRC_TEST_SVG, "problems/fulltext-page.5LARGE.svg");
		// problem (starts) here
		ComponentCache componentCache = ComponentCache.createComponentCache(largeSVG);
	}
	
	@Test
	public void testVeryLargeHangsIT() {
		File testFile = new File(PDF2SVG2, "problems/PMC6364917");
		String cmd = "-t "+testFile
				+ " --forcemake"
				+ " --maxprimitives 500000"
				;
		new AMIPDFTool().runCommands(cmd);

	}
	
	@Test
	/** seems to write each image twice.
	 * 
	 */
	public void testDuplicateImagesIT() {
		File testFile = new File(PDF2SVG2, "problems/PMC5963300");
		String cmd = "-t "+testFile
				+ " --forcemake"
				+ " --maxprimitives 5000"
				;
		new AMIPDFTool().runCommands(cmd);

	}
	
}
