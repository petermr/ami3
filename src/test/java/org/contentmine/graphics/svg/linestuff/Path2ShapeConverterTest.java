package org.contentmine.graphics.svg.linestuff;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class Path2ShapeConverterTest {

	private static final String[] COLORS = {"red", "yellow", "green", "cyan", "blue", "magenta", "brown", "black"};
	private static final Logger LOG = Logger.getLogger(Path2ShapeConverterTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
		
		@Test
		public void nopathTest() {
			List<SVGShape> shapeList = createShapeList(SVGHTMLFixtures.PATHS_NOPATH_SVG);
			Assert.assertEquals("converted", 0, shapeList.size());
		}

		@Test
		public void rectTest() {
			List<SVGShape> shapeList = createShapeList(SVGHTMLFixtures.PATHS_TEXT_LINE_SVG);
			Assert.assertEquals("converted", 1, shapeList.size());
//			Assert.assertEquals("rect", 
//					"<rect style=\"fill:none;\" signature=\"MLLLL\" x=\"42.52\" y=\"144.433\" height=\"3.005\" width=\"520.044\" id=\"rect.0\" />", 
//					shapeList.get(0).toXML());
			SVGHTMLFixtures.assertXMLContains(shapeList.get(0), "x=\"42.52\" y=\"144.433\"");
			}
		
		
		@Test
		/** The BMC Evolutionary Biology logo as paths.
		 * The small 'l' should be interpreted as a rect
		 * The small 'o' should be a circle // no longer works
		 * The 'M' should be a polyline
		 * The 'E' should ultimately break into two polylines
		 * The 'u', 'm', etc have curves and so won't be changed
		 */
		@Ignore // output of path analysis may have changed
		public void bmcLogoTest() {
			List<SVGShape> shapeList = createShapeList(SVGHTMLFixtures.PATHS_BMCLOGO_SVG);
			Assert.assertEquals("converted", 23, shapeList.size());
			Assert.assertEquals("E", 
					"<polygon fill=\"magenta\" stroke=\"green\" stroke-width=\"0.5\"" +
					" points=\"435.968 61.082 440.167 61.082 440.167 62.027 437.042 62.027 437.042 64.218 439.888 64.218 439.888 65.163" +
					" 437.042 65.163 437.042 67.633 440.167 67.633 440.167 68.578 435.968 68.578\" id=\"polygon.0\" />",
					shapeList.get(0).toXML());
			//"v" omitted
			//Assert.assertEquals("o", 
			//"<circle fill=\"magenta\" stroke=\"green\" stroke-width=\"0.5\"" +
			//" cx=\"449.58\" cy=\"65.84\" r=\"2.665\" id=\"circle.2\" />",
			//shapeList.get(2).toXML());
			Assert.assertEquals("lower case l", 
					"<rect fill=\"magenta\" stroke=\"green\" stroke-width=\"0.5\"" +
					" x=\"453.849\" y=\"60.523\" height=\"8.055\" width=\"1.009\" id=\"rect.3\" />",
					shapeList.get(3).toXML());
			Assert.assertEquals("unconverted u", 
					"<path xmlns=\"http://www.w3.org/2000/svg\" clip-path=\"url(#clipPath1)\" fill=\"magenta\" stroke=\"green\"" +
					" stroke-width=\"0.5\" d=\"M461.548 68.578 L460.571 68.578 L460.571 67.708 L460.55 67.708" +
					" C460.249 68.331 459.519 68.707 458.756 68.707 C457.338 68.707 456.705 67.827 456.705 66.355" +
					" L456.705 63.101 L457.714 63.101 L457.714 65.936 C457.714 67.214 457.994 67.837 458.874 67.891" +
					" C460.023 67.891 460.539 66.967 460.539 65.636 L460.539 63.101 L461.548 63.101 L461.548 68.578 \" id=\"path.4\" />",
					shapeList.get(4).toXML());
			Assert.assertEquals("half moon", 
					"<path xmlns=\"http://www.w3.org/2000/svg\" clip-path=\"url(#clipPath3)\" fill=\"pink\" stroke=\"purple\"" +
					" stroke-width=\"0.5\" d=\"M428.911 60.844 C425.543 55.314 425.357 48.129 429.037 42.27" +
					" C434.222 34.019 444.971 31.662 453.224 36.848 C456.212 38.725 458.352 42.501 458.352 42.501" +
					" C457.189 40.651 455.601 39.024 453.626 37.784 C446.595 33.367 437.315 35.487 432.898 42.518" +
					" C429.731 47.56 429.925 53.76 432.882 58.492 L428.911 60.844 \" id=\"path.22\" />",
					shapeList.get(22).toXML());
		}
		
		@Test
		@Ignore
		public void bmcLogoTestInSitu() throws IOException {
			AbstractCMElement svgElement = convertPathsToShapes(SVGHTMLFixtures.PATHS_BMCLOGO_SVG);
//			SVGUtil.debug(svgElement, new FileOutputStream("target/converted"+System.currentTimeMillis()+".svg"), 1);
			List<SVGElement> svgElements = SVGUtil.getQuerySVGElements(svgElement, "/*/*/svg:*");
			Assert.assertEquals("converted", 23, svgElements.size());
			Assert.assertTrue("0 "+svgElements.get(0).getClass().getSimpleName(), svgElements.get(0) instanceof SVGPolygon);
			Assert.assertTrue("1 "+svgElements.get(1).getClass().getSimpleName(), svgElements.get(1) instanceof SVGPolygon);
			//Assert.assertTrue("2 "+svgElements.get(2).getClass().getSimpleName(), svgElements.get(2) instanceof SVGCircle);
			Assert.assertTrue("3 "+svgElements.get(3).getClass().getSimpleName(), svgElements.get(3) instanceof SVGRect);
			Assert.assertTrue("4 "+svgElements.get(4).getClass().getSimpleName(), svgElements.get(4) instanceof SVGPath);
		}
		
		/*
		<svg xmlns="http://www.w3.org/2000/svg">
		 <g>
		  <path stroke="black" fill="#000000" stroke-width="0.0" 
		  d="M172.14 512.58 
		  L172.14 504.3   // A
		  C172.14 504.18 172.26 504.06 172.38 504.06 
		  C172.5 504.06 172.62 504.18 172.62 504.3 
		  L172.62 512.58  // D
		  C172.62 512.76 172.5 512.88 172.38 512.88 
		  C172.26 512.88 172.14 512.76 172.14 512.58 "
		  svgx:z="1737"/>
		 </g>
		</svg>
		
		<?xml version="1.0" encoding="UTF-8"?>
		<svg xmlns="http://www.w3.org/2000/svg">
		 <path stroke="black" fill="#000000" stroke-width="0.0" 
		 d="M172.14 512.58 
		 L172.14 504.3 
		 L172.62 504.3 
		 L172.62 512.58 
		 L172.14 512.58 "/>
		</svg>
		*/
		@Test
		public void testReplaceTwoQuadrantCapsByButt() {
			SVGElement svgElement = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "roundedline.svg"));
			SVGElement line = (SVGElement) svgElement.getChildElements().get(0).getChildElements().get(0);
			SVGHTMLFixtures.assertXMLContains(line, "x1=\"172.38\" y1=\"504.06\" x2=\"172.38\" y2=\"512.88\"");
		}
		
		@Test
		public void testHollowline() {
			SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "hollowline.svg"))
					.getChildElements().get(0);
			Assert.assertEquals("sig",  "MLLL", svgPath.getOrCreateSignatureAttributeValue());
			Path2ShapeConverter p2sConverter = new Path2ShapeConverter(svgPath);
			SVGElement line = p2sConverter.createNarrowLine();
			Assert.assertNotNull("line", line);
		}
		
		@Test
		public void testCircle() {
			/*<path fill="none" stroke="#ff0000" stroke-width="0.840" 
			d="M311.86 149.088 
			C311.86 149.833 311.256 150.431 310.517 150.431 
			C309.772 150.431 309.175 149.833 309.175 149.088 
			C309.175 148.35 309.772 147.745 310.517 147.745 
			C311.256 147.745 311.86 148.35 311.86 149.088 
			Z"/>*/
			SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "circle.svg"))
					.getChildElements().get(0);
			Assert.assertEquals("sig",  "MCCCCZ", svgPath.getOrCreateSignatureAttributeValue());
			Path2ShapeConverter p2sConverter = new Path2ShapeConverter();
			SVGElement circle = p2sConverter.convertPathToShape(svgPath);
			Assert.assertNotNull("circle", circle);
			Assert.assertTrue("circle", circle instanceof SVGCircle);
			SVGCircle svgCircle = (SVGCircle) circle;
			svgCircle.format(3);
			Assert.assertEquals("circle", 
					"<circle xmlns=\"http://www.w3.org/2000/svg\" signature=\"MCCCCZ\" cx=\"310.517\" cy=\"149.088\" r=\"1.379\" />", circle.toXML());
		}
		
		// ============================================================================
		private List<SVGShape> createShapeList(File file) {
			AbstractCMElement svgElement = SVGElement.readAndCreateSVG(file);
			Path2ShapeConverter converter = new Path2ShapeConverter();
			List<SVGShape> shapeList = converter.convertPathsToShapes(svgElement);
			return shapeList;
		}
		
		private static SVGElement convertPathsToShapes(File file) {
			SVGElement svgElement = SVGElement.readAndCreateSVG(file);
			Path2ShapeConverter converter = new Path2ShapeConverter();
			converter.setRectEpsilon(0.1);
			converter.convertPathsToShapes(svgElement);
			return svgElement;
		}

		@Test
		public void	testRect() {
			AbstractCMElement output = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "rect.svg"));
			List<SVGG> groups = SVGG.extractSelfAndDescendantGs(output);
			AbstractCMElement group61 = (AbstractCMElement)groups.get(6).getChild(1);
			AbstractCMElement group63 = (AbstractCMElement)groups.get(6).getChild(3);
			AbstractCMElement group65 = (AbstractCMElement)groups.get(6).getChild(5);
			Assert.assertTrue("1", group61 instanceof SVGRect);
			Assert.assertTrue("2", group63 instanceof SVGRect);
			Assert.assertTrue("3", group65 instanceof SVGRect);
		}
		
		@Test
		public void testCircles() {
			AbstractCMElement output = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "circle.svg"));
			Assert.assertTrue("1", output.getChild(1) instanceof SVGCircle);
			Assert.assertEquals("310.517", ((SVGCircle) output.getChild(1)).getAttribute("cx").getValue());
			Assert.assertEquals("149.088", ((SVGCircle) output.getChild(1)).getAttribute("cy").getValue());
			Assert.assertEquals("1.379", ((SVGCircle) output.getChild(1)).getAttribute("r").getValue());
		}

		@Test
		public void	testPolylines() {
			AbstractCMElement output = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "polyline.svg"));
			List<SVGG> groups = SVGG.extractSelfAndDescendantGs(output);
			Assert.assertTrue("1", groups.get(6).getChild(1) instanceof SVGPolyline);
			SVGPolyline polyline61 = (SVGPolyline) groups.get(6).getChild(1);
			polyline61.format(3);
			Assert.assertEquals("1", "27.143 82.362 129.286 28.791 159.286 125.934 58.571 133.791 102.143 75.934", polyline61.getAttribute("points").getValue());
			Assert.assertTrue("2: "+groups.get(6).getChild(3).getClass(), groups.get(6).getChild(3) instanceof SVGLine);
			SVGLine line63 = (SVGLine) groups.get(6).getChild(3);
			line63.format(3);
