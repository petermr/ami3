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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


/**
 * Real2Array is NOT a Vector of Real2s but a container for a 2 * n matrix
 * 
 * with a variety of ways of managing the data including RealArrays for the x
 * and y arrays, and also an array of Real2s The latter is only stored if
 * required, and then is cached.
 * 
 * Note that we also have Real2Vector which acts like a List<Real2>. We may
 * therefore obsolete the Real2Array at some time - it is not used in Jumbo
 * elsewhere although it might be useful for spectra.
 * 
 * NOTE: I think it's used quite a lot in fact...
 * 
 * @author P.Murray-Rust, Copyright 1997
 */
public class Real2Array implements EuclidConstants ,  Iterable<Real2>  {
	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(Real2Array.class);
	
    RealArray xarr;
    RealArray yarr;
    int nelem = 0;
    /**
     * default constructor gives an array of 0 points
     */
    public Real2Array() {
    }
    /**
     * get max and min value of Real2_Array
     * 
     * @return range
     */
    public Real2Range getRange2() {
        Real2Range range = new Real2Range();
        for (int i = 0; i < nelem; i++) {
            Real2 r2 = new Real2(xarr.elementAt(i), yarr.elementAt(i));
            range.add(r2);
        }
        return range;
    }

    /** copy
     * 
     * @param r2a
     */
    public Real2Array(Real2Array r2a) {
    	if (r2a != null && !r2a.equals(this)) {
    		RealArray xx = r2a.getXArray();
    		if (xx == null) {
    			throw new RuntimeException("Null xarr");
    		}
    		RealArray yy = r2a.getYArray();
    		if (yy == null) {
    			throw new RuntimeException("Null yarr");
    		}
    		xarr = xx == null ? null : new RealArray(xx);
    		yarr = yy == null ? null : new RealArray(yy);
    		this.nelem = r2a.nelem;
    	}
    }
    /**
     * make an Real2_Array from 2 RealVec's; takes a copy
     * 
     * @param x
     * @param y
     * @exception EuclidRuntimeException
     *                x and x must have number of elements
     */
    public Real2Array(RealArray x, RealArray y) throws EuclidRuntimeException {
        if (x.size() != y.size()) {
            throw new EuclidRuntimeException("incompatible array sizes "+x.size()+"/"+y.size());
        }
        nelem = x.size();
        xarr = (RealArray) x.clone();
        yarr = (RealArray) y.clone();
    }

    
    /** create with RealArrays of pre-allocated size.
     * 
     * @param i
     */
    public Real2Array(int size) {
    	this(new RealArray(size), new RealArray(size));
	}
    
    /** create from list of points.
     * 
     * @param points
     */
	public Real2Array(List<Real2> points) {
		for (Real2 point : points) {
			this.add(point);
		}
	}
	
	
	/**
     * compares the arrays
     * @param r2b array to compare to
     * @param epsilon tolerance in coordinates
     * @return true if all points agree within epsilon
     */
	public boolean isEqualTo(Real2Array r2b, double epsilon) {
		if (r2b == null || this.size() != r2b.size()) {
			return false;
		}
		for (int i = 0; i < this.size(); i++) {
			Real2 ra = this.get(i);
			Real2 rb = r2b.get(i);
			if (!ra.isEqualTo(rb, epsilon)) {
				return false;
			}
		}
		return true;
	}

    /**
     * appends an xy coordinate
     * @param r2
     */
    public void addElement(Real2 r2) {
    	if (nelem == 0 || xarr == null || yarr == null) {
    		xarr = new RealArray();
    		yarr = new RealArray();
    	}
    	xarr.addElement(r2.getX());
    	yarr.addElement(r2.getY());
    	nelem++;
    }
    
    /**
     * appends an xy coordinate
     * @param r2
     */
    @Deprecated // confusable with Plus // use addElement()
    public void add(Real2 r2) {
    	if (nelem == 0 || xarr == null || yarr == null) {
    		xarr = new RealArray();
    		yarr = new RealArray();
    	}
    	xarr.addElement(r2.getX());
    	yarr.addElement(r2.getY());
    	nelem++;
    }
    
