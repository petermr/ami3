package org.contentmine.graphics.svg.plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.junit.Ignore;
import org.junit.Test;

public class PlotStructurerTest {
	
	public static final Logger LOG = Logger.getLogger(PlotStructurerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String PLOT_CSV = "plot.csv";
	public static final String PLOT_SVG = "plot.svg";
	private static File TARGET_PLOTS = new File("target/plots/");

	@Test
	public void testTEX10Paths() throws IOException {
		String fileRoot = "TEX.g.10.1";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.TEX_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	public void testTEX11Paths() throws IOException {
		String fileRoot = "TEX.g.11.1";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.TEX_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	public void testTEX12Paths() throws IOException {
		String fileRoot = "TEX.g.12.0";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.TEX_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	public void testTEX13Paths() throws IOException {
		String fileRoot = "TEX.g.13.1";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.TEX_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	public void testBLK_SAM3() throws IOException {
		String fileRoot = "BLK_SAM.g.3.1";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.BLKSAM_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	@Ignore
	public void testBLK_SAM4() throws IOException {
		String fileRoot = "BLK_SAM.g.4.0";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.BLKSAM_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	public void testBLK_SAM4Bot() throws IOException {
		String fileRoot = "BLK_SAM.g.4.0.bot";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.BLKSAM_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	public void testBLK_SAM4Top() throws IOException {
		String fileRoot = "BLK_SAM.g.4.0.top";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.BLKSAM_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}

	@Test
	/** checks that graphics attributes are copied to child Elements
	 *  
	 * @throws IOException
	 */
	public void testBLK_SAM4TopSmall() throws IOException {
		String fileRoot = "BLK_SAM.g.4.0.top.small";
		File svgPathFile = createSVGPathsAndWriteToSVGPathFile(
			new File(SVGHTMLFixtures.BLKSAM_PLOT_DIR, fileRoot + ".svg"), fileRoot);
		createPlots(svgPathFile, fileRoot);
	}



	// =======================
	
	private void createFlow(File inputFile, String fileRoot) throws IOException {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(inputFile);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);

		TextChunk phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		
//		Real2 xMargins = new Real2(5.0, 5.0);
//		Real2 yMargins = new Real2(2.0, 2.0);
//		List<TextBox> textBoxList = textStructurer.createTextBoxList(phraseListList, xMargins, yMargins);
//		FlowStructurer flowStructurer = textStructurer.createFlowStructurer(phraseListList);
//		List<SVGShape> shapeList = flowStructurer.makeShapes();
		
//		SVGG g = new SVGG();
//		for (TextBox textBox : textBoxList) {
//			SVGShape rect = textBox.getOrCreateBoundingRect();
//			rect.setFill("black");
//			rect.setOpacity(0.2);
//			rect.setStrokeWidth(1.5);
//			textBox.getStringValue();
//			g.appendChild(new TextBox(textBox));
//		}
//		
//		for (SVGShape shape : shapeList) {
//			if (shape instanceof SVGRect) {
//				shape.setFill("cyan");
//				shape.setOpacity(0.2);
//			} else if (shape instanceof SVGLine) {
//				shape.setStroke("red");
//			} else if (shape instanceof SVGPolyline) {
//				SVGPolyline polyline = (SVGPolyline) shape;
//				polyline.setStroke("orange");
//				polyline.setFill("blue");
//				polyline.setStrokeWidth(1.0);
//			} else if (shape instanceof SVGPolygon) {
//				SVGPolygon polygon = (SVGPolygon) shape;
//				polygon.setStroke("blue");
//				polygon.setFill("orange");
//				polygon.setStrokeWidth(2.0);
//			} 
//			g.appendChild(shape.copy());
//		}
//		SVGSVG.wrapAndWriteAsSVG(g, new File(TARGET_PLOTS, fileRoot+"/"+PLOT_SVG));
		
	}
	
	public static File createSVGPathsAndWriteToSVGPathFile(File inputFile, String fileRoot) throws IOException {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(inputFile);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);

		TextChunk phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		
//		Real2 xMargins = new Real2(5.0, 5.0);
//		Real2 yMargins = new Real2(2.0, 2.0);
//		List<TextBox> textBoxList = textStructurer.createTextBoxList(phraseListList, xMargins, yMargins);
//		FlowStructurer flowStructurer = textStructurer.createFlowStructurer(phraseListList);
//		List<SVGShape> shapeList = flowStructurer.makeShapes();
//		
//		SVGG g = new SVGG();
//		for (TextBox textBox : textBoxList) {
//			SVGShape rect = textBox.getOrCreateBoundingRect();
//			rect.setFill("black");
//			rect.setOpacity(0.2);
//			rect.setStrokeWidth(1.5);
//			textBox.getStringValue();
//			g.appendChild(new TextBox(textBox));
//		}
//		
//		for (SVGShape shape : shapeList) {
//			if (false) {
//			} else if (shape instanceof SVGCircle) {
//				shape.setStroke("purple");
//			} else if (shape instanceof SVGLine) {
//				shape.setStroke("red");
//			} else if (shape instanceof SVGEllipse) {
//				shape.setStroke("green");
//			} else if (shape instanceof SVGPath) {
//				shape.setStroke("gray");
//			} else if (shape instanceof SVGPolyline) {
//				SVGPolyline polyline = (SVGPolyline) shape;
//				polyline.setStroke("orange");
////				polyline.setFill("blue");
//				polyline.setStrokeWidth(1.0);
//			} else if (shape instanceof SVGPolygon) {
//				SVGPolygon polygon = (SVGPolygon) shape;
//				polygon.setStroke("blue");
//				polygon.setFill("orange");
//				polygon.setStrokeWidth(2.0);
//			} else if (shape instanceof SVGRect) {
//				shape.setFill("cyan");
//				shape.setOpacity(0.2);
//			} else {
//				throw new RuntimeException("unknown Shape: "+shape);
//			}
//			g.appendChild(shape.copy());
//		}
//		File svgFile = new File(TARGET_PLOTS, fileRoot+"/"+PLOT_SVG);
//		SVGSVG.wrapAndWriteAsSVG(g, svgFile);
		return /*svgFile*/ null;
		
	}
	
	// I don't think this does anything yet
	public static File createPlots(File svgFile, String fileRoot) throws FileNotFoundException {
//		SVGG g = (SVGG) SVGElement.readAndCreateSVG(svgFile).getChildElements().get(0);
//		File outDir = new File(TARGET_PLOTS, fileRoot+"/");
//		SVGSVG svg = new SVGSVG();
//		g.detach();
//		svg.appendChild(g);
//		File outfileSVG = new File(outDir, PLOT_SVG);
////		SVGUtil.debug(svg, new FileOutputStream(outfileSVG), 1);
//		return outfileSVG;
		return null;
	}
	
	// I don't think this does anything yet
	public static File createCSV(File svgFile, String fileRoot) throws IOException {
		String blackDot = "\u25CF";
		AbstractCMElement g = (AbstractCMElement) SVGElement.readAndCreateSVG(svgFile).getChildElements().get(0);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(g);
		StringBuilder sb = new StringBuilder();
		for (SVGText text : textList) {
			if (blackDot.equals(text.getText())) {
				Real2 xy = text.getXY();
				sb.append(xy.getX()+","+xy.getY()+"\n");
			}
		}
		File outDir = new File(TARGET_PLOTS, fileRoot+"/");
		File outfileCSV = new File(outDir, PLOT_CSV);
		FileUtils.write(outfileCSV, sb.toString());
		return outfileCSV;
	}
	
	
	



}
