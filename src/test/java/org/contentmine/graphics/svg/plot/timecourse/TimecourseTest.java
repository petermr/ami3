package org.contentmine.graphics.svg.plot.timecourse;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;
import org.contentmine.graphics.svg.objects.SVGTriangle;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

/** early test to get dose/effect/time plots.
 * Initially based on Bel2014 NEJM
 * N Engl J Med 2014;371:1189-97.
DOI: 10.1056/NEJMoa1403291
 * 
 * Figure 2. Changes in Oral Glucocorticoid Dose, Rate
of Exacerbations, and Asthma Control.
Panel A shows the median percentage reduction from
baseline in the daily glucocorticoid dose in the two
study groups. At 24 weeks, the median percentage
reduction was 50% in the mepolizumab group, and
there was no reduction in the placebo group (P = 0.007).
The I bars represent 95% confidence intervals. Panel B
shows the cumulative rate of clinically significant asthma
exacerbations, with a relative reduction of 32% in
the mepolizumab group, as compared with the placebo
group, at week 24 (P = 0.04). Panel C shows changes in
responses on the Asthma Control Questionnaire 5
(ACQ-5). The score on the ACQ-5 represents the mean
of responses to five questions about the frequency or
severity of symptoms during the previous week, with
each response scored on a scale of 0 to 6 and higher
scores indicating poorer control; the minimal clinically
important difference for the mean score is 0.5 points.
Improvements were observed as early as week 2 in the
mepolizumab group, an effect that was sustained up
to week 24 (P = 0.004). The I bars represent 95% confidence
intervals around the least-square means.
 * 
 * @author pm286
 *
 */
