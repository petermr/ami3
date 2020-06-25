package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlU;

import nu.xom.Element;

public class JATSUnderlineElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSUnderlineElement.class);
public static String TAG = "underline";

    public JATSUnderlineElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlU());
	}


}
