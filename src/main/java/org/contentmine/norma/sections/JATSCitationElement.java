package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCitationElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSCitationElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "citation";

    public JATSCitationElement(Element element) {
        super(element);
    }
}
