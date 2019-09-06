package org.contentmine.eucl.euclid;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.contentmine.eucl.euclid.RealRange.Direction;

/** holds an array of RealRanges
 * may or may not overlap or be sorted
 * perhaps replace by Google rangeSet of IntervalTree later
 * @author pm286
 *
 */
public class RealRangeArray implements Iterable<RealRange> {

	private static final PrintStream SYSOUT = System.out;
	private List<RealRange> rangeList;
	private Direction direction;

	public RealRangeArray() {
		init();
	}

	/** axis the range is aligned with.
	 * 
	 * @param direction
	 */
	public RealRangeArray(Direction direction) {
		this();
		this.direction = direction;
	}

	/** deep copy
	 * 
	 * @param array
	 */
	public RealRangeArray(RealRangeArray array) {
		this();
		if (array != null && array.rangeList != null) {
			for (RealRange range : array.rangeList) {
				this.add(new RealRange(range));
			}
		}
	}

	public RealRangeArray(List<Real2Range> r2rList, Direction dir) {
		init();
		for (Real2Range r2r : r2rList) {
			RealRange range = (RealRange.Direction.HORIZONTAL.equals(dir)) ? r2r.getXRange() : r2r.getYRange();
			this.add(range);
		}
		this.direction = dir;
	}

	public RealRangeArray(List<RealRange> rangeList) {
		this.rangeList = rangeList;
	}

	public RealRangeArray(Real2Range box, Direction direction) {
		RealRange range = box.getRealRange(direction);
		this.add(range);
	}

	private void init() {
		rangeList = new ArrayList<RealRange>();
	}
	
	public void add(RealRange range) {
		if (range != null && rangeList != null) {
			rangeList.add(range);
		}
	}
	
	public void sort() {
		Collections.sort(rangeList);
	}
	
	/** sort ranges into order and merge overlapping ones
	 * 
	 */
	public void sortAndRemoveOverlapping() {
		sort();
		List<RealRange> newList = new ArrayList<RealRange>();
		Iterator<RealRange> iterator = rangeList.iterator();
		RealRange lastRange = null;
		while (iterator.hasNext()) {
			RealRange range = iterator.next();
			if (lastRange == null) {
				newList.add(range);
				lastRange = range;
			} else {
				boolean intersects = lastRange.intersects(range);
				if (intersects) {
					RealRange merged = lastRange.plus(range);
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
		if (obj == null || !(obj instanceof RealRangeArray)) {
			return false;
		}
		RealRangeArray array2 = (RealRangeArray) obj;
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

	public RealRange get(int serial) {
		return rangeList.get(serial);
	}

	public void debug() {
		for (RealRange range : rangeList) {
			SYSOUT.println(range);
		}
	}
	
	public RealRangeArray plus(RealRangeArray array) {
		RealRangeArray newArray = null;
		if (array != null) {
			newArray = new RealRangeArray();
			for (RealRange realRange : this.rangeList) {
				newArray.add(new RealRange(realRange));
			}
			for (RealRange realRange : array.rangeList) {
				newArray.add(new RealRange(realRange));
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
	public RealRangeArray inverse() {
		RealRangeArray newArray = null;
		RealRangeArray copy = new RealRangeArray(this);
		copy.sortAndRemoveOverlapping();
		if (copy.size() > 0) {
			newArray = new RealRangeArray();
			RealRange last = null;
			for (RealRange current : copy) {
				if (last != null) {
					RealRange gap = new RealRange(last.maxval, current.minval);
					newArray.add(gap);
				}
				last = current;
			}
		}
		return newArray;
	}

	public Iterator<RealRange> iterator() {
		return rangeList.iterator();
	}

	/** what do these caps do?
	 * 
	 * @param r2r
	 */
	public void addCaps(Real2Range r2r) {
		if (direction == null) {
			throw new RuntimeException("Must give direction");
		}
		addCaps(r2r, direction);
	}

	public void addCaps(Real2Range r2r, Direction dir) {
		if (direction == null) {
			this.direction = dir;
		} else {
			if (direction != dir) {
				throw new RuntimeException("Cannot change direction");
			}
		}
		RealRange range = RealRange.Direction.HORIZONTAL.equals(dir) ? r2r.getXRange() : r2r.getYRange();
		Double xmin = range.getMin();
		Double xmax = range.getMax();
		addTerminatingCaps(xmin, xmax);
	}

	/** add virtual ends to the array
	 * 
	 * @param xmin
	 * @param xmax
	 */
	public void addTerminatingCaps(Double xmin, Double xmax) {
		this.add(new RealRange(xmin, xmin));
		this.add(new RealRange(xmax, xmax));
		this.sortAndRemoveOverlapping();
	}
	
	/** remove small ranges (e.g. between characters)
	 * 
	 * @param rangeMin
	 */
	public void removeLessThan(double rangeMin) {
		List<RealRange> copyList = new ArrayList<RealRange>(rangeList);
		for (int i = 0; i < copyList.size(); i++) {
			RealRange range = copyList.get(i);
			if (range.getRange() < rangeMin) {
				rangeList.remove(range);
			}
		}
	}

	/** is the range completely contained within any subrange?
	 * 
	 * @param lowXRange
	 * @return
	 */
	public boolean includes(RealRange rr) {
		for (RealRange range : rangeList) {
			if (range.includes(rr)) return true;
		}
		return false;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	//===============================================
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Direction: "+direction+"; size: "+rangeList.size()+"\n(");
		for (int i = 0; i < rangeList.size(); i++) {
			sb.append(""+rangeList.get(i));
			if ((i+1) %5 == 0) sb.append("\n");
		}
		sb.append(")");
		return sb.toString();
	}

	public void format(int decimalPlaces) {
		for (RealRange range : rangeList) {
			range.format(decimalPlaces);
		}
	}

	public RealArray getGaps() {
		RealArray gaps = null;
		if (rangeList.size() > 1) {
			gaps = new RealArray();
			for (int i = 1; i < rangeList.size(); i++) {
				double dist = rangeList.get(i).getMin() - rangeList.get(i-1).getMax();
				gaps.addElement(dist);
			}
		}
		return gaps;
	}

	/** adds tolerance to ends of ranges
	 * see realRange.extendsRangesBy() for positive and negative tolerance
	 * if result means ranges overlap, takes the mean
	 * @param tolerance
	 */
	public void extendRangesBy(double tolerance) {
		if (rangeList.size() > 0) {
			rangeList.get(0).extendLowerEndBy(tolerance);
			for (int i = 1; i < rangeList.size(); i++) {
				RealRange range0 = rangeList.get(i-1);
				RealRange range1 = rangeList.get(i);
				double gap = range1.getMin() - range0.getMax();
				if (gap < tolerance * 2.) {
					range0.extendUpperEndBy(gap /2.);
					range1.extendLowerEndBy(gap /2.);
				} else {
					range0.extendUpperEndBy(tolerance);
					range1.extendLowerEndBy(tolerance);
				}
			}
			rangeList.get(rangeList.size()-1).extendUpperEndBy(tolerance);
		}
	}

	public boolean remove(RealRange yRange) {
		getOrCreateRangeList();
		return rangeList.remove(yRange);
	}

	private List<RealRange> getOrCreateRangeList() {
		if (rangeList == null) {
			rangeList = new ArrayList<RealRange>();
		}
		return rangeList;
	}

}
