package org.contentmine.image.plot.early.chem;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelIslandList;
import org.junit.Test;

import junit.framework.Assert;

/** extracts structures from adrenaline paper
 * 
 * @author pm286
 *
 */
public class ChemImageTest {
	private static final Logger LOG = LogManager.getLogger(ChemImageTest.class);
@Test
	public void testAdrenaline() {
		String fileRoot = "adrenaline";
		File targetDir = SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR;
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		int threshold = 120;
		diagramAnalyzer.getImageProcessor().setThreshold(threshold);
		diagramAnalyzer.setThinning(null);
		File file = new File(SVGHTMLFixtures.EARLY_CHEM_DIR, fileRoot+".png");
		diagramAnalyzer.readAndProcessInputFile(file);
		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "binarized.png"));
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreateSortedPixelIslandList();
		SVGSVG.wrapAndWriteAsSVG(pixelIslandList.get(0).getOrCreateSVGG(), new File(targetDir, "largestIsland.svg"));
		Assert.assertEquals("islands", 49, pixelIslandList.size());

	}
}