    /**
     * @param real2Array
     */
    public void add(Real2Array real2Array) {
    	if (real2Array != null) {
	    	if (nelem == 0 || xarr == null || yarr == null) {
	    		xarr = new RealArray();
	    		yarr = new RealArray();
	    	}
	    	xarr.addArray(real2Array.xarr);
	    	yarr.addArray(real2Array.yarr);
	    	nelem += real2Array.size();
	    }
    }
    
    /**
     * make an Real2_Array from pairs of numbers separated by delimiter
     * 
     * @param s
     * @param delimiter (might be a regex e.g. ",| " (comma or space))
     * @exception EuclidRuntimeException
     *                x and x must have number of elements
     */
    public static Real2Array createFromPairs(String sss, String delimiter) {
    	Real2Array real2Array = null;
    	if (sss != null) {
    		String[] ss = sss.trim().split(delimiter);
    		if (ss.length % 2 == 0) {
            	RealArray realArray = new RealArray(ss);
    	    	real2Array = Real2Array.createFromPairs(realArray);
    		}
    	}
    	return real2Array;
    }
    
    /**
     * make an Real2_Array from pairs of numbers x1,y1 .. x2,y2 .. etc
     * 
     * @param s
     * @param delimiter
     * @exception EuclidRuntimeException
     *                x and x must have number of elements
     */
    public static Real2Array createFromPairs(RealArray ra) {
    	if (ra == null) {
    		throw new RuntimeException("Null RealArray");
    	}
    	if (ra.size() % 2 != 0) {
    		throw new RuntimeException("Must have even number of points");
    	}
    	Real2Array real2Array = new Real2Array();
    	real2Array.xarr = new RealArray();
    	real2Array.yarr = new RealArray();
    	for (int i = 0; i < ra.size(); ) {
    		real2Array.xarr.addElement(ra.elementAt(i++));
    		real2Array.yarr.addElement(ra.elementAt(i++));
    		real2Array.nelem++;
    	}
    	return real2Array;
    }
    
    /**
     * extract X array.
     * 
     * @return array
     */
    public RealArray getXArray() {
        return xarr;
    }
    /**
     * extract Y array.
     * 
     * @return array
     */
    public RealArray getYArray() {
        return yarr;
    }
    /**
     * size of array.
     * 
     * @return size
     */
    public int size() {
        return nelem;
    }
    /**
     * get element.
     * 
     * @param elem
     * @return element
     */
    public Real2 elementAt(int elem) {
        return new Real2(xarr.elementAt(elem), yarr.elementAt(elem));
    }
    
    /**
     * get element.
     * 
     * @param elem
     * @return element
     */
    public Real2 get(int elem) {
        return new Real2(xarr.elementAt(elem), yarr.elementAt(elem));
    }
    
    /**
     * get element.
     * 
     * @param elem
     * @return element
     */
    public void setElement(int elem, Real2 r2) {
    	xarr.setElementAt(elem, r2.getX());
    	yarr.setElementAt(elem, r2.getY());
    }
    
    /** delete element.
     * 
     * @param i
     */
    public void deleteElement(int i) {
    	if (i >= 0 && i < nelem) {
	    	xarr.deleteElement(i);
	    	yarr.deleteElement(i);
	    	nelem--;
    	} else {
    		throw new EuclidRuntimeException("Cannt delete element at: "+i);
    	}
    }
    
    public void transformBy(Transform2 t2) {
    	for (int i = 0; i < nelem; i++) {
    		Real2 xy = this.get(i);
    		xy.transformBy(t2);
    		this.setElement(i, xy);
    	}
    }
    

    /**
     * adds r2 to each element of the array.
     * Changes this
     * @param r2
     * @return this
     */
    public Real2Array plusEquals(Real2 r2) {
    	for (int i = 0;i < nelem; i++) {
    		Real2 xy = this.get(i);
    		xy = xy.plus(r2);
    		this.setElement(i, xy);
    	}
    	return this;
    }
    
