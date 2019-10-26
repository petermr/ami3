package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSStateElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSStateElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "state";

    public JATSStateElement(Element element) {
        super(element);
    }
}