public class TimecourseTest {
	private static final Logger LOG = Logger.getLogger(TimecourseTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	/** Figure 2 A from Bel2014 is an effect/dose/time plot 
	 * I have hacked by hand, later I will use Caches
	 */
	@Test
	public void testReadBelA() {
		String fileroot = "bel";
		File sourceDir = new File(SVGHTMLFixtures.TIMECOURSE_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.TARGET_TIMECOURSE_DIR, fileroot);
		SVGElement bela = SVGElement.readAndCreateSVG(new File(sourceDir, "svg/dosagea.svg"));
		// process the control
		/**
		<g opacity="0.9" title="placebo" class="plot">
		  <g opacity="0.9" class="plot.polyline">
		  <path fill="none" style="stroke:#000000;stroke-width:0.75;" d="M333.281 126.538 L364.226 126.538 L333.281 126.538 L518.034 126.538 "/>
		 </g>
		 */
		Real2Array placeboCoords = extractCoords(bela, "placebo");
		Assert.assertEquals(4, placeboCoords.size());
		Real2Array mepCoords = extractCoords(bela, "mep");
		Assert.assertEquals(7, mepCoords.size());
		RealArray mepX = mepCoords.getXArray();
		RealArray mepY = mepCoords.getYArray();
		
		

		// *[@class='plot.polyline'
		
		
		
		
	}

	/** Figure 2 B from Bel2014 is an effect/dose/time plot 
	 * I have hacked by hand, later I will use Caches.
	 * Messy since the first plot is stepped but has lost the up-part (riser)
	 */
	@Test
	@Ignore // not sure 
	public void testReadBelB() {
		String fileroot = "bel";
		File sourceDir = new File(SVGHTMLFixtures.TIMECOURSE_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.TARGET_TIMECOURSE_DIR, fileroot);
		SVGElement belb = SVGElement.readAndCreateSVG(new File(sourceDir, "svg/dosageb.svg"));
		// process the control - this is
		/**
		<g opacity="0.9" title="placebo" class="plot">
		  <g opacity="0.9" class="plot.polyline">
		  <path fill="none" style="stroke:#000000;stroke-width:0.75;" d="M333.281 126.538 L364.226 126.538 L333.281 126.538 L518.034 126.538 "/>
		 </g>
		 */
		Real2Array mepCoords = extractCoordsSteps(belb, "mep");
		Assert.assertEquals(7, mepCoords.getList().size());
		Real2Array placeboCoords = extractCoordsBrokenSteps(belb, "placebo");
		Assert.assertEquals(4, placeboCoords.size());
		RealArray mepX = mepCoords.getXArray();
		RealArray mepY = mepCoords.getYArray();

		// *[@class='plot.polyline'
	}

	/** Figure 2 C from Bel2014 is an effect/dose/time plot 
	 * I have hacked by hand, later I will use Caches
	 */
	@Test
	public void testReadBelC() {
		String fileroot = "bel";
		File sourceDir = new File(SVGHTMLFixtures.TIMECOURSE_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.TARGET_TIMECOURSE_DIR, fileroot);
		SVGElement belc = SVGElement.readAndCreateSVG(new File(sourceDir, "svg/dosagec.svg"));
		// process the control
		/**
		<g opacity="0.9" title="placebo" class="plot">
		  <g opacity="0.9" class="plot.polyline">
		  <path fill="none" style="stroke:#000000;stroke-width:0.75;" d="M333.281 126.538 L364.226 126.538 L333.281 126.538 L518.034 126.538 "/>
		 </g>
		 */
		SVGLineList xticks = getLineList(belc, ".//*[@class='xaxis.ticks.minor']/*[local-name()='path']");
		Assert.assertEquals(19, xticks.size());
		double x0 = xticks.get(0).getMidPoint().getX();
		// becuse of major ticks 18 => 23
		double x23 = xticks.get(18).getMidPoint().getX();
		double deltax = (x23 - x0)/23 * 1.002;
		SVGLineList yticks = getLineList(belc, ".//*[@class='yaxis.ticks']/*[local-name()='path']");
		// 134/8
		Assert.assertEquals(9, yticks.size());
		double y0 = yticks.get(0).getMidPoint().getY(); // this is actually the first point
		double y5 = yticks.get(5).getMidPoint().getY();
		double deltay = - (y5 - y0); // one unit
		double yorig1 = yticks.get(8).getMidPoint().getY(); // this is y=1
		double xkludge = 1.4; //becuase the plot is wonky
		Real2 origin = new Real2(x0 - xkludge, yorig1 + (-1) * deltay); // add 1 de
		
		// these are hacked up locally - will be better code in main, I think SVGBarredPoint
		
		SVGG placebo = extractCoordsLineErrbars(belc, "placebo", origin, deltax, deltay);
		SVGG mep = extractCoordsLineErrbars(belc, "mepolizumab", origin, deltax, deltay);
	}

	/** Figure 2 C from Bel2014 is an effect/dose/time plot 
	 * I have hacked by hand, later I will use Caches
	 */
	@Test
	public void testReadBelCachesC() {
		String fileroot = "bel";
		File sourceDir = new File(SVGHTMLFixtures.TIMECOURSE_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.TARGET_TIMECOURSE_DIR, fileroot);
		File file = new File(sourceDir, "svg/dosagec.svg"); 
		ComponentCache componentCache = ComponentCache.createComponentCache(file);
		List<SVGRect> rectList = componentCache.getOrCreateRectCache().getOrCreateRectList();
		List<SVGLine> lineList = componentCache.getOrCreateLineCache().getOrCreateLineList().getLineList();
//		LOG.debug("line "+lineList.size());
		SVGSVG.wrapAndWriteAsSVG(lineList, new File(targetDir, "lines.svg"));
//		LOG.debug("short hor line "+componentCache.getOrCreateLineCache().getOrCreateShortHorizontalLineList().size());
//		LOG.debug("long hor line "+componentCache.getOrCreateLineCache().getOrCreateLongHorizontalLineList().size());
//		LOG.debug("vert line "+componentCache.getOrCreateLineCache().getOrCreateVerticalLineList().size());
		List<SVGCircle> circleList = componentCache.getOrCreateShapeCache().getCircleList();
		SVGSVG.wrapAndWriteAsSVG(circleList, new File(targetDir, "circles.svg"));
//		LOG.debug("circle "+circleList.size());
		
		List<SVGText> horizontalTexts = componentCache.getOrCreateTextCache().getOrCreateHorizontalTexts();
		List<SVGTriangle> triangleList = componentCache.getOrCreateShapeCache().getTriangleList();
//		LOG.debug("triangle "+triangleList.size());
		SVGSVG.wrapAndWriteAsSVG(triangleList, new File(targetDir, "triangles.svg"));
		
		List<SVGPolygon> polygonList = componentCache.getOrCreateShapeCache().getPolygonList();
//		LOG.debug("polygon "+polygonList.size());
		SVGSVG.wrapAndWriteAsSVG(polygonList, new File(targetDir, "polygons.svg"));
		
//		LOG.debug("hor text "+horizontalTexts.size());
		SVGSVG.wrapAndWriteAsSVG(horizontalTexts, new File(targetDir, "horText.svg"));
		List<SVGText> verticalTexts = componentCache.getOrCreateTextCache().getOrCreateVerticalTexts();
//		LOG.debug("vert text "+verticalTexts.size());
		SVGSVG.wrapAndWriteAsSVG(verticalTexts, new File(targetDir, "verText.svg"));
		
	}

	
	

	
	// ===============================
	
	private SVGG extractCoordsLineErrbars(SVGElement svgElement, String title, Real2 origin, double deltax, double deltay) {
		int xplaces = 2;
		int yplaces = 3;

		Real2 negOrigin = origin.multiplyBy(-1.0);
		SVGElement plot = SVGUtil.getQuerySVGElements(svgElement, ".//*[@title='" + title + "' and @class='plot']").get(0);
		Assert.assertNotNull(plot);
		SVGLineList errorLineList = getLineList(plot, ".//*[@class='plot.errorbar.y']/*[local-name()='path']");
		getColumns(title, deltax, deltay, negOrigin, errorLineList, 0);
		getColumns(title, deltax, deltay, negOrigin, errorLineList, 1);
		SVGLineList plotLineList = getLineList(plot, ".//*[@class='plot.polypath']/*[local-name()='path']");
		Assert.assertEquals(24, plotLineList.size());
		Real2Array points = getPointsFromPolypath(plotLineList);
		points = points.plusEquals(negOrigin);
		RealArray xpointsRaw = points.getXArray();
		RealArray xpoints = xpointsRaw.multiplyBy(1./deltax).format(xplaces);
		RealArray ypointsRaw = points.getYArray();
		RealArray ypoints = ypointsRaw.multiplyBy(1./deltay).format(yplaces);
//		LOG.debug(title+".score : "+ypoints);
//		LOG.debug(title+".week : "+xpoints);
		return null;
	}

	private void getColumns(String title, double deltax, double deltay, Real2 negOrigin, SVGLineList errorLineList, int errend) {
		int xplaces = 2;
		int yplaces = 3;
		Real2Array error0 = getPointsFromLineList(errorLineList, errend);
		Real2Array error00 = error0.plusEquals(negOrigin);
		RealArray error0x = error00.getXArray();
		error0x = error0x.multiplyBy(1./deltax).format(xplaces);
		RealArray error0y = error00.getYArray();
		error0y = error0y.multiplyBy(1./deltay).format(yplaces);
//		LOG.debug(title+".week: "+error0x);
//		LOG.debug(title+".score."+(errend == 0 ? "min" : "max")+": "+error0y);
	}

	private Real2Array getPointsFromLineList(SVGLineList lineList, int serial) {
		Real2Array points = new Real2Array();
		for (SVGLine line : lineList) {
			points.addElement(line.getXY(serial));
		}
		return points;
	}

	private Real2Array getPointsFromPolypath(SVGLineList lineList) {
		Real2Array points = new Real2Array();
		points.addElement(lineList.get(0).getXY(0));
		for (SVGLine line : lineList) {
			points.addElement(line.getXY(1));
		}
		return points;
	}

	private SVGLineList getPlotLineList(SVGElement plot, String xpath, String xpath2) {
		SVGElement errorbarsY = SVGUtil.getQuerySVGElements(plot, xpath).get(0);
		Assert.assertNotNull(errorbarsY);
		SVGLineList lineList = getLineList(errorbarsY, xpath2);
		return lineList;
	}

	private SVGLineList getLineList( SVGElement svgElement, String xpath) {
		List<SVGElement> errorY = SVGUtil.getQuerySVGElements(svgElement, xpath);
		List<SVGPath> paths = SVGPath.extractPaths(errorY);
		Path2ShapeConverter converter = new Path2ShapeConverter();
		SVGLineList lineList = new SVGLineList();
		for (SVGPath path : paths) {
			lineList.add((SVGLine) converter.convertPathToShape(path));
		}
		return lineList;
	}

	private Real2Array extractCoords(SVGElement svgElement, String title) {
		List<SVGElement> plot = SVGUtil.getQuerySVGElements(svgElement, ".//*[@title='" + title + "' and @class='plot']");
		Assert.assertEquals(1,  plot.size());
		SVGElement placeboPlot = plot.get(0);
		List<SVGElement> childs =  SVGUtil.getQuerySVGElements(placeboPlot, "./*[@class='plot.polyline']");
		Assert.assertEquals(1,  childs.size());
		List<SVGPath> paths = SVGPath.extractPaths(childs.get(0));
		Assert.assertEquals(1,  paths.size());
		SVGPath path = paths.get(0);
		String d = path.getDString();
		PathPrimitiveList prims = PathPrimitiveList.createPrimitiveList(d);
		Real2Array coords = prims.getOrCreateCoordinates();
		return coords;
	}
	
	private Real2Array extractCoordsBrokenSteps(SVGElement svgElement, String title) {
		List<SVGElement> plots = SVGUtil.getQuerySVGElements(svgElement, ".//*[@title='" + title + "' and @class='plot']");
		Assert.assertEquals(1,  plots.size());
		SVGElement plot = plots.get(0);
//		LOG.debug("sub "+plot.toXML());
		List<SVGElement> childs =  SVGUtil.getQuerySVGElements(plot, "./*[@class='plot.polypath']");
		Assert.assertEquals(1,  childs.size());
		List<SVGPath> paths = SVGPath.extractPaths(childs.get(0));
		Assert.assertEquals(1,  paths.size());
		SVGPath path = paths.get(0);
		String d = path.getDString();
		PathPrimitiveList prims = PathPrimitiveList.createPrimitiveList(d);
		Real2Array coords = prims.getOrCreateCoordinates();
		return coords;
	}
	
	private Real2Array extractCoordsSteps(SVGElement svgElement, String title) {
		List<SVGElement> plot = SVGUtil.getQuerySVGElements(svgElement, ".//*[@title='" + title + "' and @class='plot']");
//		Assert.assertEquals(1,  plot.size());
		SVGElement placeboPlot = plot.get(0);
		List<SVGElement> childs =  SVGUtil.getQuerySVGElements(placeboPlot, "./*[@class='plot.polyline']");
//		Assert.assertEquals(1,  childs.size());
//		LOG.debug("steps "+childs);
		List<SVGPath> paths = SVGPath.extractPaths(childs.get(0));
//		Assert.assertEquals(1,  paths.size());
		SVGPath path = paths.get(0);
		String d = path.getDString();
		PathPrimitiveList prims = PathPrimitiveList.createPrimitiveList(d);
		Real2Array coords = prims.getOrCreateCoordinates();
		return coords;
	}
}
