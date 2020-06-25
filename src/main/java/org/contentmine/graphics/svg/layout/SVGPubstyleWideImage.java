package org.contentmine.graphics.svg.layout;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** image that spans the whole page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleWideImage extends AbstractPubstyle {
	private static final Logger LOG = LogManager.getLogger(SVGPubstyleWideImage.class);
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
