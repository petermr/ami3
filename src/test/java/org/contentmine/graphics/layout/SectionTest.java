package org.contentmine.graphics.layout;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.junit.Test;

import junit.framework.Assert;

public class SectionTest {
	private static final Logger LOG = Logger.getLogger(SectionTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCaptureMarkedSections() {
		File targetDir = new File("target/layout/bmc");
		SVGHTMLFixtures.cleanAndCopyDir(new File(SVGHTMLFixtures.GR_LAYOUT_DIR, "bmc/"), targetDir);
		File svgFile = new File(targetDir, "middle3.html.svg");
		ComponentCache componentCache = ComponentCache.readAndCreateComponentCache(svgFile);
		List<SVGRect> rectList = componentCache.getOrCreateRectCache().getOrCreateRectList();
		Assert.assertEquals(18, rectList.size());
		List<SVGRect> markedRectList = SVGRect.extractRects(SVGElement.extractElementList(rectList, "self::*[*[local-name()='title']]"));
		Assert.assertEquals(9, markedRectList.size());
		List<SVGText> textList = componentCache.getOrCreateTextCache().getOrCreateOriginalTextList();
		Assert.assertEquals(198, textList.size());
		SVGG g = new SVGG();
		for (SVGRect markedRect : markedRectList) {
			Real2Range bbox = markedRect.getBoundingBox();
			if (bbox != null) {
				List<SVGElement> newTextList = SVGElement.extractElementsContainedInBox(textList, bbox);
				SVGElement.appendCopy(g, newTextList);
				Real2Range newBox = SVGElement.createBoundingBox(newTextList);
				SVGRect rect0 = (SVGRect) markedRect.copy();
				rect0.setFill("pink").setOpacity(0.3).setFill("none").setStrokeDashArray("5 5");
				g.appendChild(rect0);
				SVGRect newRect = SVGRect.createFromReal2Range(newBox);
				newRect.setFill("none").setStroke("blue");
				g.appendChild(newRect);
			}
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "boxes.svg"));
	}

}
