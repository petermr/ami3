package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.svg.SVGElement;

public class TableBody extends TableChunk {

	private final static Logger LOG = Logger.getLogger(TableBody.class);
	private List<TableRow> rowList;
	
	public TableBody(RealRangeArray horizontalMask, RealRangeArray verticalMask) {
		super(horizontalMask, null);
	}
	
	public TableBody(List<? extends SVGElement> elementList) {
		super(elementList);
	}

	public TableBody(TableChunk chunk) {
		this(chunk.getElementList());
	}

	/** 
		 * 
		 * @return
		 */
		public List<TableRow> createRowChunks() {
			rowList = new ArrayList<TableRow>();
			RealRangeArray vMask1 = verticalMask.inverse();
			if (vMask1 != null) {
				for (RealRange range : vMask1) {
					TableRow rowChunk = new TableRow(getHorizontalMask(), null);
					rowList.add(rowChunk);
					for (SVGElement element : elementList){
						Real2Range bbox = element.getBoundingBox();
						RealRange ybbox = bbox.getYRange();
						if (range.includes(ybbox)) {
							rowChunk.add(element);
						}
					}
					rowChunk.splitHorizontally();
					LOG.trace("ROW "+rowChunk);
				}
			}
	//		debugRows(rowElementList);
			return rowList;
		}

	/** create rows from horizontalsly separated chunks
	 * crude. may speed up later
	 */
	public List<TableRow> createUnstructuredRows() {
		rowList = null;
		if (elementList.size() > 0) {
			this.createVerticalMask();
			//guarantee no gaps
			verticalMask.format(3);
			verticalMask.extendRangesBy(100.);
			verticalMask.format(3);
			rowList = new ArrayList<TableRow>();
			for (RealRange range : verticalMask) {
				TableRow row = new TableRow();
				rowList.add(row);
				for (SVGElement element : elementList) {
					RealRange elemRange = element.getBoundingBox().getYRange();
					if (range.includes(elemRange)) {
						row.add(element);
					}
				}
			}
		}
		return rowList;
	}

	public List<TableRow> createStructuredRows() {
		createUnstructuredRows();
		createHorizontalMaskWithTolerance(TableTable.HALF_SPACE);
		horizontalMask.extendRangesBy(100.);
		if (rowList != null) {
			for (int i = 0; i < rowList.size(); i++) {
				TableRow row = rowList.get(i);
				row.createAndAnalyzeCells(horizontalMask);
			}
		}
		return rowList;
	}

	public HtmlElement createHtmlElement() {
		createStructuredRows();
		HtmlTable table = new HtmlTable();
		if (rowList != null) {
			for (TableRow row : rowList) {
				table.appendChild(row.createHtmlElement());
			}
		}
		return table;
	}
}
