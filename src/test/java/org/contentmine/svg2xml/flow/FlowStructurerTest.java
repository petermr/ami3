package org.contentmine.svg2xml.flow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.objects.ArrowFactory;
import org.contentmine.graphics.svg.objects.SVGArrow;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore // currently not being developed and occasionally throws platform-dependent errors

public class FlowStructurerTest {
	
	public static final Logger LOG = Logger.getLogger(FlowStructurerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String AMA_FIG_1_SVG = "AMA_Dobson.fig.1.svg";
	private static final String LANCETMICRO_SVG = "LANCETMicro.svg";
	private static final String LANCETMINI0_SVG = "LANCETMini0.svg";
	private static final String LANCETMINI_SVG = "LANCETMini.svg";
	private static final String LANCET_G_5_0_SVG = "LANCET.g.5.0.svg";
	private static final String LANCET_MINIPATHS_SVG = "LANCETminipaths.svg";
	private static final String LPW_G_4_0_SVG = "LPW.g.4.0.svg";

	@Test
	public void testLANCETMicro() throws IOException {
		createFlow(
			new File(SVG2XMLFixtures.FLOW_DIR, LANCETMICRO_SVG),
			"lancetMicro"
		);
	}

	@Test
	public void testLANCETMini0() throws IOException {
		createFlow(
			new File(SVG2XMLFixtures.FLOW_DIR, LANCETMINI0_SVG),
			"lancetMini0"
		);
	}

	@Test
	public void testLANCETMini() throws IOException {
		createFlow(
			new File(SVG2XMLFixtures.FLOW_DIR, LANCETMINI_SVG),
			"lancetMini"
		);
	}

	@Test
	public void testLANCETFlow() throws IOException {
		createFlow(
			new File(SVG2XMLFixtures.FLOW_DIR, LANCET_G_5_0_SVG),
			"lancet"
		);
	}

	@Test
	public void testLANCETMinipaths() throws IOException {
		createPaths(
			new File(SVG2XMLFixtures.FLOW_DIR, LANCET_MINIPATHS_SVG),
			"lancetMinipaths"
		);
	}

	@Test
	public void testLANCETPaths() throws IOException {
		createPaths(
			new File(SVG2XMLFixtures.FLOW_DIR, LANCET_G_5_0_SVG),
			"lancetPaths"
		);
	}

	@Test
	public void testAMAPaths() throws IOException {
		String outRoot = "amaPaths";
		createPaths(
			new File(SVG2XMLFixtures.FLOW_DIR, AMA_FIG_1_SVG),
			outRoot
		);
		createArrows(outRoot, 24);
	}

	@Test
	public void testLPWPaths() throws IOException {
		String outRoot = "lpwPaths";
		createPaths(
			new File(SVG2XMLFixtures.FLOW_DIR, LPW_G_4_0_SVG),
			outRoot
		);
		createArrows(outRoot, 0);
	}
	
	@Test
	public void testSAGE_Sbarra() throws IOException {
		String outRoot = "sageSbarra";
		createPaths(
			new File(SVG2XMLFixtures.FLOW_DIR, "SAGE_Sbarra.g.6.0.svg"),
			outRoot
		);
		createArrows(outRoot, 0);
	}

	// =======================
	
	private void createFlow(File inputFile, String outRoot) throws IOException {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(inputFile);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);

		TextChunk phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		
		Real2 xMargins = new Real2(5.0, 5.0);
		Real2 yMargins = new Real2(2.0, 2.0);
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
//		SVGSVG.wrapAndWriteAsSVG(g, new File("target/flow/"+outRoot+"/textbox.svg"));
		
	}
	
	private void createPaths(File inputFile, String outRoot) throws IOException {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(inputFile);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);

		TextChunk phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		
		Real2 xMargins = new Real2(5.0, 5.0);
		Real2 yMargins = new Real2(2.0, 2.0);
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
//			if (shape instanceof SVGRect) {
//				shape.setFill("cyan");
//				shape.setOpacity(0.2);
//			} else if (shape instanceof SVGLine) {
//				shape.setStroke("red");
//			} else if (shape instanceof SVGEllipse) {
//				shape.setStroke("green");
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
//			} 
//			g.appendChild(shape.copy());
//		}
//		SVGSVG.wrapAndWriteAsSVG(g, new File("target/flow/"+outRoot+"/textbox.svg"));
		
	}
	
	private void createArrows(String outRoot, int arrowCount) throws FileNotFoundException {
		File outfile = new File("target/flow/"+outRoot+"/textbox.svg");
		AbstractCMElement g = (AbstractCMElement) SVGElement.readAndCreateSVG(outfile).getChildElements().get(0);
		ArrowFactory arrowFactory = new ArrowFactory();
		arrowFactory.setMarkerEnd(SVGArrow.ARROWHEAD);
		arrowFactory.setStroke("orange");
		arrowFactory.replaceLinesAndTrianglesByArrows(g);
		Assert.assertEquals(arrowCount, arrowFactory.getArrowList().size());
		File outDir = new File("target/arrows/"+outRoot+"/");
		outDir.mkdirs();
		new File("target/arrows/").mkdirs();
		SVGSVG svg = new SVGSVG();
		svg.setMarker(SVGArrow.ARROWHEAD);
		g.detach();
		svg.appendChild(g);
		SVGUtil.debug(svg, new FileOutputStream(new File(outDir, "multiTextBox.svg")), 1);
	}
	



}
