package org.contentmine.graphics.svg.text.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.StyleBundle;
import org.contentmine.graphics.svg.linestuff.Path2ShapeConverter;
import org.contentmine.graphics.svg.rule.horizontal.LineChunk;

import nu.xom.Attribute;
import nu.xom.Element;

public class Word extends LineChunk implements Iterable<SVGText> {

	private final static Logger LOG = Logger.getLogger(Word.class);

	public final static String TAG = "word";
	public static String SPACE_SYMBOL = " ";

	private List<SVGText> childTextList;
	private boolean guessWidth = true;
	private List<? extends LineChunk> lineChunkListForFonts;
	
	public static Word createEmptyWord(Real2 xy, double fontSize) {
		Word word = new Word();
		word.childTextList = new ArrayList<SVGText>();
		SVGText text = new SVGText(xy, Word.SPACE_SYMBOL);
		text.setFontSize(fontSize);
		word.appendChild(text);
		word.childTextList.add(text);
		return word;
	};


	public Word() {
		super();
		this.setSVGClassName(TAG);
		
	}

	public Word(SVGG g) {
		super(g);
		if (Word.TAG.equals(g.getSVGClassNameString())) {
			LOG.trace("need to create subclassed Word constructor");
		}
	}

	public Word(Word word) {
		super(word);
		XMLUtil.copyAttributesFromTo(word, this);
		if (word.childTextList != null) {
			this.childTextList = new ArrayList<SVGText>();
			childTextList.addAll(word.childTextList);
		}
		guessWidth = word.guessWidth;
	}

	public Word(SVGText text) {
		this();
		this.add(text);
	}


	/** 
	 * Creates Word from characters in svgText.
	 * <p>
	 * Mainly for testing purposes. Is unlikely to provide valid coordinates
	 * for characters.
	 * <p>
	 * Splits svgText into characters.
	 * 
	 * @param svgText
	 */
	public static Word createTestWord(AbstractCMElement svgText) {
		Word word = new Word();
		List<SVGText> textList = new ArrayList<SVGText>();
		String value = svgText.getValue();
		for (int i = 0; i < value.length(); i++) {
			Real2 r2 = new Real2(0.001, 0.001);
			textList.add(new SVGText(r2, String.valueOf(value.charAt(i))));
		}
		word.setTextList(textList);
		return word;
	}
	
	public Iterator<SVGText> iterator() {
		getOrCreateChildTextList();
		return childTextList.iterator();
	}
	
	public void setTextList(List<SVGText> textList) {
		for (SVGText text : textList) {
			add(text);
		}
	}
	
	protected List<? extends LineChunk> getChildChunks() {
		return new ArrayList<LineChunk>();
	}

	public Real2 getCentrePointOfFirstCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(0).getCentrePointOfFirstCharacter();
	}
	
	private List<SVGText> getOrCreateChildTextList() {
		if (childTextList == null) {
			List<Element> textChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGText.TAG+"']");
			childTextList = new ArrayList<SVGText>();
			for (Element child : textChildren) {
				AbstractCMElement childText = new SVGText(child);
				String s = child.getValue();
				childTextList.add((SVGText)child);
			}
		}
		return childTextList;
	}

	public Real2 getCentrePointOfLastCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(childTextList.size() - 1).getCentrePointOfFirstCharacter();
	}

	public Double getRadiusOfFirstCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(0).getRadiusOfFirstCharacter();
	}

	public Double getRadiusOfLastCharacter() {
		getOrCreateChildTextList();
		return childTextList.get(childTextList.size() - 1).getRadiusOfFirstCharacter();
	}

	public void add(SVGText text) {
		this.appendChild(new SVGText(text));
	}

	private void ensureTextList() {
		if (childTextList == null) {
			childTextList = new ArrayList<SVGText>();
		}
	}
	
	public String toString() {
		return getStringValue();
	}

