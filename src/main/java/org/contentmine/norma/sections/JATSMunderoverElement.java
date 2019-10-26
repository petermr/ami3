package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMunderoverElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMunderoverElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "munderover";

    public JATSMunderoverElement(Element element) {
        super(element);
    }
}
