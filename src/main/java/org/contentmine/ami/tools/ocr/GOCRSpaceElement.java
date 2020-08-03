package org.contentmine.ami.tools.ocr;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;

import nu.xom.Element;

/**
 * 
 &lt;space x="876" y="28" dx="2" dy="2" /&gt;
 &lt;space x="902" y="28" dx="0" dy="2" /&gt;
 &lt;space x="921" y="28" dx="-1" dy="2" /&gt;
 &lt;space x="935" y="28" dx="-2" dy="2" /&gt;
 
 * @author pm286
 *
 */
public class GOCRSpaceElement extends AbstractGOCRElement {
	private static final Logger LOG = LogManager.getLogger(GOCRSpaceElement.class);
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
