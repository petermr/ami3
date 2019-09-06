package org.contentmine.svg2xml.text;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.Word;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PhraseTest {
	private static final Logger LOG = Logger.getLogger(PhraseTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private TextLine BERICHT_PAGE6_34_TEXTLINE = null;

	@Before
	public void setup() {
		TextStructurer BERICHT_PAGE6_TXTSTR = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.BERICHT_PAGE6_SVG);
		List<TextLine> BERICHT_PAGE6_TEXT_LINES = BERICHT_PAGE6_TXTSTR.getLinesInIncreasingY();
		BERICHT_PAGE6_34_TEXTLINE = BERICHT_PAGE6_TEXT_LINES.get(34);
	}

	@Test
	public void testPhraseList() {
		TextLine textLine = TextStructurer.createTextLine(SVG2XMLFixtures.RAWWORDS_SVG, 0);
		List<Phrase> phraseList = textLine.createPhraseList();
		Assert.assertEquals("phraseList", 1, phraseList.size());
		Assert.assertEquals("phrase", "Phenotypic tarsus (mm)", phraseList.get(0).getPrintableString());
		Assert.assertEquals("phrase", "{Phenotypic tarsus (mm)}", phraseList.get(0).toString());
	}
	
	@Test
	public void testPhraseList1() {
		TextLine textLine = BERICHT_PAGE6_34_TEXTLINE;
		List<Phrase> phraseList = textLine.createPhraseList();
		Assert.assertEquals("phraseList", 5, phraseList.size());
		Assert.assertEquals("phrase", "Total Topf 1", phraseList.get(0).getPrintableString());
		Assert.assertEquals("phrase", "{Total Topf 1}", phraseList.get(0).toString());
		Assert.assertEquals("phrase1", "231", phraseList.get(1).getPrintableString());
		Assert.assertEquals("phrase2", "343", phraseList.get(2).getPrintableString());
		Assert.assertEquals("phrase3", "453", phraseList.get(3).getPrintableString());
		Assert.assertEquals("phrase4", "491", phraseList.get(4).getPrintableString());
	}
	
	@Test
	public void testGetWordList() {
		SVGText t11 = new SVGText(new Real2(25., 16.), "+");
		t11.setFontSize(8.0);
		Word w11 = new Word(t11);
		Phrase p11 = new Phrase(w11);
		LOG.trace(""+p11.getOrCreateBoundingBox());
		List<Word> wordList = p11.getOrCreateWordList();
		Assert.assertEquals(1, wordList.size());
	}

}
