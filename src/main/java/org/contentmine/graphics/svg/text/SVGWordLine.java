package org.contentmine.graphics.svg.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;

/** holds a "paragraph".
 * 
 * Currently driven by <p> elements emitted by Tesseract. These in turn hold lines and words.
 * Still exploratory
 * 
 * @author pm286
 *
 */
public class SVGWordLine extends SVGG {

	
	private static final Logger LOG = Logger.getLogger(SVGWordLine.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String CLASS = "line";
	private List<SVGPhrase> phraseList;
	private List<SVGWord> wordList;
	
	public SVGWordLine() {
		super();
		this.setSVGClassName(CLASS);
	}

	public List<SVGPhrase> makePhrasesFromWords() {
		phraseList = new ArrayList<SVGPhrase>();
		getOrCreateSVGWordList();
		if (checkWordsAreSorted()) {
			for (int i = 0; i < wordList.size(); i++) {
				addWordToPhraseList(wordList.get(i));
			}
		}
		return phraseList;
	}
	
	private boolean checkWordsAreSorted() {
		for (int i = 1; i < wordList.size(); i++) {
			if (!isOrdered(wordList.get(i - 1), wordList.get(i))) {
				LOG.warn("Unordered wordlist from Tesseract: "+wordList.get(i - 1).toString()+" => "+wordList.get(i).toString());
				LOG.warn("Unordered wordlist from Tesseract: "+wordList.get(i - 1).toString()+" => "+wordList.get(i).toString());
				return false;
			}
		}
		return true;
	}

	private void addWordToPhraseList(SVGWord svgWord) {
		ensurePhraseList();
		if (phraseList.size() == 0) {
			createNewPhraseAndAddWord(svgWord);
		} else {
			SVGPhrase lastPhrase = phraseList.get(phraseList.size() - 1);
			if (lastPhrase.canGeometricallyAdd(svgWord)) {
				lastPhrase.addTrailingWord(svgWord);
			} else {
				createNewPhraseAndAddWord(svgWord);
			}
		}
	}

	private void createNewPhraseAndAddWord(SVGWord svgWord) {
		SVGPhrase phrase = new SVGPhrase();
		phraseList.add(phrase);
		phrase.addTrailingWord(svgWord);
	}

	private void ensurePhraseList() {
		if (phraseList == null) {
			phraseList = new ArrayList<SVGPhrase>();
		}
	}

	/**
	 * returns false only if both words are not null and not null coordinates.
	 * 
	 * @param svgWord0
	 * @param svgWord1
	 * @return
	 */
	private boolean isOrdered(SVGWord svgWord0, SVGWord svgWord1) {
		if (svgWord0 == null || svgWord1 == null) {
			LOG.trace("null word/s "+svgWord0+"; "+svgWord1);
			return true;
		}
		Real2 xy0 = getFirstCorner(svgWord0);
		Real2 xy1 = getFirstCorner(svgWord1);
		if (xy0 == null || xy1 == null) {
			LOG.trace("null coords "+xy0+"; "+xy1);
			return true;
		}
		return (xy0.getX() < xy1.getX());
	}

	private Real2 getFirstCorner(SVGWord svgWord) {
		Real2Range bbox = svgWord.getChildRectBoundingBox();
		return (bbox == null || bbox.getLLURCorners() == null) ? null : bbox.getLLURCorners()[0];
	}

	public List<SVGPhrase> getOrCreateSVGPhraseList() {
		if (phraseList == null) {
			List<Element> elements = XMLUtil.getQueryElements(this, "*[@class='"+SVGPhrase.CLASS+"']");
			phraseList = new ArrayList<SVGPhrase>();
			for (Element element : elements) {
				phraseList.add((SVGPhrase) element);
			}
		}
		return phraseList;
	}
	
	

	public List<SVGWord> getOrCreateSVGWordList() {
		if (wordList == null) {
			List<Element> elements = XMLUtil.getQueryElements(this, ".//*[@class='"+SVGWord.CLASS+"']");
			wordList = new ArrayList<SVGWord>();
			for (Element element : elements) {
				wordList.add((SVGWord) element);
			}
		}
		return wordList;
	}

	/** returns value if the is exactly one Phrase.
	 * 
	 * @return
	 */
	public String getSinglePhraseValue() {
		return (phraseList == null || phraseList.size() != 1) ? null : phraseList.get(0).toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (phraseList.size() == 1 && phraseList.get(0).toString().length() == 0) {
			// skip empty phrases
		} else {
			for (SVGPhrase phrase : phraseList) {
				sb.append("<"+phrase.toString()+">");
			}
		}
		return sb.toString();
	}

	public int getPhraseCount() {
		return getOrCreateSVGPhraseList().size();
	}

}
