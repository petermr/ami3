package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSDateInCitationElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSDateInCitationElement.class);
public static String TAG = "date-in-citation";

    public JATSDateInCitationElement(Element element) {
        super(element);
    }
}
