package org.contentmine.graphics.svg;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.junit.Assert;
import org.junit.Test;



/** tests SVGCircle
 * 
 * @author pm286
 *
 */
public class SVGCircleTest {
private static final Logger LOG = Logger.getLogger(SVGCircleTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double epsilon = 0.01;

	@Test
	public void testCreateHW() {
		double rad = 15.;
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), rad);
		circle.setFill("none");
		circle.setStroke("black");
		circle.setStrokeWidth(1.0);
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"10.0\" cy=\"20.0\" r=\"15.0\""
				+ " style=\"fill:none;stroke:black;stroke-width:1.0;\""
				+ " />", circle.toXML());
		Assert.assertEquals(10., circle.getX(), epsilon);
		Assert.assertEquals(20., circle.getY(), epsilon);
		Assert.assertEquals(10., circle.getCX(), epsilon);
		Assert.assertEquals(20., circle.getCY(), epsilon);
		Assert.assertTrue(new Real2(10., 20.).isEqualTo(circle.getXY(), epsilon));
		Assert.assertEquals(15., circle.getRad(), epsilon);
		Real2Range bbox = circle.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new RealRange(-5., 25.), new RealRange(5., 35.)), epsilon));
	}
	
	@Test
	public void testIsGeometricallyEqualTo() {
		SVGCircle circle0 = new SVGCircle(new Real2(10., 20.), 15.);
		Assert.assertTrue(circle0.isGeometricallyEqualTo(
				new SVGCircle(new Real2(10.005, 20.005), 15.005), epsilon));
		Assert.assertTrue(circle0.isGeometricallyEqualTo(
				new SVGCircle(new Real2(9.995, 20.005), 14.995), epsilon));
		Assert.assertFalse(circle0.isGeometricallyEqualTo(
				new SVGCircle(new Real2(10.015, 20.005), 15.005), epsilon));
		Assert.assertFalse(circle0.isGeometricallyEqualTo(
				new SVGCircle(new Real2(9.995, 20.005), 15.015), epsilon));
	}


}
