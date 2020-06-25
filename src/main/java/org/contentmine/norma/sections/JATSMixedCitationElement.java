package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMixedCitationElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSMixedCitationElement.class);
public static String TAG = "mixed-citation";

    public JATSMixedCitationElement(Element element) {
        super(element);
    }
}
