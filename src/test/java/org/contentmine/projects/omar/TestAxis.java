package org.contentmine.projects.omar;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.plot.AnnotatedAxis;
import org.contentmine.graphics.svg.plot.AnnotatedAxisTest;
import org.contentmine.graphics.svg.plot.AxisScaleBox;
import org.contentmine.graphics.svg.plot.AxisTickBox;
import org.junit.Assert;
import org.junit.Test;

public class TestAxis {
	private static final Logger LOG = Logger.getLogger(TestAxis.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	private static File OMAR_DIR = new File("src/test/resources/org/contentmine/projects/omar/test/lichtenburg19a/svg/");
	private static File OMAR_DIR = new File("src/test/resources/org/contentmine/ami/omar/test/lichtenburg19a/svg/");

	@Test
	public void testExtractAxisGraph1() throws FileNotFoundException {
		// seems to have drifted slightly
		AnnotatedAxis[] axisArray = AnnotatedAxisTest.getAxisArrayAndTestFullBox(OMAR_DIR, "page1.graph1.svg", "((327.106,423.087),(75.336,144.035))");
		
		if (axisArray == null) {
			LOG.error("FIXME empty axis");
			return;
		}
		Assert.assertEquals("FIXME ", 4, axisArray.length);
		AnnotatedAxis axis0 = axisArray[0];
		System.out.println("axis0 "+axis0);
		AnnotatedAxis axis1 = axisArray[1];
		System.out.println("axis1 "+axis1);
		/**
		fullLineBbox.format(3);
		Assert.assertEquals("full box",  "((140.415,426.016),(483.056,650.628))", fullLineBbox.toString());
		AnnotatedAxis[] axisArray = plotBox.getAxisArray();
		Assert.assertEquals("axes", 4,  axisArray.length);
		AnnotatedAxis axis0 = axisArray[0];
		AxisTickBox axisTickBox0 = axis0.getAxisTickBox();
		AxisScaleBox axisTextBox0 = axis0.getValueTextBox();
		Assert.assertEquals("axis0", "type: BOTTOM; dir: HORIZONTAL; range: (140.415,426.016)\n"
			+ "axisTickBox: box: extendedBox: ((135.415,431.016),(640.628,668.628)) bbox: ((176.127,390.327),(649.397,651.845))\n"
			+ "DIR: HORIZONTAL; inside/outside/line/extension deltas:10.0, 18.0, 5.0\n"
			+ "HOR: 1; [line: from((140.415,650.628)) to((426.016,650.628)) v((285.601,0.0))]\n"
			+ "VERT: 9; [line: from((140.415,483.056)) to((140.415,650.628)) v((0.0,167.57200000000006)), line: from((176.127,649.397)) to((176.127,651.845)) v((0.0,2.447999999999979)), line: from((211.812,649.397)) to((211.812,651.845)) v((0.0,2.447999999999979)), line: from((247.524,649.397)) to((247.524,651.845)) v((0.0,2.447999999999979)), line: from((283.223,649.397)) to((283.223,651.845)) v((0.0,2.447999999999979)), line: from((318.93,649.397)) to((318.93,651.845)) v((0.0,2.447999999999979)), line: from((354.638,649.397)) to((354.638,651.845)) v((0.0,2.447999999999979)), line: from((390.327,649.397)) to((390.327,651.845)) v((0.0,2.447999999999979)), line: from((426.035,483.056)) to((426.035,650.628)) v((0.0,167.57200000000006))]\n"
			+"majorTicks: (140.415,176.127,211.812,247.524,283.223,318.93,354.638,390.327,426.016)\n"
			+"minorTicks: ()\n"
			+ "\n"
			+"tickValues: tickNumberUserCoords: (-2.0,-1.5,-1.0,-0.5,0.0,0.5,1.0,1.5,2.0)\n"
			+"tickNumberScreenCoords: (135.925,171.61,207.322,243.007,279.626,315.339,351.024,386.732,422.42)\n"
			+ "\n",
		axis0.toString());
		Assert.assertEquals("axis0", LineDirection.HORIZONTAL, axis0.getLineDirection());
		Assert.assertEquals("axis0", "(140.415,176.127,211.812,247.524,283.223,318.93,354.638,390.327,426.016)", 
				axisTickBox0.getMajorTicksScreenCoords().toString());
		// the last value is wrong
		Assert.assertEquals("axis0", "(-2.0,-1.5,-1.0,-0.5,0.0,0.5,1.0,1.5,2.0)", 
				axisTextBox0.getTickNumberUserCoords().toString());
		// the last value is wrong
		Assert.assertEquals("axis0 numberScreen", "(135.925,171.61,207.322,243.007,279.626,315.339,351.024,386.732,422.42)", 
				axisTextBox0.getTickValueScreenCoords().toString());
		
		AnnotatedAxis axis1 = axisArray[1];
		AxisTickBox axisTickBox1 = axis1.getAxisTickBox();
		AxisScaleBox axisTextBox1 = axis1.getValueTextBox();
		Assert.assertEquals("axis1", "type: LEFT; dir: VERTICAL; range: (483.056,650.628)\n"
		+"axisTickBox: box: extendedBox: ((130.415,143.415),(478.056,655.628)) bbox: ((122.415,150.415),(478.056,655.628)) bbox: ((139.342,140.398),(510.979,622.704))\n"
		+"DIR: VERTICAL; inside/outside/line/extension deltas:10.0, 18.0, 5.0\n"
		+"HOR: 7; [line: from((140.415,483.03)) to((426.016,483.03)) v((285.601,0.0)), line: from((139.342,510.979)) to((140.398,510.979)) v((1.0559999999999832,0.0)), line: from((139.342,538.913)) to((140.398,538.913)) v((1.0559999999999832,0.0)), line: from((139.342,566.837)) to((140.398,566.837)) v((1.0559999999999832,0.0)), line: from((139.342,594.781)) to((140.398,594.781)) v((1.0559999999999832,0.0)), line: from((139.342,622.704)) to((140.398,622.704)) v((1.0559999999999832,0.0)), line: from((140.415,650.628)) to((426.016,650.628)) v((285.601,0.0))]\n"
		+"VERT: 1; [line: from((140.415,483.056)) to((140.415,650.628)) v((0.0,167.57200000000006))]\n"
		+"majorTicks: (483.056,510.979,538.913,566.837,594.781,622.704,650.628)\n"
		+"minorTicks: ()\n"
		+ "\n"
		+"tickValues: tickNumberUserCoords: (0.0,0.1,0.2,0.3,0.4,0.5,0.6)\n"
		+"tickNumberScreenCoords: (485.07,513.02,540.954,568.877,596.822,624.745,652.679)\n"
		+ "\n",
		axis1.toString());

		
		Assert.assertEquals("axis1", LineDirection.VERTICAL, axis1.getLineDirection());
		Assert.assertEquals("axis1", "(483.056,510.979,538.913,566.837,594.781,622.704,650.628)", 
				axisTickBox1.getMajorTicksScreenCoords().toString());
		Assert.assertEquals("axis1", "(0.0,0.1,0.2,0.3,0.4,0.5,0.6)", 
				axisTextBox1.getTickNumberUserCoords().toString());
		Assert.assertEquals("axis1 numberScreen", "(485.07,513.02,540.954,568.877,596.822,624.745,652.679)", 
				axisTextBox1.getTickValueScreenCoords().toString());
		
		// this grabs too much of the neighbours
		AnnotatedAxis axis2 = axisArray[2];
		Assert.assertEquals("type: TOP; dir: HORIZONTAL; range: (140.415,426.016)\n"
				+ "axisTickBox: box: extendedBox: ((135.415,431.016),(473.056,486.056)) bbox: null\n"
				+ "DIR: HORIZONTAL; inside/outside/line/extension deltas:3.0, 10.0, 5.0\n"
				+ "HOR: 1; [line: from((140.415,483.03)) to((426.016,483.03)) v((285.601,0.0))]\n"
				+ "VERT: 2; [line: from((140.415,483.056)) to((140.415,650.628)) v((0.0,167.57200000000006)), line: from((426.035,483.056)) to((426.035,650.628)) v((0.0,167.57200000000006))]\n"
		+"majorTicks: null\n"
		+"minorTicks: null\n"
		+ "\n"
		// these are wrong
		+"tickValues: tickNumberUserCoords: null\n"
		+"tickNumberScreenCoords: ()\n"
		+ "\n",
		axis2.toString());
		
		AnnotatedAxis axis3 = axisArray[3];
		Assert.assertEquals("type: RIGHT; dir: VERTICAL; range: (483.056,650.628)\n"
				+ "axisTickBox: box: extendedBox: ((423.016,436.016),(478.056,655.628)) bbox: null\n"
				+ "DIR: VERTICAL; inside/outside/line/extension deltas:3.0, 10.0, 5.0\n"
				+ "HOR: 2; [line: from((140.415,483.03)) to((426.016,483.03)) v((285.601,0.0)), line: from((140.415,650.628)) to((426.016,650.628)) v((285.601,0.0))]\n"
				+ "VERT: 1; [line: from((426.035,483.056)) to((426.035,650.628)) v((0.0,167.57200000000006))]\n"
		+"majorTicks: null\n"
		+"minorTicks: null\n"
		+ "\n"
		+"tickValues: tickNumberUserCoords: ()\n"
		+"tickNumberScreenCoords: ()\n"
		+ "\n",
		axis3.toString());
		*/

	}

}
