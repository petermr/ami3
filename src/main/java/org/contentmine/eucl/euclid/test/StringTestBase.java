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
public class StringTestBase {

	//constants
	static String S_SLASH = "/";
	static String S_SPACE = " ";
	
	/**
	 * Asserts equality of String arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * equality if individual elements are equal or both elements are null
	 * 
	 * @param message
	 * @param a
	 *            expected array may include nulls
	 * @param b
	 *            actual array may include nulls
	 */
	public static void assertEquals(String message, String[] a, String[] b) {
		String s = testEquals(a, b);
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	/**
	 * Asserts equality of String arrays.
	 * 
	 * convenience method where test is a whitespace-separated set of tokens
	 * 
	 * @param message
	 * @param expected
	 *            expected array as space concatenated
	 * @param actual
	 *            actual array may not include nulls
	 */
	public static void assertEquals(String message, String expected, String[] actual) {
		if(expected==null){
			Assert.fail(message+"; "+"null expected String");
		}
		String[] aa = expected.split(S_SPACE);
		String s = testEquals(aa, actual);
		if (s != null) {
			Assert.fail(message + "; " + s);
		}
	}

	/**
	 * match arrays. error is a == null or b == null or a.length != b.length or
	 * a[i] != b[i] nulls match
	 * 
	 * @param a
	 * @param b
	 * @return message if errors else null
	 */
	public static String testEquals(String[] a, String[] b) {
		String s = null;
		if (a == null) {
			s = "a is null";
		} else if (b == null) {
			s = "b is null";
		} else if (a.length != b.length) {
			s = "unequal arrays: " + a.length + S_SLASH + b.length;
		} else {
			for (int i = 0; i < a.length; i++) {
				if (a[i] == null && b[i] == null) {
					// both null, match
				} else if (a[i] == null || b[i] == null || !a[i].equals(b[i])) {
					s = "unequal element (" + i + "), expected: " + a[i]
							+ " found: " + b[i];
					break;
				}
			}
		}
		return s;
	}

	/**
	 * Asserts non equality of String arrays.
	 * 
	 * checks for non-null, then equality of length, then individual elements
	 * 
	 * @param message
	 * @param expected
	 *            expected array
	 * @param actual
	 *            actual array
	 */
	public static void assertNotEquals(String message, String[] expected, String[] actual) {
		String s = testEquals(expected, actual);
		if (s == null) {
			Assert.fail(message + "; arrays are equal");
		}
	}

}
