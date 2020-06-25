package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSLicensePElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSLicensePElement.class);
public static String TAG = "license-p";

    public JATSLicensePElement(Element element) {
        super(element);
    }
}
