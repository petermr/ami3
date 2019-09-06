package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.junit.Test;

import junit.framework.Assert;

/** tests the creation of pixel rings.
 * Creation is currently from PixelIslands
 * 
 * @author pm286
 *
 */
public class PixelRingTest {
	private static final Logger LOG = Logger.getLogger(PixelRingTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static String PIXEL_RINGS = "pixelRings";
	public static File TARGET_PIXEL_RINGS = new File(CMineFixtures.TARGET_DIR, PIXEL_RINGS+"/");

	@Test
	public void testCreateRingsDot() {
			PixelList pixelList = new PixelList();
			pixelList.add(new Pixel(2, 2));
			boolean diagonal = true;
			PixelIsland pixelIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelList, diagonal);
			PixelRingList pixelRingList = pixelIsland.getOrCreateInternalPixelRings();
			Assert.assertEquals(pixelRingList.size(), 1);
			Assert.assertEquals(pixelRingList.get(0).size(), 1);
	}
					
	@Test
	public void testCreateRingsSquare2x2() {
		int size = 5;
			PixelList pixelList = new PixelList();
			for (int x = 1; x < 3; x++) {
				int y = 1;
				pixelList.add(new Pixel(x, y  ));
				pixelList.add(new Pixel(x, y + 1));
			}
			boolean diagonal = true;
			PixelIsland pixelIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelList, diagonal);
			Assert.assertEquals(4, pixelIsland.size());
			PixelRingList pixelRingList = pixelIsland.getOrCreateInternalPixelRings();
			Assert.assertEquals(pixelRingList.size(), 1);
			Assert.assertEquals(pixelRingList.get(0).size(), 4);
			pixelRingList.get(0).sortXY();
			Assert.assertEquals("(1,1)(1,2)(2,1)(2,2)", pixelRingList.get(0).toString());
			PixelRing outline = pixelRingList.getOrCreateOutline();
			Assert.assertNull("only 1 ring so null", outline); 
			PixelRing outerRing = pixelRingList.getOuterPixelRing();
			Assert.assertEquals("only 1 ring ", 4, outerRing.size()); 
			outerRing.sortXY();
			Assert.assertEquals("(1,1)(1,2)(2,1)(2,2)", outerRing.toString());
			SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), new File(TARGET_PIXEL_RINGS, "square2x2.svg"));

	}

	@Test
	public void testCreateRingsSquare3x3() {
		PixelList pixelList = new PixelList();
		for (int x = 1; x < 4; x++) {
			int y = 1;
			pixelList.add(new Pixel(x, y  ));
			pixelList.add(new Pixel(x, y + 1));
			pixelList.add(new Pixel(x, y + 2));
		}
		boolean diagonal = true;
		PixelIsland pixelIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelList, diagonal);
		Assert.assertEquals(9, pixelIsland.size());
		pixelIsland.sortXY();
		Assert.assertEquals("(1,1)(1,2)(1,3)(2,1)(2,2)(2,3)(3,1)(3,2)(3,3)",
				pixelIsland.getPixelList().toString());
		PixelRingList pixelRingList = pixelIsland.getOrCreateInternalPixelRings();
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), new File(TARGET_PIXEL_RINGS, "square3x3.svg"));
		// These need mending
		Assert.assertEquals(2, pixelRingList.size());
		Assert.assertEquals(pixelRingList.get(0).size(), 8);
		pixelRingList.get(0).sortXY();
		Assert.assertEquals("(1,1)(1,2)(1,3)(2,1)(2,3)(3,1)(3,2)(3,3)", pixelRingList.get(0).toString());
		pixelRingList.get(1).sortXY();
		Assert.assertEquals("(2,2)", pixelRingList.get(1).toString());
	}

	@Test
	public void testCreateRingsRect3x4() {
		PixelList pixelList = new PixelList();
		for (int x = 1; x < 4; x++) {
			int y = 1;
			pixelList.add(new Pixel(x, y  ));
			pixelList.add(new Pixel(x, y + 1));
			pixelList.add(new Pixel(x, y + 2));
			pixelList.add(new Pixel(x, y + 3));
		}
		boolean diagonal = true;
		PixelIsland pixelIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelList, diagonal);
		Assert.assertEquals(12, pixelIsland.size());
		pixelIsland.sortXY();
		Assert.assertEquals("(1,1)(1,2)(1,3)(1,4)(2,1)(2,2)(2,3)(2,4)(3,1)(3,2)(3,3)(3,4)",
				pixelIsland.getPixelList().toString());
		PixelRingList pixelRingList = pixelIsland.getOrCreateInternalPixelRings();
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), new File(TARGET_PIXEL_RINGS, "rect3x4.svg"));
		// These need mending
		Assert.assertEquals(2, pixelRingList.size());
		Assert.assertEquals(pixelRingList.get(0).size(), 10);
		pixelRingList.get(0).sortXY();
		Assert.assertEquals("(1,1)(1,2)(1,3)(1,4)(2,1)(2,4)(3,1)(3,2)(3,3)(3,4)", pixelRingList.get(0).toString());
		pixelRingList.get(1).sortXY();
		Assert.assertEquals("(2,2)(2,3)", pixelRingList.get(1).toString());

	}
	
	@Test
	public void testCreateRings2InnerIslands() {
		PixelList pixelList = new PixelList();
		for (int x = 1; x < 5; x++) {
			int y = 1;
			pixelList.add(new Pixel(x, y  ));
			pixelList.add(new Pixel(x, y + 1));
			pixelList.add(new Pixel(x, y + 2));
			pixelList.add(new Pixel(x, y + 3));
		}
		pixelList.add(new Pixel(4, 5));
		pixelList.add(new Pixel(4, 6));
		for (int x = 5; x < 7; x++) {
			int y = 4;
			pixelList.add(new Pixel(x, y  ));
			pixelList.add(new Pixel(x, y + 1));
			pixelList.add(new Pixel(x, y + 2));
		}
		boolean diagonal = true;
		PixelIsland pixelIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelList, diagonal);
		Assert.assertEquals(24, pixelIsland.size());
		pixelIsland.sortXY();
		Assert.assertEquals("2 rings", ""
				+ "(1,1)(1,2)(1,3)(1,4)"
				+ "(2,1)(2,2)(2,3)(2,4)"
				+ "(3,1)(3,2)(3,3)(3,4)"
				+ "(4,1)(4,2)(4,3)(4,4)(4,5)(4,6)"
				+ "(5,4)(5,5)(5,6)"
				+ "(6,4)(6,5)(6,6)",
				pixelIsland.getPixelList().toString());
		PixelRingList pixelRingList = pixelIsland.getOrCreateInternalPixelRings();
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), new File(TARGET_PIXEL_RINGS, "inner2.svg"));
		// These need mending
		Assert.assertEquals(2, pixelRingList.size());
		Assert.assertEquals(19, pixelRingList.get(0).size());
		pixelRingList.get(0).sortXY();
