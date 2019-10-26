package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSTableCountElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSTableCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "table-count";

    public JATSTableCountElement(Element element) {
        super(element);
    }
}
