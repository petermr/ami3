package org.contentmine.graphics.svg.normalize;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** normalizes (compacts) texts.
 * will extract common y coordinates  and create x-array
 * 
 * creates chunks whenever style attribute changes
 * 
 * @author pm286
 *
 */
public class TextNormalizer {
	private static final Logger LOG = Logger.getLogger(TextNormalizer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
}
