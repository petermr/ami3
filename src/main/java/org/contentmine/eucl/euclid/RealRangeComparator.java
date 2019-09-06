package org.contentmine.eucl.euclid;

import java.util.Comparator;

/** comparator for use with TreeSet<Double> and other tools which normally require equals().
 * 
 * seems to sort by requiring both xmin and xmax to be in same order
 * 
 * @author pm286
 *
 */
public class RealRangeComparator implements Comparator<RealRange> {


	private RealComparator comparator;
	
	public RealRangeComparator(RealComparator comparator) {
		this.setComparator(comparator);
	}

	public RealRangeComparator(double d) {
		this(new RealComparator(d));
	}

	/**
	 * if Math.abs(d0-d1) <= epsilon  
	 * return -1 if either arg is null or any ranges in r0 or r1 are null or comparisons clash
	 */
	public int compare(RealRange r0, RealRange r1) {
		if (r0 == null || r1 == null) {
			return -1;
		}
		Double r0min = r0.getMin();
		Double r0max = r0.getMax();
		Double r1min = r1.getMin();
		Double r1max = r1.getMax();
		int compareMin = comparator.compare(r0min, r1min);
		int compareMax = comparator.compare(r0max, r1max);
		return (compareMin == compareMax) ? compareMin : -1;
	}
	
	/** set the comparator
	 * @param comparator
	 */
	public void setComparator(RealComparator comparator) {
		this.comparator = comparator;
	}

}
