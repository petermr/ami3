package org.contentmine.graphics.svg.text.line;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlSub;
import org.contentmine.graphics.html.HtmlSup;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.rule.horizontal.LineChunk;
import org.contentmine.graphics.svg.text.TextCoordinate;
import org.contentmine.graphics.svg.text.build.Blank;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.Word;
import org.contentmine.graphics.svg.text.structure.RawWords;
import org.contentmine.graphics.svg.text.structure.TextAnalyzer;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.graphics.svg.util.SVGZUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Attribute;
import nu.xom.Element;

/** 
 * Holds a list of characters, normally in a horizontal line
 * <p>
 * Exclusively used by TextAnalyzer
 * 
 * Moved from svg2xml
 * 
 * @author pm286
 */
public class TextLine implements Iterable<SVGText> {

    //current guess (number of partial spaces to trigger full space)
	private static final double SPACE_FACTOR1 = 0.2;  
	private static final String SERIF = "Serif";
	private final static Logger LOG = Logger.getLogger(TextLine.class);
	private final static PrintStream SYSOUT = System.out;

	private static final double ITALIC = -0.18;
	private static final double BOLD = 0.25;
	private static final double EPS = 0.05;
	private static final double COORD_EPS = 0.0001;
//	private static final SVGElement SUP = new SVGText(new Real2(0., 0.), "SUP");
//	private static final SVGElement SUB = new SVGText(new Real2(0., 0.), "SUB");
	private static final Double FONT_Y_FACTOR = 0.5;
	
	private List<SVGText> textList;
	private Double yCoord = null;
	private List<Double> yCoordList = null;
	private Double fontSize = null;
	private Set<TextCoordinate> fontSizeContainerSet = null;
	
	private String lineContent = null;
	private List<TextLine> subLines;
	private TextAnalyzer textAnalyzerX;
	private RealArray characterWidthArray;
	private Real2Range boundingBox = null;
	private Double meanFontSize;
	private RealArray fontSizeArray;
	private RealArray characterSeparationArray;
	private RealArray spaceWidthArray;
	private RealArray svgCharacterWidthArray;
	private RealArray excessWidthArray;
	private double spaceFactor = SVGText.DEFAULT_SPACE_FACTOR;
	private Set<TextCoordinate> fontSizeSet;
	private Suscript suscript;
	private Set<String> fontFamilySet;
	private Multiset<String> fontFamilyMultiset;
	private Multiset<Double> fontSizeMultiset;
	private TextLine superscriptLine;
	private TextLine subscriptLine;

	private void resetWhenLineContentChanged() {
		textList = null;
		yCoord = null;
		yCoordList = null;
		fontSize = null;
		fontSizeContainerSet = null;
		boundingBox = null;
		meanFontSize =null;
		fontSizeArray = null;
		
		lineContent = null;
		subLines = null;
		characterWidthArray = null;
	}
	
	public TextLine(TextAnalyzer textAnalyzerX, List<SVGText> characterList) {
		this.textList = characterList;
		this.textAnalyzerX = textAnalyzerX;
	}
	
	public TextLine(TextAnalyzer textAnalyzerX) {
		this(textAnalyzerX, new ArrayList<SVGText>());
	}
	
	public TextLine(Collection<SVGText> texts, TextAnalyzer textAnalyzer) {
		this.textAnalyzerX = textAnalyzer;
		textList = new ArrayList<SVGText>();
		textList.addAll(texts);
	}

	public TextLine() {
		
	}

	public static List<TextLine> createSortedTextLineList(Element el) {
		SVGElement g = SVGElement.readAndCreateSVG(el);
		TextStructurer textStructurer = 
				TextStructurer.createTextStructurerWithSortedLines(g);
		return textStructurer.getTextLineList();
	}

