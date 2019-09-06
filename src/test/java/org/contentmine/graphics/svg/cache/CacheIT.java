package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGRect;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("This really should be in POM or CL")

public class CacheIT {
	private static final Logger LOG = Logger.getLogger(CacheIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	/** multiple blocks of rects
	 * based on a 5-col grid, with some ragged ends.
	 * most span the table and several are in blocks  which could make a spanned table
	 * 
	 */
	@Test
	public void testRect110() {
		List<SVGRect> rectList = RectCacheTest.extractAndDisplayRects("table110.svg", "rect110.svg");
		Assert.assertEquals("rects", 110, rectList.size());
	}
	@Test
	public void testSingleFigure() throws IOException {
		String fileRoot = "10.1186_s12885-016-2685-3_page7";
		ComponentCache cache = new ComponentCache();
		File inputSVGFile = new File(SVGHTMLFixtures.G_S_FIGURE_DIR, fileRoot+".svg");
		cache.readGraphicsComponentsAndMakeCaches(inputSVGFile);
		List<Real2Range> boundingBoxes = cache.getMergedBoundingBoxes(2.0);
		SVGCacheTest.displayBoxes(new File("target/plot/debug"), cache, fileRoot, boundingBoxes, "pink");
		
	}
	// =====================
	@Test
	public void testCreateHTMLPageAllCrop() throws IOException {
		HtmlElement div = new HtmlDiv();
		for (int i = 1; i <= 9; i++) {
			File svgFile = new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga/compact/fulltext-page"+i+".svg");
			ComponentCache cache = new ComponentCache();
			cache.readGraphicsComponentsAndMakeCaches(svgFile);
			TextCache textCache = cache.getOrCreateTextCache();
			div.appendChild(new HtmlP("======page "+i+" L======="));
			RealRange yr = new RealRange(33, 698);
			HtmlElement htmlElementL = textCache.createHtmlFromBox(new RealRange(0, 260), yr);
			div.appendChild(htmlElementL);
			div.appendChild(new HtmlP("======page "+i+" R======="));
			HtmlElement htmlElementR = textCache.createHtmlFromBox(new RealRange(250, 550), yr);
			div.appendChild(htmlElementR);
		}
		XMLUtil.debug(div, new File("target/html/pages.html"), 1);
	
	}

}
