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
	
	@Test
	public void testInterpolate() {
		Real2Array r2a = createSubArray50();
		Real2 r2 = r2a.interpolate(0.3).format(1);
		Assert.assertEquals("interpolate", "(0.6,0.9)", r2.toString());
		r2 = r2a.interpolate(1.2).format(1);
		Assert.assertEquals("interpolate", "(2.4,3.6)", r2.toString());
	}
	
	@Test
	public void testSubArray() {
		Real2Array r2a = createSubArray50();
		Real2Array sub = r2a.createSubArray(0, 4, 5);
		Assert.assertEquals("sub", "((0.0,0.0)(2.0,3.0)(4.0,6.0)(6.0,9.0)(8.0,12.0))", sub.toString());
		sub = r2a.createSubArray(1, 5, 5);
		Assert.assertEquals("sub", "((2.0,3.0)(4.0,6.0)(6.0,9.0)(8.0,12.0)(10.0,15.0))", sub.toString());
		sub = r2a.createSubArray(1, 9, 5);
		Assert.assertEquals("sub", "((2.0,3.0)(6.0,9.0)(10.0,15.0)(14.0,21.0)(18.0,27.0))", sub.toString());
		try {
			sub = r2a.createSubArray(1, 9, 4);
			throw new RuntimeException(" should throw RTE");
		} catch (RuntimeException e) {
			// expected
		}
	}

	private Real2Array createSubArray50() {
		Real2Array r2a = new Real2Array(51);
		for (int i = 0; i <= 50; i++) {
			r2a.setElement(i, new Real2(i * 2, i * 3));
		}
		return r2a;
	}
	
	@Test
	public void testExtrapolate() {
		Real2Array r2a = get60ElementSemiCircle();
		System.out.println(r2a.format(1));
		Real2Array r2asub = r2a.createSubArray(0, 40, 5);
		RealArray r2acurve = r2asub.calculateDeviationsRadiansPerElement();
		System.out.println("ra2 deviation "+r2acurve.format(2));
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
	
	/** 60 elements of semicircle radius 10.
	 * 
	 * @return
	 */
	private Real2Array get60ElementSemiCircle() {
		Real2Array r2a = new Real2Array(60);
		double r = 10.0;
		for (int i = 0; i < 60; i++) {
			double t = Math.PI * ((double) i) / 60.;
			r2a.setElement(i, new Real2(r * Math.sin(t), r * Math.cos(t)));
		}
//		System.out.println(">sc >"+r2a.format(2));
		return r2a;
	}
	
	/** from an experimental curve
	 * 
	 * @return
	 */
	private Real2Array get100ElementTestArray() {
		String s = ""
				+ "((1638.0,71.0)(1637.0,72.0)(1636.0,73.0)(1635.0,73.0)(1634.0,74.0)(1633.0,74.0)(1632.0,75.0)(1631.0,75.0)"
				+ "(1630.0,76.0)(1629.0,76.0)(1628.0,77.0)(1627.0,77.0)(1626.0,78.0)(1625.0,78.0)(1624.0,79.0)(1623.0,79.0)(1622.0,80.0)"
				+ "(1621.0,80.0)(1620.0,80.0)(1619.0,81.0)(1618.0,82.0)(1617.0,82.0)(1616.0,83.0)(1615.0,84.0)(1614.0,84.0)(1613.0,85.0)"
				+ "(1612.0,85.0)(1611.0,86.0)(1610.0,86.0)(1609.0,87.0)(1608.0,88.0)(1607.0,89.0)(1606.0,89.0)(1605.0,90.0)(1604.0,90.0)"
				+ "(1603.0,91.0)(1602.0,92.0)(1601.0,92.0)(1600.0,93.0)(1599.0,94.0)(1598.0,95.0)(1597.0,95.0)"
				+ "(1596.0,96.0)(1595.0,97.0)(1594.0,98.0)(1593.0,98.0)(1592.0,99.0)(1591.0,100.0)"
				+ "(1590.0,101.0)(1589.0,102.0)(1588.0,102.0)(1587.0,103.0)(1586.0,104.0)(1585.0,105.0)"
				+ "(1584.0,106.0)(1583.0,106.0)(1582.0,107.0)(1581.0,108.0)(1580.0,109.0)(1579.0,110.0)"
				+ "(1578.0,111.0)(1577.0,111.0)(1576.0,112.0)(1576.0,113.0)(1575.0,114.0)(1574.0,114.0)(1573.0,115.0)"
				+ "(1573.0,116.0)(1572.0,117.0)(1571.0,117.0)(1570.0,118.0)(1569.0,119.0)(1568.0,120.0)"
				+ "(1567.0,121.0)(1566.0,122.0)(1565.0,123.0)(1564.0,124.0)(1563.0,125.0)"
				+ "(1562.0,126.0)(1561.0,127.0)(1560.0,127.0)(1559.0,128.0)(1558.0,129.0)(1557.0,130.0)"
				+ "(1556.0,131.0)(1555.0,132.0)(1555.0,133.0)(1554.0,134.0)(1553.0,134.0)(1552.0,135.0)"
				+ "(1551.0,136.0)(1550.0,137.0)(1549.0,138.0)(1548.0,139.0)(1547.0,140.0)(1546.0,140.0)"
				+ "(1545.0,141.0)(1545.0,142.0)(1544.0,143.0)(1543.0,143.0)(1542.0,144.0)(1541.0,145.0)"
				+ "(1540.0,146.0)(1539.0,146.0)(1538.0,147.0)(1537.0,148.0)(1536.0,149.0)(1535.0,150.0)"
				+ "(1534.0,151.0)(1533.0,152.0)(1532.0,153.0)(1531.0,153.0)(1530.0,154.0)(1529.0,155.0)"
				+ "(1528.0,155.0)(1527.0,156.0)(1526.0,157.0)(1525.0,158.0)(1524.0,158.0)(1523.0,159.0)(1522.0,160.0)"
				+ "(1521.0,161.0)(1520.0,162.0)(1519.0,162.0)(1518.0,163.0)(1517.0,164.0)(1516.0,164.0)(1515.0,164.0)(1514.0,165.0)"
				+ "(1513.0,166.0)(1512.0,166.0)(1511.0,167.0)(1510.0,168.0)(1509.0,168.0)(1508.0,169.0)(1507.0,170.0)(1506.0,170.0))";
		Real2Array r2a = Real2Array.createFromString(s);
		RealArray ra = r2a.calculate4SegmentedCurvature();
		System.out.println("curv: "+ra.format(3));
//		System.out.println(r2a);
		return r2a;
	}

}
