package org.contentmine.graphics.svg;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;

/** only used for display, mainly debugging.
 * 
 *         <stop offset="0%" style="stop-color:rgb(255,255,255);stop-opacity:0" />

 * @author pm286
 *
 */
public class SVGStop extends SVGElement {
	private static final Logger LOG = Logger.getLogger(SVGLinearGradient.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "stop";
	public final static String OFFSET = "offset";

	public SVGStop() {
		super(TAG);
	}

	public void setOffsetPercent(int pc) {
		this.addAttribute(new Attribute(OFFSET, pc+"%"));
	}
}
