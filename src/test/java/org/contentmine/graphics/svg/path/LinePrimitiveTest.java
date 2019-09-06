package org.contentmine.graphics.svg.path;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

public class LinePrimitiveTest {
	private static final Logger LOG = Logger.getLogger(LinePrimitiveTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testLinePrimitive() {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "hollowcorner.svg"));
		SVGPath svgPath = SVGPath.extractSelfAndDescendantPaths(svgElement).get(0);
		Assert.assertEquals("sig",  "MLCLLLCL", svgPath.getOrCreateSignatureAttributeValue());
		PathPrimitiveList primList = svgPath.getOrCreatePathPrimitiveList();
		LinePrimitive line1 = (LinePrimitive) primList.get(1);
		Assert.assertEquals("line1", "L287.263 89.045 ", line1.toString());
		Assert.assertEquals("line1f", "(287.263,89.045)", line1.getFirstCoord().toString());
		Assert.assertEquals("line1l", "(287.263,89.045)", line1.getLastCoord().toString());
		LinePrimitive line3 = (LinePrimitive) primList.get(3);
		Assert.assertEquals("line3", "L290.552 92.957 (290.495,92.276)", ""+line3+line3.getZerothCoord());
		LinePrimitive line4 = (LinePrimitive) primList.get(4);
		Assert.assertEquals("line4", "L290.325 92.957 (290.552,92.957)", ""+line4+line4.getZerothCoord());
		LinePrimitive line5 = (LinePrimitive) primList.get(5);
		Assert.assertEquals("line5", "L290.268 92.276 (290.325,92.957)", ""+line5+line5.getZerothCoord());
		LinePrimitive line7 = (LinePrimitive) primList.get(7);
		Assert.assertEquals("line7", "L286.583 89.215 (287.207,89.271)", ""+line7+line7.getZerothCoord());
	}
	
	@Test
	public void testCalculateMeanLine() {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "hollowcorner.svg"));
		SVGPath svgPath = SVGPath.extractSelfAndDescendantPaths(svgElement).get(0);
		PathPrimitiveList primList = svgPath.getOrCreatePathPrimitiveList();
		LinePrimitive line1 = (LinePrimitive) primList.get(1);
		LinePrimitive line3 = (LinePrimitive) primList.get(3);
		LinePrimitive line4 = (LinePrimitive) primList.get(4);
		LinePrimitive line5 = (LinePrimitive) primList.get(5);
		LinePrimitive line7 = (LinePrimitive) primList.get(7);
		
		LinePrimitive line17 = line1.calculateMeanLine(line7);
		Assert.assertEquals("line17", "L287.235 89.158", line17.toString().trim());
		LinePrimitive line35 = line3.calculateMeanLine(line5);
		Assert.assertEquals("line35", "L290.438 92.957", line35.toString().trim());
		
		primList.replaceCoordinateArray(line17.getCoordArray(), 1);
		primList.replaceCoordinateArray(line17.getReverseCoordArray(), 7);
		primList.replaceCoordinateArray(line35.getCoordArray(), 3);
		primList.replaceCoordinateArray(line35.getReverseCoordArray(), 5);
		Assert.assertEquals("skeleton", ""
			+ "M286.583 88.988 "
			+ "L287.235 89.158 "
			+ "C288.894 89.283 290.256 90.645 290.495 92.276 "
			+ "L290.438 92.957 "
			+ "L290.325 92.957 "
			+ "L290.381 92.276 "
			+ "C289.955 90.709 288.781 89.561 287.207 89.271 "
			+ "L286.583 89.101",
			primList.getDString().trim());
		SVGPath newPath = new SVGPath(primList, svgPath);
		SVGSVG.wrapAndWriteAsSVG(newPath, new File("target/skeletonLine.svg"));
	}
}
