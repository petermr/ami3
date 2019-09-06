package org.contentmine.svg2xml.figure;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGTextComparator;
import org.contentmine.graphics.svg.SVGTextComparator.TextComparatorType;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;
import org.contentmine.graphics.svg.plot.YPlotBox;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class FigureTestOLD {
	private static final Logger LOG = Logger.getLogger(FigureTestOLD.class);
	private static final double DISTANCE_DELTA = 1.0;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	@Ignore // input files missing
	public void testFigure() {
		String fileroot = "page";
		String dirRoot = "nature/p2";
		File outputDir = new File("target/figures/", dirRoot);
		File inputDir = new File(SVG2XMLFixtures.FIGURE_DIR, dirRoot);
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
			LOG.debug(label.toXML());
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, "rects.svg"));
		
	}

	@Test
	@Ignore // input files missing
	public void testFigureErrorBars() {
		String fileroot = "figure";
		String dirRoot = "nature/p3.a";
		File outputDir = new File("target/figures/", dirRoot);
		File inputDir = new File(SVG2XMLFixtures.BAR_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		AbstractPlotBox yPlotBox = new YPlotBox();
		ComponentCache componentCache = new ComponentCache(yPlotBox);
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		SVGG g = extractErrorBars(componentCache);
		
		File file = new File(outputDir, "errorBars.svg");
		LOG.debug("wrote "+file.getAbsolutePath());
		SVGSVG.wrapAndWriteAsSVG(g, file);
		Assert.assertTrue("exists "+file, file.exists());
	}

	private SVGG extractErrorBars(ComponentCache componentCache) {
		List<SVGLine> horizontalLines = componentCache.getOrCreateLineCache().getOrCreateHorizontalLineList();
		List<SVGLine> verticalLines = componentCache.getOrCreateLineCache().getOrCreateVerticalLineList();
		SVGG g = new SVGG();
		for (SVGLine verticalLine : verticalLines) {
			SVGElement horizontal0 = getTJunction(horizontalLines, verticalLine, 0);
			SVGElement horizontal1 = getTJunction(horizontalLines, verticalLine, 1);
			if (horizontal0 != null && horizontal1 != null) {
				AbstractCMElement gt = new SVGG();
				gt.appendChild(verticalLine.copy());
				gt.appendChild(horizontal0.copy());
				gt.appendChild(horizontal1.copy());
				g.appendChild(gt);
			}
		}
		return g;
	}

	private SVGElement getTJunction(List<SVGLine> horizontalLines, SVGLine verticalLine, int end) {
		for (SVGLine horizontalLine : horizontalLines) {
			Real2 midPoint = horizontalLine.getMidPoint();
			double dist = midPoint.getDistance(verticalLine.getXY(end));
			if (dist < DISTANCE_DELTA) {
				return horizontalLine;
			}
		}
		return null;
	}

	// ==============================================
	
	
}

