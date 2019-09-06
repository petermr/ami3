package org.contentmine.graphics.svg;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** only used for display, mainly debugging.
 * 
 * @author pm286
 *
 */
public class SVGLinearGradient extends AbstractSVGGradient {
	private static final Logger LOG = Logger.getLogger(SVGLinearGradient.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "linearGradient";
	public static final String DEFAULT_ID = TAG+".id";

	public SVGLinearGradient() {
		super(TAG);
	}
	

	/**
	 * creates a default radialGradient for those who can't remember or read the docs.
	 * 
    <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" style="stop-color:rgb(255,255,0);stop-opacity:1" />
      <stop offset="100%" style="stop-color:rgb(255,0,0);stop-opacity:1" />
    </linearGradient>
    
	 * @return
	 */
	public static SVGLinearGradient getDefaultYLinearGradient() {
		SVGLinearGradient linearGradient = new SVGLinearGradient();
		linearGradient.setX1Percent(0);
		linearGradient.setX2Percent(0);
		linearGradient.setY1Percent(0);
		linearGradient.setY2Percent(100);
		linearGradient.setId("Y"+DEFAULT_ID);
		linearGradient.appendStop(0, "stop-color:yellow;stop-opacity:1;");
		linearGradient.appendStop(100, "stop-color:blue;stop-opacity:1;");
		return linearGradient;
	}

	/**
	 * creates a default radialGradient for those who can't remember or read the docs.
	 * 
    <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="0%">
      <stop offset="0%" style="stop-color:rgb(255,255,0);stop-opacity:1" />
      <stop offset="100%" style="stop-color:rgb(255,0,0);stop-opacity:1" />
    </linearGradient>
    
	 * @return
	 */
	public static SVGLinearGradient getDefaultXLinearGradient() {
		SVGLinearGradient linearGradient = new SVGLinearGradient();
		linearGradient.setX1Percent(0);
		linearGradient.setX2Percent(100);
		linearGradient.setY1Percent(0);
		linearGradient.setY2Percent(0);
		linearGradient.setId("X"+DEFAULT_ID);
		linearGradient.appendStop(0, "stop-color:yellow;stop-opacity:1;");
		linearGradient.appendStop(100, "stop-color:blue;stop-opacity:1;");
		return linearGradient;
	}

}
