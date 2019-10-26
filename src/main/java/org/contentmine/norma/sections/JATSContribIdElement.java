package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSContribIdElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSContribIdElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "contrib-id";

    public JATSContribIdElement(Element element) {
        super(element);
    }
}
