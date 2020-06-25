package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCountryElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSCountryElement.class);
public static String TAG = "country";

    public JATSCountryElement(Element element) {
        super(element);
    }
}
