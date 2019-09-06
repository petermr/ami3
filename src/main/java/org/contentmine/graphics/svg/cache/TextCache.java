package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGTextComparator;
import org.contentmine.graphics.svg.StyleAttributeFactory;
import org.contentmine.graphics.svg.fonts.StyleRecord;
import org.contentmine.graphics.svg.fonts.StyleRecordFactory;
import org.contentmine.graphics.svg.fonts.StyleRecordSet;
import org.contentmine.graphics.svg.normalize.TextDecorator;
import org.contentmine.graphics.svg.plot.AnnotatedAxis;
import org.contentmine.graphics.svg.text.SVGTextLine;
import org.contentmine.graphics.svg.text.SVGTextLineList;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/** extracts texts within graphic area.
 * 
 * @author pm286
 *
 */
public class TextCache extends AbstractCache {
	
	private static final int MIN_DIFF_TEXT = 10;
	static final Logger LOG = Logger.getLogger(TextCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	/** not sure what these are for ... */
	
	private static final char BLACK_VERTICAL_RECTANGLE = (char)0x25AE;
	private static final char WHITE_VERTICAL_RECTANGLE = (char)0x25AF;
	private static final char WHITE_SQUARE = (char)0x25A1;

	private List<SVGText> horizontalTexts;
	private List<SVGText> verticalTexts;

	private List<SVGText> originalTextList;
	private Multiset<String> horizontalTextStyleMultiset;
	private Multiset<String> verticalTextStyleMultiset;
	private boolean useCompactOutput;
	private List<StyleAttributeFactory> mainStyleAttributeFactoryList;
	private List<StyleAttributeFactory> derivativeStyleAttributeFactoryList;
	private List<Multiset.Entry<String>> sortedHorizontalStyles;
	private List<StyleAttributeFactory> totalAttributeFactoryList;
	// parameters
	private int maxStylesInRow = 5;
	private List<SVGText> currentTextList;
	private StyleRecordFactory styleRecordFactory;
	private List<StyleRecord> sortedStyleRecords;
	private StyleRecordSet styleRecordSet;
	private Multimap<Double, SVGText> horizontalTextsByYCoordinate;
	private Multimap<Double, SVGText> horizontalTextsByFontSize;
	private int coordinateDecimalPlaces = 1;
	SVGTextLineList textLineListForLargestFont;
	private StyleRecordSet horizontalStyleRecordSet;
	private List<SVGText> horizontalTextListSortedY;
	Double largestCurrentFont;
	SVGTextLineList textLines;
	SVGTextLineList processedTextLines;
	private Stack<TextLineFormatter> lineFormatterStack;
	private SuscriptFormatter suscriptFormatter;

	
	public TextCache(ComponentCache svgCache) {
		super(svgCache);
		init();
	}

	private void init() {
		this.lineFormatterStack = new Stack<TextLineFormatter>();
		lineFormatterStack.push(TextLineFormatter.createDefaultFormatter(this));
		setSuscriptFormatter(new SuscriptFormatter());
	}

	private void clearVariables() {
		ownerComponentCache.allElementList = null;
		ownerComponentCache.boundingBoxList = null;
		this.horizontalTexts = null;
		this.horizontalTextStyleMultiset = null;
		this.verticalTexts = null;
		this.verticalTextStyleMultiset = null;
	}

	/** create texts from element.
	 * filter through SVGText.removeTextsWithEmptyContent and 
	 * createCompactedOutout
	 * @param svgElement
	 */
	public void extractTexts(AbstractCMElement svgElement) {
		if (svgElement != null) {
			originalTextList = SVGText.extractSelfAndDescendantTexts(svgElement);
//			LOG.debug("original "+originalTextList.size());
			ingestOriginalTextList();
		}
	}

	public void ingestOrginalTextList(List<SVGText> textList) {
		this.originalTextList = textList;
		ingestOriginalTextList();
	}
	
	void ingestOriginalTextList() {
		originalTextList = SVGText.removeTextsWithEmptyContent(originalTextList, ownerComponentCache == null ? true : ownerComponentCache.isRemoveWhitespace());
		formatCoordinates(originalTextList, coordinateDecimalPlaces);
		if (useCompactOutput) {
			createCompactedTextsAndReplace();
		}
		if (ownerComponentCache != null) {
			ownerComponentCache.addElementsToExtractedElement(this.getOrCreateOriginalTextList());
		}
		this.createHorizontalAndVerticalTexts();

	}

	public static void formatCoordinates(List<SVGText> textList, int decimalPlaces) {
		if (textList != null) {
			for (int i = 0; i < textList.size(); i++) {
				SVGText text = textList.get(i);
				text.format(decimalPlaces);
			}
		}
	}

	public List<SVGText> getOrCreateOriginalTextList() {
		if (originalTextList == null) {
			if (inputSVGElement != null) {
				extractTexts(inputSVGElement);
			}
		}
		return originalTextList;
	}
	
	public List<? extends SVGElement> getOrCreateElementList() {
		if (originalTextList == null) {
			originalTextList = new ArrayList<SVGText>();
		}
		originalTextList = getOrCreateOriginalTextList();
		return originalTextList;
	}

	/** 
	 * NYI
	 * 
	 * @param outFilename
	 * @return
	 * @deprecated("Not yet implemented")
	 */
	public AbstractCMElement debug(String outFilename) {
		AbstractCMElement g = new SVGG();
		LOG.debug("TextCache.debug(); NYI");
//		// derived
//		appendDebugToG(g, originalTextList,"yellow",  "black", 0.3, 10.0, "Helvetica");
//		appendDebugToG(g, nonNegativeYTextList, "red", "black", 0.3, 12.0, "serif");
//		appendDebugToG(g, nonNegativeNonEmptyTextList, "green", "black", 0.3, 14.0, "monospace");
//		drawBox(g, "green", 2.0);
//
//		writeDebug("texts",outFilename, g);
		return g;
	}

	private void appendDebugToG(AbstractCMElement g, List<? extends SVGElement> elementList, String stroke, String fill, double opacity, double fontSize, String fontFamily) {
		for (AbstractCMElement e : elementList) {
			SVGText text = (SVGText) e.copy();
			text.setCSSStyleAndRemoveOldStyle(null);
			text.setStroke(stroke);
			text.setStrokeWidth(0.4);
			text.setFill(fill);
			text.setFontSize(fontSize);
			text.setFontFamily(fontFamily);
			text.setOpacity(opacity);
			String s = text.getValue();
			if (text.isRot90()) {
				Real2Range box = text.getBoundingBox();
				SVGRect box0 = SVGElement.createGraphicalBox(box, 0.0, 0.0);
				box0.setStrokeWidth(0.1);
				box0.setFill("cyan");
				box0.setOpacity(0.2);
				g.appendChild(box0);
			}
			if (s == null || s.equals("null")) {
				text.setText(String.valueOf(WHITE_SQUARE));
				text.setFill("cyan");
			} else if ("".equals(s)) {
				text.setText(String.valueOf(BLACK_VERTICAL_RECTANGLE));
				text.setFill("pink");
			} else if ("".equals(s.trim())) {
				text.setText(String.valueOf(BLACK_VERTICAL_RECTANGLE));
				text.setFill("cyan");
			} else {
				addAnnotationRect(g, text);
			}
			String title = s == null || "".equals(s.trim()) ? "empty" : s;
			text.addTitle(title);
			g.appendChild(text);
		}
	}

	private void addAnnotationRect(AbstractCMElement g, SVGText text) {
		Real2Range box = text.getBoundingBox();
		SVGRect box0 = SVGElement.createGraphicalBox(box, 0.0, 0.0);
		box0.setStrokeWidth(0.1);
		box0.setFill("yellow");
		box0.setOpacity(0.2);
		g.appendChild(box0);
	}
	
	/** the bounding box of the actual text components
	 * The extent of the context (e.g. svgCache) may be larger
	 * @return the bounding box of the contained text
	 */
	public Real2Range getBoundingBox() {
		return getOrCreateBoundingBox(originalTextList);
	}

	public void createHorizontalAndVerticalTexts() {
		getOrCreateHorizontalTexts();
		getOrCreateVerticalTexts();
	}
	public List<SVGText> getOrCreateHorizontalTexts() {
		if (horizontalTexts == null) {
			horizontalTexts = SVGText.findHorizontalOrRot90Texts(originalTextList, LineDirection.HORIZONTAL, AnnotatedAxis.EPS);
			if (horizontalTexts == null) {
				horizontalTexts = new ArrayList<SVGText>();
			}
		}
		return horizontalTexts;
	}

	public List<SVGText> getOrCreateVerticalTexts() {
		if (verticalTexts == null) {
			verticalTexts = SVGText.findHorizontalOrRot90Texts(originalTextList, LineDirection.VERTICAL, AnnotatedAxis.EPS);
		}
		return verticalTexts;
	}

	public Multiset<String> getOrCreateHorizontalTextStyleMultiset() {
		if (horizontalTextStyleMultiset == null) {
			horizontalTextStyleMultiset = getTextStyleMultiset(getOrCreateHorizontalTexts());
		}
		return horizontalTextStyleMultiset;
	}
	
	public Multiset<String> getOrCreateVerticalTextStyleMultiset() {
		if (verticalTextStyleMultiset == null) {
			verticalTextStyleMultiset = getTextStyleMultiset(getOrCreateVerticalTexts());
		}
		return verticalTextStyleMultiset;
	}
	
	private Multiset<String> getTextStyleMultiset(List<SVGText> texts) {
		Multiset<String> styleSet = HashMultiset.create();
		for (SVGText text : texts) {
			String style = text.getStyle();
			style = style.replaceAll("clip-path\\:url\\(#clipPath\\d+\\);", "");
			styleSet.add(style);
		}
		return styleSet;
	}

	/** replaces long form o style by abbreviations.
	 * remove clip-paths
	 * Remove String values and attributes
	 * "font-family, Helvetica, font-weight, normal,font-size, px, font-style, #fff(fff), stroke, none, fill"
	 * 
	 * resultant string is of form:
	 * color (optional)
	 * ~ // serif (optional)
	 * ddd // font-size x 10 (mandatory)
	 * B // bold (optional
	 * I // italic (optional
	 * stroke (optional)
	 * 
	 * colors are flattened to hex hex hex
	 * color abbreviations (with some tolerances)
	 * . grey
	 * * black
	 * r g b
	 * 
	 * @return
	 */
	public Multiset<String> createAbbreviatedHorizontalTextStyleMultiset() {
		getOrCreateHorizontalTextStyleMultiset();
		Multiset<String> abbreviatedStyleSet = HashMultiset.create();
		for (Multiset.Entry<String> entry : horizontalTextStyleMultiset.entrySet()) {
			int count = entry.getCount();
			String style = entry.getElement();
			style = abbreviateStyle(style);
			abbreviatedStyleSet.add(style, count);
		}
		return abbreviatedStyleSet;
	}
	
	/** replaces long form of style by abbreviations.
	 * remove clip-paths
	 * Remove String values and attributes
	 * "font-family, Helvetica, font-weight, normal,font-size, px, font-style, #fff(fff), stroke, none, fill"
	 * 
	 * resultant string is of form:
	 * color (optional)
	 * ~ // serif (optional)
	 * ddd // font-size x 10 (mandatory)
	 * B // bold (optional
	 * I // italic (optional
	 * stroke (optional)
	 * 
	 * colors are flattened to hex hex hex
	 * color abbreviations (with some tolerances)
	 * . grey
	 * * black
	 * r g b
	 * 
	 * @return
	 */

	public static String abbreviateStyle(String style) {
		style = style.replaceAll("font-family:", "");
		style = style.replaceAll("TimesNewRoman;", "~");
		style = style.replaceAll("Helvetica;", "");
		style = style.replaceAll("font-weight:", "");
		style = style.replaceAll("normal;", "");
		style = style.replaceAll("bold;", "B");
		style = style.replaceAll("font-size:", "");
		style = style.replaceAll("(\\d+)\\.(\\d)\\d*", "$1$2");
		style = style.replaceAll("px;", "");
		style = style.replaceAll("font-style:", "");
		style = style.replaceAll("italic;", "I");
		style = style.replaceAll("#(.)(.)(.)(.)(.)(.);", "#$1$3$5;"); // compress rgb
		style = style.replaceAll("#000;", "*");
		style = style.replaceAll("#fff;", "");
		style = style.replaceAll("#[12][12][12];", "."); // grey
		style = style.replaceAll("#[012][012][cdef];", "b");
		style = style.replaceAll("#[012][cdef][012];", "g");
		style = style.replaceAll("#[cdef][012][012];", "r");
		style = style.replaceAll("stroke:", "");
		style = style.replaceAll("none;", "");
		style = style.replaceAll("fill:", "");
		style = style.replaceAll("clip-path\\:url\\(#clipPath\\d+\\);", "");
		return style;
	}

	public SVGG createCompactedTextsAndReplace() {

		TextDecorator textDecorator = new TextDecorator();
		SVGG g = textDecorator.compactTexts(originalTextList);
		originalTextList = SVGText.extractSelfAndDescendantTexts(g);
		clearVariables();
		return g;
	}

	public void setUseCompactOutput(boolean b) {
		this.useCompactOutput = b;
	}

	/** defaults to ComponentCache.MAJOR_COLORS.
	 * 
	 * @return
	 */
	public AbstractCMElement createColoredTextStyles() {
		return createColoredTextStyles(ComponentCache.MAJOR_COLORS);
	}

	/** not yet finished.
	 * will links derivative styles (bold, italic) to main style
	 * 
	 * @param color
	 * @return
	 */
	public AbstractCMElement createColoredTextStyles(String[] color) {
		List<SVGText> horTexts = getOrCreateOriginalTextList();
		Multiset<String> horizontalStyleSet = getOrCreateHorizontalTextStyleMultiset();
		createAttributeFactoryLists(horizontalStyleSet);
		createMainAndDerivativeFactpryLists();
		linkDerivativeToMainStyles(mainStyleAttributeFactoryList, derivativeStyleAttributeFactoryList);
		AbstractCMElement g = createAnnotatedTextArea(color, horTexts, sortedHorizontalStyles);
		return g;
	}

	private void createMainAndDerivativeFactpryLists() {
		mainStyleAttributeFactoryList = new ArrayList<StyleAttributeFactory>();
		derivativeStyleAttributeFactoryList = new ArrayList<StyleAttributeFactory>();
		for (StyleAttributeFactory attributeFactory : totalAttributeFactoryList) {
			if (!attributeFactory.isBold() && !attributeFactory.isItalic()) {
				mainStyleAttributeFactoryList.add(attributeFactory);
			} else {
				derivativeStyleAttributeFactoryList.add(attributeFactory);
			}
		}
	}

	private void createAttributeFactoryLists(Multiset<String> horizontalStyleSet) {
		sortedHorizontalStyles = MultisetUtil.createListSortedByCount(horizontalStyleSet);
		totalAttributeFactoryList = new ArrayList<StyleAttributeFactory>();
		for (Multiset.Entry<String> entry : sortedHorizontalStyles) {
			String style = entry.getElement();
			StyleAttributeFactory attributeFactory = new StyleAttributeFactory(style);
			totalAttributeFactoryList.add(attributeFactory);
		}
	}

	private void linkDerivativeToMainStyles(List<StyleAttributeFactory> mainStyleAttributeFactoryList,
			List<StyleAttributeFactory> derivativeStyleAttributeFactoryList) {
		if (derivativeStyleAttributeFactoryList.size() > 0) {
			for (StyleAttributeFactory nonNormalAttributeFactory : derivativeStyleAttributeFactoryList) {
				for (StyleAttributeFactory normalAttributeFactory : mainStyleAttributeFactoryList) {
					if (nonNormalAttributeFactory.isBoldOrItalicSuperset(normalAttributeFactory)) {
						// FIXME add code to capture this and link to main style
						LOG.trace("Contains: "+nonNormalAttributeFactory+"; "+normalAttributeFactory);
					}
				}
			}
		}
	}

	private AbstractCMElement createAnnotatedTextArea(String[] color, List<SVGText> horTexts,
			List<Multiset.Entry<String>> sortedHorizontalStyles) {
		AbstractCMElement g = new SVGG();
		for (SVGText horText : horTexts) {
			String style = horText.getStyle();
			for (int i = 0; i < sortedHorizontalStyles.size(); i++) {
				Multiset.Entry<String> entry = sortedHorizontalStyles.get(i);
				if (entry.getElement().equals(style)) {
					SVGText horText1 = (SVGText) horText.copy();
					SVGRect rect = SVGRect.createFromReal2Range(horText1.getBoundingBox());
					rect.setCSSStyle("fill:"+color[i % color.length]+";"+"opacity:0.5;");
					rect.addTitle(style);
					g.appendChild(rect);
					g.appendChild(horText1);
					break;
				}
			}
		}
		return g;
	}

	public List<String> createRowOfStyles(Multiset<String> styleSet) {
		List<Multiset.Entry<String>> entryList = MultisetUtil.createListSortedByCount(styleSet);
		List<String> row = new ArrayList<String>(); 
		// limit number of styles
		int entryCount = entryList.size();
		int filled = Math.min(entryCount, maxStylesInRow);
		int empty = Math.max(0, maxStylesInRow - entryCount);
		for (int i = 0; i < filled; i++) {
			Multiset.Entry<String> entry = entryList.get(i);
			row.add(entry.getElement());
			row.add(String.valueOf(entry.getCount()));
		}
		// fill jagged rows
		for (int i = 0; i < empty; i++) {
			row.add("");
			row.add("");
		}
		return row;
	}

	/** styles to output in row of styles.
	 * 
	 * @return
	 */
	public int getMaxStylesInRow() {
		return maxStylesInRow;
	}

	public void setMaxStylesInRow(int maxStylesInRow) {
		this.maxStylesInRow = maxStylesInRow;
	}

	@Override
	public String toString() {
		String s = ""
			+ "hor: "+horizontalTexts.size()+"; "
			+ "vert: "+verticalTexts.size()+"; "
			+ "textList "+originalTextList.size();
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		horizontalTexts = null;
		verticalTexts = null;

		originalTextList = null;
		horizontalTextStyleMultiset = null;
		verticalTextStyleMultiset = null;
		useCompactOutput = false;
		mainStyleAttributeFactoryList = null;
		derivativeStyleAttributeFactoryList = null;
		sortedHorizontalStyles = null;
		totalAttributeFactoryList = null;
	}

	/** creates a sublist of texts withn a cropBox.
	 * results are held in currentTextList;
	 * 
	 * @param cropBox
	 * @return
	 */
	public List<SVGText> extractCurrentTextElementsContainedInBox(Real2Range cropBox) {
		getOrCreateOriginalTextList();
		List<SVGText> textListCopy = new ArrayList<SVGText>(originalTextList);
		currentTextList = SVGText.extractTexts(
				SVGElement.extractElementsContainedInBox(textListCopy, cropBox));
		return currentTextList;
	}

//	/** hold a selection of the textList.
//	 * does not affect originalTextList.
//	 * 
//	 * @return
//	 */
//	private List<SVGText> getCurrentTextList() {
//		return currentTextList;
//	}

	public void setCurrentTextList(List<SVGText> currentTextList) {
		this.currentTextList = currentTextList;
	}

	public List<StyleRecord> createSortedStyleRecords() {
		getOrCreateStyleRecordFactory();
		getOrCreateCurrentTextList();
		styleRecordSet = styleRecordFactory.createStyleRecordSet(currentTextList);
		sortedStyleRecords = getStyleRecordSet().createSortedStyleRecords();
		return sortedStyleRecords;
	}

	/** get currentTextList.
	 * if null, copies originalTextList
	 * 
	 * @return
	 */
	public List<SVGText> getOrCreateCurrentTextList() {
		if (currentTextList == null) {
			if (originalTextList != null) {
				currentTextList = new ArrayList<SVGText>(originalTextList);
			}
		}
		return originalTextList;
	}

	public StyleRecordFactory getOrCreateStyleRecordFactory() {
		if (styleRecordFactory == null) {
			styleRecordFactory = new StyleRecordFactory();
		}
		return styleRecordFactory;
	}

	public StyleRecordSet getStyleRecordSet() {
		return styleRecordSet;
	}

	public SVGG extractYcoordAPs(Real2Range cropBox) {
		SVGG g = new SVGG();
		List<SVGText> texts = extractCurrentTextElementsContainedInBox(cropBox);
		List<StyleRecord> sortedStyleRecords = createSortedStyleRecords();
		LOG.trace(sortedStyleRecords.size());
		String stroke[] = {"red", "green", "blue", "black"};
		String fill[] = {"cyan", "magenta", "yellow", "pink"};
		for (int i = 0; i < sortedStyleRecords.size(); i++) {
			StyleRecord styleRecord = sortedStyleRecords.get(i);
			SVGG gg = styleRecord.getSortedCompressedYCoordAPGrid(
					cropBox.getXRange(), stroke[i % stroke.length], fill[i % fill.length], 0.2);
			LOG.trace(styleRecord.createSortedCompressedYCoordAPList(0.2));
			LOG.trace(styleRecord.getCSSStyle());
			g.appendChild(gg);
		}
		return g;
	}

	public SVGG extractStyledTextBBoxes(Real2Range cropBox) {
		SVGG g = new SVGG();
		List<SVGText> texts = extractCurrentTextElementsContainedInBox(cropBox);
		List<StyleRecord> sortedStyleRecords = createSortedStyleRecords();
		LOG.trace(sortedStyleRecords.size());
		for (int i = 0; i < sortedStyleRecords.size(); i++) {
			StyleRecordSet leftStyleRecordSet = getStyleRecordSet();
			SVGElement gg = leftStyleRecordSet.createStyledTextBBoxes(texts);
			gg.setId("text boxes");
			g.appendChild(gg);
		}
		return g;
	}

	public Multimap<Double, SVGText> getOrCreateHorizontalTextsByYCoordinate() {
		if (horizontalTextsByYCoordinate == null) {
			horizontalTextsByYCoordinate = ArrayListMultimap.create();
			getOrCreateCurrentTextList();
			if (horizontalTexts == null) {
				throw new RuntimeException("null horizontal texts");
			}
			for (SVGText text : horizontalTexts) {
				Double y = text.getY();
				horizontalTextsByYCoordinate.put(y, text);
			}
		}
		return horizontalTextsByYCoordinate;
	}

	public Multimap<Double, SVGText> getOrCreateHorizontalTextsByFontSize() {
		if (horizontalTextsByFontSize == null) {
			horizontalTextsByFontSize = ArrayListMultimap.create();
			getOrCreateCurrentTextList();
			for (SVGText text : horizontalTexts) {
				Double y = text.getFontSize();
				horizontalTextsByFontSize.put(y, text);
			}
		}
		return horizontalTextsByFontSize;
	}

	public SVGTextLineList getTextLinesForFontSize(Double fontSize) {
		List<SVGTextLine> textLineList = new ArrayList<SVGTextLine>();
		Multimap<Double, SVGText> textByYCoord = getOrCreateHorizontalTextsByYCoordinate();
		List<Double> yCoords =  new ArrayList<Double>(textByYCoord.keySet());
		Collections.sort(yCoords);
		for (Double y : yCoords) {
			List<SVGText> lineTexts = new ArrayList<SVGText>(textByYCoord.get(y));
			LOG.trace(">>"+lineTexts);
			SVGTextLine textLine = new SVGTextLine(lineTexts);
			Double commonFontSize = textLine.getOrCreateCommonFontSize();
			if (fontSize.equals(commonFontSize)) {
				textLineList.add(textLine);
				LOG.trace("ADD "+textLine);
			}
		}
		textLineListForLargestFont = new SVGTextLineList(textLineList);
		return textLineListForLargestFont;
	}
	
	

	/** get the text as lines of different Y.
	 * 
	 * @return
	 */
	public SVGTextLineList getOrCreateTextLines() {
		if (textLines == null) {
			List<SVGTextLine> textLineList = new ArrayList<SVGTextLine>();
			Multimap<Double, SVGText> textByYCoord = getOrCreateHorizontalTextsByYCoordinate();
			List<Double> yCoords =  new ArrayList<Double>(textByYCoord.keySet());
			Collections.sort(yCoords);
			for (Double y : yCoords) {
				List<SVGText> lineTexts = new ArrayList<SVGText>(textByYCoord.get(y));
				SVGTextLine textLine = new SVGTextLine(lineTexts);
				textLineList.add(textLine);
			}
			textLines = new SVGTextLineList(textLineList);
		}
		return textLines;
	}

	public int getCoordinateDecimalPlaces() {
		return coordinateDecimalPlaces;
	}

	public void setCoordinateDecimalPlaces(int decimalPlaces) {
		this.coordinateDecimalPlaces = decimalPlaces;
	}

	public SVGTextLineList getTextLinesForLargestFont() {
		// assume that y-coords will be the most important structure
		StyleRecordSet styleRecordSet = getOrCreateHorizontalStyleRecordSet();
		largestCurrentFont = styleRecordSet.getLargestFontSize();
		textLineListForLargestFont = this.getTextLinesForFontSize(largestCurrentFont);
		return textLineListForLargestFont;
	}

	public StyleRecordSet getOrCreateHorizontalStyleRecordSet() {
		if (horizontalStyleRecordSet == null) {
			List<SVGText> orCreateHorizontalTextListSortedY = getOrCreateHorizontalTextListSortedY();
			horizontalStyleRecordSet = new StyleRecordFactory().createStyleRecordSet(orCreateHorizontalTextListSortedY);
		}
		return horizontalStyleRecordSet;
	}

	public List<SVGText> getOrCreateHorizontalTextListSortedY() {
		if (horizontalTextListSortedY == null) {
			horizontalTextListSortedY = this.getOrCreateHorizontalTexts();
			if (horizontalTextListSortedY == null) {
				horizontalTextListSortedY = new ArrayList<SVGText>();
			}
			Collections.sort(horizontalTextListSortedY, new SVGTextComparator(SVGTextComparator.TextComparatorType.Y_COORD));
		}
		return horizontalTextListSortedY;
	}
	
	public List<SVGTextLine> getTextLinesForMinorFontSizes() {
		LOG.error("getTextLinesForMinorFontSizes NYI");
		return null;
	}

	public List<Double> getMinorFontSizes() {
		StyleRecordSet horizontalStyleRecordSet =
				getOrCreateHorizontalStyleRecordSet();
		List<Double> minorFontSizes = horizontalStyleRecordSet.getMinorFontSizes();
		return minorFontSizes;
	}

	public Double getLargestCurrentFont() {
		return largestCurrentFont;
	}

	public SVGTextLineList getProcessedTextLines() {
		return processedTextLines;
	}

	public HtmlElement createHtmlElement() {
		TextLineFormatter lineFormatter = getCurrentLineFormatter();
		if (processedTextLines != null) {
			lineFormatter.setTextLines(processedTextLines);
			for (SVGTextLine textLine : processedTextLines) {
				HtmlSpan lineSpan = textLine.createLineSpan(SVGTextLine.COLUMN_SPAN);
				lineFormatter.appendLine(lineSpan);
			}
		}
		HtmlElement htmlElement = lineFormatter.createHtmlElement();
		return htmlElement;
		
	}

	public HtmlElement createHtmlElementNew() {
		HtmlElement htmlElement = horizontalTexts == null ? null : TextCache.createHtmlFromTexts(horizontalTexts);
		return htmlElement;
		
	}

	public TextLineFormatter getCurrentLineFormatter() {
		TextLineFormatter currentLineFormatter = this.lineFormatterStack.peek();
		if (currentLineFormatter.getTextCache() == null) {
			currentLineFormatter.setTextCache(this);
		}
		return currentLineFormatter;
	}

	public HtmlElement createHtmlFromBox(RealRange xr, RealRange yr) {
		Real2Range cropBox = new Real2Range(xr, yr); 
		List<SVGText> textLines = extractCurrentTextElementsContainedInBox(cropBox);
		return createHtmlFromTexts(textLines);
	}

	public static HtmlElement createHtmlFromTexts(List<SVGText> textLines) {
		TextCache textCache1 = new TextCache(null);
		textCache1.ingestOrginalTextList(textLines);
		TextLineFormatter currentLineFormatter = textCache1.getCurrentLineFormatter();
		SVGTextLineList textLineList = currentLineFormatter.addSuscriptsAndJoinWrappedLines();
		HtmlElement htmlElement = textCache1.createHtmlElement();
		return htmlElement;
	}

	/** pushes new LineFormatter and saves current one in stack.
	 * 
	 * @param lineFormatter
	 */
	public void pushFormatter(TextLineFormatter lineFormatter) {
		lineFormatterStack.push(lineFormatter);
	}

	/** clear current LineFormatter and restore previous from stack.
	 * 
	 * if stack has 1 element, ignore
	 */
	public void popFormatter() {
		if (lineFormatterStack.size() > 1) {
			lineFormatterStack.pop();
		}
	}
	
	public SuscriptFormatter getSuscriptFormatter() {
		return suscriptFormatter;
	}

	public void setSuscriptFormatter(SuscriptFormatter suscriptFormatter) {
		this.suscriptFormatter = suscriptFormatter;
	}



}
