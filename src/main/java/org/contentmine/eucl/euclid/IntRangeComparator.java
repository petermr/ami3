package org.contentmine.eucl.euclid;

import java.util.Comparator;

public class IntRangeComparator implements Comparator<IntRange> {

	public enum End {
		MIN,
		MID,
		MAX;
	}
	
	private End end;
	
	public IntRangeComparator(End end) {
		this.end = end;
	}
	public int compare(IntRange range1, IntRange range2) {
		if (range1 == null || range2 == null) return 0;
		if (End.MIN == end) {
			return range1.getMin() - range2.getMin();
		}
		if (End.MID == end) {
			return range1.getMidPoint() - range2.getMidPoint();
		}
		if (End.MAX == end) {
			return range1.getMax() - range2.getMax();
		}
		return 0;
	}

}
