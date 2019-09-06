package org.contentmine.svg2xml.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Test;


/**
 * 
 * @author pm286
 *
 */
public class PDFRegionTest {
	private static final Logger LOG = Logger.getLogger(PDFRegionTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final int SHRINK = -5;

	@Test
	public void testExtractRegion() throws FileNotFoundException {
		File pageSvgFile = new File(SVG2XMLFixtures.TABLE_DIR, "page5rect.svg");
		SVGSVG pageSVG = (SVGSVG) SVGUtil.parseToSVGElement(new FileInputStream(pageSvgFile));
		List<SVGElement> svgElements1 = SVGUtil.getQuerySVGElements(pageSVG, ".//*");
		List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(pageSVG);
		Assert.assertEquals(2,  rects.size());
		extractAndDrawRect(svgElements1, rects.get(0), new File("target/extract/page5.1.svg"), 
				"((36.1,565.8),(472.7,754.4))", 1127, true);
		extractAndDrawRect(svgElements1, rects.get(1), new File("target/extract/page5.2.svg"), 
				"((42.8,292.7),(48.4,398.7))", 697, true);
	}

	private void extractAndDrawRect(List<SVGElement> svgElements1, SVGRect rect0, File file, 
			String bboxRef, int nelems, boolean omitRect) {
		Real2Range bbox = rect0.getBoundingBox().format(1);
		Assert.assertEquals(bboxRef, bbox.toString());
		if (omitRect) {
			bbox = bbox.getReal2RangeExtendedInX(SHRINK, SHRINK);
			bbox = bbox.getReal2RangeExtendedInY(SHRINK, SHRINK);
		}
		List<SVGElement> svgElements = SVGUtil.findElementsIntersecting(bbox, svgElements1);
		Assert.assertEquals(nelems,  svgElements.size());
		SVGG g = new SVGG();
		for (AbstractCMElement svgElement : svgElements) {
			g.appendChild(svgElement.copy());
		}
		SVGSVG.wrapAndWriteAsSVG(g, file);
	}
}
