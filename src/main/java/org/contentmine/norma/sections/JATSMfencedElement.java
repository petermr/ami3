package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMfencedElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMfencedElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mfenced";

    public JATSMfencedElement(Element element) {
        super(element);
    }
}
