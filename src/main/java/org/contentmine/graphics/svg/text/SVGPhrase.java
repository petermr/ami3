package org.contentmine.graphics.svg.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import nu.xom.Element;

/** holds a set of words which are geometrically joined into a single unit.
 * 
 * Still exploratory
 * 
 * @author pm286
 *
 */
public class SVGPhrase extends SVGG {

	
	private static final Logger LOG = Logger.getLogger(SVGPhrase.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String CLASS = "phrase";

	private static final boolean ROT0 = false;
	private static final boolean ROT90 = true;
	
	private List<SVGWord> wordList;
	private double interWordGap = 17.0; // empirical gap between Tesseract words in a phrase
	
	public SVGPhrase() {
		super();
		this.setSVGClassName(CLASS);
	}
	
	/** uses child Word elements to populate list.
	 * 
	 * @return
	 */
	public List<SVGWord> getOrCreateWordList() {
		if (this.wordList == null) {
			wordList = new ArrayList<SVGWord>();
			List<Element> elements = XMLUtil.getQueryElements(this, "*[@class='"+SVGWord.CLASS+"']");
			for (Element element : elements) {
				wordList.add((SVGWord)element);
			}
		}
		return wordList;
	}

	@Override
	public String toString() {
		ensureWordList();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (SVGWord word : wordList) {
			if (i++ > 0) {
				sb.append(" ");
			}
			sb.append(word.getValue());
		}
		return sb.toString();
	}
	
	private void ensureWordList() {
		if (this.wordList == null) {
			wordList = new ArrayList<SVGWord>();
		}
	}

	public SVGWord getLastWord() {
		getOrCreateWordList();
		return wordList.size() == 0 ? null : wordList.get(wordList.size() - 1);
	}

	public void addTrailingWord(SVGWord svgWord) {
		ensureWordList();
		wordList.add(svgWord);
	}

	public boolean canGeometricallyAdd(SVGWord svgWord) {
		SVGWord lastWord = wordList.get(wordList.size() - 1);
		double gap = svgWord.gapFollowing(lastWord);
		LOG.trace("GAP "+gap+"; "+lastWord.getChildRectBoundingBox()+"; "+svgWord.getChildRectBoundingBox());
		return gap < interWordGap;
	}

	public Real2Range getBoundingBox() {
		Real2Range bbox = null;
		for (SVGWord word : this.getOrCreateWordList()) {
			if (bbox == null) {
				bbox = word.getBoundingBox();
			} else {
				bbox = bbox.plus(word.getBoundingBox());
			}
		}
		return bbox;
	}

	public static SVGPhrase createPhraseFromCharacters(List<SVGText> textList) {
		return SVGPhrase.createPhraseFromCharacters(textList, false);
	}

	public static SVGPhrase createPhraseFromCharacters(List<SVGText> textList, boolean isRot90) {
		SVGPhrase phrase = null;
		if (textList != null && textList.size() > 0) {
			if (!isRot90) {
				phrase = extractHorizontalPhrase(textList);
			} else {
				phrase = extractRot90Phrase(textList);
			}
		}
		return phrase;
	}

	private static SVGPhrase extractHorizontalPhrase(List<SVGText> textList) {
		SVGPhrase phrase;
		phrase = new SVGPhrase();
		SVGWord word = new SVGWord(textList.get(0));
		phrase.addTrailingWord(word);
		for (int i = 1; i < textList.size(); i++) {
			SVGText text = textList.get(i); 
			if (word.canAppend(text)) {
				word.append(text);
			} else {
				word = new SVGWord(text);
				phrase.addTrailingWord(word);
			}
		}
		return phrase;
	}

	/** complex because:
	 *   moving variable is y
	 *   it runs downwards
	 *   but phrases run upwards
	 *   
	 * @param texts
	 * @return
	 */
	private static SVGPhrase extractRot90Phrase(List<SVGText> texts) {
		SVGPhrase phrase;
		phrase = new SVGPhrase();
		List<SVGText> reverseTexts = new ArrayList<SVGText>(texts);
		Collections.reverse(reverseTexts);
		SVGWord word = new SVGWord(reverseTexts.get(0), ROT90);
		phrase.addTrailingWord(word);
		for (int i = 1; i < reverseTexts.size(); i++) {
			SVGText text = reverseTexts.get(i); 
			if (word.canAppend(text)) {
				word.append(text);
			} else {
				word = new SVGWord(text, ROT90);
				phrase.addTrailingWord(word);
			}
		}
		return phrase;
	}

	public double getInterWordGap() {
		return interWordGap;
	}

	public void setInterWordGap(double interWordGap) {
		this.interWordGap = interWordGap;
	}

	public List<String> getOrCreateStringList() {
		getOrCreateWordList();
		List<String> stringList = new ArrayList<String>();
		for (SVGWord word : wordList) {
			stringList.add(word.getStringValue());
		}
		return stringList;
	}
	
	/** returns an array of all the words as numbers.
	 * useful for scales, graphs, lists, tables, etc.
	 * fails if any word is not numeric
	 * 
	 * @return null if cannot parse values
	 */
	public RealArray getNumericValues() {
		List<String> stringList = getOrCreateStringList();
    	RealArray values = RealArray.createRealArray(stringList);
    	return values;
	}

	public SVGPhrase removeWordsCompletelyOutsideRange(RealRange range) {
		getOrCreateWordList();
		SVGPhrase filteredPhrase = new SVGPhrase();
		for (SVGWord word : wordList) {
			String value = String.valueOf(word.getStringValue());
			// FIXME - why is this null?
			if ("null".equals(value)) continue;
			LOG.trace("SV "+value);
			Real2Range wordBox = word.getBoundingBox();
			RealRange wordXRange = wordBox.getXRange();
			if (wordXRange.intersects(range)) {
				filteredPhrase.addTrailingWord(word);
			}
		}
		return filteredPhrase;
	}

	/** get the yCoordinates of the words formatted to nplaces.
	 * 
	 * @param nplaces
	 * @return set of coordinates
	 */
	public Multiset<Double> createYValueSet(int nplaces) {
		RealArray yCoords = this.getYValuesOfWords();
		yCoords.format(nplaces);
		Multiset<Double> ySet = HashMultiset.create();
		for (double y : yCoords) {
			ySet.add(new Double(y));
		}
		return ySet;
	}

	public RealArray getYValuesOfWords() {
		getOrCreateWordList();
		RealArray yValues = new RealArray();
		for (SVGWord word : wordList) {
			double y = new Double(word.getY());
			yValues.addElement(y);
		}
		return yValues;
	}

	/** create a new Phrase from words with the commonest Y-value.
	 * 
	 * @param nplaces
	 * @return
	 */
	public SVGPhrase getWordsWithCommonestYValue(int nplaces) {
		Multiset<Double> ySet = this.createYValueSet(nplaces);
		Double y = (Double) MultisetUtil.getCommonestValue(ySet);
		double eps = 1.0 / (Math.pow(10.0, (double)nplaces));
		SVGPhrase phrase = new SVGPhrase();
		for (SVGWord word : wordList) {
			if (Real.isEqual(word.getY(), y, eps)) {
				phrase.addTrailingWord(word);
			}
		}
		return phrase;
	}

	/** create a new Phrase from words with the lowest Y-value.
	 * 
	 * @param nplaces
	 * @return
	 */
	public SVGPhrase getWordsWithLowestYValue(int nplaces) {
		Multiset<Double> ySet = this.createYValueSet(nplaces);
		Double y = (Double) MultisetUtil.getLowestValue(ySet);
		double eps = 1.0 / (Math.pow(10.0, (double)nplaces));
		SVGPhrase phrase = new SVGPhrase();
		for (SVGWord word : wordList) {
			if (Real.isEqual(word.getY(), y, eps)) {
				phrase.addTrailingWord(word);
			}
		}
		return phrase;
	}

	/** create a new Phrase from words with the highest X-value.
	 * 
	 * @param nplaces
	 * @return
	 */
	public SVGPhrase getWordsWithHighestXValue(int nplaces) {
		Multiset<Double> xSet = this.createXValueSet(nplaces);
		Double x = (Double) MultisetUtil.getHighestValue(xSet);
		double eps = 1.0 / (Math.pow(10.0, (double)nplaces));
		SVGPhrase phrase = new SVGPhrase();
		for (SVGWord word : wordList) {
			if (Real.isEqual(word.getX(), x, eps)) {
				phrase.addTrailingWord(word);
			}
		}
		return phrase;
	}
	
	/** get the xCoordinates of the words formatted to nplaces.
	 * 
	 * @param nplaces
	 * @return set of coordinates
	 */
	public Multiset<Double> createXValueSet(int nplaces) {
		RealArray xCoords = this.getXValuesOfWords();
		xCoords.format(nplaces);
		Multiset<Double> xSet = HashMultiset.create();
		for (double x : xCoords) {
			xSet.add(new Double(x));
		}
		return xSet;
	}

	public RealArray getXValuesOfWords() {
		getOrCreateWordList();
		RealArray xValues = new RealArray();
		for (SVGWord word : wordList) {
			double x = new Double(word.getX());
			xValues.addElement(x);
		}
		return xValues;
	}


	public SVGPhrase normalizeMinus() {
		ensureWordList();
		for (SVGWord word : wordList) {
			word.normalizeMinus();
		}
		return this;
	}

	public List<SVGText> getTextList() {
		List<SVGText> textList = new ArrayList<SVGText>();
		List<SVGWord> wordList = this.getOrCreateWordList();
		for (SVGWord word : wordList) {
			textList.add(word.getSVGText());
		}
		return textList;
		
	}

	public void reverseTextsInWords() {
		List<SVGWord> newWordList = new ArrayList<SVGWord>();
		for (SVGWord word : wordList) {
			word.reverseTexts();
			newWordList.add(word);
		}
		wordList = newWordList;
	}

	public SVGPhrase getNumericWords() {
		SVGPhrase phrase = new SVGPhrase();
		for (SVGWord word : wordList) {
			if (word.isNumeric()) {
				phrase.addTrailingWord(word);
			}
		}
		return phrase;
	}


}
