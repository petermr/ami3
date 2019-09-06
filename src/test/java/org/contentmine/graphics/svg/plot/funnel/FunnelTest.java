package org.contentmine.graphics.svg.plot.funnel;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.plot.PlotStructurerTest;
import org.junit.Ignore;
import org.junit.Test;

public class FunnelTest {
	
	private static final String SVG_SUFFIX = "a.svg";
	public static final Logger LOG = Logger.getLogger(FunnelTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	@Test
	public void testBakker_Funnel() throws IOException {
		String fileRoot = "bakker2014-page11";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	/** glyph-based characters
	 * problems with points
	 * 
	 * @throws IOException
	 */
	public void testBooth_Funnel() throws IOException {
		String fileRoot = "booth2010-page18";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	/** glyph-based characters
	 * 
	 * @throws IOException
	 */
	public void testCalvin_Funnel() throws IOException {
		String fileRoot = "calvin2011-page12";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX),
			fileRoot
		);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	/** glyph-based characters
	 * 
	 * @throws IOException
	 */
	public void testChoi_Funnel() throws IOException {
		String fileRoot = "choi2012-page5";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	/** 
	 * 
	 * @throws IOException
	 */
	public void testDong_Funnel() throws IOException {
		String fileRoot = "dong2009-page4";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	/** 
	 * 
	 * @throws IOException
	 */
	public void testKerr_Funnel() throws IOException {
		String fileRoot = "kerr2012-page5";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	/** 
	 * 
	 * @throws IOException
	 */
	public void testNair_Funnel() throws IOException {
		String fileRoot = "nair2014-page4";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot+SVG_SUFFIX),	fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	public void testRogers1_Funnel() throws IOException {
		String fileRoot = "rogers2009-page44";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}

	@Test
	@Ignore // says rel cubic not supported, but ...
	public void testSAGE_Funnel() throws IOException {
		String fileRoot = "SAGE_Sbarra_funnel.g.11.0";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
	}
	
	@Test
	public void testChrisTest() throws IOException {
		String fileRoot = "christest";
		File svgFile = PlotStructurerTest.createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.FUNNEL_DIR, fileRoot + SVG_SUFFIX), fileRoot);
		PlotStructurerTest.createPlots(svgFile, fileRoot);
//		PlotStructurerTest.createCSV(svgFile, fileRoot);
	}
	
	// =======================
	



}
