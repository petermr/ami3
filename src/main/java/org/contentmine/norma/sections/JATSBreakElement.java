package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSBreakElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSBreakElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "break";

    public JATSBreakElement(Element element) {
        super(element);
    }
}
