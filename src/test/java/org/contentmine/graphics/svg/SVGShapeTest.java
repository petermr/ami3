package org.contentmine.graphics.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.junit.Assert;
import org.junit.Test;



/** tests SVGCircle
 * 
 * @author pm286
 *
 */
public class SVGShapeTest {
private static final Logger LOG = Logger.getLogger(SVGShapeTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double epsilon = 0.01;

	@Test
	public void testIndexOfGeometricalEquivalent() {
		List<SVGShape> shapes = new ArrayList<SVGShape>();
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), 15.);
		shapes.add(circle);
		SVGShape rect = new SVGRect(new Real2(50., 60.), new Real2(80., 100.));
		shapes.add(rect);
		SVGLine line = new SVGLine(new Real2(150., 160.), new Real2(180., 50.));
		shapes.add(line);
		SVGPoly polygon = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		shapes.add(polygon);
		SVGPoly polyline = new SVGPolyline(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		shapes.add(polyline);
		SVGPath path = new SVGPath("M 2 3 L 3 4 L 5 6");
		shapes.add(path);

		Assert.assertEquals(0, SVGShape.indexOfGeometricalEquivalent(shapes, circle, epsilon));
		Assert.assertEquals(0, SVGShape.indexOfGeometricalEquivalent(shapes, new SVGCircle(new Real2(10.005, 20.), 15.005), epsilon));
		Assert.assertEquals(-1, SVGShape.indexOfGeometricalEquivalent(shapes, new SVGCircle(new Real2(10.015, 20.), 15.005), epsilon));
		Assert.assertEquals(1, SVGShape.indexOfGeometricalEquivalent(shapes, rect, epsilon));
		Assert.assertEquals(1, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGRect(new Real2(50.005, 60.), new Real2(80., 100.005)), epsilon));
		Assert.assertEquals(-1, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGRect(new Real2(50.015, 60.), new Real2(80., 100.)), epsilon));
		Assert.assertEquals(2, SVGShape.indexOfGeometricalEquivalent(shapes, line, epsilon));
		Assert.assertEquals(2, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGLine(new Real2(150.005, 160.), new Real2(179.995, 50.005)), epsilon));
		Assert.assertEquals(-1, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGLine(new Real2(150.015, 160.), new Real2(179.985, 50.005)), epsilon));
		
		Assert.assertEquals(3, SVGShape.indexOfGeometricalEquivalent(shapes, polygon, epsilon));
		Assert.assertEquals(3, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGPolygon(new Real2Array(
						new RealArray(new double[]{ 10.005, 20.005, 30.005, 40.005, 50.005, 60.005}),
						new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
						), epsilon));
		Assert.assertEquals(-1, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGPolygon(new Real2Array(
						new RealArray(new double[]{ 10.1, 20.1, 30.1, 40.1, 50.1, 60.1}),
						new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
						), epsilon));
		
		Assert.assertEquals(4, SVGShape.indexOfGeometricalEquivalent(shapes, polyline, epsilon));
		Assert.assertEquals(4, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGPolyline(new Real2Array(
						new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
						new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
						), epsilon));
		Assert.assertEquals(-1, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGPolyline(new Real2Array(
						new RealArray(new double[]{ 10.1, 20.1, 30.1, 40.1, 50.1, 60.1}),
						new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
						), epsilon));
		
		Assert.assertEquals(5, SVGShape.indexOfGeometricalEquivalent(shapes, path, epsilon));
		Assert.assertEquals(5, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGPath("M 2 3 L 3 4 L 5 6"), epsilon));
		Assert.assertEquals(5, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGPath("M 2.005 3 L 3 4 L 5 6"), epsilon));
		Assert.assertEquals(-1, SVGShape.indexOfGeometricalEquivalent(
				shapes, new SVGPath("M 2.1 2.9 L 3 4 L 5 6"), epsilon));
	}
	
	@Test
	public void testIndexesOfGeometricalEquivalent() {
		List<SVGShape> shapes = new ArrayList<SVGShape>();
		SVGCircle circle = new SVGCircle(new Real2(10., 20.), 15.);
		shapes.add(circle);
		SVGShape rect = new SVGRect(new Real2(50., 60.), new Real2(80., 100.));
		shapes.add(rect);
		SVGLine line = new SVGLine(new Real2(150., 160.), new Real2(180., 50.));
		shapes.add(line);
		rect = new SVGRect(new Real2(50.005, 60.), new Real2(80.005, 100.));
		shapes.add(rect);
		line = new SVGLine(new Real2(150.005, 160.), new Real2(180., 50.));
		shapes.add(line);
		SVGPoly polygon = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		shapes.add(polygon);
		SVGPoly polyline = new SVGPolyline(new Real2Array(
				new RealArray(new double[]{ 10., 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120., 130., 140., 150., 160.}))
				);
		shapes.add(polyline);
		SVGPath path = new SVGPath("M 2 3 L 3 4 L 5 6");
		shapes.add(path);
		SVGPoly polygon1 = new SVGPolygon(new Real2Array(
				new RealArray(new double[]{ 10.005, 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120.005, 130., 140., 150., 160.}))
				);
		shapes.add(polygon1);
		SVGPoly polyline1 = new SVGPolyline(new Real2Array(
				new RealArray(new double[]{ 10.005, 20., 30., 40., 50., 60.}),
				new RealArray(new double[]{ 110., 120.005, 130., 140., 150., 160.}))
				);
		shapes.add(polyline1);
		SVGPath path1 = new SVGPath("M 2.005 3 L 3 4 L 5.005 6");
		shapes.add(path1);

		IntArray indexes = SVGShape.indexesOfGeometricalEquivalent(shapes, new SVGCircle(new Real2(10., 20.), 15.), epsilon);
		Assert.assertEquals("(0)", indexes.toString());
		Assert.assertEquals("(0)", SVGShape.indexesOfGeometricalEquivalent(shapes, new SVGCircle(new Real2(10.005, 20.), 15.005), epsilon).toString());
		Assert.assertEquals("()", SVGShape.indexesOfGeometricalEquivalent(shapes, new SVGCircle(new Real2(10.015, 20.), 15.005), epsilon).toString());
		Assert.assertEquals("(1,3)", SVGShape.indexesOfGeometricalEquivalent(shapes, rect, epsilon).toString());
		// both rects match
		Assert.assertEquals("(1,3)", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGRect(new Real2(50.005, 60.), new Real2(80., 100.005)), epsilon).toString());
		// only one matches
		Assert.assertEquals("(1)", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGRect(new Real2(49.995, 60.), new Real2(80., 100.005)), epsilon).toString());
		// the other matches
		Assert.assertEquals("(3)", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGRect(new Real2(50.015, 60.), new Real2(80., 100.)), epsilon).toString());
		Assert.assertEquals("(2,4)", SVGShape.indexesOfGeometricalEquivalent(shapes, line, epsilon).toString());
		Assert.assertEquals("(2)", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGLine(new Real2(149.993, 160.0), new Real2(179.995, 50.005)), epsilon).toString());
		// neither matches
		Assert.assertEquals("()", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGLine(new Real2(150.015, 160.), new Real2(179.985, 50.005)), epsilon).toString());
		
		Assert.assertEquals("(5,8)", SVGShape.indexesOfGeometricalEquivalent(shapes, polygon, epsilon).toString());
		Assert.assertEquals("(8)", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGPolygon(new Real2Array(
						new RealArray(new double[]{ 10.005, 20., 30., 40., 50., 60.}),
						new RealArray(new double[]{ 110., 120.005, 130., 140., 150., 160.})
						))
						, 0.003).toString());
		// both match
		Assert.assertEquals("(6,9)", SVGShape.indexesOfGeometricalEquivalent(shapes, polyline, epsilon).toString());
		// one match
		Assert.assertEquals("(9)", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGPolyline(new Real2Array(
						new RealArray(new double[]{ 10.005, 20., 30., 40., 50., 60.}),
						new RealArray(new double[]{ 110., 120.005, 130., 140., 150., 160.})
						))
						, 0.003).toString());
		// no match
		Assert.assertEquals("()", SVGShape.indexesOfGeometricalEquivalent(
				shapes, new SVGPolyline(new Real2Array(
						new RealArray(new double[]{ 10.005, 20.1, 30., 40., 50., 60.}),
						new RealArray(new double[]{ 110., 120.005, 130., 140., 150., 160.})
						))
						, 0.003).toString());

	}

	/** eliminate duplicates from a list, using tolerance
	 */
	@Test
	public void testEliminateGeometricalDuplicates() {
		List<SVGShape> shapes = new ArrayList<SVGShape>();
		shapes.add(new SVGCircle(new Real2(10.0, 20.0), 15.0));
		shapes.add(new SVGCircle(new Real2(10.005, 20.005), 14.995));
		Assert.assertEquals(2,  shapes.size());
		SVGShape.eliminateGeometricalDuplicates(shapes, 0.01);
		Assert.assertEquals(1,  shapes.size());
	}

	

}
