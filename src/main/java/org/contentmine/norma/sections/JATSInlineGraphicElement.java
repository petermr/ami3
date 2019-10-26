package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSInlineGraphicElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSInlineGraphicElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "inline-graphic";

    public JATSInlineGraphicElement(Element element) {
        super(element);
    }
}
