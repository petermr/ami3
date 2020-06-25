package org.contentmine.graphics.svg.fonts;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.StyleBundle;

/** a font name with associated styling properties.
 * 
 * Example:
 * Helvetica with explicit attributes for weight, style, size, etc.
 * 
 * @author pm286
 *
 */
public class StyledFont {

	private static final Logger LOG = LogManager.getLogger(StyledFont.class);
private StyleBundle styleBundle;
	private String cssStyle;
	private String fontName;
	
	public StyledFont(String cssStyle) {
		this.cssStyle = cssStyle;
		styleBundle = new StyleBundle(cssStyle);
	}

	public String getFontName() {
		String fontName = styleBundle == null ? null : styleBundle.getFontName();
		LOG.trace(fontName);
		return fontName;
	}
}
