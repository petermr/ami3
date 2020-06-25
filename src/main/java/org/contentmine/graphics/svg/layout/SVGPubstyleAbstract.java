package org.contentmine.graphics.svg.layout;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** an abstract (normally on page 1)
 * 
 * @author pm286
 *
 */
public class SVGPubstyleAbstract extends AbstractPubstyle {
	private static final Logger LOG = LogManager.getLogger(SVGPubstyleAbstract.class);
public final static String SVG_CLASSNAME = "abstract";

	public SVGPubstyleAbstract() {
		super();
	}
	
	public SVGPubstyleAbstract(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
