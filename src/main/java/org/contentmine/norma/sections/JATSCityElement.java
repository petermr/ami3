package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCityElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSCityElement.class);
public static String TAG = "city";

    public JATSCityElement(Element element) {
        super(element);
    }
}
