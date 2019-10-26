package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMtextElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMtextElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mtext";

    public JATSMtextElement(Element element) {
        super(element);
    }
}
