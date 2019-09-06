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
 * contains tests for equality, etc. but not not use Assert.
 * Can therefore be used in tests without worrying about inclusion of
 * Junit, etc.
 * @author pm286
 *
 */
public class EuclidTestUtils {

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
	public static String testEquals(double[] a, double[] b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + EC.S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (!Real.isEqual(a[i], b[i], eps)) {
					s = "unequal element at (" + i + "), " + a[i] + " != "
							+ b[i];
					break;
				}
			}
		}
		return s;
	}

	/**
	 * returns a message if arrays differ.
	 * @param msg to prepend
	 * @param a array to compare
	 * @param b array to compare
	 * @param eps tolerance
	 * @return null if arrays are equal else indicative message
	 */
	public static String testEquals(String msg, double[] a, double[] b, double eps) {
		String s = testEquals(a, b, eps);
		if (s != null) {
			s = msg+": "+s;
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
			s = "unequal arrays: " + a.length + EC.S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i].length != b[i].length) {
					s = "row (" + i + ") has unequal lengths: " + a[i].length
							+ EC.S_SLASH + b[i].length;
					break;
				}
				for (int j = 0; j < a[i].length; j++) {
					if (!Real.isEqual(a[i][j], b[i][j], eps)) {
						s = "unequal element at (" + i + ", " + j + "), ("
								+ a[i][j] + " != " + b[i][j] + EC.S_RBRAK;
						break;
					}
				}
			}
		}
		return s;
	}
	
	/**
	 * returns a message if arrays differ.
	 * @param msg to prepend
	 * @param a array to compare
	 * @param b array to compare
	 * @param eps tolerance
	 * @return null if arrays are equal else indicative message
	 */
	public static String testEquals(String msg, double[][] a, double[][] b, double eps) {
		String s = testEquals(a, b, eps);
		if (s != null) {
			s = msg+": "+s;
		}
		return s;
	}
// Real2
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
	public static String testEquals(Real2 a, Real2 b, double eps) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else {
			if (!Real.isEqual(a.x, b.x, eps) ||
				!Real.isEqual(a.y, b.y, eps)) {
				s = ""+a+" != "+b;
			}
		}
		return s;
	}

// Plane3
	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static String testEquals(String msg, Plane3 expected, Plane3 test, 
			double epsilon) {
		String s = null;
		if (test == null) {
			s = msg+": null test";
		} else if (expected == null) {
			s = msg+": null expected";
		} else {
			s = testEquals(msg, expected.getArray(), test, 
				epsilon);
		}
		return s;
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 *            array must be of length 4
	 * @param expected
	 * @param epsilon
	 */
	public static String testEquals(String msg, double[] expected, Plane3 test,
			double epsilon) {
		String s = null;
		if (expected == null) {
			s = msg+": expected should not be null";
		} else if (expected.length != 4) {
			s = msg+": expected must be of length 4; was "+expected.length;
		} else if (test == null) {
			s = msg+": test should not be null";
		} else {
			s = testEquals(msg, expected, test.getArray(), epsilon);
		}
		return s;
	}

	
}