	/**
	 * Splits the list into smaller lists whenever there is a change
	 * in fontSize, physicalStyle or yCoord. This is a heuristic and may need finer tuning.
	 * 
	 * @return
	 */
	private List<TextLine> splitLineByCharacterAttributes() {
		if (subLines == null) {
			subLines = new ArrayList<TextLine>();
			Double lastFontSize = null;
			TextLine charList = null;
			for (int i = 0; i < textList.size(); i++) {
				SVGText text = textList.get(i);
				Double fontSize = text.getFontSize();
				LOG.trace("fontSize "+fontSize);
				if (i == 0) {//|| LineAttributesHaveChanged(lastFontSize, lastYCoord, lastPhysicalStyle, fontSize, yCoord, physicalStyle)) {
					charList = new TextLine(this.textAnalyzerX);
					getSubLines().add(charList);
				}
				charList.add(text);
				lastFontSize = fontSize;
			}
			if (getSubLines().size() != 1) {
				for (TextLine chList : getSubLines()) {
					chList.normalize();
				}
			}
			if (lastFontSize != null) {
				fontSize = lastFontSize;
			}
		}
		return getSubLines();
	}

	private void normalize() {
		getFontSize();
		getYCoord();
		getLineContent();
		//LOG.trace("words "+((wordSequence == null) ? "null" :  wordSequence.size()));
	}
	
	/** 
	 * Returns the common value of fontSize or null
	 * if there is any variation
	 */
	public Double getFontSize() {
		Double fs = null;
		fontSizeContainerSet = getFontSizeContainerSet();
		for (TextCoordinate fontSize : fontSizeContainerSet) {
			LOG.trace("FSZ "+fontSize);
		}
		if (fontSizeContainerSet != null) {
			if (fontSizeContainerSet.size() == 1) {
				fs = fontSizeContainerSet.iterator().next().getDouble();
			}
		}
		return fs;
	}

	/** 
	 * Returns the common value of fontFamily or null
	 * if there is any variation
	 */
	public String getFontFamily() {
		String family = null;
		getFontFamilySet();
		if (fontFamilySet != null) {
			if (fontFamilySet.size() == 1) {
				family = fontFamilySet.iterator().next();
			} else {
				LOG.trace("FF"+fontFamilySet);
			}
		}
		return family;
	}
			
	public Multiset<String> getFontFamilyMultiset() {
		if (fontFamilyMultiset == null) {
			fontFamilyMultiset = HashMultiset.create();
			for (int i = 0; i < textList.size(); i++) {
				SVGElement text = textList.get(i);
				String family = text.getFontFamily();
				fontFamilyMultiset.add(family);
			}
		}
		return fontFamilyMultiset;
	}
	
	private Set<String> getFontFamilySet() {
		if (fontFamilySet == null) {
			fontFamilySet = new HashSet<String>();
			for (int i = 0; i < textList.size(); i++) {
				SVGElement text = textList.get(i);
				String family = text.getFontFamily();
				fontFamilySet.add(family);
			}
		}
		return fontFamilySet;
	}
	
	public Set<TextCoordinate> getFontSizeContainerSet() {
		if (fontSizeContainerSet == null) {
			fontSizeContainerSet = new HashSet<TextCoordinate>();
			if (textList != null) {
				for (int i = 0; i < textList.size(); i++) {
					SVGElement text = textList.get(i);
					TextCoordinate fontSize = new TextCoordinate(text.getFontSize());
					fontSizeContainerSet.add(fontSize);
				}
			}
		}
		LOG.trace("FSSET "+fontSizeContainerSet);
		return fontSizeContainerSet;
	}
	
	/** 
	 * Returns the common value of yCoord or null
	 * if there is any variation
	 */
	public Double getYCoord() {
		if (yCoord == null) {
			getYCoordList();
			for (Double y : yCoordList) {
				if (y == null || (yCoord != null && !Real.isEqual(y, yCoord, COORD_EPS))) {
					yCoord = null;
					break;
				}
				yCoord = y;
			}
		}
		return yCoord;
	}
		
