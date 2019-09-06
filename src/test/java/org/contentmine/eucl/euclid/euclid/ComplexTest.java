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

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Complex;
import org.contentmine.eucl.euclid.Polar;
import org.contentmine.eucl.euclid.Real2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Complex.
 * 
 * @author pmr
 * 
 */
public class ComplexTest {

	private static final double EPS = 1E-14;

	Complex c0;

	Complex c1;

	Complex c2;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		c0 = new Complex();
		c1 = new Complex(1, 0);
		c2 = new Complex(1, 2);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.negative()'
	 */
	@Test
	public void testNegative() {
		c2.negative();
		Assert.assertEquals("negative", -1., c2.getReal(),EPS);
		Assert.assertEquals("negative", -2., c2.getImaginary(),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.toString()'
	 */
	@Test
	public void testToString() {
		String s = c2.toString();
		Assert.assertEquals("to string", "1.0,2.0", s);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.Complex()'
	 */
	@Test
	public void testComplex() {
		String s = c0.toString();
		Assert.assertEquals("to string", "0.0,0.0", s);
		Assert.assertEquals("empty ", 0., c0.getReal(),EPS);
		Assert.assertEquals("empty", 0., c0.getImaginary(),EPS);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.Complex(double)'
	 */
	@Test
	public void testComplexDouble() {
		Complex c = new Complex(3.);
		Assert.assertEquals("to string", "3.0,0.0", c.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.Complex(double, double)'
	 */
	@Test
	public void testComplexDoubleDouble() {
		Complex c = new Complex(3., 2.);
		Assert.assertEquals("to string", "3.0,2.0", c.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.Complex(Real2)'
	 */
	@Test
	public void testComplexReal2() {
		Complex c = new Complex(new Real2(3., 2.));
		Assert.assertEquals("real 2", "3.0,2.0", c.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.Complex(double, Angle)'
	 */
	@Test
	public void testComplexDoubleAngle() {
		Angle a = new Angle(60., Angle.Units.DEGREES);
		Complex c = new Complex(1., a);
		Assert.assertEquals("length angle", 1. / 2., c.getReal(), 1.0E-08);
		Assert.assertEquals("length angle", Math.sqrt(3.) / 2., c
				.getImaginary(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.Complex(Polar)'
	 */
	@Test
	public void testComplexPolar() {
		Polar p = new Polar(1., 2.);
		Complex c = new Complex(p);
		Assert.assertEquals("polar", 1., c.getReal(), 1.0E-08);
		Assert.assertEquals("polar", 2., c.getImaginary(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.Complex(Complex)'
	 */
	@Test
	public void testComplexComplex() {
		Complex c = new Complex(c2);
		Assert.assertEquals("complex", 1., c.getReal(), 1.0E-08);
		Assert.assertEquals("complex", 2., c.getImaginary(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.getReal()'
	 */
	@Test
	public void testGetReal() {
		Assert.assertEquals("real", 1., c2.getReal(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.getImaginary()'
	 */
	@Test
	public void testGetImaginary() {
		Assert.assertEquals("imaginary", 2., c2.getImaginary(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.multiply(Complex)'
	 */
	@Test
	public void testMultiply() {
		Complex c = c2.multiply(c2);
		Assert.assertEquals("multiply", -3., c.getReal(), 1.0E-08);
		Assert.assertEquals("multiply", 4., c.getImaginary(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.divideBy(Complex)'
	 */
	@Test
	public void testDivideBy() {
		Complex c = c1.divideBy(c2);
		Assert.assertEquals("divide", 0.2, c.getReal(), 1.0E-08);
		Assert.assertEquals("divide", -0.4, c.getImaginary(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.getR()'
	 */
	@Test
	public void testGetR() {
		double r = c2.getR();
		Assert.assertEquals("R", Math.sqrt(5.), r, 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.getTheta()'
	 */
	@Test
	public void testGetTheta() {
		Angle a = c2.getTheta();
		Assert.assertEquals("theta", Math.atan2(2., 1.), a.getAngle(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.getPolar()'
	 */
	@Test
	public void testGetPolar() {
		Polar p = c2.getPolar();
		Angle a = p.getTheta();
		Assert.assertEquals("R", Math.sqrt(5.), p.getR(), 1.0E-08);
		Assert.assertEquals("theta", Math.atan2(2., 1.), a.getAngle(), 1.0E-08);
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Complex.sqrt(Complex)'
	 */
	@Test
	public void testSqrt() {
		Complex c = Complex.sqrt(c2);
		Assert.assertEquals("sqrt x", 1.2720196, c.getReal(), 0.000001);
		Assert.assertEquals("sqrt y", 0.786151, c.getImaginary(), 0.000001);
	}

}
