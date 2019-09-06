package org.contentmine.graphics.svg.text.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.Word;

/** 
 * A list of Words.
 * 
 * Normally a subcomponent of TextLine. RawWords are the initial list of words.
 * They may or may not turn into Phrases.
 * 
 * moved from SVG2XMLß
 * 
 * @author pm286
 */
public class RawWords implements Iterable<Word> {

	private final static Logger LOG = Logger.getLogger(RawWords.class);
	
	private List<Word> wordList;

	public RawWords() {
		this.wordList = new ArrayList<Word>();
	}

	public List<Word> getWordList() {
		return wordList;
	}
	
	public void add(Word word) {
		wordList.add(word);
	}
	
	public Word get(int index) {
		return wordList.get(index);
	}
	
	public Iterator<Word> iterator() {
		return wordList.iterator();
	}
	
	public int size() {
		return wordList.size();
	}

	public RealArray getInterWordWhitePixels() {
		RealArray separationArray = new RealArray();
		for (int i = 1; i < wordList.size(); i++) {
			Word word0 = wordList.get(i - 1);
			Word word = wordList.get(i);
			double separation = Util.format(word0.getSeparationBetween(word), 3);
			separationArray.addElement(separation);
		}
		return separationArray;
	}

	public RealArray getInterWordWhiteEnSpaces() {
		RealArray spaceCountArray = new RealArray();
		for (int i = 1; i < wordList.size(); i++) {
			Word word0 = wordList.get(i - 1);
			Word word = wordList.get(i);
			double spaceCount = Util.format(word0.getSpaceCountBetween(word), 3);
			spaceCountArray.addElement(spaceCount);
		}
		return spaceCountArray;
	}

	public RealArray getStartXArray() {
		RealArray startArray = new RealArray();
		for (int i = 0; i < wordList.size(); i++) {
			Word word = wordList.get(i);
			double start = Util.format(word.getStartX(), 3);
			startArray.addElement(start);
		}
		return startArray;
	}
	
	public RealArray getMidXArray() {
		RealArray startArray = new RealArray();
		for (int i = 0; i < wordList.size(); i++) {
			Word word = wordList.get(i);
			double end = Util.format(word.getMidX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}
	
	public RealArray getEndXArray() {
		RealArray startArray = new RealArray();
		for (int i = 0; i < wordList.size(); i++) {
			Word word = wordList.get(i);
			double end = Util.format(word.getEndX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}

	public Word getLastWord() {
		return wordList.get(wordList.size() - 1);
	}
	
	/** 
	 * Start of first word.
	 * 
	 * @return
	 */
	public double getFirstX() {
		return get(0).getStartX();
	}
	
	/** 
	 * Middle coordinate (average of startX and endX). 
.	 * 
	 * @return
	 */
	public double getMidX() {
		return (getStartX() + getEndX()) / 2.;
	}
	
	/** 
	 * End of last word.
	 * 
	 * @return
	 */
	public double getEndX() {
		return getLastWord().getEndX();
	}

	/** 
	 * Start of first word.
	 * 
	 * @return
	 */
	public double getStartX() {
		return wordList.get(0).getStartX();
	}
	
	/** 
	 * Translates words into integers if possible.
	 * 
	 * @return null if translation impossible
	 */
	public IntArray translateToIntArray() {
		IntArray intArray = new IntArray();
		for (Word word : wordList) {
			Integer i = word.translateToInteger();
			if (i == null) {
				intArray = null;
				break;
			}
			intArray.addElement(i);
		}
		return intArray;
	}

//	/** 
//	 * Translates words into numbers if possible.
//	 * <p>
//	 * Doesn't yet deal with superscripts.
//	 * 
//	 * @return null if translation impossible
//	 */
//	public RealArray translateToRealArray() {
//		RealArray realArray = new RealArray();
//		for (WordNew word : wordList) {
//			Double d = word.translateToDouble();
//			if (d == null) {
//				realArray = null;
//				break;
//			}
//			realArray.addElement(d);
//		}
//		return realArray;
//	}

	/** 
	 * Some PDFs have explicit space characters, which are eliminated.
	 * <p>
	 * Multiple spaces are treated as single. Hopefully we'll deal with 
	 * multiple spaces later if they matter.
	 * 
	 * PROBABLY BUGGY
	 * 
	 * @return new RawWords 
	 */
	public List<Phrase> createPhrases() {
		List<Phrase> phraseList = new ArrayList<Phrase>();
		for (Word word : wordList) {
			Phrase phrase = word.createPhrase();
			phraseList.add(phrase);
		}
		return phraseList;
	}
	
	/** 
	 * 
	 */
	public PhraseChunk createPhraseList() {
		PhraseChunk phraseChunk = new PhraseChunk();
		Phrase phrase = null;
		for (int i = 0; i < wordList.size(); i++) {
			if (phrase == null) {
				phrase = new Phrase();
				phraseChunk.add(phrase);
			}
			Word word = wordList.get(i);
			if (word == null) {
				LOG.trace("null");
			} else {
				phrase.addWord(new Word(word));
				Word wordii = (i < wordList.size() - 1) ? wordList.get(i + 1) : null;
				Double spaceCount = word.getSpaceCountBetween(wordii);
				spaceCount = (i == wordList.size() - 1) ? new Double(0) : spaceCount;
				if (spaceCount == null || spaceCount > 1) {
					phrase = null;
				}
			}
		}
//		if (phrase != null) {
//			phrase.add(wordList.get(wordList.size() - 1));
//		}
		return phraseChunk;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for (int i = 0; i < wordList.size() - 1; i++) {
			Word word = wordList.get(i);
			sb.append(""+word.toString()+"");
			Double spaceCount = word.getSpaceCountBetween(wordList.get(i + 1));
			if (spaceCount != null) {
				for (int j = 0; j < spaceCount; j++) {
					sb.append(".");
				}
			}
		}
		sb.append(wordList.get(wordList.size() - 1).toString());
		sb.append("}");
		return sb.toString();
	}
}
