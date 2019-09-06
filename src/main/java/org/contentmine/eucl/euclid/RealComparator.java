package org.contentmine.eucl.euclid;

import java.util.Comparator;

/** comparator for use with TreeSet<Double> and other tools which normally require equals().
 * epsilon is initially set to zero, so only exact equality matches
 * 
 * @author pm286
 *
 */
public class RealComparator implements Comparator<Double> {

	private double epsilon = 0.0d;

	public RealComparator(double eps) {
		this.setEpsilon(eps);
	}

	/**
	 * if Math.abs(d0-d1) <= epsilon  
	 * return -1 if either arg is null
	 */
	public int compare(Double d0, Double d1) {
		if (d0 == null || d1 == null) {
			// THIS IS WRONG, it should throw NullPointer
			return -1;
		}
		double delta = Math.abs(d0 - d1);
		if (delta <= epsilon) {
			return 0;
		}
		return (d0 < d1) ? -1 : 1;
	}
	
	/** set the tolerance
	 * negative values are converted to positive
	 * @param epsilon
	 */
	public void setEpsilon(double epsilon) {
		this.epsilon = Math.abs(epsilon);
	}

}