	private List<Double> getYCoordList() {
		if (yCoordList == null) {
			Double lastYCoord = null;
			yCoordList = new ArrayList<Double>();
			for (int i = 0; i < textList.size(); i++) {
				SVGText text = textList.get(i);
				Double yCoord = text.getY();
				if (yCoord == null) {
					throw new RuntimeException("text has no Y coord");
				} else if (lastYCoord == null) {
					yCoordList.add(yCoord);
				}else if (!Real.isEqual(yCoord, lastYCoord, EPS)) {
					yCoordList.add(yCoord);
				}
				lastYCoord = yCoord;
			}
		}
		return yCoordList;
	}

	public List<SVGText> getCharacterList() {
		return textList;
	}

	public SVGText get(int i) {
		return textList.get(i);
	}

	public void add(SVGText svgText) {
		if (svgText != null) {
			yCoord = (yCoord == null) ? svgText.getY() : yCoord;
			ensureCharacterList();
			textList.add(svgText);
		}
	}

	private void ensureCharacterList() {
		if (textList == null) {
			textList = new ArrayList<SVGText>();
		}
	}

	public int size() {
		return textList.size();
	}

	public Iterator<SVGText> iterator() {
		return textList.iterator();
	}
	
	public List<SVGText> getSVGTextCharacters() {
		return textList;
	}

	public void sortLineByX() {
		// assumes no coincident text??
		Map<Integer, SVGText> lineByXCoordMap = new HashMap<Integer, SVGText>();
		for (SVGText text : this) {
			lineByXCoordMap.put((int) Math.round(SVGUtil.getTransformedXY(text).getX()), text);
		}
		Set<Integer> xCoords = lineByXCoordMap.keySet();
		Integer[] xArray = xCoords.toArray(new Integer[xCoords.size()]);
		Arrays.sort(xArray);
		List<SVGText> newCharacterList = new ArrayList<SVGText>();
		for (int x : xArray) {
			newCharacterList.add(lineByXCoordMap.get(x));
		}
		textList = newCharacterList;
		getFontSize();
		getYCoord();
		//getSinglePhysicalStyle();
		getLineContent();
		splitLineByCharacterAttributes();
	}
	
	public String getLineContent() {
		if (lineContent == null) {
			StringBuilder sb = new StringBuilder();

			if (textList != null) {
				for (int i = 0; i < textList.size(); i++) {
					SVGText text = textList.get(i);
					String ch = text.getText();
					sb.append(ch);
				}
			}
			lineContent = sb.toString();

		}
		LOG.trace("lineContent: " + lineContent);
		return lineContent;
	}

	public boolean isBold() {
		if (textList != null) {
			for (SVGText character : textList) {
				if (!character.isBold())
					return false;
			}
			return true;
		}
		return false;

	}

	public String toString() {
		String s;
		ensureCharacterList();
		if (getSubLines() != null && getSubLines().size() > 1) {
			s = "split: \n";
			for (TextLine splitList : getSubLines()) {
				s += "   "+splitList+"\n";
			}
		} else {
			s = "chars: "+((textList == null) ? "null" : textList.size()) +
				" Y: "+yCoord+
				" fontSize: "+fontSize+
				//" physicalStyle: "+physicalStyle+
				" >>"+getLineContent();
		}
		return s;
	}

	private List<TextLine> getSubLines() {
		return subLines;
	}

	public String getLineString() {
		StringBuilder sb = new StringBuilder();
		for (SVGText text : textList) {
			sb.append(text.getText());
		}
		return sb.toString();
	}
	
	/** 
	 * Uses space factor (default .3 at present)
	 */
	public void insertSpaces() {
		insertSpaces(spaceFactor);
	}
	
