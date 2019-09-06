package org.contentmine.ami.tools.gocr;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 <box x="1002" y="25" dx="5" dy="4" value="o" numac="3" weights="95,94,94" achars="o,O,0" />
 <box x="1010" y="25" dx="6" dy="7" value="o" numac="3" weights="97,93,93" achars="o,O,0" />
 * @author pm286
 *
 */
public class GOCRBoxElement extends AbstractGOCRElement {
	private static final Logger LOG = Logger.getLogger(GOCRBoxElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static String TAG = "box";
	private static final String VALUE = "value";
	private static final String ACHARS = "achars";
	private double fontSize = 10;

	public GOCRBoxElement(Element element) {
		super(TAG, element);
	}
	
	public SVGElement createSVGElement() {
		SVGG g = new SVGG();
		SVGRect svgRect = createSVGBox("stroke:red;stroke-width:0.5;fill:none;", "text");
		g.appendChild(svgRect);
		SVGText textElement = createSVGText("font-size:10;fill:black;font-family:helvetica;");
		g.appendChild(textElement);
		return g;
	}

	/** this is where the text size etc is set */
	
	private SVGText createSVGText(String cssStyle) {
		int x = getX();
		int y = getY();
		int dx = getDX();
		int dy = getDY();
		String value = this.getAttributeValue(VALUE);
		String achars = this.getAttributeValue(ACHARS);
		// replace comma by section mark for visual 
		achars = achars == null ? "" : achars.replace(",,", ","+((char)167));
		String achars2 = (achars.length() == 0) ? "?" : String.valueOf(achars.charAt(0));
		SVGText text = new SVGText(new Real2(x, y + dy + getTextOffset()), achars2);
		text.addAttribute(new Attribute(ACHARS, achars));
		text.setCSSStyle(cssStyle);
		text.setFontSize(fontSize);
		return text;

	}

}
