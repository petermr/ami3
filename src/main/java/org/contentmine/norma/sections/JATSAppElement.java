package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSAppElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSAppElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "app";

    public JATSAppElement(Element element) {
        super(element);
    }
}