	/** 
	 * Computes inter-char gaps. If >= computed width of space adds ONE space
	 * later routines can calculate exact number of spaces if wished
	 * this is essentially a word break detector and marker
	 */
	public void insertSpaces(double sFactor) {
		if (textList.size() > 0) {
			List<SVGText> newCharacters = new ArrayList<SVGText>();
			SVGText lastText = textList.get(0);
			newCharacters.add(lastText);
			for (int i = 1; i < textList.size(); i++) {
				SVGText text = textList.get(i);
				Double nSpaces = lastText.getEnSpaceCount(text);
			    while (nSpaces > SVGText.SPACE_FACTOR) {
			    	SVGText space = lastText.createSpaceCharacterAfter();
			    	newCharacters.add(space);
			    	nSpaces -= 1;
			    	lastText = space;
			    }
			    newCharacters.add(text);
			    lastText = text;
			}
			resetWhenLineContentChanged();
			textList = newCharacters;
		}
	}

//	/** computes inter-char gaps. If >= computed width of space adds ONE space
//	 * later routines can calculate exact number of spaces if wished
//	 * this is essentially a word break detector and marker
//	 */
//	public void insertSpaces(double sFactor) {
//		if (textList.size() > 0) {
//			List<SVGText> newCharacters = new ArrayList<SVGText>();
//			SVGText lastText = textList.get(0);
//			newCharacters.add(lastText);
//			Double fontSize = lastText.getFontSize();
//			Double lastWidth = lastText.getScaledWidth();
//			Double lastX = lastText.getX();
//			for (int i = 1; i < textList.size(); i++) {
//				SVGText text = textList.get(i);
//				double x = text.getX();
//				double extraSpace = calculateExtraSpace(sFactor, fontSize, lastWidth, lastX, x);
//			    if (extraSpace > 0) {
//			    	addSpaceCharacter(newCharacters, lastX + lastWidth, lastText);
//			    }
//			    newCharacters.add(text);
//				lastWidth = text.getScaledWidth();
//				lastX = x;
//			}
//			resetWhenLineContentChanged();
//			textList = newCharacters;
//		}
//	}

//	private double calculateExtraSpace(double sFactor, Double fontSize, Double lastWidth,
//			Double lastX, double x) {
//		double extraSpace = x - lastX - lastWidth - sFactor * fontSize;
//		return extraSpace;
//	}
//
	public Double getMeanFontSize() {
		if (meanFontSize == null) {
			getFontSizeArray();
			meanFontSize = fontSizeArray.getMean();
		}
		return meanFontSize;
	}

	private RealArray getFontSizeArray() {
		if (fontSizeArray == null || textList != null || textList.size() == 0) {
			fontSizeArray = new RealArray(textList.size());
			for (int i = 0; i < textList.size(); i++) {
				fontSizeArray.setElementAt(i, textList.get(i).getFontSize());
			}
		}
		return fontSizeArray;
	}

	public Real2Range getBoundingBox() {{
		if (boundingBox == null) 
			if (textList != null && textList.size() > 0) {
				double xmin = textList.get(0).getBoundingBox().getXMin();
				double xmax = textList.get(textList.size()-1).getBoundingBox().getXMax();
				RealRange xRange = new RealRange(xmin, xmax); 
				double ymin = textList.get(0).getBoundingBox().getYMin();
				double ymax = textList.get(textList.size()-1).getBoundingBox().getYMax();
				RealRange yRange = new RealRange(ymin, ymax); 
				boundingBox = new Real2Range(xRange, yRange);
			}
		}
		return boundingBox;
	}

	/** Array of width from SVGText @svgx:width attribute
	 * @return array of widths
	 */
	private RealArray getSVGCharacterWidthArray() {
		if (svgCharacterWidthArray == null) { 
			svgCharacterWidthArray = new RealArray(textList.size());
			for (int i = 0; i < textList.size() ; i++) {
				Double width = textList.get(i).getScaledWidth();
				svgCharacterWidthArray.setElementAt(i,  width);
			}
			svgCharacterWidthArray.format(TextAnalyzer.NDEC_FONTSIZE);
		}
		return svgCharacterWidthArray;
	}
	