//	public String getStringValue() {
//		getOrCreateChildTextList();
//		StringBuilder sb = new StringBuilder();
//		for (SVGElement text : childTextList) {
//			sb.append(text.getValue());
//		}
//		this.setStringValueAttribute(sb.toString());
//		LOG.trace("W "+this.toXML());
//		return sb.toString();
//	}
	
	public String getStringValue() {
		getOrCreateChildTextList();
		StringBuilder sb = new StringBuilder();
		if (hasSuperscript()) {
			sb.append(Phrase.SUPER_START);
		}
		if (hasSubscript()) {
			sb.append(Phrase.SUB_START);
		}
		for (int i = 0; i < childTextList.size(); i++) {
			SVGText text = childTextList.get(i);
			sb.append(text.getValue());
		}
		if (hasSuperscript()) {
			sb.append(Phrase.SUPER_END);
		}
		if (hasSubscript()) {
			sb.append(Phrase.SUB_END);
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}



	public Double getSpaceCountBetween(Word followingWord) {
		SVGText char0 = get(getCharacterCount() - 1);
		SVGText char1 = followingWord == null ? null : followingWord.get(0);
		return char1 == null || char0 == null ? null : char0.getEnSpaceCount(char1);
	}

	public Double getSeparationBetween(Word followingWord) {
		SVGText char0 = get(getCharacterCount() - 1);
		SVGText char1 = followingWord.get(0);
		return char0.getSeparation(char1);
	}

	public Integer getCharacterCount() {
		getOrCreateChildTextList();
		return childTextList.size();
	}

	public SVGText get(int index) {
		getOrCreateChildTextList();
		return index < 0 || index >= childTextList.size() ? null : childTextList.get(index);
	}

	public Double getStartX() {
		getOrCreateChildTextList();
		if (childTextList == null || childTextList.size() == 0) {
			return null;
		} else if (childTextList.get(0) == null) {
			return null;
		} else {
			return childTextList.get(0).getX();
		}
	}

	/** 
	 * Gets end point of string, including width of last character.
	 * 
	 * @return
	 */
	public Double getEndX() {
		getOrCreateChildTextList();
		SVGText endText = childTextList.get(childTextList.size() - 1);
		Double x = (endText == null ? null : endText.getX());
		Double w =  (endText == null ? null : endText.getScaledWidth(guessWidth));
		return (x == null || w == null ? null : x + w);
	}

	public Double getMidX() {
		return (getStartX() + getEndX()) / 2.;
	}

	public Double translateToDouble() {
		Double d = null;
		try {
			d = Double.valueOf(toString());
		} catch (NumberFormatException e) {
			// cannot translate
		}
		return d;
	}

	public Integer translateToInteger() {
		Integer i = null;
		try {
			i = new Integer(toString());
		} catch (NumberFormatException e) {
			// cannot translate
		}
		return i;
	}

	/** 
	 * Creates a list of Words split at spaces.
	 * <p>
	 * If no spaces, returns this.
	 * 
	 * @return
	 */
	public List<Word> splitAtSpaces() {
		getOrCreateChildTextList();
		List<Word> newWordList = new ArrayList<Word>();
		Word newWord = null;
		for (SVGText text : childTextList) {
			String value = text.getValue();
			LOG.trace(value);
			//is it a space?
			if (value.trim().length() == 0) {
				newWord = null;
			} else {
				if (newWord == null) {
					newWord = new Word();
					newWordList.add(newWord);
				}
				newWord.add(text);
			}
		}
		return newWordList;
	}

	/** 
	 * Creates a Phrase of Words from raw Words.
	 * 
	 * <p>Some raw Words contain explicit spaces and these can be split and recombined into a Phrase.
	 * If the word has no spaces then the Phrase contains a single Word.</p>
	 * 
	 * @return
	 */
	public Phrase createPhrase() {
		Phrase phrase = new Phrase();
		List<Word> splitWords = splitAtSpaces();
		for (Word word : splitWords) {
			phrase.addWord(word);
		}
		return phrase;
	}

	public Real2Range getBoundingBox() {
		if (boundingBox == null){
			getOrCreateChildTextList();
			boundingBox = new Real2Range();
			RealRange xrange = getStartX() == null || getEndX() == null ? null: new RealRange(getStartX(), getEndX());
			RealRange yrange = childTextList.size() == 0 ? null : childTextList.get(0).getBoundingBox().getYRange();
			boundingBox = xrange == null || yrange == null ? null : new Real2Range(xrange, yrange);
		}
		return boundingBox;
	}

	public RealRange getYRange() {
		getBoundingBox();
		return boundingBox.getYRange();
	}
	
	public Real2 getXY() {
		getOrCreateChildTextList();
		return (childTextList.size() == 0) ? null : childTextList.get(0).getXY();
	}

	public static List<Real2Range> createBBoxList(List<Word> wordList) {
		List<Real2Range> bboxList = new ArrayList<Real2Range>();
		for (SVGElement word : wordList) {
			bboxList.add(word.getBoundingBox());
		}
		return bboxList;
	}
	
	public Double getFontSize() {
		getOrCreateChildTextList();
		Double f = null;
		if (childTextList.size() > 0) {
			f = childTextList.get(0).getFontSize();
			for (int i = 1; i < childTextList.size(); i++) {
				Double ff = childTextList.get(i).getFontSize();
				if (ff != null) {
					f = Math.max(f,  ff);
				}
			}
		}
		return f;
	}
	
	/**
	 * @return the font style
	 */
	public String getFontStyle() {
		getOrCreateChildTextList();
		String style = null;
		if (childTextList.size() > 0) {
			style = childTextList.get(0).getFontStyle();
			for (int i = 1; i < childTextList.size(); i++) {
				String style1 = childTextList.get(i).getFontStyle();
				if (style1 != null) {
					if (!style1.equals(style)) {
						LOG.trace("changed style in word from "+style+"=>"+style1+"/"+this.getStringValue());
						style = FontStyle.NORMAL.toString();
						break;
					}
				}
			}
		}
		this.setFontStyle(style);
		return style;
	}

	/**
	 * @return the font family
	 */
	public String getFontFamily() {
		getOrCreateChildTextList();
		String family = null;
		if (childTextList.size() > 0) {
			family = childTextList.get(0).getFontFamily();
			for (int i = 1; i < childTextList.size(); i++) {
				String family1 = childTextList.get(i).getFontFamily();
				if (family1 != null) {
					if (!family1.equals(family)) {
						LOG.trace("changed family in word from "+family+"=>"+family1+"/"+this.getStringValue());
						break;
					}
				}
			}
		}
		this.setFontFamily(family);
		return family;
	}

	/**
	 * @return the font family
	 */
	public String getSVGXFontName() {
		getOrCreateChildTextList();
		String fontName = null;
		if (childTextList.size() > 0) {
			fontName = childTextList.get(0).getSVGXFontName();
			for (int i = 1; i < childTextList.size(); i++) {
				String fontName1 = childTextList.get(i).getSVGXFontName();
				if (fontName1 != null) {
					if (!fontName1.equals(fontName)) {
						LOG.trace("changed fontName in word from "+fontName+"=>"+fontName1+"/"+this.getStringValue());
						break;
					}
				}
			}
		}
		this.setFontFamily(fontName);
		return fontName;
	}

	/**
	 * @return the font weight
	 */
	public String getFontWeight() {
		getOrCreateChildTextList();
		String weight = null;
		if (childTextList.size() > 0) {
			weight = childTextList.get(0).getFontWeight();
			for (int i = 1; i < childTextList.size(); i++) {
				String weight1 = childTextList.get(i).getFontWeight();
				if (weight1 != null) {
					if (!weight1.equals(weight)) {
						LOG.trace("changed weight in word from "+weight+"=>"+weight1+"/"+this.getStringValue());
						weight = FontWeight.NORMAL.toString();
						break;
					}
				}
			}
		}
		this.setFontWeight(weight);
		return weight;
	}

	/**
	 * @return the stroke
	 */
	public String getStroke() {
		getOrCreateChildTextList();
		String stroke = null;
		if (childTextList.size() > 0) {
			stroke = childTextList.get(0).getStroke();
			for (int i = 1; i < childTextList.size(); i++) {
				String stroke1 = childTextList.get(i).getStroke();
				if (stroke1 != null) {
					if (!stroke1.equals(stroke)) {
						LOG.trace("changed stroke in word from "+stroke+"=>"+stroke1+"/"+this.getStringValue());
						stroke = null;
						break;
					}
				}
			}
		}
		if (stroke != null) this.setStroke(stroke);
		return stroke;
	}

	/**
	 * @return the fill
	 */
	public String getFill() {
		getOrCreateChildTextList();
		String fill = null;
		if (childTextList.size() > 0) {
			fill = childTextList.get(0).getFill();
			for (int i = 1; i < childTextList.size(); i++) {
				String fill1 = childTextList.get(i).getFill();
				if (fill1 != null) {
					if (!fill1.equals(fill)) {
						LOG.trace("changed fill in word from "+fill+"=>"+fill1+"/"+this.getStringValue());
						fill = null;
						break;
					}
				}
			}
		}
		if (fill != null) this.setFill(fill);
		return fill;
	}
	
	public Element copyElement() {
		getOrCreateChildTextList();
		Element element = (Element) this.copy();
		for (AbstractCMElement text : childTextList) {
			element.appendChild(text.copy());
		}
		return element;
	}

	/** this requires more work as SVGText needs to access ancestors.
	 * 
	 */
	public void pullUpChildAttributes() {
		Map<String, String> attValueMap = new HashMap<String, String>();
		Set<String> liveSet = new HashSet<String>(COMMON_ATT_NAMES);

		List<Element> childList = XMLUtil.getQueryElements(this, "*");
		for (int i = 0; i < childList.size(); i++) {
			Element child = childList.get(i);
			Attribute svgxz = child.getAttribute(Path2ShapeConverter.Z_COORDINATE, SVGX_NS);
			child.removeAttribute(svgxz);
			findFullyDuplicatedAttributes(attValueMap, liveSet, child);
		}
		for (int i = 0; i < childList.size(); i++) {
			Element child = childList.get(i);
			upliftAttributes(liveSet, child);
		}

	}

	private void upliftAttributes(Set<String> liveSet, Element child) {
		for (int j = 0; j < child.getAttributeCount(); j++) {
			Attribute attribute = child.getAttribute(j);
			String name = attribute.getLocalName();
			if (liveSet.contains(name)) {
				attribute.detach();
				this.addAttribute(attribute);
			}
		}
	}

	private void findFullyDuplicatedAttributes(Map<String, String> attValueMap, Set<String> liveSet, Element child) {
		for (int j = 0; j < child.getAttributeCount(); j++) {
			Attribute att = child.getAttribute(j);
			String name = att.getLocalName();
			String value = att.getValue();
			if (liveSet.contains(name)) {
				String value1 = attValueMap.get(name);
				if (value1 == null) {
					attValueMap.put(name, value1);
				} else if (!value1.equals(value)) {
					liveSet.remove(name);
				}
			}
		}
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildTextList();
		for (SVGText text : childTextList) {
			text.rotateAndAlsoUpdateTransforms(centreOfRotation, angle);
			LOG.trace("T: "+text.toXML());
		}
		return;
	}
	
	/** make a single HtmlSpan.
	 * 
	 * @return
	 */
	public HtmlElement toHtml() {
		HtmlElement span = new HtmlSpan();
		span.setClassAttribute("word");
		span = addSuscriptsAndStyle(span);
		String value = this.getStringValue();
		span.appendChild(value);
		return span;
	}

	public boolean isBold() {
		String weight = this.getFontWeight();
		return StyleBundle.FontWeight.BOLD.toString().equalsIgnoreCase(weight);
	}
	
	public boolean isItalic() {
		String style = this.getFontStyle();
		return StyleBundle.FontStyle.ITALIC.toString().equalsIgnoreCase(style);
	}
	
	/**
	 * @return the fill
	 */
	public String getCSSStyle() {
		getOrCreateChildTextList();
		String style = null;
		if (childTextList.size() > 0) {
			style = childTextList.get(0).getStyle();
			for (int i = 1; i < childTextList.size(); i++) {
				String style1 = childTextList.get(i).getFill();
				if (style1 != null) {
					if (!style1.equals(style)) {
						LOG.trace("changed style in word from "+style+"=>"+style1+"/"+this.getStringValue());
						style = MIXED_STYLE;
						break;
					}
				}
			}
		}
		if (style != null) this.setFill(style);
		return style;
	}
	
}
