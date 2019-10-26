package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMathElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMathElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "math";

    public JATSMathElement(Element element) {
        super(element);
    }
}
