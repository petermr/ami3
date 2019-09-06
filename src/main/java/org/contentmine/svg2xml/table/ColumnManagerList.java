package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;

/** manages a list of columns
 * 
 * @author pm286
 *
 */
public class ColumnManagerList {
	private static final Logger LOG = Logger.getLogger(ColumnManagerList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<ColumnManager> columnManagerList;
	private IntRangeArray rangeArray;

	public ColumnManagerList() {
		ensureColumnManagerList();
	}
	
	private void ensureColumnManagerList() {
		if (columnManagerList == null) {
			columnManagerList = new ArrayList<ColumnManager>();
		}
	}

	public void add(ColumnManager columnManager) {
		ensureColumnManagerList();
		columnManagerList.add(columnManager);
	}

	public ColumnManager get(int iCol) {
		ColumnManager columnManager = null;
		ensureColumnManagerList();
		if (iCol >=0 && iCol < columnManagerList.size()) {
			columnManager = columnManagerList.get(iCol);
		}
		return columnManager;
	}

	public int size() {
		ensureColumnManagerList();
		return columnManagerList.size();
	}

	public IntRangeArray getOrCreateColumnRanges() {
		if (rangeArray == null) {
			rangeArray = new IntRangeArray();
			ensureColumnManagerList();
			for (ColumnManager columnManager : columnManagerList) {
				IntRange intRange = columnManager.getEnclosingRange();
				rangeArray.add(intRange);
			}
		}
		return rangeArray;
	}

	public void debug() {
		ensureColumnManagerList();
		LOG.debug("Ranges "+getOrCreateColumnRanges());
	}
}
