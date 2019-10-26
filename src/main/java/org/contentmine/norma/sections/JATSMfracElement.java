package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMfracElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMfracElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mfrac";

    public JATSMfracElement(Element element) {
        super(element);
    }
}
