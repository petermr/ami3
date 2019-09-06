package org.contentmine.graphics.svg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.normalize.AttributeComparer;

import nu.xom.Attribute;
import nu.xom.Node;

/** supports CSS-like style attribute list of name-value pairs
 * 
 * current strategy is to use a single style attribute rather than individual "old-style" fill=, stroke=, etc.
 * this makes it easier to keep track when new attributes are added.
 * 
 * thus
 *   SVGElement circle = new SVGCircle();
 *   circle.setFill("bar")
 *   will create <svg:circle style="fill:bar;" ...
 *   
 *   if style already uses fill, it will be updated
 *   
 *   circle.getFill()
 *   will use the 'style' attribute
 *   
 * 
 * @author pm286
 *
 */
public class StyleAttributeFactory {
	private static final Logger LOG = Logger.getLogger(StyleAttributeFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public enum AttributeStrategy {
		KEEP,
		REMOVE,
		OVERWRITE,
		MERGE
	}
	private static final String STYLE = "style";
	public static final boolean CHECK_DUPLICATES = true;
	public static final boolean NO_CHECK_DUPLICATES = false;
	
	private Map<String, String> styleMap;
	private String attributeValue;

	public StyleAttributeFactory() {
		styleMap = new HashMap<String, String>();
	}

	/** create StyleAttributeFactory from packed CSS name-values
	 * 
	 * @param cssValue
	 */
	public StyleAttributeFactory(String cssValue) {
		this();
		splitAndAddAttributeToMap(cssValue);
	}

	public StyleAttributeFactory(StyleAttributeFactory styleAttributeFactory) {
		this(styleAttributeFactory.getAttributeValue());
	}

	/**
	 * removes all old-style style attributes (fill="foo", etc) and creates or updates a
	 * single style attribute.
	 * This should only be used to clean up old style attributes. new code will always create
	 * style attributes
	 * 
	 * @param element
	 * @param strategy if OVERWRITE will overwrite values in style attribute with old style values
	 * @return new Style attribute 
	 */
	public static StyleAttributeFactory createUpdatedStyleAttribute(GraphicsElement element, AttributeStrategy strategy) {
		StyleAttributeFactory oldStyleAttributeFactory = element.createStyleAttributeFactoryFromOldStyles();
		StyleAttributeFactory existingStyleAttributeFactory = element.getExistingStyleAttributeFactory();
		StyleAttributeFactory newStyleAttributeFactory = null;
		if (strategy == null) {
			throw new RuntimeException("null strategy");
		} else if (AttributeStrategy.OVERWRITE == strategy) {
			element.removeOldStyleAttributes();
			newStyleAttributeFactory = oldStyleAttributeFactory;
		} else if (AttributeStrategy.KEEP == strategy) {
			newStyleAttributeFactory = existingStyleAttributeFactory;
		} else if (AttributeStrategy.REMOVE == strategy) {
			throw new RuntimeException("REMOVE NYI");
		} else if (AttributeStrategy.MERGE == strategy) {
			newStyleAttributeFactory = oldStyleAttributeFactory.createMergedAttributeFactory(existingStyleAttributeFactory);
			element.removeOldStyleAttributes();
		} else {
			throw new RuntimeException("unknown "+strategy);
		}
		return newStyleAttributeFactory;
	}

	public static void convertElementAndChildrenFromOldStyleAttributesToCSS(GraphicsElement element) {
		
		convertOldStyleAttributesToCSSAndDelete(element);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof GraphicsElement) {
				convertElementAndChildrenFromOldStyleAttributesToCSS((GraphicsElement)child);
			}
		}
	}

	public static void convertOldStyleAttributesToCSSAndDelete(GraphicsElement element) {
		StyleAttributeFactory oldStyleAttributeFactory = element.createStyleAttributeFactoryFromOldStyles();
		String style = element.getAttributeValue(STYLE);
		if (style != null && style.trim().length() != 0) {
			StyleAttributeFactory styleAttributeFactory = new StyleAttributeFactory(style);
			oldStyleAttributeFactory = styleAttributeFactory.createMergedAttributeFactory(styleAttributeFactory);
		}
		oldStyleAttributeFactory.addStyleAttribute(element);
		oldStyleAttributeFactory.deleteOldAttributes(element);
	}

	private void deleteOldAttributes(GraphicsElement element) {
		for (String attName : styleMap.keySet()) {
			deleteAttribute(element, attName);
		}
	}

	private static void deleteAttribute(GraphicsElement element, String attName) {
		Attribute att = element.getAttribute(attName);
		if (att != null) {		
			att.detach();
		}
	}

	/** adds old-style attribute to Style attribute
	 * 
	 * @param att
	 */
	public void addToMap(Attribute att, boolean checkDuplicates) {
		String attName = att.getLocalName();
		String attValue = att.getValue();
		addToMap(checkDuplicates, attName, attValue);
	}

	public void addToMap(boolean checkDuplicates, String attName, String attValue) {
		if (checkDuplicates && styleMap.containsKey(attName)) {
			//throw new RuntimeException("Duplicate attribute name: "+attName);
			LOG.trace("Duplicate attribute name: "+attName);
		}
		styleMap.put(attName, attValue);
	}
	
	public void addToMap(String attName, String attValue) {
		this.addToMap(false, attName, attValue); 
	}
	
	/** returns CSS-like string sorted by attribute names.
	 * 
	 * @return
	 */
	public String getAttributeValue() {
		if (attributeValue == null) {
			List<String> attNames = Arrays.asList(styleMap.keySet().toArray(new String[0]));
			Collections.sort(attNames);
			StringBuilder sb = new StringBuilder();
			for (String attName : attNames) {
				String units = getUnits(attName);
				sb.append(attName+":"+GraphicsElement.addUnits(styleMap.get(attName), units)+";");
			}
			attributeValue = sb.toString();
		}
		return attributeValue;
	}

	/** merges 2 SAFs.
	 * attributes in styleAttributeFactory will overwrite 'this'
	 * 
	 * @param styleAttributeFactory to merge with this
	 * @return merged SAF. 'this' is NOT Affected
	 */
	public StyleAttributeFactory createMergedAttributeFactory(StyleAttributeFactory styleAttributeFactory) {
		StyleAttributeFactory newAttributeFactory = new StyleAttributeFactory();
		newAttributeFactory.putEntries(this.styleMap);
		if (styleAttributeFactory != null) newAttributeFactory.putEntries(styleAttributeFactory.styleMap);
		return newAttributeFactory;
	}


	// ===================================
	
	/** certain attributes (currently only font-size) sometimes require units
	 * 
	 * @param attName
	 * @return units or empty string
	 */
	private String getUnits(String attName) {
		String units = attName.equals(StyleBundle.FONT_SIZE) ? GraphicsElement.PX : "";
		return units;
	}
	
	private Attribute createAttribute() {
		Attribute attribute = new Attribute(STYLE, getAttributeValue());
		return attribute;
	}

	private void putEntries(Map<String, String> styleMap) {
		for (Map.Entry<String, String> entry : styleMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value == null && this.styleMap.get(key) != null) {
				this.styleMap.remove(key);
			} else {
				this.styleMap.put(key, value);
			}
		}
	}

	private void updateMap(Attribute styleAtt) {
		if (styleAtt != null) {
			String attValue = styleAtt.getValue();
			splitAndAddAttributeToMap(attValue);
		}
	}

	private void splitAndAddAttributeToMap(String attValue) {
		String[] values = attValue == null ? new String[] {} : attValue.split("\\s*;\\s*");
		for (String value : values) {
			if (value.trim().length() == 0) continue;
			String[] splits = value.split("\\s*:\\s*");
			if (splits.length != 2) {
				throw new RuntimeException("bad style attribute "+value+"; in "+attValue);
			}
			String styleName = splits[0];
			String styleValue = splits[1];
			if (!AttributeComparer.STYLE_SET.contains(styleName)) {
				LOG.warn("Unknown style name ignored: "+styleName);
			} else {
				styleMap.put(styleName, styleValue);
			}
		}
	}

	public String getAttributeValue(String attName) {
		return styleMap.get(attName);
	}

	public Map<String, String> getStyleMap() {
		return styleMap;
	}

	/** applies heuristics to analyze FontFamily name.
	 * 
	 * @param style
	 * @return
	 */
	public boolean expandStyle(String style) {
		String fontFamily0 = this.getAttributeValue(StyleBundle.FONT_FAMILY);
		String fontFamilyEnd = fontFamily0;
		if (!GraphicsElement.isEmptyValue(fontFamily0)) {
			String fontFamily = fontFamily0.replaceAll("^[A-Z]{6}\\+", ""); // strip prefix
			String fontFamilyB = fontFamily.replaceAll("(\\-?[Bb][Oo][Ll][Dd]|\\.[Bb])", ""); // Bold | .b
			boolean bold = !fontFamilyB.equals(fontFamily);
			fontFamilyEnd = fontFamilyB.replaceAll("(\\-?[Ii][Tt][Aa][Ll]([Ii][Cc])?|\\.[Ii])", ""); // Ital(ic)? | .i
			boolean italic = !fontFamilyEnd.equals(fontFamilyB);
			this.addToMap(StyleBundle.FONT_FAMILY, fontFamilyEnd);
			if (bold) {
				this.addToMap(StyleBundle.FONT_WEIGHT, StyleBundle.BOLD);
			}
			if (italic) {
				this.addToMap(StyleBundle.FONT_STYLE, StyleBundle.ITALIC);
			}
		}
		return fontFamily0 != null && !fontFamilyEnd.equals(fontFamily0);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CSS: "+styleMap.toString());
		return sb.toString();
	}

	public void addStyleToMap(Attribute attribute) {
		if (STYLE.equals(attribute.getLocalName())) {
			StyleAttributeFactory attributeFactory = new StyleAttributeFactory(attribute.getValue());
			StyleAttributeFactory mergedAttributeFactory = this.createMergedAttributeFactory(attributeFactory);
			this.styleMap = mergedAttributeFactory.styleMap;
		}
	}

	/** adds or replaces STYLE attribute by contents of factory.
	 * 
	 * if SAF is null or empty will DELETE existing style attribute.
	 * 
	 * @param element STYLE attribute will be changed
	 */
	void addStyleAttribute(GraphicsElement element) {
		String value = getAttributeValue();
		if (("").equals(value)) value = null;
		element.addAttribute(new Attribute(StyleAttributeFactory.STYLE, getAttributeValue()));
	}

	public static void deleteOldStyleAttributes(SVGShape shape) {
		for (String[] ss : AttributeComparer.STYLES) {
			for (String attName : ss) {
				StyleAttributeFactory.deleteAttribute(shape, attName);
			}
		}
	}

	public boolean isBold() {
		String fontWeight = styleMap.get(StyleBundle.FONT_WEIGHT);
		return StyleBundle.BOLD.equals(fontWeight);
	}

	public boolean isItalic() {
		return StyleBundle.ITALIC.equals(styleMap.get(StyleBundle.FONT_STYLE));
	}

	/** compares this with another factory.
	 * if this contains a fontWeight of bold and normal does not and
	 * all other attribute values are equal return true
	 * similarly for fontStyle
	 * 
	 * Effectively this returns true if this style is the same as normal except for
	 * fontWeight or style.
	 * 
	 * @param normalAttributeFactory
	 * @return
	 */
	public boolean isBoldOrItalicSuperset(StyleAttributeFactory normalAttributeFactory) {
//		LOG.debug("\n"+this+"\n"+normalAttributeFactory+"\n");
		StyleAttributeFactory thisCopy = new StyleAttributeFactory(this);
		StyleAttributeFactory normalCopy = new StyleAttributeFactory(normalAttributeFactory);
		Set<String> thisKeySet = thisCopy.styleMap.keySet();
		Set<String> normalKeySet = normalCopy.styleMap.keySet();
		// remove all nonWeight/Style
		List<String> thisKeyList = new ArrayList<String>(thisKeySet);
		boolean bold = false;
		boolean italic = false;
		for (String key : thisKeyList) {
			// remove all non-weightStyle attribute
			if (key.equals(StyleBundle.FONT_WEIGHT)) {
				String thisWeight = thisCopy.styleMap.get(StyleBundle.FONT_WEIGHT);
				String normalWeight = normalCopy.styleMap.get(StyleBundle.FONT_WEIGHT);
				bold = StyleBundle.BOLD.equals(thisWeight) && !(StyleBundle.BOLD.equals(normalWeight));
			} else if (key.equals(StyleBundle.FONT_STYLE)) {
				String thisStyle = thisCopy.styleMap.get(StyleBundle.FONT_STYLE);
				String normalStyle = normalCopy.styleMap.get(StyleBundle.FONT_STYLE);
				italic = StyleBundle.ITALIC.equals(thisStyle) && !(StyleBundle.ITALIC.equals(normalStyle));
			}
			thisKeySet.remove(key);
			normalKeySet.remove(key);
		}
		
		if (thisKeySet.size() == 0 && normalKeySet.size() ==  0) {
			if (bold || italic) {
//				LOG.debug("TRUE");
				return true;
			}
		}
		return false;
	}

	/** font-name is non-standard SVG
	 * we are gradually adding it as additional attribute
	 * 
	 * @param text
	 */
	public void addFontName(SVGText text) {
		String fontName = text.getSVGXFontName();
		addFontName(fontName);
	}

	public void addFontName(String fontName) {
		if (fontName != null) {
			fontName = StyleAttributeFactory.stripPublisherCode(fontName);
			addToMap(SVGText.FONT_NAME, fontName);
		}
	}

	public static String stripPublisherCode(String fontName) {
		fontName = fontName.replaceAll("^[A-Z]{6}\\+", "");
		return fontName;
	}

	public void addFontStyle(SVGText text) {
		String fontStyle = text.getFontStyle();
		addFontStyle(fontStyle);
	}

	public void addFontStyle(String fontStyle) {
		if (fontStyle != null) {
			addToMap(StyleBundle.FONT_STYLE, fontStyle);
		}
	}

	public void addFontWeight(SVGText text) {
		String fontWeight = text.getFontWeight();
		addFontWeight(fontWeight);
	}

	public void addFontWeight(String fontWeight) {
		if (fontWeight != null) {
			addToMap(StyleBundle.FONT_WEIGHT, fontWeight);
		}
	}

	public  void addFontSize(SVGText text) {
		Double fontSize = text.getFontSize();
		addFontSize(fontSize);
	}

	public void addFontSize(Double fontSize) {
		if (fontSize != null) {
			addToMap(StyleBundle.FONT_SIZE, String.valueOf(fontSize));
		}
	}
	
	public  void addFill(SVGText text) {
		String fill = text.getFill();
		addFill(fill);
	}

	public void addFill(String fill) {
		if (fill != null) {
			addToMap(StyleBundle.FILL, fill);
		}
	}
	
	public  void addStroke(SVGText text) {
		String stroke = text.getStroke();
		addStroke(stroke);
	}

	public void addStroke(String stroke) {
		if (stroke != null) {
			addToMap(StyleBundle.STROKE, stroke);
		}
	}
	
	/** font-width is non-standard SVG.
	 * 
	 * @param text
	 */
	public  void addFontWidth(SVGText text) {
		Double fontWidth = text.getSVGXFontWidth();
		addFontWidth(fontWidth);
	}

	public void addFontWidth(Double fontWidth) {
		if (fontWidth != null) {
			addToMap(StyleBundle.FONT_WIDTH, String.valueOf(fontWidth));
		}
	}
	
}
