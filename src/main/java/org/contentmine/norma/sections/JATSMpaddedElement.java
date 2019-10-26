package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMpaddedElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMpaddedElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mpadded";

    public JATSMpaddedElement(Element element) {
        super(element);
    }
}