    /** round to decimal places.
     * 
     * @param places
     * @return this
     */
    public Real2Array format(int places) {
    	if (xarr != null && yarr != null) {
	    	double[] xarray = xarr.getArray();
	    	double[] yarray = yarr.getArray();
	    	for (int i = 0; i < nelem; i++) {
	    		xarray[i] = Util.format(xarray[i], places);
	    		yarray[i] = Util.format(yarray[i], places);
	    	}
    	}
    	return this;
    }
 	
	/**
     * to space-separated string.
     * 
     * @return string
     */
    public String getStringArray() {
        // don't change this routine!!!
        StringBuffer s = new StringBuffer();
        double[] xarray = (xarr == null) ? null : xarr.getArray();
        double[] yarray = (yarr == null) ? null : yarr.getArray();
        for (int i = 0; i < nelem; i++) {
        	if (i > 0) {
                s.append(S_SPACE);
        	}
            s.append(xarray[i]);
            s.append(S_SPACE);
            s.append(yarray[i]);
        }
        return s.toString();
    }
    
	/**
     * to string.
     * 
     * @return string
     */
    public String toString() {
        // don't change this routine!!!
        StringBuffer s = new StringBuffer();
        double[] xarray = (xarr == null) ? null : xarr.getArray();
        double[] yarray = (yarr == null) ? null : yarr.getArray();
        s.append(S_LBRAK);
        for (int i = 0; i < nelem; i++) {
            s.append(S_LBRAK);
            s.append(xarray[i]);
            s.append(S_COMMA);
            s.append(yarray[i]);
            s.append(S_RBRAK);
        }
        s.append(S_RBRAK);
        return s.toString();
    }
	public Real2 getLastElement() {
		Real2 xy = null;
		if (nelem > 0) {
			xy = get(nelem-1);
		}
		return xy;
	}
	/** reverse direction of array MODIFIES THIS
	 * 
	 */
	public void reverse() {
		xarr.reverse();
		yarr.reverse();
	}

	/**
	 * sorts so that x0y0 has lowest x (0) or y (1)
	 * @param xy = 0 sort on x; xy = 1 sort on y 
	 */
	public void sortAscending(int xy) {
		IntSet is = null;
		if (xy == 0) {
			is = xarr.indexSortAscending();
		} else if (xy == 1) {
			is = yarr.indexSortAscending();
		}
		xarr = xarr.createReorderedArray(is);
		yarr = yarr.createReorderedArray(is);
	}

	/**
	 * sorts so that x0y0 has highest x (0) or y (1)
	 * @param xy = 0 sort on x; xy = 1 sort on y 
	 */
	public void sortDescending(int xy) {
		IntSet is = null;
		if (xy == 0) {
			is = xarr.indexSortDescending();
		} else if (xy == 1) {
			is = yarr.indexSortDescending();
		}
		xarr = xarr.createReorderedArray(is);
		yarr = yarr.createReorderedArray(is);
	}

	/** create subArray starting at start inclusive
	 * 
	 * @param start
	 * @return
	 */
	public Real2Array createSubArray(int start) {
		return createSubArray(start, xarr.size()-1);
	}
	
	/** create subArray
	 * 
	 * @param start inclusive
	 * @param end inclusive
	 * @return
	 */
	public Real2Array createSubArray(int start, int end) {
		RealArray xarr = this.xarr.getSubArray(start, end);
		RealArray yarr = this.yarr.getSubArray(start, end);
		return new Real2Array(xarr, yarr);
	}
	
