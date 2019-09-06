package org.contentmine.graphics.svg.fonts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGTitle;
import org.contentmine.graphics.svg.StyleAttributeFactory;
import org.contentmine.graphics.svg.StyleBundle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/** collection of fonts, normally created from page/s in document.
 * 
 * @author pm286
 *
 */
public class StyleRecordSet implements Iterable<StyleRecord> {
	private static final Logger LOG = Logger.getLogger(StyleRecordSet.class);
	private static final double YMIN = 50;
	private static final double YMAX = 750;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Map<String, StyleRecord> styleRecordByStyle;
	private Multimap<String, StyleRecord> styleRecordByFontName;
	private Multimap<Double, StyleRecord> styleRecordByFontSize;
	private Multimap<String, StyleRecord> styleRecordByFontWeight;
	private Multimap<String, StyleRecord> styleRecordByFontStyle;
	private Multimap<String, StyleRecord> styleRecordByFill;
	private Multimap<String, StyleRecord> styleRecordByStroke;
	private Multimap<Double, StyleRecord> styleRecordByYCoord;
	private TypefaceMaps typefaceMaps;
	private Multiset<String> fontNameSet;
	private boolean normalizeFontNames;
	private Double largestFont;

	public StyleRecordSet() {
		styleRecordByStyle =   new HashMap<String, StyleRecord>();
		styleRecordByFontName =   ArrayListMultimap.create();
		styleRecordByFontSize =   ArrayListMultimap.create();
		styleRecordByFontWeight = ArrayListMultimap.create();
		styleRecordByFontStyle =  ArrayListMultimap.create();
		styleRecordByFill =       ArrayListMultimap.create();
		styleRecordByStroke =     ArrayListMultimap.create();
		styleRecordByYCoord =     ArrayListMultimap.create();
		typefaceMaps = new TypefaceMaps();

	}
	

	/** creates a style from the text.
	 * searches for StyleRecord with that style. If none, creates one
	 * increments the count for character/s in text
	 * 
	 * @param text
	 * @return
	 */
	public StyleRecord getOrCreateStyleRecord(SVGText text) {
		StyleRecord styleRecord = null;
		if (text != null) {
			String cssStyle = StyleRecord.getCSSStyle(text);
			String value = text.getValue();
			value = value.replaceAll("\\s+", "");
			if (cssStyle != null) {
				styleRecord = styleRecordByStyle.get(cssStyle);
				if (styleRecord == null) {
					styleRecord = new StyleRecord(cssStyle);
					styleRecordByStyle.put(cssStyle, styleRecord);
					AddToOtherAnalyzers(cssStyle, styleRecord);
				}
				Double yCoordinate = createYCoordinate(text.getY());
				styleRecord.addYCoordinate(yCoordinate);
				styleRecordByYCoord.put(yCoordinate, styleRecord);
				styleRecord.addNonWhitespaceCharacters(value);
			}
		}
		return styleRecord;
	}
	
	private Double createYCoordinate(Double y) {
		return y == null ? null : Util.format(y, 1);
	}

	private void AddToOtherAnalyzers(String cssStyle, StyleRecord styleRecord) {
		StyleBundle styleBundle = new StyleBundle(cssStyle);
		addStyleRecordByAttribute(styleRecordByFill,       styleBundle.getFill(),       styleRecord);
		addStyleRecordByAttribute(styleRecordByStroke,     styleBundle.getStroke(),     styleRecord);
		addStyleRecordByAttribute(styleRecordByFontSize,   (Double) styleBundle.getFontSize(),   styleRecord);
		addStyleRecordByAttribute(styleRecordByFontWeight, styleBundle.getFontWeight(), styleRecord);
		addStyleRecordByAttribute(styleRecordByFontStyle,  styleBundle.getFontStyle(),  styleRecord);
		addStyleRecordByAttribute(styleRecordByFontName,   styleBundle.getFontName(),   styleRecord);
		addStyleRecordByAttribute(styleRecordByFill,       styleBundle.getFill(),       styleRecord);
	}

	private void addStyleRecordByAttribute(Multimap<String, StyleRecord> styleRecordByAttribute, 
			String attributeValue, StyleRecord styleRecord) {
		if (attributeValue != null) {
			styleRecordByAttribute.put(attributeValue, styleRecord);
		}
	}

	private void addStyleRecordByAttribute(Multimap<Double, StyleRecord> styleRecordByAttribute, 
			Double attributeValue, StyleRecord styleRecord) {
		if (attributeValue != null) {
			styleRecordByAttribute.put(attributeValue, styleRecord);
		}
	}

	public String toString() {
		String s = createStringFromSortedStyleRecords();
		return styleRecordByStyle.toString();
	}

	private String createStringFromSortedStyleRecords() {
		StringBuilder sb = new StringBuilder();
		List<String> styles = createSortedStyles();
		for (String style : styles) {
			StyleRecord styleRecord = styleRecordByStyle.get(style);
			sb.append(styleRecord.toString()+"\n");
		}
		return sb.toString();
	}

