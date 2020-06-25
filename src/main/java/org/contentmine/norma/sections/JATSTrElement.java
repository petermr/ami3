package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTr;

import nu.xom.Element;

public class JATSTrElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSTrElement.class);
public static String TAG = "tr";

    public JATSTrElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlTr());
	}

}
