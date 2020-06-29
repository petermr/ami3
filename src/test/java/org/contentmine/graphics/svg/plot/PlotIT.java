package org.contentmine.graphics.svg.plot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore("This really should be in POM or CL")
public class PlotIT extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(PlotIT.class);
@Test
	//	@Ignore // too long
	public void testConvertAllSVG2CSV() throws IOException {
		String[] fileRoots = {
//			"bakkerplot", // OK
			"calvinplot", // OK
//			"dongplot",   // OK
//			"kerrplot",   // No fullbox
//			"nairplot",   // OK
//			"sbarraplot"  // OK
			};
		for (String fileRoot : fileRoots) {
			System.out.println("===="+fileRoot+"=====");;
			AbstractPlotBox plotBox = new XYPlotBox();
			File inputSVGFile = new File(SRC_TEST_PLOT, fileRoot + ".svg");
			try {
				plotBox.readAndCreateCSVPlot(inputSVGFile);
			} catch (RuntimeException e) {
				e.printStackTrace();
				continue;
			}
			plotBox.writeProcessedSVG(new File(PlotBoxTest.TARGET_PLOT+fileRoot+".svg"));
			File csvFile = new File(PlotBoxTest.TARGET_PLOT+fileRoot+".csv");
			LOG.debug("csv: "+csvFile);
			plotBox.writeCSV(csvFile);
		}
	}

}
