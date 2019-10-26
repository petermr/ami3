package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMnElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMnElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mn";

    public JATSMnElement(Element element) {
        super(element);
    }
}
