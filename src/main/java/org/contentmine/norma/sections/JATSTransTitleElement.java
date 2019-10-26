package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSTransTitleElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSTransTitleElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "trans-title";

    public JATSTransTitleElement(Element element) {
        super(element);
    }
}
