package org.contentmine.graphics.svg.text.structure;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;
import org.contentmine.graphics.svg.text.TextAnalyzerUtils;
import org.contentmine.graphics.svg.text.TextCoordinate;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.TextChunkList;
import org.contentmine.graphics.svg.text.build.TextUtil;
import org.contentmine.graphics.svg.text.line.ScriptLine;
import org.contentmine.graphics.svg.text.line.SuscriptEditor;
import org.contentmine.graphics.svg.text.line.TabbedTextLine;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.line.TextLineSet;
import org.contentmine.graphics.svg.text.structure.TextAnalyzer.TextOrientation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** 
 * Holds text lines in order
 * to simplify TextAnalyzer
 * 
 * @author pm286
 */
public class TextStructurer {

	private static final Logger LOG = Logger.getLogger(TextStructurer.class);

	/** 
	 * Used for splitting between lineGroups
	 * 
	 * @author pm286
	 */
	public enum Splitter {
		BOLD,
		FONTSIZE,
		FONTFAMILY,
	}

	public static Normalizer.Form NORMALIZE_FORM = Normalizer.Form.NFKC;
	
	Pattern NUMBER_ITEM_PATTERN = Pattern.compile("^\\s*[\\[\\(]?\\s*(\\d+)\\s*\\.?[\\]\\)]?\\.?\\s*.*");
	
	/** 
	 * Default ratio for "isLargerThan"
	 * */
	public static final double LARGER_FONT_SIZE_RATIO = 1.02;

	private static final double Y_EPS = 0.5; // line can wobble 

	private TextAnalyzer textAnalyzer;
	
	private List<TextLine> linesWithCommonestFont;
	private List<TextLine> linesWithLargestFont;
	private List<TextLine> textLineList;
	private TextCoordinate largestFontSize;
	private TextCoordinate commonestFontSize;
	private Real2Range textLinesLargetFontBoundingBox;
	private Set<TextCoordinate> fontSizeSet;

	private Multiset<String> fontFamilySet;
	private List<Double> actualWidthsOfSpaceCharactersList;
	private Map<TextLine, Integer> textLineSerialMap;
	private List<String> textLineContentList;

	private RealArray interTextLineSeparationArray;
	private RealArray meanFontSizeArray;
	private Multiset<Double> separationSet;
	private Map<Integer, TextLine> textLineByYCoordMap;
	private RealArray textLineCoordinateArray;
	private Multimap<TextCoordinate, TextLine> textLineListByFontSize;

	private List<Real2Range> textLineChunkBoxes;

	private List<ScriptLine> initialScriptLineList;
	private List<TextLine> commonestFontSizeTextLineList;
	private List<ScriptLine> scriptedLineList;
	private HtmlElement createdHtmlElement;
	private SVGElement svgChunk;

	private Real2Range boundingBox;
	private ScriptContainer scriptContainer;
	private HtmlElement htmlElement;
	private List<SVGText> rawCharacters;
	private TextOrientation textOrientation;

	private List<RawWords> rawWordsList;
	private TextChunkList textChunkList;
	private List<TextLine> subscriptLineList;
	private List<TextLine> superscriptLineList;

	private boolean rotatable;

	private boolean omitShapeList = true;

	public TextStructurer() {
		this(new TextAnalyzer((List<SVGText>) null));
	}
	
	/** shallow copy constructor
	 * 
	 */
	public TextStructurer(TextStructurer textStructurer) {
		
        this.textAnalyzer = textStructurer.textAnalyzer ;
        
        this.linesWithCommonestFont = textStructurer.linesWithCommonestFont ;
        this.linesWithLargestFont = textStructurer.linesWithLargestFont ;
        this.textLineList = textStructurer.textLineList ;
        this.largestFontSize = textStructurer.largestFontSize ;
        this.commonestFontSize = textStructurer.commonestFontSize ;
        this.textLinesLargetFontBoundingBox = textStructurer.textLinesLargetFontBoundingBox ; 
        this.fontSizeSet = textStructurer.fontSizeSet ;

        this.fontFamilySet = textStructurer.fontFamilySet ;
        this.actualWidthsOfSpaceCharactersList = textStructurer.actualWidthsOfSpaceCharactersList ;
        this.textLineSerialMap = textStructurer.textLineSerialMap ;
        this.textLineContentList = textStructurer.textLineContentList ;

        this.interTextLineSeparationArray = textStructurer.interTextLineSeparationArray ;
        this.meanFontSizeArray = textStructurer.meanFontSizeArray ;
        this.separationSet = textStructurer.separationSet ;
        this.textLineByYCoordMap = textStructurer.textLineByYCoordMap ;
        this.textLineCoordinateArray = textStructurer.textLineCoordinateArray ;
        this.textLineListByFontSize = textStructurer.textLineListByFontSize ;

        this.textLineChunkBoxes = textStructurer.textLineChunkBoxes ;

        this.initialScriptLineList = textStructurer.initialScriptLineList ;
        this.commonestFontSizeTextLineList = textStructurer.commonestFontSizeTextLineList ;
        this.scriptedLineList = textStructurer.scriptedLineList ;
        this.createdHtmlElement = textStructurer.createdHtmlElement ;
        this.svgChunk = textStructurer.svgChunk ;

        this.boundingBox = textStructurer.boundingBox ;
        this.scriptContainer = textStructurer.scriptContainer ;
        this.htmlElement = textStructurer.htmlElement ;
        this.rawCharacters = textStructurer.rawCharacters ;
        this.textOrientation = textStructurer.textOrientation ;

        this.rawWordsList = textStructurer.rawWordsList ;
        this.textChunkList = textStructurer.textChunkList ;
        this.subscriptLineList = textStructurer.subscriptLineList ;
        this.superscriptLineList = textStructurer.superscriptLineList ;

        this.rotatable = textStructurer.rotatable ;

        this.omitShapeList =  textStructurer.omitShapeList;

	}
	
	/** 
	 * This COPIES the lines in the textAnalyzer
	 * <p>
	 * This may not be a good idea
	 * <p>
	 * @param textAnalyzer to copy lines from
	 */
	public TextStructurer(TextAnalyzer textAnalyzer) {
		this.textAnalyzer = textAnalyzer;
		if (textAnalyzer != null) {
			textAnalyzer.setTextStructurer(this);
			rawCharacters = textAnalyzer.getTextCharacters();
			transformIfNotHorizontalOrientation();
			createLinesSortedInXThenY(rawCharacters, textAnalyzer);
		}
	}

	public TextStructurer(List<SVGText> textList) {
		this(new TextAnalyzer(textList, (PageAnalyzer) null));
	}

	private void transformIfNotHorizontalOrientation() {
		textOrientation = textAnalyzer.getTextOrientation();
		if (!TextOrientation.ANY.equals(textOrientation) && 
			!TextOrientation.ROT_0.equals(textOrientation) &&
			rawCharacters.size() > 0) {
			Transform2 rot = rawCharacters.get(0).getCumulativeTransform();
			Angle angle = rot.getAngleOfRotationNew();
			angle = angle.multiplyBy(-1.0);
			Transform2 rotation = new Transform2(angle);
			Real2Range boundingBox = new Real2Range();
			for (SVGText text : rawCharacters) {
				text.applyTransformPreserveUprightText(rotation);
				Transform2 rotChar = Transform2.getRotationAboutPoint(angle, text.getXY());
				text.applyTransformPreserveUprightText(rotChar);
				boundingBox = boundingBox.plus(text.getBoundingBox());
			}
		}
	}
	
