package org.contentmine.graphics.svg.layout;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** an abstract (normally on page 1)
 * 
 * @author pm286
 *
 */
public class SVGPubstyleSection extends AbstractPubstyle {
	private static final Logger LOG = LogManager.getLogger(SVGPubstyleSection.class);
public final static String SVG_CLASSNAME = "page";

	public SVGPubstyleSection() {
		super();
	}
	
	public SVGPubstyleSection(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
