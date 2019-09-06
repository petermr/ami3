/**
 *    Copyright 2011 Peter Murray-Rust, Nick England, David Jessop
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

package org.contentmine.eucl.euclid.test;

import org.junit.Assert;

/**
 * 
 * <p>
 * superclass for manage common methods for unit tests
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class DoubleTestBase {

	//constants
	static String S_SLASH = "/";
	static String S_RBRAK = ")";

	/**
	 * Asserts equality of double arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param expected
	 *            expected array
	 * @param actual
	 *            actual array
	 * @param eps
	 *            tolerance for agreement
	 */
	public static void assertEquals(String message, double[] expected, double[] actual,
			double eps) {
		String s = testEquals(expected, actual, eps);
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	
	
	private static boolean isEqual(double a, double b, double epsilon) {
		return Math.abs(a - b) < epsilon;
	}
	
    @SuppressWarnings("unused")
	private static boolean isEqual(double[] a, double[] b, double epsilon) {
        if (a == null || b == null || a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!isEqual(a[i], b[i], epsilon))
                return false;
        }
        return true;
    }
	
	public static void assertObjectivelyEquals(String message, double[] a,
			double[] b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if ( !isEqual(a[i], b[i],
						eps)) {
					s = "unequal element at (" + i + "), " + a[i] + " != "
							+ b[i];
					break;
				}
			}
		}
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	/**
	 * Asserts non equality of double arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param a
	 *            expected array
	 * @param b
	 *            actual array
	 * @param eps
	 *            tolerance for agreement
	 */
	public static void assertNotEquals(String message, double[] a, double[] b,
			double eps) {
		String s = testEquals(a, b, eps);
		if (s == null) {
			Assert.fail(message + "; arrays are equal");
		}
	}

	/**
	 * returns a message if arrays differ.
	 * 
	 * @param a
	 *            array to compare
	 * @param b
	 *            array to compare
	 * @param eps
	 *            tolerance
	 * @return null if arrays are equal else indicative message
	 */
	static String testEquals(double[] a, double[] b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (!isEqual(a[i], b[i], eps)) {
					s = "unequal element at (" + i + "), " + a[i] + " != "
							+ b[i];
					break;
				}
			}
		}
		return s;
	}

	/**
	 * returns a message if arrays of arrays differ.
	 * 
	 * @param a
	 *            array to compare
	 * @param b
	 *            array to compare
	 * @param eps
	 *            tolerance
	 * @return null if array are equal else indicative message
	 */
	static String testEquals(double[][] a, double[][] b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i].length != b[i].length) {
					s = "row (" + i + ") has unequal lengths: " + a[i].length
							+ S_SLASH + b[i].length;
					break;
				}
				for (int j = 0; j < a[i].length; j++) {
					if (!isEqual(a[i][j], b[i][j], eps)) {
						s = "unequal element at (" + i + ", " + j + "), ("
								+ a[i][j] + " != " + b[i][j] + S_RBRAK;
						break;
					}
				}
			}
		}
		return s;
	}

}
