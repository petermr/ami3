package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMixedCitationElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSMixedCitationElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mixed-citation";

    public JATSMixedCitationElement(Element element) {
        super(element);
    }
}
