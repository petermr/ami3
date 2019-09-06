package org.contentmine.graphics.svg.layout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;

/** a footer (can be on any/every page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleFooter extends AbstractPubstyle {
	private static final Logger LOG = Logger.getLogger(SVGPubstyleFooter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String SVG_CLASSNAME = "footer";

	public SVGPubstyleFooter() {
		super();
	}
	
	public SVGPubstyleFooter(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
