package org.contentmine.graphics.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.junit.Assert;
import org.junit.Test;

public class SVGLineTest {
	
	
	private static final Logger LOG = Logger.getLogger(SVGLineTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private double epsilon = 0.01;

	private final static Double FP_EPS = 0.000001;
	private final static Angle ANGLE_EPS = new Angle(0.000001, Angle.Units.RADIANS);
	
	@Test
	public void testPerpendicular() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,1));
		SVGLine line1 = new SVGLine(new Real2(1,0), new Real2(0,0));
		Assert.assertTrue("perp", line0.isPerpendicularTo(line1, 0.00001));
	}

	@Test
	public void testPerpendicular1() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,-1));
		SVGLine line1 = new SVGLine(new Real2(1,0), new Real2(0,0));
		Assert.assertTrue("perp", line0.isPerpendicularTo(line1, 0.00001));
	}
	
	@Test
	public void testPerpendicular2() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,-1));
		SVGLine line1 = new SVGLine(new Real2(1,0.1), new Real2(0,0));
		Assert.assertFalse("perp", line0.isPerpendicularTo(line1, 0.00001));
	}
	
	@Test
	public void testParallel0() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,1));
		SVGLine line1 = new SVGLine(new Real2(0,1), new Real2(0,2));
		Assert.assertTrue("para", line0.isParallelTo(line1, ANGLE_EPS));
	}

	@Test
	public void testParallel() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,1));
		SVGLine line1 = new SVGLine(new Real2(1,0), new Real2(1,1));
		Assert.assertTrue("para", line0.isParallelTo(line1, ANGLE_EPS));
	}

	@Test
	public void testParallel1() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,-1));
		SVGLine line1 = new SVGLine(new Real2(1,0), new Real2(1,-1));
		Assert.assertTrue("para", line0.isParallelTo(line1, ANGLE_EPS));
	}
	
	@Test
	public void testParallel2() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,-1));
		SVGLine line1 = new SVGLine(new Real2(1,0.1), new Real2(0,0));
		Assert.assertFalse("perp", line0.isParallelTo(line1, ANGLE_EPS));
	}
	
	@Test
	public void testParallel3() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,-1));
		SVGLine line1 = new SVGLine(new Real2(0,0), new Real2(0,1));
		Assert.assertFalse("perp", line0.isParallelTo(line1, ANGLE_EPS));
	}
	
	@Test
	public void testOverlap() {
		SVGLine line0 = new SVGLine(new Real2(0,0), new Real2(0,1));
		SVGLine line1 = new SVGLine(new Real2(0.5, 0.5), new Real2(0.5,1.5));
		Assert.assertTrue(line0.overlapsWithLine(line1, FP_EPS));
		Assert.assertTrue(line1.overlapsWithLine(line0, FP_EPS));
		SVGLine line2 = new SVGLine(new Real2(0.5, 0.25), new Real2(0.5, 0.75));
		Assert.assertFalse(line0.overlapsWithLine(line2, FP_EPS));
		Assert.assertTrue(line2.overlapsWithLine(line0, FP_EPS));
		SVGLine line3 = new SVGLine(new Real2(0.5, 1.25), new Real2(0.5, 2.0));
		Assert.assertFalse(line0.overlapsWithLine(line3, FP_EPS));
		Assert.assertFalse(line3.overlapsWithLine(line0, FP_EPS));
		
	}
	
	@Test
	public void testExtractAndRemoveHorizontalVerticalLines() {
		double eps = 0.5;
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		lineList.add(new SVGLine(new Real2(0, 0), new Real2(0, 1)));
		lineList.add(new SVGLine(new Real2(0, 1), new Real2(0, 2)));
		lineList.add(new SVGLine(new Real2(0, 0), new Real2(1, 0)));
		lineList.add(new SVGLine(new Real2(1, 0), new Real2(2, 0)));
		lineList.add(new SVGLine(new Real2(0, 1), new Real2(1, 0)));
		lineList.add(new SVGLine(new Real2(2, 1), new Real2(1, 2)));
		List<SVGLine> horizontalList = SVGLine.extractAndRemoveHorizontalVerticalLines(
				lineList, eps, LineDirection.HORIZONTAL);
		Assert.assertEquals("horizontal", 2, horizontalList.size());
		List<SVGLine> verticalList = SVGLine.extractAndRemoveHorizontalVerticalLines(
				lineList, eps, LineDirection.VERTICAL);
		Assert.assertEquals("vertical", 2, verticalList.size());
		Assert.assertEquals("non-axial", 2, lineList.size());
	}
	

	@Test
	public void testNormalizeAndMergeAxialLines() {
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		lineList.add(new SVGLine(new Real2(0, 0), new Real2(0, 1)));
		lineList.add(new SVGLine(new Real2(0, 1), new Real2(0, 2)));
		lineList.add(new SVGLine(new Real2(0, 0), new Real2(1, 0)));
		lineList.add(new SVGLine(new Real2(1, 0), new Real2(2, 0)));
		lineList.add(new SVGLine(new Real2(0, 1), new Real2(1, 0)));
		lineList.add(new SVGLine(new Real2(2, 1), new Real2(1, 2)));
		SVGLine.normalizeAndMergeAxialLines	(lineList, 0.5);
		Assert.assertEquals("merged line", 4, lineList.size());
		for (SVGElement line : lineList) {
			LOG.trace(line);
		}
	}
	
	@Test
	public void testCreateMergedHorizontalOrVerticalLine() {
		// overlap
		SVGLine line0 = new SVGLine(new Real2(36.9, 100.0), new Real2(173.9, 100.0));
		SVGLine line1 = new SVGLine(new Real2(173.9, 100.0), new Real2(335.1, 100.0));
		SVGElement line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertEquals("exact x"+line.toXML(), "<line xmlns=\"http://www.w3.org/2000/svg\" "
				+ "x1=\"36.9\" y1=\"100.0\" x2=\"335.1\" y2=\"100.0\" />", line.toXML());
		line0 = new SVGLine(new Real2(100.0, 36.9), new Real2(100.0, 173.9));
		line1 = new SVGLine(new Real2(100.0, 173.9), new Real2(100.0, 335.1));
		line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertEquals("exact y "+line.toXML(), "<line xmlns=\"http://www.w3.org/2000/svg\" "
				+ "x1=\"100.0\" y1=\"36.9\" x2=\"100.0\" y2=\"335.1\" />", line.toXML());
		line0 = new SVGLine(new Real2(36.9, 100.0), new Real2(173.9, 100.0));
		line1 = new SVGLine(new Real2(167.1, 100.0), new Real2(335.1, 100.0));
		line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertEquals("overlap x"+line.toXML(), "<line xmlns=\"http://www.w3.org/2000/svg\" "
				+ "x1=\"36.9\" y1=\"100.0\" x2=\"335.1\" y2=\"100.0\" />", line.toXML());
		line0 = new SVGLine(new Real2(100.0, 36.9), new Real2(100.0, 173.9));
		line1 = new SVGLine(new Real2(100.0, 167.1), new Real2(100.0, 335.1));
		line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertEquals("overlap y"+line.toXML(), "<line xmlns=\"http://www.w3.org/2000/svg\" "
				+ "x1=\"100.0\" y1=\"36.9\" x2=\"100.0\" y2=\"335.1\" />", line.toXML());
	}
	
	@Test
	public void testNonMergedHorizontalOrVerticalLine() {
		// overlap
		SVGLine line0 = new SVGLine(new Real2(100.0, 36.9), new Real2(100.0, 173.9));
		SVGLine line1 = new SVGLine(new Real2(101.0, 173.9), new Real2(101.0, 335.1));
		SVGElement line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertNull("different x", line);
		line0 = new SVGLine(new Real2(36.9, 100.0), new Real2(173.9, 100.0));
		line1 = new SVGLine(new Real2(173.9, 101.0), new Real2(335.1, 101.0));
		line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertNull("different y", line);
		line1 = new SVGLine(new Real2(175.0, 100.0), new Real2(335.1, 100.0));
		line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertNull("gap x", line);
		line0 = new SVGLine(new Real2(100.0, 36.9), new Real2(100.0, 173.9));
		line1 = new SVGLine(new Real2(100.0, 175.0), new Real2(100.0, 335.1));
		line = SVGLine.createMergedHorizontalOrVerticalLine(line0, line1, 0.5);
		Assert.assertNull("gap y", line);
	}
	
	@Test
	public void testMergeParallelLines() {
		SVGLine line0 = new SVGLine(new Real2(36.9, 100.0), new Real2(173.9, 100.0));
		SVGLine line1 = new SVGLine(new Real2(173.9, 100.0), new Real2(335.1, 100.0));
		SVGLine line2 = new SVGLine(new Real2(335.1, 100.0), new Real2(503.1, 100.0));
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		lineList.add(line0);
		lineList.add(line1);
		lineList.add(line2);
		List<SVGLine> newLineList = SVGLine.mergeParallelLines(lineList, 0.1);
		Assert.assertEquals(1, newLineList.size());
		SVGElement newLine = newLineList.get(0);
		Assert.assertEquals("merged",  
				"<line xmlns=\"http://www.w3.org/2000/svg\" x1=\"36.9\" y1=\"100.0\" x2=\"503.1\" y2=\"100.0\" />",
				newLine.toXML());
	}
	
	@Test
	public void testMergeParallelLinesWithOverlap() {
		SVGLine line0 = new SVGLine(new Real2(36.9, 100.0), new Real2(173.9, 100.0));
		SVGLine line1 = new SVGLine(new Real2(167, 100.0), new Real2(335.1, 100.0));
		SVGLine line2 = new SVGLine(new Real2(328.1, 100.0), new Real2(503.1, 100.0));
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		lineList.add(line0);
		lineList.add(line1);
		lineList.add(line2);
		List<SVGLine> newLineList = SVGLine.mergeParallelLines(lineList, 0.1);
		Assert.assertEquals(1, newLineList.size());
		SVGElement newLine = newLineList.get(0);
		Assert.assertEquals("merged",  
				"<line xmlns=\"http://www.w3.org/2000/svg\" x1=\"36.9\" y1=\"100.0\" x2=\"503.1\" y2=\"100.0\" />",
				newLine.toXML());
	}
	
	@Test
	public void testCreatePolygon() {
		double rad = 15.;
		SVGLine line = new SVGLine(new Real2(10., 20.), new Real2(25, 45));
		line.setFill("none");
		line.setStroke("black");
		line.setStrokeWidth(1.0);
		Assert.assertEquals("<line xmlns=\"http://www.w3.org/2000/svg\" x1=\"10.0\" y1=\"20.0\" x2=\"25.0\" y2=\"45.0\""
				+ " style=\"fill:none;stroke:black;stroke-width:1.0;\" />", line.toXML());
		Assert.assertTrue(new Real2(10., 20.).isEqualTo(line.getXY(0), epsilon));
		Assert.assertTrue(new Real2(10., 20.).isEqualTo(line.getXY(0), epsilon));
		Assert.assertTrue(new Real2(25., 45.).isEqualTo(line.getXY(1), epsilon));
		Real2Range bbox = line.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new RealRange(10., 25.), new RealRange(20., 45.)), epsilon));
	}
	
	@Test
	public void testCreateLine() {
		double rad = 15.;
		SVGLine line = new SVGLine(new Real2(10., 20.), new Real2(25, 45));
		line.setFill("none");
		line.setStroke("black");
		line.setStrokeWidth(1.0);
		Assert.assertEquals("<line xmlns=\"http://www.w3.org/2000/svg\" x1=\"10.0\" y1=\"20.0\" x2=\"25.0\" y2=\"45.0\""
				+ " style=\"fill:none;stroke:black;stroke-width:1.0;\" />", line.toXML());
		Assert.assertTrue(new Real2(10., 20.).isEqualTo(line.getXY(0), epsilon));
		Assert.assertTrue(new Real2(10., 20.).isEqualTo(line.getXY(0), epsilon));
		Assert.assertTrue(new Real2(25., 45.).isEqualTo(line.getXY(1), epsilon));
		Real2Range bbox = line.getBoundingBox();
		Assert.assertTrue(bbox.isEqualTo(new Real2Range(new RealRange(10., 25.), new RealRange(20., 45.)), epsilon));
	}
	
	@Test
	public void testIsGeometricallyEqualTo() {
		SVGLine line0 = new SVGLine(new Real2(10., 20.), new Real2(25., 45.));
		Assert.assertTrue(line0.isGeometricallyEqualTo(
				new SVGLine(new Real2(10.005, 20.005), new Real2(25.005, 45.005)), epsilon));
		Assert.assertTrue(line0.isGeometricallyEqualTo(
				new SVGLine(new Real2(9.995, 20.005), new Real2(24.995, 45.005)), epsilon));
		Assert.assertFalse(line0.isGeometricallyEqualTo(
				new SVGLine(new Real2(10.015, 20.005), new Real2(5.005, 15.015)), epsilon));
	}

	@Test
	public void testCreateSplitLines() {
		SVGLine line = new SVGLine(new Real2(1,2), new Real2(9, 18));
		line.setCSSStyle("fill:blue;");
		List<SVGLine> splitLines = line.createSplitLines(4);
		Assert.assertEquals("nlines", 4, splitLines.size());
		Assert.assertEquals("line0", "<line xmlns=\"http://www.w3.org/2000/svg\" style=\"fill:blue;\" x1=\"1.0\" y1=\"2.0\" x2=\"3.0\" y2=\"6.0\" />", splitLines.get(0).toXML());
		Assert.assertTrue("line2", new Real2(7.0, 14.0).isEqualTo(splitLines.get(2).getXY(1), 0.01));
		Assert.assertTrue("line4", new Real2(9.0, 18.0).isEqualTo(splitLines.get(3).getXY(1), 0.01));
		LOG.debug(splitLines);
	}

}
