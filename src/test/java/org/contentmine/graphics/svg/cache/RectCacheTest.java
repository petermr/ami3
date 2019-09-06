package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

/** tests svgElements containing rects
 * 
 * @author pm286
 *
 */
public class RectCacheTest {


	/** a 2-level header bar which involves rowspans
	 * the actual body of the table is lines, not rects I think.
	 * 
	 */
	@Test
	public void testRect11() {
		List<SVGRect> rectList = extractAndDisplayRects("table11.svg", "rect11.svg");
		Assert.assertEquals("rects", 11, rectList.size());
	}

	// ============================
	
	static List<SVGRect> extractAndDisplayRects(String svgName, String outName) {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.TABLE_RECT_DIR, svgName));
		ComponentCache componentCache = new ComponentCache();
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		RectCache rectCache = componentCache.getOrCreateRectCache();
		SVGSVG.wrapAndWriteAsSVG(rectCache.getOrCreateConvertedSVGElement(), new File("target/table/cache/" + outName));
		List<SVGRect> rectList = rectCache.getOrCreateRectList();
		return rectList;
	}
}
