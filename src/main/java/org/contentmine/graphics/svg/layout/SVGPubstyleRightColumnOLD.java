package org.contentmine.graphics.svg.layout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;

/** right column of 2 or 3
 * 
 * @author pm286
 *
 */
public class SVGPubstyleRightColumnOLD extends AbstractPubstyle {
	private static final Logger LOG = Logger.getLogger(SVGPubstyleRightColumnOLD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String SVG_CLASSNAME = "rightColumn";

	public SVGPubstyleRightColumnOLD() {
		super();
	}
	
	public SVGPubstyleRightColumnOLD(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
