/**
 *    Copyright 2011 Peter Murray-Rust et. al.
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

package org.contentmine.graphics.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.testutil.TestUtils;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.AbstractCMElement;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

public class SVGElementTest {
	private static final Logger LOG = Logger.getLogger(SVGElementTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String GRAPHICS_RESOURCE = CHESConstants.ORG_CM + "/cml/graphics/examples";

	@Test
	@Ignore
	public final void testcreateSVGElement() {
		Element oldElement =TestUtils.parseValidFile(GRAPHICS_RESOURCE + XMLConstants.U_S
				+ "image12.svg");
		AbstractCMElement newSvg = SVGElement.readAndCreateSVG(oldElement);
		Assert.assertEquals("class", SVGSVG.class, newSvg.getClass());
		TestUtils.assertEqualsCanonically("copy",TestUtils.parseValidFile(GRAPHICS_RESOURCE + XMLConstants.U_S
				+ "image12.svg"), newSvg, true);
	}

	@Test
	public final void testIsIncludedByMask() {
		SVGRect rect = new SVGRect(new Real2(10., 20.), new Real2(50., 100.));
		RealRange mask = new RealRange(9., 51.);
		Assert.assertTrue(rect.isIncludedBy(mask, Direction.HORIZONTAL));
	}

	@Test
	public final void testIsNotIncludedByMask() {
		SVGRect rect = new SVGRect(new Real2(10., 20.), new Real2(50., 100.));
		RealRange mask = new RealRange(11., 49.);
		Assert.assertFalse(rect.isIncludedBy(mask, Direction.HORIZONTAL));
	}
	
	@Test
	public final void testIsIncludedByMaskArray() {
		SVGRect rect = new SVGRect(new Real2(10., 20.), new Real2(50., 100.));
		RealRangeArray rangeArray = new RealRangeArray();
		rangeArray.add(new RealRange(9., 51.));
		rangeArray.add(new RealRange(2., 7.));
		Assert.assertTrue(rect.isIncludedBy(rangeArray, Direction.HORIZONTAL));
	}

	@Test
	public final void testIsNotIncludedByMaskArray() {
		SVGRect rect = new SVGRect(new Real2(10., 20.), new Real2(50., 100.));
		RealRangeArray rangeArray = new RealRangeArray();
		rangeArray.add(new RealRange(25., 51.));
		rangeArray.add(new RealRange(9., 15.));
		Assert.assertFalse(rect.isIncludedBy(rangeArray, Direction.HORIZONTAL));
	}
			
	@Test
	public final void testIsIncludedByVerticalMask() {
		SVGRect rect = new SVGRect(new Real2(10., 20.), new Real2(50., 100.));
		RealRange mask = new RealRange(19., 101.);
		Assert.assertTrue(rect.isIncludedBy(mask, Direction.VERTICAL));
	}

	@Test
	public final void testIsNotIncludedByVerticalMask() {
		SVGRect rect = new SVGRect(new Real2(10., 20.), new Real2(50., 100.));
		RealRange mask = new RealRange(21., 99.);
		Assert.assertFalse(rect.isIncludedBy(mask, Direction.VERTICAL));
	}

	@Test
	public final void testFilterHorizontally() {
		RealRangeArray horizontalMask = new RealRangeArray();
		horizontalMask.add(new RealRange(9.,21.));
		horizontalMask.add(new RealRange(29.,41.));
		horizontalMask.add(new RealRange(49.,61.));
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		elementList.add(new SVGRect(new Real2(10., 20.), new Real2(20., 70.)));
		elementList.add(new SVGRect(new Real2(30., 0.), new Real2(40., 60.)));
		elementList.add(new SVGRect(new Real2(10., 0.), new Real2(40., 60.)));
		List<? extends SVGElement> newElementList = SVGElement.filterHorizontally(elementList, horizontalMask);
		Assert.assertEquals("filtered", 2, newElementList.size());
		elementList.add(new SVGRect(new Real2(50., 100.), new Real2(60., 160.)));
		newElementList = SVGElement.filterHorizontally(elementList, horizontalMask);
		Assert.assertEquals("filtered", 3, newElementList.size());
	}

	@Test
	public final void testFilterVertically() {
		RealRangeArray verticalMask = new RealRangeArray();
		verticalMask.add(new RealRange(9.,21.));
		verticalMask.add(new RealRange(29.,41.));
		verticalMask.add(new RealRange(49.,61.));
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		elementList.add(new SVGRect(new Real2(10., 50.), new Real2(20., 60.)));
		elementList.add(new SVGRect(new Real2(30., 30.), new Real2(40., 40.)));
		elementList.add(new SVGRect(new Real2(10., 0.), new Real2(40., 60.)));
		List<? extends SVGElement> newElementList = SVGElement.filterHorizontally(elementList, verticalMask);
		Assert.assertEquals("filtered", 2, newElementList.size());
		elementList.add(new SVGRect(new Real2(50., 10.), new Real2(60., 20.)));
		newElementList = SVGElement.filterHorizontally(elementList, verticalMask);
		Assert.assertEquals("filtered", 3, newElementList.size());
	}

	/**
	 * @param elementList elements to create mask
	 * @return RealRange array corresponding to (overlapped) ranges of elements
	 */
	@Test
	public void testCreateMask() {
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		elementList.add(new SVGRect(new Real2(0., 10.), new Real2(30., 40.)));
		elementList.add(new SVGRect(new Real2(40., 10.), new Real2(50., 40.)));
		elementList.add(new SVGRect(new Real2(45., 10.), new Real2(55., 40.)));
		RealRangeArray mask = SVGElement.createMask(elementList, Direction.HORIZONTAL);
		RealRangeArray maskRef = new RealRangeArray();
		maskRef.add(new RealRange(0., 30.));
		maskRef.add(new RealRange(40., 55.));
		Assert.assertEquals("create mask", maskRef.toString(), mask.toString());
	 }

	/**
	 * @param elementList elements to create mask
	 * @return RealRange array corresponding to (overlapped) ranges of elements
	 */
	@Test
	public void testCreateMaskWithTolerance() {
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		elementList.add(new SVGRect(new Real2(0., 10.), new Real2(30., 40.)));
		elementList.add(new SVGRect(new Real2(40., 10.), new Real2(50., 40.)));
		elementList.add(new SVGRect(new Real2(51., 10.), new Real2(55., 40.)));
		RealRangeArray mask = SVGElement.createMask(elementList, Direction.HORIZONTAL);
		RealRangeArray maskRef = new RealRangeArray();
		maskRef.add(new RealRange(0., 30.));
		maskRef.add(new RealRange(40., 50.));
		maskRef.add(new RealRange(51., 55.));
		mask = SVGElement.createMask(elementList, Direction.HORIZONTAL, 1.0);
		maskRef = new RealRangeArray();
		maskRef.add(new RealRange(-1.0, 31.));
		maskRef.add(new RealRange(39., 56.));
		Assert.assertEquals("create mask", maskRef.toString(), mask.toString());
	 }

	@Test
	public void testAngleOfRotation() {
		String character = 
		"<text transform=\"matrix(0.0,-1.0,1.0,0.0,-537.66101,906.323)\" x=\"184.331\" y=\"721.992\" font-size=\"9.0\">b</text>";
		SVGElement text = (SVGText) SVGUtil.parseToSVGElement(character);
		Transform2 t2 = text.getTransform();
		Angle angle = t2.getAngleOfRotation();
		Assert.assertTrue(angle.isEqualTo(new Angle(Math.PI * 0.5)));
		Assert.assertTrue(angle.isEqualTo(Math.PI / 2.0, 0.0001));
	}
	
	@Test
	public void getRotatedElements() {
		String xmlString = "<svg xmlns=\"http://www.w3.org/2000/svg\">"
				+ "<text  x=\"380.168\" y=\"756.731\" font-size=\"7.0\" >X</text>"
				+ "<path d=\"M191.581 732.0 L191.581 76.885\" transform=\"matrix(0.0,-1.0,1.0,0.0,-500.,900.)\"/>"
				+ "<text transform=\"matrix(0.0,-1.0,1.0,0.0,-547.66901,916.33099)\" x=\"184.331\" y=\"732.0\" >P</text>"
				+ "<g transform=\"matrix(0.0,1.0,-1.0,0.0,-500.,900.)\"><rect x1=\"10\" y1=\"50\" width=\"100\" height=\"30\"/></g>"
				+ "</svg>";
		SVGElement svgElement = SVGUtil.parseToSVGElement(xmlString);
		List<SVGElement> rotatedElements = SVGElement.getRotatedDescendantElements(svgElement, new Angle(Math.PI/2.0), 0.001);
		Assert.assertEquals(2,  rotatedElements.size());
		
	}

	@Test
	public void testRemoveLargerObjects() {
		List<SVGElement> elements =  new ArrayList<SVGElement>();
		elements.add(new SVGRect(new Real2(100., 100.), new Real2(200., 350)));
		elements.add(new SVGRect(new Real2(150., 100.), new Real2(200., 350)));
		elements.add(new SVGRect(new Real2(150., 100.), new Real2(200., 10)));
		SVGElement.removeElementsSmallerThanBox(elements, new Real2(10., 10.));
		Assert.assertEquals(3,elements.size());
		SVGElement.removeElementsSmallerThanBox(elements, new Real2(70., 70.));
		Assert.assertEquals(1,elements.size());
		SVGElement.removeElementsSmallerThanBox(elements, new Real2(300.,30.));
		Assert.assertEquals(0,elements.size());
	}
	
	@Test
	public void testAddClass() {
		SVGCircle circle = new SVGCircle();
		String className = circle.getSVGClassNameString();
		Assert.assertNull("class", className);
		List<String> classNames = circle.getSVGClassNames();
		Assert.assertNotNull("classes", classNames);
		Assert.assertEquals("classes", 0, classNames.size());
		circle.addSVGClassName("foo");
		className = circle.getSVGClassNameString();
		Assert.assertNotNull("class", className);
		Assert.assertEquals("class", "foo", className);
		classNames = circle.getSVGClassNames();
		Assert.assertNotNull("classes", classNames);
		Assert.assertEquals("classes", 1, classNames.size());
		// add second argument
		circle.addSVGClassName("bar");
		className = circle.getSVGClassNameString();
		Assert.assertNotNull("class", className);
		Assert.assertEquals("class", "foo bar", className);
		classNames = circle.getSVGClassNames();
		Assert.assertNotNull("classes", classNames);
		Assert.assertEquals("classes", 2, classNames.size());
		Assert.assertTrue("classes", classNames.contains("foo"));
		Assert.assertTrue("classes", classNames.contains("bar"));
		// add duplicate argument
		circle.addSVGClassName("bar");
		className = circle.getSVGClassNameString();
		Assert.assertNotNull("class", className);
		Assert.assertEquals("class", "foo bar", className);
		classNames = circle.getSVGClassNames();
		Assert.assertNotNull("classes", classNames);
		Assert.assertEquals("classes", 2, classNames.size());
		Assert.assertTrue("classes", classNames.contains("foo"));
		Assert.assertTrue("classes", classNames.contains("bar"));
	}
	
	@Test
	/** search for elements with given values in "class" attribute.
	 * 
	 */
	public void testQueryClass() {
		SVGG g = new SVGG();
		SVGCircle circle = new SVGCircle();
		circle.addSVGClassName("foo");
		circle.addSVGClassName("bar");
		g.appendChild(circle);
		SVGRect rect = new SVGRect();
		rect.addSVGClassName("foo");
		rect.addSVGClassName("plugh");
		g.appendChild(rect);
		List<SVGElement> sad = g.querySelfAndDescendantsForClass("bar");
		Assert.assertEquals("bar", 1, sad.size());
		sad = g.querySelfAndDescendantsForClass("foo");
		Assert.assertEquals("foo", 2, sad.size());
	}
	
	@Test
	/** search for elements with given values in "class" attribute.
	 * 
	 */
	public void testQueryClassAmbiguous() {
		SVGG g = new SVGG();
		SVGCircle circle = new SVGCircle();
		circle.addSVGClassName("foo");
		circle.addSVGClassName("bar");
		g.appendChild(circle);
		SVGRect rect = new SVGRect();
		rect.addSVGClassName("foojunk");
		rect.addSVGClassName("plugh");
		g.appendChild(rect);
		SVGEllipse ellipse = new SVGEllipse();
		ellipse.addSVGClassName("foojunk");
		ellipse.addSVGClassName("mybar");
		g.appendChild(ellipse);
		List<SVGElement> sad = g.querySelfAndDescendantsForClass("foo");
		Assert.assertEquals("foo", 1, sad.size());
		sad = g.querySelfAndDescendantsForClass("bar");
		Assert.assertEquals("bar", 1, sad.size());
		sad = g.querySelfAndDescendantsForClass("foojunk");
		Assert.assertEquals("foojunk", 2, sad.size());
	}
}