//			Assert.assertEquals("2", "77.896 270.218 342.327 270.218 342.327 270.791 77.899 270.791 82.192 270.522", polyline63.getAttribute("points").getValue());
			//Assert.assertTrue("3", groups.get(6).getChild(5) instanceof SVGPolyline);
			//Assert.assertEquals("1", "78.231 274.04 342.662 274.04 342.662 274.613 78.234 274.613", ((SVGPolyline) groups.get(6).getChild(5)).getAttribute("points").getValue());
			Assert.assertTrue("3", groups.get(6).getChild(5) instanceof SVGLine);
			SVGLine line65 = (SVGLine) groups.get(6).getChild(5);
			line65.format(3);
//			Assert.assertEquals("3", "87.934 584.909 87.934 320.478 88.508 320.478 88.508 584.906 88.239 580.613", polyline65.getAttribute("points").getValue());
			//Assert.assertTrue("2", groups.get(6).getChild(7) instanceof SVGPolyline);
			//Assert.assertEquals("1", "87.764 890.767 87.764 626.336 88.337 626.336 88.337 890.764", ((SVGPolyline) groups.get(6).getChild(7)).getAttribute("points").getValue());
			Assert.assertTrue("4", groups.get(6).getChild(7) instanceof SVGPolyline);
			SVGPolyline polyline67 = (SVGPolyline) groups.get(6).getChild(7);
			polyline67.format(3);
			Assert.assertEquals("4", "-14.561 1136.235 143.066 923.92 143.526 924.261 -14.099 1136.574 -11.755 1132.967", polyline67.getAttribute("points").getValue());
			//Assert.assertTrue("5", groups.get(6).getChild(9) instanceof SVGPolyline);
			//Assert.assertEquals("5", "-6.928 1372.659 160.361 1167.871 160.805 1168.234 -6.482 1373.019", ((SVGPolyline) groups.get(6).getChild(9)).getAttribute("points").getValue());
		}

		@Test
		@Ignore
		public void	testPolygons() {
			AbstractCMElement output = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "polygon.svg"));
			List<SVGG> groups = SVGG.extractSelfAndDescendantGs(output);
			Assert.assertTrue("1", groups.get(6).getChild(1) instanceof SVGPolygon);
		}

		@Test
		public void	testLines() {
			double eps = 0.01;
			AbstractCMElement output = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "line.svg"));
			List<SVGG> groups = SVGG.extractSelfAndDescendantGs(output);
			Assert.assertTrue("1", groups.get(6).getChild(1) instanceof SVGLine);
			SVGLine line61 = (SVGLine) groups.get(6).getChild(1);
			line61.format(2);
			SVGHTMLFixtures.assertXMLContains(line61, "x1=\"377.81\" y1=\"281.62\" x2=\"113.38\" y2=\"281.62\"");
