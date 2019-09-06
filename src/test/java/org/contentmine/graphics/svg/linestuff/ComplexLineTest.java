package org.contentmine.graphics.svg.linestuff;

import java.util.Arrays;
import java.util.List;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager.BoxEdge;
import org.contentmine.graphics.svg.linestuff.ComplexLine.CombType;
import org.contentmine.graphics.svg.linestuff.ComplexLine.Direction;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.contentmine.graphics.svg.linestuff.ComplexLine.SideOrientation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ComplexLineTest {

	final static Double EPS = 0.01;
	public SVGLine hor0;
	public SVGLine vert1;
	public SVGLine hor1a;
	public SVGLine hor1b;
	public SVGLine vert2;
	
	@Before
	public void setup() {
		hor0 = new SVGLine(new Real2(50,150), new Real2(100,150));
		hor0.setId("hor0");
		vert1 = new SVGLine(new Real2(100,100), new Real2(100,200));
		vert1.setId("vert1");
		hor1a = new SVGLine(new Real2(100,100), new Real2(200,100));
		hor1a.setId("hor1a");
		hor1b = new SVGLine(new Real2(100,200), new Real2(200,200));
		hor1b.setId("hor1b");
		vert2 = new SVGLine(new Real2(200,50), new Real2(200,150));
		vert2.setId("vert2");
	}

	
	@Test
	public void testComplexLineVertical() {
		SVGLine backbone = new SVGLine(new Real2(100.,0), new Real2(100., 200));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		LineOrientation backboneOrientation = complexLine.getBackboneOrientation();
		Assert.assertEquals(LineOrientation.VERTICAL, backboneOrientation);
		Direction direction = complexLine.getBackboneDirection();
		Assert.assertEquals(Direction.LINE_DIR_1_2, direction);
	}
	
	@Test
	public void testComplexLineHorizontal() {
		SVGLine backbone = new SVGLine(new Real2(0., 100), new Real2(200., 100));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		Assert.assertEquals(LineOrientation.HORIZONTAL, complexLine.getBackboneOrientation());
		Assert.assertEquals(Direction.LINE_DIR_1_2, complexLine.getBackboneDirection());
	}
	
	@Test
	public void testComplexLineHorizontal1() {
		SVGLine backbone = new SVGLine(new Real2(200., 100), new Real2(0., 100));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		Assert.assertEquals(LineOrientation.HORIZONTAL, complexLine.getBackboneOrientation());
		Assert.assertEquals(Direction.LINE_DIR_2_1, complexLine.getBackboneDirection());
	}
	
	@Test
	public void testJointVertical() {
		SVGLine backbone = new SVGLine(new Real2(100.,0), new Real2(100., 200));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(0., 20.), new Real2(200., 20.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNotNull(joint);
		Real2 point = joint.getPoint();
		Assert.assertTrue(new Real2(100., 20).isEqualTo(point, EPS));
		SideOrientation sideOrientation = joint.getSideOrientation();
		Assert.assertEquals(SideOrientation.CROSSING, sideOrientation);
	}

	@Test
	public void testJointHorizontal() {
		SVGLine backbone = new SVGLine(new Real2(0., 100), new Real2(200.,100));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(20., 0.), new Real2(20., 200.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNotNull(joint);
		Real2 point = joint.getPoint();
		Assert.assertTrue(new Real2(20., 100).isEqualTo(point, EPS));
		SideOrientation sideOrientation = joint.getSideOrientation();
		Assert.assertEquals(SideOrientation.CROSSING, sideOrientation);

	}

	@Test
	public void testJointVertical1() {
		SVGLine backbone = new SVGLine(new Real2(100.,0), new Real2(100., 200));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(0., 220.), new Real2(200., 220.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNull(joint);
	}

	@Test
	public void testJointHorizontal1() {
		SVGLine backbone = new SVGLine(new Real2(0., 100), new Real2(200.,100));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(220., 0.), new Real2(220., 200.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNull(joint);
	}

	@Test
	public void testJointVertical2() {
		SVGLine backbone = new SVGLine(new Real2(100.,0), new Real2(100., 200));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(220., 0.), new Real2(220., 200.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNull(joint);
	}

	@Test
	public void testJointHorizontal2() {
		SVGLine backbone = new SVGLine(new Real2(0., 100), new Real2(200.,100));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(0., 220.), new Real2(200., 220.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNull(joint);
	}

	@Test
	public void testJointVerticalButt() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(100., 50.), new Real2(300., 50.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNotNull(joint);
		Real2 point = joint.getPoint();
		Assert.assertNotNull(point);
		Assert.assertTrue("point: "+point, new Real2(100., 50.).isEqualTo(point, EPS));
		SideOrientation sideOrientation = joint.getSideOrientation();
		Assert.assertEquals("side", SideOrientation.PLUS, sideOrientation);
	}

	@Test
	public void testJointHorizontalButt() {
		SVGLine backbone = new SVGLine(new Real2(0., 100.), new Real2(200., 100.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(100., 100.), new Real2(100., 300.));
		Joint joint = complexLine.createPerpendicularJoint(line1, EPS);
		Assert.assertNotNull(joint);
		Real2 point = joint.getPoint();
		Assert.assertTrue("point", new Real2(100., 100).isEqualTo(point, EPS));
		SideOrientation sideOrientation = joint.getSideOrientation();
		Assert.assertEquals("side", SideOrientation.PLUS, sideOrientation);
	}

	@Test
	public void testMultipleJoints() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine line1 = new SVGLine(new Real2(100., 50.), new Real2(300., 50.));
		Joint joint1 = complexLine.createPerpendicularJoint(line1, EPS);
		SVGLine line2 = new SVGLine(new Real2(100., 150.), new Real2(300., 150.));
		Joint joint2 = complexLine.createPerpendicularJoint(line2, EPS);
		SVGLine line3= new SVGLine(new Real2(100., 250.), new Real2(300., 250.));
		Joint joint3 = complexLine.createPerpendicularJoint(line3, EPS);
		Assert.assertNull(joint3);
	}

	@Test
	public void testJointList() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(100., 50.), new Real2(300., 50.)),
			new SVGLine(new Real2(100., 150.), new Real2(300., 150.)),
			new SVGLine(new Real2(100., 250.), new Real2(300., 250.)), // not added
		};
		complexLine.addLines(Arrays.asList(lines));
		List<Joint> jointList = complexLine.getJointList();
		Assert.assertNotNull(jointList);
		Assert.assertEquals(2, jointList.size());
		Joint joint = jointList.get(0);
		Real2 point = joint.getPoint();
		Assert.assertTrue("point: "+point, new Real2(100., 50).isEqualTo(point, EPS));
		Assert.assertEquals(SideOrientation.PLUS, joint.getSideOrientation());
		Assert.assertTrue("point: ", new Real2(100., 150).isEqualTo(jointList.get(1).getPoint(), EPS));
		Assert.assertEquals(SideOrientation.PLUS, jointList.get(1).getSideOrientation());
	}

	@Test
	public void testAxisTicks() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(100., 0.), new Real2(150., 0.)),
			new SVGLine(new Real2(100., 50.), new Real2(150., 50.)),
			new SVGLine(new Real2(50., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(100., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(100., 200.), new Real2(150., 200.)), 
		};
		complexLine.addLines(Arrays.asList(lines));
		List<Joint> jointList = complexLine.getJointList();
		Assert.assertNotNull(jointList);
		Assert.assertEquals(5, jointList.size());
	}

	@Test
	public void testDirections() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(150., 0.), new Real2(100., 0.)),
			new SVGLine(new Real2(100., 50.), new Real2(50., 50.)),
			new SVGLine(new Real2(50., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(50., 150.), new Real2(100., 150.)),
			new SVGLine(new Real2(150., 200.), new Real2(100., 200.)), 
		};
		complexLine.addLines(Arrays.asList(lines));
		List<Joint> jointList = complexLine.getJointList();
		Assert.assertEquals(5, jointList.size());
		List<Direction> directions = Joint.getDirections(jointList);
		Assert.assertEquals("directions", 
				"["+Direction.LINE_DIR_2_1+", "+
					Direction.LINE_DIR_2_1+", "+
					Direction.LINE_DIR_1_2+", "+
					Direction.LINE_DIR_1_2+", "+
					Direction.LINE_DIR_2_1+"]", directions.toString());
		List<SideOrientation> sideOrientations = Joint.getSideOrientations(jointList);
		Assert.assertEquals("sideOrientations", 
				"["+SideOrientation.PLUS+", "+
					SideOrientation.MINUS+", "+
					SideOrientation.CROSSING+", "+
					SideOrientation.MINUS+", "+
					SideOrientation.PLUS+"]", sideOrientations.toString());
	}

	@Test
	public void testCombTypePlus() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(100., 0.), new Real2(150., 0.)),
			new SVGLine(new Real2(100., 50.), new Real2(150., 50.)),
			new SVGLine(new Real2(100., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(100., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(100., 200.), new Real2(150., 200.)), 
		};
		complexLine.addLines(Arrays.asList(lines));
		CombType combType = complexLine.getCombType();
		Assert.assertEquals(CombType.PLUS, combType);
	}

	@Test
	public void testCombTypeMinus() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(50., 0.), new Real2(100., 0.)),
			new SVGLine(new Real2(50., 50.), new Real2(100., 50.)),
			new SVGLine(new Real2(50., 150.), new Real2(100., 150.)),
			new SVGLine(new Real2(50., 150.), new Real2(100., 150.)),
			new SVGLine(new Real2(50., 200.), new Real2(100., 200.)), 
		};
		complexLine.addLines(Arrays.asList(lines));
		CombType combType = complexLine.getCombType();
		Assert.assertEquals(CombType.MINUS, combType);
	}

	@Test
	public void testCombTypeTwosided() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(150., 0.), new Real2(50., 0.)),
			new SVGLine(new Real2(150., 50.), new Real2(50., 50.)),
			new SVGLine(new Real2(150., 150.), new Real2(50., 150.)),
			new SVGLine(new Real2(150., 150.), new Real2(50., 150.)),
			new SVGLine(new Real2(150., 200.), new Real2(50., 200.)), 
		};
		complexLine.addLines(Arrays.asList(lines));
		CombType combType = complexLine.getCombType();
		Assert.assertEquals(CombType.CROSSING, combType);
	}

	@Test
	public void testCombTypePlusMinus() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(100., 0.), new Real2(50., 0.)),
			new SVGLine(new Real2(100., 50.), new Real2(150., 50.)),
			new SVGLine(new Real2(100., 150.), new Real2(50., 150.)),
			new SVGLine(new Real2(100., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(100., 200.), new Real2(50., 200.)), 
		};
		complexLine.addLines(Arrays.asList(lines));
		CombType combType = complexLine.getCombType();
		Assert.assertEquals(CombType.PLUS_OR_MINUS, combType);
	}


	@Test
	public void testCombTypeMixed() {
		SVGLine backbone = new SVGLine(new Real2(100.,0.), new Real2(100., 200.));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
			new SVGLine(new Real2(100., 0.), new Real2(50., 0.)),
			new SVGLine(new Real2(100., 50.), new Real2(150., 50.)),
			new SVGLine(new Real2(50., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(100., 150.), new Real2(150., 150.)),
			new SVGLine(new Real2(100., 200.), new Real2(50., 200.)), 
		};
		complexLine.addLines(Arrays.asList(lines));
		CombType combType = complexLine.getCombType();
		Assert.assertEquals(CombType.MIXED, combType);
	}

	@Test
	public void testSimpleTree() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		CombType[] combTypes = {
				CombType.CROSSING,
				CombType.PLUS_OR_MINUS,
				CombType.MIXED,
				CombType.PLUS_OR_MINUS,
				CombType.PLUS,
				CombType.CROSSING,
				CombType.CROSSING,
				CombType.CROSSING,
				CombType.CROSSING,
				CombType.MINUS,
				CombType.MIXED,
				CombType.PLUS_OR_MINUS,
				CombType.PLUS,
				CombType.CROSSING,
				CombType.PLUS_OR_MINUS,
				CombType.PLUS,
				CombType.MINUS,
				CombType.CROSSING,
				CombType.MINUS,
		};
		int i = 0;
		for (SVGLine svgLine : svgLines) {
			ComplexLine complexLine = ComplexLine.createComplexLine(svgLine, EPS);
			complexLine.addLines(svgLines);
			Assert.assertEquals(combTypes[i++], complexLine.getCombType());
		}
	}
	
	@Test
	public void testOrientation() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		List<SVGLine> subset = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		Assert.assertEquals(15, subset.size());
		subset = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		Assert.assertEquals(4, subset.size());
	}
	
	
	@Test
	public void testHorizontalBackbones() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		int[] branches = {1,2,1,1,1, 1,1,1,2,1, 2,1,1,1,1};
		int i = 0;
		for (SVGLine horizontalLine : horizontalLines) {
			ComplexLine horizontalComplexLine = ComplexLine.createComplexLine(horizontalLine, EPS);
			String id = horizontalLine.getId();
			List<Joint> joints = horizontalComplexLine.addLines(verticalLines);
			Assert.assertEquals("joint: "+id+" "+i, branches[i++], joints.size());
		}
	}

	@Test
	public void testVerticalBackbones() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		int[] branches = {3,7,5,3};
		int i = 0;
		for (SVGLine verticalLine : verticalLines) {
			ComplexLine verticalComplexLine = ComplexLine.createComplexLine(verticalLine, EPS);
			String id = verticalLine.getId();
			List<Joint> joints = verticalComplexLine.addLines(horizontalLines);
			Assert.assertEquals("joint: "+id+" "+i, branches[i++], joints.size());
		}
	}
	
	@Test
	public void testSortHorizontalLinesByEnds() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<SVGElement> horizontalMaxLines = BoundingBoxManager.getElementsSortedByEdge(horizontalLines, BoxEdge.XMAX);
		Assert.assertEquals("r1", "r1", horizontalMaxLines.get(0).getId());
		Assert.assertEquals("r112", "r112", horizontalMaxLines.get(1).getId());
		Assert.assertEquals("r111", "r111", horizontalMaxLines.get(2).getId());
		Assert.assertEquals("r11212", "r11212", horizontalMaxLines.get(12).getId());
		Assert.assertEquals("r11115", "r11115", horizontalMaxLines.get(11).getId());
		// this one depends on initial order
		List<SVGElement> horizontalMinLines = BoundingBoxManager.getElementsSortedByEdge(horizontalLines, BoxEdge.XMIN);
		Assert.assertEquals("r1", "r1", horizontalMinLines.get(0).getId());
		Assert.assertEquals("r111", "r111", horizontalMinLines.get(1).getId());
		Assert.assertEquals("rr1121212", "r1121212", horizontalMinLines.get(14).getId());
	}

	@Test
	public void testSortVerticalLines() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		List<SVGElement> verticalMaxLines = BoundingBoxManager.getElementsSortedByEdge(verticalLines, BoxEdge.XMAX);
		Assert.assertEquals("v11", "v11", verticalMaxLines.get(0).getId());
		Assert.assertEquals("v1121", "v1121", verticalMaxLines.get(1).getId());
		Assert.assertEquals("v1111", "v1111", verticalMaxLines.get(2).getId());
		Assert.assertEquals("v112121", "v112121", verticalMaxLines.get(3).getId());
	}

	@Test
	public void testCreateBackbonesFromVerticalLines() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<? extends SVGElement> horizontalLinesSortedMin = BoundingBoxManager.getElementsSortedByEdge(horizontalLines, BoxEdge.XMIN);
		List<? extends SVGElement> horizontalLinesSortedMax = BoundingBoxManager.getElementsSortedByEdge(horizontalLines, BoxEdge.XMAX);
