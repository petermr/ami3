package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMsupElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMsupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "msup";

    public JATSMsupElement(Element element) {
        super(element);
    }
}
