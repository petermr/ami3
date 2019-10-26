package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMonospaceElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSMonospaceElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "monospace";

    public JATSMonospaceElement(Element element) {
        super(element);
    }
}
