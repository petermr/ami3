package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSMiElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMiElement.class);
public static String TAG = "mi";

    public JATSMiElement(Element element) {
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
