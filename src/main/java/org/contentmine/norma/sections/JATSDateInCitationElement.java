package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSDateInCitationElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSDateInCitationElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "date-in-citation";

    public JATSDateInCitationElement(Element element) {
        super(element);
    }
}
