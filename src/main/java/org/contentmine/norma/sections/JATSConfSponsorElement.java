package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSConfSponsorElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSConfSponsorElement.class);
public static String TAG = "conf-sponsor";

    public JATSConfSponsorElement(Element element) {
        super(element);
    }
}
