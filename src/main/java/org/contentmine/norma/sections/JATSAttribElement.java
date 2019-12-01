package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSAttribElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSAttribElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "attrib";

    public JATSAttribElement(Element element) {
        super(element);
    }

    /** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlDiv div = new HtmlDiv();
		div.setClassAttribute("attrib");
		return deepCopyAndTransform(div);
	}

}
