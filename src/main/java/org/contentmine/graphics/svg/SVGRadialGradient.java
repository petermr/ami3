package org.contentmine.graphics.svg;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;

/** only used for display, mainly debugging.
 * 
    <radialGradient id="grad1" cx="50%" cy="50%" r="50%" fx="50%" fy="50%">
      <stop offset="0%" style="stop-color:rgb(255,255,255);stop-opacity:0" />
      <stop offset="100%" style="stop-color:rgb(0,0,255);stop-opacity:1" />
    </radialGradient>

 * @author pm286
 *
 */
public class SVGRadialGradient extends AbstractSVGGradient {
	private static final Logger LOG = Logger.getLogger(SVGRadialGradient.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "radialGradient";
	public static final String DEFAULT_ID = TAG+".id";
	public SVGRadialGradient() {
		super(TAG);
	}

	/**
	 * creates a default radialGradient for those who can't remember or read the docs.
	 * 
	 <radialGradient id="grad1">
        <stop offset="10%" style="stop-color:rgb(255,255,255);stop-opacity:0" />
        <stop offset="90%" style="stop-color:rgb(0,0,255);stop-opacity:1" />
    </radialGradient>

	 * @return
	 */
	public static AbstractCMElement getDefaultRadialGradient() {
		SVGRadialGradient radialGradient = new SVGRadialGradient();
		radialGradient.setId(DEFAULT_ID);
		radialGradient.appendStop(0, "stop-color:yellow;stop-opacity:0;");
		radialGradient.appendStop(100, "stop-color:blue;stop-opacity:1;");
		return radialGradient;
	}
}
