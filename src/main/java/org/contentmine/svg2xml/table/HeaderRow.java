package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** a horizontal row of ColumnGroups.
 * 
 * @author pm286
 *
 */
public class HeaderRow {
	private static final Logger LOG = Logger.getLogger(HeaderRow.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<ColumnGroup> columnGroupList;
	
	public HeaderRow() {
		
	}

	public void add(ColumnGroup columnGroup) {
		getOrCreateColumnGroupList();
		columnGroupList.add(columnGroup);
	}

	public List<ColumnGroup> getOrCreateColumnGroupList() {
		if (columnGroupList == null) {
			columnGroupList = new ArrayList<ColumnGroup>();
		}
		return columnGroupList;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		getOrCreateColumnGroupList();
		for (ColumnGroup columnGroup : columnGroupList) {
			sb.append(String.valueOf(columnGroup));
		}
		return sb.toString();
	}
	
	
}
