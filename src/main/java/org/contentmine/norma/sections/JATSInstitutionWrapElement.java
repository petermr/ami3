package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSInstitutionWrapElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSInstitutionWrapElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "institution-wrap";

    public JATSInstitutionWrapElement(Element element) {
        super(element);
    }
}