	public SVGSVG getDebugSVG() {
		return SVGUtil.createSVGSVG(rawCharacters);
	}
	
	public static TextStructurer createTextStructurer(File svgFile) {
		return createTextStructurer(svgFile, null);
	}

	public static TextStructurer createTextStructurer(File svgFile, TextAnalyzer textAnalyzer) {
		TextStructurer container = new TextStructurer(textAnalyzer);
		List<TextLine> textLineList = TextStructurer.createTextLineList(svgFile);
		if (textLineList != null) {
			container.setTextLines(textLineList);
		}
		return container;
	}

	public List<TextLine> getLinesInIncreasingY() {
		if (textLineList == null) {
			ensureTextLineByYCoordMap();
			List<Integer> yCoordList = Arrays.asList(textLineByYCoordMap.keySet().toArray(new Integer[0]));
			Collections.sort(yCoordList);
			textLineList = new ArrayList<TextLine>();
			int i = 0;
			textLineSerialMap = new HashMap<TextLine, Integer>();
			for (Integer y : yCoordList) {
				TextLine textLine = textLineByYCoordMap.get(y);
				textLineList.add(textLine);
				textLineSerialMap.put(textLine, i++);
			}
		}
		return textLineList;
	}
	
	private void ensureTextLineByYCoordMap() {
		if (textLineByYCoordMap == null) {
			textLineByYCoordMap = new HashMap<Integer, TextLine>();
		}
	}
	
	public Integer getSerialNumber(TextLine textLine) {
		return (textLineSerialMap == null ? null : textLineSerialMap.get(textLine));
	}
	

	public List<String> getTextLineContentList() {
		textLineContentList = null;
		if (textLineList != null) {
			textLineContentList = new ArrayList<String>();
			for (TextLine textLine : textLineList) {
				textLineContentList.add(textLine.getLineString());
			}
		}
		return textLineContentList;
	}
	
	public List<TextLine> getTextLineList() {
		return textLineList;
	}

	public void insertSpaces() {
		if (textLineList != null) {
			for (TextLine textLine : textLineList) {
				textLine.insertSpaces();
			}
		}
	}

	public void insertSpaces(double scaleFactor) {
		if (textLineList != null) {
			for (TextLine textLine : textLineList) {
				textLine.insertSpaces(scaleFactor);
			}
		}
	}
	
	public Set<TextCoordinate> getFontSizeContainerSet() {
		Set<TextCoordinate> fontSizeContainerSet = new HashSet<TextCoordinate>();
		if (fontSizeContainerSet != null) {
			for (TextLine textLine : textLineList) {
				fontSizeContainerSet.addAll(textLine.getFontSizeContainerSet());
			}
		}
		return fontSizeContainerSet;
	}

	public RealArray getMeanFontSizeArray() {
		if (meanFontSizeArray == null) {
			getLinesInIncreasingY();
			if (textLineList != null && textLineList.size() > 0) {
				meanFontSizeArray = new RealArray(textLineList.size());
				for (int i = 0; i < textLineList.size(); i++) {
					meanFontSizeArray.setElementAt(i, textLineList.get(i).getMeanFontSize());
				}
			}
			meanFontSizeArray.format(TextAnalyzer.NDEC_FONTSIZE);
		}
		return meanFontSizeArray;
	}

	public void setTextLines(List<TextLine> textLineList) {
		if (textLineList != null) {
			this.textLineList = new ArrayList<TextLine>();
			for (TextLine textLine : textLineList) {
				add(textLine);
			}
		}
	}

	private void add(TextLine textLine) {
		ensureTextLineList();
		this.textLineList.add(textLine);
	}
	
	private void ensureTextLineList() {
		if (this.textLineList == null) {
			this.textLineList = new ArrayList<TextLine>();
		}
	}

	public List<TextLine> getLinesWithLargestFont() {
		if (linesWithLargestFont == null) {
			linesWithLargestFont = new ArrayList<TextLine>();
			getLargestFontSize();
			for (int i = 0; i < textLineList.size(); i++){
				TextLine textLine = textLineList.get(i);
				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
				if (fontSize != null) {
					if (Real.isEqual(fontSize, largestFontSize.getDouble(), 0.001)) {
						linesWithLargestFont.add( textLine);
					}
				}
			}
		}
		return linesWithLargestFont;
	}

	public List<TextLine> getLinesWithCommonestFont() {
		if (linesWithCommonestFont == null) {
			linesWithCommonestFont = new ArrayList<TextLine>();
			getCommonestFontSize();
			for (int i = 0; i < textLineList.size(); i++){
				TextLine textLine = textLineList.get(i);
				Double fontSize = (textLine == null) ? null : textLine.getFontSize();
				if (fontSize != null) {
					if (Real.isEqual(fontSize, commonestFontSize.getDouble(), 0.001)) {
						linesWithCommonestFont.add( textLine);
					}
				}
			}
		}
		return linesWithCommonestFont;
	}

	public TextCoordinate getCommonestFontSize() {
		commonestFontSize = null;
		Map<Double, Integer> fontCountMap = new HashMap<Double, Integer>();
		for (TextLine textLine : textLineList) {
			Double fontSize = textLine.getFontSize();
			if (fontSize != null) {
				Integer ntext = textLine.getCharacterList().size();
				Integer sum = fontCountMap.get(fontSize);
				if (sum == null) {
					sum = ntext;
				} else {
					sum += ntext;
				}
				fontCountMap.put(fontSize, sum);
			} else {
				Multiset<Double> sizes = textLine.getFontSizeMultiset();
				for (Entry<Double> size : sizes.entrySet()) {
					Integer ntext = size.getCount();
					Integer sum = fontCountMap.get(size.getElement());
					if (sum == null) {
						sum = ntext;
					} else {
						sum += ntext;
					}
					fontCountMap.put(size.getElement(), sum);
				}
			}
		}
		getCommonestFontSize(fontCountMap);
		return commonestFontSize;
	}

	private void getCommonestFontSize(Map<Double, Integer> fontCountMap) {
		int frequency = -1;
		for (Double fontSize : fontCountMap.keySet()) {
			int count = fontCountMap.get(fontSize);
			LOG.trace(">> "+fontSize+" .. "+fontCountMap.get(fontSize));
			if (commonestFontSize == null || count > frequency) {
			    commonestFontSize = new TextCoordinate(fontSize);
			    frequency = count;
			}
		}
		if (commonestFontSize != null) LOG.trace("commonest "+commonestFontSize.getDouble());
	}
	
	public TextCoordinate getLargestFontSize() {
		largestFontSize = null;
		Set<TextCoordinate> fontSizes = this.getFontSizeSet();
		for (TextCoordinate fontSize : fontSizes) {
			if (largestFontSize == null || largestFontSize.getDouble() < fontSize.getDouble()) {
				largestFontSize = fontSize;
			}
		}
		return largestFontSize;
	}
	
