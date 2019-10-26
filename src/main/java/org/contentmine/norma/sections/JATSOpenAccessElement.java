package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSOpenAccessElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSOpenAccessElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "open-access";

    public JATSOpenAccessElement(Element element) {
        super(element);
    }
}
