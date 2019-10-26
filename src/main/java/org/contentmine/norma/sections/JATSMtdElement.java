package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMtdElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMtdElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mtd";

    public JATSMtdElement(Element element) {
        super(element);
    }
}
