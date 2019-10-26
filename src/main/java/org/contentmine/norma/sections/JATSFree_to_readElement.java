package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSFree_to_readElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSFree_to_readElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "free_to_read";

    public JATSFree_to_readElement(Element element) {
        super(element);
    }
}