	public List<String> createSortedStyles() {
		List<String> styles = new ArrayList<String>(styleRecordByStyle.keySet());
		Collections.sort(styles);
		return styles;
	}

	public List<StyleRecord> createSortedStyleRecords() {
		List<StyleRecord> styleRecords = new ArrayList<StyleRecord>();
		List<String> styles = createSortedStyles();
		for (String style : styles) {
			StyleRecord styleRecord = styleRecordByStyle.get(style);
			styleRecords.add(styleRecord);
		}
		return styleRecords;
	}

	/** gets all StyleRecords with given attribute.
	 * 
	 * @param attName
	 * @param attValue
	 * @return
	 */
	public StyleRecordSet getStyleRecordSet(String attName, String attValue) {
		StyleRecordSet styleRecordSet = new StyleRecordSet();
		if (attName != null && attValue != null) {
			for (String cssStyle : styleRecordByStyle.keySet()) {
				StyleAttributeFactory attributeFactory = new StyleAttributeFactory(cssStyle);
				String value = attributeFactory.getAttributeValue(attName);
				if (attValue.equals(value)) {
					StyleRecord styleRecord = styleRecordByStyle.get(cssStyle);
					styleRecordSet.add(cssStyle, styleRecord);
				}
			}
		}
		return styleRecordSet;
	}

	private void add(String cssStyle, StyleRecord styleRecord) {
		styleRecordByStyle.put(cssStyle, styleRecord);
	}

	public int size() {
		return styleRecordByStyle == null ? 0 : styleRecordByStyle.size();
	}

//	public List<TypefaceMaps> extractSortedTypefaceMaps(String mapsName) {
//		extractTypefaceMaps(mapsName);
//		List<TypefaceMaps> sortedMaps = new ArrayList<TypefaceMaps>(typefaceMaps);
// 	}

	public TypefaceMaps extractTypefaceMaps(String mapsName) {
		extractFontNameSet();
		typefaceMaps.setMapsName(mapsName);
		List<String> fontNames = new ArrayList<String>(fontNameSet.elementSet());
		Collections.sort(fontNames);
		for (String fontName : fontNames) {
			List<StyleRecord> styleRecordList = new ArrayList<StyleRecord>(styleRecordByFontName.get(fontName));
			List<String> fontWeights = getFontAttributes(StyleBundle.FONT_WEIGHT, styleRecordList);
			List<String> fontStyles = getFontAttributes(StyleBundle.FONT_STYLE, styleRecordList);
			Typeface typeface = new Typeface(fontName);
			typeface.setFontWeights(fontWeights);
			typeface.setFontStyles(fontStyles);
			List<String> fills = getFontAttributes(StyleBundle.FILL, styleRecordList);
			typeface.setFills(fills);
			List<String> strokes = getFontAttributes(StyleBundle.STROKE, styleRecordList);
			typeface.setStrokes(strokes);
			List<Double> sizes = getFontAttributeDoubles(StyleBundle.FONT_SIZE, styleRecordList);
			typeface.setFontSizes(sizes);
			typefaceMaps.add(typeface);
		}
		return typefaceMaps;
	}

	private List<String> getFontAttributes(String attributeName, List<StyleRecord> styleRecordList) {
		List<String> attributeList = new ArrayList<String>();
		for (StyleRecord styleRecord : styleRecordList) {
			if (attributeName != null) {
				String attributeValue = null;
				if (attributeName.equals(StyleBundle.FONT_STYLE)) {
					attributeValue = styleRecord.getFontStyle();
				} else if (attributeName.equals(StyleBundle.FONT_WEIGHT)) {
					attributeValue = styleRecord.getFontWeight();
				} else if (attributeName.equals(StyleBundle.FILL)) {
					attributeValue = styleRecord.getFill();
				} else if (attributeName.equals(StyleBundle.STROKE)) {
					attributeValue = styleRecord.getStroke();
				}
				if (attributeValue != null && !attributeList.contains(attributeValue)) {
					attributeList.add(attributeValue);
				}
			}
		}
		return attributeList;
	}

	private List<Double> getFontAttributeDoubles(String attributeName, List<StyleRecord> styleRecordList) {
		List<Double> attributeList = new ArrayList<Double>();
		for (StyleRecord styleRecordAnalyzer : styleRecordList) {
			if (attributeName != null) {
				Double attributeValue = null;
				if (attributeName.equals(StyleBundle.FONT_SIZE)) {
					attributeValue = styleRecordAnalyzer.getFontSize();
				}
				if (!attributeList.contains(attributeValue)) {
					attributeList.add(attributeValue);
				}
			}
		}
		return attributeList;
	}

	public Multiset<String> extractFontNameSet() {
		fontNameSet = HashMultiset.create();
		for (StyleRecord styleRecord : styleRecordByStyle.values()) {
			String fontName = styleRecord.getFontName();
			fontNameSet.add(fontName);
		}
		return fontNameSet;
	}

