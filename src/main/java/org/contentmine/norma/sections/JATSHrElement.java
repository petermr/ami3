package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHr;

import nu.xom.Element;

public class JATSHrElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSHrElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

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
