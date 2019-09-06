package org.contentmine.graphics.svg.linestuff;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.junit.Assert;
import org.junit.Test;

public class LineMergerTest {

	private final static Logger LOG = Logger.getLogger(LineMergerTest.class);
	
	public final static double EPS = 0.01;
	@Test
	public void testLineJoin() {
		SVGLine line0 = new SVGLine(new Real2(1.,2.), new Real2(2., 2.));
		LineMerger lineJoin = LineMerger.createLineMerger(line0, EPS, MergeMethod.OVERLAP);
		SVGLine newLine = (SVGLine) lineJoin.createNewElement(new SVGLine(new Real2(2.,2.), new Real2(3., 2.)));
		Assert.assertNotNull(newLine);
		Assert.assertTrue(SVGLine.isEqual(new SVGLine(new Real2(1., 2.), new Real2(3., 2.)), newLine, EPS));
	}
	
	@Test
	public void testLineJoin1() {
		SVGLine line0 = new SVGLine(new Real2(1.,2.), new Real2(2., 2.));
		LineMerger lineJoin = LineMerger.createLineMerger(line0, EPS, MergeMethod.OVERLAP);
		SVGLine newLine = (SVGLine) lineJoin.createNewElement(new SVGLine(new Real2(3.,2.), new Real2(2., 2.)));
		Assert.assertNotNull(newLine);
		Assert.assertTrue(SVGLine.isEqual(new SVGLine(new Real2(1., 2.), new Real2(3., 2.)), newLine, EPS));
	}
	
	@Test
	public void testLineJoin2() {
		SVGLine line0 = new SVGLine(new Real2(1.,2.), new Real2(2., 2.));
		LineMerger lineJoin = LineMerger.createLineMerger(line0, EPS, MergeMethod.OVERLAP);
		SVGLine newLine = (SVGLine) lineJoin.createNewElement(new SVGLine(new Real2(0.,2.), new Real2(1., 2.)));
		Assert.assertNotNull(newLine);
		Assert.assertTrue(SVGLine.isEqual(new SVGLine(new Real2(0., 2.), new Real2(2., 2.)), newLine, EPS));
	}
	
	@Test
	public void testLineJoin3() {
		SVGLine line0 = new SVGLine(new Real2(1.,2.), new Real2(2., 2.));
		LineMerger lineJoin = LineMerger.createLineMerger(line0, EPS, MergeMethod.OVERLAP);
		SVGLine newLine = (SVGLine) lineJoin.createNewElement(new SVGLine(new Real2(1.,2.), new Real2(0., 2.)));
		Assert.assertNotNull(newLine);
		Assert.assertTrue(SVGLine.isEqual(new SVGLine(new Real2(0., 2.), new Real2(2., 2.)), newLine, EPS));
	}
	
	@Test
	public void testLineOverlap() {
		SVGLine line0 = new SVGLine(new Real2(1.,2.), new Real2(3., 2.));
		LineMerger lineJoin = LineMerger.createLineMerger(line0, EPS, MergeMethod.OVERLAP);
		SVGLine newLine = (SVGLine) lineJoin.createNewElement(new SVGLine(new Real2(2.,2.), new Real2(3., 2.)));
		Assert.assertNotNull("newLine ", newLine);
		Assert.assertTrue(SVGLine.isEqual(new SVGLine(new Real2(1., 2.), new Real2(3., 2.)), newLine, EPS));
	}
	
	@Test
	public void testLineOverlap1() {
		SVGLine line0 = new SVGLine(new Real2(1.,2.), new Real2(3., 2.));
		LineMerger lineJoin = LineMerger.createLineMerger(line0, EPS, MergeMethod.OVERLAP);
		SVGLine newLine = (SVGLine) lineJoin.createNewElement(new SVGLine(new Real2(1.,2.), new Real2(2., 2.)));
		Assert.assertTrue(SVGLine.isEqual(new SVGLine(new Real2(1., 2.), new Real2(3., 2.)), newLine, EPS));
	}
	
	@Test
	public void testJoinLines() {
		List<SVGLine> svgLines = new ArrayList<SVGLine>();
		svgLines.add(new SVGLine(new Real2(0., 1.), new Real2(0., 2.)));
		svgLines.add(new SVGLine(new Real2(5., 1.), new Real2(5., 2.)));
		svgLines.add(new SVGLine(new Real2(0., 3.), new Real2(0., 4.)));
		svgLines.add(new SVGLine(new Real2(0., 2.), new Real2(0., 3.)));
		svgLines.add(new SVGLine(new Real2(0., 5.), new Real2(0., 6.)));
		svgLines.add(new SVGLine(new Real2(5., 3.), new Real2(5., 4.)));
		svgLines.add(new SVGLine(new Real2(3., 3.), new Real2(3., 4.)));
		svgLines.add(new SVGLine(new Real2(5., 0.), new Real2(5., 1.)));
		int i = 0;
		for (SVGLine line : svgLines) {
			line.setId("V"+(i++));
		}
		svgLines = LineMerger.mergeLines(svgLines, EPS, MergeMethod.OVERLAP);
		Assert.assertEquals(5, svgLines.size());
		for (SVGLine line : svgLines) {
			if ("V4".equals(line.getId())){
				Assert.assertTrue("", new Real2(0., 6.).isEqualTo(line.getXY(1), EPS));
			} else if ("V1x".equals(line.getId())){
				Assert.assertTrue("", new Real2(5., 2.).isEqualTo(line.getXY(1), EPS));
			} else if ("V0xx".equals(line.getId())){
				Assert.assertTrue("", new Real2(0., 4.).isEqualTo(line.getXY(1), EPS));
			}
			LOG.trace(line.toXML());
		}
	}


}
