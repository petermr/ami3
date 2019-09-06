package org.contentmine.graphics.svg.text;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;

/** holds a "paragraph".
 * 
 * Currently driven by <p> elements emitted by Tesseract. These in turn hold lines and words.
 * Still exploratory
 * 
 * @author pm286
 *
 */
public class SVGWord extends SVGG {

	
	private static final String ENDASH = "\\u2013"; // (char)8211;
	private static final String FULL_MINUS = "\\u2212";
	public final static List<String> NON_STANDARD_MINUS = Arrays.asList(new String[]{ENDASH, FULL_MINUS});
	private static final double DELTA_Y_TEXT = 0.3;
	private static final Logger LOG = Logger.getLogger(SVGWord.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String CLASS = "word";
	private double interCharacterFactor = 0.1;
	private boolean isRot90 = false; // simple approach avoiding rotating the whole text
	
	public SVGWord() {
		super();
		this.setSVGClassName(CLASS);
	}

	public SVGWord(AbstractCMElement svgText) {
		this.appendChild(svgText.copy());
	}

	public SVGWord(AbstractCMElement svgText, boolean isRot90) {
		this.appendChild(svgText.copy());
		this.setRot90(isRot90);
	}

	/** from Tesseract
	 * 
	 * @return
	 */
	public SVGText getSVGText() {
		return (SVGText) XMLUtil.getSingleElement(this, "*[local-name()='"+SVGText.TAG+"']");
	}

	public void setRot90(boolean r) {
		this.isRot90 = r;
	}
	/**
	 * gap between end of last word and start of this.
	 * 
	 * if either component is null, return zero
	 * 
	 * @param lastWord preceding word
	 * @return
	 */
	public double gapFollowing(SVGWord lastWord) {
		Real2Range lastBox = (lastWord == null) ? null : lastWord.getChildRectBoundingBox();
		Real2Range thisBox = this.getChildRectBoundingBox();
		return (lastBox == null || thisBox == null) ? 0.0 : getDistance(lastBox, thisBox);
	}

	private double getDistance(Real2Range lastBox, Real2Range thisBox) {
		double dist = -Double.MAX_VALUE;
		if (isRot90) {
			dist = lastBox.getYMin() - thisBox.getYMax();
		} else {
			dist = thisBox.getXMin() - lastBox.getXMax();
		}
		dist = Util.format(dist, 3);
		LOG.trace("dist: "+dist);
		return dist;
	}

	/**
	 * gap between end of last word and start of this.
	 * 
	 * if either component is null, return zero
	 * 
	 * @param lastWord preceding word
	 * @return
	 */
	public double gapBefore(SVGText nextText) {
		if (nextText == null) return Double.NaN;
		Real2Range nextBox = nextText.getBoundingBox();
		Real2Range thisBox = this.getBoundingBox();
		return getDistance(thisBox, nextBox);
	}

	public Real2Range getBoundingBox() {
		SVGText text = this.getSVGText();
		return text == null ? null : text.getBoundingBox();
	}
	
	public Double getFontSize() {
		SVGElement text = this.getSVGText();
		return text == null ? null : text.getFontSize();
	}

	
	@Override
	public String toString() {
		return getSVGText() == null ? null : getSVGText().toString();
	}

	public boolean canAppend(SVGText text) {
		double horizontalGap = gapBefore(text);
		if (horizontalGap > interCharacterFactor * getFontSize()) {
			return false;
		}
		Real2 deltaXY = this.getXY().subtract(text.getXY()); 
		double delta = DELTA_Y_TEXT * this.getFontSize();
		return Math.abs(isRot90 ? deltaXY.getX() : deltaXY.getY()) < delta;
	}

	public void append(SVGText newText) {
		SVGText svgText = this.getSVGText();
		if (svgText != null) {
			String textValue = svgText.getText();
			svgText.getChild(0).detach();
			String newValue = newText.getText();
			svgText.appendChild(textValue + newValue);
			svgText.getBoundingBox();
		}
	}

	public String getStringValue() {
		SVGText text = getSVGText();
		return (text == null) ? null : text.getText();
	}
	
	public Real2 getXY() {
		SVGText text = getSVGText();
		return (text == null) ? null : text.getXY();
	}

	public Double getX() {
		Real2 xy = getXY();
		return xy == null ? null : new Double(xy.getX());
	}
	
	public Double getY() {
		Real2 xy = getXY();
		return xy == null ? null : new Double(xy.getY());
	}
	
	public String getSVGTextValue() {
		AbstractCMElement text = this.getSVGText();
		return text == null ? null : text.getValue();
	}

	public void normalizeMinus() {
		String s = this.getStringValue();
		normalizeChars(s, S_MINUS, SVGWord.NON_STANDARD_MINUS);
	}

	private void normalizeChars(String s, String standardChar, List<String> nonStandardChars) {
		if (s != null) {
			String s0 = s;
			s = replaceNonStandardChars(s0, standardChar, nonStandardChars);
			if (!s.equals(s0)) {
				this.getSVGText().setText(s);
				LOG.trace("nonStandardMinus: "+s0+" => "+s);
			}
		}
	}

	/** replaces all occurrences of the nonStandardChars by the standardChar.
	 * 
	 * @param s
	 * @param standardChar
	 * @param nonStandardChars
	 * @return
	 */
	public static String replaceNonStandardChars(String s, String standardChar, List<String>nonStandardChars) {
		for (String nonStandardChar : nonStandardChars) {
			s = s.replaceAll(nonStandardChar, standardChar);
		}
		return s;
	}

	public void reverseTexts() {
		SVGText svgText = this.getSVGText();
		String text = new StringBuilder(svgText.getText()).reverse().toString();
		svgText.setText(text);
	}

	/** converts Word into Double to see if it is numeric.
	 * 
	 * @return true if can form new Double();
	 */
	public boolean isNumeric() {
		String text = this.getSVGText().getText();
		try {
			new Double(text);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
