package org.contentmine.image.plot;

import java.io.File;
import java.io.IOException;

import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.OutlineTester;
import org.junit.Test;

public class PlotIT {

	/** several lines emerging from one point.
	 * creates nested outlines.
	 * 
	 * xyplot with gridlines.
	 * 
	 */
	
	@Test
	public void testDrainSource2OutlinesCode() {
		OutlineTester outlineTester = new OutlineTester();
		outlineTester.expectedRingSizes = new int[][] {
			new int[]{32104,13784,7343},
			new int[]{92,50,1},
		};
		outlineTester.nodes = new int[] {19,1,0,0};
		outlineTester.edges = new int[] {20,1,0,0};
		outlineTester.outlines = new int[] {7388, 3174};
		
		outlineTester.dir = "electronic";
		outlineTester.inname = "drainsource2";
		outlineTester.outdir = ImageAnalysisFixtures.TARGET_ELECTRONIC_DIR;
		outlineTester.indir = ImageAnalysisFixtures.ELECTRONIC_DIR;
		
		outlineTester.islandCount = 40;
		outlineTester.mainIslandCount = 2;
		outlineTester.pixelRingListCount = new int[] {8, 2};
	
		outlineTester.analyzeAndAssertFile();
	
	}

	@Test
	/** hi-res bitmap that gives very good thinning
	 * reveals bar plots and characters (not interpreted)
	 * @throws IOException
	 */
	public void test004179BarChart() throws IOException {
		PlotTest.plotRingsAndThin(new File(ImageAnalysisFixtures.COMPOUND_DIR,
				"journal.pone.0094179.g008.png"), new File(
				"target/plot/0094179_8.svg"), new File(
				"target/plot/0094179_8_2.svg"));
	}

}
