package org.contentmine.graphics.svg.figure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGTextComparator;
import org.contentmine.graphics.svg.SVGTextComparator.TextComparatorType;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;
import org.contentmine.graphics.svg.plot.SVGBarredPoint;
import org.contentmine.graphics.svg.plot.XPlotBox;
import org.contentmine.graphics.svg.plot.YPlotBox;
import org.junit.Assert;
import org.junit.Test;

public class FigureTest {
	private static final Logger LOG = Logger.getLogger(FigureTest.class);
	private static final double DISTANCE_DELTA = 1.0;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** complete page with several typefaces and 2 separate diagrams.
	 * 
	 */
	public void testFigureLayout() {
		String fileroot = "page";
		String dirRoot = "nature/p2";
		File outputDir = new File("target/figures/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.G_S_FIGURE_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue(""+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
	//	List<SVGElement> largeCharsElements = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='text' and @font-size[.='12.0']]");
		List<SVGText> largeCharsText = SVGText.getQuerySVGTexts(svgElement, ".//*[local-name()='text' and @font-size[.='12.0']]");
		SVGSVG.wrapAndWriteAsSVG(largeCharsText, new File(outputDir, "largeChars.svg"));
		List<SVGElement> helveticaElements = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='text' and @font-family[.='Helvetica']]");
		SVGSVG.wrapAndWriteAsSVG(helveticaElements, new File(outputDir, "helvetica.svg"));
		List<SVGElement> timesElements = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='text' and @font-family[.='TimesNewRoman']]");
		SVGSVG.wrapAndWriteAsSVG(timesElements, new File(outputDir, "times.svg"));
		
		SVGG g = new SVGG();
		Collections.sort(largeCharsText, new SVGTextComparator(TextComparatorType.ALPHA));
		for (int i = 0; i < largeCharsText.size(); i++) {
			SVGText largeChar = (SVGText) largeCharsText.get(i);
			g.appendChild(largeChar.copy());
			Real2 xy = largeChar.getXY();
			SVGText label = new SVGText(xy, String.valueOf(i));
			label.setCSSStyle("font-size:5;fill:blue;");
			if (xy.getY() < 40) continue; // omit header
			xy.plusEquals(new Real2(0.0, -largeChar.getFontSize()));
			Real2 xy1 = xy.plus(new Real2(100, 60));
			SVGRect rect = new SVGRect(xy, xy1);
			label.translate(new Real2(10.,0));
			rect.setCSSStyle("fill:none;stroke-width:1.0;stroke:red;");
			g.appendChild(rect);
			g.appendChild(label);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, "rects.svg"));
		
	}

	/** single subpanel with simple bar chart.
	 * 
	 */
	@Test
	public void testFigureErrorBars() {
		String fileroot = "figure";
		String dirRoot = "nature/p3.a";
		File outputDir = new File("target/figures/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.G_S_FIGURE_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		AbstractPlotBox yPlotBox = new YPlotBox();
		ComponentCache componentCache = new ComponentCache(yPlotBox);
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		List<SVGLine> horizontalLines = componentCache.getOrCreateLineCache().getOrCreateHorizontalLineList();
		List<SVGLine> verticalLines = componentCache.getOrCreateLineCache().getOrCreateVerticalLineList();
		List<SVGBarredPoint> barredPoints = SVGBarredPoint.extractErrorBarsFromIBeams(horizontalLines, verticalLines);
		
		File file = new File(outputDir, "errorBars.svg");
		LOG.trace("wrote "+file.getAbsolutePath());
		SVGG g = new SVGG();
		// barred point isn't a true SVGElement yet so have to create the G
		for (SVGBarredPoint barredPoint : barredPoints) {
			g.appendChild(barredPoint.createSVGElement()); 
		}
		SVGSVG.wrapAndWriteAsSVG(g, file);
		LOG.trace("barred:"+file+";"+g.toXML());
		Assert.assertTrue("exists "+file, file.exists());
	}

	@Test
	/** figure has 14 subpanels (molecules) in 5 * 3 grid.
	 * NO separating lines - all done by whitespace
	 * at this stage just simple horizontal and vertical lines
	 * 
	 */
	public void textSplitSubPanels() {
		String fileroot = "figure1";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		List<Real2Range> bboxList = componentCache.getBoundingBoxList();
		SVGG g = new SVGG();
		addColouredBBoxes(bboxList, g);
		g.appendChild(svgElement.copy());
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, "bboxList.svg"));

		double interBoxMargin = 1.0; // fairly critical
		bboxList = mergeBoxesTillNoChange(bboxList, interBoxMargin);
		
		SVGG gg = new SVGG();
		List<SVGRect> rects = SVGRect.createFromReal2Ranges(bboxList, 1.0);
		SVGElement.setCSSStyle(rects, "fill:none;stroke:blue;stroke-width:0.5;");
		gg.appendChild(svgElement);
		gg.appendChildren(rects);
		SVGSVG.wrapAndWriteAsSVG(gg, new File(outputDir, "bboxMerged.svg"));
			
		
	}

	static void addColouredBBoxes(List<Real2Range> bboxList, SVGG g) {
		for (Real2Range bbox : bboxList) {
			if (bbox != null) {
				SVGRect rect = SVGRect.createFromReal2Range(bbox);
				rect.setCSSStyle("fill:none;stroke-width:0.5;stroke:red;");
				g.appendChild(rect);
			}
		}
	}
	
	// ==========================================

	static List<Real2Range> mergeBoxesTillNoChange(List<Real2Range> bboxList, double interBoxMargin) {
		while (true) {
			int start = bboxList.size();
			List<Real2Range> mergedBoxes = FigureTest.mergeBoxes(bboxList, interBoxMargin);
			int end = mergedBoxes.size();
			LOG.trace(start+", "+end);
			if (mergedBoxes.size() >= bboxList.size()) {
				break;
			}
			bboxList = mergedBoxes;
		}
		return bboxList;
	}

	static List<Real2Range> mergeBoxes(List<Real2Range> bboxList, double delta) {
		List<Real2Range> mergedBoxes = new ArrayList<Real2Range>();
		for (int i = 0; i < bboxList.size(); i++) {
			Real2Range bbox = bboxList.get(i);
			if (bbox != null) {
				boolean merged = false;
				for (int j = 0; j < mergedBoxes.size(); j++) {
					Real2Range mergedBox = mergedBoxes.get(j);
					if (mergedBox != null) {
						if (bbox.intersects(mergedBox, delta)) {
							merged = true;
							mergedBox.plusEquals(bbox);
						}
					}
				}
				if (!merged) {
					mergedBoxes.add(bbox);
				}
			}
		}
		return mergedBoxes;
	}

	// ==============================================
	
	
}

