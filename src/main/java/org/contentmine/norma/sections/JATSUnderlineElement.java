package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlU;

import nu.xom.Element;

public class JATSUnderlineElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSUnderlineElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

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
