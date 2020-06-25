package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSLicense_refElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSLicense_refElement.class);
public static String TAG = "license_ref";

    public JATSLicense_refElement(Element element) {
        super(element);
    }
}
