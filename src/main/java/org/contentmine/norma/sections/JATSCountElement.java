package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCountElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "count";

    public JATSCountElement(Element element) {
        super(element);
    }
}
