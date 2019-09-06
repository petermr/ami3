package org.contentmine.graphics.svg.plot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.junit.Assert;
import org.junit.Test;

public class SVGBarredPointTest {

	private static final Logger LOG = Logger.getLogger(SVGBarredPointTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static double RAD = 5.0;
	
	@Test
	public void testBarredPoint() throws IOException {
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(new FileInputStream(new File(SVGHTMLFixtures.G_S_PLOT_DIR, "barredPoints.svg")));
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGCircle> circleList = SVGPath.createCirclesFromPaths(pathList);
		Assert.assertEquals("circles",  52, circleList.size());
		List<SVGLineList> lineListList = SVGPath.createLineListListFromPaths(pathList, null);
		Assert.assertEquals("lineListList",  52, lineListList.size());
		List<SVGLineList> lineListList2 = SVGPath.createLineListListFromPaths(pathList, "MLML");
		Assert.assertEquals("lineList2",  11,  lineListList2.size());
		List<SVGLineList> lineListList4 = SVGPath.createLineListListFromPaths(pathList, "MLMLMLML");
		Assert.assertEquals("lineList4",  41,  lineListList4.size());
		
	}
	
	@Test
	public void testErrorBar() throws IOException {
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(new FileInputStream(new File(SVGHTMLFixtures.G_S_PLOT_DIR, "barredPoints.svg")));
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGCircle> circleList = SVGPath.createCirclesFromPaths(pathList);
		List<SVGLineList> lineListList = SVGPath.createLineListListFromPaths(pathList, null);
		SVGBarredPoint barredPoint0 = SVGBarredPoint.createPoint(circleList.get(0));
		Assert.assertNotNull("bar not null", barredPoint0);
		Assert.assertEquals("point0", "shape: class org.contentmine.graphics.svg.SVGCircle; <circle xmlns=\"http://www.w3.org/2000/svg\" cx=\"350.28\" cy=\"248.70000000000002\" r=\"1.297868444797087\" />; (350.28,248.7); bars: []", barredPoint0.toString());
		SVGLineList lineList0 = lineListList.get(0);
		SVGLine line0 = lineList0.get(0);
		line0.format(3);
		SVGErrorBar errorBar0 = barredPoint0.createErrorBar(line0, RAD, SVGHTMLFixtures.EPS);
		errorBar0.getLine().format(3);
		Assert.assertEquals("bar0", "RIGHT", errorBar0.getBarDirection().toString());
		Assert.assertEquals("bar0", "(349.158,248.738)", errorBar0.getLine().getXY(0).toString());
	}
	
	@Test
	public void testErrorBars() throws IOException {
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(new FileInputStream(new File(SVGHTMLFixtures.G_S_PLOT_DIR, "barredPoints.svg")));
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGCircle> circleList = SVGPath.createCirclesFromPaths(pathList);
		List<SVGLineList> lineListList = SVGPath.createLineListListFromPaths(pathList, null);
		int npoints = lineListList.size();
		SVGBarredPoint barredPoint0 = SVGBarredPoint.createPoint(circleList.get(0));
		SVGBarredPoint barredPointN = SVGBarredPoint.createPoint(circleList.get(npoints - 1));
		SVGLineList lineList0 = lineListList.get(0);
		Assert.assertEquals("line0",  2, lineList0.size());
		List<SVGErrorBar> errorBarList0 = barredPoint0.createErrorBarList(lineList0, RAD, SVGHTMLFixtures.EPS);
		Assert.assertEquals("bar0",  2, errorBarList0.size());
		SVGLineList lineListN = lineListList.get(npoints - 1);
		Assert.assertEquals("lineN",  4, lineListN.size());
		List<SVGErrorBar> errorBarListN = barredPointN.createErrorBarList(lineListN, RAD, SVGHTMLFixtures.EPS);
		Assert.assertEquals("bar0",  4, errorBarListN.size());
		// these should fail as the bars don't point at the points
		List<SVGErrorBar> errorBarList0N = barredPoint0.createErrorBarList(lineListN, RAD, SVGHTMLFixtures.EPS);
		Assert.assertNull("bar0N", errorBarList0N);
		List<SVGErrorBar> errorBarListN0 = barredPointN.createErrorBarList(lineList0, RAD, SVGHTMLFixtures.EPS);
		Assert.assertNull("barN0", errorBarListN0);
	}
	
	
	@Test
	public void testErrorBarList() throws IOException {
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(new FileInputStream(new File(SVGHTMLFixtures.G_S_PLOT_DIR, "barredPoints.svg")));
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGCircle> circleList = SVGPath.createCirclesFromPaths(pathList);
		List<SVGLineList> lineListList = SVGPath.createLineListListFromPaths(pathList, null);
		SVGBarredPointList barredPointList = SVGBarredPointList.createBarredPointList(circleList, lineListList, RAD, SVGHTMLFixtures.EPS);
		Assert.assertNotNull("barredPoints", barredPointList);
		Assert.assertEquals("barredPoints", 52, barredPointList.size());
		int[] points = new int[]{
				2,2,2,2,2, 2,4,4,4,4, 4,4,2,2,2, 2,2,4,4,4,
				4,4,4,4,4, 4,4,4,4,4, 4,4,4,4,4, 4,4,4,4,4,
				4,4,4,4,4, 4,4,4,4,4, 4,4
		};
		for (int i = 0; i < barredPointList.size(); i++) {
			SVGBarredPoint barredPoint = barredPointList.get(i);
			Assert.assertEquals(""+i, points[i], barredPoint.getErrorBarList().size());
		}
	}
	
	@Test
	public void testDrawErrorBarList() throws IOException {
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(new FileInputStream(new File(SVGHTMLFixtures.G_S_PLOT_DIR, "barredPoints.svg")));
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGCircle> circleList = SVGPath.createCirclesFromPaths(pathList);
		List<SVGLineList> lineListList = SVGPath.createLineListListFromPaths(pathList, null);
		SVGBarredPointList barredPointList = SVGBarredPointList.createBarredPointList(circleList, lineListList, RAD, SVGHTMLFixtures.EPS);
		SVGG g = new SVGG();
		for (int i = 0; i < barredPointList.size(); i++) {
			SVGBarredPoint barredPoint = barredPointList.get(i);
			if (i > 0) {
				SVGLine line = new SVGLine(barredPointList.get(i - 1).getOrCreateCentroid(), barredPoint.getOrCreateCentroid());
				line.setStroke("red");
				g.appendChild(line);
			}
			SVGElement shape = barredPoint.getErrorShape();
			shape.setFill("none");
			g.appendChild(shape);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/hep/testBars.svg"));
	}
}
