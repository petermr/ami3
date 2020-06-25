package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHr;

import nu.xom.Element;

public class JATSHrElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSHrElement.class);
public static String TAG = "hr";

    public JATSHrElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlHr());
	}

}
