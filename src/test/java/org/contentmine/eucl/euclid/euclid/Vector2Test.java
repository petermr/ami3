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

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.eucl.euclid.test.DoubleTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * tests for Vector2.
 * 
 * @author pmr
 * 
 */
public class Vector2Test {

	Vector2 v0;

	Vector2 v1;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		v0 = new Vector2(new Real2(3., 4.));
		v1 = new Vector2(1., 2.);
	}

	/**
	 * equality test. true if both args not null and equal within epsilon
	 * 
	 * @param msg
	 *            message
	 * @param test
	 * @param expected
	 * @param epsilon
	 */
	public static void assertEquals(String msg, Vector2 test, Vector2 expected,
			double epsilon) {
		Assert.assertNotNull("test should not be null (" + msg + EC.S_RBRAK, test);
		Assert.assertNotNull("expected should not be null (" + msg + EC.S_RBRAK,
				expected);
		DoubleTestBase.assertEquals(msg, test.getXY(), expected.getXY(),
				epsilon);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector2.Vector2(Real2)'
	 */
	@Test
	public void testVector2Real2() {
		Assert.assertEquals("vector2 real2", 3., v0.getX(),EPS);
		Assert.assertEquals("vector2 real2", 4., v0.getY(),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector2.Vector2(double, double)'
	 */
	@Test
	public void testVector2DoubleDouble() {
		Assert.assertEquals("vector2 real2", 1., v1.getX(),EPS);
		Assert.assertEquals("vector2 real2", 2., v1.getY(),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Vector2.getAngleMadeWith(Vector2)'
	 */
	@Test
	public void testGetAngleMadeWith() {
		Vector2 v1 = new Vector2(Math.sqrt(3.) / 2., 1. / 2.);
		Vector2 v2 = new Vector2(1. / 2., Math.sqrt(3.) / 2.);
		Angle a = v1.getAngleMadeWith(v2);
		Assert.assertEquals("angle", -Math.PI / 6., a.getRadian(), EPS);
		a = v2.getAngleMadeWith(v1);
		Assert.assertEquals("angle", Math.PI / 6., a.getRadian(), EPS);
	}

}
