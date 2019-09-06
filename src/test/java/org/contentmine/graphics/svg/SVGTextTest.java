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

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.testutil.TestUtils;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Element;

public class SVGTextTest {
	private static Logger LOG = Logger.getLogger(SVGTextTest.class);

	static String STRING1 ="<text x='0' y='0' transform='translate(3,335.28) scale(1.0001,-0.99988) '" +
		" style='font-size:6.2023;stroke:none;fill:black;'" +
		">ppm</text>";
	
	@Test
	@Ignore
	public void testSetup() {
		Element element = XMLUtil.parseXML(STRING1);
		GraphicsElement text = SVGElement.readAndCreateSVG(element);
		Assert.assertNotNull(text);
		Assert.assertEquals("class", SVGText.class, text.getClass());
		Assert.assertEquals("fontsize", 6.2023, text.getFontSize(), 0.0001);
		Assert.assertEquals("stroke", "none", text.getStroke());
		Assert.assertEquals("fill", "black", text.getFill());
		Assert.assertEquals("transform", "translate(3,335.28) scale(1.0001,-0.99988)",
				text.getAttributeValue("transform").trim());
	}

	@Test
	@Ignore
	public void testApplyTransform() {
		Element element = XMLUtil.parseXML(STRING1);
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(element);
		text.applyTransformAttributeAndRemove();
		String expectedS = "<text " +
				"style='font-size:6.2023;stroke:none;fill:black;'" +
				" x='3.0' y='335.28' improper='true'" +
				" xmlns='http://www.w3.org/2000/svg'>ppm</text>";
		Element expected = XMLUtil.parseXML(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
	}

	@Test
	@Ignore
	public void testFormat() {
		Element element = XMLUtil.parseXML(STRING1);
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(element);
		text.applyTransformAttributeAndRemove();
		String expectedS = "<text " +
				"style='font-size:6.2023;stroke:none;fill:black;'" +
				" x='3.0' y='335.28' improper='true'" +
				" xmlns='http://www.w3.org/2000/svg'>ppm</text>";
		Element expected = XMLUtil.parseXML(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
		text.format(1);
		expectedS = "<text " +
		"style='font-size:6.2023;stroke:none;fill:black;'" +
		" x='3.0' y='335.3' improper='true'" +
		" xmlns='http://www.w3.org/2000/svg'>ppm</text>";
		expected = XMLUtil.parseXML(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
	}

	@Test
	public void testGetXandY() {
		Element element = XMLUtil.parseXML(STRING1);
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(element);
		text.applyTransformAttributeAndRemove();
		Assert.assertEquals("x", 3.0, text.getX(), 0.01);
		Assert.assertEquals("y", 335.28, text.getY(), 0.01);
	}

	@Test
	public void testGetBoundingBox() {
		if (1 == 1) {
			LOG.error("FIXME");
			return;
		}
		Element element = XMLUtil.parseXML(STRING1);
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(element);
		text.applyTransformAttributeAndRemove();
		Real2Range bb = text.getBoundingBox();
		LOG.trace(bb);
		Real2Range bbexpect = new Real2Range(new Real2(3.0, 335.28), new Real2(3.0, 335.28));
		Assert.assertNotNull(bb);
		//Assert.assertTrue("bb", bbexpect.isEqualTo(bb, 0.01));
	}

	@Test
	@Ignore
	public void testSVGTextReal2String() {
		SVGText text = new SVGText(new Real2(1., 2.), "string");
		String expectedS = "<text style=' stroke : none; font-size : 7.654321;' " +
				"x='1.0' y='2.0' xmlns='http://www.w3.org/2000/svg'>string</text>";
		Element expected = XMLUtil.parseXML(expectedS);
		TestUtils.assertEqualsIncludingFloat("transform", expected, text, true, 0.001);
	}

	@Test
	@Ignore
	public void testGetEstimatedHorizontalLength() {
		String test1S = "<text style=' stroke : none; font-size : 7.654321;' " +
		"x='1.0' y='2.0' xmlns='http://www.w3.org/2000/svg'>string</text>";
		SVGText text1 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(test1S));
		Assert.assertEquals("font", 7.654321, text1.getFontSize(), 0.001);
		double fontWidthFactor = 1.1;
		double length = text1.getEstimatedHorizontalLength(fontWidthFactor);
		Assert.assertEquals("length", 23.4, length, 0.1);
		
		fontWidthFactor = 1.0;
		String s = "" +
				"<svg>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"3.0\" y=\"335.28\">ppm</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"14.76\" y=\"335.28\"> (f</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"19.92\" y=\"335.28\">1)</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"370.86\" y=\"342.36\">1.</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"376.08\" y=\"342.36\">0</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"316.08\" y=\"342.36\">2.</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"321.24\" y=\"342.36\">0</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"261.24\" y=\"342.36\">3.</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"266.4\" y=\"342.36\">0</text>" +
				"</svg>";
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(XMLUtil.parseXML(s));
		List<SVGElement> elementList = SVGElement.generateElementList(svg, "./svg:text");
		Assert.assertEquals("texts", 9, elementList.size());
		double[] lengths = new double[elementList.size()];
		for (int i = 0; i < elementList.size(); i++) {
			SVGText text = (SVGText) elementList.get(i);
			lengths[i] = text.getEstimatedHorizontalLength(fontWidthFactor);
			//LOG.trace("        "+Util.format(lengths[i], 1)+",");
		}
		/*for (int i = 1; i < elementList.size(); i++) {
			SVGText text0 = (SVGText) elementList.get(i-1);
			SVGText text = (SVGText) elementList.get(i);
			//LOG.trace("-----------------------");
			length[i-1] = text0.getEstimatedHorizontalLength(fontWidthFactor);
			double dist = text.getX()-text0.getX();
			//LOG.trace(String.valueOf(Util.format(text0.getY(), 2)) +" "+Util.format(text.getY(), 2)+"["+text0.getValue()+"] "+Util.format(dist, 1)+" "+length[i-1]+" "+dist/length[i-1]);
		}*/
		
		double[] expectedLength = new double[]{
		        13.1,
		        5.8,
		        5.5,
		        5.2,
		        3.5,
		        5.2,
		        3.5,
		        5.2,
		        3.5,
	        };
		String msg = TestUtils.testEquals("lengths", expectedLength, lengths, 0.1);
		if (msg != null) {
			Assert.fail(msg);
		}
	}

	@Test
	@Ignore
	public void testGetCalculatedTextEnd() {
		double fontWidthFactor = 1.05;
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML("<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		Real2 textEnd = text.getCalculatedTextEnd(fontWidthFactor);
		Assert.assertTrue("text end", new Real2(16.74119565,30.0).isEqualTo(textEnd, 0.1));
		
		text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
			"<text rotate='"+SVGElement.YPLUS+"' " +
			"style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		textEnd = text.getCalculatedTextEnd(fontWidthFactor);
		Assert.assertEquals("text end", 16.26, textEnd.getY(), 0.1);
	}

	@Test
	@Ignore
	public void testGetCalculatedTextEndCoordinate() {
		double fontWidthFactor = 1.05;
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML("<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		double textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 16.74119565, textEndCoord, 0.1);
		
		text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
			"<text rotate='"+SVGElement.YPLUS+"' " +
			"style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 16.26, textEndCoord, 0.1);
		
		text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
			"<text rotate='"+SVGElement.YMINUS+"' " +
			"style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 43.74, textEndCoord, 0.1);
	}

	@Test
	@Ignore
	public void testSetCalculatedTextEndCoordinate() {
		double fontWidthFactor = 1.05;
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML("<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		double textEndCoord = text.getCalculatedTextEndCoordinate(fontWidthFactor);
		Assert.assertEquals("text end", 16.74119565, textEndCoord, 0.1);
		text.setCalculatedTextEndCoordinate(10.0);
		Assert.assertEquals("text end", 10., text.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
	}

	@Test
	@Ignore
	public void testGetCurrentFontSize() {
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
			"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		Assert.assertEquals("font size", 6.20, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 6.20, text.getCurrentFontSize(), 0.1);
		text.setFontSize(10.0);
		Assert.assertEquals("font size", 10.0, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 6.20, text.getCurrentFontSize(), 0.1);
		text.setCurrentFontSize(5.0);
		Assert.assertEquals("font size", 10.0, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 5.0, text.getCurrentFontSize(), 0.1);
		text.setFontSize(15.0);
		Assert.assertEquals("font size", 15.0, text.getFontSize(), 0.1);
		Assert.assertEquals("current font size", 5.0, text.getCurrentFontSize(), 0.1);
	}

	@Test
	public void testGetCurrentBaseY() {
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
			Assert.assertEquals("baseY", 30., text.getCurrentBaseY(), 0.1);
			text.setCurrentBaseY(10.0);
			Assert.assertEquals("baseY", 10., text.getCurrentBaseY(), 0.1);
			text.setXY(new Real2(5., 15.));
			Assert.assertEquals("baseY", 10., text.getCurrentBaseY(), 0.1);
			text.setRotate(SVGElement.YPLUS);
			Assert.assertEquals("baseY", 5., text.getCurrentBaseY(), 0.1);
			text.setCurrentBaseY(20.0);
			Assert.assertEquals("baseY", 20., text.getCurrentBaseY(), 0.1);
	}

	@Test
	public void testGetSetRotate() {
		SVGText text = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"3.0\" y=\"30.\">ppm</text>"));
		Assert.assertNull("rotate", text.getRotate());
		text.setRotate(SVGElement.YPLUS);
		Assert.assertEquals("rotate", SVGElement.YPLUS, text.getRotate());
	}

	@Test
	@Ignore
	public void testConcatenateText() {
		double fontWidthFactor = 1.0;
		double fontHeightFactor = 1.0;
		SVGText text0 = testConcatenate(fontWidthFactor, fontHeightFactor, "<svg>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"3.0\" y=\"335.28\">ppm</text>" +
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"14.76\" y=\"335.28\"> (f</text>" +
				"</svg>", true, 20.59, "ppm (f");
		SVGText text1 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"19.92\" y=\"335.28\">1)</text>"));
		SVGText text2 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 25.44, "ppm (f1)", text0, text1);

		SVGText text3 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"370.86\" y=\"342.36\">1.</text>"));
		SVGText text4 = testConcatenate(fontWidthFactor, fontHeightFactor, false, 25.44, null, text2, text3);
		