	public Real2Range getLargestFontBoundingBox() {
		if (textLinesLargetFontBoundingBox == null) {
			getLinesWithLargestFont();
			getBoundingBox(linesWithLargestFont);
		}
		return textLinesLargetFontBoundingBox;
	}

	public static Real2Range getBoundingBox(List<TextLine> textLines) {
		Real2Range boundingBox = null;
		if (textLines.size() > 0) {
			boundingBox = new Real2Range(new Real2Range(textLines.get(0).getBoundingBox()));
			for (int i = 1; i < textLines.size(); i++) {
				boundingBox.plus(textLines.get(i).getBoundingBox());
			}
		}
		return boundingBox;
	}

	public Set<TextCoordinate> getFontSizeSet() {
		if (fontSizeSet == null) {
			if (textLineList != null) {
				fontSizeSet = new HashSet<TextCoordinate>();
				for (TextLine textLine : textLineList) {
					Set<TextCoordinate> textLineFontSizeSet = textLine.getFontSizeSet();
					fontSizeSet.addAll(textLineFontSizeSet);
				}
			}
		}
		return fontSizeSet;
	}

	/** creates a multiset from addAll() on multisets for each line
	 *  
	 * @return
	 */
	public Multiset<String> getFontFamilyMultiset() {
		if (fontFamilySet == null) {
			fontFamilySet = HashMultiset.create();
			for (TextLine textLine : textLineList) {
				Multiset<String> listFontFamilySet = textLine.getFontFamilyMultiset();
				fontFamilySet.addAll(listFontFamilySet);
			}
		}
		return fontFamilySet;
	}

	/** gets commonest font
	 *  
	 * @return
	 */
	public String getCommonestFontFamily() {
		getFontFamilyMultiset();
		String commonestFontFamily = null;
		int highestCount = -1;
		Set<String> fontFamilyElementSet = fontFamilySet.elementSet();
		for (String fontFamily : fontFamilyElementSet) {
			int count = fontFamilySet.count(fontFamily);
			if (count > highestCount) {
				highestCount = count;
				commonestFontFamily = fontFamily;
			}
		}
		return commonestFontFamily;
	}

	/** gets commonest font
	 *  
	 * @return
	 */
	public int getFontFamilyCount() {
		getFontFamilyMultiset();
		return fontFamilySet.elementSet().size();
	}

	/** get non-overlapping boundingBoxes
	 * @return
	 */
	public List<Real2Range> getDiscreteLineBoxes() {
		List<Real2Range> discreteLineBoxes = new ArrayList<Real2Range>();
//		List<TextLine> textLines = this.getLinesSortedByYCoord();
		return discreteLineBoxes;
	}

	public RealArray getInterTextLineSeparationArray() {
		getTextLineCoordinateArray();
		if (textLineList != null && textLineList.size() > 0) {
			interTextLineSeparationArray = new RealArray();
			Double y0 = textLineCoordinateArray.get(0);
			for (int i = 1; i < textLineCoordinateArray.size(); i++) {
				Double y = textLineCoordinateArray.get(i);
				interTextLineSeparationArray.addElement(y - y0);
				y0 = y;
			}
			interTextLineSeparationArray.format(TextAnalyzer.NDEC_FONTSIZE);
		}
		return interTextLineSeparationArray;
	}

	public Multimap<TextCoordinate, TextLine> getTextLineListByFontSize() {
		if (textLineListByFontSize == null) {
			textLineListByFontSize = ArrayListMultimap.create();
			for (TextLine textLine : textLineList) {
				Set<TextCoordinate> fontSizeSet = textLine.getFontSizeSet();
				if (fontSizeSet != null) {
					for (TextCoordinate fontSize : fontSizeSet) {
						textLineListByFontSize.put(fontSize, textLine);
					}
				}
			}
		}
		return textLineListByFontSize;
		
	}

	public Map<Integer, TextLine> getTextLineByYCoordMap() {
		return textLineByYCoordMap;
	}

	public Multiset<Double> createSeparationSet(int decimalPlaces) {
		getInterTextLineSeparationArray();
		interTextLineSeparationArray.format(decimalPlaces);
		separationSet = HashMultiset.create();
		for (int i = 0; i < interTextLineSeparationArray.size(); i++) {
			separationSet.add(interTextLineSeparationArray.get(i));
		}
		return separationSet;
	}

	public Double getMainInterTextLineSeparation(int decimalPlaces) {
		Double mainTextLineSeparation = null;
		createSeparationSet(decimalPlaces);
		Set<Entry<Double>> ddSet = separationSet.entrySet();
		Entry<Double> maxCountEntry = null;
		Entry<Double> maxSeparationEntry = null;
		for (Entry<Double> dd : ddSet) {
			if (maxCountEntry == null || maxCountEntry.getCount() < dd.getCount()) {
				maxCountEntry = dd;
			}
			if (maxSeparationEntry == null || maxSeparationEntry.getElement() < dd.getElement()) {
				maxSeparationEntry = dd;
			}
		}
		if (maxCountEntry.equals(maxSeparationEntry)) {
			mainTextLineSeparation = maxSeparationEntry.getElement();
		}
		return mainTextLineSeparation;
	}

	public void sortLineByXandMakeTextLineByYCoordMap(List<SVGText> textCharacters) {
		if (textLineByYCoordMap == null) {
			textLineByYCoordMap = new HashMap<Integer, TextLine>();
			Multimap<Integer, SVGText> charactersByY = TextAnalyzerUtils.createCharactersByY(textCharacters);
			for (Integer yCoord : charactersByY.keySet()) {
				Collection<SVGText> characters = charactersByY.get(yCoord);
				TextLine textLine = new TextLine(characters, textAnalyzer);
				textLine.sortLineByX();
				textLineByYCoordMap.put(yCoord, textLine);
			}
		}
	}

	public RealArray getTextLineCoordinateArray() {
		if (textLineCoordinateArray == null) {
			getLinesInIncreasingY();
			if (textLineList != null && textLineList.size() > 0) {
				textLineCoordinateArray = new RealArray();
				for (TextLine textLine : textLineList) {
					Double y0 = textLine.getYCoord();
					textLineCoordinateArray.addElement(y0);
				}
			}
			textLineCoordinateArray.format(TextAnalyzer.NDEC_FONTSIZE);
		}
		return textLineCoordinateArray;
	}

	/** 
	 * Finds maximum indent of lines
	 * <p>
	 * Must be at least 2 lines
	 * <p>
	 * Currently does not check for capitals, etc.
	 */
	public Double getMaximumRightIndent() {
		Double indent = null;
		Double xRight = null;
		if (textLineList != null && textLineList.size() > 1) {
			for (TextLine textLine : textLineList) {
				Double xLast = textLine.getLastXCoordinate();
				if (xRight == null) {
					xRight = xLast;
				}
				if (xRight - xLast > TextAnalyzer.INDENT_MIN) {
					indent = xLast;
				} else if (xLast - xRight > TextAnalyzer.INDENT_MIN) {
					indent = xRight;
				}
			}
		}
		return indent;
	}

