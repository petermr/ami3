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

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Real supports various utilities for real numbers Use Double where you want a
 * first-class Java object
 * 
 * @author (C) P. Murray-Rust, 1996
 */
public abstract class Real implements EuclidConstants {
    final static Logger LOG = Logger.getLogger(Real.class);
    /** standard for equality of numbers */
    static double epsx = 0.0000000001;
    /**
     * get current version of epsilon.
     * 
     * @return maximum difference between numbers
     */
    public static double getEpsilon() {
        return epsx;
    }
    /**
     * set current version of epsilon.
     * 
     * @param epsilon
     *            will be used in any implicit comparison until reset
     * 
     */
    public static void setEpsilon(double epsilon) {
        epsx = epsilon;
    }

    /** truncate to given number of decimals.
     * forms nint(d * 10^ndec)/10^ndec
     * @param d to truncate
     * @param ndec
     * @return
     */
    public static double normalize(double d, int ndec) {
    	int dd = 1;
    	for (int i = 0; i < ndec; i++) {
    		dd *= 10;
    	}
    	return ((double) Math.round(d * (double)dd)) / (double) dd;
    }
    /**
     * are two numbers equal within epsx.
     * 
     * @param a
     *            number
     * @param b
     *            number
     * @return true if a equals b within epsilon
     * 
     */
    public static boolean isEqual(Double a, Double b) {
        return a != null && b != null && Math.abs(a - b) < epsx;
    }
    
    /**
     * is a number zero within epsx
     * 
     * @param a
     *            number
     * @return true if a is zero within epsilon
     * 
     * @deprecated use epsilon method
     */
    public static boolean isZero(Double a) {
        return a != null && Real.isZero(a, epsx);
    }
    
    /**
     * are all members of an array equal within epsilon.
     * 
     * @param n
     *            length of array
     * @param a
     *            first array
     * @param b
     *            first array
     * @param epsilon
     *            difference
     * @return true is all arrays are of equals lengths and members are equal
     *         within epsilon
     * 
     * @deprecated omit n
     */
    public static boolean isEqual(int n, double[] a, double[] b, double epsilon) {
    	if (a == null || b == null ) return false;
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            if (!Real.isEqual(a[i], b[i], epsilon))
                return false;
        }
        return true;
    }
    
    /**
     * are all members of an array equal within epsilon.
     * 
     * @param a first array
     * @param b first array
     * @param epsilon difference
     * @return true is all arrays are of equals lengths and members are equal
     *         within epsilon
     * 
     */
    public static boolean isEqual(double[] a, double[] b, double epsilon) {
        if (a == null || b == null || a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!Real.isEqual(a[i], b[i], epsilon))
                return false;
        }
        return true;
    }
    /**
     * are all members of an array equal within epsx
     * 
     * @param n length of array
     * @param a first array
     * @param b first array
     * @return true is all arrays are of equals lengths and members are equal
     *         within epsilon
     * @deprecated use epsilon method
     */
    public static boolean isEqual(int n, double[] a, double b[]) {
        return isEqual(n, a, b, epsx);
    }
    /**
     * are two numbers equal within epsilon
     * 
     * @param a
     *            number
     * @param b
     *            number
     * @param epsilon
     *            difference
     * @return true if a equals b within epsilon
     */
    public static boolean isEqual(Double a, Double b, double epsilon) {
        return a != null && b != null && Math.abs(a - b) < epsilon;
    }
    /**
     * is a number zero within epsilon
     * 
     * @param a
     *            number
     * 
     * @param epsilon
     *            difference
     * 
     * @return true if a is zero within epsilon
     * 
     */

    /**
     * A regular expression match a number pattern.
     */
    public static final String SCIENTIFIC_PARSE = "(?:[+-]?(?:(?:\\d*(?:\\.?\\d+)?)|(?:\\d+(?:\\.?\\d*)?))(?:[EeDdGgHh][+-]?\\d+[dDfF]?)?)";
                                             //  sign? |                     number                   |      exponent?      | precision?
    /**
     * A compiled Pattern object which matches a number pattern.
     */
    public static final Pattern SCIENTIFIC_PATTERN = Pattern.compile(SCIENTIFIC_PARSE);

    /**
     * Parse a string to double value, similar to Double.parseDouble function, but
     * also try to parse against FORTRAN number, e.g. 12.3D-05.
     *
     * <p>If the return value is Double.NaN, a RuntimeException is thrown.
     * This should not happen anyway.
     *
     * @author (C) Weerapong Phadungsukanan, 2009
     * @param db String of number value
     * @return double value of the given String
     * @throws NullPointerException if db is null.
     * @throws NumberFormatException if db is not a number.
     */
    public static double parseDouble(String db) {
        double d = Double.NaN;
        db = db.trim();
        // Try to parse string using java routine first. If it does not match
        // the string could be number in FORTRAN format, [DdGgHh] as exponential
        // notation. So we replace the first exponential notation by E and then
        // try to parse again with java routine. The two steps are necessary and
        // cannot be removed otherwise the number such as 12.0d will not be parsed.
        try {
            d = Double.parseDouble(db);
        } catch (NumberFormatException nfe) {
            d = Double.parseDouble(db.replaceFirst("[DdGgHh]", "E"));
        }
        if (d == Double.NaN) throw new RuntimeException("Cannot parse {" + db + "} as double and cannot throw NumberFormatException. This is a program bug.");
        return d;
    }

    public static boolean isZero(double a, double epsilon) {
        return Math.abs(a) < epsilon;
    }
    /**
     * is a less than epsx less than b
     * 
     * @param a
     *            number
     * 
     * @param b
     *            number
     * 
     * @return true if a < b within epsx
     * 
     */
    public static boolean isLessThan(double a, double b) {
        return ((b - a) > epsx);
    }
    /**
     * is a more than epsx greater than b
     * 
     * @param a
     *            number
     * 
     * @param b
     *            number
     * 
     * @return true if a > b within epsx
     * 
     */
    public static boolean isGreaterThan(double a, double b) {
        return ((a - b) > epsx);
    }
    /**
     * set an array to zero
     * 
     * @param nelem
     *            length of array
     * 
     * @param arr
     *            array
     * 
     */
    public static void zeroArray(int nelem, double[] arr) {
        for (int i = 0; i < nelem; i++) {
            arr[i] = 0.0;
        }
    }
    /**
     * set an array to given value
     * 
     * @param nelem
     *            length of array
     * 
     * @param arr
     *            array
     * 
     * @param f
     *            the value
     * 
     */
    public static void initArray(int nelem, double[] arr, double f) {
        for (int i = 0; i < nelem; i++) {
            arr[i] = f;
        }
    }
    /**
     * print a double[]
     * 
     * @param a
     *            array
     * 
     */
    public static void printArray(double[] a) {
        for (int i = 0; i < a.length; i++) {
            LOG.info(a[i] + S_SPACE);
        }
        LOG.info("");
    }
    
}
