package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSAttribElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSAttribElement.class);
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