	/** 
	 * Finds maximum indent of lines
	 * <p>
	 * Must be at least 2 lines
	 * <p>
	 * Currently does not check for capitals, etc.
	 * 
	 * @deprecated use getMaximumRightIndent()
	 */
	@Deprecated
	public Double getMaxiumumRightIndent() {
		return getMaximumRightIndent();
	}

	public TextLineSet getTextLineSetByFontSize(double fontSize) {
		Multimap<TextCoordinate, TextLine> textLineListByFontSize = this.getTextLineListByFontSize();
		List<TextLine> textLines = (List<TextLine>) textLineListByFontSize.get(new TextCoordinate(fontSize));
		return new TextLineSet(textLines);
	}

	public List<ScriptLine> getInitialScriptLineList() {
		getTextLineChunkBoxesAndInitialScriptLineList();
		return initialScriptLineList;
	}

	/**
	 * This is heuristic. At present it is font-size equality. Font families
	 * are suspect as there are "synonyms", e.g. TimesRoman and TimesNR
	 * 
	 * @return
	 */
	public List<TextLine> getCommonestFontSizeTextLineList() {
		if (commonestFontSizeTextLineList == null) {
			TextCoordinate commonestFontSize = getCommonestFontSize();
			Double commonestFontSizeValue = (commonestFontSize == null ?
					null : commonestFontSize.getDouble());
			commonestFontSizeTextLineList = new ArrayList<TextLine>();
			for (TextLine textLine : textLineList) {
				Double fontSize = textLine.getCommonestFontSize();
				if (fontSize != null && Real.isEqual(fontSize, commonestFontSizeValue, 0.01)) {
					commonestFontSizeTextLineList.add(textLine);
					LOG.trace("COMMONEST FONT SIZE "+textLine);
				}
			}
		}
		return commonestFontSizeTextLineList;
	}

	public List<ScriptLine> getScriptedLineListForCommonestFont() {
		if (scriptedLineList == null) {
			commonestFontSizeTextLineList = getCommonestFontSizeTextLineList();
			initialScriptLineList = getInitialScriptLineList();
			scriptedLineList = new ArrayList<ScriptLine>();
			int i = 0;
			for (ScriptLine textLineGroup : initialScriptLineList) {
				List<ScriptLine> splitChunks = textLineGroup.splitIntoUniqueChunks(this);
				for (ScriptLine splitLine : splitChunks) {
					if (splitLine != null) {
						scriptedLineList.add(splitLine);
					}
				}
				i++;
			}
		}
		LOG.trace("ScriptedLineList "+scriptedLineList.size());
		return scriptedLineList;
	}
	
	public List<Real2Range> getTextLineChunkBoxesAndInitialScriptLineList() {
		if (textLineChunkBoxes == null) {
			List<TextLine> textLineList = getLinesInIncreasingY();
			textLineList = mergeLinesWithSameY(textLineList, Y_EPS);
			textLineChunkBoxes = new ArrayList<Real2Range>();
			Real2Range bbox = null;
			ScriptLine scriptLine = null;
			int i = 0;
			initialScriptLineList = new ArrayList<ScriptLine>();
			for (TextLine textLine : textLineList) {
				Real2Range bbox0 = textLine.getBoundingBox();
				LOG.trace("TL >> "+textLine.getLineString());
				if (bbox == null) {
					bbox = bbox0;
					scriptLine = new ScriptLine(this);
					addBoxAndScriptLines(bbox, scriptLine);
				} else {
					Real2Range intersectionBox = bbox.intersectionWith(bbox0);
					if (intersectionBox == null) {
						bbox = bbox0;
						scriptLine = new ScriptLine(this);
						addBoxAndScriptLines(bbox, scriptLine);
					} else {
						bbox = bbox.plusEquals(bbox0);
					}
				}
				
				scriptLine.add(textLine);
				LOG.trace("SL >>"+scriptLine);
			}
		}
		return textLineChunkBoxes;
	}

	/**
	 * @deprecated Use getTextLineChunkBoxesAndInitialScriptLineList().
	 */
	public List<Real2Range> getTextLineChunkBoxesAndInitialiScriptLineList() {
		return getTextLineChunkBoxesAndInitialScriptLineList();
	}

	private List<TextLine> mergeLinesWithSameY(List<TextLine> textLineList, Double yEps) {
		List<TextLine> newTextLineList = new ArrayList<TextLine>();
		TextLine lastTextLine = null;
		Double lastY = null;
		for (TextLine textLine : textLineList) {
			Double y = (textLine == null ? null : textLine.getYCoord());
			if (y == null) {
				y = textLine.getSVGTextCharacters().get(0).getY();
			}
			// lines with same Y?
			if (lastTextLine != null && Real.isEqual(lastY, y, yEps)) {
				lastTextLine.merge(textLine);
			} else {
				newTextLineList.add(textLine);
				lastTextLine = textLine;
				lastY = y;
			}
		}
		return newTextLineList;
	}

	private void addBoxAndScriptLines(Real2Range bbox, ScriptLine scriptLine) {
		textLineChunkBoxes.add(bbox);
		initialScriptLineList.add(scriptLine);
	}

	public static TextStructurer createTextStructurerWithSortedLines(File svgFile) {
		SVGElement svgChunk = SVGElement.readAndCreateSVG(svgFile);
		return createTextStructurerWithSortedLines(svgChunk);
	}

	public static TextStructurer createTextStructurerWithSortedLines(SVGElement svgElement) {
		List<SVGText> textCharacters = createNormalizedCharacters(svgElement);
		TextStructurer textStructurer = createTextStructurerWithSortedLines(textCharacters);
		textStructurer.setSvgChunk(svgElement);
		textStructurer.getOrCreateTextChunkListFromWords();
		return textStructurer;
	}

