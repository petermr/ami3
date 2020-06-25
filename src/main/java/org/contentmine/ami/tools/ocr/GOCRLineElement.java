package org.contentmine.ami.tools.ocr;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;

public class GOCRLineElement extends AbstractGOCRElement {
	private static final Logger LOG = LogManager.getLogger(GOCRLineElement.class);
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
