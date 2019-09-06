package org.contentmine.svg2xml.text;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.junit.Assert;
import org.junit.Test;

public class TextContainerTest {

	private final static Logger LOG = Logger.getLogger(TextContainerTest.class);

	@Test
	public void testTextContainerRead() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textContainer.getTextLineList();
		Assert.assertEquals("textLineCount", 16, textLineList.size());
	}
	

	@Test
	public void testTextContainerFonts() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textContainer.getTextLineList();
		Assert.assertEquals("header", "Helvetica", textLineList.get(0).getFontFamily());
		Assert.assertEquals("header", "TimesNewRoman", textLineList.get(1).getFontFamily());
		Assert.assertEquals("header", "TimesNewRoman", textLineList.get(15).getFontFamily());
	}
	
	@Test
	public void testTextContainerSizes() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textContainer.getTextLineList();
		Assert.assertEquals("header", 10.261, textLineList.get(0).getFontSize(), 0.001);
		Assert.assertEquals("header", 9.76, textLineList.get(1).getFontSize(), 0.01);
		Assert.assertEquals("header", 9.76, textLineList.get(15).getFontSize(), 0.01);
		for (TextLine textLine: textLineList) {
			LOG.trace(textLine);
		}
	}
	
	@Test
	public void testTextContainerWeight() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		List<TextLine> textLineList = textContainer.getTextLineList();
		Assert.assertTrue("header", textLineList.get(0).isBold());
		Assert.assertFalse("header", textLineList.get(1).isBold());
		Assert.assertFalse("header", textLineList.get(15).isBold());
		for (TextLine textLine: textLineList) {
			LOG.trace(textLine);
		}
	}
	
	@Test
	public void testTextContainerLineIsCommonestFontSize() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textContainer.getCommonestFontSize().getDouble(), 0.001);
		TextLine textLine0 = textContainer.getTextLineList().get(0);
		Assert.assertFalse("not commonest size", textContainer.isCommonestFontSize(textLine0));
		TextLine textLine1 = textContainer.getTextLineList().get(1);
		LOG.trace("textLine1 "+textLine1);
		Assert.assertTrue("commonest Font Size", textContainer.isCommonestFontSize(textLine1));;
	}
	
	@Test
	public void testTextContainerLineIsDifferentSize() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		Assert.assertEquals("commonest size", 9.76, textContainer.getCommonestFontSize().getDouble(), 0.001);
		TextLine textLine0 = textContainer.getTextLineList().get(0);
		Assert.assertFalse("not commonestFontSize", textContainer.isCommonestFontSize(textLine0));
		Assert.assertTrue("line 0 different size", textContainer.lineIsLargerThanCommonestFontSize(textLine0));
		TextLine textLine1 = textContainer.getTextLineList().get(1);
		Assert.assertTrue("isCommonestFontSize", textContainer.isCommonestFontSize(textLine1));
		Assert.assertFalse("line 1 not different size", textContainer.lineIsLargerThanCommonestFontSize(textLine1));
	}
	
	@Test
	public void testTextContainerLineIsBold() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		TextLine textLine0 = textContainer.getTextLineList().get(0);
		Assert.assertTrue("line 0 bold", textLine0.isBold());
		TextLine textLine1 = textContainer.getTextLineList().get(1);
		Assert.assertFalse("line 1 bold", textLine1.isBold());
		
		// this should be bold but didn't trigger threshold in PDF2SVG
		textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		textLine0 = textContainer.getTextLineList().get(0);
		Assert.assertFalse("line 0 bold", textLine0.isBold());
		textLine1 = textContainer.getTextLineList().get(1);
		Assert.assertFalse("line 1 bold", textLine1.isBold());
	}

	@Test
	public void testTextContainerLineGetFontFamily() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		TextLine textLine0 = textContainer.getTextLineList().get(0);
		Assert.assertEquals("line 0", "Helvetica", textLine0.getFontFamily());
		TextLine textLine1 = textContainer.getTextLineList().get(1);
		Assert.assertEquals("line 1", "TimesNewRoman", textLine1.getFontFamily());
		
	}

	@Test //this is 
	public void testTextContainerSplitLineSizesNone() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_8_SVG);
		IntArray intArray = textContainer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}
	
	@Test
		public void testTextContainerSplitLineSizesOne() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_1_6_SVG);
		IntArray intArray = textContainer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {0};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}
	
	@Test
	public void testTextContainerSplitLineSizesNoneButFont() {
		TextStructurer textContainer = TextStructurer.createTextStructurerWithSortedLines(TextFixtures.BMC_174_4_3_SVG);
		IntArray intArray = textContainer.splitGroupBiggerThanCommonest();
		LOG.trace(intArray);
		int[] ref = {};
		Assert.assertTrue("split", new IntArray(ref).equals(intArray));
	}
	
	
}
