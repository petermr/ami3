package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMiElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMiElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mi";

    public JATSMiElement(Element element) {
        super(element);
    }
}
