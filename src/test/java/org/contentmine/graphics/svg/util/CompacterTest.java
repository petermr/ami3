package org.contentmine.graphics.svg.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Attribute;
import nu.xom.Element;

public class CompacterTest {

	List<String> ATTNAMES = Arrays.asList("a", "b", "x");
	List<String> SVGNAMES = Arrays.asList("stroke", "fill", "clip-path", "fontName", "font-family", "font-weight", "font-size", "font-style", "stroke-width"
			/*"x", "y", "width"*/);
	
	@Test
	public void testRemoveAttributes() {
		Compacter compacter = new Compacter();
		compacter.setAttributeNames(ATTNAMES);
		Element e = new Element("e");
		e.addAttribute(new Attribute("a", "a1"));
		e.addAttribute(new Attribute("b", "b1"));
		e.addAttribute(new Attribute("c", "c1"));
		compacter.removeAttributes(e);
		Assert.assertEquals("attributes", "<e c=\"c1\" />", e.toXML());
	}
	
	@Test
	public void testAddAttributes() {
		Compacter compacter = new Compacter();
		compacter.setAttributeNames(ATTNAMES);
		Element e = new Element("e");
		e.addAttribute(new Attribute("a", "a1"));
		e.addAttribute(new Attribute("b", "b1"));
		e.addAttribute(new Attribute("c", "c1"));
		SVGG g = new SVGG();
		compacter.addAttributes(g, e);
		Assert.assertEquals("attributes", "<g xmlns=\"http://www.w3.org/2000/svg\" a=\"a1\" b=\"b1\" />", g.toXML());
	}

	@Test
	public void testAddAttributesChanged0() {
		Attribute a1 = new Attribute("a", "a1");
		Attribute a2 = new Attribute("a", "a1");
		Assert.assertFalse("atts", a1.equals(a2));
		Assert.assertTrue("atts", Compacter.isEqual(a1, a2));
	}
	
	@Test
	public void testAddAttributesChanged() {
		Compacter compacter = new Compacter();
		compacter.setAttributeNames(ATTNAMES);
		Element e1 = new Element("e");
		e1.addAttribute(new Attribute("a", "a1"));
		e1.addAttribute(new Attribute("b", "b1"));
		e1.addAttribute(new Attribute("c", "c1"));
		Element e2 = new Element("e");
		e2.addAttribute(new Attribute("a", "a1"));
		e2.addAttribute(new Attribute("b", "b1"));
		e2.addAttribute(new Attribute("c", "c1"));
		Assert.assertFalse("attributes", compacter.attributesChanged(e1, e2));
		Element e3 = new Element("e");
		e3.addAttribute(new Attribute("a", "a2"));
		e3.addAttribute(new Attribute("b", "b1"));
		e3.addAttribute(new Attribute("c", "c1"));
		Assert.assertTrue("attributes", compacter.attributesChanged(e1, e3));
	}
	
	@Test
	public void testExistsIn() {
		Element e1 = new Element("e");
		e1.addAttribute(new Attribute("a", "a1"));
		e1.addAttribute(new Attribute("b", "b1"));
		e1.addAttribute(new Attribute("c", "c1"));
		Assert.assertTrue("existsIn", Compacter.existsIn(e1, new Attribute("a", "a1")));
		Assert.assertFalse("existsIn", Compacter.existsIn(e1, new Attribute("a", "a2")));
		Assert.assertFalse("existsIn", Compacter.existsIn(e1, new Attribute("b", "a1")));
	}
	
	@Test
	public void testCompacter() {
		Compacter compacter = new Compacter();
		compacter.setAttributeNames(ATTNAMES);
		List<Element> elementList = new ArrayList<Element>();
		Element e1 = new Element("e");
		e1.addAttribute(new Attribute("a", "a1"));
		e1.addAttribute(new Attribute("b", "b1"));
		e1.addAttribute(new Attribute("c", "c1"));
		elementList.add(e1);
		Element e2 = new Element("e");
		e2.addAttribute(new Attribute("a", "a1"));
		e2.addAttribute(new Attribute("b", "b1"));
		e2.addAttribute(new Attribute("c", "c2"));
		elementList.add(e2);
		Element e3 = new Element("e");
		e3.addAttribute(new Attribute("a", "a2"));
		e3.addAttribute(new Attribute("b", "b1"));
		e3.addAttribute(new Attribute("c", "c2"));
		elementList.add(e3);
		SVGSVG topG = compacter.compactChildAttributesIntoGroups(elementList);
		Assert.assertEquals("g ", 
				"<svg xmlns=\"http://www.w3.org/2000/svg\"" +
				  " xmlns:svgx=\"http://www.xml-cml.org/schema/svgx\" b=\"b1\">" +
				  "<g a=\"a1\">" +
				    "<e xmlns=\"\" c=\"c1\" />" +
				    "<e xmlns=\"\" c=\"c2\" />" +
				   "</g>" +
				   "<g a=\"a2\">" +
				    "<e xmlns=\"\" c=\"c2\" />" +
				   "</g>" +
				"</svg>",
				topG.toXML());
	}
	
	@Test
	public void testChunk() throws Exception {
		AbstractCMElement svg = SVGElement.readAndCreateSVG(SVGHTMLFixtures.SVG_G_8_0_SVG);
		List<SVGElement> textList = SVGText.generateElementList(svg, "//*[local-name()='text']");
		Compacter compacter = new Compacter();
		compacter.setAttributeNames(SVGNAMES);
		SVGSVG topG = compacter.compactChildAttributesIntoGroups(textList);
		Assert.assertNotNull(topG);
		Assert.assertEquals("g", 7, topG.query("//*[local-name()='g']").size());
//		SVGUtil.debug(topG, new FileOutputStream("target/g.8.0.svg"), 1);
	}
	
	@Test
	public void testPage6Elements() throws Exception {
		AbstractCMElement svg = SVGElement.readAndCreateSVG(SVGHTMLFixtures.SVG_PAGE6_SVG);
		List<SVGElement> elementList = SVGText.generateElementList(svg, 
				"./*[local-name()='text' or local-name()='path' or local-name()='image']");
		Compacter compacter = new Compacter();
		compacter.setAttributeNames(SVGNAMES);
		List<SVGG> gList = compacter.compactElements(elementList);
		Assert.assertNotNull(gList);
		Assert.assertEquals("gList", 15, gList.size());
//		SVGUtil.debug(compacter.getTopG(), new FileOutputStream("target/page6elements.svg"), 1);
	}
	
	@Test
	public void testPage6ElementsAttributes() throws Exception {
		AbstractCMElement svg = SVGElement.readAndCreateSVG(SVGHTMLFixtures.SVG_PAGE6_SVG);
		List<SVGElement> elementList = SVGText.generateElementList(svg, 
				"./*[local-name()='text' or local-name()='path' or local-name()='image']");
		Compacter compacter = new Compacter();
		compacter.setAttributeNames(SVGNAMES);
		List<SVGG> gList = compacter.compactElements(elementList);
		compacter.compactGroups(gList);
		Assert.assertEquals("gList", 15, gList.size());
//		SVGUtil.debug(compacter.getTopG(), new FileOutputStream("target/page6elsAtts.svg"), 1);
	}
}
