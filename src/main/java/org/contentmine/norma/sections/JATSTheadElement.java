package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSTheadElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = Logger.getLogger(JATSTheadElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "thead";

    public JATSTheadElement(Element element) {
        super(element);
    }
}
