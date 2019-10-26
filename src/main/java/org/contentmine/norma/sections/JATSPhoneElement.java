package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSPhoneElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSPhoneElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "phone";

    public JATSPhoneElement(Element element) {
        super(element);
    }
}
