package org.contentmine.svg2xml.table;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealArray.Monotonicity;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Test;

import junit.framework.Assert;

/** some tables have unusual column order.
 * This is really a tests for SVGText, I think
 * 
 * @author pm286
 *
 */
public class ShuffledColumnsTest {
	private static final Logger LOG = Logger.getLogger(ShuffledColumnsTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testShuffledColumns() {
		File infile = new File(SVG2XMLFixtures.TABLE_TYPE_TEXT_DIR, "shuffledColumns.svg");
		Assert.assertTrue(infile.exists());
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(infile);
		LOG.debug(svg.toXML());
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svg);
		for (SVGText text : texts) {
			RealArray xcoords = text.getXArray();
			if (xcoords != null) {
				IntSet xidx = xcoords.indexSortAscending();
				if (!xidx.isCountingFromZero()) {
					xcoords = xcoords.createReorderedArray(xidx);
					text.reorderByIndex(xidx);
				}
				Monotonicity monotonicity = xcoords.getMonotonicity();
				Assert.assertEquals(Monotonicity.INCREASING, monotonicity);
			}
		}
	}
	
}
