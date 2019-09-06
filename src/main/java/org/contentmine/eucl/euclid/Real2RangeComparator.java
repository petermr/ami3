package org.contentmine.eucl.euclid;

import java.util.Comparator;

import org.apache.log4j.Logger;

/** comparator for use with TreeSet<Double> and other tools which normally require equals().
 * 
 * @author pm286
 *
 */
public class Real2RangeComparator implements Comparator<Real2Range> {

	private final static Logger LOG = Logger.getLogger(Real2RangeComparator.class);

	private RealRangeComparator comparatorx;
	private RealRangeComparator comparatory;
	
	public Real2RangeComparator(RealRangeComparator comparator) {
		this.setComparators(comparator, comparator);
	}

	public Real2RangeComparator(RealRangeComparator comparatorx, RealRangeComparator comparatory) {
		this.setComparators(comparatorx, comparatory);
	}

	public Real2RangeComparator(double d) {
		this(new RealRangeComparator(d));
	}

	/**
	 * if Math.abs(d0-d1) <= epsilon  
	 * return -1 if either arg is null or any ranges in r0 or r1 are null or comparisons clash
	 */
	public int compare(Real2Range r0, Real2Range r1) {
		if (r0 == null || r1 == null) {
			// THIS IS WRONG, should be NPE
			return -1;
		}
		RealRange r0x = r0.getXRange();
		RealRange r0y = r0.getYRange();
		RealRange r1x = r1.getXRange();
		RealRange r1y = r1.getYRange();
		if (r0x == null || r1x == null || r0y == null || r1y == null) {
			return -1;
		}
		int comparex = comparatorx.compare(r0x, r1x);
		int comparey = comparatory.compare(r0y, r1y);
		return (comparex == comparey) ? comparex : -1;
	}
	
	/** set the tolerance
	 * negative values are converted to positive
	 * @param epsilon
	 */
	public void setComparators(RealRangeComparator comparatorx, RealRangeComparator comparatory) {
		this.comparatorx = comparatorx;
		this.comparatory = comparatory;
	
	}

}
