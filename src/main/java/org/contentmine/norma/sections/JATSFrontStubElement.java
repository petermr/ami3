package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSFrontStubElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSFrontStubElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "front-stub";

    public JATSFrontStubElement(Element element) {
        super(element);
    }
}
