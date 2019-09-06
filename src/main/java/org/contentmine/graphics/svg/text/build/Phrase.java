package org.contentmine.graphics.svg.text.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlBr;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlI;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.rule.horizontal.LineChunk;

import nu.xom.Element;

/** 
 * A list of Words.
 * 
 * Normally a subcomponent of TextLine. Phrases are separated by large whitespace (Blank)
 * (more than normal inter-word spacing which is normally 1). A Phrase normally contains
 * a list of Words. There are no implied linguistic semantics (a Phrase could be several
 * numbers).
 * 
 * @author pm286
 */
public class Phrase extends LineChunk implements Iterable<Word> {
	
	static final Logger LOG = Logger.getLogger(Phrase.class);
	public final static String TAG = "phrase";

	static final String SUB_START = "_{";
	static final String SUPER_START = "^{";
	static final String SUB_END = "}";
	static final String SUPER_END = "}";
	
	private List<Word> childWordList;
	
	public Phrase() {
		super();
		this.setSVGClassName(TAG);
	}

	public Phrase(LineChunk phrase) {
		super(phrase);
	}

	public Phrase(Word word) {
		this();
		this.appendChild(word.copy());
		getOrCreateWordList();
//		childWordList.add(word);
	}

	public Phrase(SVGG g) {
		super(g);
	}

	public void addWord(Word word) {
		this.appendChild(word);
		this.setStringValueAttribute(null);
		this.childWordList = null;
	}
	
	public Word get(int index) {
		getOrCreateWordList();
		return childWordList.get(index);
	}
	
	public Iterator<Word> iterator() {
		getOrCreateWordList();
		return childWordList.iterator();
	}
	
	public int size() {
		getOrCreateWordList();
		return childWordList.size();
	}

	public RealArray getInterWordWhitePixels() {
		getOrCreateWordList();
		RealArray separationArray = new RealArray();
		for (int i = 1; i < childWordList.size(); i++) {
			Word word0 = childWordList.get(i-1);
			Word word = childWordList.get(i);
			double separation = Util.format(word0.getSeparationBetween(word), 3);
			separationArray.addElement(separation);
		}
		return separationArray;
	}

	public RealArray getInterWordWhiteEnSpaces() {
		getOrCreateWordList();
		RealArray spaceCountArray = new RealArray();
		for (int i = 1; i < childWordList.size(); i++) {
			Word word0 = childWordList.get(i - 1);
			Word word = childWordList.get(i);
			double spaceCount = Util.format(word0.getSpaceCountBetween(word), 3);
			spaceCountArray.addElement(spaceCount);
		}
		return spaceCountArray;
	}

	public RealArray getStartXArray() {
		getOrCreateWordList();
		RealArray startArray = new RealArray();
		for (int i = 0; i < childWordList.size(); i++) {
			Word word = childWordList.get(i);
			double start = Util.format(word.getStartX(), 3);
			startArray.addElement(start);
		}
		return startArray;
	}
	
