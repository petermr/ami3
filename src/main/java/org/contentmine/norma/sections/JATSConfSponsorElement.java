package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSConfSponsorElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSConfSponsorElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "conf-sponsor";

    public JATSConfSponsorElement(Element element) {
        super(element);
    }
}