	/** creates from output representation.
	 * 
	 * @param coords format "((1.1,2.2)(3.3,4.4)(5.5,6.6))"
	 * @return array or null if fails
	 */
	public static Real2Array createFromCoords(String coords) {
		Real2Array real2Array = null;
		if (coords != null) {
			coords = coords.trim();
			if (coords.startsWith(EuclidConstants.S_LBRAK) && coords.endsWith(EuclidConstants.S_RBRAK)) {
				real2Array = new Real2Array();
				coords = coords.substring(1, coords.length()-1);
				Pattern COORD_PATTERN = Pattern.compile("\\(([0-9\\.\\-\\+]+)\\,([0-9\\.\\-\\+]+)\\)");
				Matcher matcher = COORD_PATTERN.matcher(coords);
				while (matcher.find()) {
					try {
						double dd[] = new double[2];
						dd[0] = Util.parseFlexibleDouble(matcher.group(1));
						dd[1] = Util.parseFlexibleDouble(matcher.group(2));
						Real2 coord = new Real2(dd);
						real2Array.addElement(coord);
					} catch (Exception e) {
						LOG.trace("bad coord "+e);
						real2Array = null;
						break;
					}
				}
			}
		}
		return real2Array;
	}
	
	public Real2 getPointWithMinimumX() {
		int idx = getIndexOfPointWithMinimumX();
		return idx == -1 ? null : get(idx);
	}
	
	public int getIndexOfPointWithMinimumX() {
		RealArray xArray = getXArray();
		return (xArray == null || xArray.size() == 0) ? -1 : xArray.indexOfSmallestElement();
	}
    
	public Real2 getPointWithMaximumX() {
		int idx = getIndexOfPointWithMaximumX();
		return idx == -1 ? null : get(idx);
	}
	
	public int getIndexOfPointWithMaximumX() {
		RealArray xArray = getXArray();
		return (xArray == null || xArray.size() == 0) ? -1 : xArray.indexOfLargestElement();
	}
    
	public Real2 getPointWithMinimumY() {
		int idx = getIndexOfPointWithMinimumY();
		return idx == -1 ? null : get(idx);
	}
	
	public int getIndexOfPointWithMinimumY() {
		RealArray yArray = getYArray();
		return (yArray == null || yArray.size() == 0) ? -1 : yArray.indexOfSmallestElement();
	}
    
	public Real2 getPointWithMaximumY() {
		int idx = getIndexOfPointWithMaximumY();
		return idx == -1 ? null : get(idx);
	}
	
	public int getIndexOfPointWithMaximumY() {
		RealArray yArray = getYArray();
		return (yArray == null || yArray.size() == 0) ? -1 : yArray.indexOfLargestElement();
	}

	/** gets average of two Real2Arrays.
	 * 
	 * @param real2Array
	 * @return null if arrays are null or of different sizes
	 */
	public Real2Array getMidPointArray(Real2Array real2Array) {
		Real2Array new2Array = null;
		if (real2Array != null && this.nelem == real2Array.nelem) {
			RealArray newXArray = (this.xarr.plus(real2Array.xarr)).multiplyBy(0.5);
			RealArray newYArray = (this.yarr.plus(real2Array.yarr)).multiplyBy(0.5);
			new2Array = new Real2Array(newXArray, newYArray);
		}
		return new2Array;
	}
	
	/** gets unweighted mean of points.
	 * 
	 * @return null if no points
	 */
	public Real2 getMean() {
		Real2 mean = null;
		if (xarr != null && nelem > 0) {
			double xMean = xarr.getMean();
			double yMean = yarr.getMean();
			mean = new Real2(xMean, yMean);
		}
		return mean;
	}
	public Iterator<Real2> iterator() {
		Real2Iterator iterator = (xarr == null || yarr == null || xarr.size() != yarr.size()) ? null : new Real2Iterator(this);
		return iterator;
	}
	public Double sumProductOfAllElements() {
		return (xarr == null || yarr == null) ? null : xarr.sumProductOfAllElements(yarr);
	}
	public Real2 getLastPoint() {
		return nelem == 0 ? null : new Real2(xarr.elementAt(nelem-1), yarr.elementAt(nelem-1));
	}
	
