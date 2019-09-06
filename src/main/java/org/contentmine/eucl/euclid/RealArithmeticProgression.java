package org.contentmine.eucl.euclid;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** an arithmetic progression generator.
 * 
 * a short way of holding a scale or similar series with equally spaced real numbers.
 * may or may not be bounded.
 * 
 * Under development.
 * 
 * @author pm286
 *
 */
public class RealArithmeticProgression {
	private static final Logger LOG = Logger.getLogger(RealArithmeticProgression.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Double start;
	private Integer size;
	private Double delta;
	
	public RealArithmeticProgression() {
	}

	/**
	 * 
	 * note size is set to null and getEnd will return null
	 * 
	 * @param start
	 * @param delta
	 */
	public RealArithmeticProgression(double start, double delta) {
		this.start = start;
		this.delta = delta;
		this.size = null;
	}

	/**
	 * 
	 * @param start
	 * @param size number of elements (NOT number of steps, which is size-1) 
	 * @param delta
	 */
	public RealArithmeticProgression(double start, double delta, int size) {
		this.start = start;
		this.size = size;
		this.delta = delta;
	}

	/** creates AP from sorted monotonic equally spaced reals.
	 *  
	 *  note delta(i) is the actual difference between each successive pair
	 *  of reals.
	 *  meanDelta is the mean of delta(i)
	 *  epsilon is the allowed tolerance between any delta(i) and meanDelta
	 *  fails if any abs(delta(i) - meanDelta) > epsilon and returns null
	 *  
	 * @param realArray must have >= 2 elements
	 * @param epsilon allowed tolerance
	 * @return
	 */
	public static RealArithmeticProgression createAP(RealArray realArray, double epsilon) {
		RealArithmeticProgression arithmeticProgression = null;
		if (realArray != null && realArray.size() >= 2) {
			RealArray differenceArray = realArray.calculateDifferences();
			double delta = differenceArray.getMean();
			for (int i = 1; i < realArray.size(); i++) {
				double delta1 = realArray.get(i) - realArray.get(i - 1);
				if (Math.abs(delta1 - delta) > epsilon) {
					LOG.debug("no convergence in ArithProg");
					return arithmeticProgression;
				}
			}
			arithmeticProgression = new RealArithmeticProgression(realArray.get(0), delta, realArray.size());
		}
		return arithmeticProgression;
	}


	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Double getDelta() {
		return delta;
	}

	public void setDelta(Double delta) {
		this.delta = delta;
	}

	public void setStart(Double start) {
		this.start = start;
	}

	public Double getStart() {
		return start;
	}


	public Double getTerm(int i) {
		return start == null || delta == null ? null : start + (i * delta);
	}

	public Double getEnd() {
		return size == null || size < 2 ? null : getTerm(size - 1);
	}
	
	public RealArray getRealArray() {
		return new RealArray(size, start, delta);
	}
	
	public IntArray getIntArray() {
		RealArray realArray = getRealArray();
		return realArray == null ? null : realArray.createRoundIntArray();
	}
	
	public String toString() {
		return start+"("+delta+")*"+(size);
	}

}