	public SVGElement createStyledTextBBoxes(List<SVGText> svgTexts) {
		SVGElement g = new SVGG();
		Multiset<Integer> xStarts = HashMultiset.create();
		Multiset<Integer> xEnds = HashMultiset.create();
		for (SVGText svgText : svgTexts) {
			String cssStyle = StyleRecord.getCSSStyle(svgText);
			String content = svgText.getText();
			StyleBundle styleBundle = new StyleBundle(cssStyle);
			String fontStyle = styleBundle.getFontStyle(); 
			String fontWeight = styleBundle.getFontWeight(); 
			double strokeWidth = (StyleBundle.BOLD.equals(fontWeight)) ? 1.0 : 0.2;
			String fill = (StyleBundle.ITALIC.equals(fontStyle)) ? "#ffeeee" : "#ffcccc";
			Real2Range bbox = svgText.getBoundingBox();
			SVGRect rect = SVGRect.createFromReal2Range(bbox);
			rect.setStrokeWidth(strokeWidth);
			rect.setStroke("black");
			rect.setFill(fill);
			xStarts.add((int)(double)bbox.getXMin());
			xEnds.add((int)(double)bbox.getXMax());
			String title = "no title";
			if (content != null) {
				int len = Math.min(200, content.length());
				title = styleBundle.getFontSize() + "; "+
				    content.substring(0, len)+"; "+styleBundle.getFontName();
			}
			rect.appendChild(new SVGTitle(title));
			SVGG gg = new SVGG();
			gg.appendChild(rect);
			gg.appendChild(svgText.copy());
			g.appendChild(gg);
		}
		drawWeightedVerticalLines(g, MultisetUtil.createListSortedByCount(xStarts), "blue");
		drawWeightedVerticalLines(g, MultisetUtil.createListSortedByCount(xEnds), "green");
		return g;
	}

	private void drawWeightedVerticalLines(AbstractCMElement g, List<Multiset.Entry<Integer>> list, String stroke) {
		if (list.size() > 0) {
			double scale = 1.0;
			int commonest = list.get(0).getCount();
			for (Multiset.Entry<Integer> entry : list) {
				int count = entry.getCount();
				if (count > 1) {
					Integer x = entry.getElement();
					SVGLine line = new SVGLine(new Real2(x, YMIN), new Real2(x, YMAX));
					line.setStrokeWidth(scale * (double)count / (double)commonest);
					line.setStroke(stroke);
					g.appendChild(line);
				}
			}
		}
	}

	public Iterator<StyleRecord> iterator() {
		return styleRecordByStyle.values().iterator();
	}


	/** create new StyleRecordSet with normalized FontNames
	 * mainly bold and italic at this stage
	 * 
	 * @return
	 */
	public void  normalizeFontNamesByStyleAndWeight() {
		for (StyleRecord styleRecord : this) {
			StyleBundle styleBundle = styleRecord.getStyleBundle();
			String newFontName = styleBundle.createNormalizedFontName();
			LOG.trace("new fontName "+newFontName);
		}
	}


	public void setNormalizeFontNames(boolean normalizeFontNames) {
		this.normalizeFontNames = normalizeFontNames;
	}


	public Map<String, StyleRecord> getStyleRecordByStyle() {
		return styleRecordByStyle;
	}


	public Multimap<Double, StyleRecord> getStyleRecordByFontSize() {
		return styleRecordByFontSize;
	}


	public Multimap<String, StyleRecord> getStyleRecordByFontWeight() {
		return styleRecordByFontWeight;
	}


	public Multimap<String, StyleRecord> getStyleRecordByFontStyle() {
		return styleRecordByFontStyle;
	}


	public Multimap<String, StyleRecord> getStyleRecordByFill() {
		return styleRecordByFill;
	}


	public Multimap<String, StyleRecord> getStyleRecordByStroke() {
		return styleRecordByStroke;
	}


	public Multimap<Double, StyleRecord> getStyleRecordByYCoord() {
		return styleRecordByYCoord;
	}


	public List<Double> getSortedFontSizesDescending() {
		Multimap<Double, StyleRecord> styleRecordByFontSize = getStyleRecordByFontSize();
		List<Double> sortedFontSizesDescending = new ArrayList<Double>(styleRecordByFontSize.keySet());
		Collections.sort(sortedFontSizesDescending);
		Collections.reverse(sortedFontSizesDescending);
		return sortedFontSizesDescending;
	}

	/** get largest fontsize.
	 * 
	 * @return fontsize (null if no font sizes)
	 */
	public Double getLargestFontSize() {
		List<Double> sortedFontSizes = getSortedFontSizesDescending();
		largestFont = sortedFontSizes.size() == 0 ? null : sortedFontSizes.get(0);
		return largestFont;
	}


	public List<Double> getMinorFontSizes() {
		List<Double> sortedFontSizes = getSortedFontSizesDescending();
		List<Double> minorFontSizes = sortedFontSizes.subList(1,  sortedFontSizes.size());
		return minorFontSizes;
	}

	
}
