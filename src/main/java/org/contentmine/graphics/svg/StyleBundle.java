/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.svg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLConstants;

import nu.xom.Attribute;
import nu.xom.Element;

public class StyleBundle implements XMLConstants {


	private static Logger LOG = Logger.getLogger(StyleBundle.class);

	public static final String CLIP_PATH = "clip-path";
	public static final String DASHARRAY = "stroke-dasharray";
	public static final String FILL = "fill";
	public static final String FONT_FAMILY = "font-family";
	public static final String FONT_SIZE = "font-size";
	public static final String FONT_STYLE = "font-style";
	public static final String FONT_WEIGHT = "font-weight";
	public static final String OPACITY = "opacity";
	public static final String STROKE = "stroke";
	public static final String STROKE_WIDTH = "stroke-width";
	public static final String BOLD = "bold";
	public static final String ITALIC = "italic";
	public static final String BLACK = "black";
	public static final String NONE = "none";
	public static final String NORMAL = "normal";
	// not used in bundle
	private static final String STROKE_LINECAP = "stroke-linecap";
	// not standard SVG
	public static final String FONT_NAME = "font-name";
	public static final String FONT_WIDTH = "font-width";

	// not yet @Deprecated
    public static List<String> BUNDLE_ATTRIBUTES;
	static {
		String[] bundleAttributes = {
				CLIP_PATH,
				FILL,
				FONT_FAMILY,
				FONT_NAME,
				FONT_SIZE,
				FONT_STYLE,
				FONT_WEIGHT,
				OPACITY,
				STROKE,
				STROKE_WIDTH,
				// non-standard
				FONT_NAME,
				FONT_WIDTH,

		};
		BUNDLE_ATTRIBUTES = Arrays.asList(bundleAttributes);
	}

	/** not yet used
	public enum Bundle {
		CLIP_PATH("clip-path"),
		FILL("fill"),
		FONT_FAMILY("font-family"),
		FONT_NAME("font-name"),
		FONT_SIZE("font-size"),
		FONT_STYLE("font-style"),
		FONT_WEIGHT("font-weight"),
		OPACITY("opacity"),
		STROKE("stroke"),
		STROKE_WIDTH("stroke-width");
		private String name;

		private Bundle(String name) {
			this.name = name;
		}
	}
*/
	

	/**
			String clipPath,
			String fill,
			String fontFamily,
			double fontSize,
			String fontStyle,
			String fontWeight,
			double opacity,
			String stroke,
			double strokeWidth
	 */
	public final static StyleBundle DEFAULT_STYLE_BUNDLE = new StyleBundle(
		null,	
		"#000000",
		"sans-serif",
		8.0,
		"normal",
		"normal",
		1.0,
		"#000000",
		0.5
	);
	
	public enum FontWeight {
		NORMAL("normal"),
		BOLD("bold");
		private String value;
		private FontWeight(String value) {
			this.value = value;
		}
	}
	
	public enum FontFamily {
		SERIF("serif"),
		SANS_SERIF("sans-serif"),
		MONOSPACE("monospace");
		private String value;
		private FontFamily(String value) {
			this.value = value;
		}
	}
	
	public enum FontStyle {
		NORMAL("normal"),
		ITALIC("italic");
		private String value;
		private FontStyle(String value) {
			this.value = value;
		}
	}
	
	private String clipPath;
	private String fill;
	private String fontFamily;
	private Double fontSize;
	@SuppressWarnings("unused")
	private String fontStyle;
	private String fontWeight;
	private Double opacity;
	private String stroke;
	private Double strokeWidth;
	// not standard, partially used
	private String fontName;
	private Double fontWidth;
	
	
	private Map<String, String> atts = new HashMap<String, String>();

	static final String STYLE = "style";


	StyleBundle() {
	}
	
	public StyleBundle(String style) {
		processCSSStyle(style);
	}
	
