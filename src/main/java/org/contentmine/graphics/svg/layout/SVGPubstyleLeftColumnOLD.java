package org.contentmine.graphics.svg.layout;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** a footer (can be on any/every page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleLeftColumnOLD extends AbstractPubstyle {
	private static final Logger LOG = LogManager.getLogger(SVGPubstyleLeftColumnOLD.class);
public final static String SVG_CLASSNAME = "leftColumn";

	public SVGPubstyleLeftColumnOLD() {
		super();
	}
	
	public SVGPubstyleLeftColumnOLD(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}


}