	/** actual separation of characters by delta X of coordinates
	 * Last character cannot have separation, so array length is characterList.size()-1
	 * @return array of separations
	 */
	private RealArray getCharacterSeparationArray() {
		if (characterSeparationArray == null) { 
			characterSeparationArray = new RealArray(textList.size() - 1);
			Double x = textList.get(0).getX();
			for (int i = 0; i < textList.size() - 1; i++) {
				Double nextX = textList.get(i + 1).getX();
				Double separation = nextX - x;
				characterSeparationArray.setElementAt(i, separation);
				x = nextX;
			}
			characterSeparationArray.format(TextAnalyzer.NDEC_FONTSIZE);
		}
		return characterSeparationArray;
	}
	
	/** actual separation of space characters by delta X of coordinates
	 * array is in order of space characters but normally shorter
	 * and does not directly map onto characters
	 * we may provide a mapping index
	 * initially used for stats on space sizes
	 * @return array of separations
	 */
	private RealArray getActualWidthsOfSpaceCharacters() {
		getCharacterSeparationArray();
		if (characterSeparationArray != null) { 
			spaceWidthArray = new RealArray();
			for (int i = 0; i < textList.size() - 1; i++) {
				SVGText charx = textList.get(i);
				String text = charx.getText();
				if (XMLConstants.S_SPACE.equals(charx.getText())) {
					spaceWidthArray.addElement(characterSeparationArray.elementAt(i));
				}
			}
		}
		return spaceWidthArray;
	}


//	/**
//	 * @param newCharacters
//	 * @param spaceX
//	 * @param templateText to copy attributes from
//	 */
//	private void addSpaceCharacter(List<SVGText> newCharacters, double spaceX, SVGElement templateText) {
//		SVGText spaceText = new SVGText();
//		XMLUtil.copyAttributes(templateText, spaceText);
//		spaceText.setText(" ");
//		spaceText.setX(spaceX);
//		PDF2SVGUtil.setSVGXAttribute(spaceText, PDF2SVGUtil.CHARACTER_WIDTH, String.valueOf(SPACE_WIDTH1000));
//		newCharacters.add(spaceText);
//	}

	public Double getMeanWidthOfSpaceCharacters() {
		RealArray spaceWidths = getActualWidthsOfSpaceCharacters();
		return spaceWidths == null ? null : spaceWidths.getMean();
	}

	public Set<TextCoordinate> getFontSizeSet() {
		if (fontSizeSet == null) {
			fontSizeSet = new HashSet<TextCoordinate>();
			for (SVGElement text : textList) {
				double fontSize = text.getFontSize();
				fontSizeSet.add(new TextCoordinate(fontSize));
			}
		}
		return fontSizeSet;
	}

	public TextLine getSuperscript() {
		Double fontSize = this.getFontSize();
		TextLine superscript = null;
		Integer ii = textAnalyzerX.getSerialNumber(this);
		if (ii != null && ii > 0) {
			Double thisY = this.getYCoord();
			TextLine previousLine = textAnalyzerX.getLinesInIncreasingY().get(ii-1);
			Double previousY = previousLine.getYCoord();
			if (previousY != null && thisY != null) {
				if (thisY - previousY < fontSize * FONT_Y_FACTOR) {
					superscript = previousLine;
				}
			}
		}
		return superscript;
	}

	public TextLine getSubscript() {
		Double fontSize = this.getFontSize();
		TextLine subscript = null;
		Integer ii = textAnalyzerX.getSerialNumber(this);
		if (ii != null && ii < textAnalyzerX.getLinesInIncreasingY().size()-1) {
			Double thisY = this.getYCoord();
			TextLine nextLine = textAnalyzerX.getLinesInIncreasingY().get(ii+1);
			Double nextY = nextLine.getYCoord();
			if (nextY != null && thisY != null) {
				if (nextY - thisY < fontSize * FONT_Y_FACTOR) {
					subscript = nextLine;
				}
			}
		}
		return subscript;
	}

