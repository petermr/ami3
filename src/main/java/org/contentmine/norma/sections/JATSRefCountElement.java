package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSRefCountElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSRefCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "ref-count";

    public JATSRefCountElement(Element element) {
        super(element);
    }
}
