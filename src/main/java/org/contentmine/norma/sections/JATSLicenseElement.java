package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSLicenseElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSLicenseElement.class);
public static String TAG = "license";

    public JATSLicenseElement(Element element) {
        super(element);
    }
}
