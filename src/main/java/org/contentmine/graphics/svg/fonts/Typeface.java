package org.contentmine.graphics.svg.fonts;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** a typeface.
 * "font" is often confused with "typeface". 
 * Here we try to separate typefaces within a StyleRecordSet.
 * 
 * All styles with the same FontName belong to one TypeFace 
 * (i.e. ignore sizes, styles, weight, stroke, etc)
 * Where possible the Bold, Italic qualifiers will be mapped onto a single font. So
 * 
 * Helvetica (any size, weight, style, stroke) maps to Helvetica
 * HelveticaBold, HelveticaOblique, etc map to Helvetica
 * HelevticaNarrow is a different TypeFace
 * 
 * This is empirical. Currently it tries to manage change of style and weight
 * to allow <i> and <b> tags
 * 
 * 
 * 
 * @author pm286
 *
 */
public class Typeface implements Comparable<Typeface> {
	private static final Logger LOG = Logger.getLogger(Typeface.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String typefaceName;
	private List<String> fontStyles;
	private List<String> fontWeights;
	private List<String> fills;
	private List<String> strokes;
	private List<Double> fontSizes;

	public Typeface(String name) {
		this.setTypefaceName(name);
	}

	public String getTypefaceName() {
		return typefaceName;
	}

	private void setTypefaceName(String name) {
		this.typefaceName = name;
	}

	public List<String> getFontStyles() {
		return fontStyles;
	}

	public void setFontStyles(List<String> fontStyles) {
		this.fontStyles = fontStyles;
	}

	public List<String> getFontWeights() {
		return fontWeights;
	}

	public void setFontWeights(List<String> fontWeights) {
		this.fontWeights = fontWeights;
	}
	
	public void setFills(List<String> fills) {
		this.fills = fills;
	}

	public List<String> getFills() {
		return fills;
	}
	
	public void setStrokes(List<String> strokes) {
		this.strokes = strokes;
	}

	public List<String> getStrokes() {
		return strokes;
	}

	public void setFontSizes(List<Double> fontSizes) {
		this.fontSizes = fontSizes;
	}
	
	public List<Double> getFontSizes() {
		return fontSizes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(typefaceName+": ");
		sb.append("weights: "+fontWeights+"; styles: "+fontStyles+"; strokes: "+strokes+
				 "; fills: "+fills+"; fontSizes: "+fontSizes+";"+"\n");
		return sb.toString();
	}

	/** convenience method to extract Typeface names.
	 * 
	 * @param typefaceList
	 * @return list of names
	 */
	public static List<String> extractNames(List<Typeface> typefaceList) {
		List<String> typefaceNameList = new ArrayList<String>();
		for (Typeface typeface : typefaceList) {
			typefaceNameList.add(typeface.getTypefaceName());
		}
		return typefaceNameList;
	}

	public int compareTo(Typeface o) {
		return this.toString().compareTo(((Typeface)o).toString());
	}

}
