package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSInstitutionWrapElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSInstitutionWrapElement.class);
public static String TAG = "institution-wrap";

    public JATSInstitutionWrapElement(Element element) {
        super(element);
    }
}
