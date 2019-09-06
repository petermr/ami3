package org.contentmine.svg2xml.table;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/** move this later.
 * 
 * @author pm286
 *
 */
public class FilledTableAnalyzer {
	
	
	private static final Logger LOG = Logger.getLogger(FilledTableAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String COLSPAN = "colspan=";
	private static final String ROWSPAN = "rowspan=";
	private static final String DEFAULT_TEXT_CSS = 
			"font-size:8;font-weight:bold;fill:yellow;stroke:blue;stroke-width:0.3;font-family:sans-serif;";
	private static final String DEFAULT_BOUNDARY_CSS = "stroke:blue;stroke-width:2.0";

	private static final double XLINE_MAX = 15.;
	private static final double XLINE_MIN= 10.0;
	private static final double YLINE_MAX = 15.;
	private static final double YLINE_MIN= 10.0;

	private SVGElement svgInElement;
	private String colCssValue = DEFAULT_TEXT_CSS;
	private String rowCssValue = DEFAULT_TEXT_CSS;
	private String colTickMarkCss = DEFAULT_BOUNDARY_CSS;
	private String rowTickMarkCss = DEFAULT_BOUNDARY_CSS;
	private List<IntRange> rowYRangeList;
	private Multimap<IntRange, SVGRect> rectByIntYRange;
	private List<Integer> rowYTickMarkList;
	private List<Integer> colXTickMarkList;

	public FilledTableAnalyzer() {
		
	}

	public void readSVGElement(File svgFile) {
		svgInElement = SVGElement.readAndCreateSVG(svgFile);
	}
	
	/** creates the colpspans/rowspans. now must turn these into rowGroups.
	 * 
	 */
	public SVGG createBoundaryListsAndProcessRows() {
		
		SVGG gg = new SVGG();
		
		createBoundaryTickMarkLists();
		AbstractCMElement g = drawHorizontalTicksForRows();
		gg.appendChild(g);
		g = drawVerticalTicksForColumns();
		gg.appendChild(g);
		createAndOutputColspanCountForSpanningCells();
		
		g = new SVGG();
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgInElement);
		for (SVGText text : textList) {
			g.appendChild(text.copy());
		}
		gg.appendChild(g);
		return gg;
	}

	public List<CellRow> createCellRowList() {
		List<CellRow> cellRowList = new ArrayList<CellRow>();
 		createBoundaryTickMarkLists();
		for (IntRange rowYRange : rowYRangeList) {
			List<SVGRect> rectsInYRow = new ArrayList<SVGRect>(rectByIntYRange.get(rowYRange));
			CellRow cellRow = new CellRow(rectsInYRow, colXTickMarkList);
			LOG.trace("Counts "+cellRow.getColSpanCounts());
			cellRowList.add(cellRow);
		}
		return cellRowList;
	}

	private void createBoundaryTickMarkLists() {
		List<SVGRect> rectList = SVGRect.extractSelfAndDescendantRects(svgInElement);
		rectByIntYRange = createRectByYIntRange(rectList);
		rowYRangeList = IntRange.createSortedList(rectByIntYRange.keySet());
		rowYTickMarkList = createTickMarkListFromRanges(rowYRangeList);
		colXTickMarkList = createColumnXTickMarkList(rectByIntYRange, rowYRangeList);
	}
	
	// =======================


	// drawing routines ==================

	/** adds Cell to graphic output and annotates with row/col span count
	 * 
	 * @return
	 */
	private SVGElement createAndOutputColspanCountForSpanningCells() {
		SVGElement g = new SVGG();
		// iterate over rows
		for (IntRange rowRange : rowYRangeList) {
			List<SVGRect> rectsInRowList = new ArrayList<SVGRect>(rectByIntYRange.get(rowRange));
			for (SVGRect rectInRow : rectsInRowList) {
				SVGRect rectCopy = new SVGRect(rectInRow);
				g.appendChild(rectCopy);
				Real2Range cellBbox = rectCopy.getBoundingBox();
				AbstractCMElement gg = createAndOutputColspanCountForSpannningCell(cellBbox);
				g.appendChild(gg);
				gg = createAndOutputRowspanCountForSpanningRow(cellBbox);
				g.appendChild(gg);
			}
		}
		return g;
	}


	/** adds SVGText with rowspan count to any boxes with rowspan > 1
	 * 
	 * @param cellBbox
	 * @return
	 */
	private SVGElement createAndOutputRowspanCountForSpanningRow(Real2Range cellBbox) {
		SVGElement g = new SVGG();
		IntRange yrange = new IntRange(cellBbox.getYRange());
		int rowspans = createSpanCounts(yrange, rowYTickMarkList);
		if (rowspans > 1) {
			SVGText rowspanText = new SVGText(cellBbox.getAllCornerPoints()[3], ROWSPAN+rowspans);
			rowspanText.setCSSStyle(rowCssValue);
			g.appendChild(rowspanText);
		}
		return g;
	}

