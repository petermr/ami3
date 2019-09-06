package org.contentmine.eucl.euclid.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

import nu.xom.Node;

/** mainly static tools. for managing multisets
 * 
 * originally strongly typed static but being gradually reworked to parameterised.
 * 
 * @author pm286
 *
 */
public class MultisetUtil<T extends Object> {
	private static final Logger LOG = Logger.getLogger(MultisetUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static <T> Iterable<Entry<T>> getEntriesSortedByValue(Multiset<T> set) {
		return  ImmutableSortedMultiset.copyOf(set).entrySet();		
	}
	
	public static <T> Iterable<Multiset.Entry<T>> getEntriesSortedByCount(Multiset<T> objectSet) {
		ImmutableMultiset<T> copyHighestCountFirst = null;
		try {
			copyHighestCountFirst = Multisets.copyHighestCountFirst(objectSet);
		} catch (NullPointerException npe) {
//			LOG.error("NPE: "+npe);
		}
		return copyHighestCountFirst == null ? null : copyHighestCountFirst.entrySet();
	}

	/** extracts a list of attribute values.
	 * 
	 * @return
	 */
	public static List<String> getAttributeValues(Node searchNode, String xpath) {
		List<Node> nodes = XMLUtil.getQueryNodes(searchNode, xpath);
		List<String> nodeValues = new ArrayList<String>();
		for (Node node : nodes) {
			String value = node.getValue();
			if (value != null && value.trim().length() != 0) {
				nodeValues.add(value);
			}
		}
		return nodeValues;
	}

	public static <T> Comparable<T> getLowestValue(Multiset<T> valueSet) {
		Iterable<Multiset.Entry<T>> values = MultisetUtil.getEntriesSortedByValue(valueSet);
		Multiset.Entry<T> entries = values.iterator().hasNext() ? (Multiset.Entry<T>) values.iterator().next() : null;
		Comparable<T> value = (entries == null) ? null : (Comparable<T>) entries.getElement();
		return value;
	}

	public static <T> Comparable<T> getHighestValue(Multiset<T> valueSet) {
		Iterable<Multiset.Entry<T>> values = MultisetUtil.getEntriesSortedByValue(valueSet);
		List<Entry<T>> entries = createEntryList(values);
		Comparable<T> value = entries.size() == 0 ? null : (Comparable<T>) entries.get(entries.size() - 1).getElement();
		return value;
	}

	public static <T> Comparable<T> getCommonestValue(Multiset<T> valueSet) {
		Iterable<Multiset.Entry<T>> values = MultisetUtil.getEntriesSortedByCount(valueSet);
		Multiset.Entry<T> entries = values.iterator().hasNext() ? (Multiset.Entry<T>) values.iterator().next() : null;
		Comparable<T> value = (entries == null) ? null : (Comparable<T>) entries.getElement();
		return value;
	}

	public static <T> List<Entry<T>> createEntryList(Iterable<Entry<T>> iterable) {
		List<Entry<T>> entries = new ArrayList<Entry<T>>();
		if (iterable != null) {
			for (Entry<T> entry : iterable) {
				entries.add(entry);
			}
		}
		return entries;
	}

	public static <T> List<Entry<T>> createListSortedByValue(Multiset<T> set) {
		return MultisetUtil.createEntryList(MultisetUtil.getEntriesSortedByValue(set));
	}

	public static <T> List<Entry<T>> createListSortedByCount(Multiset<T> set) {
		return MultisetUtil.createEntryList(MultisetUtil.getEntriesSortedByCount(set));
	}

	/** this has unchecked Comparator ... needs fixing.
	 * 
	 * @param entryList
	 */
	public static <T extends Comparable> void sortListByValue(List<Entry<T>> entryList) {
		Collections.sort(entryList, new Comparator() {
			public int compare(Object o1, Object o2) {
				if (o1 == null || o2 == null ) return 0;
				if (!(o1 instanceof Entry<?>)) return 0;
				if (!(o2 instanceof Entry<?>)) return 0;
				Entry<?> e1 = (Entry<?>) o1;
				Entry<?> e2 = (Entry<?>) o2;
				Object j1 = e1.getElement();
				Object j2 = e2.getElement();
				if (!(j1 instanceof Comparable) || !(j2 instanceof Comparable)) {
					return 0;
				}
				return (((Comparable)j1).compareTo((Comparable)j2));
			}
			
		});
		
	}

	public static Map<Integer, Integer> createIntegerFrequencyMap(Multiset<Integer> set) {
		Map<Integer, Integer> countByInteger = new HashMap<Integer, Integer>();
		for (Entry<Integer> entry : set.entrySet()) {
			countByInteger.put(entry.getElement(), entry.getCount());
		}
		return countByInteger;
	}

	/** creates new list with entries whose count is at least a given value
	 * 
	 * @param entries
	 * @param minCount
	 * @return new List (empty if none)
	 */
	public static <T> List<Entry<T>> createEntriesWithCountGreater(List<Entry<T>> entries, int minCount) {
		List<Entry<T>> newEntries = new ArrayList<Entry<T>>();
		for (Entry<T> entry : entries) {
			if (entry.getCount() >= minCount) {
				newEntries.add(entry);
			}
		}
		return newEntries;
	}
	
	/** create Multset from the String representation.
	 * 
	 * @param multisetString
	 * @return
	 */
	public static Multiset<String> createMultiset(String multisetString) {
		Multiset<String> plantPartMultiset = HashMultiset.create();
		multisetString = multisetString.substring(1,  multisetString.length() - 1);
		String[] multisetStrings = multisetString.split("\\s*\\,\\s*");
		for (String s : multisetStrings) {
			String[] ss = s.split(" x ");
			String value = ss[0];
			int count = ss.length ==  1 ? 1 : Integer.parseInt(ss[1]);
			plantPartMultiset.add(value, count);
		}
		return  plantPartMultiset;
	}



	public static void writeCSV(File csvFile, List<Entry<String>> entryList, String title) throws IOException {
		if (csvFile != null) {
			List<String> rows = new ArrayList<String>();
			if (title != null) {
				rows.add(title+","+"count");
			}
			for (Entry<String> entry : entryList) {
				String element = entry.getElement();
				element = Util.escapeCSVField(element);
				rows.add(element+","+entry.getCount());
			}
			csvFile.getParentFile().mkdirs();
			FileUtils.writeLines(csvFile, rows);
		}
	}

	/** output file without title.
	 * 
	 * @param csvFile
	 * @param entryList
	 * @throws IOException
	 */
	public static void writeCSV(File csvFile, List<Entry<String>> entryList) throws IOException {
		writeCSV(csvFile, entryList, null);
	}

	/** gets the most frequent count in the list.
	 * 
	 * @param valueList
	 * @return
	 */
	public static <T> int getMaximumCount(List<Entry<T>> valueList) {
		int countMax = 0;
		for (Entry<T> yMin : valueList) {
			if (yMin.getCount() > countMax) {
				countMax = yMin.getCount();
			}
		}
		return countMax;
	}

	/** gets subList with values with frequencies over given fraction of maximum.
	 * 
	 * @param valueList
	 * @param cutOff
	 * @return
	 */
	public static <T> List<Entry<T>> getMostFrequentValues(List<Entry<T>> valueList, int minCount) {
		List<Entry<T>> newList = new ArrayList<Entry<T>>();
		for (int i = valueList.size() - 1; i >= 0; i--) {
			Entry<T> entry = valueList.get(i);
			if (entry.getCount() >= minCount) {
				newList.add(entry);
			}
		}
		return newList;
	}

	/** start with sorted list of Entry<Integer>
	 * 
	 * @param sortedByValueList
	 * @param minValue
	 * @return
	 */
	public static IntArray extractSortedArrayOfValues(List<Entry<Integer>> sortedByValueList, int minValue) {
		List<Entry<Integer>> mostFrequentValues = 
				MultisetUtil.getMostFrequentValues(sortedByValueList, minValue);
		MultisetUtil.sortListByValue(mostFrequentValues);
		IntArray valueArray = new IntArray();
		for (Entry<Integer> entry : mostFrequentValues) {
			Integer value = entry.getElement();
			valueArray.addElement(value);
		}
		return valueArray;
	}

	/** coalesce close values.
	 * 
	 * @param sortedByValueList
	 * @param tolerance
	 * @return
	 */
	public static List<Entry<Integer>> bundleCounts(List<Entry<Integer>> sortedByValueList, int tolerance) {
		Multiset.Entry<Integer> lastEntry = null;
		Multiset<Integer> countSet = HashMultiset.create();
		for (Multiset.Entry<Integer> entry : sortedByValueList) {
			Integer thisCount = entry.getCount();
			Integer thisValue = entry.getElement();
			Integer lastCount = lastEntry == null ? null : lastEntry.getCount();
			Integer lastValue = lastEntry == null ? null : lastEntry.getElement();
			// neighbours?
			if (lastValue != null && lastValue >= thisValue - tolerance) {
				if (lastCount > thisCount) {
					// increment last, dont add this
					countSet.add(lastValue, thisCount);
				} else {
					// transfer lastCount to this, delete last
					countSet.add(thisValue, thisCount + lastCount);
					countSet.remove(lastValue);
				}
			} else {
				countSet.add(thisValue, thisCount);
			}
			lastEntry = entry;
		}
		List<Entry<Integer>> countList = MultisetUtil.createListSortedByValue(countSet);
		return countList;
	}



}
