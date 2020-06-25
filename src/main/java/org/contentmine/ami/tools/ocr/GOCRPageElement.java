package org.contentmine.ami.tools.ocr;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;
import nu.xom.Elements;

public class GOCRPageElement extends AbstractGOCRElement {
	private static final Logger LOG = LogManager.getLogger(GOCRPageElement.class);
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
