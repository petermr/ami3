package org.contentmine.svg2xml.text;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Test;

public class SubSuperscriptTest {

	private static final Logger LOG = Logger.getLogger(SubSuperscriptTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testSuperscripts() {
		File inputFile = new File(SVG2XMLFixtures.TEXT_DIR, "superscript.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		TextChunk phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		phraseListList.applySubAndSuperscripts();
		LOG.trace(phraseListList.getStringValue());
	}

	@Test
	public void testSubscripts() {
		File inputFile = new File(SVG2XMLFixtures.TEXT_DIR, "subscript.svg");
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		TextChunk phraseListList = textStructurer.getTextChunkList().getLastTextChunk();
		phraseListList.applySubAndSuperscripts();
		LOG.trace(phraseListList.getStringValue());
	}
}