	/** adds SVGText with colspan count to any boxes with colspan > 1
	 * 
	 * @param cellBbox
	 * @return
	 */

	private SVGElement createAndOutputColspanCountForSpannningCell(Real2Range cellBbox) {
		SVGElement g = new SVGG();
		IntRange xrange = new IntRange(cellBbox.getXRange());
		int colspans = createSpanCounts(xrange, colXTickMarkList);
		if (colspans > 1) {
			SVGText colspanText = new SVGText(cellBbox.getAllCornerPoints()[3], COLSPAN+colspans);
			colspanText.setCSSStyle(colCssValue);
			g.appendChild(colspanText);
		}
		return g;
	}


	private SVGElement drawVerticalTicksForColumns() {
		SVGElement g = new SVGG();
		for (Integer jcol : colXTickMarkList) {
			SVGLine line = new SVGLine(new Real2(jcol, YLINE_MIN), new Real2(jcol, YLINE_MAX));
			line.setCSSStyle(colTickMarkCss);
			g.appendChild(line);
		}
		return g;
	}

	private SVGElement drawHorizontalTicksForRows() {
		SVGElement g = new SVGG();
		for (Integer irow : rowYTickMarkList) {
			SVGLine line = new SVGLine(new Real2(XLINE_MIN, irow), new Real2(XLINE_MAX, irow));
			line.setCSSStyle(rowTickMarkCss);
			g.appendChild(line);
		}
		return g;
	}

	// private static =========================
	
	private static Multimap<IntRange, SVGRect> createRectByYIntRange(List<SVGRect> rectList) {
		Multimap<IntRange, SVGRect> rectByYIntRange = ArrayListMultimap.create();
		for (SVGRect rect : rectList) {
			rect.setBoundingBoxCached(true);
			Real2Range r2range = rect.getBoundingBox();
			IntRange irange = new IntRange(r2range.getRealRange(Direction.VERTICAL));
			rectByYIntRange.put(irange, rect);
		}
		return rectByYIntRange;
	}
	

	/**
	 * Iterate over all rowYRanges , retrieve Rects and create XTickMarks
	 * This could be used elsewhere.
	 * 
	 * @param rectByIntYRange rects indexed by YRange (rows)
	 * @param rowYRangeList range or rows to index over
	 * @return
	 */
	private static List<Integer> createColumnXTickMarkList(Multimap<IntRange, SVGRect> rectByIntYRange, List<IntRange> rowYRangeList) {
		Set<IntRange> colXRangeSet = new HashSet<IntRange>();
		for (IntRange rowYRange : rowYRangeList) {
			List<SVGRect> rectsInRowList = new ArrayList<SVGRect>(rectByIntYRange.get(rowYRange));
			for (SVGRect rectInRow : rectsInRowList) {
				IntRange colXRange = new IntRange(rectInRow.getBoundingBox().getXRange());
				colXRangeSet.add(colXRange);
			}
		}
		List<Integer> colXTickMarkList = createTickMarkListFromRanges(IntRange.createSortedList(colXRangeSet));
		return colXTickMarkList;
	}


	
	/** create list of sorted coordinates delineating rows or columns.
	 * iterates over IntRange list (either X or Y ) and creates a set
	 * then sort it into tickMarks
	 * 
	 * @param rangeList
	 * @return
	 */
	private static List<Integer> createTickMarkListFromRanges(List<IntRange> rangeList) {
		Set<Integer> tickMarkSet = new HashSet<Integer>();
		for (IntRange range : rangeList) {
			int min = range.getMin();
			int max = range.getMax();
			tickMarkSet.add(min);
			tickMarkSet.add(max);
		}
		List<Integer> tickMarkList = new ArrayList<Integer>(tickMarkSet);
		Collections.sort(tickMarkList);
		return tickMarkList;
	}

	/** 
	 * 
	 * @param range
	 * @param tickMarkList 
	 * @return number of cells in tickMarkList spanned by range
	 */
	private static int createSpanCounts(IntRange range, List<Integer> tickMarkList) {
		Integer min = range.getMin();
		Integer max = range.getMax();
		int idx = tickMarkList.indexOf(min);
		if (idx >= 0) {
			for (int i = idx + 1; i < tickMarkList.size(); i++) {
				if (tickMarkList.get(i).equals(max)) {
					return i - idx;
				}
			}
		}
		return 0;
	}




}
