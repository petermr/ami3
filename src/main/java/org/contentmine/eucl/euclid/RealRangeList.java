package org.contentmine.eucl.euclid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * a sorted list of ranges.
 * 
 * keeps list sorted at all times.
 * 
 * when a RealRange is added it looks for the next lowest and highest ranges. If
 * it does not overlap it is inserted in the free space. If it overlaps with
 * either or both they merge.
 * 
 * Currently buublesort-like and assumes "not too many" and may suffer from
 * quadratic performance.
 * 
 * // TODO add binary chop or other sort
 * 
 * @author pm286
 * 
 */
public class RealRangeList {

	private static final long serialVersionUID = 1L;
	private final static Logger LOG = Logger.getLogger(RealRangeList.class);

	private List<RealRange> rangeList;
	private int pointer;
	private boolean merged;
	private RealRange newRange;
	private RealRange oldRange;

	public RealRangeList() {
		rangeList = new ArrayList<RealRange>();
	}

	/**
	 * adds range and returns position of result.
	 * 
	 * if range overlaps with any existing ranges merges them
	 * 
	 * if no overlap inserts before next highest non-overlapping range
	 * 
	 * @param range
	 *            to add
	 * @return
	 */
	public int addRange(RealRange range) {
		int result = -1;
		if (rangeList.size() == 0) {
			rangeList.add(range);
			result = 0;
		} else {
			this.newRange = range;
			result = insertRange1();
		}
		return result;
	}
	
	private int insertRange1() {
		pointer = findFirstLargerOrOverlappingExistingRange();
		int firstHigher = pointer;
		List<Integer> overlappingRanges = findAllOverlappingRanges();
		subsumeAndDeleteAllOverlappingRanges(overlappingRanges);
		rangeList.add(firstHigher, newRange);
		return firstHigher;
	}

	private int findFirstLargerOrOverlappingExistingRange() {
		pointer = 0;
		for (; pointer < rangeList.size(); pointer++) {
			oldRange = rangeList.get(pointer);
			if (oldRange.getMax() >= newRange.getMin()) {
				break;
			}
		}
		return pointer;
	}

	private List<Integer> findAllOverlappingRanges() {
		List<Integer> overlappingRanges = new ArrayList<Integer>();
		for (; pointer < rangeList.size(); pointer++) {
			oldRange = rangeList.get(pointer);
			if (oldRange.getMin() > newRange.getMax()) {
				break;
			}
			newRange.plusEquals(oldRange);
			overlappingRanges.add(pointer);
		}
		return overlappingRanges;
	}

	private void subsumeAndDeleteAllOverlappingRanges(List<Integer> overlappingRanges) {
		Collections.reverse(overlappingRanges);
		int noverlap = overlappingRanges.size();
		for (int i = 0; i < noverlap; i++) {
			int toRemove = overlappingRanges.get(i);
			rangeList.remove(toRemove);
		}
	}

	public int size() {
		return rangeList.size();
	}

	public RealRange get(int i) {
		return (i < 0 || i >= rangeList.size()) ? null : rangeList.get(i);
	}

	public RealRange remove(int i) {
		return (i < 0 || i >= rangeList.size()) ? null : rangeList.remove(i);
	}

	public String toString() {
		return rangeList.toString();
	}
}
