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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRangeComparator.End;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * maximum and minimum values
 * 
 * Contains two ints representing the minimum and maximum of an allowed or
 * observed range.
 * <P>
 * Default is range with low > high; this can be regarded as the uninitialised
 * state. If points are added to a default IntRange it becomes initialised.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class IntRange implements EuclidConstants, Comparable<IntRange> {
	
	
	private static final Logger LOG = Logger.getLogger(IntRange.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static Pattern CURLY_PATTERN1 = Pattern.compile("\\{([^,]+)\\}");
	private final static Pattern CURLY_PATTERN2 = Pattern.compile("\\{([^,]+),([^,]+)\\}");
	private final static Pattern WILD_PATTERN2 = Pattern.compile("\\s*[\\(\\{]?\\s*(\\-?\\d+)[,|\\s+]\\s*(\\-?\\d+)\\s*[\\)\\}]?\\s*"); // crude
	private final static String ANY = "*";

	public final static IntRangeComparator ASCENDING_MIN_COMPARATOR = new IntRangeComparator(End.MIN);
	
    /**
     * maximum of range
     */
    protected int maxval;
    /**
     * minimum of range
     */
    protected int minval;
    /**
     * creates invalid range from MAX_VALUE to MIN_VALUE
     */
    public IntRange() {
        minval = Integer.MAX_VALUE;
        maxval = Integer.MIN_VALUE;
    }
    /**
     * initialise with min and max values; if minv > maxv create inValid
     * IntRange
     * 
     * @param minv
     * @param maxv
     */
    public IntRange(int minv, int maxv) {
        maxval = maxv;
        minval = minv;
        if (minval > maxval) {
            minval = Integer.MAX_VALUE;
            maxval = Integer.MIN_VALUE;
        }
    }
    /**
     * copy constructor
     * 
     * @param r
     */
    public IntRange(IntRange r) {
        minval = r.minval;
        maxval = r.maxval;
    }
    
    /**
     * 
     * @param r
     */
    public IntRange(RealRange r) {
        minval = (int) Math.round(r.minval);
        maxval = (int) Math.round(r.maxval);
    }

    /** create from integers
     * traps invalid IntRange
     * 
     * @param min
     * @param max
     * @return null if invalid
     */
    public static IntRange create(int min, int max) {
    	return min > max ? null : new IntRange(min, max);
    }
    
    /** create from strings in the wild such as 
     * 1,3
     * {1,3}
     * (1, 3)
     * -3 -1
     * @param s
     * @return null if fails
     */
    public static IntRange parse(String s) {
    	IntRange intRange = null;
    	if (s != null) {
    		Matcher matcher = WILD_PATTERN2.matcher(s);
			if (matcher.matches()) {
    			intRange = IntRange.create(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
    		}
    	}
    	return intRange;
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
     * @return equals
     * 
     */
    public boolean isEqualTo(IntRange r) {
        return (r != null && minval == r.minval && maxval == r.maxval && minval <= maxval);
    }
    
    
    @Override
    public boolean equals(Object o) {
    	boolean equals = false;
    	if (o != null && o instanceof IntRange) {
    		IntRange ir =(IntRange) o;
    		equals = this.minval == ir.minval && this.maxval == ir.maxval;
    	}
    	return equals;
    }
    
    @Override
    public int hashCode() {
    	return 17*minval + 31*maxval;
    }
    
    /**
     * combine two ranges if both valid; takes greatest limits of both, else
     * returns InValid
     * 
     * @param r2
     * @return range
     */
    public IntRange plus(IntRange r2) {
        if (!this.isValid()) {
            if (r2 == null || !r2.isValid()) {
                return new IntRange();
            }
            return new IntRange(r2);
        }
        IntRange temp = new IntRange();
        temp = new IntRange(Math.min(minval, r2.minval), Math.max(maxval,
                r2.maxval));
        return temp;
    }
    
    public boolean intersectsWith(IntRange r2) {
    	IntRange r = this.intersectionWith(r2);
    	return r != null && r.isValid();
    }
    /**
     * intersect two ranges and take the range common to both; return invalid
     * range if no overlap
     * 
     * @param r2
     * @return range
     */
    public IntRange intersectionWith(IntRange r2) {
        if (!isValid() || r2 == null || !r2.isValid()) {
            return new IntRange();
        }
        int minv = Math.max(minval, r2.minval);
        int maxv = Math.min(maxval, r2.maxval);
        return new IntRange(minv, maxv);
    }
    /**
     * get minimum value (MAX_VALUE if inValid)
     * 
     * @return min
     */
    public int getMin() {
        return minval;
    }
    /**
     * get maximum value (MIN_VALUE if inValid)
     * 
     * @return max
     * 
     */
    public int getMax() {
        return maxval;
    }
    /**
     * get range (MIN_VALUE if invalid)
     * 
     * @return range
     */
    public int getRange() {
        if (!isValid())
            return Integer.MIN_VALUE;
        return maxval - minval;
    }
    /**
     * does one range include another
     * 
     * @param r2
     * @return includes
     */
    public boolean includes(IntRange r2) {
        return (r2 != null && r2.isValid() && this.includes(r2.getMin()) && this
                .includes(r2.getMax()));
    }
    /**
     * is a int within a IntRange
     * 
     * @param f
     * @return includes If inValid, return false
     */
    public boolean includes(int f) {
        return f >= minval && f <= maxval;
    }
    /**
     * synonym for includes()
     * 
     * @param f
     * @return includes
     */
    public boolean contains(int f) {
        return includes(f);
    }
    /**
     * add a value to a range
     * 
     * @param x
     */
    public void add(int x) {
        maxval = Math.max(maxval, x);
        minval = Math.min(minval, x);
    }
    /**
     * to string
     * 
     * @return string
     */
    public String toString() {
        return (minval > maxval) ? "NULL" : S_LBRAK + minval + S_COMMA + maxval + S_RBRAK;
    }

    /** comparees on min values
     * 
     * @param intRange
     * @return
     */
	public int compareTo(IntRange intRange) {
		if (intRange == null) {
			return -1;
		} else if (this.minval < intRange.minval) {
			return -1;
		} else if (this.minval > intRange.minval) {
			return 1;
		} else {
			if (this.maxval < intRange.maxval) {
				return -1;
			} else if (this.maxval > intRange.maxval) {
				return 1;
			}
		}
		return 0;
	}
	
	/** makes new IntRange extended by deltaMin and deltaMax.
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
	public IntRange getRangeExtendedBy(int minExtend, int maxExtend) {
		return  new IntRange(minval - minExtend, maxval + maxExtend);
	}
	
	/** do ranges touch but not overlap?
	 * 
	 * range from [a,b] inclusive touches [c,a-1] or [b+1,c]
	 * 
	 * ([a,b] overlaps with [b,c])
	 * @param range
	 * @return
	 */
	public boolean touches(IntRange range) {
		return range != null && 
			(this.maxval + 1 == range.minval || range.maxval + 1 == this.minval); 
	}

	/** mid point of range.
	 * 
	 * @return
	 */
	public int getMidPoint() {
		return (minval + maxval)/2;
	}
	
	/** interprets a String as an IntRange.
	 * 
	 * {m,n} is interpreted as IntRange(m,n)
	 * {*,n} is interpreted as IntRange(any,n)
	 * {m,*} is interpreted as IntRange(m,any)
	 * {*,*} is interpreted as IntRange(any,any)
	 * {m} is interpreted as IntRange(m,m)
	 * {*} is interpreted as IntRange(any,any)
	 * 
	 * @param token
	 * @return null if cannot create a valid IntRange
	 */
	public static IntRange parseCurlyBracketString(String token) {
		IntRange intRange = null;
		if (token != null) {
			Integer min = null;
			Integer max = null;
			token = token.replaceAll("\\s+", ""); // strip spaces
			Matcher matcher = CURLY_PATTERN2.matcher(token);
			try {
				if (matcher.matches()) {
					String minS = matcher.group(1);
					String maxS = matcher.group(2);
					min = (ANY.equals(minS)) ? -Integer.MAX_VALUE : new Integer(minS);
					max = (ANY.equals(maxS)) ?  Integer.MAX_VALUE : new Integer(maxS);
				} else {
					matcher = CURLY_PATTERN1.matcher(token);
					if (matcher.matches()) {
						String minS = matcher.group(1);
						min = (ANY.equals(minS)) ? -Integer.MAX_VALUE : new Integer(minS);
						max = min;
					}
				}
				intRange = new IntRange(min, max);
			} catch (Exception e) {
				LOG.error("Cannot parse range: "+token);
			}
		}
		return intRange;
	}

	/** creates a list of IntRanges from {...} syntax.
	 * 
	 * uses parseCurlyBracketString()
	 * 
	 * @param tokens
	 * @return
	 */
	public static List<IntRange> createIntRangeList(List<String> tokens) {
		List<IntRange> intRangeList = new ArrayList<IntRange>();
		for (String token : tokens) {
			IntRange intRange = IntRange.parseCurlyBracketString(token);
			if (intRange == null) {
				throw new RuntimeException("Cannot parse ("+token+") as IntRange in : "+tokens);
			}
			intRangeList.add(intRange);
		}
		return intRangeList;
	}
	
	/** joins intRanges A and B when A.getMax() == B.getMin().
	 * Ranges must either "join" within a tolerance or be disjoint.
	 * Overlapping ranges will throw an RuntimeException.
	 * 
	 * (1,5), (5,10), (10,15)
	 *  creates
	 *  (1,15)
	 *  
	 * (1,5), (5,10), (15,20), (20, 25)
	 *  creates
	 *  (1,10) (15, 25)
	 *  
	 * (1,5),  (15,20)
	 *  creates
	 *  (1,5) (15, 20)
	 *  
	 *  (1,5) (2,10) (5, 12) throws an Exception
	 *  
	 * @param rangeList can be in any order, not altered 
	 * @param tolerance allows for overlap, i.e. (1,5) and (4,10) would "touch" , so would (1,5) and (6,10)
	 * @return List of non-touching joined ranges, sorted by new R.getMin()
	 */
	public static List<IntRange> joinRanges(List<IntRange> rangeList, int tolerance) {
		if (rangeList.size() <= 1) {
			return rangeList;
		}
		List<IntRange> sortedList = new ArrayList<IntRange>();
		for (IntRange range : rangeList) {
			sortedList.add(new IntRange(range)); // must copy to preserve original list unaltered
		}
		Collections.sort(sortedList, ASCENDING_MIN_COMPARATOR);
		List<IntRange> newList = new ArrayList<IntRange>();
		IntRange currentRange = sortedList.get(0);
		newList.add(currentRange);
		for (int i = 1; i < sortedList.size(); i++) {
			IntRange range = sortedList.get(i);
			if (currentRange.canJoin(range, tolerance)) {
				currentRange.setMax(range.getMax());
			} else {
				if (range.getMin() < currentRange.getMax()) {
					throw new RuntimeException("ranges overlap: "+currentRange+" / "+range);
				}
				currentRange = range;
				newList.add(currentRange);
			}
		}
		return newList;
	}
	
	/** can two ranges be joined at a common point?
	 * (1,5) and (5,10) can be joined.
	 * Because ranges are sometimes created from real numbers, a tolerance is allowed. 
	 * This should should normally not be set beyond 1
	 * (1,5) and (6,10) would join with tolerance=1, as would (1,5) and (4,10)
	 * (1,5) and (3,10) would not be joinable with tolerance=1
	 * 
	 * @param range
	 * @param tolerance
	 * @return
	 */
	public boolean canJoin(IntRange range, int tolerance) {
		return Math.abs(this.maxval - range.minval) <= tolerance; 
	}
//	public static Iterable<Multiset.Entry<Integer>> getIntegerEntriesSortedByCount(Multiset<Integer> integerSet) {
//		return Multisets.copyHighestCountFirst(integerSet).entrySet();
//	}

	
	public static List<Multiset.Entry<IntRange>> getIntRangeEntryListSortedByCount(Multiset<IntRange> intRangeSet) {
		return Lists.newArrayList(IntRange.getIntRangeEntriesSortedByCount(intRangeSet));
	}
	
	public static Iterable<Multiset.Entry<IntRange>> getIntRangeEntriesSortedByCount(Multiset<IntRange> intRangeSet) {
		return Multisets.copyHighestCountFirst(intRangeSet).entrySet();
	}


	public void setMax(int max) {
		if (max >= this.minval) {
			this.maxval = max;
		}
	}

	public void setMin(int min) {
		if (min <= this.maxval) {
			this.minval = min;
		}
	}
	
	/** create sorted list from a set of IntRanges.
	 * 
	 * @param intRangeSet
	 * @return
	 */
	public static List<IntRange> createSortedList(Set<IntRange> intRangeSet) {
		List<IntRange> intRangeList = new ArrayList<IntRange>(intRangeSet);
		Comparator<IntRange> lowEndComparator = new IntRangeComparator(IntRangeComparator.End.MIN);
		Collections.sort(intRangeList, lowEndComparator);
		return intRangeList;
	}
	
	/** creates integer array from min to max inclusive
	 * returns emptyList if invalid or min = Integer.MIN_VALUE or max = Integer.MAX_VALUE
	 * @return
	 */
	public IntArray createArray() {
		IntArray intArray = new IntArray(); 
		if (isValid() && minval > Integer.MIN_VALUE && maxval < Integer.MAX_VALUE) {
			for (int i = minval; i <= maxval; i++) {
				intArray.addElement(i);
			}
		}
		return intArray;
		
	}
	/** alters this
	 * 
	 * @param intRange2
	 * @return
	 */
	public IntRange not(IntRange intRange2) {
		if (this.maxval > intRange2.minval) {
			this.maxval = Math.max(minval, Math.min(this.maxval, intRange2.minval));
		}
		if (this.minval < intRange2.maxval) {
			this.minval = Math.min(maxval, Math.max(this.minval, intRange2.maxval));
		}
		return this;
	}
}
