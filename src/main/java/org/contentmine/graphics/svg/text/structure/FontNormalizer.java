package org.contentmine.graphics.svg.text.structure;

import java.awt.Color;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** attempts to normalize fonts to make postprocessing easier.
 * 
 * @author pm286
 *
 */
public class FontNormalizer {
	private static final Logger LOG = Logger.getLogger(FontNormalizer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static FontNormalizer DEFAULT_NORMALIZER;
	private static final int DEFAULT_THRESHOLD = 240;
	static {
		DEFAULT_NORMALIZER = new FontNormalizer();
		DEFAULT_NORMALIZER.setBoldThreshold(DEFAULT_THRESHOLD);
	};
	
	private int boldThreshold = DEFAULT_THRESHOLD;

	public FontNormalizer() {
		
	}
	
	/** when normal font is gray rather than black, the threshold at which text becomes bold.
	 * 
	 * @param threshold
	 */
	public void setBoldThreshold(int threshold) {
		this.boldThreshold = threshold;
	}
	
	/** add additional logic here. Maybe externalize it later.
	 * 
	 * @param fontName
	 * @return
	 */
	public static boolean isBoldFontName(String fontName) {
		boolean isBold = false;
		if (fontName != null) {
			fontName = fontName.toLowerCase();
			isBold = 
				fontName.contains("black") ||
				fontName.contains("heavy") ||
				fontName.contains("ultra") ||
				fontName.contains("bold");
		}
		return isBold;
		
	}

	public static FontNormalizer getDefaultNormalizer() {
		return FontNormalizer.DEFAULT_NORMALIZER;
	}
	
	public int getBoldThreshold() {
		return boldThreshold;
	}

	public boolean isBoldColor(String colorS) {
		boolean bold = false;
		if (colorS != null && !colorS.equals("none") && !colorS.equals("")) {
			Color color = null;
			if (colorS.startsWith("#")) {
				String colorRGB = colorS.replaceFirst("#", "");
				color = new Color(Integer.parseInt(colorRGB, 16));
			} else {
				color = Color.getColor(colorS);
			}
			bold = color == null ? null :
				color.getRed() < boldThreshold && color.getGreen() < boldThreshold && color.getBlue() < boldThreshold;
		}
		return bold;
	}
	
	// FIXME normlize font size 0 to 8.0

	
}
