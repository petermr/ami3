package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSRomanElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSRomanElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "roman";

    public JATSRomanElement(Element element) {
        super(element);
    }
}
