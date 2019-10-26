package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSPageCountElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSPageCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "page-count";

    public JATSPageCountElement(Element element) {
        super(element);
    }
}
