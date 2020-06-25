package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Test;

import junit.framework.Assert;

/** tests LineBoxTree (e.g. axial boxes)
 * 
 * @author pm286
 *
 */
public class LineBoxCacheTest extends AbstractCacheTest {
	private static final Logger LOG = LogManager.getLogger(LineBoxCacheTest.class);
@Test
	public void testDisplayLinesInPage() {
		String root = "fullPageNestedPanels";
		File pageFile = new File(CACHE_TEST, root+".svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(pageFile);
		SVGLineList lineList = LineCache.createLineCacheAndDisplay(CACHE_TEST, root+".lines.svg", svgElement);
		Assert.assertEquals("lines",  59, lineList.size());
		File svgOutfile = new File(pageFile.toString().replace(".svg",  ".out.svg"));
		SVGSVG.wrapAndWriteAsSVG(lineList, svgOutfile);
		Assert.assertTrue(svgOutfile+" exists", svgOutfile.exists());
	}


	@Test
	public void testCreateLineCachePage1() {
		String root = "panel1";
		File pageFile = new File(CACHE_TEST, root+".svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(pageFile);
		LineCache lineCache = LineCache.createLineCache(svgElement);
		List<SVGLine> horizontalLineList = lineCache.getOrCreateHorizontalLineList();
		Assert.assertEquals("lines",  5, horizontalLineList.size());
		List<SVGLine> verticalLineList = lineCache.getOrCreateVerticalLineList();
		Assert.assertEquals("lines",  6, verticalLineList.size());

		/*LineBoxCache lineBoxTree = */new LineBoxCache().createLineBoxes(horizontalLineList, verticalLineList);
	}

	@Test
	public void testCreateLineCachePage12() {
		String root = "panel12";
		File pageFile = new File(CACHE_TEST, root+".svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(pageFile);
		LineCache lineCache = LineCache.createLineCache(svgElement);
		List<SVGLine> horizontalLineList = lineCache.getOrCreateHorizontalLineList();
		Assert.assertEquals("lines",  17, horizontalLineList.size());
		List<SVGLine> verticalLineList = lineCache.getOrCreateVerticalLineList();
		Assert.assertEquals("lines",  10, verticalLineList.size());

		/*LineBoxCache lineBoxTree = */new LineBoxCache().createLineBoxes(horizontalLineList, verticalLineList);
	}

	@Test
	public void testCreateLineCache() {
		String root = "fullPageNestedPanels";
		File pageFile = new File(CACHE_TEST, root+".svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(pageFile);
		LineCache lineCache = LineCache.createLineCache(svgElement);
		List<SVGLine> horizontalLineList = lineCache.getOrCreateHorizontalLineList();
		Assert.assertEquals("lines",  31, horizontalLineList.size());
		List<SVGLine> verticalLineList = lineCache.getOrCreateVerticalLineList();
		Assert.assertEquals("lines",  24, verticalLineList.size());

		/*LineBoxCache lineBoxTree = */new LineBoxCache().createLineBoxes(horizontalLineList, verticalLineList);
	}

	@Test
	public void testCreateLineCache1() {
		String root = "fullPageNestedPanels1";
		File pageFile = new File(CACHE_TEST, root+".svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(pageFile);
		LineCache lineCache = LineCache.createLineCache(svgElement);
		List<SVGLine> horizontalLineList = lineCache.getOrCreateHorizontalLineList();
		Assert.assertEquals("lines",  176, horizontalLineList.size());
		List<SVGLine> verticalLineList = lineCache.getOrCreateVerticalLineList();
		Assert.assertEquals("lines",  210, verticalLineList.size());

		/*LineBoxCache lineBoxTree = */new LineBoxCache().createLineBoxes(horizontalLineList, verticalLineList);
	}


	
}
