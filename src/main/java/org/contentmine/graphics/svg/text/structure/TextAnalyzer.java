package org.contentmine.graphics.svg.text.structure;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Polar;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.text.TextCoordinate;
import org.contentmine.graphics.svg.text.line.TextLine;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/** 
 * Attempts to assemble characters into meaningful text
 * 
 * moved from svg2xml
 * 
 * @author pm286
 */
public class TextAnalyzer /*extends ChunkAnalyzer*/ {

	private final static Logger LOG = Logger.getLogger(TextAnalyzer.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum TextOrientation {
		ANY,
		IRREGULAR,
		ROT_0,
		ROT_PI2,
		ROT_PI,
		ROT_3PI2,
	}
	
	private static final Real2 TEXT_ROT_0 = new Real2(1., 0.);
	private static final Real2 TEXT_ROT_PI2 = new Real2(0., 1.);
	private static final Real2 TEXT_ROT_PI = new Real2(-1., 0.);
	private static final Real2 TEXT_ROT_3PI2 = new Real2(0., -1.0);
	private static final Double MIN_DOT_ANGLE = 0.99;
	
	public static final String TEXT1 = "text1";
	public static final String CHUNK = "chunk";
	public static final String TEXT = "TEXT";
	
	public static final double DEFAULT_TEXTWIDTH_FACTOR = 0.9;
	public static final int NDEC_FONTSIZE = 3;
	public static final double INDENT_MIN = 1.0; //pixels
	
	public static Double TEXT_EPS = 1.0;

	private TextLine rawCharacterList;
	private Map<Integer, TextLine> characterByXCoordMap;
	private AbstractCMElement svgParent;
    private List<SVGText> textCharacters;
    
    private TextOrientation textOrientation = TextOrientation.ANY;
    private TextAnalyzer rot0TextAnalyzer = null;
    private TextAnalyzer rotPi2TextAnalyzer = null;
    private TextAnalyzer rotPiTextAnalyzer = null;
    private TextAnalyzer rot3Pi2TextAnalyzer = null;
    private TextAnalyzer rotIrregularTextAnalyzer = null;
    private FontNormalizer fontNormalizer;
	private PageAnalyzer pageAnalyzer;
	/** 
	 * Refactored container 
	 * */
	private TextStructurer textStructurer;
//	protected List<AbstractContainer> abstractContainerList;

	public TextAnalyzer() {
		this((PageAnalyzer)null);
	}
	public TextAnalyzer(PageAnalyzer pageAnalyzer) {
//		super(pageAnalyzer);
		// FIXME placeholder may not work
		setPageAnalyzer(pageAnalyzer);
	}
	private void setPageAnalyzer(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
	}
	
	private TextAnalyzer(PageAnalyzer pageAnalyzer, TextOrientation textOrientation) {
		this(pageAnalyzer);
		this.textOrientation = textOrientation;
	}
	
	public TextAnalyzer(List<SVGText> textList, PageAnalyzer pageAnalyzer) {
//		super(pageAnalyzer);
		setPageAnalyzer(pageAnalyzer);
		setTextList(textList);
	}

	public TextAnalyzer(List<SVGText> textList) {
//		super((PageAnalyzer) null);
		setPageAnalyzer(null);
		setTextList(textList);
	}

	public TextAnalyzer(AbstractCMElement svgElement, PageAnalyzer pageAnalyzer) {
		this(SVGText.extractSelfAndDescendantTexts(svgElement), pageAnalyzer);
	}

	public String getTag() {
		return TEXT1;
	}
	
	public Map<Integer, TextLine> getCharacterByXCoordMap() {
		return characterByXCoordMap;
	}

	public void analyzeTexts(List<SVGText> textCharacters) {
		if (textCharacters == null) {
			throw new RuntimeException("null characters: ");
		} else {
			this.textCharacters = textCharacters;
			ensureTextContainerWithSortedLines().sortLineByXandMakeTextLineByYCoordMap(textCharacters);
		}
	}

	public List<TextLine> getLinesInIncreasingY() {
		return ensureTextContainerWithSortedLines().getLinesInIncreasingY();
	}

	private void getRawCharacterList(List<SVGElement> textElements) {
		rawCharacterList = new TextLine(this);
		for (int i = 0; i < textElements.size(); i++) {
			SVGText text = (SVGText) textElements.get(i);
			text.setBoundingBoxCached(true);
			rawCharacterList.add(text);
		}
	}
	
	/** puts all characters (usually SVGText of length 1) into
	 * a single container
	 * @return
	 */
	public TextLine getRawTextCharacterList() {
		if (rawCharacterList == null) {
			List<SVGElement> textElements = SVGUtil.getQuerySVGElements(svgParent, ".//svg:text");
			getRawCharacterList(textElements);
			LOG.trace("read "+rawCharacterList.size()+" raw characters "+rawCharacterList.toString());
		}
		return rawCharacterList;
	}
	

	public void debug() {
		debug("xmap", characterByXCoordMap);
	}

	public List<TextLine> getTextLines() {
		return ensureTextContainerWithSortedLines().getTextLineList();
	}
	
	private TextStructurer ensureTextContainerWithSortedLines() {
		if (textStructurer == null) {
			textStructurer = TextStructurer.createTextStructurerWithSortedLines(textCharacters, this);
		} else {
			textStructurer.sortLineByXandMakeTextLineByYCoordMap(textCharacters);
		}
		return textStructurer;
	}
	
	// ===========utils============================
	
	private void debug(String string, Map<Integer, TextLine> textByCoordMap) {
		if (textByCoordMap == null) {
			LOG.warn("No textCoordMap "+textStructurer.getTextLineByYCoordMap());
		} else {
			Set<Integer> keys = textByCoordMap.keySet();
			Integer[] ii = keys.toArray(new Integer[keys.size()]);
			Arrays.sort(ii);
			for (int iz : ii) {
				TextLine textList = textByCoordMap.get(iz);
				for (SVGText text : textList) {
					LOG.trace(">> "+text.getXY()+" "+text.getText()+ " ");
				}
			}
		}
	}


	public List<SVGText> getTextCharacters() {
		ensureTextCharacters();
		return textCharacters;
	}
	
	public void setTextList(List<SVGText> textCharacters) {
		this.textCharacters = textCharacters;
		normalizeText();
	}
	
	/** normalizes fontWeights and probably more
	 * 
	 */
	private void normalizeText() {
		ensureFontNormalizer();
	}

	/** includes FontNormalizer.getDefaultNormalizer() by default.
	 * 
	 */
	private void ensureFontNormalizer() {
		if (fontNormalizer == null) {
			fontNormalizer = FontNormalizer.getDefaultNormalizer();
		}
	}

	public void addCharacter(SVGText character){
		ensureTextCharacters();
		textCharacters.add(character);
	}

	private void ensureTextCharacters() {
		if (textCharacters == null) {
			textCharacters = new ArrayList<SVGText>();
		}
	}

	// =========== Delegates ============
	public Double getMainInterTextLineSeparation(int decimalPlaces) {
		return ensureTextContainerWithSortedLines().getMainInterTextLineSeparation(decimalPlaces);
	}

	public RealArray getInterTextLineSeparationArray() {
		return ensureTextContainerWithSortedLines().getInterTextLineSeparationArray();
	}

	public TextStructurer getTextStructurer() {
		return ensureTextContainerWithSortedLines();
	}

	public List<TextLine> getLinesWithLargestFont() {
		return ensureTextContainerWithSortedLines().getLinesWithCommonestFont();
	}

	public Real2Range getTextLinesLargestFontBoundingBox() {
		return ensureTextContainerWithSortedLines().getLargestFontBoundingBox();
	}

	public Integer getSerialNumber(TextLine textLine) {
		return ensureTextContainerWithSortedLines().getSerialNumber(textLine);
	}

	public List<String> getTextLineContentList() {
		return ensureTextContainerWithSortedLines().getTextLineContentList();
	}

	public void insertSpaces() {
		ensureTextContainerWithSortedLines().insertSpaces();
	}

	public RealArray getTextLineCoordinateArray() {
		return ensureTextContainerWithSortedLines().getTextLineCoordinateArray();
	}

	public Multimap<TextCoordinate, TextLine> getTextLineListByFontSize() {
		return ensureTextContainerWithSortedLines().getTextLineListByFontSize();
	}

	public void insertSpaces(double d) {
		ensureTextContainerWithSortedLines().insertSpaces(d);
	}

	public void getTextLineByYCoordMap() {
		ensureTextContainerWithSortedLines().getTextLineByYCoordMap();
	}

	public Multiset<Double> createSeparationSet(int decimalPlaces) {
		return ensureTextContainerWithSortedLines().createSeparationSet(decimalPlaces);
	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

//	/** 
//	 * Counter is container counter
//	 * 
//	 * @param analyzerX
//	 * @param suffix
//	 * @param pageAnalyzer
//	 * @return
//	 */
//	public List<AbstractContainer> createContainers() {
//		TextStructurer textStructurer1 = this.getTextStructurer();
//		textStructurer1.getScriptedLineListForCommonestFont();
//		List<TextStructurer> splitList = textStructurer1.splitOnFontBoldChange(-1);
//		List<TextStructurer> textStructurerList = splitList;
//		LOG.trace(" split LIST "+textStructurerList.size());
//		if (textStructurerList.size() > 1) {
//			splitBoldHeaderOnFontSize(textStructurerList);
//		}
//		ensureAbstractContainerList();
//		for (TextStructurer textStructurer : textStructurerList) {
//			ScriptContainer scriptContainer = ScriptContainer.createScriptContainer(textStructurer, pageAnalyzer);
////			scriptContainer.setChunkId(this.getChunkId());
//			abstractContainerList.add(scriptContainer);
//		}
//		return abstractContainerList;
//	}

	private void splitBoldHeaderOnFontSize(List<TextStructurer> textStructurerList) {
		TextStructurer textStructurer0 = textStructurerList.get(0);
		if (textStructurer0.getScriptedLineListForCommonestFont().size() > 1) {
			textStructurer0.getScriptedLineListForCommonestFont();
			List<TextStructurer> splitList = textStructurer0.splitOnFontSizeChange(999);
			List<TextStructurer> fontSplitList = splitList;
			if (fontSplitList.size() > 1) {
				int index = textStructurerList.indexOf(textStructurer0);
				textStructurerList.remove(index);
				for (TextStructurer splitTC : fontSplitList) {
					textStructurerList.add(index++, splitTC);
				}
				LOG.trace("SPLIT FONT");
			}
		}
	}

	public List<SVGText> getHorizontalTextCharacters() {
		ensureRotatedTextAnalyzers();
		return rot0TextAnalyzer.getTextCharacters();
	}

	public void ensureRotatedTextAnalyzers() {
		if (TextOrientation.ANY.equals(textOrientation)) {
			createRotatedTextAnalyzers();
		}
	}

	public void createRotatedTextAnalyzers() {
		if (rot0TextAnalyzer == null ||
			rotPi2TextAnalyzer == null ||
			rotPiTextAnalyzer == null ||
			rot3Pi2TextAnalyzer == null ||
			rotIrregularTextAnalyzer == null) {
			rot0TextAnalyzer = new TextAnalyzer(pageAnalyzer, TextOrientation.ROT_0);
			rotPi2TextAnalyzer = new TextAnalyzer(pageAnalyzer, TextOrientation.ROT_PI2);
			rotPiTextAnalyzer = new TextAnalyzer(pageAnalyzer, TextOrientation.ROT_PI);
			rot3Pi2TextAnalyzer = new TextAnalyzer(pageAnalyzer, TextOrientation.ROT_3PI2);
			rotIrregularTextAnalyzer = new TextAnalyzer(pageAnalyzer, TextOrientation.IRREGULAR);
			addCharactersToAnalyzers();
		}
	}

	private void addCharactersToAnalyzers() {
		for (SVGText text : textCharacters) {
			Transform2 t2 = text.getCumulativeTransform();
			Angle angle = t2.getAngleOfRotationNew();
			Polar polar = new Polar(1.0, angle);
			Real2 r2 = polar.getXY();
			if (r2.dotProduct(TEXT_ROT_0) > MIN_DOT_ANGLE) {
				rot0TextAnalyzer.addCharacter(text);
			} else if (r2.dotProduct(TEXT_ROT_PI2) > MIN_DOT_ANGLE) {
				rotPi2TextAnalyzer.addCharacter(text);
			} else if (r2.dotProduct(TEXT_ROT_PI) > MIN_DOT_ANGLE) {
				rotPiTextAnalyzer.addCharacter(text);
			} else if (r2.dotProduct(TEXT_ROT_3PI2) > MIN_DOT_ANGLE) {
				rot3Pi2TextAnalyzer.addCharacter(text);
			} else {
				rotIrregularTextAnalyzer.addCharacter(text);
			}
		}
	}

	public TextAnalyzer getRot0TextAnalyzer() {
		ensureRotatedTextAnalyzers();
		return rot0TextAnalyzer;
	}
	
	public TextAnalyzer getRotPi2TextAnalyzer() {
		ensureRotatedTextAnalyzers();
		return rotPi2TextAnalyzer;
	}
	
	public TextAnalyzer getRotPiTextAnalyzer() {
		ensureRotatedTextAnalyzers();
		return rotPiTextAnalyzer;
	}
	
	public TextAnalyzer getRot3Pi2TextAnalyzer() {
		ensureRotatedTextAnalyzers();
		return rot3Pi2TextAnalyzer;
	}
	
	public TextAnalyzer getRotIrregularTextAnalyzer() {
		ensureRotatedTextAnalyzers();
		return rotIrregularTextAnalyzer;
	}
	
	public List<SVGText> getRot0TextCharacters() {
		return getRot0TextAnalyzer().getTextCharacters();
	}

	public List<SVGText> getRotPi2TextCharacters() {
		return getRotPi2TextAnalyzer().getTextCharacters();
	}

	public List<SVGText> getRotPiTextCharacters() {
		return getRotPiTextAnalyzer().getTextCharacters();
	}

	public List<SVGText> getRot3Pi2TextCharacters() {
		return getRot3Pi2TextAnalyzer().getTextCharacters();
	}
	
	public List<SVGText> getRotIrregularTextCharacters() {
		return getRotIrregularTextAnalyzer().getTextCharacters();
	}
	
	public void setTextOrientation(TextOrientation textOrientation) {
		this.textOrientation = textOrientation;
	}

	public TextOrientation getTextOrientation() {
		return textOrientation;
	}

	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append("characters: "+((rawCharacterList == null) ? "null" : rawCharacterList.size()));
		sb.append("characters: " + (textCharacters == null ? "null" : textCharacters.size()));
		return sb.toString();
	}
}
