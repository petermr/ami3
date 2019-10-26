package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSInstitutionElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSInstitutionElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "institution";

    public JATSInstitutionElement(Element element) {
        super(element);
    }
}
