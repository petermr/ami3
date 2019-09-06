package org.contentmine.graphics.svg.text.line;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.text.structure.TextAnalyzer;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.graphics.svg.util.SVGZUtil;

/** 
 * Holds one or more TextLines in a chunk
 * <p>
 * Bounding boxes of textLines overlap
 * 
 * @author pm286
 */
public class ScriptLine implements Iterable<TextLine> {

	private final static Logger LOG = Logger.getLogger(ScriptLine.class);
	
	private static final double X_CHARACTER_TOL = 0.45;  // heuristic
	private static final double FONT_SIZE_EPS = 0.02;  // heuristic
	private static final double SUSCRIPT_EPS = 0.5;  // heuristic
	public static final String SUB = "sub";
	public static final String SUP = "sup";
	public static final String SUSCRIPT = "suscript";

	//private static final Double SPACEFACTOR = 0.12;// 
	//FIXME
	//private static final Double SPACEFACTOR = TextLine.DEFAULT_SPACE_FACTOR;
	//space appears to be ca .29 * fontSize so take half this
	private static final Double SPACEFACTOR = 0.15; 
	public static final String TERM = "  %%%%\n";

	// crude (later we shall scan indents for this)
	private static final Double LEFT_INDENT_MIN = 5.;
	private static final Double RIGHT_INDENT_MIN = 5.;
	
	protected List<TextLine> textLineList = null;
	protected TextStructurer textStructurer;
	private int largestLine;
	private StyleSpans styleSpans;
	private String textContentWithSpaces;

	private Double spaceFactor = SPACEFACTOR;

	private HtmlElement htmlElement;
	
	public ScriptLine(TextStructurer textStructurer) {
		textLineList = new ArrayList<TextLine>();
		this.textStructurer = textStructurer;
	}
	
	public Iterator<TextLine> iterator() {
		return textLineList.iterator();
	}
	
	public int size() {
		return textLineList.size();
	}
	
	public void add(TextLine textLine) {
		textLineList.add(textLine);
	}
	
	public TextLine get(int i) {
		return textLineList.get(i);
	}

	/** 
	 * Generate ScriptLines by splitting into groups based around the commonest font size
	 * 
	 * @param textStructurer
	 * @return
	 */
	public List<ScriptLine> splitIntoUniqueChunks(TextStructurer textStructurer) {
		IntArray commonestFontSizeArray = createSerialNumbersOfCommonestFontSizeLines();
		List<ScriptLine> splitArray = new ArrayList<ScriptLine>();
		if (commonestFontSizeArray.size() < 2) {
			splitArray.add(this);
		} else {
			Integer lastCommonestFontSizeSerial = null;
			Integer groupStart = 0;
			Double lastY = null;
			for (int serial = 0; serial < textLineList.size(); serial++) {
				TextLine textLine = get(serial);
				Double currentY = textLine.getYCoord();
				if (textStructurer.isCommonestFontSize(textLine)) {
					if (lastCommonestFontSizeSerial != null) {
						int delta = serial - lastCommonestFontSizeSerial;
						// two adjacent commonestFont lines
						if (delta == 1) {
							packageAsGroup(groupStart, lastCommonestFontSizeSerial, splitArray);
							groupStart = serial;
						} else if (delta == 2) {
							TextLine midLine = textLineList.get(serial - 1);
							Double midY = midLine.getYCoord();
							if (midY == null || lastY == null || currentY == null) {
								LOG.trace("null "+midY+" / "+currentY + " / "+lastY);
							} else if (midY - lastY > currentY - midY) {
								packageAsGroup(groupStart, serial - 1, splitArray);
								groupStart = serial;
							} else {
								packageAsGroup(groupStart, lastCommonestFontSizeSerial, splitArray);
								groupStart = serial - 1;
							}
						} else if (delta == 3) {
							// assume subscript and then superscript
							packageAsGroup(groupStart, serial - 2, splitArray);
							groupStart = serial - 1;
						} else {
							reportErrorOrMaths(splitArray);
						}
					} else {
						if (serial >= 2) {
							reportErrorOrMaths(splitArray);
						}
						// continue processing
					}
					lastCommonestFontSizeSerial = serial;
					lastY = textLineList.get(serial).getYCoord();
					// last line of group?
				}
				if (serial == textLineList.size() - 1) {
					packageAsGroup(groupStart, serial, splitArray);
				}
			}
		}
		return splitArray;
	}