//		(1,1)(1,2)(1,3)(1,4)(2,1)(2,2)(2,3)(2,4)(3,1)(3,2)(3,3)(3,4)(4,1)(4,2)(4,3)(4,4)(4,5)(4,6)(5,4)(5,5)(5,6)(6,4)(6,5)(6,6)
		
		Assert.assertEquals("ring 0", ""
				+ "(1,1)(1,2)(1,3)(1,4)"
				+ "(2,1)(2,4)"
				+ "(3,1)(3,4)"
				+ "(4,1)(4,2)(4,3)(4,4)(4,5)(4,6)"
				+ "(5,4)(5,6)"
				+ "(6,4)(6,5)(6,6)", pixelRingList.get(0).toString());
		pixelRingList.get(1).sortXY();
		PixelRing ringList1 = pixelRingList.get(1);
		Assert.assertEquals("2 islands",""
				+ "(2,2)(2,3)(3,2)(3,3)"
				+ "(5,5)", 
				ringList1.toString());
		PixelIslandList pixelIslandList = PixelIslandList.createPixelIslandList((PixelList)ringList1);
		Assert.assertEquals("number of islands level 1", 2, pixelIslandList.size());
		return;
	}

	@Test
	public void testCreateRings2InnerIslandsDepth3() {
		PixelList pixelList = new PixelList();
		for (int x = 1; x < 6; x++) {
			int y = 1;
			pixelList.add(new Pixel(x, y  ));
			pixelList.add(new Pixel(x, y + 1));
			pixelList.add(new Pixel(x, y + 2));
			pixelList.add(new Pixel(x, y + 3));
			pixelList.add(new Pixel(x, y + 4));
		}
		pixelList.add(new Pixel(6, 5));
		pixelList.add(new Pixel(7, 5));
		pixelList.add(new Pixel(8, 5));
		for (int x = 4; x < 9; x++) {
			int y = 6;
			pixelList.add(new Pixel(x, y  ));
			pixelList.add(new Pixel(x, y + 1));
			pixelList.add(new Pixel(x, y + 2));
			pixelList.add(new Pixel(x, y + 3));
		}
		boolean diagonal = true;
		PixelIsland pixelIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelList, diagonal);
		Assert.assertEquals(48, pixelIsland.size());
		pixelIsland.sortXY();
		Assert.assertEquals("2 rings", ""
				+ "(1,1)(1,2)(1,3)(1,4)(1,5)"
				+ "(2,1)(2,2)(2,3)(2,4)(2,5)"
				+ "(3,1)(3,2)(3,3)(3,4)(3,5)"
				+ "(4,1)(4,2)(4,3)(4,4)(4,5)(4,6)(4,7)(4,8)(4,9)"
				+ "(5,1)(5,2)(5,3)(5,4)(5,5)(5,6)(5,7)(5,8)(5,9)"
				+ "(6,5)(6,6)(6,7)(6,8)(6,9)"
				+ "(7,5)(7,6)(7,7)(7,8)(7,9)"
				+ "(8,5)(8,6)(8,7)(8,8)(8,9)",
				pixelIsland.getPixelList().toString());
		PixelRingList pixelRingList = pixelIsland.getOrCreateInternalPixelRings();
		SVGSVG.wrapAndWriteAsSVG(pixelRingList.plotPixels(), new File(TARGET_PIXEL_RINGS, "inner2depth3.svg"));
		// These need mending
		Assert.assertEquals(3, pixelRingList.size());
		Assert.assertEquals(30, pixelRingList.get(0).size());
		pixelRingList.get(0).sortXY();
		
		Assert.assertEquals("ring 0", ""
				+ "(1,1)(1,2)(1,3)(1,4)(1,5)"
				+ "(2,1)(2,5)"
				+ "(3,1)(3,5)"
				+ "(4,1)(4,5)(4,6)(4,7)(4,8)(4,9)"
				+ "(5,1)(5,2)(5,3)(5,4)(5,5)(5,9)"
				+ "(6,5)(6,9)"
				+ "(7,5)(7,9)"
				+ "(8,5)(8,6)(8,7)(8,8)(8,9)",
				pixelRingList.get(0).toString());
		pixelRingList.get(1).sortXY();
		Assert.assertEquals("2 islands",""
				+ "(2,2)(2,3)(2,4)"
				+ "(3,2)(3,4)"
				+ "(4,2)(4,3)(4,4)"
				+ "(5,6)(5,7)(5,8)"
				+ "(6,6)(6,8)"
				+ "(7,6)(7,7)(7,8)",
				pixelRingList.get(1).toString());

	}
	
	@Test 
	public void testClipVertical() {
		String root = "graphGrid";
		File sourceFile = new File(ImageAnalysisFixtures.TEST_DIAGRAM_PLOT_MISC_DIR, root+".png");
		LOG.debug(sourceFile);
		BufferedImage image = ImageUtil.readImage(sourceFile);
		Assert.assertNotNull(image);
		image = ImageUtil.clipSubImage(image, new Int2Range(new IntRange(150, 300), new IntRange(0, 100)));
		ImageUtil.writePngQuietly(image, new File(TARGET_PIXEL_RINGS, root+".png"));
		DiagramAnalyzer diagramAnalyzer = DiagramAnalyzer.processUnthinnedImage(image);
		PixelList pixelList = diagramAnalyzer.getPixelList();
		Assert.assertEquals("pixelList", 1637, pixelList.size());
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		pixelIslandList.sortBySizeDescending();
		Assert.assertEquals("pixelIslandList", 37, pixelIslandList.size());
		PixelIsland pixelIsland0 = pixelIslandList.get(0);
		Assert.assertEquals("pixelIsland0", 1465, pixelIsland0.size());
		SVGSVG.wrapAndWriteAsSVG(pixelIsland0.createSVG(), new File(TARGET_PIXEL_RINGS, root+".island0.svg"));
//		ImageUtil.writePngQuietly(image, new File(TARGET_PIXEL_RINGS, root+".island0.png"));
		PixelRingList pixelRingList = pixelIsland0.getOrCreateInternalPixelRings();
		Assert.assertEquals("pixelRingList size", 3, pixelRingList.size());
		Assert.assertEquals("pixelRing0", 970, pixelRingList.get(0).size());
		Assert.assertEquals("pixelRing1", 403, pixelRingList.get(1).size());
		Assert.assertEquals("pixelRing2", 92, pixelRingList.get(2).size());
	}

	
}