	/** extract text elements from svg and normalize.
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGText> createNormalizedCharacters(AbstractCMElement svgElement) {
		SVGDefs.removeDefs(svgElement);
		List<SVGText> textCharacters = SVGText.extractSelfAndDescendantTexts(svgElement);
		boolean normalized = TextUtil.normalize(textCharacters, NORMALIZE_FORM);
		return textCharacters;
	}

	/** may not be used - if I can seamlessly merge TextCache in here.
	 * 
	 * @param svgElement
	 * @return
	 */
	public static TextStructurer createTextStructurerWithSortedLinesAndCaches(SVGElement svgElement) {
		// FIXME - may crash later
		AbstractPlotBox box = null;
		ComponentCache componentCache = new ComponentCache(box);
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		boolean normalized = TextUtil.normalize(svgElement, NORMALIZE_FORM);
		SVGDefs.removeDefs(svgElement);
		List<SVGText> textCharacters = SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgElement, ".//svg:text"));
		TextStructurer textStructurer = createTextStructurerWithSortedLines(textCharacters);
		textStructurer.setSvgChunk(svgElement);
		textStructurer.getOrCreateTextChunkListFromWords();
		return textStructurer;
	}

	public void setSvgChunk(SVGElement svgChunk) {
		this.svgChunk = svgChunk;
	}

	public static TextStructurer createTextStructurerWithSortedLines(List<SVGText> textCharacters, TextAnalyzer textAnalyzer) {
		TextStructurer textStructurer = new TextStructurer(textAnalyzer);
		return textStructurer;
	}

	private void createLinesSortedInXThenY(List<SVGText> textCharacters, TextAnalyzer textAnalyzer) {
		sortLineByXandMakeTextLineByYCoordMap(textCharacters);
		textLineList = getLinesInIncreasingY();
		for (TextLine textLine : textLineList) {
			LOG.trace("TL "+textLine);
		}
		/*if (false) {
			textAnalyzer.setTextCharacters(textCharacters);
		}*/
		textAnalyzer.setTextStructurer(this);
	}
	
	public static TextStructurer createTextStructurerWithSortedLines(List<SVGText> textCharacters) {
		TextAnalyzer textAnalyzer = new TextAnalyzer();
		textAnalyzer.setTextList(textCharacters);
		TextStructurer textStructurer = new TextStructurer(textAnalyzer);
		// the next two lines may be unnecessary
		textStructurer.sortLineByXandMakeTextLineByYCoordMap(textCharacters);
		List<TextLine> textLineList = textStructurer.getLinesInIncreasingY(); 
		textAnalyzer.setTextStructurer(textStructurer);
		return textStructurer;
	}
	
	public TextAnalyzer getTextAnalyzer() {
		ensureTextAnalyzer();
		return textAnalyzer;
	}

	private void ensureTextAnalyzer() {
		if (textAnalyzer == null) {
			textAnalyzer = new TextAnalyzer((PageAnalyzer) null);
		}
	}

	/** 
	 * Finds maximum indent of lines
	 * <p>
	 * Must be at least 2 lines
	 * <p>
	 * Currently does not check for capitals, etc.
	 */
	public Double getMaximumLeftIndentForLargestFont() {
		Double indent = null;
		Double xLeft = null;
		List<TextLine> textLineListWithLargestFont = this.getLinesWithCommonestFont();
		if (textLineListWithLargestFont != null && textLineListWithLargestFont.size() > 1) {
			for (TextLine textLine : textLineListWithLargestFont) {
				Double xStart = textLine.getFirstXCoordinate();
				if (xStart == null) {
					throw new RuntimeException("null start");
				}
				if (xLeft == null) {
					xLeft = xStart;
				}
				if (xLeft - xStart > TextAnalyzer.INDENT_MIN) {
					indent = xLeft;
				} else if (xStart - xLeft > TextAnalyzer.INDENT_MIN) {
					indent = xStart;
				}
			}
		}
		return indent;
	}

	/** 
	 * Finds maximum indent of lines
	 * <p>
	 * Must be at least 2 lines
	 * <p>
	 * Currently does not check for capitals, etc.
	 */
	public static Double getMaximumLeftIndent(List<TextLine> textLineList) {
		Double indent = null;
		Double xLeft = null;
		if (textLineList != null && textLineList.size() > 1) {
			for (TextLine textLine : textLineList) {
				Double xStart = textLine.getFirstXCoordinate();
				if (xStart == null) {
					throw new RuntimeException("null start");
				}
				if (xLeft == null) {
					xLeft = xStart;
				}
				if (xLeft - xStart > TextAnalyzer.INDENT_MIN) {
					indent = xLeft;
				} else if (xStart - xLeft > TextAnalyzer.INDENT_MIN) {
					indent = xStart;
				}
			}
		}
		return indent;
	}

	public static List<TextLine> createTextLineList(File svgFile) {
		TextStructurer textStructurer = createTextStructurerWithSortedLines(svgFile);
		List<TextLine> textLineList = textStructurer.getLinesInIncreasingY();
		return textLineList;
	}

	public static TextAnalyzer createTextAnalyzerWithSortedLines(List<SVGText> characters, PageAnalyzer pageAnalyzer) {
			TextAnalyzer textAnalyzer = new TextAnalyzer(pageAnalyzer);
			TextStructurer.createTextStructurerWithSortedLines(characters, textAnalyzer);
			return textAnalyzer;
	}

	/*private static void mergeParas(HtmlP pCurrent, HtmlP pNext) {
		Elements currentChildren = pCurrent.getChildElements();
		if (currentChildren.size() > 0) {
			HtmlElement lastCurrent = (HtmlElement) currentChildren.get(currentChildren.size() - 1);
			HtmlSpan currentLastSpan = (lastCurrent instanceof HtmlSpan) ? (HtmlSpan) lastCurrent : null;
			Elements nextChildren = pNext.getChildElements();
			HtmlElement firstNext = nextChildren.size() == 0 ? null : (HtmlElement) nextChildren.get(0);
			HtmlSpan nextFirstSpan = (firstNext != null && firstNext instanceof HtmlSpan) ? (HtmlSpan) firstNext : null;
			int nextCounter = 0;
			// merge texts
			if (currentLastSpan != null && nextFirstSpan != null) {
				String mergedText = mergeLineText(currentLastSpan.getValue(), nextFirstSpan.getValue());
				LOG.trace("Merged "+mergedText);
				lastCurrent.setValue(mergedText);
				nextCounter = 1;
			}
			//merge next line's children
			for (int i = nextCounter; i < nextChildren.size(); i++) {
				pCurrent.appendChild(HtmlElement.create(nextChildren.get(i)));
			}
		}
	}

	private static String mergeLineText(String last, String next) {
		//merge hyphen minus
		if (last.endsWith("-")) {
			return last.substring(0, last.length()-1) + next;
		} else {
			return last + " " + next;
		}
	}*/

	public boolean endsWithRaggedLine() {
		return createdHtmlElement != null &&
				!createdHtmlElement.getValue().endsWith(".");
	}

	public boolean startsWithRaggedLine() {
		boolean starts = false;
		if (createdHtmlElement != null && createdHtmlElement.getValue().length() > 0) {
			Character c = createdHtmlElement.getValue().charAt(0);
			if (c != null) {
				starts = !Character.isUpperCase(c);
			}
		}
		return starts;
	}

	public boolean lineIsLargerThanCommonestFontSize(int lineNumber) {
		TextLine textLine = (lineNumber < 0 || lineNumber >= textLineList.size()) ?
				null : textLineList.get(lineNumber);
		return lineIsLargerThanCommonestFontSize(textLine);
	}

	public boolean lineIsLargerThanCommonestFontSize(TextLine textLine) {
		boolean isLargerThan = false;
		Double commonestFontSize = getCommonestFontSize().getDouble();
		if (textLine != null && commonestFontSize != null) {
			Double fontSize = textLine.getFontSize();
			if (fontSize != null) {
				isLargerThan = fontSize / commonestFontSize > LARGER_FONT_SIZE_RATIO;
			}
		}
		return isLargerThan;
	}

	public boolean isCommonestFontSize(TextLine textLine) {
		getCommonestFontSizeTextLineList();
		return textLine != null && commonestFontSizeTextLineList.contains(textLine);
	}

	public boolean isCommonestFontSize(ScriptLine textLineGroup) {
		getCommonestFontSizeTextLineList();
		TextLine largestLine = textLineGroup.getLargestLine();
		return textLineGroup != null && commonestFontSizeTextLineList.contains(largestLine);
	}

	public boolean lineGroupIsLargerThanCommonestFontSize(int lineNumber) {
		TextLine textLine = (lineNumber < 0 || lineNumber >= textLineList.size()) ?
				null : textLineList.get(lineNumber);
		return lineIsLargerThanCommonestFontSize(textLine);
	}

	public boolean lineGroupIsLargerThanCommonestFontSize(ScriptLine textLineGroup) {
		boolean isLargerThan = false;
		Double commonestFontSize = getCommonestFontSize().getDouble();
		if (textLineGroup != null && commonestFontSize != null) {
			Double fontSize = textLineGroup.getFontSize();
			if (fontSize != null) {
				isLargerThan = fontSize / commonestFontSize > LARGER_FONT_SIZE_RATIO;
			}
		}
		return isLargerThan;
	}

	
	/** 
	 * Split after line where font size changes to / from bigger than commonest
	 * <p>
	 * Dangerous if there are sub- or superscripts (use splitGroupBiggerThanCommonest)
	 * 
	 * @return
	 */
	public IntArray splitBiggerThanCommonest() {
		Double commonestFontSize = this.getCommonestFontSize().getDouble();
		IntArray splitArray = new IntArray();
		for (int i = 0; i < textLineList.size() - 1; i++) {
			TextLine textLineA = textLineList.get(i);
			Double fontSizeA = textLineA.getFontSize();
			TextLine textLineB = textLineList.get(i+1);
			Double fontSizeB = textLineB.getFontSize();
			if (fontSizeA != null && fontSizeB != null) {
				double ratioAB = fontSizeA / fontSizeB;
				//Line increases beyond commonest size?
				if (Real.isEqual(fontSizeA, commonestFontSize, 0.01) 
						&& ratioAB < 1./LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				} else if (Real.isEqual(fontSizeB, commonestFontSize, 0.01) 
						&& ratioAB > LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				}
			}
		}
		return splitArray;
	}

	
	/** 
	 * Split after textLineGroup where font size changes to / from bigger than commonest
	 * 
	 * @return
	 */
	public IntArray splitGroupBiggerThanCommonest() {
		getScriptedLineListForCommonestFont();
		Double commonestFontSize = this.getCommonestFontSize().getDouble();
		IntArray splitArray = new IntArray();
		for (int i = 0; i < scriptedLineList.size() - 1; i++) {
			ScriptLine textLineGroupA = scriptedLineList.get(i);
			Double fontSizeA = textLineGroupA.getFontSize();
			ScriptLine textLineB = scriptedLineList.get(i + 1);
			Double fontSizeB = textLineB.getFontSize();
			if (fontSizeA != null && fontSizeB != null) {
				double ratioAB = fontSizeA / fontSizeB;
				// line increases beyond commonest size?
				if (Real.isEqual(fontSizeA, commonestFontSize, 0.01) 
						&& ratioAB < 1./LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				} else if (Real.isEqual(fontSizeB, commonestFontSize, 0.01) 
						&& ratioAB > LARGER_FONT_SIZE_RATIO) {
					splitArray.addElement(i);
				}
			}
		}
		return splitArray;
	}

	/** 
	 * Split the textStructurer after the lines in array.
	 * <p>
	 * If null or size() == 0, returns list with 'this', so a returned list of size 0
	 * effectively does nothing
	 * 
	 * @param afterLineGroups if null or size() == 0, returns list with 'this';
	 * @return
	 */
	public List<TextStructurer> splitLineGroupsAfter(IntArray afterLineGroups) {
		getScriptedLineListForCommonestFont();
		List<TextStructurer> textStructurerList = new ArrayList<TextStructurer>();
		if (afterLineGroups == null || afterLineGroups.size() == 0) {
			textStructurerList.add(this);
		} else {
			int start = 0;
			for (int i = 0; i < afterLineGroups.size(); i++) {
				int lineNumber = afterLineGroups.elementAt(i);
				if (lineNumber > scriptedLineList.size() - 1) {
					throw new RuntimeException("bad index: "+lineNumber);
				}
				TextStructurer newTextStructurer = createTextStructurerFromTextLineGroups(start, lineNumber);
				textStructurerList.add(newTextStructurer);
				start = lineNumber + 1;
			}
			TextStructurer newTextStructurer = createTextStructurerFromTextLineGroups(start, scriptedLineList.size() - 1);
			textStructurerList.add(newTextStructurer);
		}
		return textStructurerList;
	}

	private TextStructurer createTextStructurerFromTextLineGroups(int startLineGroup, int lineGroupNumber) {
		getScriptedLineListForCommonestFont();
		TextStructurer textStructurer = new TextStructurer((TextAnalyzer)null);
		textStructurer.textAnalyzer = this.textAnalyzer;
		for (int iGroup = startLineGroup; iGroup <= lineGroupNumber; iGroup++) {
			ScriptLine textLineGroup = scriptedLineList.get(iGroup);
			if (textLineGroup != null) {
				List<TextLine> textLineList = textLineGroup.getTextLineList();
				textStructurer.add(textLineList);
			}
		}
		return textStructurer;
	}

	private void add(List<TextLine> textLineList) {
		for (TextLine textLine : textLineList) {
			this.add(textLine);
		}
	}

	public List<TextStructurer> split(Splitter splitter) {
		if (Splitter.BOLD.equals(splitter)) {
			return splitOnFontBoldChange(0);
		}
		if (Splitter.FONTSIZE.equals(splitter)) {
			return splitOnFontSizeChange(0);
		}
		if (Splitter.FONTFAMILY.equals(splitter)) {
			return splitOnFontFamilyChange(0);
		}
		throw new RuntimeException("Unknown splitter: "+splitter);
	}

	/** 
	 * Splits bold line(s) from succeeding ones.
	 * <p>
	 * May trap smaller headers - must catch this later
	 * 
	 * @return
	 */
	public List<TextStructurer> splitOnFontBoldChange(int maxFlip) {
		IntArray splitter = getSplitArrayForFontWeightChange(maxFlip);
		LOG.trace("SPLIT "+splitter);
		return splitIntoList(splitter);
	}
	
	/** 
	 * Splits line(s) on fontSize.
	 * 
	 * @return
	 */
	public List<TextStructurer> splitOnFontSizeChange(int maxFlip) {
		IntArray splitter = getSplitArrayForFontSizeChange(maxFlip);
		return splitIntoList(splitter);
	}

	/** 
	 * Splits line(s) on fontFamily.
	 * 
	 * @return
	 */
	public List<TextStructurer> splitOnFontFamilyChange(int maxFlip) {
		IntArray splitter = getSplitArrayForFontFamilyChange(maxFlip);
		return splitIntoList(splitter);
	}

	/** 
	 * Splits line(s) on fontSize.
	 * 
	 * @return
	 */
	public IntArray getSplitArrayForFontWeightChange(int maxFlip) {
		getScriptedLineListForCommonestFont();
		Boolean currentBold = null;
		IntArray splitArray = new IntArray();
		if (scriptedLineList.size() > 0) {
			int nFlip = 0;
			for (int i = 0; i < scriptedLineList.size(); i++) {
				ScriptLine scriptLine = scriptedLineList.get(i);
				Boolean isBold = (scriptLine == null) ? null : scriptLine.isBold();
				if (currentBold == null) { 
					currentBold = isBold;
					// insist on leading bold
					if (maxFlip < 0 && !isBold) {
						return splitArray;
					}
				} else if (!currentBold.equals(isBold)) {
					splitArray.addElement(i - 1);
					currentBold = isBold;
					if (nFlip++ >= maxFlip) break;
				}
			}
		}
		return splitArray;
	}
	
	
	/** 
	 * Splits line(s) on fontSize.
	 * 
	 * @return
	 */
	public IntArray getSplitArrayForFontSizeChange(int maxFlip) {
		double EPS = 0.01;
		getScriptedLineListForCommonestFont();
		Double currentFontSize = null;
		IntArray splitArray = new IntArray();
		if (scriptedLineList.size() > 0) {
			int nFlip = 0;
			for (int i = 0; i < scriptedLineList.size(); i++) {
				Double fontSize = scriptedLineList.get(i).getFontSize();
				if (currentFontSize == null) {
					currentFontSize = fontSize;
				} else if (!Real.isEqual(fontSize, currentFontSize, EPS)) {
					splitArray.addElement(i - 1);
					currentFontSize = fontSize;
					if (nFlip++ >= maxFlip) break;
				}
			}
		}
		return splitArray;
	}
	
	/** 
	 * Splits line(s) on fontSize.
	 * 
	 * @return
	 */
	public IntArray getSplitArrayForFontFamilyChange(int maxFlip) {
		getScriptedLineListForCommonestFont();
		String currentFontFamily = null;
		IntArray splitArray = new IntArray();
		if (scriptedLineList.size() > 0) {
			int nFlip = 0;
			for (int i = 0; i < scriptedLineList.size(); i++) {
				String fontFamily = scriptedLineList.get(i).getFontFamily();
				if (currentFontFamily == null) {
					currentFontFamily = fontFamily;
				} else if (!fontFamily.equals(currentFontFamily)) {
					splitArray.addElement(i - 1);
					currentFontFamily = fontFamily;
					if (nFlip++ >= maxFlip) {
						break;
					}
				}
			}
		}
		return splitArray;
	}

	private List<TextStructurer> splitIntoList(IntArray splitter) {
		List<TextStructurer> splitList = null;
		if (splitter != null && splitter.size() != 0) {
			splitList = splitLineGroupsAfter(splitter);
		}  else {
			splitList = new ArrayList<TextStructurer>();
			splitList.add(this);
		}
		return splitList;
	}

	/*private SVGG oldCreateSVGGChunk() {
		SVGG g = new SVGG();
		for (TextLine textLine : textLineList) {
			for (SVGText text : textLine) {
				g.appendChild(new SVGText(text));
			}
		}
		return g;
	}*/

	/* 
	 * Attempts to split into numbered list by line starts.
	 * 
	 * @return
	 */
	/*private List<TextStructurer> splitNumberedList() {
		getScriptedLineList();
		List<TextStructurer> splitLineGroups = new ArrayList<TextStructurer>();
		int last = 0;
		for (int i = 0; i < scriptedLineList.size(); i++) {
			ScriptLine tlg = scriptedLineList.get(i);
			String value = tlg.getRawValue();
			LOG.trace(value);
			Matcher matcher = NUMBER_ITEM_PATTERN.matcher(value);
			if (matcher.matches()) {
				Integer serial = Integer.parseInt(matcher.group(1));
				LOG.trace(">> "+serial);
				addTextLineGroups(splitLineGroups, last, i);
				last = i;
				LOG.trace("split: "+i);
			}
		}
		addTextLineGroups(splitLineGroups, last, scriptedLineList.size());
		return splitLineGroups;
	}*/

	private void addTextLineGroups(List<TextStructurer> splitLineGroups, int last, int next) {
		if (next > last) {
			TextStructurer tc = new TextStructurer((TextAnalyzer)null);
			splitLineGroups.add(tc);
			for (int j = last; j < next; j++) {
				tc.add(scriptedLineList.get(j));
			}
		}
	}

	private void add(ScriptLine textLineGroup) {
		ensureScriptedLineList();
		scriptedLineList.add(textLineGroup);
		for (TextLine textLine : textLineGroup) {
			this.add(textLine);
		}
	}

	private List<ScriptLine> ensureScriptedLineList() {
		if (scriptedLineList == null) {
			scriptedLineList = new ArrayList<ScriptLine>();
		}
		return scriptedLineList;
	}

	public SVGElement getSVGChunk() {
		return svgChunk;
	}
	
	public Real2Range ensureBoundingBox() {
		if (boundingBox == null) {
			if (svgChunk != null) {
				boundingBox = svgChunk.getBoundingBox();
			}
		}
		return boundingBox;
	}
	
	public Real2Range getBoundingBox() {
		ensureBoundingBox();
		return boundingBox;
	}
	
	public RealRange getXRange() {
		ensureBoundingBox();
		return boundingBox == null ? null : boundingBox.getXRange();
	}
	
	public RealRange getYRange() {
		ensureBoundingBox();
		return boundingBox == null ? null : boundingBox.getYRange();
	}
	
	public ScriptContainer getScriptContainer() {
		if (scriptContainer == null) {
			scriptContainer = ScriptContainer.createScriptContainer(this, (PageAnalyzer) null);
		}
		return scriptContainer;
	}
	
	public HtmlElement createHtmlElement() {
		if (htmlElement == null) {
			getScriptContainer();
			htmlElement = scriptContainer.createHtmlElement();
		}
		return htmlElement;
	}

	public String toString() {
		String lineSeparator = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		if (textLineList == null) {
			sb.append("null");
		} else {
			sb.append("TextStructurer: "+ textLineList.size() + lineSeparator);
			for (TextLine textLine : textLineList) {
				sb.append(textLine.toString() + lineSeparator);
			}
		}
		return sb.toString();
	}

	/** 
	 * Detach every character in rawCharacters.
	 */
	public void detachCharacters() {
		for (AbstractCMElement character : rawCharacters) {
			character.detach();
		}
	}

	/** 
	 * Create list of Phrases from textLines
	 */
	public List<RawWords> createRawWordsListFromTextLineList() {
		if (rawWordsList == null) {
			rawWordsList = new ArrayList<RawWords>();
			getLinesInIncreasingY();
			for (TextLine textLine : textLineList) {
				RawWords rawWords = textLine.getRawWords();
				rawWordsList.add(rawWords);
			}
		}
		return rawWordsList;
	}