	public StyleBundle(
			String clipPath,
			String fill,
			String fontFamily,
			double fontSize,
			String fontStyle,
			String fontWeight,
			double opacity,
			String stroke,
			double strokeWidth
			) {
			if (clipPath != null && !clipPath.trim().equals(S_EMPTY)) {
				this.clipPath = clipPath.trim();
			}
			if (fill != null && !fill.trim().equals(S_EMPTY)) {
				this.fill = fill.trim();
			}
			if (fontFamily != null && !fontFamily.trim().equals(S_EMPTY)) {
				this.fontFamily = fontFamily.trim();
			}
			if (fontName != null && !fontName.trim().equals(S_EMPTY)) {
				this.fontName = fontName.trim();
			}
			if (fontSize > 0) {
				this.fontSize = new Double(fontSize);
			}
			if (fontStyle != null && !fontStyle.trim().equals(S_EMPTY)) {
				this.fontStyle = fontStyle.trim();
			}
			if (fontWeight != null && !fontWeight.trim().equals(S_EMPTY)) {
				this.fontWeight = fontWeight.trim();
			}
			if (opacity > 0) {
				this.opacity = new Double(opacity);
			}
			if (stroke != null && !stroke.trim().equals(S_EMPTY)) {
				this.stroke = stroke.trim();
			}
			if (strokeWidth > 0) {
				this.strokeWidth = new Double(strokeWidth);
			}
		}
	public StyleBundle(
			String clipPath,
			String fill,
			String fontFamily,
			double fontSize,
			String fontStyle,
			String fontWeight,
			double opacity,
			String stroke,
			double strokeWidth,
			String fontName
			) {
		this(clipPath,
			fill,
			fontFamily,
			fontSize,
			fontStyle,
			fontWeight,
			opacity,
			stroke,
			strokeWidth);
			if (fontName != null && !fontName.trim().equals(S_EMPTY)) {
				this.fontName = fontName.trim();
			}
		}
	public StyleBundle(StyleBundle styleBundle) {
		this.copy(styleBundle);
	}
	
	public void copy(StyleBundle styleBundle) {
		if (styleBundle != null) {
			this.clipPath    = styleBundle.clipPath;
			this.fill        = styleBundle.fill;
			this.fontFamily  = styleBundle.fontFamily;
			this.fontName    = styleBundle.fontName;
			this.fontSize    = styleBundle.fontSize;
			this.fontStyle   = styleBundle.fontStyle;
			this.fontWeight  = styleBundle.fontWeight;
			this.opacity     = styleBundle.opacity;
			this.stroke      = styleBundle.stroke;
			this.strokeWidth = styleBundle.strokeWidth;
			this.atts        = new HashMap<String, String>();
			for (String name : styleBundle.atts.keySet()) {
				atts.put(name, atts.get(name));
			}
		}
	}
	
	void processCSSStyle(String cssStyle) {
		if (cssStyle != null) {
			cssStyle = cssStyle.trim();
			if (!cssStyle.equals(S_EMPTY)) {
				String[] ss = cssStyle.split(S_SEMICOLON);
				for (String s : ss) {
					s = s.trim();
					if (s.equals(S_EMPTY)) {
						continue;
					}
					String[] aa = s.split(S_COLON);
					String attName = aa[0].trim();
					String attVal = aa[1].trim();
					if (attName.equals(CLIP_PATH)) {
						clipPath = attVal;
					} if (attName.equals(FILL)) {
						fill = attVal;
					} else if (attName.equals(FONT_FAMILY)) {
						fontFamily = attVal; 
					} else if (attName.equals(FONT_NAME)) {
						fontName = attVal; 
					} else if (attName.equals(FONT_SIZE)) {
						fontSize = getDouble(attVal); 
					} else if (attName.equals(FONT_STYLE)) {
						fontStyle = attVal; 
					} else if (attName.equals(FONT_WEIGHT)) {
						fontWeight = attVal; 
					} else if (attName.equals(OPACITY)) {
						opacity = getDouble(attVal); 
					} else if (attName.equals(STROKE)) {
						stroke = attVal;
					} else if (attName.equals(STROKE_WIDTH)) {
						strokeWidth = getDouble(attVal); 
					} else {
						atts.put(attName, attVal);
					}
				}
			}
		} else {
//			copy(DEFAULT_STYLE_BUNDLE);
 		}
	}
	
	/** attVal may be null 
	 * 
	 * @param attName
	 * @param attVal
	 */
	public void setSubStyle(String attName, Object attVal) {
		if (attName == null) {
			throw new RuntimeException("null style");
		} else if (attName.equals(CLIP_PATH)) {
			clipPath = (String) attVal;
		} else if (attName.equals(FILL)) {
			fill = (String) attVal;
		} else if (attName.equals(FONT_FAMILY)) {
			fontFamily = (String) attVal; 
		} else if (attName.equals(FONT_NAME)) {
			fontName = (String) attVal; 
		} else if (attName.equals(FONT_SIZE)) {
			fontSize = getDouble(String.valueOf(attVal)); 
		} else if (attName.equals(FONT_STYLE)) {
			fontStyle = (String) attVal; 
		} else if (attName.equals(FONT_WEIGHT)) {
			fontWeight = (String) attVal; 
		} else if (attName.equals(OPACITY)) {
			opacity = getDouble(String.valueOf(attVal)); 
		} else if (attName.equals(STROKE)) {
			stroke = (String) attVal;
		} else if (attName.equals(STROKE_WIDTH)) {
			strokeWidth = getDouble(String.valueOf(attVal)); 
		} else {
			atts.put(attName, String.valueOf(attVal));
		}

	}
	