	/** mainly debug
	 * 
	 * @return
	 */
	public List<TextLine> createSuscriptTextLineList() {
		List<TextLine> textLineList = new ArrayList<TextLine>();
		Integer thisIndex = 0;
		TextLine superscript = this.getSuperscript();
		List<SVGText> superChars = (superscript == null) ? new ArrayList<SVGText>() : superscript.textList;
		Integer superIndex = 0;
		TextLine subscript = this.getSubscript();
		List<SVGText> subChars = (subscript == null) ? new ArrayList<SVGText>() : subscript.textList;
		Integer subIndex = 0;
		TextLine textLine = null;
		while (true) {
			SVGText nextSup = peekNext(superChars, superIndex);
			SVGText nextThis = peekNext(textList, thisIndex);
			SVGText nextSub = peekNext(subChars, subIndex);
			SVGText nextText = textWithLowestX(nextSup, nextThis, nextSub);
			if (nextText == null) {
				break;
			}
			Suscript suscript = Suscript.NONE;
			if (nextText.equals(nextSup)) {
				superIndex++;
				suscript = Suscript.SUP;
			} else if (nextText.equals(nextThis)) {
				thisIndex++;
				suscript = Suscript.NONE;
			} else if (nextText.equals(nextSub)) {
				subIndex++;
				suscript = Suscript.SUB;
			}
			if (textLine == null || !(suscript.equals(textLine.getSuscript()))) {
				textLine = new TextLine(textAnalyzerX);
				textLine.setSuscript(suscript);
				textLineList.add(textLine);
			}
			textLine.add(nextText);
		}
		for (TextLine tLine : textLineList) {
			tLine.insertSpaces();
		}
		return textLineList;
	}
	
	public static HtmlElement createHtmlElement(List<TextLine> textLineList) {
		HtmlP p = new HtmlP();
		for (TextLine textLine : textLineList) {
			HtmlElement pp = textLine.getHtmlElement();
			if (pp instanceof HtmlSpan) {
				SVGZUtil.moveChildrenFromTo(pp, p);
			} else {
				p.appendChild(HtmlElement.create(pp));
			}
		}
		return p;
	}
//

	public HtmlElement getHtmlElement() {
		HtmlElement htmlElement = null;
		Suscript suscript = this.getSuscript();
		if (suscript == null || suscript.equals(Suscript.NONE)) {
			htmlElement = new HtmlSpan();
		} else if (suscript.equals(Suscript.SUB)) {
			htmlElement = new HtmlSub();
		} else if (suscript.equals(Suscript.SUP)) {
			htmlElement = new HtmlSup();
		}
		// this may create one or more span children as we encounter fonts and styles
		// even sub/super may have different styles within them
		addCharacters(htmlElement);
		LOG.trace("Html Element: "+htmlElement.toXML());
		return htmlElement;
	}

	private void addCharacters(HtmlElement htmlElement) {
		String currentFontFamily = null;
		String currentFontStyle = null;
		String currentFontWeight = null;
		String currentColor = null;
		Double currentFontSize = null;
		HtmlSpan span = null;
		StringBuffer sb = null;
		for (SVGElement character : textList) {
			String fontFamily = character.getFontFamily();
			String fontStyle = character.getFontStyle();
			String fontWeight = character.getFontWeight();
			String color = character.getFill();
			Double fontSize = character.getFontSize();
			if (!equals(currentFontSize, fontSize, 0.01) ||
				!equals(currentColor, color) ||
				!equals(currentFontStyle, fontStyle) ||
				!equals(currentFontWeight, fontWeight) ||
				!equals(currentFontFamily, fontFamily)
				) {
				if (span != null) {
					span.setValue(sb.toString());
				}
				span = new HtmlSpan();
				StringBuffer sbatt = new StringBuffer();
				addStyle(sbatt, "font-size", fontSize);
				addStyle(sbatt, "color", color);
				addStyle(sbatt, "font-style", fontStyle);
				addStyle(sbatt, "font-weight", fontWeight);
				addStyle(sbatt, "font-family", fontFamily);
				span.addAttribute(new Attribute("style", sbatt.toString()));
				htmlElement.appendChild(span);
				sb = new StringBuffer();
				currentFontFamily = fontFamily;
				currentFontStyle = fontStyle;
				currentFontWeight = fontWeight;
				currentColor = color;
				currentFontSize = fontSize;
			}
			sb.append(character.getValue());
		}
		if (span != null) {
			span.setValue(sb.toString());
		}
	}

