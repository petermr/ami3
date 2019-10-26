package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSLicensePElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSLicensePElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "license-p";

    public JATSLicensePElement(Element element) {
        super(element);
    }
}
