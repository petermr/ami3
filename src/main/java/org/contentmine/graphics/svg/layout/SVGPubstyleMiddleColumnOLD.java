package org.contentmine.graphics.svg.layout;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** middle column of 3 or single column
 * 
 * @author pm286
 *
 */
public class SVGPubstyleMiddleColumnOLD extends AbstractPubstyle {
	private static final Logger LOG = LogManager.getLogger(SVGPubstyleMiddleColumnOLD.class);
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
