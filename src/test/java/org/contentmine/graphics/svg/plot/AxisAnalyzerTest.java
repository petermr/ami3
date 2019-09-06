package org.contentmine.graphics.svg.plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class AxisAnalyzerTest {

	private static final Logger LOG = Logger.getLogger(AxisAnalyzerTest.class);

	@Test
	@Ignore // until we write the axis stuff
	public void testAxes() {
		AbstractCMElement g = SVGG.createSVGGChunk(SVGHTMLFixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.5);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		List<Axis> horizontalAxisList = axisAnalyzer.getHorizontalAxisList();
		Assert.assertEquals(1, horizontalAxisList.size());
		List<Axis> verticalAxisList = axisAnalyzer.getVerticalAxisList();
		Assert.assertEquals(1, verticalAxisList.size());
	}
	
	@Test
	/** the horizontal axis is not quite horizontal.
	 * 
	 */
	@Ignore // until we write the axis stuff
	public void testAxesSmallEpsilon() {
		AbstractCMElement g = SVGG.createSVGGChunk(SVGHTMLFixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.01);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		List<Axis> horizontalAxisList = axisAnalyzer.getHorizontalAxisList();
		Assert.assertEquals(0, horizontalAxisList.size());
		List<Axis> verticalAxisList = axisAnalyzer.getVerticalAxisList();
		Assert.assertEquals(1, verticalAxisList.size());
	}
	
	@Test
	@Ignore // until we write the axis stuff
	public void testPlotBoxLinePlots() {
		AbstractCMElement g = SVGG.createSVGGChunk(SVGHTMLFixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.5);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		GraphPlotBox plotBox = axisAnalyzer.getPlotBox();
		Assert.assertNotNull(plotBox);
		LOG.trace("plotBox "+plotBox);
	}
	
	@Test
	//@Ignore // OOME needs debuggiing
	@Ignore // until we write the axis stuff
	public void testPlotScatterplot() {
		AbstractCMElement g = SVGG.createSVGGChunk(SVGHTMLFixtures.SCATTERPLOT_7_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		GraphPlotBox plotBox = axisAnalyzer.getPlotBox();
		Assert.assertNotNull(plotBox);
		LOG.trace("plotBox "+plotBox);
	}
	
	
	@Test
	@Ignore // OOME needs debugging
	public void testPlotScatterplotFive() {
		AbstractCMElement g = SVGG.createSVGGChunk(SVGHTMLFixtures.SCATTERPLOT_FIVE_7_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.1);
		axisAnalyzer.createVerticalHorizontalAxisListAndPlotBox();
		List<Axis> horizontalAxisList = axisAnalyzer.getHorizontalAxisList();
		Assert.assertEquals(5, horizontalAxisList.size());
		List<Axis> verticalAxisList = axisAnalyzer.getVerticalAxisList();
		Assert.assertEquals(5, verticalAxisList.size());
		List<GraphPlotBox> plotBoxList = axisAnalyzer.getPlotBoxList();
		Assert.assertNotNull(plotBoxList);
		Assert.assertEquals("plotBoxList ", 5, plotBoxList.size());
		
		SVGG gg = new SVGG();
		for (GraphPlotBox plotBox : plotBoxList) {
			gg.appendChild(plotBox.drawBox());
		}
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/plotBoxe5.svg"));
	}
	
	@Test
	@Ignore // OOME needs debuggiing
	public void testScatterplotHorizontalText() throws FileNotFoundException {
//		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_7_2_SVG,  "./svg:g", 0);
//		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
//		axisAnalyzer.setEpsilon(0.1);
//		Axis horizontalAxis = axisAnalyzer.getHorizontalAxis();
//		Assert.assertNotNull(horizontalAxis);
//		List<SVGText> textList = horizontalAxis.extractText(g);
//		Assert.assertEquals("characters", 48, textList.size());
//		TextStructurer textStructurer = new TextStructurer(textList);
//		HtmlElement htmlElement = textStructurer.createHtmlElement();
//		SVGUtil.debug(htmlElement, new FileOutputStream("target/horizontalAxis.html"), 1);
//		Assert.assertEquals("horizontal", "<div xmlns=\"http://www.w3.org/1999/xhtml\">0 .00   0.01   0.02   0.03   0.04   0.05   <p />" +
//				"<b>dN of </b> <b><i>EF-1 </i></b> ��  null </div>", htmlElement.toXML());
	}
	
	@Test
	public void testScatterplotVerticalText() throws FileNotFoundException {
//		SVGG g = SVGG.createSVGGChunk(Fixtures.SCATTERPLOT_7_2_SVG,  "./svg:g", 0);
//		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
//		axisAnalyzer.setEpsilon(0.1);
//		Axis verticalAxis = axisAnalyzer.getVerticalAxis();
//		Assert.assertNotNull(verticalAxis);
//		List<SVGText> textList = verticalAxis.extractText(g);
//		Assert.assertEquals("characters", 42, textList.size());
//		TextStructurer textStructurer = new TextStructurer(textList);
//		HtmlElement htmlElement = textStructurer.createHtmlElement();
//		SVGUtil.debug(htmlElement, new FileOutputStream("target/verticalAxis.html"), 1);
//		// this is the vertical text
//		Assert.assertEquals("horizontal", "<div xmlns=\"http://www.w3.org/1999/xhtml\">0.04   <sup>0.03  </sup> <p /><b><i> </i></b> <b><i>b</i></b> <b>" +
//				"<i>u</i></b> <b><i>t</i></b> <b><i>-</i></b>�� <p /><b> </b> <b>f</b> <sup>0.02  </sup> <p />" +
//				"<b>o</b> <b> </b> <b>N</b> <b>d</b> 0.01   0.00   </div>", htmlElement.toXML());
	}
	
	@Test
	@Ignore // until we write the axis stuff
	public void testLinePlotsHorizontalText() throws FileNotFoundException {
		AbstractCMElement g = SVGG.createSVGGChunk(SVGHTMLFixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
		axisAnalyzer.setEpsilon(0.5);
 		Axis horizontalAxis = axisAnalyzer.getHorizontalAxis();
 		horizontalAxis.processScaleValuesAndTitlesNew(g);
	}
	
	
	@Test
	public void testLinePlotsHorizontalTextOld() throws FileNotFoundException {
//		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
//		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
//		axisAnalyzer.setEpsilon(0.5);
// 		Axis horizontalAxis = axisAnalyzer.getHorizontalAxis();
//		  Assert.assertNotNull(horizontalAxis);
//		List<SVGText> textList = horizontalAxis.extractText(g);
//		  Assert.assertEquals("characters", 30, textList.size());
//		TextStructurer textStructurer = new TextStructurer(textList);
//		List<TextLine> textLineList = textStructurer.getTextLineList();
//		RawWords axialValues = textLineList.get(0).getRawWords();
//		  Assert.assertEquals("axial values", 4, axialValues.size());
//		TextLine textLine1 = textLineList.get(1);
//		  RealArray xArray = textLine1.getXCoordinateArray();
//		  RealArray differenceArray = xArray.calculateDifferences().format(3);
//		  RealArray widthArray = textLine1.getFontWidthArray();
//		  RealArray separationArray = textLine1.getSeparationArray().format(3);
//		  RealArray scaledSeparationArray = separationArray.multiplyBy(1. / textLine1.getFontSize()).format(3);
//		  List<String> valueArray = textLine1.getValueList();
//// 		  System.out.println("X "+textLine1+
////				"\n"+xArray+
////				"\n"+widthArray+
////				"\n"+differenceArray+
////				"\n"+separationArray+
////				"\n"+scaledSeparationArray+
////				"\n"+valueArray.toString());
// 		 RawWords title = textLine1.getRawWords();
//  		 Assert.assertEquals("axial values", 1, title.size());
	}
	
	@Test
	public void testLinePlotsVerticalText() throws FileNotFoundException {
//		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
//		AxisAnalyzer axisAnalyzer = new AxisAnalyzer(g);
//		axisAnalyzer.setEpsilon(0.1);
//		Axis verticalAxis = axisAnalyzer.getVerticalAxis();
//		Assert.assertNotNull(verticalAxis);
//		// add all this to vertical axis
//		List<SVGText> textList = verticalAxis.extractText(g);
//		Assert.assertEquals("characters", 44, textList.size());
//		TextAnalyzer textAnalyzer = new TextAnalyzer(textList);
//		TextAnalyzer rotPi2TextAnalyzer = textAnalyzer.getRotPi2TextAnalyzer();
//		Assert.assertNotNull(rotPi2TextAnalyzer);
//		List<SVGText> rotPi2Text = rotPi2TextAnalyzer.getTextCharacters();
//		Assert.assertEquals(31, rotPi2Text.size());
//		HtmlElement htmlElementRotPi2 = rotPi2TextAnalyzer.getTextStructurer().createHtmlElement();
//		SVGUtil.debug(htmlElementRotPi2, new FileOutputStream("target/verticalAxis.html"), 1);
//		// this is the vertical text
//		Assert.assertEquals("horizontal", 
//				"<div xmlns=\"http://www.w3.org/1999/xhtml\">Total number of eggs fertilised </div>", htmlElementRotPi2.toXML());
	}
	
// =====================================================================
	
	private void debug(List<SVGText> textList) {
		for (SVGText text : textList) {
			System.out.println(">>> "+text.getXY()+" ["+text.getValue()+"]");
		}
	}

}