	public Object getSubStyle(String attName) {
		Object subStyle = null;
		if (attName.equals(CLIP_PATH)) {
			subStyle = getClipPath();
		} else if (attName.equals(FILL)) {
			subStyle = getFill();
		} else if (attName.equals(FONT_FAMILY)) {
			subStyle = getFontFamily();
		} else if (attName.equals(FONT_NAME)) {
			subStyle = getFontName();
		} else if (attName.equals(FONT_SIZE)) {
			subStyle = getFontSize();
		} else if (attName.equals(FONT_WEIGHT)) {
			subStyle = getFontWeight();
		} else if (attName.equals(FONT_STYLE)) {
			subStyle = getFontStyle();
		} else if (attName.equals(OPACITY)) {
			subStyle = getOpacity();
		} else if (attName.equals(STROKE_LINECAP)) {
			LOG.info("ignored style: "+attName);
		} else if (attName.equals(STROKE)) {
			subStyle = getStroke();
		} else if (attName.equals(STROKE_WIDTH)) {
			subStyle = getStrokeWidth();
		} else {
			subStyle = atts.get(attName);
		}
		return subStyle;
	}
	
	void convertAndRemoveExplicitAttributes(GraphicsElement element) {
		for (String attName : StyleBundle.BUNDLE_ATTRIBUTES) {
			Attribute att = element.getAttribute(attName);
			if (att != null) {
				this.setSubStyle(attName, att.getValue());
				att.detach();
			}
		}
		for (String attName : atts.keySet()) {
			this.setSubStyle(attName, atts.get(attName));
		}
		String cssString = this.toString();
		if (cssString != null && cssString.trim().length() > 0) {
			element.addAttribute(new Attribute(STYLE, cssString));
		}
 	}

	void removeStyleAttributesAndMakeExplicit(GraphicsElement element) {
		for (String attName : StyleBundle.BUNDLE_ATTRIBUTES) {
			Object attVal = this.getSubStyle(attName);
			if (attVal != null) {
				element.addAttribute(new Attribute(attName, String.valueOf(attVal)));
				this.removeStyle(attName);
			}
		}
		String cssString = this.toString();
		Attribute styleAttribute = element.getAttribute(STYLE);
		// remove or modify old CSS style
		if (cssString == null || cssString.trim().length() == 0) {
			if (styleAttribute != null) {
				styleAttribute.detach();
			}
		} else {
			// make sure anything left is still kep
			element.addAttribute(new Attribute(STYLE, cssString));
		}
	}

	public void removeStyle(String attName) {
		setSubStyle(attName, null);
	}

	public static Double getDouble(String s) {
		Double d = null;
		if (s != null && !"null".equals(s)) {
			s = GraphicsElement.removeTrailingPx(s);
			s = GraphicsElement.removeTrailingPx(s);
			try {
				d = Double.parseDouble(s);
			} catch (NumberFormatException e) {
				throw new RuntimeException("bad double in style: "+s);
			}
		}
		return d;
	}

	public String getClipPath() {
		return clipPath;
	}
	
