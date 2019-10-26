package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMstyleElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMstyleElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mstyle";

    public JATSMstyleElement(Element element) {
        super(element);
    }
}
