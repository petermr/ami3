package org.contentmine.ami.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGClipPath;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.Norma;
import org.contentmine.svg2xml.PDF2SVGConverter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multimap;

//@Ignore("still under development")
public class HEPTest {
	
	private static final Logger LOG = Logger.getLogger(HEPTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	// SVG viewer http://www.smashingmagazine.com/2014/05/love-generating-svg-javascript-move-to-server/

	@Test
	@Ignore("long")
	public void test50523() {
		File file = new File("../pdfs/misc/art%3A10.1007%2Fs002880050523/fulltext.pdf");
		Assert.assertTrue(file.exists());
		String args = "-q ../pdfs/misc/art%3A10.1007%2Fs002880050523/ -i fulltext.pdf -o fulltext.pdf.html --transform pdf2html";
		Norma norma = new Norma();
		norma.run(args);
	}
	
	@Test
	@Ignore
	public void createPDFHTML() throws Exception {
		FileUtils.copyDirectory(new File("../pdfs/misc/art%3A10.1007%2Fs002880050523/"), new File("target/hep/"));
//		new Norma().run("-i ../pdfs/misc/art%3A10.1007%2Fs002880050523/fulltext.pdf -o target/hep/");
		new Norma().run("-q target/hep/ -i fulltext.pdf -o fulltext.pdf.html --transform pdf2html");
	}
	
 
	
	@Test
	@Ignore // too long
	public void test50523Converter() throws Exception {
		File file = new File("../pdfs/misc/art%3A10.1007%2Fs002880050523/fulltext.pdf");
		Assert.assertTrue(file.exists());
		PDF2SVGConverter converter = new PDF2SVGConverter();
		LOG.error("FIXME");
//		converter.openPDFFile(file);
	}
		
	@Test
	@Ignore // too long
// PDF uses zillions (10000) of small pngs to create shaded background ARRGHH.
	public void testHEP1() throws Exception {
		File file = new File(NAConstants.TEST_AMI_DIR, "hep/hep1/hep1.pdf");
		Assert.assertTrue(file.exists());
		PDF2SVGConverter converter = new PDF2SVGConverter();
//		converter.openPDFFile(file);
		for (int i = 1; i < 24; i++) {
			LOG.debug("page: "+i);
			drawWithoutClipPath("hep/hep1/hep1-page"+i+".");
			exploreClipPaths("hep/hep1/hep1-page"+i+".");
		}
		
	}

	//=========================================
	
	private void drawWithoutClipPath(String root) {
		File svgFile = new File(NAConstants.TEST_AMI_DIR, root+"svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		List<SVGElement> defs = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='defs']");
		for (SVGElement def : defs) {
			def.detach();
			SVGSVG.wrapAndWriteAsSVG(svgElement, new File("target/"+root+".noclip.svg"));
		}
	}

	private void exploreClipPaths(String root) {
		File svgFile = new File(NAConstants.TEST_AMI_DIR, root+"svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		List<SVGClipPath> clipPathList = SVGClipPath.extractUsedClipPaths(svgElement);
//		LOG.debug("before: "+clipPathList.size());
//		SVGClipPath.detachUnusedClipPathElements(svgElement, clipPathList);
//		LOG.debug("a: "+clipPathList.size());
//		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
//		List<SVGText> textList = SVGText.extractTexts(
//				SVGUtil.getQuerySVGElements(svgElement, "//*[local-name()='text']"));
		SVGClipPath largestClipPath = SVGClipPath.getLargestClipPath(clipPathList);
		if (largestClipPath != null) {
			clipPathList.remove(largestClipPath);
			LOG.trace("largest: "+largestClipPath+"; from "+clipPathList.size());
//			drawContentsByClipPathId(root, svgElement, clipPathList);
			drawGeometricalContentsOfClipPath(root, svgElement, clipPathList);
		}
	}

	private void drawContentsByClipPathId(String root, SVGElement svgElement, List<SVGClipPath> clipPathList) {
		Multimap<String, SVGElement> elementsByClipPath = SVGClipPath.getElementsByClipPath(svgElement);
		for (SVGClipPath clipPath : clipPathList) {
			String clipPathId = clipPath.getId();
			SVGG g = new SVGG();
			List<SVGElement> elements = new ArrayList<SVGElement>(elementsByClipPath.get(clipPathId));
			for (SVGElement element : elements) {
				g.appendChild(element.copy());
			}
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/"+root+clipPathId+".id.svg"));
		}
	}

	private void drawGeometricalContentsOfClipPath(String root, SVGElement svgElement, List<SVGClipPath> clipPathList) {
//		Map<String, SVGClipPath> clipPathById = SVGClipPath.getClipPathById(svgElement);
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(svgElement, "//*");
		for (SVGClipPath clipPath : clipPathList) {
			String clipPathId = clipPath.getId();
			SVGG g = new SVGG();
//			SVGClipPath clipPath = clipPathById.get();
			Real2Range bbox = clipPath.getBoundingBox();
			for (SVGElement element : elements) {
				if (bbox.includes(element.getBoundingBox())) {
					g.appendChild(element.copy());
				}
			}
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/"+root+clipPathId+".contents.svg"));
		}
	}
	
	private void extractPoints(File svgFile, File outfile, Real2Range bbox) {
		SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		List<SVGClipPath> clipList = SVGClipPath.extractClipPaths(svgElement);
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGText> textList = SVGText.extractTexts(
				SVGUtil.getQuerySVGElements(svgElement, "//*[local-name()='text']"));
		removeElementsOutside(new SVGRect(bbox), pathList, textList);
		SVGG gg = createSVGObjects(pathList, textList);
		SVGSVG.wrapAndWriteAsSVG(gg, outfile);
	}
	
	
	
	@Test
	public void testGraph() {
		SVGElement svgElement = SVGElement.readAndCreateSVG(new File(NAConstants.TEST_AMI_DIR, "hep/33.1.svg"));
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGText> textList = SVGText.extractTexts(
				SVGUtil.getQuerySVGElements(svgElement, "//*[local-name()='text']"));
		SVGG gg = createSVGObjects(pathList, textList);
//		Assert.assertEquals(123, gg.)
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/hep/hep0.svg"));
	}

	@Test
	public void testGraphInBox() {
		File svgFile = new File(NAConstants.TEST_AMI_DIR, "hep/fulltext-page17.svg");
		SVGElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		List<SVGPath> pathList = SVGPath.extractPaths(svgElement);
		List<SVGText> textList = SVGText.extractTexts(
				SVGUtil.getQuerySVGElements(svgElement, "//*[local-name()='text']"));
		SVGRect big = new SVGRect(new Real2(30., 25.), new Real2(300., 280.));
		big.setStrokeWidth(0.3);
		big.setStroke("blue");
		SVGRect small = new SVGRect(new Real2(168.5, 36.), new Real2(279, 142.6));
		small.setStrokeWidth(0.3);
		small.setStroke("red");
		removeElementsInside(small, pathList, textList);
		removeElementsOutside(big, pathList, textList);

		Assert.assertEquals("text", 102,textList.size());
		Assert.assertEquals("path", 445, pathList.size());
		SVGG gg = createSVGObjects(pathList, textList);
		gg.appendChild(big);
		gg.appendChild(small);
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/hep/points.svg"));
	}

	// ========================================================
	
	private void removeElementsInside(SVGRect box, List<SVGPath> pathList, List<SVGText> textList) {
		removeElements(box, pathList, true);
		removeElements(box, textList, true);
	}

	private void removeElementsOutside(SVGRect box, List<SVGPath> pathList, List<SVGText> textList) {
		removeElements(box, pathList, false);
		removeElements(box, textList, false);
	}

	private void removeElements(SVGRect box, List<? extends SVGElement> svgElementList, boolean keep) {
		for (int i = svgElementList.size() - 1; i >= 0; i--) {
			SVGElement element = svgElementList.get(i);
			if (box.includes(element) == keep) {
				svgElementList.remove(i);
			}
		}
	}

	private SVGG createSVGObjects(List<SVGPath> pathList, List<SVGText> textList) {
		List<SVGPath> pathMCCCCList = new ArrayList<SVGPath>();
		List<SVGPath> pathMLList = new ArrayList<SVGPath>();
		List<SVGPath> pathMLLLZList = new ArrayList<SVGPath>();
		List<SVGPath> pathOtherList = new ArrayList<SVGPath>();
		for (SVGPath path : pathList) {
			String sig = path.createSignatureFromDStringPrimitives();
			if ("MCCCC".equals(sig)) {
				pathMCCCCList.add(path);
			} else if ("ML".equals(sig)) {
				pathMLList.add(path);
			} else if ("MLLLZ".equals(sig)) {
				pathMLLLZList.add(path);
			} else {
				pathOtherList.add(path);
				LOG.trace(">>"+sig);
			}
		}
		List<SVGLine> lineList = makeLines(pathMLList);
		List<SVGCircle> circleList = makeCircles(pathMCCCCList);
		List<SVGRect> rectList = makeRects(pathMLLLZList);
		SVGG gg = makePoints(lineList, circleList);
		for (SVGRect rect : rectList) {
			gg.appendChild(rect);
		}
		for (SVGPath path : pathList) {
			gg.appendChild(new SVGPath(path));
		}
		for (SVGText text : textList) {
			gg.appendChild(new SVGText(text));
		}
		return gg;
	}

	private SVGG makePoints(List<SVGLine> lineList, List<SVGCircle> circleList) {
		SVGG gg = new SVGG();
		for (SVGCircle circle : circleList) {
			SVGG point = new SVGG();
			Real2Range bbox = circle.getBoundingBox();
			point.appendChild(new SVGCircle(circle));
			for (SVGLine line : lineList) {
				Real2Range lineBox = line.getBoundingBox();
				if (bbox.intersectionWith(lineBox) != null) {
					point.appendChild(new SVGLine(line));
				} 
			}
			gg.appendChild(point);
		}
		return gg;
	}

	private List<SVGLine> makeLines(List<SVGPath> pathMLList) {
		List<SVGLine> lineList = new ArrayList<SVGLine>(); 
		for (SVGPath path : pathMLList) {
			SVGPoly poly = path.createPolyline();
			SVGLine line = poly.createLineList().get(0);
			line.setStrokeWidth(0.3);
			lineList.add(line);
		}
		return lineList;
	}
	
	private List<SVGCircle> makeCircles(List<SVGPath> pathMCCCCList) {
		List<SVGCircle> circleList = new ArrayList<SVGCircle>(); 
		for (SVGPath path : pathMCCCCList) {
			Real2Range bbox = path.getBoundingBox();
			// increase the radius to capture the lines
			SVGCircle circle = new SVGCircle(bbox.getCentroid(), bbox.getXRange().getRange() / 1.5);
			circleList.add(circle);
		}
		return circleList;
	}
	
	private List<SVGRect> makeRects(List<SVGPath> pathMLLLZList) {
		List<SVGRect> rectList = new ArrayList<SVGRect>(); 
		for (SVGPath path : pathMLLLZList) {
			Real2Range bbox = path.getBoundingBox();
			// increase the radius to capture the lines
			SVGRect rect = SVGRect.createFromReal2Range(bbox);
			rectList.add(rect);
		}
		return rectList;
	}
}
