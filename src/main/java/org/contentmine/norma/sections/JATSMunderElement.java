package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMunderElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMunderElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "munder";

    public JATSMunderElement(Element element) {
        super(element);
    }
}
