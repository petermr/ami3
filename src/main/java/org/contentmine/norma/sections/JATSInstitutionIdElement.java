package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSInstitutionIdElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSInstitutionIdElement.class);
public static String TAG = "institution-id";

    public JATSInstitutionIdElement(Element element) {
        super(element);
    }
}
