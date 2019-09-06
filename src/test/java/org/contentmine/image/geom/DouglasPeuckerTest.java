package org.contentmine.image.geom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageAnalysisFixtures;
import org.junit.Assert;
import org.junit.Test;

public class DouglasPeuckerTest {
	
	private final static Logger LOG = Logger.getLogger(DouglasPeuckerTest.class);
	@Test
	public void testLine() {
		DouglasPeucker douglasPeucker = new DouglasPeucker(1.0);
		List<Real2> points = new ArrayList<Real2>();
		points.add(new Real2(1.0, 11.0));
		points.add(new Real2(2.0, 12.0));
		points.add(new Real2(3.0, 13.0));
		points.add(new Real2(4.0, 14.0));
		points.add(new Real2(5.0, 15.0));
		List<Real2> reducedList = douglasPeucker.reduce(points);
		Assert.assertEquals("reduce", 2, reducedList.size());
		Assert.assertEquals("point0", "(1.0,11.0)", reducedList.get(0).toString());
		Assert.assertEquals("point0", "(5.0,15.0)", reducedList.get(1).toString());
	}

	@Test
	public void testNoChange() {
		DouglasPeucker douglasPeucker = new DouglasPeucker(0.01);
		double d = 0.1;
		List<Real2> points = new ArrayList<Real2>();
		points.add(new Real2(1.0-d, 11.0));
		points.add(new Real2(2.0+d, 12.0));
		points.add(new Real2(3.0-d, 13.0));
		points.add(new Real2(4.0+d, 14.0));
		points.add(new Real2(5.0-d, 15.0));
		List<Real2> reducedList = douglasPeucker.reduce(points);
		Assert.assertEquals("reduce", 5, reducedList.size());
		Assert.assertEquals("point0", "(0.9,11.0)", reducedList.get(0).toString());
		Assert.assertEquals("point0", "(2.1,12.0)", reducedList.get(1).toString());
		Assert.assertEquals("point0", "(2.9,13.0)", reducedList.get(2).toString());
		Assert.assertEquals("point0", "(4.1,14.0)", reducedList.get(3).toString());
		Assert.assertEquals("point4", "(4.9,15.0)", reducedList.get(4).toString());
	}

	@Test
	public void testStraightened() {
		DouglasPeucker douglasPeucker = new DouglasPeucker(0.1);
		double d = 0.01;
		List<Real2> points = new ArrayList<Real2>();
		points.add(new Real2(1.0-d, 11.0));
		points.add(new Real2(2.0+d, 12.0));
		points.add(new Real2(3.0-d, 13.0));
		points.add(new Real2(4.0+d, 14.0));
		points.add(new Real2(5.0-d, 15.0));
		List<Real2> reducedList = douglasPeucker.reduce(points);
		Assert.assertEquals("reduce", 2, reducedList.size());
		Assert.assertEquals("point0", "(0.99,11.0)", reducedList.get(0).toString());
		Assert.assertEquals("point1", "(4.99,15.0)", reducedList.get(1).toString());
	}

	@Test
	public void testBend() {
		DouglasPeucker douglasPeucker = new DouglasPeucker(0.1);
		double d = 0.01;
		List<Real2> points = new ArrayList<Real2>();
		points.add(new Real2(1.0-d, 11.0));
		points.add(new Real2(2.0+d, 12.0));
		points.add(new Real2(3.0-d, 13.0));
		points.add(new Real2(2.0+d, 14.0));
		points.add(new Real2(1.0-d, 15.0));
		List<Real2> reducedList = douglasPeucker.reduce(points);
		Assert.assertEquals("reduce", 3, reducedList.size());
		Assert.assertEquals("point0", "(0.99,11.0)", reducedList.get(0).toString());
		Assert.assertEquals("point1", "(2.99,13.0)", reducedList.get(1).toString());
		Assert.assertEquals("point2", "(0.99,15.0)", reducedList.get(2).toString());
	}
	
	@Test
	public void testLinesFromPixels() throws IOException {
//		Vectorizer vectorizer = new Vectorizer();
//		vectorizer.readFile(Fixtures.MALTORYZINE_THINNED_PNG);
//		List<PixelIsland> islandList = vectorizer.createIslands();
//		Assert.assertEquals("islands", 5, islandList.size());
//		vectorizer.segment(islandList.get(1));
//		int[] islands =   {492, 33,  25,  29,  25};
	}

	@Test
	/** not sure whether still current.
	 * 
	 *  have removed other contours files
	 * 
	 */
	public void testContours() {
		SVGElement contour = SVGElement.readAndCreateSVG(new File(ImageAnalysisFixtures.LINES_DIR, "contours/1.svg"));
		List<SVGLine> lines = SVGLine.extractSelfAndDescendantLines(contour);
		Real2Array points0 = SVGLine.extractPoints(lines, 0.00001);
		List<Real2> points = points0.getList(); 
		DouglasPeucker douglasPeucker = new DouglasPeucker(0.1);
		List<Real2> reducedList = douglasPeucker.reduce(points);
		boolean close = true;
		SVGG g = (SVGG) SVGLine.plotPointsAsTouchingLines(reducedList, close);
		Assert.assertEquals("lines", 11, reducedList.size());
		File file = new File("target/contours/");
		file.mkdirs();
		SVGSVG.wrapAndWriteAsSVG(g, new File(file, "1r.svg"));
	}

}
