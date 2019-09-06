/**
 *    Copyright 2011 Peter Murray-Rust
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.euclid;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * maximum and minimum values
 * 
 * Contains two doubles representing the minimum and maximum of an allowed or
 * observed range.
 * <P>
 * Default is range with low > high; this can be regarded as the uninitialised
 * state. If points are added to a default RealRange it becomes initialised.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class RealRange implements EuclidConstants, Comparable<RealRange>  {
	 
	
	private static final Logger LOG = Logger.getLogger(RealRange.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum Direction {
		HORIZONTAL,
		VERTICAL
	};
	
	private final static Pattern CURLY_PATTERN1 = Pattern.compile("\\{([^,]+)\\}");
	private final static Pattern CURLY_PATTERN2 = Pattern.compile("\\{([^,]+),([^,]+)\\}");
	private final static String ANY = "*";	
	
    /**
     * maximum of range
     */
    protected Double maxval;
    /**
     * minimum of range
     */
    protected Double minval;
    /**
     * creates invalid range from POSITIVE_INFINITY to NEGATIVE_INFINITY
     */
    public RealRange() {
        minval = Double.POSITIVE_INFINITY;
        maxval = Double.NEGATIVE_INFINITY;
    }
    /**
     * initialise with min and max values; if minv > maxv create inValid
     * RealRange
     * 
     * @param minv
     * @param maxv
     */
    public RealRange(double minv, double maxv) {
        setRange(minv, maxv);
    }
    
    /**
     * initialise with min and max values; if minv > maxv create inValid
     * RealRange
     * 
     * @param minv
     * @param maxv
     * @param normalize swap params if min > max
     */
    public RealRange(double minv, double maxv, boolean normalize) {
    	if (minv > maxv) {
    		double temp = minv;
    		minv = maxv;
    		maxv = temp;
    	}
        setRange(minv, maxv);
    }
    /** sets range.
     * overwrites any previous info
     * @param minv
     * @param maxv
     */
    public void setRange(double minv, double maxv) {
        maxval = maxv;
        minval = minv;
        if (minval > maxval) {
            minval = Double.POSITIVE_INFINITY;
            maxval = Double.NEGATIVE_INFINITY;
        }
    }
    /**
     * copy constructor
     * 
     * @param r
     */
    public RealRange(RealRange r) {
        minval = r.minval;
        maxval = r.maxval;
    }
    /**
     * from an IntRange
     * 
     * @param ir
     */
    public RealRange(IntRange ir) {
        minval = (double) ir.minval;
        maxval = (double) ir.maxval;
    }
    
	public static RealRange getRange(String s) {
		RealRange rr = null;
		RealArray ra = new RealArray(s);
		if (ra.size() == 2) {
			rr = new RealRange(ra.get(0), ra.get(1));
		}
		return rr;
	}

	/** use with care as uses ==
	 * 
	 */
    
    @Override
    public boolean equals(Object o) {
    	boolean equals = false;
    	if (o != null && o instanceof RealRange) {
    		RealRange ir =(RealRange) o;
    		equals = this.minval == ir.minval && this.maxval == ir.maxval;
    	}
    	return equals;
    }
    
    @Override
    public int hashCode() {
    	return 17*(int)Math.round(minval) + 31*(int)Math.round(maxval);
    }
    
    /**
     * a Range is only valid if its maxval is not less than its minval; this
     * tests for uninitialised ranges
     * 
     * @return valid
     */
    public boolean isValid() {
        return (minval <= maxval);
    }
    
    /**
     * invalid ranges return false
     * 
     * @param r
     * @return equal
     */
    @Deprecated
    public boolean isEqualTo(RealRange r) {
        return (r != null && Real.isEqual(minval, r.minval)
                && Real.isEqual(maxval, r.maxval) && minval <= maxval);
    }
    
    /**
     * invalid ranges return false
     * 
     * @param r
     * @return equal
     */
    public boolean isEqualTo(RealRange r, double eps) {
        return (r != null && Real.isEqual(minval, r.minval, eps)
                && Real.isEqual(maxval, r.maxval, eps) && minval <= maxval);
    }
    /**
     * combine two ranges if both valid; takes greatest limits of both, else
     * returns InValid
     * 
     * @param r2
     * @return range
     */
    public RealRange plus(RealRange r2) {
    	if (r2 == null) {
    		return this;
    	}
        if (!this.isValid()) {
            if (r2 == null || !r2.isValid()) {
                return new RealRange();
            }
            return new RealRange(r2);
        }
        RealRange temp = new RealRange(
        		Math.min(minval, r2.minval), Math.max(maxval, r2.maxval));
        return temp;
    }
    
    public RealRange plusEquals(RealRange r2) {
    	if (r2 != null) {
    		minval = Math.min(minval, r2.minval);
    		maxval = Math.max(maxval, r2.maxval);
    	} 
    	return this;
    }
    
    public boolean intersects(RealRange r2) {
    	RealRange r = this.intersectionWith(r2);
    	return r != null && r.isValid();
    }
    /**
     * intersect two ranges and take the range common to both;
     * return null if no overlap
     * 
     * @param r2
     * @return range
     */
    public RealRange intersectionWith(RealRange r2) {
    	RealRange inter = null;
        if (isValid() && r2 != null && r2.isValid()) {
	        double minv = Math.max(minval, r2.minval);
	        double maxv = Math.min(maxval, r2.maxval);
	        if (minv <= maxv) {
	        	inter = new RealRange(minv, maxv);
	        }
        }
        return inter;
    }
    
    /**
     * do two ranges (nearly) intersect?
     * if ranges intersect or are within 2*delta will return true
     * (The actual RealRange intersection will be null if they don't actually touch/overlap)
     * 
     * @param r2
     * @param delta tolerance
     * @return range
     */
    public boolean intersects(RealRange r2, double delta) {
        if (isValid() && r2 != null && r2.isValid()) {
	        double minv = Math.max(minval - delta, r2.minval -delta);
	        double maxv = Math.min(maxval + delta, r2.maxval + delta);
	        if (minv <= maxv) {
	        	return true;
	        }
        }
        return false;
    }
    /**
     * get minimum value (POSITIVE_INFINITY if inValid)
     * 
     * @return min
     */
    public Double getMin() {
        return minval;
    }
    
    /**
     * get maximum value (NEGATIVE_INFINITY if inValid)
     * 
     * @return max
     */
    public Double getMax() {
        return maxval;
    }
    
    /**
     * get centroid value (NEGATIVE_INFINITY if inValid)
     * 
     * @return centroid
     */
    public double getMidPoint() {
        return (minval + maxval) * 0.5;
    }
    
    /**
     * get range (NaN if invalid)
     * 
     * @return range
     */
    public Double getRange() {
        if (!isValid())
            return Double.NaN;
        return maxval - minval;
    }
    /**
     * does one range include another
     * 
     * @param r2
     * @return includes
     */
    public boolean includes(RealRange r2) {
        return (r2 != null && r2.isValid() && this.includes(r2.getMin()) && this
                .includes(r2.getMax()));
    }
    /**
     * is a double within a RealRange
     * 
     * If inValid, return false
     * 
     * @param f
     * @return includes
     */
    public boolean includes(double f) {
        return f >= minval && f <= maxval;
    }
    /**
     * synonym for includes()
     * 
     * @param f
     * @return includes
     */
    public boolean contains(double f) {
        return includes(f);
    }
    /**
     * add a value to a range
     * 
     * @param x
     */
    public void add(double x) {
        maxval = Math.max(maxval, x);
        minval = Math.min(minval, x);
    }
    /** return a number uniformaly distributed within the range.
     * 
     * @return number.
     */
    public Double getRandomVariate() {
        double range = maxval - minval;
        return minval + Math.random() * range;
    }

    /** get scale to convert this range to same extent as other.
     * 
     * @param range to scale to
     * @return scale or Double.NaN
     */
    public Double getScaleTo(RealRange range) {
    	Double scale = null;
    	if (range != null) {
    		try {
    			scale = range.getRange() / this.getRange();
    		} catch (Exception e) {
    			// returns null;
    		}
    	}
    	return scale;
    }
    
    /**
     * if min > max swap them
     */
    public void normalize() {
		if (minval > maxval) {
			double temp = minval;
			minval = maxval;
			maxval = temp;
		}
    }
    
    /** gets minimum signed translation required to move point into range
     * If range=(-1, 10)
     * -3 returns 2
     * -1 returns 0
     * 3 returns 0
     * 10 returns 0
     * 12 returns -2
     * @param p null returns Double.NaN
     * @return 0 if in or on range
     */
    public double distanceOutside(double d) {
    	double dd = Double.NaN;
    	if (!Double.isNaN(d)) {
    		dd = 0.0;
    		if (d < minval) {
    			dd = d - minval;
    		} else if (d > maxval) {
    			dd = maxval - d;
    		}
    	}
    	return dd;
    }
    
    /**
     * to string. format: "NULL" or EC.S_LBRAK+minval+S_COMMA+maxval+S_RBRAK;
     * 
     * @return string
     */
    public String toString() {
        return (minval > maxval) ? "NULL" : EC.S_LBRAK + minval + EC.S_COMMA + maxval + EC.S_RBRAK;
    }
	public RealRange format(Integer decimalPlaces) {
		maxval = Util.format(maxval, decimalPlaces);
		minval = Util.format(minval, decimalPlaces);
		return this;
	}
	
    /** comparees on min values
     * 
     * @param realRange
     * @return
     */
	public int compareTo(RealRange realRange) {
		if (realRange == null) {
			return -1;
		} else if (this.minval < realRange.minval) {
			return -1;
		} else if (this.minval > realRange.minval) {
			return 1;
		} else {
			if (this.maxval < realRange.maxval) {
				return -1;
			} else if (this.maxval > realRange.maxval) {
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * subtracts tolerance from min and adds to max
	 * if tolerance is negative adds and subtracts
	 * if this would result in maxval < minval sets them to mean
	 * @param tolerance
	 */
	public void extendBothEndsBy(double tolerance) {
		this.minval -= tolerance;
		this.maxval += tolerance;

		if (tolerance < 0.0) {
			if (minval > maxval) {
				double middle = (minval + maxval) /2.0;
				minval = middle;
				maxval = middle;
			}
		}
	}
	
	/** extend minval
	 * 
	 * @param tolerance if negative cannot get larger than maxval
	 */
	public void extendLowerEndBy(double tolerance) {
		this.minval -= tolerance;

		if (tolerance < 0.0) {
			if (minval > maxval) {
				minval = maxval;
			}
		}
	}
	
	/** extend maxval
	 * 
	 * @param tolerance if negative cannot get lower than minval
	 */
	public void extendUpperEndBy(double tolerance) {
		this.maxval += tolerance;

		if (tolerance < 0.0) {
			if (minval > maxval) {
				maxval = minval;
			}
		}
	}
	
	/** makes new RealRange extended by deltaMin and deltaMax.
	 * 
	 * the effect is for positive numbers to increase the range.
	 * if extensions are negative they are applied, but may result
	 * in invalid range (this is not checked at this stage).
	 * <p>
	 * Does not alter this.
	 * </p>
	 * 
	 * @param minExtend subtracted from min
	 * @param maxExtend  added to max
	 */
	public RealRange getRangeExtendedBy(double minExtend, double maxExtend) {
		return  new RealRange(minval - minExtend, maxval + maxExtend);
	}
	
	/** interprets a String as an RealRange.
	 * 
	 * {m,n} is interpreted as RealRange(m,n)
	 * {*,n} is interpreted as RealRange(any,n)
	 * {m,*} is interpreted as RealRange(m,any)
	 * {*,*} is interpreted as RealRange(any,any)
	 * {m} is interpreted as RealRange(m,m)
	 * {*} is interpreted as RealRange(any,any)
	 * 
	 * @param token
	 * @return null if cannot create a valid RealRange
	 */
	public static RealRange parseCurlyBracketString(String token) {
		RealRange intRange = null;
		if (token != null) {
			Double min = null;
			Double max = null;
			token = token.replaceAll("\\s+", ""); // strip spaces
			Matcher matcher = CURLY_PATTERN2.matcher(token);
			try {
				if (matcher.matches()) {
					String minS = matcher.group(1);
					String maxS = matcher.group(2);
					min = (ANY.equals(minS)) ? -Double.MAX_VALUE : new Double(minS);
					max = (ANY.equals(maxS)) ?  Double.MAX_VALUE : new Double(maxS);
				} else {
					matcher = CURLY_PATTERN1.matcher(token);
					if (matcher.matches()) {
						String minS = matcher.group(1);
						min = (ANY.equals(minS)) ? -Double.MAX_VALUE : new Double(minS);
						max = min;
					}
				}
				intRange = new RealRange(min, max);
			} catch (Exception e) {
				LOG.error("Cannot parse range: "+token);
			}
		}
		return intRange;
	}

	/** creates a list of RealRanges from {...} syntax.
	 * 
	 * uses parseCurlyBracketString()
	 * 
	 * @param tokens
	 * @return
	 */
	public static List<RealRange> createRealRangeList(List<String> tokens) {
		List<RealRange> realRangeList = new ArrayList<RealRange>();
		for (String token : tokens) {
			RealRange realRange = RealRange.parseCurlyBracketString(token);
			if (realRange == null) {
				throw new RuntimeException("Cannot parse ("+token+") as RealRange in : "+tokens);
			}
			realRangeList.add(realRange);
		}
		return realRangeList;
	}
	
	public static boolean isEqual(RealRange range0, RealRange range1, double eps) {
		return (range0 == null && range1 == null) ? false :
			Real.isEqual(range0.getMin(), range1.getMin(), eps) &&
			Real.isEqual(range0.getMax(), range1.getMax(), eps);
	}
	
	/** compares ranges independent of origin.
	 * 
	 * @param range
	 * @return
	 */
	public boolean isLessThan(RealRange range) {
		return range != null && getRange() < range.getRange();
	}
	
	/**
	 * scales this to range.
	 * range.origin = range.min - this.toRange * this.min
	 * @param range
	 * @return
	 */
	public Double getConstantTo(RealRange range) {
		return range == null ? null : range.getMin() - getScaleTo(range) * this.getMin();
	}
	
	/**
	 * uses bRange to transform x
	 * aligns this.min with range.min and this.max with range.max and then transform x
	 * into range 
	 * 
	 *  return getConstantTo(range) + x * range.getRange() / this.getRange()
	 *  
	 * @param range 
	 * @param x
	 * @return
	 */
	public double transformToRange(RealRange range, double x) {
// uses	 getConstantTo(bRange) + x * getScaleTo(range);
		return getConstantTo(range) + x * range.getRange() / this.getRange();
	}

}
