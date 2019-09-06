package org.contentmine.ami.tools.gocr;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;

import nu.xom.Element;

/**
 * 
 <space x="876" y="28" dx="2" dy="2" />
 <space x="902" y="28" dx="0" dy="2" />
 <space x="921" y="28" dx="-1" dy="2" />
 <space x="935" y="28" dx="-2" dy="2" />
 
 * @author pm286
 *
 */
public class GOCRSpaceElement extends AbstractGOCRElement {
	private static final Logger LOG = Logger.getLogger(GOCRSpaceElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static String TAG = "space";

	public GOCRSpaceElement(Element element) {
		super(TAG, element);
	}
	
	public SVGRect createSVGElement() {
//		SVGRect svgRect = createSVGBox("stroke:blue;stroke-width:0.5;fill:none;", "space");
		SVGRect svgRect = createSVGBox("stroke:none;stroke-width:0.0;fill:none;", "space");
		return svgRect;
	}

}
