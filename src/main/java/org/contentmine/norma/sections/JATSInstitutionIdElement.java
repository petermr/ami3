package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSInstitutionIdElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSInstitutionIdElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "institution-id";

    public JATSInstitutionIdElement(Element element) {
        super(element);
    }
}
