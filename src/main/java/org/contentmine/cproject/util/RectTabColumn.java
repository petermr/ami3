package org.contentmine.cproject.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** contains name and values of a column of RectangularTable
 * 
 * RectangularTable is row-based so this may be a copy of column values
 * 
 * @author pm286
 *
 */
public class RectTabColumn implements Iterable<String> {
	private static final Logger LOG = Logger.getLogger(RectTabColumn.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<String> columnValues;
	private String header;
	
	public RectTabColumn() {
		columnValues = new ArrayList<>();
	}
	
	public static RectTabColumn createColumn(List<String> values) {
		RectTabColumn column = null;
		if (values != null) {
			column = new RectTabColumn();
			column.columnValues = new ArrayList<>(values);
		} else {
			LOG.warn("Null values in RectTabColumn");
		}
		return column;
	}

	public static RectTabColumn createColumn(List<String> values, String header) {
		RectTabColumn column = RectTabColumn.createColumn(values);
		if (column != null) {
			column.setHeader(header);
		}
		return column;
	}

	public void add(String value) {
		columnValues.add(value);
	}

	public List<String> getValues() {
		return columnValues;
	}

	public int size() {
		return columnValues.size();
	}

	public String get(int i) {
		return columnValues.get(i);
	}

	public List<String> subList(int startRow, int size) {
		return columnValues.subList(startRow, size);
	}

	@Override
	public Iterator<String> iterator() {
		return columnValues.iterator();
	}

	public List<String> flatten() {
		List<String> flattenStrings = new ArrayList<>(); 
		for (String s : columnValues) {
			String ss = null;
			if (s == null) {
				ss = "NULL";
			} else {
				ss = new String(s);
				ss = ss.replaceAll("[A-Z]", "A");
				ss = ss.replaceAll("[a-z]", "a");
				ss = ss.replaceAll("[0-9]", "0");
				ss = ss.replaceAll("[αβδ]", "α");
				ss = ss.replaceAll("\n", " ");
				ss = ss.replaceAll("\\s+", " ");
			}
			flattenStrings.add(ss);
		}
		return flattenStrings;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public List<Entry<String>> extractSortedMultisetListForColumn() {
		Multiset<String> set = HashMultiset.create();
		set.addAll(getValues());
		return CMineUtil.getEntryListSortedByCount(set);
	}

	@Override
	public String toString() {
		return header+": "+columnValues;
	}
}
