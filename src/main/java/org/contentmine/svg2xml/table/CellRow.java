package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.SVGRect;

/** supports rows will filled and possible empyty cells.
 * 
 * @author pm286
 *
 */
public class CellRow {
	private static final Logger LOG = Logger.getLogger(CellRow.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private ArrayList<SVGRect> rectsInYRow;
	private List<Integer> colXTickMarkList;
	private List<SVGRect> sortedRects;
	private List<IntRange> xRangeList;
	private List<Integer> colSpanCounts;

	private int tickPointer;

	public CellRow() {
		
	}

	/** create row from list of rects and list of tickmarks.
	 * These will have been created by FilledTaleAnalyzer or similar tool.
	 * 
	 * @param rectsInYRow
	 * @param colXTickMarkList
	 */
	public CellRow(List<SVGRect> rectsInYRow, List<Integer> colXTickMarkList) {
		this.rectsInYRow = new ArrayList<SVGRect>(rectsInYRow);
		this.colXTickMarkList = colXTickMarkList;
		sortRectsByTickMarks();
		addColspanCounts();
	}

	private void sortRectsByTickMarks() {
		sortedRects = new ArrayList<SVGRect>();
		xRangeList = createXRangeListFromRects(rectsInYRow);
		
		for (int itick = 0; itick < colXTickMarkList.size() - 1; itick++) {
			for (int irange = 0; irange < xRangeList.size(); irange++) {
				IntRange intRange = xRangeList.get(irange);
				if (intRange.getMin() == colXTickMarkList.get(itick)) {
					sortedRects.add(rectsInYRow.get(irange));
				}
			}
		}
		if (sortedRects.size() != rectsInYRow.size()) {
			throw new RuntimeException("Error in sorting rects");
		}
		xRangeList = createXRangeListFromRects(rectsInYRow);
	}

	private void addColspanCounts() {
		if (xRangeList.size() == 0) {
			return;
		}
		colSpanCounts = new ArrayList<Integer>();
		tickPointer = 0;
		for (int iRange = 0; iRange < xRangeList.size(); iRange++) {
			IntRange xRange = xRangeList.get(iRange);
			tickPointer = getTick(xRange.getMin());
			if (tickPointer == -1) {
				LOG.error("ran off end of ticks");
				break;
			}
			int tickPointer1 = getTick(xRange.getMax());
			if (tickPointer1 == -1) {
				LOG.error("ran off end of ticks");
				break;
			}
			int colspan = tickPointer1 - tickPointer;
			LOG.trace(iRange+": "+xRangeList+"; "+xRangeList.size()+"; "+tickPointer+"; "+colspan+"; "+tickPointer1);
			tickPointer = tickPointer1;
			colSpanCounts.add(colspan);
		}
	}

	private int getTick(int x) {
		for (int i = tickPointer; i < colXTickMarkList.size(); i++) {
			if (colXTickMarkList.get(i) == x) {
				return i;
			}
		}
		LOG.trace(tickPointer+"; "+colXTickMarkList.size()+"; "+x+"; "+colXTickMarkList);
		return -1;
	}

	private List<IntRange> createXRangeListFromRects(List<SVGRect> rects) {
		List<IntRange> xRangeList = new ArrayList<IntRange>();
		for (int j = 0; j < rects.size(); j++) {
			xRangeList.add(new IntRange(rects.get(j).getBoundingBox().getXRange()));
		}
		return xRangeList;
	}
	
	public List<Integer> getColSpanCounts() {
		return colSpanCounts;
	}

}
