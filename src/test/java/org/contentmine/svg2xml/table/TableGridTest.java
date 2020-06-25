package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Test;

public class TableGridTest {

	private static final Logger LOG = LogManager.getLogger(TableGridTest.class);
@Test
	public void testReadRelative() throws FileNotFoundException {
		File cellFile = new File(SVG2XMLFixtures.TABLE_DIR, "els/table1top.svg");
		SVGSVG svg = (SVGSVG) SVGUtil.parseToSVGElement(new FileInputStream(cellFile)); 
		List<SVGShape> shapeList = SVGUtil.makeShapes(svg);
	}
}
