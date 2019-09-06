package org.contentmine.graphics.svg.glyphs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.fonts.GlyphSet;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.contentmine.graphics.svg.plot.XPlotBox;
import org.junit.Assert;
import org.junit.Test;

/** translates cursive glyphs into characters.
 * 
 * @author pm286
 *
 */
public class GlyphTest {
	
	public static final Logger LOG = Logger.getLogger(GlyphTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testGlyphDebug() {
		String fileroot = "figure4b";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
//		LOG.debug("CC"+componentCache);
		List<SVGRect> rects = componentCache.getOrCreateRectCache().getOrCreateRectList();
		SVGSVG.wrapAndWriteAsSVG(rects, new File(outputDir, fileroot+"/"+"rects.svg"));
		List<SVGLine> lines = componentCache.getOrCreateLineCache().getOrCreateLineList().getLineList();
		SVGSVG.wrapAndWriteAsSVG(lines, new File(outputDir, fileroot+"/"+"lines.svg"));
//		List<SVGPolyline> polylines = componentCache.getOrCreatePolylineCache().getOrCreatePolylineList();
		List<SVGPolyline> polylines = componentCache.getOrCreateShapeCache().getPolylineList();
		SVGSVG.wrapAndWriteAsSVG(polylines, new File(outputDir, fileroot+"/"+"polylines.svg"));
//		List<SVGPolygon> polygons = componentCache.getOrCreatePolygonCache().getOrCreatePolygonList();
		List<SVGPolygon> polygons = componentCache.getOrCreateShapeCache().getPolygonList();
		SVGSVG.wrapAndWriteAsSVG(polygons, new File(outputDir, fileroot+"/"+"polygons.svg"));
		List<SVGPath> paths = componentCache.getOrCreatePathCache().getCurrentPathList();
		SVGG g = new SVGG();
		for (int i = 0; i < paths.size(); i++) {
			SVGPath path = paths.get(i);
			g.appendChild(path.copy());
			Real2 xy = path.getXY();
			SVGText text = SVGText.createDefaultText(xy, ""+i);
			g.appendChild(text);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, fileroot+"/"+"paths.svg"));
	}

	@Test
	public void testSplitAfterZ() {
		String fileroot = "figure4b";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		List<SVGPath> paths = componentCache.getOrCreatePathCache().getCurrentPathList();
		SVGPath cyp1a2 = paths.get(40); //CYP1A2
		PathPrimitiveList pathPrimitiveList = cyp1a2.getOrCreatePathPrimitiveList();
//		LOG.debug(">>>"+new SVGPath(pathPrimitiveList).getSignature());
		List<PathPrimitiveList> pathPrimitiveListList = pathPrimitiveList.splitAfter(ClosePrimitive.class);
		Assert.assertEquals("split", 8, pathPrimitiveListList.size());
		SVGG g = createAnnotatedSVG(pathPrimitiveListList);
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, fileroot+"/"+"splitAfterZ.svg"));
	}


	@Test
	/** split all glyphs before M and create SVG.
	 * 
	 */
	public void testSplitAllBeforeMSVG() {
		String fileroot = "figure4b";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		List<SVGPath> paths = componentCache.getOrCreatePathCache().getCurrentPathList();
		SVGG g = new SVGG();
		for (SVGPath path : paths) {
			PathPrimitiveList pathPrimitiveList = path.getOrCreatePathPrimitiveList();
			List<PathPrimitiveList> pathPrimitiveListList = pathPrimitiveList.splitBefore(MovePrimitive.class);
			AbstractCMElement gg = createAnnotatedSVG(pathPrimitiveListList);
			g.appendChild(gg);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, fileroot+"/"+"splitAllBeforeM.svg"));
	}

	@Test
	public void testSplitAllBeforeMSignature() {
		String fileroot = "figure4b";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		GlyphSet glyphSet = new GlyphSet();
		glyphSet.createGlyphSetsAndAnalyze(fileroot, outputDir, inputFile);
		
	}

	/** this is chemistry
	 * 
	 */
	@Test
	public void testFigure1() {
		String fileroot = "figure1";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		GlyphSet glyphSet = new GlyphSet();
		glyphSet.createGlyphSetsAndAnalyze(fileroot, outputDir, inputFile);
		
	}

	/** reads a chemistry diagram and creates a path set using GlyphSet
	 * includes writing lines to glyphset.
	 * we will triage these out in a later version
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testFigure1M1() throws IOException {
		String fileroot = "figure1.M1";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		GlyphSet glyphSet = new GlyphSet();
		glyphSet.createGlyphSetsAndAnalyze(fileroot, outputDir, inputFile);
		glyphSet.writeGlyphSet(new File(outputDir, fileroot+"/"+"rawPathSet.xml"));
	}	
	
	/** this is chemistry as well
	 * contains lines and glyphs as paths.
	 * 
	 * translates each path either to a line or looks up as a glyph. The glyphs have already been stored 
	 * and interpreted manually in glyphSet.xml
	 * 
	 */
	@Test
	public void testFigure1M1Chemistry() {
		String fileroot = "figure1.M1";
		String dirRoot = "glyphs";
		File outputDir = new File("target/", dirRoot);
		File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
		File inputFile = new File(inputDir, fileroot + ".svg");
		Assert.assertTrue("exists: "+inputFile, inputFile.exists());
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputFile);
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		
		SVGG g = new SVGG();
		List<SVGLine> lines = componentCache.getOrCreateLineCache().getOrCreateLineList().getLineList();
		for (SVGLine line : lines) {
			line.setStrokeWidth(0.3);
			g.appendChild(line.copy());
			
		}
		// prepared glyphs
		GlyphSet glyphSet = GlyphSet.readGlyphSet(new File(inputDir, "glyphSet.xml"));
		List<SVGPath> paths = componentCache.getOrCreatePathCache().getCurrentPathList();
		List<SVGText> textList = new ArrayList<SVGText>();
		for (SVGPath path : paths) {
			SVGText text = glyphSet.createTextFromGlyph(path);
			textList.add(text);
		}
		for (SVGText text : textList) {
			g.appendChild(text);
		}
		File file = new File(outputDir, fileroot+"/"+"characters.svg");
		SVGSVG.wrapAndWriteAsSVG(g, file);
		
	}

	


	// ================
	
	private SVGG createAnnotatedSVG(List<PathPrimitiveList> pathPrimitiveListList) {
		SVGG g = new SVGG();
		for (int serial = 0; serial < pathPrimitiveListList.size(); serial++) {
			PathPrimitiveList pathPrimitiveListx = pathPrimitiveListList.get(serial);
			g.appendChild(pathPrimitiveListx.createAnnotatedSVG(String.valueOf(serial)));
//			LOG.debug(pathPrimitiveListx.getOrCreateSignature());
		}
		return g;
	}
}
