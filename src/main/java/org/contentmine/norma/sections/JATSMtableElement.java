package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMtableElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMtableElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mtable";

    public JATSMtableElement(Element element) {
        super(element);
    }
}
