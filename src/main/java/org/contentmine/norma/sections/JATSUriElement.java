package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSUriElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSUriElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "uri";

    public JATSUriElement(Element element) {
        super(element);
    }
}
