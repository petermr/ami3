package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSDefItemElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSDefItemElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "def-item";

    public JATSDefItemElement(Element element) {
        super(element);
    }
}
