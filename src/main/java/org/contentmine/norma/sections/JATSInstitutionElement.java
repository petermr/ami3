package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSInstitutionElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSInstitutionElement.class);
public static String TAG = "institution";

    public JATSInstitutionElement() {
        super(TAG);
    }

    public JATSInstitutionElement(Element element) {
        super(element);
    }
}
