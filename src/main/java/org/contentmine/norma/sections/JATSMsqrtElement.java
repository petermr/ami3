package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMsqrtElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMsqrtElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "msqrt";

    public JATSMsqrtElement(Element element) {
        super(element);
    }
}
