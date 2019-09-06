package org.contentmine.graphics.svg.path;

import java.io.File;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

public class ArcTest {

	private final static Logger LOG = Logger.getLogger(ArcTest.class);

	@Test
	public void testArc() {
		SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "hollowcorner.svg"))
				.getChildElements().get(0);
		Assert.assertEquals("sig",  "MLCLLLCL", svgPath.getOrCreateSignatureAttributeValue());
		PathPrimitiveList primList = svgPath.getOrCreatePathPrimitiveList();
		Arc arc = new Arc((CubicPrimitive) primList.get(2));
		Real2 centre = arc.getCentre();
		Assert.assertNotNull("centre", centre);
		centre = centre.format(3);
		Assert.assertEquals("centre", "(287.93,91.61)", centre.toString());
		Double radius = Util.format(arc.getRadius(), 2);
		Assert.assertEquals("centre", 2.65, radius, 0.001);
		CubicPrimitive cubic = arc.getCubicPrimitive();
		Assert.assertEquals("centre", "C288.894 89.283 290.256 90.645 290.495 92.276", cubic.toString().trim());
	}

	@Test
	public void testMeanArc() {
		SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_PATHS_DIR, "hollowcorner.svg"))
				.getChildElements().get(0);
		PathPrimitiveList primList = svgPath.getOrCreatePathPrimitiveList();
		Arc meanArc = primList.createMeanCubic(2, 6);
		Assert.assertEquals("skeleton", ""
				+ "M286.583 88.988 "
				+ "L287.263 89.045 "
				+ "C288.837 89.422 290.105 90.676 290.381 92.276 "
				+ "L290.552 92.957 "
				+ "L290.325 92.957 "
				+ "L290.268 92.276 "
				+ "C290.105 90.676 288.837 89.422 287.235 89.158 "
				+ "L286.583 89.215",
				primList.getDString().trim());
		SVGPath newPath = new SVGPath(primList, svgPath);
		SVGSVG.wrapAndWriteAsSVG(newPath, new File("target/skeleton.svg"));
	}
}
