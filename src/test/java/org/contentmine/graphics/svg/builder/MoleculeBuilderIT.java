package org.contentmine.graphics.svg.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.fonts.GlyphSet;
import org.contentmine.graphics.svg.plot.XPlotBox;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

@Ignore("This really should be in POM or CL")
public class MoleculeBuilderIT {
	private static final Logger LOG = Logger.getLogger(MoleculeBuilderIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
		public void testMoleculesInBoxes() {
	//		MoleculeBuilder moleculeBuilder = new MoleculeBuilder();
			String dirRoot = "molecules/cypMolecules";
			String fileroot = "moleculesInBoxes";
			File outputDir = new File("target/", dirRoot);
			File inputDir = new File(SVGHTMLFixtures.GR_SVG_DIR, dirRoot);
			File inputFile = new File(inputDir, fileroot + ".svg");
			SVGElement svgElement = SVGElement.readAndCreateSVG(inputFile);
			XPlotBox xPlotBox = new XPlotBox();
			ComponentCache componentCache = new ComponentCache(xPlotBox); 
			componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
			List<SVGRect> rectList = componentCache.getOrCreateRectCache().getOrCreateRectList();
			// picks up some of the characters as rects
			SVGElement.removeElementsSmallerThanBox(rectList, new Real2(30., 30.));
			SVGSVG.wrapAndWriteAsSVG(rectList, new File(outputDir, fileroot+"/"+"rects.svg"));
			Assert.assertEquals("boxes", 14, rectList.size());
			List<SVGElement> allElements0 = SVGElement.extractSelfAndDescendantElements(svgElement);
			GlyphSet glyphSet = GlyphSet.readGlyphSet(new File(inputDir, "glyphSet.xml"));
			// doesn't work
			List<SVGElement> allElements = glyphSet.createTextFromGlyph(allElements0);
	//		List<SVGElement> allElements = allElements0;
			int[] boxCount = {56,60,57,44,59,60,54,57,56,59,59,59,56,61};
			for (int box = 0; box < boxCount.length; box++) {
				LOG.debug("========"+box+"======= =======");
				SVGRect rect = rectList.get(box);
				List<SVGElement> allElementsTemp = new ArrayList<SVGElement>(allElements);
				// FIXME this is messy
				List<SVGElement> elementsInBox = SVGElement.extractElementsContainedInBox(allElementsTemp, 
						rect.getBoundingBox().extendBothEndsBy(Direction.VERTICAL, 2.0, 2.0));
				// kludge
				String boxFileroot = fileroot+"/"+"box"+"."+box;
				String outputFileRoot = boxFileroot+"/"+"orig.svg";
				File boxOutputFile = new File(outputDir, outputFileRoot);
				SVGSVG.wrapAndWriteAsSVG(elementsInBox, boxOutputFile);
				LOG.debug("writing: "+boxOutputFile);
				MoleculeBuilder moleculeBuilderi = new MoleculeBuilder();
				try {
					File outputDir2 = new File("target/", dirRoot+"/"+boxFileroot);
					moleculeBuilderi.setOutputDir(outputDir2);
					File inputDir2 = new File("target/", dirRoot);
					moleculeBuilderi.setInputDir(inputDir2);
					moleculeBuilderi.setInputFile(boxOutputFile);
					AbstractCMElement svgElementi = SVGElement.readAndCreateSVG(boxOutputFile);
					moleculeBuilderi.createWeightedLabelledGraph(svgElementi);
					moleculeBuilderi.outputFiles(fileroot);
				} catch (Exception e) {
					LOG.error("Cannot parse box: "+box+"; skipping: "+e);
				}
			}
		}

}
