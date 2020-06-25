package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCitationElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSCitationElement.class);
public static String TAG = "citation";

    public JATSCitationElement(Element element) {
        super(element);
    }
}
