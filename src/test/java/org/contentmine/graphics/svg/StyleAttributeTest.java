package org.contentmine.graphics.svg;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Test;

import junit.framework.Assert;
import nu.xom.Attribute;

/** test StyleAttribute
 * 
 * @author pm286
 *
 */
public class StyleAttributeTest {

	@Test
	public void testDefaultRemoveStyles() {
		GraphicsElement element = new SVGLine();
		element.removeAllStyles();
		// seem to be some built in attributes // we will remove them later
		Assert.assertEquals("<line xmlns=\"http://www.w3.org/2000/svg\" />",
			element.toXML());
	}
	
	@Test
	public void testDefaultAttributes() {
		GraphicsElement element = new SVGCircle();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" />",
			element.toXML());
		StyleAttributeFactory styleAttributeFactory = 
			element.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("style", "", styleAttributeFactory.getAttributeValue());
	}
	
	@Test
	public void testSetOldStyle() {
		String xmlString = "<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"10.0\" cy=\"20.0\" r=\"5.0\" />";
		GraphicsElement circle = SVGElement.readAndCreateSVG(XMLUtil.parseXML(xmlString));
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\""
				+ " cx=\"10.0\" cy=\"20.0\" r=\"5.0\" />",
				circle.toXML());
		circle.removeAllStyles();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\""
				+ " cx=\"10.0\" cy=\"20.0\" r=\"5.0\" />",
				circle.toXML());
		circle.setFill("blue");
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\""
				+ " cx=\"10.0\" cy=\"20.0\" r=\"5.0\" style=\"fill:blue;\" />",
				circle.toXML());
		circle.setStroke("yellow");
		// seem to be some built in attributes // we will remove them later
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" "
				+ "cx=\"10.0\" cy=\"20.0\" r=\"5.0\" style=\"fill:blue;stroke:yellow;\" />",
			circle.toXML());
		StyleAttributeFactory styleAttributeFactory = 
			circle.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("style", "fill:blue;stroke:yellow;", styleAttributeFactory.getAttributeValue());
	}
	
	@Test
	public void testRemove() {
		SVGElement circle = new SVGCircle();
		circle.createStyleAttributeFactoryFromOldStyles();
		circle.removeOldStyleAttributes();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" />",
			circle.toXML());
		circle.setCXY(new Real2(10.0, 20.0));
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"10.0\" cy=\"20.0\" />",
			circle.toXML());
		// REMOVE only removes Style attributes
		circle.removeOldStyleAttributes();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"10.0\" cy=\"20.0\" />",
			circle.toXML());
	}
	
	@Test
	public void testOldStyle() {
		GraphicsElement circle = new SVGCircle();
		circle.setStroke("red");
		StyleAttributeFactory oldStyleAttributeFactory = 
			circle.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" style=\"stroke:red;\" />",
			circle.toXML());
		Assert.assertEquals("style", "stroke:red;", oldStyleAttributeFactory.getAttributeValue());
	}
	
	@Test
	public void testNonSVGAttributes() {
		GraphicsElement circle = new SVGCircle();
		circle.addAttribute(new Attribute("stroke", "red"));
		circle.addAttribute(new Attribute("fill", "blue"));
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" stroke=\"red\" fill=\"blue\" />",
				circle.toXML());
		StyleAttributeFactory oldStyleAttributeFactory = circle.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("style", "fill:blue;stroke:red;", oldStyleAttributeFactory.getAttributeValue());
		circle.convertOldStyleToStyle();
	}
	
	@Test
	public void testCSSStyle() {
		GraphicsElement circle = new SVGCircle();
		circle.removeOldStyleAttributes();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" />", circle.toXML());
		circle.setCSSStyleAndRemoveOldStyle("stroke:red;fill:green;");
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" style=\"fill:green;stroke:red;\" />",
				circle.toXML());
		StyleAttributeFactory styleAttributeFactory = circle.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("fill:green;stroke:red;", styleAttributeFactory.getAttributeValue());
	}
	
	@Test
	public void testCSSStylePriority() {
		GraphicsElement circle = new SVGCircle();
		circle.setUseStyleAttribute(true);
		circle.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" />", circle.toXML());
		circle.setStroke("blue");
		circle.setFill("yellow");
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" style=\"fill:yellow;stroke:blue;\" />",
				circle.toXML());
		StyleAttributeFactory.convertElementAndChildrenFromOldStyleAttributesToCSS(circle);
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" style=\"fill:yellow;stroke:blue;\" />",
			circle.toXML());
	}
	
	@Test
	public void testCSSStyleConflict() {
		GraphicsElement circle = new SVGCircle();
		circle.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" />", circle.toXML());
		circle.setStroke("blue");
		circle.setFill("yellow");
		circle.setCSSStyleAndRemoveOldStyle("stroke:red;fill:green;");
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" style=\"fill:green;stroke:red;\" />",
				circle.toXML());
		circle.createStyleAttributeFactoryFromOldStyles();
		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" style=\"fill:green;stroke:red;\" />",
			circle.toXML());
	}
	
//	@Test
//	public void testOldStyle() {
//		SVGElement circle = new SVGCircle();
//		StyleAttributeFactory.createStyleAttributeFactoryFromOldStyle(circle, AttributeStrategy.REMOVE);
//		Assert.assertEquals("<circle xmlns=\"http://www.w3.org/2000/svg\" />", circle.toXML());
//		
//	}
	
}