	public RealArray getMidXArray() {
		getOrCreateWordList();
		RealArray startArray = new RealArray();
		for (int i = 0; i < childWordList.size(); i++) {
			Word word = childWordList.get(i);
			double end = Util.format(word.getMidX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}
	
	public RealArray getEndXArray() {
		getOrCreateWordList();
		RealArray startArray = new RealArray();
		for (int i = 0; i < childWordList.size(); i++) {
			Word word = childWordList.get(i);
			double end = Util.format(word.getEndX(), 3);
			startArray.addElement(end);
		}
		return startArray;
	}

	public Word getLastWord() {
		getOrCreateWordList();
		return childWordList.get(childWordList.size() - 1);
	}
	
	/** start of first word.
	 * 
	 * @return
	 */
	public Double getFirstX() {
		Word word = get(0);
		if (word == null) {
			return 0.0;
		} else {
			return word.getStartX();
		}
	}
	
	/** middle coordinate (average of startX and endX. 
.	 * 
	 * @return
	 */
	public Double getMidX() {
		return (getStartX() + getEndX()) / 2.;
	}
	
	/** end of last word.
	 * 
	 * @return
	 */
	public Double getEndX() {
		return getLastWord().getEndX();
	}

	/** start of first word.
	 * 
	 * @return
	 */
	public Double getStartX() {
		getOrCreateWordList();
		return childWordList.get(0).getStartX();
	}
	
	/** translates words into integers if possible.

	 * @return null if translation impossible
	 */
	public IntArray translateToIntArray() {
		getOrCreateWordList();
		IntArray intArray = new IntArray();
		for (Word word : childWordList) {
			Integer i = word.translateToInteger();
			if (i == null) {
				intArray = null;
				break;
			}
			intArray.addElement(i);
		}
		return intArray;
	}

	/** translates words into numbers if possible.

	 * doesn't yet deal with superscripts.
	 * 
	 * @return null if translation impossible
	 */
	public RealArray translateToRealArray() {
		getOrCreateWordList();
		RealArray realArray = new RealArray();
		for (Word word : childWordList) {
			Double d = word.translateToDouble();
			if (d == null) {
				realArray = null;
				break;
			}
			realArray.addElement(d);
		}
		return realArray;
	}

	public List<Word> getOrCreateWordList() {
		if (childWordList == null) {
			List<Element> wordChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+Word.TAG+"']");
			childWordList = new ArrayList<Word>();
			for (Element child : wordChildren) {
				// FIXME
				childWordList.add(new Word((SVGG)child));
//				childWordList.add((Word)child);
			}
		}
		return childWordList;
	}

	public String getPrintableString() {
		getOrCreateWordList();
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < childWordList.size() - 1; i++) {
			Word word = childWordList.get(i);
			sb.append(word.toString());
			Double spaceCount = word.getSpaceCountBetween(childWordList.get(i + 1));
			for (int j = 0; j < spaceCount; j++) {
				sb.append(" ");
			}
		}
		sb.append(childWordList.get(childWordList.size() - 1).toString());
		return sb.toString();
	}

	/** creates rectangle between two phrases.
	 * 
	 * <b>sets bounding box in Blank</b>
	 * @param nextPhrase
	 * @return
	 */
	public Blank createBlankBetween(Phrase nextPhrase) {
		
		Real2Range thisBBox = this.getBoundingBox();
		Real2Range nextBBox = nextPhrase.getBoundingBox();
		RealRange xrange = new RealRange(thisBBox.getXMax(), nextBBox.getXMin());
		RealRange yrange = new RealRange(
				Math.min(thisBBox.getYMin(), nextBBox.getYMin()),
				Math.max(thisBBox.getYMax(), nextBBox.getYMax()));
		Blank blank = new Blank(new Real2Range(xrange, yrange));
		return blank;
	}

	public Real2Range getBoundingBox() {
		getOrCreateWordList();
		if (childWordList.size() == 0) {
			return null;
		}
		Word word0 = childWordList.get(0);
		if (word0 == null) {
			return null;
		}
		Word wordN = childWordList.get(childWordList.size() - 1);
		RealRange xRange = null;
		if (wordN != null) {
			if (word0.getStartX() != null && wordN.getEndX() != null) {
				xRange = new RealRange(word0.getStartX(), wordN.getEndX());
			}
		}
		if (word0.getYRange() == null) {
			return null;
		}
		RealRange yRange = wordN == null || wordN.getYRange() == null ? 
				word0.getYRange() : word0.getYRange().plus(wordN.getYRange());
		Real2Range bbox = xRange == null || yRange == null ? null :
			new Real2Range(xRange, yRange);
		return bbox;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append(getStringValue());
		sb.append("}");
		return sb.toString();
	}

