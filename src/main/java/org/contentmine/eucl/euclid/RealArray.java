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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.MultisetUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * array of doubles
 * 
 * RealArray represents a 1-dimensional array of doubles and is basically a
 * wrapper for double[] in Java There are a lot of useful member functions
 * (sorting, ranges, parallel operations
 * 
 * The default is an array with zero points All arrays are valid objects.
 * 
 * Attempting to create an array with < 0 points creates a default array (zero
 * points).
 * 
 * Since double[] knows its length (unlike C), there are many cases where
 * double[] can be safely used. However it is not a first-class object and
 * RealArray supplies this feature. double[] is referenceable through getArray()
 * 
 * note that the length of the internal array may not be a useful guide to the
 * number of elements. Use contractArray() to adjust arraySize to number of
 * elements (useful after adding elements).
 * 
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public class RealArray extends ArrayBase implements Iterable<Double> {
	
    final static Logger LOG = Logger.getLogger(RealArray.class);
    /** filter */
    public enum Filter {
        /** */
        GAUSSIAN("Gaussian"),
        /** */
        GAUSSIAN_FIRST_DERIVATIVE("Gaussian First Derivative"),
        /** */
        GAUSSIAN_SECOND_DERIVATIVE("Gaussian Second Derivative");
        /** string value */
        public String string;
        private Filter(String s) {
            this.string = s;
        }
    }
    
	public enum Monotonicity {
		INCREASING,
		DECREASING
	}
	
    /**
     * maximum number of elements (for bound checking) - resettable
     */
    private int maxelem = 10000;
    /**
     * actual number of elements
     */
    private int nelem;
    /**
     * the array of doubles
     */
    private double[] array;
    private int bufsize = 5;
    private DecimalFormat format = null;
    
    /**
     * create default Array. default is an array of zero points
     */
    public RealArray() {
        nelem = 0;
        bufsize = 5;
        array = new double[bufsize];
    }
    /**
     * checks potential size of array. if n < 0, set to 0, otherwise adjust
     * bufsize to be consistent
     * 
     * @param n
     *            size of array
     * @return false if negative
     */
    private boolean checkSize(int n) {
        if (n < 0) {
            n = 0;
            return false;
        } else {
            nelem = n;
            if (nelem > maxelem)
                maxelem = nelem;
            if (bufsize < nelem)
                bufsize = nelem;
            return true;
        }
    }
    // expands buffer if necessary and copies array into it
    private void makeSpace(int newCount) {
    	if (bufsize < 5) {
    		bufsize = 5;
    	}
        if (newCount >= bufsize || array.length < newCount) {
            while (newCount >= bufsize) {
                bufsize *= 2;
            }
            double[] array1 = new double[bufsize];
            System.arraycopy(array, 0, array1, 0, nelem);
            array = array1;
        }
    }
    /**
     * creates n-element array. initialised to 0
     * 
     * @param n
     *            size of array
     */
    public RealArray(int n) {
        this(n, 0.0);
    }
    /**
     * create n-element array initialised linearly. values are elem1+(i-1)*delta
     * 
     * @param n
     *            size of array
     * @param elem1
     *            starting value
     * @param delta
     *            setpsize
     */
    public RealArray(int n, double elem1, double delta) {
        if (!checkSize(n))
            return;
        array = new double[n];
        bufsize = n;
        double ff = elem1;
        for (int i = 0; i < n; i++) {
            array[i] = ff;
            ff += delta;
        }
    }
    /**
     * create Array initialized to constant value. all elements of the array are
     * set to a given value
     * 
     * @param n
     *            size of array
     * @param elem1
     *            value to set
     */
    public RealArray(int n, double elem1) {
        if (!checkSize(n))
            return;
        array = new double[n];
        bufsize = n;
        for (int i = 0; i < n; i++) {
            array[i] = elem1;
        }
    }
    /**
     * create Array from part of java array. use first n elements of array.
     * 
     * @param n
     *            number of elements to use
     * @param arr
     *            array to read from
     * @throws EuclidRuntimeException
     *             n larger than arraysize
     */
    public RealArray(int n, double[] arr) throws EuclidRuntimeException {
        if (!checkSize(n))
            throw new EuclidRuntimeException("Cannot have negative array length");
        if (n > arr.length) {
            throw new EuclidRuntimeException("Array size too small");
        }
        array = new double[n];
        bufsize = n;
        System.arraycopy(arr, 0, array, 0, n);
    }
    /**
     * create Array from java array.
     * 
     * @param arr
     *            array to read from
     */
    public RealArray(double[] arr) {
        setElements(arr);
    }
    /**
     * create from IntArray.
     * 
     * @param ia
     *            IntArray to copy from
     */
    public RealArray(IntArray ia) {
        if (!checkSize(ia.size()))
            return;
        array = new double[nelem];
        bufsize = nelem;
        for (int i = 0; i < nelem; i++) {
            array[i] = (new Double(ia.elementAt(i))).doubleValue();
        }
    }
    /**
     * create from subarray of another Array.
     * 
     * @param m
     *            array to slice
     * @param low
     *            inclusive start index of array
     * @param high
     *            inclusive end index of array
     * @throws EuclidRuntimeException
     *             low > high or negative indices or outside size of m
     */
    public RealArray(RealArray m, int low, int high) throws EuclidRuntimeException {
        if (low < 0 || low > high || high >= m.size()) {
            throw new EuclidRuntimeException("index out of range " + low + EC.S_SLASH + high);
        }
        nelem = high - low + 1;
        checkSize(nelem);
        array = new double[nelem];
        bufsize = nelem;
        System.arraycopy(m.array, low, array, 0, nelem);
    }
    /**
     * create mixed sliced array. use another IntArray to subscript this one
     * where I(this) = I(ref) subscripted by I(sub); Result has dimension of
     * I(sub). caller is responsible for making sure elements of sub are unique
     * 
     * @param ref
     *            matrix to slice
     * @param sub
     *            subscripts.
     * @throws EuclidRuntimeException
     *             if any of I(sub) lies outside 0...refmax-1
     */
    public RealArray(RealArray ref, IntArray sub) throws EuclidRuntimeException {
        this(sub.size());
        for (int i = 0; i < sub.size(); i++) {
            int j = sub.elementAt(i);
            if (j < 0 || j >= ref.size()) {
                throw new EuclidRuntimeException("index out of range " + j);
            }
            this.setElementAt(i, ref.elementAt(j));
        }
    }
    /** gets range distributed about a midpoint.
     * 
     * array is from n steps from mid-halfrange to mid to mid+halfrange
     * @param mid midpoint of range
     * @param nsteps odd number of steps
     * @param halfrange
     * @return array
     */
    public static RealArray getSymmetricalArray(
        double mid, int nsteps, double halfrange) {
        if (nsteps < 3 || nsteps % 2 != 1) {
            throw new EuclidRuntimeException(
                "Number of steps must be positive odd number; was: "+nsteps);
        }
        int nhalfsteps = (nsteps - 1) / 2;
        double step = halfrange / nhalfsteps;
        RealArray realArray = new RealArray(nsteps, mid - halfrange, step); 
        return realArray;
    }
    
    /**
     * clone.
     * 
     * @return the clone
     */
    public Object clone() {
        RealArray temp = new RealArray(nelem);
        temp.nelem = nelem;
        temp.maxelem = maxelem;
        System.arraycopy(array, 0, temp.array, 0, nelem);
        temp.bufsize = nelem;
        return (Object) temp;
    }
    /**
     * copy constructor.
     * 
     * @param m
     *            array to copy
     */
    public RealArray(RealArray m) {
        this.shallowCopy(m);
        System.arraycopy(m.array, 0, array, 0, nelem);
    }
    /**
     * Create customized array. create a given 'shape' of array for data
     * filtering An intended use is with RealArray.arrayFilter(). The shapes
     * (before scaling by maxval) are:
     * <UL>
     * <LI>"TRIANGLE"; 1/nn, 2/nn, ... 1 ... 2/nn, 1/nn; nelem is set to 2*nn -
     * 1
     * <LI>"ZIGZAG"; 1/nn, 2/nn, ... 1 ... 1/nn, 0, -1/nn, -2/nn, -1, ...
     * -1/nn,; nelem is set to 4*nn - 1
     * </UL>
     * step is maxval / nn
     * 
     * @param nn
     *            number of elements
     * @param shape
     *            TRIANGLE or ZIGZAG
     * @param maxval
     *            used to compute step
     */
    public RealArray(int nn, String shape, double maxval) {
        if (shape.toUpperCase().equals("TRIANGLE")) {
            nelem = nn * 2 - 1;
            if (!checkSize(nelem))
                return;
            array = new double[nelem];
            double delta = maxval / ((double) nn);
            for (int i = 0; i < nn; i++) {
                array[i] = (i + 1) * delta;
                array[nelem - i - 1] = array[i];
            }
        } else if (shape.toUpperCase().equals("ZIGZAG")) {
            nelem = nn * 4 - 1;
            if (!checkSize(nelem))
                return;
            array = new double[nelem];
            double delta = maxval / ((double) nn);
            for (int i = 0; i < nn; i++) {
                array[i] = (i + 1) * delta;
                array[2 * nn - i - 2] = array[i];
                array[2 * nn + i] = -array[i];
                array[nelem - i - 1] = -array[i];
            }
            array[2 * nn - 1] = 0.0;
        }
    }
    /**
     * construct from an array of Strings. must represent doubles
     * 
     * @param strings
     *            values as Strings
     * @exception EuclidRuntimeException
     *                a string could not be interpreted as doubles
     */
    public RealArray(String[] strings) throws EuclidRuntimeException {
        this(strings.length);
        for (int i = 0; i < strings.length; i++) {
        	try {
        		String db = strings[i];
				array[i] = (db.trim().equals("") || db.equals("NaN")) ? Double.NaN : Real.parseDouble(db);
        	} catch (Exception e) {
        		throw new EuclidRuntimeException("Bad array element at ("+i+") :"+strings[i]+":", e);
        	}
        }
    }
    
    /** interprets list of Strings as numbers.
     * 
     * @param stringList
     * @return null if stringList is null, zero length or has non-numbers
     */
    public static RealArray createRealArray(List<String> stringList) {
    	RealArray realArray = null;
    	if (stringList != null && stringList.size() > 0) {
	    	try {
	    		realArray = new RealArray(stringList.toArray(new String[0]));
	    	} catch (EuclidRuntimeException ere) {
	    		// bad strings
	    	}
    	}
    	return realArray;
    }
    
    /**
     * create from a space-separated string of doubles.
     * 
     * @param string
     *            of form "1.1 -3.2 0.56E-04 2..."
     * @exception NumberFormatException
     *                a substring could not be interpreted as double
     */
    public RealArray(String string) throws NumberFormatException {
        this(string.split(S_WHITEREGEX));
    }
    /** create from lilst of Doubles.
     * 
     * @param yCoords
     */
    public RealArray(List<Double> values) {
    	this();
    	if (values == null) {
    		throw new RuntimeException("null array");
    	}
    	array = new double[values.size()];
    	nelem = 0;
    	for (Double d : values) {
    		array[nelem++] = d;
    	}
	}
    
	/**
     * set output format. doesn't yet do anything!
     * 
     * @param f
     */
    public void setFormat(DecimalFormat f) {
        format = f;
    }
    /**
     * get output format.
     * 
     * @return format
     */
    public DecimalFormat getFormat() {
        return format;
    }
    /**
     * replace all values of NaN with given value. use with care as there is
     * probably something wrong
     * 
     * @param d
     *            default value
     */
    public void replaceNaN(double d) {
        for (int i = 0; i < nelem; i++) {
            if (Double.isNaN(array[i]))
                array[i] = d;
        }
    }
    /**
     * contracts internal array to be of same length as number of elements.
     * should be used if the array will be used elsewhere with a fixed length.
     * called by getArray()
     */
    private void contractArray() {
        double[] array1 = new double[nelem];
        System.arraycopy(array, 0, array1, 0, nelem);
        array = array1;
    }
    /**
     * shallowCopy.
     * 
     * @param m
     *            array to copy
     */
    void shallowCopy(RealArray m) {
        nelem = m.nelem;
        bufsize = m.bufsize;
        maxelem = m.maxelem;
        array = m.array;
    }
    /**
     * creates a filter based on Gaussian and derivatives. Scaled so that
     * approximately 2.5 sigma is included (that is value at edge is ca 0.01 of
     * centre
     * 
     * @param halfWidth
     * @param function
     * @return array
     */
    public static RealArray getFilter(int halfWidth, Filter function) {
        if (!function.equals(Filter.GAUSSIAN)
            && !function.equals(Filter.GAUSSIAN_FIRST_DERIVATIVE)
            && !function.equals(Filter.GAUSSIAN_SECOND_DERIVATIVE))
            return null;
        if (halfWidth < 1)
            halfWidth = 1;
        double xar[] = new double[2 * halfWidth + 1];
        double limit = 7.0; // ymin ca 0.01
        double sum = 0;
        double x = 0.0;
        double y = 1.0;
        // double dHalf = Math.sqrt(0.693);
        double dHalf = limit * 0.693 * 0.693 / (double) halfWidth;
        for (int i = 0; i <= halfWidth; i++) {
            if (function.equals(Filter.GAUSSIAN)) {
                y = Math.exp(-x * x);
            }
            if (function.equals(Filter.GAUSSIAN_FIRST_DERIVATIVE)) {
                y = -2 * x * Math.exp(-x * x);
            }
            if (function.equals(Filter.GAUSSIAN_SECOND_DERIVATIVE)) {
                y = (4 * (x * x) - 2.0) * Math.exp(-x * x);
            }
            xar[halfWidth + i] = (function
                    .equals(Filter.GAUSSIAN_FIRST_DERIVATIVE)) ? -y : y;
            xar[halfWidth - i] = y;
            sum += (i == 0) ? y : 2 * y;
            x += dHalf;
        }
        // normalise for Gaussian (only = the others are meaningless)
        if (function.equals(Filter.GAUSSIAN)) {
            for (int i = 0; i < 2 * halfWidth + 1; i++) {
                xar[i] /= sum;
            }
        }
        RealArray r = new RealArray(xar);
        return r;
    }
    /** creates a normal distribution about a mean. 
     * returns N*(exp((x-mean)^2/(2*sigma^2))
     * where N = 1/sigma*Math.sqrt(2*PI)
     * 
     * The distribution is scaled to unit area over -INF to +INF
     * so a smaller selection will not be precisely normalised
     * however -5 sigma to + 5 sigma should be fine
     * 
     * the x range is nsteps from maen-range to mean+range. This should be
     * prepared using 
     * RealArray xvalues = RealArray.getSymmetricalArray(mean, nsteps, halfrange);
     * RealArray normalDist = xvalues.getNormalDistribution(sigma);
     * nsteps should be an odd positive integer
     * halfrange is the range either side of the mean
     * 
     * @param sigma standard deviation
     * @return array
     */
    public RealArray getNormalDistribution(double sigma) {
        int nsteps = this.size();
        double norm = 1.0/(sigma*Math.sqrt(2*Math.PI));
        double scale = 1.0/(2*sigma*sigma);
        RealArray normal = new RealArray(this.size());
        double[] array = this.getArray();
        double mean = (array[0] + array[nsteps-1])/2.0;
        for (int i = 0; i < nsteps; i++) {
            double delta = array[i] - mean;
            normal.array[i] = norm * Math.exp(-delta * delta * scale);
        }
        return normal;
    }
    
    /** gets a variate from a distribution.
     * 'this' is normally a regularly spaced RealArray x (e.g. 
     * with values x[i] = x0, x0+dx, x0+2*dx, etc.
     * 
     * distribution is a set of frequencies of occurrence of the values
     * x[0], x[1]..., i.e. f[0], f[1]...
     * 
     * The method uses a cumulative dictribution with a uniformaly
     * distributed variable (e.g. math.random() to read off the interpolated
     * value of x.
     * 
     * the cumulativeSum is normally used as a cache, e.g.
     * RealArray x = new RealArray(11, 20., 1.) // gives 20, 21 ...30
     * RealArray freq = new RealArray(new double[] {23., 3., 45....)
     * RealArray cumulativeSum = new RealArray();
     * 
     * for (int i = 0, i < npoints; i++) {
     *   double random = x.getRandomVariate(freq, sumulativeSum);
     * }
     * 
     * the size of this and freq must be identical
     * @param distribution 
     * @param cumulativeDistribution initially clea, then used as cache
     * @return a random variate
     */
    public double getRandomVariate(
            RealArray distribution, RealArray cumulativeDistribution) {
        if (cumulativeDistribution.size() == 0) {
            RealArray cumul = distribution.cumulativeSum();
            cumulativeDistribution.setElements(cumul.getArray());
        }
        double[] cArray = cumulativeDistribution.getArray();
        double range = cArray[cArray.length-1] - cArray[0];
        double probe = cArray[0] + Math.random()* range;
        return lineSearch(probe, cumulativeDistribution);
    }
    /** binary search on monotonic increasing distribution.
     * 'this' must be regular array of values (x0, x0+dx, x0+2dx...)
     * @param probe
     * @param distribution
     * @return interpolated point in this
     */
    public double lineSearch(double probe, RealArray distribution) {
        if (this.size() <= 1) {
            throw new EuclidRuntimeException("unfilled arrays in line search");
        }
        if (this.size() != distribution.size()) {
            throw new EuclidRuntimeException("unequal arrays in line search");
        }
        double[] distArray = distribution.getArray();
        // binary chop
        int top = distArray.length-1;
        int bottom = 0;
        boolean change = true;
        while (change) {
            if (top - bottom <= 1) {
                break;
            }
            change = false;
            int mid = (top + bottom) / 2;
            if (distArray[mid] < probe) {
                bottom = mid;
                change = true;
            } else if (distArray[mid] > probe) {
                top = mid;
                change = true;
            }
        }
        double ratio = 
            (probe - distArray[bottom]) /
            (distArray[top] - distArray[bottom]);
        double step = array[1] - array[0];
        return this.array[bottom] + step * ratio;
    }
    /** get element by index.
     * 
     * @param elem the index
     * @exception ArrayIndexOutOfBoundsException
     *                elem >= size of <TT>this</TT>
     * @return element value
     */
    public double elementAt(int elem) throws ArrayIndexOutOfBoundsException {
        return array[elem];
    }
    
    /** get element by index.
     * 
     * @param elem the index
     * @exception ArrayIndexOutOfBoundsException
     *                elem >= size of <TT>this</TT>
     * @return element value
     */
    public double get(int elem) throws ArrayIndexOutOfBoundsException {
        return array[elem];
    }
    /**
     * get actual number of elements.
     * 
     * @return number of elements
     */
    public int size() {
        return nelem;
    }
    /**
     * get java array. always adjusted to be same length as element count
     * 
     * @return the array
     */
    public double[] getArray() {
        if (nelem != array.length) {
            contractArray();
        }
        return array;
    }
    /**
     * clear all elements of array. sets value to 0.0
     */
    public void clearArray() {
        for (int i = 0; i < size(); i++) {
            array[i] = 0.0;
        }
    }
    /**
     * get java array in reverse order.
     * 
     * @return array
     */
    public double[] getReverseArray() {
        int count = size();
        double[] temp = new double[count];
        for (int i = 0; i < size(); i++) {
            temp[i] = this.array[--count];
        }
        return temp;
    }
    private void checkConformable(RealArray m) throws EuclidRuntimeException {
        if (nelem != m.nelem) {
            throw new EuclidRuntimeException();
        }
    }
    /**
     * are two arrays equal.
     * 
     * @param f
     *            array to compare
     * @return true if arrays are of same size and this(i) = f(i)
     */
    public boolean isEqualTo(RealArray f) {
        return equals(f, Real.getEpsilon());
    }
    /**
     * are two arrays equal.
     * 
     * @param f
     *            array to compare
     * @param epsilon
     *            tolerance
     * @return true if arrays are of same size and Real.isEqual(array[i],
     *         f.array[i], epsilon)
     */
    public boolean equals(RealArray f, double epsilon) {
        boolean equal = false;
        try {
            checkConformable(f);
            equal = true;
            for (int i = 0; i < nelem; i++) {
                if (!Real.isEqual(array[i], f.array[i], epsilon)) {
                    equal = false;
                    break;
                }
            }
        } catch (Exception e) {
            equal = false;
        }
        return equal;
    }
    /**
     * adds arrays. does not modify this
     * 
     * @param f
     *            array to add
     * @exception EuclidRuntimeException
     *                f is different size from <TT>this</TT>
     * @return new array as this + f
     */
    public RealArray plus(RealArray f) throws EuclidRuntimeException {
        checkConformable(f);
        RealArray m = (RealArray) this.clone();
        for (int i = 0; i < nelem; i++) {
            m.array[i] = f.array[i] + array[i];
        }
        return m;
    }
    /**
     * adds arrays. modifies this += f
     * 
     * @param f
     *            array to add
     * @exception EuclidRuntimeException
     *                f is different size from <TT>this</TT>
     */
    public void plusEquals(RealArray f) throws EuclidRuntimeException {
        checkConformable(f);
        for (int i = nelem - 1; i >= 0; --i) {
            array[i] += f.array[i];
        }
    }
    /**
     * subtracts arrays. does not modify this
     * 
     * @param f
     *            array to substract
     * @exception EuclidRuntimeException
     *                f is different size from <TT>this</TT>
     * @return new array as this - f
     */
    public RealArray subtract(RealArray f) throws EuclidRuntimeException {
        checkConformable(f);
        RealArray m = (RealArray) this.clone();
        for (int i = 0; i < nelem; i++) {
            m.array[i] = array[i] - f.array[i];
        }
        return m;
    }
    /**
     * array subtraction. modifies this -= f
     * 
     * @param f
     *            array to subtract
     * @exception EuclidRuntimeException
     *                f is different size from <TT>this</TT>
     */
    public void subtractEquals(RealArray f) throws EuclidRuntimeException {
        checkConformable(f);
        for (int i = nelem - 1; i >= 0; --i) {
            array[i] -= f.array[i];
        }
    }
    
    /**
     * elementwise array division. does not modify THIS
     * 
     *   dividedArray[i] = this[i] / ra[i]
     *      if divide by zero, new element is Infinity

     * 
     *  {4,6,8,12} / {2, 0, 2, 4} gives {2, NaN,4,3}
     *  
     * @param ra array to divide by
     * @exception EuclidRuntimeException ra is different size from <TT>this</TT>
     * 
     */
    public RealArray divideBy(RealArray ra) throws EuclidRuntimeException {
        checkConformable(ra);
        RealArray dividedArray = new RealArray(this.size());
        for (int i = nelem - 1; i >= 0; --i) {
        	try {
        		dividedArray.array[i] = this.array[i] / ra.array[i];
        	} catch (ArithmeticException ae) {
        		dividedArray.array[i] = Double.NaN;
        	}
        }
        return dividedArray;
    }
    
    
    /**
     * change the sign of all elements. MODIFIES this
     */
    public void negative() {
        for (int i = 0; i < size(); i++) {
            array[i] = -array[i];
        }
    }
    /**
     * add a scalar to all elements. creates new array; does NOT modify 'this';
     * for subtraction use negative scalar
     * 
     * @param f
     *            to add
     * @return new array
     */
    public RealArray addScalar(double f) {
        RealArray m = (RealArray) this.clone();
        for (int i = 0; i < nelem; i++) {
            m.array[i] += f;
        }
        return m;
    }
    /**
     * array multiplication by a scalar. creates new array; does NOT modify
     * 'this'
     * 
     * @param f
     *            multiplier
     * @return the new array
     */
    public RealArray multiplyBy(double f) {
        RealArray m = (RealArray) this.clone();
        for (int i = 0; i < nelem; i++) {
            m.array[i] *= f;
        }
        return m;
    }
    /**
     * add a scalar to each element. creates new array; does NOT modify
     * 'this'
     * 
     * @param f multiplier
     * @return the new array
     */
    public RealArray plus(double f) {
        RealArray m = (RealArray) this.clone();
        for (int i = 0; i < nelem; i++) {
            m.array[i] += f;
        }
        return m;
    }
    
    /**
     * set element value.
     * 
     * @param elem
     *            index
     * @param f
     *            value
     * @exception ArrayIndexOutOfBoundsException
     *                elem >= size of <TT>this</TT>
     */
    public void setElementAt(int elem, double f)
            throws ArrayIndexOutOfBoundsException {
        array[elem] = f;
    }
    /**
     * get array slice. creates new array; does not modify this
     * 
     * @param start
     *            index inclusive
     * @param end
     *            index inclusive
     * @return new array
     */
    public RealArray getSubArray(int start, int end) {
        int nel = end - start + 1;
        RealArray f = new RealArray(nel, 0);
        System.arraycopy(array, start, f.array, 0, nel);
        return f;
    }
    /**
     * set array slice. copy whole array into the array.
     * 
     * @param start
     *            index in this
     * @param a
     *            array to copy
     * @throws ArrayIndexOutOfBoundsException
     *             start < 0 or start+a.length > this.size()
     */
    public void setElements(int start, double[] a) {
        if (start < 0 || start + a.length > nelem) {
            throw new ArrayIndexOutOfBoundsException("was "+start+" in 0-"+a.length);
        }
        System.arraycopy(a, 0, this.array, start, a.length);
    }
    /** set array.
     * clears any existing arrays
     * @param a
     *            array to copy
     */
    public void setElements(double[] a) {
        nelem = a.length;
        array = new double[nelem];
        bufsize = nelem;
        System.arraycopy(a, 0, array, 0, nelem);
    }
    /**
     * is the array filled with zeros.
     * 
     * @return true if this(i) = 0
     */
    public boolean isClear() {
        for (int i = 0; i < nelem; i++) {
            if (!Real.isZero(array[i], Real.getEpsilon()))
                return false;
        }
        return true;
    }
    /**
     * initialise array to given value. this(i) = f
     * 
     * @param f
     *            value to set
     */
    public void setAllElements(double f) {
        Real.initArray(nelem, array, f);
    }
    /**
     * sum all elements.
     * 
     * @return sigma(this(i))
     */
    public double sumAllElements() {
        double sum = 0.0;
        for (int i = 0; i < nelem; i++) {
            sum += array[i];
        }
        return sum;
    }
    
    /**
     * sum all product of elements.
     * 
     * @return sigma(this(i)*y(i)
     */
    public double sumProductOfAllElements(RealArray yarr) {
    	if (yarr == null || yarr.size() != nelem) {
    		throw new RuntimeException("arrays must be of equal length");
    	}
        double sum = 0.0;
        for (int i = 0; i < nelem; i++) {
            sum += array[i]*yarr.elementAt(i);
        }
        return sum;
    }
    
    /**
     * sum of all absolute element values.
     * 
     * @return sigma(abs(this(i)))
     */
    public double absSumAllElements() {
        double sum = 0.0;
        for (int i = 0; i < nelem; i++) {
            sum += Math.abs(array[i]);
        }
        return sum;
    }
    /**
     * inner product. dotProduct(this)
     * 
     * @return sigma(this(i)**2)
     */
    public double innerProduct() {
        return this.dotProduct(this);
    }
    /**
     * dot product of two arrays. sigma(this(i)*(f(i));
     * 
     * @param f
     *            array to multiply
     * @exception EuclidRuntimeException
     *                f is different size from <TT>this</TT>
     * @return dot product
     */
    public double dotProduct(RealArray f) throws EuclidRuntimeException {
        checkConformable(f);
        double sum = 0.0;
        for (int i = 0; i < nelem; i++) {
            sum += array[i] * f.array[i];
        }
        return sum;
    }
    /**
     * Euclidean length of vector
     * 
     * @return length
     */
    public double euclideanLength() {
        return Math.sqrt(innerProduct());
    }
    /**
     * root mean square sqrt(sigma(x(i)**2)/n)
     * 
     * @exception EuclidRuntimeException
     *                must have at least 1 point
     * @return rms
     */
    public double rms() throws EuclidRuntimeException {
        if (nelem == 0) {
            throw new EuclidRuntimeException("must have at least one point");
        }
        return euclideanLength() / Math.sqrt((double) nelem);
    }
    /**
     * get unit vector
     * 
     * @exception EuclidRuntimeException
     *                elements of <TT>this</TT> are all zero
     * @return the unit vector
     */
    public RealArray unitVector() throws EuclidRuntimeException {
        double l = euclideanLength();
        if (Real.isZero(l, Real.getEpsilon())) {
            throw new EuclidRuntimeException("zero length vector");
        }
        double scale = 1.0 / l;
        RealArray f = new RealArray(nelem);
        f = this.multiplyBy(scale);
        return f;
    }
    /**
     * cumulative sum of array. create new array as elem[i] = sum(k = 0 to i)
     * f[k] does not modify 'this'
     * 
     * @return each element is cumulative sum to that point
     */
    public RealArray cumulativeSum() {
        RealArray temp = new RealArray(nelem);
        double sum = 0.0;
        for (int i = 0; i < nelem; i++) {
            sum += array[i];
            temp.array[i] = sum;
        }
        return temp;
    }
    /**
     * apply filter. convolute array with another array. This is 1-D image
     * processing. If <TT>filter</TT> has <= 1 element, return <TT>this</TT>
     * unchanged. <TT>filter</TT> should have an odd number of elements. The
     * filter can be created with a IntArray constructor filter is moved along
     * stepwise
     * </P>
     * 
     * @param filter
     *            to apply normally smaller than this
     * @return filtered array
     */
    public RealArray applyFilter(RealArray filter) {
        if (nelem == 0 || filter == null || filter.nelem <= 1) {
            return this;
        }
        int nfilter = filter.size();
        int midfilter = (nfilter - 1) / 2;
        RealArray temp = new RealArray(nelem);
        double wt = 0;
        double sum = 0;
        for (int j = 0; j < midfilter; j++) {
            // get weight
            wt = 0.0;
            sum = 0.0;
            int l = 0;
            for (int k = midfilter - j; k < nfilter; k++) {
                wt += Math.abs(filter.array[k]);
                sum += filter.array[k] * this.array[l++];
            }
            temp.array[j] = sum / wt;
        }
        wt = filter.absSumAllElements();
        for (int j = midfilter; j < nelem - midfilter; j++) {
            sum = 0.0;
            int l = j - midfilter;
            for (int k = 0; k < nfilter; k++) {
                sum += filter.array[k] * this.array[l++];
            }
            temp.array[j] = sum / wt;
        }
        for (int j = nelem - midfilter; j < nelem; j++) {
            // get weight
            wt = 0.0;
            sum = 0.0;
            int l = j - midfilter;
            for (int k = 0; k < midfilter + nelem - j; k++) {
                wt += Math.abs(filter.array[k]);
                sum += filter.array[k] * this.array[l++];
            }
            temp.array[j] = sum / wt;
        }
        return temp;
    }
    /**
     * trims array to lie within limit.
     * 
     * if flag == BELOW values below limit are set to limit. if flag == ABOVE
     * values above limit are set to limit. by repeated use of trim() values can
     * be constrained to lie within or outside a window does not modify this.
     * 
     * @param flag
     *            BELOW or ABOVE
     * @param limit
     *            value to constrain
     * @return new array
     */
    public RealArray trim(Trim flag, double limit) {
        RealArray temp = new RealArray(nelem);
        for (int i = 0; i < nelem; i++) {
            double v = array[i];
            if ((flag == Trim.BELOW && v < limit)
                    || (flag == Trim.ABOVE && v > limit))
                v = limit;
            temp.array[i] = v;
        }
        return temp;
    }
    /**
     * index of largest element.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             array is zero length
     * @return index
     */
    public int indexOfLargestElement() {
        if (nelem == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int index = -1;
        double value = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < nelem; i++) {
            if (array[i] > value) {
                value = array[i];
                index = i;
            }
        }
        return index;
    }
    /**
     * index of smallest element.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             array is zero length
     * @return index
     */
    public int indexOfSmallestElement() {
        if (nelem == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int index = -1;
        double value = Double.POSITIVE_INFINITY;
        for (int i = 0; i < nelem; i++) {
            if (array[i] < value) {
                value = array[i];
                index = i;
            }
        }
        return index;
    }
    /**
     * value of largest element.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             array is zero length
     * @return value
     */
    public double largestElement() throws ArrayIndexOutOfBoundsException {
        return array[indexOfLargestElement()];
    }
    /**
     * value of largest element. synonym for largestElement();
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             array is zero length
     * @return value
     */
    public double getMax() throws ArrayIndexOutOfBoundsException {
    	int idx = indexOfLargestElement();
        return idx == -1 ? null : array[idx];
    }
    /**
     * value of smallest element.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             array is zero length
     * @return index
     */
    public double smallestElement() throws ArrayIndexOutOfBoundsException {
    	int idx = indexOfSmallestElement();
        return idx == -1 ? null : array[idx];
    }
    /**
     * value of smallest element. synonym for smallestElement();
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             array is zero length
     * @return value
     */
    public double getMin() throws ArrayIndexOutOfBoundsException {
        return array[indexOfSmallestElement()];
    }
    /**
     * calculates mean as sum of all elements divided by number of elements
     * @return null if zero-length array.
     */
    public Double getMean() {
    	Double mean = null;
    	if (nelem > 0) {
    		mean = 0.0;
    		for (int i = 0; i < nelem; i++) {
    			mean += array[i];
    		}
    		mean /= (double) nelem;
    	}
    	return mean;
    }
    
    /**
     * range of array.
     * 
     * @throws ArrayIndexOutOfBoundsException
     *             array is zero length
     * @return (minValue, maxValue)
     */
    public RealRange getRange() throws ArrayIndexOutOfBoundsException {
        if (nelem == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        RealRange r = new RealRange();
        for (int i = 0; i < nelem; i++) {
            r.add(array[i]);
        }
        return r;
    }
    /**
     * delete element and close up. modifies this.
     * 
     * @param elem
     *            to delete
     * @throws ArrayIndexOutOfBoundsException
     *             elem out of range
     */
    public void deleteElement(int elem) throws ArrayIndexOutOfBoundsException {
        if (elem < 0 || elem >= nelem) {
            throw new ArrayIndexOutOfBoundsException();
        }
        nelem--;
        if (bufsize > nelem * 2) {
            bufsize /= 2;
        }
        double[] temp = new double[bufsize];
        System.arraycopy(array, 0, temp, 0, elem);
        System.arraycopy(array, elem + 1, temp, elem, nelem - elem);
        array = temp;
    }
    /**
     * delete elements and close up. modifies this.
     * 
     * @param low
     *            lowest index inclusive
     * @param high
     *            highest index inclusive
     * @throws ArrayIndexOutOfBoundsException
     *             low or high out of range or low > high
     */
    public void deleteElements(int low, int high)
            throws ArrayIndexOutOfBoundsException {
        if (low < 0 || low > high || high >= nelem) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int ndeleted = high - low + 1;
        double[] temp = new double[nelem - ndeleted];
        System.arraycopy(array, 0, temp, 0, low);
        System.arraycopy(array, high + 1, temp, low, nelem - low - ndeleted);
        array = temp;
        nelem -= ndeleted;
        bufsize = nelem;
        double[] array = new double[nelem];
        System.arraycopy(temp, 0, array, 0, nelem);
    }
    /**
     * insert element and expand. modifies this.
     * 
     * @param elem
     *            index of element to insert
     * @param f
     *            value of element
     * @throws ArrayIndexOutOfBoundsException
     *             elem out of range
     */
    public void insertElementAt(int elem, double f)
            throws ArrayIndexOutOfBoundsException {
        if (elem < 0 || elem > nelem) {
            throw new ArrayIndexOutOfBoundsException();
        }
        double[] array1 = new double[nelem + 1];
        System.arraycopy(array, 0, array1, 0, elem);
        array1[elem] = f;
        System.arraycopy(array, elem, array1, elem + 1, nelem - elem);
        nelem++;
        array = array1;
    }
    /**
     * insert an array and expand. modifies this.
     * 
     * @param elem
     *            index of element to insert
     * @param f
     *            value of element
     * @throws ArrayIndexOutOfBoundsException
     *             elem out of range
     */
    public void insertArray(int elem, RealArray f)
            throws ArrayIndexOutOfBoundsException {
        int n = f.size();
        if (elem < 0 || elem >= nelem || n < 1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        nelem += n;
        double[] array1 = new double[nelem];
        System.arraycopy(array, 0, array1, 0, elem);
        System.arraycopy(f.getArray(), 0, array1, elem, n);
        System.arraycopy(array, elem, array1, n + elem, nelem - elem - n);
        array = array1;
    }
    /**
     * append element. modifies this.
     * 
     * @param f
     *            element to append
     */
    public void addElement(double f) {
        makeSpace(nelem + 1);
        array[nelem++] = f;
    }
    /**
     * append elements. modifies this.
     * 
     * @param f
     *            elements to append
     */
    public void addArray(RealArray f) {
        LOG.trace("COPY0 "+array.length+"//"+nelem+"/"+f.nelem+"/"+array.length);
        makeSpace(nelem + f.nelem);
        LOG.trace("COPY1 "+array.length+"//"+nelem+"/"+f.nelem+"/"+array.length);
        System.arraycopy(f.array, 0, array, nelem, f.nelem);
        nelem += f.nelem;
    }
    /**
     * get reordered Array. reorder by index in IntSet new(i) = this(idx(i))
     * does NOT modify array
     * 
     * @param idx
     *            array of indexes
     * @exception EuclidRuntimeException
     *                an element of idx is outside range of <TT>this</TT>
     * @return array
     * 
     */
    public RealArray getReorderedArray(IntSet idx) throws EuclidRuntimeException {
        RealArray temp = new RealArray(nelem);
        for (int i = 0; i < nelem; i++) {
            int index = idx.elementAt(i);
            if (index > nelem) {
                throw new EuclidRuntimeException("index out of range " + index);
            }
            temp.array[i] = array[index];
        }
        return temp;
    }

    /** normalize to given number of places
     * replaces each element by (nint(elem*10^ndec))/10^ndec
     * @param dd array 
     * @param ndec number of places
     */
    public static void round(double[] dd, int ndec) {
    	for (int i = 0; i < dd.length; i++) {
    		dd[i] = Real.normalize(dd[i], ndec);
    	}
    }
    /**
     * get elements within a range.
     * 
     * @param r
     *            within which element values must lie
     * @return indexes of conforming elements
     */
    public IntSet inRange(RealRange r) {
        int n = size();
        IntSet temp = new IntSet();
        for (int i = 0; i < n; i++) {
            if (r.isValid() && r.includes(array[i])) {
                temp.addElement(i);
            }
        }
        return temp;
    }
    /**
     * get elements outside a range.
     * 
     * @param r
     *            outside which element values must lie
     * @return indexes of conforming elements
     */
    public IntSet outOfRange(RealRange r) {
        int n = size();
        IntSet temp = new IntSet();
        for (int i = 0; i < n; i++) {
            if (r.isValid() && !r.includes(array[i])) {
                temp.addElement(i);
            }
        }
        return temp;
    }
    /**
     * returns values as strings.
     * 
     * @return string values of elements
     */
    public String[] getStringValues() {
        String[] temp = new String[nelem];
        for (int i = 0; i < nelem; i++) {
            temp[i] = Double.toString(array[i]);
        }
        return temp;
    }
    
    public String getStringArray() {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < nelem; i++) {
            if (i > 0) {
                s.append(S_COMMA);
            }
            s.append(array[i]);
        }
        return s.toString();
    }
    
    /**
     * gets values as string.
     * within brackets and including commas
     * @return element values separated with spaces
     */
    public String toString() {
        // don't change this routine!!!
        StringBuffer s = new StringBuffer();
        s.append(S_LBRAK);
        for (int i = 0; i < nelem; i++) {
            if (i > 0) {
                s.append(S_COMMA);
            }
            s.append(array[i]);
        }
        s.append(S_RBRAK);
        return s.toString();
    }
    /**
     * some operations on float[] (static)
     */
    /**
     * delete elements (lo - > hi inclusive) in a float[] and close up; if hi >=
     * float.length hi is reset to float.length-1.
     * 
     * @param f
     * @param hi
     * @param low
     * @return new array
     */
    public static double[] deleteElements(double[] f, int low, int hi) {
        if (hi >= f.length)
            hi = f.length - 1;
        if (low < 0)
            low = 0;
        int ndel = hi - low + 1;
        if (ndel <= 0)
            return f;
        double[] temp = new double[f.length - ndel];
        System.arraycopy(f, 0, temp, 0, low);
        System.arraycopy(f, hi + 1, temp, low, f.length - hi - 1);
        return temp;
    }
    
    /** if RA is an autocorrelation array find first maximum after origin.
     * rather hairy so not generally recommended
     * runs through array till value below cutoff
     * then runs till value above cutoff
     * then aggregates values until drops below cutoff
     * @param cutoff
     * @return the maximum element (may be non-integral)
     */
    public double findFirstLocalMaximumafter(int start, double cutoff) {
    	double index = Double.NaN;
    	boolean hitmin = false;
    	boolean hitmax = false;
    	double sigyx = 0.0;
    	double sigy = 0.0;
    	for (int i = start; i < nelem; i++) {
    		double d = array[i];  
    		if (!hitmin && !hitmax) {
    			if (d < cutoff) {
    				hitmin = true;
    			}
    		} else if (hitmin && !hitmax) {
    			if (d > cutoff) {
    				hitmax = true;
    				hitmin = false;
    			}
    		} else if (hitmax) {
    			if (d < cutoff) {
    				hitmin = true;
    				break;
    			}
    			sigyx += d*i;
    			sigy += d;
    		}
    	}
    	if (hitmin && hitmax) {
    		index = sigyx / sigy;
    	}
    	return index;
    }
    

    /** find baseline.
     * experimental approach to finding baseline and adjusting to it.
     * Finds peak of distribution
     * read source code if you need to use this
     * @throws JumboException (many)
     * @return base offset
     */
 	public double getBaseLine() {
 		double baseOffset;
 		Univariate univariate = new Univariate(this);
 		int binCount = 100;
 		univariate.setBinCount(binCount);
 		double step = this.getRange().getRange()/(double)binCount;
 		double min = this.getMin();
 		int[] bins = univariate.getHistogramCounts();
 		int ibMax = -1;
 		int binMax = -1;
 		for (int i = 0; i < bins.length; i++) {
 			if (bins[i] > binMax) {
 				binMax = bins[i];
 				ibMax = i;
 			}
 		}
 		int iMin = -1;
 		for (int i = ibMax; i >= 0; i--) {
 			if (bins[i] < binMax/2) {
 				iMin = i;
 				break;
 			}
 		}
 		iMin = (iMin > 0) ? iMin : 0;
 		int iMax = -1;
 		for (int i = ibMax; i < binCount; i++) {
 			if (bins[i] < binMax/2) {
 				iMax = i;
 				break;
 			}
 		}
 		iMax = (iMax > 0) ? iMax : binCount-1;
 		if (iMin == ibMax || ibMax == binCount-1) {
 			baseOffset = 0.0;
 		} else {
 			double weight = 0.0;
 			double sum = 0.0;
 			for (int i = iMin; i <= iMax; i++) {
 				double w = (double) bins[i];
 				weight += w;
 				sum += w*i;
 			}
 			double deltaB = sum/weight;
 			baseOffset = step*(deltaB)+min;
 		}
 		return baseOffset;
 	}
 	
    /** round to decimal places.
     * 
     * @param places
     * @return this
     */
    public RealArray format(int places) {
    	for (int i = 0; i < nelem; i++) {
    		array[i] = Util.format(array[i], places);
    	}
    	return this;
    }
 	
    /**
     * copy a double[] into a new one
     */
    /*--
     private static double[] copy(double[] f) {
     double temp[] = new double[f.length];
     System.arraycopy(f, 0, temp, 0, f.length);
     return temp;
     }
     --*/
    /**
     * quick sort - modified from p96 - 97 (Hansen - C++ answer book)
     * 
     * Scalar sort refers to sorting IntArray and RealArray (and similar
     * classes) where the objects themeselves are sorted.
     * 
     * Index sort refers to sorting indexes (held as IntSet's) to the object and
     * getting the sorted object(s) with reorderBy(IntSet idx);
     * 
     */
    private void xfswap(double[] x, int a, int b) {
        double tmp = x[a];
        x[a] = x[b];
        x[b] = tmp;
    }
    // scalar sort routines (internal)
    private static final int CUTOFF = 16;
    private void inssort(int left, int right) {
        int k;
        for (int i = left + 1; i <= right; i++) {
            double v = array[i];
            int j;
            for (j = i, k = j - 1; j > 0 && array[k] > v; j--, k--) {
                array[j] = array[k];
            }
            array[j] = v;
        }
    }
    private int partition(int left, int right) {
        int mid = (left + right) / 2;
        if (array[left] > array[mid])
            xfswap(array, left, mid);
        if (array[left] > array[right])
            xfswap(array, left, right);
        if (array[mid] > array[right])
            xfswap(array, mid, right);
        int j = right - 1;
        xfswap(array, mid, j);
        int i = left;
        double v = array[j];
        do {
            do {
                i++;
            } while (array[i] < v);
            do {
                j--;
            } while (array[j] > v);
            xfswap(array, i, j);
        } while (i < j);
        xfswap(array, j, i);
        xfswap(array, i, right - 1);
        return i;
    }
    private void iqsort(int left, int right) {
        while (right - left > CUTOFF) {
            int i = partition(left, right);
            if (i - left > right - i) {
                iqsort(i + 1, right);
                right = i - 1;
            } else {
                iqsort(left, i - 1);
                left = i + 1;
            }
        }
    }
    /**
     * sorts array into ascending order. MODIFIES this
     */
    public RealArray sortAscending() {
        if (nelem <= 0)
            return null;
        iqsort(0, nelem - 1);
        inssort(0, nelem - 1);
        return this;
    }
    /**
     * sorts array into descending order. MODIFIES this
     */
    public void sortDescending() {
        sortAscending();
        reverse();
    }
    /**
     * puts array into reverse order. MODIFIES this
     */
    public void reverse() {
        int i = 0, j = nelem - 1;
        while (i < j) {
            xfswap(array, i, j);
            i++;
            j--;
        }
    }

    /** create new reordered array.
     * 'this' is not modified
     * 
     * @param intSet
     * @return
     */
    public RealArray createReorderedArray(IntSet intSet) {
    	RealArray ra = null;
    	if (intSet != null && intSet.size() == this.size()) {
    		ra = new RealArray(intSet.size());
    		for (int i = 0; i < intSet.size(); i++) {
    			ra.setElementAt(i, this.get(intSet.elementAt(i)));
    		}
    	}
    	return ra;
    }
    
    private static final int XXCUTOFF = 16;
    /**
     * get indexes of ascending sorted array. this array NOT MODIFIED
     * 
     * @return indexes idx so that element(idx(0)) is lowest
     */
    public IntSet indexSortAscending() {
        if (nelem <= 0) {
            return new IntSet();
        }
        IntSet idx = new IntSet(nelem);
        IntArray iarray = new IntArray(idx.getElements());
        xxiqsort(iarray, array, 0, nelem - 1);
        xxinssort(iarray, array, 0, nelem - 1);
        try {
            idx = new IntSet(iarray.getArray());
        } catch (Exception e) {
            throw new EuclidRuntimeException(e.toString());
        }
        return idx;
    }
    /**
     * get indexes of descending sorted array. this array NOT MODIFIED
     * 
     * @return indexes idx so that element(idx(0)) is highest
     */
    public IntSet indexSortDescending() {
        IntSet idx;
        idx = indexSortAscending();
        int[] temp = new IntArray(idx.getElements()).getReverseArray();
        try {
            idx = new IntSet(temp);
        } catch (Exception e) {
            throw new EuclidRuntimeException(e.toString());
        }
        return idx;
    }
    private void xxinssort(IntArray iarr, double[] pfl, int left, int right) {
        int j, k;
        for (int i = left + 1; i <= right; i++) {
            int v = iarr.elementAt(i);
            for (j = i, k = j - 1; j > 0 && pfl[iarr.elementAt(k)] > pfl[v]; j--, k--) {
                iarr.setElementAt(j, iarr.elementAt(k));
            }
            iarr.setElementAt(j, v);
        }
    }
    private int xxpartition(IntArray iarr, double[] pfl, int left, int right) {
        int mid = (left + right) / 2;
        if (pfl[iarr.elementAt(left)] > pfl[iarr.elementAt(mid)])
            xxfswap(iarr, left, mid);
        if (pfl[iarr.elementAt(left)] > pfl[iarr.elementAt(right)])
            xxfswap(iarr, left, right);
        if (pfl[iarr.elementAt(mid)] > pfl[iarr.elementAt(right)])
            xxfswap(iarr, mid, right);
        int j = right - 1;
        xxfswap(iarr, mid, j);
        int i = left;
        double v = pfl[iarr.elementAt(j)];
        do {
            do {
                i++;
            } while (pfl[iarr.elementAt(i)] < v);
            do {
                j--;
            } while (pfl[iarr.elementAt(j)] > v);
            xxfswap(iarr, i, j);
        } while (i < j);
        xxfswap(iarr, j, i);
        xxfswap(iarr, i, right - 1);
        return i;
    }
    private void xxiqsort(IntArray iarr, double[] pfl, int left, int right) {
        while (right - left > XXCUTOFF) {
            int i = xxpartition(iarr, pfl, left, right);
            if (i - left > right - i) {
                xxiqsort(iarr, pfl, i + 1, right);
                right = i - 1;
            } else {
                xxiqsort(iarr, pfl, left, i - 1);
                left = i + 1;
            }
        }
    }
    private void xxfswap(IntArray iarr, int a, int b) {
        int t = iarr.elementAt(a);
        iarr.setElementAt(a, iarr.elementAt(b));
        iarr.setElementAt(b, t);
    }
    
    /**
	 * @param monotonicity
	 * @return
	 */
	public Monotonicity getMonotonicity() {
		Monotonicity monotonicity = null;
		if (size() > 1) {
			double last = get(0);
			for (int i = 1; i < size(); i++) {
				double current = get(i);
				// equality with last
				Monotonicity m = null;
				if (current < last) {
					m = Monotonicity.DECREASING;
				} else if (current > last) {
					m = Monotonicity.INCREASING;
				}
				// compare with last monotonocity
				if (m != null) {
					if (monotonicity == null) {
						monotonicity = m;
					} else if (monotonicity != m) {
						monotonicity = null;
						break;
					}
				}
				last = current;
			}
		}
		return monotonicity;
	}
	/**
     * checks RealArray is not null and is of given size.
     * 
     * @param array
     *            to check
     * @param size
     *            required size
     * @throws EuclidRuntimeException
     *             if null or wrong size
     */
    public static void check(RealArray array, int size) throws EuclidRuntimeException {
        if (array == null) {
            throw new EuclidRuntimeException("null array");
        } else if (array.size() != size) {
            throw new EuclidRuntimeException("array size required (" + size
                    + ") found " + array.size());
        }
    }

	/**
	 * parse string as realArray.
	 * 
	 * @param s
	 * @param delimiterRegex
	 * @return true if can be parsed.
	 */
	public static boolean isFloatArray(String s, String delimiterRegex) {
		boolean couldBeFloatArray = true;
		String[] ss = s.split(delimiterRegex);
		try {
			new RealArray(ss);
		} catch (Exception e) {
			couldBeFloatArray = false;
		}
		return couldBeFloatArray;
	}
	

	/** creates scaled array so it runs spans new Range.
	 * e.g.
	 * array = {1,2,3}
	 * thisx0, thisx1 = {0.5, 2.5}
	 * targetx0, targetx1 = {100, 200}
	 * would create {125., 175., 225.}
	 * 
	 * allows for x0 > x1
	 * @param thisx0 low map point of this
	 * @param thisx1 high map point of this
	 * @param targetx0 low map point of target
	 * @param targetx1 high map point of target
	 * @return
	 */
	public RealArray createScaledArrayToRange(double thisX0, double thisX1, double targetX0, double targetX1) {
		RealArray newArray = null;
		if (this.nelem > 1) {
			Double scale = null;
			try {
				scale = (targetX0 - targetX1) / (thisX0 - thisX1); 
			} catch (Exception e) {
				//
			}
			if (scale != null && !Double.isNaN(scale) && !Double.isInfinite(scale) && nelem > 0) {
				newArray = new RealArray(this);
				newArray = newArray.addScalar(-thisX0);
				newArray = newArray.multiplyBy(scale);
				newArray = newArray.addScalar(targetX0);
			}
		}
		return newArray;
	}


	/** creates scaled array so it runs spans new Range.
	 * e.g.
	 * array = {1,2,3}
	 * newRange = {5, 9}
	 * would create (5, 7, 9}
	 * 
	 * allows for x0 > x1
	 * @param newRange
	 * @return
	 */
	public RealArray createScaledArrayToRange(double x0, double x1) {
		RealArray newArray = null;
		if (this.nelem > 1) {
			double thisX0 = this.get(0);
			double thisX1 = this.get(nelem - 1);
			Double scale = null;
			try {
				scale = (x0 - x1) / (thisX0 - thisX1); 
			} catch (Exception e) {
				//
			}
			if (scale != null && !Double.isNaN(scale) && !Double.isInfinite(scale) && nelem > 0) {
				newArray = new RealArray(this);
				newArray = newArray.addScalar(-thisX0);
				newArray = newArray.multiplyBy(scale);
				newArray = newArray.addScalar(x0);
			}
		}
		return newArray;
	}
	/** casts an integer array to RealArray
	 * 
	 * @param integers
	 * @return
	 */
	public static RealArray createRealArray(int[] integers) {
		RealArray realArray = null;
		if (integers != null) {
			realArray = new RealArray(integers.length);
			for (int i = 0; i < integers.length; i++) {
				realArray.array[i] = (double) integers[i];
			}
		}
		return realArray;
	}
	
	/** casts an integer array to RealArray
	 * 
	 * @param integers
	 * @return
	 */
	public static RealArray createRealArray(IntArray intArray) {
		int[] ints = (intArray == null) ? null : intArray.array;
		return RealArray.createRealArray(ints);
	}

	/** calculate differences between elements i and i+1
	 * 
	 * @return array of n-1 differences elem[i+1] - elem[i].
	 */
	
	public RealArray calculateDifferences() {
		RealArray differenceArray = new RealArray(nelem - 1);
		for (int i = 0; i < nelem - 1; i++) {
			double diff = array[i + 1] - array[i];
			differenceArray.setElementAt(i, diff);
		}
		return differenceArray;
	}
	
	/** converts RealArray into IntArray.
	 * 
	 * casts to (int).
	 * 
	 * @return new IntArray
	 */
	@Deprecated // use createRoundIntArray
	public IntArray createIntArray() {
		IntArray intArray = new IntArray(nelem);
		for (int i = 0; i < nelem; i++) {
			intArray.array[i] = (int) array[i];
		}
		return intArray;
	}
	public Iterator<Double> iterator() {
		return (array == null || array.length < nelem) ? null : new DoubleIterator(array, nelem);
	}
	
	/** assumes all elements are equally spaced with "x" separation of 1.0.
	 * 
	 * a linear interpolation approach. calculates new values of cells. x may be gt or lt 0 and may
	 * have integer arts (e.g. 1.23)
	 * 
	 * // FIXME not yet working for large negative shifts
	 * 
	 * @param delta distance to shift by
	 * @return shifted array
	 */
	public RealArray shiftOriginToRight(double delta) {
		RealArray newArray = new RealArray(nelem);
		int offset = 0;
		//shift d to 0,1 interval and compute integer offset
		while (delta < 0.0) {
			offset++;
			delta++;
		}
		while (delta >= 1.0) {
			offset--;
			delta--;
		}
		LOG.trace(offset+" "+delta);
		for (int i = 0; i < nelem; i++) {
			int index0 = i - offset +1;
			int index1 = index0 - 1;
			double previousValue = getValueCorrectedForEnds(index1);
			double thisValue = getValueCorrectedForEnds(index0);
			LOG.trace(i+" "+previousValue+" "+thisValue);
			double newValue = previousValue * (1.0-delta) + thisValue * delta;
			newArray.setElementAt(i, newValue);
		}
		LOG.trace("newArray: "+newArray.format(2)+"\n");
		
		return newArray;
	}
	
	/** interpolate array into new array of different length.
	 * 
	 * 
	 * @param newElem new array length
	 * @return inerpolated array
	 * 
	 * 
	 */
	@Deprecated // not yet tested and better to use imgscalr for images
	public RealArray scaleAndInterpolate(int newNelem) {
		if (newNelem == nelem) {
			return new RealArray(this);
		}
		RealArray newArray = new RealArray(newNelem);
		double old2NewScale = (double) nelem / (double) newNelem;
		double pixelScale = 1.0;
//		double old2NewScale = 1.0;
		for (int iold = 0; iold < nelem; iold++) {
			LOG.trace("===="+iold+"====");
			double value = this.elementAt(iold) * pixelScale;
			// partial chunk at LH end
			if (old2NewScale < 1) {
				scaleFewToMany(newArray, old2NewScale, iold, value);
			} else {
				scaleManyToFew(newNelem, newArray, old2NewScale, iold, value);
			}
		}
		LOG.trace("newArray: "+newArray.format(2)+"\n");
		
		return newArray;
	}
	private void scaleManyToFew(int newNelem, RealArray newArray,
			double old2NewScale, int iold, double value) {
		double lowerNew = (double) iold / old2NewScale;
		double upperNew = (double) (iold + 1) / old2NewScale;
		int intLowerNew = (int) lowerNew;
		int intUpperNew = (int) upperNew;
		if (intUpperNew == intLowerNew) intUpperNew++;
		lowerNew = intLowerNew * old2NewScale;
		upperNew = intUpperNew * old2NewScale;
		LOG.trace(lowerNew+" "+upperNew);
		if (lowerNew <= iold && upperNew >= iold+1) {
			newArray.array[intLowerNew] += value;
			LOG.trace("complete "+intLowerNew+" "+value);
		} else {
			double delta = 0;
			int index = -1;
			if (lowerNew > iold) {
				delta = lowerNew - iold;
				index = intLowerNew;
			} else {
				delta = upperNew - iold;
				index = intUpperNew;
			}
			newArray.array[index] += value * delta;
			if (index < newNelem -1) newArray.array[index + 1] += value * (1.0 - delta);
			LOG.trace("split "+index+" "+delta+" "+value);
		}
	}
	private void scaleFewToMany(RealArray newArray, double old2NewScale, int iold,
			double value) {
		double lowerNew = ((double) iold) / old2NewScale;
		double upperNew = ((double) (iold + 1)) / old2NewScale;
		int intLowerNew = (int) lowerNew;
		int intUpperNew = (int) upperNew;
		if (intLowerNew + 1 < upperNew) {
			double delta = value * (intLowerNew + 1 - lowerNew);
			LOG.trace("deltalow: "+Util.format(delta, 2));
			newArray.array[intLowerNew] += delta;
		}
// these are complete chunks of value
		for (int middle = intLowerNew + 1; middle < intUpperNew; middle++) {
			LOG.trace("deltamid: "+Util.format(value, 2));
			newArray.array[middle] += value;
		}
		if (intUpperNew < upperNew) {
			double delta = value * (upperNew - intUpperNew);
			LOG.trace("deltahi: "+Util.format(delta, 2));
			newArray.array[intUpperNew] += delta;
		}
	}
	private double getValueCorrectedForEnds(int index) {
		double value = 0.0;
		if (index < 0) {
			value = this.elementAt(0);
		} else if (index >= nelem) {
			value = this.elementAt(nelem - 1);
		} else {
			value = this.elementAt(index);
		}
		return value;
	}
	
	
	/** assumes all elements are equally spaced with "x" separation of 1.0.
	 * 
	 * a linear interpolation approach. calculates new values of cells. x may be gt or lt 0 and may
	 * have integer arts (e.g. 1.23)
	 * 
	 * Not fully tested for all cases.
	 * 
	 * @param delta distance to shift by
	 * @return shifted array
	 */
	public RealArray shiftOriginToRightOld(double delta) {
		RealArray array0 = new RealArray(this);
		RealArray array1 = new RealArray(this);
		int offset = 0;
		//shift d to 0,1 interval and compute integer offset
		while (delta < 0) {
			offset++;
			delta++;
		}
		while (delta >= 1) {
			offset--;
			delta--;
		}
		double scale = Math.abs(delta);
		array0.shiftArrayRight(offset);
		array1.shiftArrayRight(offset);
		array0 = array0.multiplyBy(scale);
		array1 = array1.multiplyBy(1.0 - scale);
		if (delta >= 0) {
			array0.shiftArrayRight(-1);
		} else if (delta < 0) {
			array1.shiftArrayRight(1);
		}
		RealArray newArray = array0.plus(array1);
		
		return newArray;
	}
	
	/** shift array and fill with end elements.
	 * 
	 * @param nplaces if positive sift right; if negative shift left
	 */
	public void shiftArrayRight(int nplaces) {
		if (nplaces >= 0) {
			for (int i = 0; i < nplaces; i++) {
				this.insertElementAt(0, this.get(0));
				this.deleteElement(nelem - 1);
			}
		} else {
			nplaces = 0 - nplaces;
			for (int i = 0; i < nplaces; i++) {
				this.insertElementAt(nelem, this.get(nelem - 1));
				this.deleteElement(0);
			}
			
		}
	}
	
	/** gets last element in array
	 * @return element ; Double.NaN for zero length array
	 */
	public double getLast() {
		return nelem == 0 ? Double.NaN : this.get(nelem - 1);
	}
	
	/**
	 *  create a set of double differences between neighbouring elements.
	 *  Probably most useful when array is already sorted.
	 *  
	 *  Example:
	 *  realArray = (1.2 2.3 3.4 4.6 5.7)
	 *  set will be
	 *  [1.1 x 3, 1.2]
	 *  
	 * @return
	 */
	public Multiset<Double> createDoubleDifferenceMultiset(int nplaces) {
		Multiset<Double> deltaSet = HashMultiset.create();
		RealArray deltaArray = calculateDifferences();
		deltaArray.format(nplaces);
		for (int i = 0; i < deltaArray.size(); i++) {
			deltaSet.add(new Double(deltaArray.elementAt(i)));
		}
		return deltaSet;
	}

	/** list of diffs sorted by countDescending.
	 * 
	 * new RealArray("1 2 3 4 5  8 9 10  13 14 15 16");
	 * gives [1.0 x 9, 3.0 x 2]
	 * 
	 * @param nplaces format delta Array
	 * 
	 * @return 

	 */
	public List<Multiset.Entry<Double>> createSortedDoubleDifferenceList(int nplaces) {
		Multiset<Double> diffSet = createDoubleDifferenceMultiset(nplaces);
		List<Multiset.Entry<Double>> diffList = MultisetUtil.createListSortedByCount(diffSet);
		return diffList;
	}

	/** commonest difference.
	 * 
	 * new RealArray("1 2 3 4 5  8 9 10  13 14 15 16");
	 * gives 1.0
	 * 
	 * @param nplaces format delta Array
	 * 
	 * @return 

	 */
	public Double getCommonestDifference(int nplaces) {
		List<Multiset.Entry<Double>> diffList = createSortedDoubleDifferenceList(nplaces);
		return diffList == null || diffList.size() == 0 ? null : diffList.get(0).getElement();
	}

	/** second commonest difference.
	 * 
	 * new RealArray("1 2 3 4 5  8 9 10  13 14 15 16");
	 * gives 3.0
	 * 
	 * @param nplaces format delta Array
	 * 
	 * @return 

	 */
	public Double getSecondCommonestDifference(int nplaces) {
		List<Multiset.Entry<Double>> diffList = createSortedDoubleDifferenceList(nplaces);
		return diffList == null || diffList.size() < 2 ? null : diffList.get(1).getElement();
	}


	/** create a set of all values as formatted Doubles.
	 * 
	 * @param nplaces number of places to format
	 * @return the Multiset of values
	 */
	
	public Multiset<Double> createDoubleMultiset(int nplaces) {
		Multiset<Double> set = HashMultiset.create();
		RealArray array = new RealArray(this);
		array.format(nplaces);
		for (int i = 0; i < array.size(); i++) {
			set.add(new Double(array.elementAt(i)));
		}
		return set;
	}

	
	/**
	 *  create a set of integer differences between neighbouring elements.
	 *  Probably most useful when array is already sorted.
	 *  
	 *  Example:
	 *  realArray = (1.2 2.3 3.4 4.6 5.7)
	 *  set will be
	 *  [1 x 4]
	 *  
	 * @return
	 */
	public Multiset<Integer> createIntegerDifferenceMultiset() {
		Multiset<Integer> deltaSet = HashMultiset.create();
		if (this != null) {
			IntArray deltaArray = calculateDifferences().createIntArray();
			for (int i = 0; i < deltaArray.size(); i++) {
				deltaSet.add(new Integer((int)deltaArray.elementAt(i)));
			}
		}
		return deltaSet;
	}
	/** does the array contain one or more Doubel.NaN.
	 * 
	 * @return
	 */
	public boolean hasNaN() {
		for (int i = 0; i < nelem; i++) {
			if (Double.isNaN(array[i])) {
				return true;
			}
		}
		return false;
	}

	/** caluclate new array with absolute values of elements.
	 * 
	 * @return absolute values
	 */
	public RealArray getAbsoluteValues() {
		RealArray absArray = new RealArray(this);
		for (int i = 0; i < nelem; i++) {
			if (absArray.array[i] < 0) {
				absArray.array[i] = - absArray.array[i];
			}
		}
		return absArray;
	}
	
	/** creates List of ArithmeticProgression from sorted monotonic reals with chunks of equally spaced elements.
	 *  
	 *  note delta(i) is the actual difference between each successive pair
	 *  of reals.
	 *  meanDelta is the mean of delta(i)
	 *  epsilon is the allowed tolerance between any delta(i) and meanDelta
	 *  fails if any abs(delta(i) - meanDelta) > epsilon and returns null
	 *  
	 *  attempts to find chunks with spacing (nearly) equal 
	 *  
	 *  must have commonest difference at least count 2 
	 *  
	 * @param realArray must have >= 4 elements
	 * @param epsilon allowed tolerance
	 * @return
	 */
	public List<RealArray> createArithmeticProgressionList(double epsilon) {
		RealArray currentRealArray = null;
		List<RealArray> realArrayList = new ArrayList<RealArray>();
		int size = this.size();
		if (size >= 4) {
			int nplaces = (int) Math.ceil(-1.0 * Math.log10(epsilon));
			List<Multiset.Entry<Double>> diffList = this.createSortedDoubleDifferenceList(nplaces);
			Multiset.Entry<Double> diff0 = diffList.get(0);
//			Multiset.Entry<Double> diff1 = diffList.get(1);
			if (diff0.getCount() >= 2) {
				double commonestDiff = diff0.getElement();
				for (int i = 1; i < size; i++) {
					double delta = this.get(i) - this.get(i - 1);
					if (Real.isEqual(commonestDiff, delta, epsilon)) {
						if (currentRealArray == null) {
							currentRealArray = new RealArray();
							currentRealArray.addElement(this.get(i - 1));
							realArrayList.add(currentRealArray);
						}
						currentRealArray.addElement(this.get(i));
					} else {
						currentRealArray = null;
					}
				}
			}
		}
		return realArrayList;
	}
	
	/** iterates through array eliminating "almost equal" neighbours.
	 * If two neighbouring elements are within epsilon, then one is removed
	 * 
	 * 12.1, 13.0, 13.1, 14.0, 14.1, 17 epsilon=0.2
	 * gives
	 * 12.1 13.0, 14.0, 17
	 * 
	 * probably not very efficient. Also if array consists of lost of nearly equal
	 * elements the results may be unclear.
	 * 
	 * this realArray will normally be sorted
	 * 
	 * @param epsilon
	 * @return new array
	 */
	 
	 
	public RealArray compressNearNeighbours(double epsilon) {
		RealArray newArray = new RealArray();
		int nn = 0;
		if (this.nelem > 0) {
			double last = array[nn++];
			newArray.addElement(last);
			for (int i = 1; i < nelem; i++) {
				double xx = array[i];
				if (!Real.isEqual(xx, last, epsilon)) {
					newArray.addElement(xx);
				}
				last = xx;
			}
		}
		return newArray;
	}
	
	public IntArray createRoundIntArray() {
		IntArray intArray = new IntArray();
		for (int i = 0; i < nelem; i++) {
			intArray.addElement((int) Math.round(array[i]));
		}
		return intArray;
	}
	
	/** computes the time lag product of the array.
	 * primarily for detecting whether there is a regular repeat 
	 * NOT YET TESTED
	 */
	@Deprecated //"NOT YET TESTED"
	private double createSimpleAutoConvolution0(int offset, int range) {
		RealArray correlation = new RealArray(Math.min(nelem, offset+range));
		double sum = 0.0;
		int min = Math.min(nelem - offset, range);
		int n = 0;
//		LOG.debug(offset+"/"+min);
		for (int i = offset; i <  min; i++) {
			sum += array[i] * array [i + offset];
			n++;
		}
		double corr = sum / n;
//		LOG.debug("off " + offset + " corr "+corr);
		return corr;
	}

	/**
	 * 
	 * @param maxoffset
	 * @param range
	 * @return
	 */
	public RealArray createSimpleAutoConvolution(int maxoffset,  int range) {
		maxoffset = Math.min(maxoffset, this.nelem);
		RealArray corrArray = new RealArray(maxoffset);
		for (int offset = 0; offset < maxoffset; offset++) {
			double corr = createSimpleAutoConvolution0(offset, range);
			corrArray.setElementAt(offset, corr);
		}
		return corrArray;
	}
	
	/**flatten values outside range onto range
	 * 
	 * @param int2
	 */
	public void flattenOutside(IntRange range) {
		for (int i = 0; i < nelem; i++) {
			this.setElementAt(i, Math.min(range.getMax(), Math.max(range.getMin(), this.elementAt(i))));
		}
	}
	
	/** gets indexes of elements within range 
	 * if array is [1., 20., 23., 3., 40.]
	 * and range is (15., 25.)
	 * returns [1, 2]
	 * 
	 * @param intRange
	 * @return indexes
	 */
	
	public IntArray getIndexesWithinRange(RealRange range) {
		IntArray intArray = new IntArray();
		for (int i = 0; i < nelem; i++) {
			if (range.includes(array[i])) {
				intArray.addElement(i);
			}
		}
		return intArray;
	}
	
	/** creates array of index ranges for "peaks" in array
	 * ex: 1.,3.3,4.1,5.,1.,1.,99.,3.,32.,11. and limit of 1.9
	 * (2,4),(7,10)
	 * @param limit
	 * @return
	 */
	public List<IntRange> createMaskArray(double limit) {
		List<IntRange> rangeArray = new ArrayList<IntRange>();
		IntRange range = null;
		for (int i = 0; i < nelem; i++) {
			double value = array[i];
			if (value >= limit) {
				if (range == null) {
					range = new IntRange(i, i);
					rangeArray.add(range);
				 }
				range.setMax(i);
			} else {
				range = null;
			}
		}
		return rangeArray;
		
	}
	


}
class DoubleIterator implements Iterator<Double> {

	private int counter;
	private double[] array;
	private double nelem;
	
	public DoubleIterator(double[] array, double nelem) {
		this.array = array;
		this.nelem = nelem;
		counter = -1;
	}
	
	public boolean hasNext() {
		return counter < nelem - 1;
	}

	public Double next() {
		if (!hasNext()) return null;
		return (Double) array[++counter];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	
}

