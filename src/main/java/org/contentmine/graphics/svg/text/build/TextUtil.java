package org.contentmine.graphics.svg.text.build;

import java.text.Normalizer;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGText;

public class TextUtil {

	private static final double LIGATURE_SCALE = 0.7;
	private static final Logger LOG = Logger.getLogger(TextUtil.class);
	private static final Double MIN_FONT_WIDTH = 1.0; // in units of 1000.
	private static final Double DEFAULT_FONT_WIDTH = 750.0; /*500.0*/
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** works but not in this context.
	 * 
	 * @param s
	 * @return
	 */
	public static AbstractCMElement normalize(String s) {
		if (!Normalizer.isNormalized(s, Normalizer.Form.NFKC)) {
//			SVGElement parent = (SVGElement) child.getParent();
//			int index = parent.indexOf(child);
//			parent.removeChild(index);
//			double width = childText.getSVGXFontWidth() / 1000.;
//			if (width <= 0.0) {
//				width = 1.0;
//			}
//			double height = childText.getFontSize();
//			width *= height;
//			s = Normalizer.normalize(s, Normalizer.Form.NFKC);
//			LOG.trace(">>>"+s+"; "+width);
//			Real2 xy0 = childText.getXY();
//			int len = s.length();
//			width /= len;
//			for (int i = 0; i < len; i++) {
//				Real2 xy = xy0.plus(new Real2((double)i * width, 0.0));
//				SVGText ss = new SVGText(xy, String.valueOf(s.charAt(i)));
//				childTextList.add(ss);
//			}
//			for (int i = len - 1; i >= 0; i--) {
//				Real2 xy = xy0.plus(new Real2((double)i * width, 0.0));
//				SVGText ss = new SVGText(xy, String.valueOf(s.charAt(i)));
//				parent.insertChild(ss, index);
//			}
		}
		return null;
	}

	/** normalize text according to Normalizer
	 * 
	 * @param svgText text to normalize, changed in situ
	 * @param form from Normalizer
	 * @return true if changes made
	 */
	public static boolean normalize(SVGText svgText, Normalizer.Form form) {
		boolean normalized = false;
		TextUtil.fudgeZeroFontWidth(svgText);
		String string = svgText.getValue();
		
		if (!Normalizer.isNormalized(string, form)) {
			AbstractCMElement parent = (AbstractCMElement) svgText.getParent();
			int index = parent.indexOf(svgText);
			parent.removeChild(index);
			double unscaledWidth = svgText.getSVGXFontWidth() / 1000.;
			if (unscaledWidth <= 0.0) {
				unscaledWidth = 1.0;
			}
			double fontSize = svgText.getFontSize();
//			double scaledWidth = unscaledWidth * fontSize;
			String newString = Normalizer.normalize(string, Normalizer.Form.NFKC);
			if (" ff fi ffi fl ffl ct ".indexOf(" "+newString+" ") == -1) {
				LOG.trace(">replaced>"+string+"/"+(int)string.charAt(0)+"; by "+newString+"/"+(int)newString.charAt(0));
			}
			Real2 xy0 = svgText.getXY();
			int len = newString.length();
			double ligatureScale = len == 1 ? 1.0 : LIGATURE_SCALE * len;
			double newUnscaledWidth = unscaledWidth / ligatureScale;
			double newScaledWidth = newUnscaledWidth * fontSize;
			StringBuilder sb = new StringBuilder();
			for (int i = len - 1; i >= 0; i--) {
				String newChar = String.valueOf(newString.charAt(i));
				Real2 xy = xy0.plus(new Real2((double)i * newScaledWidth, 0.0));
				// this makes sure the attributes are copied
				SVGText svgTextNew = new SVGText(svgText);
				svgTextNew.setXY(xy);
				svgTextNew.setText(newChar);
				svgTextNew.setSVGXFontWidth(1000. * newScaledWidth);
				parent.insertChild(svgTextNew, index);
			}
			normalized = true;
		}
		return normalized;
	}

	/** some character do not have font width specified. add a "reasonable" default
	 * @param svgText
	 */
	public static void fudgeZeroFontWidth(SVGText svgText) {
		Double width = svgText.getSVGXFontWidth();
		if (width == null || width < MIN_FONT_WIDTH) {
			svgText.setSVGXFontWidth(DEFAULT_FONT_WIDTH);
		}
	}

	/** normalizes text according to Unicode Normalizer.
	 * 
	 * @param svgChunk changed in situ.
	 * @param form from Normalizer
	 * @return true if changes made
	 */
	public static boolean normalize(AbstractCMElement svgChunk, Normalizer.Form form) {
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgChunk);
		boolean normalized = normalize(textList, form);
		return normalized;
	}

	/**
	 * 
	 * @param textList list to normalize
	 * @param form normalizer
	 * @return 
	 */
	public static boolean normalize(List<SVGText> textList, Normalizer.Form form) {
		boolean normalized = false;
		for (SVGText text : textList) {
			normalized |= normalize(text, form);
		}
		return normalized;
	}

	/** determines whether one or more characters has a possible descender in glyph.
	 * currently {} [] () // will add more
	 * 
	 * @param txt
	 * @return
	 */
	public static boolean containsDescender(String txt) {
		if (txt != null) return false;
		for (int i = 0; i < txt.length(); i++) {
			char c = txt.charAt(i);
			if (c == '{' || c == '}' || c == '(' || c ==')' || c == '[' || c == ']') {
				return true;
			}
		}
		return false;
	}

}
