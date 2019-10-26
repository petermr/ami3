package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMspaceElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMspaceElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mspace";

    public JATSMspaceElement(Element element) {
        super(element);
    }
}
