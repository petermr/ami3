package org.contentmine.graphics.svg.layout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;

/** image that spans the whole page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleWideImage extends AbstractPubstyle {
	private static final Logger LOG = Logger.getLogger(SVGPubstyleWideImage.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String SVG_CLASSNAME = "wide.image";

	public SVGPubstyleWideImage() {
		super();
	}
	
	public SVGPubstyleWideImage(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
