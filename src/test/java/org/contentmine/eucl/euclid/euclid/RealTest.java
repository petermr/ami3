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

import static org.contentmine.eucl.euclid.EuclidConstants.EPS;

import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Test;

/**
 * test Real.
 * 
 * @author pmr
 * 
 */
public class RealTest {

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real.zeroArray(double, double[])'
	 */
	@Test
	public void testZeroArray() {
		double[] rr = new double[5];
		Real.zeroArray(5, rr);
		DoubleTestBase.assertEquals("double[] ", new double[] { 0.0, 0.0, 0.0,
				0.0, 0.0 }, rr, EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real.initArray(double, double[],
	 * double)'
	 */
	@Test
	public void testInitArray() {
		double[] rr = new double[5];
		Real.initArray(5, rr, 3.0);
		DoubleTestBase.assertEquals("double[] ", new double[] { 3.0, 3.0, 3.0,
				3.0, 3.0 }, rr, EPS);
	}
	
	@Test
	public void testIsRealDDD() {
		Assert.assertTrue(Real.isEqual(2.0, 1.99, 0.1));
	}

}
