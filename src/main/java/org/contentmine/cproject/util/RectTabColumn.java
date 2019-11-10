package org.contentmine.cproject.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

	private List<String> columnValues;
	private String header;
	
	public RectTabColumn() {
		columnValues = new ArrayList<>();
	}
	
	public RectTabColumn(List<String> values) {
		Collections.copy(columnValues, values);
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

}
