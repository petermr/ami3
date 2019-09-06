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

/**
 * 
 * 2-D int limits
 * 
 * Contains two IntRanges. Can therefore be used to describe 2-dimensional
 * limits (for example axes of graphs, rectangles in graphics, limits of a
 * molecule, etc.)
 * <P>
 * Default is two default/invalid IntRange components. Adding points will create
 * valid ranges.
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class Int2Range implements EuclidConstants {
    /**
     * X-range
     */
    IntRange xrange;
    /**
     * Y-range
     */
    IntRange yrange;
    /**
     * creates zero range.
     * 
     * 
     */
    public Int2Range() {
        xrange = new IntRange();
        yrange = new IntRange();
    }
    /**
     * initialise with min and max values;
     * 
     * @param xr
     * @param yr
     */
    public Int2Range(IntRange xr, IntRange yr) {
        if (xr.isValid() && yr.isValid()) {
            xrange = xr;
            yrange = yr;
        }
    }
    /**
     * copy constructor
     * 
     * @param r
     */
    public Int2Range(Int2Range r) {
        if (r.isValid()) {
            xrange = new IntRange(r.xrange);
            yrange = new IntRange(r.yrange);
        }
    }
    
    /**
     * copy constructor
     * 
     * @param r
     */
    public Int2Range(Real2Range r) {
        xrange = new IntRange(r.xrange);
        yrange = new IntRange(r.yrange);
    }
    /**
     * a Int2Range is valid if both its constituent ranges are
     * 
     * @return valid
     */
    public boolean isValid() {
        return (xrange != null && yrange != null && xrange.isValid() && yrange
                .isValid());
    }
    /**
     * is equal to.
     * 
     * @param r2
     * @return true if equal
     */
    public boolean isEqualTo(Int2Range r2) {
        if (isValid() && r2 != null && r2.isValid()) {
            return (xrange.isEqualTo(r2.xrange) && yrange.isEqualTo(r2.yrange));
        } else {
            return false;
        }
    }
    
    @Override
    public boolean equals(Object o) {
    	boolean equals = false;
    	if (o != null && o instanceof Int2Range) {
    		Int2Range i2r =(Int2Range) o;
    		equals = this.getXRange().equals(i2r.getXRange()) && 
    				 this.getYRange().equals(i2r.getYRange());
    	}
    	return equals;
    }
    
    @Override
    public int hashCode() {
    	return 17*xrange.hashCode() + 31*yrange.hashCode();
    }
    
    /**
     * merge two ranges and take the maximum extents
     * 
     * @param r2
     * @return range
     */
    public Int2Range plus(Int2Range r2) {
        if (!isValid()) {
            if (r2 == null || !r2.isValid()) {
                return new Int2Range();
            } else {
                return new Int2Range(r2);
            }
        }
        if (r2 == null || !r2.isValid()) {
            return new Int2Range(this);
        }
        return new Int2Range(xrange.plus(r2.xrange), yrange.plus(r2.yrange));
    }
    /**
     * intersect two ranges and take the range common to both; return invalid
     * range if no overlap or either is null/invalid
     * 
     * @param r2
     * @return range
     * 
     */
    public Int2Range intersectionWith(Int2Range r2) {
        if (!isValid() || r2 == null || !r2.isValid()) {
            return new Int2Range();
        }
        IntRange xr = this.getXRange().intersectionWith(r2.getXRange());
        IntRange yr = this.getYRange().intersectionWith(r2.getYRange());
        return new Int2Range(xr, yr);
    }
    /**
     * get xrange
     * 
     * @return range
     */
    public IntRange getXRange() {
        return xrange;
    }
    /**
     * get yrange
     * 
     * @return range
     */
    public IntRange getYRange() {
        return yrange;
    }
	/** extends XRange.
	 * 
	 * does not alter this. Uses range.extendBy(). Positive numbers will expand the range 
	 * 
	 * @param leftSide 
	 * @param rightSide
	 */
	public Int2Range  getInt2RangeExtendedInX(int leftSide, int rightSide) {
		Int2Range i2r = new Int2Range(this);
		if (i2r.xrange != null) {
			i2r.xrange = i2r.xrange.getRangeExtendedBy(leftSide, rightSide);
		}
		return i2r;
	}
	/** extends XRange.
	 * 
	 * does not alter this. Uses range.extendBy(). Positive numbers will expand the range 
	 * 
	 * @param topSide
	 * @param bottomSide
	 */
	public Int2Range getInt2RangeExtendedInY(int topExtend, int bottomExtend) {
		Int2Range i2r = new Int2Range(this);
		if (i2r.yrange != null) {
			i2r.yrange = i2r.yrange.getRangeExtendedBy(topExtend, bottomExtend);
		}
		return i2r;
	}

    /**
     * is an Int2 within a Int2Range
     * 
     * @param p
     * @return includes
     */
    public boolean includes(Int2 p) {
        if (!isValid()) {
            return false;
        }
        return (xrange.includes(p.getX()) && yrange.includes(p.getY()));
    }
    /**
     * is one Int2Range completely within another
     * 
     * @param r
     * @return includes
     */
    public boolean includes(Int2Range r) {
        if (!isValid() || r == null || !r.isValid()) {
            return false;
        }
        IntRange xr = r.getXRange();
        IntRange yr = r.getYRange();
        return (xrange.includes(xr) && yrange.includes(yr));
    }
    /**
     * add a Int2 to a range
     * 
     * @param p
     */
    public void add(Int2 p) {
        xrange.add(p.getX());
        yrange.add(p.getY());
    }
    /**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        return EC.S_LBRAK + (xrange == null ? "null" : xrange.toString()) 
        		+ EC.S_COMMA + (yrange == null ? "null" : yrange.toString()) + EC.S_RBRAK;
    }
    
    /** do two boxes touch?
     * 
     * if box a extends to x and box b extends from x+1 they are touching.
     * uses IntRange.touches()
     * 
     * Note that if box a and b share an integer coordinate then they *intersect*, not touch
     * 
     * @param bbox
     * @return
     */
	public boolean touches(Int2Range bbox) {
		return this.xrange.touches(bbox.xrange) || this.yrange.touches(bbox.yrange);
	}
	public Int2 getLimits() {
		return new Int2(xrange.getRange(), yrange.getRange());
	}
	
	public Integer getXMin() {
		return xrange == null ? null : xrange.getMin();
	}
	
	public Integer getXMax() {
		return xrange == null ? null : xrange.getMax();
	}
	
	public Integer getYMin() {
		return yrange == null ? null : yrange.getMin();
	}
	
	public Integer getYMax() {
		return yrange == null ? null : yrange.getMax();
	}
}