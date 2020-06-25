package org.contentmine.graphics.svg.layout;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** a header (can be on any/every page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleHeader extends AbstractPubstyle {
	private static final Logger LOG = LogManager.getLogger(SVGPubstyleHeader.class);
public final static String SVG_CLASSNAME = "header";
	private Map<String, String> temporaryKV;

	public SVGPubstyleHeader() {
		super();
	}
	
	public SVGPubstyleHeader(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}



}
