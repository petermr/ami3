package org.contentmine.graphics.svg.plot.forest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ColorStore;
import org.contentmine.graphics.svg.util.ColorStore.ColorizerType;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class ForestTestIT {
	public static final Logger LOG = Logger.getLogger(ForestTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static File inputDir = SVGHTMLFixtures.FOREST_DIR;
	private static File targetDir = new File("target/forest/");
	private static String[] FILL = new String[] { "orange", "green", "blue", "red", "cyan" };
	private File outputFile;




	@Test
	public void testColorize() throws IOException {
		for (String fileRoot : new String[] {
			"blue",
			"gr1a",
			"gr2a",
			"srep36553-f2",
			"f2a",
			"srep44789-f5",
			"f5a",
			"tableBlue",
			}) {
			File forestFile = new File(ImageAnalysisFixtures.FOREST_DIR, fileRoot + ".png");

			ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
			colorAnalyzer.readImage(forestFile);
			colorAnalyzer.setOutputDirectory(new File("target/"+fileRoot));
			LOG.debug("colorAnalyze "+fileRoot);
			colorAnalyzer.defaultPosterize();
		}
	}

	//============
	
	private void plotPoints(String fileRoot, int isl, PixelRingList pixelRingList, int maxSize) {
		int maxSize0 = maxSize - 1;
		SVGG g = new SVGG();
		if (pixelRingList.size() > maxSize) {
			PixelRing outline = pixelRingList.get(maxSize).getPixelsTouching(pixelRingList.get(maxSize0));
			outline.plotPixels(g, "black");
			// this is the outline of the symbol
			File file = new File("target/" + fileRoot + "/plotLine"+isl+"Points"+maxSize0+""+maxSize+".svg");
//			SVGSVG.wrapAndWriteAsSVG(g, file);
			LOG.debug("plotted points "+file+"/"+FileUtils.sizeOf(file));
		}
	}

}
