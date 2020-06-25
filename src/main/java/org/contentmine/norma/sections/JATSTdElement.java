package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTd;

import nu.xom.Element;

public class JATSTdElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSTdElement.class);
public static String TAG = "td";

    public JATSTdElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlTd());
	}

}
