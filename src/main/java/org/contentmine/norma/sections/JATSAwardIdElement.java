package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSAwardIdElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSAwardIdElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "award-id";

    public JATSAwardIdElement(Element element) {
        super(element);
    }
}
