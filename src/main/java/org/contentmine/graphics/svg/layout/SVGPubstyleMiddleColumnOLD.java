package org.contentmine.graphics.svg.layout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;

/** middle column of 3 or single column
 * 
 * @author pm286
 *
 */
public class SVGPubstyleMiddleColumnOLD extends AbstractPubstyle {
	private static final Logger LOG = Logger.getLogger(SVGPubstyleMiddleColumnOLD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String SVG_CLASSNAME = "middleColumn";

	public SVGPubstyleMiddleColumnOLD() {
		super();
	}
	
	public SVGPubstyleMiddleColumnOLD(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
