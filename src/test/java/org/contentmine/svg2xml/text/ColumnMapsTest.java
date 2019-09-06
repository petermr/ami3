package org.contentmine.svg2xml.text;

import java.util.List;

import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.RawWords;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ColumnMapsTest {

	private TextLine BERICHT_PAGE6_34_TEXTLINE = null;

	@Before
	public void setup() {
		TextStructurer BERICHT_PAGE6_TXTSTR = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.BERICHT_PAGE6_SVG);
		List<TextLine> BERICHT_PAGE6_TEXT_LINES = BERICHT_PAGE6_TXTSTR.getLinesInIncreasingY();
		BERICHT_PAGE6_34_TEXTLINE = BERICHT_PAGE6_TEXT_LINES.get(34);
	}
	
	
	@Test
	public void testTextLine() {
		TextLine textLine = BERICHT_PAGE6_34_TEXTLINE;
		Assert.assertEquals("textline", "chars: 24 Y: 536.4 fontSize: 10.193 >>Total Topf 1231343453491", textLine.toString());
		RawWords rawWords = textLine.getRawWords();
		Assert.assertEquals("raw", 5, rawWords.size());
		Assert.assertEquals("word0", "Total Topf 1", rawWords.get(0).toString());
		Assert.assertEquals("word1", "231", rawWords.get(1).toString());
		Assert.assertEquals("word2", "343", rawWords.get(2).toString());
		Assert.assertEquals("word3", "453", rawWords.get(3).toString());
		Assert.assertEquals("word4", "491", rawWords.get(4).toString());
	}
	
}