//		for (SVGLine verticalLine : verticalLines) {
//			List<? extends SVGElement> touchingLines = ComplexLine.findTouchingOtherElements(verticalLine, horizontalLinesSortedMin, horizontalLinesSortedMax, BoxEdge.XMIN, EPS);
//		}
	}

	@Test
	public void testCreateBackbonesFromHorizontalLines() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(SVGHTMLFixtures.PATHS_SIMPLE_TREE_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines(svg);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		List<? extends SVGElement> verticalLinesSortedMin = BoundingBoxManager.getElementsSortedByEdge(verticalLines, BoxEdge.YMIN);
		List<? extends SVGElement> verticalLinesSortedMax = BoundingBoxManager.getElementsSortedByEdge(verticalLines, BoxEdge.YMAX);
	}

	@Test
	public void testSortedJoints() {
		SVGLine backbone = new SVGLine(new Real2(100,100), new Real2(100,300));
		ComplexLine complexLine = ComplexLine.createComplexLine(backbone, EPS);
		SVGLine[] lines = {
				new SVGLine(new Real2(100,100), new Real2(200,100)),
				new SVGLine(new Real2(100,300), new Real2(200,300)),
				new SVGLine(new Real2(100,200), new Real2(50,200)),
				new SVGLine(new Real2(100,150), new Real2(150,150)),
		};
		complexLine.addLines(Arrays.asList(lines));
		List<Joint> jointList = complexLine.getJointList();
		Assert.assertTrue(jointList.get(0).isAtEndOfBackbone(EPS));
		Assert.assertTrue(jointList.get(1).isAtEndOfBackbone(EPS));
		Assert.assertFalse(jointList.get(3).isAtEndOfBackbone(EPS));
		List<Joint> sortedJointList = complexLine.getSortedJointList();
		Assert.assertTrue(sortedJointList.get(0).isAtEndOfBackbone(EPS));
		Assert.assertFalse(sortedJointList.get(1).isAtEndOfBackbone(EPS));
		Assert.assertTrue(sortedJointList.get(3).isAtEndOfBackbone(EPS));
	}
	
	@Test
	public void testLineEnds() {
		List<SVGLine> lines = Arrays.asList(new SVGLine[]{hor0, vert1, hor1a, hor1b, vert2});
		List<SVGLine> verts = ComplexLine.createSubset(lines, LineOrientation.VERTICAL, EPS);
		List<ComplexLine> complexVerts = ComplexLine.createComplexLines(verts, EPS);
		List<SVGLine> hors = ComplexLine.createSubset(lines, LineOrientation.HORIZONTAL, EPS);
		List<ComplexLine> complexHors = ComplexLine.createComplexLines(hors, EPS);
		ComplexLine complexHor0 = ComplexLine.createComplexLineAndAddLines(hor0, verts, EPS);
		Assert.assertNotNull(complexHor0.getJointAtEnd(SideOrientation.PLUS));
		Assert.assertNull(complexHor0.getJointAtEnd(SideOrientation.MINUS));
		ComplexLine complexVert1 = ComplexLine.createComplexLineAndAddLines(vert1, hors, EPS);
		Assert.assertNotNull(complexVert1.getJointAtEnd(SideOrientation.PLUS));
		Assert.assertNotNull(complexVert1.getJointAtEnd(SideOrientation.MINUS));
		ComplexLine complexHor1a = ComplexLine.createComplexLineAndAddLines(hor1a, verts, EPS);
		Assert.assertNotNull(complexHor1a.getJointAtEnd(SideOrientation.PLUS));
		Assert.assertNotNull(complexHor1a.getJointAtEnd(SideOrientation.MINUS));
		ComplexLine complexHor1b = ComplexLine.createComplexLineAndAddLines(hor1b, verts, EPS);
		Assert.assertNull(complexHor1b.getJointAtEnd(SideOrientation.PLUS));
		Assert.assertNotNull(complexHor1a.getJointAtEnd(SideOrientation.MINUS));
		ComplexLine complexVert2 = ComplexLine.createComplexLineAndAddLines(vert2, hors, EPS);
		Assert.assertNull(complexVert2.getJointAtEnd(SideOrientation.PLUS));
		Assert.assertNull(complexVert2.getJointAtEnd(SideOrientation.MINUS));
	}
	
	
	@Test
	public void testLineEnds1() {
		List<SVGLine> lines = Arrays.asList(new SVGLine[]{hor0, vert1, hor1a, hor1b, vert2});
		List<ComplexLine> complexLines = ComplexLine.createComplexLineAndAddLines(lines, lines, EPS);
		List<SVGLine> verts = ComplexLine.createSubset(lines, LineOrientation.VERTICAL, EPS);
		List<ComplexLine> complexVerts = ComplexLine.createComplexLines(verts, EPS);
		List<SVGLine> hors = ComplexLine.createSubset(lines, LineOrientation.HORIZONTAL, EPS);
		List<ComplexLine> complexHors = ComplexLine.createComplexLines(hors, EPS);
		ComplexLine complexHor0 = ComplexLine.createComplexLineAndAddLines(hor0, verts, EPS);
		
		List<ComplexLine> emptyEndedComplexLines = 
				ComplexLine.extractLinesWithBranchAtEnd(complexLines, SideOrientation.EMPTYLIST);
		Assert.assertNotNull(emptyEndedComplexLines);
		Assert.assertEquals(1, emptyEndedComplexLines.size()); 
		Assert.assertEquals("vert2", emptyEndedComplexLines.get(0).getBackbone().getId()); 
		
		List<ComplexLine> doubleEndedComplexLines = 
				ComplexLine.extractLinesWithBranchAtEnd(complexLines, SideOrientation.MINUSPLUSLIST);
		Assert.assertNotNull(doubleEndedComplexLines);
		Assert.assertEquals(2, doubleEndedComplexLines.size()); 
		
		List<ComplexLine> minusEndedComplexLines = 
				ComplexLine.extractLinesWithBranchAtEnd(complexLines, SideOrientation.MINUSLIST);
		Assert.assertNotNull(minusEndedComplexLines);
		Assert.assertEquals(1, minusEndedComplexLines.size()); 
		Assert.assertEquals("hor1b", minusEndedComplexLines.get(0).getBackbone().getId()); 
		
		List<ComplexLine> plusEndedComplexLines = 
				ComplexLine.extractLinesWithBranchAtEnd(complexLines, SideOrientation.PLUSLIST);
		Assert.assertNotNull(plusEndedComplexLines);
		Assert.assertEquals(1, plusEndedComplexLines.size()); 
		Assert.assertEquals("hor0", plusEndedComplexLines.get(0).getBackbone().getId()); 
	}

// ====================================================================================
	
//	public static List<SVGLine> extractLines(SVGElement svgElement) {
//		List<SVGElement> svgElems = SVGUtil.getQuerySVGElements(svgElement, "svg:line");
//		List<SVGLine> svgLines = new ArrayList<SVGLine>();
//		for (SVGElement svgElem : svgElems) {
//			svgLines.add((SVGLine)svgElem);
//		}
//		return svgLines;
//	}
	

}
