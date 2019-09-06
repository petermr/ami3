package org.contentmine.graphics.svg;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.testutil.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Attribute;

public class GraphicsElementTest {
	private static final Logger LOG = Logger.getLogger(GraphicsElementTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testUseStyleAttribute() {
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), 3.);
		circle.setStroke("red");
		circle.setStrokeWidth(1.0);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' style=\"fill:#aaffff;stroke:red;stroke-width:1.0;\""+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
	}

	@Test
	public void testUseStyleAttribute2() {
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), 3.);
		// not default attributes (maybe a bad idea, but...)
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' "+ 
				" xmlns='http://www.w3.org/2000/svg' />", 
				circle, true, 0.001);
		circle.setUseStyleAttribute(true);
		circle.setStroke("red");
		circle.setStrokeWidth(1.0);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' style=\"fill:#aaffff;stroke:red;stroke-width:1.0;\""+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
	}
	

	@Test
	public void testUseStyleAttribute3() {
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), 3.);
		circle.setUseStyleAttribute(true);
		circle.setStroke("red");
		circle.setStrokeWidth(1.0);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' style='stroke:red;stroke-width:1.0;'"+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
		circle.setUseStyleAttribute(false);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' stroke=\"red\" stroke-width=\"1.0\""+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
	}
	

	@Test
	public void testUseStyleAttribute4() {
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), 3.);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' "+ 
				" xmlns='http://www.w3.org/2000/svg' />", 
				circle, true, 0.001);
		circle.setUseStyleAttribute(true);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' "+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
		circle.setStroke(null);
		circle.setStrokeWidth(null);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' style='stroke:none;stroke-width:0.0;'"+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
		circle.setUseStyleAttribute(false);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' stroke='none' stroke-width='0.0' "+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
		circle.setStroke("red");
		circle.setStrokeWidth(1.0);
		TestUtils.assertEqualsIncludingFloat("style",
				"<circle cx='10.0' cy='20.0' r='3.0' stroke='red' stroke-width='1.0'"+ 
				" xmlns='http://www.w3.org/2000/svg'/>", circle, true, 0.001);
	}
	

	@Test
	public void testUseStyleAttribute5() {
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), 3.);
		circle.addAttribute(new Attribute("style", "line-cap : smooth;"));
		Assert.assertNull("fill", circle.getFill());
		Assert.assertNull("stroke", circle.getStroke());
		Assert.assertNull("bundle", circle.getAttributeValue(StyleBundle.STROKE));
		Assert.assertEquals("width", 0.0, circle.getStrokeWidth(), 0.001);
		Assert.assertEquals("style", "line-cap : smooth;", circle.getStyle());
		circle.setUseStyleAttribute(true);
		Assert.assertNull("bundle",  circle.getAttributeValue(StyleBundle.STROKE));
		Assert.assertNull("bundle",  circle.getStroke());
		Assert.assertEquals("style", "line-cap:smooth;", circle.getStyle());
		circle.setUseStyleAttribute(false);
		circle.setStroke("red");
		circle.setStrokeWidth(3.0);
		circle.setOpacity(0.2);
		circle.setFill(null);
		Assert.assertEquals("fill", "none", circle.getFill());
		Assert.assertEquals("stroke", "red", circle.getStroke());
		Assert.assertEquals("stroke", "red",  circle.getAttributeValue(StyleBundle.STROKE));
		Assert.assertEquals("opacity", 0.2, circle.getOpacity(), 0.001);
		Assert.assertEquals("opacity", "0.2",  circle.getAttributeValue(StyleBundle.OPACITY));
		Assert.assertEquals("width", 3.0, circle.getStrokeWidth(), 0.001);
		Assert.assertEquals("style", "line-cap:smooth;", circle.getStyle());
		circle.setUseStyleAttribute(true);
		Assert.assertNull("bundle",  circle.getAttributeValue(StyleBundle.STROKE));
		Assert.assertEquals("bundle",  "red", circle.getStroke());
//		Assert.assertEquals("style", "stroke:red;stroke-width:3.0;opacity:0.2;line-cap:smooth;", circle.getStyle());
	}
}
