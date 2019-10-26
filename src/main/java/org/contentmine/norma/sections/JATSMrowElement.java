package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMrowElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMrowElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mrow";

    public JATSMrowElement(Element element) {
        super(element);
    }
}
