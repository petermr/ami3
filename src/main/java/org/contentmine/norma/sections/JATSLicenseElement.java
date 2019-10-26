package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSLicenseElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSLicenseElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "license";

    public JATSLicenseElement(Element element) {
        super(element);
    }
}