//	public ColumnMaps createColumnMaps() {
//		ColumnMaps columnMaps = new ColumnMaps(this);
//		return columnMaps;
//	}
//
//	public List<Tab> createSingleTabList() {
//		getTextLineList();
//		createRawWordsListFromTextLineList();
//		ColumnMaps columnMaps = new ColumnMaps(this);
//		columnMaps.getTabs();
//		List<Tab> tabList = columnMaps.createSingleTabList();
//		return tabList;
//	}

	public List<TabbedTextLine> createTabbedLineList() {
		getTextLineList();
		// FIXME
		return null;
	}

	/** 
	 * Convenience method for reading a page and extracting a line.
	 * <p>
	 * Perhaps mainly used in test.
	 * 
	 * @param svgFile
	 * @return
	 */
	public static TextLine createTextLine(File svgFile, int lineNumber) {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgFile);
		List<TextLine> textLines = textStructurer.getLinesInIncreasingY();
		return textLines.get(lineNumber);
	}

	/*public void setTextList(List<SVGText> textList) {
		this.textList = textList;
	}*/
	
	public List<SVGText> getTextList() {
		getTextAnalyzer();
		if (textAnalyzer != null) {
			return textAnalyzer.getTextCharacters();	
		}
		return null;
	}
	
	public void setTextCharacters(List<SVGText> textList) {
		getTextAnalyzer();
		if (textAnalyzer != null) {
			textAnalyzer.setTextList(textList);	
		}
	}

	public TextChunkList getOrCreateTextChunkListFromWords() {
		if (textChunkList == null) {
			List<RawWords> rawWordsList = this.createRawWordsListFromTextLineList();
			textChunkList = new TextChunkList();
			for (RawWords rawWords : rawWordsList) {
				PhraseChunk phraseChunk = rawWords.createPhraseList();
				textChunkList.add(phraseChunk);
			}
		}
		return textChunkList;
	}

