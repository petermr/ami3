package org.contentmine.svg2xml.plot.bar;

import java.io.File;

import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.plot.YPlotBox;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** test bar plot
 * 
 * @author pm286
 *
 */
public class BarTest {

	@Test
	@Ignore // file does not exist
	public void testBar() {
		String fileroot = "figure";
		File inputDir = new File(SVG2XMLFixtures.BAR_DIR, "nature/p3.a");
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("file should exist"+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		YPlotBox mediaBox = new YPlotBox();
		mediaBox.readAndCreateBarPlot(svgElement);
	}
}
