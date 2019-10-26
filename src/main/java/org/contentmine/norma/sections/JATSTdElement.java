package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSTdElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = Logger.getLogger(JATSTdElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "td";

    public JATSTdElement(Element element) {
        super(element);
    }
}
