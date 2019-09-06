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

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.ArrayBase;
import org.contentmine.eucl.euclid.EC;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * test Real2Array
 * 
 * @author pmr
 * 
 */
public class Real2ArrayTest {

	private final static Logger LOG = Logger.getLogger(Real2ArrayTest.class);
	
	Real2Array ra0;
	Real2Array ra1;

	/**
	 * setup.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		ra0 = new Real2Array();
		ra1 = new Real2Array(new RealArray(new double[] { 1, 2, 3, 4, 5, 6 }),
				new RealArray(new double[] { 11, 12, 13, 14, 15, 16 }));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Array.Real2Array()'
	 */
	@Test
	public void testReal2Array() {
		Assert.assertEquals("empty", "()", ra0.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Array.getRange2()'
	 */
	@Test
	public void testGetRange2() {
		Real2Range real2Range = ra1.getRange2();
		Assert.assertTrue("range2", real2Range.isEqualTo(new Real2Range(
				new RealRange(1, 6), new RealRange(11, 16)), 0.001));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Array.Real2Array(RealArray,
	 * RealArray)'
	 */
	@Test
	public void testReal2ArrayRealArrayRealArray() {
		Assert.assertEquals("realArrays", EC.S_LBRAK + "(1.0,11.0)" + "(2.0,12.0)"
				+ "(3.0,13.0)" + "(4.0,14.0)" + "(5.0,15.0)" + "(6.0,16.0)"
				+ EC.S_RBRAK, ra1.toString());
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Array.getXArray()'
	 */
	@Test
	public void testGetXArray() {
		RealArray xarr = ra1.getXArray();
		Assert.assertTrue("getXArray", xarr.isEqualTo(new RealArray(
				new double[] { 1., 2., 3., 4., 5., 6. })));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Array.getYArray()'
	 */
	@Test
	public void testGetYArray() {
		RealArray yarr = ra1.getYArray();
		Assert.assertTrue("getYArray", yarr.isEqualTo(new RealArray(
				new double[] { 11., 12., 13., 14., 15., 16. })));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Array.size()'
	 */
	@Test
	public void testSize() {
		Assert.assertEquals("size", 6, ra1.size());
	}


	@Test
	public void testSumProductOfAllElements() {
		Assert.assertEquals("sum", 301., ra1.sumProductOfAllElements(), 0.001);
	}

	@Test
	public void testCreateFromPairs() {
		String s = "1,2 3,4 5,6 7,8";
		Real2Array real2Array = Real2Array.createFromPairs(s, ArrayBase.ARRAY_REGEX);
		Assert.assertEquals("size", 4, real2Array.size());
		RealArray xarr = real2Array.getXArray();
		Assert.assertTrue("getXArray", xarr.isEqualTo(new RealArray(
				new double[] { 1., 3., 5., 7. })));
		RealArray yarr = real2Array.getYArray();
		Assert.assertTrue("getYArray", yarr.isEqualTo(new RealArray(
				new double[] { 2., 4., 6., 8. })));
	}

	/**
	 * Test method for 'org.contentmine.eucl.euclid.Real2Array.elementAt(int)'
	 */
	@Test
	public void testElementAt() {
		Real2 real2 = ra1.elementAt(4);
		Assert.assertEquals("elementAt", 5., real2.getX(), EPS);
		Assert.assertEquals("elementAt", 15., real2.getY(), EPS);
	}
	@Test
	public void testCreateFromCoords() {
		String coords = "((112.559,238.695)(121.217,238.695)(129.215,238.695)(139.877,238.695)(146.543,238.695)(149.543,238.695)(153.533,238.695)(160.295,238.695)(167.621,238.695)(176.279,238.695)(182.876,238.695)(186.836,238.695)(197.498,238.695)(204.164,238.695)(208.154,238.695)(211.199,238.695)(219.863,238.695)(223.199,238.695)(227.879,238.695)(230.879,238.695)(236.189,238.695)(241.499,238.695)(244.817,238.695)(250.127,238.695)(256.109,238.695)(259.091,238.695)(262.091,238.695)(266.069,238.695)(272.051,238.695)(276.029,238.695)(279.029,238.695))"	;
		Real2Array real2Array = Real2Array.createFromCoords(coords);
		Assert.assertNotNull("coords", real2Array);
		Assert.assertEquals("coords", 31, real2Array.size());
		
		Assert.assertTrue("coords 0, found: "+real2Array.get(0), new Real2(112.559,238.695).isEqualTo(real2Array.get(0), 0.001));
	}
	
	@Test
	public void testGetMidPointArray() {
		Real2Array r2array0 = new Real2Array(
				new RealArray(new double[]{1., 2., 3., 4.}),
				new RealArray(new double[]{21., 22., 23., 24.})
				);
		Real2Array r2array1 = new Real2Array(
				new RealArray(new double[]{21., 22., 23., 24.}),
				new RealArray(new double[]{41., 42., 43., 44.})
				);
		Real2Array midArray = r2array0.getMidPointArray(r2array1);
		LOG.trace("mid "+midArray);
		Assert.assertEquals("mid", "((11.0,31.0)(12.0,32.0)(13.0,33.0)(14.0,34.0))", midArray.toString());
	}
	
	@Test
	public void testIterator() {
		RealArray xArray = new RealArray(new double[]{0.,1.,2.});
		RealArray yArray = new RealArray(new double[]{10.,11.,12.});
		Real2Array real2Array = new Real2Array(xArray, yArray);
		Iterator<Real2> realIterator = real2Array.iterator();
		Assert.assertTrue("start", realIterator.hasNext());
		Assert.assertTrue("start", realIterator.hasNext());
		Assert.assertTrue("start", realIterator.hasNext());
		Assert.assertTrue("start", realIterator.hasNext());
		Assert.assertTrue("start", new Real2(0., 10.).isEqualTo(realIterator.next(), 0.001));
		Assert.assertTrue("start", new Real2(1., 11.).isEqualTo(realIterator.next(), 0.001));
		Assert.assertTrue("after 1", realIterator.hasNext());
		Assert.assertTrue("after 1", new Real2(2., 12.).isEqualTo(realIterator.next(), 0.001));
		Assert.assertFalse("end", realIterator.hasNext());
		Assert.assertNull("after 2", realIterator.next());
	}
	

	@Test
	public void testIterators() {
		RealArray realArray = new RealArray(new double[]{0,1,2});
		Iterator<Double> realIterator00 = realArray.iterator();
		Iterator<Double> realIterator01 = realArray.iterator();
		Assert.assertTrue("start", realIterator00.hasNext());
		Assert.assertEquals("start", 0., (double) realIterator00.next(), 0.001);
		Assert.assertEquals("start", 1., (double) realIterator00.next(), 0.001);
		Assert.assertEquals("start", 0., (double) realIterator01.next(), 0.001);
		Assert.assertEquals("end0", 2., (double) realIterator00.next(), 0.001);
		Assert.assertFalse("end0", realIterator00.hasNext());
		Assert.assertTrue("middle1", realIterator01.hasNext());
		Assert.assertNull("endo", realIterator00.next());
		Assert.assertEquals("start", 1., (double) realIterator01.next(), 0.001);
	}
	
	@Test
	public void testSort() {
		Real2Array r2a = get6ElementTestArray();
		r2a.sortAscending(0);
		Assert.assertEquals("xa", "((1.0,9.0)(2.0,8.0)(4.0,7.0)(5.0,1.0)(7.0,3.0)(8.0,6.0))", r2a.toString());
		r2a.sortAscending(1);
		Assert.assertEquals("xa", "((5.0,1.0)(7.0,3.0)(8.0,6.0)(4.0,7.0)(2.0,8.0)(1.0,9.0))", r2a.toString());
		r2a.sortDescending(0);
		Assert.assertEquals("xa", "((8.0,6.0)(7.0,3.0)(5.0,1.0)(4.0,7.0)(2.0,8.0)(1.0,9.0))", r2a.toString());
		r2a.sortDescending(1);
		Assert.assertEquals("xa", "((1.0,9.0)(2.0,8.0)(4.0,7.0)(8.0,6.0)(7.0,3.0)(5.0,1.0))", r2a.toString());
	}
	
	@Test
	public void testGetMidPointOfEnds() {
		Real2Array r2a = get6ElementTestArray();
		Real2 midPoint = r2a.getMidpointOfEnds();
		Assert.assertEquals("(4.5,7.5)", midPoint.toString());
	}

	@Test
	public void testGetRotatedAboutMidPointOfEnds() {
		Real2Array r2a = get6ElementTestArray();
		Real2Array rot = r2a.getRotatedAboutMidPoint();
		Assert.assertEquals("((8.0,6.0)(2.0,12.0)(7.0,7.0)(4.0,14.0)(5.0,8.0)(1.0,9.0))", rot.toString());
	}
	
	// ======================================
	private Real2Array get6ElementTestArray() {
		Real2Array r2a = new Real2Array();
		r2a.addElement(new Real2(1.0, 9.0));
		r2a.addElement(new Real2(7.0, 3.0));
		r2a.addElement(new Real2(2.0, 8.0));
		r2a.addElement(new Real2(5.0, 1.0));
		r2a.addElement(new Real2(4.0, 7.0));
		r2a.addElement(new Real2(8.0, 6.0));
		return r2a;
	}

}
