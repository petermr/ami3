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
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Test;

public class SVGPolygonTest {

	private static final Logger LOG = Logger.getLogger(SVGPolygonTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String ARROWHEAD_S = "<polygon  points=\"178.9 130.294 175.648 122.336 178.9 124.225 182.152 122.336\"/>";
	public final static SVGPolygon ARROWHEAD;
	static {
		ARROWHEAD = (SVGPolygon) SVGElement.readAndCreateSVG(XMLUtil.parseXML(ARROWHEAD_S));
		ARROWHEAD.getReal2Array();
	}
	private double epsilon = 0.01;

	@Test
	public void testIsMirror() {
		Assert.assertFalse(ARROWHEAD.hasMirror(0, SVGLine.EPS));
		Assert.assertTrue(ARROWHEAD.hasMirror(1, SVGLine.EPS));
	}
	
	@Test
	public void testCreatePolygon() {
		SVGPolygon polygon = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		polygon.setFill("none");
		polygon.setStroke("black");
		polygon.setStrokeWidth(1.0);
		Assert.assertEquals("<polygon xmlns=\"http://www.w3.org/2000/svg\" "
				+ "points=\"10.0 110.0 20.0 120.0 30.0 130.0 40.0 140.0 50.0 150.0 60.0 160.0\""
				+ " style=\"fill:none;stroke:black;stroke-width:1.0;\" />", polygon.toXML());
		Assert.assertTrue(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.})
				).isEqualTo(polygon.getReal2Array(), epsilon));
		Assert.assertTrue(new Real2Array(
				new RealArray(new double[]{ 10.005, 20.005, 30.005, 40.005, 50.005, 60.005}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.})
				).isEqualTo(polygon.getReal2Array(), epsilon));
		Real2Range bbox = polygon.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new RealRange(10., 60.), new RealRange(110., 160.)), epsilon));
	}
	
	@Test
	public void testCreatePolygonFromLines() {
		List<SVGLine> lines = new ArrayList<SVGLine>();
		lines.add(new SVGLine(new Real2(10., 110.), new Real2(20., 120.)));
		lines.add(new SVGLine(new Real2(20., 120.), new Real2(30., 130.)));
		lines.add(new SVGLine(new Real2(30., 130.), new Real2(40., 140.)));
		lines.add(new SVGLine(new Real2(40., 140.), new Real2(50., 150.)));
		lines.add(new SVGLine(new Real2(50., 150.), new Real2(60., 160.)));
		lines.add(new SVGLine(new Real2(60., 160.), new Real2(10., 110.)));
		SVGPolygon polygon = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		polygon.setFill("none");
		polygon.setStroke("black");
		polygon.setStrokeWidth(1.0);
		Assert.assertEquals("<polygon xmlns=\"http://www.w3.org/2000/svg\" "
				+ "points=\"10.0 110.0 20.0 120.0 30.0 130.0 40.0 140.0 50.0 150.0 60.0 160.0\""
				+ " style=\"fill:none;stroke:black;stroke-width:1.0;\" />", polygon.toXML());
		Assert.assertTrue(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.})
				).isEqualTo(polygon.getReal2Array(), epsilon));
		Assert.assertTrue(new Real2Array(
				new RealArray(new double[]{ 10.005, 20.005, 30.005, 40.005, 50.005, 60.005}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.})
				).isEqualTo(polygon.getReal2Array(), epsilon));
		Real2Range bbox = polygon.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new RealRange(10., 60.), new RealRange(110., 160.)), epsilon));
	}
	
	@Test
	public void testIsGeometricallyEqualTo() {
		SVGPoly polygon = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		SVGPoly polygon1 = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10.005, 20.005, 30.005, 40.005, 50.005, 60.005}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		SVGPoly polygon2 = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10.01, 20.01, 30.01, 40.01, 50.01, 60.01}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		Assert.assertTrue(polygon.isGeometricallyEqualTo(
				polygon1, epsilon));
		Assert.assertTrue(polygon2.isGeometricallyEqualTo(polygon1, 0.006));
		Assert.assertFalse(polygon2.isGeometricallyEqualTo(polygon, 0.006));
	}


}
