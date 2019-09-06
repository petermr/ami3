package org.contentmine.graphics.svg.linestuff;


import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.SideOrientation;
import org.junit.Assert;
import org.junit.Test;

public class JointTest {

	double EPS = 0.01;
	final static SideOrientation nullOrientation = null;
	
	@Test
	public void testIsAtEndOfBackbone1() {
		SVGLine backbone = new SVGLine(new Real2(100,100), new Real2(100,300));
		Real2 point = new Real2(100,100);
		SVGLine line = new SVGLine(point, new Real2(200,100));
		Joint joint = new Joint(point, backbone, line, nullOrientation, EPS);
		Assert.assertTrue(joint.isAtEndOfBackbone(EPS));
	}
	
	@Test
	public void testIsAtEndOfBackbone2() {
		SVGLine backbone = new SVGLine(new Real2(100,100), new Real2(100,300));
		Real2 point = new Real2(100,200);
		SVGLine line = new SVGLine(point, new Real2(0,200));
		Joint joint = new Joint(point, backbone, line, nullOrientation, EPS);
		Assert.assertFalse(joint.isAtEndOfBackbone(EPS));
	}
	
	@Test
	public void testIsAtEndOfBackbone3() {
		SVGLine backbone = new SVGLine(new Real2(100,100), new Real2(100,300));
		Real2 point = new Real2(100,300);
		SVGLine line = new SVGLine(point, new Real2(200,300));
		Joint joint = new Joint(point, backbone, line, nullOrientation, EPS);
		Assert.assertTrue(joint.isAtEndOfBackbone(EPS));
	}
}
