package org.contentmine.svg2xml.table;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.util.SVGFilenameUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Multiset;

public class RuledTest {
	private static final Logger LOG = Logger.getLogger(RuledTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String RULED = "ruled";
	private static final File RULED_DIR = new File(SVG2XMLFixtures.TABLE_DIR, RULED);
	private static final File RULED2001_1 = new File(RULED_DIR, "/_bmj.2001.323.1–5/tables/table1/table.svg");
	private static final File RULED2001_1MICRO = new File(RULED_DIR, "/_bmj.2001.323.1–5/tables/table1/tablemicro.svg");
	private static final File RULED2001_2 = new File(RULED_DIR, "/_bmj.2001.323.1–5/tables/table2/table.svg");
	private static final File RULED2001_3 = new File(RULED_DIR, "/_bmj.2001.323.1–5/tables/table3/table.svg");
	private static final File RULED2001_4 = new File(RULED_DIR, "/_bmj.2001.323.1–5/tables/table4/table.svg");
	static final File RULED1007_1 = new File(RULED_DIR, "/10.1007.s13142-010-0006-y/tables/table1/table.svg");
	private static final File RULED1007_2 = new File(RULED_DIR, "/10.1007.s13142-010-0006-y/tables/table1/table.svg");
	private static final File RULED1007_1MICRO = new File(RULED_DIR, "/10.1007.s13142-010-0006-y/tables/table1/tablemicro.svg");
	
	
	static File[] RULED_FILES = new File[] {
		RULED2001_1,
		RULED2001_2,
		RULED2001_3,
		RULED2001_4,
		RULED1007_1,
		RULED1007_2,
	};
	
	private static final double EPS_ANGLE = 0.001;


	/** test conversion of thin rects to lines
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testThinRectToLineAndAttributes0() throws FileNotFoundException {
		File svgFile = RULED2001_1MICRO;
		ComponentCache svgStore = new ComponentCache();
		svgStore.setSplitAtMove(true);
		svgStore.readGraphicsComponentsAndMakeCaches(svgFile);
		AbstractCMElement svgElement = (AbstractCMElement) svgStore.getExtractedSVGElement();
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(svgElement);
		Assert.assertEquals("lines", 19, lineList.size());
		SVGLine svgLine0 = lineList.get(0);
		Assert.assertEquals("line0", "none", svgLine0.getFill());
		Assert.assertEquals("line0", "line1", svgLine0.getId());
		Assert.assertEquals("line0", 0.469, svgLine0.getStrokeWidth(), 0.001);
		Assert.assertEquals("line0", 66.332, svgLine0.getXY(0).getX(), 0.001);
		Assert.assertEquals("line0", 59.319, svgLine0.getXY(0).getY(), 0.001);
		RealArray widthArray = new RealArray();
		RealArray lengthArray = new RealArray();
		for (int i = 0; i < lineList.size(); i++) {
			SVGLine line = lineList.get(i);
			widthArray.addElement(line.getStrokeWidth());
			lengthArray.addElement(Util.format(line.getLength(), 3));
		}
		Assert.assertEquals("width", 
				"(0.469,0.234,0.234,0.234,0.234,0.093,0.093,0.093,0.093,0.093,0.093,0.093,0.093,0.093,0.093,0.093,0.093,0.093,0.093)",
				widthArray.toString());
		Assert.assertEquals("length", 
				"(409.205,408.941,146.852,146.853,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941,408.941)",
				lengthArray.toString());
		List<Multiset.Entry<Double>> lengthSet = MultisetUtil.createListSortedByValue(lengthArray.createDoubleMultiset(3));
		Assert.assertEquals("lengths", "[146.852, 146.853, 408.941 x 16, 409.205]",  lengthSet.toString());
		List<Multiset.Entry<Double>> widthSet = MultisetUtil.createListSortedByValue(widthArray.createDoubleMultiset(3));
		Assert.assertEquals("widths", "[0.093 x 14, 0.234 x 4, 0.469]",  widthSet.toString());
		File svgOutFile = SVGFilenameUtils.getCompactSVGFilename(new File("target/"+RULED), new File("target/"+RULED+"/"+svgFile.getPath()+"micro"));
		SVGSVG.wrapAndWriteAsSVG(svgElement, svgOutFile, 1000., 1000.);
	}
	

	// ==============================================
	
	static void debugLines(List<SVGLine> lineList, File svgOutfile) {
		SVGG g = new SVGG();
		for (SVGLine line : lineList) {
			g.appendChild(line.copy());
			g.appendChild(new SVGCircle(line.getXY(0), 2.0));
			g.appendChild(new SVGCircle(line.getXY(1), 2.0));
		}
		SVGSVG.wrapAndWriteAsSVG(g, svgOutfile);
	}
	
	private Real2Range makeResizedBbox(SVGRect rect, int delta) {
		Real2Range rectBox = rect.getBoundingBox();
		rectBox.extendBothEndsBy(Direction.HORIZONTAL, delta, delta);
		rectBox.extendBothEndsBy(Direction.VERTICAL, delta, delta);
		return rectBox;
	}
}
