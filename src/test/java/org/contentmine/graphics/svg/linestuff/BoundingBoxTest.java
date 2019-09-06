package org.contentmine.graphics.svg.linestuff;

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
import org.junit.Test;

public class BoundingBoxTest {
	private static final Logger LOG = Logger.getLogger(BoundingBoxTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/* line is vertical - should create a bbox
	 * 
	 */
	public void testAxialBBox() {
		String fileroot = "figure1.M1.mini";
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
		for (Real2Range bbox : bboxList) {
			if (bbox != null) {
				SVGRect boxRect = SVGRect.createFromReal2Range(bbox);
				boxRect.setCSSStyle("fill:none;stroke-width:2.0;stroke:red;");
	//			LOG.debug("BB "+bbox+"; "+boxRect.toXML()+"; "+bbox.getXRange());
				g.appendChild(boxRect);
			}
		}
		SVGElement figure = (SVGElement) svgElement.copy();
		g.appendChild(figure);
		figure.setCSSStyle("opacity:0.3;stroke:blue;"); 
		
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, "bboxList0.svg"));
			
		
	}


}
