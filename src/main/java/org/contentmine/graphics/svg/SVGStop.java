package org.contentmine.graphics.svg;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Attribute;

/** only used for display, mainly debugging.
 * 
 *         <stop offset="0%" style="stop-color:rgb(255,255,255);stop-opacity:0" />

 * @author pm286
 *
 */
public class SVGStop extends SVGElement {
	private static final Logger LOG = LogManager.getLogger(SVGStop.class);
public final static String TAG = "stop";
	public final static String OFFSET = "offset";

	public SVGStop() {
		super(TAG);
	}

	public void setOffsetPercent(int pc) {
		this.addAttribute(new Attribute(OFFSET, pc+"%"));
	}
}
