package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSNamedContentElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSNamedContentElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "named-content";

    public JATSNamedContentElement(Element element) {
        super(element);
    }
}
