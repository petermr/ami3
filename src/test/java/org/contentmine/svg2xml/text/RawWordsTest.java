package org.contentmine.svg2xml.text;

import java.util.List;

import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.Word;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.RawWords;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RawWordsTest {

	private TextLine BERICHT_PAGE6_34_TEXTLINE = null;

	@Before
	public void setup() {
		TextStructurer BERICHT_PAGE6_TXTSTR = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.BERICHT_PAGE6_SVG);
		List<TextLine> BERICHT_PAGE6_TEXT_LINES = BERICHT_PAGE6_TXTSTR.getLinesInIncreasingY();
		BERICHT_PAGE6_34_TEXTLINE = BERICHT_PAGE6_TEXT_LINES.get(34);
	}

	public static RawWords RAW_WORDS = TextLineTest.PAGE_TEXT_LINE.getRawWords();
	public static RawWords RAW_WORDS1 = TextLineTest.PAGE_TEXT_LINE1.getRawWords();
	public static List<TextLine> DK_LIST = TextLine.createSortedTextLineList(
			XMLUtil.parseQuietlyToDocument(SVG2XMLFixtures.DK_PAGE1_SVG).getRootElement());


	@Test
	public void testGetRawWords() {
		Assert.assertEquals("rawWords", 4, RAW_WORDS.size());
	}
	
	@Test
	public void testGetEndX() {
		Assert.assertEquals("word end", 538.593, (double) RAW_WORDS.getEndX(), 0.001);
	}

	@Test
	public void testGetMidX() {
		Assert.assertEquals("word mid", 517.009, (double) RAW_WORDS.getMidX(), 0.001);
	}

	@Test
	public void testGetStartX() {
		Assert.assertEquals("word start", 495.426, (double) RAW_WORDS.getStartX(), 0.001);
	}


	@Test
	public void testGetInterWordWhitePixels() {
		RealArray separationArray = RAW_WORDS.getInterWordWhitePixels();
		separationArray.format(3);
		Assert.assertEquals("word separation", "(2.614,2.622,2.67)", separationArray.toString());
	}

	@Test
	public void testGetInterWordWhiteEnSpaces() {
		RealArray spaceCountArray = RAW_WORDS.getInterWordWhiteEnSpaces();
		spaceCountArray.format(3);
		Assert.assertEquals("word separation", "(0.596,0.598,0.609)", spaceCountArray.toString());
	}
	
	@Test
	public void testGetStartXArray() {
		RealArray startArray = RAW_WORDS1.getStartXArray();
		startArray.format(3);
		Assert.assertEquals("word start", "(82.484,147.669,212.854,278.039)", startArray.toString());
		RealArray deltaArray = startArray.calculateDifferences().format(3);
		Assert.assertEquals("subtract", "(65.185,65.185,65.185)", deltaArray.toString());

	}
	
	@Test
	public void testGetEndXArray() {
		RealArray endArray = RAW_WORDS1.getEndXArray();
		endArray.format(3);
		Assert.assertEquals("word end", "(90.764,155.949,221.135,286.32)", endArray.toString());
		RealArray deltaArray = endArray.calculateDifferences().format(3);
		Assert.assertEquals("subtract", "(65.185,65.186,65.185)", deltaArray.toString());
	}
	
	@Test
	public void testGetMidXArray() {
		RealArray midArray = RAW_WORDS1.getMidXArray();
		midArray.format(3);
		Assert.assertEquals("word start", "(86.624,151.809,216.994,282.179)", midArray.toString());
		RealArray deltaArray = midArray.calculateDifferences().format(3);
		Assert.assertEquals("subtract", "(65.185,65.185,65.185)", deltaArray.toString());
	}
	
//	@Test
//	public void testTranslateTolRealArray() {
//		RealArray realArray = RAW_WORDS1.translateToRealArray().format(3);
//		Assert.assertEquals("translate", "(16.0,17.0,18.0,19.0)", realArray.toString());
//	}
	
	@Test
	public void testPageWithColumns() {
//		Assert.assertEquals("dk", 57, DK_LIST.size());
//		Multiset<Integer> startSet = WordList. getStartXIntSet(DK_LIST);
//		Assert.assertEquals("sets", 489, startSet.size());
//		Assert.assertEquals("sets", 24, startSet.entrySet().size());
//		List<Entry<Integer>> entryList = new ArrayList<Entry<Integer>>();
//		for (Entry<Integer> entry: startSet.entrySet()) {
//			entryList.add(entry);
//		}
//		for (Entry<Integer> entry: entryList) {
//			if (entry.getCount() <= 3) {
//				startSet.remove(entry.getElement());
//			}
//		}
//		Assert.assertEquals("sets", 11, startSet.entrySet().size());
//		System.out.println(startSet);
	}
	
	@Test
	public void testPhrase() {
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(SVG2XMLFixtures.RAWWORDS_SVG);
		RawWords rawWords = textStructurer.createRawWordsListFromTextLineList().get(0);
		Word word = rawWords.get(0);
		Phrase phrase = word.createPhrase();
		Assert.assertEquals("phrase", "{Phenotypic tarsus (mm)}", phrase.toString());
		Assert.assertEquals("phrase", "Phenotypic tarsus (mm)", phrase.getPrintableString());
		List<Word> wordList = phrase.getOrCreateWordList();
		Assert.assertEquals("phrase", 3, wordList.size());
		Assert.assertEquals("word0", "Phenotypic", wordList.get(0).toString());
		Assert.assertEquals("word1", "tarsus", wordList.get(1).toString());
		Assert.assertEquals("word2", "(mm)", wordList.get(2).toString());
	}
	
	@Test
	public void testPhrase1() {
		TextLine textLine = BERICHT_PAGE6_34_TEXTLINE;
		RawWords rawWords = textLine.getRawWords();
		Assert.assertEquals("rawSpaces", "{Total Topf 1...........................231.....343.....453.....491}",
				rawWords.toString());
		Word word0 = rawWords.get(0);
		Assert.assertEquals("word0", "Total Topf 1", word0.toString());
		Phrase phrase0 = word0.createPhrase();
		Assert.assertEquals("phrase", "{Total Topf 1}", phrase0.toString());
		Assert.assertEquals("phrase", "Total Topf 1", phrase0.getPrintableString());
		Assert.assertEquals("word1", "231", rawWords.get(1).createPhrase().getPrintableString());
		Assert.assertEquals("word1", "343", rawWords.get(2).createPhrase().getPrintableString());
		Assert.assertEquals("word1", "453", rawWords.get(3).createPhrase().getPrintableString());
		Assert.assertEquals("word1", "491", rawWords.get(4).createPhrase().getPrintableString());
	}
	
	
}
