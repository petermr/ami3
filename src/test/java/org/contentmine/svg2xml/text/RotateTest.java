package org.contentmine.svg2xml.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.page.PageLayoutAnalyzerNEW;
import org.contentmine.svg2xml.table.TableStructurer;
import org.junit.Assert;
import org.junit.Test;


public class RotateTest {
	private static final Logger LOG = Logger.getLogger(RotateTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void readRotatedText() throws FileNotFoundException {
		File file = new File(SVG2XMLFixtures.TABLE_DIR, "rotate/pageTableText.svg");
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(new File(SVG2XMLFixtures.TABLE_DIR, "rotate/pageTableText.svg"));
		List<SVGText> textList = textStructurer.getCharacterList();
		Assert.assertEquals(409,  textList.size());
		
	}
	
	@Test
	public void testRotatedCharacters() {
		File file = new File(SVG2XMLFixtures.TABLE_DIR, "rotate/pageTableText.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(file);
		SVGElement chunk = textStructurer.getSVGChunk();
		Real2Range bbox = chunk.getBoundingBox();
		double yRange = bbox.getYRange().getRange();
		Real2 centre = bbox.getLLURCorners()[0].plus(new Real2(yRange/2.0, yRange/2.0));
		SVGG rotatedVerticalText = textStructurer.createChunkFromVerticalText(centre, new Angle(-1.0 * Math.PI / 2));
		SVGSVG.wrapAndWriteAsSVG(rotatedVerticalText, new File(SVG2XMLFixtures.TARGET, "text/rotate/pageTable.svg"));
		
	}
	@Test
	public void testRotatedTableText() {
		File file = new File(SVG2XMLFixtures.TABLE_DIR, "rotate/page4Clipped.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(file);
		AbstractCMElement rotatedVerticalText = textStructurer.createChunkFromVerticalText(new Angle(-1.0 * Math.PI / 2));
		// This is text only
		SVGSVG.wrapAndWriteAsSVG(rotatedVerticalText, new File(SVG2XMLFixtures.TARGET, "rotate/page4Clipped.svg"), 900, 800);
		
		
	}
	
	@Test
	public void testRotatedTable() throws FileNotFoundException {
		File file = new File(SVG2XMLFixtures.TABLE_DIR, "rotate/page4Clipped.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(file);
		SVGElement chunk = textStructurer.getSVGChunk();
		Angle angle = new Angle(-1.0 * Math.PI / 2);
		TableStructurer tableStructurer = TableStructurer.createTableStructurer(textStructurer);
		List<SVGShape> pathList = tableStructurer.getOrCreateShapeList();
		SVGElement.rotateAndAlsoUpdateTransforms(pathList, chunk.getCentreForClockwise90Rotation(), angle);
		SVGSVG.wrapAndWriteAsSVG(pathList, new File("target/rotate/pathList.svg"));
	}
	
	@Test
	public void testRotatedTableChunk() throws FileNotFoundException {
		File file = new File(SVG2XMLFixtures.TABLE_DIR, "rotate/page4Clipped.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(file);
		AbstractCMElement chunk = PageLayoutAnalyzerNEW.rotateClockwise(textStructurer);
		SVGSVG.wrapAndWriteAsSVG(chunk, new File("target/rotate/chunk.svg"), 900, 800);
	}

}
