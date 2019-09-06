package org.contentmine.graphics.svg.layout;

import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;

/** a header (can be on any/every page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleHeader extends AbstractPubstyle {
	private static final Logger LOG = Logger.getLogger(SVGPubstyleHeader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
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
