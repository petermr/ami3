package org.contentmine.ami.tools.ocr;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;

import nu.xom.Element;
import nu.xom.Elements;

public abstract class AbstractGOCRElement extends Element {
	private static final Logger LOG = LogManager.getLogger(AbstractGOCRElement.class);
public static String TAG = "page";

//	public static final String X = "x";
//	public static final String Y = "y";
	public static final String DX = "dx";
	public static final String DY = "dy";

	private int textOffset = 0;


	protected AbstractGOCRElement(String tag) {
		super(tag);
	}

	public AbstractGOCRElement(String tag, Element rawElement) {
		this(tag);
		createGOCRDescendants(rawElement);
	}

	public void createGOCRDescendants(Element rawElement) {
		XMLUtil.copyAttributes(rawElement, this);
		addChildGOCRElements(rawElement);
	}

	private void addChildGOCRElements(Element rawElement) {
		Elements rawChildElements = rawElement.getChildElements();
		for (int i = 0; i < rawChildElements.size(); i++) {
			Element rawChildElement = rawChildElements.get(i);
			String rawChildTag = rawChildElement.getLocalName();
			AbstractGOCRElement gocrChildElement = null;
			if (false) {
			} else if (GOCRBlockElement.TAG.equals(rawChildTag)) {
				gocrChildElement = new GOCRBlockElement(rawChildElement);
			} else if (GOCRBoxElement.TAG.equals(rawChildTag)) {			
				gocrChildElement = new GOCRBoxElement(rawChildElement);
			} else if (GOCRLineElement.TAG.equals(rawChildTag)) {
				gocrChildElement = new GOCRLineElement(rawChildElement);
			} else if (GOCRPageElement.TAG.equals(rawChildTag)) {
				gocrChildElement = new GOCRPageElement(rawChildElement);
			} else if (GOCRSpaceElement.TAG.equals(rawChildTag)) {
				gocrChildElement = new GOCRSpaceElement(rawChildElement);
			} else {
				LOG.debug("unknown element: "+ rawChildTag);
			}
			if (gocrChildElement != null) {
				this.appendChild(gocrChildElement);
			}
		}
	}
	
	protected Integer getX() {
		return new Integer(this.getAttributeValue(SVGRect.X));
	}
	protected Integer getY() {
		return new Integer(this.getAttributeValue(SVGRect.Y));
	}
	protected Integer getDX() {
		return new Integer(this.getAttributeValue(DX));
	}
	protected Integer getDY() {
		return new Integer(this.getAttributeValue(DY));
	}
	
	public abstract SVGElement createSVGElement();
	
	protected void addChildSVGElements(SVGElement svgElement) {
		Elements childElements = this.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			AbstractGOCRElement childElement = (AbstractGOCRElement) childElements.get(i);
			svgElement.appendChild(childElement.createSVGElement());
		}
	}

	protected SVGRect createSVGBox(String cssValue, String clazz) {
		int x = getX();
		int y = getY();
		int dx = getDX();
		int dy = getDY();

		SVGRect svgRect = new SVGRect(x, y, dx, dy);
		SVGRect.setClassAttributeValue(svgRect, clazz);
		svgRect.setCSSStyle(cssValue);
		return svgRect;
	}

	public int getTextOffset() {
		return textOffset;
	}

	public void setTextOffset(int textOffset) {
		this.textOffset = textOffset;
	}
	

	
}
