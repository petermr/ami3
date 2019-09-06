package org.contentmine.graphics.svg.normalize;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.junit.Test;

import junit.framework.Assert;

public class TextNormalizerTest {
	private static final Logger LOG = Logger.getLogger(TextNormalizerTest.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}


	@Test
	/** compact y coords in a single line
	 * 
	 */
	public void testCompactY1Line() {
		SVGSVG pageSvg = (SVGSVG) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_TEXT_DIR, "oneLine.svg"));
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(pageSvg);
		Assert.assertEquals(69,  texts.size());
		TextDecorator textDecorator = new TextDecorator();
		textDecorator.setAddBoxes(true);
		textDecorator.compactTexts(texts);
		SVGG g = textDecorator.makeCompactedTextsAndAddToG();
		SVGSVG.wrapAndWriteAsSVG(g, new File(new File("target/text/normalize"), "oneLine"+".svg"));
	}
	
	@Test
	/** compact y coords in a paragraph
	 * 
	 */
	public void testCompactPara() {
		String fileRoot = "onePara.svg";
		SVGSVG pageSvg = (SVGSVG) SVGElement.readAndCreateSVG(new File(SVGHTMLFixtures.G_S_TEXT_DIR, fileRoot));
		List<SVGText> singleTexts = SVGText.extractSelfAndDescendantTexts(pageSvg);
		Assert.assertEquals(449,  singleTexts.size());
		TextDecorator textDecorator = new TextDecorator();
		textDecorator.compactTexts(singleTexts);
		SVGG g = textDecorator.makeCompactedTextsAndAddToG();
		SVGSVG.wrapAndWriteAsSVG(g, new File(new File("target/text/normalize"), fileRoot+".svg"));
	}
	
	@Test
	/** compact y coords in a page
	 * 
	 */
	public void testCompactPage() {
		String fileRoot = "CM_pdf2svg_BMCCancer_9_page4.svg";
		File file = new File(SVGHTMLFixtures.G_S_TEXT_DIR, fileRoot);
		Assert.assertEquals("filesize",  1082352, FileUtils.sizeOf(file));
		SVGSVG pageSvg = (SVGSVG) SVGElement.readAndCreateSVG(file);
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(pageSvg);
		Assert.assertEquals(4783,  texts.size());
		TextDecorator textDecorator = new TextDecorator();
		textDecorator.compactTexts(texts);
		SVGG g = textDecorator.makeCompactedTextsAndAddToG();
		File file2 = new File(new File("target/text/normalize"), fileRoot+".svg");
		SVGSVG.wrapAndWriteAsSVG(g, file2);
		long size = FileUtils.sizeOf(file2);
		Assert.assertTrue("filesize "+size,   size > 90000 && size < 140000);
	}
}