//
//	public FlowStructurer createFlowStructurer(SVGElement phraseListList) {
////		createPhraseListListFromWords();
//		flowStructurer = new FlowStructurer(phraseListList);
//		flowStructurer.setTextStructurer(this);
//		return flowStructurer;
//	}

	/**
	 * @return
	 */
	public TextChunkList getTextChunkList() {
		if (textChunkList == null) {
			getOrCreateTextChunkListFromWords();
		}
		return textChunkList;
	}

	/** assumes sorted lines
	 */
	public void extractAndApplySuscripts() {
		List<TextLine> textLineList = getTextLineList();
		subscriptLineList = new ArrayList<TextLine>();
		superscriptLineList = new ArrayList<TextLine>();
		for (int i = 0; i < textLineList.size(); i++) {
			TextLine thisLine = textLineList.get(i);
			double thisSize = thisLine.getFontSize();
			double thisY = thisLine.getYCoord();
			double thisTop = thisY - thisSize;
			if (i > 0) {
				TextLine previousLine =textLineList.get(i - 1);
				double previousSize = previousLine.getFontSize();
				double belowTop = thisTop - previousLine.getYCoord();
				if (previousSize / thisSize < 0.9 && belowTop < -2) {
					superscriptLineList.add(previousLine);
					thisLine.setSuperscriptLine(previousLine);
				}
			}
			if (i < textLineList.size() - 1) {
				TextLine nextLine = textLineList.get(i + 1);
				double nextSize = nextLine.getFontSize();
				double aboveBottom = thisY - (nextLine.getYCoord() - nextSize); 
				if (nextSize / thisSize < 0.9 && aboveBottom > 2) {
					subscriptLineList.add(nextLine);
					thisLine.setSubscriptLine(nextLine);
				}
			}
		}
	}

	public List<TextLine> getSuperscriptLineList() {
		return superscriptLineList;
	}

	public List<TextLine> getSubscriptLineList() {
		return subscriptLineList;
	}

