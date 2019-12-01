package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTbody;

import nu.xom.Element;

public class JATSTbodyElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = Logger.getLogger(JATSTbodyElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "tbody";

    public JATSTbodyElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlTbody());
	}

}
