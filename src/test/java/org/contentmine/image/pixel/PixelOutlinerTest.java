package org.contentmine.image.pixel;

import java.io.File;
import java.util.List;

import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

public class PixelOutlinerTest {

	@Test
	public void testAnalyzeOutline() {
		PixelList diamond = PixelListTest.CREATE_DIAMOND();
		PixelOutliner outliner = new PixelOutliner(diamond);
		outliner.setMaxIter(50);
		outliner.createOutline();
	}
	
	@Test
	public void testAnalyzeOutlineWithSpiroJunction() {
		PixelList spiro = PixelListTest.CREATE_DIAMOND_SPIRO();
		PixelOutliner outliner = new PixelOutliner(spiro);
		outliner.setMaxIter(50);
		outliner.createOutline();
	}
	
	@Test
	public void testAnalyzeOutlineSegments() {
		PixelList diamond = PixelListTest.CREATE_DIAMOND();
		PixelOutliner outliner = new PixelOutliner(diamond);
		outliner.createOutline();
		SVGPoly polygon = outliner.getSVGPolygon();
		SVGG g = new SVGG();
		g.appendChild(diamond.getOrCreateSVG());
		g.appendChild(polygon);
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/pixels/diamondPolygon.svg"));
	}
	
	@Test
	public void testTwoIsland() {
		PixelList diamond = PixelListTest.CREATE_TWO_ISLANDS();
		PixelOutliner outliner = new PixelOutliner(diamond);
		outliner.createOutline();
		List<SVGPolygon> polygonList = outliner.getPolygonList();
		Assert.assertEquals("polygons", 2, polygonList.size());
		Assert.assertEquals("polygon0", 24, polygonList.get(0).size());
		Assert.assertEquals("polygon1", 24, polygonList.get(1).size());
		SVGG g = new SVGG();
		for (SVGPoly polygon : polygonList) {
			g.appendChild(polygon);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/pixels/twoIslands.svg"));
	}
	




}
