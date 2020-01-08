package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.junit.Test;

import junit.framework.Assert;

/** tests LineBoxTree (e.g. axial boxes)
 * 
 * @author pm286
 *
 */
public class LineBoxCacheTest extends AbstractCacheTest {
	private static final Logger LOG = Logger.getLogger(LineBoxCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testDisplayLinesInPage() {
		String root = "fullPageNestedPanels";
		File pageFile = new File(CACHE_TEST, root+".svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(pageFile);
		SVGLineList lineList = LineCache.createLineCacheAndDisplay(CACHE_TEST, root+".lines.svg", svgElement);
		Assert.assertEquals("lines",  59, lineList.size());
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


	
}
