package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlBr;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSBreakElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSBreakElement.class);
public static String TAG = "break";

    public JATSBreakElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlBr());
	}

}
