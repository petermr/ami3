package org.contentmine.graphics.svg.plot.flow;
/** a network of flows (as in CONSORT)
 * 
 * @author pm286
 *
 */

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGClipPath;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGDesc;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGEllipse;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGImage;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPattern;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGScript;
import org.contentmine.graphics.svg.SVGTSpan;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGTitle;

import nu.xom.Element;

public abstract class SVGFlowElement extends SVGG {
	private static final Logger LOG = Logger.getLogger(SVGFlowElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public SVGFlowElement(String tag) {
		super(tag);
	}
	


}