//	public List<TextBox> createTextBoxList(TextChunk phraseListList, Real2 xMargins, Real2 yMargins) {
//		textBoxList = new ArrayList<TextBox>();
//		for (int i = 0; i < phraseListList.size(); i++) {
//			PhraseChunk phraseList = phraseListList.get(i);
//			for (int j = 0; j < phraseList.size(); j++) {
//				Phrase phrase = phraseList.get(j);
//				Real2Range phraseBox = phrase.getBoundingBox();
//				phraseBox = phraseBox.getReal2RangeExtendedInX(xMargins.x, xMargins.y).getReal2RangeExtendedInY(yMargins.x, yMargins.y);
//				int ibox = -1;
//				for (int k = 0; k < textBoxList.size(); k++) {
//					TextBox textBox = textBoxList.get(k);
//					Real2Range bboxi = textBox.getBoundingBox(); 
//					Real2Range intersect = phraseBox.intersectionWith(bboxi);
//					if (!SVGUtil.isNullReal2Range(intersect)) {
//						textBox.add(new Phrase(phrase));
//						ibox = k;
//						break;
//					}
//				}
//				if (ibox == -1) {
//					TextBox textBox = new TextBox(phrase);
//					textBoxList.add(textBox);
//				}
//			}
//		}
//		return textBoxList;
//	}

	public void rotateAsBlock(Real2 xy, Angle angle) {
		for (TextLine textLine : textLineList) {
			textLine.rotate(xy, angle);
		}
	}

	public void formatTextLineTransforms(int nplaces) {
		for (TextLine textLine : textLineList) {
			textLine.formatTransform(nplaces);
		}
	}

	public List<SVGText> getRotatedCharacters(Angle angle, double eps) {
		List<SVGText> characterList = getCharacterList();
		return SVGText.getRotatedTexts(characterList, angle, eps);
	}

	public List<SVGText> getCharacterList() {
		List<SVGText> allTextList = new ArrayList<SVGText>();
		for (TextLine textLine : textLineList) {
			List<SVGText> textList = textLine.getSVGTextCharacters();
			allTextList.addAll(textList);
		}
		return allTextList;
	}

	public SVGG createChunkFromVerticalText(Real2 rotCentre, Angle rotAngle) {
		SVGG g = new SVGG();
		if (textChunkList != null) {
			textChunkList.rotateAll(rotCentre, rotAngle);
			textChunkList.formatTransformRecursively(5);
			List<SVGText> textList = SVGText.extractSelfAndDescendantTextsWithSpecificAngle(textChunkList, new Angle(0.0), 0.001);
			g = new SVGG();
			for (SVGText text : textList) {
				text.applyTransformAttributeAndRemove();
				g.appendChild(new SVGText(text));
			}
		}
		return g;
	}

	public SVGG createChunkFromVerticalText(Angle angle) {
		Real2 centre = svgChunk.getCentreForClockwise90Rotation();
		return createChunkFromVerticalText(centre, angle);
	}

	public boolean hasAntiClockwiseCharacters() {
		return getRotatedCharacters(new Angle(Math.PI / 2.0), 0.0001).size() > 0;
	}

	public void setRotatable(boolean b) {
		this.rotatable = b;
	}

	public boolean isRotatable() {
		return rotatable;
	}

	public void condenseSuscripts() {
		SuscriptEditor suscriptEditor = new SuscriptEditor(textChunkList.getLastTextChunk());
		suscriptEditor.mergeAll();
	}
	public void setSVGChunk(SVGElement svgChunk) {
		this.svgChunk = svgChunk;
	}



}
