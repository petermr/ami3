package org.contentmine.ami.tools.gocr;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;

public class GOCRLineElement extends AbstractGOCRElement {
	private static final Logger LOG = Logger.getLogger(GOCRLineElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static String TAG = "line";

	public GOCRLineElement(Element element) {
		super(TAG, element);
	}
	
	public SVGElement createSVGElement() {
		SVGElement svgElement = new SVGG(TAG);
		addChildSVGElements(svgElement);
		return svgElement;
	}
	
}