	public List<Real2> getList() {
		List<Real2> points = new ArrayList<Real2>();
		for (int i = 0; i < nelem; i++) {
			points.add(new Real2(xarr.elementAt(i), yarr.elementAt(i)));
		}
		return points;
	}
	
	public static Real2Array createReal2Array(Real2 ... points) {
		List<Real2> pointList = points == null ? new ArrayList<Real2>() : Arrays.asList(points);
		return new Real2Array(pointList);
	}

	/** multiples (scales) all elemnts of this.
	 * 
	 * @param d
	 * @return
	 */
	public void multiplyBy(double d) {
		xarr = xarr.multiplyBy(d);
		yarr = yarr.multiplyBy(d);
	}
	
	/** rotates array geometrically about geometric midpoint.
	 * xymid = (xy0+xyn )/2.
	 * then xyj = 2*xymid - xyk, where j+k = n
	 * 
	 * @return modified array
	 */
	public Real2Array getRotatedAboutMidPoint() {
		Real2Array rotatedXY = new Real2Array(nelem);
		if (nelem > 0) {
			int last = nelem - 1;
			Real2 midxy = this.get(0).getMidPoint(this.get(last));
			Real2 midxy2 = midxy.plus(midxy);
			for (int i = 0; i < nelem; i++) {
				Real2 rotated2 = midxy2.subtract(this.get(i));
				rotatedXY.setElement(i, rotated2);
			}
		}
		return rotatedXY;
	}
	public Real2 getMidpointOfEnds() {
		return (get(0).plus(get(size() - 1))).multiplyBy(0.5);
	}
	public RealArray calculateDeviationsRadiansPerElement() {
		int size = size();
		RealArray curvature = new RealArray(size - 2);
		for (int j = 1; j < size - 1; j++) {
			int i = (j - 1) ;
			int k = (j + 1) ;
			Angle angle = Real2.getAngle(get(i), get(j), get(k));
			angle.normalizeTo2Pi();
			double ang = angle.getRadian() - Math.PI;
			curvature. setElementAt(i, ang);
		}
		return curvature;
	}
	/** creates 4 roughly equal segments and calculates 3 values of curvature
	 * "units" are pixels per radian. 
	 * 
	 * @return
	 */
	public RealArray calculate4SegmentedCurvature() {
		RealArray curvature = null;
		// divide into 4
		int size = size();
		// has to be large enough to be meaningful
		if (size > 4) {
			Real2Array segments = new Real2Array(5);
			segments.setElement(0, get(0));
			int mid = size / 2;
			segments.setElement(2, get(mid));
			int q1 = mid / 2;
			segments.setElement(1, get(q1));
			int q3 = (mid + size) / 2;
			segments.setElement(3, get(q3));
			segments.setElement(4, getLastPoint());
			curvature = segments.calculateDeviationsRadiansPerElement();
		}
		return curvature;
	}
	
	public void addAll(Real2Array coordinateArray) {
		if (coordinateArray != null) {
			for (Real2 coord : coordinateArray) {
				this.addElement(coord);
			}
		}
	}
	public List<String> createCSVRows(String xTitle, String yTitle) {
		List<String> rowList = new ArrayList<String>();
		rowList.add("row, " + xTitle + ", " + yTitle + ", \n");
		for (int i = 0; i < size(); i++) {
			Real2 xy = get(i);
			rowList.add((i+1) + "," + xy.getX() + "," + xy.getY() + "," + "\n");
		}
		return rowList;
	}
	
	
}
class Real2Iterator implements Iterator<Real2> {

	private Iterator<Double> xIterator;
	private Iterator<Double> yIterator;
	
	public Real2Iterator(Real2Array real2Array) {
		this.xIterator = real2Array.xarr.iterator();
		this.yIterator = real2Array.yarr.iterator();
	}
	
	public boolean hasNext() {
		return xIterator.hasNext() && yIterator.hasNext();
	}

	public Real2 next() {
		Double x = xIterator.next();
		Double y = yIterator.next();
		return (x == null || y == null) ? null : new Real2(x, y);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
