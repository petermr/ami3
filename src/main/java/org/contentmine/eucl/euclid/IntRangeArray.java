package org.contentmine.eucl.euclid;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** holds an array of IntRanges
 * may or may not overlap or be sorted
 * @author pm286
 *
 */
public class IntRangeArray implements Iterable<IntRange> {

	private static final PrintStream SYSOUT = System.out;
	private List<IntRange> rangeList;

	public IntRangeArray() {
		init();
	}
	
	public IntRangeArray(List<IntRange> ranges) {
		init();
		rangeList.addAll(ranges);
	}

	/** deep copy
	 * 
	 * @param array
	 */
	public IntRangeArray(IntRangeArray array) {
		this();
		for (IntRange range : array.rangeList) {
			this.add(new IntRange(range));
		}
	}

	public IntRangeArray(int height) {
		rangeList = new ArrayList<IntRange>(height);
		for (int i = 0; i < height; i++) {
			rangeList.add(null);
		}
	}

	private void init() {
		rangeList = new ArrayList<IntRange>();
	}
	
	public void add(IntRange range) {
		rangeList.add(range);
	}
	
	public void sort() {
		Collections.sort(rangeList);
	}
	
	public void sortAndRemoveOverlapping() {
		sort();
		List<IntRange> newList = new ArrayList<IntRange>();
		Iterator<IntRange> iterator = rangeList.iterator();
		IntRange lastRange = null;
		while (iterator.hasNext()) {
			IntRange range = iterator.next();
			if (lastRange == null) {
				newList.add(range);
				lastRange = range;
			} else {
				boolean intersects = lastRange.intersectsWith(range);
				if (intersects) {
					IntRange merged = lastRange.plus(range);
					newList.set(newList.size() - 1, merged);
					lastRange = merged;
				} else {
					newList.add(range);
					lastRange = range;
				}
			}
		}
		rangeList = newList;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof IntRangeArray)) {
			return false;
		}
		IntRangeArray array2 = (IntRangeArray) obj;
		if (this.size() != array2.size()) return false;
		for (int i = 0; i < this.size(); i++) {
			if (!this.get(i).equals(array2.get(i))) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int h = 17;
		for (int i = 0; i < rangeList.size(); i++) {
			h += rangeList.get(i).hashCode() * 31;
		}
		return h;
	}

	public int size() {
		return rangeList.size();
	}

	public IntRange get(int serial) {
		return rangeList.get(serial);
	}

	public void debug() {
		for (IntRange range : rangeList) {
			SYSOUT.println(range);
		}
	}
	

	public IntRangeArray plus(IntRangeArray array) {
		IntRangeArray newArray = null;
		if (array != null) {
			newArray = new IntRangeArray();
			for (IntRange intRange : this.rangeList) {
				newArray.add(new IntRange(intRange));
			}
			for (IntRange intRange : array.rangeList) {
				newArray.add(new IntRange(intRange));
			}
			newArray.sortAndRemoveOverlapping();
		}
		return newArray;
	}

	/** create array representing the gaps in this
	 * gaps at ends are NOT filled
	 * does not alter this
	 * @return
	 */
	public IntRangeArray inverse() {
		IntRangeArray newArray = null;
		IntRangeArray copy = new IntRangeArray(this);
		copy.sortAndRemoveOverlapping();
		if (copy.size() > 0) {
			newArray = new IntRangeArray();
			IntRange last = null;
			for (IntRange current : copy) {
				if (last != null) {
					IntRange gap = new IntRange(last.maxval, current.minval);
					newArray.add(gap);
				}
				last = current;
			}
		}
		return newArray;
	}

	public Iterator<IntRange> iterator() {
		return rangeList.iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (IntRange range : rangeList) {
			sb.append(String.valueOf(range));
		}
		sb.append("]");
		return sb.toString();
	}

	public void set(int i, IntRange intRange) {
		ensureCapacity(i + 1);
		rangeList.set(i, intRange);
	}

	/** ensure total size of list is >= cap
	 * fill with null if necessary
	 * @param cap
	 */
	private void ensureCapacity(int cap) {
		int currentSize = rangeList.size();
		if (cap >= currentSize) {
			for (int i = currentSize; i < cap; i++) {
				rangeList.add(null);
			}
		}
	}

	/** generates midpoint of each slice
	 * 
	 * @param offset
	 * @return
	 */
	public Real2Array generateYMidpointArray() {
		Real2Array points = new Real2Array(size());
		for (int y = 0; y < size(); y++) {
			IntRange yslice = get(y);
			if (yslice != null) {
				double xmid = yslice.getMidPoint();
				points.setElement(y, new Real2(xmid, y));
			}
		}
		return points;
	}
}
