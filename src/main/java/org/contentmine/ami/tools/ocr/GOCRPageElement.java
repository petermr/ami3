package org.contentmine.ami.tools.ocr;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;
import nu.xom.Elements;

public class GOCRPageElement extends AbstractGOCRElement {
	private static final Logger LOG = Logger.getLogger(GOCRPageElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static String TAG = "page";
	private SVGElement svgElement;

	public GOCRPageElement() {
		super(TAG);
	}

	public GOCRPageElement(Element element) {
		super(TAG, element);
	}

	public SVGElement createSVGElement() {
		svgElement = new SVGG(TAG);
		addChildSVGElements(svgElement);
		return svgElement;
	}

	
}