	private static void addStyle(StringBuffer sbatt, String attName, String value) {
		if (value != null) {
			sbatt.append(attName+":"+value+";");
		}
	}

	private static void addStyle(StringBuffer sbatt, String attName, Double value) {
		if (value != null) {
			sbatt.append(attName+":"+value+"px;");
		}
	}

	private boolean equals(String s1, String s2) {
		return (s1 == null && s2 == null) ||
				(s1 != null && s2 != null && s1.equals(s2));
	}

	private boolean equals(Double s1, Double s2, double eps) {
		return (s1 == null && s2 == null) ||
				(s1 != null && s2 != null && Real.isEqual(s1, s2, eps));
	}

	public Suscript getSuscript() {
		return suscript;
	}

	public void setSuscript(Suscript suscript) {
		this.suscript = suscript;
	}

	public static SVGText textWithLowestX(SVGText nextSup, SVGText nextThis, SVGText nextSub) {
		SVGText lowestText = null;
		if (nextSup == null && nextThis == null && nextSub == null) {
			return null;
		}
		if (nextSup != null && (lowestText == null || (lowestText.getX() > nextSup.getX()))) {
			lowestText = nextSup;
		}
		if (nextThis != null && (lowestText == null || (lowestText.getX() > nextThis.getX()))) {
			lowestText = nextThis;
		}
		if (nextSub != null && (lowestText == null || (lowestText.getX() > nextSub.getX()))) {
			lowestText = nextSub;
		}
		LOG.trace("lowestX "+lowestText.getX());
		return lowestText;
	}

	public static SVGText peekNext(List<SVGText> characterList, Integer index) {
		SVGText text = null;
		if (characterList != null) {
			text = (index >= characterList.size()) ? null : characterList.get(index);
		}
		return text;
	}

