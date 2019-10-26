package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSStringNameElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSStringNameElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "string-name";

    public JATSStringNameElement(Element element) {
        super(element);
    }
}
