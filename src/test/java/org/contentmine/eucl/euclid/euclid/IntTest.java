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

package org.contentmine.eucl.euclid.euclid;

import org.contentmine.eucl.euclid.Int;
import org.junit.Assert;
import org.junit.Test;

/**
 * test Int.
 * 
 * @author pmr
 * 
 */
public class IntTest {

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int.zeroArray(int, int[])'
	 */
	@Test
	public void testZeroArray() {
		int[] ii = new int[5];
		Int.zeroArray(5, ii);
		String s = Int.testEquals((new int[] { 0, 0, 0, 0, 0 }), ii);
		if (s != null) {
			Assert.fail("int[] " + "; " + s);
		}
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Int.initArray(int, int[], int)'
	 */
	@Test
	public void testInitArray() {
		int[] ii = new int[5];
		Int.initArray(5, ii, 3);
		String s = Int.testEquals((new int[] { 3, 3, 3, 3, 3 }), ii);
		if (s != null) {
			Assert.fail("int[] " + "; " + s);
		}
	}

}
