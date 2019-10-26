package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSPrefixElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSPrefixElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "prefix";

    public JATSPrefixElement(Element element) {
        super(element);
    }
}
