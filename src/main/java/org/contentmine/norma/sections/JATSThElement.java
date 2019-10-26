package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSThElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = Logger.getLogger(JATSThElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "th";

    public JATSThElement(Element element) {
        super(element);
    }
}
