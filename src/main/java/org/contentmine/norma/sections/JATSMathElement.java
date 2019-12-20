package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlCol;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSMathElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMathElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "math";

    public JATSMathElement(Element element) {
        super(element);
    }
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlDiv div = new HtmlDiv();
		div.setClassAttribute(TAG);
		return deepCopyAndTransform(div);
	}


}