	public String getStringValue() {
		getOrCreateWordList();
		StringBuilder sb = new StringBuilder();
		if (hasSuperscript()) {
			sb.append(SUPER_START);
		}
		if (hasSubscript()) {
			sb.append(SUB_START);
		}
		for (int i = 0; i < childWordList.size() - 1; i++) {
			Word word = childWordList.get(i);
			sb.append(word.getStringValue());
			Double spaceCount = word.getSpaceCountBetween(childWordList.get(i + 1));
			if (spaceCount != null) {
				for (int j = 0; j < spaceCount; j++) {
					sb.append(Word.SPACE_SYMBOL);
				}
			}
		}
		if (childWordList.size() > 0) {
			sb.append(""+childWordList.get(childWordList.size() - 1).toString()+"");
		}
		if (hasSuperscript()) {
			sb.append(SUPER_END);
		}
		if (hasSubscript()) {
			sb.append(SUB_END);
			LOG.trace("SUB: "+sb.toString());
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public HtmlSpan getSpanValue() {
		getOrCreateWordList();
		HtmlSpan phraseSpan = new HtmlSpan();
		for (int i = 0; i < childWordList.size() - 1; i++) {
			Word word = childWordList.get(i);
			HtmlElement wordSpan = addStyledWord(phraseSpan, word);
			Double spaceCount = word.getSpaceCountBetween(childWordList.get(i + 1));
			if (spaceCount != null) {
				for (int j = 0; j < spaceCount; j++) {
					wordSpan.appendChild(new HtmlBr());
				}
			}
		}
		addStyledWord(phraseSpan, childWordList.get(childWordList.size() - 1));
		return phraseSpan;
	}

	private HtmlElement addStyledWord(HtmlSpan phraseSpan, Word word) {
		HtmlSpan wordSpan = new HtmlSpan();
		phraseSpan.appendChild(wordSpan);
		addStyles(word, wordSpan);
		return wordSpan;
	}

	private void addStyles(Word word, HtmlElement wordSpan) {
		String bold = word.getFontWeight();
		if (FontWeight.BOLD.toString().equalsIgnoreCase(bold)) {
			HtmlB b = new HtmlB();
			wordSpan.appendChild(b);
			wordSpan = b;
		}
		String style = word.getFontStyle();
		if (FontStyle.ITALIC.toString().equalsIgnoreCase(style)) {
			HtmlI it = new HtmlI();
			wordSpan.appendChild(it);
			wordSpan = it;
		}
		String value = word.getStringValue();
		wordSpan.appendChild(value);
	}

	public IntRange getIntRange() {
		return new IntRange((int)(double)getFirstX(), (int)(double)getEndX());
	}
	
	protected List<? extends LineChunk> getChildChunks() {
		getOrCreateWordList();
		return childWordList;
	}

	public Element copyElement() {
		getOrCreateWordList();
		Element element = (Element) this.copy();
		for (Word word : childWordList) {
			element.appendChild(word.copyElement());
		}
		return element;
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateWordList();
		for (Word word : childWordList) {
			word.rotateAll(centreOfRotation, angle);
			LOG.trace("W: "+word.hashCode()+"/"+word.toXML());
		}
		updateChildWordList();
		return;
	}
	
	public void updateChildWordList() {
		for (int i = 0; i < childWordList.size(); i++) {
			this.replaceChild(this.getChildElements().get(i), childWordList.get(i));
		}
	}
	
	public HtmlElement toHtml() {
		HtmlElement span = new HtmlSpan();
		span.setClassAttribute("phrase");
		span = addSuscriptsAndStyle(span);
		Word lastWord = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < childWordList.size(); i++){
			Word word = new Word(childWordList.get(i));
			HtmlElement wordElement = word.toHtml();
			if (i > 0 && lastWord.shouldAddSpaceBefore(word)) {
				span.appendChild(SPACE);
			}
			span.appendChild(wordElement.getValue());
			lastWord = word;
		}
		LOG.trace("SPAN" + span.toXML());
		return span;
	}

	/** merges words in phrase into this.
	 * does not destroy phrase.
	 * 
	 * @param phrase
	 */
	public void mergePhrase(Phrase phrase) {
		List<Word> words = phrase.getOrCreateWordList();
		for (Word word : words) {
			Word newWord = new Word(word);
			if (phrase.hasSubscript()) newWord.setSubscript(true);
			if (phrase.hasSuperscript()) newWord.setSuperscript(true);
			newWord.setStringValueAttribute((String)null);
			this.addWord(newWord);
		}
	}

	public boolean contains(Pattern regex) {
		String value = getStringValue();
		Matcher matcher = regex.matcher(value);
		return matcher.find();
	}

	public String getCSSStyle() {
		String pStyle = null;
		for (Word word : this) {
			String wordStyle = word.getCSSStyle();
			if (pStyle == null) {
				wordStyle = pStyle;
			} else if (pStyle.equals(wordStyle)) {
				// OK
			} else {
				pStyle = MIXED_STYLE;
			}
		}
		return pStyle;
	}



}
