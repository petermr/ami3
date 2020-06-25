package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTbody;

import nu.xom.Element;

public class JATSTbodyElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSTbodyElement.class);
public static String TAG = "tbody";

    public JATSTbodyElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlTbody());
	}

}
