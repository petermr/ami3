package org.contentmine.graphics.svg;

import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.EuclidTestUtils;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.CubicPrimitive;
import org.contentmine.graphics.svg.path.LinePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SVGPathTest {

	
	private final static Logger LOG = Logger.getLogger(SVGPathTest.class);
	private final static Angle ANGLE_EPS = new Angle(0.01, Units.RADIANS);
	private static final Double LINE_EPS = 1.0;
	private static final Double MAX_WIDTH = 1.0;
	
	@Test
	public void testCreatePolyline() {
		String d = "M379.558 218.898 L380.967 212.146 L380.134 212.146 L378.725 218.898 L379.558 218.898";
		SVGPath path = new SVGPath(d);
		SVGPoly polyline = path.createPolyline();
		Assert.assertNotNull(polyline);
		Real2Array r2a = polyline.getReal2Array();
		String errmsg = EuclidTestUtils.testEquals("xarray", new double[] {379.558,380.967,380.134,378.725,379.558}, r2a.getXArray().getArray(), 0.001);
		Assert.assertNull(errmsg);
		errmsg = EuclidTestUtils.testEquals("xarray", new double[] {218.898,212.146,212.146,218.898,218.898}, r2a.getYArray().getArray(), 0.001);
		Assert.assertNull(errmsg);
	}
	
	@Test
	public void testBBScalefactor() {
		SVGPath path1 = new SVGPath("M1 2 L3 4 L1 2");
		SVGPath path2 = new SVGPath("M2 4 L2 8 L6 4");
		Double d = path1.getBoundingBoxScalefactor(path2);
		Assert.assertEquals("scale", 2.0, d, 0.00001);
	}
	
	@Test
	public void testScalefactor1() {
		SVGPath path1 = new SVGPath("M1 2 L3 4 L1 2");
		SVGPath path2 = new SVGPath("M2 4 L2 8 L6 4");
		Double d = path1.getScalefactor(path2, 0.00001);
		Assert.assertNull("cannot get scalefactor", d);
	}
	
	@Test
	public void testScalefactor2() {
		SVGPath path1 = new SVGPath("M1 2 L3 4 L1 2");
		SVGPath path2 = new SVGPath("M2 4 L6 8 L2 4");
		Double d = path1.getScalefactor(path2, 0.00001);
		Assert.assertEquals("scale", 2.0, d, 0.00001);
	}
	
	@Test
	@Ignore
	public void testCircle() {
		SVGPath path1 = new SVGPath(
				"M408.95 493.497 C408.95 492.438 407.805 491.779 406.889 492.308 C405.971 492.839 405.972 494.161 406.89 494.69 C407.807 495.217 408.95 494.557 408.95 493.497");
		SVGCircle circle = path1.createCircle(0.5);
		Assert. assertNotNull(circle);
		Assert.assertEquals("rad", 1.675, circle.getRad(), 0.1);
		Assert.assertEquals("cx", 407.4, circle.getCX(), 0.2);
		Assert.assertEquals("cx", 493.5, circle.getCY(), 0.2);
		
	}

	@Test
	public void testGeneralPath() {
		GeneralPath generalPath = new GeneralPath();
		generalPath.moveTo(1.0d, 2.0d);
		generalPath.lineTo(3.0d, 4.0d);
		generalPath.quadTo(5.0d, 6.0d, 7.0d, 8.0d);
		generalPath.curveTo(9.0d, 10.0d, 11.0d, 12.0d, 13.0d, 14.0d);
		generalPath.closePath();
		SVGPath path = new SVGPath(generalPath);
		String d = path.getDString();
		Assert.assertNotNull("d", d);
		Assert.assertEquals("d", "M 1.0 2.0 L 3.0 4.0 Q 5.0 6.0 7.0 8.0 C 9.0 10.0 11.0 12.0 13.0 14.0 Z", d.trim());
	}

	@Test
	public void testFormat() {
		String d = "M 1.1234 2.12345 L 3.1234567 4.12 C 5.123456 6.1 7.123456789 8.12345 9.12345 10.12345 Z";
		SVGPath path = new SVGPath(d);
		path.format(3);
		Assert.assertEquals("format", "M1.123 2.123 L3.123 4.12 C5.123 6.1 7.123 8.122 9.123 10.123 Z", path.getDString().trim());
	}

	@Test
	public void testFormat1() {
		String d = "M 219.75799560546875 604.5350341796875 L 229.24200439453125 604.5350341796875";
		SVGPath path = new SVGPath(d);
		path.format(3);
		Assert.assertEquals("format", "M219.758 604.535 L229.242 604.535", path.getDString().trim());
	}

	@Test
	public void testPrimitives1() {
		SVGPath svgPath = new SVGPath("M100 200L250,300");
		PathPrimitiveList primitives = svgPath.getOrCreatePathPrimitiveList();
		Assert.assertEquals("prim", 2, primitives.size());
		Assert.assertTrue("prim", primitives.get(0) instanceof MovePrimitive);
		Assert.assertEquals("prim", 1, primitives.get(0).getCoordArray().size());
		Assert.assertTrue("prim", primitives.get(1) instanceof LinePrimitive);
		Assert.assertEquals("prim", 1, primitives.get(1).getCoordArray().size());
	}
	
	@Test
	public void testPrimitives2() {
		SVGPath svgPath = new SVGPath("M100 200 L250,300 C100 290 240 110 400 230 Z");
		PathPrimitiveList primitives = svgPath.getOrCreatePathPrimitiveList();
		Assert.assertEquals("prim", 4, primitives.size());
		Assert.assertTrue("prim", primitives.get(0) instanceof MovePrimitive);
		Assert.assertTrue("prim", primitives.get(1) instanceof LinePrimitive);
		Assert.assertTrue("prim", primitives.get(2) instanceof CubicPrimitive);
		Assert.assertEquals("prim", 3, primitives.get(2).getCoordArray().size());
		Assert.assertTrue("prim", primitives.get(3) instanceof ClosePrimitive);
//		Assert.assertNull("prim", primitives.get(3).getCoordArray());
	}
	

	@Test 
	public void testGetSkeleton() {
		SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "hollowcorner.svg"))
				.getChildElements().get(0);
		PathPrimitiveList primList = svgPath.getOrCreatePathPrimitiveList();
		
		primList.createMeanLine(1, 7);
		primList.createMeanCubic(2, 6);
		primList.createMeanLine(3, 5);
		primList.remove(4);

		Assert.assertEquals("skeleton", ""
			+ "M286.583 88.988 "
			+ "L287.235 89.158 "
			+ "C288.837 89.422 290.105 90.676 290.381 92.276 "
			+ "L290.438 92.957 "
			+ "L290.381 92.276 "
			+ "C290.105 90.676 288.837 89.422 287.235 89.158 "
			+ "L286.583 89.101",
			primList.getDString().trim());
		SVGPath newPath = new SVGPath(primList, svgPath);
		SVGSVG.wrapAndWriteAsSVG(newPath, new File("target/skeletonPath.svg"));
	}
	
	@Test
	public void testRemoveRoundedCaps() {
		SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.MOLECULES_DIR, "image.g.2.13.svg"))
				.getChildElements().get(0).getChildElements().get(0);
		PathPrimitiveList primList = svgPath.getOrCreatePathPrimitiveList();
		String signature = svgPath.getOrCreateSignatureAttributeValue();
		Assert.assertEquals("MLCCLCC", signature);
		primList.replaceUTurnsByButt(5);
		primList.replaceUTurnsByButt(2);
		SVGPath newPath = new SVGPath(primList, svgPath);
		Assert.assertEquals("new d", "M415.26 526.26 L415.26 517.98 L415.74 517.98 L415.74 526.26 L415.26 526.26", newPath.getDString().trim());
		Assert.assertEquals("new sig", "MLLLL", newPath.getOrCreateSignatureAttributeValue());
		
	}

	@Test
	public void testRemoveRoundedCaps1() {
		SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.MOLECULES_DIR, "image.g.2.13.svg"))
				.getChildElements().get(0).getChildElements().get(0);
		PathPrimitiveList primList = svgPath.getOrCreatePathPrimitiveList();
		String signature = svgPath.getOrCreateSignatureAttributeValue();
		Assert.assertEquals("MLCCLCC", signature);
		svgPath.replaceAllUTurnsByButt(ANGLE_EPS);
		SVGPath newPath = new SVGPath(primList, svgPath);
		Assert.assertEquals("new d", "M415.26 526.26 L415.26 517.98 L415.74 517.98 L415.74 526.26 L415.26 526.26", newPath.getDString().trim());
		Assert.assertEquals("new sig", "MLLLL", newPath.getOrCreateSignatureAttributeValue());
		
	}

	@Test
	public void testCreateLine() {
		SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.MOLECULES_DIR, "image.g.2.13.svg"))
				.getChildElements().get(0).getChildElements().get(0);
		SVGPath newPath = svgPath.replaceAllUTurnsByButt(ANGLE_EPS);
		SVGElement line = newPath.createLineFromMLLLL(ANGLE_EPS, LINE_EPS);
		Assert.assertNotNull("line", line);
		Assert.assertEquals("line", "<line xmlns=\"http://www.w3.org/2000/svg\" x1=\"415.5\" y1=\"517.98\" x2=\"415.5\" y2=\"526.26\" />",
				line.toXML().trim());
		
	}

	@Test
	public void testCreateLines() {
		List<SVGPath> pathList = SVGPath.extractPaths(SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.MOLECULES_DIR, "image.g.2.13.svg")));
		Angle angleEps = new Angle(2., Units.RADIANS);
		Assert.assertEquals("paths", 13, pathList.size());
		SVGG g = new SVGG();
		int i = 0;
		for (SVGPath path : pathList) {
			LOG.trace(path.getOrCreateSignatureAttributeValue());
			SVGPath newPath = path.replaceAllUTurnsByButt(angleEps);
			if (newPath != null) {
				SVGElement line = newPath.createLineFromMLLLL(angleEps, LINE_EPS);
				if (line != null) {
					g.appendChild(line);
				} else {
					newPath.setFill("red");
					g.appendChild(newPath.copy());
				}
			} else {
				path.setFill("blue");
				g.appendChild(path.copy());
			}
			i++;
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/moleculeLines.svg"));
	}


	@Test
	public void testCreateLine9() {
		List<SVGPath> pathList = SVGPath.extractPaths(SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.MOLECULES_DIR, "image.g.2.13.svg")));
		SVGPath path = pathList.get(9);
		Angle angle2 = new Angle(0.02, Units.RADIANS);
		Assert.assertEquals("old sig", "MLCCLCC", path.getOrCreateSignatureAttributeValue());
		PathPrimitiveList primList = path.getOrCreatePathPrimitiveList();
		List<Integer> quadrantStartList = primList.getUTurnList(angle2);
		Assert.assertEquals("uturns", 2, quadrantStartList.size());
		SVGPath newPath = path.replaceAllUTurnsByButt(angle2);
		Assert.assertEquals("new sig", "MLLLL", newPath.getOrCreateSignatureAttributeValue());
		SVGElement line = newPath.createLineFromMLLLL(angle2, MAX_WIDTH);
		Assert.assertNotNull(line);
	}


}
