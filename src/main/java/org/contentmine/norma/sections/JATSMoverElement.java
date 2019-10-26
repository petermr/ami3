package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMoverElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMoverElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mover";

    public JATSMoverElement(Element element) {
        super(element);
    }
}