	public void setClipPath(String clipPath) {
		this.clipPath = clipPath;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public String getStroke() {
		return stroke;
	}

	public void setStroke(String stroke) {
		this.stroke = stroke;
	}

	public Double getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(Double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public Double getFontSize() {
		return fontSize;
	}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(String fontWeight) {
		this.fontWeight = fontWeight;
	}

	public Double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
	
	public String toString() {
		String s = "";
		s = addString(s, clipPath, CLIP_PATH);
		s = addString(s, fill, FILL);
		s = addString(s, stroke, STROKE);
		s = addDouble(s, strokeWidth, STROKE_WIDTH);
		s = addString(s, fontFamily, FONT_FAMILY);
		s = addDouble(s, fontSize, FONT_SIZE);
		s = addString(s, fontStyle, FONT_STYLE);
		s = addString(s, fontWeight, FONT_WEIGHT);
		s = addDouble(s, opacity, "opacity");
		for (String attName : atts.keySet()) {
			s = addString(s, atts.get(attName), attName);
		}
		return s;
	}

	private String addDouble(String s, Double value, String name) {
		if (value != null && !Double.isNaN(value)) {
			s += ""+name+":"+value+S_SEMICOLON;
		}
		return s;
	}
	private String addString(String s, String value, String name) {
		if (value != null && !value.trim().equals(S_EMPTY)) {
			s += ""+name+":"+value+S_SEMICOLON;
		}
		return s;
	}

	/** set the attributes from a style bundle
	 * 
	 * @param style
	 */
	public void setStyle(Element element, StyleBundle styleBundle) {
		String style = styleBundle == null ? null : styleBundle.toString();
		if (style != null) {
			element.addAttribute(new Attribute(STYLE, style));
		}
	}

	public static String getStyle(Element element) {
		return element.getAttributeValue(STYLE);
	}
	
	public static boolean isBold(Element element) {
		StyleBundle styleBundle = StyleBundle.getStyleBundle(element);
		String weight = styleBundle == null ? null : styleBundle.getFontWeight();
		return StyleBundle.FontWeight.BOLD.equals(weight);
	}
	
	public static boolean isItalic(Element element) {
		StyleBundle styleBundle = StyleBundle.getStyleBundle(element);
		String fontStyle = styleBundle == null ? null : styleBundle.getFontStyle();
		return StyleBundle.FontStyle.ITALIC.equals(fontStyle);
	}

	public static String getFill(Element element) {
		StyleBundle styleBundle = StyleBundle.getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getFill();
	}

	public static Double getFontSize(Element element) {
		StyleBundle styleBundle = StyleBundle.getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getFontSize();
	}

	public static Double getOpacity(Element element) {
		StyleBundle styleBundle = StyleBundle.getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getOpacity();
	}

	public static Double getStrokeWidth(Element element) {
		StyleBundle styleBundle = StyleBundle.getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getStrokeWidth();
	}

	public static String getStroke(Element element) {
		StyleBundle styleBundle = StyleBundle.getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getStroke();
	}

	public static String getFontWeight(Element element) {
		StyleBundle styleBundle = getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getFontWeight();
	}

	public static String getFontStyle(Element element) {
		StyleBundle styleBundle = getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getFontStyle();
	}

	public static String getFontFamily(Element element) {
		StyleBundle styleBundle = getStyleBundle(element);
		return styleBundle == null ? null : styleBundle.getFontFamily();
	}

	public static StyleBundle getStyleBundle(Element element) {
		String style = element.getAttributeValue(STYLE);
		return style == null ? null : new StyleBundle(style);
	}

	public String getCSSStyle() {
		StringBuilder sb = new StringBuilder();
		addNameValueTo(sb, CLIP_PATH, clipPath);
		addNameValueTo(sb, FILL, fill);
		addNameValueTo(sb, FONT_FAMILY, fontFamily);
		addNameValueTo(sb, FONT_SIZE, fontSize);
		addNameValueTo(sb, FONT_STYLE, fontStyle);
		addNameValueTo(sb, FONT_WEIGHT, fontWeight);
		addNameValueTo(sb, OPACITY, opacity);
		addNameValueTo(sb, STROKE, stroke);
		addNameValueTo(sb, STROKE_WIDTH, strokeWidth);
		addNameValueTo(sb, FONT_NAME, fontName);
		addNameValueTo(sb, FONT_WIDTH, fontWidth);
		return sb.toString();
		
	}

	private void addNameValueTo(StringBuilder sb, String name, String value) {
		if (value != null) {
			sb.append(createNameValue(name, value));
		}
	}

	private void addNameValueTo(StringBuilder sb, String name, Double value) {
		if (value != null) {
			sb.append(createNameValue(name, String.valueOf(value)));
		}
	}

	private String createNameValue(String name, String value) {
		return name+":"+value+";";
	}

	/** empirically guesses the "parent font name" for bold and italic modifiers.
	 * 
	 * @return
	 */
	public String createNormalizedFontName() {
		LOG.trace(fontName + "// "+fontStyle+" // "+fontWeight);
		String newFontName = normalizeBold(fontName);
		newFontName = normalizeItalic(newFontName);
		return newFontName;
	}

	private String normalizeBold(String fontName) {
		// examples are FooBold Foo-Bold, Foo.B, Foo+20
		String bold1 = "(\\-?Bold)";
		String bold2 = "(\\.B$)";
		String bold3 = "(\\+20$)";
		String newFontName = fontName.replaceAll("(" + bold1 + "|" + bold2 + "|" + bold3 + ")", "");
		if (!newFontName.equals(fontName)) {
			if (!StyleBundle.BOLD.equals(fontWeight)) {
				LOG.warn("Bold font?? without explicit weight: "+fontName);
				fontWeight = StyleBundle.BOLD;
			}
		}
		return newFontName;
	}

	private String normalizeItalic(String fontName) {
		// examples are FooBold Foo-Bold, Foo.B, Foo+20
		String ital1 = "(\\-?Italic)";
		String ital2 = "(\\-?Oblique)";
		String ital3 = "(\\.I$)";
		String newFontName = fontName.replaceAll("(" + ital1 + "|" + ital2 + "|" + ital3 + ")", "");
		if (!newFontName.equals(fontName)) {
			if (!StyleBundle.BOLD.equals(fontWeight)) {
				LOG.warn("Bold font?? without explicit weight: "+fontName);
				fontWeight = StyleBundle.BOLD;
			}
		}
		return newFontName;
	}

	/** match fontWeights
	 * assume null == normal
	 * 
	 * @param styleBundle
	 * @return
	 */
	public boolean matchesFontWeight(StyleBundle styleBundle2) {
		String fontWeight2 = styleBundle2.getFontWeight();
		fontWeight2 = fontWeight2 == null ? NORMAL : fontWeight2;
		String fontWeight1 = this.fontWeight == null ? NORMAL : fontWeight;
		return fontWeight1.equals(fontWeight2);
	}
	
	/** match fontStyles
	 *  assume null == normal
	 * 
	 * @param styleBundle
	 * @return
	 */
	public boolean matchesFontStyle(StyleBundle styleBundle2) {
		String fontStyle2 = styleBundle2.getFontStyle();
		fontStyle2 = fontStyle2 == null ? NORMAL : fontStyle2;
		String fontStyle1 = this.fontStyle == null ? NORMAL : fontStyle;
		return fontStyle1.equals(fontStyle2);
	}
	
	/** 
	 * 
	 * @param styleBundle
	 * @return
	 */
	public boolean matchesFontSize(StyleBundle styleBundle, double tolerRatio) {
		Double fontSize2 = styleBundle.getFontSize();
		if (fontSize == null || fontSize2 == null) return false;
		// normalize ratio to < 1.0
		double ratio = tolerRatio < 1 ? tolerRatio : 1 / tolerRatio;
		return  (fontSize / fontSize2 > ratio && fontSize2  / fontSize > ratio) ;
	}
	
	/**
	 * null values match
	 * @param styleBundle
	 * @return
	 */
	public boolean matchesStrokeWidth(StyleBundle styleBundle, double tolerRatio) {
		Double strokeWidth2 = styleBundle.getStrokeWidth();
		if (strokeWidth == null || strokeWidth2 == null) return true;
		// normalize ratio to < 1.0
		double ratio = tolerRatio < 1 ? tolerRatio : 1 / tolerRatio;
		return  (strokeWidth / strokeWidth2 > ratio && strokeWidth2  / strokeWidth > ratio) ;
	}
	
	/** 
	 * null returns false
	 * @param styleBundle
	 * @return
	 */
	public boolean matchesStroke(StyleBundle styleBundle) {
		String stroke2 = styleBundle.getStroke();
		if (stroke == null) {
			return (stroke2 == null);
		}
		return  stroke.equals(stroke2) ;
	}
	
	/** assume null == false
	 * 
	 * @param styleBundle
	 * @return
	 */
	public boolean matchesFill(StyleBundle styleBundle) {
		String fill2 = styleBundle.getFill();
		if (fill == null) {
			return (fill2 == null);
		}
		return  fill.equals(fill2) ;
	}
	
	/** 
	 * difficult because foo-bold should match foo
	 * @param styleBundle
	 * @return
	 */
	public boolean matchesFontFamily(StyleBundle styleBundle) {
		String fontFamily2 = styleBundle.getFontFamily();
		if (fontFamily == null || fontFamily2 == null) {
			throw new RuntimeException("cannot have null font families");
		}
		if (fontFamily.equals(fontFamily2)) {
			LOG.trace("MATCH");
			return true;
		} else {
			LOG.trace(fontFamily + " != " + fontFamily2);
			return false;
		}
	}
}
