package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCountryElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSCountryElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "country";

    public JATSCountryElement(Element element) {
        super(element);
    }
}