		SVGText text5 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
			"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
			"improper=\"true\" x=\"376.08\" y=\"342.36\">0</text>"));
		SVGText text6 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 379.55, "1.0", text3, text5);
		
		SVGText text7 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
		"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"316.08\" y=\"342.36\">2.</text>"));
		SVGText text8 = testConcatenate(fontWidthFactor, fontHeightFactor, false, 25.44, null, text6, text7);
		
		SVGText text9 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
		"<text style=\"font-family:'Helvetica',sans-serif;font-size:6.2023;stroke:none;fill:black;\" " +
		"improper=\"true\" x=\"321.24\" y=\"342.36\">0</text>"));

		SVGText text10 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 324.71, "2.0", text7, text9);
		
	}

	/**
	 * @param fontWidthFactor
	 * @param fontHeightFactor
	 * @param s
	 */
	private SVGText testConcatenate(double fontWidthFactor,
			double fontHeightFactor, String s, boolean mergedExpected, double endExpected, String textExpected) {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(XMLUtil.parseXML(s));
		List<SVGElement> elementList = SVGElement.generateElementList(svg, "./svg:text");
		Assert.assertEquals("texts", 2, elementList.size());
		SVGText text0 = ((SVGText)elementList.get(0));
		SVGText text1 = ((SVGText)elementList.get(1));
		SVGText text2 = testConcatenate(fontWidthFactor, fontHeightFactor,
				mergedExpected, endExpected, textExpected, text0, text1);
		return text2;
	}

	/**
	 * @param fontWidthFactor
	 * @param fontHeightFactor
	 * @param mergedExpected
	 * @param endExpected
	 * @param textExpected
	 * @param text0
	 * @param text1
	 * @return
	 */
	private SVGText testConcatenate(double fontWidthFactor,
			double fontHeightFactor, boolean mergedExpected,
			double endExpected, String textExpected, SVGText text0,
			SVGText text1) {
		boolean merged = text0.concatenateText(fontWidthFactor, fontHeightFactor, text1, 0.5, -0.5, 0.1);
		Assert.assertTrue("merged", merged == mergedExpected);
		if (merged) {
			String newText = text0.getValue();
			Assert.assertEquals("text", textExpected, newText);
			double end = text0.getCalculatedTextEndCoordinate(fontWidthFactor);
			Assert.assertEquals("extent", endExpected, end, 0.1);
		}
		return text0;
	}
	
	@Test
	@Ignore
	public void testConcatenate2() {
		double fontWidthFactor = 1.0;
		double fontHeightFactor = 1.0;
		/*
		<g class="peak">
		  <line style="stroke-width:0.131;stroke-linecap:round;" x1="89.22" y1="121.98" x2="89.22" y2="125.88" /> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"306.72\">17</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"300.54\">8</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"297.54\">.</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"295.74\">616</text> 
		  <line style="stroke-width:0.131;stroke-linecap:round;" x1="89.22" y1="277.86" x2="89.22" y2="304.14" /> 
		</g>
		*/
		SVGText text0 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"306.72\">17</text>"));
		Assert.assertEquals("text0", 300.25, text0.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
		
		SVGText text1 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"300.54\">8</text> "));
		Assert.assertEquals("text1", 297.30, text1.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
		SVGText text01 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 297.30, "178", text0, text1);
		
		SVGText text2 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"297.54\">.</text> "));
		SVGText text02 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 295.92, "178.", text01, text2);
		
		SVGText text3 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:5.7793;stroke:none;fill:black;\" " +
				"improper=\"true\" rotate=\"Y\" x=\"80.16\" y=\"295.74\">616</text>"));
		SVGText text03 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 286.03, "178.616", text01, text3);
	}
	
	@Test
	@Ignore
	public void testConcatenate3() {
		double fontWidthFactor = 1.0;
		double fontHeightFactor = 1.0;
		/*
		<g>
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" improper=\"true\" x=\"24.36\" y=\"303.36\">7</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" improper=\"true\" x=\"28.68\" y=\"303.36\">5</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" improper=\"true\" x=\"32.94\" y=\"303.36\"> M</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" improper=\"true\" x=\"41.47\" y=\"303.36\">H</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" improper=\"true\" x=\"46.99\" y=\"303.36\">z,</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" improper=\"true\" x=\"55.11\" y=\"303.36\">CD</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" improper=\"true\" x=\"66.08\" y=\"303.36\">Cl</text> 
		  <text style=\"font-family:'Helvetica',sans-serif;font-size:5.7091;stroke:none;fill:black;\" improper=\"true\" x=\"73.26\" y=\"301.68\">3</text> 
		</g>
		*/
		SVGText text0 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				" <text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"24.36\" y=\"303.36\">7</text> "));
		Assert.assertEquals("text0", 28.69, text0.getCalculatedTextEndCoordinate(fontWidthFactor), 0.1);
		
		SVGText text1 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"28.68\" y=\"303.36\">5</text> "));
		SVGText text01 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 33.01, "75", text0, text1);
		
		SVGText text2 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"32.94\" y=\"303.36\"> M</text> "));
		SVGText text02 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 41.52, "75 M", text01, text2);
		
		SVGText text3 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"41.47\" y=\"303.36\">H</text>"));
		SVGText text03 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 47.04, "75 MH", text01, text3);
		
		SVGText text4 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"46.99\" y=\"303.36\">z,</text>"));
		SVGText text04 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 53.02, "75 MHz,", text01, text4);
		
		SVGText text5 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"55.11\" y=\"303.36\">CD</text>"));
		SVGText text05 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 66.25, "75 MHz, CD", text01, text5);
		
		SVGText text6 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:7.7348;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"66.08\" y=\"303.36\">Cl</text>"));
		SVGText text06 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 73.81, "75 MHz, CDCl", text01, text6);
		
		SVGText text7 = (SVGText) SVGElement.readAndCreateSVG(XMLUtil.parseXML(
				"<text style=\"font-family:'Helvetica',sans-serif;font-size:5.7091;stroke:none;fill:black;\" " +
				"improper=\"true\" x=\"73.26\" y=\"301.68\">3</text>"));
		SVGText text07 = testConcatenate(fontWidthFactor, fontHeightFactor, true, 76.45, "75 MHz, CDCl_{3", text01, text7);
	}

	@Test
	public void textDisplay() {
		Real2 shift = new Real2(100., 200.);
		SVGSVG svg = new SVGSVG();
		SVGText text = new SVGText(shift, ".__zero");
		text.setStrokeWidth(0.3);
		text.setStroke("red");
		text.setFill("yellow");
		text.setFontSize(20.);
		svg.appendChild(text.copy());
		text.setText(".__pi4");
		text.rotateText(new Angle(Math.PI / 4));
		LOG.trace("Transform: "+text.toXML());
		svg.appendChild(text.copy());
		text.setText(".__pi2");
		text.rotateText(new Angle(Math.PI / 2));
		LOG.trace("Transform: "+text.toXML());
		svg.appendChild(text.copy());
		text.setText(".__3pi4");
		text.rotateText(new Angle(3 * Math.PI / 4));
		LOG.trace("Transform: "+text.toXML());
		svg.appendChild(text.copy());
		text.setText(".__pi");
		text.rotateText(new Angle(Math.PI));
		LOG.trace("Transform: "+text.toXML());
		svg.appendChild(text.copy());
		File textDir = new File("target/text/");
		textDir.mkdirs();
		XMLUtil.outputQuietly(svg, new File(textDir, "text3.svg"), 1);
	}
	
	@Test
	public void testRemoveCharacter() {
		String textS = "abc";
		RealArray xArray = new RealArray(new double[]{1.0, 2.0, 3.0});
		SVGText text = new SVGText();
		text.setText(textS);
		text.setX(xArray);
		LOG.debug(text.toXML());
		text.removeCharacter(1);
		Assert.assertEquals("deleted 1", "<text xmlns=\"http://www.w3.org/2000/svg\" x=\"1.0,3.0\">ac</text>", text.toXML());
		LOG.debug(text.toXML());
		
	}
	
}
