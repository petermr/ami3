package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSTransSourceElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSTransSourceElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "trans-source";

    public JATSTransSourceElement(Element element) {
        super(element);
    }
}
