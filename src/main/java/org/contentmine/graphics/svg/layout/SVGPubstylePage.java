package org.contentmine.graphics.svg.layout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;

/** a page (will contain many components
 * 
 * @author pm286
 *
 */
public class SVGPubstylePage extends AbstractPubstyle {
	private static final Logger LOG = Logger.getLogger(SVGPubstylePage.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String SVG_CLASSNAME = "page";

	public SVGPubstylePage() {
		super();
	}
	
	public SVGPubstylePage(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
