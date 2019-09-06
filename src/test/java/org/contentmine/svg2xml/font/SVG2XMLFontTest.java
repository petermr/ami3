package org.contentmine.svg2xml.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SVG2XMLFontTest {

	private final static Logger LOG = Logger.getLogger(SVG2XMLFontTest.class);

	private static final PrintStream SYSOUT = System.out;

	@Test
	public void readCorpus() {
		AbstractCMElement bmc = SVGElement.readAndCreateSVG(SVG2XMLFixtures.BMC_RUNNING_NORMAL_SVG);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(bmc);
		SVG2XMLFont font = new SVG2XMLFont("bmc.running");
		for (int i = 0; i < textList.size() - 1; i++) {
			SVGText svgText = textList.get(i);
			font.getOrCreateSVG2XMLCharacter(svgText);
		}
		LOG.trace(font.ensureSortedUnicodeList().size());
		font.debug("font");
	}

	@Test
	public void readCorpusAndWidths() {
		AbstractCMElement bmc = SVGElement.readAndCreateSVG(SVG2XMLFixtures.BMC_RUNNING_NORMAL_SVG);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(bmc);
		SVG2XMLFont font = new SVG2XMLFont("bmc.running");
		font.addTextListAndGenerateSizes(textList);
		font.debug("font");
	}

	@Test
	public void readCorpusAndWidths32() {
		AbstractCMElement bmc = SVGElement.readAndCreateSVG(SVG2XMLFixtures.IMAGE_3_2_SVG);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(bmc);
		SVG2XMLFont font = new SVG2XMLFont("image.3.2");
		font.addTextListAndGenerateSizes(textList);
		font.debug("font");
	}
	
	@Test
	@Ignore // not portable
	public void testGetFontMetrics() {
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
		Font font = new Font("Helvetica", Font.PLAIN, 1000);
		FontMetrics fontMetrics = g2d.getFontMetrics(font);
		int[] ww = fontMetrics.getWidths();
		Assert.assertEquals("nchars", 256, ww.length);
		for (int i = 0; i < ww.length; i++) {
			LOG.trace(i+": "+(char)i+" "+ww[i]);
		}
		Assert.assertEquals("space", 278, ww[32]);
		Assert.assertEquals("shriek", 278, ww[33]);
		Assert.assertEquals("A", 667, ww[65]);
		Assert.assertEquals("M", 833, ww[77]);
		Assert.assertEquals("a", 556, ww[97]);
		Assert.assertEquals("Aring", 667, ww[197]);
	}


}
