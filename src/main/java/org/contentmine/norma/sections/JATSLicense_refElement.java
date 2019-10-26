package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSLicense_refElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSLicense_refElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "license_ref";

    public JATSLicense_refElement(Element element) {
        super(element);
    }
}
