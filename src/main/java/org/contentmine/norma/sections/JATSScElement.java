package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;

import nu.xom.Element;

public class JATSScElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSScElement.class);
public static String TAG = "sc";

    public JATSScElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlSpan span = new HtmlSpan();
		span.setClassAttribute(TAG);
		return deepCopyAndTransform(span);
	}


}
