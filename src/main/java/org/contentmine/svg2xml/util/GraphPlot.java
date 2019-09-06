package org.contentmine.svg2xml.util;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGRect;

/** utilities for plotting
 * 
 * @author pm286
 *
 */
public class GraphPlot {

	public static SVGRect createBoxWithFillOpacity(Real2Range bbox, String fill, double opacity) {
		SVGRect plotRect = SVGRect.createFromReal2Range(bbox);
		if (plotRect == null) {
			plotRect = new SVGRect(new Real2(0.0, 0.0), new Real2(200., 30.));
		}
		plotRect.setFill(fill);
		plotRect.setOpacity(opacity);
		return plotRect;
	}


}
