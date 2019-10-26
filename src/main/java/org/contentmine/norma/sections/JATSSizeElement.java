package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSSizeElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSSizeElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "size";

    public JATSSizeElement(Element element) {
        super(element);
    }
}
