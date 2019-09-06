package org.contentmine.graphics.svg.figure;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.plot.XPlotBox;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("This really should be in POM or CL")
public class FigureIT {
	private static final Logger LOG = Logger.getLogger(FigureIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** figure has 2 subpanels (molecules).
	 * NO separating lines - all done by whitespace
	 * at this stage just simple horizontal and vertical lines
	 * 
	 */
	public void textSplitSubPanels2() {
		String fileroot = "figure2graphic";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot+"/"+fileroot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		LOG.debug("created caches");
		List<Real2Range> bboxList = componentCache.getBoundingBoxList();
		SVGG g = new SVGG();
		FigureTest.addColouredBBoxes(bboxList, g);
		g.appendChild(svgElement.copy());
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, "bboxList.svg"));
	
		double interBoxMargin = 2.0; // fairly critical
		bboxList = FigureTest.mergeBoxesTillNoChange(bboxList, interBoxMargin);
		
		SVGG gg = new SVGG();
		List<SVGRect> rects = SVGRect.createFromReal2Ranges(bboxList, 1.0);
		SVGElement.setCSSStyle(rects, "fill:none;stroke:blue;stroke-width:0.5;");
		gg.appendChild(svgElement);
		gg.appendChildren(rects);
		SVGSVG.wrapAndWriteAsSVG(gg, new File(outputDir, "bboxMerged.svg"));		
	}

}
