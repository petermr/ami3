package org.contentmine.graphics.svg.cache;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLineList;
import org.junit.Test;

import junit.framework.Assert;

/** tests LineBoxTree (e.g. axial boxes)
 * 
 * @author pm286
 *
 */
public class LineBoxTreeTest extends AbstractCacheTest {
	private static final Logger LOG = Logger.getLogger(LineBoxTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testPage() {
		String root = "fullPageNestedPanels";
		File pageFile = new File(CACHE_TEST, root+".svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(pageFile);
		SVGLineList lineList = LineCache.createLineCacheAndDisplay(CACHE_TEST, root+".lines.svg", svgElement);
		Assert.assertEquals("lines",  59, lineList.size());

		LineBoxTree lineBoxTree = new LineBoxTree(svgElement);
	}


	
}
