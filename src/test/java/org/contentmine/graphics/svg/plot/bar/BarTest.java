package org.contentmine.graphics.svg.plot.bar;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;
import org.contentmine.graphics.svg.plot.XYPlotBox;
import org.contentmine.graphics.svg.plot.YPlotBox;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class BarTest {
	private static final Logger LOG = Logger.getLogger(BarTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testBar1() throws IOException {
		String fileRoot = "../bar/art%3A10.1186%2Fs13148-016-0230-5/svg/fig3";
		AbstractPlotBox plotBox = new XYPlotBox();
		File inputSVGFile = new File(SVGHTMLFixtures.G_S_PLOT_DIR, fileRoot+".svg");
		plotBox.readGraphicsComponents(inputSVGFile);
		plotBox.writeProcessedSVG(new File("target/bar/"+fileRoot+".svg"));
	}

	@Test
	/** several bars
	 *  // not yet working
	 * @throws IOException
	 */
	@Ignore
	public void testBarPlot() throws IOException {
		String fileRoot = "barchart1.10";
		AbstractPlotBox plotBox = new XYPlotBox();
		File inputSVGFile = new File(SVGHTMLFixtures.G_S_BAR_DIR, fileRoot+".svg");
		try {
			plotBox.readAndCreateCSVPlot(inputSVGFile);
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			Assert.assertEquals("No axial tickbox: BOTTOM", e.getMessage());
//			Assert.assertEquals("no axial tickbox", "null", String.valueOf(e.getMessage()));
		}
		plotBox.writeProcessedSVG(new File("target/bar/"+fileRoot+".svg"));
	}
	
	@Test
	/** two simple bars
	 * NYI
	 * @throws IOException
	 */
	public void testBarPlot1() throws IOException {
		String fileRoot = "figure4.2";
		AbstractPlotBox plotBox = new XYPlotBox();
		File inputSVGFile = new File(SVGHTMLFixtures.G_S_BAR_DIR, fileRoot+".svg");
		try {
			plotBox.readAndCreateCSVPlot(inputSVGFile);
		} catch (RuntimeException e) {
//			Assert.assertNull(""+e, e.getMessage());
			Assert.assertEquals("No Axial TICKBOX", null, e.getMessage());
		}
		plotBox.writeProcessedSVG(new File("target/bar/"+fileRoot+".svg"));
	}

	@Test
	/** this is a bar plot and we need to FIXME the bottom axis.
	 * 
	 */
	public void testBarNatureP3a() {
		String fileroot = "figure";
		File inputDir = new File(SVGHTMLFixtures.G_S_FIGURE_DIR, "nature/p3.a");
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue(""+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		YPlotBox mediaBox = new YPlotBox();
		try {
			mediaBox.readAndCreateBarPlot(svgElement);
		} catch (RuntimeException e) {
			LOG.error("FIXME: "+e.getMessage());
		}
	}

}
