package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMsubElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMsubElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "msub";

    public JATSMsubElement(Element element) {
        super(element);
    }
}
