package org.contentmine.svg2xml.table;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.util.SVGFilenameUtils;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class FilledTableAnalyzerTest {
	private static final Logger LOG = Logger.getLogger(FilledTableAnalyzerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String FILLED = "filled";
	private static final File FILLED_DIR = new File(SVG2XMLFixtures.TABLE_DIR, FILLED);
	private static final File FILLED1007_2 = new File(FILLED_DIR, "/_10.1007.s00038-009-8028-2/tables/table2/table.svg");
	private static final File FILLED1007_3 = new File(FILLED_DIR, "/_10.1007.s00038-009-8028-2/tables/table3/table.svg");
	private static final File FILLED1007_4 = new File(FILLED_DIR, "/_10.1007.s00038-009-8028-2/tables/table4/table.svg");
	private static final File FILLED1016Y_1 = new File(FILLED_DIR, "/_10.1016.j.ypmed.2009.07.022/tables/table1/table.svg");
	private static final File FILLED1016Y_2 = new File(FILLED_DIR, "/_10.1016.j.ypmed.2009.07.022/tables/table2/table.svg");
	private static final File FILLED1016Y_3 = new File(FILLED_DIR, "/_10.1016.j.ypmed.2009.07.022/tables/table3/table.svg");
	private static final File FILLED1016_1 = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table1/table.svg");
	private static final File FILLED1016_2 = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table2/table.svg");
	private static final File FILLED1016_2MICRO = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table2/tablemicro.svg");
	private static final File FILLED1016_2MINI = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table2/tablemini.svg");
	private static final File FILLED1016_3 = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table3/table.svg");
	private static final File FILLED1136_2 = new File(FILLED_DIR, "/10.1136.bmjopen-2016-12335/tables/table2/table.svg");
	private static final File FILLED1136_5 = new File(FILLED_DIR, "/10.1136.bmjopen-2016-12335/tables/table5/table.svg");
	private static final File FILLED1136_6 = new File(FILLED_DIR, "/10.1136.bmjopen-2016-12335/tables/table6/table.svg");

	private static final double LEFT_PAD = -2.0;
	private static final double RIGHT_PAD = -2.0;
	private static final double BOTTOM_PAD = -2.0;
	private static final double TOP_PAD = -2.0;

	
	static File[] FILLED_FILES = new File[] {
		FILLED1007_2,
		FILLED1007_3,
		FILLED1007_4,
		FILLED1016Y_1,
		FILLED1016Y_2,
		FILLED1016Y_3,
		FILLED1016_1,
		FILLED1016_2,
		FILLED1016_3,
		FILLED1136_2,
		FILLED1136_5,
		FILLED1136_6,
	};
	
	private static final double EPS_ANGLE = 0.001;


	@Test
	public void testStyles() throws FileNotFoundException {
		File svgFile = FILLED1016_2MICRO;
		ComponentCache svgStore = new ComponentCache();
		svgStore.readGraphicsComponentsAndMakeCaches(svgFile);
		AbstractCMElement svgElement = (AbstractCMElement) svgStore.getExtractedSVGElement();
		File svgOutFile = SVGFilenameUtils.getCompactSVGFilename(new File("target/"+FILLED), new File("target/"+FILLED+"/"+svgFile.getPath()+"micro"));
		SVGSVG.wrapAndWriteAsSVG(svgElement, svgOutFile, 1000., 1000.);
	}

	private static final File FILLED1007_2_ELEM = new File(FILLED_DIR, "/_10.1007.s00038-009-8028-2/tables/table2/table.svg.elems.svg");
	private static final File FILLED1007_3_ELEM = new File(FILLED_DIR, "/_10.1007.s00038-009-8028-2/tables/table3/table.svg.elems.svg");
	private static final File FILLED1007_4_ELEM = new File(FILLED_DIR, "/_10.1007.s00038-009-8028-2/tables/table4/table.svg.elems.svg");
	private static final File FILLED1016Y_1_ELEM = new File(FILLED_DIR, "/_10.1016.j.ypmed.2009.07.022/tables/table1/table.svg.elems.svg");
	private static final File FILLED1016Y_2_ELEM = new File(FILLED_DIR, "/_10.1016.j.ypmed.2009.07.022/tables/table2/table.svg.elems.svg");
	private static final File FILLED1016Y_3_ELEM = new File(FILLED_DIR, "/_10.1016.j.ypmed.2009.07.022/tables/table3/table.svg.elems.svg");
	private static final File FILLED1016_1_ELEM = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table1/table.svg.elems.svg");
	private static final File FILLED1016_2_ELEM = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table2/table.svg.elems.svg");
	private static final File FILLED1016_3_ELEM = new File(FILLED_DIR, "/10.1016.S2213-2600_14_70195-X/tables/table3/table.svg.elems.svg");
	private static final File FILLED1136_2_ELEM = new File(FILLED_DIR, "/10.1136.bmjopen-2016-12335/tables/table2/table.svg.elems.svg");
	private static final File FILLED1136_5_ELEM = new File(FILLED_DIR, "/10.1136.bmjopen-2016-12335/tables/table5/table.svg.elems.svg");
	private static final File FILLED1136_6_ELEM = new File(FILLED_DIR, "/10.1136.bmjopen-2016-12335/tables/table6/table.svg.elems.svg");

	static File[] FILLED_ELEM_FILES = new File[] {
			FILLED1007_2_ELEM,
			FILLED1007_3_ELEM,
			FILLED1007_4_ELEM,
			FILLED1016Y_1_ELEM,
			FILLED1016Y_2_ELEM,
			FILLED1016Y_3_ELEM,
			FILLED1016_1_ELEM,
			FILLED1016_2_ELEM,
			FILLED1016_3_ELEM,
			FILLED1136_2_ELEM,
			FILLED1136_5_ELEM,
			FILLED1136_6_ELEM,
		};

	/** rotate element positions position
	 * @throws FileNotFoundException 
	 * 
	 */
	@Test
	public void testElemFiles() throws FileNotFoundException {
		List<List<Integer>> countListList = new ArrayList<List<Integer>>();
		
		for (File svgFile : FILLED_ELEM_FILES) {
			SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
			List<SVGElement> descendants = SVGElement.extractSelfAndDescendantElements(svgElement);
			List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(svgElement);
			List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(svgElement);
			List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgElement);
			List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(svgElement);
			List<Integer> row = new ArrayList<Integer>();
			row.add(descendants.size());
			row.add(rectList.size());
			row.add(lineList.size());
			row.add(textList.size());
			row.add(pathList.size());
			countListList.add(row);
		}
	}
	
	@Test
	public void testCreateRow() {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(FILLED1007_2_ELEM);
		List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(svgElement);
		Multimap<IntRange, SVGRect> rectByIntYRange = ArrayListMultimap.create();
		for (SVGRect rect : rectList) {
			rect.setBoundingBoxCached(true);
			Real2Range r2range = rect.getBoundingBox();
			Int2Range i2range = new Int2Range(r2range);
			IntRange irange = new IntRange(r2range.getRealRange(Direction.VERTICAL));
			rectByIntYRange.put(irange, rect);
		}
		SVGG g = new SVGG();
		Set<IntRange> keySet = rectByIntYRange.keySet();
		List<IntRange> keyList = IntRange.createSortedList(keySet);
		Multiset<IntRange> rangeSet = HashMultiset.create();
		for (IntRange yrange : keyList) {
			SVGLine line = new SVGLine(new Real2(10., yrange.getMin()), new Real2(10., yrange.getMax()));
			line.setCSSStyle("stroke-width:2.;stroke:blue;");
			g.appendChild(line);
			List<SVGRect> rowList = new ArrayList<SVGRect>(rectByIntYRange.get(yrange));
			for (SVGRect rowRect : rowList) {
				SVGRect r1 = new SVGRect(rowRect);
				g.appendChild(r1);
				Real2Range b1 = r1.getBoundingBox();
				IntRange range = new IntRange(b1.getXRange());
				rangeSet.add(range);
				Real2Range b2 = b1.getReal2RangeExtendedInX(LEFT_PAD, RIGHT_PAD).getReal2RangeExtendedInY(BOTTOM_PAD,  TOP_PAD);
				SVGRect r2 = SVGRect.createFromReal2Range(b1);
				r2.setCSSStyle("stroke:green;stroke-width:0.5;fill:none;");
				g.appendChild(r2);
			}
		}
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgElement);
		for (SVGText text : textList) {
			g.appendChild(text.copy());
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/"+FILLED+"/rows.svg"));
	}
	
}
