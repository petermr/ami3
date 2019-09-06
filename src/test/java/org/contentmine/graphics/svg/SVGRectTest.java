package org.contentmine.graphics.svg;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.junit.Assert;
import org.junit.Test;



/** tests SVGRect
 * 
 * @author pm286
 *
 */
public class SVGRectTest {
private static final Logger LOG = Logger.getLogger(SVGRectTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double epsilon = 0.01;

	@Test
	public void testCreateHW() {
		double width = 15.;
		double height = 25.;
		SVGRect rect = new SVGRect( 10., 20., width, height);
		Assert.assertEquals("<rect xmlns=\"http://www.w3.org/2000/svg\""
				+ " x=\"10.0\" y=\"20.0\" width=\"15.0\" height=\"25.0\" />", rect.toXML());
		Assert.assertEquals(10., rect.getX(), epsilon);
		Assert.assertEquals(20., rect.getY(), epsilon);
		Assert.assertTrue(new Real2(10., 20.).isEqualTo(rect.getXY(), epsilon));
		Assert.assertEquals(15., rect.getWidth(), epsilon);
		Assert.assertEquals(25., rect.getHeight(), epsilon);
		Real2Range bbox = rect.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new Real2(10., 20.), new Real2(25., 45.)), epsilon));
	}
	
	@Test
	public void testCreateXY() {
		SVGRect rect = new SVGRect( new Real2(10., 20.), new Real2(25., 45.));
		Assert.assertEquals("<rect xmlns=\"http://www.w3.org/2000/svg\""
				+ " x=\"10.0\" y=\"20.0\" width=\"15.0\" height=\"25.0\" />", rect.toXML());
		Assert.assertEquals(10., rect.getX(), epsilon);
		Assert.assertEquals(20., rect.getY(), epsilon);
		Assert.assertEquals(15., rect.getWidth(), epsilon);
		Assert.assertEquals(25., rect.getHeight(), epsilon);
		Real2Range bbox = rect.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new Real2(10., 20.), new Real2(25., 45.)), epsilon));
	}
	@Test
	public void testIsGeometricallyEqualTo() {
		SVGShape rect0 = new SVGRect(new Real2(10., 20.), new Real2(25., 45.));
		Assert.assertTrue(rect0.isGeometricallyEqualTo(
				new SVGRect(new Real2(10.005, 20.005), new Real2(25.005, 45.005)), epsilon));
		Assert.assertTrue(rect0.isGeometricallyEqualTo(
				new SVGRect(new Real2(9.995, 20.005), new Real2(25.005, 45.005)), epsilon));
		Assert.assertTrue(rect0.isGeometricallyEqualTo(
				new SVGRect(9.995, 20.005, 15., 25.), epsilon));
		Assert.assertFalse(rect0.isGeometricallyEqualTo(
				new SVGRect(new Real2(10.015, 20.005), new Real2(25.005, 45.005)), epsilon));
		Assert.assertFalse(rect0.isGeometricallyEqualTo(
				new SVGRect(new Real2(9.985, 20.005), new Real2(25.005, 45.005)), epsilon));
	}


}
