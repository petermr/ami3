package org.contentmine.graphics.svg;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.junit.Assert;
import org.junit.Test;



/** tests SVGEllipse
 * 
 * @author pm286
 *
 */
public class SVGEllipseTest {
private static final Logger LOG = Logger.getLogger(SVGEllipseTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double epsilon = 0.01;

	@Test
	public void testCreateHW() {
		double rad = 15.;
		SVGEllipse ellipse = new SVGEllipse(new Real2(10., 20.), new Real2(5, 15));
		ellipse.setFill("none");
		ellipse.setStroke("black");
		ellipse.setStrokeWidth(1.0);
		Assert.assertEquals("<ellipse xmlns=\"http://www.w3.org/2000/svg\" cx=\"10.0\" cy=\"20.0\" rx=\"5.0\" ry=\"15.0\""
				+ " style=\"fill:none;stroke:black;stroke-width:1.0;\""
				+ " />", ellipse.toXML());
		Assert.assertEquals(10., ellipse.getX(), epsilon);
		Assert.assertEquals(20., ellipse.getY(), epsilon);
		Assert.assertEquals(10., ellipse.getCX(), epsilon);
		Assert.assertEquals(20., ellipse.getCY(), epsilon);
		Assert.assertTrue(new Real2(10., 20.).isEqualTo(ellipse.getXY(), epsilon));
		Assert.assertEquals(5., ellipse.getRX(), epsilon);
		Assert.assertEquals(15., ellipse.getRY(), epsilon);
		Real2Range bbox = ellipse.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new RealRange(5., 15.), new RealRange(5., 35.)), epsilon));
	}
	
	@Test
	public void testIsGeometricallyEqualTo() {
		SVGEllipse ellipse0 = new SVGEllipse(new Real2(10., 20.), 5., 15.);
		Assert.assertTrue(ellipse0.isGeometricallyEqualTo(
				new SVGEllipse(new Real2(10.005, 20.005), 5.005, 15.005), epsilon));
		Assert.assertTrue(ellipse0.isGeometricallyEqualTo(
				new SVGEllipse(new Real2(9.995, 20.005), 4.995, 14.995), epsilon));
		Assert.assertFalse(ellipse0.isGeometricallyEqualTo(
				new SVGEllipse(new Real2(10.015, 20.005), 5.005, 15.015), epsilon));
	}


}
