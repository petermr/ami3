package org.contentmine.svg2xml.text;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.rule.horizontal.LineChunk;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LineChunkTest {

	private static Logger LOG = Logger.getLogger(LineChunkTest.class);
	
	private TextLine BERICHT_PAGE6_34_TEXTLINE = null;

	@Before
	public void setup() {
		TextStructurer BERICHT_PAGE6_TXTSTR = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.BERICHT_PAGE6_SVG);
		List<TextLine> BERICHT_PAGE6_TEXT_LINES = BERICHT_PAGE6_TXTSTR.getLinesInIncreasingY();
		BERICHT_PAGE6_34_TEXTLINE = BERICHT_PAGE6_TEXT_LINES.get(34);
	}
	
	@Test
	public void testLineChunks() {
		TextLine textLine = BERICHT_PAGE6_34_TEXTLINE;
		List<LineChunk> lineChunkList = textLine.getLineChunks();
		Assert.assertEquals("chunks", 9, lineChunkList.size());
		for (int i = 0; i < lineChunkList.size(); i++) {
			LOG.trace(lineChunkList.get(i));
		}
	}

}
