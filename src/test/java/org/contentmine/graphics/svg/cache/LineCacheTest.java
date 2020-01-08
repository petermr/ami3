package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.junit.Assert;
import org.junit.Test;

/** tests svgElements containing lines
 * 
 * @author pm286
 *
 */
public class LineCacheTest {


	private static final File OUTDIR = new File("target/table/cache/");

	/** a 2-level header bar which involves rowspans
	 * the actual body of the table is lines, 
	 * 
	 */
	@Test
	public void testLine11() {
		System.out.println(SVGHTMLFixtures.TABLE_RECT_DIR);
		SVGLineList lineList = LineCache.extractAndDisplayLines(SVGHTMLFixtures.TABLE_RECT_DIR, "table11.svg", OUTDIR, "line11.svg");
		// all boxes are formed from 4 lines 7*14 + 2 + 4 + 1+7 + 7*2 + 14 * 8 === 98+14+14+112 => 238
		// two lines in top row are butted
		Assert.assertEquals("lines", 238, lineList.size());
	}

	// ============================

}
