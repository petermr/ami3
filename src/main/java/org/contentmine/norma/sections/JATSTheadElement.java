package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlThead;

import nu.xom.Element;

public class JATSTheadElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSTheadElement.class);
public static String TAG = "thead";

    public JATSTheadElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlThead());
	}

}
