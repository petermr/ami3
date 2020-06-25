package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTh;

import nu.xom.Element;

public class JATSThElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSThElement.class);
public static String TAG = "th";

    public JATSThElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlTh());
	}

}