	public String getSpacedLineString() {
		StringBuilder sb = new StringBuilder();
		if (textList != null) {
			for (AbstractCMElement text : textList) {
				String s = text.getValue();
				if (s.trim().length() == 0) {
					s = " ";
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public Double getFirstXCoordinate() {
		getBoundingBox();
		RealRange xRange = (boundingBox == null ? null : boundingBox.getXRange());
		return (xRange == null ? null : xRange.getMin());
	}

	public Double getLastXCoordinate() {
		getBoundingBox();
		RealRange xRange = (boundingBox == null ? null : boundingBox.getXRange());
		return (xRange == null ? null : xRange.getMax());
	}

	public String getRawValue() {
		StringBuilder sb = new StringBuilder();
		if (textList != null) {
			for (AbstractCMElement text : textList) {
				sb.append(text.getValue());
			}
		}
		return sb.toString();
	}

	/**
	 * Merges two lines (at present only characters from second one)
	 * <p>
	 * Used when two lines have same Y-coord but have been split into two parts
	 * <p>
	 * Mean and communal properties are probably rubbish (maybe should be nulled?)
	 * 
	 * @param textLine
	 */
	public void merge(TextLine textLine) {
		for (SVGText character : textLine.textList) {
			textList.add(character);
		}
		sortLineByX();
	}

	public Double getCommonestFontSize() {
		getFontSizeMultiset();
		Set<Entry<Double>> entrySet = fontSizeMultiset.entrySet();
		Double commonestSize = null;
		Integer commonestCount = null;
		for (Entry<Double> entry : entrySet) {
			Double size = entry.getElement();
			Integer count = entry.getCount();
			if (commonestSize == null) {
				commonestSize = size;
				commonestCount = count;
			} else {
				if (count > commonestCount) {
					commonestSize = size;
					commonestCount = count;
				}
			}
		}
		return commonestSize;
	}
	
	public Multiset<Double> getFontSizeMultiset() {
		if (fontSizeMultiset == null) {
			fontSizeMultiset = HashMultiset.create();
			if (textList != null) {
				for (int i = 0; i < textList.size(); i++) {
					SVGElement text = textList.get(i);
					Double size = text.getFontSize();
					fontSizeMultiset.add(size);
				}
			}
		}
		return fontSizeMultiset;
	}

	/** 
	 * Gets raw list of words (no Phrases yet).
	 * 
	 * @return
	 */
	public RawWords getRawWords() {
		int ntext = textList.size();
		RawWords rawWords = new RawWords();
		Word word = new Word();
		rawWords.add(word);
		for (int i = 0; i < ntext; i++) {
			SVGText text = new SVGText(textList.get(i));
			word.add(text);
			if (i < ntext - 1)  {
				SVGText nextText = textList.get(i + 1);
				Double spaceCount = text.getEnSpaceCount(nextText);
				if (spaceCount != null && spaceCount > SPACE_FACTOR1) {
					word = new Word();
					rawWords.add(word);
				}
			}
		}
		String style = word.getFontStyle();
		String weight = word.getFontWeight();
		return rawWords;
	}

	public RealArray getXCoordinateArray() {
		RealArray xArray = new RealArray();
		for (int i = 0; i < textList.size(); i++) {
			xArray.addElement(textList.get(i).getX());
		}
		return xArray;
	}

	public RealArray getFontWidthArray() {
		RealArray xArray = new RealArray();
		for (int i = 0; i < textList.size(); i++) {
			xArray.addElement(textList.get(i).getSVGXFontWidth());
		}
		return xArray;
	}

	public List<String> getValueList() {
		List<String> valueList = new ArrayList<String>();
		for (AbstractCMElement text : textList) {
			valueList.add(text.getValue());
		}
		return valueList;
	}

	public RealArray getSeparationArray() {
		RealArray separationArray = new RealArray();
		for (int i = 0; i < textList.size() - 1; i++) {
			Double space = textList.get(i).getSeparation(textList.get(i + 1));
			separationArray.addElement(space);
		}
		return separationArray;
	}

	public List<Phrase> createPhraseList() {
		List<Phrase> phraseList = new ArrayList<Phrase>();
		RawWords rawWords = getRawWords();
		for (Word word : rawWords) {
			Phrase phrase = word.createPhrase();
			phraseList.add(phrase);
		}
		return phraseList;
	}
	
	public List<LineChunk> getLineChunks() {
		List<LineChunk> lineChunkList = new ArrayList<LineChunk>();
		List<Phrase> phraseList = this.createPhraseList();
		for (int i = 0; i < phraseList.size(); i++) {
			Phrase phrase = phraseList.get(i);
			if (i > 0) {
				Blank blank = phraseList.get(i - 1).createBlankBetween(phrase);
				lineChunkList.add(blank);
			}
			lineChunkList.add(phrase);
		}
		return lineChunkList;
	}

	public void setSuperscriptLine(TextLine previousLine) {
		this.superscriptLine = previousLine;
	}

	public void setSubscriptLine(TextLine nextLine) {
		this.subscriptLine = nextLine;
	}
	
	public void rotate(Real2 xy, Angle angle) {
		for (int i = 0; i < textList.size(); i++) {
			SVGText text = textList.get(i);
			Transform2 t2 = Transform2.getRotationAboutPoint(angle, xy);
			text.setTransform(t2);
		}
	}

	public void formatTransform(int nplaces) {
		for (SVGText text : textList) {
			text.formatTransform(nplaces);
		}
	}
}