//			SVGLine line63 = (SVGLine) groups.get(6).getChild(3);
//			line63.format(2);
//			Assert.assertEquals(""+line63.toXML(), "", line63.toXML());
//			SVGLine line65 = (SVGLine) groups.get(6).getChild(5);
//			line65.format(2);
//			Assert.assertTrue(""+line65.toXML(), SVGLine.isEqual(line65, new SVGLine(new Real2(113.38, 281.62), new Real2(377.81, 281.62)), eps));
//			SVGLine line67 = (SVGLine) groups.get(6).getChild(7);
//			line67.format(2);
//			Assert.assertTrue(""+line67.toXML(), SVGLine.isEqual(line67, new SVGLine(new Real2(113.38, 281.62), new Real2(377.81, 281.62)), eps));
//			SVGLine line69 = (SVGLine) groups.get(6).getChild(9);
//			line69.format(2);
//			Assert.assertTrue(""+line69.toXML(), SVGLine.isEqual(line69, new SVGLine(new Real2(113.38, 281.62), new Real2(377.81, 281.62)), eps));

//			Assert.assertTrue("6", groups.get(6).getChild(11) instanceof SVGLine);
//			Assert.assertEquals("45.23", ((SVGLine) groups.get(6).getChild(11)).getAttribute("x2").getValue());
//			Assert.assertEquals("956.551", ((SVGLine) groups.get(6).getChild(11)).getAttribute("y2").getValue());
//			Assert.assertEquals("45.23", ((SVGLine) groups.get(6).getChild(11)).getAttribute("x1").getValue());
//			Assert.assertEquals("993.007", ((SVGLine) groups.get(6).getChild(11)).getAttribute("y1").getValue());
//			Assert.assertTrue("7", groups.get(6).getChild(13) instanceof SVGLine);
//			Assert.assertEquals("34.289", ((SVGLine) groups.get(6).getChild(13)).getAttribute("x1").getValue());
//			Assert.assertEquals("1093.131", ((SVGLine) groups.get(6).getChild(13)).getAttribute("y1").getValue());
//			Assert.assertEquals("55.806", ((SVGLine) groups.get(6).getChild(13)).getAttribute("x2").getValue());
//			Assert.assertEquals("1063.702", ((SVGLine) groups.get(6).getChild(13)).getAttribute("y2").getValue());
//			Assert.assertTrue("8", groups.get(6).getChild(15) instanceof SVGLine);
//			Assert.assertEquals("133.188", ((SVGLine) groups.get(6).getChild(15)).getAttribute("x1").getValue());
//			Assert.assertEquals("1174.733", ((SVGLine) groups.get(6).getChild(15)).getAttribute("y1").getValue());
//			Assert.assertEquals("-22.88", ((SVGLine) groups.get(6).getChild(15)).getAttribute("x2").getValue());
//			Assert.assertEquals("1388.195", ((SVGLine) groups.get(6).getChild(15)).getAttribute("y2").getValue());
//			Assert.assertTrue("9", groups.get(6).getChild(17) instanceof SVGLine);
//			Assert.assertEquals("6.718", ((SVGLine) groups.get(6).getChild(17)).getAttribute("x1").getValue());
//			Assert.assertEquals("1478.041", ((SVGLine) groups.get(6).getChild(17)).getAttribute("y1").getValue());
//			Assert.assertEquals("116.673", ((SVGLine) groups.get(6).getChild(17)).getAttribute("x2").getValue());
//			Assert.assertEquals("1478.041", ((SVGLine) groups.get(6).getChild(17)).getAttribute("y2").getValue());
//			Assert.assertTrue("10", groups.get(6).getChild(19) instanceof SVGLine);
//			Assert.assertEquals("41.012", ((SVGLine) groups.get(6).getChild(19)).getAttribute("x1").getValue());
//			Assert.assertEquals("1494.658", ((SVGLine) groups.get(6).getChild(19)).getAttribute("y1").getValue());
//			Assert.assertEquals("41.012", ((SVGLine) groups.get(6).getChild(19)).getAttribute("x2").getValue());
//			Assert.assertEquals("1552.994", ((SVGLine) groups.get(6).getChild(19)).getAttribute("y2").getValue());
//			Assert.assertTrue("11", groups.get(6).getChild(21) instanceof SVGLine);
//			Assert.assertEquals("33.234", ((SVGLine) groups.get(6).getChild(21)).getAttribute("x1").getValue());
//			Assert.assertEquals("1594.36", ((SVGLine) groups.get(6).getChild(21)).getAttribute("y1").getValue());
//			Assert.assertEquals("74.953", ((SVGLine) groups.get(6).getChild(21)).getAttribute("x2").getValue());
//			Assert.assertEquals("1563.954", ((SVGLine) groups.get(6).getChild(21)).getAttribute("y2").getValue());
//			Assert.assertTrue("12", groups.get(6).getChild(23) instanceof SVGLine);
//			Assert.assertEquals("-24.034", ((SVGLine) groups.get(6).getChild(23)).getAttribute("x1").getValue());
//			Assert.assertEquals("1889.092", ((SVGLine) groups.get(6).getChild(23)).getAttribute("y1").getValue());
//			Assert.assertEquals("132.033", ((SVGLine) groups.get(6).getChild(23)).getAttribute("x2").getValue());
//			Assert.assertEquals("1675.631", ((SVGLine) groups.get(6).getChild(23)).getAttribute("y2").getValue());
//			Assert.assertTrue("16", groups.get(6).getChild(31) instanceof SVGLine);
//			Assert.assertEquals("-64.214", ((SVGLine) groups.get(6).getChild(31)).getAttribute("x1").getValue());
//			Assert.assertEquals("2270.362", ((SVGLine) groups.get(6).getChild(31)).getAttribute("y1").getValue());
//			Assert.assertEquals("200.215", ((SVGLine) groups.get(6).getChild(31)).getAttribute("x2").getValue());
//			Assert.assertEquals("2270.362", ((SVGLine) groups.get(6).getChild(31)).getAttribute("y2").getValue());
//			Assert.assertTrue("19", groups.get(6).getChild(37) instanceof SVGLine);
//			Assert.assertEquals("55.508", ((SVGLine) groups.get(6).getChild(37)).getAttribute("x1").getValue());
//			Assert.assertEquals("2643.014", ((SVGLine) groups.get(6).getChild(37)).getAttribute("y1").getValue());
//			Assert.assertEquals("55.508", ((SVGLine) groups.get(6).getChild(37)).getAttribute("x2").getValue());
//			Assert.assertEquals("2907.443", ((SVGLine) groups.get(6).getChild(37)).getAttribute("y2").getValue());
//			Assert.assertTrue("21", groups.get(6).getChild(41) instanceof SVGLine);
//			Assert.assertEquals("103.489", ((SVGLine) groups.get(6).getChild(41)).getAttribute("x1").getValue());
//			Assert.assertEquals("3262.113", ((SVGLine) groups.get(6).getChild(41)).getAttribute("y1").getValue());
//			Assert.assertEquals("-52.578", ((SVGLine) groups.get(6).getChild(41)).getAttribute("x2").getValue());
//			Assert.assertEquals("3475.574", ((SVGLine) groups.get(6).getChild(41)).getAttribute("y2").getValue());
		}
		
		@Test
		@Ignore
		public void	testLinesFails() {
			AbstractCMElement output = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "line.svg"));
			List<SVGG> groups = SVGG.extractSelfAndDescendantGs(output);
			Assert.assertTrue("13", groups.get(6).getChild(25) instanceof SVGLine);
			Assert.assertTrue("14", groups.get(6).getChild(27) instanceof SVGLine);
			Assert.assertTrue("15", groups.get(6).getChild(29) instanceof SVGLine);
			Assert.assertTrue("17", groups.get(6).getChild(33) instanceof SVGLine);
			Assert.assertTrue("18", groups.get(6).getChild(35) instanceof SVGLine);
			Assert.assertTrue("20", groups.get(6).getChild(39) instanceof SVGLine);
			Assert.assertTrue("22", groups.get(6).getChild(43) instanceof SVGLine);
			Assert.assertTrue("23", groups.get(6).getChild(45) instanceof SVGLine);
		}
		
		@Test
		public void testPathWithMoves() {
			AbstractCMElement output = convertPathsToShapes(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "pathwithmoves.svg"));
			List<SVGG> groups = SVGG.extractSelfAndDescendantGs(output);
			Assert.assertEquals(output.getChild(433).toString(), "line: from((154.5,434.34)) to((154.5,452.1)) v((0.0,17.760000000000048))");
			Assert.assertEquals(output.getChild(434).toString(), "line: from((201.78,461.58)) to((216.72,452.58)) v((14.939999999999998,-9.0))");
			Assert.assertEquals(output.getChild(435).toString(), "line: from((154.5,434.34)) to((170.1,425.1)) v((15.599999999999994,-9.239999999999952))");
			Assert.assertEquals(output.getChild(436).toString(), "line: from((138.0,479.76)) to((138.0,466.2)) v((0.0,-13.560000000000002))");
		}


	@Test
	public void test3SidedRectNoSplit() {
		SVGG g = (SVGG) SVGSVG.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "unclosedRect.svg"))
				.getChildElements().get(0);
		SVGPath path = (SVGPath) g.getChildElements().get(0);
		Assert.assertEquals("M98.055 294.622 L98.055 286.872 L464.854 286.872 L464.854 294.622",  path.getDString().trim());
		Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
		path2ShapeConverter.setSplitPolyLines(false);
		List<SVGShape> shapes = path2ShapeConverter.convertPathsToShapes(g);
		Assert.assertFalse(path2ShapeConverter.isSplitPolyLines());
		Assert.assertEquals(1,  shapes.size());
		SVGPolyline polyline = (SVGPolyline) shapes.get(0);
		Assert.assertEquals("((98.055,294.622)(98.055,286.872)(464.854,286.872)(464.854,294.622))", polyline.getReal2Array().toString());
	}
	
	@Test
	public void test3SidedRectSplit() {
		SVGG g = (SVGG) SVGSVG.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "unclosedRect.svg"))
				.getChildElements().get(0);
		SVGPath path = (SVGPath) g.getChildElements().get(0);
		Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
		path2ShapeConverter.setSplitPolyLines(true);
		List<SVGShape> shapes = path2ShapeConverter.convertPathsToShapes(g);
		Assert.assertTrue(path2ShapeConverter.isSplitPolyLines());
		Assert.assertEquals(3,  shapes.size());
		String[] lineValue = {
				"98.055 294.622 98.055 286.872",
				"98.055 286.872 464.854 286.872",
				"464.854 286.872 464.854 294.622"
		};
		for (int i = 0; i < shapes.size(); i++) {
			SVGElement shape = shapes.get(i);
			Assert.assertEquals("SVGLine", shape.getClass().getSimpleName());
			SVGLine line = (SVGLine) shape; 
			Assert.assertEquals(lineValue[i], String.valueOf(line.getGeometricHash()));
		}
	}
	
	@Test
	public void testRelativeMove() {
		SVGPath path = new SVGPath();
	    path.setDString(""
	    		+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z");
		Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
		path2ShapeConverter.setSplitPolyLines(false);
		// this shouldn't be necessary
		AbstractCMElement g = new SVGG();
		g.appendChild(path);
		List<SVGShape> shapes = path2ShapeConverter.convertPathsToShapes(g);
		Assert.assertEquals(1, shapes.size());
		SVGElement shape = shapes.get(0);
		Assert.assertEquals("SVGRect", shape.getClass().getSimpleName());
		SVGElement rect = (SVGElement) shape;
		Assert.assertEquals("((34.264,133.58),(165.875,177.159))", rect.toString());
	}

	
	@Test
	@Ignore // test depends on sort
	public void testChainedRelativeMoveRects() {
		SVGPath path = new SVGPath();
	    path.setDString(""
	    		+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z "
		    	+ "m 99.317,0 69.449,0 0,11.284 -69.449,0 z");
		Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
		path2ShapeConverter.setSplitPolyLines(false);
		// this shouldn't be necessary
		AbstractCMElement g = new SVGG();
		g.appendChild(path);
		List<SVGShape> shapes = path2ShapeConverter.convertPathsToShapes(g);
		Assert.assertEquals(2, shapes.size());
//		Collections.sort(shapes);
		SVGElement rect0 = (SVGElement) shapes.get(0);
		Assert.assertEquals("((34.264,133.58),(165.875,177.159))", rect0.toString());
		SVGRect rect1 = (SVGRect) shapes.get(1);
		rect1.format(3);
		Real2Range bbox = rect1.getBoundingBox();
		bbox.format(3);
		Assert.assertEquals("((133.58,203.029),(165.875,177.159))", bbox.toString());
		writeColouredShapes(shapes, new File("target/tables/rect2.svg"));
	}
		
		@Test
		public void testTwoRowsOfInternalMoves() {
			SVGPath path = new SVGPath();
		    path.setDString(""
					+ "m 34.264,165.875 99.316,0 0,11.284 -99.316,0 z "
					+ "m 99.317,0 69.449,0 0,11.284 -69.449,0 z "
					+ "m 69.449,0 63.426,0 0,11.284 -63.426,0 z "
					+ "m 63.426,0 62.112,0 0,11.284 -62.112,0 z "
					+ ""
					+ "m -232.192,22.567 99.316,0 0,11.283 -99.316,0 z "
					+ "m 99.317,0 69.449,0 0,11.283 -69.449,0 z "
					+ "m 69.449,0 63.426,0 0,11.283 -63.426,0 z "
					+ "m 63.426,0 62.112,0 0,11.283 -62.112,0 z ");
			AbstractCMElement g = new SVGG();
			g.appendChild(path);
			Path2ShapeConverter path2ShapeConverter = new Path2ShapeConverter();
			List<SVGShape> shapes = path2ShapeConverter.convertPathsToShapes(g);
			path2ShapeConverter.setSplitPolyLines(false);
			Assert.assertEquals(8, shapes.size());
			writeColouredShapes(shapes, new File("target/tables/multipleRect.svg"));
	}
		
    @Test
    public void testMakeRect() {
    	String pathXML = "<path id=\"path1676\" d=\"M128.441 668.122 L135.743 668.122 L135.743 559.484 L128.441 559.484 Z\" stroke-width=\"0.31\" fill=\"#cccccc\" clip-path=\"url(#clipPath1)\" stroke=\"black\"/>";
    	SVGPath path = (SVGPath) SVGElement.readAndCreateSVG(XMLUtil.parseXML(pathXML));
    	SVGElement shape = new Path2ShapeConverter().convertPathToShape(path);
    	Assert.assertEquals(shape.getClass(), SVGRect.class);
    }


	/** tests converting a thin rect into a line with width.
	 * 
	 */
	@Test
	public void testThinRect() {
		String svgXML = "<svg xmlns:svg=\"http://www.w3.org/2000/svg\">"
				+ " <path id=\"path1261\"	d=\"M353.537 106.51 L566.929 106.51 L566.929 106.227 L353.537 106.227 Z\""
				+ " stroke-width=\"0.0\" fill=\"#131313\" clip-path=\"url(#clipPath1)\" stroke=\"black\" />"
				+ "</svg>";
		AbstractCMElement svgElement0 = SVGElement.readAndCreateSVG(svgXML);
		ComponentCache svgStore = new ComponentCache();
		svgStore.readGraphicsComponentsAndMakeCaches(svgElement0);
		AbstractCMElement svgElement = (AbstractCMElement) svgStore.getExtractedSVGElement();
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(svgElement);
		Assert.assertEquals("lines", 1, lineList.size());
		SVGLine svgLine0 = lineList.get(0);
		Assert.assertEquals("line0", "#131313", svgLine0.getFill());
		Assert.assertEquals("line0", "line1", svgLine0.getId());
		Assert.assertEquals("line0", 0.283, svgLine0.getStrokeWidth(), 0.001);
//		Real2Test.assertEquals("line0 XY0", new Real2(353.537, 106.369), svgLine0.getXY(0), 0.001);
		Assert.assertEquals("line0", 353.537, svgLine0.getXY(0).getX(), 0.001);
		Assert.assertEquals("line0", 106.369, svgLine0.getXY(0).getY(), 0.001);
	}
	


	// =============================
	
	private void writeColouredShapes(List<SVGShape> shapes, File file) {
		SVGG gg = new SVGG();
		for (int i = 0; i < shapes.size(); i++) {
			SVGElement shape = shapes.get(i);
			shape.setFill(COLORS[i % COLORS.length]);
			gg.appendChild(shape.copy());
		}
		SVGSVG.wrapAndWriteAsSVG(gg, file);
	}
}
