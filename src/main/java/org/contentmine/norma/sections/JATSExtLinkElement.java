package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSExtLinkElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSExtLinkElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "ext-link";

    public JATSExtLinkElement(Element element) {
        super(element);
    }
}