	private ScriptLine packageAsGroup(int groupStart, int groupEnd, List<ScriptLine> splitArray) {
		ScriptLine group = new ScriptLine(textStructurer);
		Double maxFontSize = 0.0;
		largestLine = -1;
		int lineNumber = 0;
		for (int i = groupStart; i <= groupEnd; i++) {
			TextLine textLine = textLineList.get(i);
			Double fontSize = textLine.getFontSize();
			if (fontSize != null && fontSize > maxFontSize) {
				largestLine = lineNumber;
				maxFontSize = fontSize;
			}
			lineNumber++;
			group.add(textLine);
		}
		splitArray.add(group);
		return group;
	}

	private IntArray createSerialNumbersOfCommonestFontSizeLines() {
		int iline = 0;
		IntArray commonestFontSizeArray = new IntArray();
		for (TextLine textLine : textLineList) {
			if (textStructurer.isCommonestFontSize(textLine)) {
				commonestFontSizeArray.addElement(iline);
				if (commonestFontSizeArray.size() > 1) {
					LOG.trace("COMMONEST FONT SIZE "+commonestFontSizeArray.size());
				}
			}
			iline++;
		}
		return commonestFontSizeArray;
	}
	
	public String summaryString() {
		StringBuilder sb = new StringBuilder("");
		List<ScriptWord> scriptWordList = getScriptWordList();
		int i = 0;
		for (ScriptWord word : scriptWordList) {
			if (i++ > 0 ) sb.append(" ");
			sb.append(word.summaryString());
		}
		/*for (TextLine textLine : textLineList) {
			sb.append(SVG2XMLUtil.trimText(30, textLine.getSpacedLineString())+"");
		}*/
		sb.append("\n");
		return sb.toString();
	}
		
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		int i = 0;
		for (TextLine textLine : textLineList) {
			if (i++ > 0) sb.append("\n");
			sb.append(textLine.getSpacedLineString());
		}
		sb.append(TERM);
		return sb.toString();
	}
	
	public List<TextLine> createSuscriptTextLineList() {
		List<TextLine> outputTextLineList = null;
		TextLine superscript = null;
		TextLine middleLine = null;
		TextLine subscript = null;
		if (textLineList.size() == 1) {
			middleLine = textLineList.get(0);
		} else if (textLineList.size() == 2) {
			TextLine text0 = textLineList.get(0);
			TextLine text1 = textLineList.get(1);
			if (!textStructurer.isCommonestFontSize(text0) && !textStructurer.isCommonestFontSize(text1)) {
				Double fontSize0 = text0.getFontSize();
				Double fontSize1 = text1.getFontSize();
				if (fontSize1 == null) {
					superscript = null;
					middleLine = text0;
					subscript = text1;
				} else if(fontSize0 == null) {
					superscript = text0;
					middleLine = text1;
					subscript = null;
			    } else if(fontSize0 > fontSize1) {
					superscript = null;
					middleLine = text0;
					subscript = text1;
				} else {
					superscript = text0;
					middleLine = text1;
					subscript = null;
				}
			} else if (textStructurer.isCommonestFontSize(text0) && !textStructurer.isCommonestFontSize(text1)) {
					superscript = null;
					middleLine = text0;
					subscript = text1;
			} else if (!textStructurer.isCommonestFontSize(text0) && textStructurer.isCommonestFontSize(text1)) {
				superscript = textLineList.get(0);
				middleLine = textLineList.get(1);
				subscript = null;
			} else {
				for (TextLine tLine : textLineList) {
					LOG.trace(">>>> "+tLine);
				}
				LOG.error("Only one commonestFontSize allowed for 2 line textLineGroup");
			}
		} else if (this.textLineList.size() == 3) {
			if (!textStructurer.isCommonestFontSize(textLineList.get(0)) &&
				!textStructurer.isCommonestFontSize(textLineList.get(2))) {
				superscript = textLineList.get(0);
				middleLine = textLineList.get(1);
				subscript = textLineList.get(2);
			} else {
				reportErrorOrMathsSuscript();
				middleLine = new TextLine();
				subscript = null;
				superscript = null;
			}
		} else {
			reportErrorOrMathsSuscript();
			middleLine = new TextLine();
			subscript = null;
			superscript = null;
		}
		outputTextLineList = createSuscriptTextLineList(superscript, middleLine, subscript);
		return outputTextLineList;
	}

	/** 
	 * NYI 
	 */
	private ScriptLine reportErrorOrMathsSuscript() {
		LOG.trace("Suscript problem: Maths or table? "+textLineList.size());
		ScriptLine group = new ScriptLine(textStructurer);
		//splitArray.add(group);

		for (TextLine textLine : textLineList) {
			LOG.trace("text "+textLine);
		}
		return group;
	}
	
	private ScriptLine reportErrorOrMaths(List<ScriptLine> splitArray) {
		LOG.trace("Maths or table? "+textLineList.size());
		//TextLineGroup group = new TextLineGroup();
		ScriptLine group = null;
	    splitArray.add(group);
	    splitArray.add(null);

		for (TextLine textLine : textLineList) {
			LOG.trace("text "+textLine);
		}
		return group;
	}
	
	/** 
	 * Preparation for HTML
	 * 
	 * @return
	 */
	public static List<TextLine> createSuscriptTextLineList(TextLine superscript, TextLine middleLine, TextLine subscript) {
		List<TextLine> textLineList = new ArrayList<TextLine>();
		if (subscript == null && middleLine == null && superscript == null) {
			textLineList.add(null);
			return textLineList;
		}
		List<SVGText> middleChars = (middleLine == null ? null : middleLine.getCharacterList());
		Integer thisIndex = 0;
		List<SVGText> superChars = (superscript == null ? new ArrayList<SVGText>() : superscript.getCharacterList());
		Integer superIndex = 0;
		List<SVGText> subChars = (subscript == null ? new ArrayList<SVGText>() : subscript.getCharacterList());
		Integer subIndex = 0;
		TextLine textLine = null;
		while (true) {
			SVGText nextSup = TextLine.peekNext(superChars, superIndex);
			SVGText nextThis = TextLine.peekNext(middleChars, thisIndex);
			SVGText nextSub = TextLine.peekNext(subChars, subIndex);
			SVGText nextText = TextLine.textWithLowestX(nextSup, nextThis, nextSub);
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
				TextAnalyzer textAnalyzerX = null;
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

	public HtmlElement createHtmlElement() {
		if (htmlElement == null) {
			List<TextLine> lineList = createSuscriptTextLineList();
			htmlElement = TextLine.createHtmlElement(lineList);
			SVGZUtil.removeStyles(htmlElement);
		}
		return htmlElement;
	}
	
	public TextLine getLargestLine() {
		return (textLineList.get(largestLine));
	}

	public boolean isBold() {
		return (textLineList.get(largestLine)).isBold();
	}

	public String getFontFamily() {
		return (textLineList.get(largestLine)).getFontFamily();
	}

	public Double getFontSize() {
		return (textLineList.get(largestLine)).getFontSize();
	}

	public Double getMeanFontSize() {
		return (textLineList.get(largestLine)).getMeanFontSize();
	}

	public List<TextLine> getTextLineList() {
		return textLineList;
	}
	
	public String getRawValue() {
		return (textLineList.get(largestLine)).getRawValue();
	}

	public List<SVGText> getTextList() {
		List<SVGText> textList = new ArrayList<SVGText>();
		for (TextLine textLine : textLineList) {
			textList.addAll(textLine.getSVGTextCharacters());
		}
		return textList;
	}
	
	public String toShortString() {
		String s = getLargestLine().toString();
		return s.substring(0, Math.min(20, s.length()));
	}
	
	public String toUnderscoreAndCaretString() {
		List<TextLine> parts = createSuscriptTextLineList();
		String result = "";
		for (TextLine part : parts) {
			String character = "";
			if (part == null) {
				continue;
			}
			if (part.getSuscript() == Suscript.SUB) {
				character = "_";
			} else if (part.getSuscript() == Suscript.SUP) {
				character = "^";
			}
			result = result + character + part.getLineContent() + character;
		}
		return result;
	}

	public List<ScriptWord> getScriptWordList() {
		List<ScriptWord> scriptWordList = new ArrayList<ScriptWord>();
		RealRangeArray rangeArray = getWordRangeArray();
		LOG.trace("WA "+rangeArray);
		List<SVGText> characters = getSVGTextCharacters();
		int rangeCounter = 0;
		int nlines = textLineList.size();
		IntArray lineCounterArray = new IntArray(nlines);
		// assume characters are sorted
		ScriptWord word = null;
		while (true) {
			SVGText character = null;
			RealRange currentRange = (rangeCounter >= rangeArray.size() ? null : rangeArray.get(rangeCounter));
			Double lowestX = Double.MAX_VALUE;//9999999.0;
			Integer lowestLine = null;
			SVGText lowestCharacter = null;
			for (int iline = 0; iline < nlines; iline++) {
				TextLine textLine = textLineList.get(iline);
				int lineCounter = lineCounterArray.elementAt(iline);
				character = (lineCounter >= textLine.size() ? null : textLine.get(lineCounter));
				if (character != null) {
					double x = character.getX();
					if (x < lowestX) {
						lowestX = x;
						lowestLine = iline;
						lowestCharacter = character;
					}
				}
			}
			LOG.trace((lowestCharacter == null ? "null" : "[" + lowestCharacter.getValue() + "_"+lowestCharacter.getX() + "/"+lowestX + "/"+lowestLine));
			if (currentRange == null || lowestX <= currentRange.getMax()) {
				if (word == null) {
					word = new ScriptWord(nlines);
					scriptWordList.add(word);
				}
				/*if (character == null) {
					break;
				}*/
				word.add(lowestCharacter, lowestLine);
				lineCounterArray.incrementElementAt(lowestLine);
			} else {
				word = null;
				rangeCounter++;
				//currentRange = (rangeCounter >= rangeArray.size() ? null : rangeArray.get(rangeCounter));
			}
			if (lowestLine == null && character == null) {
				break;
			}
		}
		return scriptWordList;
	}

	public RealRangeArray getWordRangeArray() {
		Double fontSize = getMeanFontSize();
		if (fontSize == null) {
			fontSize = 8.0; // just in case
		}
		RealRangeArray wordRangeArray = new RealRangeArray();
		List<SVGText> characters = getSVGTextCharacters();
		for (SVGText character : characters) {
			wordRangeArray.add(character.getBoundingBox().getXRange());
		}
		wordRangeArray.sort();
		wordRangeArray.extendRangesBy(X_CHARACTER_TOL);
		wordRangeArray.sortAndRemoveOverlapping();
		return wordRangeArray;
	}

	public List<SVGText> getSVGTextCharacters() {
		List<SVGText> characters = new ArrayList<SVGText>();
		for (TextLine textLine : textLineList) {
			characters.addAll(textLine.getSVGTextCharacters());
		}
		return characters;
	}

	public List<SVGText> getSortedCharacters() {
		List<SVGText> sortedCharacters = new ArrayList<SVGText>();
		int nline = textLineList.size();
		// make stacks of each textLine
		List<Stack<SVGText>> characterStackList = new ArrayList<Stack<SVGText>>(); 
		for (int i = 0; i < nline; i++) {
			Stack<SVGText> characterStack = new Stack<SVGText>();
			characterStackList.add(characterStack);
			TextLine textLine = textLineList.get(i);
			for (int j = textLine.size() - 1; j >= 0; j--) {
				SVGText character = textLine.get(j);
				characterStack.push(character);
			}
		}
		// find lowest character X
		while (true) {
			Double x = null;
			int lowestLine = -1;
			Double xmin = 99999.;
			for (int i = 0; i < nline; i++) {
				Stack<SVGText> characterStack = characterStackList.get(i);
				if (!characterStack.isEmpty()) {
					x = characterStack.peek().getX();
					if (x < xmin) {
						lowestLine = i;
						xmin = x;
					}
				}
			}
			if (x == null) {
				break;
			}
			Stack<SVGText> lowestXStack = characterStackList.get(lowestLine);
			SVGText text = lowestXStack.pop();
			sortedCharacters.add(text);
		}
		return sortedCharacters;
	}

	// FIXME move to StyleSpanContainer
	/** 
	 * Creates spans whenever any of the following change:
	 * <p>
	 * Bold, 
	 * italic, 
	 * fontSize, 
	 * yCoord, 
	 * fontName, 
	 * stroke, 
	 * fill
	 * @return
	 */
	public StyleSpans getStyleSpans() {
		if (styleSpans == null) {
			styleSpans = new StyleSpans();
			//List<SVGText> characters = getSVGTextCharacters();
			List<SVGText> characters = getSortedCharacters();
			StyleSpan currentSpan = null;
			boolean inBold = false;
			boolean inItalic = false;
			String currentFontName = null;
			Double currentFontSize = null;
			String currentFill = null;
			String currentStroke = null;
			Double currentY = null;
			Double lastX = null;
			SVGText character = null;
			String value = null;
			for (int i = 0; i < characters.size(); i++) {
				character = characters.get(i);
				LOG.trace(character.getValue()+" "+character.getX()+" "+character.getY()+" "+character.getFontSize());
				boolean bold = character.isBold();
				boolean italic = character.isItalic();
				String fontName = character.getSVGXFontName();
				String fill = character.getFill();
				String stroke = character.getStroke();
				Double fontSize = character.getFontSize();
				Double x = character.getX();
				Double y = character.getY();
				value = character.getText();
				if (lastX != null) {
					double deltaX = x - lastX;
					LOG.trace("   DX " +deltaX+" "+deltaX/fontSize);
					if (deltaX > getSpaceFactor() * fontSize) {
						insertComputedSpace(currentSpan, lastX, y, fontSize);
					}
				}
				// have any attributes changed?
				if (i == 0  || bold != inBold || italic != inItalic || 
						!areStringsEqual(currentFontName, fontName) ||
						!areStringsEqual(currentFill, fill) ||
						!areStringsEqual(currentStroke, stroke) ||
						!areDoublesEqual(currentFontSize, fontSize, FONT_SIZE_EPS) ||
						!areDoublesEqual(currentY, y, SUSCRIPT_EPS)
						) { 
					currentSpan = new StyleSpan(bold, italic);
					styleSpans.add(currentSpan);
					// sub/superscript
					if (currentFontSize != null && fontSize != null && fontSize < currentFontSize) {
						if (currentY - y > 1.0  ) {
							SVGUtil.setSVGXAttribute(character, SUSCRIPT, SUP);
						} else if (y - currentY > 1.0  ) {
							SVGUtil.setSVGXAttribute(character, SUSCRIPT, SUB);
						}
					}
					inBold = bold;
					inItalic = italic;
					currentFontName = fontName;
					currentFontSize = fontSize;
					currentFill = fill;
					currentStroke = stroke;
					currentY = y;
				}
				if (character != null && currentSpan != null) {
					currentSpan.addCharacter(character);
					Double width = character.getScaledWidth();
					if (width != null) {
						lastX = x + width;
						LOG.trace("   W "+width+" "+lastX);
					}
				}
			}
		}
		return styleSpans;
	}

	private Double getSpaceFactor() {
		return spaceFactor ;
	}

	private void insertComputedSpace(StyleSpan currentSpan, Double lastX, Double y, Double defaultFontSize) {
		Real2 xy = new Real2(lastX, y);
		if (new Real2(0., 0.).isEqualTo(xy, 0.0000001)) {
			throw new RuntimeException("Suspicious space at "+xy);
		}
		SVGText space = new SVGText(xy, " ");
		Double fontSize = currentSpan.getFontSize();
		if (fontSize == null) {
//			throw new RuntimeException("Null fontSize "+currentSpan);
			fontSize = defaultFontSize;
		}
		space.setFontSize(fontSize);
		currentSpan.addCharacter(space);
	}

	private boolean areStringsEqual(String s0, String s1) {
		return (s0 == null && s1 == null) || 
			(s0 != null && s0.equals(s1)) ||
			(s1 != null && s1.equals(s0));
			   
	}
	
	private boolean areDoublesEqual(Double d0, Double d1, Double eps) {
		return d0 != null && d1 != null && Real.isEqual(d0, d1, eps);
			   
	}

	public Real2Range getBoundingBox() {
		Real2Range boundingBox = null;
		for (TextLine textLine : textLineList) {
			Real2Range r2r = textLine.getBoundingBox();
			boundingBox = (boundingBox == null) ? r2r : boundingBox.plus(r2r);
		}
		return boundingBox;
	}

	public Double getLeftMargin() {
		Real2Range bbox = getBoundingBox();
		Double leftMargin = null;
		if (bbox != null) {
			RealRange range = bbox.getXRange();
			if (range != null) {
				leftMargin = range.getMin();
			}
		}
		return leftMargin;
	}

	public Double getRightMargin() {
		Real2Range bbox = getBoundingBox();
		Double rightMargin = null;
		if (bbox != null) {
			RealRange range = bbox.getXRange();
			if (range != null) {
				rightMargin = range.getMax();
			}
		}
		return rightMargin;
	}

//	public Double getRightMargin() {
//		Real2Range bbox = getBoundingBox();
//		return bbox == null ? null : bbox.getXMax();
//	}

	public boolean endsWithPeriod() {
		int len = getTextContentWithSpaces().length();
		return len > 0 && textContentWithSpaces.charAt(len - 1) == '.';
	}

	/** indent of lastCharacter in leftwards direction, so normally >= 0
	 * 
	 * @param xRange
	 * @return
	 */
	public Double getRightIndent(RealRange xRange) {
		Double rightMargin = getRightMargin();
		return rightMargin == null || xRange == null ? null : xRange.getMax() - rightMargin; 
	}
	
	/** indent of lastCharacter in rightwards direction, so normally >= 0
	 * 
	 * @param xRange
	 * @return
	 */
	public Double getLeftIndent(RealRange xRange) {
		Double leftMargin = getLeftMargin();
		return leftMargin == null || xRange == null ? null : leftMargin - xRange.getMin(); 
	}
	
	public String getTextContentWithSpaces() {
		if (textContentWithSpaces == null) {
			getStyleSpans();
			textContentWithSpaces = styleSpans.getTextContentWithSpaces();
//			StringBuilder sb = new StringBuilder();
//			textContentWithSpaces = sb.toString();
		}
		return textContentWithSpaces;
	}

//	private StyleSpans ensureStyleSpans() {
//		if (styleSpans == null) {
//			styleSpans = new StyleSpans();
//		}
//		return styleSpans;
//	}

	/** uses indent and/or chunk of bold as heuristics
	 * 
	 * @param xRange
	 * @return
	 */
	public boolean indentCouldStartParagraph(RealRange xRange) {
		boolean couldStart = false;
		Double leftIndent = getLeftIndent(xRange);
		couldStart = leftIndent != null && leftIndent > LEFT_INDENT_MIN;
		return couldStart;
	}

	/** uses bold as heuristics
	 * 
	 * @param xRange
	 * @return
	 */
	public boolean endsWithBoldSpan() {
		StyleSpan lastStyleSpan = getLastStyleSpan();
		return lastStyleSpan != null && lastStyleSpan.isBold();
	}

	/** uses bold as heuristics
	 * 
	 * @param xRange
	 * @return
	 */
	public boolean startsWithBoldSpan() {
		StyleSpan firstStyleSpan = getFirstStyleSpan();
		return firstStyleSpan != null && firstStyleSpan.isBold();
	}

	public StyleSpan getFirstStyleSpan() {
		getStyleSpans();
		return (styleSpans == null || styleSpans.size() == 0) ? null : styleSpans.get(0);
	}

	public StyleSpan getLastStyleSpan() {
		getStyleSpans();
		return (styleSpans == null || styleSpans.size() == 0) ? null : styleSpans.get(styleSpans.size() - 1);
	}

	public boolean couldEndParagraph(RealRange xRange) {
		Double rightIndent = getRightIndent(xRange);
		return rightIndent != null && (rightIndent > RIGHT_INDENT_MIN) && endsWithPeriod();
	}

	
}
