package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCityElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSCityElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "city";

    public JATSCityElement(Element element) {
        super(element);
    }
}
